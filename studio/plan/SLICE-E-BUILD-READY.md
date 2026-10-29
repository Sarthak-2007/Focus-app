# SLICE E — BUILD-READY

*Filed by Ilya, coordinator of the Improvers, under Pipeline Law 9
(plan/WORK-QUEUE.md item 3). Stage 3 gate per plan/PIPELINE.md: exact
numbers, exact code sites, a price, named risks, and every open fork
named with a recommended ruling and its price both ways.*

*Sources of law: plan/SLICE-D-E-BUILD-READY.md (Slice E sections) AS
RULED by Ren's fork rulings (mail/marlowe/read/0905) and the B-5
ruling; plan/MASTER-PLAN.md (the Seal lives in §2.6 and §5 — the
brief's "§7" is the Ledger/Drawer; recorded here so the next reader
does not hunt the wrong section); plan/REGISTER.md for every constant;
plan/PLAN-AUDIT.md corrections. Part files: plan/slice-e-parts/01–26.*

---

## 0. SUPERSESSION — THE LAID SEAL IS DEAD IN THIS SLICE

Ruling B-5 (Ren): **the Bit-Identical Seal governs Slice E. The Laid
Seal is dead.** Wherever plan/SLICE-D-E-BUILD-READY.md still prints
Laid Seal passages for Slice E — §0 lines 53–54 ("Slice E is one
joint, the Seal, laid"), the E2 section, finding F14, and the Slice E
step-2 line — **this file supersedes them explicitly.** No joint in
Slice E varies the seal by how the session came to exist. The sound
and the haptic of the seal are functions of nothing: not the hour,
not the app count, not the phase, not whether a man or a routine
armed the session.

Two consequences of B-5, stated now so nobody re-derives them:

1. **The filed §5 sentence "E2's fact rule needs Migration 8→9 to
   land first" is DEAD.** It was true only under the Laid Seal, whose
   fact set E2 would have carried. Under B-5, E2 carries no facts.
   Migration 8→9 remains a predecessor of this slice for other
   reasons (see §9, the dependency chain) — but not for E2.
2. **§3.4 of the Master Plan ("nothing seals at window-open") is not
   contradicted by E2's abolition** — it is a routines question and
   is carried as fork F-S1 in §12, not silently resolved here.

---

## 1. THE PRICE (union, deletes-once, per file)

Counted by Waage (16-waage.md) from the actual insertion regions in
today's trunk, reconciled against every part file on disk, then
adjusted by the coordinator for Nocke's part, which landed after
Waage's count. Tests are always separate. Slide-rewrite regions
(Marlowe's lane, builds/slide) are priced with bands, not false
precision.

| Joint | What it is | Production (net) | Tests |
|---|---|---|---|
| E1 | The Seal Throw — voice of the seal, bit-identical | **≈226** | ≈167 |
| E2 | Seal facts — ABOLISHED by B-5 | **0** | 0 |
| E3 | The seal in the picture — wheel caught, sled ghost, night turn | **≈285** | ≈140 |
| E4 | The undoing — end cascade, detention, receipt, № | **≈550** | ≈230 |
| E5 | The accents leave — hub needle turn, scheme pin | **≈141** | ≈150 |
| E6 | The sled goes home | **≈141** | ≈138 |
| Receipt contract | ReceiptState, assembly, publication, store (Zettel, net of overlap) | **≈109** | ≈80 |
| Nav under the seal | Band lock, landing, PARK wiring (Band) | **≈67** | ≈70 |
| Capture + migration + curves | The held count, Migration 9→10, curve tokens (Fader/Saum/Spule union) | **≈34** | ≈95 |
| Tasks ended-edge | Desk-clock hide re-homed (Zeile) | **≈2** | ≈25 |
| **UNION** | | **≈1,555** (band ≈1,440–1,670) | **≈1,095** |

**The filed ≈1,700 was wrong, and here is exactly how (Waage):** the
D/E file's own E rows sum to 1,202; a per-file union can only shrink
a sum, never grow it. The ≈498 gap was Live Wire / D8-controller
substrate silently carried into Slice E's headline. Waage's count
(net correction −377 = −498 phantom + ≈121 of real growth) was then
overtaken by the late landings — Nocke's persistence, Zettel's
receipt contract, Band's nav lock, Fader's capture, Zeile's edge —
≈232 more of honestly priced machinery no earlier count had seen.
The union lands at ≈1,555: close to the filed headline by
coincidence, and unlike it, accounted line by line. (Test lines:
Regal's 15 OWN gap tests, §13, are indexed but unpriced by any seat
— allow ≈100 above the union's ≈1,095.)

**Contingents — NOT in the union, each priced at its fork in §12:**
F-S1 routine-seal convergence (+18 minimal / +104 full / 0 deferred);
F-L1 receipt host (+35 / +60 / +85); F-L2 FLOW finish (+30 / +25
tests); if F-N1 is refused, E4 loses
≈90 AND its pulse law becomes unbuildable (§12, F-N1, consequence
stated there in full).

**Merge-train position:** token (merged) → counterfoil (merged) →
routines → drawer+digest → slide → chrome/PARK → **Slice E last**.
Full dependency chain: §9.

---

## 2. E1 — THE SEAL THROW (voice of the seal)

*Owners: Siegel (01), Riegel (02). Call site: Klinke (03).*

### 2.1 The law

`playSealThrow()` takes **zero parameters** and its command object
`SealThrowCommand` has **zero fields**. Bit-identity is structural,
not a promise: there is no slot through which a fact could enter.
Exactly one call site exists in production code, owned by the
transition funnel (§2.4). The two proofs that pin it:

- **Buffer-hash golden test** — the rendered throw, hashed over
  44,100 samples, must equal the golden hash on every run, on every
  device profile the harness knows.
- **Haptic capture test** — the vibration waveform emitted at the
  seal must equal the filed pattern exactly (timings AND amplitudes).

### 2.2 The render (exact numbers, REGISTER-bound)

- Body: sine partials at 360 Hz base, weights {0.6, 0.28, 0.37, 2.2},
  chord ratio 1.5, overall gain **0.62** (the seal is quieter than
  the completion pulse's 0.68 — Finish Pass 1 finding, adopted).
- Bolt: xorshift32, seed `0x9E3779B9`, entering at sample 1323
  (+30 ms), low-pass 900 Hz (α = 0.1203), −6 dB, gone by +85 ms.
- Room: two comb filters, 29.7 ms / 31.1 ms, feedback g = 0.836 —
  **a build-time constant**, not a runtime value. Roadmap note only:
  g → 0.78 ships, if ever, as its own commit with its own golden
  hash. It is not a tuning knob in this build.
- Total render 1,000 ms; the audible tail fades across samples
  39,690–44,100. **There is no session bed in the trunk and none is
  built by this slice** (the "bed" recipe is Orson's, unbuilt; E4's
  filed "bed out 300 ms" is dead machinery until a bed build is
  seated). E1 ships lawfully uncaught by any bed.

### 2.3 The haptic is NOT the shipped waveform

Shipped `playHarmonicSeal()` timings [0, 24, 38, 16] ms are kept.
Amplitudes change: [0, 255, **0**, 175] — the middle amplitude goes
110 → **0**. The shipped waveform's middle buzz fills the gap the
design intends to be felt: a strike, a true silence, a settle. E1
files the haptic as designed, not as shipped.

### 2.4 The lane, the prime, and the call site

The engine's serial queue (Waage, verified against
`TactileSoundEngine.kt`, 378 lines, read whole) caps a transient
three ways — `enqueueTransient` coerces to ≤6,000 samples (~136 ms),
the synth loop to 7,056 (160 ms), one render buffer of that size —
and renders serially before a blocking write. A 1,000 ms Throw
truncates silently unless all three caps move together, and its t=0
attack is only true if the buffer is primed before the seal moment.

Therefore E1 builds (Riegel): **its own pre-rendered lane and a
dedicated AudioTrack**, primed at latch, preempting — the Throw never
waits in the shared queue and never mixes. This lane is ≈82 of E1's
price. The cheaper alternative (Siegel's queued form, E1 ≈144) makes
t=0 a hope instead of a property and is NOT recommended; it is priced
here so the trade is visible.

**Call-site placement — Klinke governs** (coordinator ruling, made
during this build): Siegel's filed placement — "after startSession
returns, inside armMonolithSession" — fires up to ~500 ms BEFORE
t=0, ahead of proof and ahead of the re-anchor. Rejected. The Throw
is a statement **inside the t=0 frame execution**, in this order:
claim flag → `sealSession` success → publish SEALED_RUNNING → Throw
last. Nothing sounds before the session provably exists.

### 2.5 The D8 interface E1 hangs off (PROVING — not yet built)

Interface design is filed (Opal, Slice D); **zero lines of it exist
in today's trunk.** Slice E does not build PROVING — but E1's call
site, E3's t=0, and E4's controller all hang off it, so the contract
is stated here as a dependency (§9):

`ArmProvingController` exposing `StateFlow<ArmPhase>` with phases
IDLE / PROVING(attemptId, L, sessionId, links) / JAMMED /
SEALED_RUNNING(sessionId, t0Mono) / RELEASING. The three links:
**L1** SESSION-ROW; **L2** SHIELD-PROTECTING — *must ship
session-keyed*: today's `protecting` is an unkeyed Boolean, a
stale-shield hole (a shield protecting yesterday's session satisfies
today's proof); **L3** A11Y-SESSION-ARMED — the collector does not
exist yet. D8 owns the re-anchor and re-sign (payload
`wall|mono|boot|target|stopwatch`; an un-resigned session earns the
false 900 s tamper penalty). Two amendments from Klinke: the
controller sets lastTick := t0 (+0 lines), and `sealSession` returns
Boolean (+≈2 lines). Klinke's call-site share of E1: ≈+9 production,
+45 tests.

### 2.6 What E1 deletes

F13 census: 17 `playHarmonicSeal` call sites in the trunk. E1 kills
exactly 2: the whole line at `BlockitCoreViews.kt:1184` (including
its completion-pulse fallback) and the site at
`MonolithViewModel.kt:911`. The site at VM:344 is E4's mis-spend and
is untouched by E1. The remaining 14 sites are a separate
reserved-voice sweep, NOT this slice.

### 2.7 E1 sites and price

| File | Change | Net |
|---|---|---|
| `TactileSoundEngine.kt` | Throw renderer (three caps moved together), dedicated lane + AudioTrack, prime/preempt machinery | ≈+232 (engine+lane; Siegel's renderer ≈+90 of it verified against Orson's branch ≈98) |
| `MonolithViewModel.kt` | call-site wiring | ≈+3 |
| `BlockitCoreViews.kt` | −2 (site :1184 deleted whole) | −2 |
| Klinke share | t=0 frame placement | ≈+9 |
| **E1 production** | | **≈226–242** (Waage 226 governs; Riegel's 327 was gross) |
| Tests | buffer-hash golden, haptic capture, prime/preempt, call-site order | ≈152 (+15 T-FLOW, Latch — §1 books E1 tests at ≈167) |

---

## 3. E2 — SEAL FACTS: ABOLISHED (0 lines)

Under the Laid Seal, E2 would have carried the session's facts (hour,
app count, phase) into the seal's rendering. B-5 kills the premise:
the Throw has no parameter slot, so there is nothing for facts to
ride. **E2 is 0 production lines, 0 test lines.** Any future proposal
to make the seal "expressive" must first repeal B-5 in the open; it
cannot arrive as an E2 revival.

What survives of the E2 neighbourhood — and it is not E2 — is the
routines convergence: routine-armed sessions must reach the same
Throw through the same funnel. That machinery is priced as a
contingent at fork F-S1 (§12), because whether a routine seal sounds
at all is Ren's ruling, not this file's.

Routine-side facts the RECEIPT needs (E4, not E2): the routine line
on the slip is fed by routineId + windowStart/EndMillis + the routine
name frozen at seal time into `profileName = "Routine: <name>"`,
rendered in the form `WITHIN FASTED BLOCK · 05:00–08:00` — never the
word "by". Both snapshots a receipt or a seal needs — the routine
stamps and `heldAppsAtSeal` — are captured **in the Repository at
insert, never in a view or a ViewModel** (Reihe; Saum §8 concurs).

---

## 4. E5 — THE ACCENTS LEAVE

*Owners: Nabe (09, hub needle), Horiz (10, scheme pin). Independent
count: Taste (26), E5 all-in ≈152, banded — Waage's 141 governs.*

### 4.1 E5a — the hub needle turns (Nabe ≈85 production, +95 tests)

At the seal the Deck's hub needle leaves its accent bearing and
settles to the neutral bearing. Every number held from Mara/Uhr,
none re-opened:

- Sweep **315°**; rest plateau **±9°**; easing cos² out to **±34°**
  of terminal approach.
- Peak brightness 10% by day, 8% by night.
- PRESS: 45 ms, 1 dp dip. SEAT: 160 ms settle.
- Offset: depth × 2 dp, reaching its full throw at 135° of sweep.
- The travel is gated by one constant, `CORE_TRAVEL_GATE`; the
  device criterion is named `DEVICE_PASS_E5A` — a device that fails
  it loses the travel only, never the turn.

### 4.2 E5b — the scheme pin (Horiz ≈61 production, +55 tests)

At the seal the colour scheme pins: from t=0 to t=400 the app holds
the scheme it sealed in, whatever the system or the hour does; the
pin releases **at** t=400, not after. Finding F23: the pin is a
SCHEME PIN — the driver sits at the root, and the conversion table
is exact under round-half-up (Horiz's table is normative; any
implementation whose rounding differs fails the table tests).

Interaction with E3's night split (§5.3): the pin holds the scheme;
the night turn moves the Deck's hero and ground inside it. They are
different mechanisms on different clocks and must not be merged.

Carriers B1/B2 sit in Marlowe's slide lane at
`builds/slide` :1284–1287 and :1316 — **⚠ RE-PIN after the slide
merge lands**; their trunk addresses will move.

**Chrome warning, stated once here:** the session dot at
`MonolithCustomComponents.kt:227` is E5b's carrier — it is spoken
for. The chrome build's sheet must be told to use a different
carrier; two joints may not share one.

---

## 5. E3 — THE SEAL IN THE PICTURE

*Owners: Zug-II (08, shot state), Rad (07, the wheel), Fenja (24,
Deck-file union count). Waage re-weigh: ≈285 production, ≈130 tests.
Fenja's independent count of the Deck file alone: 184 lines, band
165–205. Zug-II's filed ≈365 was gross; the union governs.*

### 5.1 One shot state

`SealShotState`: a single Animatable, `remember(sessionId)`, whose
clock derives from **t0Mono** — the same t=0 the Throw sounds at
(§2.5). Phases: **180 / 780 / 1050 ms**. Every visual element of the
seal reads this one state; no element keeps its own timer. (Clock
ownership has a fork — F-Z1, §12: VM ground-mirror recommended vs
hoisted state.)

### 5.2 The ghosts — the wheel is CAUGHT, not coasted

At the seal, the wheel and the sled do not animate to rest; they are
**caught** — frozen at the exact fraction and angle the seal found
them in, and held as ghosts while the session runs.

- Wheel: Rad's `freezeAtSeal()` wraps the existing `stopPhysics()`
  at `BlockitCoreViews.kt:918` — angular velocity zeroed, θ frozen.
  A coasted wheel (decelerating to a stop) is a different, longer,
  unreproducible picture; the caught wheel is the seal's signature.
- **The ghost is born at the flip frame F**: `isSessionRunning`
  flips at the insert emission (`TactileDeckScreen.kt:118`) — during
  PROVING, *before* t=0. Therefore both AndroidViews must be
  **hoisted out of the `isSessionRunning` conditional** (wheel at
  Deck :273, sled at Deck :486); left inside, the view is destroyed
  and recreated at fraction 0 at the flip, and the ghost is stillborn.
- Sled ghost fraction = **1.00 by definition of t=0** (the sled is
  what made t=0 happen).

### 5.3 The night turn — and the polluted boolean

`effectiveNight` splits in two: **heroNight** turns at t=0;
**groundNight** turns in one step at t=400. But the boolean is
polluted upstream: `MainActivity.kt:232` ORs the session state into
`isDarkTheme`, and `Theme.kt:104–108` flips the whole colour scheme
on it — so today "night" can mean three different things in three
places. The fix lands at the caller/theme level, not in the Deck:
E3's split is only buildable on a boolean that means one thing.

### 5.4 Input death — 8 surfaces

From t=0 the Deck's input is dead, at **8 surfaces**, including two
that hide: the FLOW key at Deck :310 (inside the wheel branch —
invisible when the wheel is not composed) and the scroll surface at
Deck :176. Law: **dead taps drop at the source.** Queued motion is a
queued tap — anything buffered before t=0 and delivered after is
still a tap on a sealed session, and it drops too.

### 5.5 Spectacle audit

Zero freezes anywhere outside the two ghosts; the Ledger keeps its
4 sanctioned motions and gains none. The seal's spectacle is the
Throw, the caught wheel, and the night turn — nothing else moves.

⚠ **RE-PIN:** E3's slide-region sites move when Marlowe's slide
merge lands; they are flagged in the part files and must be re-pinned
against the merged trunk before E3 is built, not discovered during.

---

## 6. E6 — THE SLED GOES HOME

*Owners: Spule (11, curves + resume law), Schlitten (13, the sled
contract). Waage re-weigh: ≈141 production, ≈138 tests. Schlitten
standalone +147, marginal +134 after the slide rewrite.*

### 6.1 The resume law — Fen's formula is WRONG, replaced

Fen's filed resume formula was recomputed by Spule and **proved
wrong**: it restarts the easing at slope ≈0, while a frozen return
carries velocities down to −2.56 fractions/s; the duration error runs
−105…+44 ms. A resumed return under Fen's formula visibly hesitates,
then hurries. It is replaced by **time-offset resume**: freeze the
animator's playTime at the interrupt; on resume, seek back to the
frozen playTime and play the remainder — remaining = 650 −
frozenPlayTime. The curve is never re-entered; it is re-joined.
(This replacement needs Ren's word only because his F2 adoption took
Fen's form — fork F-RS, §12.)

### 6.2 Who owns what

The **view owns the 650 ms clock** (the return is a view animator
with a literal 650 at the call site — never a duration token, so it
can never be unified with the Shield bank-return's 650 ms spring or
re-owned from Compose). The **ViewModel owns ReceiptState and the
trigger**, plus the once-only gate: Schlitten's `SledReturnGate`, a
4-stage witness/expiry class — an unwitnessed return never runs, a
witnessed one runs exactly once (VM share ≈+49: ReceiptState, gate,
flow, publish + expiry job, wrappers).

### 6.3 The contract (Schlitten)

- The rest contract **replaces the :496 reset** in the Deck's
  `update{}` block: at receipt publication the idle cap words swap in
  as the **last writer of `labelText`** — Aldo's S0, `SLIDE OR TAP TO
  START` — a full detention (1,000 ms) before the return starts, so
  the sled never travels wearing a sealed-era word. The fade law at
  :1111 gains the `|| isReturning` disjunct (the return *reveals* the
  words; it does not occlude them).
- Accent trail suppressed while returning (`BlockitCoreViews.kt:1102`
  gains `&& !isReturning`).
- Interrupt: a DOWN during the return freezes it (time-offset, §6.1);
  hit-testing is against the sled's slab geometry as it stands, not
  its rest position. `onDetachedFromWindow` retires the animator —
  no return survives its view.
- **Silence is total**: Schlitten audited every inherited feel path;
  none fires on the return. The sled goes home quietly.
- **The cap's colour at arrival** (gap found by Schlitten; no seat
  had filed it): Mara's drain leaves the cap in terminal ink;
  F-ORANGE makes the cap the Deck's one accent object at rest. A sled
  that pops orange at f=0 commits, in colour, the teleport E6 exists
  to abolish in position. **Coordinator's ruling, adopted in this
  file:** inside `animateReturnHome()`'s first frame the view resets
  its cap tone to rest (+1 line); the pop hides inside motion onset;
  the sled travels home *as* the rest object. Acceptance test T7
  pins both ends (fill at detention == control fill; fill at arrival
  == `tokAccent()`, day and night). The alternative — rest cap stays
  ink — flips T7 and deletes the line; it is the D-coordinator's to
  argue at review, not a fork for Ren.

### 6.4 Curves (Spule)

`MonolithCurves.kt` gains the `ReturnHome` interpolator
(0.3, 0, 0.5, 1) and the slice's token set — six tokens total across
E's curve needs, ≈+21 net carried in Seams (§8). **No `DURATION_650`
and no `DURATION_160` token is ever created.**

**Shipped leak, flagged to the slide lane:** today's
`animateSpringBack` (280 ms, MechanicalOvershoot) serves BOTH
committed and uncommitted sled returns. R8 forbids one curve serving
both. The slide rewrite must split it; E6's return is not built on
the shared spring.

---

## 7. E4 — THE UNDOING (session end, detention, the receipt)

*Owners: Draht (05, cascade + retry), Nocke (06, timing machine +
discriminator), Fach (25, VM/DAO counts), Linea (19, end-state
frame). Waage pre-Nocke re-weigh 509; coordinator's composition with
Nocke landed: ≈550 production, ≈230 tests.*

### 7.1 One commanded link

The end of a session is a **reverse cascade with exactly one
commanded link**. Nothing else is commanded, because nothing else
can be:

- **R1 SESSION-ROW — constitutive.** The row flip IS the end. It
  needs a new DAO read, `getSessionById`: the active-session query
  filters `isActive = 1`, so the flipped row reads NULL through every
  existing door. The cascade's first act is learning to see the row
  it just flipped.
- **R2 SHIELD-STOPPED — the only faultable link.** Witness:
  `MonolithShieldService.instance == null`. The shield's own
  `protecting == false` report is self-testimony and is **dropped**
  (audit F6): a stuck service reports itself stopped.
- **R3 A11Y — DERIVED, never witnessed.** No collector exists and
  none is built (the witnessed alternative is fork F-D1, §12).

**E4 commands `stopShield` at E0.** Nobody does today: the shield
self-stops ~2 s late, outside the 400 ms budget, on its own
schedule. The end cascade's one command exists because the budget
cannot wait for the shield's manners.

The receipt's blocked line has **exactly one lawful subject**:
`SHIELD — STILL BLOCKED (SERVICE DIDN'T STOP)`. No other component
may be named in that line, because no other link can fault.

### 7.2 The E0 edge

The end edge rides **`sessionHistoryFlow`, NOT `activeSession`**
(Fach): the active flow goes null at the flip for every kind of end
alike — completion, disarm, break — and cannot tell them apart. The
history head can. Guard, non-negotiable: **`!isStopwatchMode`**.
`disarmActiveSession` writes `completedSuccessfully = completed`, so
a stopwatch session disarmed at its floor would fake a completion
edge. The guard is what stands between E4 and a receipt printed for
a session that merely stopped. (Full FLOW law: §7.7.)

### 7.3 Retry — the Routine pattern, not WorkManager

If R2 faults, the release retries on an **AlarmManager pair** —
`ReleaseRetryScheduler` / `ReleaseRetryReceiver` — cloned from the
Routine machinery's shape: persisted schedule, boot re-arm, and a
third trigger on Deck-open (a user who opens the app is a retry
occasion). WorkManager is absent from the project and stays absent;
it was considered and rejected (Draht). Retry state persists in the
9→10 fault columns (§8).

### 7.4 The detention clock (Nocke)

From the E0 edge, one clock, no branches in its arithmetic:

- **Happy path:** the receipt issues at **E+1480 ms**.
- **Slow path:** shield confirmed stopped late — receipt by
  **E+1800 ms** at the latest (+280 of patience, no more).
- **Faulted:** at **E+1800 ms exactly**, the receipt issues as
  **ENDED-FAULT** with the §7.1 blocked line. Not at discovery of
  the fault — at the clock's end. The user never waits longer for bad
  news than for good.
- **Death-booked sessions** (the app died mid-session; the end is
  reconstructed) run **the same clock**, with the E+700 pulse slot
  **empty** — B-12 ratified: no completion pulse sounds for a
  session nobody witnessed ending.
- **Unwitnessed branch U:** if the end is discovered with no live
  edge at all, the receipt issues at discovery. There is no replay.

### 7.5 The № and the claim (Nocke)

Receipts are numbered, and the number is a claim, not a counter:
`claimComposite` runs transactionally against the `guard_settings`
singleton, and **№ increments only on a claimed, genuine
completion**. Three columns ride the singleton (fork F-N1 carries
them). **Trap, named:** `Repository.kt:729` REPLACE-writes that
singleton row — any claim field stored there without fixing :729 is
clobbered by the next settings write. The fix is part of the claim's
price, not a follow-up.

### 7.6 The discriminator — gated on fork F-N1

Everything in §7.4–7.5 presumes the machine can tell a completion
from a disarm from a death. **Today it cannot** (Nocke, proved from
the trunk): the flipped row is writer-blind — three different ending
writers produce identical `isActive=false ∧
completedSuccessfully=true`, `outcomeReason` is never persisted on
the row — and shield death is persisted nowhere (RAM-only;
START_STICKY erases the gaps). The repair — an `outcome` column
written by every ending writer, a persisted shield beat riding the
shield's existing 2 s row write, and a `shieldGapSeen` latch at 10 s
staleness, all riding Migration 9→10 — is **fork F-N1 (§12),
recommended APPROVE, ≈90 production lines inside this joint's
price.** The zero-migration alternative was examined by Nocke and
rejected with evidence. If F-N1 is refused, §7.4's clock still runs
but the pulse and the death line have no lawful decider — see the
fork for the full consequence.

### 7.7 FLOW (stopwatch) sessions (Latch, 18)

Fach's guard (§7.2) is the floor: no FLOW session ever fakes an E0
through the ticker. Latch's full register, joint by joint: E1
applies as amended, test-only (T-FLOW arms through the key's own
write and asserts the golden hash — the Throw is bit-identical for
FLOW by construction, zero parameters, zero fields; +15 tests). E3
applies with one table row (the hero counts **up** from `00:00`;
the wheel ghost is caught at the FLOW basin θ=108° identically;
+10 tests). E5b's six carriers are all mode-agnostic — no FLOW-born
orange carrier exists. The span audit is clean: `spanElapsed ≡
elapsed` for every FLOW session, proved from the Repository:1036
early return. **The one open question is the finish itself** — is a
past-floor FLOW stop an E0 with pulse and held frame? Fork **F-L2**
(§12), Latch recommending YES (+30/+25 contingent). Two collateral
lies named but NOT priced into E: the running track's
`elapsed/1200` fraction prints 100% from the floor onward (running-
Deck owner's), and a phantom `spanTarget` via the wheel-only
dial<10 path (dies with MASTER §2.6's flag ruling).

### 7.8 The end-state frame (Linea)

While the receipt detains, the Deck defocuses behind it: scrim alpha
**0.12**, +32 lines inside this joint. The slip itself persists
settled, never re-strikes, and leaves on the next DOWN or on
navigation away (Linea's frame ruling — it rides whichever host fork
F-L1 selects, §12). **The receipt's host is fork F-L1**: Linea
measured the Deck and there is no lawful room on it — slip core
≈104 dp, typical ≈184 dp, fullest ≈268 dp; the filed reserve is
empty and the sled track is occupied at receipt t=0. Three hosts are
priced at the fork; none is in this joint's union price.

### 7.9 The pulse's mis-spends die here

`playCompletionPulse` is a reserved voice. Today it is spent by:
the ratchet warning (VM:159), the task tear
(`FocusSequencerScreen.kt:377` — Zeile's part, 23, carries the tear's
own ruling), and the fireFeel else-branch reached through the
**emergency valve's own hold-completion**
(`BlockitSuitcaseAndSwitchesViews.kt:572`) — the completion pulse
currently sounds when a session is *broken*. E4's census kills the
mis-spends in its scope; the tear's replacement voice is fork F-N2
(distinct `playTearPulse` recommended; E4 is not blocked by it).

### 7.10 E4 composition (coordinator's, deletes-once)

| Piece | Owner | Net |
|---|---|---|
| VM finish machine (ticker response split from drive) | Fach/Waage | ≈145 |
| Repository once-ever guard | Draht | +4 |
| Reverse cascade in the D8 controller | Draht | ≈70 |
| Retry pair + hooks + boot re-arm | Draht | ≈103 |
| Fault-persistence accessors (§8 columns) | Draht | ≈22 |
| Discriminator + shield beat + № claim (§7.5–7.6) | Nocke | ≈90 |
| Defocus scrim | Linea | ≈32 |
| Deck end-state frame (ex-defocus) | Linea | ≈85 |
| **E4 production** | | **≈550** |
| Tests (Nocke's library; Regal indexes, §13) | | ≈230 |

---

## 8. MIGRATION 9→10 AND THE HELD-COUNT (Saum, 15)

One migration object, `MIGRATION_9_10`, composing on Voss's
`MIGRATION_8_9` — the chain is never forked, and this slice never
ships a second 9→10. As filed it carries **four ADD COLUMNs**:

| Column | Type | Meaning |
|---|---|---|
| `heldAppsAtSeal` | INTEGER NOT NULL DEFAULT **−1** | apps held at the seal. **−1 = sealed before counting existed; 0 is a real count.** No backfill — a backfilled −1 would be a fabricated count, and this app does not fabricate. |
| `releaseFaultShield` | (Draht) | R2 faulted at release |
| `releaseRetriesDone` | (Draht) | retry attempts spent |
| `releaseFaultedAtWallMillis` | (Draht) | when the fault was booked |

**F-DRAHT-2 is resolved: the fault columns ride 9→10** (they were
candidates for 8→9; they are E4's state and they ride E4's
migration). By coordinator ruling (§10.1), a fifth column joins the
filed four: `heldSitesAtSeal` (+2), under Saum's pre-freeze offer —
sites are captured at the seal or omitted forever, never
recomputed. **If Ren approves F-N1**, its columns (session `outcome`,
the shield-beat fields, the `guard_settings` claim columns) ride
**this same migration object** — one migration, never two; the
freeze is the last moment they can join.

**Capture — exactly one write site.** `heldAppsAtSeal` is written
inside `Repository.startSession` (:950; the insert lands at :997),
counting LOCKED + PEEK_2M rows from `dao.getAllAppsSnapshot()`
(finding F18, ruled A). Not in a view, not in the ViewModel, not at
receipt time — at the seal, in the repository, once.

E0's discriminator needs **no column of its own** in this migration
(Draht/Saum) — it reads the history head (§7.2). The discriminator's
*persistence* needs are F-N1's columns, above; the two statements do
not contradict: one is about the edge, the other about the record.

**Price:** migration joint ≈+23 production, +95 tests
(`HeldAppsMigrationTest`, patterned on `TokenMigrationTest`; chain
test 8→9→10 included). Together with Spule's curves (≈+21), the
component prices sum ≈44; the union (§1) books Seams at ≈21 after
overlap absorption into the E4/E6 file counts, which share the
Repository and curves files. B-15 (audit) **closes when Migrations
8→9 AND 9→10 have both landed with chain tests green** — not before.

---

## 9. THE DEPENDENCY CHAIN (plain)

Slice E builds **last** in the merge train. In order, with the state
of each link as filed:

1. **Live Wire substrate — LANDED in trunk.** E's ground. The token
   merge and counterfoil tear are also merged (combined build 1,
   tests 24/24 green).
2. **Slice D steps 2+3 — slide/strip (Marlowe, in flight, builds/
   slide).** E3's slide-region sites, E5b's carriers B1/B2, and E6's
   marginal price all ⚠ RE-PIN against this merge. D's slide merge
   must also land the seat law (no spring-home on success) before or
   with E3.
3. **D8 — PROVING / `ArmProvingController` — NOT YET BUILT.** The
   interface is designed (Opal) and amended (Klinke, §2.5); zero
   lines exist in the trunk. **E1's call site, E3's t=0, and E4's
   cascade controller all hang off this controller.** Slice E does
   not build PROVING, but it cannot be built before PROVING's
   interface exists in code — building E against the paper interface
   alone is how the Laid Seal happened.
4. **Routines rebuild — Migration 8→9 (Voss, in flight).** Predecessor
   for two reasons only: the migration chain (9→10 composes on 8→9,
   §8) and the published routines interface E's convergence would
   consume — `RoutineDerive.derive` / `RoutineHeartbeat.tick`. The
   filed claim that E2 needs 8→9 is dead (§0). If fork F-S1 rules
   the full convergence into this slice, `SessionSealTransition`
   (§12) is written against those two published functions and no
   others.
5. **Slice E's own Migration 9→10** (§8) — lands with E4.
6. **Chrome / PARK — before E in the train, and warned:** chrome
   ships at exactly one carrier; the session dot at
   `MonolithCustomComponents.kt:227` is E5b's (§4.2). Chrome's sheet
   must name a different carrier before it builds, or E5b and chrome
   collide on one view.

**B-15 closes** when 8→9 and 9→10 have both landed with chain tests
green.

### Named risks

- **PROVING slips** → E1/E3/E4 have no lawful host. Mitigation: the
  interface in §2.5 is frozen by this file; a builder who finds it
  insufficient files the gap, never improvises a second controller.
- **The compile queue** (Pipeline law: max two gradle runs at once,
  flag protocol) — E's build competes with the routines, drawer and
  slide lanes on a 2-core machine. E's ≈1,095 test lines are priced;
  their wall-clock is not E's to promise.
- **The shield's manners** — E4's `stopShield` command and the
  session-keyed L2 are unproven on a device; the Robolectric witness
  (`instance == null`) is a model of the service, not the service.
  Device proof is a SHIP-stage gate, not a BUILD-stage claim.
- **Repository:729** — the REPLACE trap (§7.5) sits in a hot file;
  the claim fix must land in the same commit as the claim columns or
  not at all.
- **The gates that never ran** — §F7's gate and the Cap-Crop Gate
  have no run report anywhere (Saum, evidence search of plan/, the
  wall, and mail/ren/). F5's clause — *Tear-Seal deleted unbuilt if
  the gate hasn't run by the Slice E freeze* — is therefore live.
  Fork F-G1. Spule's gap G-1 rides it: the pawl lift has **no named
  easing in any part**; if the rack gate passes, Opal names the
  easing before build, or an unnamed framework default ships by
  accident.
- **Header chrome is an unexamined surface** (Zug-II, letter 0958,
  read in full at the mail-compliance pass): §5.4's input-death
  census covers the Deck's 8 surfaces and §11.1 the band, but the
  header chrome above the Deck was named by Zug-II as needing a
  seat and never got one. If it is live during the shot it is
  Band-class work (small); it must be examined at build review,
  before E3 is built on the assumption that the census is complete.

---

## 10. THE RECEIPT CONTRACT AND THE HELD COUNT

*Owners: Zettel (14, the contract), Fader (12, the count).*

### 10.1 Two phases, two names (Zettel)

- **`PendingReceipt`** — the detained facts, born at E0, invisible,
  internal to E4. Nothing renders from it.
- **`ReceiptState`** — born only at ISSUANCE (E+1480 happy, E+1800
  ceiling, §7.4), carrying **18 fields**, each with a named consumer:
  sessionId, receiptNo, publishedMono, wall clock, restored flag,
  endedFault, stillBlockedShield — plus the frozen provenance: title
  from the row's `intention`, arm/ended-at wall times, span planned/
  sealed seconds, `heldAppsAtSeal`, `heldSitesAtSeal`, routine
  label, banked-break seconds, shieldWasOff, and the
  assembly-reduced deflection list. **There is deliberately no total
  field** — §5.7's prohibition on a deflection total is structural,
  not a rendering choice.

Sole entry point: `MonolithViewModel.publishReceipt(sessionId)`,
with the repository claim transaction (§7.5) beneath it. Gate:
`completedSuccessfully` only. **A valve-ended session publishes NO
receipt** — Fen's "both end paths converge" is overruled by MASTER
§5.7/§5.3.8, and E6 inherits the gate (no receipt, no sled return
trigger). The store is **Room** (the `guard_settings` home — fork
F-N1/F-SCH-2, §12): DataStore has zero usages in the trunk and only
a Room transaction makes № increment + slot overwrite + latch
atomic.

Boundary, precise: **E owns** the type, assembly, publication,
store, rehydration, deflection *reduction*, and the E6 trigger
interface. **Emil's D9 composition** — slip layout, the Strike's
220/380/110 rendering, wording, footer, save-as-image — is NOT in
E's price, and composition receives `ReceiptState` and nothing else:
no DAO access, no Flow access.

Coordinator rulings inside this joint (build craft, not forks):
**`heldSitesAtSeal` rides 9→10** under Saum's pre-freeze +2 offer —
sites are captured or omitted, never recomputed; and on a legacy
`heldAppsAtSeal = −1` row, **the receipt omits the apps figure**
(Fader's ruling, adopted by Zettel mid-run).

Price: Zettel ≈+130 production, +80 tests; booked in §1 at **+109**
after the −21 overlap with Schlitten's publish machinery (the
`publishReceipt` is one function, priced once).

### 10.2 The held count (Fader)

There is **no sealed count in today's trunk** — `heldAppsAtSeal` has
zero hits. What exists is one wrong derivation copied twice, both
LOCKED-only: the Deck strip (`TactileDeckScreen.kt:120`, printed as
`SHUT/WILL SHUT $n APPS`) and the nav badge (`MainActivity.kt:226–
228`). On the seed registry the two wrong numbers print **7** and
**12**; the seal actually holds **10** (7 LOCKED + 3 PEEK_2M). The
count's law:

- Count from `BlockedAppEntity.resolvedTier` — the predicate is
  `resolvedTier != OPEN`, matching both enforcers. The PEEK *lease
  state* (`activePeekUntilElapsedRealtime`) never enters the count:
  enforcers consult it only to momentarily exempt; counting it would
  make a frozen number flicker.
- One shared derivation, `countHeldApps(apps)` (+3), owned by the
  Repository; Uma's live flow must call it too — one numeral, three
  consumers (strip, imprint, receipt), or the lie returns in a
  second costume.
- Capture at **INSERT** inside `startSession` (§8), with snapshot +
  `deactivateAllSessions()` + insert wrapped in a single
  `withTransaction` (in-file precedent: the token ledger) — today's
  `startSession` is non-transactional, and an unwrapped capture can
  freeze a count for a session that never existed.
- One correction to D6's filing: its "structurally guaranteed" strip
  freeze fails for a returning Deck, because the ratchet tightens
  tiers mid-session — the strip's sealed tense switches its source
  to the column (+4). Exactly one writer exists for the column; no
  DAO update for it will ever be written.

Price ≈+16 production (booked +13 in §1 net of Saum's migration
share), ≈95 test lines gross (T-CAP-1…10; the DDL test is shared
with §8's migration test and counted once in the union).

---

## 11. THE FINISH BEYOND THE DECK

*Owners: Band (20, navigation), Zeile (23, Tasks), Wort (17, words).*

### 11.1 The band under the seal (Band, ≈67 production, +70 tests)

Premise correction, filed in the open: the band's view is
`MonolithBottomNavView` (`BlockitCoreViews.kt:1237–1419`, host
`MainActivity.kt:484–510`) — not `MonolithCustomComponents.kt`; that
file carries only Horiz's B5 header dot at :227 (§4.2).

Today the band is composed in every state, outside the Deck's tree,
and **completely ungated**: `onTouchEvent` self-assigns
`selectedTabIndex` before the callback, and `navigateTo` (VM:369) is
unconditional, sound first. One tap at t=0+300 walks off the sealed
session's screen and **amputates the Vertigo** — derive-don't-replay
means the rest of the shot never exists. E therefore locks the band
at **two layers**:

- The **view** consumes the touch, changes no state, and answers with
  one throttled `fireFeel(4L)` acknowledgment per press (MASTER §6.5
  form) — a swallowed tap is felt, not ignored.
- The **VM** refuses inside `navigateTo`, *before* `playRotaryDetent`
  at :370 — a refused navigation makes no sound.

The lock predicate is the **controller's phase** (Draht's
ArmProvingController), never `activeSession` alone: it engages
synchronously at the arm call (closing the one-frame
insert→flow-emission race), and holds PROVING → SEALED → RELEASING
until **rack end** — a session-only gate would re-open during the
held frame, which is Linea's detention trap. Held-frame band taps
route to the finish skip; rack-phase taps are pure swallow.
`bandLocked` must live in the VM beside the controller — if it lives
in the view, the gate silently never engages (Band's named risk).
The no-deferred-navigation proof enumerates all four stores (view
field + animator, VM StateFlow, recomposition `update{}`, persistence);
the swallow writes to none. Lock ≈49.

**The E1200 landing is clean** — the destination never moved, so the
derived selection is DECK and the rack-end detent jump is the
identity. One real residue: `showUnifiedStatusDrawer`
(MainActivity:200) is cleared by nothing — open the drawer, arm
beneath it, land with it open. Fix: the t=0 edge forces all
drawer/PARK state closed (160 ms exit), ≈8. The faulted sheet's
host and the band's life under it are fork **F-B1** (§12); PARK's
wiring out of the fault sheet ≈10, inside Band's 67.

### 11.2 Tasks at the finish (Zeile, ≈2 production, +25 tests)

Tasks learns a session ended through exactly one Boolean —
`isSessionActive`, derived from `activeSession` going null at the
flip. It inherits the flip's **write-side** lateness in full (dual
drivers: VM ticker 1 s, Shield monitor 2 s; ≤1000 ms ceiling while
the VM lives) and none of the branch's: if the Shield wins the write
race, Tasks unseals and the VM's ticker branch *never runs*.

**Two corrections this forces on the filed text.** (1) The D/E
file's "E4 kills the up-to-1000 ms lateness" is true only of
announcement-relative-to-write, false end-to-end — for Deck and
Tasks alike. **E0 is the write instant**: one finish instant,
app-wide. (2) The lawful shape is **two edges**: a coarse *ended*
edge (Tasks, AOD, effectiveNight) and a discriminated *finished*
edge (pulse, cascade, receipt, №, sled — §7's machinery). Forcing
one edge to carry both facts is the smell. Casualty of the single-
edge design: the ticker branch's desk-clock hide (VM:345) is
cause-blind and is skipped entirely when the Shield wins the race —
re-homed to the coarse ended-edge, ≈2 lines (in §1).

A reopened task is **not** an un-finished session — the boundary is
proven, not asserted: ReceiptState has no task field, the № is
consumed inside `claimComposite` only, and §5.7's receipt content
list contains neither `completedTasksSummary` nor
`tasksCompletedCount` — sourcing any receipt line from them at E1480
would leak a mid-detention reopen into the slip, and is forbidden by
name. **Adjacent leak, NOT Slice E's:** Ruth's receipt repair
(drawer tree) is scoped to the still-active session, so a
post-finish reopen leaves the finished session's Vault summary
permanently false — routed to Ren for Ruth's lane; cure ≈+6 via
Nocke's by-id query. Whether Tasks' own rows sit under the detention
gate is fork **F-Z2** (§12): Zeile dissents from Linea — detention
detains the receipt, not the user.

### 11.3 The words (Wort — lexicon, binding on every E surface)

- `SHIELD — STILL BLOCKED (SERVICE DIDN'T STOP)` — **the only
  printable form**: receipt-only, once, never on the imprint. The
  per-app variant (`INSTAGRAM — STILL BLOCKED (PERMISSION)`) is
  DEAD — a fabricated link under the one-link grammar (§7.1). Bare
  `STILL BLOCKED` is a family name for prose, never a printable
  line. Lawful maximum on any slip: **one** such line (Wort RK-4 —
  Linea's F-L1 slip measure priced up to three; the overstatement is
  ≤56 dp and is noted at fork F-L1).
- The imprint swap happens at the **E1200 cut**, not the E0 flip:
  `{n} APPS HELD` → re-measured `{n} APPS WILL SHUT` / `1 APP WILL
  SHUT`. `1 APP HELD` is ruled (parallel grammar) — the trunk prints
  `1 APPS` today; D9 owns that cure and must not carry the wrong form
  into `ImprintSlot`. HELD survives only in this count form, in this
  home.
- `№ 048` — № + space + zero-pad minimum 3, grows to `№ 1000`,
  never truncates. A faulted sheet carries a № (the session
  genuinely completed).
- `BROKE` · `{n} MIN IN` · `OPEN` ·
  `EMERGENCY BREAK USED — NEXT AVAILABLE {WEEKDAY}` (full weekday
  name, caps) stand, home-locked. `ENDED BY EMERGENCY BREAK` is a
  record-rendering label only — valve sessions get no finish
  receipt, and the label is forbidden on the receipt and on the
  ENDED-FAULT sheet.
- `THE SHIELD WAS OFF FOR PART OF THAT SESSION` — receipt
  provenance block only, exact. The footer stands exact:
  `THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY RECORD LIVES IN YPT`
  (Schublade verified the Drawer consistency: the Drawer disowns
  sessions as content, never as existence — CLEAR, conditional on
  `lastReceipt` staying single-slot and no receipt element ever
  rendering in the Drawer).
- `WITHIN FASTED BLOCK · 05:00–08:00` stands; `BY …` is banned;
  ~~manual-overlap sessions get no routine line~~ **[STRUCK
  Oct 29 (Sana, per Ren's ruling F-Z5): a hand-armed session
  inside a routine window gets the `WITHIN …` line at receipt
  assembly; the `BY …` ban stands.]** `MISSED` **never
  prints in E** — a MISSED routine produces zero E0s and zero
  receipts; the silence is the rendering.
- `SLIDE OR TAP TO START` stands as the receipt-state cap word
  (§6.3). Audit F6 verified clean: neither `PROOF: SELF-REPORT` nor
  `END-TO-END` appears as a printable form on any E surface.
- The ENDED-FAULT sheet's own words are specced **nowhere** — fork
  **F-W1** (§12) carries Wort's draft.

---

## 12. OPEN FORKS — WHAT REN MUST RULE

*Fork IDs are the part files' own, with two coordinator renames to
break collisions: Latch's FLOW fork (his file: F-L1) is **F-L2**
here — F-L1 is Linea's, filed first and cited across the wall;
Spule's resume fork (his file: F-S1) is **F-RS** — F-S1 is Siegel's.
Every fork carries the coordinator's recommendation and the price
both ways. Nothing else in this file is open.*

**F-L1 — the receipt has no lawful room on the Deck** (Linea, 19).
Measured: slip core ≈104 dp, typical ≈184 dp, fullest ≈268 dp; the
filed reserve is lawfully empty and the sled track is occupied at
receipt t=0. (Wort's RK-4 trims the fullest case by ≤56 dp — the
STILL BLOCKED line is lawful at most once; the ranking below does
not change.)
- **A — end-state reflow** (the Deck rearranges to make room):
  ≈+85. NOT recommended — it moves the ghosts E3 froze.
- **B — wheel-zone host** (the slip lives over the caught wheel):
  ≈+35. Runner-up; the only reading under which "inline, no
  overlay" survives literally.
- **C — generalise the aperture sheet to all branches**: ≈+60.
  **RECOMMENDED.** It strikes the words "no overlay" from MASTER
  §5.7 and admits what the measurements prove: the Deck cannot hold
  its own receipt. One host, every branch, no special cases.
Riding any option (Linea's frame ruling, not itself a fork): the
slip persists settled, never re-strikes, leaves on next DOWN or
nav-away; the defocus scrim (§7.8) ships with the chosen host.

**F-S1 — does a routine seal sound, and through what** (Siegel 01 /
Reihe 04; Reihe's F-R1/F-R2/F-R3 fold in here). MASTER §3.4 says
nothing seals at window-open; the routines rebuild materialises
sessions at window-open. If routine seals sound at all, B-5 leaves
exactly one lawful shape: the same Throw, through the same funnel.
- **(i) Full convergence**: a named interface
  `SessionSealTransition.proveAndSeal(sessionId, latchMonoMs):
  SealVerdict {SEALED, JAMMED, ANNULLED}` — process-scoped, owns the
  only `playSealThrow` call; callers are the Deck arm at latch and
  `RoutineHeartbeat.tick`'s materialise (a fourth act after
  applySmartLoadout → startSession → startShield), plus an
  in-process tombstone against re-mint/re-annul loops. Needs
  Reihe's routine-E0 machinery (new DAO edge `getSessionByIdFlow`,
  terminal predicate incl. ATTENDED/PARTIAL, SLEEP arm edge; MISSED
  is never an E0). **≈+104 production, +85 tests.** RECOMMENDED if
  routine seals sound in this slice — it is the only branch under
  which B-5's "every seal" is testable (Regal's E-T13 is shelved
  CONFLICT until this is ruled: Reihe's tests fire the Throw on a
  routine arm, Klinke's pin the routine path silent).
- **(ii) Minimal receiver** (Siegel): the funnel learns a second
  receiver, no E0 machinery: **≈+18**. Sounds the Throw without the
  proof predicate — cold-start routine receipts stay unpriced and
  the tombstone problem is inherited by whoever builds them later.
- **(iii) Defer**: routine seals stay silent in this slice; the
  convergence becomes the routines rebuild's capstone. **0 lines.**
  Lawful, and honest about it — but then "the seal" is a Deck-only
  fact until Voss's lane closes it.
- **(iv) F-R3, the cheap funnel-local second call site (+6):
  REJECTED by the coordinator** — it sounds before proof, the exact
  defect §2.4 killed on the Deck path.
Inseparable from the ruling: whether the **SLEEP 20:00** seal sounds
(a man asleep hears nothing; the bit-identity law does not care),
and the **Otis-N1 text amendment** to §3.4 either way.

**F-K1 — the recovery Throw** (Klinke, 03). The process died between
seal and Throw; on relaunch the controller re-witnesses the row. If
all three links confirm, that completion frame is the row's t0.
**RECOMMENDED: the Throw FIRES — once, late, and truthfully** (in
E1's price). The alternative (a suppression flag, the seal stays
silent) costs +3 and writes an exception into B-5: a seal that
provably happened would be the one seal never heard. If any witness
is dead at recovery there is no Throw under either ruling —
annulment, the Guard sentence owns the screen.

**F-N1 + F-SCH-2 — the persistence package, one ruling** (Nocke 06 /
Schublade 21). §7.6: without an `outcome` column written by every
ending writer, a persisted shield beat, and the `shieldGapSeen`
latch, E4 cannot lawfully tell a completion from a disarm from a
death — and the №'s home must be settled in the same breath:
Schublade proved three filed homes for the same two facts (Zettel's
table — already withdrawn by Zettel himself; Nocke's and Reihe's
`guard_settings` columns), and Saum's filed `MIGRATION_9_10`
contains **none** of them.
- **APPROVE (RECOMMENDED):** one unified 9→10 manifest owned by
  Saum — Draht's four columns + the F-N1 columns + `guard_settings`
  claim columns (+3 ALTERs, no extra version bump), Zettel's
  publish-anchor and transaction semantics adopted whole, the
  Repository:729 REPLACE fix in the same commit. **≈+90 production,
  already inside E4's price in §1.** Zero Drawer impact (Schublade
  verified E's write set never touches a Drawer-read store).
- **REFUSE:** E4 drops ≈90 — and §7.4's pulse and death line lose
  their lawful decider. The only honest fallback is *no pulse for
  any session whose outcome cannot be proven clean*, which
  re-opens B-12's settled ground and makes the death-booked clock
  decorative. The zero-migration alternative was examined and
  rejected with evidence (Nocke §2.5 — whose stray claim that 8→9
  already carries `heldAppsAtSeal` is, for the record, **false**
  against Rurik's closed census).

**F-N2 — the tear's voice** (Nocke, 06). The counterfoil tear spends
`playCompletionPulse`, a reserved voice (§7.9).
**RECOMMENDED: a distinct `playTearPulse`** (small, priced in the
sound engine's sweep; E4 is not blocked either way). Keeping the
mis-spend costs 0 lines and a lie every time a task is torn.

**F-L2 — the FLOW finish** (Latch, 18). A stopwatch session never
flips in `updateSessionProgress` (early return, Repository:1036–
1049), so §5.7's FINISH does not literally cover it; its past-floor
disarm (VM:930–937) bypasses the valve, spends no token, and books
`completedSuccessfully = true`. Latch's argument: the floor IS the
target — it is literally the stored `targetDurationSeconds` — so a
past-floor FLOW session is fully elapsed in the only sense its
contract defines.
- **RULED AN E0 (RECOMMENDED):** HELD FRAME + pulse at E700, with
  two amendments — E0's source edge is the
  `disarmActiveSession(completed=true)` edge (the ticker flip
  provably never comes), and the held-frame picture freezes the
  count-up total (no `00:00` step, no full arc). **+30 production,
  +25 tests, contingent — not in §1.** Below-floor FLOW exits go
  through the valve → BROKE, unchanged, under either ruling.
- **COUNTER-RULING:** ≈10 lines less, and a session the plan books
  as completed gets no finish state at all. The release cascade,
  detention and receipt are **not optional under either ruling** —
  only the pulse and the held frame are at stake.

**F-RS — the sled's resume law** (Spule, 11; Schlitten, 13). Fen's
filed formula was adopted under Ren's F2; Spule recomputed it and
proved it wrong (restarts at slope ≈0 against freeze velocities to
−2.56 frac/s; duration error −105…+44 ms; at f=0.10 it amputates
62% of the road). **RECOMMENDED: time-offset resume** (§6.1 — in
E6's price; if Ren restores Fen's literal formula it is a two-line
change and the velocity jump in Spule's table is the price, stated,
not hidden).

**F-S2 — the Vertigo Settle does not settle** (Spule, 11).
Recomputed from Wren's filed easing (0.5, 0, 0.85, 0.35): travel per
phase = Departure 1.5%, Recession 37.4%, **Settle 61.1%** — at the
shot's *highest* velocity (slope 4.33; wheel moving 83 dp/s) into a
hard stop at t=1050. Wren's "Settle decelerates" claim is
mathematically false. **RECOMMENDED: keep the easing, strike the
claim, device-gate the result** (0 lines; the curve may be beautiful
— it is just not a settle, and the file must stop saying so). The
alternative — a re-filed easing that truly decelerates — is
undesigned and unpriced; ruling for it sends E3's curve back to
Spule before build.

**F-S3 — token names** (Spule, 11). **RECOMMENDED: scoped names**
(`HubPressDip`, `HubSeatReturn`) for the hub's two curves — 0 lines
either way; the ruling is whether a curve's name states its one
lawful consumer. Filed for Ren because names in `MonolithCurves.kt`
are studio law, not because the price differs.

**F-Z1 — the clock-owner inversion** (Zug-II, 08). The theme is an
ancestor of the Deck; a `remember`ed state inside
`TactileDeckScreen` cannot feed it in the same pass.
- **(a) VM mirror (RECOMMENDED):** the controller exposes
  `StateFlow<ShotGround>` flipping at t0Mono+400; ≈15 lines, inside
  E3's price; keeps the E3 entry's letter ("owned in
  TactileDeckScreen") for the visual clock.
- **(b) Hoist:** `SealShotState` remembered in `MainActivity`,
  passed down — one clock, no mirror, cleaner code, ≈ the same
  lines; the price is the E3 entry's letter.

**F-Z3 — where the grace/emergency chips live** (Zug-II, 08). They
leave the sled slot (the ghost owns it) and compose at Settle
(t=1050) by CUT 0 — under no ruling do they evict the ghost or
compose during 0–1050. **RECOMMENDED: the session-utilities row**
(DESK CLOCK / HELD) if Kato's room sum (821 dp) takes a third/fourth
chip; fallback: a second row directly under it, also at Settle
(≈+10). Grace keeps ≥8.95 s of usable window either way.

**F-Z2 — does the detention gate reach Tasks' rows** (Zeile, 23,
dissenting from Linea 19 §3 Layer 2). **RECOMMENDED: Zeile
governs.** §4.1 says everything returns on session end; detention
detains the receipt, not the user, and Band's lock releases travel
at rack end on its own law. Tasks is never wired to `finishState`.
Price: removing the extension is absorbed in E4's band; wiring it
would cost Zeile's ≈2 plus a second gate to keep honest forever.

**F-D1 — R3, derived or witnessed** (Draht, 05). **RECOMMENDED:
DERIVED** (in E4's price) — §7.1. The witnessed build (Opal's a11y
collector) costs ≈+35 and buys a witness for a link that cannot
fault independently; if it ever can, the collector is its own
build.

**F-B1 — the faulted sheet's host** (Band, 20). Wren rises the sheet
at E1800 with the rack completing behind it by ≤E2080; §6.5's
seat-before-rise forbids rising before chrome unlocks; no text
states the sheet's host level, and the host level decides whether
the band lives under it. **RECOMMENDED: viewport host (never
root), seat-before-rise exempted for the faulted sheet only** (it is
the detention exit, not a presentation), band live from rack end,
and **the band never waits on the pawl** — a pawl-keyed lock traps
him ≥30 s on the fault screen with PARK's FIX board unreachable.
Lawful exits in order: the sheet's own one-gesture dismissal; any
band stop = nav-away (Linea); PARK, whose invocation dismisses the
receipt sheet on the same input — two sheets never stack. Priced
inside Band's 67. The counter-ruling (root host, no exemption)
costs the same lines and the trap.

**F-B2 — PARK's degrade** (Band, 20). **Dormant**: it becomes a
fork only if Ren extends §6.1's read-only degrade to PARK, which
has no read-only mode in any filing. Band's drawer-residue fix
(§11.1, +8) is recommended and priced without it.

**F-SCH-1 — the Drawer's `day_notes` table has no booked ride**
(Schublade, 21). Every Drawer-train document promises it **8→9**;
Rurik's build-ready 8→9 is a closed census (16 ADD COLUMNs, zero
new tables) and names `day_notes` nowhere — the promised ride is
unbooked. Severity is bookkeeping, not data (capture already ships
safely in prefs).
- **RECOMMENDED: amend 8→9 before it lands** — one CREATE TABLE +
  entity + drain; the trunk is still v8, so today it costs paper;
  after Voss's merge lands it costs a migration number.
- Fallback: re-file B-31 for 9→10 (collides with this slice's
  manifest discipline — F-N1's unified manifest would gain a
  Drawer table). Standalone 10→11: rejected by Schublade.
This is the routines/drawer lanes' cost, not Slice E's — but the
freeze cannot pretend it isn't on the same desk.

**F-W1 — the ENDED-FAULT sheet's words** (Wort, 17). Specced
nowhere — not in the D/E file, not in Wren's part, not in MASTER
§5.7 (the composition is Emil's D9, expressly outside E4's price).
Wort's draft-for-Ren: **the sheet IS the receipt detained** — same
№, provenance, deflections, death line, footer, with the one STILL
BLOCKED line last.
- **RECOMMENDED: no title.** The sheet needs no name; it is the
  receipt, held.
- If Ren requires a title, the single draft candidate:
  `RECEIPT — RELEASE NOT PROVED`.
Binding meanwhile, ruled by the lexicon alone: `ENDED-FAULT` never
prints (it is a state name; it collides with ENDED BY EMERGENCY
BREAK and it grades a completed session), and no `TAP TO…`,
`RETRY`, or `FAILED` line may stand on the sheet — the fault offers
no act. Two minor drafts ride along: the micro-line length dialect
(`50 MIN` vs the formatter's `50M`) and outcome-word placement in
the routine line.

**F-G1 — the gates that never ran, and F5's clause** (Saum, 15,
evidence). No run report for the §F7 gate or the Cap-Crop Gate
exists in plan/, on the wall, or in mail/ren/. F5's clause —
*Tear-Seal deleted unbuilt if the gate hasn't run by the Slice E
freeze* — is therefore live at exactly the moment this file freezes
the slice.
- **RECOMMENDED: run both gates before the freeze** — one device
  session, 0 lines — and let the results amend what they amend.
- Or rule the clause **executed** (the Tear-Seal dies unbuilt, 0
  lines, a named scope loss), or **waived** (0 lines, and the
  clause's teeth are gone for every future slice — say so plainly).
Riding it, Spule's gap G-1: the pawl lift has **no named easing in
any part**. If the rack gate passes, Opal names the easing before
build; if it fails, the gap is moot. What may not happen is a
framework default shipping by accident.

**F-Z5 — the routine line for a hand-armed session** (Zettel, 14).
Reihe freezes the routine line at insert from routineId; a session
armed by hand *inside* a routine window has no routineId — yet
Uma/§5.7 promise it the line. **RECOMMENDED: honour §5.7 at
assembly** — the line is receipt provenance, fed by window overlap
when the receipt assembles (≈+8, inside §10's band); the frozen-at-
insert law governs *counts* (Fader), not provenance lines. The
counter-ruling keeps Reihe's freeze pure and the promised line
silently absent.

---

## 13. ACCEPTANCE TESTS — THE INDEX (Regal, 22)

The master skeleton is **plan/slice-e-parts/22-regal.md**: 69 rows,
**E-T01…E-T69**, each with joint, surface, one-sentence assertion,
evidence class, and the trunk file the test lands in. This section
is the index and the law; Regal's file is the skeleton itself.

**By evidence class:** UNIT 31 (five of them are grep gates — one
call site, no second writer, zero `soundEngine` tokens in E6's VM
additions — and are never dressed as anything else) · RENDER 29 ·
DEVICE 8 (E-T10, E-T11, E-T12, E-T21, E-T26, E-T30, E-T43, E-T68 —
device obligations, results recorded in BUILD-CHANGES, never
simulated) · MIGRATION 1 (E-T69, the 9→10 chain, written against
F-N1's recommended schema and flagged conditional until Voss
registers it).

**By ownership:** REF 53 — index entries pointing at seat-owned
tests (Siegel/Riegel's golden hash and haptic capture, Klinke's
call-site order, Reihe's T-R series, Draht's cascade, Rad's catch,
Zug-II's shot, Nabe/Horiz, Spule/Schlitten, Fader's T-CAP-1…10,
Zettel's T1–T10, Zeile's T-Z1–T-Z6, Latch's T-FLOW, Linea's frame)
with assertions not duplicated · OWN 15 — gaps no seat filed, owned
by Regal's skeleton: the E0 edge, the slow and unwitnessed detention
branches, cascade order, all three pulse-eligibility rows, the
STILL BLOCKED grep/render pair, the engine-spy half of sled silence,
Migration 9→10 · CONFLICT 1 — **E-T13 (routine-arm Throw) is
shelved until F-S1 is ruled**; Band's eight nav tests (T-B1–T-B8)
slot at E-T70+ at merge.

**Untestable as written — named, not smoothed (Regal §9-U1…U10):**
U1 the ±8 ms audio/haptic lock is a measurement, not a gate · U2
on-device acoustic bit-identity is disclaimed by Siegel himself —
the golden hash binds the *render*, the device is a hearing, not a
hash · U3 the routine-arm contradiction (F-S1) · U4 "detention
≤E1800 in any branch" is **false for process death** — Wren's own
relaunch text issues "else immediately," possibly hours late,
lawfully; the testable form is ≤E1800 while the process lives +
immediate-and-once on relaunch · U5 Wren's per-app STILL BLOCKED
example is unreachable under the one-link grammar (Wort concurs:
DEAD) · U6 the 9→10 expected schema is underdetermined by the
REGISTER until F-N1 is ruled · U7 the finish's bed-out has no bed
to test — no bed exists (§2.2) · U8 DEVICE_PASS_E5A and the room
gate are human judgments, filed as device obligations · U9 the
pulse's "0 dB ref" has no calibrated reference · U10 "intent −210"
is observable only as its latch+210 shadow in the t0 formula.

A joint is DONE at PROOF (Pipeline stage 5) when its rows pass —
greens in the harness for UNIT/RENDER/MIGRATION, recorded device
results for DEVICE, and the grep gates re-run against the merged
trunk, not the lane tree.

---

*Filed by Ilya (Improvers) from 26 part files, plan/slice-e-parts/
01–26. Prices are Waage's union method (16) as reconciled by the
coordinator after the late landings (06, 12, 14, 17, 18, 20, 21, 22,
23). What this file does not do: build PROVING (§9, link 3), amend
MASTER-PLAN or the D/E file (Sana's settlement sweep owns the
paper), or rule a single fork in §12 — those are Ren's, each with a
recommendation and both prices on the table.*
