# SLICE E — DRAHT (Improvers, the wire): E4's RELEASE PROOF INTERFACE

*Seat: Draht, Improvers — the wire. Coordinator: Ilya. Joint owned:
the Release Proof interface at the finish — the reverse link set,
its evidence under F6 as ruled by Ren (Oct 29, 09:05, RULINGS.md:
"the stamp prints `PROOF: LIVE BIND` only where the verdict reads
system-side state; otherwise SELF-REPORT or the stamp drops;
END-TO-END is forbidden everywhere"), the cascade's coroutine
plumbing inside E4's 1200ms HELD FRAME, and the silent retry's
wake + persistence. Timing law (the HELD FRAME, the detention
formula, receipt ≤ E1800) is Wren's E4 and is NOT re-specced here;
link definitions at arm are Opal's D8. All citations are against
TODAY's trunk (`Focus-app/app/src/main/java/com/example/…`),
read this run. No code edited; no gradle run. Inbox read at
start: empty.*

---

## 0 — VERDICT, IN ONE BREATH

The reverse set is **not** D8's three links read open. It is
**two witnessed links and one derivation**: R1 SESSION-ROW-DISARMED
(witnessed, and pre-confirmed — it *is* E0), R2 SHIELD-STOPPED
(the only commanded link, and the only link that can fault),
R3 A11Y-GATE-OPEN (derived from R1 by construction — the a11y
service has no cached session state to disarm, so a witness for
it would print INTENT as PROOF). The pawl lifts on R1 ∧ R2 with
R3 derived-true. **`STILL BLOCKED` has exactly one printable
subject at finish — the shield** — and its evidence is the same
grade that lets the arm print LIVE BIND: a framework-callback
registry, never the service's own flag. Wren's filed example
`INSTAGRAM — STILL BLOCKED (PERMISSION)` (wren.md §4.3) names a
per-app link that D8 killed by name; it must not survive into
the receipt grammar (§2.4).

Three trunk facts move the spec, all verified this run:

1. **The Live Wire substrate D8 specced is already built** —
   `service/LiveWire.kt` whole; shield `instance`/`protecting`
   (MonolithShieldService.kt:206–217); a11y `instance`/
   `lastRootMono`/`onServiceConnected` (:39–59). What D8 specced
   and trunk does **not** have: the a11y session-Flow collector
   and `armedSessionId` stamp. §3 rules E4 must not need it.
2. **The flipped row is invisible to the active-session reads.**
   `getActiveSessionFlow`/`getActiveSessionSnapshot`
   (data/MonolithDao.kt:57–61) filter `WHERE isActive = 1` — after
   the completion flip they return **null**, not the row. Opal's
   filed CONFIRMED-OPEN(L1) ("snapshot returns the id with
   `isActive == false`", opal.md §2) cannot be read that way in
   this trunk; R1 needs a by-id query that does not exist yet
   (no `getSessionById` in the DAO — grep-verified).
3. **Nobody stops the shield on the natural finish.** The flip is
   written by `updateSessionProgress`
   (data/MonolithRepository.kt:1007, flip :1102–1113), driven by
   the Shield's own 2000ms monitor loop
   (service/MonolithShieldService.kt:67–149) and the VM's 1s
   ticker (ui/MonolithViewModel.kt:330–366, completion branch
   :340–348). The ticker branch plays the seal sound and never
   calls `stopShield`; the shield dies only when its own loop
   next wakes and sees the inactive row (:76–90). Left as-is,
   shield death lands up to ~2000ms after E0 — outside the
   400ms link budget on a healthy phone. **E4 must command the
   stop itself** (§4.2). Commanding is ours; confirming stays
   system-side.

---

## 1 — THE REVERSE LINK SET AT FINISH

Arm set (D8, for reference): L1 SESSION-ROW · L2
SHIELD-PROTECTING · L3 A11Y-SESSION-ARMED — proven shut at arm,
in parallel, t=0 = max(last CONFIRMED-SHUT, latch + 210ms,
pawl-seated).

Reverse set at finish (this filing — names are new, use them):

| # | Reverse link | CONFIRMED-OPEN means (exact) | Commanded? | Can fault? |
|---|---|---|---|---|
| R1 | **SESSION-ROW-DISARMED** | Row `id` reads `isActive == false ∧ completedSuccessfully == true` by id | No — the flip is E0's trigger, written by `updateSessionProgress` on verified elapsed | **No** (see §2.1) |
| R2 | **SHIELD-STOPPED** | `MonolithShieldService.instance == null` (onDestroy has run) ∧ `protecting == false` | **Yes** — `stopShield` issued at E0 | **Yes** — the only faultable link |
| R3 | **A11Y-GATE-OPEN** | *Derived:* R1 confirmed ∧ (a11y bound ∨ unbound) — the per-event gate reads the flipped row and returns | No | **No** — no fault state exists |

**Order** (Wren: exact reverse of the seal chain): R3, R2, R1.
In substance the order collapses — R3 derives and R1 confirms in
the E0 frame itself; R2 is the cascade's only wait. Stated
plainly so nobody builds a three-stage sequencer for a one-link
wait: **the reverse cascade is one commanded link carrying a
400ms budget, flanked by two stamps taken at E0.**

### 1.1 Why the set is not the same three read open

- **L1 → R1 is a true mirror.** Same object (the row), opposite
  value, read-only on this path. But its *role* inverts: at arm
  it was the first thing dispatched; at finish it is the event
  that starts the clock. A link that is confirmed by the frame
  that starts its own cascade is not a cascade step.
- **L2 → R2 is a mirror with a different witness.** At arm,
  CONFIRMED-SHUT reads a flag the service sets after its first
  loop (`protecting`, :117–120). At finish the corresponding
  flag-read is **not** admissible as the confirm (§2.2): R2's
  witness is the service's *absence*, not its flag.
- **L3 has no mirror.** At arm, L3 proved a state change (the
  service's gate opening for this row). At finish there is no
  corresponding state to change back: the accessibility service
  is system-bound, survives the session by design, and holds **no
  cached session state** — every event re-reads the row fresh
  (`scope.launch { dao.getActiveSessionSnapshot() … if
  (!activeSession.isActive) return }`,
  MonolithAccessibilityService.kt:92–95). The instant R1 flips,
  the next event's gate is open *by construction*. There is
  nothing to witness that R1 does not already prove; a collector
  stamp would show the service *observed* the flip, and
  observation is not the gate. Building it anyway is priced as
  a fork (F-DRAHT-1) and recommended against.

---

## 2 — EVIDENCE, LINK BY LINK (F6 APPLIED)

F6 as ruled: a printed claim stands only where the verdict reads
SYSTEM-side state; self-asserting evidence drops the claim. The
applied reading in SLICE-D-E-BUILD-READY §6/F6 sets the grade
bar the arm already meets: framework-held bind + a system-
delivered event stamp + a live AppOps read — "no leg is a
self-assertion." Each reverse read is graded against that bar.

### 2.1 R1 — RECORD-constitutive · printable · stamp = E0

Read: **new** `MonolithDao.getSessionById(id)` (the active
queries filter it out, §0.3.2), returning the row with
`isActive == false ∧ completedSuccessfully == true`; the edge
that fires E0 is the `repository.activeSessionFlow`
(MonolithRepository.kt:81) emission active→null for the sealed
id, confirmed by the by-id read in the same frame (null alone is
ambiguous — annulment-by-deletion also yields null; the by-id
read is what distinguishes *completed* from *un-existed*).

Grade: the row is not evidence *about* the session's end — for
every consumer in the system it **is** the session's end. Both
enforcers take their orders from this exact read (shield loop
:76–81 stopSelfs on it; a11y gate :94–95 returns on it). And on
the FINISH path E4 never writes it: the flip is produced by
`updateSessionProgress` from HMAC-verified elapsed
(`computeVerifiedSessionElapsed`, MonolithRepository.kt:578ff
via :1010), so the controller reads a fact it did not author.
That is the strongest class available to any link in this app,
and it is printable without qualification.

**Fault analysis:** R1 cannot fault inside Release Proof. E0 is
*defined* as the observed flip (Wren E1 amendment: E0 rides the
StateFlow edge, not the ticker); a cascade that starts has R1
confirmed in its first frame. The only branch where R1 reads
shut is process-death re-derivation finding the row still
active — and that is not a faulted release, it is a session
that never finished: no receipt, no ENDED-FAULT, no STILL
BLOCKED (Wren §4.3 re-derivation, unchanged). Any build that
can print a SESSION fault line at finish has invented an
unreachable state.

### 2.2 R2 — SYSTEM-side (framework-callback registry) · printable · the F6 trap lives here

Read: `MonolithShieldService.instance == null`, with
`protecting == false` as a necessary precursor, never as the
confirm.

Why the flag alone drops the claim: `protecting` is the
service's own @Volatile (MonolithShieldService.kt:216–217). It
is set false at the *top* of the ACTION_STOP_SHIELD branch
(:37–41) — the service announcing its intention to stop, before
`stopForeground`/`stopSelf` have run — and `stopShield` itself
swallows every exception (:242–251, "Safe ignore"), so a
command can vanish silently while the flag later flips on a
half-executed stop. A pawl that lifts on `protecting == false`
lifts on a self-report of intent. Under F6 that claim drops.

`instance == null` is a different class of fact: `instance` is
cleared only in `onDestroy` (:195–203) — a framework-invoked
lifecycle callback; the service cannot assert it, it can only
undergo it. This is exactly the grade the arm accepts for the
a11y leg (`instance != null` set in `onServiceConnected`,
MonolithAccessibilityService.kt:55–59, read as the framework-
held bind). Symmetric standard, both directions: **existence
and non-existence are framework-witnessed; flags are
self-reports.** CONFIRMED-OPEN(R2) := instance == null.

One wiring gap in today's trunk: `LiveWire.snapshot()`
(service/LiveWire.kt:64–68) surfaces `shieldProtecting` but not
instance-liveness, and `WireSnapshot` (LiveWire.kt:59–63) has no
field for it — so the `snapshots` StateFlow **cannot emit R2's
confirm edge**: on shield death the snapshot's fields are
unchanged (protecting was already false from the stop branch)
and StateFlow conflation swallows the poke. Fix, priced in §5:
add `shieldInstanceAlive: Boolean` to `WireSnapshot`, read
`MonolithShieldService.instance != null` in `snapshot()`.
`poke()` already fires from `onDestroy` (:200), so the death
edge then emits. No consumer of `snapshots` exists in trunk yet
(grep: none outside LiveWire.kt), so the field addition breaks
nothing.

### 2.3 R3 — DERIVED · no stamp of its own · no fault line, ever

Derivation, stated as the controller's rule:

```
CONFIRMED-OPEN(R3) := CONFIRMED-OPEN(R1)
                      ∧ (a11yBound ∨ ¬a11yBound)   // both branches open
```

- Bound: the gate is a per-event fresh snapshot read
  (MonolithAccessibilityService.kt:92–95); with the row flipped,
  every subsequent event returns before any intercept branch.
  There is no stale armed copy to clear.
- Unbound at finish: nothing exists that could intercept —
  open vacuously, and *stronger* than the bound case, not a
  fault. (An a11y death mid-session is a §5.6 session-health
  fact with its own home on the receipt; it is not a release
  fault and must not be re-homed here.)
- The stamp R3 carries in the link map is `DERIVED(E0)` — a
  distinct mark from `CONFIRMED(mono)`, so no surface can ever
  render it as a witnessed notch (Opal's lens: a derivation
  wearing a witness's fill is the blur this slice exists to
  kill). If the §F7 rack ships its release mirror (fork F9),
  R3's downward leap fires at E0 with the other E0 stamps.

### 2.4 STILL BLOCKED, judged by its own rule

The print is a claim — "this link is still shut" — so F6 grades
its evidence too. Printable set at finish, exhaustively:

| Candidate line | Evidence at halt | Verdict |
|---|---|---|
| `SHIELD — STILL BLOCKED (SERVICE DIDN'T STOP)` | `instance != null` at E0+400 after a commanded stop — framework-callback registry reads the service still alive | **PRINTS** — system-side, same grade as LIVE BIND |
| `SESSION — STILL BLOCKED` | None possible (§2.1: unreachable in this path) | **Never prints** — no evidence can exist |
| `ACCESS — STILL BLOCKED` | None possible (§2.3: no fault state) | **Never prints** — the claim drops under F6 |
| `<APP> — STILL BLOCKED` (Wren's example) | Per-app links were killed by D8 (apps are intercepted on attempt, never shut, never opened) | **Never prints** — fabricated link; the example in wren.md §4.3 is a naming error and is corrected here |

So the receipt's fault grammar at finish is a **one-link
grammar**: zero or one STILL BLOCKED lines, naming SHIELD, with
Suki's cause class for a commanded stop unanswered. That is not
a thinner receipt — it is the receipt telling the only release
lie available to it not to tell. Cause-word note: `(PERMISSION)`
in Wren's example is also wrong for this link — no permission is
involved in a service that refused to die; the cause is the
unanswered stop.

Detention interaction (Wren's formula, untouched): P = R2's
confirm stamp. Happy/slow/faulted branches and the E1800
ENDED-FAULT ceiling stand exactly as filed in wren.md §4.3; this
filing only fixes what P *is*.

---

## 3 — WHAT PERFORMS EACH READ: TODAY vs TO BUILD

| Read | Exists today (site) | Must be built |
|---|---|---|
| E0 edge (active→null, this id) | `activeSessionFlow` (Repository:81 ← Dao:57–58); VM already collects it as `activeSession` StateFlow (MonolithViewModel.kt:280–282) | Edge detector + by-id confirmation in the controller (§4) |
| R1 by-id row read | ✗ — active queries filter `isActive = 1` (Dao:57–61); history snapshot (Dao:64–67) could substitute but is a 60-row scan with the wrong semantics | `getSessionById(id)` query, ~5 lines |
| R2 command (`stopShield`) | ✓ MonolithShieldService.kt:242–251; called today only from disarm paths (VM:934, :960, :982) and RoutineAlarmReceiver:63 — **never on natural completion** | One call at E0 (§4.2) |
| R2 confirm (instance death) | ✓ registry + `onDestroy` clearing + `LiveWire.poke()` (:195–203) | `WireSnapshot.shieldInstanceAlive` field + `snapshot()` leg (LiveWire.kt:59–68), ~4 lines — without it the confirm edge cannot emit (§2.2) |
| R3 derivation | ✓ gate code (A11y:92–95) + `instance` (A11y:44) | Nothing. The a11y session collector is **not** built for E4 (fork F-DRAHT-1) |
| Pawl-lift / RELEASED phase | ✗ — no ArmPhase/RELEASING type exists in trunk (grep: none) | In D8's `ArmProvingController` (opal.md §3.1 already reserves `RELEASING(sessionId, links)` in the same sealed type) — E4 extends that controller, does not grow a second one |

---

## 4 — PLUMBING: THE CASCADE INSIDE THE HELD FRAME

### 4.1 Shape — one collector, one wait, no loops

Constraints honoured: no new polling loops; the tools are
`LiveWire.snapshots` + `poke()`; the VM's 1s ticker keeps display
duties only and **loses its completion branch** (MonolithViewModel
.kt:343–348 — the seal-sound mis-spend dies there, E4/E1 joint).

```
// In ArmProvingController (D8's file), viewModelScope. The VM's
// existing activeSession StateFlow is the E0 source — no new
// collector on the DAO flow, no ticker involvement.

private var sealedId: Int = -1            // set at SEALED_RUNNING
private var prevActiveId: Int? = null

init {
    scope.launch {
        vm.activeSession.collect { row ->
            val wasSealed = prevActiveId == sealedId && sealedId > 0
            prevActiveId = row?.id
            if (wasSealed && row == null) onCompletionEdge(sealedId)
        }
    }
}

private suspend fun onCompletionEdge(id: Int) {
    val rowById = repository.getSessionById(id) ?: return
    if (!rowById.completedSuccessfully || rowById.isActive) return
    // ^ annulment/deletion and disarm exits fall out here:
    //   Release Proof runs on the FINISH flip only (Wren §4.1).

    val e0 = SystemClock.elapsedRealtime()
    _phase.value = RELEASING(id, e0, links = mapOf(
        R1 to CONFIRMED(e0),
        R3 to DERIVED(e0),
        R2 to PENDING))

    MonolithShieldService.stopShield(appContext)      // §4.2

    val confirmMono = withTimeoutOrNull(400L) {
        LiveWire.snapshots.first { !it.shieldInstanceAlive }
        SystemClock.elapsedRealtime()
    }
    if (confirmMono != null) {
        _phase.value = RELEASED(id, pawlLiftMono = confirmMono)
        // Wren's formula consumes pawlLiftMono as P. Done.
    } else {
        _phase.value = RELEASING_FAULT(id, faulted = setOf(R2),
                                     faultedAtMono = e0 + 400)
        ReleaseRetry.onFault(appContext, id)          // §5 of this file
        // Detention formula issues ENDED-FAULT at E1800 (Wren).
    }
}
```

Why this shape is the whole shape:

- **Event-driven wait.** `snapshots.first { … }` suspends on
  the StateFlow; `poke()` from the shield's `onDestroy` is the
  wake. The only timer is `withTimeoutOrNull(400)` — a deadline,
  not a poll: zero periodic reads, zero `delay`-loops added.
  StateFlow semantics give the race for free: if the shield's
  own loop won (flipped the row and stopSelf'd before the VM
  observed the edge — Shield :83–90), the current snapshot
  already reads dead and `first` returns on its initial value
  check, stamp = E0. Both orders, one expression.
- **400ms/link timeout-halt** binds R2, the only link with a
  wait. Halt = the `else` branch: phase stays RELEASING (fault
  variant), already-stamped links keep their stamps, nothing is
  re-shut, nothing downstream exists to hold. The held frame is
  never extended (Wren): the cascade's outcome only moves P
  inside the detention formula.
- **Skip tap** (Wren §4.2) jumps the cut, not the cascade — this
  coroutine is untouched by it; it runs behind the defocused
  Deck in the slow branch exactly as behind the held frame in
  the happy branch.
- **Test seam (build requirement, not a nicety):** the
  controller takes `snapshots: StateFlow<WireSnapshot>` and
  `shieldAlive: () -> Boolean` as constructor parameters
  defaulting to LiveWire's, because `LiveWire` is a global
  object reading service statics — without the seam the F6
  tests in §6 cannot run on the JVM/Robolectric suite that
  trunk already carries (app/build.gradle.kts:129–134).

### 4.2 The E0 stop command — the missing call

At natural completion today the shield stops itself, late
(§0.3.3). E4 issues `stopShield` in the E0 frame. Consequences,
checked: `stopShield` posts ACTION_STOP_SHIELD; the handler
(:37–41) clears the flag, removes the foreground notification,
stopSelfs; `onDestroy` clears the registry and pokes. Typical
latency is a frame-to-tens-of-ms — inside the 400ms budget with
the same OEM cold-start caveat Opal filed as R-1, mirrored:
an OEM that delays service teardown faults R2 on a healthy
phone, and the honest answer is the retry (§5), not a longer
budget. If the shield already died (race above), the
`startService` in `stopShield` delivers the intent to a dead
service — a no-op start of a stop action on a non-running
service; harmless, and the confirm has already landed anyway.

### 4.3 Process death inside RELEASING

Controller state dies with the process (Opal §3.1 law, adopted).
On relaunch the VM finds no active row; re-derivation (Wren
§4.3) reads the by-id row: completed ∧ shield instance state
sampled fresh — instance null ⇒ release stands confirmed,
nothing replays; instance alive (the death ate our stop) ⇒
this is a *faulted release discovered late*: persist the fault
(§5.2), run one retry pass immediately, issue no receipt
replay beyond Wren's rule (pending receipt on its formula
against the original E0 if inside the ceiling, else at once —
its fault lines computed from the fresh sample). The held
frame never replays.

---

## 5 — THE SILENT RETRY: WAKE + PERSISTENCE

Scope: faulted links only — which at finish means exactly one
possible fault: R2, the shield that didn't die. A retry pass =
re-issue `stopShield`, wait ≤400ms on the same `snapshots.first`
wait (headless variant: read–command–delay(400)–re-read inside
`goAsync`), re-read `instance`. Heal = clear the persisted
fault (§5.2); the pawl lifts silently if a controller is alive;
**the issued receipt is never amended** (Wren), the record
simply stops being true.

### 5.1 What wakes it — trunk-checked

| Candidate | Trunk fact | Verdict |
|---|---|---|
| **WorkManager** | Zero references in the tree (grep: no WorkManager/Worker/WorkRequest anywhere) | **Rejected** — a new dependency and subsystem to fire two alarms |
| **AlarmManager** | In trunk and idiomatic: `service/RoutineScheduler.kt` (`setAndAllowWhileIdle`, RTC_WAKEUP, :28–47), receiver pattern in `service/RoutineAlarmReceiver.kt` (`goAsync` + IO coroutine, :24–83), re-armed at boot in `service/MonolithBootReceiver.kt:36–39` | **Adopted** — build a sibling pair on the same pattern |
| **Deck-open** | `MainActivity.kt:149–155` — `LaunchedEffect(currentDestination)` already fires `LiveWire.noteResidencyStart()` on DECK entry | **Adopted** — the third trigger hooks the same effect via `viewModel.onDeckEntered()` |
| viewModelScope delay | Dies with the process; the +30s retry must survive the death that faults often accompany | Rejected as the mechanism (the alarm covers the alive case too — one mechanism, three triggers) |

Build: `ReleaseRetryScheduler` + `ReleaseRetryReceiver` in
`service/`, modelled line-for-line on the Routine pair.
Request codes: the Routine pair spends `routineId*2 + {0,1}`
(RoutineScheduler.kt:66) over small auto-increment ids; the
retry pair takes the fixed codes **917001 (+30s)** and
**917002 (+5min)** — collision-free against any plausible
routine id, and only one faulted release can be outstanding
(§5.3). Alarms are `setAndAllowWhileIdle(RTC_WAKEUP, …)` —
inexact is correct here: a silent retry minutes-precise under
Doze is still silent and still free; no exact-alarm permission
is requested for a healing pass.

Triggers, in order: **alarm 1 at fault+30s · alarm 2 at
fault+5min · Deck-open thereafter** (each Deck entry runs one
pass while a fault persists — Wren's "thereafter on next Deck
open" reads as every open until healed; passes are idempotent
and invisible). `MonolithBootReceiver` re-arms whichever timed
alarm is still owed (alarms die at reboot; the fault doesn't —
§5.2), overdue alarms fire immediately on re-arm.

### 5.2 Where the fault state persists

**On the session row**, riding the already-booked Migration
9→10 (F15 `heldAppsAtSeal`, SLICE-D-E §5 item 5) — the fault is
a fact about *this session's release*, and the row is where its
receipt facts already live:

- `releaseFaultShield: Boolean = false` — a Boolean, not a link
  set: only one link can fault (§2), and a set-valued column
  would pretend otherwise.
- `releaseRetriesDone: Int = 0` — 0, 1, 2 = timed alarms spent;
  Deck-open passes don't increment past the schedule, they just
  run.
- `releaseFaultedAtWallMillis: Long = 0` — wall clock, for the
  record and for boot re-arm arithmetic; all *timing* decisions
  stay on alarms + monotonic reads, never on this field.

Accessors: `MonolithDao` `@Query` update + the by-id read from
§3 (one query serves R1 and the retry). Rejected homes:
SharedPreferences (no state prefs exist in trunk — grep finds
only a comment in CryptoIntegrityManager.kt:16; a one-record
prefs file contradicts the Room-only pattern) and a dedicated
table (the `emergency_break_spends` precedent is an append-only
*ledger*; a fault is a mutable state on an existing row, not a
ledger entry).

### 5.3 Supersede + heal rules

- **New seal supersedes.** A faulted shield that never died is
  the *same instance* the next arm's `startShield` re-drives:
  its monitor loop restarts over the new row and L2 confirms
  shut from reality (Opal). On SEALED_RUNNING for a new id, the
  controller cancels both retry alarms; the old row's fault
  columns stay as written history (true at issuance, never
  reprinted, never retried — the receiver checks its intent's
  sessionId against the latest sealed/completed id and no-ops
  on mismatch). The old fault must never print against the new
  session.
- **Heal writes once.** Any pass that reads `instance == null`
  clears the three columns and cancels outstanding alarms.
  Headless heals (receiver) touch the record only — no pawl
  exists to lift, nothing announces.
- **Exhaustion is silent too.** After the Deck-open passes the
  fault simply remains on the row; the next arm reads reality
  (above). No escalation, no Guard sentence is minted by this
  joint — a stuck shield at next DOWN is the Live Wire's OFF
  story, told by D7's table, not by Release Proof.

---

## 6 — PRICE (net lines, production unless marked)

Against the merged sheet: Wren's E4 ≈410 already carries a
sequencer (~110) and a retry scheduler (~40). This filing
re-prices the interface scope inside it; the delta is stated,
not buried.

| Piece | File | Net |
|---|---|---|
| `WireSnapshot.shieldInstanceAlive` + `snapshot()` leg | service/LiveWire.kt | +4 |
| `getSessionById(id)` query | data/MonolithDao.kt | +5 |
| Reverse cascade in `ArmProvingController`: E0 edge detector, stamps, E0 `stopShield`, `snapshots.first` wait, 400ms halt, RELEASED/FAULT phases (replaces Wren's 110-line sequencer — one commanded link needs no per-link machinery) | new in D8's controller file | ≈70 (−40 vs merged) |
| Retry pair: scheduler + receiver on the Routine pattern | service/ReleaseRetryScheduler.kt + ReleaseRetryReceiver.kt (new) | ≈85 |
| Deck-open hook (`onDeckEntered` + the MainActivity effect line) | ui/MonolithViewModel.kt + MainActivity.kt:149–155 | ≈10 |
| Boot re-arm of owed retry alarms | service/MonolithBootReceiver.kt | ≈8 |
| Fault persistence: 3 columns + accessors (migration SQL rides F15's 9→10; +8 if F15 is refused and it stands alone) | data/MonolithModels.kt + MonolithDao.kt + MonolithDatabase.kt | ≈22 |
| **Draht interface total** | | **≈204** |
| Same scope as merged (sequencer 110 + retry 40) | | 150 |
| **E4 line moves: ≈410 → ≈465** | | **Δ ≈ +55** |

Explicitly **not** built, not priced: the a11y session-Flow
collector for release (§1.1, fork F-DRAHT-1); any per-app open
check; WorkManager. Tests: **≈110 lines** (§7), in trunk's
existing Robolectric/JVM suite.

Risks, named:
- **R-D1 — OEM service-teardown delay** faults R2 on healthy
  phones (mirror of Opal R-1). Mitigation is the retry, never a
  longer link budget; if device trials show it chronic, the
  fork is a standing shield (Opal F-OPAL-3's territory), not a
  constant change here.
- **R-D2 — `stopShield`'s swallowed exceptions** (Shield:242–251)
  mean a command can be issued and lost without any signal.
  The design already treats the command as untrusted — truth is
  only ever the instance read — so the swallow costs a retry
  cycle, never a false confirm. No change proposed; noted so
  nobody "fixes" the confirm to read the flag for convenience.
- **R-D3 — E0 edge vs the shield's own flip.** The shield loop
  can flip the row *and* die before the VM's StateFlow emits;
  §4.1's shape absorbs both orders, but the acceptance test T4
  must force the race, not hope for it.

---

## 7 — ACCEPTANCE TESTS

Controller tests run against the §4.1 seam (fake snapshots flow
+ fake alive-read), Robolectric/JVM per trunk's suite:

- **T1 — Happy finish.** Row flips for the sealed id → E0
  stamped; `stopShield` commanded in the E0 frame; snapshots
  emits `shieldInstanceAlive = false` at +120ms → RELEASED,
  P = +120 ≤ E1200; receipt formula receives P (Wren's test
  asserts E1480).
- **T2 — The F6 test.** Snapshot emits `shieldProtecting =
  false` with `shieldInstanceAlive = true` held past 400ms →
  **no RELEASED, no pawl lift**; phase = RELEASING_FAULT,
  faulted = {R2} only. The flag alone never lifts the pawl.
- **T3 — Fault grammar.** T2's branch issues ENDED-FAULT at
  E1800 with a fault list of exactly one line (SHIELD). Assert
  in every branch of every test: fault lines ⊆ {SHIELD} — no
  SESSION line, no ACCESS line, no per-app line can be produced
  by this code path.
- **T4 — Race, both orders.** (a) Shield dies *before* the E0
  edge is observed → `first` returns on the initial snapshot,
  P = E0. (b) Shield dies at +399ms → RELEASED; at +401ms →
  faulted (the 400ms halt is a wall, both sides tested).
- **T5 — Annulment is not a finish.** Row deleted pre-completion
  (active→null with no by-id row) → no RELEASING, no receipt,
  no fault persisted.
- **T6 — Persistence + death.** Fault at issuance → row carries
  `releaseFaultShield = true, retriesDone = 0`; process death;
  BootReceiver path re-arms alarm 1; receiver pass at +30s with
  instance now null → columns cleared, alarms cancelled,
  receipt state untouched (assert no second issuance).
- **T7 — Supersede.** Fault outstanding for id N; new session
  N+1 seals → retry alarms cancelled; a stale receiver fire for
  N no-ops; N's columns remain as history and print nowhere.
- **T8 — No-poll audit.** During a 400ms unanswered wait, the
  fake snapshots flow records zero controller-initiated reads
  beyond the suspended `first` (confirm arrives only on an
  emission) — the no-new-polling-loops law, tested, not
  asserted.
- **T9 — Deck-open heal.** Fault persisted, timed retries spent;
  Deck entry runs one pass; instance dead → healed silently;
  instance alive → `stopShield` re-issued, still silent.

---

## 8 — FORKS

- **F-DRAHT-1 — R3: derived (this filing) vs witnessed.**
  Witnessed = build Opal's a11y session collector (~35 lines:
  Flow collector in `onServiceConnected`, disarm stamp,
  cancel in `onDestroy`) so R3 carries a `CONFIRMED(mono)`
  stamp. It would prove the service *observed* the flip; the
  gate needs no observation — it re-reads per event (A11y:
  92–95). **Recommendation: DERIVED.** If D8 builds the
  collector anyway for the arm's L3, E4 still reads R3 as
  derived — an available stamp does not oblige a claim.
- **F-DRAHT-2 — Fault persistence rides Migration 9→10 (F15)
  vs stands alone.** Riding shares the migration ceremony with
  `heldAppsAtSeal`; standing alone costs the same ~8 SQL lines
  and decouples E4 from F15's ruling. **Recommendation: ride
  if F15 is adopted as recommended; the columns are specified
  either way and E4 does not block on it.**

No other forks. The link set, the witness for R2, and the
one-line fault grammar are rulings this filing makes inside
its joint, each traced to trunk code or to F6 as ruled.

*— Draht, Improvers · the wire*
