# ISSUE #46 — RETHINK EVIDENCE (analysis only, no cures decided)

*Compiled Oct 30, 2026, under Sarthak's freeze order (05:07 IST):
all routine feature work is frozen; the routine model is under
rethink. This document is EVIDENCE for that rethink — it proposes
no decision. Cure options are listed neutrally, with costs, only
where the rethink must judge them.*

*Code base for every citation:* `origin/main` @
**73aaeaef0e197187e0c0411bce42a6a8f0e43bec** (the base this branch
forked from; DB version 12, `MonolithDatabase.kt:25`). Prior
evidence folded in: `studio/plan/ROUTINE-NOFIRE-AUDIT.md` (Voss,
Oct 29, written against the batch-7 / DB-v10 trunk — its line
numbers differ from this base, its mechanisms re-verified here),
and the C5 read-only seat-bundle audit of #46 against `e29acdf`.

*Branch state, for the record:* this branch
(`agent/issue-46`) carries two commits made BEFORE the freeze
order arrived — `309f470` and `2ec4e39` — implementing the
setAlarmClock cure family (exact chain alarm, crash-safe receiver,
rearm-first heartbeat, tests). CI run 40976286444: assemble SUCCESS,
270 unit tests executed, new tests passing. **Under the freeze
these commits ship nothing; their disposition is POSTEN's call.**
They are useful to the rethink only as a priced, working example
of what curing the scheduler family alone looks like.

---

## A. The 20:00 SLEEP no-fire — the exact failure chain, link by link

The event (Oct 29): batch 7 installed ~17:19 IST; the SLEEP window
(20:00–03:30) should have materialised a session and sounded its
seal at 20:00. Sarthak's report at ~20:21: "No it didn't." No
session, no seal, no trace. The chain that had to work, end to end:

### Link 1 — Schedule truth (routine rows)
Room rows seeded by the reseed transform: FASTED 05:00–08:00,
DAY BLOCKS 09:00–19:30, SLEEP 20:00–03:30, all daily, all enabled
(`MonolithRepository.routineTargets()` / `reseedRoutinesV1()`,
Repository.kt:863; `routines` table shape at MonolithModels.kt:549).
**Status: sound as code; the device's actual rows were never
observed** (audit §L-D: the transform matches one exact legacy
row and skips the insert if any row is named "SLEEP" — device-only
fact, suspect, ranked low).

### Link 2 — The reseed is UI-gated
`reseedRoutinesV1()` is called only from
`ensureSeededAndSyncPackages()` (Repository.kt:192), whose only
callers are ViewModel paths (MonolithViewModel.kt:768 and :2451).
No background component reseeds. The reseed itself rearms the chain
(Repository.kt:911–913), but only after the UI has run at least
once since the flag was unset.
**Status: PROVEN WEAK (code-complete).** Arming the 20:00 boundary
depended, in part, on the user opening the app — for a feature
whose entire purpose is firing while the user does nothing.

### Link 3 — Arming: the chain's only spontaneous path is a tick
The single chain alarm is armed inside `RoutineHeartbeat` ticks
(or the UI reseed above). Every trigger funnels into `tick()`:
chain alarm, boot / package-replaced (MonolithBootReceiver.kt,
tick at its onReceive), clock change, forced RESUME ticks, routine
CRUD, session edges, the shield loop's cadence, and 30 s-throttled
opportunistic ticks from the accessibility service and the
notification listener (17 heartbeat call sites across 8 files).
Between install (17:19) and 20:00 the only *spontaneous* trigger
was MY_PACKAGE_REPLACED. OEM autostart managers can swallow that
process start silently; the receiver must also open Room (running
migrations) inside goAsync()'s ~10 s budget.
**Status: PROVEN FRAGILE as a design (single point of failure);
whether it failed that night is device-only (audit §L-B, §4).**

