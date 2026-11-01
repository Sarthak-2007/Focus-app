# Issue #16 — Builder: merge-flag verification (Pastille's cell)

*Builder seat, Oct 30 2026. DECLARED only — nothing compiled, no
gradle, no test run, per the build authority. Trees traced in code:
pill lane (`builds/pill/`), merged batch-9 tree
(`builds/merge-batch9/`), Frist's lane (`builds/truth-3-times/`),
and the cell clone (`builds/cells/issue-16/repo/`, branch
`agent/issue-16`). The clone appeared mid-task; the Flag A
composition was already pre-applied on it by Pastille (see her
`repo/studio/issue-16/merge-flags.md`). My grant-gate hardening is
applied on top, as real edits, in the same two files — see §A4.*

---

## Flag A — `start2MinutePeek`: Suki's guard × Pastille's launch

**Verdict: COMPOSES — with the grant gate in §A4. The naive
composition (guard + launch, as pre-applied on this branch)
composes for today's constant durations but does NOT compose
once Flag B is live; §A4 closes that, and is the only change I
had to make.**

### A1. The two halves, as they stand in their own trees

**The guard (batch-9 `MonolithViewModel.kt:2384`, label in code:
Truth Car 3 UNA2-01 "second gate").** Synchronous, at the top of
`start2MinutePeek`, before any state mutation:

```kotlin
val entity = apps.value.find { it.packageName == state.interceptedPackageName }
val cooldownNow = entity?.let { repository.getPeekCooldownRemainingSeconds(it) } ?: 0
if (!peekOfferFor(state.interceptPath, entity, cooldownNow, _floatingPeekPill.value.isActive)) {
    return   // airlock left standing
}
val pkg = state.interceptedPackageName   // NO blank default
```

`peekOfferFor` (`MonolithViewModel.kt:141`, batch-9) is a pure
expression — `path.canHonourPeekLease && entity != null &&
entity.resolvedTier == PEEK_2M && cooldownLeftSeconds == 0 &&
!pillActive`. `InterceptPath.canHonourPeekLease`
(`MonolithAccessibilityService.kt:46`, batch-9) is true only for
`APP_TIER` / `SHIELD_APP`; `UNKNOWN` (missing/unparseable intent
extra, `MainActivity.kt:138-141`) never offers.

**The launch (pill `MonolithViewModel.kt:2155`, ST-08).** Inside
`viewModelScope.launch`, after `repository.startPeekLease(pkg)`,
before `recordIntercept`:

```kotlin
runCatching {
    val launchIntent = appContext.packageManager
        .getLaunchIntentForPackage(pkg)
        ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (launchIntent != null) appContext.startActivity(launchIntent)
}
```

### A2. Every path traced

Callers of `start2MinutePeek` (both trees, exhaustive grep):
exactly one wiring — `MainActivity` `onTake2MinutePeek =
viewModel::start2MinutePeek` (pill :775 / batch-9 :798) — fed by
exactly one surface, the Airlock chip
(`AirlockAndDisarmOverlays.kt`, pill :567 / batch-9 :670), which
renders only when `countdownSecs == 0 && state.canUse2MinutePeek`.
No service, receiver, or test in `main` calls it directly.

**Unguarded-launch paths in the pill lane alone (all real):**

1. `start2MinutePeek` itself rechecks *nothing*. The chip's
   visibility is a snapshot computed by `triggerAirlockIntercept`
   at intercept time; any invocation that reaches the function —
   a tap on a stale airlock (cooldown armed by another door
   since, pill already active, tier changed) — grants + launches.
2. Pill's `canPeek` in `triggerAirlockIntercept` (:2129) is
   `(appEntity == null || tier == PEEK_2M) && …` — an *unlisted*
   package defaults the offer ON, the exact case the guard
   exists to kill.
