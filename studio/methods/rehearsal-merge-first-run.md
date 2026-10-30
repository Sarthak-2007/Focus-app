# THE REHEARSAL MERGE — FIRST RUN (proof of the instrument)

Takt, Methods · Oct 29, 2026, 23:31 IST
Tool: `design-refs/swarm/tools/rehearsal-merge.sh` (built this seating,
spec: methods/merge-pipeline-timing.md §4). Run on the three declared
Car-3 lanes, in Konto's map order (§5, times excluded — the seat was
refused, there is no lane):

    tools/rehearsal-merge.sh truth-3-sweep truth-3-shorts truth-3-sound

Wall time ~10 s. Exit 1 (six failed hunks — the census doing its job).
Scratch tree emitted at builds/rehearsal-merge/Focus-app; the live
census at builds/rehearsal-merge/census.txt (this file's appendix is
that census, verbatim).

## Amendments the build taught (also recorded in the script's header)

(a) GNU patch 2.7.6 prints NO per-hunk line for an exactly-landing
    hunk unless --verbose is passed. The first cut of the tool parsed
    a whole clean lane as silence (sweep: 12 files, 0 hunks counted)
    while its changes demonstrably landed (scratch RoutineDerive.kt
    md5 == the lane's). Caught by measuring, not by trusting: the
    census now runs patch --verbose and counts only Hunk lines.
(b) -E is passed so a lane's file deletion rehearses as a deletion.
(c) --- /+++ timestamps are stripped by field ($2); the first cut
    carried tab+timestamp through rel() into every section header.

## The census, in one paragraph

Sweep 19/19 CLEAN on the pristine base. Shorts 12 clean / 12 offset /
2 FAILED. Sound 11 clean / 4 offset / 4 FAILED. Six shared files —
exactly Konto's map set (MonolithModels, ParkDrawer,
MonolithAccessibilityService, ScreenshotHarnessTest,
MonolithViewModel, MainActivity). HAND flag correct for this run's
shape: one MIGRATION_11_12 in the field (Falk's), drops in as-is;
the two-migration composition (Falk + Tamm) arms when breaks joins.

## The six failed hunks — each one a real composition, none a surprise

1. **shorts · MonolithAccessibilityService hunk 1 (the import).**
   Falk adds `import com.example.data.GuardSettingsEntity`; the hunk's
   context names `import com.example.data.BlockedBrowserDomains` —
   the line Bodo's UNA-06 cure deleted (the import died with the
   fallback). Two correct cures, one context line. Hand compose:
   add Falk's import beside Bodo's deletion; one line each way.
2. **shorts · MonolithAccessibilityService hunk 6 (@@ -194).**
   Falk removes the local `val guardSettings =
   dao.getGuardSettingsSnapshot()` in the step-4 browser block —
   his gate hoists the read earlier (hunks 4/5 landed, offset +9).
   The hunk's context sits inside the block Bodo's A-4 edits had
   already moved. Rehearsal consequence, named: scratch today keeps
   BOTH the hoisted read and the local read — a harmless duplicate,
   not a compile break, and not Falk's intended text.
3. **sound · MainActivity hunk 1 (the UNA-02 call site).** Imke wires
   the social sled into the same ParkDrawer call Falk had just wired
   his shorts sled into (his hunk landed at 643, offset +4). Both
   sleds belong in one call; the rehearsal holds only Falk's.
