# SLICE E × ROUTINES — THE NAMED INTERFACE
## Reihe (Improvers, routine rows) — SLICE E BUILD-READY wave

*My joint: where Slice E's seal/finish machinery (E1 Throw, E4 Release
Proof) meets Voss's routines rebuild (derive / heartbeat / finalise).
Sources checked in the trunk and the merged sheets, not in briefs
about them: `ROUTINES-REBUILD-READY.md` + parts (sten §1.5–1.6.2,
otis §2.5/§4, rurik, freya), `SLICE-D-E-BUILD-READY.md` (§0, D8, E1,
E2 as struck by B-5, E4, F15), seat parts orson §4, wren §4,
emil §3.2, and `MASTER-PLAN.md` §3.4 / §5.7 / §5.8. Trunk citations
are to the shipped tree at seating.*

---

## 0. THE INTERFACE, VERBATIM (this is the deliverable's headline)

Both session births already pass one point — the repository insert.
Neither birth passes Slice E's lawful seal point, because that point
(the PROVING → SEALED-RUNNING transition) lives, as specced, inside a
ViewModel-resident controller that a routine birth never enters. The
convergence is therefore built, once, as a named process-scoped
interface. This name, this signature, and these two callers are the
whole joint:

```kotlin
// NEW FILE: service/SessionSealTransition.kt
// Process-scoped (one instance per process, constructed with
// dao/repository + LiveWire registries + TactileSoundEngine).
// Owns the ONLY playSealThrow() call in the app, as the last
// statement of the SEALED branch.

interface SessionSealTransition {
    /**
     * Runs D8's 3-link cascade (L1 SESSION-ROW · L2 SHIELD-PROTECTING ·
     * L3 A11Y-SESSION-ARMED) for an already-inserted session row,
     * computes t=0, re-anchors + re-signs the row at t=0, then fires
     * E1's Throw. [latchMonoMs] is the manual arm's latch sample
     * (t=0 = max(lastLinkConfirmed, latchMonoMs + 210, pawlSeated));
     * a routine birth passes null and the latch term drops out of
     * the max (no hand, no breath to honour). The value NEVER reaches
     * the sound renderer — the renderer takes no provenance input
     * (orson §3.6), so routine and manual seals are bit-identical
     * by construction, not by discipline.
     */
    suspend fun proveAndSeal(sessionId: Int, latchMonoMs: Long?): SealVerdict
}

enum class SealVerdict { SEALED, JAMMED, ANNULLED }
```

**Exactly two callers, both after the insert returns:**

1. **Manual** — the Deck arm controller, at latch, exactly where
   SLICE-D-E §0 step 5 / E1 already put the transition. The VM keeps
   *rendering* PROVING from the transition's StateFlow; it stops
   *executing* the transition. One state machine, two doors.
2. **Routine** — `RoutineHeartbeat.tick`'s materialise dispatch
   (otis §2.5, row 1), as a **fourth act** after
   `applySmartLoadout → startSession(…routineId/window stamps) →
   startShield()`: `sealTransition.proveAndSeal(newId, null)`.
   `primeForSeal()` is called by the funnel at materialise start,
   standing in for the latch priming a routine has no latch for
   (orson §4.2; +2 lines, priced in §5 below).

**Deadline law is D8's, unchanged, both doors:** L+500 → JAMMED
(machine-only retry); pre-t0 failure at L+1500 → annulment by row
deletion. One routine-only guard rides the annulment: the funnel
records the annulled natural key `(routineId, windowStartMillis)`
in an in-process tombstone set, checked before materialise, so the
30s opportunistic ticks (otis §2.3 H2/H3) cannot re-mint/re-annul
the same window every 30s inside one process. The tombstone dies
with the process — a reboot earns exactly one fresh proving
attempt, which is the retry D8 intends, not a loop.

---

## 1. SESSION BIRTH — same insert, divergent everything-after

