# SLICE E — LATCH (Improvers, the FLOW key): FLOW THROUGH EVERY JOINT

**Seat:** Latch, Improvers — I own the FLOW key and the mode it engages.
**Joint:** FLOW (stopwatch) sessions through Slice E — the exceptions
register. The D/E file (SLICE-D-E-BUILD-READY.md §2) specs E1–E6 almost
entirely in timed-session language; this file answers, for each joint:
*applies unchanged / applies as amended (exact amendment) / cannot
apply (the law that forbids it)*.
**Method:** every line verified this run against today's trunk
(`~/workspace/clone-inspect/Focus-app`, read only, no gradle run).
Inbox read at start: no unread letters (four in `read/` — Floor's
legend floor, Moss's trap, Ilya ×2 on voicing and Wild's deletion;
none touches Slice E timing, and Moss's trap is answered where it
intersects E1, §1.3).

---

## 0 — VERDICT TABLE

| Joint | Verdict | Price delta |
|---|---|---|
| **E1 · The Throw at a FLOW arm** | **Applies as amended — test-only.** The PROVING transition is mode-blind; the Throw fires bit-identically. One acceptance amendment (T-FLOW, §1.4). Production Δ0. | +15 tests |
| **E3 · Vertigo at a FLOW arm** | **Applies as amended — one table row.** Glyph/ground choreography identical; the hero's content cut is word→count-up, not span→count-down (§2.2). Production Δ0. | +10 tests |
| **E4 · The finish** | **FORK F-L1 (§3).** A FLOW session has no zero-crossing; its past-floor disarm is neither §5.7's FINISH nor its BROKE in the plan's literal text. Recommendation: it **is** an E0, with pulse, under an amended held-frame picture. | +30 prod, +25 tests if ruled as recommended |
| **E6 · Sled Goes Home at a FLOW finish** | **Applies unchanged**, conditional on F-L1 (its anchor is E4's receipt). Fen already priced the disarm path's ReceiptState publication. | Δ0 |
| **E5b · Orange exit under FLOW** | **Applies unchanged.** Every carrier verified mode-agnostic; no FLOW-born orange carrier exists that Mara missed (§5.2 sweep). | Δ0 |
| **Span derivations** | **No E surface lies** (§6, proved per reader). `spanElapsed` ≡ `elapsed` for every FLOW session by invariant; `spanRemaining` is floor-remaining and no E surface reads it as session-remaining. Two collateral lies outside E's price, named in §6.4. | Δ0 in E |

**Total FLOW delta against Slice E's ≈1,700 net: ≈ +30 production,
+50 tests (≈ +80). One fork: F-L1.**

---

## 1 — E1 · THE THROW AT A FLOW ARM

### 1.1 The trace — `startSession`'s stopwatch branch (Repository:950)

`MonolithRepository.kt:950–999`, verified today:

- `:957` — `val isStopwatch = durationMinutes < 10`. This is the
  **only** branch point. It changes exactly three stored facts:
  `targetDurationSeconds` := `STOPWATCH_MIN_FLOOR_SECONDS` = 1200
  (`:958–959`, constant at `:94`), the entity's `isStopwatchMode`
  flag (`:985`), and the HMAC's `isStopwatch` input (`:966–972` —
  per-session signature data, not a voice parameter).
- Everything else is one path: `deactivateAllSessions()` (`:956`),
  one `FocusSessionEntity` construction (`:974–996`), one
  `dao.insertSession` (`:997`), same return type. There is no
  FLOW insert, no FLOW DAO call, no FLOW early return.
- The FLOW arm reaches it by the same door as every arm:
  `MonolithViewModel.armMonolithSession()` (`:909–921`) →
  `repository.startSession(durationMinutes = _dialMinutes.value)`
  (`:913–918`). The FLOW key's own write sets that dial:
  Deck click handler `TactileDeckScreen.kt:311–314` →
  `selectStudyPreset(STOPWATCH_FLOW)` + `onDialMinutesChanged(5)`;
  VM `selectStudyPreset` (`:394–399`) sets dial :=
  `preset.workMinutes` = 5 and blocks := `preset.defaultBlocks` = 1
  (`MonolithModels.kt:68`). Dial 5 < 10 → the branch above.

