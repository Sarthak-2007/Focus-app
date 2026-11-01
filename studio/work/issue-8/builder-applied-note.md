# Issue #8 — Builder 2 (Applier) note — patch plan APPLIED in the clone

Applied `builder-patch-plan.md` verbatim in
`~/workspace/clone-inspect/builds/cells/issue-8/` (branch `agent/issue-8`),
in the plan's order: Job 1 → Jobs 3+2 in one sitting → T1/T2.
Anchor text was treated as authoritative over line numbers; every anchor
was checked against the clone before its edit ran. **Anchor mismatches:
NONE — 23/23 edits applied, 0 skipped.** Nothing compiled, no tests run,
nothing committed or pushed (DECLARED only; LADE handles GitHub).

Pre-verified anchors confirmed in the clone before starting:
`MonolithRepository.kt:2050` = `CurioCatalog.artifacts.shuffled().first().id`;
`DrawerScreen.kt:72` = `private enum class DrawerView { LANDING, INDEX, MONTH, CABINET }`.

## Edits applied

### Job 1 — forge fix
1. `app/src/main/java/com/example/data/MonolithRepository.kt` — in
   `updateSessionProgress`, the lot `CurioCatalog.artifacts.shuffled().first().id`
   replaced with the plan's verbatim block: lot draws from the complement of
   `dao.getForgedArtifactIds().toSet()` (the DISTINCT set the cabinet reads),
   `.shuffled().firstOrNull()?.id`, with the plan's full "Forge truth
   (AUDIT-drawer §2A)" comment. Write-side only; no DAO/schema/UI change.

### Job 3 — cabinet eviction (DrawerScreen.kt unless noted)
2. 3a signature: cabinet comment block (11 lines) + `artifacts` /
   `forgedArtifactIds` params deleted; `onDeclareTodayWord` is now the last
   param, followed by the plan's verbatim issue-#8 eviction comment.
3. 3b state: `var cabinetArtifact by remember { … }` line deleted.
4. 3c cabinet page: the entire CABINET early-return block deleted — from its
   header comment (`// CABINET — the curios, re-homed (chrome C10).`,
   including the dashed separator lines of that header) through the closing
   `return` + `}` immediately before the `// LIST VIEWS` header.
5. 3d dead branch: the `DrawerView.CABINET -> { item { Spacer(…) } }`
   when-branch with its "Unreachable" comment deleted.
6. 3e CURIOS row #1 (globally-empty landing): the `DrawerNavRow("CURIOS")`
   item block deleted.
