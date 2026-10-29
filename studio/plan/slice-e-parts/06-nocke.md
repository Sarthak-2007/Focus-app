# SLICE E · E4 — NOCKE (Improvers, the cam — timing)

**Seat:** Nocke, Improvers — timing. **Coordinator:** Ilya.
**Joint:** E4's timing machine + detention proof (SLICE-D-E-BUILD-READY
§E4; MASTER-PLAN §5.7; REGISTER: HELD FRAME 1200ms, RECEIPT
DETENTION ≤1.8s, completion pulse reserved for verified
fully-elapsed sessions only).
**Scope:** the E0 state machine, the detention arithmetic in every
branch, and the discriminator the filed design silently assumed.
Opal's link checks (D8), Emil's receipt composition, Wren's rack
build and Fen's sled are consumed, not priced.
**Trunk:** `Focus-app`, read this run (Oct 29, ~09:45 IST), DB
version **8**. No code edited; no gradle run. Inbox empty at start
and at finish.

---

## 0 — VERDICT

E4 builds, but **not as filed**. Wren's part closes with "No
migration, no new persistence — the persisted record already
carries everything the formulas re-derive from." That sentence is
false in today's trunk, in two places that decide whether the
finish is lawful:

1. **The flip's row cannot say why the session ended.** Three
   different writers produce the identical row shape
   `isActive=false ∧ completedSuccessfully=true` (natural flip,
   stopwatch completion, routine window end), and `outcomeReason`
   is never persisted on the session (§2). An E0 observer reading
   the row cannot lawfully choose between pulse, no-pulse, and
   no-finish-at-all.