### 1.2 The PROVING transition never reads the flag

Siegel's lawful call site (01-siegel.md §3.1) is *inside the function
executing the PROVING → SEALED-RUNNING transition, after the session
insert returns, before `startShield`*. That transition is D8's §F2
cascade. Its three links, checked for a mode branch:

- **L1 SESSION-ROW** — the insert itself (§1.1: one path).
- **L2 SHIELD-PROTECTING** — `MonolithShieldService`'s monitor loop
  stamps `protecting = true` after its first full iteration under a
  live session. The loop's one `isStopwatchMode` branch
  (`MonolithShieldService.kt:95–99`) formats the *notification text*
  (`Stopwatch +%02d:%02d (20m floor)`) — it sits after
  `updateSessionProgress` and does not gate the stamp. In the other
  direction the stopwatch branch of `updateSessionProgress`
  (Repository `:1036–1049`) returns the session still active, so the
  loop's exit condition (`!updated.isActive` → stopSelf) can never
  fire spuriously for FLOW. Stamp timing is mode-independent.
- **L3 A11Y-SESSION-ARMED** — the accessibility service's session-Flow
  collector + residency stamp; grep-verified this run: no
  `isStopwatchMode` read anywhere in
  `MonolithAccessibilityService.kt`.

And the voice itself cannot vary: `playSealThrow()` takes **zero
parameters**, `SealThrowCommand` has **zero fields** (Siegel §2/§5).
There is no input through which FLOW-ness could enter the render.
**The Throw fires at a FLOW seal bit-identically — not by discipline,
by construction.** `primeForSeal()` at latch is likewise mode-blind.

### 1.3 The one dishonesty near this joint — and why it is not E1's

Vera's/Moss's divergence is real and sits *before* the seal: the
Deck's idle `isStopwatchMode` (`TactileDeckScreen.kt:122–126`) is
`studyPreset == STOPWATCH_FLOW || dialMinutes < 10`, while the
repository's is `dialMinutes < 10` alone. FLOW preset + wheel turned
to 50 → the Deck renders FLOW / NO LIMIT while `startSession` arms an
honest timed 50-minute session — and the Throw then seals *that*
session, bit-identically, truthfully voicing a seal whose *mode the
screen lied about*. The lie is in the mode derivation, not the voice.
Its fix is already ruled elsewhere and is not priced here:
MASTER-PLAN §2.6's FLOW ruling (single `flowEngaged` flag read by UI
and repository; the `durationMinutes < 10` inference deleted; wheel
floor 10 in timed mode). E1 must not be acceptance-tested through the
divergent path and then blamed or credited for it — hence §1.4.

Siegel's fork **F-S1** (routine arms sound no seal today;
`RoutineAlarmReceiver.kt:45`) gains nothing from FLOW: the receiver
arms with `durationMinutes = RoutineSchedule.durationMinutes(routine)`
— window lengths, never the FLOW key's 5 — and FLOW is a manual mode
with no routine author. F-S1 stands exactly as Siegel filed it.

### 1.4 Amendment (acceptance only) — T-FLOW

Siegel's T1 simulates the three arms by calling the renderer three
times; that proves the buffer, not the arm. Add one integration
assertion, in the style of `ExampleRobolectricTest.kt:177` (which
already asserts a dial<10 session `isStopwatchMode`):

> **T-FLOW:** drive the FLOW key's own write (preset FLOW, dial 5) →
> arm → assert (a) the inserted entity has `isStopwatchMode == true`
> and `targetDurationSeconds == 1200`; (b) exactly **one**
> `SealThrowCommand` is offered on the PROVING → SEALED-RUNNING
> transition; (c) its rendered buffer's SHA-256 equals Siegel's
> `GOLDEN_SHA256`. A FLOW arm that seals a timed session (the §1.3
> divergence, if it survives into the build) fails (a) loudly instead
> of passing as a FLOW seal.

