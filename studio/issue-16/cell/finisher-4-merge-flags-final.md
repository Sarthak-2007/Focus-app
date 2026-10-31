# Issue #16 — Merge-flags package, FINAL (batch-10 execution copy)

*Finisher-4 (issue-16 cell), Oct 30 2026. Supersedes
`repo/studio/issue-16/merge-flags.md` (Pastille) in polish only —
every fact below was re-verified line by line against the actual
files on Oct 30, and §7 lists the three places where the earlier
facts needed refinement. DECLARED only: nothing in this package
was compiled or test-run, and nothing here claims otherwise.
Only Ren compiles, on Sarthak's word.*

**How to use this package:** execute §6 top to bottom. Each step
names its source file and line, its target file, and the exact
text to move. No step requires opening a fourth tree or
re-deriving a verdict.

---

## 0. Verdicts at a glance

| Item | Verdict | Shape of the merge |
|---|---|---|
| **Flag A** — `start2MinutePeek` composition | **COMPOSES** | Batch-9 function kept whole; pill's ST-08 launch block inserted inside its coroutine, between the grant and the record; one import added. Already composed on branch `agent/issue-16` (clone state quoted in full in §A2). |
| **Flag B** — Frist's `EditableTimesProvider` into the pill seam | **COMPOSES, ordered** | Pure file replacement *in the pill lineage*; in the batch-9 lineage it is an **add** (no provider file, no repository instance exists there yet — §B3). Lands only **after** `MIGRATION_12_13` + the entity fields (§B2). No ViewModel consumer changes. |
| **Bonus delta** — pill `startPeekLease` taken wholesale | **TAKE IT, with two companions** | Brings provider-read duration, the `peekMinutes = 0` refusal, and one-live-lease enforcement. Requires (i) the DAO method `clearOtherPeekLeases`, which batch-9 lacks, and (ii) the `Boolean` return patch (§C3) that the clone's grant gate already expects. |

**Trees and anchors (all verified Oct 30):**

| Tree | Path | Role |
|---|---|---|
| Batch-9 merge tree | `~/workspace/clone-inspect/builds/merge-batch9/Focus-app/` | Merge base. DB **v12**. |
| Pill lane | `~/workspace/clone-inspect/builds/pill/Focus-app/` | ST-08 + seam + bonus delta source. DB v11. |
| Truth-3-times lane (Frist) | `~/workspace/clone-inspect/builds/truth-3-times/Focus-app/` | Flag B provider source. DB v12 (its own, different `MIGRATION_11_12`). |
| Cell clone | `~/workspace/clone-inspect/builds/cells/issue-16/repo/`, branch `agent/issue-16` | Flag A already composed here; builder's grant gate sits on top, uncommitted (2 files modified vs HEAD: `MonolithViewModel.kt`, `MonolithRepository.kt`). |

---

## FLAG A — `start2MinutePeek`: Suki's guard × Pastille's launch

### A1. The two halves, verbatim

**Half 1 — the guard (batch-9).**
`app/src/main/java/com/example/ui/MonolithViewModel.kt:2384–2416`
in the batch-9 tree. The grant re-derives the offer before
anything mutates; a refusal returns with the airlock standing;
a blank package gets **no** default:

```kotlin
    fun start2MinutePeek() {
        val state = _airlockState.value
        // UNA2-01, second gate: the grant re-derives the offer from
        // the CURRENT row, cooldown and pill — a stale airlock
        // state can never sell a lease its door couldn't honour,
        // and a blank package no longer defaults to an unrelated
        // app. A refused grant leaves the airlock standing.
        val entity = apps.value.find { it.packageName == state.interceptedPackageName }
        val cooldownNow = entity?.let { repository.getPeekCooldownRemainingSeconds(it) } ?: 0
        if (!peekOfferFor(state.interceptPath, entity, cooldownNow, _floatingPeekPill.value.isActive)) {
            return
        }
        val pkg = state.interceptedPackageName
        val name = state.interceptedAppName.ifBlank { "2m Peek App" }
        soundEngine.playMechanicalLatch(true)
        _airlockState.value = state.copy(isVisible = false)
        _floatingPeekPill.value = FloatingPeekPillState(
            isActive = true,
            packageName = pkg,
            appName = name,
            secondsRemaining = MonolithRepository.PEEK_DURATION_SECONDS
        )
        viewModelScope.launch {
            repository.startPeekLease(pkg)
            repository.recordIntercept(
                packageName = pkg,
                appName = name,
                sessionIntention = state.sessionIntention.ifBlank { _intentionText.value },
                outcome = "Used 2m Peek",
                triggerReason = state.selectedReason.shortTag
            )
        }
    }
```

