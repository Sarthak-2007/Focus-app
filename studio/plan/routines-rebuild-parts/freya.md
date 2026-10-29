# LIE #13 — THE KILL PROOF — Freya, MaxImpl seat (session/overlay; lie-hunter)

*Routines rebuild, preparatory part. Subject: the shipped fabrication —
`RoutineAlarmReceiver` starts a full session at `BOUNDARY_START` and wall-clock
elapsed is booked as completed at `BOUNDARY_END`. Method: shipped code only,
read-only, verified line by line Oct 29. Seeds recomputed from the tree as it
stands today — not from the register, not from Sten's future seeds.*

---

## 1. The manufactured-minutes arithmetic — recomputed from the current seeds

The only seed site in the tree is `MonolithRepository.ensureSeededAndSyncPackages`
(`MonolithRepository.kt`, seed block `if (dao.getAllRoutinesSnapshot().isEmpty())`).
Two rows, both `enabled = true`:

| Seed row (verbatim) | startMinutes | endMinutes | daysMask | loadout |
|---|---|---|---|---|
| `Morning Deep Work` | `9 * 60` = 540 (09:00) | `13 * 60` = 780 (13:00) | `RoutineSchedule.MON_FRI` = 31 | `STUDY` |
| `Night Wind-Down` | `22 * 60 + 30` = 1350 (22:30) | `7 * 60` = 420 (07:00) | `RoutineSchedule.ALL_DAYS` = 127 | `SLEEP` |

Duration is `RoutineSchedule.durationMinutes` (`MonolithModels.kt:390–394`):
`d = end − start; if (d ≤ 0) d += 1440; return d.coerceIn(15, 720)`.

- **Morning Deep Work:** 780 − 540 = **240 min**. Inside the clamp. Fires Mon–Fri.
- **Night Wind-Down:** 420 − 1350 = −930 → +1440 = **510 min**. Inside the clamp.
  Fires every day. (Overnight window: 22:30 → 07:00 next day.)

### The true numbers

- **Per scheduled weekday (Mon–Fri): 240 + 510 = 750 manufactured minutes
  exactly** — 12 h 30 m. The register's "~750" is not approximate on these
  days; it is exact.
- **Per weekend day (Sat–Sun): 510 manufactured minutes** (Night only).
- **Per week: 5 × 750 + 2 × 510 = 3,750 + 1,020 = 4,770 min = 79.5 hours/week**
  of completed focus an untouched phone can earn.
- **Calendar-day average: 4,770 / 7 = 681.4 min/day (≈ 11 h 22 m).** Anyone
  quoting "750/day" as an average overstates by ~69 min/day; 750 is the
  Mon–Fri figure and the peak, and it is the honest headline because those
  are the days the record is read against.

Conditions for the full figure (all verified in §2): every boundary alarm
delivers, and no session of his own is active at a START (the receiver's one
guard). The windows never collide with each other (Night ends 07:00, Morning
starts 09:00), so neither seed ever blocks the other's START. §2 also shows
the figure does **not** depend on the END alarm at all — the shield loop
mints the same totals on its own — so "alarms deliver" above really only
means "START delivers once per window."

**The second head of the lie, priced separately:** 510 of the 750 minutes
(68%) are a **SLEEP**-loadout window. The shipped receiver has no SLEEP
branch — it starts, ticks, completes and books a Night Wind-Down session
exactly like a study session, and `compute7DayHistory` (§2, step 9) sums it
into his focus history with no loadout filter. Even on a night he *was*
present, asleep in bed is not 8 h 30 m of focus. Ghost attendance is 750;
honest-attendance-but-wrong-category is 510 of it. The rebuild must kill
both heads (Demand D8).

