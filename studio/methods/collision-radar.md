# COLLISION RADAR — which files the merge train will fight over, seen today

Gitta, Methods (version-control tooling) · Sweep 2, commission 2 · measured 2026-10-29, ~10:20–10:45 IST
Method: `diff -rq` + canonical `diff -ruN` of every lane tree vs current trunk, source set = tree minus build/, .gradle/, .git/, .kotlin/, local.properties, *.apk (tools/lane-diff.sh). Base manifests: builds/<lane>/BASE-MANIFEST.sha256 (all six lanes, confidence: reconstructed — see each header). Full diffs: merge-review/token.diff, merge-review/routines.diff (+ .summary.json each), both hash-verified against fresh canonical runs.

## Headlines

1. **token ∩ routines = 11 of 11.** Every file the token lane touched is also changed by the routines lane. Token is already hand-merged into trunk, so this is not a lane-vs-lane fight — it means the routines merge re-opens every single file the token merge just settled, against a trunk that has since moved again. There is no cheap order here: routines merges against post-token trunk, file by file, all 11.
2. **LiveWire.kt is only-in-trunk for EVERY lane.** All six lane bases predate `app/src/main/java/com/example/service/LiveWire.kt`. Every future merge must reconcile the lane-side absence of LiveWire (and its registrations) — this is systemic, not incidental. Any lane whose code calls LiveWire-adjacent registries will surface it at merge time, not before.
3. **Live-vs-live content collisions today: zero — by luck of shape, not by design.** The three other live lanes (drawer, shieldpick, slide) are purely additive with disjoint new files. The collisions are latent (below), and they all detonate on the same two files: `data/MonolithDatabase.kt` and `data/MonolithRepository.kt`.
4. **Routines is the merge train's critical path.** 20 changed + 8 added files, shared-code flags data=4, viewmodel=1, views=2 — including the DB v8→9 migration (MIGRATION_8_9). Everything else should merge around it, not through it.

## Per-lane state vs trunk-current (measured)

| lane | changed | only-in-lane | only-in-base | shape |
|---|---|---|---|---|
| token | 11 | 0 | 1 (LiveWire.kt) | merged already; residual is trunk-side progress (see finding in token.summary.json) |
| counterfoil | 0 | 0 | 6 | merged; identical to trunk where overlapping |
| routines | 20 | 8 | 1 (LiveWire.kt) | LIVE; the big one (+2557/−1115) |
| drawer | 0 | 10 | 2 | LIVE; purely additive |
| shieldpick | 0 | 3 | — | record lane (cap chosen, knob unmerged); additive |
| slide | 0 | 3 | 6 | LIVE; work sits in a .tmp file (below) |

## The 11 shared files (token's set = routines' subset)

app/src/main/java/com/example/MainActivity.kt · data/MonolithModels.kt · data/MonolithRepository.kt · service/MonolithAccessibilityService.kt · service/MonolithShieldService.kt · service/TactileSoundEngine.kt · ui/MonolithViewModel.kt · ui/components/AirlockAndDisarmOverlays.kt · ui/screens/FocusSequencerScreen.kt · ui/views/BlockitCoreViews.kt · app/src/test/java/com/example/ScreenshotHarnessTest.kt

Routines additionally changes: AndroidManifest.xml, data/MonolithDao.kt, data/MonolithDatabase.kt (v8→9), service/MonolithBootReceiver.kt, service/MonolithNotificationListener.kt, service/RoutineAlarmReceiver.kt, service/RoutineScheduler.kt, ui/screens/ShieldMatrixScreen.kt, ui/views/BlockitSuitcaseAndSwitchesViews.kt — and adds data/RoutineDerive.kt, service/RoutineHeartbeat.kt, 5 test files, shots-redirect.txt.

## Latent collisions (not visible in today's diff counts)

- **slide → TactileDeckScreen.kt.** Slide's work lives in `ui/screens/TactileDeckScreen.kt.tmp`, a sibling of the tracked file — it registers as zero changed today. The day it is promoted to the real file it lands on TactileDeckScreen.kt while the Deck's neighbours (BlockitCoreViews, FocusSequencerScreen) are exactly what routines is rewriting. Promote it only after the routines merge, and diff it against post-merge trunk, not against slide's stale base.
- **drawer → MonolithDatabase / MonolithRepository.** Drawer's three new data stores (DayWordStore.kt, DrawerDay.kt, WallAccrual.kt) are additive today, but wiring them in needs registration in the database and repository — the two files routines is heaviest in (DB v8→9 migration included). If drawer wires itself against its own base (v8), the merge inherits a phantom migration conflict. Sequence: routines first, drawer registration on top of v9.
- **ScreenshotHarnessTest.kt is the studio's shared scratch file.** Token changed it, routines changed it (differently — the shots-redirect logic), drawer/shieldpick/slide each carry their own shots-redirect.txt. Expect this file to conflict at every merge until the redirect convention settles into trunk once.

## Recommendations to the merge train (Kraftarm)

1. Merge order: routines → drawer (registration only) → slide (.tmp promotion, re-diffed) → shieldpick stays a record. Token/counterfoil need nothing: their residual vs trunk is trunk's own progress, not unmerged lane work.
2. Before each merge, regenerate that lane's diff with `tools/lane-diff.sh <lane> trunk <label>` and compare the changed-file list against this radar; any NEW shared-code file (theme/views/viewmodel/data) appearing in two lanes' lists is a radar hit — stop and sequence it, don't discover it mid-merge.
3. Treat LiveWire reconciliation as a named merge step for every lane, not a surprise: lanes' bases have no LiveWire; trunk does.
4. New lanes from today: Bausatz's make-lane.sh freezes the base at creation (`tools/lane-base.sh <lane>`, confidence exact) — with an exact base, the next radar is arithmetic, not archaeology.

## Provenance & limits

All counts measured 2026-10-29 against trunk as it stood that morning; trunk is uncommitted and moving, and routines is a live lane — this radar is a snapshot. The six BASE-MANIFEST.sha256 files are reconstructions against current trunk (bodies identical, sha256 6b1c922e21a9ff2c6331076258086e67329f9cc4bba780d3db48b6def7553770, 151 files, `sha256sum -c`-verified against trunk); per-lane headers state exactly which entries are exact and which are approximations.
