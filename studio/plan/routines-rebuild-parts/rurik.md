# ROUTINES REBUILD — Migration(8,9), exact and build-ready

**Seat:** Rurik, Maximum-Implementable (data plumbing). **Coordinator:** Voss.
**Scope of this file:** the complete schema delta for DB **8 → 9** — every
column, its table, type, nullability, default, and the verbatim Kotlin
migration that creates them. Nothing else in the routines rebuild is specced
here; the engine (Sten's reconciler / query / funnel) consumes this schema.
**Read-only sources, verified this seating:** shipped
`data/MonolithModels.kt` / `MonolithDatabase.kt` / `MonolithDao.kt` (DB
version **7** as shipped; the token build takes it to 8 first);
`plan/maximpl-parts/05-sten-routines.md` (the plan); **`invention/maximpl-
parts/05-sten.md` §5 (Sten's *Derive, Don't Fire* — the arbitrated design,
DDL written in full)**; `invention/maximpl-parts/08-nadia.md` Mech. 3;
`invention/maximpl-parts/07-rurik.md` Inv. 3; arbitration as recorded in
`plan/REGISTER.md` ("Data — migration arbitration") and
`invention/MAXIMPL-INVENTIONS.md` (Voss's coordinator rulings).

---

## 0. The headline, corrected — "routines 13 cols" is not a 13-column routines table

The arbitration headline reads "routines 13 cols." Read literally — the
`routines` table gaining or holding 13 columns — **it is false, and the
code + the arbitrated DDL both prove it:**

- Shipped `RoutineEntity` (`MonolithModels.kt`, table `routines`) has
  **7 columns**: `id, name, startMinutes, endMinutes, daysMask,
  loadoutName, enabled`.
- Sten's arbitrated Migration(8,9) DDL (`invention/05-sten.md` §5) is
  **13 `ADD COLUMN` statements spread over three tables** — 2 on
  `routines`, 7 on `focus_sessions`, 4 on `guard_settings` — and **zero
  new tables**. The merge record says it in so many words: *"13 ADD
  COLUMNs over routines / focus_sessions / guard_settings"*
  (`MAXIMPL-INVENTIONS.md`); the Register: *"Sten's 13 ADD COLUMNs."*
- After this migration the `routines` table has **9 columns**, not 13.

So "13" is the count of the routines *rebuild's* column delta, not of the
`routines` table. With the two arbitrated ride-alongs (§2, §3), the full
Migration(8,9) is **15 `ADD COLUMN` statements, 0 tables, 0 indexes,
0 data writes.**

Two older texts are superseded and must not be built from:

- **Plan §3.6** (`plan/maximpl-parts/05-sten-routines.md`) specced a
  Migration(7,8) adding `pausedUntilMillis`, `autoPausedAtMillis`,
  `deleteAfterWindowEnd` to `routines` plus a `CREATE TABLE
  routine_attendance`. All of it is struck: `autoPausedAtMillis` is
  **formally NOT created** (auto-pause struck, MASTER-PLAN §0; Sten
  invention §6 kill list), `deleteAfterWindowEnd` dies with R5 (a live
  session obeys its stored snapshot, so pending-delete is unnecessary),
  and `routine_attendance` is **never created** (attendance is a query
  over session rows, Sten invention §3).
- **My own invention file** (`invention/07-rurik.md`) put `taskId` in a
  shared v7→v8 and Nadia's file put `startedAtSessionElapsedSec` on the
  7→8 ride. Arbitration overruled both placements: 7→8 is token-only
  and CLOSED; both columns ride **8→9** (this file). My step-rows and
  completion-events tables are **9→10**, named in §6, and are NOT in
  this migration.

---

## 1. The census — all 15 columns

Types are Room/SQLite types as they must appear in DDL. Kotlin types are
the entity field types the builder declares in the same change; Room's
post-migration schema validation compares the two, so they are part of
the spec, not commentary.

### 1a. Sten's 13 (arbitrated DDL, `invention/05-sten.md` §5 — verbatim)

**`routines` — +2 (7 → 9 columns)**

| # | Column | DDL type | Kotlin | Justification against Sten's derive() design |
|---|---|---|---|---|
| 1 | `pausedUntilMillis` | `INTEGER NOT NULL DEFAULT 0` | `Long = 0L` | Plan §3.5 pause-until. It is an input to the one derivation: `windowState(routine, now, …) = f(enabled, pausedUntilMillis, daysMask, …)` (Inv. §1.1 R1). 0 = not paused. |
| 2 | `scheduleEditedAtMillis` | `INTEGER NOT NULL DEFAULT 0` | `Long = 0L` | Inv. §3.5 eligibility horizon: the attendance query judges a no-session window only if it starts after the last edit — before that horizon the current row cannot prove the window was ever scheduled, so the query says UNKNOWN instead of inventing a MISSED. Stamped by the single `persistRoutine` funnel on every write. 0 = no edit recorded under the new engine. |

**`focus_sessions` — +7 (the session row becomes the attendance row, Inv. §3 Q1)**

| # | Column | DDL type | Kotlin | Justification |
|---|---|---|---|---|
| 3 | `routineId` | `INTEGER` (nullable, no default) | `Int? = null` | Derivation basis (a), Inv. §0: the one routine fact not recoverable from existing rows. END/stand-down matching becomes `active.routineId == routine.id` against the stored window end — **Lie #13 (name-matched END) dies here**. NULL = manual session. |
| 4 | `windowStartMillis` | `INTEGER NOT NULL DEFAULT 0` | `Long = 0L` | Frozen schedule snapshot (R5): with `routineId` it is the natural key `(routineId, windowStartMillis)` that makes materialisation an idempotent upsert (R3) and keys the attendance query. |
| 5 | `windowEndMillis` | `INTEGER NOT NULL DEFAULT 0` | `Long = 0L` | The live session's law (R5): it stands down by its **stored** end, whatever happens to the routine row afterwards — this column is what deletes the rename fence, the reseed postponement, and pending-delete in one move. |
| 6 | `routineOutcome` | `TEXT` (nullable, no default) | `String? = null` | The outcome stamp (Q1): NULL until finalised, then ATTENDED / PARTIAL / MISSED / LATE_FIRE per plan §5.4 thresholds. Stamps are immutable (Inv. §3.3) — finalise-on-observe never re-judges a stamped row. |
| 7 | `evidenceMask` | `INTEGER NOT NULL DEFAULT 0` | `Int = 0` | Plan §5.3's E1–E4 evidence bits, retargeted by the single funnel `recordAttendanceEvidence` from the dead attendance table onto this row. |
| 8 | `firstEvidenceMillis` | `INTEGER NOT NULL DEFAULT 0` | `Long = 0L` | One end of the evidence instant set from which attended seconds are computed once at finalise (Q2 — ⋃[t, t+5min] ∩ window), replacing the plan's 30-second sampler. 0 = no evidence yet. |
| 9 | `lastEvidenceMillis` | `INTEGER NOT NULL DEFAULT 0` | `Long = 0L` | The other end of that set; also the trailing-5-minute decay input. 0 = no evidence yet. |

**`guard_settings` — +4 (the single-row settings table absorbs every global)**

| # | Column | DDL type | Kotlin | Justification |
|---|---|---|---|---|
| 10 | `routineReseedV1Done` | `INTEGER NOT NULL DEFAULT 0` | `Boolean = false` | Plan §4 reseed flag. The reseed data transform is **not** in this migration (TOKEN-SPEC §6.4 / Sten A1): it runs in `ensureSeededAndSyncPackages` under `seedMutex`, flag-guarded, version-blind. This column is only its memory. |
| 11 | `routineReseedNoteDismissed` | `INTEGER NOT NULL DEFAULT 0` | `Boolean = false` | Plan §4: the one-time `SCHEDULE RESET TO YOUR DAY` strip shows until he opens any routine editor (or 14 days pass). |
| 12 | `lastBootAtMillis` | `INTEGER NOT NULL DEFAULT 0` | `Long = 0L` | Inv. §3 Q3's boot witness: a closed window containing a boot, with no session, is the only provable NOT_DELIVERED. Stamped at every boot / seed-sync. One instant only — older windows honestly fall to UNKNOWN/MISSED rules (Inv. §3.2). |
| 13 | `testDateEpochDay` | `INTEGER NOT NULL DEFAULT -1` | `Long = -1L` | Inv. §4 Saturday: the test-date exception as one Long + a predicate inside `RoutineSchedule` (`isSuppressed`), so suppressed windows never exist to any consumer — no miss, no streak gap, by construction. −1 = no test date set. (Epoch-day is a `Long` from `LocalDate.toEpochDay()`; SQLite stores it in the same INTEGER.) |

### 1b. The two ride-alongs (arbitration: same migration, named owners)

| # | Column | Table | DDL type | Kotlin | Owner |
|---|---|---|---|---|---|
| 14 | `startedAtSessionElapsedSec` | `focus_tasks` | `INTEGER` (nullable, no default) | `Int? = null` | Nadia — §2 below |
| 15 | `taskId` | `focus_sessions` | `INTEGER` (nullable, no default) | `Int? = null` | Rurik — §3 below |

No other schema change is in 8→9. In particular: **no new entity is
registered in `@Database`** (zero new tables — the entity list in
`MonolithDatabase.kt` is untouched by this migration), no index is
created (the `(routineId, windowStartMillis)` natural key is enforced
by the reconciler's upsert logic, Sten R3 — a unique index is not in
Sten's arbitrated DDL and none is added here), and the migration
**writes no data** (§4).

---

## 2. `startedAtSessionElapsedSec` — Nadia's offset (column 14)

- **Table:** `focus_tasks` (`FocusTaskEntity`).
- **Semantics:** the session-elapsed seconds at the instant this task
  was promoted to ACTIVE. From then on the task's live spent time is a
  derivation, not a counter: `taskSpentSec = frame.sessionElapsedSec −
  startedAtSessionElapsedSec` (Nadia Inv. Mech. 3). On completion the
  final derived value is stamped once into the existing
  `actualSecondsSpent` and this column returns to NULL — non-NULL means
  exactly one thing: *this task is currently counting against a live
  session offset.*
- **Who writes it:** the repository's promotion funnel only
  (`promoteTaskToActive` / the Arm Ticket's `cueTask`, Inv. 3 — the
  single path by which a task becomes ACTIVE). Two writer rules, both
  in that funnel's neighbourhood, ~2 lines each: (i) promotion
  mid-session stamps the Now Frame's current `sessionElapsedSec`;
  promotion with no session running stamps `0`; (ii) **arm re-stamp:**
  when a session arms with an already-ACTIVE task, the arm path resets
  that task's offset to `0` in the same write that creates the session
  — session elapsed restarts at 0, so an offset carried over from a
  previous session would derive negative/understated time. Nothing
  else writes the column; the per-second tick
  (`incrementActiveTaskSecond`) is *deleted* by Nadia's mechanism, not
  given a new target.
- **Why elapsed-session seconds, not wall clock:** the value is an
  offset **inside session-elapsed space** — the same space
  `derive()` publishes. That space is built from
  `startElapsedRealtimeMillis` (`SystemClock.elapsedRealtime()` —
  monotonic) plus the session's reboot accounting (`bootCount`,
  `accumulatedBeforeRebootSeconds`, both already on
  `FocusSessionEntity`). A wall-clock stamp (`System.currentTimeMillis`)
  would inherit every wall-clock fault — user clock changes, timezone
  travel, NTP steps — and would need re-anchoring through the boot
  count to compare against session elapsed at all. An offset in
  session-elapsed seconds needs no conversion, survives reboot (the
  reboot is already priced into the session's own elapsed derivation),
  and cannot drift: it is sampled once, from the frame, at promotion.
- **Nullability / existing rows — the no-backfill law applied:** the
  column is **nullable with no DEFAULT**, so existing rows get `NULL`.
  `0` could not serve as the unknown marker: 0 is a real, common value
  (a task promoted at the instant its session arms). NULL is the only
  truthful statement about a pre-migration ACTIVE task — *no offset
  was ever recorded for it* — and derivations treat NULL as "fall back
  to stored `actualSecondsSpent` until the next promotion stamps an
  offset." Inventing 0 for a task promoted mid-session under the old
  build would attribute the whole session's elapsed to it: a fabricated
  number of exactly the kind TOKEN-SPEC §6.3 forbids.

## 3. `taskId` — the session↔task bond (column 15, mine)

- **Table:** `focus_sessions` (`FocusSessionEntity`) — **not** the
  step tables. (The `taskId` inside `TaskSubStepEntity` is a different
  column on a different table; it belongs to 9→10, §6.)
- **Semantics:** the id of the `FocusTaskEntity` this session was
  armed **for**, carried by the Arm Ticket (Inv. 3): `cueTask(taskId)`
  mints the ticket, `armFromTicket` consumes it and writes the id once,
  at session creation, alongside the frozen `intention` title snapshot
  (which is thereby demoted to a display label — it is no longer the
  linkage). The per-second tick's replacement credits the active
  session's `taskId`; sessions with NULL credit nothing and perform no
  queue surgery.
- **Who writes it:** `startSession(taskId = ticket.taskId)` at arm —
  the only writer, once, never updated. Routine sessions carry NULL
  (their bond is `routineId`, column 3 — a routine is not a task);
  plain Deck arms with no cued task carry NULL. NULL therefore means
  *this session was not armed for a task*, which for manual and
  routine sessions alike is simply true.
- **Backfill rule — none, and why that is truthful:** existing rows get
  `NULL` (nullable column, no DEFAULT clause, no UPDATE in the
  migration — TOKEN-SPEC §6.3's law: Room migrations here never invent
  data). A backfill is *possible-looking* and would be false: the only
  candidate evidence is the copied `intention` title string and the
  reverse pointer `completedInSessionId` on task rows. Titles are not
  identity — two tasks can share a title, a rename orphans the string,
  and `completedInSessionId` is 0 for out-of-session completions and
  stale after un-complete (Inv. file, D6). Matching on either would
  mint bonds the old build never recorded and present guesses as
  history. NULL says exactly what is true of every pre-migration
  session: **no task bond was recorded.** Sten's DDL sets the same
  precedent one column away — pre-existing sessions get
  `routineId = NULL` = manual, *"which is true: no shipped session was
  ever routine-stamped in a way the new engine may claim."*

---

## 4. Migration(8,9) — the full Kotlin block

Style follows TOKEN-SPEC §6's `MIGRATION_7_8` (an `object : Migration`
with verbatim `execSQL` strings). Sten's 13 statements are reproduced
**verbatim** from `invention/05-sten.md` §5; the two ride-alongs follow
in the same form, grouped and commented by owner. The builder lands
this in `MonolithDatabase.kt` rebased onto the token build's
`getInstance` — never a wholesale rewrite (Sten C1).

```kotlin
val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Sten — routines: pause (plan §3.5) + eligibility horizon (Inv. §3.5).
        // autoPausedAtMillis is NOT created: auto-pause is struck.
        // deleteAfterWindowEnd is NOT created: R5 makes pending-delete unnecessary.
        db.execSQL("ALTER TABLE routines ADD COLUMN pausedUntilMillis INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE routines ADD COLUMN scheduleEditedAtMillis INTEGER NOT NULL DEFAULT 0")

        // Sten — focus_sessions: routine identity, frozen window, evidence, outcome.
        db.execSQL("ALTER TABLE focus_sessions ADD COLUMN routineId INTEGER")
        db.execSQL("ALTER TABLE focus_sessions ADD COLUMN windowStartMillis INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE focus_sessions ADD COLUMN windowEndMillis INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE focus_sessions ADD COLUMN routineOutcome TEXT")
        db.execSQL("ALTER TABLE focus_sessions ADD COLUMN evidenceMask INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE focus_sessions ADD COLUMN firstEvidenceMillis INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE focus_sessions ADD COLUMN lastEvidenceMillis INTEGER NOT NULL DEFAULT 0")

        // Sten — guard_settings: reseed flags (plan §4) + boot witness (Q3) + test date (§4).
        db.execSQL("ALTER TABLE guard_settings ADD COLUMN routineReseedV1Done INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE guard_settings ADD COLUMN routineReseedNoteDismissed INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE guard_settings ADD COLUMN lastBootAtMillis INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE guard_settings ADD COLUMN testDateEpochDay INTEGER NOT NULL DEFAULT -1")

        // Nadia — focus_tasks: session-elapsed offset at promotion (Inv. 08, Mech. 3).
        // Nullable, no DEFAULT: existing rows stay NULL = no offset ever recorded.
        db.execSQL("ALTER TABLE focus_tasks ADD COLUMN startedAtSessionElapsedSec INTEGER")

        // Rurik — focus_sessions: the Arm Ticket's task bond (Inv. 07, Inv. 3).
        // Nullable, no DEFAULT, no backfill: NULL = no bond was recorded.
        db.execSQL("ALTER TABLE focus_sessions ADD COLUMN taskId INTEGER")

        // NO new table. routine_attendance is never created.
    }
}
```

**Entity fields declared in the same change** (`MonolithModels.kt`,
defaults must match the DDL above or Room's schema validation fails
loudly on first open — which, with the fallback gone, is the intended
failure mode): `RoutineEntity` + `pausedUntilMillis: Long = 0L`,
`scheduleEditedAtMillis: Long = 0L`; `FocusSessionEntity` +
`routineId: Int? = null`, `windowStartMillis: Long = 0L`,
`windowEndMillis: Long = 0L`, `routineOutcome: String? = null`,
`evidenceMask: Int = 0`, `firstEvidenceMillis: Long = 0L`,
`lastEvidenceMillis: Long = 0L`, `taskId: Int? = null`;
`FocusTaskEntity` + `startedAtSessionElapsedSec: Int? = null`;
`GuardSettingsEntity` + `routineReseedV1Done: Boolean = false`,
`routineReseedNoteDismissed: Boolean = false`,
`lastBootAtMillis: Long = 0L`, `testDateEpochDay: Long = -1L`.

**Registration (cumulative, no fallback line):**

```kotlin
@Database(
    entities = [ /* unchanged — 8→9 creates no table */ ],
    version = 9,
    exportSchema = false
)
// in MonolithDatabase.getInstance's builder:
Room.databaseBuilder(storageContext, MonolithDatabase::class.java, "monolith_tactile_db")
    .addMigrations(MIGRATION_7_8, MIGRATION_8_9)
    .build()
```

There is **no `fallbackToDestructiveMigration` line** — the token
build deleted it, and this build does not re-add it. From 8→9 on, a
missing migration is a loud `IllegalStateException`, never a wipe.

**Price:** 15 `execSQL` lines + 15 entity field lines + the version
bump and one-word `.addMigrations` extension ≈ **35 lines across 2
files** (`MonolithDatabase.kt`, `MonolithModels.kt`); chain tests §5
≈ 120 lines. No DAO change is forced by the schema alone. Half a day
including the tests — Sten's build order step 1, unchanged.

---

## 5. Chain tests that must accompany it (TOKEN-SPEC §8 item 8)

Item 8's two tests, added to — never replacing — token test 6, with
the fixture contents and assertions made exact:

**Test 8a — v7-shaped DB opens under v9 via the chain.**
Fixture: a database built at the shipped v7 schema (all 9 entities,
no `emergency_break_spends`), holding: ≥2 routine rows (one exactly
matching an old seed, one user-edited and one disabled — contents
chosen so a destructive or transforming migration would be caught),
≥2 session rows (one active, one completed, with non-trivial
`elapsedSeconds` / `completedTasksSummary`), ≥1 task row in ACTIVE
status with sub-steps in its notes string, the single
`guard_settings` row, and blocked-app/site rows. Open with the v9
builder (`.addMigrations(MIGRATION_7_8, MIGRATION_8_9)`, no fallback).
Assert:
- every pre-existing row is present and **field-for-field identical**
  in all its v7 columns (routine rows intact — item 8's words);
- `emergency_break_spends` exists and is **empty** (7→8 ran);
- new columns carry exactly the §1 defaults: routines
  `pausedUntilMillis = 0`, `scheduleEditedAtMillis = 0`; sessions
  `routineId IS NULL`, `taskId IS NULL`, `routineOutcome IS NULL`,
  `windowStartMillis = windowEndMillis = 0`, `evidenceMask = 0`,
  `firstEvidenceMillis = lastEvidenceMillis = 0`; tasks
  `startedAtSessionElapsedSec IS NULL`; guard_settings
  `routineReseedV1Done = 0`, `routineReseedNoteDismissed = 0`,
  `lastBootAtMillis = 0`, `testDateEpochDay = -1`;
- no `routine_attendance` table exists.

**Test 8b — v8-shaped DB with spend rows opens under v9, spends intact.**
Fixture: a database at the post-token v8 schema holding ≥1 row in
`emergency_break_spends` with known `sessionId`, `spentAtWallMillis`,
`weekAnchorMillis`, `heldSeconds`, plus routine and session rows as
in 8a. Open with the v9 builder. Assert: the spend rows are present
with every field **exactly** as written (item 8's words — spends
intact); routine/session/task/guard rows and new-column defaults
exactly as in 8a; the 7→8 migration does **not** re-run (the spends
table is neither recreated nor emptied).

Both tests fail loudly — exception, not silent pass — if either
migration is unregistered, which is the behaviour the deleted fallback
now guarantees in production too.

---

## 6. What is NOT here — named, so nobody builds it twice

- **9→10 (the plumbing batch, mine + Otis, later):** `task_sub_steps`
  (Inv. 1: `id`, `taskId`, `ordinal`, `title`, `isDone`) and
  `task_completion_events` (Inv. 2) — Rurik; Otis's readiness log —
  his to spec. Append/child tables only; no existing row's meaning
  changes. Nothing in 8→9 pre-creates, pre-names, or depends on them.
- **The reseed data transform** — not a migration callback (§1a #10).
- **Writers and derivations** — the reconciler, the attendance query,
  the evidence funnel, the Now Frame, the Arm Ticket: Sten's / Nadia's
  / my engine files. This file only guarantees the columns they read
  exist, with the defaults and nulls their truth rules require.

— Rurik, MaxImpl (data plumbing) · routines rebuild, migration part
