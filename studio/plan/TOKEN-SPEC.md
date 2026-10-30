# TOKEN SPEC — the weekly emergency break, closed

*Phase 3, MaxImpl (Voss). Closes Critics merge condition 9 — the wave's one
open spec hole. Buildable without questions: every rule below is decided,
every number is final, and §1 separates what the code does today from what
this spec changes. Sources: shipped code (verified line by line, Oct 29),
MASTER-PLAN §0/§0.1-9, HPA-28 (Humans), Barriere valve spec (the valve
itself is built and shipped — this document is about the TOKEN behind it,
not the hold).*

---

## 0. Decisions, in one screen

1. **The token is not a counter. It is a fact derived from an append-only
   spend ledger.** New Room table `emergency_break_spends`; remaining =
   `1 − spends booked against the current week anchor`, never below 0.
2. **The week turns over Monday 00:00 local civil time** in the device's
   current timezone — the app's existing Monday-first convention
   (RoutineSchedule's day index is already Monday-based). One shared
   definition (`MonolithWeek`), consumed by the token and by the Ledger
   rebuild's weekly line. One fact, one definition.
3. **A spend is written only at valve completion** — the instant held time
   reaches 60.0s — inside the same database transaction as the disarm.
   Partial and banked attempts write **nothing, ever** (§4.3).
4. **Enforcement lives in the repository.** The ViewModel's in-memory
   token flow is deleted as a source of truth. The repository refuses a
   break at zero regardless of what any UI asks; the UI number is a
   rendering, the repository is the law.
5. **DB 7 → 8 with an explicit `Migration(7, 8)`, and
   `fallbackToDestructiveMigration(true)` is removed in the same change.**
   Fail loud, never wipe (§6).

## 1. Current state — what the code does today (verified)

- The token count is
  `MonolithViewModel._weeklyEmergencyTokensRemaining = MutableStateFlow(1)`
  — **in-memory only, never persisted**. Any process death (force-stop,
  reboot, LMK kill) silently refills the week's break. The once-a-week
  cap is currently unenforceable; this is the debt in its exact shape.
- Spend happens in `confirmDisarmSession`: the VM decrements its own
  flow if `tokens > 0`, then disarms **regardless** — at zero the disarm
  still proceeds; only the valve *view* refuses touch at zero
  (`Emergency60sValveView.onTouchEvent`). Enforcement is a rendering.
- `repository.disarmActiveSession(completed, outcomeReason)` books the
  session's verified elapsed seconds; grace-cancel is detected by
  *string-matching* `outcomeReason.contains("Grace Cancel")`. The
  outcome reason is **not** stored on the session row — for early disarms
  it survives only inside an `InterceptLogEntity.outcome`.
- The valve's bank state (held/banked ms) lives only in View fields.
  Nothing is written to disk during an attempt, at a bank, or on a slip.
  The completion callback fires exactly once, at 60.0 held seconds.
  (HPA-28's "nothing recorded before the first bank; token spent only at
  60.0 held seconds" — verified true of the shipped build, and §4.3
  makes it a requirement, not an accident.)
- DB: version **7**, `exportSchema = false`,
  `fallbackToDestructiveMigration(true)`, device-protected storage
  context (the database is readable before first unlock).
- Single-row app-state precedent exists: `GuardSettingsEntity`
  (`@PrimaryKey id = 1`). The token does **not** use that pattern — a
  counter row cannot render history, and the Ledger rebuild needs the
  spend as a record (receipt: break UNSPENT/spent). A ledger can; a
  counter can't. Hence decision 1.

## 2. The spend ledger

New entity, table `emergency_break_spends`, append-only (no update, no
delete path anywhere in the app):

| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `sessionId` | INTEGER NOT NULL | the `FocusSessionEntity.id` this break ended; the join key for the receipt and the Ledger |
| `spentAtWallMillis` | INTEGER NOT NULL | wall-clock instant of completion |
| `weekAnchorMillis` | INTEGER NOT NULL | the Monday-00:00 anchor (§3) of the week this spend counts against — **stored at spend time, never recomputed** |
| `heldSeconds` | INTEGER NOT NULL | always 60 at completion today; stored so the record says what was paid, in the valve's own terms |

No other columns. In particular **no attempt rows, no bank rows, no
counter row**. DAO additions (names final):

```kotlin
@Insert suspend fun insertEmergencyBreakSpend(spend: EmergencyBreakSpendEntity)
@Query("SELECT * FROM emergency_break_spends WHERE weekAnchorMillis = :anchor")
suspend fun getSpendsForWeek(anchor: Long): List<EmergencyBreakSpendEntity>
@Query("SELECT * FROM emergency_break_spends WHERE weekAnchorMillis = :anchor")
fun observeSpendsForWeek(anchor: Long): Flow<List<EmergencyBreakSpendEntity>>
@Query("SELECT * FROM emergency_break_spends ORDER BY spentAtWallMillis DESC")
suspend fun getAllSpendsSnapshot(): List<EmergencyBreakSpendEntity>
```

Derived truth, the only formula in the system:

```
remaining(now) = max(0, 1 − getSpendsForWeek(weekAnchorMillis(now)).size)
```

## 3. The week anchor

**Definition.** `MonolithWeek.weekAnchorMillis(nowMillis: Long, zone:
ZoneId = ZoneId.systemDefault()): Long` — the instant of Monday
00:00:00.000 local time of the week containing `now`, in `zone`:

```
date  = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
anchor = monday.atStartOfDay(zone).toInstant().toEpochMilli()
```

A spend counts against the current week **iff** its stored
`weekAnchorMillis` equals the current anchor computed at evaluation
time. Rollover needs no job, no reset, no alarm: at Monday 00:00 the
equality simply stops matching last week's rows.

**Edge cases, ruled:**

- **Travel / timezone change.** The anchor is always computed in the
  device's *current* zone; a stored spend keeps the anchor instant it
  was booked under. Comparing instants (not local dates) means a zone
  change can shift the effective refill by at most the zone difference
  (bounded ≈26h, only after intercontinental travel, always resolving
  at the next true local Monday). Accepted, deliberately: zone-of-spend
  bookkeeping is a second definition of the week, and one fact gets one
  definition.
- **DST.** `atStartOfDay(zone)` applies java.time's gap/overlap rules
  (a nonexistent midnight shifts forward to the first valid instant).
  Asia/Kolkata has no DST; the rule is stated so the code is correct in
  any zone the phone lands in.
- **Clock changes.** The anchor can only be computed from wall clock —
  elapsed-realtime cannot name a Monday. A user who sets the clock
  forward a week refills early; that attack is out of scope (his phone,
  his discipline tool — the same trust every on-device rule here runs
  on). What clock games *cannot* do is erase history: spends are rows,
  and moving the clock back does not delete them.
- **Scope.** `MonolithWeek` does not replace `RoutineSchedule`;
  routines keep their own day-mask math, and nothing in routines
  consumes the week anchor. Spends attribute by *completion* instant
  (§4.2); routine windows attribute by *start* day — different facts,
  never week-joined. The Monday refill resets no routine streak.
  *(Sten, Charge 3 — stands unamended; this note is his offered guard
  sentence.)*
- **Direct Boot.** The database lives in device-protected storage, so
  `remaining()` is computable before first unlock, in the same contexts
  where the shield service already runs. No new storage location.

## 4. Spend semantics

### 4.1 The only write

At valve completion, and nowhere else, the repository runs **one
transaction**:

```
completeEmergencyBreak(): BreakResult   // @Transaction boundary
  1. active = dao.getActiveSessionSnapshot()
       ?: return NoActiveSession
  2. if getSpendsForWeek(currentAnchor).size >= 1
       return RefusedNoToken            // session stays sealed
  3. insert EmergencyBreakSpendEntity(
       sessionId = active.id,
       spentAtWallMillis = now,
       weekAnchorMillis = currentAnchor,
       heldSeconds = 60)
  4. Disarm, dispatched on the active session's kind:
     - *Routine session* (`profileName == "Routine: <name>"` — the
       shipped recogniser, until id-based matching lands): compute the
       05-sten-routines §5.4 outcome from the attendance row's accrued
       `attendedSeconds` vs the window (or remainder, if late-fired),
       then disarm by that outcome — ATTENDED stands as booked, PARTIAL
       via `disarmRoutineSessionPartial(attendedSeconds)`, MISSED via
       `disarmRoutineSessionUnattended()` — and run `finalizeAttendance`
       for that window, all inside this transaction. SLEEP-loadout
       routines are exempt from attendance (§5.4): no finalise runs,
       and their session disarms by the generic path.
     - *Any other session:* `disarmActiveSession(completed = false,
       outcomeReason = EMERGENCY_BREAK_REASON)` as specced.
  5. return Completed
```

Atomicity is the point: no interleaving where the session ends but the
spend is lost, or the spend lands but the session stays sealed, and no
double-completion race (two rapid callbacks) can spend twice — step 2
runs inside the transaction. `BreakResult` is a sealed type with exactly
the three outcomes above. The step-4 dispatch exists because the generic
disarm books `max(verified wall-clock, elapsed)` plus a Partial Stamp —
Lie #13's booking — which must never run on a routine session, at break
completion any more than at window end (Sten B1). The routines rebuild
changing the *inside* of this dispatch is not a new caller of a
break-class disarm under §5's convention — it is this step's named
owner arriving. No other caller may bypass it.

### 4.2 The boundary race

A hold that *starts* Sunday 23:59 and *completes* Monday 00:01 spends
against **the anchor of the completion instant** (the new week). The
break happens when it completes; that is the week that pays. Ruled, so
no builder improvises the other way.

### 4.3 Attempts write nothing — verified, and now required

HPA-28, checked against the shipped code and adopted as law here:

- Bank crossings (15/30/45) write nothing. Banks are view-local feel,
  not records.
- Slips, drains, abandoned attempts, OS cancels (which the shipped
  valve correctly treats as slips) write nothing.
- The disarm challenge merely *opening* writes nothing (VM state only).
- **No attempts counter may be added** — not for analytics, not for the
  Ledger.
- This silence governs `emergency_break_spends` and token accounting
  only. It does not reach `routine_attendance`: a hold started inside a
  routine window stamps E4 attendance evidence per 05-sten-routines
  §5.3 — presence in a scheduled window, not a valve attempt — and no
  builder may suppress it under this section. Conversely, E4 evidence
  is never an input to `remaining()`. *(Sten B4.)* The record of the valve is binary per week: a spend row, or
  silence. (Do not confuse this with the existing intercept-log entries
  written by `disarmActiveSession` for disarms that *did* happen —
  including the Partial Stamp for a broken session's attended time.
  Those are session records, not attempt records, and they are
  unchanged.)

### 4.4 Grace cancel is not a break

The existing "Accidental Start Grace Cancel" path (free abort of an
accidental arm) is untouched: no spend row, no spend check, existing
zero-booking behaviour preserved. It never passes through
`completeEmergencyBreak`.

### 4.5 The session's record of its own break

The spend row **is** the record — `sessionId` is the join. The rebuilt
Ledger renders the break from this table, never from string-matching
`outcomeReason`: a valve-ended session's row in the tab carries its
break line (`BROKEN AT {elapsed} · HOLD SPENT`, elapsed from the
session's booked attended time) sourced from the spend row. This
satisfies the outstanding cross-file charge (Nox C-07) as re-homed by
the KEEP-THE-TAB verdict (MASTER-PLAN §0.1-6): the partial is stated
once, in the record's home. The finish receipt does not render it —
the receipt's trigger is `completedSuccessfully` only (Freya §4.3),
a valve break is booked `completed = false` (§4.1), and completion
and emergency never share an exit form (buildspec point 6). The
outcome-reason constant for this path is exactly `"Emergency Break"`
(`EMERGENCY_BREAK_REASON`), replacing the free-text reason the overlay
currently passes for valve completions. *(Folded per Freya's Charge-2
ruling, Phase 3: exit form and record are separable — the record
stands in full, the receipt clause of the first draft fell.)*

## 5. Enforcement — repository is the law

- **New repository surface** (names final):
  `emergencyBreaksRemaining(nowMillis): Int`,
  `observeEmergencyBreaksRemaining(): Flow<Int>` (re-anchored on
  collection and on day-change; implementation: `observeSpendsForWeek`
  mapped through the formula, anchor refreshed when the flow is
  collected and whenever the shield/VM re-reads state),
  `completeEmergencyBreak(): BreakResult`.
- **ViewModel:** `_weeklyEmergencyTokensRemaining` (the bare
  `MutableStateFlow(1)`) is **deleted**. The VM exposes the repository
  flow as state. `confirmDisarmSession` loses its token block entirely —
  it never touches the token again. The overlay's completion callback
  routes to a VM method that calls `repository.completeEmergencyBreak()`
  and only on `Completed` runs the existing shield-stop/stats refresh;
  on `RefusedNoToken` the session stays sealed and the overlay
  transitions in place per Amendment B below.
- The valve view still receives a plain `tokensRemaining: Int` and
  refuses touch at 0 — a *rendering* of repository truth, not the
  enforcement itself. Any other present or future caller of a
  break-class disarm must go through `completeEmergencyBreak`;
  `disarmActiveSession` with the `"Emergency Break"` reason from
  anywhere else is a build error by convention, stated here so the
  merge audit can charge it.
- The stopwatch clean-completion path (`completed = true`) and routine
  BOUNDARY_END disarms never interact with the token. A break that ends
  a *routine* session does interact — only through §4.1's dispatch,
  which is the one place token spend and attendance finalisation meet.
  *(Sten B2.)*

**Amendment A — derivation, not snapshot** *(Freya Charge 3)*.
`DisarmChallengeState.weeklyEmergencyTokensRemaining` is never written
at open time. The overlay's state is derived from
`observeEmergencyBreaksRemaining()` for as long as the overlay is
visible (the VM combines the repository flow into the disarm-challenge
state; it does not copy it). A count rendered in an open overlay is
the repository's current truth or it is not rendered at all.

**Amendment B — the Refused transition** *(Freya Charge 3)*. On
`RefusedNoToken` returned to an open overlay, the overlay transitions
in place to the SPENT state (buildspec point 8): body swaps to the
tokens=0 copy, the valve renders its spent state and refuses touch,
the chip remains the only action, and the overlay does **not** close.
No sound plays — in particular the seal
(`playHarmonicSeal(arming = false)`) fires only on `Completed`, after
the transaction commits; a refused break is silent. The attempt's
bank is neither spent nor cleared by the refusal: it remains the
session's bank in the ViewModel, frozen behind a disabled valve,
and dies with the session as buildspec law already provides.
("Existing zero-token state" in this spec means the buildspec point-8
state, not the shipped valve's legacy label.)

**Amendment C — rollover during an open visit** *(Freya Charge 3)*.
`observeEmergencyBreaksRemaining()` must emit on a week-anchor change
while collected (a midnight tick, not only on collection or re-read);
an overlay left open across Monday 00:00 must never render last
week's count. Transitions inside an open visit: if the count changes
while the valve is not gripped (OPEN_IDLE or SPENT), the overlay
transitions in place — 1→0 enters SPENT (Amendment B's rendering),
0→1 leaves SPENT for OPEN_IDLE with the tokens=1 copy, the valve
enabled, and the session's bank unchanged (the bank belongs to the
session, not the week). If the count changes during HOLDING, GRACE,
or DRAINING, nothing on the surface changes mid-attempt: the attempt
runs to its own boundary (completion call, abandon, drain to floor),
and `completeEmergencyBreak()` decides under §4.2. The rendering
never interrupts a grip.

**Amendment D — the valve's count setter is pure** *(Freya Charge 4,
found in the shipped code)*. The valve view's `tokensRemaining: Int`
input survives, but its setter loses its side effect: assigning a new
count gates touch and rendering only, and must never call
`resetHold()` or otherwise touch held, banked, or drain state. Hold
reset is owned by the view's own attempt lifecycle (completion, and
the session-identity change at a new arm) — never by a count
rendering. Until this line lands, §5's live flow must not be wired
into an open overlay: the snapshot-at-open wiring it replaces is, on
this one point, safer than the flow.

## 6. The DB precondition — Migration(7, 8), fallback deleted

Adding the entity takes the database **7 → 8**. Non-negotiables:

1. **Explicit migration**, registered in `MonolithDatabase.getInstance`:

```kotlin
val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `emergency_break_spends` (" +
            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "`sessionId` INTEGER NOT NULL, " +
            "`spentAtWallMillis` INTEGER NOT NULL, " +
            "`weekAnchorMillis` INTEGER NOT NULL, " +
            "`heldSeconds` INTEGER NOT NULL)"
        )
    }
}
```

2. **`fallbackToDestructiveMigration(true)` is removed in the same
   change.** With it present, any future un-migrated bump silently wipes
   sessions, routines, and spends — including the very rows the routines
   reseed migrates (this coordinator's Phase-2 flag). After this change,
   a missing migration is a loud `IllegalStateException` in testing,
   never a silent wipe on his phone. This is permanent posture, not a
   one-version patch. *(Note for the merge audit: this deletion
   supersedes 05-sten-routines §3.6's sentence permitting
   `fallbackToDestructiveMigration` to remain for older gaps. Sten's
   file is to be corrected at its next touch; until then this spec
   governs — Sten A3.)*
3. **No backfill.** There is nothing truthful to backfill: today's
   in-memory spends were never recorded, so v8 starts with an empty
   ledger and a full token. Any "migration" that invented a past spend
   would be a fabricated record. (Consequence, stated plainly: on the
   upgrade week only, a break spent pre-upgrade under the old build is
   not remembered. One week, once, disclosed here — not hidden.)
4. **Sequencing vs the routines rebuild.** This migration owns 7→8 for
   the spend table **only**. The routines rebuild's schema needs (if
   any) take the next number (8→9) if it lands second, or this takes
   8→9 if it lands second — the migrations are independent and additive,
   touching disjoint tables. Two 7→8 migrations must never exist; Ren
   sequences the landings.
   The routines reseed is not a Room migration callback. The routines
   migration (7→8 or 8→9, per landing order) is schema only — routines
   columns, `routine_attendance`, the reseed-flag columns on
   `guard_settings`. The reseed data transform runs in
   `ensureSeededAndSyncPackages` under `seedMutex`, guarded by
   `routineReseedV1Done`, and is version-number-blind: whichever number
   the routines schema migration carries, the reseed executes
   identically. Once either migration ships, it is never renumbered or
   edited; the other registers after it, and both stay registered
   permanently, so a direct 7→9 upgrade runs the chain 7→8→9 in order.
   *(Sten A1 — which also corrects, on the record, the "reseed runs
   inside that migration" wording of the routines preconditions
   letter: the compression was the letter's, not the spec's.)*
   Both migrations live in the one `getInstance` builder as
   `.addMigrations(MIGRATION_7_8, MIGRATION_8_9)` once both have
   landed, with no fallback line. Whichever build lands second
   rebases its builder change onto the first's; neither may rewrite
   `getInstance` wholesale. *(Sten C1.)*

## 7. Surface contract (what the screens may say)

- Every rendering of the count traces to `remaining()` — the Deck chip
  (`$n/1 WEEK`), the disarm overlay, the valve. One number, one source.
  The count is a rendering of repository truth; no surface holds,
  decrements, or refunds it.
- The overlay's sentences are Freya's buildspec point 2, verbatim, and
  are incorporated by reference, not restated: title `EMERGENCY BREAK` ·
  body at 1 remaining: "This ends the session early. It is the only
  break this week." · body at 0: "This week's break is already spent.
  Sessions end when their time does." · rule line under the valve:
  "Hold to fill 60 seconds. Every 15 seconds banks. If your hand slips,
  what isn't banked drains away. The banked part stays." · chip
  `KEEP FOCUSING`. This spec changes no word of them, and no builder
  copy is sourced from this section.
- The shipped body sentence ("Sessions are unbreakable by tap. You have
  N weekly emergency break token remaining. Hold the physical valve
  continuously for 60 seconds to abort.") is false twice over —
  continuity died with the banked verdict, and it presents the token as
  a thing the UI holds — and is deleted wherever it appears. The words
  "continuously" / "continuous" may not appear in any valve or overlay
  copy. The count leaves the body with it: the body's job is
  consequence, the valve's job is the count (valve-drawn, per buildspec
  points 2 and 8), the rule line's job is mechanics. One job per
  element.
- The Ledger's rendering of a spend is specified in §4.5 as amended by
  Freya's Charge-2 ruling (record in the tab; no finish receipt for a
  broken session). A week in the rebuilt Ledger shows the spend as a
  fact of that week (or its absence as silence — UNSPENT is a word for
  the DAY CLOSED frame per Humans, not a row of its own).

