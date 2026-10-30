# ISSUE #133 — WAVE-3 REPORT (recomposed on current main)

**Lead:** GESTALT cluster (Wave-3B). The original lane (716dafe +
93c5b11, base 0a00e95) was overtaken repeatedly by main's movement —
issue #107 landed its own LoadoutRules / setAppCategory composition,
the classifier was rewritten in whole-token discipline by the
STOSS-34 reconciliation, and the design-revamp wave moved
MonolithModels and ExampleRobolectricTest. This branch is a single
commit on current main carrying ONLY the residual #133 cures; the
rest is dispositioned FIXED-ELSEWHERE. The recipe was proven in CI
run 40999903899 (407 tests, failures = shared floor only) before
this final composition; the study rule is expressed in the
classifier's current token-sequence discipline (matches(...)) and
checked against every AppCatalogReconcileTest L2 expectation.

## Dispositions (5 findings)
1. Deep Study rations lectures — **FIXED here**: LoadoutRules STUDY
   holds BROWSERS/YouTube/Chrome + EDUCATION OPEN (sniper remains
   the feed guard); STUDY subtitle corrected to match;
   ExampleRobolectricTest updated to the new law.
2. No education category / no correction — **FIXED here +
   FIXED-ELSEWHERE**: EDUCATION + deterministic Anki home + study
   signals added to the ONE classifier (AppCategorizer); the
   correction path itself (setAppCategory + correctable Shield
   rows) already landed via issue #107.
3. Mid-session rescue / silent revocation — **SPLIT**:
   mid-session rescue STANDS-BLOCKED (any door is a HOLD weakening;
   priced options in the closing comment); between-session hand
   revocation's silence cured FIXED-ELSEWHERE (#107 preview+confirm
   prints the counts before anything applies); routine revocation
   STANDS-BLOCKED on the routines-rethink freeze.
4. Dead "allowed study sites" list — **FIXED here by removal**
   (list + its one test assertion; no allowlist exists in
   enforcement, nothing consumed it).
5. SLEEP locks the study kit — **STANDS-BLOCKED** (routine freeze),
   priced: make LoadoutRules' SLEEP branch category-aware
   (EDUCATION -> OPEN), ~3 lines + tests; pinned unchanged by
   StudyOnPhoneRulesTest in the meantime. Partial relief from
   finding 2: Anki is at least filed EDUCATION now.

## Not shipped from the original lane, deliberately
HandTierStore + AppCategorization (a second classifier) + the
repository sovereignty seam: superseded by #107's preview+confirm
law (preview == commit). Two classifiers on one tree is exactly
the drift #107's merge ruling killed; this composition keeps ONE.