The guard's predicate (`MonolithViewModel.kt:141–151`, batch-9;
`:142` in the clone) is pure — no write, no state change:

```kotlin
internal fun peekOfferFor(
    path: InterceptPath,
    entity: BlockedAppEntity?,
    cooldownLeftSeconds: Int,
    pillActive: Boolean
): Boolean =
    path.canHonourPeekLease &&
        entity != null &&
        entity.resolvedTier == AppBlockTier.PEEK_2M &&
        cooldownLeftSeconds == 0 &&
        !pillActive
```

**Half 2 — the launch (pill lane, ST-08).**
`MonolithViewModel.kt:2155–2192` in the pill tree. The lane has
**no guard at all**, and its package line is the defect the
guard exists to kill — it must **not** survive the merge
(pill `:2157`, verbatim):

```kotlin
        val pkg = state.interceptedPackageName.ifBlank { "com.whatsapp" }
```

Its one true addition sits inside the coroutine, after
`repository.startPeekLease(pkg)` (`:2168`) and before
`repository.recordIntercept(...)` (`:2184`) — pill `:2176–2183`,
verbatim:

```kotlin
            runCatching {
                val launchIntent = appContext.packageManager
                    .getLaunchIntentForPackage(pkg)
                    ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (launchIntent != null) {
                    appContext.startActivity(launchIntent)
                }
            }
```

### A2. The composed function, in full — quote of the clone

Source of truth: cell clone,
`repo/app/src/main/java/com/example/ui/MonolithViewModel.kt:2381–2451`,
reproduced character for character. This is the exact function
the batch-10 merger takes. It is Pastille's composition
(batch-9 base + ST-08 + import) **plus** the builder's grant
gate (§A5) — the clone has moved one step past the original
composition, and this quote, not the older description, is
authoritative.

