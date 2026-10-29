# LIE-HUNT — MERGED TRUNK — BELEG (Critics seat 23, receipts)

Seated by Hale. Ground: the merged trunk ONLY at
`~/workspace/clone-inspect/Focus-app/app/src/main/java/com/example/`
(lane reports not used as evidence; tree-wide greps over `app/src/main`
are quoted where a claim is about absence). Inbox `mail/beleg/` checked
at seating: empty (only an empty `read/`). No code changed — charges only.

Verdict in one line: the token core — one transaction, append-only
ledger, derived count, registered migration, no destructive fallback —
is TRUE in the shipped code. What survives of the old counter world is
not a counter; it is one hardcoded initial value, and it renders.

---

## CHARGES

### B-1 (S2) — A hardcoded `1` still reaches the valve: the derived flow is born claiming a full token

`ui/MonolithViewModel.kt:150-152`:

```kotlin
val weeklyEmergencyTokensRemaining: StateFlow<Int> =
    repository.observeEmergencyBreaksRemaining()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
```

The third `stateIn` argument is the initial value. Until the repository
flow's first emission lands (week-anchor tick + Room query), every
collector reads **1** — a full, unspent token — regardless of the
ledger. The wiring carries that value straight to both token surfaces:
`MainActivity.kt:195` collects it, `MainActivity.kt:564` passes it as
`tokensRemaining` to the overlay. On a week whose break is already
spent, a cold process that opens the disarm challenge inside that
window renders the live offer — `AirlockAndDisarmOverlays.kt:627-628`
("This ends the session early. It is the only break this week.") and
the composed valve at `:637` — over a ledger that says SPENT.

Scope, stated honestly: this is a rendering lie, not an enforcement
breach. The repository re-checks inside the transaction (see A-1/A-2)
and refuses; HOLD survives. But TELL-THE-TRUTH fails for the window,
and the window is exactly the cold-open moment this studio has charged
before. The old world was a bare `MutableStateFlow(1)`; that is dead
(A-5). This is its residue: the same numeral, demoted to an initial
value, still the first thing the UI believes.

### B-2 (S3) — `completeEmergencyBreak()` transacts on a re-fetched singleton, not on the database its DAO belongs to

`data/MonolithRepository.kt:1224-1226`:

```kotlin
suspend fun completeEmergencyBreak(): BreakResult {
    val database = MonolithDatabase.getInstance(context)
    return database.withTransaction {
```

The repository is constructed with a DAO (`MonolithRepository.kt:75-78`)
and never derives its transaction from that DAO's database; it
re-fetches the process singleton. Atomicity therefore holds by wiring
convention, not by construction. The convention currently holds
everywhere in production — `MainActivity.kt:84-85`:

```kotlin
val database = MonolithDatabase.getInstance(applicationContext)
val repository = MonolithRepository(applicationContext, database.monolithDao())
```

— and the services build their DAOs from the same singleton. But any
future (or test-harness) repository built over a different database
instance would run its check/insert/disarm writes outside this
transaction silently: the `withTransaction` would still open, commit,
and prove nothing. The function's own KDoc (`:1211-1223`) claims
"Atomicity is the point" without naming the assumption that makes it so.

### B-3 (S3) — The one-per-week invariant has no schema teeth: no UNIQUE constraint backs the in-transaction check

Entity, `data/MonolithModels.kt:325-332`:

```kotlin
@Entity(tableName = "emergency_break_spends")
data class EmergencyBreakSpendEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Int,
    val spentAtWallMillis: Long,
    val weekAnchorMillis: Long,
    val heldSeconds: Int
)
```

No `indices`, no unique constraint — and the migration DDL matches it
(`MonolithDatabase.kt:44-49`, see A-4): nothing at the schema level
prevents two rows against one `weekAnchorMillis`. The invariant lives
solely in the check at `MonolithRepository.kt:1231`
(`if (dao.getSpendsForWeek(anchor).size >= 1)`). Today that is enough —
there is exactly one writer and it passes through that check inside a
write transaction (A-1, A-3). This is charged as a hardening gap, not a
live lie: the first future direct-DAO writer bypasses the law and the
database will not object. (The derivation already defends the read
side: `(1 - spendCount).coerceAtLeast(0)`, `MonolithWeek.kt:41-42`.)

### B-4 (S3, adjacent surface — outside my named ground, found by the tree-wide token grep; flagged for Hale) — The Deck carries a second hardcoded `1` and the app's one numeric token count

`ui/screens/TactileDeckScreen.kt:115`:

