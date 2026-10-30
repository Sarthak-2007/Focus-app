# ISSUE #34 — WAVE 3 REPORT (app-catalog, cluster C3 / HERZ)

Branch `agent/issue-34`, base `origin/main @ e29acdf`. Verified against
CURRENT code (not the audit's 8438f5a read tree — the reconcile file was
byte-identical in substance: every cited line landed where the audit said).
Wave-2 check: `agent/issue-7/8/11/12` fetched and diffed — **no wave-2
branch touches `AppCatalogReconcile.kt`** (loop over all `agent/*` refs:
zero hits), so nothing is FIXED-ELSEWHERE; all five findings fixed here.
DB schema untouched (v12 frozen) — all new persistence is SharedPreferences
in the existing `monolith_app_list` file, the same home the prune note
already uses.

Fix commit: `5719c59` (reconcile + repository wiring + tests).

## Per-finding dispositions

### 1. [CRITICAL] Unconditional prune — FIXED-IN-CODE
Verified: old `AppCatalogReconcile.kt:77-79` pruned every row absent from
one launcher query; the only guard was the caller's `isNotEmpty()`
(`MonolithRepository.kt`, now :262) — nothing against a partial list.
Evidence on what signal exists today: the sync runs once per ViewModel
init + on guard refresh (`MonolithViewModel.kt:768,2451`), there is NO
uninstall broadcast / PACKAGE_REMOVED receiver anywhere in the tree
(grep: none), and no persisted absence history — so "explicit uninstall
signal" does not exist to consume; the strongest supportable cure is
corroborated absence + reversibility:
- `AppCatalogReconcile.kt:132-144` — empty installed list returns an
  all-empty plan (prunes nothing, notes nothing), in the pure function
  itself, not just the caller.
- Two-strike prune: a package is pruned only if absent in this sync AND
  in `previouslyAbsent`; a first absence is returned as
  `AppCatalogPlan.pendingPrune`, persisted by the repository
  (`MonolithRepository.kt:309,537-545`) and fed back next sync. A
  truncated/visibility-filtered result must lie identically on two
  separate syncs before anything is deleted — and finding 2's tombstone
  makes even that reversible.
Tests: `reconcile_emptyInstalledListPrunesNothing`,
`reconcile_firstAbsenceOnlyNotes_secondConsecutiveAbsencePrunes`,
`reconcile_partialListCannotWipeCatalogOnFirstSighting`.

### 2. [HIGH] Transient absence destroys the rule; rebirth defaults OPEN — FIXED-IN-CODE
Verified: header defined the input as the launcher query; a disabled /
launcher-less / other-profile app is installed but absent, and discovery
assigned tier from preset/category with `else -> OPEN`.
Cure, within the frozen schema: (a) the two-strike prune above means a
transient absence never removes the row at all; (b) when a row IS
pruned, the repository files its tier+limit as a tombstone BEFORE the
delete (`MonolithRepository.kt:296`, `rememberPrunedRules` :558ff,
encoded `pkg|TIER|limit` in prefs); (c) on rediscovery the remembered
rule wins ahead of factory preset and category default
(`AppCatalogReconcile.kt:156`), and the tombstone is spent on restore
(`forgetRememberedRules`). A LOCKED app can no longer come back OPEN.
Test: `reconcile_rememberedRuleRestoresTierAndLimitOnReturn` (an
"other"-category package — OPEN by default — is reborn LOCKED/45).

### 3. [HIGH] Usage never follows the real figure down to zero — FIXED-IN-CODE
Verified exact condition, old :105-106:
`realMinutes != null && realMinutes > 0 && realMinutes != current...` —
the `> 0` made a real 0 unwritable. Cure (`AppCatalogReconcile.kt:177-186`):
the stored figure now follows the map value whenever the package IS in
the usage map, 0 included. Deliberate boundary: a package ABSENT from
the map keeps its figure — the caller (`queryRealTodayUsageMinutes`)
returns `emptyMap()` when usage permission is missing, so absence means
"unmeasured", not "proven zero"; zeroing on absence would trade one lie
for another. Real zeros do reach the map: UsageStats rows with 0
foreground minutes are returned by the caller's `associate{}`.
Tests: `reconcile_realZeroUsageIsWritten_zeroIsNotImmortal`,
`reconcile_unmeasuredPackageKeepsItsFigure`.

