# Issue #8 — BUILDER PATCH PLAN (Drawer audit fixes, main-app pass)

**Status: CODE EDITS PENDING THE CLONE.** The cell clone
`~/workspace/clone-inspect/builds/cells/issue-8/` (branch `agent/issue-8`)
did **not** exist when this pass ran (cells present: issue-2, 4, 5, 6, 7,
10, 12, 13, 14, 15 — no issue-8). Per brief, this plan was prepared from a
read-only investigation of the trunk copy at
`~/workspace/clone-inspect/Focus-app`. Apply it verbatim in the cell clone
once it exists. Line numbers below are TRUNK line numbers — the anchor
text is authoritative if the branch has drifted. Nothing was compiled,
no tests were run, nothing committed or pushed. DECLARED only.

Audit read in full before planning: `studio/plan/AUDIT-drawer.md`
(Lade's verdict, Oct 30, 2026). Only the brief's three jobs are in this
pass; other §2 rulings are listed as out-of-scope at the end so nobody
mistakes their absence for oversight.

---

## JOB 1 — FORGE BUG FIX (audit §2A, "THE SILENT DEATH")

**Re-verified in trunk (audit cites Repository:1861 + Dao:68; the trunk
copy has grown since — same code, new addresses):**

- WRITE side — `app/src/main/java/com/example/data/MonolithRepository.kt:2049-2051`,
  inside `suspend fun updateSessionProgress(...)` (starts :1934):

```kotlin
        val forgedId = if (allBlocksCompleted && !isRoutineSession) {
            CurioCatalog.artifacts.shuffled().first().id
        } else null
```

  Draws from **all six** artifacts, unconditionally.

- READ side — `app/src/main/java/com/example/data/MonolithDao.kt:74-75`:

```kotlin
    @Query("SELECT DISTINCT forgedArtifactId FROM focus_sessions WHERE forgedArtifactId IS NOT NULL")
    suspend fun getForgedArtifactIds(): List<String>
```

  wrapped by `MonolithRepository.kt:671-672`
  (`forgedArtifactIds(): Set<String> = dao.getForgedArtifactIds().toSet()`).

**The disagreement, confirmed:** from the second forge onward the lot can
re-pick an already-forged id (a duplicate `forgedArtifactId` lands on the
new session row); once the DISTINCT set is complete (6/6), every later
completed span still "forges" — invisibly, because the cabinet reads the
DISTINCT set and can never show a seventh thing. The other
`forgedArtifactId` writers are NOT forge sites and stay untouched:
session birth (`null`, :1368), the finalise funnel (preserves/clears the
session's own id, :1040), `disarmActiveSession`'s `partial_stamp`
(:2316/:2334 — a different stamp, not a catalog artifact).

**Fix — write side only (the smaller, safer change).** Changing the read
side instead (dropping DISTINCT) would make the cabinet print duplicates
and still leave session rows claiming forges nothing can honour — wrong
direction, bigger blast radius. Filtering the lot to the complement of
the very set the cabinet reads makes disagreement impossible by
construction, touches one expression in one function, and needs no DAO,
schema, or UI change. `updateSessionProgress` is already `suspend` and
`dao` is in scope, so the DISTINCT read is a direct call.

REPLACE (Repository :2049-2051, exact old text above) WITH:

```kotlin
        val forgedId = if (allBlocksCompleted && !isRoutineSession) {
            // Forge truth (AUDIT-drawer §2A): the lot draws ONLY from
            // what the cabinet cannot already show. The cabinet reads
            // the DISTINCT forged set (MonolithDao.getForgedArtifactIds),
            // so the lot draws from that same set's complement — the
            // two sides can never disagree. Drawing from all six
            // forged repeats from the second forge onward, and once
            // the set stood complete, forged invisibly forever while
            // the printed rule kept its promise. When nothing unforged
            // remains, this span forges nothing new (null): no fresh
            // claim the cabinet cannot honour. (Whether a completed-set
            // finish prints anything elsewhere is Ren's escalated call,
            // audit §7.3 — not this fix.)
            val alreadyForged = dao.getForgedArtifactIds().toSet()
            CurioCatalog.artifacts
                .filter { it.id !in alreadyForged }
                .shuffled()
                .firstOrNull()?.id
        } else null
```

Behaviour after fix: forges 1–6 each add exactly one NEW artifact (order
still by lot); forge 7+ yields `forgedId = null`, and the ending write's
existing `forgedId ?: session.forgedArtifactId` keeps the completing
session's own prior value — no duplicate claim is minted. `.first()` →
`.firstOrNull()` is mandatory: the filtered list can lawfully be empty.
Routine-session fence (`!isRoutineSession`) unchanged.

---

## JOB 2 — INDEX DIES → INLINE MONTHS SHELF (audit §2, ALL MONTHS row)

All edits in `app/src/main/java/com/example/ui/screens/DrawerScreen.kt`
unless noted. Audit spec, verbatim core: *"LANDING becomes one scroll —
current month, then a MONTHS shelf: one ruled row per earlier stored
month (name incl. year, NO counts), each opening MONTH as today."*
No ViewModel/DAO change is needed: `storedMonths` already reaches the
screen, and MONTH already opens via `onSelectMonth` + view change with
its existing paging.

**2a. View enum (:72):**

```kotlin
private enum class DrawerView { LANDING, INDEX, MONTH, CABINET }
```
→
```kotlin
private enum class DrawerView { LANDING, MONTH }
```
(CABINET leaves in the same line — Job 3.)

**2b. Delete the INDEX branch** in the list `when (view)` — from
`DrawerView.INDEX -> {` (:620) through its closing `}` immediately before
`DrawerView.MONTH -> {` (:652), including its `DrawerNavRow("BACK")`
item and the `items(storedMonths, key = { "month-…" })` rows. (The shelf
in 2d re-uses that row body verbatim, including testTag
`drawer_month_<year>_<month>`.)

**2c. Delete the ALL MONTHS row** in the LANDING branch — the whole
`item { Row(... .clickable { view = DrawerView.INDEX } ...
.testTag("drawer_all_months") ... Text("ALL MONTHS") ... Text("›") ...)
HorizontalDivider(...) }` block (:745-772 approx).

**2d. Insert the MONTHS shelf** in the LANDING branch's non-empty path,
immediately AFTER the current-month groups and their
`if (drawerHasEarlier) { item { EarlierRow(onClick = onLoadEarlier) } }`
block, before the branch closes. (Same LazyListScope shape the branch
already uses for `val groups` + `items(...)`.)

```kotlin
                    // MONTHS shelf (AUDIT-drawer §2): the INDEX
                    // corridor is dead. Earlier stored months stand
                    // inline on the landing itself — one ruled row
                    // per month (name incl. year, NO counts), each
                    // opening MONTH exactly as the index row did.
                    // The current month is the landing's own body
                    // above, never a shelf row. A word-only month is
                    // still an address and still shelves.
                    val earlierMonths = storedMonths.filterNot {
                        it.year == currentYearMonth.first &&
                            it.month == currentYearMonth.second
                    }
                    if (earlierMonths.isNotEmpty()) {
                        item {
                            Text(
                                text = "MONTHS",
                                fontFamily = AnonymousProFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = scheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                        items(
                            earlierMonths,
                            key = { "shelf-${it.year}-${it.month}" }
                        ) { month ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectMonth(month.year, month.month)
                                        view = DrawerView.MONTH
                                    }
                                    .padding(vertical = 10.dp)
                                    .testTag("drawer_month_${month.year}_${month.month}"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = monthLabel(month.year, month.month),
                                    fontFamily = AtkinsonHyperlegibleFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = scheme.onBackground
                                )
                            }
                            HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)
                        }
                    }
```

Notes: old-task depth 3 taps → 2 (landing → shelf row → MONTH), per
audit. Empty current month + earlier months: landing still shows today's
heading in silence and the shelf names the real months (§4 acquittal
preserved). Globally-empty landing: `storedMonths` is empty, the shelf
renders nothing — no change needed in that branch for this job.

**2e. List header comment (:509):**
`// LIST VIEWS — landing / month index / one month / search results.`
→ `// LIST VIEWS — landing (current month + MONTHS shelf) / one month / search results.`

**Flag for LADE (not silently decided here):** audit §2's BACK bullet
also says MONTH's BACK should stop *"silently resetting the VM month"*.
MONTH's current BACK handler calls
`onSelectMonth(currentYearMonth…)` before landing — that reset is
currently what reloads the landing's current-month rows; removing it
without a landing-reload path would leave the landing rendering the
browsed month's rows (the screen groups whatever `drawerRows` holds).
This plan KEEPS the handler as-is (honest parent = LANDING is satisfied
by the shelf's death of INDEX); the reset question belongs with Plan's
saveable-state wave (§2 VIEW STATE). LADE/Ren to rule if they want it in
this pass after all.

---

## JOB 3 — CABINET EVICTION FROM THE DRAWER (audit §2A)

Remove the cabinet from the Drawer UI/flow only. **Do NOT delete any
cabinet data/state** — explicitly PRESERVED and reachable in code:
`CurioCatalog` + `KineticArtifact` (`MonolithModels.kt:492, :799`),
`MonolithDao.getForgedArtifactIds()` (:74-75),
`MonolithRepository.forgedArtifactIds()` (:671-672), and the ViewModel's
`_forgedArtifactIds` flow + its `refreshDrawer()` read
(`MonolithViewModel.kt:1840-1841, :1881`). No new home is built — that
destination is the parked decision in issue #21. §2A's in-cabinet cures
(FORGED tag kill, standing forge line kill, detail PROVENANCE) travel
with the cabinet to that future build; the whole Drawer instance of
them disappears with the block deletion below.

**3a. `DrawerScreen.kt` — signature (:148-162).** DELETE the cabinet
comment block + both params:

```kotlin
    onDeclareTodayWord: (String?) -> Unit,
    // The cabinet (chrome C10): … (11 comment lines) …
    artifacts: List<KineticArtifact> = CurioCatalog.artifacts,
    forgedArtifactIds: Set<String> = emptySet()
) {
```
→
```kotlin
    onDeclareTodayWord: (String?) -> Unit
    // Issue #8 (AUDIT-drawer §2A): the cabinet is EVICTED from the
    // Drawer — no CURIOS row, no cabinet view, no cabinet params.
    // Its data stays in code (CurioCatalog, the DISTINCT forged-id
    // read, the VM's forged flow) for its future home (issue #21);
    // nothing is deleted — it simply has no Drawer address.
) {
```

**3b. `DrawerScreen.kt` — state (:166).** DELETE:
`var cabinetArtifact by remember { mutableStateOf<KineticArtifact?>(null) }`

**3c. `DrawerScreen.kt` — cabinet page (:325-507).** DELETE the entire
early-return block, from the comment anchor
`// CABINET — the curios, re-homed (chrome C10).` through the closing
`return` + `}` immediately before the `// LIST VIEWS` comment (:509).
This removes the cabinet list (incl. the FORGED row tag and the standing
forge-rule line) and the lore detail (incl. "NOT YET FORGED") from the
Drawer in one cut.

**3d. `DrawerScreen.kt` — dead when-branch.** DELETE:
```kotlin
            // Unreachable: the CABINET page returns early above,
            // before this column exists. Present for exhaustiveness.
            DrawerView.CABINET -> {
                item { Spacer(Modifier.height(0.dp)) }
            }
```
(Audit orders this dead branch killed explicitly; with 2a/3c it would
not compile anyway.)

**3e. `DrawerScreen.kt` — both CURIOS rows.** DELETE both identical
`item { DrawerNavRow(label = "CURIOS", onClick = { view =
DrawerView.CABINET }, modifier = Modifier.testTag("drawer_curios_row"))
HorizontalDivider(...) }` blocks — one in the globally-empty landing
branch (:736-743), one in the non-empty landing branch (:774-781).

**3f. `DrawerScreen.kt` — orphaned helper + imports.** DELETE the whole
`private fun DrawerNavRow(...)` composable (:815-831) — its only three
call sites die in 2b/3e. DELETE imports :41
`import com.example.data.CurioCatalog` and :45
`import com.example.data.KineticArtifact` (no remaining uses in the
file). All other imports stay live (Spacer still serves DETAIL; the
search capsule keeps RoundedCornerShape/border/clip).

**3g. `app/src/main/java/com/example/MainActivity.kt` — call site.**
In the `MonolithDestination.VAULT ->` DrawerScreen call, DELETE:
```kotlin
                                    artifacts = com.example.data.CurioCatalog.artifacts,
                                    forgedArtifactIds = forgedArtifactIds
```
(:641-642) and the trailing comma on the preceding
`onDeclareTodayWord = viewModel::declareTodayWord,` line. Also DELETE
the now-unconsumed collection at :217
(`val forgedArtifactIds by viewModel.forgedArtifactIds.collectAsStateWithLifecycle()`).
The ViewModel flow itself is NOT touched (see preserved list above).

---

## TEST COMPANIONS (declared edits, NOT run — nobody compiles)

**T1. `app/src/test/java/com/example/DrawerRenderTest.kt`**
- KDoc: "populated landing + detail + month index" → "populated landing
  + detail + shelf-opened month".
- Replace (:227-229):
```kotlin
        composeRule.onNodeWithTag("drawer_all_months").performClick()
        settle(600)
        capture("dr_drawer_index_day.png")
```
with:
```kotlin
        // INDEX is dead (issue #8): the earlier month opens from the
        // landing's MONTHS shelf row. seedPopulated files one task in
        // last month, so that shelf row exists.
        val shelfMonth = LocalDate.now().minusMonths(1)
        composeRule.onNodeWithTag(
            "drawer_month_${shelfMonth.year}_${shelfMonth.monthValue}"
        ).performClick()
        settle(600)
        capture("dr_drawer_month_day.png")
```
(`LocalDate` is already imported in this file.)

**T2. `app/src/test/java/com/example/ScreenshotHarnessTest.kt`**
- `captureAllDestinationsDay`: DELETE the cabinet block (navigate to
  VAULT → `drawer_curios_row` click → `06_cabinet.png` capture →
  `cabinet_row_artifact_lissajous` click → `07_cabinet_detail.png`
  capture, :197-205); leave a one-line comment: cabinet evicted from
  the Drawer (issue #8), its captures end here.
- `captureAllDestinationsNight`: DELETE the `drawer_curios_row` click +
  `n06_cabinet.png` capture block (:252-257).
- Colour-census zero-fill list (:845-846): DELETE the
  `06_cabinet.png`/`n06_cabinet.png` entries (guarded by `isFile`, so
  stale-harmless — deleted for honesty).
- `tcDrawerDetailsAndSubSteps` KDoc (:731-732): "The cabinet and
  composer states ride the standing captures (06/07/03)." → "The
  composer state rides the standing captures (03); the cabinet
  captures ended with its eviction from the Drawer (issue #8)."

No other test references the removed tags/views (verified by grep over
the whole test tree: only the two files above mention
`drawer_all_months` / `drawer_curios_row` / `cabinet_*`).

---

## EXPLICITLY OUT OF THIS PASS (audit §2/§2A items not in the brief)

Filing zone + tomorrow filing; search entry-line redesign + sealed-search
enablement + "NO STORED TASK UNDER X" re-scope + substep-match second
line; day-heading/row-spacing finish cures; DETAIL estimate/spent
restack + substep strike kill; unified BACK + saveable view state;
cabinet PROVENANCE print, FORGED-tag/forge-line cures in its future
home, the toys decision (Option A/B) — all await their own passes /
Ren's escalations (§7). Search's curio-name falsehood resolves itself
territorially once 3c lands (curios no longer claim a Drawer address),
but the re-scoped line text is NOT changed here.

## APPLY ORDER

Job 1 (Repository, independent) → Job 3a–3g + Job 2a–2e (same file, one
sitting; enum line serves both) → T1/T2. Then hand to LADE for GitHub;
Ren compiles only on Sarthak's word.

— Builder (issue #8 cell)