*Not* counted above, for the record: the three fake history sessions seeded
in the same function (50 + 75 + 25 = 150 min, pre-completed) are a separate
fraud (Critics T-1/T-2, the Ledger rebuild's kill). They are not Lie #13 and
no minute of them is in the 4,770.

---

## 2. The code path, line by line — a completed session nobody attended

Cast: a phone face-down on a desk for a full Mon–Fri day. No human touches
it after 09:00. Every step below is shipped code; file:line given throughout.

**Step 1 — START fires on the event's say-so.**
`RoutineAlarmReceiver.kt:38–52` (`BOUNDARY_START`): loads the routine,
checks only `routine.enabled` (L35) and that no session is active
(L39–40: `active == null || !active.isActive`). That guard is the *only*
attendance-adjacent check in the entire path — and it checks for the
*absence of a competing session*, never for the presence of a person.
It then applies the loadout (L44) and starts a session (L45–50).

**Step 2 — The session is born full-sized.**
L48: `durationMinutes = RoutineSchedule.durationMinutes(routine)` — the
**full window** (240 / 510), regardless of when delivery actually lands.
A START delivered at 12:58 for the Morning window still arms 240 minutes
(it would seal until 16:58 — Sten §5.6 named this; it matters here because
it proves duration comes from the *schedule arithmetic*, never from time
actually spent in the window).

**Step 3 — The row is written with a clock, not a witness.**
`MonolithRepository.startSession` (`MonolithRepository.kt:869ff`):
`targetDurationSeconds = durationMinutes × 60`; the new
`FocusSessionEntity` carries `elapsedSeconds = 0`,
`startWallTimeMillis = wallNow`, `startElapsedRealtimeMillis = monoNow`,
`isActive = true`, `completedSuccessfully = false`. Note what the schema
has **no field for**: `FocusSessionEntity` (`MonolithModels.kt:249–274`)
contains no attendance, evidence, or presence field of any kind. The
fabrication is not a missing `if` — there is no variable in the system
that could have stopped it.

**Step 4 — The shield starts ticking the clock into the row.**
`RoutineAlarmReceiver.kt:51` starts `MonolithShieldService`, whose
2-second loop (`MonolithShieldService.kt:66–74,133`) calls
`repository.updateSessionProgress(activeSession)` for as long as the
session is active. Nobody needs to attend for this loop to run; it runs
*because* the machine started it.

**Step 5 — "Verified" elapsed is monotonic passage, nothing more.**
`updateSessionProgress` (`MonolithRepository.kt:926ff`) calls
`computeVerifiedSessionElapsed` (`:578ff`): in the normal branch,
`monoElapsedSec = (nowMono − startElapsedRealtimeMillis) / 1000` and the
return is `accumulatedBeforeRebootSeconds + monoElapsedSec`. The
"verification" verifies exactly one thing — that time passed and the
HMAC/drift checks pass. A desk passes this verification perfectly. The
tick then writes `elapsedSeconds = verifiedElapsed` back to the row
(`:957`, stopwatch branch; `:1022`, timed branch, coerced to target).

**Step 6 — First mint: the session completes itself.**
`updateSessionProgress` (`:1002,1016–1032`): when
`verifiedElapsed ≥ effectiveTarget`, `allBlocksCompleted = true`
(routine sessions are started with `studyBlocksCount = 1`), and the row
is written with `elapsedSeconds = target`, `isActive = false`,
**`completedSuccessfully = true`**, and
`forgedArtifactId = CurioCatalog.artifacts.shuffled().first().id` — a
prize, minted for the desk. At this point the END alarm is *redundant*:
the full 240 / 510 is already booked as a completed success.

**Step 7 — Second mint: END books the same lie by the other door.**
If the shield loop is dead or slow and the row is still active when
`BOUNDARY_END` delivers, `RoutineAlarmReceiver.kt:54–64` matches the
session by the mutable name string
`active.profileName == "Routine: ${routine.name}"` (L56–57 — the
rename-orphan defect, Sten §3.3) and calls
`disarmActiveSession(completed = true, outcomeReason = "Routine Window Ended")`
(L59–62). `completed` is not derived from anything; it is a literal the
receiver asserts.

**Step 8 — The booking formula takes the maximum of two clocks.**
`disarmActiveSession` (`MonolithRepository.kt:1085ff`):
`bankedElapsed = max(verifiedElapsed, active.elapsedSeconds)` (L1089) and
the row is updated `elapsedSeconds = bankedElapsed, isActive = false,
completedSuccessfully = completed` (L1095–1102). `max()` is the mint's
die: whichever clock ran longer wins, and neither clock ever consulted
a human. (The Grace-Cancel zeroing at L1088 keys on
`outcomeReason.contains("Grace Cancel")` — "Routine Window Ended" does
not contain it, so no rescue there.)

**Step 9 — The total launders it.**
`compute7DayHistory` (`:1119ff`) sums `elapsedSeconds` over session rows
by start day, loadout-blind, outcome-blind beyond the row's own fields.
The 750 lands in his 7-day focus history, his block counts
(`totalMins / 25`), and every surface downstream. There is no later
checkpoint: once Step 6 or 8 writes the row, every reader is honest
about a false row.

**Path summary:** the fabrication needs exactly three ingredients —
(1) an event trusted as a fact (START), (2) elapsed *derived at booking
time from clocks* (`max(verified, stored)` / mono difference), and
(3) `completed` asserted by the caller. Remove any one and this instance
dies; the rebuild removes all three, which is what §3 proves class-wide.

---

## 3. The kill argument — impossibility, argued adversarially

### 3.0 The property that does the killing

Sten's Derive-Don't-Fire, reduced to the four properties this proof
depends on (full demand list in §4 — if any demand is dropped in
reconciliation, the corresponding paragraph of this proof is void and
must be re-argued):

- **P1 — Derivation writes nothing.** Window state is a pure function
  `derive(schedule rows, now, session rows) → state`. It creates no
  session, sets no elapsed, books no outcome. A boundary alarm is a
  hint that triggers re-derivation; its payload (which boundary, how
  late, whether duplicated or hallucinated) is never believed.
- **P2 — Creation is not booking.** The only session a routine ever
  gets is materialised by reconcile with `elapsedSeconds = 0`,
  `target = remainder` (window end − now), under the natural key
  `(routineId, windowStartMillis)` — at most one per window, ever.
- **P3 — Booking has exactly one source: the session row's own accrued,
  evidence-gated ticks.** Bookable accrual is a quantity that grows
  *only* while the session is live and attendance evidence is current
  (Sten §5.3 evidence kinds; accrual from stored evidence timestamps,
  Sten-invention Q2, or an evidence-gated tick — the arithmetic may
  choose, the gate may not). It is a **stored, incremental** quantity:
  it is never recomputed at booking time from start/end timestamps.
- **P4 — Finalise assigns; it never maximises.** The single booking
  funnel writes `booked := min(accrued, window length)`, sets the
  outcome from accrued vs window (ATTENDED / PARTIAL / MISSED per
  Sten §5.4 thresholds), and zeroes a MISSED row. The generic
  `disarmActiveSession` **refuses** routine rows. Unfinalised routine
  rows contribute 0 to every total.

**Theorem (the class, not the instance).** Define *fabrication* as a
finalised routine row with `booked > accrued` — minutes in the record
that no live, evidenced tick ever produced. Under P1–P4 fabrication is
unreachable: booked values are written only by finalise (P4); finalise
writes only `min(accrued, …)` (P4); accrued grows only by live
evidence-gated ticks on that row (P3); derive and alarms write no
booked value at all (P1, P2). Therefore `booked ≤ accrued` for every
routine row, by construction of the only writer. Lie #13 required a
writer that sourced minutes from *clocks at an event*; that writer no
longer exists. This is a proof about the write-set, not a promise
about behaviour — it survives any scheduling bug, because scheduling
bugs can now only affect *whether* a session materialises, never
*how many minutes it books*.

### 3.1 Adversarial scenarios — each one constructed, each one dying

**A1. The END hint never arrives (alarm lost, Doze eats it, OEM kills it).**
Shipped: END was one of two mints; its loss merely deferred booking to
the shield loop (Step 6) — the lie survived losing an alarm.
Rebuilt: there is no END event and nothing books at a boundary. State
at `now > windowEnd` derives CLOSED; finalise-on-observe (any later
wake: boot, resume, next reconcile) books `min(accrued, …)` from the
row. An untouched window accrued 0 → books 0, outcome MISSED. **Dies:**
the missing hint removes a *doorbell*, not a *witness* — the witness
was never the alarm.

**A2. The shield service is killed mid-window (LMK, force-stop of the
service, crash).**
Construction: service dies at minute 40 of FASTED; he studies,
evidenced, for 40 minutes; the desk sits for the remaining 140.
Rebuilt: accrual is stored and incremental (P3) — the dead interval
adds exactly 0 because no tick ran and no evidence timestamp exists in
it. On the next wake, reconcile finalises from the stored accrual:
40 evidenced minutes book (PARTIAL), 140 do not. **Dies — and note the
load-bearing word is *stored*:** if a builder "simplifies" finalise
to recompute elapsed as `monoNow − monoStart` (the shipped
`computeVerifiedSessionElapsed` habit), the dead interval refills and
A2 resurrects the lie in full. That is why D3/D4 are demands, not
suggestions, and why test T5 (§5) exists.

**A3. Reboot across a boundary (phone off 04:00–09:30, across FASTED's
whole 05:00–08:00 window; or reboot mid-window).**
Shipped code's reboot branch is itself a mint:
`computeVerifiedSessionElapsed` (`:597–599`) returns
`max(accumulatedBeforeRebootSeconds, elapsedSeconds) + nowMono/1000` —
seconds since the *new* boot are added to a session that was dead
through them, and BOOT_COUNT change is treated as continuity, not as
a gap.
Rebuilt: the routine path is fenced off that branch entirely (D4).
Boot is just another reconcile trigger (P1): post-boot `now` derives
the 05:00 window CLOSED (or the mid-window remainder, if still open);
finalise/materialise read only stored accrual and stored evidence.
Phone-off-through-window: no session row or an accrued-0 row →
MISSED/NOT_DELIVERED per Sten's rules, 0 booked. Mid-window reboot:
pre-reboot accrual stands; post-reboot ticks can accrue only if the
session is live again *inside* the window and evidence is current —
real minutes, really gated. **Dies.**

**A4. Clock set forward (user jumps wall clock +3 h mid-window; or
across the window end).**
Rebuilt: window *identity* is wall-clock by definition (a schedule is
a civil-time object — TOKEN-SPEC §3 accepts the same trust), so a
forward jump closes the window early. But booking is not wall-derived
(P3): the jump advances `now` and adds **zero** ticks — accrual is
monotonic/incremental and evidence-gated, and finalise caps at
accrued. Forward clock can only *truncate* a booking, never inflate
one: `booked ≤ accrued` is clock-independent because neither side of
the inequality is computed from the wall clock at booking time.
Clock set *backward* re-opens civil time, but the natural key
`(routineId, windowStartMillis)` (P2) refuses a second session for a
window that already has one; a *different* day's window is a genuinely
different scheduled window — his phone, his clock, his discipline
tool (TOKEN-SPEC §3's ruled scope), and even then it books only live
evidenced ticks from that point on, never retroactive minutes. **Dies
as fabrication;** the residue is self-deception about *which day it
is*, which no on-device record can police and none is asked to.

**A5. Duplicate / hallucinated / 3-hours-late START hints.**
Rebuilt: hints assert nothing (P1). A hint inside the window
materialises at most the remainder (P2); a hint after the window
derives OUTSIDE and materialises nothing; duplicates hit the natural
key and no-op. Worst case cost of a spurious hint is one reconcile
pass over a dozen rows. **Dies.**

**A6. Rename / delete / retime the routine mid-window.**
Shipped: rename orphaned the END match (name string); the session
then ran to self-completion and booked anyway (Step 6).
Rebuilt: the session row carries `routineId` + frozen
`windowStartMillis/windowEndMillis` (Sten-invention §1 R5); matching
is by id against the *stored* window. The routine row governs future
windows only. The live row stands down and finalises by its snapshot
whatever happens to its parent. **Dies.**

### 3.2 The one survivor — named, bounded, and priced

I tried to keep a fabrication alive through every seam above. The
class in §3.0 is dead. Exactly one adjacent survivor remains, and it
is **not** Lie #13 — it must not be allowed to wear its name:

> **Presence is not study.** Bookable accrual is evidence-*gated*
> (P3), and the evidence kinds (Sten §5.3: session-surface acts,
> foreground return, intercepts, hold start) prove a person was
> *there*, not that they were *studying*. A phone generating genuine
> evidence while its owner makes tea accrues honestly-labelled
> attended minutes. Bound: each evidence instant covers at most its
> trailing-5-minute window (Sten §5.3), so a single stray act mints
> ≤ 5 min; sustained fabrication requires sustained presence-like
> behaviour for the full window — at which point the record's claim
> ("he was present in the window") is literally true, and the
> stronger claim ("he studied") is one Monolith never makes (Sten N3:
> books are not apps; the app will not infer study). **The kill is
> total for unattended booking. The survivor is the definition of
> attendance itself,** accepted in Sten §5.3–5.4 and priced here so
> nobody later calls it a regression.

One implementation trap sits *inside* the class and is therefore a
demand, not a survivor: if the builder implements "accrued ticks" as
raw monotonic ticks of a live shield service (desk ticking happily,
§2 Step 5 with a new name), A2's trap fires in reverse — the service
is alive, the ticks are real, and an untouched desk books the full
window. P3's evidence gate is the only thing between that build and
Lie #13 reincarnated. D3 states it as a testable requirement (T4).

---

## 4. Demands on derive() — numbered, for Sten to reconcile, Voss to arbitrate

These are the properties §3's proof consumed. Each is stated as a
requirement on the rebuilt design (derive(), reconcile, finalise —
whoever owns the line), not as a comment on Sten's draft, which
already satisfies most of them by my reading.

- **D1 — Purity.** `derive()` / `windowState()` takes (schedule rows,
  now, session rows, settings) and returns state. It performs no DAO
  write, no alarm operation, no service start. Testable: calling it
  1,000 times in a loop leaves every table byte-identical.
- **D2 — Single booking funnel.** Exactly one code path writes a
  routine session's final `elapsedSeconds` / `completedSuccessfully`:
  finalise. `disarmActiveSession` refuses (or reroutes) any session
  with `routineId != null`; `updateSessionProgress`'s completion
  branch sets neither `completedSuccessfully = true` nor a
  `forgedArtifactId` for routine rows. Grep is the audit: no other
  writer exists.
- **D3 — Accrual is stored, incremental, and evidence-gated.**
  Bookable accrual for a routine row grows only (a) while that
  session is live, (b) in increments produced by a running tick or by
  coverage computed over *stored* evidence timestamps, and (c) only
  across intervals evidence covers. It is never computed at finalise
  time as a difference of timestamps (wall or monotonic) spanning an
  interval nobody sampled.
- **D4 — No max(), no reboot top-up, on the routine path.** Finalise
  **assigns** `booked := min(accrued, scheduled window length)`. The
  shipped formulas `max(verifiedElapsed, elapsedSeconds)` and
  `prior + (nowMono / 1000)` (reboot branch) must be unreachable for
  routine rows.
- **D5 — Natural-key materialisation.** At most one session per
  `(routineId, windowStartMillis)`, created with elapsed 0 and
  target = remainder; remainder < 5 min materialises nothing.
  Repeated reconcile is idempotent.
- **D6 — Frozen window, id identity.** The session row stores
  `routineId`, `windowStartMillis`, `windowEndMillis` at
  materialisation; all matching and stand-down use those, never the
  live routine row and never a name string.
- **D7 — Unfinalised rows are worth zero.** Every total/history query
  excludes routine rows with no finalised outcome (or equivalently,
  such rows carry booked 0 until finalise writes). A crash between
  materialise and finalise can under-book, never over-book.
- **D8 — SLEEP books no study minutes.** A SLEEP-loadout routine
  session contributes 0 to focus/study history totals regardless of
  attendance (kills the 510-minute category head, §1). Its lockdown
  behaviour is untouched; its minutes are simply never focus minutes.
- **D9 — Legacy rows are not laundered.** Rows finalised by the
  shipped build have no accrual record. Migration must not set their
  accrued := booked (that would certify ghosts retroactively). They
  are either excluded from the audit invariant's scope by an explicit
  `legacy` marker, or excluded from totals — builder's choice, but
  the choice is written and tested (T10).

---

## 5. Acceptance — the routines build is done when these pass

*(Style: TOKEN-SPEC §8. Each test names the shipped behaviour it
replaces; a test the shipped build already passes proves nothing and
none of these is one.)*

1. **Seed-arithmetic test:** against the pre-rebuild seeds as fixture
   (Morning Deep Work 540→780 MON_FRI; Night Wind-Down 1350→420
   ALL_DAYS), `durationMinutes` returns 240 and 510; a simulated
   untouched Mon–Fri week under the rebuilt engine books **0** total
   routine minutes (shipped: 4,770).
2. **Ghost test:** phone untouched through a full STUDY window →
   a session row may exist, finalised MISSED, `elapsedSeconds = 0`,
   `completedSuccessfully = false`, `forgedArtifactId = null`, and
   the 7-day history total is unchanged.
3. **Remainder test:** first reconcile at windowStart + 22 min
   materialises a session with target = window remainder (±1 min),
   never the full window; first reconcile at remainder < 5 min
   materialises nothing.
4. **Evidence-gate test (the reincarnation trap):** shield service
   alive for a full window, session live, **zero** evidence events →
   finalised booked = 0. Then: one evidence instant at minute 30 →
   booked ≤ 5 min from that instant's coverage, not the window.
5. **Killed-service test:** service killed at 40 evidenced minutes
   into a 180-minute window; next wake after window end finalises
   booked = 40 (± one tick), never the dead interval, never 180.
6. **Reboot tests:** (a) reboot spanning a whole window → 0 booked,
   outcome MISSED or NOT_DELIVERED, streak treatment per spec;
   (b) reboot mid-window → booked = pre-reboot accrual + only
   post-reboot *evidenced, in-window* accrual; the since-boot
   seconds of the shipped reboot branch contribute nothing.
7. **Clock tests:** wall clock +3 h mid-window → booked ≤ accrued
   at jump time (no inflation); wall clock set back across an
   already-materialised window → still exactly one session row for
   `(routineId, windowStartMillis)`.
8. **Stale/duplicate hint tests:** START hint 3 h after window end →
   no session, no outcome booked, chain armed at next true boundary;
   reconcile ×3 inside one window → exactly one session row.
9. **Rename/delete mid-window tests:** rename at mid-window →
   stand-down and outcome proceed by id (Lie #13's regression test —
   the shipped build fails it); delete at mid-window → session
   finalises at its stored end with its outcome; routine gone
   immediately.
10. **History audit test (the standing invariant):** a property test
    over generated schedules, evidence patterns, kills, reboots and
    clock moves, **plus** a direct database audit runnable against a
    real post-rebuild database: *no session row exists whose booked
    `elapsedSeconds` exceeds its tick-accrued elapsed*
    (`booked ≤ accrued` for every finalised routine row; SLEEP rows
    book 0 into study totals per D8). Legacy pre-rebuild rows are
    handled exactly as D9 specifies — flagged or excluded, never
    silently certified — and the audit report names which.
11. **Funnel audit:** static check (grep-level, run in CI or by
    hand at merge): `disarmActiveSession`'s `max(verified, stored)`
    formula and the `completed = true` literal in
    `RoutineAlarmReceiver` no longer exist on any routine path; the
    receiver (or its hint-shell successor) contains no call that
    writes elapsed or completion.
12. **Migration chain (extends TOKEN-SPEC §8.6/§8.8, never
    replaces):** v7-shaped DB → v9 via 7→8→9 with routine rows and
    spend rows intact; v8-shaped DB → v9 with spends intact; the
    routines reseed executes under its flag identically at either
    landing order.

---

*— Freya, Maximum-Implementable · session seat · lie-hunter.
Proof, not opinion: §1 is arithmetic, §2 is the shipped code,
§3 is a write-set argument, §§4–5 are how the builder is held to it.*