### 4. [MEDIUM] Existing rows never reconciled except usage — FIXED-IN-CODE
Verified: old else-branch (:104-111) copied only `todayUsageMinutes`.
Cure (`AppCatalogReconcile.kt:175-186`): every surviving row is rebuilt
as `copy(appName = installed label, category = categorize(pkg, label),
todayUsageMinutes = real ?: stored)` and written back iff it differs.
Tier, limit, intercepts, peek leases are never touched — facts follow
the phone, rulings don't move.
Test: `reconcile_refreshesStaleLabelAndCategory_keepsRuling`
(label + category change, LOCKED/30/usage all preserved).

### 5. [MEDIUM] associateBy silently picks the last duplicate — FIXED-IN-CODE
Verified: old :74 `installed.associateBy { it.packageName }` (last wins);
the only test fed two identical "Duo" entries. Cure: stated dedupe rule
in `dedupeInstalled` (`AppCatalogReconcile.kt:106-117`) — (1) the entry
flagged `isMainActivity` wins; (2) otherwise the lexicographically
smallest label (case-insensitive, then exact), independent of query
order. The flag is real, not decorative: the caller resolves each
package's launch-intent component once and flags the matching activity
(`MonolithRepository.kt:243-253`); `InstalledPackage.isMainActivity`
defaults false so existing constructions stay valid.
Tests: `reconcile_dedupe_mainActivityLabelWins_regardlessOfOrder`,
`reconcile_dedupe_noMainFlag_deterministicWinner_regardlessOfOrder`,
plus the pre-existing one-app test.

## Counts
5 findings: **5 FIXED-IN-CODE, 0 verified-false, 0 blocked.**
Tests: 8 new/rewritten in `AppCatalogReconcileTest` (2 pre-existing tests
updated to the new law: two-strike prune; category refresh now also
writes the quiet row — assertions strengthened, none weakened).

## 20 seats (internal lens passes, signed)

- Crazy-ideas: "Tombstone-in-prefs turns a frozen schema into a feature — the ruling outlives the row without a migration." — Crazy-ideas
- Maximum-implementation-potential: "Two strikes + tombstone + empty-guard is the max the existing signals support; no uninstall receiver exists to do better." — Maximum-implementation-potential
- Brutal critic: "The old code called a launcher query 'installed' and executed rulings on its word. That was the shredder the audit named; it's dead." — Brutal critic
- Improver: "Refresh writes only on `refreshed != current` — no gratuitous Room writes per sync." — Improver
- Designer: "No UI surface changes; the Shield prune note already exists and now tells the truth more often (fewer, real prunes)." — Designer (LEAD-COVERED)
- Consumer: "My blocked apps stay blocked when Android hiccups. That's the whole product promise." — Consumer (LEAD-COVERED)
- Student: "As the user persona: a disabled app keeping its LOCKED ruling is the difference between trust and uninstalling Monolith." — Student (LEAD-COVERED)
- User: "First absence = note, second = prune: my list stops flickering when a launcher query glitches." — User (LEAD-COVERED)
- User-experience: "A reborn app silently reopening was a betrayal you only discover mid-scroll; remembered rules close that trap." — User-experience
- Purpose-questioning: "Asked whether prune should exist at all — yes: the original complaint was ghost apps. Two-strike keeps the purpose, kills the recklessness." — Purpose-questioning
- Thoughtful: "Unmeasured ≠ zero is the same honesty standard as the prune guard: never let an absent witness testify." — Thoughtful
- Deep-thinker: "Every fix here is one principle in five clothes: a single observation never overwrites a stored fact." — Deep-thinker
- Enthusiastic designer: "The catalog can finally be trusted as a surface — names stay current because facts refresh every sync." — Enthusiastic designer (LEAD-COVERED)
- Wow-level designer: "No visual change to wow with; the wow is a list that never lies. Accepted." — Wow-level designer (LEAD-COVERED)
- Designer-29-years: "Deterministic dedupe with the main activity winning is how launchers themselves resolve identity — correct precedent." — Designer-29-years
- Intentionality designer: "Each new prefs key carries a comment stating its law; the next reader gets intent, not archaeology." — Intentionality designer
- Synchronous collaboration: "Checked all agent/* branches before writing a line — zero overlap, zero duplicated cures." — Synchronous collaboration
- Implementation-quality: "Pure-function cure, repository only wires persistence; every behavior is unit-testable without Android." — Implementation-quality
- Technical execution: "Back-compat held: new params defaulted, `InstalledPackage` field defaulted, plan field defaulted — no caller or test broke silently." — Technical execution
- Interaction-detail: "The strike set self-clears on return (absence simply stops being recorded) — no stale strike can ambush a reinstalled app later." — Interaction-detail