## 8. Acceptance — the build is done when these pass

1. `MonolithWeek` unit tests: ordinary Monday; Sunday 23:59 vs Monday
   00:00 in Asia/Kolkata; a DST zone (America/New_York, spring-forward
   week); a zone-change pair (same instant, two zones, anchors differ
   by the ruled bound).
2. Remaining-derivation tests: no spends → 1; one spend this anchor → 0;
   one spend last anchor → 1; two spends same anchor (data corruption
   drill) → 0, never negative.
3. Refusal test: at 0 remaining, `completeEmergencyBreak()` returns
   `RefusedNoToken`, the session row stays active, and **no** spend row
   is inserted.
4. Atomicity test: forced failure inside the disarm step leaves no
   spend row; forced failure at insert leaves the session sealed.
5. Attempt-silence test: simulated bank crossings + slip + abandon
   (view-level) produce zero rows in `emergency_break_spends` and zero
   repository writes.
6. Migration test: a v7-shaped database with session/routine rows opens
   under v8 — rows intact, spends table present and empty; and the
   builder no longer contains `fallbackToDestructiveMigration`.
7. Restart test: spend, kill the process, relaunch — remaining is 0.
   (The exact scenario the current build fails.)
8. (Routines landing, when it comes) Chain tests, added to — never
   replacing — test 6: a v7-shaped database opens under v9 via the
   registered chain (routine rows intact, spends table present), and
   a v8-shaped database (spend rows present) opens under v9 with
   spend rows intact. *(Sten A2.)*
