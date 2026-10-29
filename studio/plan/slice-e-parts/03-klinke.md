# Klinke — Improvers (the latch) · SLICE E BUILD-READY · E1's ONE LAWFUL CALL SITE + THE t=0 PLUMBING

*Seat: Klinke, Improvers — the latch. Seated by Ilya (Improvers
coordinator) for the SLICE E BUILD-READY wave. Joint: SLICE-D-E-
BUILD-READY §0 step 5 + §5 build-order step 4→5 (entry E1), under
MASTER-PLAN §2.6: at t=0 the session row is re-anchored and
re-signed; t=0 = the first vsync frame the controller executes
PROVING → SEALED-RUNNING. Method: the whole ARM path traced this
run against TODAY's trunk (`~/workspace/clone-inspect/Focus-app`,
read only, no gradle run), every line re-read, none inherited.
Companion filings: Siegel `01-siegel.md` (E1's engine + the two
deaths — I concur with his lines and amend his §3.1 placement,
§2.3 below), Opal `plan/slice-de-parts/opal.md` §3 (the D8
cascade this joint consumes), Orson `plan/slice-de-parts/
orson.md` §4 (the call-site law). Inbox at start: empty (read/
empty).*

---

## 0. Verdict

**BUILDS — against a named D8 interface that exists in full as a
filed design and in zero lines of today's trunk.** Four facts,
each proven in the sections below:

1. **PROVING does not exist in the trunk** (grep this run:
   `PROVING`, `ArmPhase`, `ArmProvingController`, `sealSession`,
   `SEALED_RUNNING`, `playSealThrow`, `primeForSeal`,
   `annulProvingSession` — **0 hits each**). What Slice E consumes
   from D8 is named exactly in §1: Opal's `ArmProvingController`
   + `ArmPhase` StateFlow + the §3.2 t=0 execution, with ONE
   amendment E1 requires (sealSession returns Boolean, §4.3).
2. **The re-anchor + re-sign at t=0 is D8's build, not E's.**
   Opal §3.2 step 1 owns it as `repository.sealSession(sessionId,
   t0)`, inside D8's §F2 price. §4 specs it field-by-field against
   today's signing code, because a wrong re-sign is a false
   900-second tamper penalty (Repository:96, :1011).