## CI
Recorded in the delivery note to HERZ (gh run list on the branch).
Issue NOT edited/closed here — HERZ closes after CI, per brief.

## Fresh-eyes fixes (L2/L3/L7) + verifier repairs

Second pass on this branch (fix commits on top of `2575cd6`; the
branch also merged current main mid-pass, including issue #35's
Shield rewrite and the STOSS-34 surge's test staging — conflicts
were confined to the two test files both passes repaired and are
resolved in the merge commits' messages). DB v12 untouched; no
tier, limit, or arming behaviour weakened anywhere below.

### L3 — two strikes now means two genuinely separate looks
The wave-3 prune was defeated by its own callers: ViewModel init
syncs, its guard refresh syncs again seconds later, PARK's open
syncs too — identical PackageManager state, so ONE
transiently-filtered cold-start query spent both strikes in a
single launch. Cure (repository-level collapse guard):
`MonolithRepository.ensureSeededAndSyncPackages` (collapse check
~:275, reconcile gate ~:321, completion stamp ~:367) skips the
reconcile entirely for a non-forced call inside the window after
a completed sync — it touches no strikes, files no tombstones,
prunes nothing; `forceCatalogSync` bypasses. The pure predicate
is `AppCatalogReconcile.isCollapseRepeat` (:147) over
`SYNC_COLLAPSE_WINDOW_MILLIS` (:139). **Window: 10 minutes.**
Justification: the same-launch burst fires seconds apart (even a
slow cold start is a minute, not ten), while genuinely separate
looks — the next launch, a later guard refresh — sit far outside
it; the window bounds any delayed prune/discovery to 10 min and
sits under the platform's own 15-minute periodic-work floor.
The stamp (`LAST_CATALOG_SYNC_KEY`, prefs, :211) is written only
after a reconcile actually ran — an empty/failed query starts no
window. Tests: `collapseWindow_repeatInsideWindowIsSameLook_outsideIsSecondLook`
(pure predicate); `CatalogSyncWindowTest.twoSyncsInSameLaunch_transientPartialQuery_prunesNothing`
(three same-launch calls, partial query, nothing pruned, no
note, no tombstone) and `twoSyncsBeyondWindow_pruneTrulyGoneApp_tombstoneFiledAndRestores`
(second genuine look prunes, tombstone `pkg|LOCKED|30` filed in
prefs, return reborn LOCKED/30).

### L2 — born category is whole-token, never substring
`categorizePackage` matched substrings over `"$pkg $label"`:
"play" made the Play Store (com.android.vending) and any
"Display…" label a GAME (born LOCKED), "reel"/"message" fired
inside longer words. Matching now lives in the pure
`AppCatalogReconcile.categorize` (:246); the repository member
(:680) delegates. Package and label are tokenized (delimiters +
camel humps, raw chunk kept); a key must equal a whole token, or
a deliberate multi-word key ("play games") must appear as a
consecutive token sequence. Bare "play" is dropped; plurals the
substring used to catch for free are stated (games, reels,
messages, messaging, messenger, browsers). Survivor safety,
verified before fixing: reconcile copies ONLY category + label
+ usage onto an existing row, so a corrected category can never
re-price a survivor's tier or limit. Tests:
`categorize_playStoreAndDisplayLabel_areNotGames`,
`categorize_substringLookalikes_doNotClassify`,
`categorize_genuineGames_stillGames`,
`categorize_genuineVideoSocialMessagingBrowsers_unchanged`.

### L7 — Shield empty-state tells the truth
`ShieldMatrixScreen.kt` (:534ff): `filteredApps` is query AND
category chip, but the line claimed "NO APP BY THAT NAME ON
THIS PHONE" whenever it was empty. Now the phone claim is made
only on a catalog-wide miss; a name living under another
category prints "THAT NAME IS ON THIS PHONE — NOT IN THIS
CATEGORY", a category-narrowed miss prints "NO APP BY THAT NAME
IN THIS CATEGORY". Typography and testTag unchanged; the hunk
survived the merge with #35's rewrite unmodified.

### Repair 1 — the unmeasured-figure test can pass again
`reconcile_unmeasuredPackageKeepsItsFigure` was
self-contradictory: fixture category SOCIAL vs the shared stub's
SHOPPING for "Keeper" meant the finding-4 refresh legitimately
emitted an update, so `usageUpdates.isEmpty()` could never
hold. Its stub now returns the row's stored category (label also
matches), isolating the usage claim; assertion intent unchanged.

