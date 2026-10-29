# ROUTINE NO-FIRE AUDIT — the 20:00 SLEEP that never fired

*Commissioned by Ren, Oct 29, 2026, after the bench verdict: batch 7
(DB v10, installed ~17:19 IST) was on Sarthak's phone; the SLEEP
window opened 20:00 the same evening; nothing fired, no seal
sounded. His report: "No it didn't." Audited by Voss (MaxImpl),
code-reading only, merged trunk `~/workspace/clone-inspect/Focus-app`.
No lane, no gradle, nothing built — a build gate is in force and the
fix spec in §5 joins the gated build.*

**Ranked verdict, up front:**

1. **Most likely — the seal annulled its own session.** Even with a
   perfect 20:00 alarm, the routine seal gives two external services
   **1,500 ms from a background cold start** to be alive in-process
   and armed on the newborn session; miss the deadline and the
   session row is **deleted** and its window tombstoned. One of the
   two witnesses (the shield service) can be silently refused its
   background start, and the refusal is swallowed by a catch-all.
   The failure mode is *designed* to leave exactly what he reported:
   nothing, anywhere.
2. **Also live — the chain was never armed.** Arming exists only
   inside a heartbeat tick or a UI-only reseed; between install
   (17:19) and 20:00 the only spontaneous trigger was the
   package-replaced broadcast. If the OEM swallowed that process
   start and he never opened the app, no 20:00 alarm ever existed.
3. **Possible but weaker — the inexact alarm was still pending** at
   his 20:21 report. The chain uses `setAndAllowWhileIdle` — Doze
   defers it; tonight's chain also needed *two* hops (19:30, then
   20:00). Deferral produces late, not never; it settles itself the
   moment the app is opened (see §4.3).

---

## 1. The chain, link by link, as built

1. **Schedule truth** — Room routine rows: FASTED 05:00–08:00,
   DAY BLOCKS 09:00–19:30, SLEEP 20:00–03:30, all daily, all
   enabled (`MonolithRepository.routineTargets()`; reseed transform
   `reseedRoutinesV1()`, Repository.kt:796).
2. **The reseed is UI-gated.** Its only caller is
   `ensureSeededAndSyncPackages()` (Repository.kt:167, reseed at
   :188), invoked only from `MonolithViewModel` init
   (MonolithViewModel.kt:633) and `refreshSystemGuard()` (:2199).
   No background component ever reseeds. The reseed itself rearms
   the chain (Repository.kt:844–846) — but only when the app's UI
   has run at least once since the flag was unset.
3. **Arming** — `RoutineHeartbeat.reconcileOnce` step 5
   (RoutineHeartbeat.kt:173–183): whenever derive's next boundary
   differs from the in-memory last-armed value, or the chain
   PendingIntent is missing, it rearms the single chain alarm.
   Every trigger funnels into the same tick: chain alarm, boot /
   package-replaced (MonolithBootReceiver.kt:34–47), forced RESUME
   ticks (ViewModel :636, :1995), routine CRUD (:1307), session
   edges, the shield loop's cadence, and 30 s-throttled
   opportunistic ticks from the accessibility service and
   notification listener.
4. **The alarm API is inexact.** `RoutineScheduler.rearmChain`
   (RoutineScheduler.kt:41–46):
   `setAndAllowWhileIdle(RTC_WAKEUP, boundary, intent)` — batched
   into Doze maintenance windows, quota-limited in deep Doze.
   **Zero exact-alarm surface exists in the app**: grep for
   `setExact*` / `setAlarmClock` / `canScheduleExactAlarms` across
   `app/src/main` returns 0 hits, and the manifest declares neither
   `SCHEDULE_EXACT_ALARM` nor `USE_EXACT_ALARM`. PendingIntent
   identity is sound (explicit component intent, request code
   `0x5201`, `FLAG_UPDATE_CURRENT | FLAG_IMMUTABLE`).
