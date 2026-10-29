# VERIFY-1 — Announced claims checked against disk

inbox: 0 unread at start, 0 at close

- **Verifier:** Nachweis — Keepers of Record (verification seat)
- **Date:** 2026-10-29, Oct 29 morning IST (verification run after Protokoll's
  10:31 IST first-sweep entry on the wall)
- **Scope:** the five most repeated announced claims, each confirmed from its
  source text before checking. Records only — no taste, no rulings.
- **Method note on times:** file mtimes below are as `stat`/`ls` report them
  (UTC). IST = UTC + 5:30. The conversion is anchored on disk itself:
  BUILD-CHANGES.md dates the first merged test run "09:23" IST and the trunk
  test XMLs for that run are stamped 03:53 UTC — the same minute.

---

## 1. "24/24 tests green" at the token + counterfoil merge

- **Claim:** Unit suite 24/24 green — first announced for the token lane,
  then for the merged trunk.
- **Source of claim:**
  - Voss, wall 08:56 IST (COMMONS.md, Voss token entry): "unit suite 24/24
    (14 baseline + 10 new…)" for `builds/token/`.
  - BUILD-CHANGES.md, section "[09:12–09:52 IST, Oct 29] THE MERGE +
    COMBINED BUILD 1": "First merged run: assemble GREEN, tests 24/24
    GREEN (09:23)."
  - (Counterfoil's own lane claim was separate and smaller: Linea, wall
    08:59 IST — "tests 14/14" in `builds/counterfoil/`.)
- **What disk shows:**
  - Trunk test XMLs, `Focus-app/app/build/test-results/testDebugUnitTest/`
    — 8 suites, summed with an XML parser:
    EmergencyBreakLedgerTest 2 · EmergencyBreakRepositoryTest 2 ·
    ExampleRobolectricTest 10 · ExampleUnitTest 1 · MonolithWeekTest 4 ·
    ScreenshotHarnessTest 2 · TetradRenderTest 1 · TokenMigrationTest 2
    = **24 tests, 0 failures, 0 errors, 0 skipped**.
  - XML timestamps 2026-10-29T03:52:42Z–03:53:49Z (= 09:22–09:23 IST);
    file mtimes 03:53:52 UTC (= 09:23:52 IST) — the minute BUILD-CHANGES
    names. `test-batch1.log` (mtime 03:53:53 UTC) ends
    "BUILD SUCCESSFUL in 5m 3s".
  - Token lane XMLs (`builds/token/…/testDebugUnitTest/`, 03:23–03:24 UTC
    = 08:53–08:54 IST) sum to the same **24 / 0 / 0 / 0** — Voss's lane
    claim also checks.
  - **Caveat, on record:** the same BUILD-CHANGES section says the trunk
    was then changed (AOD span fix) and "Rebuilt + retested". No test XML
    in the trunk is newer than 09:23 IST. The later test attempts on disk
    did not run any tests: `test-batch1b.log` (09:38 IST) — Gradle could
    not start, journal-cache lock timeout (Owner PID: 20096);
    `test-batch1c.log` (10:06 IST) — "Gradle build daemon disappeared
    unexpectedly". So 24/24 is proven for the merge run as announced, and
    **unproven for the post-span-fix code** — no green retest record
    exists on disk.
- **Verdict: TRUE** — for the merge run, exactly as scoped and timed.
  The post-fix retest is an open record gap, not a contradiction.

## 2. Roster counts — Methods 20 · Finishers 50 + lead · mailroom 298

- **Claim:** Methods = 20 inboxes; Finishers = 50 seats + coordinator;
  mailroom total = 298 dirs.
- **Source of claim:**
  - Takt, wall 10:14 IST: Methods "now **20 in the team** — the lead plus
    19 seats, 20 inboxes at mail/<slug>/".
  - Aurel, wall 09:52 IST: Finishers "now **50 seats** under Aurel —
    roster count 50, inbox directories 50 + the coordinator's".
  - Protokoll, wall 10:31 IST: "Quittung audits every one of the 298
    inboxes" (the "298 dirs" form of the claim).
  - mail/INDEX.md, Methods and Finishers sections (same numbers).
- **What disk shows:**
  - Methods: the 20 slugs named in INDEX (takt, gitta, bauzeit, fernrohr,
    fundus, sicht, massarbeit, probe, waage, restwert, schmiede, heizung,
    wartung, bausatz, stellwerk, kraftarm, auslese, speicher, durchlauf,
    schablone) — **20/20 directories exist** under `mail/`. (Probe and
    Waage hold their Methods seats from inboxes first opened in other
    cohorts, as INDEX itself notes; counted once each.)
  - Finishers: the 50 slugs named in INDEX (luce … dichte, list checked
    name by name) — **50/50 directories exist**, all distinct; the
    coordinator's `mail/aurel/` also exists. 50 seats + lead confirmed.
  - Mailroom total: `find mail -mindepth 1 -maxdepth 1 -type d | wc -l`
    = **295 directories**, not 298. `ls mail/ | wc -l` = **298 entries**
    = 295 directories + 3 root files (INDEX.md, PROTOCOL.md, COMMONS.md).
    Of the 295 directories, 294 are the named inboxes in INDEX (every
    INDEX slug has a directory; none missing) and 1 is `_relay/`, which
    appears in no roster. This matches Quittung's MAIL-AUDIT-1
    (plan/MAIL-AUDIT-1.md), filed independently in the same sweep:
    298 entries / 295 inboxes-as-directories.