```kotlin
    /**
     * Grants a 2-Minute Peek lease with the Draggable Edge-Snapping Countdown Pill
     * and arms the mandatory 20-minute cooldown in Room storage immediately.
     */
    fun start2MinutePeek() {
        val state = _airlockState.value
        // UNA2-01, second gate: the grant re-derives the offer from
        // the CURRENT row, cooldown and pill — a stale airlock
        // state can never sell a lease its door couldn't honour,
        // and a blank package no longer defaults to an unrelated
        // app. A refused grant leaves the airlock standing.
        val entity = apps.value.find { it.packageName == state.interceptedPackageName }
        val cooldownNow = entity?.let { repository.getPeekCooldownRemainingSeconds(it) } ?: 0
        if (!peekOfferFor(state.interceptPath, entity, cooldownNow, _floatingPeekPill.value.isActive)) {
            return
        }
        val pkg = state.interceptedPackageName
        val name = state.interceptedAppName.ifBlank { "2m Peek App" }
        soundEngine.playMechanicalLatch(true)
        _airlockState.value = state.copy(isVisible = false)
        _floatingPeekPill.value = FloatingPeekPillState(
            isActive = true,
            packageName = pkg,
            appName = name,
            secondsRemaining = MonolithRepository.PEEK_DURATION_SECONDS
        )
        viewModelScope.launch {
            // Issue #16, builder's grant gate (on top of the
            // composition below): the launch and the record are
            // gated on the grant actually landing. Once Frist's
            // provider is live, startPeekLease can refuse at the
            // source (peekMinutes = 0 — no lease written, no row
            // touched), and peekOfferFor above does not read
            // peekMinutes, so without this gate a stale offer
            // would launch an app no lease protects, under a pill
            // seeded over nothing, and record a peek that never
            // happened. A refused grant clears the seeded pill
            // and stops here.
            val granted = repository.startPeekLease(pkg)
            if (!granted) {
                _floatingPeekPill.value = FloatingPeekPillState(isActive = false)
                return@launch
            }
            // ST-08 (Small Things register), composed in from the
            // pill lane at the batch-10 merge (issue #16, flag A):
            // a taken peek takes him TO the peeked app — the lease's
            // first seconds are his, not the walk's. The grant is
            // written before the launch fires, so the sniper admits
            // the app he lands in; a package with no launcher entry
            // (or a launch the system refuses) simply leaves him
            // where he stands, lease running, exactly as before
            // this verb existed. Suki's pre-grant guard above runs
            // first and unchanged — a refused grant never reaches
            // this launch.
            runCatching {
                val launchIntent = appContext.packageManager
                    .getLaunchIntentForPackage(pkg)
                    ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (launchIntent != null) {
                    appContext.startActivity(launchIntent)
                }
            }
            repository.recordIntercept(
                packageName = pkg,
                appName = name,
                sessionIntention = state.sessionIntention.ifBlank { _intentionText.value },
                outcome = "Used 2m Peek",
                triggerReason = state.selectedReason.shortTag
            )
        }
    }
```

**Exact insertion point (if composing by hand instead of taking
the clone):** in the batch-9 function, inside
`viewModelScope.launch { … }`, the ST-08 `runCatching` block
goes **immediately after the `repository.startPeekLease(pkg)`
statement and immediately before the
`repository.recordIntercept(` call** — in batch-9 line terms,
between `:2407` and `:2408`. Nothing else in the function moves.

### A3. Ordering proof — guard → grant → launch → record

```
1. GUARD    peekOfferFor(...) == false  →  return.
            Synchronous, in the function body, before the latch
            sound, before isVisible = false, before the pill is
            seeded, before any coroutine exists.
            Consequence: a refused grant leaves the airlock
            standing and writes nothing.

2. LATCH    soundEngine.playMechanicalLatch(true);
            airlock hidden; pill seeded isActive = true —
            still synchronous. Because the seed is synchronous
            and the guard reads _floatingPeekPill.value.isActive,
            a second sequential invocation (double-tap) fails
            the guard at step 1. The guard must never be moved
            inside the coroutine: two rapid taps would both
            pass before either wrote anything.

3. GRANT    repository.startPeekLease(pkg) — first statement
            inside viewModelScope.launch. The lease (and its
            cooldown anchor) is written to Room here, and
            nowhere else: startPeekLease →
            dao.updatePeekLeaseAndCooldown is the single
            cooldown-arming write.

4. GATE     (clone's addition, §A5) granted == false →
            pill cleared, return@launch. Steps 5–6 never run
            for a grant the repository refused at the source.

5. LAUNCH   ST-08 runCatching block. Fires strictly after the
            grant is written — this is the load-bearing order:
            the sniper admits the app he lands in *because the
            lease already exists when the launched app appears*.
            runCatching + the null check make a package with no
            launcher entry a no-op: he stays where he stands,
            lease running.

6. RECORD   repository.recordIntercept(..., outcome =
            "Used 2m Peek", ...) — exactly once, last. A peek
            is only ever recorded after it was granted (and,
            with the gate, only when the grant landed).
```

Each step's position is forced by the step after it: guard
before latch (else a refusal hides the airlock), latch before
coroutine (else the double-tap race opens), grant before
launch (else the sniper meets an unleased app), record last
(else the log claims peeks that never happened). Any merge
that permutes these six steps is a different, broken function.

### A4. Import delta — exactly one line

```diff
  import android.content.Context
+ import android.content.Intent
  import android.os.SystemClock
```

