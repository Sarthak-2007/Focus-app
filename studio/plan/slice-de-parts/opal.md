# SLICE DE — OPAL (Wild, machinery): PROOF RACK vs LIVE WIRE — THE LAYER RECONCILIATION

*Seat 12, Wild. Lens: a part reports INTENT or PROOF, never both
blurred. Standing honoured: my 0705 kills stand (Form B creep, the
serial cascade, §3's 1.8s detention, the no-per-step-haptics law);
t=0 formula untouched; §F2 ships unconditionally; the rack ships
only through §F7. Sources read whole: my invention file (§F2/§F6/
§F7), BEYOND-deck W4+W5, REGISTER, Ines 02 whole — her §1 warning
("link" is undefined in W4) is answered in §2 below, and her §3
shrink is adopted with one correction, named there. Code cited
from `Focus-app/app/src/main/java/com/example/`.*

---

## 1. VERDICT

**RECONCILED — at N=3, not N=loadout.**

Live Wire (Ines B, adopted in REGISTER: verdict synchronous ≤5ms
at DOWN and at the gate; the 1500ms deadline, the Park and the
arm-path UNKNOWN are dead) does not kill the cascade. It *shrinks
and re-times* it. The wire answers the pre-latch question — *are
the enforcers alive?* — synchronously, so nothing pre-latch needs
proving after the latch. What remains post-latch is exactly what
the latch itself creates: a row, a shield start, and an
accessibility service whose session gate has just opened. Those
are the cascade's links: **three, fixed, witnessable in code.**

What dies in the reconciliation, by name:

- **The per-app link.** My flagship priced the rack at
  N = snapshotted loadout size, 3–12. That N is dead. Apps are
  never "shut" in advance and there is nothing per-app to confirm
  before an attempt: they are intercepted *on* the attempt —
  `MonolithAccessibilityService.onAccessibilityEvent` (service/
  MonolithAccessibilityService.kt:41ff, intercept at its per-event
  path) with the Shield poll as backup
  (`MonolithRepository.queryForegroundBlockedApp`,
  data/MonolithRepository.kt:475). A rack notch per app would
  report INTENT (the loadout he chose) as PROOF (a check that
  returned). That is the blur my lens exists to kill.
- **The Canary as a cascade link.** A true intercept proof cannot
  be witnessed inside L+500ms without launching a sacrificial
  target on the arm path. Live Wire demoted it to an optional
  once-per-entry diagnostic (Ines §3); it is never a latch gate
  and never a notch.
- **Usage-stats as a cascade link.** It is a wire *input*, not a
  link: no command is dispatched for it at latch and nothing
  about it changes at latch. See §2.
- **The strip cells as cascade steps, and the rack as a second
  band.** One 56dp band, one tenant at a time — §4.

What survives untouched: §F2's parallel dispatch, concurrent
400ms clocks, deadline L+500ms, jam-set semantics; the t=0
formula `max(last link CONFIRMED-SHUT, latch + 210ms,
pawl-seated frame)`; the rack's rendering law (§F3) with its
geometry re-based to N=3 (step 56/3 = 18.7dp, every arm, every
loadout); §F5's release mirror at the repriced 500ms ceiling.

---

## 2. THE LINK LIST — defined exactly, witnesses in code

Dispatch precondition (not a link): at latch the controller
re-reads `liveWarrant()` one final time (Ines §3: the wire is
read at DOWN and at the 0.68 gate; the latch read is the third
and last). If the wire is dead at latch there is no dispatch and
no cascade — that is a pre-latch refusal under Live Wire law
(travel limit 0.65, Rosa's sentence), never a jam. Jams are only
for links dispatched and unanswered.

| # | Link | CONFIRMED-SHUT means (exact) | Witness in code (site) | Dispatch at latch |
|---|---|---|---|---|
| L1 | **SESSION-ROW** | The session row exists and is the active row: insert returned an id AND a snapshot read returns that id with `isActive == true` | `MonolithRepository.startSession` (data/MonolithRepository.kt:869–924; row built `isActive = true` at :910, `dao.insertSession` returns id); snapshot via `MonolithDao.getActiveSessionSnapshot` (data/MonolithDao.kt:60–61). CONFIRMED stamp = `elapsedRealtime()` read on snapshot return | `repository.startSession(...)` — the only DB write of the cascade |
| L2 | **SHIELD-PROTECTING** | The Shield service is a live instance, foreground start succeeded, AND its monitor loop has completed one iteration that read the L1 row as active | **New witness — does not exist today:** `startShield` (service/MonolithShieldService.kt, companion) swallows every exception and `onBind` returns `null` (:178), so "start requested" proves nothing. Add companion `@Volatile instance` + `@Volatile protecting` + `@Volatile firstLoopMono/sessionId`: `protecting` set only after `ServiceCompat.startForeground` returns in `onStartCommand` AND the first `startMonitoringLoop` iteration (loop at :65ff, first pass runs before its `delay(2000L)`) reads `getActiveSessionSnapshot()?.isActive == true` for the latched id. CONFIRMED stamp = the mono the flag flips | `MonolithShieldService.startShield(appContext)` — called from VM today at ui/MonolithViewModel.kt:907, moved inside the batch (§3) |
| L3 | **A11Y-SESSION-ARMED** | The accessibility service is bound AND has *observed this session as active* — its own session gate, which today early-returns when no active session exists, is now open for this row | **New witness — does not exist today:** the service has no `onServiceConnected`/`onUnbind` at all, and its event path returns before root acquisition when there is no active session (service/MonolithAccessibilityService.kt:48–58; Ines §0). Add companion `@Volatile instance` (set in a new `onServiceConnected`, cleared in `onUnbind`/`onDestroy`) + a session collector in the service's existing `scope`: it collects `dao.getActiveSessionFlow()` (MonolithDao.kt:58) and stamps `@Volatile armedSessionId/armedMono` when it sees the latched id with `isActive == true`. Precondition carried in from the wire: `instance != null` ∧ residency stamp (`lastRootMono` from this Deck residency, Ines §3 part 1 — stamp moved before the early return) still valid at latch. CONFIRMED stamp = `armedMono` | No command exists to dispatch — and none is invented. L3's "dispatch" is L1 itself: inserting the active row is the act the service observes. It runs in parallel by construction, not by courtesy |

**Grade of proof, stated so the seam never over-claims:** L1+L2+L3
prove *row live, shield running its loop over that row, a11y
bound with its session gate open for that row*. They do **not**
prove an intercept will fire on a blocked app. That grade is
LIVE BIND + ROW-OBSERVED — stronger than SELF-REPORT (the
residency stamp proves the event pipeline processed this visit's
windows), weaker than Rosa's END-TO-END (only a canary or a real
attempt proves interception). Fork F-OPAL-1 prices the stamp word.

**Killed candidate links, with the reason each cannot be a link:**

- **Per-app (× loadout):** unwitnessable in advance — see §1.
  A per-app "CONFIRMED" before any attempt would be fabricated.
- **Usage-stats allowed:** a synchronous AppOps read
  (`hasUsageStatsPermission`, MonolithRepository.kt:493–515,
  `MODE_ALLOWED`) with no latch transition. It belongs to
  `liveWarrant()`; re-reading it post-latch and seating a notch
  for it would print a precondition as a proof. Note its real
  job: it enables L2's poll detection (`queryForegroundBlockedApp`
  returns null without it, :476) — the a11y path (L3) does not
  depend on it, which is why its loss degrades the shield's
  backup sense and never unseats t=0.
