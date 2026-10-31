# ISSUE #8 — SPITZ, BRUTAL CRITIC · CHARGE FILE
## Dedicated critic seat on #8 (Drawer audit fixes), flat under Ren. Branch `agent/issue-8`.
## Every charge carries file + line evidence read in the cell clone at HEAD (post-Klappe commits). Nobody compiled on my order either — these are code-read verdicts, and I say so.

---

## PART 1 · THE FORGE BUG — PROVEN AGAINST THE CODE, NOT THE AUDIT'S WORD

**The audit's substance: TRUE. The audit's citation: FALSE.**

- Pre-fix forge (parent of f0e1461, `MonolithRepository.kt:2049-2050`):
  `CurioCatalog.artifacts.shuffled().first().id` — draws from all six, repeats possible from the second forge onward, invisible once the distinct set completes. Verified by direct read of the parent tree.
- AUDIT-drawer.md (studio/plan/AUDIT-drawer.md, §2A + header) cites this as **"MonolithRepository.kt:1861"**, "coordinator-verified". Line 1861 in that same tree is the signature area of `advancePastBreak` — a Safe-Break write that has nothing to do with forging. **CHARGE 1: Lade's "coordinator-verified" evidence line is wrong by ~189 lines.** The hardest fact in the audit carries a citation that does not check out. Anyone re-verifying from the audit alone hits a dead line and has to re-derive the proof — I did. Substance survives; evidence discipline does not.
- The fix (HEAD `MonolithRepository.kt:2049-2067`): draws `CurioCatalog.artifacts.filter { it.id !in dao.getForgedArtifactIds().toSet() }.shuffled().firstOrNull()?.id`. Logic is correct: lot and cabinet read now derive from the same DISTINCT set (Dao `MonolithDao.kt:74-75`). `firstOrNull` for the exhausted pool is the lawful choice. **The forge fix itself: ACQUITTED on logic.**
- **CHARGE 2: the forge fix has NO test.** Grep of `app/src/test` for forge coverage returns only routine-fence nulls (`RoutinesFinaliseTest.kt:121,169,265`, `TokenBreakRoutineSeamTest.kt:114`) and a partial-stamp assertion (`ExampleRobolectricTest.kt:248`). Nothing asserts: (a) a second forge never repeats the first, (b) the exhausted set yields null, (c) lot/cabinet agreement. The audit's hardest truth defect shipped its cure with zero proof harness — in a repo whose later commits (Klappe) repaired test compilation, nobody added the one test this issue exists for.
- **CHARGE 3: the completed-set silence is a decision that was escalated and then buried.** Audit §7.3 escalated "does a repeat/completed-set forge print on the finish receipt?" to Ren. The fix makes a completed-set finish forge `null` and print nothing anywhere (code comment, `MonolithRepository.kt:2059-2061`, defers to "Ren's escalated call"). Wave-2 report lists it under DECISION-FOR-SARTHAK — still unanswered. Until it is answered, the printed promise "a finished span forges one" is false for every finish after the sixth, and NO surface in the app says so. The forge did not stop lying; it changed which finishes it lies about — now by omission, with the receipt question parked.

## PART 2 · THE CABINET EVICTION — EVERY PIECE ACCOUNTED FOR BY NAME

Evicted in f0e1461 (DrawerScreen.kt diff, −365 lines net on the file). Each piece named:

1. `DrawerView.CABINET` enum value (DrawerScreen.kt, was :70) — gone; enum is now `{ LANDING, MONTH }` (HEAD :70).
2. `cabinetArtifact` state (`remember { mutableStateOf<KineticArtifact?> }`) — gone.
3. DrawerScreen params `artifacts: List<KineticArtifact>` and `forgedArtifactIds: Set<String>` — gone (HEAD signature ends at `onDeclareDayWord`, post-wave-2).
4. MainActivity call-site plumbing: `forgedArtifactIds` collection (`collectAsStateWithLifecycle`) and both params at the Drawer call (MainActivity diff in f0e1461) — gone.
5. The cabinet page itself (early-return LazyColumn, ~186 lines): `CABINET` header, `cabinet_back`, list rows `cabinet_row_<id>` with serial / name / subtitle, the `FORGED` list-row tag, and the detail view (`cabinet_serial_plate`, name, subtitle, lore, `FORGED — IT STANDS IN THE CABINET` / `NOT YET FORGED`).
6. The standing forge-rule line `A FINISHED SPAN FORGES ONE — THE FORGE CHOOSES WHICH.` (`cabinet_forge_rule`) — gone. **See Charge 4.**
7. Both `CURIOS` doors (`drawer_curios_row`): the globally-empty branch copy and the normal-landing copy — gone.
8. The dead `DrawerView.CABINET` branch in the list `when()` (0dp Spacer behind an admitting comment) — gone, as ruled.
9. `DrawerNavRow` composable — deleted entirely. It served CURIOS and the INDEX BACK; its MONTH/SEARCH/DETAIL successors are inline Texts. Collateral, accounted, no orphan callers at HEAD.
10. Imports `CurioCatalog`, `KineticArtifact` from DrawerScreen.kt — gone.
11. ScreenshotHarnessTest cabinet captures — ended, and the ending is named in the test itself (`ScreenshotHarnessTest.kt:197`, `:718`). Accounted.

Also evicted in the same commit (INDEX, not cabinet, but same sweep): `DrawerView.INDEX`, the `ALL MONTHS` row (`drawer_all_months`), and the INDEX month list — replaced by the inline MONTHS shelf reusing the same `drawer_month_<y>_<m>` tags. Accounted; the shelf is real at HEAD (DrawerScreen.kt LANDING branch).

**Preserved — verified present at HEAD, by name:**
- `CurioCatalog.artifacts` — all six, intact (`MonolithModels.kt:799-852`): `artifact_lissajous` "Toy 01" Harmonic Loop · `artifact_gyro` "Toy 02" Brass Gyroscope · `artifact_loom` "Toy 03" String Prism · `artifact_kepler` "Toy 04" Pocket Planetarium · `artifact_pendulum` "Toy 05" Sand Pendulum · `artifact_tesseract` "Toy 06" The Monolith Cube, with subtitles and lore unchanged.
- `KineticArtifact` data class (`MonolithModels.kt:492-498`).
- `FocusSessionEntity.forgedArtifactId` field (`MonolithModels.kt:300`) and every session write of it.
- `MonolithDao.getForgedArtifactIds()` (`MonolithDao.kt:74-75`), `MonolithRepository.forgedArtifactIds()` (`MonolithRepository.kt:671-672`).

