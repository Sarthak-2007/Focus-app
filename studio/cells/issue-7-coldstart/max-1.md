# MAX-1 — MAXIMIZER: the cold-start cure at its ceiling (issue #7)

Seat: **MAX-1, Maximizer**, BINDUNG's cold-start cure cell.
Branch: `agent/issue-7-coldstart`. Base evidence: branch HEAD `55bcae4`
(BUILD-2) on `1b641aa` (BUILD-1); every mechanism below was read in the
landed code, not in the brief. Report to BINDUNG only.

**The maximal question:** the landed cure deletes the fog *sentence*.
The ceiling is a cure where the fog is **structurally impossible**,
cold-open truth is **instant at the moment of intent**, and every
remaining failure **names its own cause**. Six pushes, each with
mechanism + price + risk, ranked at the end. Two of them find the
landed spec timid or incomplete; one rejects the brief's own
suggestion (probe-and-wait) with a better mechanism.

---

## 0. What the landed cure actually is (ground truth for every price)

- `probePipelineNow()` (MonolithAccessibilityService): synchronous
  `rootInActiveWindow != null` check → stamps `lastRootMono` + poke;
  null root → returns false, **counted nowhere**; throw → counted in
  `detectionFailures`. `requestPipelineProbe()` runs it fire-and-forget
  on the service scope and **discards the Boolean**.
- Probes fire at: residency start, **once** at +1500 ms (guarded: same
  residency, still unstamped), and in `onServiceConnected` when a
  residency is already active.
- Grace: `RESIDENCY_GRACE_MS = 4_000`, silence inside, `BLOCKING IS
  OFF` past it or with no residency carried. Fog string: 0 hits in
  `app/src`. Usage DENIED/UNSET rows outrank the stamp race.
- The slide reads the warrant **synchronously** at DOWN, on every
  MOVE, and at the gate crossing (`armWarrantNow()`, ≤5 ms law,
  Ines). Travel limits: LIVE → 1.0, UNPROVEN → CATCH 0.84 (travels,
  cannot commit), OFF → hard stop 0.65. `onLatch` dispatches only on
  a LIVE latch frame and, by settled dispatch law (Charge-2), never
  re-reads the warrant — the cascade's proving owns a death after
  the frame.
- The shield loop already ticks every 2 s (`onShieldIteration`), but
  it re-stamps ACCESSIBILITY from `instance != null` — **existence,
  not root proof**. Nothing at rest re-probes the root.

---

## 1. Continuous proof — a standing heartbeat

**Verdict: REJECT the standing loop; ADOPT its decaying-series form
(§1b) and its stale-triggered form (§1c).**

### 1a. Standing heartbeat while the Deck is resident — REJECT-WITH-REASON

- *Mechanism as proposed:* probe every N s while the Deck shows.
- *Price:* the probe itself is cheap (one binder root check, no node
  traversal — sub-ms to a few ms; even at 5 s cadence the battery
  cost is below measurement noise). The price is not energy, it is
  **meaning**.
- *Risk (decisive):* a heartbeat manufactures freshness. `lastRootMono`
  would always be current while the Deck is open, so
  `stampedThisResidency` becomes "polled alive at some point while
  sitting here" instead of proof carried by the pipeline's own
  traffic. Worse, the probe and the event path share root
  *acquisition* but not event *dispatch*: a pipeline whose root
  answers but whose events stop flowing (the exact bound-but-blind
  failure the past-grace OFF exists to name) would be heartbeat-
  masked indefinitely — OFF could never print while the Deck is
  open. The heartbeat cures the print by blinding the judge.
- *After arming it is doubly redundant:* the seal cascade already
  judges the wire continuously — L3's armed witness
  (`armedSessionId`/`armedMono`, re-pended the moment its witness
  dies), the event path stamping on every foreground event, the
  shield's 2 s iteration. A heartbeat adds a second, weaker proof
  beside a stronger one and invites the two to disagree.

### 1b. Decaying re-probe series at residency start — ADOPT-NOW (BUILD-1)

- *Mechanism:* replace the single +1500 ms re-probe with a bounded
  series — probes at **0, +600, +1500, +3000 ms**, each guarded
  exactly as today (same residency, still unstamped), stopping
  forever on the first stamp. All inside
  `WatchStore.noteResidencyStart()`; no new triggers, no new files.