5. **The receiver** — `RoutineAlarmReceiver` (manifest:
   `exported="false"`, `directBootAware="true"` — correct for an
   explicit-component intent) → `goAsync()` →
   `RoutineHeartbeat.tick(CHAIN_ALARM, force = true)`
   (RoutineAlarmReceiver.kt:22–31).
6. **The tick** (RoutineHeartbeat.kt:99–186): read rows →
   `RoutineDerive.derive()` (pure) → finalise due sessions → SLEEP
   stand-down check → **materialise** at most one desired session →
   launch its proving off-pass → shield bookkeeping → rearm chain →
   stamp `lastRoutineTickMillis` (MonolithDao.kt:263 — a liveness
   fact written on every completed pass).
7. **Materialise** (Repository.kt:983–998): apply the loadout,
   `startSession(...)` inserts the session **born-active** with the
   frozen window stamps, then `MonolithShieldService.startShield`.
8. **The seal** (SessionSealTransition.kt, `prove()` :186–262):
   three witnesses, re-read live every 25 ms —
   **L1** the session row active;
   **L2** `MonolithShieldService.instance != null && protecting &&
   protectingSessionId == sessionId`, *in this process* (:146–154);
   **L3** `MonolithAccessibilityService.instance != null &&
   armedSessionId == sessionId` (:156–162; the a11y service arms
   from a Room Flow collector inside itself,
   MonolithAccessibilityService.kt:87–101).
   t=0 = max(last witness confirm, latch + 210 ms). Jam at
   latch+500 (one shield re-dispatch). **Annul at latch+1500:
   `annulProvingSession` deletes the row and a tombstone
   (routineId, windowStartMillis) bars re-materialising that window
   for the rest of the process's life** (:232–248). The heartbeat
   launches the proving and **ignores its verdict entirely** —
   "its verdict needs no witness here" (RoutineHeartbeat.kt:158–161).
   Nothing ever retries the seal inside the window.
9. **The 03:30 boundary** — any tick inside the SLEEP window offers
   the containing window's end as the next boundary
   (RoutineDerive.nextBoundary), so the chain wakes at 03:30; the
   tick's step 2 stands the SLEEP session down by stored window end
   — deactivated, **unstamped, no finalise, no receipt** (the J2
   law) — and step 4 stops the shield. Overnight math is verified:
   `windowContaining` checks yesterday's and today's starts
   (MonolithModels.kt:580–601); SLEEP duration = 210 − 1200 + 1440
   = 450 min, inside the 30..1080 clamp; day bits Mon=0..Sun=6 with
   ALL_DAYS=127 make the mask a non-issue tonight.

## 2. Every silent-failure link, ranked for tonight's facts

Tonight's facts: **update** install ~17:19 over batch 6; first
window 20:00 the same evening; the phone was in his hand by 20:09
(his messages); no evidence Monolith itself was opened after the
install.

### L-A — Proving annulment deletes the fired session (MOST LIKELY)