### Link 4 — The alarm itself is inexact
`RoutineScheduler.rearmChain` (RoutineScheduler.kt:41–46) arms with
`setAndAllowWhileIdle(RTC_WAKEUP, boundary, intent)` — batched
into Doze maintenance windows, quota-limited in deep Doze. No
exact-alarm surface exists anywhere on this base: no
`setExact*`/`setAlarmClock`/`canScheduleExactAlarms` in
`app/src/main`, and the manifest declares neither
`SCHEDULE_EXACT_ALARM` nor `USE_EXACT_ALARM`. That night's chain
also needed **two hops** (19:30 DAY BLOCKS end, rearm, then 20:00),
so a deferred first hop deferred the second's *arming*, not just
its delivery.
**Status: PROVEN WEAK (code).** By itself it predicts *late*, not
*never* — audit §L-C ranks it third for that night (the phone was
awake in his hand from ~20:09).

### Link 5 — Receiver
`RoutineAlarmReceiver` (RoutineAlarmReceiver.kt:21–30): `goAsync()`
→ a **bare** `CoroutineScope(Dispatchers.IO).launch` →
`RoutineHeartbeat.tick(CHAIN_ALARM, force = true)`, `finish()` in a
finally. No SupervisorJob, no exception handler: any exception in
the tick is an uncaught background-broadcast crash.
**Status: PROVEN WEAK (code) as a robustness fact; no evidence it
fired that night.**

### Link 6 — Heartbeat reconcile
`RoutineHeartbeat.reconcileOnce` (RoutineHeartbeat.kt:~99–188):
read rows → `RoutineDerive.derive()` (pure) → finalise due →
SLEEP stand-down → materialise at most one desired session →
launch its proving off-pass → shield bookkeeping → **rearm chain
(step 5 of 6, :173–184; the rearm call at :181)** → liveness stamp
`stampLastRoutineTick` (:187). Two structural facts:
- The chain's renewal is the **last** substantive step of the very
  work the chain exists to enable — any exception in steps 1–4
  leaves the chain unarmed (see §C, the self-hosting fragility).
- Step 5 sets `lastArmedBoundaryMillis = boundary` (:183)
  **unconditionally**, immediately after a `rearmChain` that can
  silently no-op (Link 4 / Finding 4). The system records "armed"
  for arms that never happened.
**Status: PROVEN WEAK (code), both facts.**

### Link 7 — Materialise
`MonolithRepository.materialiseRoutineSession` (Repository.kt:1086):
apply loadout → `startSession(...)` inserts the session **born
active** with frozen window stamps → `MonolithShieldService.startShield`.
**Status: sound as far as it goes; its shield start is Link 8's
problem.**

### Link 8 — Shield start can be silently refused
`MonolithShieldService.startShield` (MonolithShieldService.kt:293):
`startForegroundService` wrapped in `catch (_: Exception)` —
"Safe ignore in test environments" (:303–304), a second catch-all
at :314–315. On Android 12+ (targetSdk 36) a background FGS start
is denied unless an exemption applies — and an **inexact**
allowWhileIdle alarm confers none (exact alarms / alarm clocks
do). The denial exception is swallowed by design.
**Status: PROVEN WEAK (code): the refusal is real, silent, and
unrecorded.**

### Link 9 — The seal: a 1,500 ms witness race that deletes on loss
`SessionSealTransition.prove` (SessionSealTransition.kt:200;
`proveAndSeal` :178): three witnesses re-read live — L1 the session
row active; L2 the shield service alive **in this process**,
protecting this session; L3 the accessibility service bound and
armed on this session. Jam (one shield re-dispatch) at latch+500.
**Annul at latch+1500** (`ANNUL_MS`, :279–280): if any witness is
dark, `annulProvingSession` **deletes the session row** (:249) and
a tombstone (:251–252) bars re-materialising that window for the
process's life (`isTombstoned`, checked by the heartbeat before
materialising). The heartbeat launches the proving off-pass and
**ignores its verdict**. The SLEEP 20:00 case starts this race
from a background cold start, needing two external services alive
and armed within 1.5 s.
**Status: PROVEN WEAK (code) — and the audit's ranked #1 cause for
that night (§L-A): it needs only one 1.5-second race lost, and its
failure mode deletes the evidence, producing exactly the observed
"nothing, anywhere."**

