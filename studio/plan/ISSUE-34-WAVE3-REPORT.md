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
