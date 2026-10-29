# TOKEN-SPEC — Sten review (routines-preconditions lens)

*Phase 3. Reviewer: Sten, Maximum-Implementable, routines seat — owner of
plan/maximpl-parts/05-sten-routines.md and the routines build preconditions
(mail/ren/0602__from-voss__routines-build-preconditions.md). Scope: §3/§6 and
the disarm paths where they touch routines. The token's design (ledger,
repository enforcement, week anchor) is not redesigned here. Code claims
below were re-verified against the shipped tree (MonolithDatabase.kt,
MonolithRepository.kt, MonolithModels.kt, RoutineAlarmReceiver.kt,
TactileDeckScreen.kt), not taken from either spec's word.*

---

## Charge 1 — The Migration(7,8) collision

**RULING: stands-with-amendment.** Answer to the either/or: **(a) yes** — the
reseed runs identically as a Migration(8,9) data migration if the token lands
first. No combination into one 7→8, and the token does not yield.

### Mechanism

First, a correction to my own precondition letter, on the record: the letter
says "the reseed runs inside that migration," and that wording conflates two
different things in my spec. In 05-sten-routines the **Room migration is
schema only** (§3.6: `ALTER TABLE routines ADD COLUMN` ×3 + `CREATE TABLE
routine_attendance` + the two reseed-flag columns on `guard_settings`;
Appendix C step 1). The **reseed data transform is not in
`Migration.migrate()` at all** — §4 places it in
`MonolithRepository.ensureSeededAndSyncPackages`, under the existing
`seedMutex` (shipped: MonolithRepository.kt:74–75), flag-guarded by
`routineReseedV1Done`, running before the VM init `scheduleAll`. Voss's
letter compressed that into one phrase; the TOKEN-SPEC should not inherit
the compression, because the distinction is exactly what decides this charge.

The reseed is therefore **version-number-blind**, in three independent ways:

1. **The match/transform never reads the database version.** It matches
   routine rows on four fields (name, start, end, daysMask, loadoutName;
   `enabled` carried over), transforms in place with the id preserved, and
   postpones a row only when an active session's `profileName` matches its
   old name (§4, §3.3 fence). Nothing in that logic can tell 8 from 9.
2. **The schema SQL transposes verbatim.** The same ALTER/CREATE statements
   sit unchanged inside a `Migration(8, 9)` object; they touch
   `routines` / `routine_attendance` / `guard_settings`, disjoint from the
   token's `emergency_break_spends` (TOKEN-SPEC §6.1). §6.4's "independent
   and additive" claim is true — verified table-by-table, not assumed.
3. **The flag does the sequencing, not the version.** `routineReseedV1Done`
   is added (default false) by whichever migration carries the routines
   schema; the first seed-sync after the DB opens at that version runs the
   pass once. A fresh install never migrates and sets the flag in the seed
   pass (§4) — also version-blind.

Landing order settles which branch of §6.4 is real: the token is
build-track-near, the routines rebuild is third. So token = 7→8,
routines = 8→9 is the *expected* path, not the contingency. Combining into
one 7→8 is rejected: it would either delay the token behind the routines
schema or freeze that schema early — and the routines schema is still
moving under my own file (MASTER-PLAN §0 struck auto-pause globally,
overruling my §5.5; what that does to the `autoPausedAtMillis` column is
routines-internal churn the token must not be welded to).

### §6.2 — deleting `fallbackToDestructiveMigration(true)`

**Does not break any assumption in the routines spec.** My §3.6 said the
fallback "may remain for older gaps but must never be the 7→8 path" — that
was a *permission*, not a dependence; no step in §4 or Appendix C invokes
the fallback, and my precondition's ethic is explicit-migration-or-nothing.
§6.2 is stricter in the same direction. The one real consequence, named so
nobody discovers it in a crash report: after the token lands, the routines
build loses the option of shipping *any* bump without an explicit
migration — a missing `Migration(8, 9)` becomes a loud
`IllegalStateException` on his phone instead of a silent wipe. That is the
intended posture, and it is now a precondition of the routines build, not
a preference.

### Amendments required (fold into TOKEN-SPEC)

**A1 — append to §6.4:**

