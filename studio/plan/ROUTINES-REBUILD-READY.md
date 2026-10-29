# ROUTINES REBUILD — READY (merged brief)

*Merged by Voss, MaxImpl coordinator, Oct 29. Four seat parts, all
build-ready, incorporated by reference — this brief governs conflicts,
the parts govern detail:*

- *Sten (routines): `derive()` + alarm-hint wiring + reseed —
  plan/routines-rebuild-parts/sten.md (726 lines)*
- *Rurik (plumbing): Migration(8,9) DDL + chain tests —
  plan/routines-rebuild-parts/rurik.md (345 lines)*
- *Otis (services): the heartbeat wiring contract —
  plan/routines-rebuild-parts/otis.md (525 lines)*
- *Freya (session/lie-hunter): the Lie #13 kill proof + demands +
  acceptance — plan/routines-rebuild-parts/freya.md (441 lines)*

*Status: READY TO BUILD the hour the token build (7→8) is merged.
Nothing below waits on a question. Where seats disagreed or a flag
crossed files, the coordinator's arbitration is recorded in §2 and is
part of the brief.*

---

## 1. The build in one paragraph

Routine window state becomes a **pure function** — `RoutineDerive.derive
(schedule rows, now, active session, recent session rows, settings)` —
that writes nothing. A single funnel, `RoutineHeartbeat.tick(source)`,
is the only mover: it captures one clock, reads the rows inside a
Mutex+coalescing guard, evaluates derive(), and dispatches only
idempotent transitions (materialise-at-remainder by the natural key
`(routineId, windowStartMillis)`; finalise through the one booking
funnel; rearm the one chain alarm). Alarms are demoted to content-free
hints that may only force a tick. Booking is evidence-priced at
finalise from stored evidence timestamps and can never exceed them —
Lie #13's writer (an event trusted as a fact + elapsed derived from
clocks at booking + completion asserted by the caller) no longer
exists. Migration(8,9) is schema-only; the reseed is a version-blind
data transform in seed-sync under its flag.

## 2. Coordinator's arbitrations (binding on the builder)

- **A1 — Otis's tick stamp rides 8→9.** `guard_settings` gains a fifth
  column: `lastRoutineTickMillis INTEGER NOT NULL DEFAULT 0`, stamped
  by the funnel at the end of every completed tick. Rurik's census is
  thereby **16 ADD COLUMNs** (routines 2, focus_sessions 8 incl.
  Rurik's `taskId`, focus_tasks 1 — Nadia's `startedAtSessionElapsedSec`,
  guard_settings 5). Without it routines would ship a full build with
  no heartbeat-absence detection; the 9→10 readiness log later becomes
  this scalar's backing store (~6 lines, Otis §6 contract).