- Batch-9 VM imports: `android.content.Context` at `:3`, **no**
  `Intent` anywhere in the file (verified by full-file read).
- Pill VM: `import android.content.Intent` at `:4`. Clone: `:4`.
- Nothing else is needed: `appContext` already exists in the
  batch-9 VM (constructor property, `:204`; clone `:205`), and
  `Intent.FLAG_ACTIVITY_NEW_TASK` is the launch block's only
  `Intent` use.

### A5. The grant gate — why the clone carries one step more

Pastille's composition alone composes for today's constant
durations. It does **not** compose once Flag B is live:

- `peekOfferFor` never reads `peekMinutes` (see §A1 — the
  predicate is path + row + tier + cooldown + pill only).
- The pill repository's `startPeekLease` refuses silently at
  `peekMinutes = 0` (`return`, `Unit` — §C1): the caller cannot
  tell a refusal from a grant.
- Composed naively, a stale offer would therefore: pass the
  guard → seed the pill → no-op the lease → **launch an app no
  lease protects** → record `"Used 2m Peek"` for a peek that
  never happened.

The clone closes this (builder's fix, present in the §A2
quote): `startPeekLease` returns `Boolean`, and the coroutine
gates launch + record on it, clearing the seeded pill on
refusal. The clone's repository half (batch-9 base, always
grants today) is at clone `MonolithRepository.kt:1204–1213`:

```kotlin
    suspend fun startPeekLease(packageName: String): Boolean {
        val nowMono = SystemClock.elapsedRealtime()
        val leaseEndMono = nowMono + PEEK_DURATION_MILLIS
        dao.updatePeekLeaseAndCooldown(
            packageName = packageName,
            activeUntilMono = leaseEndMono,
            peekEndMono = leaseEndMono
        )
        return true
    }
```

**Merger's obligation:** when the pill lane's `startPeekLease`
is taken wholesale at batch-10 (bonus delta, §C), apply the
companion `Boolean` patch in §C3 to it — otherwise the §A2
function has nothing truthful to gate on.

### A6. What must NOT survive the merge

- [ ] Pill `:2157` — `ifBlank { "com.whatsapp" }` is gone;
      the composed line is `val pkg = state.interceptedPackageName`.
- [ ] No second copy of `recordIntercept` or of the ST-08
      block anywhere in the composed function (each appears
      exactly once — grep the merged file).
- [ ] The guard is **not** inside `viewModelScope.launch`.
- [ ] `end2MinutePeekEarly` is untouched by this flag.

---

## FLAG B — Frist's `EditableTimesProvider` into the pill seam

### B1. The seam, member by member (all verified)

Pill seam source: `builds/pill/Focus-app/app/src/main/java/com/example/data/EditableTimes.kt` (45 lines).
Frist provider source: `builds/truth-3-times/Focus-app/app/src/main/java/com/example/data/EditableTimes.kt` (67 lines).

| Member | Pill seam | Frist's provider | Match |
|---|---|---|---|
| Package / class | `com.example.data.EditableTimesProvider` | identical | ✅ |
| Constructor | `(dao: MonolithDao)`, param annotated `@Suppress("unused")` (pill `:31–33`) | `(dao: MonolithDao)`, annotation dropped — the dao is used now (truth-3-times `:34–36`) | ✅ type identical; annotation delta only |
| `peekDurationMillis` | `suspend fun (): Long` = `PEEK_DURATION_MILLIS` constant (pill `:38`) | `suspend fun (): Long` = settings row `peekMinutes` clamped 0–30 × 60_000 (truth-3-times `:41–45`) | ✅ |
| `breakDurationSeconds` | `suspend fun (): Int` = `SAFE_BREAK_DURATION_SECONDS` constant (pill `:44`) | `suspend fun (): Int` = settings row `breakMinutes` clamped 0–60 × 60 (truth-3-times `:51–55`) | ✅ |
| Companion constants | none | adds `PEEK_MINUTES_MIN/MAX = 0/30`, `BREAK_MINUTES_MIN/MAX = 0/60` (truth-3-times `:57–66`) | ✅ additive; no pill consumer references them |