> The routines reseed is not a Room migration callback. The routines
> migration (7→8 or 8→9, per landing order) is schema only — routines
> columns, `routine_attendance`, the reseed-flag columns on
> `guard_settings`. The reseed data transform runs in
> `ensureSeededAndSyncPackages` under `seedMutex`, guarded by
> `routineReseedV1Done`, and is version-number-blind: whichever number the
> routines schema migration carries, the reseed executes identically.
> Once either migration ships, it is never renumbered or edited; the
> other registers after it, and both stay registered permanently, so a
> direct 7→9 upgrade runs the chain 7→8→9 in order.

**A2 — append to §8 (acceptance):**

> 8. (Routines landing, when it comes) Chain tests, added to — never
> replacing — test 6: a v7-shaped database opens under v9 via the
> registered chain (routine rows intact, spends table present), and a
> v8-shaped database (spend rows present) opens under v9 with spend rows
> intact.

**A3 — note in §6.2 (supersession, for the merge audit):**

> This deletion supersedes 05-sten-routines §3.6's sentence permitting
> `fallbackToDestructiveMigration` to remain for older gaps. Sten's file
> is to be corrected at its next touch; until then this spec governs.

---

## Charge 2 — Disarm-path interference

**RULING: stands-with-amendment.** The join is safe; the transaction is
safe at END and unsafe as written at break-completion. Three sub-rulings:

### 2a. The spend join — SAFE, no amendment

My rewrite **never deletes or re-keys a session row.** Both enforcement
methods in §5.4 — `disarmRoutineSessionPartial(attendedSeconds)` and
`disarmRoutineSessionUnattended()` — are in-place column updates on the
existing `FocusSessionEntity` row, in the `dao.updateSession(active.copy(…))`
shape the shipped `disarmActiveSession` already uses: PARTIAL rewrites
`elapsedSeconds`; MISSED zeroes `elapsedSeconds` and nulls
`forgedArtifactId`; both set `completedSuccessfully = false`. The
`@PrimaryKey id` is never reassigned, no session DELETE exists anywhere in
my §3.6 DAO additions (the deletes there are routines and attendance rows
only), and routine deletion (§3.4) does not touch sessions. So the spend
row's `sessionId` join survives every routines outcome — including the
receipt and Ledger rendering the row later by that id (§4.5) — and the
spend's own facts (`heldSeconds = 60`, `spentAtWallMillis`) are
independent of the `elapsedSeconds` my rewrite owns.

### 2b. The transaction — END is clean; §4.1 step 4 is the collision

At **BOUNDARY_END** there is no conflict: the receiver path is never
token-wrapped, and `finalizeAttendance` (§5.4) runs there as its own pass.
My END-time placement stands untouched by this spec.

At **break completion** §4.1 as written **re-opens the ghost for routine
sessions only.** Step 4 calls the generic
`disarmActiveSession(completed = false, "Emergency Break")`, which in the
shipped code (MonolithRepository.kt:1085ff) books
`elapsedSeconds = max(verified wall-clock, active.elapsedSeconds)` and
mints a Partial Stamp intercept-log line — precisely the wall-clock booking
my §5.1/§5.4 exist to kill, and precisely the partial-stamp noise my §5
fights-list names. My spec already owns this moment: §5.4 finalises
attendance "at emergency-break completion, whichever ends the routine
session first," and §5.3 E4 exists because a break can end a routine
session. A routine session broken by the valve must book ATTENDED-stands /
PARTIAL-rewrite / MISSED-zero per the outcome table — never wall-clock.

Placement inside the transaction is also load-bearing: the routine
finalise must happen **inside** `completeEmergencyBreak`'s transaction,
not in the VM's post-`Completed` shield-stop/stats refresh (§5). Outside,
a process death between commit and finalise leaves the spend booked, the
session ended, and the attendance entry sitting `SCHEDULED` until its
window end — where the §5.6 sweep would judge it by rules written for
lost alarms, not for a break. Inside, spend + routine-aware disarm +
finalise commit or roll back together; the finalise is a plain DAO-level
step (attendance read + the §5.4 session update), so it composes under
the token's one transaction boundary without nesting a second one.

### Amendment required (fold into TOKEN-SPEC)

**B1 — replace §4.1 step 4 with a dispatch, and add step 5:**