3. Pill's `val pkg = state.interceptedPackageName.ifBlank {
   "com.whatsapp" }` — a blank package grants a lease on, and
   launches, an unrelated app.
4. Pill's debug caller `MainActivity:504` (`onSimulateIntercept`)
   fires the airlock with a hardcoded package and no path
   concept at all. (Batch-9 dropped this call site; the composed
   tree must not resurrect it without an `InterceptPath`.)

**In batch-9 alone:** guard present, launch *absent* — no
unguarded launch exists, but taking batch-9's function verbatim
at the merge silently deletes ST-08.

**Composition-order hazards (checked against the branch):** the
guard must stay (i) synchronous in the function body — if it
moves inside `viewModelScope.launch`, two rapid taps both pass
before either writes anything (double lease write, double
launch, double `recordIntercept`); (ii) on
`state.interceptedPackageName` *before* any defaulting — pill's
`ifBlank` default must not survive (it does not on this branch);
(iii) the launch must stay *after* `startPeekLease` inside the
coroutine, so the lease is written before the sniper can see
the launched app (pill's own stated reason). The branch's
pre-applied composition satisfies all three.

### A3. Double-fire analysis

- **Guard double-fire: none.** `peekOfferFor` is pure — no lease
  write, no cooldown arm, no state mutation. It is evaluated
  once in `triggerAirlockIntercept` (the offer) and once in
  `start2MinutePeek` (the grant gate) by design; two evaluations,
  zero side effects. The cooldown is armed in exactly one place,
  `repository.startPeekLease` → `dao.updatePeekLeaseAndCooldown`,
  called exactly once per successful `start2MinutePeek`.
- **Function double-invocation: closed by the composition.**
  The guard reads `_floatingPeekPill.value.isActive`, and the
  function sets it `true` synchronously in its body before the
  coroutine is dispatched — a second sequential invocation
  (double-tap) sees `pillActive = true` and returns. In the pill
  lane alone this path is open: a second invocation re-grants
  (lease end re-stamped), re-launches, and double-records.
- `recordIntercept` and the ST-08 block each appear exactly once
  in the composed function. `end2MinutePeekEarly` is untouched
  and unaffected.

### A4. The one real hole in the naive composition — FIXED on this branch

Pastille's lead report says `peekMinutes = 0` "composes with
Flag A's guard semantics (refusal at the source)". In code it
does not — yet:

- `peekOfferFor` never reads `peekMinutes`. (Frist's own VM
  folds `peekMinutes.value > 0` into the *trigger's* `canPeek`,
  truth-3-times `MonolithViewModel.kt:2182`, but his
  `start2MinutePeek` :2212 rechecks nothing — safe there only
  because his tree has no ST-08 launch.)
- The pill repository's `startPeekLease` (pill
  `MonolithRepository.kt:1180`) already refuses silently at 0:
  `val durationMillis = editableTimes.peekDurationMillis();
  if (durationMillis <= 0L) return` — `Unit` return, caller
  cannot tell.
- So in the composed tree, post-Flag-B: setting edited to 0
  between intercept and tap (or any stale offer) → guard passes
  → pill seeded active with 120 s → lease call no-ops → **ST-08
  launches an app no lease protects**, and `recordIntercept`
  logs "Used 2m Peek" for a peek that never happened. Today
  this is latent (both providers return the 120 s constant);
  Flag B makes it live. The two flags therefore cannot be
  verdicted independently.

**Fix applied in the clone (DECLARED, uncompiled):**

1. `MonolithViewModel.kt` (clone, on top of Pastille's
   composition): the coroutine now reads
   `val granted = repository.startPeekLease(pkg)`; on
   `!granted` it clears the seeded pill
   (`FloatingPeekPillState(isActive = false)`) and
   `return@launch` — no launch, no record.
2. `MonolithRepository.kt` (clone, batch-9 base):
   `startPeekLease` now returns `Boolean` (`return true` after
   the write; this tree's version has no refusal path yet).
   Statement-position callers (all existing tests) are
   source-compatible with a new return value.

**Companion patch for the batch-10 merge** — when the pill
lane's `startPeekLease` is taken wholesale (Pastille's bonus
delta: it also carries `dao.clearOtherPeekLeases`, the
one-live-lease enforcement batch-9 lacks), its signature must
change the same way. Exact diff against
`builds/pill/Focus-app/app/src/main/java/com/example/data/MonolithRepository.kt:1180`:

```diff
-    suspend fun startPeekLease(packageName: String) {
+    suspend fun startPeekLease(packageName: String): Boolean {
         val durationMillis = editableTimes.peekDurationMillis()
-        if (durationMillis <= 0L) return
+        if (durationMillis <= 0L) return false
         val nowMono = SystemClock.elapsedRealtime()
         val leaseEndMono = nowMono + durationMillis
         dao.clearOtherPeekLeases(packageName)
         dao.updatePeekLeaseAndCooldown(
             packageName = packageName,
             activeUntilMono = leaseEndMono,
             peekEndMono = leaseEndMono
         )
+        return true
     }
```

---

## Flag B — Frist's `EditableTimesProvider` × Pastille's seam

**Verdict: COMPOSES at the seam — zero signature/type
mismatches, provider and existing consumers unchanged. It is
NOT a one-file drop-in: two prerequisites (§B2) must land
first, in the order stated. One scope warning in §B3.**

### B1. The seam vs the provider, member by member

Pastille's seam is structural, not an interface: the file
`data/EditableTimes.kt` (pill) declares the class, and the one
consumer is `MonolithRepository.kt:95` —
`private val editableTimes = EditableTimesProvider(dao)` — plus
one live call site, `editableTimes.peekDurationMillis()` at
`startPeekLease` (pill :1181).

| Member | Pastille's seam (pill) | Frist's provider (truth-3-times) | Match |
|---|---|---|---|
| Package / class | `com.example.data.EditableTimesProvider` | identical | ✅ |
| Constructor | `(dao: MonolithDao)` — with `@Suppress("unused")` on the param | `(dao: MonolithDao)` — annotation dropped, param now used | ✅ type/name identical; annotation delta only |
| `peekDurationMillis` | `suspend fun (): Long` (= `PEEK_DURATION_MILLIS`) | `suspend fun (): Long` (settings row `peekMinutes` clamped 0–30 × 60_000) | ✅ |
| `breakDurationSeconds` | `suspend fun (): Int` (= `SAFE_BREAK_DURATION_SECONDS`) | `suspend fun (): Int` (settings row `breakMinutes` clamped 0–60 × 60) | ✅ |
| Companion constants | none | adds `PEEK_MINUTES_MIN/MAX`, `BREAK_MINUTES_MIN/MAX` | ✅ additive; no pill consumer references them (Frist's own setters do) |

Frist's header states the same contract ("same class name,
same constructor, same two suspend functions"), and his tree
wires the identical instance at `MonolithRepository.kt:96`.
**Zero changes to the provider file, zero changes to Pastille's
construction or call site.** His test header
(`EditableTimesTruthTest.kt`) confirms authorship: "Frist's
slice".

### B2. Prerequisites — what the provider's *body* needs that the target trees don't have

The provider body calls `dao.getGuardSettingsSnapshot()` and
reads `row.peekMinutes` / `row.breakMinutes`:

1. ✅ `MonolithDao.getGuardSettingsSnapshot(): GuardSettingsEntity?`
   exists with the identical signature in all three trees
   (pill :244, truth-3-times :235, batch-9/clone :241).
2. ❌ **`GuardSettingsEntity.peekMinutes: Int = 2` /
   `breakMinutes: Int = 10` do not exist** in pill (entity ends
   at `lastReceiptSessionId`, `MonolithModels.kt:705`) or in
   batch-9/clone (entity ends at `shortsReelsSniperEnabled`).
   Swapping only `EditableTimes.kt` into either tree leaves
   two unresolved references — the file does not compile
   against the v11/v12 entity.
3. ❌ **Migration vehicle:** Frist's own `MIGRATION_11_12`
   (truth-3-times `MonolithDatabase.kt:195`) cannot be reused
   against the batch-9 lineage: batch-9 already shipped a
   *different* `MIGRATION_11_12` under the same name and version
   pair (Falk's `shortsReelsSniperEnabled` + Tamm's
   `breakTakenSeconds`; its DB is already v12, pill is v11).
   The composed tree needs the two guard columns added by a
   **new `MIGRATION_12_13`** (Pastille's lead report names the
   same vehicle: "MIGRATION_12_13 … Frist's five columns,
   batch-10 epic"), entity fields appended after
   `shortsReelsSniperEnabled` to match ADD COLUMN order.
   The provider needs only the two `guard_settings` columns;
   Frist's three `focus_sessions` adds (`breakSeconds`,
   `extensionSecondsAdded`, `extensionLog`) belong to his wider
   slice, not to this swap.

**Order: entity fields + MIGRATION_12_13 → provider file.
Reversed, the tree does not compile; in that order, the swap
is a pure file replacement.**

Not needed by the provider (only by Frist's setters, if they
land): `MonolithDao.updatePeekMinutes` / `updateBreakMinutes`
(truth-3-times `MonolithDao.kt:285/288`) and the repository's
`setPeekMinutes` / `setBreakMinutes` / `peekMinutes()` /
`breakMinutes()` — the provider is read-only.

### B3. Scope warning — what the swap does *not* wire

- In the pill lane, `breakDurationSeconds()` has **no caller**:
  pill's only live call through the seam is
  `peekDurationMillis()`. Pill's break still reads the constant
  directly (`MonolithRepository.kt:1897`,
  `safeBreakSecondsRemaining = SAFE_BREAK_DURATION_SECONDS`),
  and neither pill nor batch-9 has `FocusSessionEntity.breakSeconds`
  (the creation stamp is Frist-slice work, truth-3-times
  `MonolithRepository.kt:1377`). After the swap alone, editable
  *breaks* still do nothing — nobody should claim otherwise.
- The pill countdown seed in both pill and batch-9 VMs is the
  constant `MonolithRepository.PEEK_DURATION_SECONDS`; Frist's
  VM seeds `peekMinutes.value * 60`. Identical at the defaults
  (120), divergent the day `peekMinutes` is edited — the seed
  should move to the provider's value at the same merge, or
  the pill will print a countdown the lease doesn't hold.

---

## State left behind

- Clone (`builds/cells/issue-16/repo/`, `agent/issue-16`,
  uncommitted): `MonolithViewModel.kt` + `MonolithRepository.kt`
  modified — Pastille's ST-08 composition + my grant gate
  (§A4). Nothing else touched; no compile, no gradle, no tests
  run (Ren's authority, Sarthak's word).
- Pill lane and truth-3-times trees: read-only throughout.
- Cross-check: my verdicts agree with Pastille's lead report
  (`repo/studio/issue-16/merge-flags.md`) on both flags,
  including the `MIGRATION_12_13` dependency order; the single
  delta is §A4 — her "refusal at the source composes with the
  guard" needed the grant to become *visible* to the launcher,
  which is the fix above.

— Builder, Pastille's cell (issue #16)