```kotlin
weeklyEmergencyTokensRemaining: Int = 1,
```

and `:522`:

```kotlin
monoBadge = "$weeklyEmergencyTokensRemaining/1 WEEK",
```

The default is dormant — `MainActivity.kt:344` always passes the
collected derived value — so in production wiring the badge renders
the ledger's truth. Two residues stand: (1) the default is the same
hardcoded full-token assumption as B-1, waiting for the first caller
who omits the argument; (2) the badge is the one place in the app
where the token renders as a numeric `$n/1` count at all — the overlay
(my ground) renders prose and a boolean gate only (A-5). Whether a
standing Deck count is lawful is the register's question (Una's U-file
charges §5.3.9's chip against §5.3.8's one-rendering law in the plan);
in the CODE it exists, wired, and truthful only because B-1's flow is.

### B-5 (S3) — An overlay left open across Monday 00:00 can render last week's count for up to ~60 seconds

`data/MonolithRepository.kt:1188-1198`:

```kotlin
private fun weekAnchorTickFlow(): Flow<Long> = flow {
    var lastAnchor: Long? = null
    while (true) {
        val anchor = MonolithWeek.weekAnchorMillis(System.currentTimeMillis())
        if (anchor != lastAnchor) {
            lastAnchor = anchor
            emit(anchor)
        }
        delay(60_000L)
    }
}
```

The re-anchor is a 60-second poll, so the live count
(`:1202-1209`, `flatMapLatest` over this tick) crosses the boundary up
to one tick late. The function's own KDoc (`:1181-1187`) states the
requirement as "an overlay left open across Monday 00:00 must never
render last week's count" — the code renders it for ≤60 s past the
turn. Enforcement is never stale (the spend path recomputes `now`
fresh inside the transaction, `:1229-1230`); this is rendering lag
only, charged because the KDoc claims the stronger property.

---

## ACQUITTALS

### A-1 (Q1) — Exactly ONE production insert call site, inside ONE `withTransaction`, ordered check → insert → disarm. ACQUITTED.

Tree-wide grep over `app/src/main` for `insertEmergencyBreakSpend`
returns exactly two lines: the DAO declaration
(`data/MonolithDao.kt:83-84`, bare `@Insert`, no REPLACE) and one call.
`grep withTransaction` over the repository returns the import
(`:21`) and one use. The body, `data/MonolithRepository.kt:1224-1248`:

```kotlin
suspend fun completeEmergencyBreak(): BreakResult {
    val database = MonolithDatabase.getInstance(context)
    return database.withTransaction {
        val active = dao.getActiveSessionSnapshot()
            ?: return@withTransaction BreakResult.NoActiveSession
        val now = System.currentTimeMillis()
        val anchor = MonolithWeek.weekAnchorMillis(now)
        if (dao.getSpendsForWeek(anchor).size >= 1) {
            return@withTransaction BreakResult.RefusedNoToken
        }
        dao.insertEmergencyBreakSpend(
            EmergencyBreakSpendEntity(
                sessionId = active.id,
                spentAtWallMillis = now,
                weekAnchorMillis = anchor,
                heldSeconds = EMERGENCY_BREAK_HELD_SECONDS
            )
        )
        if (active.profileName.startsWith(ROUTINE_PROFILE_PREFIX)) {
            disarmRoutineSessionByBreak(active)
        } else {
            disarmActiveSession(completed = false, outcomeReason = EMERGENCY_BREAK_REASON)
        }
        BreakResult.Completed
    }
}
```

Order is literal: session check (`:1227`) → token check (`:1231`) →
insert (`:1234`) → disarm dispatch (`:1242-1246`) → `Completed`
(`:1247`). The check is `>= 1`, so a corrupt second row refuses rather
than compounding. Two remarks, neither a charge: (a) the non-routine
disarm re-reads the active snapshot inside `disarmActiveSession`
(`:1275`) instead of consuming `active` — inside the same write
transaction the re-read returns the same row, so the ordering property
is unaffected; (b) the transaction's database is the singleton —
that coupling is B-2.

### A-2 (Q2) — `RefusedNoToken` writes NOTHING and leaves the session sealed. ACQUITTED.

Repository side: the refusal returns at `MonolithRepository.kt:1231-1233`,
before the insert (`:1234`) and before either disarm branch
(`:1242-1246`). Every statement executed before it is a read —
`getActiveSessionSnapshot()` (`:1227`), `getSpendsForWeek(anchor)`
(`:1231`). No `updateSession`, no intercept log, no spend row: the
transaction commits empty.