- **Overlay / notification-listener / device-admin:** collected
  by `getSystemGuardStatus` (MonolithRepository.kt:603ff) for the
  Guard screen; none is on the intercept path of either service.
  Guard facts, not seal links.
- **Shorts/Reels sniper, browser-URL blocker:** not separate
  links — they are branches *inside* the same
  `onAccessibilityEvent` after the same session gate
  (MonolithAccessibilityService.kt, steps 3–4 of its flow). L3's
  gate-open witness covers them together; separate notches would
  render one code path three times.
- **`isAccessibilityEnabled()` as a witness:** the Settings-string
  check (MonolithRepository.kt:525–538) proves a toggle was once
  flipped. It is never a CONFIRMED witness for anything; Live
  Wire already replaced it on the arm path and the cascade does
  not resurrect it.

**Release mirror (for §F5, same links, reversed):**
CONFIRMED-OPEN(L1) = snapshot returns the id with
`isActive == false` after `disarmActiveSession`
(MonolithRepository.kt:1085); CONFIRMED-OPEN(L2) = shield
`instance == null || protecting == false` after `stopShield`;
CONFIRMED-OPEN(L3) = a11y collector stamps the id as inactive
(same Flow, same stamp field, value flips). Deadline, detention
and STILL BLOCKED law per §F5 as filed (ceiling 500ms, receipt
issues past it with names, silent retry 30s / 5min / next Deck
open).