7. 3e CURIOS row #2 (non-empty landing): the identical item block deleted,
   together with its own 2-line lead comment ("The cabinet's door: ONE
   furniture row under / ALL MONTHS (chrome C10).") — that comment belonged
   to this row alone and would otherwise have been orphaned print.
8. 3f helper: the whole `private fun DrawerNavRow(...)` composable deleted
   (all three call sites died in 2b/3e).
9. 3f imports: `import com.example.data.CurioCatalog` and
   `import com.example.data.KineticArtifact` deleted (no remaining uses).
10. 3g `app/src/main/java/com/example/MainActivity.kt`: `artifacts = …` /
    `forgedArtifactIds = …` args deleted from the DrawerScreen call (trailing
    comma on `onDeclareTodayWord = …` removed), and the unconsumed
    `val forgedArtifactIds by viewModel.forgedArtifactIds.collectAsStateWithLifecycle()`
    collection deleted.

### Job 2 — INDEX dies → inline MONTHS shelf (DrawerScreen.kt)
11. 2a enum: `DrawerView { LANDING, INDEX, MONTH, CABINET }` →
    `DrawerView { LANDING, MONTH }` (serves Jobs 2+3).
12. 2b INDEX branch: the whole `DrawerView.INDEX -> { … }` branch deleted
    (BACK DrawerNavRow item + `items(storedMonths, …)` rows).
13. 2c ALL MONTHS row: the whole `drawer_all_months` item block deleted
    from the non-empty landing.
14. 2d MONTHS shelf: the plan's shelf code inserted verbatim in the landing's
    non-empty path, immediately after the current-month groups and their
    `if (drawerHasEarlier) { item { EarlierRow(onClick = onLoadEarlier) } }`
    block, before the branch closes (MONTHS header + one ruled
    `drawer_month_<y>_<m>` row per earlier stored month, `monthLabel` incl.
    year, NO counts, opens MONTH via `onSelectMonth` + view change).
15. 2e header comment: `// LIST VIEWS — landing / month index / one month /
    search results.` → `// LIST VIEWS — landing (current month + MONTHS
    shelf) / one month / search results.`

### Test companions (declared edits only, not run)
16. T1 `app/src/test/java/com/example/DrawerRenderTest.kt`: KDoc "populated
    landing + detail + month index" → "populated landing + detail +
    shelf-opened month"; the `drawer_all_months` click + `dr_drawer_index_day.png`
    capture replaced with the plan's verbatim MONTHS-shelf row click
    (`drawer_month_<year>_<monthValue>` for `LocalDate.now().minusMonths(1)`)
    + `dr_drawer_month_day.png` capture.
17. T2 `app/src/test/java/com/example/ScreenshotHarnessTest.kt`: day cabinet
    block (CURIOS click → `06_cabinet.png` → detail click →
    `07_cabinet_detail.png`) deleted and replaced with the one-line comment
    "Cabinet evicted from the Drawer (issue #8) — its captures end here.";
    night `drawer_curios_row` click + `n06_cabinet.png` block deleted;
    colour-census `06_cabinet.png` / `n06_cabinet.png` entries deleted;
    `tcDrawerDetailsAndSubSteps` KDoc updated to the plan's wording
    ("The composer state rides the standing captures (03); the cabinet
    captures ended with its eviction from the Drawer (issue #8)." — wrapped
    across the file's existing KDoc lines).

### Copied into the clone
18. `builder-patch-plan.md` and `builder-note.md` copied from
    `~/workspace/issue-8-builder/studio/work/issue-8/` into this folder,
    beside this note.

## Anchor mismatches
None. Every plan anchor matched the clone's actual text exactly. Two
harmless trunk→clone drifts worth recording (neither blocked an edit —
the content anchors matched):
- T2's day function is named `captureAllDestinations` in the clone
  (plan called it `captureAllDestinationsDay`); the cabinet block sat at
  the plan's lines 197–205 and matched verbatim.
- T2's census entries sat at clone lines 843–844 (plan cited 845–846);
  text matched verbatim.

## Deliberately NOT touched
- The plan's FLAG: MONTH's BACK still calls
  `onSelectMonth(currentYearMonth…)` before landing — handler preserved
  verbatim, reset question left for the saveable-state wave / LADE's ruling.
- PRESERVED cabinet data/code, verified still present after the edits:
  `CurioCatalog` + `KineticArtifact` (MonolithModels.kt:799 / :492),
  `MonolithDao.getForgedArtifactIds()` (:75),
  `MonolithRepository.forgedArtifactIds()` (:671-672), ViewModel
  `_forgedArtifactIds` flow (:1840-1841) + its `refreshDrawer()` read (:1881).
  No new cabinet home built (parked, issue #21).
- Residual stale comments the plan does not order changed, left as-is for
  LADE to rule on: the landing branch header
  `// ---------- LANDING: ALL MONTHS + current month ----------` and the
  current-month comment "…renders in SILENCE under the ALL MONTHS row —".
- Formatting only: two deletion seams in DrawerScreen.kt (after the DETAIL
  return, and before DayHeading's KDoc) were tidied from two blank lines
  back to the file's single-blank-line convention. No code text changed.

## Post-edit verification (declared, no compile)
- Residual sweep: no `DrawerView.INDEX`, `DrawerView.CABINET`,
  `drawer_all_months`, `drawer_curios_row`, `cabinetArtifact`,
  `DrawerNavRow`, or `cabinet_*` references remain in DrawerScreen.kt;
  no `forgedArtifactIds` / `CurioCatalog` references remain in
  MainActivity.kt; no removed tags remain in either test file. The only
  `CurioCatalog` mention left in DrawerScreen.kt is inside the plan's own
  eviction comment (3a).
- Diff totals: 5 files changed, 89 insertions, 328 deletions
  (DrawerScreen.kt carries the bulk: −191-line cabinet page, INDEX branch,
  rows, helper).

— Builder 2 — Applier (issue #8 cell)