ViewModel side, `ui/MonolithViewModel.kt:985-988`:

```kotlin
BreakResult.RefusedNoToken, BreakResult.NoActiveSession -> {
    // Session stays sealed; the overlay remains open and
    // re-renders from the repository flow. Nothing sounds.
}
```

The branch is empty. Contrast the `Completed` branch (`:978-984`),
which alone plays the seal, closes the challenge
(`_disarmChallenge.value = … isVisible = false`), and calls
`MonolithShieldService.stopShield(appContext)`. On refusal none of
those fire: the session row stays `isActive`, the shield service keeps
running, the overlay stays open and — because its count is the
repository flow, not a snapshot — re-renders in place to the SPENT
face (A-5). The sealed state is preserved at every layer the refusal
touches.

### A-3 (Q3) — The spend is booked against the anchor computed at spend time; remaining is derived from rows; no path mutates or deletes a spend row. ACQUITTED.

Spend-time anchor: `now` is taken inside the transaction at
`MonolithRepository.kt:1229`, the anchor is computed from that same
instant at `:1230`, and the row stores both verbatim —
`spentAtWallMillis = now` (`:1237`), `weekAnchorMillis = anchor`
(`:1238`). The stored anchor is never recomputed anywhere (the only
other anchor computations are for reads: `:1177`, `:1191`).

Derived, never stored: `emergencyBreaksRemaining`
(`:1176-1178`) and the live flow (`:1202-1209`) both count rows for
the current anchor and pass the count through the single formula,
`data/MonolithWeek.kt:41-42`:

```kotlin
internal fun emergencyBreaksRemainingFromSpendCount(spendCount: Int): Int =
    (1 - spendCount).coerceAtLeast(0)
```

A grep for token-named state over `data/` finds no counter field in
any entity — the hits are the `BreakResult` type, comments, and this
derivation. The `1` in the formula is the weekly allowance itself
(the law), not a stored balance.

Append-only, verified by enumeration: the DAO's whole spend surface
is `MonolithDao.kt:81-93` — one bare `@Insert` (`:83-84`) and three
SELECTs (`:86-87`, `:89-90`, `:92-93`). No `@Update`, no `@Delete`,
no `UPDATE`/`DELETE` query in the file names `emergency_break_spends`
(the file's `@Update`s at `:25, :53, :72, :131, :176` and DELETEs at
`:75, :109, :112, :134, :137, :153` all target other tables).
Repository spend calls are exactly: the one insert (A-1),
`getSpendsForWeek`, `observeSpendsForWeek`. `getAllSpendsSnapshot`
has zero production callers (grep: DAO declaration + test sources
only) and is read-only regardless.

### A-4 (Q4) — Migration(7,8) DDL matches the entity exactly, the migration is registered, and `fallbackToDestructiveMigration` survives nowhere as code. ACQUITTED.

Version and registration, `data/MonolithDatabase.kt`:

```kotlin
version = 8,                                   // :24
val MIGRATION_7_8 = object : Migration(7, 8) { // :41
    .addMigrations(MIGRATION_7_8)              // :79, inside Room.databaseBuilder(...) :74-80
```

DDL vs entity, column by column. Entity
(`data/MonolithModels.kt:326-332`) → DDL (`MonolithDatabase.kt:44-49`):

