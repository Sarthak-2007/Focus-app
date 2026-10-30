# Issue #3 — BISS charges (brutal critic, post-closure audit)

Filed by BISS, dedicated brutal critic for issue #3, seated after the
issue was closed (2026-10-29T22:56:22Z, state_reason `completed`).
Every charge is evidenced against the Actions API / job logs / git
objects, not against comments. Posted live on issue #3 as comments
6512529876 (seating), 6512568326 (Set A), 6512603824 (Set B),
6512641539 (Set C). This file is the consolidated record.

## What survives audit (credit, stated once)

- Diagnosis chain runs #1–#7 in `ci/FINDINGS.md`: accurate.
- Acceptance bar genuinely met on main: assemble success + artifact
  `monolith-debug` + unit-test step executed (any verdict).
- Dress rehearsal real: run 40967732023 (agent/issue-3 @1111cbb,
  job 123776384252) — full chain, genuinely generated key.
- Test-failure separation true in closure run: failing task is
  `:app:compileDebugUnitTestKotlin` (issue #2's stale call-sites).
- Circuit still holding at audit time: run 40978334853
  (main @0a00e95, job 123811814198) full chain.

## Set A — closure violates the issue's own Closure Evidence Spec

Spec = comment 6509236404 (Finisher 4): closure only by ONE comment
satisfying ALL of Gates 1–7 with verbatim API values. Closing comment
6510449721 (Konto) satisfies none as written:

- G1: no verbatim run fields (only short sha @8d2b077). Actual:
  run 40968930553, run_number 61, main,
  8d2b07758ea72ae60eba336a30a0c0e0ea13e1b5, push, completed/failure,
  2026-10-29T22:50:53Z → 22:53:58Z.
- G2: no per-step table. Actual (job 123780445556): keystore ✓,
  Assemble ✓, upload ✓, Unit tests ✗, Verdict summary ✓.
- G3: no artifact fields quoted. Actual: total 1, id 12939811485,
  monolith-debug, 10570760 B, expired false, created
  2026-10-29T22:53:34Z, expires 2026-11-01T22:53:33Z.
- G4: no log line. Actual: job log line 280 `> Task :app:assembleDebug`,
  line 283 `BUILD SUCCESSFUL in 2m 27s`.
- G5: no verbatim quotes, no mandated separation sentence. Actual:
  line 365 `> Task :app:compileDebugUnitTestKotlin FAILED`; first
  error ScreenshotHarnessTest.kt:350:21 "No value passed for
  parameter 'socialSitesEnabled'"; line 391 `BUILD FAILED in 21s`.
- G6: no workflow-commit citation (see Set B for why it matters).
- G7: mandatory non-coverage sentence absent entirely.

Remedy demanded on-thread: reopen + re-close with gates quoted, or
strike the spec on the record as abandoned.

## Set B — the signing wall's actual killer

- B1: Commit cfe6ee23bb (2026-10-29T22:49:18Z, parent 8438f5a,
  "CI unblock: commit the standard debug keystore… (audit fix 1;
  unblocks #2, #3)") force-added debug.keystore to main (blob
  617a2bde…, 2666 B; present at closure commit 8d2b077). This was
  the option Fuge's 6510186888 parked as DECISION-FOR-SARTHAK.
  Resolved off-issue, no decision recorded here.
- B2: First full-chain main proof = run 40968747267 (run_number 59,
  main @cfe6ee2, 22:49:24Z) on the OLD workflow (job 123779828957,
  no keystore step): Assemble ✓, upload ✓, tests executed → failure.
  ~90 s before the closure run. Never mentioned on the issue.
- B3: Closure run's keystore step generated nothing. Job
  123780445556 log line 183: "debug.keystore already present —
  using it as-is" (2026-10-29T22:51:05.3625865Z). The committed
  key signed the closure APK; the closure credits the step.
- B4: Committed key was wrong: commit 0a00e959c3
  (2026-10-30T00:11:22Z, "SIGNING FIX (all lanes)") replaced it —
  cfe6ee2's key was a "wrong fresh debug.keystore (FE:08:0C:88)",
  not the installed builds' signer (SHA-256 BD:15:AD:5F…27:FF).
  Main keystore blob now a3c045dc…. All artifacts 22:49Z–00:11Z
  (incl. closure artifact 12939811485) cannot update-install over
  the phone build.
- B5: Both cures coexist on main unreconciled: committed keystore +
  generate-if-absent step that can never generate. Removing the
  keystore silently restores throwaway signing (B4's failure mode)
  while the step still reports success.

## Set C — owner, discipline, meter

- C1: Sarthak's only question (6509136030, "does this repo needs
  to be public?") never answered on the issue. Answer: no for CI;
  yes-or-Pro only for branch protection (protection/rulesets 403
  on free-plan private — Critic Charge 8, 6508980068). CI is
  advisory; red main is pushable.
- C2: Verdict-per-run discipline (6509244812) broken for the three
  runs that mattered: 40967732023 (rehearsal), 40968747267 (#59),
  40969279638 (run_number 62, main @8ccdce5 — second full-chain
  main run 3 min after closure run). No verdict comments exist.
- C3: Unfiltered `on: push` still live post-closure: 192 total runs
  (2026-10-30T00:13Z) → 224 (00:43Z), ~32 builds/30 min, all
  concluding failure at issue #2's test step, each uploading an
  UNTESTED artifact. Deferred paths-filter/concurrency package
  (6510186888) is now the burning item.
- C4: Retelling drift: "full chain proved" + missing Gate 7 line
  invites "the build works." Proven scope is exactly: assemble +
  archive + test-step execution on GitHub's machines. Tests have
  never passed in CI.

## Standing watch

BISS checks every new rescue-build run's verdict against its job
steps/logs as it lands; regressions in assemble/upload or changes
in the keystore step's guard behaviour get a same-hour charge on
issue #3.

— BISS, brutal critic, issue #3 cell