- **A2 — Finalise assigns in EVERY arm (Freya D4 over Sten §1.6.2's
  ATTENDED wording).** `elapsedSeconds := attendedSeconds` (capped at
  the window) for ATTENDED, PARTIAL and MISSED alike — never "verified
  elapsed stands." Consequence: Freya's invariant `booked ≤
  evidence-accrued` holds exactly, with no threshold slack, and her
  audit test T10 is an equality-grade check.
- **A3 — Legacy routine rows: identity vs totals split (Freya D9 vs
  Rurik/Sten).** `routineId = NULL` still means "not a rebuild routine
  session" for all matching (no backfill, no re-judging — that half
  stands). But for TOTALS, pre-8→9 routine rows — recognisable as
  `routineId IS NULL AND profileName LIKE 'Routine: %' AND
  windowStartMillis = 0` — are LEGACY: excluded from every history/
  Ledger sum, never stamped, and named as excluded-legacy in the audit
  report. Excluding is not certifying; nothing about them is rewritten.
- **A4 — D3 vs §1.6.1: no conflict, recorded so nobody "simplifies."**
  Freya's D3 expressly permits accrual computed as coverage over
  *stored* evidence timestamps; Sten's §1.6.1 union-of-5-minute-
  coverages is exactly that. The forbidden form — a timestamp
  *difference* spanning an unsampled interval — appears nowhere and
  must not be introduced as an optimisation in either direction.
- **A5 — Pause expiries are chain boundaries (Otis §2.4 amendment,
  accepted).** The chain enumerator's minimum includes each enabled
  routine's `pausedUntilMillis` when it falls inside an otherwise open
  window (+~5 lines inside Sten's scheduler rewrite, already in his
  price's margin). Test-date rollover needs no boundary: suppression
  lives inside the schedule functions.
- **A6 — The GuardSettings REPLACE-write is a build task, not a
  wrinkle.** `MonolithRepository.setBrowserBlockerEnabled` currently
  REPLACE-writes a fresh `GuardSettingsEntity(...)`; after 8→9 that
  would silently reset `routineReseedV1Done`, `lastBootAtMillis`,
  `testDateEpochDay` and `lastRoutineTickMillis` to defaults. The
  build changes it to copy the existing row. Acceptance: toggling the
  browser blocker after the reseed leaves all four fields intact.

Two reconciliations that needed no ruling, stated so the builder
doesn't re-litigate them: the Shield loop reaches derive() **through
the funnel** (Otis §2.3 cadence: iteration 1, 30s throttle, immediate
on the completion edge) — Sten's `deriveOne` is the evaluation the
funnel performs for the active routine, not a second door; and the
token build's interim routine branch (`disarmRoutineSessionByBreak`,
which books tick-accrued elapsed only) is **replaced** by Sten's
§1.6.2 finalise funnel, with the `disarmActiveSession` head guard
routing TOKEN-SPEC §4.1's dispatch into it — TOKEN-SPEC test B3
(§8 item 9) is the acceptance for that seam.

## 3. Build sequence (order matters where stated)

1. **Migration(8,9)** — Rurik's Kotlin block verbatim (16 columns per
   A1), registered `.addMigrations(MIGRATION_7_8, MIGRATION_8_9)`, no
   fallback line (already deleted by the token build — verify at
   merge per Otis §6, don't trust). Entities gain the fields. A6's
   copy-fix lands in this step.
2. **`RoutineDerive`** (Sten §1) + the `RoutineSchedule` helpers
   (windowContaining, suppression inside the four named consumers,
   effectiveNextStartMillis, the 30..1080 clamp unify — the clamp
   lands with or before step 5, never after).
3. **Repository funnels** — materialise (natural key, snapshot columns
   at insert), finalise (Sten §1.6.2 as amended by A2), the evidence
   writer (E4 hold-start stamp, Sten §2.4), the two fences
   (disarmActiveSession head guard; updateSessionProgress completion
   fence), the compute7DayHistory exclusions (unfinalised routine rows
   + A3 legacy rows).
4. **Heartbeat wiring** (Otis §§1–4) — `RoutineHeartbeat.tick`, the
   receiver as hint shell calling tick(CHAIN_ALARM), the chain
   scheduler (Sten §2.1 + A5), boot receiver ordering + TIME_CHANGED/
   TIMEZONE actions, manifest deltas (+2 actions, directBootAware on
   the alarm receiver), Shield/A11Y/Listener tick calls at Otis's
   cadences. N1–N6 prohibitions are acceptance-checked (Otis T9 is a
   grep test).
5. **Reseed** (Sten §2.5) — version-blind transform in
   ensureSeededAndSyncPackages under seedMutex, exact-match S1/S2 →
   DAY BLOCKS 09:00–19:30 + SLEEP 20:00–03:30 (ids preserved, enabled
   carried), FASTED BLOCK 05:00–08:00 inserted, edited rows untouched,
   legacy-alarm cleanup, flag set last; fresh installs seed the three
   targets directly. Targets: FASTED BLOCK 05:00–08:00 DAILY·STUDY ·
   DAY BLOCKS 09:00–19:30 DAILY·STUDY · SLEEP 20:00–03:30 DAILY·SLEEP.
6. **Triggers** (Sten §1.8 sites 3–6 via the funnel) + the reset-note
   strip (§2.5, told once).

## 4. What dies

Lie #13, both heads: the boundary-event booking (750 manufactured
min/weekday — 240 STUDY + 510 SLEEP booked as focus; 4,770 min/week
on an untouched phone) and its category fraud. The proof is Freya §3:
a write-set argument (booked ≤ accrued for every routine row, by
construction of the only writer) plus five adversarial scenarios
(killed END hint, killed service, reboot across a boundary, clock
forward, duplicate/late hints), each constructed and killed. The one
survivor is named and accepted: **presence is not study** — evidence
proves a person was there (≤5 min per evidence instant), never that
they studied; that is the definition of attendance, not a fabrication
path. Also dead: the per-routine boundary alarms and their request-
code arithmetic, `routine_attendance` (never created), the rename
fence, pending-delete, reseed postponement, MISSED_AUTOPAUSED.

## 5. Price (consolidated)

Sten ≈ +680 / −90 across ~10 files incl. ~180 lines of tests · Otis
delta ≈ +190, 1 new file, 7 files touched · Rurik ≈ 35 lines + ~120
lines of chain tests · Freya's suite folds into Sten's test count
except T10's property/audit test (builder prices it inside the
finalise work). **Total ≈ +1,050 lines, ~2 focused build days** (Sten's
estimate governs; Otis/Rurik deltas ride inside it). Zero new tables,
permissions, alarms beyond the one chain, or timers.

## 6. Acceptance (consolidated — all must pass)

- TOKEN-SPEC §8 items 8 (migration chain: v7→v9 with routine + spend
  rows intact; v8→v9 with spends intact) and 9/B3 (routine break:
  spend booked against the session id, outcome per §5.4 via the
  finalise funnel inside the token transaction, no Partial Stamp,
  forced failure at finalise leaves no spend and a sealed session).
- Sten's eight: stale hint · duplicate wake · rename mid-window ·
  delete mid-window · ghost (MISSED, 0 booked, history unchanged) ·
  Saturday (suppression by date, SLEEP exempt, no judgement, streak
  unbroken) · eligibility (paused span mints no MISSEDs) · reseed
  (ids preserved, edited seed untouched + successor inserted, second
  run a no-op).
- Freya's twelve (freya.md §5), incl. T4 the reincarnation trap
  (live service, full window, zero evidence → booked 0) and T10 the
  standing history audit (`booked ≤ accrued` over every finalised
  routine row in a real post-rebuild database; legacy rows reported
  as excluded per A3).
- Otis's ten (otis.md §5), incl. the triple-wake race (exactly one
  session row, funnel passes ≤ 2), the pre-unlock LOCKED_BOOT pass,
  pause-expiry arming (A5), and the tick-purity grep (N-prohibitions).

## 7. Named limits (documented, not solved)

Doze punctuality without SCHEDULE_EXACT_ALARM (declined deliberately —
lateness is priced as remainder/LATE_FIRE, never hidden) · force-stop
resurrection (platform ceiling; the resume tick repairs, the gap books
only evidenced minutes) · E3 evidence association by timestamp-interval
∩ the one active session (InterceptLogEntity has no sessionId — the
weakest joint, workable because only one session is ever active) ·
one stored test date (a second date on a different day uses
pause-until, Sten §1.4).

— Voss, MaxImpl coordinator. Parts: sten.md · rurik.md · otis.md ·
freya.md in plan/routines-rebuild-parts/. Seat letters: mail/voss/
0806 (Freya), 0810 (Rurik), 0812 (Otis), 0812 (Sten).
