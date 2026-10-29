# ROUTINES REBUILD — OTIS (MaxImpl, services/readiness)
## The services heartbeat, applied to routines: what ticks, what it hands derive(), what moves, and what death costs

*Seat brief: Sten owns `derive()` (Derive-Don't-Fire: window state =
f(schedule, now, session rows); alarms demoted to hints). This file owns
only the wiring underneath it — the heartbeat that calls it, the contract
at the seam, the death cases. Read-only evidence from the shipped tree
(`Focus-app/…`), my invention file
(`invention/maximpl-parts/09-otis.md` §§1–2, the Heartbeat Tap + Readiness
Log), and the strip as merged in `plan/BEYOND-deck.md` W4 (the 20s poll
and its "permission broadcasts" trigger die there; no `registerReceiver`
exists anywhere in the tree — grep, whole tree, zero hits).*

**Verdict up front — the uncomfortable one, priced below:** there is
**no always-on periodic heartbeat in the shipped app, and routines must
not invent one.** The only real loop (Shield, 2s) is deliberately dead
exactly when routines need their most important tick — *before* a window
opens, when no session exists. The heartbeat for routines is therefore
a **composition, not a timer**: (1) the Shield's existing 2s loop while
a session runs, (2) system-bound services' events opportunistically
while the phone is in use, (3) a closed, enumerated set of discrete
wakes — chain-alarm hint, boot, resume, CRUD, session edges, clock
change — while nothing runs. §1 proves the set is closed: routine state
can only change at moments one of those three lanes covers, so silence
between ticks is not absence of coverage, it is proof nothing changed.
A missed heartbeat is therefore always a **delay, never a corruption**
— because `derive()` re-reads rows, never increments them (§3).

---

## §1 — What the heartbeat IS in code (shipped mechanisms only, named)

### 1.1 The three real tick sources, with their actual periods

| # | Mechanism as shipped | Period / trigger | Alive when | Wakes it |
|---|---|---|---|---|
| H1 | `MonolithShieldService.startMonitoringLoop()` — `serviceScope.launch { while(isActive){ …; delay(2000L) } }`, `SupervisorJob + Dispatchers.IO`, service returns `START_STICKY`, foreground (`SPECIAL_USE`) | **2s**, fixed | **Only while a session is active.** First iteration self-terminates: `getActiveSessionSnapshot()==null‖!isActive → stopForeground+stopSelf+break` (`MonolithShieldService.kt:66–72`); also exits on session completion (`:75–80`) | `startForegroundService` from session start / boot receiver |
| H2 | `MonolithAccessibilityService.onAccessibilityEvent()` — per-event `scope.launch{}` reading DAO snapshots | **No period.** One launch per window/content event from any app; bursts under use, **zero events on an idle, screen-off phone** | Whenever the grant stands — system-bound, session or no session; manifest `directBootAware=true` | The system's accessibility manager (binding is Android's own poll — my Heartbeat Tap, invention §1) |
| H3 | `MonolithNotificationListener.onNotificationPosted()` | **No period.** Per posted notification | Whenever the grant stands, session or no session | The system's notification manager binding |

**What H1 already pays every 2s (the ride is free because the fare is
sunk):** one session snapshot read, `updateSessionProgress` (a session
*write* + HMAC verify), one full apps snapshot, one usage-stats
foreground query, one notification rewrite. Adding routines to this
loop is not a new cost centre; §2.3 prices the increment at <1% of the
loop's existing per-iteration work, throttled.

**What does NOT exist and must not be claimed:** no periodic work
outside a session (no WorkManager, no handler, no second service loop),
no `registerReceiver`/`ContentObserver` anywhere, no lifecycle hooks on
either bound service (`onServiceConnected`, `onListenerConnected`,
`onUnbind`, `onListenerDisconnected` are all absent from the shipped
files — the hooks my Readiness mechanisms wire). Any spec that says
"the heartbeat ticks every N outside a session" is describing a new
always-on process. This spec refuses to build one, and §1.2 is why it
doesn't need to.

### 1.2 The closed-transition-set theorem (why event+hint coverage is complete)

