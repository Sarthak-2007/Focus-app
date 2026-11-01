# ISSUE #10 — RECEIPT CELL PLAN & CLOSE
## Zettel, cell lead. Cell: Designer · Innovator · Critic · Builder + Finishers 1–5 + Feel 1–5 (15 seats).
## Branch: agent/issue-10. State: DECLARED — code written, NOTHING compiled, nothing tested (his law).

Source audit: studio/plan/AUDIT-receipt.md. Issue scope (the four items):
W-41 HELD line killed as printed · deflection outcome filter · the
slip's total · persisted dismissal. The slip's printed character is
acquitted and binding throughout (Finisher 5 verified every acquittal
holds in the current tree).

## WHAT WAS BUILT (Builder, 5 files, +228/−14, diff verified by lead)

1. **Outcome filter (A4)** — `InterceptOutcome` enumeration in
   ReceiptState.kt; `assemblePendingReceipt` allowlist-filters on
   `log.outcome` BEFORE the 30s dedupe. Peeks print nothing and eat
   no dedupe slot. Files: ReceiptState.kt, MonolithRepository.kt.
2. **The total (A1)** — ReceiptSheet.kt prints
   `RAN {longSpan(sealedElapsedSeconds)}` under ENDED over a 1dp
   rule, from the field that was carried through every joint and
   read zero times; omitted when 0. Placement ratified per audit
   item 5 (see Ruling 2).
3. **Persisted dismissal** — dismissed № in a `monolith_receipt`
   SharedPreferences store (commit(), house prefs pattern);
   `rehydrateReceipt()` refuses the dismissed №; VM
   `dismissReceipt()` wired at both MainActivity dismissal sites.
   ZERO migration. Files: MonolithRepository.kt,
   MonolithViewModel.kt, MainActivity.kt.
4. **HELD print-side preparation (W-41 seam)** — the false summed
   line no longer prints (STATE-ONLY interim law). `ReceiptHeldFigures`
   + nullable `heldFigures` on PendingReceipt/ReceiptState; the sheet
   prints only `receipt.heldLine`; assembly carries a marked
   **W-41 FILL POINT** comment. No columns invented, no migration.

## LEAD RULINGS ON THE CELL'S FORKS

- **R1 · A4 placement is binding (Critic Attack 1).** The filter
  stays ASSEMBLY-LOCAL, vocabulary enumerated, filter-before-dedupe —
  as built. It must never move into the DAO/`getInterceptLogsBetween`
  layer: that query also feeds routine judgement (see N1), and a
  DAO-level filter would silently rewrite judgements.
- **R2 · A1 placement ratified; rounding charge preserved.**
  Placement = audit item 5 (under ENDED, over a rule), against item
  16's section reading — Finisher 2's order stands. Critic Attack 3
  stands as an OPEN QUESTION for Ren: `longSpan` ceilings, so a
  61-second span prints `RAN 2 MIN` — the audit ordered this form
  unanimously and the code follows the audit; if Ren rules the total
  must be exact, the change is one formatter call. Note for B5: on
  routine rows RAN and PARTIAL's attended figure can be the same
  seconds under two names — resolve before B5 lands.
- **R3 · Vocabulary honesty (Critic Attack 2).** The blocked set
  includes `"Blocked"`, which no audited writer emits (writers:
  "Stayed Focused", "Split Into 10m Micro-Step"; let-in: "Used 2m
  Peek"). Fail-safe as built; at first compile/test, grep the literal
  across production writers — if none, strike it from the set.
- **R4 · HELD return grammar amended (Critic Attack 7).** When the
  split lands, the line returns in the in-tree tenant grammar —
  `{shut} HELD · {peek} PEEK · {sites} SITES`
  (MonolithCustomComponents.kt:475–483) — HELD binding the shut
  count only, zero segments omitted per the tenant law (:471–474).
  NOT the Designer's umbrella format. The Builder's seam is
  grammar-agnostic; this lands inside `heldLine` at the fill point.
- **R5 · Dismissal home (Designer vs Innovator).** The prefs store
  as built. The Designer's guard_settings column (priced, one
  v12→v13 migration) stays on record in designer.md; Critic Attack 5
  (A9 was priced mergeable-now but is a migration) is moot on the
  built route.
