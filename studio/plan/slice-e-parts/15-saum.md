# Saum — Improvers (the seam) · SLICE E BUILD-READY · MIGRATION 9→10 + THE DEPENDENCY CHAIN

*Joint: Slice E's seams — Migration 9→10 (fork F15, ruled ADOPT by
Ren, Oct 29 09:05, `plan/RULINGS.md` Slice D/E rulings; Marlowe's
letter `mail/ren/0826`) and the dependency chain the merged file
carries. All line numbers against TODAY's trunk
(`~/workspace/clone-inspect/Focus-app`, read this run, no code
edited, no gradle run). Inbox at seating: empty (no unread
letters; `mail/saum/read/` empty). Coordination letters sent
this run: Nocke (E0/fault columns), Draht (F-DRAHT-2 resolved).*

**Trunk stamp, stated once because everything below hangs on
it:** trunk is DB **version 8** today — `data/MonolithDatabase.kt:24`
(`version = 8`), one migration registered
(`MIGRATION_7_8` at `:41`, `.addMigrations(MIGRATION_7_8)` at
`:79`), `fallbackToDestructiveMigration` already deleted by the
token build. Migration 8→9 does **not** exist in trunk; it is
Voss's routines lane in flight (§2.4). This spec is written to
compose on top of his registered name, exactly as Rurik filed
it (`plan/routines-rebuild-parts/rurik.md` §4:
`MIGRATION_8_9`, `.addMigrations(MIGRATION_7_8, MIGRATION_8_9)`,
`version = 9`).

---

## 1 — MIGRATION 9→10, BUILD-EXACT

### 1.1 The DDL, verbatim (the whole migration)

Four `ADD COLUMN`s, all on `focus_sessions`, zero new tables,
zero indexes, zero data writes. Column 1 is F15; columns 2–4 are
Draht's E4 fault persistence (§1.5), folded into this same
migration as required — one migration ceremony for the slice,
not two.

```kotlin
val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // F15 (Emil) — the frozen sealed count, captured at the seal.
        // NOT NULL DEFAULT -1: -1 = no count was ever captured.
        // 0 is a real seal (empty loadout), so 0 cannot be the sentinel.
        db.execSQL("ALTER TABLE focus_sessions ADD COLUMN heldAppsAtSeal INTEGER NOT NULL DEFAULT -1")

        // Draht (E4, 05-draht.md §5.2) — Release Proof fault state.
        // Exactly one link can fault (R2 SHIELD), so a Boolean, not a set.
        db.execSQL("ALTER TABLE focus_sessions ADD COLUMN releaseFaultShield INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE focus_sessions ADD COLUMN releaseRetriesDone INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE focus_sessions ADD COLUMN releaseFaultedAtWallMillis INTEGER NOT NULL DEFAULT 0")
    }
}
```

Lands in `data/MonolithDatabase.kt` directly below Voss's
`MIGRATION_8_9` block (which itself lands below `MIGRATION_7_8`,
today `:41–52`). Style follows `MIGRATION_7_8` verbatim-form:
`object : Migration`, one `execSQL` per column, comment naming
the owner and the truth rule — the form Rurik's 8→9 block
already established for this chain.

### 1.2 `heldAppsAtSeal` — type, nullability, default, argued

- **DDL:** `heldAppsAtSeal INTEGER NOT NULL DEFAULT -1`
- **Entity:** `val heldAppsAtSeal: Int = -1` — non-null `Int`,
  sentinel `-1`. The DDL default and the entity default must
  match exactly or Room's post-migration schema validation
  fails loudly on first open — with the destructive fallback
  deleted, that loud failure is the intended failure mode
  (Rurik §4, same law for 8→9).

**Why `-1` / NOT NULL, from the legacy-row case.** The case that
decides it: *a session sealed before the migration* — a row
written at v9 (or earlier), `isActive = 1`, still live when the
phone updates to the v10 build; on restore it is the active
session again, and the Deck's SEALED tenant must print from it.
For that row **no count was ever captured** — the capture code
did not exist when it sealed. The stored value must say exactly
that, and nothing else:

1. **Not `0`.** `0` is a real, lawful seal: an empty loadout
   seals zero apps and prints plainly (Uma, SLICE-D-E D6: "0
   prints plainly — an empty loadout is a state, not a
   failure"). Defaulting legacy rows to 0 would print
   `0 APPS HELD` for a session that may have sealed ten — a
   fabricated count, of exactly the kind TOKEN-SPEC §6.3's
   no-backfill law forbids (the same law Rurik §3 applies to
   `taskId`: no invented bonds; and §2 to Nadia's offset: NULL
   over a false 0).
2. **Not NULL.** A nullable `Int?` would also be truthful, but
   it costs a null branch at *every* reader for the life of the
   app, and the SEALED tenant's law is that it reads
   `activeSession.heldAppsAtSeal` **and nothing else** (Emil
   §3.2). `-1` inside a non-null `Int` keeps that read total:
   every value `≥ 0` is a captured count; the single value
   `-1` is "never captured". A count is `count { … } ≥ 0` by
   construction, so the sentinel can never collide with a real
   capture. Precedent inside this very chain: 8→9's
   `testDateEpochDay INTEGER NOT NULL DEFAULT -1` (Rurik §1a
   #13 — "−1 = no test date set"), a sentinel-in-domain-excluded
   value on a NOT NULL column, chosen over NULL for the same
   reason.
3. **No backfill, ever.** The migration contains no `UPDATE`.
   Nothing truthful exists to backfill from: the live Flow
   count at migration time is the count *now*, not the count
   at that row's seal, and writing it would mint the exact lie
   F15 was adopted to kill (Emil §7 fork (b), rejected: a
   restore-time recompute "silently absorbs any tier edit made
   during the gap").
4. **What `-1` renders is not my value to rule.** The value law
   ends at the column: `-1` ⇔ never captured. The rendering
   law is **Zettel's** (the receipt) — the standing filed form
   until his part lands is Emil §3.2's: a `-1` row renders the
   SEALED tenant from the live count **once** and freezes that
   for the residency, never reprinting. If Zettel rules
   otherwise, only the render changes; the column, the capture,
   and this migration do not.

The same argument covers columns 2–4 with `0`/`false`: a
pre-9→10 row was never faulted by Release Proof (the machinery
did not exist), its retries spent are truly 0, and it has no
fault instant — `0` is the truthful value, not a sentinel, so
no `-1` is needed there (Draht §5.2 field definitions, adopted
verbatim).

### 1.3 Entity field additions (`data/MonolithModels.kt`)

`FocusSessionEntity` begins at `:249` today. Four fields, one
change, defaults matching §1.1 to the digit:

```kotlin
@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    // … existing fields through tasksCompletedCount (:273) unchanged …
    val tasksCompletedCount: Int = 0,
    // F15: apps held at the seal, frozen at insert; -1 = never captured.
    val heldAppsAtSeal: Int = -1,
    // E4 (Draht §5.2): Release Proof fault state for this session's release.
    val releaseFaultShield: Boolean = false,
    val releaseRetriesDone: Int = 0,
    val releaseFaultedAtWallMillis: Long = 0
)
```

Placement is at the **tail** (after `tasksCompletedCount`,
`:273`, the current last field) — not beside `totalStudyBlocks`
(`:262`, Emil §3.2's parenthetical): constructor call sites in
`MonolithRepository.kt` (`:974`, `:278`, `:297`, `:316`) all use
named arguments, so tail placement is the zero-risk position —
no positional call site exists to break, and the diff stays
append-only in spirit with the migration. No other entity
changes; `@Database`'s entity list is untouched (no new table).

### 1.4 Version bump + registration (`data/MonolithDatabase.kt`)

Cumulative, in the merge train's order — this change is written
**on top of** Voss's 8→9 landing, never instead of it:

```kotlin
@Database(
    entities = [ /* unchanged — 9→10 creates no table */ ],
    version = 10,               // :24 — 8 today, 9 after Voss's lane
    exportSchema = false
)
// in getInstance's builder (:79 today):
.addMigrations(MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)
```

`MIGRATION_8_9` is named **exactly as Rurik filed it**
(`plan/routines-rebuild-parts/rurik.md` §4) — if Voss's lane
registers any other name, this registration line takes that
name; the dependency is on the *chain position* (8→9 must be
registered before 9→10 in the same builder call), not on the
spelling. No `fallbackToDestructiveMigration` line exists
today and none is re-added: a missing 9→10 on a v9 phone must
be a loud `IllegalStateException`, never a wipe (TOKEN-SPEC
§6.2 law, carried by both prior legs).

### 1.5 The second column that wasn't — and the three that are

**Nocke's E0 discriminator needs no column.** E0 is the
repository completion flip (SLICE-D-E §0.6/§2-E4). As Draht's
E4 interface files it (`05-draht.md` §2, §4.1): the edge
detector observes `activeSession`, then discriminates by
reading the row **by id** —

- `isActive == false ∧ completedSuccessfully == true` → a
  finish: Release Proof runs;
- no row by that id → annulment-by-deletion (pre-t0, D8):
  **not** a finish — no RELEASING, no receipt, no fault
  (Draht T5);
- `completedSuccessfully == false` → a disarm (grace cancel,
  emergency break, valve): not a finish.

Every input to that discrimination is already persisted:
`completedSuccessfully` is a shipped column on
`FocusSessionEntity` (`MonolithModels.kt:249`ff.), written by
`updateSessionProgress`'s completion branch
(`MonolithRepository.kt`, `completedSuccessfully =
allBlocksCompleted`) and by `disarmActiveSession` /
`disarmRoutineSessionByBreak`. The one new read it needs is a
DAO query, not a column: `getSessionById(id)` (+5, in Draht's
price) — necessary because `getActiveSessionSnapshot`
(`MonolithDao.kt:60–61`) filters `isActive = 1`, so the flipped
row reads NULL through every existing accessor. **No
outcome/booking marker is added for E0.** (Routine sessions'
outcome marker already exists separately: `routineOutcome`,
arriving on this same row in 8→9, Rurik §1a #6 — E4 does not
read it and this migration does not touch it.) Coordinated by
letter to Nocke (`mail/nocke/0955`), with the standing offer:
any further marker he names before the freeze joins this
migration at **+2 lines** (1 DDL + 1 entity field); after
9→10 ships it costs a 10→11 of its own.

**What does ride, as columns 2–4, is Draht's fault
persistence** (`05-draht.md` §5.2, his fork F-DRAHT-2 —
resolved here as RIDE, his recommended branch): the Release
Proof fault is a fact about *this session's release*; the
retry alarms (fault+30s, fault+5min, Deck-open thereafter)
must survive the process death that faults often accompany,
so the fault state cannot live in VM memory. Three columns,
§1.1/§1.3 verbatim: `releaseFaultShield` (Boolean — only one
link can fault, R2 SHIELD-STOPPED; a set-valued column would
pretend otherwise), `releaseRetriesDone` (0/1/2 timed alarms
spent), `releaseFaultedAtWallMillis` (wall clock, for the
record and boot re-arm arithmetic only — all timing decisions
stay on alarms + monotonic reads). Rejected homes, Draht's
reasoning adopted: SharedPreferences (no state prefs exist in
trunk; contradicts the Room-only pattern) and a dedicated
table (the `emergency_break_spends` precedent is an append-
only *ledger*; a fault is mutable state on an existing row).
**Price delta for folding them in: +6 lines in this spec**
(3 DDL + 3 entity fields) versus ≈+8 DDL-side lines plus a
second migration ceremony if they stood alone; Draht's DAO
accessors and writers remain in his E4 price (≈22, his §6) —
counted once, there, not here.

### 1.6 Write sites that must set `heldAppsAtSeal` (the seal capture — Fader's joint)

**Exactly one capture site exists, and it is sufficient —
because both session-birth paths funnel through it.**

| # | Site | Change |
|---|---|---|
| W1 | `data/MonolithRepository.kt:950` `startSession(...)` — entity construction at `:974–996`, insert `dao.insertSession(session)` at `:997` | Compute the snapshot **inside** `startSession`, before construction: `val heldAppsAtSeal = dao.getAllAppsSnapshot().count { it.resolvedTier == AppBlockTier.LOCKED \|\| it.resolvedTier == AppBlockTier.PEEK_2M }`, and pass `heldAppsAtSeal = heldAppsAtSeal` in the `:974` constructor. The count source is the **DAO snapshot** (`MonolithDao.kt:17`, `getAllAppsSnapshot()`), never the UI Flow — the sealed line prints the proved set, not the displayed set (Emil §3.2). Tier set is F18 as ruled: LOCKED + PEEK_2M (`resolvedTier != OPEN`, Uma). |

That one site covers **every** seal, by funnel:

- **Manual arm:** `ui/MonolithViewModel.kt:913–918`
  (`armMonolithSession` → `repository.startSession(...)`).
- **Routine arm:** `service/RoutineAlarmReceiver.kt:45–50`
  (`BOUNDARY_START` → `repository.startSession(...)`, after
  `applySmartLoadout` at `:44` — the loadout is applied
  *before* the call, so the snapshot counts the routine's own
  staged tiers, which is the count that seal actually holds).

**Non-sites, named so nobody adds a second capture:**

- The three seed inserts in `ensureSeededAndSyncPackages`
  (`MonolithRepository.kt:278`, `:297`, `:316`) do **not** set
  the field — they take the entity default `-1`, which is the
  truth for fabricated demo rows (their deletion is already
  charged elsewhere, Blok S1-2 / B-30 family): no held count
  was ever captured for them, and `-1` says so.
- **No update site ever writes the column.** Every later write
  to the row — `updateSessionProgress` (`:1007`ff., incl. the
  completion flip E0 reads), `disarmActiveSession`,
  `disarmRoutineSessionByBreak`, the cascade's t=0 re-anchor +
  re-sign (D8) — goes through `session.copy(...)` on a row read
  from the DAO, which carries the field untouched by
  construction. **Invariant I-1:** any future write that
  constructs a fresh `FocusSessionEntity` for an existing
  session instead of `copy()`-ing the stored row silently
  resets the count to `-1` — forbidden; the acceptance test
  T-C below pins it.
- **Invariant I-2 (the fault columns' mirror):** Draht's heal
  write ("clears the three columns") must go through his DAO
  `@Query` update, never a `copy()`-update from a stale read —
  a stale copy written after W1's insert is impossible (insert
  precedes any fault), but a stale copy written across a
  *re-anchor* could clobber fault state; the targeted update
  cannot. (Letter to Draht, `mail/draht/0955`.)

**No DAO change is forced by column 1** — `@Insert`
(`MonolithDao.kt:70`) carries the whole entity. Draht's
`getSessionById` + fault `@Query` update are his §6 lines.

### 1.7 Price (net lines, this spec only)

| Piece | File | Net |
|---|---|---|
| `MIGRATION_9_10` block (4 execSQL + object shell + owner comments) | `data/MonolithDatabase.kt` | +13 |
| Registration: `version = 10` (1 word over Voss's 9) + `, MIGRATION_9_10` in `.addMigrations(...)` | `data/MonolithDatabase.kt` | +1 |
| Entity: 4 fields + 2 comment lines | `data/MonolithModels.kt` | +6 |
| Seal capture: snapshot count (2 lines) + constructor arg (1) | `data/MonolithRepository.kt` | +3 |
| **Production total** | 3 files | **≈ +23 net** |
| Migration + chain tests (§1.8) | `app/src/test/…/HeldAppsMigrationTest.kt` (new) + chain extension | ≈ +95 |
| **Total** | | **≈ +118** |

Cross-checks against double-counting: Emil's filed +9
(entity field + `startSession` snapshot, his §5) is inside
this price (my +6 entity line carries 4 fields, not 1 — the
+3 delta over Emil is Draht's three fields, whose *migration*
share Draht himself priced inside his ≈22; to keep the merged
roll-up honest, count the 3 DDL + 3 entity lines **here** and
strike 6 from Draht's 22 when Ilya merges, or vice versa —
never both). If F15 had been refused and Draht stood alone,
his migration would cost the same object shell (+≈8 by his
own count) — riding saves one ceremony, not the columns.

### 1.8 Migration test shape (pattern: the token build's test)

Pattern file: `app/src/test/java/com/example/TokenMigrationTest.kt`
(Robolectric, `@Config(sdk = [36])`, `FrameworkSQLiteOpenHelperFactory`,
a hand-built old-version database opened under the new version
running the registered migration object directly, then raw-SQL
assertions; plus a Room round-trip test). New file
`HeldAppsMigrationTest.kt`, same shape, four tests:

- **T-A — v9-shaped DB opens under v10, legacy row truthful.**
  Fixture: a `focus_sessions` table at the **v9** shape (v8
  columns + Rurik's 8→9 session columns) holding two rows —
  one `isActive = 1` sealed pre-migration (the §1.2 case), one
  completed — with non-trivial values in every v9 column.
  `onUpgrade` asserts `oldVersion == 9, newVersion == 10` and
  runs `MonolithDatabase.MIGRATION_9_10.migrate(db)`. Assert:
  both rows present, every v9 field field-for-field identical;
  `heldAppsAtSeal == -1` on **both** rows (no backfill);
  `releaseFaultShield == 0`, `releaseRetriesDone == 0`,
  `releaseFaultedAtWallMillis == 0`; a raw
  `INSERT` naming all four new columns round-trips.
- **T-B — Full chain v7 → v10.** Token test's fixture extended:
  v7-shaped DB (no spends table) opened with
  `.addMigrations(MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)`.
  Assert spends table exists and is empty (7→8 ran), 8→9
  defaults per Rurik §5 (sessions `routineId IS NULL`,
  `windowStartMillis == 0`, …), and T-A's four defaults —
  proving the legs compose in registration order and none
  re-runs.
- **T-C — Room round-trip + capture survival (Invariant I-1).**
  Room v10 DB via the production builder shape: insert a
  session through `dao.insertSession` with
  `heldAppsAtSeal = 7`; read back `7`; then
  `dao.updateSession(row.copy(isActive = false,
  completedSuccessfully = true))` (the E0 flip's shape) and
  assert the re-read row still carries `7` — and, with fault
  columns set, that a `copy()` progress update preserves them
  too.
- **T-D — Capture arithmetic (repository level, Robolectric).**
  Seed apps at LOCKED ×3, PEEK_2M ×2, OPEN ×4; call
  `startSession(...)`; assert the returned/stored row's
  `heldAppsAtSeal == 5`. Repeat with zero apps → `0` (a real
  seal of nothing — distinct from `-1`, asserted as such).
  Routine path: same assertion through the receiver's call
  shape is *not* re-tested here — W1's funnel position makes
  it the same code path (T-D + the funnel citation is the
  proof; a receiver-level test belongs to the routines lane).

Acceptance for the migration itself: T-A…T-D green; a v9 phone
DB upgraded in place loses zero rows; grep receipt —
`MIGRATION_9_10` appears exactly twice in `app/src/main`
(definition + registration), `fallbackToDestructiveMigration`
appears zero times in the tree.

### 1.9 Named risks (this joint)

- **R-S1 — Two 9→10s.** REGISTER's Data arbitration
  (`plan/REGISTER.md`, Data) books 9→10 as the *plumbing
  batch* (Rurik's step-rows + completion-events tables, Otis's
  readiness log); F15 + Draht's columns ride the same number
  by Ren's ruling. If the plumbing tables ship in a
  `MIGRATION_9_10` of their own *first*, this block must be
  re-based as 10→11 (Emil §7's fallback logic: a migration of
  its own — never the in-memory fallback) and §1.4/§3
  re-pinned. One `MIGRATION_9_10` object must carry the whole
  batch, or the batch must be sequenced before either half
  builds. Flagged for Ilya/Ren at merge, not a fork here:
  F15's ruling already fixes the ride.
- **R-S2 — Voss's lane slips or renames.** §1.4 composes on
  `MIGRATION_8_9` as Rurik filed it. A rename is a one-word
  fix at registration; a *slip* (E ready, 8→9 not landed)
  blocks this migration absolutely — 9→10 cannot register
  against a trunk still at version 8 without stranding v9
  phones that never existed. The chain ships in train order
  (§2) or not at all.
- **R-S3 — Capture-vs-proof ordering.** W1 captures at insert;
  the §F2 cascade proves links *after* insert and re-anchors
  at t=0. If a seal is annulled pre-t0 (row deleted, D8), the
  captured count dies with the row — correct. But if the
  cascade's proof ever *changes* the held set (it cannot
  today: apps are intercepted on attempt, never shut in
  advance — D8's per-app-link kill), the frozen count would
  print the insert-time set. Named so a future cascade change
  re-opens this joint instead of silently falsifying the
  imprint.
- **R-S4 — `-1` reaching a surface that doesn't know it.**
  Every reader of the column must treat `-1` as "never
  captured" (Zettel's rendering law, §1.2.4). A reader that
  prints it raw prints `-1 APPS HELD` — the acceptance render
  check (arm on a migrated legacy DB, Deck shows the SEALED
  tenant with no `-1` anywhere) is owed by the D9/Zettel
  build, and is stated here so it is not lost between joints.

---

## 2 — THE DEPENDENCY CHAIN, STATED PLAINLY (for the merged file)

What Slice E consumes, car by car, in merge-train order
(PIPELINE Law 5: token → counterfoil → routines → drawer +
digest → slide → chrome/PARK → Slice E):

1. **Live Wire substrate — LANDED (consumed, not waited on).**
   The service registries, residency root-stamp, three-valued
   usage split, `liveWarrant()`, Otis's heartbeat StateFlow
   (SLICE-D-E §5 step 1). E consumes: the `WireSnapshot` the
   reverse cascade waits on (Draht: +1 field,
   `shieldInstanceAlive`, inside E4's price) and the stamp
   ordering at `MonolithAccessibilityService.kt:41`. No E
   joint builds any of it; a slip here would have blocked D
   first.
2. **Slice D steps 2+3 — slide/strip (Marlowe, IN FLIGHT).**
   Step 2 (the slide view rewrite, D1–D5/D10) and step 3
   (strip + sentences + imprint, D6/D7/D9 on Kato's 821 sheet).
   E consumes: (a) the slide's final geometry — E3's ghost
   subjects are the wheel and sled *as the slide build leaves
   them*; every slide-region site in E's parts is flagged
   RE-PIN AFTER SLIDE MERGE (Zug-II, Horiz B1/B2); (b) D9's
   `ImprintSlot`, the **only reader** of §1's column — the
   migration (§1) has no build dependency on step 3 and can
   land before it, but `heldAppsAtSeal` is dead storage until
   the imprint builds; (c) Siegel's E1 call-site pin
   (`MonolithViewModel.kt:909–921` today) must be re-pinned
   against whatever function executes PROVING→SEALED-RUNNING
   after the slide/cascade builds move it (Siegel risk 7).
3. **D8 cascade §F2 / PROVING — NOT YET BUILT (E hangs off
   it).** §F2 ships unconditionally (≈465 + ~130t, Opal).
   E consumes, by name: **t=0** itself
   (`max(last link CONFIRMED-SHUT, latch + 210ms, pawl-seated
   frame)`, single `elapsedRealtime` clock) — **E1's call
   site** hangs off the PROVING→SEALED-RUNNING transition the
   cascade lands, **E3's `t0Mono`** is sampled from it, D9's
   Strike triggers on it; the **PROVING controller**
   (`ArmProvingController`) is the file E4's reverse cascade
   and E0 edge detector live in (Draht §6); the annulment verb
   (row deletion at L+1500, pre-t0, F7 as ruled) is the
   deletion E0's discriminator must not mistake for a finish
   (§1.5). Nothing in E can build before this car; §1's
   migration is the slice's only piece exempt from that.
4. **Routines 8→9 (Voss, IN FLIGHT) — consumed ONLY as
   follows, and the filed dependency that DIES:**
   - **(a) Chain predecessor (hard).** `MIGRATION_9_10`
     presupposes `MIGRATION_8_9` registered and `version = 9`
     landed (§1.4, R-S2). This is a schema-chain dependency,
     not a behaviour dependency.
   - **(b) The published interface only.** Any routine fact E
     ever touches reaches it through the routines rebuild's
     published surface as contracted to Reihe — the pure
     `RoutineDerive.derive(...)` + the single
     `RoutineHeartbeat.tick(source)` funnel
     (`plan/ROUTINES-REBUILD-READY.md` §1) — and through the
     shared `startSession` funnel (§1.6: routine seals get
     their Throw-convergence, Siegel F-S1, *inside* the
     routines rebuild per his recommendation, and their
     `heldAppsAtSeal` for free through W1). E writes **no**
     query against the 8→9 columns (`routineId`,
     `windowStartMillis`, `routineOutcome`, …) of its own.
   - **(c) THE DEPENDENCY THAT DIES, stated explicitly:**
     SLICE-D-E §5 item 5 as filed reads *"E1+E2 (engine;
     E2's fact rule needs the routines rebuild, Migration
     8→9 — schedule with or after it)."* **That dependency is
     DEAD under B-5** (Ren's ruling, Oct 29 08:58; MASTER-PLAN
     §9 B-5 ruled in the same letter). The Laid Seal is dead,
     so its fact predicate — the primed snapshot at DOWN, the
     latch-time evaluation, the DAO `EXISTS` query over the
     8→9 `routineId`/`windowStartMillis` columns, UNKNOWN →
     Throw, manual-always-Throw (SLICE-D-E §2-E2, struck text)
     — is dead with it: no selection machinery, no primed
     snapshot, no EXISTS query, no per-provenance rendering.
     The one seal voice fires bit-identically at every seal
     (Siegel, `01-siegel.md`). §5 item 5 must be reprinted in
     the merged Slice E file **without** the E2 clause; what
     survives of item 5's routines reference is (a) and (b)
     above, nothing more. (F14, the dead variant's wet send,
     dies with the same subject — SLICE-D-E §6, struck.)
5. **This slice's own 9→10 (§1) — lands with E, gates only
   the imprint's sealed tenant and E4's retry survival.**
   Nothing else in E reads the new columns: E1/E2 (engine),
   E3 (shot), E5 (light) are schema-free; E4 consumes
   columns 2–4; E6 consumes Fen's VM-owned `ReceiptState`,
   not the row. The migration may land any time after car 4;
   it must land **before or with** the D9 imprint build and
   the E4 build, or both read a column that does not exist.
6. **Chrome — E touches it at exactly ONE carrier; the chain
   names it because it is real (Horiz's joint).** The chrome
   session dot, `ui/components/MonolithCustomComponents.kt:227`
   today (`.background(if (isSessionActive)
   scheme.primaryContainer else scheme.outline)` in
   `MonolithPullDownHeader`), is one of the three carriers the
   seal *births* inside E3's shot (Mara E5b carrier B5;
   Horiz `10-horiz.md`, re-grepped at `:227` in today's
   trunk): E5b re-tones it **at birth to ink**
   (`scheme.onBackground` — day `#2C2A29` / night `#D2C6BB`).
   That is an E-slice edit inside a chrome-owned file, priced
   in E5b (≈61 net incl. the F23 scheme pin, Horiz), **not**
   in the chrome build — and the chrome build (merge-train
   car before Slice E) must be told, in its own sheet, that
   `:227`'s accent state is spoken for: if the chrome build
   re-tones or moves the header first, E5b's B5 line re-pins
   (same flag class as the slide re-pins in car 2). E consumes
   nothing else from chrome; chrome consumes nothing from E.

**One-line form for the merged file:** *E = Live Wire (landed)
→ slide/strip (in flight; re-pin E's sites after it) → §F2
PROVING/t0 (unbuilt; E1/E3/E4 hang off it) → routines 8→9
(chain predecessor + published interface only; the E2 fact-
rule dependency is dead under B-5) → 9→10 (this file) →
chrome at one dot (E5b, Horiz).*

---

## 3 — GATED VARIANTS AT THIS FREEZE: GATE STATUS, EVIDENCE ONLY

*No recommendation is made in this section. The freeze-point
question goes to Ren as a fork from Ilya.*

### 3.1 The rack (§F7 gate) — **NO EVIDENCE THE GATE HAS RUN**

- The gate is a builder's checklist, filed in
  `plan/slice-de-parts/opal.md` (D8): ABAB 20+20 arms, ≥40%
  median proving-time drop vs the strip-only build, removal
  trial with split verdict = fail, forced-jam/log-audit
  automatic fails. It is a *device* gate: it can only run
  against a built §F2 + rack on a phone.
- Ren's rulings on file adopt the gate, they do not record it
  running: F7 annulment adopted, F8 standing shield held
  closed, F9 "release rack under the same §F7 gate = yes"
  (Marlowe's letter, `mail/ren/0826`; rulings book,
  `plan/RULINGS.md`, Oct 29 09:05 — all 24 forks ruled en
  bloc). A ruling that a gate governs is not a gate result.
- Searched this run: the whole `plan/` dir, `COMMONS.md`
  (wall, tail and full grep for `§F7` / ABAB / gate-result
  phrasings), and `mail/ren/` entire. **Every hit is the
  gate's definition, its price (≈370 gross, SLICE-D-E §4), or
  its forks — no run report, no ABAB numbers, no pass/fail,
  no device session is filed anywhere.** §F2 itself — the
  substrate the gate measures — is not yet built (§2, car 3),
  so on the filed evidence the gate *cannot* have run.
- **Status at this freeze: gate NOT RUN. The rack stands
  unbuilt under its own law (SLICE-D-E §5 item 6: "build only
  if their gates are ruled/run").**

### 3.2 The Tear-Seal variant (F5 / Cap-Crop Gate) — **NO EVIDENCE THE GATE HAS RUN**

- The variant: Nils's Tear-Seal slide form, +≈680 gross /
  2.5–3 days, behind the **Cap-Crop Gate** (`plan/slice-de-parts/nils.md`
  §5: cap-cropped progress read ±10% kills it untuned; cruise
  stillness; eyes-closed crack-vs-seal; mesh legibility).
- The ruling on file (F5, Ren, Oct 29 09:05;
  `plan/RULINGS.md:115`; Marlowe `mail/ren/0826` item 3):
  *"the merged slide form ships; Tear-Seal is a priced variant
  behind the Cap-Crop Gate, **deleted unbuilt if the gate
  hasn't run by the Slice E freeze**."*
- Searched this run, same sweep as §3.1 (`plan/`, wall,
  `mail/ren/`): every hit is the gate's definition, the
  variant's price, or the fork. **No gate session, no
  cap-crop measurement, no pass/fail is filed anywhere.** One
  aggravating record: the plan audit (`plan/plan-audit-parts/orig.md`,
  F5 charged S3) found the ruling as transcribed had swallowed
  a condition — Fen's §2.9 verdict on the tear's Sled-Goes-Home
  replacement ending, which Nils's own filing makes part of
  the gate's business — so even the gate's *complete
  checklist* is not cleanly on file, let alone its result.
- **Status at this freeze: gate NOT RUN. Under F5's own text,
  the variant's "deleted unbuilt" clause is the live one at
  the freeze — whether the freeze is *now* is Ren's call, via
  Ilya's fork, not this file's.**

---

## 4 — B-15's CLOSURE NOTE (what Ren can strike)

MASTER-PLAN §9, charge **B-15 · Token + migration build
debt** (owner: the build; status OPEN IN PART): *"the 7→8
leg LANDED Oct 29: Migration 7→8 shipped,
`fallbackToDestructiveMigration(true)` deleted in trunk, the
token ledger-derived (audit PA-S1-05) · closes fully when:
Migrations 8→9 / 9→10 land as specced (Register, Data) —
§7's permanence audit (§7.9-3) cannot pass before this
closes."*

**When the §1 build lands (with Voss's 8→9 ahead of it in the
same train), Ren can strike B-15 in full** — reworded for his
letter, one line:

> **B-15 CLOSED** — Migrations 8→9 (routines, Rurik/Voss) and
> 9→10 (F15 `heldAppsAtSeal` + E4 fault columns, Saum §1) both
> landed as specced; the chain 7→8→9→10 is registered with no
> destructive fallback anywhere in it; §7.9-3's permanence
> audit is unblocked on the migration leg.

Two precision guards on that strike, so it is not over-struck:

1. The strike is earned by the **landings**, not by this spec:
   B-15 closes when both migrations are *in trunk with their
   chain tests green* (Rurik §5 for 8→9; §1.8 here for 9→10),
   per its own closing condition.
2. REGISTER's Data line books the 9→10 *plumbing batch*
   (Rurik's step-rows + completion-events tables, Otis's
   readiness log) under the same migration number (R-S1). If
   those tables land inside the same `MIGRATION_9_10`, the
   strike above is whole. If they are sequenced to a later
   migration, B-15's "(Register, Data)" leg remains open
   **for that remainder only** and the strike must say so —
   the F15/E4 columns alone do not discharge a batch they
   only rode.

No other charge closes with this build. (B-31, date-notes
storage, rides 8→9 and is Voss's strike to claim; B-8's
HELD-capture device test is a different debt — §1's column is
its *prerequisite*, not its closure.)

---

## NEXT

(a) *What I want next:* the merge-train watch for this seam —
when Voss's 8→9 lands, re-pin §1.4 against the registered name
actually in trunk and file the one-line confirmation to Ilya;
and the same re-pin pass over §1.6/§2 if the slide build moves
`startSession` or its callers first.
(b) *What I could do unassigned:* audit the *other* booked
9→10 riders (Rurik's step-rows/completion-events, Otis's
readiness log) against §1's single-object requirement (R-S1)
and file the consolidated 9→10 batch sheet, so the migration
that ships is one object with every rider's DDL verbatim.

— Saum, Improvers · the seam (seated by Ilya)