- *Why:* the two landed probes can both lose to a **root-null
  transient** — Monolith's own window still animating in, a recents
  transition, a splash frame — on a healthy phone. After +1500 ms
  nothing ever probes again until an a11y event happens to fire;
  a quiet Deck then prints OFF at 4 s on a healthy phone. That is
  the fog's cousin: a false resting verdict. The slow-*bind* case
  is already covered (rebind re-probes in `onServiceConnected`);
  the root-null transient is the uncovered one. The series covers
  it inside the grace, so the grace's promise ("silence while the
  machine works") is backed by a machine that actually keeps
  working for the whole window.
- *Price:* ≤4 binder root checks per residency worst case, zero
  once stamped (the common case pays exactly 1, as today). ~10
  lines in one owned function + one Robolectric test extension
  (series stops on stamp; superseded residency never probed).
- *Risk:* negligible — same guard discipline as the landed +1500;
  no print changes; grace arithmetic untouched.

### 1c. Stale-triggered re-probe — NEXT-PR

- *Mechanism:* the VM ticker already re-reads the warrant when
  `warrantStaleForPrint` trips (45 s). Add: that same refresh also
  calls `requestPipelineProbe()`. Freshness is re-earned exactly
  when freshness is questioned, never on a standing loop.
- *Price:* one line at the ticker + the probe call; fires at most
  once per 45 s per Deck residency and only when stale.
- *Why not now:* `MonolithViewModel` is outside this cell's file
  ownership. BINDUNG ruling requested in the #7 comment.

---

## 2. Listener-driven re-proof — replace the +1500 ms guess with an event

**Verdict: NEXT-PR. Real, but it does not replace the +1500 ms —
and the event it waits for is not the race that remains.**

- *What exists in the platform, honestly:*
  - `AccessibilityManager.AccessibilityStateChangeListener` reports
    the **global** accessibility on/off state, not this service's
    bind. It cannot name Monolith's binding.
  - There is **no per-service bind callback outside the service** —
    the precise bind event is `onServiceConnected`, and the cure
    already probes there. The listener idea's strongest form is
    already landed.
  - `ContentObserver` on `Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES`
    *does* fire the instant the grant string changes in Settings —
    moments **before** the bind completes. An observer in the app
    process could fire a probe attempt + a Watch sweep on that
    event, so a FIX trip's return is judged against a proof the
    toggle itself triggered, instead of waiting for ON_RESUME.
- *Price:* one observer registration per process (must be paired
  with unregistration; a leaked observer is a process-lifetime
  callback), OEM coalescing quirks on some ROMs, and it proves the
  *grant string changed* — the probe remains the only judge of the
  bind. Bundle the twin key `enabled_notification_listeners` in
  the same registration or don't do it at all.
- *Risk:* low, but the gain is narrow: ON_RESUME already sweeps on
  return, and `onServiceConnected` already re-probes. The observer
  buys seconds on one path (FIX return), nothing on the cold-open
  path (no grant changes there). The +1500 ms guess is **not** the
  weak link — §1b's root-null transient is, and no listener fires
  for it. Keep the timed series; add the observer next PR as a
  FIX-return accelerator, not as a replacement.

---

## 3. The ARM path — probe at the gesture, never wait inside it

**Verdict: REJECT synchronous probe-and-wait. ADOPT-NOW its async
form: probe-at-DOWN + probe-at-gate, riding travel time the hand
already spends.**

### 3a. Probe-and-wait (~300 ms at the seal attempt) — REJECT-WITH-REASON

- *Price, honestly:* `rootInActiveWindow` is a binder call. Waiting
  on it at the gate means stalling the gesture's critical path up
  to ~300 ms on exactly the arms that are already sick — a visible
  freeze mid-throw, against the warrantProvider's settled ≤5 ms
  synchronous law, with ANR tail risk if the system server is
  loaded. It also collides with settled dispatch law: `onLatch`
  receives only LIVE latch frames and deliberately never re-reads
  (Charge-2 — two derivations of one fact will disagree); a wait
  inserted there is a second verdict wearing a timer. And it
  duplicates the cascade, whose entire proving machine (jam at
  +500 ms, annulment at +1500 ms) already exists to judge a wire
  that dies or lags after the frame.
- The healthy-phone case it targets — *stamp stale at DOWN, service
  actually alive* — does not need a wait. It needs the probe fired
  early enough that the stamp lands **during the gesture**.

