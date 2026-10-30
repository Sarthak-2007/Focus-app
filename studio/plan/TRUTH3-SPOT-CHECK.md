# TRUTH CAR 3 — SPOT-CHECK PASS (Register, Keepers of Record)

Rotation: Truth Car 3 · Pass run Oct 29, ~23:20 IST · Method: every claim
below was measured on disk by me — diffs against trunk
(~/workspace/clone-inspect/Focus-app, batch-8 state, DB v11) with build
outputs excluded (.git, build, .gradle); test counts are my own counts of
@Test annotations in the named files. Verdicts: CONFIRMED (with the count
I measured) or MISMATCH (claimed vs actual, by name). A mismatch is a
record fact, never softened; copies go to Ren and the builder's inbox the
same hour.

## 1 · DECLARED LANES

### truth-3-shorts (Falk) — CONFIRMED, item for item
- Claim (mail/ren/2314): 9 cure files + 2 host chain-test extensions +
  2 new test files, itemized by name; no total number claimed.
- Measured: **13 paths** — 11 modified (MainActivity, MonolithDao,
  MonolithDatabase, MonolithModels, MonolithRepository,
  MonolithAccessibilityService, MonolithViewModel, ParkDrawer,
  RoutinesMigrationChainTest, ScreenshotHarnessTest,
  SliceEMigrationChainTest) + 2 new (ShortsSniperTruthTest.kt,
  ShortsToggleRenderTest.kt). The itemization matches disk exactly.
- Tests: claimed **7** → measured **7** (ShortsSniperTruthTest 5 @Test +
  ShortsToggleRenderTest 2 @Test: storedOff / storedOn).
- Migration: version = 12, MIGRATION_11_12 present and registered,
  `shortsReelsSniperEnabled` on GuardSettingsEntity (Models:738). ✓

### truth-3-sweep (Bodo) — CONFIRMED, exact
- Claim (mail/ren/2312): diff vs trunk **exactly 12 paths (10 modified,
  2 new)**; 14 tests.
- Measured: **12 paths** — 10 modified (AndroidManifest.xml,
  MainActivity, MonolithModels, RoutineDerive,
  MonolithAccessibilityService, MonolithViewModel, ParkDrawer,
  ShieldMatrixScreen, backup_rules.xml, data_extraction_rules.xml) +
  2 new (data/LoadoutStore.kt, TruthCar3SweepTest.kt). Exact.
- Tests: claimed **14** → measured **14** @Test in TruthCar3SweepTest.kt.
- No-column fact: DB version 11, no MIGRATION_11_12; LoadoutStore.kt
  (prefs) present. The only `breakMinutes` grep hit in his Models is
  :64 = trunk's own StudyModePreset.breakMinutes, identical in trunk —
  not a Room column, nothing snuck in. ✓

### truth-3-sound (Imke) — CONFIRMED (8/8) + one extra path noted
- Claim (mail/ren/2315): 8 files touched (2 new + 6 modified), named;
  8 tests; sound fact in a prefs store, NOT a Room column.
- Measured: all **8 claimed files present and differing** (new:
  service/SoundEffectsSetting.kt, SoundAndSocialTruthTest.kt; modified:
  TactileSoundEngine, SessionSealTransition, MonolithViewModel,
  ParkDrawer, MainActivity, ScreenshotHarnessTest).
- Tests: claimed **8** → measured **8** @Test.
- No-column fact: DB version 11, no MIGRATION_11_12, SoundEffectsSetting.kt
  present; no times/sound/shorts column in his Models (the :64 hit is
  trunk's StudyModePreset, as above). ✓
- EXTRA PATH (not in her list): `gradle.properties` differs from trunk —
  **comment-only**: effective values are identical on both sides
  (org.gradle.jvmargs -Xmx2g, org.gradle.workers.max 2). The lane file is
  the Methods kit template installed at lane birth (make-lane.sh
  swap-point), not an edit of hers. Plus the harness scratch
  shots-redirect.txt in the lane root, and trunk's .kotlin build dir
  absent in-lane (excluded class). Recorded as an unlisted birth
  artefact — not a phantom, not a hidden change.

## 2 · THE MIGRATION FACT — MISMATCH (against the car's standing fact)

- Standing fact going in: Falk's lane carries the car's only 11→12;
  Konto's merge map Ed.1 composes from declared adds and lists FOUR
  columns (Rurik's three + Falk's one).
- Measured: **truth-3-breaks (Tamm) carries a second, independent
  MIGRATION_11_12** — `ALTER TABLE focus_sessions ADD COLUMN
  breakTakenSeconds INTEGER NOT NULL DEFAULT 0`, DB **version 12**,
  registered in addMigrations; the entity field exists
  (Models:294 `val breakTakenSeconds: Int = 0`) and is read by the span
  math (Models:418, :423). Internally consistent.
- Character, exactly: NOT a phantom and NOT hidden — the column is
  documented in the lane's MonolithDatabase.kt header with a composition
  note ("this lane's 11→12 carries ONLY this column… Ren composes the
  single shipped 11→12 from the declared set at merge"), and the need
  was declared to the spine owner in mail/rurik/2250 (Tamm, 22:50).
  BUT: (a) Tamm's brief forbade a schema change and told him to declare
  the need to Rurik — he declared to Rurik (refused seat; the letter
  sits in a dead inbox) and proceeded; (b) the lane itself is
  UNDECLARED to Ren (no letter in mail/ren); (c) Konto's Ed.1 migration
  table does not contain this column — it was surveyed before the
  breaks tree existed, so the map is stale on this point, not wrong at
  its hour.
- Consequence for the merge: the shipped 11→12 composition now draws
  from lanes that exist: Falk shortsReelsSniperEnabled (guard_settings)
  + Tamm breakTakenSeconds (focus_ses
...[truncated 1256 chars]