4. **sound · ParkDrawer hunks 1–2** (the board comment + board region
   where Falk's sled and Bodo's line landed first). Her hunks 3–5
   landed (offset up to +79, after Falk's sled insertion) — the
   social sled IS in the scratch (`park_social_sled` present), the
   comment text is not.
5. **sound · ScreenshotHarnessTest hunk 1** (the same ParkDrawer
   call's param list Falk edited). Scratch's harness file equals
   Falk's version byte-for-byte — Imke's harness change is wholly
   absent, exactly as the failed hunk states.

## Union verification — scratch diff vs the union of the lanes' diffs

Method: canonical-exclude diff -rq sets + per-file byte comparison.

- **Changed-file SETS EQUAL**: trunk→scratch = 26 paths; union of
  the three lanes' trunk→lane sets = 26 paths. No file gained,
  none lost, none extra.
- **20 single-lane files: scratch byte-identical to the owning
  lane's file** — including all 6 lane-created files (LoadoutStore,
  SoundEffectsSetting, ShortsSniperTruthTest, ShortsToggleRenderTest,
  SoundAndSocialTruthTest, TruthCar3SweepTest), verified by cmp
  after correcting a path-mangling bug in my own check script (its
  first pass mislabelled the six as "deleted in both"; re-run with
  exact paths: all six NEWFILE-OK).
- **5 multi-lane files composed** (scratch differs from every
  contributing lane's version, as a composition must): MainActivity,
  MonolithModels, MonolithAccessibilityService, MonolithViewModel,
  ParkDrawer.
- **1 multi-lane file equals one lane exactly**: ScreenshotHarness
  Test == shorts' version — the arithmetic of failed hunk 5 above,
  not a silent loss.
- **The delta between the full union and the scratch is exactly the
  six failed hunks**, each named above with its content verified
  present/absent in scratch (GuardSettingsEntity import absent
  while `shortsSniperArmed` sits at :87 using the type — the
  scratch's AccessibilityService would NOT compile as it stands,
  which is precisely the sentence a rehearsal exists to print;
  UNA-02 MainActivity wiring absent; both sleds present in
  ParkDrawer). No other delta exists.

## Lane-hygiene sightings for the real merge (Ren's call, named here)

Sound's lane also diffs `gradle.properties` (the ratified lane
settings — expected) and `shots-redirect.txt` (screenshot-harness
state, not app code). Both applied CLEAN in the rehearsal; whether
they ride the real merge is Ren's ruling, not the tool's.

## What the tool still owes

- A run over all five car lanes + applist when Suki, Tamm and
  Ordner declare (the tool takes the lane list as arguments;
  ~1–2 min). The A-1 second pass (--with-warrant) runs with it.
- The full-six run will arm the MIGRATION COMPOSITION flag (Falk +
  Tamm both carry MIGRATION_11_12; verified in both trees tonight:
  MonolithDatabase.kt:181 / :190, version = 12 in both).

— Takt, Methods · Oct 29, 2026

## APPENDIX — the census, verbatim (builds/rehearsal-merge/census.txt, 23:31 run)

```
# REHEARSAL MERGE — census
# run: 2026-10-29 23:31 IST · tool: design-refs/swarm/tools/rehearsal-merge.sh (Takt, Methods)
# base: trunk /home/hatch/workspace/clone-inspect/Focus-app (copied fresh to /home/hatch/workspace/clone-inspect/builds/rehearsal-merge/Focus-app — trunk is never written)
# lane order: truth-3-sweep truth-3-shorts truth-3-sound
#
# LIMIT OF THIS INSTRUMENT — this census proves patches LAND, not that
# the result is TRUE. Order-sensitive compositions (A-2's seal tail,
# A-3's startPeekLease) can apply clean in the wrong order and still
# be false. The rehearsal tree is EVIDENCE, never the merge source:
# Ren's hand merge is the merge; this tree is what he diffs it against.
# Staleness: each lane's diff hash + newest source mtime are pinned
# below; a lane that edits after this run makes this a photograph of
# a moving car. A re-run costs ~1-2 minutes; re-run at every declaration.

## LANE truth-3-sweep  (diff sha256 e38899955b7d…, newest source mtime 2026-10-29 23:09 IST)
  CLEAN   app/src/main/AndroidManifest.xml — hunk 1: Hunk #1 succeeded at 21.
  CLEAN   app/src/main/java/com/example/MainActivity.kt — hunk 1: Hunk #1 succeeded at 191.
  CLEAN   app/src/main/java/com/example/MainActivity.kt — hunk 2: Hunk #2 succeeded at 551.
  CLEAN   app/src/main/java/com/example/data/LoadoutStore.kt — hunk 1: Hunk #1 succeeded at 1.
  CLEAN   app/src/main/java/com/example/data/MonolithModels.kt — hunk 1: Hunk #1 succeeded at 684.
  CLEAN   app/src/main/java/com/example/data/RoutineDerive.kt — hunk 1: Hunk #1 succeeded at 274.
  CLEAN   app/src/main/java/com/example/service/MonolithAccessibilityService.kt — hunk 1: Hunk #1 succeeded at 9.
  CLEAN   app/src/main/java/com/example/service/MonolithAccessibilityService.kt — hunk 2: Hunk #2 succeeded at 31.
  CLEAN   app/src/main/java/com/example/service/MonolithAccessibilityService.kt — hunk 3: Hunk #3 succeeded at 185.
  CLEAN   app/src/main/java/com/example/service/MonolithAccessibilityService.kt — hunk 4: Hunk #4 succeeded at 333.
  CLEAN   app/src/main/java/com/example/ui/MonolithViewModel.kt — hunk 1: Hunk #1 succeeded at 301.
  CLEAN   app/src/main/java/com/example/ui/MonolithViewModel.kt — hunk 2: Hunk #2 succeeded at 1920.
  CLEAN   app/src/main/java/com/example/ui/components/ParkDrawer.kt — hunk 1: Hunk #1 succeeded at 106.
  CLEAN   app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt — hunk 1: Hunk #1 succeeded at 72.
  CLEAN   app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt — hunk 2: Hunk #2 succeeded at 104.
  CLEAN   app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt — hunk 3: Hunk #3 succeeded at 180.
  CLEAN   app/src/main/res/xml/backup_rules.xml — hunk 1: Hunk #1 succeeded at 1.
  CLEAN   app/src/main/res/xml/data_extraction_rules.xml — hunk 1: Hunk #1 succeeded at 1.
  CLEAN   app/src/test/java/com/example/TruthCar3SweepTest.kt — hunk 1: Hunk #1 succeeded at 1.
  lane totals: files=12 clean=19 offset/fuzz=0 failed=0 binary=0 (.orig backups scrubbed: 0)

## LANE truth-3-shorts  (diff sha256 c4198916dca1…, newest source mtime 2026-10-29 23:09 IST)
  OFFSET  app/src/main/java/com/example/MainActivity.kt — hunk 1 applies with offset: Hunk #1 succeeded at 643 (offset 4 lines).
  CLEAN   app/src/main/java/com/example/data/MonolithDao.kt — hunk 1: Hunk #1 succeeded at 248.
  CLEAN   app/src/main/java/com/example/data/MonolithDatabase.kt — hunk 1: Hunk #1 succeeded at 22.
  CLEAN   app/src/main/java/com/example/data/MonolithDatabase.kt — hunk 2: Hunk #2 succeeded at 165.
  CLEAN   app/src/main/java/com/example/data/MonolithDatabase.kt — hunk 3: Hunk #3 succeeded at 229.
  OFFSET  app/src/main/java/com/example/data/MonolithModels.kt — hunk 1 applies with offset: Hunk #1 succeeded at 744 (offset 16 lines).
  CLEAN   app/src/main/java/com/example/data/MonolithRepository.kt — hunk 1: Hunk #1 succeeded at 765.
  FAILS   app/src/main/java/com/example/service/MonolithAccessibilityService.kt — hunk 1: Hunk #1 FAILED at 10.
  OFFSET  app/src/main/java/com/example/service/MonolithAccessibilityService.kt — hunk 2 applies with offset: Hunk #2 succeeded at 24 (offset -1 lines).
  OFFSET  app/src/main/java/com/example/service/MonolithAccessibilityService.kt — hunk 3 applies with offset: Hunk #3 succeeded at 77 (offset 9 lines).
  OFFSET  app/src/main/java/com/example/service/MonolithAccessibilityService.kt — hunk 4 applies with offset: Hunk #4 succeeded at 171 (offset 9 lines).
  OFFSET  app/src/main/java/com/example/service/MonolithAccessibilityService.kt — hunk 5 applies with offset: Hunk #5 succeeded at 188 (offset 9 lines).
  FAILS   app/src/main/java/com/example/service/MonolithAccessibilityService.kt — hunk 6: Hunk #6 FAILED at 194.
    failing hunk head (app/src/main/java/com/example/service/MonolithAccessibilityService.kt.rej):
    | --- app/src/main/java/com/example/service/MonolithAccessibilityService.kt
    | +++ app/src/main/java/com/example/service/MonolithAccessibilityService.kt
    | @@ -10,6 +10,7 @@
    |  import com.example.MainActivity
    |  import com.example.data.AppBlockTier
    |  import com.example.data.BlockedBrowserDomains
    | +import com.example.data.GuardSettingsEntity
    |  import com.example.data.MonolithDatabase
  OFFSET  app/src/main/java/com/example/ui/MonolithViewModel.kt — hunk 1 applies with offset: Hunk #1 succeeded at 665 (offset 9 lines).
  OFFSET  app/src/main/java/com/example/ui/MonolithViewModel.kt — hunk 2 applies with offset: Hunk #2 succeeded at 1315 (offset 9 lines).
  OFFSET  app/src/main/java/com/example/ui/MonolithViewModel.kt — hunk 3 applies with offset: Hunk #3 succeeded at 1939 (offset 13 lines).
  CLEAN   app/src/main/java/com/example/ui/components/ParkDrawer.kt — hunk 1: Hunk #1 succeeded at 81.
  OFFSET  app/src/main/java/com/example/ui/components/ParkDrawer.kt — hunk 2 applies with offset: Hunk #2 succeeded at 127 (offset 6 lines).
  OFFSET  app/src/main/java/com/example/ui/components/ParkDrawer.kt — hunk 3 applies with offset: Hunk #3 succeeded at 493 (offset 6 lines).
  OFFSET  app/src/main/java/com/example/ui/components/ParkDrawer.kt — hunk 4 applies with offset: Hunk #4 succeeded at 583 (offset 6 lines).
  CLEAN   app/src/test/java/com/example/RoutinesMigrationChainTest.kt — hunk 1: Hunk #1 succeeded at 331.
  CLEAN   app/src/test/java/com/example/ScreenshotHarnessTest.kt — hunk 1: Hunk #1 succeeded at 336.
  CLEAN   app/src/test/java/com/example/ScreenshotHarnessTest.kt — hunk 2: Hunk #2 succeeded at 346.
  CLEAN   app/src/test/java/com/example/ShortsSniperTruthTest.kt — hunk 1: Hunk #1 succeeded at 1.
  CLEAN   app/src/test/java/com/example/ShortsToggleRenderTest.kt — hunk 1: Hunk #1 succeeded at 1.
  CLEAN   app/src/test/java/com/example/SliceEMigrationChainTest.kt — hunk 1: Hunk #1 succeeded at 255.
  lane totals: files=13 clean=12 offset/fuzz=12 failed=2 binary=0 (.orig backups scrubbed: 5)

## LANE truth-3-sound  (diff sha256 f416db69a058…, newest source mtime 2026-10-29 23:12 IST)
  FAILS   app/src/main/java/com/example/MainActivity.kt — hunk 1: Hunk #1 FAILED at 645.
    failing hunk head (app/src/main/java/com/example/MainActivity.kt.rej):
    | --- app/src/main/java/com/example/MainActivity.kt
    | +++ app/src/main/java/com/example/MainActivity.kt
    | @@ -645,6 +645,12 @@
    |                          onDismiss = { closePark(dismissReceipt = false) },
    |                          onFixClick = { item -> openFixPane(item) },
    |                          onToggleSound = { viewModel.toggleSound() },
    | +                        // Truth Car 3 (UNA-02): the SOCIAL SITES
    | +                        // board line's sled — reads and writes the
  CLEAN   app/src/main/java/com/example/service/SessionSealTransition.kt — hunk 1: Hunk #1 succeeded at 95.
  CLEAN   app/src/main/java/com/example/service/SessionSealTransition.kt — hunk 2: Hunk #2 succeeded at 302.
  CLEAN   app/src/main/java/com/example/service/SoundEffectsSetting.kt — hunk 1: Hunk #1 succeeded at 1.
  CLEAN   app/src/main/java/com/example/service/TactileSoundEngine.kt — hunk 1: Hunk #1 succeeded at 41.
  CLEAN   app/src/main/java/com/example/service/TactileSoundEngine.kt — hunk 2: Hunk #2 succeeded at 279.
  CLEAN   app/src/main/java/com/example/service/TactileSoundEngine.kt — hunk 3: Hunk #3 succeeded at 325.
  CLEAN   app/src/main/java/com/example/ui/MonolithViewModel.kt — hunk 1: Hunk #1 succeeded at 25.
  CLEAN   app/src/main/java/com/example/ui/MonolithViewModel.kt — hunk 2: Hunk #2 succeeded at 202.
  OFFSET  app/src/main/java/com/example/ui/MonolithViewModel.kt — hunk 3 applies with offset: Hunk #3 succeeded at 1286 (offset 13 lines).
  FAILS   app/src/main/java/com/example/ui/components/ParkDrawer.kt — hunk 1: Hunk #1 FAILED at 80.
  FAILS   app/src/main/java/com/example/ui/components/ParkDrawer.kt — hunk 2: Hunk #2 FAILED at 125.
  OFFSET  app/src/main/java/com/example/ui/components/ParkDrawer.kt — hunk 3 applies with offset: Hunk #3 succeeded at 350 (offset 10 lines).
  OFFSET  app/src/main/java/com/example/ui/components/ParkDrawer.kt — hunk 4 applies with offset: Hunk #4 succeeded at 384 (offset 10 lines).
  OFFSET  app/src/main/java/com/example/ui/components/ParkDrawer.kt — hunk 5 applies with offset: Hunk #5 succeeded at 672 (offset 79 lines).
    failing hunk head (app/src/main/java/com/example/ui/components/ParkDrawer.kt.rej):
    | --- app/src/main/java/com/example/ui/components/ParkDrawer.kt
    | +++ app/src/main/java/com/example/ui/components/ParkDrawer.kt
    | @@ -80,7 +80,9 @@
    |   * except the FIX key, radii 16 (panel) and 8 (key) only, orange
    |   * zero. The board is nine ruled lines over the Watch Store; the
    |   * only object is the FIX key, present only while a permission lack
    | - * exists. Below the board: the Sound sled (the shipped control,
    | + * exists. (Truth Car 3, UNA-02, seats ONE exception on the board
  FAILS   app/src/test/java/com/example/ScreenshotHarnessTest.kt — hunk 1: Hunk #1 FAILED at 345.
    failing hunk head (app/src/test/java/com/example/ScreenshotHarnessTest.kt.rej):
    | --- app/src/test/java/com/example/ScreenshotHarnessTest.kt
    | +++ app/src/test/java/com/example/ScreenshotHarnessTest.kt
    | @@ -345,7 +345,9 @@
    |                      parkReturnTick = 0,
    |                      onDismiss = {},
    |                      onFixClick = {},
    | -                    onToggleSound = {}
    | +                    onToggleSound = {},
  CLEAN   app/src/test/java/com/example/SoundAndSocialTruthTest.kt — hunk 1: Hunk #1 succeeded at 1.
  CLEAN   gradle.properties — hunk 1: Hunk #1 succeeded at 1.
  CLEAN   shots-redirect.txt — hunk 1: Hunk #1 succeeded at 1.
  lane totals: files=10 clean=11 offset/fuzz=4 failed=4 binary=0 (.orig backups scrubbed: 4)

## SHARED FILES (touched by more than one lane in this run)
  app/src/main/java/com/example/data/MonolithModels.kt — truth-3-sweep truth-3-shorts
  app/src/main/java/com/example/ui/components/ParkDrawer.kt — truth-3-sweep truth-3-shorts truth-3-sound
  app/src/main/java/com/example/service/MonolithAccessibilityService.kt — truth-3-sweep truth-3-shorts
  app/src/test/java/com/example/ScreenshotHarnessTest.kt — truth-3-shorts truth-3-sound
  app/src/main/java/com/example/ui/MonolithViewModel.kt — truth-3-sweep truth-3-shorts truth-3-sound
  app/src/main/java/com/example/MainActivity.kt — truth-3-sweep truth-3-shorts truth-3-sound

## HAND FLAGS — compositions the rehearsal must never silently perform
  MIGRATION — one lane carries MIGRATION_11_12: truth-3-shorts. It drops
  in as-is this run; if a second migration lane joins later, composing
  the single shipped object becomes a HAND item (see Konto's map §2).

## TOTALS
  hunks: clean=42 offset/fuzz=16 failed=6 · binary files=0
  scratch vs trunk: 26 paths differ (diff -rq, canonical excludes)
  scratch tree: /home/hatch/workspace/clone-inspect/builds/rehearsal-merge/Focus-app
  per-lane patches: /home/hatch/workspace/clone-inspect/builds/rehearsal-merge/patches/<lane>.patch (raw: <lane>.raw.diff) · rejects: /home/hatch/workspace/clone-inspect/builds/rehearsal-merge/rejects/
# END OF CENSUS — remember the limit: patches landing != truth.
```

## ADDENDUM — the A-1 second pass (23:33 IST, same seating)

    tools/rehearsal-merge.sh --with-warrant truth-3-sweep truth-3-shorts truth-3-sound

Konto's §5 order (warrant first). Result: warrant 7/7 CLEAN on the
pristine base — its append took the armedMono anchor exactly. When
shorts then reached the same anchor, Falk's append hunk FAILED at 68
(the anchor is occupied; his remaining service hunks rode offsets up
to +39, failed total for the run: 8 vs 6). The census printed the
A-1 NAMED COLLISION block with both functions reported present in
the scratch (Stempel's notePipelineProof at the anchor; Falk's
shortsSniperArmed carried by his later hunks) and the standing
sentence: stacked != composed. The instrument behaves as specced on
the hard case: the double-append is a named collision with hunk
numbers, never a silent stack. After this pass the canonical
three-lane run was re-executed, so builds/rehearsal-merge/ again
holds the 23:31 census's tree (diff hashes in the census header
pin the lane states; the lanes did not move between runs).