**Price: +15 test lines, +0 production. E1 total stays ≈210 net
(Siegel) + 15 tests.**

---

## 2 — E3 · THE VERTIGO SHOT AT A FLOW ARM

### 2.1 What the shot actually depends on

Wren's shot (wren.md §3, D/E §2-E3) is driven by one `Animatable`
from `t0Mono`, and its choreography inputs are: the hero's content
cut at t=0 (A3: glyph tier lands with the cut, ground steps once at
t=400), the wheel ghost (Rad, 07-rad.md), the task line, the seated
cap + Mara's exit inside Departure, the imprint Strike at t=0, input
death (A4). Of these, exactly **one** reads session mode: the hero's
content. Everything else is a function of `vertigoT` and geometry.

**The wheel ghost at FLOW:** Rad's CAUGHT ruling needs no FLOW
exception. At a FLOW arm the wheel sits in the FLOW basin,
θ_F = 108° (`BlockitCoreViews.kt:403–416`: FLOW seats at 108, travel
108..432); `freezeAtSeal()`/`stopPhysics()` (:918–925) catches it
there exactly as at any detent. The sweep arc
(`sweepNormalized = (θ − 108)/324`, :591–601) reads **0** at the FLOW
seat — an empty arc on the ghost is the truth (no duration was
swept), and the wheel carries no orange (Rad §1.3), so it is not an
E5b carrier either. If the hand flicked the wheel *into* the basin
and slid immediately, the integrator may be in capture-spring
mid-strait at frame F; the catch is mid-flight and honest under the
same ruling — the value comes from state, never from the ghost's
angle. The FLOW/FINISHES row (`TactileDeckScreen.kt:296–335`, the
FLOW key's home) does **not** ghost (Rad §3.1: idle-only for every
arm) — the key leaves at F, and the mode's word survives in the
running branch's `OPEN FLOW • 20M FLOOR` line (Deck `:354`, the
`finishPair` FLOW branch `:156–157`) past the shot.

### 2.2 The amendment — the hero row, FLOW case

Wren's §3.3 table, hero row, is written for a countdown: *"content
hard-cuts at t=0 from span form (`170`/`2H 50M`) to running form
(counting from t=0)."* For FLOW there is no span form and no
count-down. The exact amendment, one row:

> **Hero readout, FLOW arm:** content hard-cuts at t=0 from the word
> **`FLOW`** (idle hero, `TactileDeckScreen.kt:237`) to the running
> form **`00:00` counting UP** (`displaySeconds = elapsedSeconds`,
> Deck `:129` branch), strikes each true second through Recession
> and Settle exactly as filed. Scale invariant 1.0, glyph tier at
> t=0, ground step at t=400 — **unchanged**.

Does it *read* the same? Yes, and arguably cleaner: the timed cut
swaps one number for its remainder; the FLOW cut swaps a *word* for
a *number* — the seal visibly converts an open intention into a
measured fact, which is the shot's whole sentence. Direction
(up vs down) is a fact of the mode, already the running Deck's law
(Deck `:129`), not shot grammar; no motion, easing, tick-train
timestamp (170/320/460/580/680/760), or the t=1050 settle knock
changes. The spectacle audit (Wren §3.4) is untouched: state is not
held (the count-up strikes its seconds), picture is not held, input
died by state change. **Freezes spent: still 0. Ledger stays four.**

### 2.3 Price