- **R6 · Sound §4.4 (Feel 4's flag).** Accepted as specced: the E880
  knock pair is P-gated with the pulse — the pair is part of the
  finish voice; fallback recorded in feel-4-sound.md.

## DEPENDENCY FLAG — W-41 (for Ren's batch-10 hand-work)

The HELD line stays omitted until ALL of these land:
- Persisted split columns `heldShutAtSeal` / `heldPeekAtSeal`
  (Suki's Car-3 offer), stamped **at seal, from enforcement state,
  by a named writer** — Critic Attack 6 / N4: today's held counts
  are BIRTH counts; copying them across would inherit a
  birth-not-seal lie.
- An enforcer-derived sites figure (placeholder name in the design:
  `heldSitesEnforced`). Mechanism corrected by Critic (a) / N5: no
  enforcer factory-fallback exists; the lie's real mechanism is
  reseed-on-empty — an emptied list silently resurrects. The sites
  figure must count what the enforcer actually holds at seal.
- Then only the ~3-line adapter at the fill point flips; sheet,
  state, and copy sites never change (Innovator §4).

## OUTGOING CHARGES (Critic N-series — Ren to route; they outrank the print cures)

- **N1 (HIGH)** — a peek counts as attendance: `computeAttendedSeconds`
  (MonolithRepository.kt:965–966) reads the same intercept logs with
  no outcome or intention filter; a "Used 2m Peek" row books the
  same attendance arc as a turn-away. Routine-judgement surface
  (issues #12/#13), NOT a receipt fix.
- **N2 (HIGH)** — `recordIntercept` increments
  `phantomReachesBlocked` for peeks; the live shield notification
  prints let-ins as "urges stopped" (MonolithShieldService.kt:144).
  Shield surface (issue #7).
- **N3 (HIGH)** — the deflection list is a list of RESOLUTIONS, not
  attempts; biased in both directions. Meaning-of-the-list question,
  above the print cures.
- **N6 (MEDIUM)** — dedupe is per-package; all site rows share the
  browser's package. Known limit on A4/A10 as built.
- **N7 (LOW, process)** — the audit's line numbers are stale
  throughout; the Critic's filing carries current-tree lines.

## NOT BUILT — deliberately

- **BREAK BANKED kill (audit item 10 / Group A)** — outside the
  cell's four assigned items; the line still prints in code. Flagged
  for batch-10 Group A.
- **Group B (parked, issue #21)** — TEAR (B1), SLIP BOOK (B2), F-W1
  `FOR:` amendment (B3), fault-line rewording + retry print +
  corrected-duplicate stamp (B4), PARTIAL-with-evidence (B5),
  SLEEP stand-down drawer-only (B6), THE SENTENCE (B7). Feel 2's
  tear spec is research for B1's ruling, labelled proposal-tier.
- **Group A print finishes beyond the four items** (head №, footer
  fit/night-ink, shape/anchor, feed arrival, sound map, paper stock)
  are SPECCED to gallery standard in the finisher/feel documents —
  paper only, for the batch-10 merge to implement.

## VERIFICATION OWED (nobody compiles — Ren's process)

- First compile + the receipt test suites on this branch.
- R3's `"Blocked"` literal grep.
- Stand-down gate (Designer Dep. 2 / Finisher 2): verify
  `spanElapsedSeconds` on a ROUTINE_SLEEP_STANDDOWN row; if
  structural-0, stand-down slips omit RAN + its rule (the built
  `> 0` omission already covers the print side).
- Branch-U settled question (Feel 1): branch-U/discovery receipts
  carry `restored = false` yet "simply stand" — a VM-side ruling
  for Ren; the A7 gate implements the lawful `restored` flag.

## SPEC INDEX — studio/plan/issue-10/

designer.md · innovator.md · critic.md · builder.md ·
finisher-1-head.md · finisher-2-body.md · finisher-3-deflections.md ·
finisher-4-footer-fault.md · finisher-5-consistency.md ·
feel-1-arrival.md · feel-2-tear.md · feel-3-settle.md ·
feel-4-sound.md · feel-5-paper.md

— Zettel, cell lead, issue #10. Paper and uncompiled code only.