Frist's two function bodies, verbatim (the whole behavioural
content of the swap):

```kotlin
    suspend fun peekDurationMillis(): Long {
        val row = dao.getGuardSettingsSnapshot() ?: GuardSettingsEntity()
        val minutes = row.peekMinutes.coerceIn(PEEK_MINUTES_MIN, PEEK_MINUTES_MAX)
        return minutes * 60_000L
    }

    suspend fun breakDurationSeconds(): Int {
        val row = dao.getGuardSettingsSnapshot() ?: GuardSettingsEntity()
        val minutes = row.breakMinutes.coerceIn(BREAK_MINUTES_MIN, BREAK_MINUTES_MAX)
        return minutes * 60
    }
```

An absent settings row reads as the entity defaults (2 and 10),
so a never-seeded install behaves bit-identically to the app
before the columns existed. The provider is read-only and is
read at grant time and at session-creation time **only** — a
live lease already holds an absolute end; an edit prices the
future, never the present.

**Take the file whole** from the truth-3-times path above.
Do not hand-edit it during the swap.

### B2. Dependency order — the flag's one sharp edge

The provider's body needs `row.peekMinutes` /
`row.breakMinutes`. Neither the pill tree (DB v11) nor the
batch-9 tree (DB v12) has those columns. The order below is
not a preference; reversed at any point, the tree does not
compile.

**Step 1 — Migration + entity (must land first, together).**

- A **new** `MIGRATION_12_13` in `MonolithDatabase.kt`,
  bumping the database `version = 12` → `13` and registering
  in `addMigrations(...)`. It cannot reuse Frist's migration:
  his is `MIGRATION_11_12` (truth-3-times
  `MonolithDatabase.kt:195–203`), and the batch-9 lineage
  already shipped a **different** `MIGRATION_11_12` under the
  same name and version pair (batch-9
  `MonolithDatabase.kt:190–195`: `shortsReelsSniperEnabled` +
  `breakTakenSeconds`). Name and pair are both taken.
- Minimum SQL for this flag (the two guard columns; defaults
  are the shipped constants, so existing rows behave
  identically):

```sql
ALTER TABLE guard_settings ADD COLUMN peekMinutes INTEGER NOT NULL DEFAULT 2;
ALTER TABLE guard_settings ADD COLUMN breakMinutes INTEGER NOT NULL DEFAULT 10;
```

  Frist's full slice carries five columns in his
  `MIGRATION_11_12` — the two above plus three on
  `focus_sessions` (`breakSeconds` DEFAULT 600,
  `extensionSecondsAdded` DEFAULT 0, `extensionLog` TEXT
  DEFAULT ''). Those three belong to his wider slice, **not**
  to this swap; if the batch-10 epic's `MIGRATION_12_13`
  carries all five, Flag B is still satisfied by the two.
- Entity: append to `GuardSettingsEntity`
  (`MonolithModels.kt:745`, batch-9) **after**
  `shortsReelsSniperEnabled` (`:778` — currently the last
  field, so it gains a trailing comma), matching the
  migration's ADD COLUMN order:

```kotlin
    val peekMinutes: Int = 2,
    val breakMinutes: Int = 10
```

  (In Frist's own tree the fields sit at
  `MonolithModels.kt:835–836`, before his tree's
  `shortsReelsSniperEnabled` position — that ordering belongs
  to his lineage's composed migration and must **not** be
  copied into the batch-9 lineage.)

**Step 2 — DAO / entity check (verify, do not create).**

- `MonolithDao.getGuardSettingsSnapshot(): GuardSettingsEntity?`
  already exists with the identical signature the provider
  calls — batch-9 `MonolithDao.kt:241`, clone `:241`, pill
  `:244`, truth-3-times `:235`. **No DAO change is needed for
  the provider itself.** (The bonus delta adds one DAO method
  for a different reason — §C2.)