| column | entity | Room type | DDL |
|---|---|---|---|
| `id` | `@PrimaryKey(autoGenerate = true) val id: Int = 0` | INTEGER PK AUTOINCREMENT NOT NULL | `` `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL `` |
| `sessionId` | `val sessionId: Int` | INTEGER NOT NULL | `` `sessionId` INTEGER NOT NULL `` |
| `spentAtWallMillis` | `val spentAtWallMillis: Long` | INTEGER NOT NULL | `` `spentAtWallMillis` INTEGER NOT NULL `` |
| `weekAnchorMillis` | `val weekAnchorMillis: Long` | INTEGER NOT NULL | `` `weekAnchorMillis` INTEGER NOT NULL `` |
| `heldSeconds` | `val heldSeconds: Int` | INTEGER NOT NULL | `` `heldSeconds` INTEGER NOT NULL `` |

Same five columns, same order, same types, same nullability; neither
side declares indices or foreign keys (the absence of a UNIQUE index
is B-3, a hardening gap — it is not a DDL/entity mismatch; Room's
expected schema and the created table agree). The DDL's
`IF NOT EXISTS` is the only addition and changes nothing created.

Fallback: a grep for `fallbackToDestructiveMigration` across the whole
of `app/src` returns exactly one hit — the comment at
`MonolithDatabase.kt:59` recording its removal. There is no call to
`fallbackToDestructiveMigration()` or `…OnDowngrade()` anywhere in the
tree. The builder chain at `:74-80` is `addMigrations(MIGRATION_7_8)`
then `.build()` — nothing else.

### A-5 (Q5, in-ground part) — No bare MutableStateFlow token, no count in the challenge state, no number reaches the valve, and at zero the overlay renders SPENT with the valve uncomposed. ACQUITTED.

- The VM's full `MutableStateFlow` list (grep over
  `ui/MonolithViewModel.kt`) contains no token field; the token is the
  derived `StateFlow` at `:150-152` (its initial value is B-1 — the
  flow itself is honestly derived from
  `repository.observeEmergencyBreaksRemaining()`).
- `DisarmChallengeState` (`ui/MonolithViewModel.kt:67-75`) carries
  `isVisible` and `requiredHoldSeconds = 60` only — no count is
  snapshotted into it at open time; `requestDisarmChallenge`
  (`:940-944`) writes exactly those two fields.
- The valve receives no count. In the overlay's `AndroidView`
  (`AirlockAndDisarmOverlays.kt:638-653`) the factory sets
  `isNightMode` and `soundEngine` (`:641-642`); the update block sets
  only `view.requiredSeconds = state.requiredHoldSeconds` (`:646`)
  and the completion callback (`:647`). `requiredSeconds` is the
  60-second hold length — the mechanism's own constant, not the token.
- SPENT at zero: the body text switches at `:627-631`
  (`"This week's break is already spent. Sessions end when their time does."`),
  and the valve block is gated by `if (tokensRemaining > 0)` at
  `:637` — at 0 the valve is not composed at all; only the SPENT text
  and the KEEP FOCUSING chip (`:663-669`) remain. The overlay prints
  no numeric count anywhere: its only numerals are the hold mechanics
  ("60 seconds", "15 seconds", `:656`).
- The remaining hardcoded numerals in the token path are lawful
  constants, not counters: the allowance `1` in the derivation
  formula (A-3), the refusal threshold `>= 1` (`Repository:1231`),
  and `EMERGENCY_BREAK_HELD_SECONDS = 60` (`Repository:106`), stored
  on the row at `:1239`.

### A-6 (Q6) — Sunday 23:59 and Monday 00:01 land in different weeks under Asia/Kolkata; the anchor arithmetic is correct. ACQUITTED.

The definition, `data/MonolithWeek.kt:28-32`:

```kotlin
fun weekAnchorMillis(nowMillis: Long, zone: ZoneId = ZoneId.systemDefault()): Long {
    val date = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
    val monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    return monday.atStartOfDay(zone).toInstant().toEpochMilli()
}
```

Every production caller omits `zone` (Repository `:1177`, `:1191`,
`:1230`), so the device zone governs — Asia/Kolkata on his phone, as
the KDoc's "device's current timezone" (`MonolithWeek.kt:12-13`)
intends. Worked with the actual `java.time` calls
(`previousOrSame(MONDAY)` on the LocalDate, then `atStartOfDay(zone)`;
Kolkata is UTC+05:30 with no DST, so no gap/overlap adjustment fires):

- **Sun 2026-11-01 23:59 IST** (epoch 1793557740000): LocalDate is a
  Sunday → `previousOrSame(MONDAY)` walks back 6 days to **Mon
  2026-10-26** → anchor = 2026-10-26T00:00+05:30 = **1792953000000**.
- **Mon 2026-11-02 00:01 IST** (epoch 1793557860000): LocalDate is a
  Monday → `previousOrSame` returns the same day, **2026-11-02** →
  anchor = 2026-11-02T00:00+05:30 = **1793557800000**.

The anchors differ by exactly 604800000 ms (7 days). Two minutes of
wall time across the boundary therefore book against different
anchors, and because every read is an equality match on the stored
anchor (`MonolithDao.kt:86`, `:89`), Monday's count cannot see
Sunday's row. A hold that *completes* at Monday 00:01 spends against
the new week by construction: `now` and its anchor are computed at
completion, inside the transaction (`Repository:1229-1230`). The one
imperfection at the boundary is rendering-side only and is charged
separately as B-5.

---

**Worst finding, one plain sentence:** On a week whose break is already spent, the app's first rendering after a cold start still offers him the valve — `stateIn(…, 1)` at `MonolithViewModel.kt:152` is born claiming a full token, and only the repository's refusal, not the screen, tells the truth.

— Beleg, Critics seat 23 (receipts)
