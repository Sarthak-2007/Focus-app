# Issue #16 — Merge flags for the batch-10 merge, verified in code

*Pastille (cell lead), Oct 30 2026. Both flags re-verified line by
line against the three trees (pill lane, batch-9 merge tree,
truth-3-times). The composition for Flag A is pre-applied on
branch `agent/issue-16` — DECLARED, uncompiled, per the build
authority (only Ren compiles, on Sarthak's word).*

## Flag A — `start2MinutePeek`: Suki guard + Pastille launch

**Verdict: COMPOSES. One hand-merge, already prepared on this
branch.**

The two halves, as they stand:

- **Batch-9 (Suki's half)** — `MonolithViewModel.kt:2384`: the
  grant first re-derives the offer from the current row, cooldown
  and pill (`peekOfferFor(...)`, UNA2-01 second gate); a refusal
  returns with the airlock standing, and a blank package no
  longer defaults to an unrelated app. Then latch + pill state,
  then the coroutine: `repository.startPeekLease(pkg)` →
  `repository.recordIntercept(...)`.
- **Pill lane (Pastille's half)** — `MonolithViewModel.kt:2155`:
  no guard (the lane's base predates it), and its
  `ifBlank { "com.whatsapp" }` package default — which the guard
  exists to kill — must NOT survive the merge. Its one true
  addition is ST-08 inside the coroutine: after the grant,
  `runCatching { getLaunchIntentForPackage(pkg) … startActivity }`,
  then `recordIntercept`.

**The composition** (applied on this branch):

1. Batch-9 function kept whole — guard, latch, pill state.
2. ST-08's `runCatching` launch block inserted inside the
   coroutine **between `startPeekLease` and `recordIntercept`**.
3. `import android.content.Intent` added (pill VM line 4;
   absent from the batch-9 VM). `appContext` already exists in
   the batch-9 VM (line 204).

Ordering, which is the whole point of the flag:

```
guard (pre-grant, refusal leaves the airlock standing)
  -> grant written (startPeekLease)
    -> launch fires (the sniper admits the app he lands in,
       because the lease is already written)
      -> intercept recorded
```

## Flag B — Frist's `EditableTimesProvider` into Pastille's seam

**Verdict: SWAPS IN UNCHANGED. No consumer rewiring.**

- Same class: `com.example.data.EditableTimesProvider`, same
  constructor `(MonolithDao)`, same two suspend signatures —
  `peekDurationMillis(): Long`, `breakDurationSeconds(): Int`.
  (Frist's version drops the `@Suppress("unused")` on the dao;
  the dao is used now. That is the only structural delta.)
- Pastille's consumers: `MonolithRepository.kt:95`
  (`private val editableTimes = EditableTimesProvider(dao)`) and
  the single call in `startPeekLease` (:1181). Frist's own tree
  wires the identical instance at :96. Neither side's call sites
  change.
- Frist's provider reads `dao.getGuardSettingsSnapshot()` —
  already present in the batch-9 DAO (`MonolithDao.kt:241`) —
  and the entity's `peekMinutes` / `breakMinutes` columns.

**Dependency order at the merge** (this is the flag's one sharp
edge): the provider file may only land **after** MIGRATION_12_13
adds `peekMinutes` / `breakMinutes` to `GuardSettingsEntity`
(Frist's five columns, batch-10 epic). Before that migration the
file does not compile against the v12 entity; after it, it drops
in as a pure file replacement. Values then read clamped
(peek 0–30, break 0–60); `peekMinutes = 0` makes
`startPeekLease` grant nothing, which composes with Flag A's
guard semantics (refusal at the source).

## Bonus delta found while verifying (repository, not VM)

Pastille's `startPeekLease` also carries one-live-lease
enforcement batch-9 lacks: it reads the duration through the
provider (0 → no grant) and calls
`dao.clearOtherPeekLeases(packageName)` before writing, so
storage can never hold two live leases (pill lane
`MonolithRepository.kt:1180–1191` vs batch-9 :1194–1202, which
writes the constant directly). At the batch-10 merge this
function should be taken from the pill lane wholesale, then
Flag B's provider lands under it per the order above.

— Pastille