- Not needed by the provider, do not pull in for this flag:
  Frist's DAO setters `updatePeekMinutes` /
  `updateBreakMinutes` (truth-3-times `MonolithDao.kt:285/288`)
  and his repository `setPeekMinutes` / `setBreakMinutes` /
  `peekMinutes()` / `breakMinutes()`. Those serve the settings
  surface, a separate landing.

**Step 3 — Provider file lands.**

- Copy truth-3-times `data/EditableTimes.kt` over the target
  `data/EditableTimes.kt` (pill lineage: replacement;
  batch-9 lineage: new file — see §B3).

**Step 4 — Consumers: untouched.**

- No ViewModel call site changes. No signature, type, or
  construction change anywhere the provider is consumed —
  the repository wiring in §B3 is the only repository-side
  line, and it arrives together with the bonus delta's
  wholesale `startPeekLease`, not as a consumer rewiring.

### B3. Refinement the merger must know: batch-9 has no seam yet

"Swap" is exact for the pill lineage. Verified against the
batch-9 tree and the clone:

- Neither tree contains `data/EditableTimes.kt` at all
  (full `data/` directory listing checked — 15 files, no
  `EditableTimes.kt`).
- Neither repository contains the instance or any
  `editableTimes` reference (grep: zero hits). The pill
  repository holds the instance at
  `MonolithRepository.kt:95` —
  `private val editableTimes = EditableTimesProvider(dao)` —
  and its single live call at `:1181`, inside
  `startPeekLease`. Frist's tree wires the identical instance
  at `MonolithRepository.kt:96`.

So in the batch-9 lineage the landing is: **add the file
(step 3) + add the instance line** — the instance arrives
naturally when the bonus delta's `startPeekLease` is taken
wholesale (§C), since that function calls
`editableTimes.peekDurationMillis()`. Order consequence:
§C's wholesale take depends on steps 1–3 having landed;
never land §C before Flag B's step 3.

### B4. Scope warnings — what this swap does NOT wire

- **Editable breaks stay unwired by this swap alone.** In the
  pill and batch-9 lineages `breakDurationSeconds()` has **no
  caller** — the only live call through the seam is
  `peekDurationMillis()`. The creation-time stamp that
  consumes it exists only in Frist's tree
  (`MonolithRepository.kt:1377`,
  `breakSeconds = editableTimes.breakDurationSeconds()`),
  riding his `focus_sessions.breakSeconds` column. Nobody
  signs off "editable breaks work" from Flag B alone.
- **The pill countdown seed stays constant.** Both pill and
  batch-9/clone ViewModels seed
  `secondsRemaining = MonolithRepository.PEEK_DURATION_SECONDS`
  (clone `MonolithViewModel.kt:2405`). Identical to the
  provider's value at the default (120 s), divergent the day
  `peekMinutes` is edited — the lease would hold the edited
  length while the pill prints the constant. Moving the seed
  to the provider's value is a separate, named follow-up; it
  is **not** part of Flag A or Flag B, and this package does
  not silently include it.

---

## BONUS DELTA — pill `startPeekLease`, taken wholesale

Found while verifying Flag B; it lives in the repository, not
the ViewModel. Batch-9's version writes the constant directly
and enforces nothing else. The pill version carries three
behaviours batch-9 lacks. **Take the pill function wholesale
at batch-10**, subject to §C2 and §C3.

### C1. The two functions, verbatim

Pill — `MonolithRepository.kt:1180–1191`:

```kotlin
    suspend fun startPeekLease(packageName: String) {
        val durationMillis = editableTimes.peekDurationMillis()
        if (durationMillis <= 0L) return
        val nowMono = SystemClock.elapsedRealtime()
        val leaseEndMono = nowMono + durationMillis
        dao.clearOtherPeekLeases(packageName)
        dao.updatePeekLeaseAndCooldown(
            packageName = packageName,
            activeUntilMono = leaseEndMono,
            peekEndMono = leaseEndMono
        )
    }
```