### The three cause families (the rethink must judge all three)
Per ROUTINE-NOFIRE-AUDIT's ranking, #46's five findings cover
only family #3:
1. **Seal/witness machinery** — Link 8+9: session fired, then
   annulled and deleted by its own proving. (No #46 finding.)
2. **Arming provenance** — Link 2+3: chain never armed because
   arming depended on UI/boot-receiver luck. (No #46 finding.)
3. **Scheduler chain** — Links 4+5+6: inexact alarm, crash-prone
   receiver, silent failures, rearm-last ordering. (**Issue #46.**)

What only the phone can settle (audit §4, still open): whether he
opened Monolith between 17:19 and 20:00; OEM + autostart/battery
state; the stored routine rows; a11y/listener on/off; whether
`lastRoutineTickMillis` ≈ 20:00 with no SLEEP row (which would
prove Link 9 over Links 2–3).

---

## B. What the current machinery costs

**Components (main sources, this base):**
- `RoutineScheduler.kt` — the single chain alarm + legacy-alarm
  cancellation (shipped per-routine alarms reproduced only to be
  cancelled).
- `RoutineAlarmReceiver.kt` — chain receiver (manifest-declared,
  directBootAware).
- `RoutineHeartbeat.kt` — 189 lines; the single reconcile funnel,
  its coalescing mutex/pending-again, opportunistic throttle, and
  the in-memory `lastArmedBoundaryMillis`.
- `RoutineDerive` / `RoutineSchedule` (data layer, pure) — window
  math, judgements (ATTENDED/PARTIAL/MISSED/UNKNOWN…), eligibility
  horizons, orphan detection.
- `SessionSealTransition.kt` — the proving cascade (3 witnesses,
  jam, annul, tombstones) shared with manual arms.
- `MonolithRepository` routine surface — reseed transform,
  `materialiseRoutineSession`, `finaliseRoutineSession`,
  `annulProvingSession`, history computation over routine rows.
- Trigger plumbing in 8 files: boot receiver, accessibility
  service, notification listener, shield service loop, ViewModel
  (resume/CRUD/session edges), ArmProvingController.

**Data cost:**
- Table `routines` (MonolithModels.kt:549): id, name,
  startMinutes, endMinutes, daysMask, loadoutName, enabled,
  pausedUntilMillis, scheduleEditedAtMillis.
- Table `focus_sessions` carries the routine machinery's columns
  (MonolithModels.kt:267, :309–317): routineId (nullable),
  windowStartMillis, windowEndMillis, routineOutcome,
  evidenceMask — plus finalise/attendance evidence in
  `intercept_logs`.
- Table `guard_settings` carries `lastBootAtMillis` (:755) and
  `lastRoutineTickMillis` (:760) — the funnel's liveness stamps.
- DB is at version 12 largely *because* of this machinery's
  migrations (8→9 routines rebuild, 11→12 taken column, etc.).

**Permissions / platform surface:** RECEIVE_BOOT_COMPLETED,
FOREGROUND_SERVICE(+SPECIAL_USE), POST_NOTIFICATIONS,
SYSTEM_ALERT_WINDOW, PACKAGE_USAGE_STATS, VIBRATE — and the
*absence* of any exact-alarm permission (see Finding 1). Plus two
bound external services the seal depends on (accessibility +
notification listener) and a foreground service that must be
startable from the background.

**Background-work cost:** one always-renewed alarm chain; a
reconcile on every boot, package replace, clock change, resume,
CRUD, session edge, shield-loop cadence, and throttled
opportunistic wakes from two always-on services. Every tick reads
the full routine/session snapshot set and runs the pure derive.

**Unmerged prior work (the NOFIRE lane):**
`~/workspace/clone-inspect/builds/nofire/Focus-app/` — a local-only
lane with written fixes (USE_EXACT_ALARM + guarded
`setExactAndAllowWhileIdle` `armAt()`, ST-06 pre-arm knock alarm at
−60 s, adopt-not-annul, reseed inside reconcileOnce). Its
`proof.log` ends in BUILD FAILED lines (Oct 29, RAM/write-wall
era): **DECLARED, never compiled, no CI verdict.** Even if merged
as-is it would leave: a silent inexact fallback with no denial
stamp; rearm still at step 5; receiver still bare-scope; silent
scheduler returns kept; no scheduler/receiver tests (heartbeat
tests only). It is evidence of a *partial* cure family's shape,
not a candidate to adopt blind.

---

## C. What the model assumes — and what the no-fire falsified

1. **"Alarms are hints, never the mechanism"** (RoutineScheduler's
   own header). The design assumes some *other* trigger will always
   reconcile in time. **Falsified for the idle phone:** every
   non-alarm trigger requires the user to do something (open the
   app, reboot, interact) or a service to wake opportunistically.
   For a phone sitting untouched at 19:59, the alarm **is** the
   mechanism — the model's central sentence is false exactly in
   the case the feature exists for. (C5 bundle, structural
   critique.)
2. **Self-hosting renewal.** The chain is a single one-shot token
   whose renewal is the *last* step of the work it enables
   (Link 6). It assumes every reconcile completes. One exception,
   one killed process mid-tick, one swallowed start — and the
   token is gone with no external re-armer. **Falsified as a
   robustness assumption by construction** (Links 5–6); whether
   it is what failed that night is unproven.
3. **Background starts are available.** The tick → materialise →
   `startForegroundService` path assumes Android 12+ background
   FGS restrictions won't bite, or that swallowing the denial is
   acceptable. **Falsified:** the denial path exists, is silent,
   and feeds Link 9's annulment.
4. **Two external services can witness within 1,500 ms of a cold
   background start.** The seal assumes the shield service and the
   accessibility service are alive, bound, and armed near-
   instantly — including right after an app update, when a11y
   rebinding is OEM-dependent. **The audit's ranked #1 falsified
   assumption for the 20:00 event.**
5. **The user opens the app.** Reseed and several reconcile
   triggers are UI-gated (Link 2–3); the model quietly assumes
   regular app opens keep the machinery converged. A discipline
   tool whose user is succeeding *by not touching his phone*
   violates this assumption by succeeding.
6. **OEM behaviour is stock-Android behaviour.** Autostart
   managers (MIUI/ColorOS/Funtouch class), battery optimisation,
   and force-stop semantics are assumed away. Unverifiable from
   code; the device facts in §A remain open.
7. **Deleting the evidence is safe.** Annulment deletes the row
   and tombstones the window, assuming a failed proving means the
   session should never have existed. The no-fire showed the cost:
   after the failure there was *nothing to inspect* — no row, no
   stamp, no log — which is why a one-line user report ("No it
   didn't") cannot be split among three cause families without
   device archaeology.
8. **Late is acceptable.** `setAndAllowWhileIdle` assumes Doze
   deferral is a tolerable price for a *user-asked clock time*.
   The product assumption underneath — a 20:00 SLEEP routine is a
   promise about 20:00 — was never the scheduler's to relax.

Assumptions still standing (checked, not falsified): day-mask and
timezone arithmetic for that night; PendingIntent identity/flags;
receiver manifest shape; SLEEP's never-finalise (J2) semantics;
the pure derive's window/judgement math (heavily unit-tested).

---

## D. The five findings of issue #46 — disposition as evidence

**F1 [CRITICAL] — Chain alarm is inexact; no exact-alarm
permission.** **VERIFIED on main.** `setAndAllowWhileIdle` at
RoutineScheduler.kt:41–46 (arm at :46 in the base read; the
audit's :43 citation is the same statement on the older tree);
zero exact-alarm API use in `app/src/main`; neither
SCHEDULE_EXACT_ALARM nor USE_EXACT_ALARM in the manifest.
*Partially addressed, unverified, in the nofire lane only*
(USE_EXACT_ALARM + guarded exact arm — never compiled).
*Cure options for the rethink to judge, neutrally:* (i) alarm-clock
arming (SCHEDULE_EXACT_ALARM, user-revocable, user-visible alarm
affordance) — strongest exemption, new visible system surface;
(ii) exact-and-allow-while-idle (USE_EXACT_ALARM, auto-granted,
no visible affordance; SCHEDULE_EXACT_ALARM variant is revocable
and needs a denial path); (iii) keep inexact and accept lateness
as model truth. Each also changes Link 8 (exact delivery is an
FGS-start exemption; inexact is not).

**F2 [HIGH] — Receiver runs the reconcile inside goAsync()'s
~10 s budget; rearm is the last step, so a slow/killed tick leaves
the chain unarmed.** **VERIFIED everywhere (main + nofire).**
Rearm is step 5 of 6 in `reconcileOnce` (RoutineHeartbeat.kt:173–
184; stamp step 6 at :187); everything fallible — DB reads,
finalise, materialise, seal launch, shield stop — runs before it.
The nofire lane's reseed-in-reconcile does not move the rearm.
*Options:* rearm-first ordering (arm from the routines snapshot
before session work); an arm path independent of the reconcile
entirely (e.g. arm-at-schedule-edit + arm-at-boot only); or a
watchdog trigger outside the chain. Costs differ mainly in how
much of the funnel model survives.

**F3 [HIGH] — Any exception in the reconcile crashes the app from
a background broadcast; receiver scope has no supervisor/handler.**
**VERIFIED everywhere.** Bare `CoroutineScope(Dispatchers.IO)` at
RoutineAlarmReceiver.kt:21–30 (launch at :24). The SupervisorJob
inside RoutineHeartbeat protects only its `tickAsync` callers —
the receiver calls the suspending `tick` directly, outside it.
*Options:* supervised scope + handler at the receiver (small,
local); moving all entry points behind the heartbeat's own scope
(structural); both leave the question of what a *failed* tick
should record (see F4).

**F4 [MEDIUM] — Scheduler fails silently; heartbeat believes it.**
**VERIFIED everywhere.** RoutineScheduler.kt:41–42 (base read:
:42–43): null AlarmManager or null PendingIntent → bare `return`,
no log, no result. RoutineHeartbeat.kt:183 then sets
`lastArmedBoundaryMillis` unconditionally — the in-memory record
claims an arm the platform never received, and because the
recorded boundary now "matches", only the FLAG_NO_CREATE probe
stands between that lie and a permanently unretried chain.
*Options:* a result type returned by the scheduler and honoured
by the heartbeat (armed/degraded/failed recorded distinctly);
exception-propagating scheduler with caller-side handling; or
probe-verified recording (re-check armed state after arming).
The degraded-arm question — record it as armed, or retry
forever — is a model judgement, not an implementation detail.

**F5 [MEDIUM] — Zero test coverage for scheduler or receiver.**
**VERIFIED on main.** No scheduler/receiver test exists under
`app/src/test` on this base (routine coverage is derive/finalise/
reseed/migration only). The nofire lane adds heartbeat tests only
— still no scheduler/receiver coverage, and uncompiled besides.
*Options:* pure-decision extraction + JVM tests (cheap, limited
to decision logic); Robolectric shadow-AlarmManager tests
(closer to the platform, slower, SDK-sensitive — this repo's
Robolectric classes have their own JDK/SDK history); source
tripwires (weakest, but they catch silent regressions of exactly
the F1 kind).

---

## E. What this document does not decide

Whether the routine model survives at all; whether the seal's
witness design, the UI-gated reseed, or the scheduler family is
the rethink's first target; and the disposition of this branch's
two frozen pre-freeze commits (`309f470`, `2ec4e39`) and of the
nofire lane. Issue #46 stays **open, RETHINK-PENDING**. The
device-only questions in §A (audit §4) remain the cheapest
evidence nobody has collected yet.

— C5 seat (Wave-3), Oct 30, 2026