### 3b. Probe-at-DOWN + probe-at-gate — ADOPT-NOW (BUILD-2 seam, BINDUNG to rule the host file)

- *Mechanism:* the slide's DOWN-frame read already exists
  (`onPressStart` carries the DOWN-frame warrant state). When that
  read is **UNPROVEN + unstamped + bound**, the host fires
  `requestPipelineProbe()` — fire-and-forget, off-thread, the
  exact call residency start uses. Again at the Schmitt gate
  up-crossing if the gate read is still UNPROVEN+unstamped. No
  read ever waits; `armWarrantNow()` stays pure and ≤5 ms.
- *Why it wins:* a human throw to the gate (0.68) and through the
  damped gear to the catch (finger p = 0.92) takes on the order of
  200–400 ms. The probe's expected landing is one binder call —
  milliseconds on a healthy phone. The warrant is re-read on every
  MOVE and at the gate by existing machinery, so the stamp that
  lands mid-throw is seen by the same throw: **a healthy phone
  never refuses an arm over a stale stamp, and the gesture pays
  zero added latency.** If the probe loses, today's behaviour
  stands exactly — the cap rests at the catch, uncommitted. No
  regression path exists.
- *Price:* one extra root check per DOWN on an unstamped residency
  only (stamped residencies — the overwhelming majority — pay
  nothing), plus one at the gate in the same narrow case. ~15
  lines at the `onPressStart` host seam + a `probeIfUnstamped()`
  helper on WatchStore (owned file) that encapsulates the
  bound/unstamped test so the host never derives it twice.
- *Risk:* a probe fired per repeated DOWN on a genuinely blind
  pipeline — bounded by the unstamped condition and by human tap
  rate; each is the same cheap check residency start already runs.
- *Ownership note:* the helper is BUILD-1/BUILD-2 territory
  (WatchStore); the one-line host call sits in the Deck host
  (`TactileDeckScreen` wiring in `MainActivity`), outside the
  cell's listed files. Consultation posted on #7: BINDUNG either
  extends BUILD-2's ownership to the host seam (the refusal
  table's other half already lives there — §6a) or this drops to
  NEXT-PR intact. The design does not change either way.

---

## 4. Boot / rebind coverage — the Launcher surface stands unproven

**Verdict: ADOPT-NOW the service-side half (BUILD-1); NEXT-PR the
launcher-residency half.**

- *Verified gap:* `LauncherBlockScreen` reads **no warrant at all** —
  session row only (DIAG-1 §5 found the same absence and filed it
  as "no fog path"; the maximizer's reading is sharper: no fog,
  but also **no proof**). It renders `visible = launcherOpen &&
  sealed` in `MainActivity`. Now trace process death mid-session:
  the process restarts; the session row is restored from Room; the
  service rebinds — and `onServiceConnected` probes **only if
  `WatchStore.residencyActive()`**, which in a fresh process is
  false (`residencyStartMono = 0` until the Deck is entered). A
  relaunch that goes straight to a sealed surface — an intercept
  intent into the Airlock, the Guard-line tap into the launcher —
  can stand in the new process with **zero pipeline proof in this
  process**, while the phone presents itself as sealed. The Deck
  path heals itself on entry; the sealed surfaces have no entry
  law at all.
- *Mechanism (a), ADOPT-NOW — BUILD-1:* in
  `startSessionArmedCollector()`, the first emission carrying a
  non-null **active session** fires `requestPipelineProbe()`
  unconditionally (not gated on residency). The collector already
  observes exactly the fact that matters — *a session is sealed in
  a process whose pipeline has not yet proved itself* — and its
  armed stamp (`armedMono`) is already the cascade's L3 witness.
  One probe per bind-with-active-session; the stamp then exists
  for whichever surface asks first.
- *Mechanism (b), NEXT-PR:* launcher visibility starts a residency
  of its own — the host calls `noteResidencyStart()` when
  `LauncherBlockScreen` becomes visible, exactly as Deck entry
  does. The launcher prints no sentence (it is a door, not a
  board; PARK owns prints) — the point is not a print, it is that
  the sealed home's wire is **proved, not assumed**, and the
  autopsy record (§5) gains the residency to attach to. Host file
  is outside cell ownership; BINDUNG ruling requested.