Batch-9 — `MonolithRepository.kt:1194–1202`:

```kotlin
    suspend fun startPeekLease(packageName: String) {
        val nowMono = SystemClock.elapsedRealtime()
        val leaseEndMono = nowMono + PEEK_DURATION_MILLIS
        dao.updatePeekLeaseAndCooldown(
            packageName = packageName,
            activeUntilMono = leaseEndMono,
            peekEndMono = leaseEndMono
        )
    }
```

The three deltas, in order of appearance:

1. **Duration through the provider** (`editableTimes.peekDurationMillis()`
   instead of the `PEEK_DURATION_MILLIS` constant) — this is
   the call that makes Flag B live at grant time.
2. **Refusal at the source** — `durationMillis <= 0` returns
   before any write: `peekMinutes = 0` grants nothing and
   touches no row (no lease, no cooldown armed).
3. **One live lease** — `dao.clearOtherPeekLeases(packageName)`
   runs before the grant write, so storage can never hold two
   live leases.

### C2. DAO companion — `clearOtherPeekLeases` must be added

Verified: the method exists **only** in the pill DAO
(`MonolithDao.kt:40–41`). Batch-9 DAO: absent. Clone DAO:
absent. Truth-3-times DAO: absent. Taking §C1 wholesale
without this method does not compile. Add it to
`MonolithDao.kt` immediately after
`updatePeekLeaseAndCooldown` (batch-9 `:32`), verbatim from
the pill DAO, query included:

```kotlin
    @Query("UPDATE blocked_apps SET activePeekUntilElapsedRealtime = 0 WHERE packageName != :packageName AND activePeekUntilElapsedRealtime > 0")
    suspend fun clearOtherPeekLeases(packageName: String)
```

Semantics (pill DAO doc, `:34–39`): zeroes every **other**
row's live lease in the same write round; cooldown anchors
are untouched — a zeroed row keeps the cooldown it earned.

### C3. Signature companion — the `Boolean` patch (§A5's other half)

Apply to the pill function as taken. Exact diff against pill
`MonolithRepository.kt:1180`:

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

Statement-position callers (existing tests) are
source-compatible with the new return value — a `Boolean`
returned into a statement position is legal Kotlin and needs
no call-site change. The one expression-position caller is
the composed ViewModel (§A2), which is written for exactly
this signature.

---

## 6. Batch-10 execution sequence (single ordered list)

Do these in order. Steps 1–2 are Flag B's prerequisites;
steps 3–5 land Flag B + the bonus delta; step 6 is Flag A
(already composed on `agent/issue-16` — if the merger works
from that branch, step 6 is a verify-only step).

1. [ ] `MonolithDatabase.kt`: add `MIGRATION_12_13` (SQL in
       §B2 step 1), bump `version` 12 → 13, register the
       migration in `addMigrations(...)`.
2. [ ] `MonolithModels.kt`: append `peekMinutes: Int = 2` and
       `breakMinutes: Int = 10` to `GuardSettingsEntity`
       after `shortsReelsSniperEnabled` (comma added there).
3. [ ] `data/EditableTimes.kt`: land Frist's file whole from
       `builds/truth-3-times/Focus-app/app/src/main/java/com/example/data/EditableTimes.kt`.
4. [ ] `MonolithDao.kt`: add `clearOtherPeekLeases` (§C2)
       after `updatePeekLeaseAndCooldown`. Confirm
       `getGuardSettingsSnapshot` present (expected `:241`
       pre-merge) — no change to it.
5. [ ] `MonolithRepository.kt`: ensure the instance
       `private val editableTimes = EditableTimesProvider(dao)`
       exists (pill `:95` placement); replace `startPeekLease`
       with the pill function (§C1) **with** the `Boolean`
       patch applied (§C3).
6. [ ] `MonolithViewModel.kt`: take `start2MinutePeek` exactly
       as quoted in §A2 (guard + latch + gate + ST-08 +
       record) and add `import android.content.Intent` (§A4).
       Confirm `com.whatsapp` appears nowhere in the function.