The hero content is already a `when` on `isStopwatchMode` in the
shipped Deck (`:129`, `:237`); Wren's shot wiring consumes the same
branch — **+0 production**. Acceptance: extend the filed frame-diff
(Wren §3.1: both entries frame-identical from t=0) with a FLOW arm —
assert transform/blur/alpha samples identical to a timed arm's at
every sampled frame, hero glyphs excepted. **+10 test lines.
E3 stays ≈350 + 10 tests (Rad's ≈142 + 70t share unchanged).**

---

## 3 — E4 · THE FLOW FINISH — **FORK F-L1**

### 3.1 The fact pattern (trunk)

A stopwatch session **never completes in the repository**:
`updateSessionProgress` early-returns for `isStopwatchMode`
(Repository `:1036–1049`) — elapsed copied, `isActive` untouched, the
Safe-Break machinery (`:1052ff`) and the completion flip
(`allBlocksCompleted`) unreachable. There is no zero to cross:
`targetDurationSeconds` = 1200 is a floor, and the count runs past
it indefinitely.

Its finish is his hand, in `MonolithViewModel.requestDisarmChallenge()`
(`:923–945`), and the code already classifies it:

- **Past the floor** (`elapsedSeconds >= STOPWATCH_MIN_FLOOR_SECONDS`,
  `:925–927`): the valve challenge is **bypassed entirely** —
  `disarmActiveSession(completed = true, outcomeReason =
  "Stopwatch Completed")` (`:930–937`). No token spent, no 60s hold,
  `completedSuccessfully` booked **true**.
- **Below the floor**: falls through to the 60s disarm challenge
  (`:940–944`) — i.e. the valve. A below-floor FLOW exit is a BROKE
  by the ordinary road, and **E4 correctly never sees it** (Wren
  §4.1: emergency exits never enter the E-clock). Applies unchanged,
  no amendment, no pulse — §5.7's BROKE silhouette verbatim.

### 3.2 Why the plan does not settle it (the fork is real)

- §5.7's **FINISH** is defined as *"(the timer reaches zero — the
  completion composite…)"*: hero steps to `00:00`, arc stands full.
  A FLOW finish has no zero-crossing and no full arc; the silhouette
  as written cannot be produced truthfully.
- §5.7's **BROKE** is the banked hold completing. The past-floor
  FLOW disarm spends no hold and no token. It is not BROKE either.