`WindowState = f(enabled, pausedUntil, daysMask, test-date suppression,
isActiveNow(routine, now), active?.routineId)` (Sten R1). Its inputs
can change only when:

| Input change | Covered by |
|---|---|
| (a) `now` crosses a computed boundary — window start, window end, `pausedUntilMillis` expiry (§2.4, my amendment), test-date day rollover *as evaluated inside the schedule functions* (suppressed starts are simply never returned, Sten §4) | The **one chain alarm** armed at `min(all such boundaries)` (Sten C1+C2) — firing = a hint that forces a tick (§2.2) |
| (b) Routine/settings rows change (CRUD, toggle, pause, reseed, test-date set) | The write funnel itself calls `tick(CRUD)` after commit (Sten §1.3 wiring, renamed §2.1) |
| (c) Session rows change: manual start, manual disarm, progress-completion | Start/disarm funnel calls `tick(SESSION_EDGE)`; completion happens *inside* H1, which ticks (§2.3) |
| (d) Wall clock / timezone changes — schedule is wall-clock, so a jump *is* a boundary crossing with no alarm able to predict it | **New:** `ACTION_TIME_CHANGED` + `ACTION_TIMEZONE_CHANGED` added to `MonolithBootReceiver`'s filter (+2 manifest lines; the receiver and its `goAsync` pattern already exist) → `tick(TIME_CHANGED)` |
| (e) Process/service death, reboot, Doze | Changes **no input** — only delays enforcement. Detected and repaired at the next wake (§3); boot is itself a wake |

There is no sixth row. Between covered moments the derived state is
provably constant, so a periodic out-of-session poll would spend
battery re-deriving a constant. **Rejected inventions, priced so the
refusal is a decision not an omission:** (i) keep Shield alive 24/7 as
an idle FGS heartbeat — persistent notification, Play FGS policy
exposure, and the service's own first loop iteration is *written* to
`stopSelf` with no session; overturning that is a product change, not
wiring: **rejected**. (ii) WorkManager periodic 15-min safety tick —
minimum period 15 min, deferrable by hours in Doze, buys exactly one
thing (self-absence detection) that §3.2's lazy detection already
provides free at every wake: **rejected for this build**, named as the
first belt to add if a device trial ever shows wakes not arriving.

### 1.3 Battery accounting, stated once

