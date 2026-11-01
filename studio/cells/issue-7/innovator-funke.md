# Issue #7 — Shield: making "is it blocking?" a proved fact, not a printed mood

**Seat:** FUNKE, INNOVATOR, in VOGT's cell for issue #7, "Main-app pass — Shield (audit fixes)."
**Source read:** `studio/plan/AUDIT-shield.md` in full (VOGT's verdict, R-1…R-13), plus the
Shield / state / service code on branch `agent/issue-7` (base `8438f5a`).
**Deliverable:** innovation analysis only. No app code edited. Nothing compiled, no tests run —
this cell closes **DECLARED**.
**Scope note:** the ±30-minute routine-time chip item is **DELEGATED to Voss's batch-10 time
wheels** (audit R-8). It is marked delegated in §6 and no local workaround is proposed for it.

---

## 0. The finding under the finding: R-5 as filed is underpowered

The audit's headline is right: Shield never prints whether the wall is standing *right now*
(Fremd C1, Muede F6), and R-5 makes the status sentence merge item #1. But R-5's recipe —
"derived from the session snapshot the screen already receives" — would power that sentence
from the weakest witness in the building:

- What Shield receives today is `isSessionActive`, which `MainActivity` derives as
  `activeSession?.isActive == true` — a **database row's flag**. A row can say active while
  both enforcers are dead: the accessibility service unbound, the shield service killed,
  the process restarted. That is not a hypothetical; it is the same failure family as the
  standing "CAN'T CHECK BLOCKING" cold-start bug. A sentence powered by the row alone
  upgrades the audit's missing sentence into a *confidently printed falsehood*, which is
  worse: SHUT-as-a-state at least never claimed "now."

- Enforcement is in fact doubly gated in code, and neither gate is the row's mere existence:
  `MonolithAccessibilityService.onAccessibilityEvent` re-reads the active-session snapshot
  per event and returns unless it is active; `MonolithShieldService`'s monitor loop stands
  itself down when the snapshot is null/inactive. The row is an *input* to enforcement,
  never *evidence of* it.

The good news, and the reason this file exists: **the app already built — for the Deck's
seal cascade — a proper witness system, and Shield simply never reads it.** The innovation
here is mostly *routing existing proof to the screen whose whole job is that proof*, plus
one honest state the audit's binary sentence doesn't have.

### The witness inventory (all in-tree today, all unused by Shield)

| Witness | Where | What it actually proves | Freshness behaviour |
|---|---|---|---|
| Session row (`id`, `isActive`, intention, `routineId`, `isInSafeBreak`, `shieldLastBeatWallMillis`) | Room, via snapshot/flow | A session is *supposed* to be running; the shield loop also stamps `shieldLastBeatWallMillis` into this row each iteration (`updateSessionProgress(stampShieldBeat = true)`, Slice E) | Persisted — survives process death, therefore can be **stale proof**; never sufficient alone |
| `MonolithShieldService.protecting` / `protectingSessionId` / `protectingMono` | Shield service companion | The foreground service started **and a full monitor iteration completed over that exact session id** — the flip happens only after the iteration reads the row, computes the blocked set, and polls the foreground | Reset to false/−1/0 on every start, stop, and destroy. In-memory only: death clears it |
| `WatchStore` engine heartbeat (`engineHeartbeatMono/Wall`, `engineProtecting`, republished **every shield iteration**, ~2 s loop) | `WatchStore.onShieldIteration` | The shield loop is *still turning now*, not merely flipped once | Monotonic stamp per iteration; age is computable at render. The store's own print-staleness threshold is `STALE_AFTER_MS = 45_000` |
| `MonolithAccessibilityService.instance` | A11y service companion | The accessibility service is **bound right now** | Cleared on unbind/destroy |
| `armedSessionId` / `armedMono` | A11y service companion, written by its own session collector (`startSessionArmedCollector`) | The a11y service's *own* session gate has observed **this session id** as active — its event path is open for this row | Cleared on unbind; re-derived from the session Flow on rebind |
| `lastRootMono` | A11y service, stamped in `onAccessibilityEvent` **before** any early return, only when `rootInActiveWindow` was actually acquired | The a11y pipeline *processed a real foreground event* — the service is not just bound, it is working | Monotonic; stamps on events, so it is event-driven, not periodic (this matters in §2) |
| `WatchStore.deriveWarrant` + residency stamp (`stampedThisResidency`) | WatchStore / repository `standingWarrant()` | The Deck's pre-arm verdict (bound ∧ stamped-this-residency ∧ usage ALLOWED). A *different question* from Shield's — see §4 | Warrant carries `readMono`; stale LIVE degrades to UNPROVEN at print |
| Intercept record (`recordIntercept` → `InterceptLogEntity` + `phantomReachesBlocked` on the session row) | Repository, called on the intercept path | A block **actually happened**: a named app was turned away, in this session | Persisted with the session; the strongest evidence in the app — and the rarest (see Mechanism B) |

Two doctrines already in the code that any mechanism must obey (both are stated in the
services' own comments, and both are correct):

1. **"Proof is never persisted; persisted proof is stale proof"** (Live Wire registry
   comments in both services). Any status that survives process death by reading a stored
   "was blocking" flag is lying by construction.
2. **Witnesses are keyed by session id.** `protectingSessionId` and `armedSessionId` exist
   precisely so a witness for session *N* can never be spent on session *N+1*. Any Shield
   derivation that ignores the id match reintroduces the confusion they were built to kill.

The cascade already composes exactly these witnesses for the seal:
`ArmProvingController.LiveArmWitnesses` and `SessionSealTransitionImpl.shieldMono/a11yMono`
are the same two reads, in two places, for the arm path. Shield needs the **third reader of
the same facts — not a fourth derivation of them.**

---

## 1. Mechanism A — The Standing Conjunction (reuse the cascade's witnesses, continuously)

### How it works

One pure derivation — working name `deriveShieldStatus(...)`, sitting beside
`RoutineDerive` / `WatchStore.deriveWarrant` as the single home of this logic — takes only
witness inputs plus a monotonic `now` and returns a sealed status. The ViewModel feeds it
the active-session snapshot, the two services' witness stamps, the `WatchStore` snapshot,
and a 1-second tick while Shield is visible (the tick is not optional: staleness is derived
at render, and a composable that never recomposes would freeze a fresh proof on screen
forever — the slip's 700 ms sweeper in `ShieldMatrixScreen` is the in-file precedent).

The conjunction for the strong claim, all keyed to the *current* session id `S`:

1. **Row link:** an active session row with id `S` exists.
2. **Shield link:** `MonolithShieldService.instance != null` ∧ `protecting` ∧
   `protectingSessionId == S` ∧ the WatchStore engine heartbeat for that protection is
   **fresh** (age against a Shield-specific threshold — proposed ≤10 s, i.e. ~5 missed
   2-second iterations; the store's 45 s print threshold is calibrated for permission
   boards, not for a loop that beats every 2 s, and borrowing it would let a dead loop
   print BLOCKING for most of a minute).
3. **A11y link:** `MonolithAccessibilityService.instance != null` ∧ `armedSessionId == S`.
4. **Pipeline link:** `lastRootMono` ≥ the later of (session start, Shield-residency start)
   — the residency pattern the warrant already uses (`noteResidencyStart` /
   `stampedThisResidency`). This proves the pipeline processed a foreground *during this
   session / this visit*, without demanding a periodic stamp the event-driven pipeline
   cannot honestly supply (see failure modes).

Only when all four hold may the screen print the strong sentence. Anything else prints the
weak sentence **with the failed link named** (§4 gives the exact table).

### Evidence quality

Highest available without new machinery. This is service/process evidence, session-keyed,
with a freshness-decaying heartbeat — three full grades above permission evidence
(a granted accessibility permission with a dead service prints green on every
permission-powered design, and proves nothing about tonight) and two grades above the
session row (intent, not enforcement). It stops one grade short of a witnessed block —
deliberately; Mechanism B explains why that last grade cannot be the power source, and §4
bolts it on as corroboration instead.

### Failure modes

- **Process death / rebind:** all in-memory witnesses clear, the status drops to
  NOT BLOCKING — *correctly*. During the brief re-proving window (the cascade's own
  vocabulary: jam at +500 ms, annul at +1500 ms) Shield should print the proving form of
  the weak sentence, not flicker the strong one. No grace period that keeps printing
  BLOCKING on a dead witness: a grace period is a lie with a timer.
- **Wedged-but-bound a11y service** (instance exists, gate armed, event thread stuck):
  the pipeline link is the tripwire — no root stamp since residency start → UNPROVEN,
  never BLOCKING. A service that armed and *then* wedged mid-session is only partially
  caught (its old stamp satisfies "≥ session start"); tightening that to a rolling
  window is Mechanism C's territory, deferred for cause (§3).
- **Heartbeat threshold mis-set:** too tight → false NOT BLOCKING flicker under load
  (the loop also does DB work per iteration); too loose → the 45 s failure above. 10 s is
  a starting price, flagged for on-device tuning, and it must live in one named constant
  next to the derivation, never inline in the composable.
- **Clock discipline:** all ages on `elapsedRealtime()` (monotonic), exactly as the
  cascade and WatchStore already do. Wall clock appears only in the printed ENDS time.
  A wall-clock age anywhere in this derivation is a bug by definition.
- **Safe Break:** the conjunction still holds during a Safe Break (SHUT, sniper, and
  sites stay enforced; PEEK is lawfully open), so the derivation must take
  `isInSafeBreak` as an input and qualify the sentence (§4) — a status that ignores the
  break will either lie about PEEK or cry wolf about the wall.

### Implementation cost

**Low–medium, and mostly plumbing.** Zero new services, zero schema change, zero new
permissions. New work: (a) the pure derivation + its truth table as unit-testable code
(the witnesses are already plain values; `LiveArmWitnesses` is the seam precedent);
(b) ViewModel plumbing of the witness set + tick into `ShieldMatrixScreen` — noting that
R-9 already changes this composable's contract, so the witness inputs should ride that
same contract surgery instead of arriving as a second one; (c) the sentence composable.
The witnesses' *writers* do not change at all.

### User comprehension

Best of the three, because the sentence can afford to be short: the machinery behind it
is complicated, the print is not. One line, present tense, and when it says NOT it says
*which link* in his words, not the link's code name — "SHIELD ISN'T RUNNING" teaches him
the fix (reopen / re-arm); "L2 UNPROVEN" teaches him nothing. The failed-link word is also
the diagnostic VOGT/Ren need when he reports "it says not blocking" — the screen has
already done the triage.

---

## 2. Mechanism B — Proof by Fire (status powered by the last witnessed block)

### How it works

Forget inferring enforcement from liveness: power the sentence from the intercept record.
The session row already accrues `phantomReachesBlocked`, and `recordIntercept` already
logs each turn-away with package, app, and outcome. Extend that record with a monotonic
stamp and the session id, and derive: BLOCKING is claimable only while the last *actual
intercept in this session* is younger than some window; a dedicated second line always
prints the receipt — "LAST TURNED AWAY: INSTAGRAM · 14:02."

An active variant exists — a canary probe: the shield loop already polls the foreground
against the blocked set every iteration (`queryForegroundBlockedApp` over UsageStats),
so a blocked foreground *is* detected continuously. But a probe that manufactures a
block (launching a SHUT app at itself) is rejected outright: it would pollute the
intercept count, fight the user's foreground, and prove the launch path, not the
interception path.

### Evidence quality

Per-event: **the strongest in the app** — a witnessed block is not evidence *of*
blocking, it *is* blocking, once. As a power source for a standing sentence: **the
weakest of the three**, for a structural reason no tuning fixes:

- **Proof starvation at rest.** Blocks happen only when he reaches for a blocked thing.
  A quiet, successful session — phone face-down, wall standing, zero reaches — generates
  zero proof, forever. Any freshness window then decays a *true* BLOCKING into NOT
  BLOCKING precisely when the wall is working best. The mechanism systematically
  punishes success and rewards temptation.
- It conflates two different facts a 1 AM reader cannot be asked to separate: "nobody
  has tried the wall" and "the wall is down."

### Failure modes

Starvation (above) is the fatal one. Additionally: the intercept record is **persisted**,
so a naive implementation resurrects doctrine violation #1 — last session's heroic block
printed as this session's proof — unless the session-id keying is enforced, at which
point early-session (pre-first-reach) status is permanently unproven anyway.

### Implementation cost

Medium for the honest version (monotonic + session keying on the intercept record, a
derivation, decay logic), and the spend buys a sentence that is *less* true at rest than
Mechanism A's. The cheap version (persisted count, no keying) is cheap because it lies.

### User comprehension

The receipt line itself is superb — concrete, named, timed; nothing else in the app
speaks that plainly. Which is why §4 keeps it — **as the corroborating second line under
Mechanism A's sentence, never as its power.** As the headline it teaches a false model:
"it only counts when something was stopped."

**Verdict: rejected as the power source; adopted as corroboration.**

---

## 3. Mechanism C — The Challenge Beat (active challenge–response self-probe)

### How it works

Don't read standing flags — *interrogate*. On Shield entry and on a slow beat thereafter,
the status derivation issues a nonce; each enforcer must answer inside a window by
stamping (nonce, mono) through the WatchStore: the shield service from inside its monitor
loop (proving the loop turned *and* re-read the current blocked set from the DAO while
answering), the a11y service from its event pipeline (proving it can still acquire a root
window — e.g. answering on its next real event, or via a self-query of
`rootInActiveWindow`). Unanswered or late ⇒ that link prints dead. This is the only
mechanism that deterministically catches the **wedged witness**: a service whose
`instance` and armed stamps are all still standing from before it stopped working —
Mechanism A catches that case only at session/residency start, via the pipeline link.

### Evidence quality

Freshest possible short of a real block: proof is *manufactured on demand*, age ≈ the
challenge window, and it exercises real sub-paths (loop turn, DAO read, root acquisition)
rather than flags those paths set once. One honest caveat: a probe proves the *probed*
path. If the challenge bypasses the tier/lease/sniper logic (it must — it cannot fire a
real intercept), a bug inside that logic survives a green probe. Challenge evidence is
about *liveness of the machinery*, not correctness of the rules — same limit as A,
arrived at more expensively.

### Failure modes

- **Complexity risk, the real cost:** a nonce protocol across two services and a store,
  with its own races (answer landing after the window, answers crossing session
  boundaries — the id-keying discipline has to be re-implemented for nonces).
- **Battery / churn:** bounded if the beat is entry + slow (≥30 s), but it is still a
  standing interrogation the other mechanisms don't need.
- **False deaths:** a busy main/IO thread delays an answer past the window and the
  screen declares a healthy wall dead — the exact flicker failure A risks only at its
  heartbeat edge, here built into the core loop.
- It solves a failure (mid-session wedge with standing stamps) that **has not been
  observed in the field** for this app, while the failure that *has* been observed —
  witnesses existing and Shield never reading them — is solved by A for a fraction of
  the price.

### Implementation cost

**High** — new protocol surface in both services + WatchStore, new test matrix
(nonce × session × timing), on-device soak to set the window. Priced honestly: this is
a build of its own, not a line item in the Shield merge.

### User comprehension

Identical print to A (the sentence doesn't say how it knows), so comprehension is
equal — which is precisely the argument against paying C's price today: the user-visible
gain over A is zero until a wedge is actually observed.

**Verdict: deferred, named as the successor.** If device use ever shows BLOCKING printed
over a wedged enforcer, C is the filed answer. Until then it is insurance bought before
the accident class exists.

### Comparison

| | Powers the claim from | Evidence grade | Fatal / worst failure | Cost | Print |
|---|---|---|---|---|---|
| R-5 as filed (baseline) | Session row flag | Intent only | Confident green over dead enforcers | Trivial | One line |
| Permission checks (rejected class) | Settings/AppOps grants | Capability only | Granted-but-dead prints green | Low | One line |
| **A — Standing Conjunction** | Session-keyed service witnesses + fresh heartbeat | Process/service, freshness-decaying | Wedge after arming, partially caught | **Low–med** | One line + named failed link |
| B — Proof by Fire | Last real intercept | Per-event: actual; standing: starved | Decays a working wall at rest | Med | Receipt line (kept as corroboration) |
| C — Challenge Beat | On-demand nonce answers | Freshest liveness | Complexity, false deaths | High | Same as A |

---

## 4. Recommendation — Mechanism A, with B as its second line; the exact state spec

**Adopt A. The status sentence must be a derived status object, not a composable-side
boolean** — one derivation, one truth table, every surface (Shield headline today;
anything else tomorrow) reading the same result. This is the app's own established
pattern (`RoutineDerive.derive`, `WatchStore.deriveWarrant`): pure function, inputs as
parameters, same inputs ⇒ same verdict.

### What powers each printed state

Inputs: active-session snapshot (id, intention/profile, `routineId`, `isInSafeBreak`,
computed end), shield witnesses (`instance`, `protecting`, `protectingSessionId`,
engine-heartbeat mono), a11y witnesses (`instance`, `armedSessionId`, `lastRootMono`),
residency mono, `nowMono`, last intercept for this session (app name + wall time).

| # | Condition (first match wins) | Prints |
|---|---|---|
| 1 | No active session row | `NOT BLOCKING — NO SESSION RUNNING` |
| 2 | Row active; shield link fails (no instance, not protecting, id mismatch, or heartbeat stale) | `NOT BLOCKING — SESSION RUNNING · SHIELD ISN'T RUNNING` (during the post-latch proving window: `… · STILL PROVING`) |
| 3 | Row + shield hold; a11y link fails (unbound or `armedSessionId != S`) | `NOT BLOCKING — SESSION RUNNING · ACCESSIBILITY ISN'T ANSWERING` |
| 4 | Row + shield + a11y hold; pipeline link fails (no root stamp since session/residency start) | `NOT BLOCKING — SESSION RUNNING · BLOCKING UNPROVEN` |
| 5 | All four links hold; not in Safe Break | `BLOCKING NOW — <SESSION LABEL> · ENDS <HH:MM>` |
| 6 | All four links hold; `isInSafeBreak` | `BLOCKING NOW — SAFE BREAK · SHUT STAYS SHUT · ENDS <HH:MM>` |

Rules that keep the table honest:

- **The strong claim is conjunctive and session-keyed.** No subset prints BLOCKING NOW.
  In particular `isSessionActive` alone — the R-5 recipe — is row 1's *input*, never a
  verdict. If the merge can only afford one change to R-5's text, it is this: strike
  "derived from the session snapshot," insert "derived from the session snapshot **and
  the cascade's witnesses, keyed by that session's id**."
- **`<SESSION LABEL>`** comes from the row's own identity (routine name when `routineId`
  is set — recovering the audit's example, "SLEEP SESSION" — otherwise the session's
  intention), and **`<ENDS>`** from the same end computation the shield notification
  already prints; a stopwatch-mode session has no end and must print none
  (`BLOCKING NOW — <LABEL> · NO SET END`) rather than borrow one.
- **NOT BLOCKING is never bare when a session exists.** Rows 2–4 keep "SESSION RUNNING"
  in the sentence and name the failed link in plain words. The binary the user asked for
  (blocking / not blocking) is preserved in the first two words; the suffix is what makes
  the weak form *actionable* instead of alarming, and it is the difference between a
  status and a riddle at 1 AM.
- **Corroboration line (Mechanism B, demoted to its proper job):** under the sentence,
  when this session has ≥1 intercept: `LAST TURNED AWAY — <APP> · <HH:MM> · <n> THIS
  SESSION` (from the intercept record + `phantomReachesBlocked`). When there are none,
  the line is **absent**, never "0 BLOCKS" — absence of reaches is not a fact about the
  wall, and printing it as one is B's starvation fallacy in miniature.
- **RUNNING NOW markers for routines** (audit R-5, second half) get the same evidence
  discipline — see §5: `isActiveNow` is window containment, not a running session, and
  must not power the marker alone.
- **Do not reuse `deriveWarrant` for this.** The warrant answers a pre-arm question
  (capability, before any session exists; its own comment notes shield-protecting is
  deliberately *not* a conjunct there, since the shield starts with the session). Shield's
  question is post-arm and shield-protecting is its central conjunct. Two questions, two
  derivations, **one shared witness set** — forking the witnesses would be the sin;
  forking the verdicts is just honesty.

---

## 5. Standing Orders de-duplication, and truthful routine labels — one abstraction for both

The slip problem (audit R-1) and the routine-label problem (R-6) are the same disease in
two sections: **one fact, cooked independently in more than one place, until the copies
disagree.** The cure is the same in both: a single derived model that every print reads.

### 5a. The RuleBook: one list model, three readers

Today `ShieldMatrixScreen` enumerates his rulings twice: the Standing Orders slip
(`groupedByRule()` → `SlipLine`s, with its own `slipTap` commit path, its own ratchet-gate
mirror, and the `RestingFlip` machinery) above a catalogue that enumerates the same apps
again with sleds. Note the sharpest detail: `slipTap` **re-implements the ViewModel's
ratchet rule in the composable** to decide whether to run its choreography ("This gate
mirrors the ViewModel's rule exactly") — two derivations of one refusal, guaranteed to
drift, and the audit's invisible-refusal charge (R-7) lives exactly in that seam.

Proposed mechanism — extend what already exists instead of adding a parallel model:

1. **One derivation:** widen `AppRules`/`groupedByRule()` (whose own header already states
   the right doctrine — "the counts are the list sizes themselves, so a printed count
   cannot disagree with the lines under it") into the single catalogue model:
   three groups — PEEK, SHUT, OPEN — each `(tier, apps sorted as today, count = apps.size)`,
   computed once from `allApps`. OPEN joins the model precisely so the tally can be a
   *fold of the groups* rather than a separately computed triple.
2. **Three readers, zero re-derivation:** (a) the headline tally is `groups.map { count }`
   — arithmetic that cannot disagree with the catalogue because it *is* the catalogue;
   (b) the catalogue renders the groups in PEEK → SHUT → OPEN order (R-1's adopted
   recomposition; group headers carry the PEEK contract line per R-6); (c) the tally
   words-as-jumps scroll the existing saveable `listState` to the group header keys.
3. **One commit path:** the sled's `onSetAppTier`, full stop. The slip's lines,
   `slipLines`, `slipTap`, and its ratchet mirror are **deleted, not folded** — R-1's
   ~200 retired lines. If the resting-flip choreography is worth keeping, it is hoisted
   onto the catalogue row itself (same `RestingFlip` state machine, keyed by package,
   resting in its old *group*): one rest state, living on the only list that remains.
   A flip refused by the ratchet then prints at the sled the user actually touched,
   which is R-7 satisfied by construction instead of by a second banner route.
4. **Search honesty:** under search the groups filter for display, but the tally keeps
   printing the *unfiltered* group counts (they are counts of his decisions, per Liv
   D6's reasoning in the current code), and the catalogue prints `SHOWING n` only while
   filtered (audit merge item 3). One model makes this trivially consistent: filtered
   view for rows, unfiltered model for counts, no third number anywhere.

Failure mode this kills beyond the scroll bill: any future surface (a widget, the Deck,
a receipt) that needs "how many SHUT" reads the same groups — the next duplicate book
becomes impossible to write accidentally, because the count literally has no second
computation to copy.

### 5b. `RoutinePrintState`: one label derivation for header, row, and scheduler talk

Today the row prints its state from `routine.enabled` alone (the ON/OFF cap in
`RoutineRow`), the header counts from `RoutineSchedule.liveCount` (`enabled ∧
daysMask != 0`), and the scheduler obeys `isLive` + the derive — three cooks. The
visible casualty: a routine with `daysMask = 0` prints **ON** on its cap (its meta line
even confesses "NO DAYS SET" a few dp away) while the header refuses to count it and the
chain alarm silently skips it. The row is lying in the user's face with the truth printed
underneath it.

Proposed mechanism — one pure label function in the `RoutineSchedule`/`RoutineDerive`
home, returning a sealed print state, consumed by the header count, every row, and any
future surface:

| Input truth | Print state | Where it shows |
|---|---|---|
| `enabled == false` | `OFF` | Row cap; excluded from header |
| `enabled ∧ daysMask == 0` | **`WON'T FIRE`** | Row cap (replaces ON); excluded from header — the header and the row now agree *by construction*, since both exclude/read the same state |
| `enabled ∧ daysMask != 0 ∧` pause covers the relevant window (`WindowState.PAUSED`) | `PAUSED` | Row; counted separately from ARMED if the header ever prints it |
| Window contains now ∧ derive says `OPEN_RUNNING` (a live session actually carries this `routineId` + window) | **`RUNNING NOW`** | Row marker (audit R-5's marker, on evidence — see below) |
| Otherwise (live, not running) | **`ARMED`** | Row cap; the header counts exactly these + RUNNING NOW as `n ARMED` (R-6's ACTIVE → ARMED, with `liveCount`'s derivation — acquitted by the audit — as the count's source) |

Two evidence notes, both applying §0's lesson inside the routines section:

- **`RUNNING NOW` must not be powered by `RoutineSchedule.isActiveNow` alone.** That
  helper proves the *window* contains now — schedule intent again. A window can contain
  now while its session was annulled by the cascade, is HELD by another session, or was
  never materialised (`RoutineDerive` already distinguishes `OPEN_RUNNING` /
  `OPEN_IDLE` / `OPEN_HELD`). The marker's honest power source is
  `WindowState.OPEN_RUNNING` — the derive's verdict that an active session *carries this
  routine and window*. `isActiveNow` is acceptable as a cheap pre-filter for *when to
  ask the derive*, never as the marker's proof. (This refines R-5's "markers from
  `isActiveNow`" wording; the audit's intent is honoured, its witness upgraded.)
- **`WON'T FIRE` needs no new detection logic** — `isLive` already encodes it, and
  `nextBoundary` already skips non-live routines (UNA-09 unified those two; the row cap
  is simply the last reader still cooking its own boolean). The entire fix is *routing*:
  the row stops reading `enabled` directly.

Comprehension gain, stated plainly: after this, every word on a routine row is the same
derivation the scheduler obeys — ARMED means the chain will fire it, WON'T FIRE means it
will not, RUNNING NOW means a session carrying it exists. A label the machine itself
doesn't use is decoration; these are the machine's own words, printed.

---

## 6. Explicitly delegated / out of scope

- **±30-minute routine-time chips (audit R-8): DELEGATED to Voss's batch-10 time wheels.**
  No local workaround, no interim chip arithmetic, and no editor design is proposed in
  this file. The wheels merge is Voss's lane; this cell's only note is that §5b's label
  derivation is editor-agnostic and will read the same routine rows whatever editor
  writes their times.
- Everything else in VOGT's merge list (R-2, R-3, R-4, R-7…R-13) is untouched by this
  file except where §4/§5 deliberately strengthen R-5/R-6's evidence and R-1's
  de-duplication, as tasked.

## 7. Open risks VOGT should carry into the merge

1. **Heartbeat threshold (10 s proposed) is a price, not a measurement.** It needs one
   on-device soak (cold start, Doze wake, heavy load) before it hardens; ship it as a
   named constant beside the derivation so the soak has exactly one knob.
2. **Residency semantics on Shield differ from the Deck's.** The warrant's residency is
   Deck-entry; Shield needs its own residency start (Shield-entry, re-noted on tab
   return) or the pipeline link should key off session start alone. Either is honest;
   pick one in the builder spec and write it into the derivation's contract comment —
   an unrecorded choice here becomes the next audit's C1.
3. **The proving-window print (row 2, "STILL PROVING") must have a bounded life** — if
   proving exceeds the cascade's own annulment horizon (~1.5 s) without resolving, the
   failed-link form replaces it. An unbounded "proving" is UNPROVEN wearing a friendlier
   word.
4. **Verification stays on-device** (audit merge item 13's pattern): the sentence must be
   exercised against a routine firing, an a11y unbind mid-session, and a shield kill —
   the three deaths this design exists to print truthfully. None of that is provable on
   paper, and this file claims none of it.

---

## Draft GitHub comment (for VOGT to post on issue #7)

— FUNKE (INNOVATOR), for VOGT's issue #7 cell. Filed `studio/cells/issue-7/innovator-funke.md` (analysis only, no code, DECLARED). Headline: R-5's status sentence must not be powered by the session row alone — that proves intent, not enforcement, and would print a confident lie over dead enforcers. The app already owns the proof (the seal cascade's witnesses); Shield just never reads it. I propose three mechanisms and recommend **A, the Standing Conjunction**: BLOCKING NOW only when the active session's id is matched by the shield's protecting witness with a fresh (~2 s-loop) heartbeat *and* the accessibility service's armed witness, plus a root-event stamp from this session/residency; every other case prints NOT BLOCKING with the failed link named in plain words ("SHIELD ISN'T RUNNING", "ACCESSIBILITY ISN'T ANSWERING", "BLOCKING UNPROVEN"). B (proof by last real block) is rejected as the power source — it starves exactly when the wall works — but adopted as the corroboration line ("LAST TURNED AWAY — …"). C (challenge–response self-probe) is deferred as the named successor if a wedged-but-bound service is ever observed. Also filed: one RuleBook model (tally = fold of the grouped catalogue, slip + its duplicate ratchet gate deleted, one sled commit path) and one `RoutinePrintState` derivation (OFF / WON'T FIRE for `daysMask=0` / PAUSED / RUNNING NOW powered by `WindowState.OPEN_RUNNING`, not `isActiveNow` alone / ARMED), so header, rows, and scheduler can never disagree again. The ±30-min chip item is DELEGATED to Voss's batch-10 time wheels — no local workaround proposed.