- §5.7's pulse law + REGISTER: the pulse is *"reserved strictly for
  verified fully-elapsed sessions"*. Wren §4.1 gates E0 on the
  code's condition `!isActive && completedSuccessfully` — **which a
  past-floor FLOW finish literally satisfies** — and in the same
  paragraph names *"stopwatch-early completions"* as not-E0, without
  defining the term. If "stopwatch-early" means *below-floor*, it is
  already excluded by §3.1's valve routing and the sentence is
  redundant; if it means *any stopwatch finish* (early relative to a
  target that doesn't exist), it contradicts Wren's own gate
  condition. The plan is silent at exactly the joint that matters.
  Forked, as instructed.

### 3.3 The argument — the floor IS the target

Three independent derivations converge:

1. **The entity's own terms.** `startSession` stores
   `targetDurationSeconds = 1200` for a stopwatch session
   (Repository `:958–959`). The session's contracted duration — the
   only one it has — is the floor. At disarm, verified elapsed ≥
   target: the session has elapsed **fully, in the only sense its
   contract defines**. Everything past 20:00 is surplus the user
   donated, not a remainder he abandoned. "Fully elapsed" fails only
   if read as "elapsed to a countdown target", a reading that would
   also deny the word to any session — timed sessions are the only
   ones that *have* countdown targets, and the pulse law was written
   to exclude *broken and death-booked* sessions (the ratified
   08:58 ruling: a session booked BROKEN gets no pulse because the
   pulse is the ear's word for "finished clean"). This session is
   booked the opposite: `completedSuccessfully = true`.
2. **The release machinery is not a celebration.** Release Proof
   exists so the phone's opening is *proved*, link by link, and
   STILL BLOCKED has a home. A FLOW session blocks exactly the same
   apps through exactly the same services. Exempting its finish
   from E0 would leave its links unproven-open and its receipt
   undetained — not a sterner aesthetic, a functional hole. The
   reverse cascade, the detention formula (receipt ≤ E1800), and
   the pawl lift must run for FLOW or not be trusted for anyone.
3. **His act past the floor is a completion, not an escape.** The
   app itself routes it around the valve (VM `:930`): the machine
   already treats past-floor disarm as *done*, not *broken out*.
   E4 dressing it as anything less would contradict the booking the
   repository writes in the same transaction.

### 3.4 Recommendation — F-L1 ruled as follows

> **F-L1 · FLOW FINISH (Latch's recommendation to Ren):**
> A stopwatch disarm at/after the floor **is an E0**: the controller
> executes SEALED-RUNNING → RELEASING on the
> `disarmActiveSession(completed = true)` edge (VM `:933`), keyed
> once-ever by `(sessionId, completedSuccessfully)` — the same
> once-ever keying as the timed flip, so process death cannot
> re-fire it. HELD FRAME 1200ms whole, reverse cascade inside it,
> **completion pulse at E700**, CUT 0 at E1200, detention formula
> and receipt unchanged.
> **Amended picture (the silhouette must not lie):** the held frame
> does **not** step the hero to `00:00` and does **not** stand an
> arc full. It freezes the count-up readout at its final elapsed
> (`25:00` form — scale continuous 1.0 from the running readout,
> exactly Wren's continuity clause), micro-line = task title +
> **true elapsed length** (`PHYSICS — 25 MIN` — the same grammar,
> fed from `elapsedSeconds`). Settle knock E0, bed out 300, silence
> 400, pulse E700, knock pair E880: timestamps unchanged.
> **E0's source edge is doubled, not moved:** timed E0 rides the
> StateFlow edge of the `updateSessionProgress` flip; FLOW E0 rides
> the disarm edge above, because the flip provably never comes for
> stopwatch (Repository `:1036–1049`). The VM's immediate
> `playHarmonicSeal(arming = false)` at VM `:931` on this branch is
> superseded by the E0 machine (the same mis-spend correction E4
> already makes at VM `:344` for timed completion); Siegel's §4.3
> survivor classification for `:931` stands for its other callers'
> semantics — this branch's call is E4's to re-voice.
> **Not E0, unchanged:** below-floor FLOW exits (valve → BROKE) and
> grace cancels (≤10s) — Wren §4.1 already excludes both.

If Ren rules **against** the pulse/held-frame for FLOW (the honest
counter-case: the pulse should mean *the timer itself finished*,
and a chosen stop, however clean, is a different silhouette), the
cascade + detention + receipt half of F-L1 is **not** optional —
it ships regardless, and only the 1200ms frame and E700 pulse fall
away, replaced by the disarm path's plain CUT. Price difference
between the two rulings: ≈10 lines. The fork is about the ear's
word, not about proof.

### 3.5 Price (as recommended)

Against Wren's E4 ≈410 (timing only): second E0 source edge +
keying (+~18), FLOW held-frame picture branch — freeze count-up
total instead of the `00:00` step (+~12) = **+30 production**;
tests: FLOW E0 fires once from the disarm edge, no E0 from
`updateSessionProgress` for stopwatch, below-floor FLOW produces
no E0/pulse, held-frame total equals final elapsed (**+25 tests**).
**E4 FLOW-inclusive: ≈440 production + 25 tests.**

---

## 4 — E6 · THE SLED GOES HOME AT A FLOW FINISH

**Applies unchanged, conditional on F-L1.** Fen's trigger is
mode-blind by construction: a VM-owned
`ReceiptState(sessionId, receiptPublishedMonoMs)` on the monotonic
clock, published in the emission that first makes the receipt
visible; the return fires once at receipt +1000ms, 650ms powered
(`PathInterpolator(0.3, 0, 0.5, 1)`), total silence, never replays
if unwitnessed / navigated-away / process-dead. Nothing in Fen's
spec, the rest contract (sealed / running / finishing / receipt /
idle, `initialFraction = 1f` once in the host factory), or the
interrupt law (ACTION_DOWN on the view; cap hit-test captures from
the frozen fraction) reads session mode — the sled view has no
stopwatch input to read.

Fen already paid for this convergence: his price table's VM row is
*"VM:327–333 **+ disarm path** — publish ReceiptState… (both end
paths converge upstream)"*. Under F-L1-as-recommended the FLOW
finish publishes its receipt at E1480 by E4's formula like any
finish, so the sled's anchor is E2480 → home by E3130, same numbers.
Under F-L1's counter-ruling (no held frame), the receipt still
publishes (detention formula unchanged) and E6 still anchors to it.
**E6 breaks only if a ruling denied FLOW finishes a receipt at
all** — no seat has proposed that, and §3.3(2) argues it would hole
Release Proof. **Price delta: 0. E6 stays ≈102 net + ~60 tests.**

---

## 5 — E5b · THE ORANGE EXIT UNDER FLOW

### 5.1 Carrier-by-carrier

Mara's exit (D/E §2-E5b): four steps t = 0/50/100/150
(×1.00 / ×0.75 / ×0.50 / terminal token), zero orange from t=200,
no interpolation, driven by one phase value from the shot clock.
Every carrier, checked for a mode input:

| Carrier | Site | FLOW reading |
|---|---|---|
| B1 slide cap | `BlockitCoreViews.kt:1133` | The slide has no mode input; the cap that seats a FLOW arm is the same cap. Exit verbatim. |
| B2 progressive trail α55 | `BlockitCoreViews.kt:1104–1106` | Same — a function of `dragFraction`, not mode. |
| B3 done sub-step numerals (conditional) | `TactileDeckScreen.kt:436` | Task line; mode-blind. Present-or-absent by task state, identically under FLOW. |
| B4 running progress fill (born at seal) | `TactileDeckScreen.kt:346` | Born for FLOW too (running branch composes for any active session) — re-toned at birth to `inkControlFill()`, verbatim. (Its *fraction* is a separate lie; §6.4 — colour is this joint, fraction is not.) |
| B5 chrome session dot (born) | `MonolithCustomComponents.kt:227` | Mode-blind; re-tone to `onBackground` verbatim. |
| B6 grace-abort chip + badge rule (born) | `TactileDeckScreen.kt:512`; badge `MonolithCustomComponents.kt:180` | FLOW sessions get the same grace window (`elapsedSeconds <= 10`, Deck `:505`) — chip composes during the shot's settle for a FLOW arm exactly as for a timed arm. Re-tone verbatim. |

### 5.2 The FLOW-specific sweep (nothing missed)

I swept every `isStopwatchMode` branch in the Deck for an orange
source Mara's list doesn't name: Deck `:308–309` (FLOW key fill —
`inkControlFill()`, ink by construction; Mara already audited the
key clean) and `:322` (its label — `onInkControlFill()`). The FLOW
key itself is deleted at frame F with the idle row (Rad §3.1) and
never enters the exit window as a carrier. The stopwatch running
readout and the `OPEN FLOW • 20M FLOOR` line are ink/`onSurfaceVariant`.
**No FLOW-born or FLOW-only orange carrier exists. The exit's phase
driver is the shot clock (§2), which FLOW shares. Applies unchanged,
price delta 0 — E5b stays ≈50 for the exit (≈130 with D6 hub light,
whose FLOW case Mara already filed: the core is drawn in FLOW, the
press registers nothing, count is never an input).**

---

## 6 — THE SPAN DERIVATIONS — DOES ANY E SURFACE LIE?

### 6.1 The derivations, as shipped (MonolithModels.kt:296–312)

```kotlin
spanTargetSeconds  = n · targetDurationSeconds + penaltySecondsAdded
                     + (n−1) · 600
spanElapsedSeconds = if (isInSafeBreak)
                       (currentBlockIndex−1) · (target+600)
                         + effectiveTargetSeconds
                         + (600 − safeBreakSecondsRemaining)
                     else (currentBlockIndex−1) · (target+600)
                         + elapsedSeconds
spanRemainingSeconds = (spanTargetSeconds − spanElapsedSeconds)
                         .coerceAtLeast(0)
```

The branch is on `isInSafeBreak`, never on `isStopwatchMode` — the
question the brief poses. For a stopwatch session three invariants
hold, all from the Repository `:1036–1049` early return:

- `isInSafeBreak` is **never** true (the break machinery at `:1052`
  is unreachable; `startSession` initialises it false, `:988`).
- `currentBlockIndex` is **never** advanced (stays 1).
- `totalStudyBlocks` = 1 for every FLOW-key arm (preset
  `defaultBlocks = 1`, `MonolithModels.kt:68`, written by
  `selectStudyPreset`, VM `:398`).

### 6.2 The proof — `spanElapsed` is honest, `spanRemaining` is floor-remaining

Substituting the invariants: `spanElapsedSeconds = 0 · cycle +
elapsedSeconds = ` **elapsedSeconds, exactly**, for the session's
whole life, at any elapsed, past any floor. It counts up without
bound and never fabricates a break. Any surface reading span-elapsed
for FLOW reads the truth.

`spanTargetSeconds` = 1200 + penalty — **the floor wearing the
target's name**. Consequently `spanRemainingSeconds` =
max(0, 1200 + penalty − elapsed): a true statement about the *floor*
(remaining obligation), and a false statement about the *session*
(0 from 20:00 onward, while the session runs on). It lies **only
through a reader** who renders it as session-remaining. The
inventory of readers is therefore the whole question:

### 6.3 Every reader, audited

| Reader | Reads | FLOW verdict |
|---|---|---|
| **D6 TIME cell, sealed** (Pia) | frame finishes-at, derived from span-remaining | **Exempted upstream**: Pia §3.2 — FLOW = `NO END` in all states, no crossing sentence. E owes nothing. |
| **E3 hero** (Deck `:129`) | `elapsedSeconds` directly (stopwatch branch), never a span field | Safe. |
| **E4 held frame / receipt facts** (Wren §4.2–4.3) | "true elapsed", deflections, timestamps | Safe — and doubly safe: even a builder who reached for `spanElapsedSeconds` gets `elapsedSeconds` by §6.2's proof. The micro-line's length must come from elapsed (F-L1, §3.4) — with the proof, either source is the same number. |
| **E1 / E5b / E6** | no time field at all (voice / colour phase / receipt timestamp) | Safe by absence. |
| **Desk clock** (trunk: `AirlockAndDisarmOverlays.kt:105`) | branches: stopwatch → `elapsedSeconds`, else `spanRemainingSeconds` | Safe in trunk; in the plan, MASTER §5.8.2 (Nacht) rules the frame itself carries **no remaining and no total** for FLOW — ELAPSED stands alone, bar absent. Consistent, not an E price. |
| **Airlock overlay** (`AirlockAndDisarmOverlays.kt:105`, `:123–124`) | display branches on the flag; the span fraction is guarded `!isStopwatchMode` | Safe. |
| **Now Frame** (MASTER §5.2) | publishes penalty-aware remaining + finishes-at for §5 surfaces | §5.8.2's ruling is frame-level language ("no remaining and no total **in the frame**"), so §5.4's second reading and §5.5's *It will keep until {finish}* inherit the absence. Not an E surface; flagged so the §5 builder doesn't re-derive a FLOW finish from `spanRemaining` = 0 at the floor and print a finish that never happens. Watch item, no E price. |

**Conclusion: no E surface reads a span value for a stopwatch
session and lies.** The derivations' FLOW safety is not luck — it
rests on the two invariants of §6.1, which is why they are stated
as invariants: if a future change ever lets a stopwatch session
enter a Safe Break or advance `currentBlockIndex`, §6.2's proof
dies and this section must be re-run.

### 6.4 Collateral lies — named, **not** priced into E

1. **The running progress track (Deck `:143–149`, `:346`, `:359`).**
   Not a span field, but the same floor-as-target family:
   `progressFraction = elapsed / 1200` clamped to 1.0 for stopwatch,
   printed beside it as a **%**. From 20:00 onward a FLOW session
   shows a full bar and `100%` while running indefinitely — a
   completeness claim the mode forbids. Wren already excluded the
   track from the shot; its post-settle life is the running-Deck
   owner's. Flagged for that owner (and for §5.4's stepped arc,
   whose segment count N = total ÷ 5 has no FLOW total to divide).
2. **Phantom span target via the wheel-only path (trunk only).**
   Dial the wheel under 10 *without* the FLOW key, with blocks
   n > 1 set: `startSession` stores n (coerced 1..8, `:986`) and
   `spanTarget` prices (n−1) phantom breaks + n floors for a session
   that will never break or advance. No E surface reads it (§6.3),
   and MASTER §2.6's flag ruling deletes the path itself. Named so
   the §2.6 builder knows the entity can currently carry it.

---

## 7 — PRICE ROLL-UP (FLOW delta only)

| Joint | Filed (D/E §4) | FLOW delta | FLOW-inclusive |
|---|---|---|---|
| E1 Throw | ≈210 net | +15 tests | ≈210 + 15t |
| E3 Vertigo | ≈350 | +10 tests | ≈350 + 10t |
| E4 Release Proof + finish | ≈410 | +30 prod, +25 tests | ≈440 + 25t |
| E5 (D6 + exit) | ≈130 | 0 | ≈130 |
| E6 Sled | ≈102 + ~60t | 0 | ≈102 + ~60t |
| **Slice E total** | **≈1,700 net** | **≈ +30 prod, +50 tests** | **≈1,780 all-in** |

No migration, no new entity field, no new voice, no new token is
introduced by any FLOW exception in this file. `isStopwatchMode`
already exists on the entity; every amendment above consumes it or
the disarm edge that already exists.

## 8 — ACCEPTANCE TESTS (FLOW set, consolidated)

1. **T-FLOW** (§1.4): FLOW-key arm → entity `isStopwatchMode`,
   target 1200, exactly one `SealThrowCommand`, buffer hash =
   GOLDEN.
2. **E3 frame-diff** (§2.3): FLOW arm vs timed arm — identical
   transform/blur/alpha samples t=0..1050, hero glyphs excepted;
   wheel ghost θ = 108° at F, arc sweep 0.
3. **E4 FLOW E0** (§3.5): past-floor disarm fires E0 once from the
   disarm edge; `updateSessionProgress` never fires it for
   stopwatch; process death mid-RELEASING does not re-fire;
   held-frame total == final `elapsedSeconds`; pulse exactly once
   at E700.
4. **E4 exclusions**: below-floor FLOW disarm (valve) and grace
   cancel produce no E0, no pulse, no held frame.
5. **E6 anchor**: FLOW receipt publishes → sled return starts at
   receipt +1000ms, 650ms, silent; unwitnessed receipt → no replay.
6. **E5b zero-orange**: at t ≥ 200 of a FLOW seal, sampled Deck
   pixels contain no accent-family value (B1–B6 all terminal/ink).
7. **Span invariants**: for a stopwatch entity at elapsed ∈
   {0, 1199, 1200, 1500, 7200}, `spanElapsedSeconds == elapsedSeconds`
   and `isInSafeBreak == false && currentBlockIndex == 1`.

## 9 — RISKS

1. **F-L1 is the only fork, and E6 hangs from it** (§4). If it sits
   unruled at the E4 build, the builder will either improvise the
   FLOW finish or ship the VM's current instant disarm-seal at
   `:931` — the mis-spend E4 exists to kill, surviving by default.
   Rule it before E4 builds, not after.
2. **The §1.3 mode divergence can poison T-FLOW's meaning** if the
   §2.6 flag build lands first and changes how a FLOW arm is
   driven; T-FLOW is written against the key's *write*, not the
   dial inference, so it survives the flag change — re-pin its
   driver line in the build's proof either way.
3. **Invariant fragility** (§6.3): the span proof is two invariants
   deep. Test 7 above is the tripwire; it costs nothing and fails
   loudly if the break machinery ever reaches stopwatch.

---

## NEXT

- **What I want to do next:** re-run this register against the
  §2.6 `flowEngaged` flag build the moment it lands — §1.3's
  divergence and §6.4(2)'s phantom path both die there, and
  T-FLOW's driver needs its re-pin verified by the seat that owns
  the key, not assumed.
- **What I could do unassigned:** the same exceptions treatment for
  FLOW through Slice D's strip (D6 TIME is Pia-exempted, but the
  rack's step count and the imprint's sealed tenant at a FLOW seal
  have never been read with the flag in hand), and a FLOW pass over
  §5.4's stepped arc (N = total ÷ 5 with no total — §6.4(1)'s
  sibling, currently ownerless).

— Latch, Improvers · the FLOW key (seated by Ilya)
