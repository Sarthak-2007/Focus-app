# ISSUE #112 — CI ADDENDUM (to ISSUE-112-WAVE3-REPORT.md)

## CI verdicts (rescue-build workflow)
| Run | Tree | Verdict |
|---|---|---|
| 40986104406 | 0217dea (the 5-finding fix) | Assemble debug GREEN (APK artifact uploaded); **Unit tests FAILURE** |
| 40986339981 | 1491b94 (+ report) | FAILURE (Unit tests) |
| 41007489582 | 0a427ef (+ AudibilityProbe hardening) | FAILURE (Unit tests) |

## Bisection — the failure pre-exists this branch
Job logs are unreachable from the cell box (the egress proxy returns
empty bodies for the Azure blob log host; gh's log cache writes are
walled), so the failure was bisected by branch surgery via the Git
API instead:

| Diag branch | Tree | Verdict |
|---|---|---|
| agent/issue-112-diagA (e25151d) | #112 main-code changes, new test file REMOVED | FAILURE (Unit tests) |
| agent/issue-112-diagB (fb7119b) | #112 changes, ONLY SoundHapticsLivedTest kept | FAILURE (Unit tests) |
| agent/issue-112-diagC (= 0a00e95) | **PRISTINE origin/main, zero #112 changes** | **FAILURE (Unit tests)** — run 41003714832 |

**Conclusion: origin/main at 0a00e95 is itself red on the Unit tests
step.** Every #112 tree inherits that failure; no #112 tree can go
green until the base is repaired (merge-train / Konto territory, not
this cell's). What this cell CAN state on evidence:
- Assemble debug is green on the #112 code (artifact uploaded, run
  40986104406) — the code compiles and packages.
- The identical failing step (Unit tests) fires on the untouched
  base, on base+only-my-tests, and on base+my-code-without-my-tests.
- The exact failing test NAMES could not be extracted from this box
  (log hosts blocked, see above) — flagged for KARTE, who may have a
  working log route. If any #112 test is among the failures beyond
  the base set, it is most plausibly ParkPrintsSilentWhenThePhone-
  CannotBeHeard (the only new test touching composition + shadows);
  the AudibilityProbe hardening in 0a427ef already removes its
  likeliest hazard (unguarded service reads during composition).

Diag branches were deleted after this addendum was recorded; their
run records persist under the run IDs above.