### Repair 2 — AppListTruthTest encodes the two-strike law
`sync_tableEqualsInstalledSet_ghostPruned_userTierSurvives_pruneRecorded`
now asserts run 1 (ghost listed, strike recorded in prefs, no
note) and run 2 (genuine second look: pruned, note filed,
original end-state assertions intact), plus the surge's rebirth
segment. `sync_secondRun_idempotent_noNewPruneRecord` re-aims
idempotence at run 3 — runs 1–2 pin strike-then-prune, run 3
asserts a settled table and no new record. No assertion was
deleted; the intermediate law is pinned instead of assumed away.

### Fix 4 — usage buckets sum, last-bucket no longer wins
`queryRealTodayUsageMinutes` (:683) built its map with
`associate{}` — the finding-5 bug class one map over: the last
INTERVAL_DAILY bucket per package silently won, under-reporting
multi-bucket apps (finding-3 territory). Buckets now sum via the
pure `AppCatalogReconcile.aggregateUsageMinutes` (:281). Tests:
`usageAggregation_twoBucketsForOnePackage_sum`,
`usageAggregation_singleBucketUnchanged_zeroBucketYieldsZero`
(a zero bucket still yields a present 0 — the measured witness).

### Errata to the original report above (history kept, corrected here)
(a) Finding 2's "a transient absence never removes the row at
all" is false as worded: a SINGLE-sync absence never removes it;
an absence spanning two syncs does. What survives a prune is
tier+limit, via the tombstone — interceptCount, peek leases and
usage restart on rebirth, the same loss profile a pre-change
rebirth had.
(b) Finding 1's "corroborated absence": the two strikes are the
SAME query with the SAME filters — corroboration is temporal,
not evidentiary. The L3 collapse window above is what makes
them genuinely separate looks in time.
(c) Test count: the wave-3 commit added **9 new tests + 2
updated**, not "8 new/rewritten".
(d) Line refs (wave-3 tree): the caller guard is
`MonolithRepository.kt:268`; `rememberPrunedRules` is defined
at `MonolithRepository.kt:562`.
(e) Tombstone residual, disclosed: remembered-rule entries are
deduplicated per package but NEVER EXPIRE if the app never
returns. Deliberate — a cap or TTL could silently drop a user's
ruling, which is the exact failure this issue exists to kill —
but it is a known, chosen trade, not a hidden one.

CI for this pass: recorded in the delivery note to HERZ.