Mechanism, end to end in shipped code: the 20:00 tick materialises
SLEEP and launches `proveAndSeal`. L2 requires the shield **service
object alive in the tick's process**, already protecting the new
session. The service is started via `startForegroundService`
(MonolithShieldService.kt:285–298). On Android 12+ (targetSdk 36)
a background FGS start is denied unless an exemption applies — and
an **inexact** allowWhileIdle alarm confers none (exact alarms and
alarm-clocks do). The denial throws
`ForegroundServiceStartNotAllowedException` — into a
**catch-all that swallows every exception** ("Safe ignore in test
environments", :295–297). The +500 ms jam re-dispatch hits the same
wall, also silently. L3 independently requires the accessibility
service bound and alive; post-update rebinding is OEM-dependent
and silent when it doesn't happen. At latch+1500 with either
witness dark: row deleted, tombstone set, shield stopped. **The
user-visible result is precisely "nothing fired, no seal sounded,"
and the deletion destroys the evidence by design.**
*Code proves:* the 1500 ms deadline, the delete-on-timeout, the
swallowed start, the verdict nobody reads. *Only the device can
prove:* whether it happened tonight (corroboration: the DB's
`lastRoutineTickMillis` ≈ 20:00 with no SLEEP row existing).

### L-B — No tick ran between install and 20:00; the chain never existed

Arming happens only inside a tick (§1.3) or the UI-gated reseed
(§1.2). Spontaneous triggers in the 17:19→20:00 window: exactly
one — `MY_PACKAGE_REPLACED` (MonolithBootReceiver). Two silent
ways it dies: (i) OEM autostart managers (MIUI / ColorOS /
Funtouch-class) block background receiver process starts for
non-whitelisted apps with no user-facing trace; (ii) the
receiver's `goAsync` coroutine must open Room — running the 9→10
migration on first open — inside the broadcast's ~10 s budget on a
contended box. If both that and every opportunistic tick failed
*and* he never opened Monolith, no alarm was ever set.
*Why it ranks below L-A:* alarms survive package updates, and the
self-rearming design self-heals from any single live hop (a
surviving 19:30 alarm fires into the new receiver and rearms
20:00). Total silence by this route needs *every* hop dead;
L-A needs one 1.5-second race lost.
*Code proves:* the trigger inventory is complete — there is no
other arming path. *Only the device can prove:* OEM brand +
autostart/battery state, and whether he opened the app (§4.1–4.2).

### L-C — Inexact delivery deferred past his report

`setAndAllowWhileIdle` in Doze fires inside maintenance windows
only (≈1 per 9 min per app in deep Doze). Tonight's chain needed
two hops — 19:30 (DAY BLOCKS end) then a rearm to 20:00 — so a
deferred first hop defers the second's *arming*, not just its
delivery. This yields a **late** fire, and a tick landing inside
the window at any point still materialises the session at
remainder (RoutineDerive, OPEN_IDLE). Weakened by the phone being
awake in his hand from ~20:09 (screen-on opens maintenance
windows), so a still-pending 20:00 alarm at 20:21 is possible but
not probable. *Settles itself:* §4.3.

### L-D — His stored rows never matched the reseed (device-only)

The S2 transform matches only an exact
("Night Wind-Down", 1350, 420, ALL_DAYS, SLEEP) row; the
missing-target insert is skipped if **any** row carries the exact
name "SLEEP" (Repository.kt:796–843). His DB accumulated since the
v7 era; trunk renders prove the seeds only on a *fresh* database.
Ranked low — routines were never hand-editable before batch 8, so
pristine seeds are expected — but his actual rows (names, minutes,
enabled) are readable only on his phone (§4.4).

### L-E — A held window: something else stood active at 20:00

Derive refuses to materialise into an occupied span (OPEN_HELD,
RoutineDerive.kt deriveOne). A DAY session would be finalised by
the same tick before materialising (step order saves it), so this
needs a *manual* session left running or an unfinalisable orphan.
No evidence for it; device-visible if true (§4.3's open shows a
running session instead).

### Excluded by the code (checked, not suspected)

- **Exact-alarm permission** — absent because never used; the
  absence is consistent, not itself the fault. It becomes the fix
  lever (F1).
- **PendingIntent flags / identity** — sound (§1.4).
- **Receiver export/manifest** — correct (§1.5).
- **Day-mask / timezone math** — verified by hand for Thu
  2026-10-29 20:00 IST (§1.9).
- **SLEEP never finalising** — by design (J2); the stand-down path
  exists and is armed by the same chain.
- **Battery exemption** — allowWhileIdle fires without it; its
  only effect is deferral, already counted in L-C.

## 3. What the report cannot distinguish

His one line — "No it didn't" — answers a two-part question
(started on time? made its sound?). Three different worlds produce
it: (a) no session exists (L-A after deletion, L-B, L-D);
(b) a session exists but never sealed/sounded (an annulment that
failed to delete is impossible — but a born-active row adopted
silently by `recover()` on a later open is not; also L-E);
(c) a session sealed but the phone was silenced. §4 is the
smallest set of questions that separates them.

## 4. Device-only facts — what only his phone can settle

1. **Did he open Monolith at all between installing batch 7
   (~17:19) and 20:00?** Splits L-B: one open guarantees a forced
   RESUME tick + reseed ran.
2. **Phone brand/OEM, and Monolith's autostart + battery setting**
   (Unrestricted vs Optimized). Splits L-B's OEM half.
3. **Open Monolith now and watch:** a forced tick runs on open.
   SLEEP session materialises on the spot → delivery/arming was
   the failure (L-B/L-C), derive is innocent. It appears then
   *vanishes within ~2 seconds* → **L-A reproduced live**. A
   different session already running → L-E.
4. **A Shield screenshot** — the three routine rows as stored
   (names, times, on/off). Settles L-D outright.
5. **System settings:** Monolith's accessibility service ON?
   Notification listener ON? Phone on silent / DND at 20:00?
   Separates world (c) from (a)/(b) in §3.
6. **Was the phone rebooted, or Monolith force-stopped / swiped
   from recents, between 17:19 and 20:00?** Force-stop cancels
   alarms and receivers until the next manual open — a complete
   L-B by his own hand, and nobody's bug.

## 5. Fix spec — for the gated build (nothing ships early)

**F1 — Exact chain alarm** (kills L-C; supplies the exemption that
un-welds L-A's shield half). Declare `USE_EXACT_ALARM` in the
manifest (auto-granted; correct for a clock-core app, and this
build is sideloaded). In `RoutineScheduler.rearmChain`: when
`canScheduleExactAlarms()`, `setExactAndAllowWhileIdle`; else fall
back to the current call. Exact-alarm delivery is an Android 12+
FGS-start exemption, so the 20:00 tick may lawfully start the
shield. *Price: ~15 lines, RoutineScheduler.kt + 1 manifest line.*

**F2 — A routine proving may not delete on witness timeout**
(kills L-A). In `SessionSealTransition.prove`, for the routine
door: if the annul deadline arrives with L1 confirmed (the row is
ours and active), degrade to **adopt** — the semantics `recover()`
already applies to old rows — instead of `annulProvingSession` +
tombstone. Annulment remains only when L1 itself never confirms.
Plus, in `RoutineHeartbeat.reconcileOnce`: an active, unsealed
routine row that no proving owns gets its proving **relaunched**
on the next tick, so a lost race is retried, not entombed.
*Price: ~30–45 lines across SessionSealTransition.kt +
RoutineHeartbeat.kt, plus one regression test (witness-dark
proving leaves the session standing and seals on re-prove).*

**F3 — A denied shield start becomes a recorded fact** (supports
F2's diagnosis). `startShield`'s catch-all stamps a WatchStore
fact (`shieldStartDenied`, wall time) instead of swallowing into
silence; the Guard line can then name it. *Price: ~10 lines.*

**F4 — The reseed leaves the UI** (kills L-B's reseed half). Call
`reseedRoutinesV1()` from `reconcileOnce`'s setup (it is
idempotent and flag-gated) so boot / package-replaced ticks
converge the schedule with no UI involved. *Price: ~5 lines
moved, MonolithRepository/RoutineHeartbeat.*

**F5 — Print the chain's liveness** (makes the next failure
visible within a day, not at a failed 20:00). `lastRoutineTickMillis`
already exists; print it on Shield as "LAST ROUTINE CHECK HH:MM".
A dead chain becomes a stale timestamp he can see. *Price: ~10
lines + one render in the build's proof.*

Sequencing inside the gated build: F1 + F4 are independent; F2
depends on nothing but must land with its test; F3/F5 anywhere.
The 20:00 bench re-runs on the gated build's first night: the
verdict that closes this audit is his, on the phone.

— Voss, MaxImpl, Oct 29, 2026, ~20:27 IST