Zero new alarms (the chain is Sten's), zero new wakelocks, zero new
timers, zero network. H1's increment: throttled (§2.3). H2/H3
increments: gated by a monotonic compare (§2.3) — a rejected tick costs
one `AtomicLong` read. Discrete wakes: each already wakes the process
for its own existing job (boot receiver, resume observer, alarm
broadcast — the alarm's `RTC_WAKEUP` window is the system's, the tick
must finish inside the existing `goAsync` budget, §2.5). The heartbeat's
total new energy claim over a day of no sessions and no use is: **the
chain alarm's deliveries alone** — i.e. the doorbell Sten already
budgeted, nothing more.

---

## §2 — The wiring contract (the seam Sten's derive() plugs into)

### 2.1 One funnel: `RoutineHeartbeat.tick(source)`

New file `service/RoutineHeartbeat.kt` (~130 lines). **Every** heartbeat
source — all three lanes — calls this one function; nobody calls
`RoutineReconciler.reconcile()` directly. (Naming law: Sten's
*reconcile* is the derive+apply phase **inside** a tick; the tick is
the heartbeat's unit. His trigger list, R4, is this funnel's caller
list, verbatim plus `TIME_CHANGED`.)

```kotlin
enum class TickSource { SHIELD_LOOP, CHAIN_ALARM, BOOT, RESUME, CRUD,
                        SESSION_EDGE, A11Y_OPPORTUNISTIC, TIME_CHANGED,
                        PACKAGE_REPLACED }
object RoutineHeartbeat {
    suspend fun tick(source: TickSource, force: Boolean = false)
}
```

**Serialisation — the part the shipped code cannot survive without:**
`startSession()` (`MonolithRepository.kt:869`) does
`deactivateAllSessions()` + insert with no guard, and at least three
tick sources can fire in the same millisecond (chain hint + resume +
an accessibility event as he picks the phone up at a boundary). Two
concurrent materialisations = two session rows, one deactivated
mid-insert. The funnel therefore owns a **`Mutex` + coalescing flag**:
a tick arriving while one runs sets `pendingAgain` and returns; the
running tick loops once more if the flag is set. Ticks never queue
deeper than one extra pass, never run concurrently, and a tick's
inputs (§2.2) are read **inside** the lock, after it is acquired —
never snapshotted by the caller and handed in stale.

### 2.2 Inputs handed to derive() — one read pass, one clock

Per tick, inside the mutex, on `Dispatchers.IO`:

1. `nowWall = System.currentTimeMillis()` — **captured once.** Every
   decision in the pass uses this value; nobody re-reads the clock
   mid-pass (a pass straddling a boundary must not half-derive two
   worlds). `nowMono = SystemClock.elapsedRealtime()` captured with it,
   for throttle/age arithmetic only — never for schedule math.
2. `routines = dao.getAllRoutinesSnapshot()` (exists, `MonolithDao.kt:154`)
3. `active = dao.getActiveSessionSnapshot()` (exists, `:61`)
4. `settings = dao.getGuardSettingsSnapshot()` (exists, `:144`) —
   carries pause/test-date/`lastBootAtMillis` (Sten 8→9 columns)
5. The two existence sets derive() needs for Sten R3/finalise:
   session rows keyed `(routineId, windowStartMillis)` for windows
   derive() reports open/just-closed, and the unfinalised-routine list
   (`routineId != null && routineOutcome == null && !isActive`) —
   Sten's 2 DAO queries (~8 lines, his §3.6 price), called by the
   funnel, handed over as lists.
6. `source` — for the tick stamp (§3.2) and logging only. **derive()
   may not branch on source.** A tick from a hallucinated source must
   produce the identical result to any other tick at the same `now`.

**Explicitly NOT handed over:** the alarm's boundary extra / routine id
(the chain hint is content-free, Sten C1 — receiver ignores extras,
§2.4); any UI/ViewModel state; the foreground package; and the heavy
readiness sweep `getSystemGuardStatus()` (HMAC + clock-drift + DAO +
4 system queries, `MonolithRepository.kt:603–631`) — **forbidden in
the tick path by name.** The tick is a schedule pass, not a diagnostic.

### 2.3 Cadence rules per lane

- **H1 (Shield loop):** the loop calls `tick(SHIELD_LOOP)` on iteration
  1, then **at most once per 15 iterations (30s)**, and **immediately
  on any iteration where `updateSessionProgress` returns
  `isActive == false`** (completion edge — the stand-down/finalise
  decision must not wait out the throttle). The loop's own jobs are
  untouched and remain the loop's alone: progress ticking, blocked-set
  computation, foreground query, notification. The tick call is ~12
  added lines in `MonolithShieldService.kt`, after the progress call.
- **H2/H3 (opportunistic):** hooks added per my invention file
  (Accessibility `onServiceConnected`/`onUnbind`, Listener
  `onListenerConnected`/`onListenerDisconnected` → readiness writers,
  unchanged) **plus** one throttled tick call: in
  `onAccessibilityEvent`'s existing coroutine, **before** the
  `activeSession == null → return` early exit, and in
  `onNotificationPosted` identically: if
  `nowMono − lastOpportunisticMono >= 30_000`, stamp and launch
  `tick(A11Y_OPPORTUNISTIC)`. Intercept/blocking work keeps priority —
  the tick launches in the services' existing scopes, never blocks the
  event path, and a denied throttle costs nothing. Rationale for
  existence at all: while he is actively using the phone, boundaries
  get derived within 30s even if the chain hint was swallowed — the
  belt to the alarm's braces, at zero idle cost.
- **Discrete wakes:** unthrottled, `force = true`. Boot, resume, CRUD,
  session edges, time change, package replaced, chain hint.

### 2.4 Alarms as hints — the receiver's demotion, exact

`RoutineAlarmReceiver` becomes Sten's hint shell (~28 lines, his
price) with this file's one change: its body is
`RoutineHeartbeat.tick(CHAIN_ALARM, force = true)` inside the existing
`goAsync` + IO coroutine — it reads **no** routine row, switches on
**no** boundary string, starts/stops **nothing**. Early, late,
duplicated, spurious, or delivered-for-a-deleted-routine hints all cost
exactly one derive pass and nothing else (Sten R4's stale-hint test is
the acceptance test for this section). End-of-tick, the funnel — not
the receiver — rearms the chain (Sten C2).