**Answer: the same function creates the row, in the trunk and after
the rebuild. The divergence is not the birth; it is that only one
birth ever reaches a seal.**

Trunk, verified:

- Manual: `MonolithViewModel.armMonolithSession()`
  (`MonolithViewModel.kt:909`) → `MonolithRepository.startSession`
  (**`:950`**) → insert at `dao.insertSession` → `startShield`.
- Routine: `RoutineAlarmReceiver.kt:44–49` — the same
  `repository.startSession(intention = routine.name,
  profileName = "Routine: ${routine.name}", …)` → `startShield`.
  Today both births are literally the same call, and today *neither*
  seals lawfully: the seal sounds twice, both times pre-insert
  (view `BlockitCoreViews.kt:1184`, VM `:910` in trunk numbering) —
  E1's two deaths, already specced.

After the rebuild, still the same function:

- Sten §1.5: the reconciler calls
  `repository.materialiseRoutineSession(desiredSession)`, which
  applies the loadout and inserts **through `startSession`**, which
  "gains those three as optional parameters with manual defaults
  (`routineId = null`, windows = 0)".
- Otis §2.5, row 1 (the funnel is "sole caller of this path"):
  `applySmartLoadout(loadout)` → `startSession(…, durationMinutes =
  remainder, + routineId/windowStartMillis/windowEndMillis stamps)`
  → `MonolithShieldService.startShield()`.