2. **Shield death leaves no trace anywhere.** Shield liveness is
   in-memory only, by Live Wire doctrine ("Proof is never
   persisted", `service/LiveWire.kt:13-14`). Branch (b) — fully
   elapsed but the shield died mid-session, booked BROKEN, death
   line on the receipt, **no pulse** (§5.6 + §9 B-12 ratified) —
   has its truth existing *nowhere* at E0 today.

The fix is one persisted discriminator, written by the code that
already knows the answer at the moment it ends the session, plus a
persisted shield beat that makes death *detectable by staleness*.
**6 ADD COLUMNs, Migration 9→10** (sequenced after the routines
lane's 8→9, which carries `heldAppsAtSeal`). Fork F-N1, §8 —
recommendation: approve as specced; the zero-migration alternative
is examined and fails on exactly the ambiguity it was meant to
avoid (§2.4).

Everything else in the filed design survives verification: the
held frame, the formula, the fault sheet, the retry cadence. Two
timing amendments are forced by code evidence and made in §4/§5:
E4 must **command** the shield's stop at its cascade step (today
nobody does on the finish path, and the shield's self-stop polls
at 2000ms — relying on it would fault healthy finishes), and the
death-booked held frame keeps its E880 knock pair but leaves the
E700 pulse slot **empty**.

---

## 1 — TRUNK FACTS, VERIFIED (today's lines)

### 1.1 The flip and its discovery

- The natural flip is written inside
  `MonolithRepository.updateSessionProgress`
  (`data/MonolithRepository.kt:1007`): `allBlocksCompleted` at
  **:1096**, the ending copy at **:1100-1113** writes
  `isActive = !allBlocksCompleted`,
  `completedSuccessfully = allBlocksCompleted`,
  `tamperDetected = tamper` (HMAC clock-tamper — *not* shield
  death; the two must never be conflated).
- It has **two callers on two clocks**: the VM ticker
  (`ui/MonolithViewModel.kt:342`, `startTickerLoop` at **:330**,
  `delay(1000L)`) and the Shield monitor loop
  (`service/MonolithShieldService.kt:74`, `delay(2000L)`).
- The VM discovers the flip from the ticker's return value and
  answers with the **seal**: `playHarmonicSeal(arming = false)` at
  `MonolithViewModel.kt:344`. Discovery is up to **1000ms late**,
  as filed.
- Stopwatch sessions **never flip**: `updateSessionProgress`
  early-returns for `isStopwatchMode` (Repository:1049-1062) with
  `isActive` untouched.
- The StateFlow edge exists: `activeSession`
  (`MonolithViewModel.kt:280`) ← `repository.activeSessionFlow`
  (`MonolithRepository.kt:81`) ← `dao.getActiveSessionFlow()`
  (`MonolithDao.kt:57-58`, `WHERE isActive = 1 … LIMIT 1`). Every
  ending — flip or disarm — drives it to `null`. **The edge says
  "ended"; it cannot say how.** There is no `getSessionById` DAO
  query today (E4 adds one, §6).

### 1.2 The pulse is spent three times, once by the valve itself

`playCompletionPulse` (`service/TactileSoundEngine.kt:360`, gain
0.68) has exactly three production call sites today:

| Site | Spender |
|---|---|
| `ui/MonolithViewModel.kt:159` | `triggerRatchetWarning` — a *warning* (as filed) |
| `ui/views/BlockitCoreViews.kt:219` | `fireFeel`'s else-branch: any `hapticMs ∉ {1,4,8}` routes to the pulse |
| `ui/screens/FocusSequencerScreen.kt:377` | the task tear's sole sound (Counterfoil §2.4) |

The brief's suspect resolves through the middle row: the live
caller of the else-branch is
`ui/views/BlockitSuitcaseAndSwitchesViews.kt:572` — the emergency
valve's 60s hold, on completion, fires `fireFeel(10L)`. **Today
the completion pulse sounds at the moment a session is broken by
the valve** — the precise inverse of the Register's reservation.
(The second `fireFeel(10L)`, `BlockitCoreViews.kt:1184`, fires
only in the engine-null fallback where the seal was intended.)
E4's reclaim (§5.4): the ratchet call is deleted, `fireFeel`'s
else-branch re-maps to `playFirmClick()`, and the tear collision
is fork F-N2.

### 1.3 Every writer that ends a session (complete map)

| # | Writer (site) | Row written | Reason known at write |
|---|---|---|---|
| W1 | `updateSessionProgress` flip (Repository:1100-1113) | `completedSuccessfully=true` | full elapse (+ HMAC tamper flag) |
| W2 | `disarmActiveSession` via `completeEmergencyBreak` (Repository:1245; txn :1222) | `completedSuccessfully=false` | valve — **and a spend row** `EmergencyBreakSpendEntity(sessionId)` in the same transaction (Models:326-332) |
| W3 | `disarmActiveSession` via VM `requestDisarmChallenge` (VM:933) | `completedSuccessfully=**true**` | "Stopwatch Completed" (past the 20-min floor) |
| W4 | `disarmActiveSession` via VM `confirmDisarmSession` (VM:959) | `completedSuccessfully=false` | "Ended Early" / "Accidental Start Grace Cancel" (`MainActivity.kt:333`) |
| W5 | `disarmActiveSession` via `RoutineAlarmReceiver` (service/RoutineAlarmReceiver.kt:61) | `completedSuccessfully=**true**` | "Routine Window Ended" |
| W6 | `disarmRoutineSessionByBreak` (Repository:1263-1272) | `completedSuccessfully=false` | routine valve branch — no reason carried at all |

`disarmActiveSession` itself: `MonolithRepository.kt:1274-1310`.

### 1.4 What is persisted about endings today

- `outcomeReason` is a **transient parameter**. Its only
  persistence is an `InterceptLogEntity` row with
  `outcome = outcomeReason` (Repository:1292-1303), inserted
  **only when `!completed && !isGraceCancel`** — i.e. for W2/W4's
  non-grace cases. For every `completed=true` ending (W1, W3, W5)
  nothing is recorded anywhere.
- `FocusSessionEntity` (Models:249-274) carries
  `completedSuccessfully` + `tamperDetected` and nothing else
  about the ending. No completion timestamp exists (as E6's part
  already found).
- **No `lastReceipt` slot, no sequence counter, no
  composite-fired flag exists anywhere** (grep: zero hits). §5.7's
  once-ever keying is unbuilt, not merely unwired.
- Shield liveness: `MonolithShieldService.instance` /
  `protecting` are `@Volatile` companion fields
  (ShieldService:206-218), commented "in-memory only, never
  persisted"; `onDestroy` clears them (:195-203). The Live Wire
  snapshot (`service/LiveWire.kt:85-89`) reads them live. If the
  shield dies and restarts (START_STICKY), the registry reads
  healthy again — the gap is erased by construction.
- Single process: no `android:process` in the manifest. App death
  = VM death = shield death, together. `MonolithBootReceiver`
  restarts the shield on boot if a row is still active
  (BootReceiver:30-33) — it does not flip or classify anything.

---

## 2 — THE DISCRIMINATOR (the hard problem, developed)

### 2.1 The three branches and where each truth lives today

**(a) Fully elapsed, clean → pulse lawful.** Truth: W1's flip.
But the row it leaves is bit-identical to W3's and W5's rows.
*Exists, but not distinguishable.*

**(b) Fully elapsed, shield died mid-session → BROKEN booking,
death line, NO pulse (B-12).** Truth: **exists nowhere.** No beat,
no gap record, no death flag. The shield was the only witness to
its own life and it kept the record in RAM. `tamperDetected`
cannot substitute: it is HMAC clock-forgery evidence computed in
`computeVerifiedSessionElapsed`, a different fact about a
different adversary (and Maas's lie-hunt shows even it is dropped
on break writes). *Does not exist — must be manufactured during
the session, not reconstructed at E0.*

**(c) Valve-ended → BROKE form, no finish receipt (§5.7/§5.3.8).**
Truth: W2 writes `completedSuccessfully=false`, so the *finish*
observer never mistakes it for (a) — and uniquely, the spend row
keyed by `sessionId` is a second, transactional witness. This
branch is safe today **only because** E4 keys on the flip shape;
it becomes fully explicit under §2.3 at no extra cost.

### 2.2 The shield beat — manufacturing (b)'s truth

The shield's monitor loop already writes the session row every
iteration via `updateSessionProgress` (ShieldService:74). The
beat rides that existing write — **zero added writes**:

- New column `shieldLastBeatWallMillis: Long = 0` on
  `focus_sessions`. `updateSessionProgress` gains a parameter
  `stampShieldBeat: Boolean = false`; the Shield passes `true`
  (ShieldService:74), the VM ticker passes nothing. The ending
  copy and every intermediate copy stamp
  `shieldLastBeatWallMillis = System.currentTimeMillis()` when
  the flag is set. (Wall, not mono: the value must survive
  process death and stay comparable after reboot; mono anchors
  are already carried by `bootCount` discipline elsewhere, and
  wall is the clock the beat's consumers all share.)
- New column `shieldGapSeen: Boolean = false` — the **latch**.
  Staleness at the flip alone misses die-and-restart: a shield
  killed at minute 30 and sticky-restarted at minute 49 presents
  a fresh beat at a minute-50 flip. So the gap is latched *while
  the session runs*, by whichever observer is alive:
  - the VM ticker, each 1s iteration, before its progress call:
    if `nowWall − max(shieldLastBeatWallMillis, startWallTimeMillis) > 10_000`
    while the row is active → write `shieldGapSeen = true` once.
  - the relaunch path (§4, branch U): the same test applied to
    the still-active row at VM init / boot recovery, before any
    resume — a gap that spanned process death is provable from
    the stale beat and is latched then.
  - Threshold **10s = 5 missed beats** (monitor period 2000ms,
    ShieldService loop). The `max(…, startWallTimeMillis)` term
    gives the shield its first 10s to start and stamp; a beat of
    0 with session age > 10s is itself the evidence.
- At the flip, W1 computes the outcome **inside the repository,
  from a fresh snapshot re-read** (`dao.getActiveSessionSnapshot()`
  — the caller's copy can be 2s stale, and writers race, §7 R2):
  `ELAPSED_SHIELD_GAP` if `shieldGapSeen || nowWall −
  shieldLastBeatWallMillis > 10_000`, else `ELAPSED_CLEAN`.

### 2.3 The outcome column — one write per ending, by the knower

New column `outcome: String = ""` on `focus_sessions`, written in
the **same copy** that ends the session, by every writer in §1.3:

| Writer | `outcome` written |
|---|---|
| W1 flip | `ELAPSED_CLEAN` or `ELAPSED_SHIELD_GAP` (§2.2) |
| W2 valve (generic) | `VALVE_BROKE` |
| W3 stopwatch | `STOPWATCH_ENDED` |
| W4 | `GRACE_CANCEL` if the reason contains "Grace Cancel" (the test `disarmActiveSession` already performs at Repository:1277), else `ENDED_EARLY` |
| W5 routine window | `ROUTINE_ENDED` |
| W6 routine valve | `VALVE_BROKE` |

`disarmActiveSession` already receives `outcomeReason` — the
mapping is a 6-arm `when` at Repository:1274, and the existing
intercept-log insert is untouched. W6 stamps its constant
directly. Rows ended before this migration keep `outcome = ""`:
history, never observed by E4 (E4 only ever reads the row whose
ending it just witnessed or discovered, §3).

**E4's law, restated on the column:** the finish composite fires
iff `outcome ∈ {ELAPSED_CLEAN, ELAPSED_SHIELD_GAP}`. The pulse
fires iff `outcome == ELAPSED_CLEAN`. Everything else — valve,
grace, early, stopwatch, routine — never enters the E-clock, by
construction rather than by the observer's inference. (Stopwatch
and routine endings are disarms, not the timer reaching zero;
§5.7's FINISH is the timer's. Their own forms are other slices'
business; E4 guarantees only that it never dresses them as one.)

### 2.4 The zero-migration alternative — examined, rejected

Derive at observation from what exists: spend-row lookup kills
(c) reliably; intercept-log lookup names W2/W4-non-grace. But the
ambiguity that matters — W1 vs W3 vs W5, all
`completedSuccessfully=true` — is exactly where **nothing** is
persisted (§1.4). Inference from shape (stopwatch flag, profile
prefix) misclassifies the routine session whose timer genuinely
reaches zero inside its window, and says nothing about (b) at
all. A discriminator that is right except on the branches the
pulse law turns on is not a discriminator. The column is the
honest price: **say so plainly — E4 needs persistence Wren's
filing said it didn't.**

### 2.5 Migration and sequencing

Trunk is DB **v8** (`data/MonolithDatabase.kt:24`; only
`MIGRATION_7_8` registered, :41/:79). The routines lane owns
**8→9** (15 ADD COLUMNs incl. `heldAppsAtSeal`, per Voss's wall
report — that lane's tree, not this trunk). E4 therefore files
**Migration 9→10**:

```sql
ALTER TABLE focus_sessions ADD COLUMN outcome TEXT NOT NULL DEFAULT '';
ALTER TABLE focus_sessions ADD COLUMN shieldLastBeatWallMillis INTEGER NOT NULL DEFAULT 0;
ALTER TABLE focus_sessions ADD COLUMN shieldGapSeen INTEGER NOT NULL DEFAULT 0;
ALTER TABLE guard_settings ADD COLUMN lastCompositeSessionId INTEGER NOT NULL DEFAULT -1;
ALTER TABLE guard_settings ADD COLUMN lastReceiptOutcome TEXT NOT NULL DEFAULT '';
ALTER TABLE guard_settings ADD COLUMN receiptSequence INTEGER NOT NULL DEFAULT 0;
```

All defaults, no table rebuild, no data rewrite. Build order is a
hard dependency: if 8→9 has not merged when E4 builds, E4 waits —
renumbering under the routines lane is how migration chains die.

---

## 3 — THE E0 STATE MACHINE

**Owner:** a finish controller in the VM (process-scoped,
`viewModelScope`, started in `init` — not in any composition, so
swiping the Deck away cannot cancel the finish mid-detention).

**Observation (replaces the ticker's discovery):** a collector on
`repository.activeSessionFlow`, tracking the previous emission's
`(id, isActive)`. The edge is `prev.isActive == true ∧ current ==
null` for session `prev.id`. On the edge, the controller re-reads
the ended row via the new `dao.getSessionById(prev.id)` and reads
`outcome`. The ticker's flip branch (VM:340-348) is **cut**: the
ticker keeps display ticking, burn-in and the peek pill; it plays
no seal and announces no finish. (E0 acceptance, carried from
Wren's risk 2: E0 = the collector's handling frame, ≤1 frame
after the Room emission; the emission-to-flip-write lag is
measured on device, not assumed.)

**The claim (before any animation, §5.7):** on a FINISH outcome,
the controller first calls
`repository.claimComposite(sessionId, outcome)` — one
`withTransaction` on the `guard_settings` singleton row
(Models:504-508, the §5.7 `lastReceipt` slot's home):

- if `lastCompositeSessionId == sessionId` → returns `null`:
  **SUPPRESSED** — the composite has fired for this session,
  once, ever. No state, no sound, no receipt. Terminal.
- else → sets `lastCompositeSessionId = sessionId`,
  `lastReceiptOutcome = outcome`, `receiptSequence += 1`,
  returns the new sequence (the receipt's `№`). The counter
  increments **only here** — only at a claimed genuine
  completion, per §5.7. Death-booked finishes claim and number
  like clean ones (both are FINISH-form receipts; the valve's
  BROKE gets no receipt and no number). Ruled from §5.7's text;
  stated, not forked.

**States:**

```
IDLE
 └─ edge ∧ outcome ∈ FINISH ∧ claim = № ──► RELEASING(sessionId, №, outcome, e0Mono)
RELEASING   links: L1 CONFIRMED@E0 · L3 PENDING · L2 PENDING
 ├─ all links CONFIRMED-OPEN ──► RELEASED(pawlLiftMono)
 ├─ any link 400ms timeout ──► HALTED(faultSet)   (chain stops; §5)
 └─ formula instant reached ──► RECEIPT_ISSUED(№, faultSet?)
RECEIPT_ISSUED ──► publish ReceiptState(sessionId, №, mono) ──► IDLE
                   (E6's sled trigger consumes ReceiptState at +1000ms)
Any other outcome, or claim = null ──► stay IDLE (no-op, by law)
```

`e0Mono = SystemClock.elapsedRealtime()` sampled in the edge
handler. All held-frame phases are **derived** from `e0Mono` by
the Deck (Wren's derive-don't-replay discipline): phase =
f(now − e0Mono); nothing is scheduled that a recomposition would
need to catch up. The sound events (E700/E880) are fired by the
controller's own finish job against the same `e0Mono`, so a Deck
that isn't composed doesn't mute the room — and a backgrounded
app takes branch U (§4), never a half-heard film.

**The reverse cascade at a finish (timing only; checks are
Opal's D8):** reverse link order **L3 → L2 → L1**, event-driven,
400ms/link timeout-halt — with two facts the filed design didn't
have:

- **L1 is pre-confirmed at E0.** CONFIRMED-OPEN(L1) = the row
  reads `isActive == false` — at a finish, that *is* the flip.
  The cascade's live steps are L3 and L2; L1 enters the map
  CONFIRMED with stamp `e0Mono`.
- **L2 must be commanded by E4.** CONFIRMED-OPEN(L2) = shield
  `instance == null || protecting == false` after `stopShield`
  (Opal's mirror). On the finish path today **nobody calls
  `stopShield`** — the shield notices the flipped row on its own
  2000ms poll and stops itself (ShieldService monitor). An
  E4 that waits for that reads P ≈ E2000 on healthy finishes and
  faults them all. So: at the L2 step the controller calls
  `MonolithShieldService.stopShield(appContext)` (the call the
  disarm paths already make, VM:934/960/982); L2 confirms on the
  frame `protecting` flips in `onStartCommand`'s STOP branch
  (ShieldService:38-44) — tens of ms, witnessed via
  `LiveWire.snapshots`.
- **L3's witness is D8's build, consumed here.** CONFIRMED-OPEN(L3)
  = the a11y service's session collector stamping this id
  inactive. That collector **does not exist in today's trunk**
  (the a11y service has no `getActiveSessionFlow` collection —
  grep-verified; only `lastRootMono` at
  `service/MonolithAccessibilityService.kt:51`). E4 specifies the
  interface it consumes — a `@Volatile armedSessionId /
  inactiveStampMono` pair or equivalent StateFlow — and prices
  only its own side. Named dependency, §7 R3.
- **Halt semantics:** a timeout halts the chain *at that link*:
  downstream links are not commanded (if L3 halts, `stopShield`
  is not called by the cascade — the shield still self-stops
  ≤2000ms by its own poll, uncommanded and unwitnessed, which is
  precisely what the fault says). Already-open links stay open;
  nothing re-shuts. The halted set is the receipt's STILL BLOCKED
  set and the retry's work list (§5.3).

---

## 4 — DETENTION ARITHMETIC, EVERY BRANCH

The filed formula stands, now over a defined `P` (pawl-lift =
final CONFIRMED-OPEN frame) and a defined fault condition:

```
R0 (rack start)  = max(E1200, P)
receipt t=0      = min( max(E1480, R0 + 280), E1800 )
faulted          ⇔  cascade HALTED  ∨  P > E1520
```

| Branch | Condition | Receipt t=0 | Detention | Notes |
|---|---|---|---|---|
| **Happy** | P ≤ E1200 (typical P ≈ E100-E400: L3 stamp ≤150ms + commanded L2 ≤150ms, sequential) | **E1480** | 1480ms | 320ms margin under ceiling |
| **Slow** | E1200 < P ≤ E1520 | P + 280 ∈ (E1480, **E1800**] | ≤1800ms | rack waits for proof; held frame does **not** extend — 1200ms is its whole life in every branch |
| **Faulted** | halt, or P > E1520 | **E1800 exactly** | 1800ms | ENDED-FAULT sheet over the still-defocused Deck (cut still lands E1200); rack completes behind the sheet by ≤ E2080; pawl stays seated; STILL BLOCKED printed once per faulted link, on the receipt only |
| **Death-booked** | outcome = ELAPSED_SHIELD_GAP, any P | per happy/slow/faulted rows above | ≤1800ms | §5 below — same detention, different film: **no pulse** |
| **Valve / grace / early / stopwatch / routine** | outcome ∉ FINISH | — | — | never enters the E-clock; no held frame, no receipt, no № (valve takes §5.7's 0ms BROKE splice, another joint's build) |
| **U — unwitnessed** | flip discovered after the fact (below) | at discovery | 0 from discovery | no replay of any kind |

**Branch U — the unwitnessed finish (the detention proof's hard
case).** §5.7's keying is what survives death: the transition is
persisted, the claim is transactional, so the finish can be
*discovered* without being *repeated*. Discovery points: VM init
(active row already ended — impossible; ended rows aren't active)
— precisely: on relaunch, a still-`isActive` row whose span has
elapsed flips on the first ticker/monitor pass; and a row that
flipped just before death is found by scanning for
`outcome ∈ FINISH ∧ id != lastCompositeSessionId` at VM init.
Both reduce to one rule at discovery instant `D` (monotonic):

- Estimate the true end: same-boot →
  `endMono ≈ startElapsedRealtimeMillis + spanTargetSeconds×1000`
  (row fields, incl. penalty via `spanTargetSeconds`,
  Models:296-298); after reboot (bootCount mismatch) the wall
  analogue from `startWallTimeMillis`. The estimate is used
  **only** to classify witnessed-vs-late and to print true
  elapsed — never to backdate a sound.
- If the app is foregrounded at `D` **and** `D − endMono ≤
  1800ms`: run the witnessed path with E0 := D (the film starts
  on arrival; detention measured from the observed E0 — the only
  E0 a late observer can honestly swear to).
- Else: **claim, number, and issue the receipt at D**, over the
  sharp idle Deck. No held frame, no pulse (its moment is past;
  a late pulse would timestamp a lie), no rack (nobody is
  arriving — Wren's relaunch law, extended from process death to
  backgrounding: a flip observed while the app is not foreground
  takes this branch on next foreground arrival). Detention from
  discovery = 0; detention from true end is unbounded and is
  stated as such — the ≤1.8s law governs detention *after
  observation*, and the claim-before-animation law is the part
  of §5.7 built to outlive the process. The receipt's facts
  (№, death line, true elapsed) all read from the persisted row:
  true in every particular, merely late.
- Backgrounded-but-alive at the flip (process lives, user
  elsewhere): the edge collector still fires and claims at true
  E0; the film and sounds are **not** played into an empty room —
  the controller marks the claim `unwitnessed` and branch U's
  discovery rule runs at next Deck foreground. (Android gives a
  non-service VM no lawful voice in the background; pretending
  otherwise would spec a sound nobody hears and a detention
  nobody can verify.)

---

## 5 — THE HELD FRAME, PER OUTCOME

### 5.1 Clean (ELAPSED_CLEAN) — as filed

E0 settle knock 24ms @ 1.0 · bed out E0-300 · true silence
E300-700 · **pulse at E700**, picture pixel-identical at E699/E700
· power-down knock pair at E880 (24ms @ 1.0, 12ms @ 0.5) ·
imprint SEALED tenant + lip persist through the hold, slug swap
to re-measured IDLE at the E1200 cut, lip fading E1200-1440 under
the rack · CUT 0 at E1200 · RACK 280 from R0. One tap during
E0-1200 skips to the cut (filed skip law, kept).

### 5.2 Death-booked (ELAPSED_SHIELD_GAP) — the B-12 film

Identical clock, one empty slot: bed out 300 · silence · **E700
passes in silence — no pulse, no substitute sound, no haptic
stand-in** (any marker at E700 would dress the session as
completion in a second channel; B-12's ruling is about the
dressing, not the decibels) · E880 knock pair **retained** (the
Room Stops are the room powering down — a fact, true in this
branch too; they are Orson's cadence, not the completion voice) ·
receipt carries `THE SHIELD WAS OFF FOR PART OF THAT SESSION`
(§5.6) in its provenance block; the record books BROKEN. Detention
formula unchanged. If Ren reads B-12 wider — no knock pair either
— the pair's deletion is a 0-line change to this spec; flagged
here so the reading is his, not the builder's.

### 5.3 STILL BLOCKED and silent retry — as filed, wired

STILL BLOCKED prints **once**, on the receipt at issuance, per
faulted link (`INSTAGRAM — STILL BLOCKED (PERMISSION)` grammar is
Opal/Suki's; the *homing* is E4's law: never in the imprint slot,
never reprinted). Retry: only faulted links, **+30s / +5min /
next Deck-open**, silent in all particulars; the retry re-runs
the remaining chain from the halted link (commanding `stopShield`
then, if L2 is in the set). A healed fault lifts the pawl
silently wherever the app is, amends nothing on the issued
receipt, and files to the record. Process death with a fault
outstanding: the retry state is re-derived at relaunch from
`lastReceiptOutcome == ENDED-FAULT-equivalent ∧ links still
shut` — the fault set itself is *not* persisted by E4 (it is
re-checkable reality at Deck-open, which is the third retry
trigger; a persisted fault set would be a second, staler truth).

---

## 6 — PRICE (net lines, production; against today's trunk)

| Piece | Site | Net |
|---|---|---|
| Entity columns (3 session + 3 settings) | `data/MonolithModels.kt` | +7 |
| Migration 9→10 (6 ALTERs) + registration + version | `data/MonolithDatabase.kt` | +22 |
| Outcome stamps: flip classification (fresh re-read + §2.2 test), disarm `when`, W6 constant | `data/MonolithRepository.kt` | +28 |
| Beat: `stampShieldBeat` param + stamp in copies; gap latch write | Repository + `service/MonolithShieldService.kt:74` | +9 |
| `claimComposite` transaction + `getSessionById` | Repository + `data/MonolithDao.kt` | +28 |
| Finish controller: edge collector, claim dispatch, E0 clock, sound schedule (E700/E880), branch-U discovery scan | `ui/MonolithViewModel.kt` | +118 |
| Ticker flip branch cut (seal + stats move to controller) | `ui/MonolithViewModel.kt:340-348` | −6 |
| Reverse-cascade sequencer (L3→L2 over Live Wire snapshots + D8's L3 stamp interface; 400ms timeout-halt; pawl-lift event) | VM / new `FinishRelease.kt` | +95 |
| Silent-retry scheduler (30s / 5min / Deck-open, faulted only) | VM | +40 |
| Pulse reclaim: ratchet call deleted; `fireFeel` else → `playFirmClick` | VM:159; `BlockitCoreViews.kt:219` | +1 |
| Deck wiring: held-frame branch on FinishState, skip tap, defocus-at-cut params, ReceiptState publication for E6 | `ui/screens/TactileDeckScreen.kt` + VM | +53 |
| **Production total** | | **≈ +395 net** |
| Tests (§7) | `app/src/test/…` | ≈ +230 |

Against the filed E4 price (≈410, timing only): the delta is
honest — roughly **+90 of this is the discriminator, beat and
claim persistence Wren's filing priced at zero** ("no migration,
no new persistence"), and the sequencer/deck lines land where
filed. Excluded, as filed: Opal's link-check implementations
(incl. the L3 a11y collector), Emil's receipt composition,
Wren's rack machinery (~110, shared with nav), Fen's sled.

---

## 7 — RISKS

- **R1 · Migration sequencing.** 9→10 presupposes the routines
  lane's 8→9 in the merge train. If another lane files a 9→10
  first, Ren reassigns numbers at merge — the ALTERs themselves
  are order-independent (distinct columns, distinct tables).
- **R2 · Two flip writers race.** VM ticker (1s) and Shield
  monitor (2s) can both be inside `updateSessionProgress` across
  the flip. Both classify from the §2.2 fresh re-read, and the
  classification is a pure function of the row + now — but a beat
  landing between the two reads can straddle the 10s threshold.
  Worst case is a ±1-beat boundary disagreement on a gap of
  ≈10s, i.e. a session whose shield died *at* the boundary is
  booked either way by microseconds. Accepted and stated: the
  threshold is a proof standard, not a physical constant; gaps
  that matter (minutes) classify identically from every seat.
- **R3 · L3 witness is D8's unbuilt joint.** Without the a11y
  session collector, every finish halts at L3 (400ms) and faults.
  E4 must not ship its sequencer enabled before D8's stamp
  exists; acceptance test T6 is the gate. (Fallback if Ren wants
  E4 first: L3 reads CONFIRMED-OPEN from the row emission the
  a11y service already receives via its event gate — weaker
  witness, Opal's grade drops; not recommended, not priced.)
- **R4 · A11y death is not in the discriminator.** §5.6's line
  names the shield, and the beat covers the shield. An a11y-only
  death (shield polling still blocks via usage-stats, degraded)
  books CLEAN today and under this spec. Named residual — widening
  §5.6 is Ren's call, not E4's inference.
- **R5 · E0 emission lag** (Wren's risk 2, carried): Room's
  invalidation-to-emission lag on a loaded device adds to every
  E-timestamp. Acceptance measures it (T2); if it exceeds one
  frame materially, the remedy is the flip's *return value*
  routed from both callers into the controller — priced as a
  +15-line variant, held in reserve, not built on spec.
- **R6 · `guard_settings` is a REPLACE-written row**
  (Repository:729 rewrites it whole for the browser toggle). The
  claim transaction must read-modify-write inside
  `withTransaction` — it does (§3) — and the settings writer must
  not clobber claim fields: it constructs
  `GuardSettingsEntity(browserBlockerEnabled = …)` fresh, which
  under the new columns would reset them to defaults. **Named
  build note:** Repository:729 must copy the existing row. One
  line; T5 tests it.

---

## 8 — FORKS FOR REN

- **F-N1 · The persisted discriminator (the fork).** 6 columns,
  Migration 9→10 after routines' 8→9, outcome written by every
  ending writer, shield beat + gap latch per §2. **Recommendation:
  approve as specced.** The alternative (derive at E0) is disproven
  in §2.4; the live-read alternative (Live Wire at E0) is
  disproven by the restart-erasure in §1.4. Without F-N1, E4 can
  build the film but cannot lawfully decide when to play it —
  the pulse would be gated on a guess, which is the mis-spend
  this slice exists to correct, moved one layer down.
- **F-N2 · Pulse exclusivity vs the tear.** REGISTER reserves the
  pulse for verified fully-elapsed sessions; the task tear spends
  the same function as its settled sole sound (Counterfoil §2.4,
  acquitted by Moss's lie-hunt). Both cannot be literally true of
  one voice. **Recommendation:** the engine gains a distinct
  `playTearPulse()` (same body, different chord ratio — the ear
  keeps the family, the law keeps the reservation; ≈+12 lines,
  Tasks lane's build), and `playCompletionPulse` becomes
  finish-only. E4 is **not blocked** by this ruling: the finish
  calls the pulse either way; exclusivity lands when the tear
  migrates.

No other forks. The detention formula, the counter's scope, the
exclusion of stopwatch/routine endings, and branch U's no-replay
rule are all derived from filed law (§5.7, REGISTER, B-12) and
are stated as rulings of this seat, open to correction, not
questions.

---

## 9 — ACCEPTANCE TESTS

- **T1 · Discriminator matrix** (repository test, in-memory Room,
  `EmergencyBreakRepositoryTest` pattern): drive all six writers
  (W1-W6) → assert the §2.3 outcome on the ended row, including
  W3/W5's `completedSuccessfully=true` rows carrying
  `STOPWATCH_ENDED` / `ROUTINE_ENDED`, never `ELAPSED_*`.
- **T2 · E0 latency**: flip via a test session; assert the
  controller enters RELEASING ≤1 frame after the flow emission,
  and (device) emission lags the repository write by a measured
  figure recorded in the test report — no eyeballing.
- **T3 · Detention formula** (pure function, table-driven):
  P ∈ {E0, E1199, E1200, E1300, E1520, E1521, halt@L3, halt@L2}
  × outcome ∈ {CLEAN, GAP} → receipt instants {E1480, E1480,
  E1480, E1580, E1800, fault/E1800, fault/E1800, fault/E1800};
  assert no branch exceeds E1800 and the held frame never
  exceeds 1200.
- **T4 · Beat & gap**: shield-stamped session, beat frozen at
  T−9s / T−11s / never → flip classifies CLEAN / GAP / GAP;
  gap latched mid-session then shield restarts and beats fresh
  to the flip → still GAP (the restart-erasure case, §2.2).
- **T5 · Claim once-ever**: claim → №=n; claim again (same id,
  new repository instance over the same DB — the process-death
  simulation) → null, counter unchanged; browser-toggle write
  (R6) between claims → claim fields intact. Valve/grace rows →
  claim never callable (controller no-op asserted at VM level).
- **T6 · Cascade timing** (fake link stamps): L3 confirms at
  +120ms, L2 (commanded at L3's frame) at +140ms → P ≤ E400,
  receipt E1480; L3 never confirms → halt at E400, `stopShield`
  never commanded, receipt = ENDED-FAULT at E1800 naming L3;
  retry at +30s re-runs from L3.
- **T7 · Pulse exclusivity** (source-level, builder checklist +
  grep gate in CI terms): `playCompletionPulse` call sites in
  the built tree == {finish controller} ∪ {tear, until F-N2
  lands}; ratchet and `fireFeel` routes dead.
- **T8 · Branch U**: session flips, process killed before
  discovery (claimed-never), relaunch → receipt issued at
  discovery with correct № and death line, no held frame state
  entered, second relaunch → nothing (claim persists).
- **T9 · Migration 9→10** (`TokenMigrationTest` pattern): v9
  fixture with live rows → migrate → columns defaulted, sessions
  and the guard row intact, chain 7→8→9→10 opens clean.

---

## NEXT (per Law 10 — what I want, what I could do)

- **I want:** the cam's seat on E4's build review — when the
  finish controller lands, re-measure T2/T6 against the built
  tree and file the timing verdict (the numbers above are the
  contract I'd be checking against).
- **I could do, unassigned:** the same beat-staleness treatment
  for the a11y service (R4's residual) if Ren widens §5.6; or a
  timing audit of the seal cascade's 210ms cadence against the
  built D-slice, same method as this part.

— Nocke, Improvers · the cam (timing)