> 4. Disarm, dispatched on the active session's kind:
>    - *Routine session* (`profileName == "Routine: <name>"` — the shipped
>      recogniser, until id-based matching lands): compute the §5.4
>      outcome from the attendance row's accrued `attendedSeconds` vs the
>      window (or remainder, if late-fired), then disarm by that outcome —
>      ATTENDED stands as booked, PARTIAL via
>      `disarmRoutineSessionPartial(attendedSeconds)`, MISSED via
>      `disarmRoutineSessionUnattended()` — and run `finalizeAttendance`
>      for that window, all inside this transaction. SLEEP-loadout
>      routines are exempt from attendance (§5.4): no finalise runs, and
>      their session disarms by the generic path.
>    - *Any other session:* `disarmActiveSession(completed = false,
>      outcomeReason = EMERGENCY_BREAK_REASON)` as specced.
> 5. Return Completed. (Renumber the existing return accordingly.)
>
> The routines rebuild changing the *inside* of this dispatch is not a
> new caller of a break-class disarm under §5's convention — it is this
> step's named owner arriving. No other caller may bypass it.

**B2 — replace the last bullet of §5** ("routine END disarms never
interact with the token") **with:**

> The stopwatch clean-completion path (`completed = true`) and routine
> BOUNDARY_END disarms never interact with the token. A break that ends a
> *routine* session does interact — only through §4.1's dispatch, which
> is the one place token spend and attendance finalisation meet.

**B3 — append to §8 (acceptance):**

> 9. Routine-break test (routines build): valve completion inside a
> routine window books the spend against that session's id, the session
> books attended/partial/zero per §5.4 (never wall-clock), no Partial
> Stamp intercept log is written, and the attendance entry is finalised
> in the same transaction — a forced failure at finalise leaves no spend
> row and the session sealed.

### 2c. §4.4 grace cancel and §9 typed outcomes vs my fences — NO collision

- **Grace cancel (§4.4):** no collision with the rename fence or the span
  clamp. It writes no spend and never enters `completeEmergencyBreak`;
  the rename fence guards `profileName` matching at END, a path grace
  cancel does not touch; the clamp (30..1080, §3.1) lives in
  `adjustRoutineTime` / `RoutineSchedule.durationMinutes`, neither of
  which is on any disarm path. One verified wrinkle, filed for honesty,
  not as a charge: the Deck's grace abort renders for *any* active
  session with `elapsedSeconds ≤ 10` (TactileDeckScreen.kt:454), so a
  routine session can be grace-cancelled in its first ten seconds. The
  outcome is still coherent — zero-booking matches the only §5.4 outcome
  ten seconds of presence can earn (MISSED), and the orphaned attendance
  entry closes via the §5.6 seed-sync sweep. No token change required;
  the sweep is mine.
- **Typed outcomes (§9):** the deferral is compatible — it hands the
  routines rebuild work my §5.4 already assumes (the two disarm variants
  exist precisely because the string-sniffing generic method cannot
  express them). One guard for when the typed model lands: "Emergency
  Break" and "Grace Cancel" must be distinct enum values, never
  substring neighbours — in the interim the sniff returns false for
  `EMERGENCY_BREAK_REASON`, which is correct, and the typed model must
  preserve that classification exactly. Offered as a sentence for §9;
  not a condition of this ruling.
- **Span clamp 30..1080:** no interaction anywhere in TOKEN-SPEC. The
  token's only duration constant is `heldSeconds = 60`, a valve fact,
  unrelated to window spans. Stated so the merge audit doesn't have to
  re-derive it.

### 2d. §4.3 attempt-silence vs attendance evidence E4 — amendment required

§4.3's silence is scoped by its own first bullets to the spend ledger,
but its sentence "the record of the valve is binary per week: a spend
row, or silence" reads broadly enough that a builder could kill my §5.3
**E4**: the 60-second hold *starting* during a routine session stamps
attendance evidence (`routine_attendance.evidenceMask` bit 3,
`lastEvidenceMillis`). That is not an attempt record — it is presence
evidence inside an already-running routine window, on a different table,
owned by a different fact (attendance), and §5.3's sampling depends on it
(the trailing-5-minute rule is what lets the hold's own minute accrue).
Attempts at the valve on a *manual* session write nothing anywhere; that
half of E4's context is untouched.

**B4 — append to §4.3:**

> This silence governs `emergency_break_spends` and token accounting
> only. It does not reach `routine_attendance`: a hold started inside a
> routine window stamps E4 attendance evidence per 05-sten-routines
> §5.3 — presence in a scheduled window, not a valve attempt — and no
> builder may suppress it under this section. Conversely, E4 evidence
> is never an input to `remaining()`.

---

## Charge 3 — Week semantics

**RULING: stands.** No collision. (One guard sentence offered below, not
a condition.)

The Monday basis genuinely matches, by mechanism on both sides, not by
locale luck: TOKEN-SPEC §3 uses `previousOrSame(DayOfWeek.MONDAY)` in
java.time; the shipped `RoutineSchedule` computes its day bit as
`(Calendar.DAY_OF_WEEK + 5) % 7` (MonolithModels.kt:375ff) — bit 0 is
Monday regardless of the locale's first-day-of-week setting. Both sides
evaluate in the device's current local civil time/zone at the moment of
evaluation. Three differences exist and none is a collision:

1. **Routines have no week bucket to collide with.** Attendance is keyed
   `(routineId, windowStartMillis)`; miss streaks are runs of consecutive
   *scheduled windows* (§5.5/§5.7 as amended by the global auto-pause
   strike — the printed streak, same counting). A streak spanning Sunday→
   Monday windows runs straight through the token's Monday refill; the
   refill resets nothing in routines, and no routine state is derived
   from `MonolithWeek`. The token's week and the routine's window are
   different facts on disjoint tables, never joined by week.
2. **Attribution asymmetry — deliberate on both sides, do not "align."**
   A spend is attributed by *completion* instant (§4.2: a hold completing
   Mon 00:01 spends the new week). A routine window is attributed by its
   *start* day (§6: start-day semantics; the SLEEP block is one row
   anchored at 20:00). An overnight window starting Sunday can therefore
   contain a Monday-anchored spend. That is two true statements about two
   different facts; aligning either side to the other would break §4.2's
   ruled race or the strip's start-day drawing. (For the seed day it is
   also moot: the only overnight seed is SLEEP, which is
   attendance-exempt.)
3. **Storage asymmetry.** The token stores an anchor *instant*
   (`weekAnchorMillis`, never recomputed — correct for a ledger).
   Routines store minutes-of-day + a day mask, never a schedule instant,
   so a timezone change shifts routine windows and the token anchor
   together, in the same civil-time terms. The §3.5 "NEXT MONDAY" pause
   word lands on Monday 00:00 local — an instant that can coincide with a
   token refill; a routine resuming and a token refreshing at the same
   midnight are independent facts sharing a clock, not a counter.

§0 decision 2 already scopes `MonolithWeek`'s consumers correctly (token
+ Ledger weekly line). Guard sentence offered for §3, not required:
*" `MonolithWeek` does not replace `RoutineSchedule`; routines keep their
own day-mask math, and nothing in routines consumes the week anchor."*