**So the shared point that exists is the insert return inside
`startSession` (`MonolithRepository.kt:950`). It cannot host the
Throw.** Orson's law (E1 §4.1) puts the call *after* the insert
returns but inside the PROVING → SEALED-RUNNING transition — and
for the manual arm the insert is only cascade link L1 of 3; t=0
waits on L2/L3 confirmation and the latch+210 term. A Throw planted
in `startSession` would fire before proof on every manual arm —
the exact pre-fact celebration E1 kills. Planting it in the
funnel after `startShield()` is the same lie one door down:
`startShield` swallows failures today (D8, L2: "`startShield` runs
only after the insert" and returns no verdict), so a routine Throw
there can sound over a shield that never started.

**And as the two merges stand, the routine birth never reaches the
transition at all.** The transition's owner is a VM controller
(D8: "PROVING is a VM controller StateFlow only"). A routine
materialise runs inside `RoutineHeartbeat.tick` — chain-alarm
`goAsync`, LOCKED_BOOT, boot, CRUD sources — in processes and
moments where no Activity, no ViewModel, and no latch exist. A
05:00 FASTED BLOCK materialise on an untouched phone would insert
a born-active row, start the shield, and seal **silently**: no
PROVING, no t=0 re-anchor, no Throw. Bit-Identical means *every*
seal; the merged sheets, read together, give routine seals none.
That is the hole this file closes with §0's interface — the
transition's *execution* moves to the process-scoped
`SessionSealTransition`; its *rendering* (the StateFlow the Deck
paints PROVING from) stays in the VM, fed by the transition.

This is a convergence, specced and priced (§5) — not a fork.
The cheap alternative is filed as Fork F-R3 with its price, and
recommended against.

---

## 2. THE VOICE AT A ROUTINE ARM — it fires, at full gain, and two laws collide over it

**Under B-5 there is exactly one lawful answer: the Throw fires
bit-identically at a routine seal — same 1000ms render, same fixed
seed, same body gain 0.62 (0 dB ref), bolt −6 dB at +30ms, room
tail reaching −46 dB at ≈900ms, same haptic.** E2 in the merged
sheet is struck in place: the Laid Seal (the quiet variant,
gain 0.124) is dead, and "the ruling governs placement, not
volume." A routine-only quieter seal *is* the Laid Seal wearing a
schedule; a routine-only silence is a second class of seal. Both
are provenance renderings, and provenance rendering is what B-5
killed. §0's interface enforces this structurally: the renderer
receives no origin, no latch value, no window fact.

**The consequence, stated honestly.** At 05:00 the phone is
typically across the room or on a nightstand, the room is silent,
and nobody is holding it. What fires is one second of sound at the
seal's full filed gain — a low thock with a room tail, over by
≈1000ms, never repeated, never looped — plus one haptic buzz,
which on a hard nightstand may carry further than the sound.
Sarthak's day makes this less exotic than it reads: he wakes at
03:30, so the 05:00 FASTED seal lands mid-morning-routine, not in
his sleep. The two night-edge cases are real and named: **SLEEP
seals at 20:00** (his sleep time — the Throw sounds in the room he
is falling asleep in) and a **ghost morning** (sick, away, phone
untouched — the Throw fires to an empty room, and the seal it
voices is the Dev-collision seal: the machine fired first). One
second, once, is the whole cost. There is no lawful volume knob
to soften it with; anyone who wants one must reopen B-5, not
quietly patch this joint.

**What the law says — it is NOT silent, and it disagrees with
itself:**

- `MASTER-PLAN §3.4` speaks *against the premise*: "a routine is
  a WINDOW, not a firing… Sessions inside a window are still his
  slides… **Nothing seals, cues demandingly, or starts counting
  at window-open**" (HPA-01/02/04). Read literally, §3.4 abolishes
  the routine birth §1 describes — no materialise, no seal, no
  voice question. This is **Fork F-R1**, the master fork, below.
- `MASTER-PLAN §5` speaks in pieces, none of them about this
  sound: §5.8's AOD is "unconditionally silent," and §5.8.7 rules
  a routine firing changes **zero pixels** and the idle face
  "becomes the running face at the next minute tick" — a §5 that
  elsewhere presupposes routine *firings* exist, against its own
  §3.4. No §5 passage names a seal sound, at a routine firing or
  anywhere (the seal's voice is deferred to §2.6/Slice E).
- Otis §4 **N1** speaks against the mechanism: the heartbeat tick
  carries "no haptic, no sound, no toast." Materialise is a tick
  dispatch; a Throw inside its proving transition is a sound the
  tick caused. N1's intent is that the *schedule pass* makes no
  noise of its own — the seal voice is a session act the tick
  dispatches, in the same class as `startShield()`'s notification,
  which N1 expressly leaves standing. But the letter of N1 and
  its grep acceptance (otis T9, tick purity) must be amended to
  say so, whitelisting the `SessionSealTransition` dispatch by
  name. Text amendment, 0 code lines, priced in §5.

**Fork F-R2 (for Ren, recommendation attached):** confirm the
Throw fires bit-identically at every routine seal, SLEEP's 20:00
seal included, with the N1 amendment above. The alternative —
routine seals silent or softened — requires reopening B-5 and
rebuilding E2's provenance machinery (≈ +110 net over Throw-only,
per the struck E2's own price), and buys a seal that tells him,
in tone, that the machine armed it. Recommendation: **fire it.**

---

## 3. E4's E0 FOR ROUTINE SESSIONS — observe the finalise stamp, not the progress flip

**Wren's E0, as specced, can never fire for a routine session.**
Her gate (wren §4.1, merged E4) is the frame
`updateSessionProgress` first returns `isActive = false ∧
completedSuccessfully = true`, observed on the session StateFlow.
Sten's fences break both conjuncts for routine rows:

- `updateSessionProgress`'s completion branch is fenced for
  `routineId != null`: at target it sets `isActive = false`
  **without** `completedSuccessfully = true` and without an
  artifact — the row sits *unfinalised* (`routineOutcome == null`),
  expressly so a routine session "can never mint minutes by
  merely reaching its target."
- The booking flip belongs to `finaliseRoutineSession`
  (sten §1.6.2, as amended by the merge's A2: `elapsedSeconds :=
  attendedSeconds`, capped, in **every** arm): ATTENDED writes
  `completedSuccessfully = true`; PARTIAL and MISSED write
  `false` — permanently. A gate keyed on `completedSuccessfully`
  gives PARTIAL/MISSED routine finishes **zero** E0s, forever.
- And finalise itself is *finalise-on-observe*: it runs inside a
  later tick (shield-loop completion edge, resume, boot, chain) —
  possibly **hours after** the true finish (stored
  `windowEndMillis`), in a process that may have died in between.
  There may be no VM alive at the true finish to observe anything.

**The edge Release Proof must observe — one DAO edge, one
terminal predicate.** A new per-id flow (the history flow's
LIMIT 60 is the wrong tool for a keyed observer):

```kotlin
// MonolithDao.kt (+4 lines)
@Query("SELECT * FROM focus_sessions WHERE id = :id")
fun getSessionByIdFlow(id: Int): Flow<FocusSessionEntity?>
```

E0 for session `id` = the first emission of `getSessionByIdFlow(id)`
in which the row satisfies the **terminal predicate**:

```
(!isActive && routineId == null && completedSuccessfully)                 // manual FINISH — wren's gate, unchanged
|| (!isActive && routineOutcome == "ATTENDED")                            // routine FINISH-equivalent
|| (!isActive && routineOutcome == "PARTIAL")                             // routine ended — receipt form, no pulse
|| (!isActive && routineId != null && routineOutcome == null
      && loadout == SLEEP && now >= windowEndMillis)                      // SLEEP — J2: never stamped, stands down by stored end
```

`MISSED` is terminal for *booking* and is **not** an E0: no held
frame, no receipt, no pulse for a session nobody attended — its
one telling is the Shield row's LAST line (sten/master §3.4).
Celebrating a ghost would be the finish-form lie §5.7's
never-share-an-exit-form law exists to kill.

**Form per outcome (inside the one E0 clock):** the E-clock,
reverse cascade, detention formula (receipt t=0 =
`min(max(E1480, R0+280), E1800)`) and HELD FRAME run identically
for ATTENDED and PARTIAL; the **completion pulse at E700 fires
iff the outcome is ATTENDED** (or manual FINISH / SLEEP-ran-full)
— PARTIAL is a booked-less-than-full ending and takes the B-12
treatment: no pulse, and the held frame's micro-line prints the
partial truth in sten's row vocabulary (`PARTIAL — 1H 05M OF 3H`).

**Exactly once — never zero, never twice — across process death.**
Observation alone cannot carry this (a VM that was dead at
finalise observes nothing; a VM restarted after issuance would
observe the terminal row again). Three rules close it:

1. **Live edge:** E0 fires on a live-observed transition into the
   terminal predicate for an id, keyed by `sessionId` in Fen's
   `ReceiptState` — rotate/recompose/tab-return never re-fires
   (emil's settled-on-return law, same shape).
2. **Cold-start edge:** on first collection, a row *already*
   terminal is a pending receipt — **unless** its id equals the
   persisted `lastReceiptSessionId` (below). Pending = issue the
   receipt **immediately on Deck open, held frame NOT replayed**
   (wren §4.3's process-death rule, generalised from RELEASING to
   finalise). The receipt's printed finish is the row's stored
   `windowEndMillis` — never the observation time; finalise
   lateness is the machine's delay and prints nowhere.
   Never-zero is this rule: a finish nobody witnessed is still
   told, once, at the next open. A newer session's seal voids an
   older unwitnessed receipt (the §5.7 lastReceipt slot is
   single; the older slip is superseded, not queued).
3. **The persisted marker:** `guard_settings` gains
   `lastReceiptSessionId INTEGER NOT NULL DEFAULT 0`, riding
   **Migration 9→10** in the same batch as emil's
   `heldAppsAtSeal` (F15) — +1 DDL line, +1 entity field, written
   at receipt issuance in the same transaction as the §5.7
   sequence-counter increment. Without it, "unreplayable even by
   process death" (§5.7 / §6.5) is a runtime claim on a VM-owned
   `ReceiptState` that dies with the process — for manual
   sessions the gap was theoretical; routine finalise makes it
   the common case.

**The detention race, named and ruled:** finalise may still not
have run when a receipt's detention ceiling arrives (edge
observed via the progress fence, outcome still null). At
issuance the receipt prints what the row *then* proves; if
`routineOutcome` is still null it issues **without an outcome
word** (§4), and a later finalise **never amends an issued
receipt** — wren's healed-fault law, applied to outcomes.

---

## 4. THE RECEIPT'S ROUTINE LINE — frozen at insert, read at issuance, zero new E columns

§5.7 (Uma's provenance): the receipt carries an arm-time frozen
snapshot, and "the routine line says *within* his routine, never
*by* it." The fields, from Migration 8→9's eight `focus_sessions`
columns (merge A1 census: `routineId`, `windowStartMillis`,
`windowEndMillis`, `routineOutcome`, `evidenceMask`,
`firstEvidenceMillis`, `lastEvidenceMillis`, + rurik's `taskId`):

| Field | Feeds the line? | When written |
|---|---|---|
| `routineId` | **Yes — existence + identity.** Line prints iff `routineId != null`. | Insert (materialise snapshot) |
| `windowStartMillis` / `windowEndMillis` | **Yes — the window printed.** Frozen schedule truth (sten R5). | Insert |
| routine **name** | **Yes — via `profileName`.** 8→9 adds no name column; the name survives frozen inside the existing `profileName = "Routine: <name>"` written at insert (sten: "legacy rendering only; identity is routineId"). The receipt strips the prefix. A mid-window rename rewrites the routine row, never this string — the receipt names the routine as it stood at sealing, by construction. | Insert |
| `routineOutcome` | **The outcome word only** (ATTENDED / PARTIAL), when stamped before issuance; omitted when still null (§3 race). Never the line's existence. | Finalise |
| `evidenceMask`, `firstEvidenceMillis`, `lastEvidenceMillis` | **No.** They price the booking (`elapsedSeconds` finalise rewrote); the receipt reads the booked result, not the evidence. | During session / finalise |
| `taskId` | **No.** Routine sessions carry no Arm Ticket (`taskId = null`); the receipt's micro-line for a routine session is its `intention` (= routine name at materialise), not a task. | — |

**Line form:** `WITHIN FASTED BLOCK · 05:00–08:00` — name +
frozen window, the routine as container. Never `BY`, never a
verb with the routine as subject. **Boundary:** a *manual*
session that overlaps a window (HELD, `routineId == null`) gets
**no** routine line on its receipt — printing one would require
recomputing a past overlap at issuance, an unfrozen fact; HELD's
telling lives on the Shield row, once, in its own home.

**Does E need any of this at seal time? No new capture — and one
binding coordination with `heldAppsAtSeal`.** `routineId` +
window stamps are already written at insert by materialise
(sten §1.5: "snapshot columns written at insert"). Emil's
`heldAppsAtSeal` capture point is the same insert, inside
`startSession` (emil §3.2), counted from
`dao.getAllAppsSnapshot()`. The two snapshots are therefore
frozen at the same act, and the routine path is safe **iff the
order inside the funnel stays**: `applySmartLoadout` (otis §2.5
row 1, act 1) **before** `startSession` (act 2) — the sealed
count then counts the routine's loadout, not whatever the Deck
had staged. The coordination rule for both builders, stated
once: **both snapshots are captured in the repository at
insert, never in a view or ViewModel.** A DOWN-time primed
snapshot (the dead E2 predicate's mechanism) has no routine
counterpart — a routine birth has no DOWN — and any builder who
moves `heldAppsAtSeal` capture up into the VM silently breaks
routine receipts while manual receipts keep working. Test T-R11
(§6) is the guard.

---

## 5. PRICE

Counting method: net production lines = lines added − lines
deleted, counted per file against the merged sheets' own filed
prices; work already priced inside D8 (cascade ≈465), E1
(Throw ≈210), E4 (finish ≈410), or the routines rebuild
(≈+1,050) is **excluded** — this is the delta for the joint only.
Tests counted separately, as the sheets count them.

| # | Item | File | Net |
|---|---|---|---|
| 1 | `SessionSealTransition` interface + impl: cascade invocation, t=0 with nullable latch term, re-anchor hand-off, Throw as last statement, priming entry | `service/SessionSealTransition.kt` (new) | +48 |
| 2 | Annulment tombstone set + check | in (1)'s funnel-facing half / `RoutineHeartbeat.kt` | +12 |
| 3 | VM arm controller delegates execution to (1); keeps PROVING rendering | `MonolithViewModel.kt` | −10 |
| 4 | Funnel materialise: act 4 `proveAndSeal(id, null)` + `primeForSeal()` at materialise | `RoutineHeartbeat.kt` (otis delta) | +7 |
| 5 | `getSessionByIdFlow` | `MonolithDao.kt` | +4 |
| 6 | E0 terminal predicate + outcome classification + cold-start pending branch, inside wren's priced VM finish machine | `MonolithViewModel.kt` | +22 |
| 7 | `lastReceiptSessionId`: DDL (9→10, with F15's column) + entity field + write-at-issuance | `MonolithDatabase.kt` / `MonolithModels.kt` / VM | +7 |
| 8 | Receipt routine line (existence, name strip, window format, outcome word-or-omission) | receipt composable / VM receipt builder | +14 |
| | **Total** | | **≈ +104 net production** |
| | Tests T-R1…T-R12 (§6) | test source set | ≈ +85 |

Fork F-R3's price, for comparison: **+6 net** (one direct
`playSealThrow()` in the funnel after `startShield()`), and it
buys a seal that can sound over a dead shield. Not recommended.

---

## 6. ACCEPTANCE TESTS

- **T-R1 Same birth, one voice.** Manual arm and routine
  materialise (fake clock 05:00, tick source BOOT, **no Activity
  or ViewModel created**) each produce exactly one session row
  and exactly one `playSealThrow()` invocation (counting fake
  engine), after the insert.
- **T-R2 One call site (grep).** `playSealThrow` occurs exactly
  once in production code, inside `SessionSealTransition`;
  `startSession`, materialise, and the heartbeat funnel contain
  none. Otis T9 tick-purity grep passes with the transition
  dispatch whitelisted by name (F-R2's N1 amendment).
- **T-R3 Routine jam.** Accessibility unbound at window start:
  cascade JAMMED at L+500, annulled at L+1500, **zero** Throws,
  row deleted; a RESUME tick inside the same window re-mints
  nothing (tombstone); process restart earns exactly one fresh
  proving attempt.
- **T-R4 Bit-identical.** PCM hash of a routine-triggered seal
  render equals a manual-triggered one; the transition passes
  no provenance argument to the engine (asserted by signature).
- **T-R5 Late finalise.** Routine session; process killed
  before window end; relaunch 3h after: finalise-on-observe
  stamps the outcome; receipt issues exactly once at Deck open,
  held frame not replayed, printed finish =
  `windowEndMillis`; dismiss → kill → relaunch → no second
  receipt (`lastReceiptSessionId` holds the id).
- **T-R6 Live exactly-once.** ATTENDED finalise with Deck open:
  E0 fires once; rotate, recompose, leave and return mid-hold —
  no re-fire (sessionId key).
- **T-R7 MISSED silence.** Ghost window, zero evidence: finalise
  MISSED, `elapsedSeconds = 0`, zero E0s, zero receipts, zero
  pulses; the Shield row's LAST line is the only telling.
- **T-R8 PARTIAL form.** PARTIAL outcome: receipt issues on the
  E-clock, `playCompletionPulse` call count 0, micro-line prints
  `PARTIAL — {booked} OF {window}`.
- **T-R9 SLEEP edge.** SLEEP session stands down at its stored
  end with `routineOutcome == null`: the predicate's SLEEP arm
  fires the E0 path exactly once; the receipt carries the
  within-line and **no** attendance word.
- **T-R10 Frozen line.** Rename + retime the routine
  mid-session: the receipt prints the insert-time name (from
  `profileName`) and the insert-time window. A manual session
  overlapping the window prints no routine line.
- **T-R11 Snapshot coordination.** Edit app tiers after a
  routine materialise: imprint + receipt sealed counts equal
  the count at materialise (post-`applySmartLoadout`, at
  insert), not the edited count. Same test, manual arm, for
  regression.
- **T-R12 Outcome race.** Detention ceiling reached with
  `routineOutcome` still null: receipt issues with no outcome
  word; the later finalise writes its stamp and does not amend
  or re-issue the receipt.

---

## 7. FORKS

- **F-R1 — THE MASTER FORK (Ren only).** `MASTER-PLAN §3.4`'s
  windows law ("nothing seals… at window-open; sessions inside
  a window are still his slides") vs the routines rebuild's
  materialise (the window seals itself, attendance-priced).
  Both are merged documents; §5.8.7 of the same master plan
  presupposes firings §3.4 abolishes. **Recommendation:** the
  rebuild governs the machinery (it is the later, build-ready,
  coordinator-merged spec of exactly this mechanism, and §3.4
  by its own header owns only "what Shield shows of it") — Ren
  strikes or re-scopes the one §3.4 sentence. If Ren instead
  rules §3.4 governs whole: this file's §§1–4 are moot, the
  joint costs **0 lines**, and the death is on the routines
  side (materialise + the funnel's session dispatch, inside
  sten's ≈+170 reconciler block) — E then needs nothing from
  routines, because there are no routine sessions.
- **F-R2 — THE VOICE AT 05:00 (Ren; §2).** Confirm bit-identical
  Throw at every routine seal (SLEEP 20:00 included) + the
  otis-N1 text amendment. Alternative reopens B-5 at ≈ +110 net.
  **Recommendation: confirm.**
- **F-R3 — THE CHEAP CONVERGENCE (Ren; §1/§5).** One lawful
  call site via `SessionSealTransition` (+104 net, this file)
  vs a second, funnel-local `playSealThrow()` call (+6 net)
  that sounds before any proof a routine seal holds.
  **Recommendation: the interface.** A seal voice that can fire
  over a dead shield is the pre-fact celebration E1 exists to
  kill, moved to the one hour he cannot check it.

No other forks. Q3's edge and Q4's line are specced closed above.

## 8. NAMED RISKS

- **Split-brain transition.** Execution moves out of the VM;
  PROVING rendering stays. If a builder keeps a second,
  VM-local transition path "for the UI," the two machines
  drift and T-R2 is the only thing standing between the build
  and a third seal site. The interface is the transition; the
  VM is a subscriber.
- **Audio-focus at 05:00.** `primeForSeal()` requests transient
  focus at materialise (orson's risk 6, extended): if he sleeps
  to media, the duck window opens at the routine seal, not at a
  latch. Filed, bounded (≤210ms semantics), not hidden.
- **Tombstone amnesia.** In-process only, by design (§0): each
  process start inside an annulled window earns one re-prove.
  On a crash-looping phone inside a window that is one proving
  attempt per crash — bounded by the crash rate, never a timer.
- **SLEEP's unfinalised stand-down** is the predicate's weakest
  arm (no stamp to observe; it keys on stored window end +
  `isActive` flip). If a future build stamps SLEEP rows, the
  arm collapses into the ATTENDED arm and this risk retires.

— Reihe, Improvers · routine rows · for Ilya (coordinator) and
Voss (routines lane). No code edited, no gradle run.