---

## 3. THE CASCADE — state machine and t=0, in code terms

### 3.1 Where PROVING lives — exact

**PROVING is a controller state in the ViewModel layer only. It
is not a DB state, not a session column, not a migration.**

- The session row is born exactly as today: one insert,
  `isActive = true` from birth (MonolithRepository.kt:910).
  No `prepareArm`/inactive row (Ines C is not adopted here):
  the Shield monitor loop *stops itself* on a null/inactive
  snapshot (MonolithShieldService.kt, monitor loop), so an
  inactive-row design would kill L2's witness at birth and buy
  a second lifecycle for nothing. Born-active also means
  enforcement onset is at insert, up to 500ms before t=0 —
  lawful because no surface claims SEALED before t=0 (§3.3),
  and §3.2's re-anchor means those milliseconds are never
  *counted* as session time.
- New type, one file: `ArmProvingController` exposing
  `StateFlow<ArmPhase>` to the Deck:

```
sealed ArmPhase {
  IDLE
  PROVING(attemptId, latchMono L, sessionId: Int?,
          links: Map<Link, LinkMark>)   // LinkMark = PENDING |
                                        // CONFIRMED(mono) | JAMMED
  JAMMED(attemptId, jamSet: Set<Link>, retryCount: Int)  // a
          // PROVING substate, not a new top-level phase: same
          // attemptId, confirmed marks are never revoked
  SEALED_RUNNING(sessionId, t0Mono)
  RELEASING(sessionId, links: Map<Link, LinkMark>)      // §F5
}
```

`provenCount` is *derived* (`links.values.count { CONFIRMED }`),
never stored separately — the rack, the strip-fallback and the
controller can then never disagree about the count, because
there is one field. This also discharges REGISTER's data
rationing: migrations 7→8 / 8→9 / 9→10 are already spoken for;
the cascade takes none. Persisted PROVING was rejected on
machinery grounds too: a PROVING row surviving process death
would be a claim about proofs nobody holds (§F4's rack law —
position re-derived, never restored — applied to the state
itself). §3.4 covers process death.

### 3.2 Dispatch, clocks, t=0