- *Price:* (a) ~5 lines in an owned file + test note that the probe
  is device-side (as `ColdStartWarrantTest` already states for the
  probe itself). (b) one host effect.
- *Risk:* (a) none material — a probe on bind-with-session is the
  rebind cure's own logic with its residency precondition relaxed
  to the stronger fact (a live seal). (b) a launcher residency
  supersedes the Deck's stamp bookkeeping on return — the same
  supersession Deck re-entry already performs today, tested in
  `ColdStartResidencyStampTest.aNewResidencySupersedesTheOldStamp`.

---

## 5. Self-diagnosis — when OFF prints, it must say WHY, somewhere he can read it

**Verdict: ADOPT-NOW the capture (BUILD-1); NEXT-PR the print
surface. This is the cure's biggest remaining hole.**

- *The hole, verified:* today a past-grace OFF is **folklore-grade**.
  `probePipelineNow()`'s result is discarded by its only caller;
  a null-root failure increments **nothing** (only a throw reaches
  `detectionFailures`); no record distinguishes *never bound* from
  *bound but root-blind* from *grant absent*. The next cold-start
  failure on his phone will be exactly as undiagnosable as this
  one was — diagnosed from memory and screenshots, twice reported,
  never measured.
- *The record — `BlindAutopsy`, derived, never stored as a state:*
  - The service's probe returns a sealed result instead of a bare
    Boolean: `Stamped` / `NoRoot` / `Threw`. Companion keeps, per
    binding (dying with it, like `lastRootMono`): `probeAttempts`,
    `probeNoRoot`, `probeThrew`, `lastProbeResult`,
    `lastProbeMono`.
  - At the first past-grace unstamped read of a residency (and at
    residency end, whichever first), WatchStore derives the reason
    from facts it already can read:
    `GRANT_ABSENT` (settings string lacks the package —
    `isAccessibilityEnabled()`'s read) · `NEVER_BOUND` (grant
    listed, `instance == null` at expiry) · `BOUND_NO_ROOT`
    (instance live, every probe `NoRoot`) · `PROBE_THREW`
    (throws > 0) · `STAMPED_LATE` (a stamp landed after the grace
    — the near-miss that proves the window, not the pipeline, was
    wrong).
  - Persistence: one SharedPreferences record beside
    `KEY_LAST_PROVED_WALL` in the existing `monolith_watch` prefs
    (the #48 precedent — a tiny wall-clock fact needs no table, no
    schema change): reason + wall time + tallies. Survives process
    death, which is precisely the failure mode it exists for.
  - Print: **never on the Deck** (the Deck prints verdicts, not
    autopsies). It lands on PARK's diagnostic line — the surface
    that already exists in law (§3.3: the diagnostic renders only
    when invoked) — as one line, e.g.
    `LAST BLIND — BOUND, NO ROOT · 4 PROBES, 0 STAMPS · 12:04:33`.
    NEXT-PR because `ParkDrawer` is outside cell ownership.