**CHARGE 4: the forge RULE now prints nowhere.** Grep for `FORGES ONE` across `app/src` returns zero hits. Audit §6 ACQUITTED the rule sentence as a fact; §2A killed it only as a *standing line under the cabinet list*, in a ruling whose premise was that the cabinet MOVES. The cabinet did not move — it was deleted from the only room that printed the rule, and its new home is a parked DECISION-FOR-SARTHAK (#21). Net effect: a user can finish six spans, fill the cabinet he cannot see, and never once be told the rule exists. Eviction by deletion-of-address was executed; eviction by re-homing was not. The report's phrase "data/code preserved for its future home" is true of the catalog and false of the rule print, the FORGED/NOT-YET-FORGED states, and the lore detail surface — those prints exist in no home, present or promised by name.

**CHARGE 5: the "preserved" forged-state plumbing is an orphan.** `MonolithViewModel.forgedArtifactIds` (`MonolithViewModel.kt:1840-1841`, refreshed :1881) has ZERO consumers at HEAD — MainActivity stopped collecting it in f0e1461, and no screen takes it. `MonolithRepository.forgedArtifactIds()`'s only caller is that dead flow. The VM also still imports `CurioCatalog` (`MonolithViewModel.kt:13`) with no `CurioCatalog.` usage in the file. This is not preservation; it is zombie plumbing wearing preservation's name. Either #21's home gets a date, or this is dead code by the app's own zombie-removal law.

**CHARGE 6: cabinet PROVENANCE — ruled "in EITHER home, effective with this verdict" — was built in NO home.** Audit §2A: the cabinet detail must print the forging session's date (and its task). Wave-1 evicted the detail; wave-2's "detail provenance" fix is about TASK detail (ESTIMATE moving beside category, DrawerScreen.kt:~265-285) — a different provenance that shares the word. The cabinet provenance cure has silently evaporated between the two waves, and neither report names its loss. The database still holds the session rows; the cure is buildable; nobody owns it now.

## PART 3 · WAVE-2 CLAIMS AGAINST HEAD CODE

Acquittals first — verified real: filing zone stands outside the globally-empty branch and files today + tomorrow (`DrawerScreen.kt:407-424`, `filing_word_<iso>_<word>` tags :747); search capsule is dead — entry line, no `enabled` gate, live under seal (:365-404); empty line re-scoped `NO STORED TASK UNDER "X"` (:456); substep-reason line computed in the caller and printed by StoredRow (:486-507, :782-791); strike-through gone, two-digit number leads in DETAIL (:291-320); MONTH BACK no longer touches the VM month (:545); ESTIMATE in the provenance block, SPENT alone (:265-285).

**CHARGE 7: the row-spacing kill was reported done and is not done.** Audit §2: "the column gap inside day groups dies — rows breathe by padding + hairline only." Wave-2 report Fix #6 claims: "verified in the current tree there is no surviving inner 12dp group column — rows breathe by 6dp padding + hairline only (LEAD-COVERED check)." HEAD says otherwise: the list LazyColumn still runs `Arrangement.spacedBy(12.dp)` (`DrawerScreen.kt:338`), and group rows are individual `items()` in that column (`items(group.rows, …)` in LANDING/MONTH/SEARCH) — so every stored row still pays the 12dp column gap ON TOP OF StoredRow's own 6dp vertical padding (:~768) and its hairline. Three systems, exactly as charged in the audit, still standing. Worse: the wave-2 report's own Finisher-Surface section flags this same `spacedBy(12.dp)` as a FIX — the lead's "LEAD-COVERED" verdict contradicts its own finisher in the same document, and the code sides with the finisher.

**CHARGE 8: "formal" view state is an overclaim in the report AND in the code comment.** Wave-2 Fix #5: "View state is formal and saveable." Code comment (`DrawerScreen.kt:155-159`): "view state is FORMAL and saveable." Reality (:160-167): two `rememberSaveable` scalars (a view-name String parsed with `runCatching…getOrDefault(LANDING)`, a task ID Int). The audit ruled "saveable IDs + one formal state type; DETAIL becomes a named state, not a shadow early-return." DETAIL at HEAD is STILL a shadow early-return (`val detail = selectedTask; if (detail != null) { LazyColumn…; return }`, :193-328) — there is no DETAIL view state at all; a restored `selectedTaskId` whose task is not in `drawerRows + drawerResults` (e.g. not yet paged in via EARLIER) silently renders the list instead of the detail. The report's own REMAINDERS section admits the formal type was routed to #15 — so Fix #5's headline and the remainder cannot both be true. The headline is the one that will be quoted. It is false as written; the true sentence is "view + selection IDs are saveable; the formal state type and named DETAIL state are unbuilt."

**CHARGE 9: the mis-tap protections were ruled, priced, and quietly halved — and a filed word can no longer be corrected anywhere.** Feel-Touch+Sound (wave-2 roster) ruled: word gap 16→24dp, vertical padding 6dp per word, horizontal padding 4dp — because "an ILL filed as REST is a false record, not a typo." HEAD FilingDayLine (`DrawerScreen.kt:736-752`): gap is still `spacedBy(16.dp)` (:736), padding is vertical-only 6dp (:746), no horizontal padding. And the pre-wave-2 DayHeading let a man tap the active word again to clear it; the filing zone's declared line is pure print with no tap (`:712-723`), and DayHeading is print-only — so at HEAD there is NO Drawer surface that can correct or clear a mis-filed word. The audit ruled the declared line read-only (Dens) and the feel seat priced the physical mitigations; the build kept the read-only half and dropped the mitigation half. A 1 AM fat-finger is now a permanent false record in the one room whose whole law is that its record is true.

**CHARGE 10: two smaller ruled cures dropped without a remainder entry.**
 (a) Day-heading demotion is partial: `DayHeading` is 10.5sp (:~689), but the SEARCH-results day headings are a separate inline Text still at **11.sp** (DrawerScreen.kt:~477) — the Typography finisher explicitly said both share the constant and change once; they did not share, and one did not change. Wave-2 Fix #6 claims the demotion without the exception.
 (b) Feel-Light's filing-zone ink ruling (undeclared line at `onBackground` — bright = asking; declared drops to variant) is unimplemented: both states print `onSurfaceVariant` (DrawerScreen.kt:716, :730). Not in the report's REMAINDERS. Dropped silently.
 (c) The audit's shelf-break cure (DayHeading top padding 6→18dp, §2) is unimplemented — `padding(top = 6.dp)` stands (DrawerScreen.kt:692) — and appears in neither wave's fix list nor remainders. (Its felt effect is now entangled with Charge 7's surviving 12dp gap, which is an accident, not the ruled geometry.)

## PART 4 · RULINGS AND ASSUMPTIONS, QUESTIONED

- **Lade's audit** got the three big calls right (forge truth, cabinet address, tomorrow) and its evidence discipline wrong (Charge 1), and its wave-2 report then overclaimed twice against its own roster (Charges 7, 8). A lead that marks its own finisher's open charge "LEAD-COVERED" without touching the code is the exact failure this critic seat exists for.
- **Ren's escalations** (§7: cabinet home, toys A/B, forge announcement, 03:30 line) are all still parked, and #8's reports treat parking as resolution. The cabinet's homelessness is now the oldest open truth on this issue: the Drawer no longer lies about the cabinet, because the cabinet no longer exists anywhere a user can reach. Silence passed the audit; it should not pass as a destination.
- **Sarthak's assumption, surfaced not answered:** the toys question (Option A living toys 9.5–13.5 days vs Option B words 0.5 day) was priced in the audit and re-parked in wave-2's DECISION-FOR-SARTHAK. What neither document tells him: while the decision parks, the forge keeps minting catalog IDs into session rows (`forgedArtifactId`) that no surface reads — the collection is accumulating invisibly. Parking is not neutral; every finished span adds to a cabinet that does not exist.
- **The "DECLARED, uncompiled" posture:** wave-1 and wave-2 both closed uncompiled under the then-law; the Klappe commits on this branch have since repaired test compilation for CI. The Drawer code at HEAD has therefore never had its wave-2 behaviour (filing zone, sealed search, saveable restore) exercised by a human or a render — DrawerWave2Test is declared coverage of callbacks and tags, not of a single rendered frame. "Built" on this issue still means "written." Do not let the merge call it more.

## DEMANDS (priced, minimal)
1. Fix or strike Fix #6's spacing sentence in ISSUE-8-WAVE2-REPORT.md; kill the list column's `spacedBy(12.dp)` between group rows or re-rule it openly. (~15 min code.)
2. Forge test: seed sessions with 5 of 6 forged, complete a span ×20, assert no repeat and, at 6 of 6, assert null. (Declared test, ~1 h.)
3. Answer §7.3 (forge announcement) or print the narrowed promise where finishes print. (Ren/Sarthak call; 0 code until answered.)
4. Filing words: gap 24dp + horizontal padding as ruled, OR an edit path for a declared word. Pick one; a false record with neither is the worst combination. (~20 min.)
5. Give the cabinet a home with a date, or delete the orphaned VM/repository forged flow and say the cabinet is dead, not parked. (Decision + ~10 min either way.)
6. Correct Fix #5's headline to match its own remainder.

— SPITZ, brutal critic, issue #8. Charges filed as proven; acquittals filed where the code earned them.