- **Latch frame L:** the commit instant of W4 (displayed
  progress crossing 0.84 under contact; tap path: the driven
  mesh's seat frame = onLatch). `L = SystemClock.elapsedRealtime()`,
  read once, on the main thread, stored in the attempt. This is
  the cascade's only clock. Every stamp in this slice is
  `elapsedRealtime()` — the same clock the repository already
  anchors sessions to (MonolithRepository.kt:884) and verifies
  against (computeVerifiedSessionElapsed, :578ff). Wall clock
  never enters proving. (Choreographer frame times are
  uptime-based and are *not* used as stamps; see pawl below.)
- **Batch (≤16ms spread, same vsync window, §F2 as filed):**
  (i) launch `repository.startSession(...)` on the VM scope —
  its insert return resolves L1's pending half; (ii)
  `MonolithShieldService.startShield(appContext)`; (iii) L3
  arms itself off the Room Flow when L1 lands. L2/L3
  confirmations that arrive before L1 resolves are **held**
  (timestamped, uncounted): provenCount applies them on the
  frame L1 confirms. If L1 fails, held marks are discarded with
  the attempt — a shield protecting no row has proved nothing
  about a seal.
- **Per-link timeout 400ms, concurrent from L** (§F2).
  **Proving deadline = L + 500ms** (400 + 100 scheduling slack).
  Typical last arrival on this list: 40–220ms (insert ~10–40ms;
  shield first loop ~50–200ms cold; a11y Flow emission
  ~10–60ms after insert) — the 210ms dwell floor is the usual
  binding term on a healthy morning, as §F2 predicted.
- **t=0, in code:**

```
on each CONFIRMED stamp and each frame while PROVING:
  if all links CONFIRMED:
    lastConfirm = max(links.CONFIRMED.mono)          // sampled
        // at each witness site (§2), stored in the mark
    pawlSeated  = elapsedRealtime() read *inside* the frame
        // callback in which the rack flag seats at notch N
        // (or, in the §F7-fallback rendering, inside the frame
        // the last strip cell fills) — a stamp, not a duration
    t0 = max(lastConfirm, L + 210, pawlSeated)
    at the first vsync frame with frameMono >= t0, CAS
    PROVING -> SEALED_RUNNING(t0) — exactly once, one zero:
      1. repository.sealSession(sessionId, t0):
         row.copy(startElapsedRealtimeMillis = t0,
                  startWallTimeMillis = wallNowAtThatFrame,
                  hmacSignature = signSessionState(...t0...))
         — REQUIRED, not polish: startSession anchored the
         row at insert (:884-895); without the re-anchor the
         session would count up to 500ms it was never proven
         for. Re-sign with CryptoIntegrityManager.
         signSessionState on the new anchors or the HMAC check
         in computeVerifiedSessionElapsed flags tamper and
         adds the penalty — a false tamper is the cost of
         forgetting this line.
      2. soundEngine.playHarmonicSeal(arming = true) — its
         ONLY arm-path call. The two pre-proof seals die:
         the view's at release and the VM's at
         ui/MonolithViewModel.kt:892, which today plays
         *before* the insert coroutine is even launched
         (:892–907). A seal sound before proof is INTENT
         wearing PROOF's clothes.
      3. The session readout / Now Frame starts consuming the
         re-anchored row. Before this frame the Deck renders
         PROVING (§4); the row's existence alone renders
         nothing as "running".
```

- **ARM re-price (cascade's contribution only):** proving
  ceiling 1,800ms (W4 serial) → 500ms; typical proving
  600–1,100ms → 210ms floor-bound. REGISTER's priced bounds
  stand (Deck ≈2.0–2.3s typical / ≈3.3s worst) — Live Wire
  already removed the press-poll/Park time those bounds were
  carrying; this slice does not re-price what it does not own.

### 3.3 Deadline expiry — the lawful failure shape

At **L+500ms** with any link PENDING, those links become the
**jam set** (§F4, unchanged in spirit, exact in mechanics):

1. Phase → JAMMED (same attempt). Rack freezes at its notch
   (§4); sled skews its filed 12° at the frozen position; jam
   haptics as filed (20ms @ 1.0 ×3 @ 90ms). No t=0, no seal
   sound, no readout start. The jam set is **named** in Rosa's
   reserved 22dp slot below the track — one sentence, link
   names, human words: e.g. `SHIELD DIDN'T ANSWER — STILL
   PROVING.` Names, never a count (§F6: the strip keeps names
   for exactly this frame).
2. **The session exists during a jam — say so plainly.** The
   row is active and the services that *are* live enforce from
   it. The Deck's claim in this window is PROVING/JAMMED —
   never SEALED, and never `NOTHING WILL BE HELD` either: partial
   enforcement is a fact and both sentences would lie about it
   in opposite directions.
3. **Retry is the machine's, never his.** HOLD prices
   mid-session decisions at zero, and a jammed proving offers
   no verb — no Retry button, no Cancel. Retry #1 dispatches
   at the deadline frame, jam set only, in parallel, same
   400ms clocks from re-dispatch. Confirmed marks from round 1
   stand (monotone within the attempt).
4. **Final deadline L+1500ms** (one retry window): any link
   still unconfirmed → **annulment**. The attempt un-exists:
   `repository.annulProvingSession(sessionId)` =
   `dao.deleteSessionById(id)` (data/MonolithDao.kt:75–76 —
   the delete already exists) + `stopShield`. No tombstone, no
   Ledger line, no history row (§F4: *no record, no ghost
   minutes* — a session whose t=0 never fired is not a session
   that failed; it is an arm that was refused late). The Deck
   returns the cap on the universal spring and prints the
   refusal in Rosa's slot, refusal tense: `〈LINK〉 — NO PROOF.
   NOTHING WAS SEALED.` L1-jam special case: if the insert
   itself never returned, there is no row to delete; annulment
   is `stopShield` + attempt discard only.
   *Can the session un-exist?* Yes — but only down this one
   road: pre-t0, machine-initiated, total (row deleted, not
   deactivated — `disarmActiveSession` writes an outcome and
   is forbidden here), and never after t=0. After t=0 the only
   exits are W4's two lawful doors (completion, banked hold).
5. Permission death mid-proving is not special: the link joins
   the jam set at its timeout or its witness clearing,
   whichever is first (§F4 as filed).

### 3.4 Process death during PROVING

Controller state dies with the process — by design (§3.1).
The row survives, active, anchored at insert. On relaunch the
controller finds an active row with no live attempt and runs
**one recovery attempt**: the three witnesses re-sampled as a
fresh cascade (new L′ = relaunch frame, same 500ms clocks).
All three live → that completion frame is the row's t0 (its
first and only one) and §3.2's re-anchor runs there — the
downtime was unproven time and is never counted (Rosa's
duration objection, honoured in miniature). Any witness dead
at recovery → the Guard sentence owns the screen
(`GUARD … — 〈FIRST LACK〉`, REGISTER) and the row is annulled
per §3.3(4) on the recovery deadline; he is never shown a
"running" session the machine cannot currently prove.

---

## 4. RACK vs STRIP — the surface law, and §F2 priced apart

**The room carries ONE 56dp band** (Kato's math; REGISTER:
fixed sum 796, reserve = H − 796 — this slice spends 0dp of
new room). The band is Rosa's address: 56dp, 8dp above the
track (its 64dp with beats already booked in W4's room debt).
The rack does not get a second band, and the strip does not
keep the band while the rack performs elsewhere. **Tenancy is
by controller phase, transitions cut on phase frames, never
mid-frame, never crossfaded** (a crossfade would render both
tenants in one frame — two renderings of the proving fact at
once):

| Phase | Band tenant | What it renders |
|---|---|---|
| IDLE (standing, pre-latch) | **STRIP** (Rosa) | Her cells as re-based by Live Wire: bind-state rendering, no cadence, no freshness stamp arithmetic (REGISTER killed the poll machinery; the geometry and the BLOCK/APPS/TIME addresses stand). This is readiness — INTENT's neighbour, pre-commitment |
| PROVING / JAMMED / retry | **RACK** (Opal) | The strip unmounts **at the latch frame**. The rack mounts at provenCount 0 and renders the cascade only: flag at `(provenCount/3) × 56dp`, steps of 18.7dp, §F3 transit/bunching/click law unchanged, end numerals `0` and `3` (Anonymous Pro, §F1). Jam: frozen at its notch, §3.3 |
| SEALED_RUNNING | **RACK**, frozen | At t=0 the flag locks to ink as the pawl seats (§F1: the 400ms orange drain runs; the rack is the Deck's only orange while live). It stays, ink-dark and still readable, for the session's Deck life — the proof the seal stands on, kept in view |
| RELEASING | **RACK**, reversed | §F5: pawl half-lifts to its ride detent, flag leaps down one notch per CONFIRMED-OPEN, full lift at notch 0 issues the receipt |
| Receipt dismissed / annulled → IDLE | **STRIP** returns | On the IDLE frame, not before |

**Why the cells are not the steps (the §F6 fight, settled by
applying it):** the strip's cells and the rack's notches are
both "3" now, and the coincidence is a trap. BLOCK/APPS/TIME
are pre-latch readiness queries about *permissions and time*;
SESSION/SHIELD/A11Y are post-latch proofs about *this arm*.
One cell carrying both would report INTENT at 08:00 and PROOF
at 08:00:00.400 in the same fill — the exact blur my standing
kills. They also fail at different times with different lawful
consequences (wire-death refuses the gesture; link-jam annuls
an attempt), so a merged cell could not even fail honestly.
§F6's terms, applied in full: **the strip keeps the BLOCK
sentences in its two truthful forms and the names on the jam
frame (its reserved below-track slot — a different, already-
booked 22dp, not the band); the rack owns the count during
proving; on a healthy morning the strip's cells render
readiness until latch and then leave.** No surface ever prints
the proving count twice; at any single frame exactly one
tenant renders in the band.

**§F7 surrender, pre-built (so it costs no redesign if it
fires):** if the gate in §5 fails, the rack never mounts. The
band stays the strip's in every phase, and at latch the strip's
three cells **re-map 1:1 to the links** — words become
`SESSION / SHIELD / ACCESS` at 15sp in Rosa's families, fill
grammar hers (recess flush = confirmed, raised = pending,
cap-tone + doubled hairline = jammed) — and §F2's cascade runs
unchanged behind it. The words change at latch because the
facts changed; the geometry doesn't because the room can't
pay for two.

### §F2 — shipped unconditionally, priced separately (lines)

The cascade is true whether or not it is fast and whether or
not the rack exists (my anti-case's split, adopted as the
build order). Prices are gross / net lines, Ines's convention:

| Piece | Files | Gross | Net |
|---|---|---|---|
| `ArmProvingController` — phases, batch dispatch, concurrent 400ms clocks, held marks, jam/retry/annul, t=0 CAS | new file (ui/) | ~200 | ~200 |
| A11y witnesses: companion instance, `onServiceConnected`/`onUnbind`, residency stamp moved pre-return, session-Flow collector + armed stamp | service/MonolithAccessibilityService.kt | ~85 | ~75 |
| Shield witnesses: companion instance + `protecting` + first-loop stamp | service/MonolithShieldService.kt | ~50 | ~45 |
| Repository: `sealSession` re-anchor + re-sign (~30), `annulProvingSession` wrapper (~10); `startSession` untouched | data/MonolithRepository.kt | ~40 | ~40 |
| VM: `armMonolithSession` rewritten as latch entry into the controller; pre-proof seal deleted (:892); `requestArmWithPreFlightCheck` path superseded by Live Wire verdict (REGISTER) | ui/MonolithViewModel.kt | ~90 | ~55 |
| Deck host: phase collection, PROVING/JAMMED rendering states (non-rack parts: sled skew, jam sentence slot) | ui/screens/TactileDeckScreen.kt | ~60 | ~50 |
| **§F2 subtotal** | **6 files** | **≈525** | **≈465** |
| Tests: deadline/jam/annul, held-mark ordering, t=0 max() terms, re-anchor HMAC validity | test source set | ~130 | ~130 |

*Prerequisite not in this price:* the Live Wire registries and
`liveWarrant()` themselves (Ines §3 priced ≈665 gross / ≈400
net for her whole B, of which the registry work overlaps the
two witness rows above by ~60 lines — a builder must not
double-count them).

### The rack — gated, priced separately (lines)

| Piece | Files | Gross |
|---|---|---|
| Rack view: slot, flag (14×20), pawl (seated / ride / lifted), N=3 geometry from `links.size`, `0`/`3` numerals, §F3 transit + bunching + reduced-motion law | new view (ui/views/) or BlockitCoreViews.kt | ~230 |
| Band tenancy wiring: mount swap at latch/t=0/receipt frames in the Deck host | TactileDeckScreen.kt | ~55 |
| Seat click: haptic 4ms @ 0.35 per transit + sound voice, bunching queue | service/TactileSoundEngine.kt + controller hook | ~30 |
| Release mirror render: ride detent, downward leaps (reuses transit) | rack view + controller | ~55 |
| **Rack subtotal** | | **≈370** |

---

## 5. TOTAL PRICE (lines)

- **§F2 cascade (unconditional): ≈525 gross / ≈465 net + ~130 test lines.**
- **Proof Rack (only through §6's gate): ≈370 gross.**
- **Slice total if the gate passes: ≈895 gross / ≈835 net + tests.**
- **Slice total if the gate fails: §F2 only** + fallback cell
  re-map ≈45 gross (words + phase-driven fill binding in the
  strip host) — the surrender is priced, not improvised.
- Excluded, own slices, not hidden costs: Live Wire itself
  (above), HoldSpring, Ledger Proof Stamp, Rosa's Tachograph
  (her duration-proof fight is *not* settled by this slice —
  §7, R-3).

---

## 6. THE GATE — §F7 as a builder's checklist

Run in order. Any automatic-fail item ends the trial on the
spot. The rack ships only if **every** item passes.

**Setup**
- [ ] One device: Sarthak's phone; record model + Android/OEM
      build in the trial log. Battery >50%, thermal state cool
      at start, same Deck residency for every arm (never the
      first-ever Deck entry — the residency stamp must exist).
- [ ] Two builds, one flag apart: SERIAL (links dispatched and
      awaited one-by-one, all else identical) and PARALLEL
      (§F2 as specced). Temporary per-arm log line in both:
      `attemptId, L, L1mono, L2mono, L3mono, t0` (~15 lines,
      deleted before merge either way).

**Speed — §F7(a)**
- [ ] 20 arms per build, alternating ABAB, first 2 of each
      discarded. Loadout, duration, blocks identical.
- [ ] Proving duration per arm = `t0 − L` from the log.
- [ ] **PASS iff median(PARALLEL) ≤ 0.60 × median(SERIAL)**
      (≥40% median drop). Report both medians in the log; no
      rounding in your favour.

**Truth — automatic fails**
- [ ] Log audit over all 40 arms: any t=0 stamped before all
      three CONFIRMED marks, any seal sound before t=0, any
      session whose readout counted pre-re-anchor time →
      **trial failed, rack and cascade both return to Marlowe.**
- [ ] One forced jam per build (revoke Shield's foreground
      start via OEM battery restriction, or kill the a11y
      service mid-proving): rack/cells freeze, no t=0, retry
      fires without any user verb, annulment deletes the row
      at L+1500 (verify in DB: no history row, no Ledger line).

**Removal trial — §F7(b)**
- [ ] Third build: PARALLEL with the rack deleted (fallback
      cell re-map rendering). Screen-record 5 proving windows
      per build, same morning conditions.
- [ ] Side-by-side to Marlowe and Ren, unlabelled: both must
      call the rack version *visibly poorer without it* —
      i.e. the rack build reads richer in proof legibility.
      A split verdict, or "different but equal," = **FAIL**.

**Feel**
- [ ] Seat click countable: 3 transits, 3 distinct clicks at
      arm's length, eyes closed, on the trial device. If the
      motor smears 4ms @ 0.35, **one** retune to 6ms and one
      re-run is allowed (§F7 as filed); a second smear = FAIL
      for the haptic clause only — the rack may still ship
      silent if (a) and (b) passed, and the log says so.

**Consequence, pre-committed:** (a) and (b) both pass → rack
ships at ≈370 lines. Either fails → §F6 surrender executes
automatically: fallback re-map ships, §F2 is untouched, and
nobody re-litigates it in the merge — including me.

---

## 7. RISKS

- **R-1 — Shield cold-start variance.** `startForegroundService`
  on aggressive OEM skins can exceed the 400ms link timeout on
  cold process starts; the jam/retry/annul path (§3.3) would
  then fire on healthy mornings. Mitigation is the retry
  window, not a longer deadline — if the gate trial shows L2
  as the chronic jam, the honest fix is a *standing* shield
  (started at Deck entry, before the gesture), which is a new
  fork for Ren, not a silent constant change.
- **R-2 — The a11y collector is a new lifecycle.** The service
  today is purely event-driven with a per-event `scope.launch`;
  a standing Flow collector in `onServiceConnected` must be
  cancelled in `onDestroy` (its `scope.cancel()` exists) or it
  leaks across rebinds and double-stamps. Double-stamp is
  benign for t=0 (marks are idempotent per attempt) but the
  leak is not.
- **R-3 — Rosa's duration objection stands over this whole
  slice.** L1–L3 prove the seal at t=0; nothing here proves
  hour four. Her Tachograph is the only filed answer and it is
  not in this price. This slice's duty is narrower: it must
  never let t=0's stamp *wording* claim the duration
  (F-OPAL-1), and the receipt must not print proven-language
  past t=0 until her fight is settled by the merge.
- **R-4 — Re-anchor race.** Shield's monitor loop calls
  `updateSessionProgress` on its own 2s cadence and could read
  the row between insert and `sealSession`; `computeVerified
  SessionElapsed` on a <500ms-old row is harmless (elapsed 0),
  and the re-anchor re-signs, so the window is safe — but the
  gate's log audit (§6) must include one arm where the loop is
  forced to tick inside proving.
- **R-5 — N is now a lie-magnet in the other direction.** The
  rack shows `3` every morning. A reader (including a future
  seat) will mistake it for the app count. The end numerals
  are `0` and `3` *links*; the loadout count lives in the
  imprint tenant and the strip's APPS cell, pre-latch, where
  it has always lived. If any render ever labels the rack
  with an app name or count, that is a build error under my
  lens, not a wording choice.
- **R-6 — Parallel dispatch on shared Room state.** §F7's
  filed HIGH risk stands: the stack may serialise internally
  (then the rack renders serial leaps — degraded, never
  false, because every render is event-driven) and the gate,
  not argument, settles whether the time leap is real.

---

## 8. FORKS FOR REN — with my recommendations

- **F-OPAL-1 — The seam's proof grade word.** Options:
  `PROOF: LIVE BIND` (new grade) / `PROOF: SELF-REPORT`
  (Rosa's existing lower grade) / `PROOF: END-TO-END`
  (forbidden by the evidence — listed only so its rejection
  is on record). **My recommendation: `PROOF: LIVE BIND`.**
  SELF-REPORT undersells the residency stamp; END-TO-END
  would let t=0 testify past its evidence (R-3).
- **F-OPAL-2 — Annulment deadline.** L+1500 (one retry, as
  specced) vs L+2500 (two retries). **Recommendation:
  L+1500.** ARM's wall is safe either way; the cost of the
  longer window is blocked-but-unsealed limbo on a morning
  that has already failed once, and a jam that survives one
  full retry is a repair job (Guard), not a proving job.
- **F-OPAL-3 — Standing shield (only if R-1 fires at the
  gate).** Start the Shield at Deck entry so L2 is a re-sample
  instead of a cold start. **Recommendation: hold this fork
  closed until the gate names L2 as chronic jam** — a standing
  foreground service is a battery/notification cost every
  morning to fix a failure not yet measured.
- **F-OPAL-4 — Release rack under the same gate.** §F5's
  mirror reuses the arm rack's part and transit; the gate
  measures arm proving only. **Recommendation: same gate, no
  second trial** — a part proven legible going up is legible
  coming down; a separate release trial would price ceremony,
  not evidence.

## 9. MAIL / WALL NOTE

- Inbox cleared: Cato 0711 (clock-zero — his Cuff re-timed to
  the proof-seal frame; noted, no conflict with §3) and Rosa
  0715 (proof-is-a-duration) moved to `read/`. Rosa's charge is
  answered at R-3/F-OPAL-1 in this slice — the pawl does not
  testify about hour four, and the stamp is forbidden the words
  that would let it.
- Letters sent with this filing: to **Ines**
  (`mail/ines/0815__from-opal__link-list-defined.md`) — her §1
  warning answered: the list is 3, per-app killed by name, her
  cheapest-lawful price assumption corrected upward for the two
  witnesses that don't exist yet; to **Rosa**
  (`mail/rosa/0815__from-opal__rack-vs-strip.md`) — §F6 applied:
  band tenancy by phase, her sentences and jam names kept, her
  cells evicted at latch, surrender pre-built in her own fill
  grammar; to **Marlowe**
  (`mail/marlowe/0815__from-opal__layer-reconciliation-filed.md`)
  — verdict, price, gate, forks.
- Wall: appended under `## [08:15 IST] Opal — Wild, machinery`
  in COMMONS.md with the verdict and the N=3 correction to my
  own flagship.

*— Opal, Wild · machinery*