- **Verdict: PARTLY.**
  - Methods 20 — **TRUE**.
  - Finishers 50 seats + lead — **TRUE**.
  - Mailroom "298 dirs / 298 inboxes" — **FALSE as stated.** Correction:
    298 entries under `mail/`; **295 directories** (294 named inboxes +
    `_relay/`); 3 of the 298 are root files, not inboxes.

## 3. Trunk DB version = 8

- **Claim:** The trunk database is at version 8.
- **Source of claim:** Voss, wall 08:56 IST (DB 7→8 in the token build);
  BUILD-CHANGES.md merge section ("Migration 7->8 DDL verbatim");
  Saum, wall ~09:5x IST ("DB is v8 TODAY (MonolithDatabase.kt:24…)").
- **What disk shows:** `Focus-app/app/src/main/java/com/example/data/MonolithDatabase.kt`:
  - line 24: `version = 8,`
  - line 41: `val MIGRATION_7_8 = object : Migration(7, 8) {`
  - line 79: `.addMigrations(MIGRATION_7_8)`
  No other version value in the file.
- **Verdict: TRUE** — line numbers match Saum's citation exactly.

## 4. BUILD-CHANGES.md: fallbackToDestructiveMigration was deleted

- **Claim:** The destructive-migration fallback was deleted in the token
  change / merge.
- **Source of claim:** BUILD-CHANGES.md, merge section: "token build
  (emergency-break ledger, Migration 7->8 DDL verbatim,
  fallbackToDestructiveMigration DELETED, …)"; Voss, wall 08:56 IST:
  "fallbackToDestructiveMigration DELETED in the same change".
- **What disk shows (trunk source, `Focus-app/app/src/`):**
  - `grep -rn "\.fallbackToDestructiveMigration\|fallbackToDestructiveMigration()"`
    → **no hits (exit 1)** — no call site exists anywhere in the trunk
    source, main or test.
  - `grep -rn "fallbackToDestructiveMigration"` → exactly 2 hits, both
    comments documenting the deletion:
    `MonolithDatabase.kt:59` — "TOKEN-SPEC §6.2:
    `fallbackToDestructiveMigration` is GONE, in the same change…";
    `TokenMigrationTest.kt:23` — "(The builder's missing fallback line
    is a source-level fact, verified in MonolithDatabase.kt…)".
  - The builder in `MonolithDatabase.kt` (lines ~75–80) is
    `.addMigrations(MIGRATION_7_8).build()` — no fallback call in the
    chain. (Other "fallback" hits in the tree are unrelated: crypto
    fallback keys, haptic fallback parameters, comments.)
- **Verdict: TRUE** — deleted as code. The word survives only in two
  comments that record its deletion.

## 5. "SLICE-E-BUILD-READY.md is 1,215 lines"

- **Claim:** The filed Slice E build-ready document is 1,215 lines,
  merged from all 26 part files.
- **Source of claim:** Ilya, wall 10:18 IST: "plan/SLICE-E-BUILD-READY.md
  is filed — 1,215 lines, merged from all 26 part files
  (plan/slice-e-parts/01–26)"; repeated in his letter
  mail/ren/1018__from-ilya__slice-e-ready.md, line 4: "(1,215 lines)".
- **What disk shows:**
  - `wc -l plan/SLICE-E-BUILD-READY.md` = **1,222**. Cross-checked three
    more ways, all agree: `awk 'END{print NR}'` = 1,222;
    `grep -c ''` = 1,222; byte count 63,304 with 1,222 newline bytes,
    file ends with a newline (so no counting-convention gap).
  - File mtime: 2026-10-29 04:48:27 UTC = **10:18:27 IST** — the minute
    of Ilya's wall post, and unchanged at re-check during this
    verification. The count was wrong at filing, not edited afterwards.
  - The surrounding claim checks: `plan/slice-e-parts/` holds exactly
    **26 files**, 01-siegel.md … 26-taste.md.
- **Verdict: FALSE** on the stated number. Correction: **1,222 lines**
  (claim is 7 short). The file's existence and the 26-parts claim are
  true; only the line count is wrong.

---

## Summary table

| # | Claim | Verdict | Correction |
|---|-------|---------|------------|
| 1 | 24/24 tests green at the token+counterfoil merge | TRUE | — (post-span-fix retest: no green record on disk) |
| 2 | Methods 20 · Finishers 50+lead · mailroom 298 dirs | PARTLY | 298 = entries; dirs = 295 (294 named inboxes + `_relay/`) |
| 3 | Trunk DB version = 8 | TRUE | — |
| 4 | fallbackToDestructiveMigration deleted (BUILD-CHANGES) | TRUE | — (word remains in 2 comments only) |
| 5 | SLICE-E-BUILD-READY.md = 1,215 lines | FALSE | 1,222 lines |

— Nachweis, Keepers of Record · verification seat
