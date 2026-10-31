# BUILDER — Issue #10, Receipt (audit fixes) — DECLARED close

Builder seat, Zettel's cell. Branch `agent/issue-10` in the cell clone
`~/workspace/clone-inspect/builds/cells/issue-10/` (cloned from the local
canonical tree `~/workspace/clone-inspect/builds/merge-batch9/Focus-app`
per brief; nothing pushed). **Uncompiled code — DECLARED only. No gradle
was run, no test was run (his law).** The slip's printed character was
not redesigned: no shape, rhythm, arrival, scrim, or wording changes
outside the four fixes below.

Base note: the merge-batch9 tree predates the studio/ migration commit
(8438f5a), so it carries no `studio/plan/AUDIT-receipt.md`. The audit was
read from the identical file in a sibling cell clone
(`builds/cells/issue-13/studio/plan/AUDIT-receipt.md`). No studio/ files
were added to this branch.

## Files changed — 5 (+228 / −14)

1. `app/src/main/java/com/example/data/ReceiptState.kt` (+108)
2. `app/src/main/java/com/example/data/MonolithRepository.kt` (+64)
3. `app/src/main/java/com/example/ui/components/ReceiptSheet.kt` (+41)
4. `app/src/main/java/com/example/ui/MonolithViewModel.kt` (+15)
5. `app/src/main/java/com/example/MainActivity.kt` (+14)

## Fix 1 — Outcome-filtered deflection assembly (audit item 11 / Neu C-03) — DONE

- `ReceiptState.kt`: new `InterceptOutcome` object enumerating the
  intercept log's whole outcome vocabulary from every writer in the
  tree — `"Blocked"` (test/harness spelling), `"Stayed Focused"`
  (transmuteImpulseAndReturn — turned away), `"Split Into 10m
  Micro-Step"` (splitActiveTaskAndReturn — intercepted, never let in),
  `"Used 2m Peek"` (the peek grant — LET IN). Plus
  `isBlockedInterceptOutcome()`: a case-insensitive ALLOWLIST of the
  three blocked outcomes. Anything not enumerated — a peek, or any
  future unknown value — is not a deflection.
- `MonolithRepository.assemblePendingReceipt`: the deflection filter
  now also requires `isBlockedInterceptOutcome(log.outcome)`. It runs
  in the first filter, before the 30s dedupe, so a peek row cannot eat
  a real block's dedupe slot for the same package. Peek visits no
  longer print as blocks.

## Fix 2 — Total line `RAN {longSpan}` (audit item 5 / A1) — DONE

- `ReceiptSheet.kt`: under the ENDED line, a 1dp rule
  (HorizontalDivider, house pattern) and
  `RAN ${longSpan(receipt.sealedElapsedSeconds)}`.
- The span data actually exists: `sealedElapsedSeconds` is sealed from
  the session row's `spanElapsedSeconds` at assembly and was already
  carried through PendingReceipt → ReceiptState, read zero times by
  the sheet (the audit's finding). No new field, no recompute.
- Guarded `> 0`: a zero span omits the line rather than printing
  `RAN 0 MIN` (the slip's omission law). On FLOW slips (no PLANNED)
  it is the headline figure, as the audit ruled.

## Fix 3 — Persisted receipt dismissal (audit item 20, interim law) — DONE

- Mechanism: SharedPreferences, following the app's existing
  prefs-store pattern (seal marker, day words). New repository door in
  `MonolithRepository.kt`:
  - consts `RECEIPT_PREFS_NAME = "monolith_receipt"`,
    `DISMISSED_RECEIPT_NUMBER_KEY = "dismissed_receipt_number"`;
  - `dismissedReceiptNumber(): Int` (0 = none; №s start at 1);
  - `dismissReceipt(number)` — synchronous `commit()` (the DayWordStore
    G-02 standard), one slot only: №s mint forward and are never
    re-issued, so the latest dismissed № is the whole truth.
- Enforcement at the cold-start door: `rehydrateReceipt()` returns
  null when the standing № equals the dismissed № — every host
  inherits it.
- Wiring: `MonolithViewModel.dismissReceipt()` persists the standing
  receipt's № (state flow deliberately NOT cleared — in-session host
  law unchanged). `MainActivity.kt` calls it at both dismissal sites:
  the sheet's `onDismiss` and the navigation/PARK `LaunchedEffect`,
  alongside the existing `remember` state (kept as-is).
- **DB version implication: NONE — deliberate.** Current schema is
  v12. The alternative (a `lastDismissedReceiptNumber` column on
  guard_settings) would require a 12→13 version bump + migration for
  one integer; it was NOT done and NOT silently implied. If Ren later
  wants the fact inside Room (e.g. alongside a W-41/B4 v13), the
  prefs value drains trivially into such a column.

## Fix 4 — HELD print side, W-41 preparation ONLY (audit item 9) — DONE (prep)

- The false line is OUT of the print: the sheet no longer reads
  `heldAppsAtSeal`/`heldSitesAtSeal` (the summed count that booked
  PEEK apps as HELD + the blind site-list size). No HELD line prints
  today — the audit's STATE-ONLY ruling. The summed figures stay on
  the state as record.
- The seam, structured to consume the future columns with no sheet
  change:
  - `ReceiptState.kt`: new `ReceiptHeldFigures(shutApps, peekApps,
    heldSites)` with `printLine()` in the Shield header's existing
    split dialect (zero parts omitted, null when nothing held);
    nullable `heldFigures` (default null) appended at the END of both
    `PendingReceipt` and `ReceiptState` constructors (positional
    construction in ScreenshotHarnessTest stays valid), plus
    `ReceiptState.heldLine`.
  - `ReceiptSheet.kt`: prints `receipt.heldLine?.let { … }` and
    nothing else for HELD.
  - `MonolithRepository.kt`: publish + rehydrate pass
    `heldFigures = pending.heldFigures`; assembly carries a marked
    **W-41 FILL POINT** comment in `assemblePendingReceipt` — the one
    place to fill `ReceiptHeldFigures` from the persisted
    heldShutAtSeal/heldPeekAtSeal columns + enforcer-derived sites
    when Ren's batch-10 data work lands.
- **No columns were invented, no migration written, no DB version
  touched.** The formatter's `{n} HELD · {m} PEEK · {k} SITES`
  composition is provisional print-side code only — Ren/W-41 can
  reword it at the fill point without structural change.

## Not done / not touched (by design)

- TEAR and SLIP BOOK: parked (issue #21, audit group B) — not built.
- All other audit Group A items (A2 outcome words, A3 footer noun,
  A5–A8, A10, A11) are outside this brief's four fixes — untouched.
- BREAK BANKED line: left exactly as-is (audit item 10 kills it, but
  it is not one of the four assigned fixes; flagged here for Zettel).
- Tests: not run and not modified (no-compile law). Existing tests
  were checked by eye for compatibility: ScreenshotHarnessTest's
  positional ReceiptState construction (new param is last + defaulted)
  and its `"BLOCKED"` intercept seeds (the allowlist matches
  case-insensitively, so its deflection expectations still hold);
  ReceiptClaimRepositoryTest calls only unchanged signatures.

## Blockers

- None for the four fixes. The W-41 data columns themselves remain
  Ren's batch-10 work, as briefed — this lane only prepared the print
  side.
