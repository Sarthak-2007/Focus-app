# WELLE — Technical execution spec: Batch 10 composition + MIGRATION_12_13 (#15)

*Welle, technical execution. The everything-merge, audited at the
data layer: what the composed migration actually is, whether the
entity and the migrated schema are one shape, what happens to the
data already on his phone, and what is still unproven. Evidence:
`origin/batch-10` (tip c61859b), Konto's merge plan editions 1–3 in
this cell, trunk v12. Nobody compiled or merged — Ren does both,
on Sarthak's word.*

## 1. State of the merge (verified, not assumed)

- Every cell branch `agent/issue-2 … agent/issue-17` is merged into
  `batch-10`, including `agent/issue-12` (443fa73, F-1) and this
  cell's own branch (2666941). Two hand-work/fix rounds and a signing
  fix sit on top.
- `batch-10` is the **only** branch at DB version 13. A census of
  every remote branch's `MonolithDatabase.kt` shows no competing
  migration and no lane-private 11→12 text surviving anywhere —
  the lanes' deltas were re-based into the one 12→13, as the plan
  required.
- Two batch-10 additions are deliberately NOT schema:
  `ReceiptFiledStore` (#13) and `SetupStore` (#14) are
  SharedPreferences stores beside Room (DayWordStore pattern,
  `commit()` writes). They move no version and need no migration —
  correct call; a filed-marker or a setup stamp is chrome, not
  session truth.

## 2. MIGRATION_12_13 — the composed migration, column by column

Location: `MonolithDatabase.kt` on batch-10, registered in
`addMigrations(…, MIGRATION_11_12, MIGRATION_12_13)`; DB version 13.
Shape: **schema only — 8 ADD COLUMNs, zero data writes, no new
table, no backfill.** ADD COLUMN order mirrors the entity append
order per table (the composed-order law), which matters because
Room's runtime TableInfo comparison includes column order affinity
via the expected schema the entities generate.

| # | Table | Column | SQL | Entity default | Provenance | Truthful pre-v13 value? |
|---|---|---|---|---|---|---|
| 1 | guard_settings | peekMinutes | `INTEGER NOT NULL DEFAULT 2` | `Int = 2` | Frist (truth-3-times) | Yes — the lease was hardcoded 2 min |
| 2 | guard_settings | breakMinutes | `INTEGER NOT NULL DEFAULT 10` | `Int = 10` | Frist | Yes — breaks were hardcoded 10 min |
| 3 | focus_sessions | breakSeconds | `INTEGER NOT NULL DEFAULT 600` | `Int = 600` | Frist | Yes — `SAFE_BREAK_DURATION_SECONDS` was the constant this replaces; a session in flight across the upgrade prices its breaks at exactly what they were |
| 4 | focus_sessions | extensionSecondsAdded | `INTEGER NOT NULL DEFAULT 0` | `Int = 0` | Frist (ADD TIME) | Yes — no extension could ever have been granted |
| 5 | focus_sessions | extensionLog | `TEXT NOT NULL DEFAULT ''` | `String = ""` | Frist (W-44) | Yes — no events existed to log |
| 6 | focus_sessions | withinLabel | `TEXT` (nullable, no DEFAULT) | `String? = null` | Voss (routine-edit, RC-03) | Yes — NULL = no label was frozen; the only nullable column of the eight, correctly so |
| 7 | focus_sessions | heldShutAtSeal | `INTEGER NOT NULL DEFAULT -1` | `Int = -1` | W-41 split (Suki's Car-3 offer, built at this merge) | Yes — -1 = not recorded, by analogy to heldAppsAtSeal; the HELD line prints only when shut, peek AND sites are all ≥ 0, else it is omitted. A pre-v13 row can never print a fabricated split |
| 8 | focus_sessions | heldPeekAtSeal | `INTEGER NOT NULL DEFAULT -1` | `Int = -1` | W-41 split | Same as #7 |

Entity/migration agreement: I checked all eight — Kotlin type,
nullability, and default value match the SQL exactly (`Int↔INTEGER`,
`String↔TEXT`, defaults 2/10/600/0/''/-1/-1, withinLabel nullable
with no default on both sides). The struck column `testDateEpochDay`
is created nowhere, as at v9–v12.

**Effect on existing user data:** every existing row survives
untouched except gaining the eight defaults above, each of which is
the value that row was already living under. Singleton
`guard_settings` row (id=1) gains peek 2 / break 10 — the exact
hardcoded behaviour it replaces, so the first v13 launch behaves
identically until he changes a setting. No destructive fallback
exists (`fallbackToDestructiveMigration` was removed at v8 and stays
removed): a missing or malformed migration is a loud crash in
testing, never a silent wipe on his phone.

## 3. The one unproven link: there is NO 12→13 migration test

This is the gap I am filing as the #15 data-layer blocker:

- `RoutinesMigrationChainTest` on batch-10 still ends its chain at
  `MIGRATION_11_12` and validates "against every v12 entity".
- `BreakMigrationTest` covers 11→12 alone.
- Nothing opens a v12-shaped file, applies `MIGRATION_12_13`, and
  validates the result against the v13 entities. Room only compares
  TableInfo at open time on a real device — i.e. the first true test
  of this migration, as composed, would currently be **his phone**.

**The cure (priced):** extend the BreakMigrationTest pattern —
(a) v12-shaped file + 12→13 alone: all 8 columns land, a pre-existing
session row reads breakSeconds 600 / extensionLog '' / withinLabel
NULL / held split -1, the settings row reads peek 2 / break 10, the
row survives, `testDateEpochDay` exists nowhere;
(b) full chain 7→8→…→12→13 through Room's own builder, validated
against every v13 entity (the chain test's existing shape, one link
longer).
Effort: ~½ builder-day. Risk of writing it: none — test-only.
Risk of NOT writing it: a single default-value string mismatch
(Room compares defaults textually) ships as an
`IllegalStateException` on first launch after update, on the one
phone that matters, with no destructive fallback to save it.

## 4. Composition risks beyond the migration

1. **F-1's two surviving risks ride this merge** (full treatment in
   my #12 spec): the unguarded sealed-intention rewrite in
   `completeTaskAndAdvance` (batch-10 `MonolithRepository.kt:2865`,
   ESR-02 ruling still open) and the seed-sync dedup key
   `intention|targetDurationSeconds|completedTasksSummary` (:441),
   whose collision F-1 makes materially likelier. Both are ~2–5-line
   fixes + one test each; both are Ren's merge rulings, and neither
   should ride silently.
2. **Held-split birth writer (W-41):** the split is written by the
   birth writer beside the merged count (commit 060c9b2) because it
   cannot be reconstructed at print time. Verify at merge that the
   writer runs inside `startSession`'s birth transaction (the
   Fader writer law that governs heldAppsAtSeal) — a split written
   outside it could disagree with the count it splits.
3. **Ordering inside the merge itself:** Konto's plan Step 2
   (migration composed FIRST) was followed — fb5213a lands the
   composed migration + entities as one commit, before the fix
   rounds. The R1 sub-decision the plan carried (rows 7–8 exist in
   no tree: confirm -1 / set 0 / hold) was resolved as **-1** in the
   composed text, consistent with the seal-count columns. Correct.
4. **Signing:** c61859b replaced the committed debug keystore with
   the one that signed his installed builds — without it no CI APK
   from this branch installs over his phone build. Not a data-layer
   item, but it gates whether the v13 build can reach the phone at
   all, so it belongs on this epic's checklist.

## 5. Price of the epic, honestly

- Composition itself: done on the branch (16 cell merges + hand-work
  + 2 fix rounds). Remaining data-layer price: the §3 migration test
  (~½ day) + the two §4.1 rulings/fixes (~½ day with tests).
- Data risk if shipped as-is: low probability, high blast radius —
  concentrated entirely in §3 (unvalidated first-open on his phone)
  and §4.1 (silent record loss / receipt re-titling, both slow
  truth leaks rather than crashes).
- Recommendation: Batch 10 is mergeable in composition; it is not
  phone-ready until the 12→13 migration test exists and passes and
  the two F-1 rulings are made explicitly.

— Welle, technical execution