3. **The naive call-site placement is a trap.** Siegel §3.1's
   fallback — `playSealThrow()` inside `armMonolithSession()`'s
   coroutine after `repository.startSession(...)` returns — fires
   at **L1-confirm**, up to ~500 ms before t=0, before L2/L3
   confirm and before the re-anchor: a pre-proof seal wearing the
   new voice. It is lawful only in a world where D8 never lands.
   D8 ships unconditionally (SLICE-D-E §1-D8: "§F2 ships
   unconditionally"), so the lawful site is **inside the
   controller's t=0 frame execution**, and `armMonolithSession()`
   carries no seal call at all in the built form (§2).
4. **The view's dying site has no session-exists guarantee —
   none was ever built** (§5). `BlockitCoreViews.kt:1184` fires
   on release, before the commit callback that eventually causes
   the insert. Removing it breaks nothing, because nothing
   downstream reads it.

**One fork** (F-K1, §9): the recovery-t0 Throw after process
death. Recommendation: it fires.

---

## 1. The named D8 interface — exactly what E1 consumes

Source of the design: Opal, `slice-de-parts/opal.md` §2 (link
list), §3.1 (where PROVING lives), §3.2 (dispatch, clocks, t=0),
§3.3 (deadline/annulment), §3.4 (process death); merged at
SLICE-D-E §1-D8 (RECONCILED AT N=3) and §0 step 5.

### 1.1 The interface, named

| Consumed piece | Name / shape as D8 files it | Home |
|---|---|---|
| Controller | `ArmProvingController` — new file, VM layer, exposing `StateFlow<ArmPhase>` to the Deck (opal.md §3.1, §F2 price table) | new file `ui/` |
| Phase type | `sealed ArmPhase { IDLE · PROVING(attemptId, latchMono L, sessionId: Int?, links: Map<Link, LinkMark>) · JAMMED(attemptId, jamSet: Set<Link>, retryCount: Int) — a PROVING substate, same attemptId · SEALED_RUNNING(sessionId, t0Mono) · RELEASING(...) }`; `LinkMark = PENDING \| CONFIRMED(mono) \| JAMMED`; `provenCount` derived, never stored | controller file |
| Links | **L1 SESSION-ROW** (insert returned id ∧ snapshot returns that id with `isActive == true`) · **L2 SHIELD-PROTECTING** (live instance ∧ startForeground succeeded ∧ first monitor iteration read the L1 row active) · **L3 A11Y-SESSION-ARMED** (bound ∧ its session collector saw the latched id active) | opal.md §2 |
| The transition E1 hangs off | Opal §3.2 verbatim shape: *"at the first vsync frame with frameMono >= t0, CAS PROVING -> SEALED_RUNNING(t0) — exactly once, one zero: 1. `repository.sealSession(sessionId, t0)` … 2. the seal sound — its ONLY arm-path call … 3. the session readout starts consuming the re-anchored row."* Post-B-5, step 2's sound is `playSealThrow()` (Siegel §2/§3) | controller |
| t=0 value | `t0 = max(lastConfirm, L + 210, pawlSeated)`, all stamps `SystemClock.elapsedRealtime()`; L read once, main thread, at latch | opal.md §3.2; MASTER-PLAN §2.6 |
| Re-anchor | `repository.sealSession(sessionId, t0)` — §4 of this file | `data/MonolithRepository.kt` (new) |
| Annulment | `repository.annulProvingSession(sessionId)` = `dao.deleteSessionById(id)` + `stopShield`, pre-t0 only, at L+1500 | Repository wrapper over DAO:75–76 (exists today) |

**E1's positional requirement, stated so the builder cannot
misread it:** `playSealThrow()` is a statement *inside* that
transition execution — after `sealSession` has returned success,
in the same frame execution — never a subscriber to
`ArmPhase.SEALED_RUNNING`, never a `LaunchedEffect`, never a
collector side-effect. A StateFlow collector can be conflated,
replayed on re-subscription, and re-delivered after process
death; the sound must be an act of the transition, not a
reaction to the state it publishes (§3, guards G1/G4).

### 1.2 Trunk status today — what D8 still owes, by name

The Live Wire substrate (SLICE-D-E §5 step 1) HAS landed since
Opal filed; her "does not exist today" witness notes are partly
stale. Verified this run:

**Already in trunk (D8 consumes, does not build):**
- `LiveWire` object — `service/LiveWire.kt:65–98`: registries,
  `snapshots: StateFlow<WireSnapshot>` (:91–92), `poke()`,
  `stampedThisResidency()` (:80–83), `noteResidencyStart()`
  (called `MainActivity.kt:152`).
- `liveWarrant()` — `data/MonolithRepository.kt:578–596`,
  synchronous; three-valued `usageAccess()` at :536–562
  (ALLOWED/DENIED/UNSET — Opal/D6's split is built).
- Shield registry: `@Volatile instance` + `@Volatile protecting`
  — `service/MonolithShieldService.kt:206–218`; `instance` set
  only after `ServiceCompat.startForeground` returns (:51–61);
  `onDestroy` clears both (:195–204).
- A11y registry: `@Volatile instance` + `lastRootMono` —
  `service/MonolithAccessibilityService.kt:39–53`;
  `onServiceConnected` (:55–59) and `onUnbind` (:61–67) exist;
  the residency stamp is written **before** the own-package
  early return (:77–84 stamps, :87 returns) — Rosa's
  stamp-before-return ordering, satisfied; `onDestroy` clears
  the instance and cancels the scope (:300–307).

**Still missing — D8's remaining delta, and E1's dependencies:**
- (a) **The controller, `ArmPhase`, and every line of the
  cascade** — 0 hits (§0.1).
- (b) **L2's session keying.** Today's `protecting` is a bare
  Boolean (Shield:216–218), set after the first *full* monitor
  iteration (:117–120) — but it is not keyed to a sessionId and
  carries no `firstLoopMono`. Opal §2-L2 requires
  `@Volatile firstLoopMono/sessionId`. Without the keying, a
  `protecting == true` left by a previous session's still-living
  shield (the service is `START_STICKY`, Shield:64; it
  self-stops only on its next ≤2 s tick after the old row is
  deactivated, :75–79) can be read as this attempt's L2 inside
  that window. **E1 requires the keying as filed** (risk R-K2).
- (c) **L3's witness, entire.** The a11y service has no
  session-Flow collector and no `armedSessionId`/`armedMono`
  (grep: 0 hits). Today it learns of a session only per-event,
  via `scope.launch` + `dao.getActiveSessionSnapshot()`
  (A11y:92–95) — an event-path read, not a standing witness.
- (d) **`sealSession` + `annulProvingSession`** in the
  repository — 0 hits. `startSession` is untouched by D8
  (Opal §F2 table) and stays as traced in §2.1.

---

## 2. Today's ARM path, traced end to end (the ground the plumbing replaces)

### 2.1 The chain, with today's exact lines

1. **View commit (release-only).** `SlideToStartView.
   onTouchEvent`, `ui/views/BlockitCoreViews.kt:1174–1193`:
   on `ACTION_UP`/`ACTION_CANCEL`, if `dragFraction >= 0.82f`
   (:1177): snap to 1f (:1178), **seal sound (:1184, §5)**,
   `onSlideCommitted?.invoke()` (:1185), spring home (:1186).
   There is no under-contact commit today — D1 replaces this
   whole block; it is traced here because E1's two deaths live
   in it.
2. **Host binding.** `TactileDeckScreen.kt:499`:
   `view.onSlideCommitted = onArmSession` (set in the
   AndroidView `update{}` block, :493–500; the view itself is
   mounted only while `!isSessionRunning`, :486).
   `MainActivity.kt:330`: `onArmSession =
   viewModel::requestArmWithPreFlightCheck`. A second entry
   bypasses the check: the pre-flight modal's confirm,
   `MainActivity.kt:550`: `onConfirmAndArmAnyway =
   viewModel::armMonolithSession`.
3. **VM pre-flight (legacy).** `MonolithViewModel.kt:896–903`
   `requestArmWithPreFlightCheck()` — gates on the
   Settings-string check `repository.isAccessibilityEnabled()`
   (Repository:606–616), NOT on `liveWarrant()` (:578). D5/D8
   kill this path; today it is the only gate.
4. **VM arm.** `MonolithViewModel.kt:909–921`
   `armMonolithSession()`:
   - `:911` — `soundEngine.playHarmonicSeal(arming = true)` —
     **pre-launch, pre-insert, pre-anything. Death 2.**
   - `:912` — `viewModelScope.launch {`
   - `:913–918` — `repository.startSession(intention =
     _intentionText.value, profileName =
     _selectedProfileName.value, durationMinutes =
     _dialMinutes.value, studyBlocksCount =
     _studyBlocksCount.value)` — the brief's "~:913" is exact.
     The return value (the entity) is **discarded** today.
   - `:919` — `MonolithShieldService.startShield(appContext)`
     — inside the same launch, after the insert returns.
     `startShield` itself swallows all exceptions
     (Shield:227–240) — "start requested" proves nothing,
     which is why L2 needs its witness (§1.2b).
   - **No guard of any kind**: no active-session check, no
     in-flight check. Re-entry inserts a second row (step 5
     deactivates the first). A double commit today = 2 inserts
     and up to 4 seal sounds (view + VM, twice).
5. **Repository insert.** `data/MonolithRepository.kt:950–999`
   `startSession(...)`: `:956` `dao.deactivateAllSessions()`;
   anchors sampled at :963–965 (`wallNow =
   System.currentTimeMillis()`, `monoNow =
   SystemClock.elapsedRealtime()`, `bootCount =
   querySystemBootCount()`); HMAC signed at :966–972 over
   exactly those anchors; row built `isActive = true` (:991),
   `startWallTimeMillis = wallNow` (:979),
   `startElapsedRealtimeMillis = monoNow` (:980),
   `lastTickElapsedRealtimeMillis = monoNow` (:983),
   `hmacSignature = hmac` (:984); **insert at :997**
   (`dao.insertSession(session).toInt()`, DAO:69–70); returns
   `session.copy(id = id)` at :998.
6. **Shield start.** Shield `onStartCommand` (:35–65):
   `startForeground` (:51–56) → `instance = this` (:60) →
   `startMonitoringLoop()` (:62); the loop's first pass runs
   before its `delay(2000L)` (:148), reads the active snapshot
   (:74), ticks `updateSessionProgress` (:81), and only after
   that full iteration sets `protecting = true` (:117–120).

**Net shape of today:** the seal sounds at step 1 (view,
release) and step 4 (VM, pre-launch) — twice, both before the
row exists at step 5 — and nothing in the chain ever learns
whether steps 5–6 succeeded. `startShield` failure is
swallowed; `startSession` failure would surface only as a dead
coroutine. That is the whole disease E1 + D8 cure.

### 2.2 The second startSession caller — out of E1's scope, named

`service/RoutineAlarmReceiver.kt:45–51`: routine window START
calls `repository.startSession(...)` directly (no VM, no
gesture, no latch) and then `startShield`. These sessions are
**not PROVING arms**: no attempt, no cascade, no t=0, no
re-anchor, and (today) no seal sound at all. E1's call site
must not be placed in or under `startSession` — that would
fire the Throw for routine arms at insert (pre-proof again)
and double-fire for Deck arms. Siegel raises the convergence
question as his fork F-S1 (+≈18 if ruled in); this joint takes
no position on the sound, but the plumbing position is fixed:
**`sealSession` is controller-invoked only**, and routine rows
keep their insert anchors until/unless a ruling says
otherwise. Acceptance test T13 pins this scope.

### 2.3 Placement ruling (amends Siegel §3.1's fallback)

Siegel §3.1 gives today's-shape insertion (after VM:913,
before VM:919) and adds: "If the cascade lands the transition
as its own function/file, the call lives there." The cascade
DOES so land — that is D8's filed, unconditional form (§1.1).
Therefore, as this joint's ruling for the merge:

- The built `playSealThrow()` site is **inside
  `ArmProvingController`'s t=0 frame execution** (§3.1), full
  stop. The VM-coroutine placement is **not** an acceptable
  interim build of E1: it fires on L1 alone (insert + snapshot,
  ~10–60 ms after latch on a healthy morning — i.e. ~150–200 ms
  before the earliest lawful t=0 at L+210, and unboundedly
  early when L2/L3 are slow), before the re-anchor it is
  supposed to celebrate. Shipping it would rebuild today's lie
  with a better voice.
- `armMonolithSession()` in the built form is the controller's
  latch entry (D8's VM row, Opal §F2: "rewritten as latch entry
  into the controller; pre-proof seal deleted"). It contains
  **zero** sound calls. VM:911 dies; nothing replaces it in
  the VM.

---

## 3. The t=0 plumbing — frame execution, ordering proof, guards

### 3.1 The sealing frame, specced exactly

- **Clock discipline.** All stamps `elapsedRealtime()`
  (Opal §3.2). The frame source is
  `android.view.Choreographer` — idiom already in trunk
  (`BlockitCoreViews.kt:423–424`, post at :949). Choreographer's
  `frameTimeNanos` is the *uptime* clock and is **never** used
  as a stamp; `pawlSeated` and the `frameMono >= t0` test are
  `elapsedRealtime()` reads taken *inside* the frame callback.
- **Registration.** One standing frame callback, registered
  when the phase enters PROVING, removed on every exit from
  PROVING (seal, annul, jam-exhaustion — jam keeps it: JAMMED
  is a PROVING substate and its retry confirms through the
  same loop). Per-attempt register/unregister pairs are
  forbidden (leak + double-callback risk, R-K4).
- **Re-evaluation triggers.** On each CONFIRMED stamp (via
  `LiveWire.snapshots` / witness pokes reaching the controller
  on the main thread) and on each frame while PROVING:
  if all links CONFIRMED → `t0 = max(lastConfirm, L + 210,
  pawlSeated)`; the first frame with `frameMono >= t0`
  executes the claim block.
- **The claim block (the transition execution), in order:**
  1. **Claim.** `attempt.sealClaimed` flips false→true
     (an `AtomicBoolean` on the attempt object — the
     controller is main-thread confined by construction, but
     witness stamps originate on service threads, and the
     claim must hold even if a stamp callback ever arrives
     off-main). If already claimed: return. This flag, not
     the phase value, is the exactly-once lock.
  2. **Re-anchor.** `repository.sealSession(sessionId, t0)`
     (§4). If it returns **false** (row gone or inactive —
     annulment won the race): **no Throw, ever, for this
     attempt**; the attempt exits via D8's annul path. The
     claim stays spent — a dead attempt cannot seal late.
  3. **Publish.** `_armPhase.value =
     SEALED_RUNNING(sessionId, t0Mono = t0)`. (The readout,
     D9's Strike, E3's shot all consume this edge; they are
     downstream of the write in step 2, so no surface can
     render "running" from un-re-anchored anchors.)
  4. **Sound.** `soundEngine.playSealThrow()` — the
     transition's last statement. Exactly here, exactly once.
- **`primeForSeal()`** is called by the controller **at
  latch**, inside the dispatch batch (Orson §4.2 / Siegel
  §3.1): the focus transaction happens during the ≥210 ms
  proving window, never on t=0's frame.

### 3.2 Ordering proof — insert returns → cascade confirms → t=0 frame → Throw

| Step | Event | Site / witness | Why the next step cannot precede it |
|---|---|---|---|
| 1 | Latch L sampled; batch dispatched (≤16 ms spread): `primeForSeal()`, launch `startSession`, `startShield` | controller (D8); today's shapes VM:912–919 move here | enforcement dispatches only at latch (MASTER-PLAN §2.6, F-DISPATCH) |
| 2 | Insert returns id | Repository:997–998 | `startSession` is suspend; the return is the first moment the id exists |
| 3 | **L1 CONFIRMED** — snapshot returns that id, `isActive == true`; stamp = `elapsedRealtime()` at snapshot return | DAO:60–61 (Opal §2-L1) | insert alone is not L1: a returned id with no readable active row proves nothing |
| 4 | **L2 CONFIRMED** — shield's first monitor iteration *under that sessionId* stamps `protecting`/`firstLoopMono` | Shield:117–120 + D8 keying delta (§1.2b) | the loop reads the L1 row (Shield:74) — it cannot confirm before the row is readable |
| 5 | **L3 CONFIRMED** — a11y collector stamps `armedMono` for that id | D8 new collector over DAO:58 Flow | the Flow emits the row only after insert commits |
| 6 | Held marks applied: L2/L3 stamps that beat L1 are timestamped, uncounted, until step 3 lands; if L1 fails they are discarded with the attempt | Opal §3.2 | a shield protecting no row proves nothing about a seal |
| 7 | t0 computed; first frame `frameMono >= t0` runs the claim block | §3.1 | t0 ≥ every CONFIRMED stamp and ≥ L+210 by construction of `max()` |
| 8 | `sealSession` writes re-anchored + re-signed row; returns true | §4 | step 9 is gated on this return value, same frame execution |
| 9 | `SEALED_RUNNING` published | §3.1 step 3 | publication follows the write — no frame renders running-time from insert anchors |
| 10 | **`playSealThrow()` — exactly once** | §3.1 step 4 | steps 1–9 are its only path; every guard below closes a second path |

### 3.3 Double-fire guards, one per attack

- **G1 — Recomposition.** The call is not in composition. The
  Deck only *collects* `ArmPhase`; recomposition, re-collection,
  and AndroidView re-`update{}` (Deck:493–500 runs on every
  recomposition today) cannot reach the sound. The controller
  is VM-scoped and survives all of it. (D9's Strike IS
  composition-side and carries its own session-id keying —
  Emil's joint, not this one.)
- **G2 — Double latch / double entry.** `beginArm` while
  `phase != IDLE` returns without dispatching (controller
  guard, inside D8's controller price). This closes today's
  hole (VM:909–921 re-enters freely, §2.1.4). Second lock: even
  a dispatched second attempt has its own claim flag and its
  own sessionId — and `startSession`'s `deactivateAllSessions`
  (Repository:956) means the first attempt's `sealSession`
  id-check (§4) fails against the superseding row. It cannot
  seal a session that is no longer the active one.
- **G3 — Jam → retry.** JAMMED keeps the attemptId; confirmed
  marks are monotone within the attempt (Opal §3.3.3); the
  claim flag lives on the attempt, not on the phase value, so
  PROVING→JAMMED→PROVING cycles cannot reset it. Retry success
  flows into the same §3.1 evaluation. Exactly once survives
  retries by construction.
- **G4 — Process death between insert and t=0.** The
  controller, the attempt, and the claim die with the process;
  the row survives born-active on insert anchors. **No Throw
  can fire posthumously** — no code is running. On relaunch the
  controller finds an active row with no live attempt and runs
  Opal §3.4's recovery: witnesses re-sampled as a fresh
  cascade (new attemptId, new L′); if all three confirm, that
  completion frame is the row's t0, `sealSession` runs there,
  and the Throw fires — **once, late, and truthfully** (fork
  F-K1, §9). If any witness is dead at recovery: annulment on
  the recovery deadline, Guard sentence owns the screen, no
  Throw ever. Exactly-once is per-attempt AND per-row here:
  the dead attempt never claimed, the recovery attempt claims
  once.
- **G5 — Annulment race at the deadline.** Annulment
  (L+1500, row deletion) is pre-t0 only (Opal §3.3.4).
  Same-frame contention is decided by execution order on the
  main thread: annul first → step 2's id/active check fails →
  no Throw (the claim is spent on a dead attempt); seal first
  → t0 has fired → annulment is forbidden by law and D8's
  annul path must check `phase == SEALED_RUNNING` and refuse.
  The Boolean return in §4.3 is what makes the first branch
  airtight instead of hopeful.
- **G6 — sealSession failure.** `signSessionState` cannot
  throw outward (Crypto:62–80, double fallback); a DB failure
  on the update surfaces as `sealSession == false` → G5's
  no-Throw branch → attempt exits via jam/annul. The seal
  never sounds over an un-re-anchored row.

---

## 4. The re-anchor + re-sign — who owns it, and the exact spec

### 4.1 Ownership

**D8 owns it.** Opal files it twice: §3.2 step 1 of the t=0
execution ("REQUIRED, not polish") and the §F2 price table's
Repository row ("`sealSession` re-anchor + re-sign (~30)").
SLICE-D-E §1-D8 carries it ("the row stays born-active and is
re-anchored + re-signed at t=0"), and MASTER-PLAN §2.6 makes
it law. **It is not E's build and not E1's price.** E1's
interest is contractual: the transition may not reach
`playSealThrow()` until `sealSession` returns success in the
same frame execution (§3.1). If any future merge tries to
move `sealSession` into Slice E's price, this file is the
record that it was D8's line first.

### 4.2 The spec, against today's signing code

`CryptoIntegrityManager.signSessionState` —
`data/CryptoIntegrityManager.kt:53–81`. The signed payload is
exactly five fields (:60):
`"$startWallTimeMillis|$startElapsedRealtimeMillis|$bootCount|$targetDurationSeconds|$isStopwatch"`.
Verification (`verifySessionState`, :83–93) recomputes from
the row's stored fields; `computeVerifiedSessionElapsed`
(Repository:659–682) flags tamper when
`drift > 90 s || !hmacValid` (:673), and tamper costs
`HMAC_TAMPER_PENALTY_SECONDS = 15 * 60 = 900` s, added once
(Repository:96, applied :1011). **Any anchor change without a
matching re-sign is indistinguishable from forgery.** Hence:

```kotlin
// data/MonolithRepository.kt — new, beside startSession (:950)
suspend fun sealSession(sessionId: Int, t0Mono: Long): Boolean {
    val row = dao.getActiveSessionSnapshot()          // DAO:60-61
    if (row == null || row.id != sessionId || !row.isActive) return false
    val wallNow = System.currentTimeMillis()           // read at write
                                                       // time, inside
                                                       // the t=0 frame
    val resigned = row.copy(
        startElapsedRealtimeMillis = t0Mono,           // RE-ANCHOR
        startWallTimeMillis = wallNow,                 // paired (below)
        lastTickElapsedRealtimeMillis = t0Mono,        // §4.3 note
        hmacSignature = CryptoIntegrityManager.signSessionState(
            startWallTimeMillis = wallNow,
            startElapsedRealtimeMillis = t0Mono,
            bootCount = row.bootCount,                 // UNCHANGED
            targetDurationSeconds = row.targetDurationSeconds,
            isStopwatch = row.isStopwatchMode
        )
    )
    dao.updateSession(resigned)                        // DAO:72-73
    return true
}
```

Field-by-field law:
- **`startElapsedRealtimeMillis := t0`** — the re-anchor
  itself. Without it the session counts up to 500 ms it was
  never proven for (Opal §3.2); with it,
  `computeVerifiedSessionElapsed`'s mono leg (:670–671)
  yields elapsed ≈ 0 at t=0 and counts only proven time.
- **`startWallTimeMillis := wallNow`** — must move with the
  mono anchor: the verifier's drift leg (:664, :672) compares
  wall-elapsed against mono-elapsed; moving only one anchor
  leaves drift ≈ the proving duration (≤0.5 s — survivable
  against the 90 s bound, but a gratuitous lie in the data;
  Opal's sketch moves both, and both is correct).
- **`bootCount`, `targetDurationSeconds`, `isStopwatchMode`
  — unchanged**, taken from the re-read row, never from
  caller-held copies. A boot change mid-proving is a reboot:
  `elapsedRealtime` has reset, t0 is meaningless, and the case
  belongs to G4 recovery, not to this function.
- **Everything else from the re-read row** (`elapsedSeconds`,
  `phantomReachesBlocked`, block state): the Shield's monitor
  loop is already ticking this row during proving
  (Shield:81), and the intercept path writes it too
  (Repository:1138). Copying from the insert-time entity the
  VM discarded (§2.1.4) would clobber those writes.
- **Not in the transaction's way:** the row stays
  `isActive = true` throughout — born-active is the design
  (Opal §3.1); `sealSession` never touches `isActive`.

### 4.3 Two amendments to Opal's sketch (named, priced in §7)

1. **`lastTickElapsedRealtimeMillis := t0`** — Opal's §3.2
   sketch omits it. The row is born with `lastTick = insert
   mono` (Repository:983). Leaving it is *safe* (the reboot
   tests at Repository:667–668 and :1015–1016 fire only when
   `nowMono < lastTick`, impossible here, and the first tick
   overwrites it, :1042) — but a re-anchored row whose tick
   stamp predates its own start is an inconsistency a future
   reader must re-derive as harmless. Setting it costs **+0
   lines** (same `copy`). Set it.
2. **`sealSession` returns `Boolean`** — Opal's sketch is
   written as a statement. The return value is the load-bearing
   piece of guards G5/G6 (§3.3): it is the only thing standing
   between the Throw and a row that no longer exists. **+~2
   lines** inside D8's Repository row. E1 requires this; it is
   an interface amendment, not a fork — if D8 ships it
   Unit-returning, E1's ordering proof has an open seam and
   this file should be cited against the merge.

Race note (R-K3): `dao.updateSession` is a whole-row `@Update`
(DAO:72–73), so a Shield tick (Shield:81 → Repository:1007ff)
or an intercept increment (Repository:1138) landing between
`sealSession`'s read and write is clobbered by the stale copy.
Blast radius: `elapsedSeconds` self-heals next tick (it is
recomputed from anchors); `phantomReachesBlocked` can lose one
increment in a ≤500 ms window. Mitigation if D8 wants it:
wrap read+write in the repository's existing `withTransaction`
pattern (the pattern Beleg verified at Repository:1226 for
`completeEmergencyBreak`) — **+~4 lines**, inside D8's row.
Named, not required by E1: the Throw's ordering does not
depend on it.

---

## 5. The view's dying site — its guarantee today is NONE

**Site:** `ui/views/BlockitCoreViews.kt:1184`, inside
`SlideToStartView.onTouchEvent`, `ACTION_UP`/`ACTION_CANCEL`
branch (:1174–1193):

```kotlin
if (dragFraction >= 0.82f) {          // :1177 — a float field
    dragFraction = 1f                 // :1178
    invalidate()                      // :1179
    val eng = soundEngine             // :1183
    if (eng != null) eng.playHarmonicSeal(true) else fireFeel(10L)  // :1184
    onSlideCommitted?.invoke()        // :1185
    animateSpringBack(0f)             // :1186
}
```

**What guarantees a session exists when it fires? Nothing —
by construction, nothing can.** The complete inventory of its
inputs is: the view-local `dragFraction` field, and
`soundEngine != null` (an engine reference the host sets at
Deck:490/:495 — a *sound* presence check, not a session
check). The session does not exist yet: the insert is three
hops downstream of this line — :1185 `onSlideCommitted` →
Deck:499 → MainActivity:330 → VM:896 → VM:909 → launch
VM:912 → Repository:950 → insert :997 — and the VM then plays
the seal *again* at :911, also before that insert. The view's
seal precedes the session's existence by the entire
VM + coroutine + pre-flight + DB path. Two further facts:
`ACTION_CANCEL` shares the branch (:1174), so a *cancelled*
gesture can sound the seal; and on success the thumb springs
home (:1186) — the commit lie D1 deletes wholesale.

**Consequence for removal — stated plainly, because it is
the best news in this file:** deleting :1184 removes **no
lawful behaviour and breaks no consumer**. Nothing downstream
reads the sound; the branch's only output that matters,
`onSlideCommitted`, is untouched (Siegel §3.2's deletion
shape: whole line dies, including the `else fireFeel(10L)`
fallback that spends `playCompletionPulse` — the reserved
completion voice, `TactileSoundEngine.kt:360` — on an arm).
No session-exists guarantee needs replacing at this site,
because none was ever provided or consumed here; the
guarantee is *created* at the new site (§3) rather than
restored at the old one.

**One seam the merge must respect:** the Deck host unmounts
the slide the moment `isSessionRunning` flips (Deck:486,
derived Deck:118 from the Room active-session Flow — VM:280–
281 ← Repository:81 ← DAO:58), i.e. **at insert**, which under
D8 is up to ~500 ms *before* t=0. So during PROVING the slide
is already gone from composition. That is D8/D9's rendering
problem (rack/strip phase tenancy, F17 ghost retention), not
E1's — but it means :1184's deletion must land **with** D1's
slide rewrite (SLICE-D-E §5 step 2), not as a lone E1 commit:
deleted alone, arms lose the view seal while keeping the
release-commit, the spring-home, and the VM's :911 — the worst
combination. E1's two deaths land in two different builds
(D1's view build; D8's VM/controller build) and the interim
tree must never be shipped between them.

---

## 6. Insertion points — the builder's list

| # | Point | File / site | Content |
|---|---|---|---|
| 1 | **THE call site** | `ArmProvingController` (new, D8) — t=0 claim block, step 4 | `soundEngine.playSealThrow()` as the transition's last statement, gated on claim + `sealSession(...) == true` + publish (§3.1) |
| 2 | Priming call | controller dispatch batch at latch | `soundEngine.primeForSeal()` (engine side is Siegel's E1 price) |
| 3 | VM death + delegation | `ui/MonolithViewModel.kt:909–921`; delete :911 | `armMonolithSession()` becomes the controller's latch entry; no sound call remains in the VM arm path |
| 4 | Re-anchor | `data/MonolithRepository.kt`, new fn beside :950 | `sealSession(sessionId, t0Mono): Boolean` per §4.2 (D8-owned) |
| 5 | View death | `ui/views/BlockitCoreViews.kt:1184` (+ comment :1180–1183 rewrite) | whole line deleted, with D1's slide build (§5 seam) |

Explicitly NOT insertion points: `startSession` (would fire
for routine arms, §2.2), any view or gesture callback, any
`ArmPhase` collector/`LaunchedEffect`, `requestArmWithPreFlight
Check` (dies with D5/D8 regardless).

---

## 7. Price — the call-site share, re-priced

**Counting method:** net = added − deleted production lines,
counted per file against today's trunk text; test lines
counted separately and labelled; a line is charged to exactly
one slice (SLICE-D-E §4's union method). D8-owned lines are
shown for the seam total and charged to D8.

**What was filed:** Orson §6 priced the call-site share at
**≈0 net** (VM +2, view −2) inside E1's ≈210 — a price for
moving one line within `armMonolithSession()`'s coroutine.
That placement is ruled out in §2.3, so the share is re-priced
against the D8 interface:

| Piece | File | Net | Charged to |
|---|---|---|---|
| View death (:1184 + comment rewrite) | BlockitCoreViews.kt | −2 | **D1** (lands in its slide build, §5) — removed from E1's count to kill the double-count |
| VM pre-launch death (:911) | MonolithViewModel.kt | −1 | E1 (executed inside D8's VM rewrite) |
| Claim block: claim flag + `sealSession`-Boolean branch + publish + `playSealThrow()` | ArmProvingController.kt (new) | +9 | E1 share of the controller (D8 filed the controller at ~200 gross incl. "t=0 CAS"; the bare CAS one-liner it replaces is ~3 of those — **absorbed-adjusted E1 increment ≈ +6**) |
| `primeForSeal()` call at latch | controller batch | +1 | E1 |
| `sealSession` Boolean return | MonolithRepository.kt | +2 | D8 (E1-required amendment, §4.3.2) |
| `sealSession` body (~30) + `annulProvingSession` (~10) | MonolithRepository.kt | +40 | D8 (as filed, Opal §F2) |
| Plumbing tests (§8, T1–T13 minus Siegel's render tests) | test source set | +45 | E1 increment over D8's filed ~130 cascade tests (which already name held-mark ordering, t=0 `max()` terms, re-anchor HMAC validity) |

**Re-priced call-site share (E1): ≈ +9 net production**
(+9 claim block, +1 prime call, −1 VM death; view death
re-homed to D1), **+45 test lines**. Against the filed ≈0:
the delta buys exactly what the filed price did not carry —
the claim flag, the seal-gate branch, and the guard tests.
E1's slice total is unchanged within tolerance: Siegel's
≈210 net stands (engine ≈150 + his tests), with this +9/+45
sitting on the D8↔E1 seam — if the merge charges the claim
block to D8's controller line, E1's increment over filed is
≈ +3 production and the +45 tests. Either accounting: **no
change to the SLICE-D-E §4 union estimate (±10%)**.

---

## 8. Acceptance tests

Controller tests run against fakes (repository, `sealSession`,
sound engine, witness stamps) with a driven frame clock;
repository tests against the real DAO/Crypto on the existing
test harness. Names are the contract.

- **T1 · ordering_insertBeforeThrow** — all links confirm →
  sealing frame → invocation order is exactly: `sealSession
  (id, t0)` → publish `SEALED_RUNNING` → `playSealThrow()`,
  each exactly once; `startShield` was dispatched at latch,
  not before.
- **T2 · noThrowBeforeAllLinks** — any link PENDING at frames
  ≥ L+210 → zero `sealSession` calls, zero Throws.
- **T3 · t0Formula** — `t0 == max(lastConfirm, L+210,
  pawlSeated)` with each term binding in turn (3 cases).
- **T4 · recompositionSurvival** — `ArmPhase` unsubscribed and
  re-collected mid-PROVING (Deck recomposition) → still
  exactly one Throw at t=0.
- **T5 · doubleLatch** — `beginArm` twice (second while
  PROVING) → one insert, one Throw. Plus: a stale first
  attempt against a superseding row → its `sealSession`
  returns false, zero Throws from it.
- **T6 · jamRetryExactlyOnce** — L2 jams at L+500, retry
  confirms → exactly one Throw; claim flag observed unspent
  until the sealing frame.
- **T7 · annulRace_sealLoses** — row deleted before the
  sealing frame → `sealSession` false → zero Throws, phase
  exits PROVING, claim spent.
- **T8 · annulAfterT0_forbidden** — after `SEALED_RUNNING`,
  the annul path refuses; row survives intact.
- **T9 · processDeathRecovery** — fresh controller over a
  born-active, never-sealed row → recovery cascade → t0 at the
  recovery completion frame → `sealSession` + exactly one
  Throw. Variant: one witness dead → annulment, zero Throws,
  row deleted.
- **T10 · sealSession_hmacValid** — after `sealSession`:
  `CryptoIntegrityManager.verifySessionState(row) == true`;
  `computeVerifiedSessionElapsed(row)` returns `tamper ==
  false` and elapsed ≈ 0 at t0+ε; `startElapsedRealtime
  Millis == t0`; `lastTickElapsedRealtimeMillis == t0`;
  `bootCount` / `targetDurationSeconds` / `isStopwatchMode`
  unchanged; no 900 s penalty on the next
  `updateSessionProgress`.
- **T11 · sealSession_wrongIdOrInactive** — returns false;
  row byte-identical.
- **T12 · armPathGrepGate** — source assertion (Siegel §5's
  gate, concurred): `playHarmonicSeal(true)` has zero call
  sites in `BlockitCoreViews.kt` and in `armMonolithSession`;
  `playSealThrow()` has exactly one call site in the whole
  tree, inside the controller.
- **T13 · routinePathSilent** — a `RoutineAlarmReceiver`
  START arm produces no `playSealThrow` and no `sealSession`
  call in this slice (pins §2.2's scope against accidental
  convergence; F-S1 is Siegel's fork, not settled here).

Device-only (not unit-testable, carried from Orson §7):
audio-vs-haptic offset at the t=0 frame — the plumbing
guarantees the *call* lands in the frame execution; the
speaker-vs-buzz offset is measured on Sarthak's phone per
Orson's 240 fps protocol.

---

## 9. Risks and the one fork

- **R-K1 — D8 slips, E1 is pressured to ship alone.** The
  Siegel fallback placement would be the path of least
  resistance and it fires at L1 (§2.3). Mitigation: build
  order (SLICE-D-E §5: cascade step 4 before Slice E step 5)
  is the gate; E1's call site must not merge without the
  controller. The engine (Siegel's ≈150) may land any time —
  it is inert without a caller.
- **R-K2 — L2 ships Boolean-only.** If D8 lands `protecting`
  as it stands today (Shield:216–218, unkeyed), the
  stale-shield window (§1.2b) lets L2 confirm on a previous
  session's proof. E1's ordering proof (§3.2 step 4) is
  written against the keyed witness; a Boolean-only build
  must be called out at merge as a deviation from Opal §2,
  not silently absorbed.
- **R-K3 — `sealSession` whole-row race** (§4.3): a lost
  `phantomReachesBlocked` increment is the worst case;
  `withTransaction` mitigation priced +~4 in D8's row.
- **R-K4 — Frame-callback registration.** One standing
  callback per §3.1; a per-attempt registration leaks
  callbacks and can double-execute the evaluation (the claim
  flag still saves exactly-once, but the leak is a build
  error on its own).
- **R-K5 — The discarded return value.** Today VM:913–918
  throws away the entity; the controller needs the id for
  L1/sealSession. Trivial, but it is a real edit inside the
  batch dispatch, not a move.

**FORK F-K1 — the recovery Throw (for Ren, via Ilya).**
After process death mid-PROVING, Opal §3.4 makes the recovery
completion frame "the row's t0" and runs "§3.2's re-anchor
there" — §3.2's step 2 is the seal sound, so the filed design
fires the Throw on relaunch, possibly minutes after the
gesture, into a fresh process. Options: **(a) FIRE** — t=0 is
defined by proof completion, not by the gesture; B-5's law is
one voice bit-identical *at every seal*, and a recovered seal
is a seal; silence would make recovery arms a second,
voiceless class of seal. **(b) SILENT recovery** — re-anchor
and publish, no Throw: the sound's meaning is the gesture's
receipt, and a receipt arriving uncaused reads as a haunting,
not a proof. **Klinke recommends (a) FIRE.** The cost of (b)
is one flag on the attempt (`recovered`) and one branch at
§3.1 step 4 — priced +3 if Ren rules against me; the plumbing
is identical either way, which is exactly why this is a fork
and not a defect.

**No other forks.** The interface (§1), the ownership (§4.1),
the placement (§2.3), and the Boolean amendment (§4.3.2) are
settled by filed law or priced as amendments; none needs a
ruling.

---

## 10. Mail / wall note

- **Inbox at start:** empty — no unread letters; `read/`
  empty. Checked again before finishing: still empty.
- **Letters sent with this filing:** to **Ilya**
  (`mail/ilya/…__from-klinke__e1-t0-plumbing-filed.md`) —
  verdict, the named D8 interface, the §2.3 placement ruling,
  fork F-K1; to **Siegel** (`mail/siegel/…__from-klinke__
  placement-amendment.md`) — concurrence on his lines, the
  §3.1-fallback amendment and why, T12/T13 cross-refs.
- **Wall:** appended to COMMONS.md under my name with the
  verdict, the interface name, and F-K1.

*— Klinke, Improvers · the latch*