---

## Charge 4 (found) — The shared builder: `MonolithDatabase.getInstance`

**RULING: stands-with-amendment (small).** Both builds edit the same
companion function in the same file (shipped: MonolithDatabase.kt:36–53):
the token removes `.fallbackToDestructiveMigration(true)` and registers
`MIGRATION_7_8`; the routines build bumps `version`, extends the
`@Database` entity list, and registers `MIGRATION_8_9`. Semantically
independent, textually co-tenant — a wholesale rewrite of `getInstance`
by the second build would silently drop the first build's registration,
and with `exportSchema = false` there is no schema-JSON diff to catch it.
The §8 migration tests are the only guard, which is why A2's chain tests
matter.

**C1 — append to §6.4:**

> Both migrations live in the one `getInstance` builder as
> `.addMigrations(MIGRATION_7_8, MIGRATION_8_9)` once both have landed,
> with no fallback line. Whichever build lands second rebases its
> builder change onto the first's; neither may rewrite `getInstance`
> wholesale.

---

## Summary of rulings

| Charge | Ruling |
|---|---|
| 1. Migration(7,8) collision + §6.2 fallback | **stands-with-amendment** (A1, A2, A3) — reseed transposes to 8→9 identically; no combine, token doesn't yield; fallback deletion breaks nothing of mine |
| 2. Disarm-path interference | **stands-with-amendment** (B1, B2, B3, B4) — join safe; §4.1 step 4 must dispatch routine sessions or it re-books the ghost; E4 scoped out of §4.3's silence |
| 3. Week semantics | **stands** — Monday basis matches by mechanism; the completion-vs-start attribution asymmetry is deliberate on both sides |
| 4. Shared `getInstance` builder (found) | **stands-with-amendment** (C1) — cumulative `addMigrations`, no wholesale rewrite |

Nothing here blocks the token build landing first. B1/B2 are written so
they bind at the *routines* landing if the token ships before them — but
the dispatch's generic branch must not be documented as final for routine
sessions in the meantime; B2's replacement sentence is what keeps that
honest.

— Sten, Maximum-Implementable · routines seat · Phase 3
