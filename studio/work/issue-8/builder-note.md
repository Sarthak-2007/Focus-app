# Issue #8 — Builder note (DECLARED, not applied)

**Code edits are PENDING THE CLONE.** The cell clone
`~/workspace/clone-inspect/builds/cells/issue-8/` (branch `agent/issue-8`)
did not exist at build time, so per brief NO files were edited. All work
below is a verified patch plan against the trunk copy
(`~/workspace/clone-inspect/Focus-app`), ready to apply verbatim in the
clone. Full plan: `builder-patch-plan.md` (same folder). Nothing
compiled, no tests run, nothing committed/pushed.

## Files the plan changes (once applied in the clone)

1. `app/src/main/java/com/example/data/MonolithRepository.kt` — **Job 1,
   forge fix.** In `updateSessionProgress` (:2049-2051 trunk), the lot
   `CurioCatalog.artifacts.shuffled().first().id` (all six) is replaced
   by a draw from the complement of `dao.getForgedArtifactIds()` — the
   DISTINCT set the cabinet reads — with `.firstOrNull()`, so a
   completed set forges nothing new instead of forging invisibly.
   Re-verified: write side shuffled all six; read side is
   `SELECT DISTINCT` (MonolithDao.kt:74-75). Write-side fix chosen:
   one expression, no DAO/schema/UI change; read-side fix would print
   duplicates and keep the lie.

2. `app/src/main/java/com/example/ui/screens/DrawerScreen.kt` — **Jobs
   2 + 3.** Enum `DrawerView { LANDING, MONTH }` (INDEX + CABINET gone);
   INDEX branch and ALL MONTHS row deleted; inline MONTHS shelf added
   to the landing (one ruled row per earlier stored month, monthLabel
   incl. year, NO counts, reuses `drawer_month_<y>_<m>` tag, opens
   MONTH as before); whole CABINET early-return block, both CURIOS
   rows, dead `DrawerView.CABINET` when-branch, `cabinetArtifact`
   state, cabinet params + comment, orphaned `DrawerNavRow`, and the
   CurioCatalog/KineticArtifact imports all deleted.

3. `app/src/main/java/com/example/MainActivity.kt` — **Job 3 call
   site.** `artifacts =` / `forgedArtifactIds =` args (:641-642) and
   the unconsumed `forgedArtifactIds` collection (:217) deleted.

4. `app/src/test/java/com/example/DrawerRenderTest.kt` — INDEX
   navigation replaced by MONTHS-shelf row click; capture renamed
   `dr_drawer_month_day.png`; KDoc updated.

5. `app/src/test/java/com/example/ScreenshotHarnessTest.kt` — cabinet
   captures (day 06/07, night n06), their census entries, and the
   stale KDoc line removed.

## Explicitly preserved (cabinet data/state — no deletion)

`CurioCatalog` + `KineticArtifact` (MonolithModels.kt),
`MonolithDao.getForgedArtifactIds()`,
`MonolithRepository.forgedArtifactIds()`, ViewModel `_forgedArtifactIds`
flow + `refreshDrawer()` read. No new cabinet home built (parked,
issue #21).

## Flag for LADE

Audit §2's "MONTH's BACK stops silently resetting the VM month" is NOT
in the plan: that reset currently reloads the landing's current-month
rows, so removing it needs the saveable-state wave's landing-reload
path. Plan keeps the handler; parent stays LANDING honestly once INDEX
dies. Ruling requested if LADE wants it in this pass.

— Builder (issue #8 cell)