9. Routine-break test (routines build): valve completion inside a
   routine window books the spend against that session's id, the
   session books attended/partial/zero per 05-sten-routines §5.4
   (never wall-clock), no Partial Stamp intercept log is written, and
   the attendance entry is finalised in the same transaction — a
   forced failure at finalise leaves no spend row and the session
   sealed. *(Sten B3.)*

## 9. Out of scope, debts named (not hidden)

- The `outcomeReason.contains("Grace Cancel")` string-sniff inside
  `disarmActiveSession`, and free-text outcomes generally, want a typed
  outcome model. That is the **routines rebuild's** work (it reopens
  this file's disarm paths anyway); this spec deliberately does not
  expand into it. The break path specified here does not rely on the
  sniff — it keys on the spend row.
- When the typed outcome model lands (routines rebuild), "Emergency
  Break" and "Grace Cancel" must be distinct enum values, never
  substring neighbours; the interim string-sniff already classifies
  `EMERGENCY_BREAK_REASON` correctly (the sniff returns false for it),
  and the typed model must preserve that classification exactly.
  *(Sten, Charge 2c — offered, adopted.)*
- The seeded fake sessions and floored history totals (Critics T-1/T-2,
  Zug's unowned-record charge) are the **Ledger rebuild's** kills, not
  this spec's — but note §2's ledger is deliberately a *separate, clean*
  table: the token's truth never transits the falsified record store.
- Guard's fate **[GUARD?]** does not touch this spec: the token lives
  behind the valve, not behind any Guard surface.

*— Voss, Maximum-Implementable coordinator, Phase 3.*

---

## Phase-3 review record — this spec is FINAL as folded

Reviewed by its two charged seats; every ruling folded verbatim above
(amendments carry their owners' names inline):

- **Sten** (plan/token-reviews/sten.md) — Charge 1 migration:
  stands-with-amendment (A1–A3; the reseed is version-blind and
  transposes to Migration(8,9); no combine, token keeps 7→8; §6.2's
  fallback deletion breaks nothing of his). Charge 2 disarm paths:
  stands-with-amendment (B1–B4; the spend join is safe, but §4.1's
  original step 4 would have re-booked Lie #13's ghost for routine
  sessions — the dispatch is now inside the transaction). Charge 3
  week semantics: stands. Charge 4 (found): C1, cumulative
  `addMigrations`.
- **Freya** (plan/token-reviews/freya.md) — Charge 1 copy: BLOCKED,
  buildspec point-2 copy stands verbatim, §7 replaced with her rule
  text. Charge 2 receipt: BLOCKED in part — exit form and record are
  separable; the spend record stands in the Ledger tab, the finish
  receipt stays completion-only (her decisive point: under the
  demote-era two-form regime C-07 needed the receipt; the kept tab is
  the partial's home now, and receipt + tab would be two renderings
  of one fact). Charge 3 owner split: stands-with-amendment (A/B/C —
  derivation not snapshot; Refused lands in SPENT in place, silent,
  bank frozen; the flow emits on anchor change, never yanking a
  grip). Charge 4 (found): BLOCKED — the shipped valve's
  `tokensRemaining` setter calls `resetHold()` and would erase the
  bank under the live flow; Amendment D makes the setter pure and
  gates the flow wiring on it.

Coordinator's note: both found-charges (Freya 4, Sten 4) were real
defects in or under the draft, caught by the seats charged to look.
The ledger, anchor, transaction shape, and migration in §§2–4/6 were
endorsed untouched by both reviews.