**Otis amendment to Sten C1 (flagged for Voss, priced):** the chain's
`nextBoundary` minimum must also include each enabled routine's
`pausedUntilMillis` when it is `> now` and falls inside an otherwise
open window — a pause expiring mid-window is a state transition no
start/end boundary predicts, and without it that window's materialise
waits until window end (i.e. never). `RoutineScheduler.rearmChain`:
+~5 lines inside the existing boundary enumerator. Test-date rollover
needs **no** amendment: suppression lives inside the schedule
functions, so a suppressed start is never offered as a boundary.

**Manifest delta:** add `android:directBootAware="true"` to
`RoutineAlarmReceiver` (+1 attribute). It currently lacks it while
Shield, Accessibility, Listener and BootReceiver all carry it; a hint
delivered pre-unlock after reboot would otherwise sit undelivered
until unlock — harmless to truth (the LOCKED_BOOT tick, §3.1, already
derived that window) but free to fix, and the receiver's only storage
is the device-protected DB, so direct-boot execution is safe.

### 2.5 Transitions: who starts/stops the session row — the heartbeat is the only mover

`derive()` returns, per routine, a state; the funnel diffs it against
the rows it read and dispatches **only** these transitions. No other
code path in the routines build may start or stop a routine session.

| Derive result (diffed vs rows) | Mover | Exact acts, in order |
|---|---|---|
| `OPEN_IDLE`, no session row for `(routineId, windowStart)`, remainder ≥ 5 min (Sten R2.3/R3) | **Funnel → Reconciler.materialise** (sole caller of this path) | `repository.applySmartLoadout(loadout)` → `repository.startSession(intention = routine.name, profileName = "Routine: <name>"` *(display string only — matching by it is Lie #13, dead)*, `durationMinutes = remainder`, `studyBlocksCount = 1`, + Sten's `routineId/windowStartMillis/windowEndMillis` stamps) → `MonolithShieldService.startShield()` |
| `OPEN_HELD` (window open, active session is manual or another routine's) | **Nobody** | No start, no end, no loadout (Sten R2.5). Recorded only as the derived state surfaces read |
| Active routine session, `now >= its stored windowEndMillis` | **Funnel → Sten's finalise funnel** (§3.4 of his file — the generic `disarmActiveSession` **refuses** `routineId != null` rows) | Finalise prices evidence → outcome stamp (immutable) → `MonolithShieldService.stopShield()` if no active session remains. Never `disarmActiveSession(completed = true)` — that call is the ghost's mint (`MonolithRepository.kt:1085`, books `max(verified, stored)` elapsed on the caller's word) |
| Session completed by H1's own progress tick (`isActive == false`, `routineOutcome == null`) | **Funnel, finalise-on-observe** (Sten §3.3) | Same finalise funnel. Both stand-down paths converge here; finalise is idempotent and never re-judges a stamped row, so Shield-beats-funnel and funnel-beats-Shield are the same outcome |
| `OUTSIDE` / `OPEN_RUNNING` / window already has its session row | **Nobody** | No-op. Duplicate ticks are free by construction (natural-key existence check, §2.2 item 5) |
| Any transition, or chain probe (`FLAG_NO_CREATE`) reports unarmed, or boundary set changed | **Funnel → `RoutineScheduler.rearmChain`** | Rearm at the new minimum boundary (incl. §2.4 pause expiries). Rearm is **not** run on no-op H1 ticks — AlarmManager is not churned every 30s for a boundary that hasn't moved |

**Manual sessions are never the funnel's business:** a manual start
mid-window produces HELD (above); a manual disarm fires
`tick(SESSION_EDGE)`, whose derive sees the freed window and — per
Sten R3 — materialises **nothing** for a window that has been held.
The user's own disarm of a *routine* session routes through Sten's
finalise funnel at the repository guard (his §3.4), and the following
SESSION_EDGE tick likewise never resurrects that window.

**Tick budget (hard):** one pass = 3–5 local DAO reads + pure math over
≤ a dozen routines. Target < 50 ms, ceiling 2 s, absolute bound inside
the broadcast `goAsync` window. No network, no HMAC sweep (§2.2), no
WakeLock acquisition — the funnel runs inside wakes other mechanisms
already paid for, and `tick()` itself must never try to keep the
device awake to finish "one more thing."

---

## §3 — Death cases: absence, detection, recovery

The load-bearing property, stated once: **the heartbeat writes nothing
that truth depends on.** Schedule rows, session rows, evidence
timestamps are written by CRUD, by materialise/finalise, and by the
session's own progress path — the tick only *reads* them and dispatches
transitions whose every act is idempotent (natural-key materialise,
immutable-stamp finalise, rearm-at-minimum). A tick that never happens
therefore leaves the world *unmoved*, not *wrong*; the next tick
derives the whole present from rows and moves it once. Delay is the
only failure mode the design admits. Each death below is that property
instantiated.

### 3.1 The deaths, one by one

**Shield killed mid-session (OEM killer, memory pressure).**
`START_STICKY` (`MonolithShieldService.kt`, onStartCommand return) —
the system restarts the service with a null intent when it can; the
recreated loop's first act under this spec is `tick(SHIELD_LOOP,
iteration 1)` before/with its first progress call: derive sees the
still-active session row (Room, device-protected storage — it survived
the process) and the open window → `OPEN_RUNNING`, no duplicate
materialise (natural key), progress resumes via the existing
reboot/monotonic re-anchor in `updateSessionProgress`
(`MonolithRepository.kt:926 ff` — boot-count + `lastTick` comparison
already handles the monotonic reset). **Honest limit, named:** a user
*force-stop* puts the app in Android's stopped state — `START_STICKY`
does not resurrect it, no alarm fires, nothing runs until he opens the
app. That ceiling is the platform's, not this design's; the resume
tick on that open derives and repairs everything (below), and the
record books only evidenced minutes for the gap because finalise
prices stored timestamps, not wall-span.

**Phone reboot — the LOCKED_BOOT_COMPLETED path.**
`MonolithBootReceiver` already receives `LOCKED_BOOT_COMPLETED` +
`BOOT_COMPLETED` + `MY_PACKAGE_REPLACED` + `QUICKBOOT_POWERON`,
`directBootAware=true`, and the DB is built on
`createDeviceProtectedStorageContext()` (`MonolithDatabase.kt:33–43`)
— so DAO reads are lawful **pre-unlock**, before credential storage
exists. New wiring (+~8 lines in the receiver): its `goAsync` body
becomes, in this order —
1. stamp `guard_settings.lastBootAtMillis = nowWall` (Sten §3 Q3's
   boot witness — the NOT_DELIVERED distinction rides on it);
2. `tick(BOOT, force = true)` — derive over the world as it is *now*:
   a routine session whose window ended while the phone was off is
   finalised from stored evidence (off-time generates no evidence
   timestamps, so coverage shrinks toward MISSED — book-less is the
   arithmetic, Sten §3 Q2); a window open *now* with no session row
   materialises at remainder; an in-window pre-reboot session is
   re-adopted, not restarted;
3. `startShield()` **iff** derive left an active session standing
   (replaces today's unconditional `active != null` check — same
   effect, one source of truth);
4. rearm the chain (today's `scheduleAll` call, Sten's rewrite).
Both boot actions fire for one reboot, so this body runs twice:
the funnel's mutex + idempotent transitions make pass two a no-op
(Sten's duplicate-wake test, extended to boot in §5). Package-replaced
follows the identical path with `source = PACKAGE_REPLACED` — an
upgrade mid-window loses nothing because the session's law is its
stored window snapshot (Sten R5).

**Doze.**
The chain hint is `setAndAllowWhileIdle` (shipped
`RoutineScheduler.kt`, retained by Sten) — inexact by design, and
Android rations allow-while-idle deliveries under deep Doze, so a
boundary hint can land minutes late with **no exact-alarm permission
in the app and none requested** (Sten §1.5 declines
`SCHEDULE_EXACT_ALARM`; this file concurs — punctuality is not worth a
permission + Play-policy surface). Consequences, all absorbed: the
late tick derives at its actual `now` → materialise-at-remainder
(≥ 5 min rule refuses to seal him for a stub), or finalise if the
window already closed. Surfaces print **scheduled** times, never
delivery times (Sten's drift honesty), so a late doorbell never
becomes a printed lie. H1 under Doze: the FGS loop's `delay(2000L)`
can be CPU-throttled while the device sleeps — progress is computed
from verified wall/monotonic spans at each actual iteration, not by
counting iterations, so a throttled loop under-runs its notification,
not the session's truth. (One shipped exception the builder must not
"fix" via the tick: the Safe-Break branch of `updateSessionProgress`
decrements `safeBreakSecondsRemaining` by 1 **per call** — call-count,
not clock. §4 N3 exists because of it.)

**Clock jump (travel, manual set, NTP correction).**
`TIME_CHANGED` tick (§1.2d): forward jump past a window end → the
tick stands the session down through finalise, evidence-priced from
real timestamps — the jump mints no minutes because finalise measures
evidence instants, not the wall span between them. Backward jump into
an already-materialised window → natural key: the session row for
`(routineId, windowStartMillis)` exists → no second materialise; a
finalised window's stamp is immutable → no resurrection.

**The funnel itself dies mid-tick** (process killed between materialise
steps): worst interleaving is loadout applied, session not yet
inserted — the next tick derives `OPEN_IDLE` with no session row and
materialises (loadout re-apply is idempotent; `applySmartLoadout`
rewrites the same app tiers). Or: session inserted, Shield not yet
started — next tick (any lane) or the boot/resume path sees an active
session with no Shield and calls `startShield()`. Every prefix of the
transition sequence is a state a later tick repairs. No transaction
spanning services exists in Android; this is the achievable form of
atomicity, and it is sufficient *because* derive is stateless.

### 3.2 Detecting the heartbeat's own absence — lazy, by predicate, never by watchdog

No process runs when the app is dead, so no watchdog can watch — a
watchdog process would need its own watchdog, and §1.2 already proved
periodic self-checks buy nothing. Detection is instead **a predicate
evaluated at every wake and every read**, two forms:

1. **Tick age.** One new scalar rides Sten's 8→9 ALTER batch on
   `guard_settings`: `lastRoutineTickMillis INTEGER NOT NULL DEFAULT 0`
   (+1 DDL line, +1 entity field — §6). The funnel stamps it (wall
   clock, with its `source` kept in-process only) at the **end** of
   every completed tick. Absence is then arithmetic, in my Readiness
   Log's STALE-as-predicate semantics: a reader (Guard screen, strip,
   any future Ledger surface) computes expected-vs-actual from context
   — *tick age > 60 s while a session row is active* ⇒ the Shield lane
   died silently; *a scheduled boundary lies in `(lastTick, now]` and
   no tick followed it* ⇒ the chain lane was swallowed. Either reading
   is computed by whoever looks, from the stamp — nobody maintains a
   "heartbeat healthy" flag that a dead process can strand at true.
2. **State mismatch — the stronger detector, and it is free.** At any
   wake, if `derive(schedule, now, rows) != what the rows are doing`
   (window open, no session; session standing past its stored end;
   chain unarmed), the mismatch **is** the detection — and the same
   tick that detects it repairs it, because detection and reconcile
   are the same pass. There is no separate health subsystem to build,
   feed, or lie about its own health.

### 3.3 Why a missed heartbeat can never corrupt — the three-line proof

(1) Truth lives in rows the tick doesn't write (§3 preamble).
(2) Every tick recomputes from `(schedule, one captured now, rows)` —
history of previous ticks is not an input, so a gap leaves no residue
to compound. (3) Every dispatched act is idempotent and evidence-priced
at finalise. Therefore the cost function of a missed tick is bounded
by *enforcement lateness* (a seal that starts at remainder, a stand-down
finalised late) and is identically zero in the book. This is Sten's
§1.2 ghost proof, extended from bookings to the engine itself.

---

## §4 — What must NOT ride the heartbeat (named prohibitions)

- **N1 — Nothing user-facing.** No composable, no strip/chrome refresh,
  no haptic, no sound, no toast. The one notification the app owns —
  Shield's — is updated by H1's loop on its own line, as shipped; the
  tick neither rewrites it nor adds a "routine started" notification
  (a routine sealing is the schedule keeping an appointment, not news).
  Surfaces read derived state via their own flows (Sten R1: every
  surface calls the one function) whenever they render.
- **N2 — Nothing that books time by itself.** The tick never writes
  `elapsedSeconds`, never sets `completedSuccessfully`, never forges an
  artifact, never sums history. It dispatches transitions; **finalise**
  (Sten's funnel) is the only place minutes are priced, from evidence.
  Heartbeat = *when to look*; derive = *what is true*; finalise =
  *what is booked*. Any line that collapses two of these is a defect.
- **N3 — Never call `updateSessionProgress` from the tick.** It is
  H1's, once per loop iteration. Its Safe-Break branch decrements by
  call count (§3.1); a second caller accelerates the break countdown
  and double-writes the session. The tick reads the session row; it
  does not tick the session.
- **N4 — No heavy sweeps.** `getSystemGuardStatus()` is named forbidden
  (§2.2). No readiness re-poll rides the tick either: the strip's
  engine is my Heartbeat Tap (bind hooks + Shield beat), a *separate*
  consumer of the same services — the routines tick must not become
  the poller W4 killed, wearing a schedule.
- **N5 — No loadout/session side effects outside a transition.** A
  no-op tick touches exactly two things: the tick stamp (§3.2) and,
  when needed, the chain rearm. In particular it never re-applies a
  loadout to a running session (HELD discipline) and never "refreshes"
  a running routine session's duration to the live schedule — the
  stored window is its law (Sten R5).
- **N6 — No new time-keeping.** The tick creates no timer, alarm,
  worker, or loop beyond §1's lanes and Sten's chain. Anyone needing a
  new cadence files it as a new mechanism with its own battery price,
  against §1.2's theorem.

---

## §5 — Acceptance tests (heartbeat-specific; Sten's suite stands beneath)

1. **Swallowed hint:** cancel the chain alarm in-test; open the app
   mid-window → resume tick materialises at **remainder**, chain
   re-armed at the next true boundary, no full-duration booking.
2. **Triple-wake race:** fire chain hint + resume + accessibility event
   in the same millisecond at a boundary → exactly **one** session row
   for `(routineId, windowStartMillis)`; funnel passes ≤ 2.
3. **Kill Shield mid-window** (process kill, not force-stop): sticky
   restart's first tick derives `OPEN_RUNNING`; no duplicate session;
   tick-age gap is visible as a stamp gap, nothing else changes.
4. **Reboot mid-window, phone left locked:** LOCKED_BOOT tick finalises
   / re-adopts / materialises pre-unlock from the device-protected DB;
   the subsequent BOOT_COMPLETED pass is a no-op.
5. **Reboot across a whole window** (off before start, on after end):
   no session row, no minted minutes; the window judges per Sten Q3
   with `lastBootAtMillis` inside it (NOT_DELIVERED where provable).
6. **Doze delay:** `dumpsys deviceidle` forced idle across a boundary;
   on the delivered hint, one pass — remainder or finalise, scheduled
   times printed, delivery time printed nowhere.
7. **Clock forward past window end:** TIME_CHANGED tick stands down
   via finalise; `elapsedSeconds` reflects evidence only. **Clock
   back into the same window:** no second materialise, no un-finalise.
8. **Pause expiry mid-window** (§2.4 amendment): pause until 14:00
   inside a 09:00–17:00 window → chain armed at 14:00; materialise at
   remainder from 14:00, not before.
9. **Tick purity (grep test):** the tick path contains no call to
   `getSystemGuardStatus`, `updateSessionProgress`,
   `disarmActiveSession`, or any notification/UI API.
10. **Force-stop ceiling:** force-stop mid-window; relaunch next day →
    first resume tick repairs all state; the gap window books only
    evidenced minutes. (Documents the platform limit in §3.1 as tested
    behaviour, not hope.)

---

## §6 — Migration verdict: the 9→10 readiness log is NOT designed here — one scalar is, and it cannot wait

Per the arbitration (8→9 = Sten's routines schema; 9→10 = Rurik's step
tables + my readiness log): **the readiness log itself does not need
designing now, and routines must not wait for it.** What routines need
from it is one fact — *when did the engine last complete a tick* —
consumed only as the §3.2 age predicate. That fact ships **now**, as
the single scalar `guard_settings.lastRoutineTickMillis` inside Sten's
8→9 ALTER batch (+1 line of his DDL, +1 entity field; his migration
already touches that table three times). If it waited for 9→10,
routines would ship for a full build with **no absence detection at
all** — boot gaps, silent Shield deaths, and swallowed hints would be
indistinguishable from health, and retrofitting the stamp later would
need its own migration for one column.

The contract the later seat must honour (so 9→10 is a backing-store
swap, not a redesign): writers stamp through the funnel only; readers
ask the funnel/DAO for `lastRoutineTickMillis`, never the table's
shape. When the log lands, HEARTBEAT rows (throttled Shield beats, my
invention §2: ≤ 1 per 10 s) become the store, the scalar becomes
`MAX(atWallMillis)` over them, and the routines wiring changes by
**~6 lines** (the stamp call retargets). Everything else about the
log — cells, sources, pruning, cold-start semantics — remains the
later seat's, undesigned here on purpose.

**Dependency named, not hidden (repeated from my invention file):**
shipped `MonolithDatabase.kt:49` still carries
`.fallbackToDestructiveMigration(true)` and DB version is still 7.
The token build is specced to delete the fallback and own 7→8; if it
hasn't when 8→9 merges, *both* migrations are void on upgrade morning
and §3.2's stamp arrives by destructive fallback with the rest of the
DB. Verify at merge, not at runtime.

---

## §7 — Price (Otis delta only; Sten's reconciler/window/finalise prices stand as filed)

| Item | Files | Lines |
|---|---|---|
| `RoutineHeartbeat.kt` — funnel: mutex+coalesce, clock capture, input pass, throttle gates, transition dispatch, tick stamp, rearm-if-needed | 1 new | ~130 |
| Shield loop tick call (iteration-1 + 30s throttle + completion edge) | `MonolithShieldService.kt` | +~12 |
| Accessibility: bind hooks (readiness, from invention §1) + throttled opportunistic tick before the session-null exit | `MonolithAccessibilityService.kt` | +~16 |
| Listener: bind hooks + opportunistic tick gate | `MonolithNotificationListener.kt` | +~12 |
| Boot receiver → tick(BOOT) ordering (§3.1) + TIME_CHANGED/TIMEZONE actions in its `when` | `MonolithBootReceiver.kt` | +~10 |
| Manifest: +2 actions on BootReceiver, +`directBootAware` on RoutineAlarmReceiver | `AndroidManifest.xml` | +~3 |
| Chain enumerator: pause-expiry boundaries (§2.4 amendment) | `RoutineScheduler.kt` ( inside Sten's rewrite) | +~5 |
| `lastRoutineTickMillis`: entity field + 8→9 DDL line + funnel stamp write | `MonolithModels.kt` / Sten's migration | +~3 |
| Receiver-as-hint: covered by Sten's −46 rewrite; Otis delta (call tick, not reconcile) | — | ~0 |

**Total Otis delta: 1 new file, 7 files touched, ~190 lines. Zero new
tables, zero new permissions, zero new alarms, zero new timers.**
Unbuildable as designed: nothing in the wiring. Two platform ceilings
are documented, not solved — Doze punctuality without an exact-alarm
permission (§3.1), and force-stop resurrection (§3.1) — and the spec's
claim in both places is honesty (remainder, evidence-priced books,
lazy detection), never punctuality or immortality.

— Otis, MaxImpl · services/readiness · ROUTINES REBUILD