- *Price:* ~40–60 lines across the service + WatchStore (both
  owned), one prefs key, one derivation-table test (five reasons ×
  the tally states that produce them, Robolectric, following
  `ColdStartWarrantTest`'s pattern). No DB migration. No print
  change in this PR.
- *Risk:* a derived reason can mislabel an exotic path (e.g. bound,
  root present, but the *poke* lost) — mitigated by deriving from
  tallies at read time rather than recording a verdict at failure
  time, and by `STAMPED_LATE` existing as an honest bucket. A
  wrong reason is worse than none; the derivation table is the
  test surface that keeps it honest.

---

## 6. Where the spec was timid or wrong

### 6a. The fog is deleted but NOT structurally impossible — ADOPT-NOW (BUILD-2)

- *Verified:* `RefusalAction.RETRY_CHECK` **still exists** in the
  enum (`MonolithComponentLogic.kt:178`), and the Deck's action
  handler still carries a live `RETRY_CHECK` branch
  (`TactileDeckScreen.kt`, the sentence slot's `onAction`) routing
  to `onRetryBlockingCheck()`. The string died; the **machinery
  that issued it survives**. Any future row re-issues the fog with
  one line and no compile error. That is deletion, not
  impossibility.
- *Mechanism:* delete the enum value and the handler branch. The
  `when` over `RefusalAction` is exhaustive, so the compiler then
  **proves** no sentence can carry a retry action — the fog becomes
  unrepresentable in the type system, which is the only
  "structurally impossible" that means anything. `onRetryBlockingCheck`
  keeps its lawful caller (PARK's RE-CHECK) or is removed if it
  has none — BUILD-2 verifies at the call sites. BUILD-3's
  whitelist test already asserts no row issues RETRY_CHECK; after
  this change that test guards a compile-time fact, belt over
  braces.
- *Price:* ~10 lines across two files (the second is the host seam
  of §3b — one BINDUNG ownership ruling covers both).
- *Risk:* none identified — the branch is unreachable in the cured
  table (no row issues the action; BUILD-3's sweep proves it).

### 6b. Grace was given to the stamp race but not to the bind race — NEXT-PR (needs a chair ruling)

- *The timidity:* the cure silences UNPROVEN for 4 s because a
  cold open's stamp races. But **not-bound prints OFF instantly,
  at any age** — and a routine rebind gap (system rebind after a
  service crash, a config change, an OEM battery manager cycling
  the binding) is the same cold-start physics: grant intact,
  instance momentarily null, Deck open. On a healthy phone that
  gap can flash `BLOCKING IS OFF` — a false OFF, the fog mirrored:
  not "can't check" but a death certificate for a guard that is
  mid-rebind. The landed table conflates two different facts
  under `a11yBound = false`: *grant revoked* (observed death —
  OFF instantly, correctly) and *grant listed, bind in flight*
  (unproven, not dead).
- *Mechanism:* `standingWarrant()` already sits beside a repository
  that can read the grant's listing (`isAccessibilityEnabled()` —
  the settings-string check). Pass it as a third fact (cached in
  WatchStore from the sweep and bind hooks, **not** read per
  gesture frame — the string read is a provider call and the
  slide's ≤5 ms law stands). Table: `!bound && grantListed &&
  in-grace` → silence; `!bound && !grantListed` → OFF at any age;
  past grace → OFF regardless.
- *Price:* one cached Boolean + one derivation parameter + table
  rows + tests. The honest cost is doctrinal: it softens "not
  bound = OFF instantly" — which is why this is a **ruling for
  BINDUNG/Ren, not a builder's call**. Worst case it delays a true
  OFF (service crashed, grant intact) by ≤4 s, once; the current
  form prints a false OFF on every routine rebind, forever.
- *Rank:* NEXT-PR, explicitly gated on the ruling. Filed here so
  the trade is on record with its price, not discovered on his
  phone.

### 6c. "Instant" truth earlier than residency start — REJECT-WITH-REASON

- Probing from `Application.onCreate` / pre-residency would save
  roughly one frame over probing at residency start. The probe at
  residency start *is* the instant proof on a healthy phone (one
  binder call). Likewise, relaxing `stampedThisResidency` to
  accept a stamp from a freshness window *before* residency start
  would re-admit the stale-proof class the residency law exists
  to kill, to save milliseconds the probe already saves lawfully.
  Both are complexity theatre. Rejected.

---

## Ranking — every idea, its owner, its price in one line

| # | Idea | Rank | Builder / gate | Price |
|---|---|---|---|---|
| 1b | Decaying re-probe series 0/+600/+1500/+3000, stop on stamp | **ADOPT-NOW** | BUILD-1 (`WatchStore.noteResidencyStart`) | ~10 lines + 1 test; ≤4 cheap checks/residency worst case |
| 3b | Probe-at-DOWN + probe-at-gate (async, rides the throw) | **ADOPT-NOW** | BUILD-1 helper (`WatchStore`) + BUILD-2 host seam — BINDUNG ownership ruling | ~15 lines; 1 extra root check per unstamped DOWN only; **zero** gesture latency |
| 4a | Probe on bind-with-active-session (collector's first active emission) | **ADOPT-NOW** | BUILD-1 (service) | ~5 lines; 1 probe per bind-with-session |
| 5 | BlindAutopsy capture: sealed probe result, tallies, derived reason, prefs record | **ADOPT-NOW** | BUILD-1 (service + WatchStore) | ~40–60 lines, 1 prefs key, derivation-table test; no migration |
| 6a | Delete `RefusalAction.RETRY_CHECK` + its Deck handler branch | **ADOPT-NOW** | BUILD-2 (+ host seam, same ruling as 3b) | ~10 lines; compiler then proves the fog unrepresentable |
| 1c | Stale-triggered re-probe on the VM ticker's warrant refresh | NEXT-PR | VM file — outside cell ownership | 1 line + probe call; ≤1 per 45 s |
| 2 | ContentObserver on the a11y (and listener) settings keys → probe + sweep on grant change | NEXT-PR | New registration in service/app process | 1 observer pair, unregister discipline; buys FIX-return seconds only |
| 4b | Launcher visibility starts its own residency | NEXT-PR | MainActivity host — outside cell ownership | 1 host effect; no new print |
| 5-print | Autopsy line on PARK's diagnostic surface | NEXT-PR | ParkDrawer — outside cell ownership | 1 derived line, invoked-only rendering |
| 6b | Bind-race grace: `grantListed` as a warrant fact; silence for in-grace rebind gaps | NEXT-PR | **Chair ruling required** (softens not-bound doctrine) | 1 cached Boolean + derivation param + tests; ≤4 s delayed true-OFF worst case |
| 1a | Standing heartbeat probe while Deck resident | **REJECT-WITH-REASON** | — | Cheap in energy, fatal in meaning: manufactures freshness, masks bound-but-blind, duplicates the cascade post-arm |
| 3a | Synchronous probe-and-wait (~300 ms) at the seal attempt | **REJECT-WITH-REASON** | — | Up to ~300 ms gesture stall, breaks the ≤5 ms read law + Charge-2 dispatch law, duplicates cascade proving; 3b gets the same win for free |
| 6c | Pre-residency probing / pre-residency stamp freshness window | **REJECT-WITH-REASON** | — | Saves ~1 frame / re-admits stale proof; the residency probe is already the instant proof |

**Consultation (open, any route — filed on issue #7 with this
report):** to **BUILD-1** — §1b, §4a, §5-capture are yours, all in
owned files; the §5 derivation table is the test that makes the
autopsy trustworthy. To **BUILD-2** — §6a is the difference between
"deleted" and "impossible"; §3b's host call is one line beside your
table's own handler. To **BUILD-3** — §1b and §5 each need one test
extension in your pattern; §6a makes your whitelist test a
compile-backed fact. To **BINDUNG** — two rulings requested: (i)
extend cell ownership to the Deck host seam (`TactileDeckScreen`
handler + `MainActivity` warrant host) for §3b/§6a, or both drop
to NEXT-PR intact; (ii) §6b's doctrine question — is a
grant-listed rebind gap "observed death" or "unproven"? MAX-1's
position is on record above; the call is yours.

---

## Efficiency account — MAX-1

- **Read first:** `SARTHAK-WORDS.md` (his words win), the cell
  RECORD (seat table, ownership, BINDUNG's coordination comment
  `6515653955`), both landed build commits (`1b641aa`, `55bcae4`)
  in full, and the primary code: `WatchStore` (residency, grace,
  derivation), `MonolithAccessibilityService` (probe, bind,
  collector), `MonolithComponentLogic` (cured table, enum),
  `BlockitCoreViews` (slide reads, travel limits, gate),
  `ArmProvingController` (dispatch law, cascade deadlines),
  `MonolithRepository.standingWarrant`, `LauncherBlockScreen`,
  `MainActivity` host wiring, `ColdStartWarrantTest`, and the
  DIAG-1 / FEEL-1 / FRESH-1 filings (checked for collisions —
  DIAG-1 §5 noted the launcher's absence as "no fog path"; §4
  here takes the sharper consequence).
- **Artefacts:** this file — 5 brief questions answered with
  priced mechanisms, 3 additional findings from the code itself
  (§1b's root-null hole, §5's discarded probe result, §6a's
  surviving RETRY_CHECK machinery, §6b's bind-race asymmetry —
  four, and each one verified at a file:line, not inferred).
- **Waste admitted:** three `gh`/git-remote calls burned ~60 s
  against this session's GitHub latency (KEEP-1's record warned
  it runs 70–90 s/call; the first probes were still spent
  re-learning it). The issue comment was therefore written to be
  posted once, complete, with a long timeout.
- **Idling:** none. No code was touched — by design: this seat's
  product is the maximal design for BINDUNG to adopt or reject
  with reasons, and every ADOPT-NOW item names its builder and
  its file.

— MAX-1, Maximizer, cold-start cure cell (issue #7)