7. [ ] Hand the composed tree to Ren. **The merger does not
       compile it** — compilation happens once, on Sarthak's
       word, per the build authority.

## 7. Verification record — what was checked, what changed

Every line number in this package was read from the file
itself on Oct 30, not carried over from an earlier report.

**Verified exactly as briefed:**

- Batch-9 `start2MinutePeek` at `MonolithViewModel.kt:2384`,
  guard-first, no blank-package default. ✅
- Pill `start2MinutePeek` at `MonolithViewModel.kt:2155`, no
  guard, `ifBlank { "com.whatsapp" }` at `:2157`, ST-08
  `runCatching` launch between `startPeekLease` (`:2168`) and
  `recordIntercept` (`:2184`). ✅
- `import android.content.Intent`: pill VM `:4`, clone `:4`,
  absent from batch-9 VM; `appContext` present in batch-9 VM
  at `:204`. ✅
- Frist seam identity: same package/class, same constructor
  `(MonolithDao)`, same two suspend signatures; provider reads
  `dao.getGuardSettingsSnapshot()`, present in batch-9 DAO at
  `:241`. ✅
- Bonus delta: pill `startPeekLease` at
  `MonolithRepository.kt:1180–1191` calls
  `dao.clearOtherPeekLeases` (`:1185`); batch-9's version at
  `:1194–1202` writes the constant and lacks it. ✅

**Refinements (facts updated, none contradicted):**

1. **The clone's Flag A is one step past Pastille's
   composition.** The briefed composition (base + ST-08 +
   import) is all present, but the clone also carries the
   builder's grant gate — `startPeekLease: Boolean` in both
   the ViewModel coroutine and the clone repository
   (uncommitted working-tree changes on `agent/issue-16`,
   2 files). §A2 quotes the clone as it actually stands;
   §A5 + §C3 carry the reasoning and the companion patch.
   Without the gate, Flag A + Flag B composed together would
   launch unleased apps at `peekMinutes = 0`.
2. **Flag B is an add, not a swap, in the batch-9 lineage**
   (§B3): no `EditableTimes.kt` file and no
   `EditableTimesProvider` instance exist in batch-9 or the
   clone. "Consumers untouched" holds for every ViewModel
   call site; the repository instance line is part of the
   landing, arriving with §C.
3. **"Frist's five columns"** resolves to 2 guard-settings
   columns (needed by this flag) + 3 focus-sessions columns
   (his wider slice), all five in his `MIGRATION_11_12`
   (truth-3-times `MonolithDatabase.kt:197–201`). The
   batch-10 vehicle must be a new `MIGRATION_12_13` — the
   `11_12` name/pair is already spent in the batch-9 lineage
   on different SQL. §B2 gives the minimum SQL and the
   entity-field placement for this lineage.

## 8. Sign-off checklist

**Merger (batch-10):**

- [ ] §6 executed in order; no step skipped, none reordered.
- [ ] Merged `start2MinutePeek` diffed against §A2 — identical.
- [ ] `grep com.whatsapp` over the merged ViewModel: the only
      acceptable hits are pre-existing, outside this function;
      zero inside `start2MinutePeek`.
- [ ] `grep -c "recordIntercept"` inside the composed
      function: the `"Used 2m Peek"` record appears once.
- [ ] `grep clearOtherPeekLeases`: present in merged DAO and
      called exactly once, inside `startPeekLease`.
- [ ] `grep MIGRATION_12_13`: defined once, registered once,
      `version = 13`.
- [ ] Tree handed to Ren DECLARED, uncompiled, with this
      package attached.

**Cell sign-off:**

- [x] Both flags + bonus delta verified against source files
      (paths and lines in §0, §7).
- [x] Composed function quoted from the clone, not
      reconstructed.
- [x] Ordering proof stated step by step (§A3).
- [x] Dependency order stated with its failure mode (§B2).
- [x] No compilation claimed; no test run claimed.

— Finisher-4 (issue-16 cell)
