# 23 — ZEILE (Improvers, task rows) · SLICE E BUILD-READY
## The Tasks screen at the session finish — unseal truth, the reopened task, arrival at E0

*Seated by Ilya (Improvers coordinator) for the SLICE E BUILD-READY
wave. My joint: Tasks (`FocusSequencerScreen.kt`) at the finish —
MASTER-PLAN §4.1's sealed-state law against SLICE-D-E entries
E4/E6. All file:line against TODAY's trunk
(`~/workspace/clone-inspect/Focus-app`), read this run; sibling
parts cited by file. No code edited, no gradle run. Inbox at
start and before filing: no unread letters (nine in `read/`,
none touching this joint).*

**Headline verdicts**

1. **Tasks never reads the ticker branch. It reads the coarse
   flow edge — and always has.** Its unseal is downstream of the
   repository's flip *write*, not of the VM branch at
   `MonolithViewModel.kt:341–348`. It therefore inherits the
   flip's **write-side** lateness (≤1000ms, shared with the Deck)
   and none of the branch's. E4's StateFlow edge, as designed by
   Nocke (06) and priced by Fach (25), does **not** move the
   write earlier — so the build-ready text's claim that E4 kills
   the up-to-1000ms lateness is true only of announcement
   relative to the write, and false end-to-end, for both
   screens. Named as the split in §1.3; my ruling: accept
   E0 = the write instant, one finish instant app-wide.
2. **The ticker branch has exactly three side effects** (seal
   sound, desk-clock hide, stats refresh) **and one of them is
   already racy, one is multiply redundant, and one is
   under-served by a genuine-completion-only edge.** The lawful
   shape is **two edges**: a coarse *ended* edge (Tasks unseal,
   desk-clock hide, `effectiveNight`) and a discriminated
   *finished* edge (pulse, receipt, №, sled). Two edges is not
   the smell — forcing one edge to carry both facts is. §1.4.
3. **A reopened task touches nothing in Slice E's finish state.**
   `ReceiptState`, the № counter and `lastReceipt` are all keyed
   by `sessionId` and written only at a claimed genuine session
   completion; the reopen path writes task rows only (plus, in
   Ruth's lane, the *active* session's summary). Boundary proven
   in §2. **One real leak exists — and it is not Slice E's:** a
   reopen *after* the session finished leaves the finished
   session's `completedTasksSummary`/`tasksCompletedCount`
   permanently false, because Ruth's receipt repair is scoped
   to the still-active session. Routed to Ilya in §2.3.
4. **Arrival at E0 renders correctly with zero new wiring — and
   Tasks must never be wired to the finish state.** The single
   Boolean gate makes every composition wholly sealed or wholly
   open; §4.6's DONE-below-the-line does not move at a session
   finish at all. If a builder gates Tasks on
   `finishState == IDLE` / receipt / rest, Tasks stays sealed
   1.2–3.1s after the session ended — a §4.1 violation. I
   dissent, by name, from Linea 19 §3 Layer 2's extension of
   the detention gate to Tasks' own rows (fork F-Z1, §3.4).

---

## 1 — Q1: HOW TASKS LEARNS A SESSION ENDED

### 1.1 The chain, line by line (trunk, today)

| Step | Site | Fact |
|---|---|---|
| Flip written | `MonolithRepository.kt:1007` `updateSessionProgress`; the ending copy at `:1110–1111` writes `isActive = !allBlocksCompleted, completedSuccessfully = allBlocksCompleted`; persisted by `dao.updateSession` (`:1125` per Fach 25) | The **only** natural-completion write. Stopwatch never flips here — its branch returns early with `isActive` untouched (Repo `:1049–1062` per Nocke 06 §1.1); stopwatch ends via `disarmActiveSession` (Repo `:1274–1310`, `isActive = false` at `:1287–1288`) |
| DAO flow | `MonolithDao.kt:57–58` — `getActiveSessionFlow()`: `WHERE isActive = 1 … LIMIT 1` | After the flip the row **leaves the query**: the flow emits `null`, never the completed row |
| Repository flow | `MonolithRepository.kt:81` `activeSessionFlow = dao.getActiveSessionFlow()` | — |
| VM flow | `MonolithViewModel.kt:280–281` `activeSession = repository.activeSessionFlow.stateIn(WhileSubscribed(5000), null)` | One host-level collector feeds every screen |
| Host | `MainActivity.kt:169` collects `activeSession`; `:390` passes `isSessionActive = activeSession?.isActive == true` into `FocusSequencerScreen` (Deck gets the same expression at `:258`, Shield at `:358`; `effectiveNight` at `:232` derives from the same value) | Tasks' predicate is **cause-blind**: `null` (any ending — flip, disarm, grace, valve, routine) unseals it. That is §4.1's law: *everything returns on session end*, not on genuine completion |
| Screen | `FocusSequencerScreen.kt:77` — one `isSessionActive: Boolean` parameter. Every sealed gate branches on it and nothing else: `effectiveNight` `:98`, blank-leaf perforation `:282`, leaf item `:306`, `tear()` guard `:374`, leaf perforation `:399`, running mark `:413`, `DONE — TEAR` absence `:488` | One Boolean ⇒ one composition is wholly sealed or wholly open (§3) |

**The ticker branch is not in this chain.** Deleting
`MonolithViewModel.kt:343–348` wholesale would not delay Tasks'
unseal by one frame: the unseal rides the `dao.updateSession`
write *inside* `updateSessionProgress`, which the branch merely
also observes via the return value in the same iteration.

### 1.2 The lateness Tasks actually inherits — write-side, dual-driver

The flip exists only when someone *calls* `updateSessionProgress`.
It has exactly two callers on two clocks:

- VM ticker: `startTickerLoop` (`MonolithViewModel.kt:330`),
  branch at `:340–348`, `delay(1000L)` at `:364`.
- Shield monitor: `MonolithShieldService.kt:81` (loop
  `:67–149`), `delay(2000L)` at `:145`; on `!updated.isActive`
  it stops itself (`:82–88`).

So the true target instant is crossed between ticks, and the
flip is written at the next tick of whichever driver fires
first: **≤1000ms late while the VM ticker lives** (the Shield's
2s cadence can only beat it by phase, never worsen the ceiling).
Tasks inherits this in full — no write, no emission, no unseal.
What Tasks does *not* inherit is any branch-side lag: when the
VM ticker wins the race, branch and unseal are simultaneous;
when the **Shield** wins, Tasks unseals and the VM branch
**never runs at all** (the ticker's next iteration reads
`activeSession.value == null` at `:340` and skips) — proof the
branch's side effects are racy-optional while the flow's are
guaranteed (see §1.4, desk clock).

### 1.3 What E4's edge does and does not kill — the split, named

Both filed E4 designs observe a flow, and neither schedules
the write:

- **Nocke (06 §3):** a collector on `repository.activeSessionFlow`,
  edge `prev.isActive == true ∧ current == null`, re-reading the
  ended row via a new `dao.getSessionById(prev.id)`; ticker
  branch cut, ticker keeps display ticking. His own acceptance
  line concedes the seam: *"the emission-to-flip-write lag is
  measured on device, not assumed."*
- **Fach (25, region D/H):** the observer rides
  `sessionHistoryFlow` (Repo `:82`) instead — because the
  `WHERE isActive = 1` query means a completed session never
  emits on `activeSession` — and the ticker becomes *"the bare
  progress call"*: it still calls `updateSessionProgress` every
  1s. That call is what writes the flip, still on the 1s grid.
- No Slice E part prices a target-scheduled writer (no
  exact-alarm / delay-until-target flip). Draht 05's
  AlarmManager adoption is for the *release retry*, not the flip.

**Verdict:** SLICE-D-E's E4 entry ("today it is discovered up
to 1000ms late on the 1s ticker", `MonolithViewModel.kt:316–335`
in its older numbering) conflates write and discovery. E4
kills the *discovery* layer — which for the Deck's announcement
was already zero when the VM won its own race, and infinite
(the branch never ran) when the Shield won. The honest gain of
E4 is **reliability and discrimination** (the finish fires
exactly once, for genuine completions only), not end-to-end
latency. **Tasks' finish latency after E4 is unchanged:
≤1000ms write quantization + ≤1 frame emission-to-composition**
(Nocke's E0 acceptance). My ruling as this seat: **accept it,
and name E0 = the flip-write instant everywhere** — Deck held
frame, Tasks unseal, AOD hide all share one E0, so no two
screens can disagree about whether the session has ended; the
alternative (a scheduled writer) is a new mechanism nobody
priced, chasing a sub-second truth no surface prints. The
build-ready E4 text should be corrected to say so — filed to
Ilya, not silently absorbed.

### 1.4 Every other consumer of the old ticker branch — census

The branch (`MonolithViewModel.kt:343–348`) has exactly three
side effects beyond the progress call. Each one's fate on E4's
edge:

| Consumer | Trunk site | Served by the discriminated FINISH edge? | Verdict |
|---|---|---|---|
| **Seal sound** `playHarmonicSeal(arming = false)` | VM `:344` | Yes — that *is* E4: the mis-spend dies, the voice becomes the completion pulse at E700, FINISH outcomes only (Nocke 06 §3; Siegel 01: this site is E4's, untouched by E1) | **Served.** The branch's raison d'être, correctly re-homed |
| **Desk-clock hide** `_isAlwaysOnDeskClockVisible.value = false` | VM `:345`; consumer is the overlay's pure `visible` flag (`MainActivity.kt:517–523`; `AirlockAndDisarmOverlays.kt:68ff` — the flag is the only switch; a missed hide leaves the clock up over idle time) | **Under-served.** Fach 25 region E moves the line verbatim into `beginFinish` — which fires only on FINISH outcomes (his region D guard: `completedSuccessfully ∧ !isStopwatchMode`). But the clock must hide on **any** ending (MASTER §5.8.1: the AOD exists only while sealed). The disarm paths mostly duplicate the hide — `confirmDisarmSession` VM `:956`, valve VM `:980` — but the stopwatch-complete path (VM `:931–938`) **already lacks it today**, and when the Shield wins the flip race (§1.2) the branch never runs and the clock stays up | **Re-home to the coarse edge.** One line in the VM's ended-edge collector (any `active → null`), alongside Tasks' predicate. Cause-blind need, cause-blind edge. ≈2 lines inside E4's VM share; not a third mechanism |
| **7-day stats refresh** `_sevenDayStats.value = repository.compute7DayHistory()` | VM `:346`; reader is the Vault/Ledger surface via `sevenDayStats` | **Does not need the edge at all.** The refresh is idempotent (Repo `:1308ff` recomputes from the history snapshot) and already has five other triggers: init VM `:315`, `navigateTo(VAULT)` VM `:374–378`, stopwatch disarm VM `:935`, `confirmDisarmSession` VM `:961`, valve VM `:983`, plus `refreshSystemGuard` VM `:1104`. The only surface that displays stats is reached *through* `navigateTo(VAULT)`, which recomputes before display | **Safe to lose from the branch.** Fach moves it into `beginFinish` anyway (0/0) — harmless surplus, not a dependency. Day-granularity data; a ≤1000ms or even until-next-navigation staleness is invisible. Not a smell |
| *(not branch consumers, for completeness)* Shield self-stop; `effectiveNight`; Tasks unseal | Shield `:82–88`; MainActivity `:232`; §1.1 | Shield: E4 **commands** `stopShield` at its cascade step instead of waiting for the 2s poll (Nocke 06 §3, Draht 05 §4.2) — served by the cascade, not the edge. `effectiveNight` + Tasks: coarse flow, automatic | Two edges stand: **coarse** (ended: Tasks, AOD hide, night) + **discriminated** (finished: pulse, cascade, receipt, №, sled). Each consumer sits on the edge whose fact it actually needs. The smell would be a single edge pretending "ended" and "finished" are one fact — stopwatch and valve endings are the counterexamples |

Also recorded: the VM's `activeTask` collector (VM `:318–325`)
is not a branch consumer but reacts at unseal — once the
session is inactive its dial/intention writes un-gate, which is
Linea 19 §2.2's detention leak on the Deck; her
`finishState == IDLE` collector gate is the fix, and it belongs
to the Deck's frozen record, not to Tasks (§3.4).

---

## 2 — Q2: THE REOPENED TASK vs SLICE E'S FINISH STATE

*The path: Wieder's Last-Finished Line, ruled IN by Sarthak
(RULINGS: "Add it — let me undo a mistaken finish", Oct 29
09:10; DIGEST-AND-DRAWER-BUILD-BRIEFS B-3 amendment) — latest
completion only, `REOPEN` composed only while unsealed, calling
the existing repository toggle; building in Ruth's lane.*

### 2.1 The boundary, proven object by object

**`ReceiptState` (E6/Fen) — no knowledge needed, none possible.**
Fen's spec (slice-de-parts/fen.md §3a) defines it as
`ReceiptState(sessionId: Int, receiptPublishedMonoMs: Long)` —
VM-owned, no DB write, **no task field exists in it**. Its
+1000ms sled job is guarded by (i) `sessionId` still the latest
completed session, (ii) Deck composed with the receipt as
tenant, (iii) `returnRunForSessionId` once-only. A reopen writes
`focus_tasks` rows via `completeTaskAndAdvance`; it creates no
session, changes no `sessionId`, publishes no `ReceiptState`.
A reopen landing *inside* detention (E0→E2480 — lawful, since
REOPEN is composed from E0, §3) therefore cannot cancel, delay
or re-target the sled trigger: the guard's three conditions are
all session-keyed, and the reopen moves none of them.

**The № counter — incremented by sessions, never by tasks.**
MASTER §5.7: *"a monotonic sequence counter, incremented only
at genuine completion."* Nocke 06 §3 gives it a mechanism:
`claimComposite(sessionId, outcome)` — one transaction that
returns `null` (SUPPRESSED) if `lastCompositeSessionId ==
sessionId`, else sets it and does `receiptSequence += 1`. The
reopen path never calls it, and the trunk toggle's un-complete
branch (`MonolithRepository.kt:1441–1450`) writes **task rows
only** — `status` restored to QUEUED/ACTIVE,
`completedAtMillis = 0L`, `dao.updateTask` — it never touches
the session row's `isActive`/`completedSuccessfully`. A
reopened task is therefore provably not an un-finished
session: the session stays COMPLETED in history, its № stays
consumed, and no second claim can fire for it. № must not
decrement on reopen, and in this design it *cannot*.

**`lastReceipt` — overwritten by sessions, never by tasks.**
§5.7: *"A single `lastReceipt` slot, overwritten by the next
session"* (Nocke homes it on the `guard_settings` singleton,
written inside the same claim transaction). Linea 19 §5.4/T10:
the strike is keyed once-ever by `sessionId`; a reopen cannot
re-fire it. A reopen creates no session ⇒ no overwrite, no
replay, no amendment. The receipt of the session that
*contained* the task keeps printing the arm-time frozen
snapshot (Uma's provenance, §5.7) — which is correct: the
receipt is a record of the session, and the session did end
with that task marked done at the time.

**The one build trap, forbidden by name:** §5.7's receipt
content list is closed — provenance snapshot, deflections,
death line, break label, № — and contains **neither**
`completedTasksSummary` **nor** `tasksCompletedCount`. If a
builder sourced any receipt line from those live fields at
issuance (E1480), a reopen inside E0→E1480 would rewrite the
receipt mid-detention — a real leak through a shortcut the
spec does not take. Build note for E4/E6: *the receipt reads
the E0/arm-time snapshots and the claim transaction only;
no task-completion aggregate is a lawful input.*

**Tasks-side effects of a reopen are lawful truth, not
contamination:** `completedTasks` (VM `:182–185`) drops the
task the frame its status flips, so its §4.6 done row leaves
below-the-line; the Footing re-sums it as it re-enters
ACTIVE/QUEUED. That is Tasks describing the present — the
finish state describes a past session. The two never share a
field.

### 2.2 What the reopen does *not* repair — the adjacent leak (not Slice E's)

Named so nobody mistakes it for a finish-state hole, and so
Ilya can route it to Ruth's lane, where it belongs:

- Trunk: the un-complete branch never touches the session
  (Wieder phase-1 #4), so a task completed mid-session and
  reopened leaves its entry in that session's
  `completedTasksSummary` and `tasksCompletedCount + 1` behind
  forever (completion write: Repo `:1463–1475` in trunk
  numbering from my read — entry appended, count incremented,
  `dao.updateSession`).
- Ruth's lane **builds** the repair — drawer tree
  `MonolithRepository.kt:1389–1411`: on un-complete, strip the
  task's exact entry and decrement. **But it is scoped to the
  still-active session:** it reads
  `dao.getActiveSessionSnapshot()` and requires
  `target.completedInSessionId == session.id`. A reopen
  **after** that session finished — the common Last-Finished
  case, noticing the mistake once the session is over — finds
  no active session (or a *different* one) and the repair does
  not run. The finished session's record keeps the false entry
  and count permanently; the Vault renders that count
  (`CurioVaultScreen.kt:257`), and an honest re-completion in a
  later session writes the task into a second session's
  summary — Wieder's "stored twice, one of them false forever,"
  surviving for exactly the post-finish reopen.
- This is a **session-record** defect (Drawer/Vault lane),
  not a Slice E finish-state defect: ReceiptState, № and
  `lastReceipt` are clean (§2.1) because none of them reads the
  summary string. Cure, when Ruth's lane takes it: on reopen,
  look the session up **by `completedInSessionId`** (the by-id
  query Nocke's E4 already adds) rather than by active-session
  snapshot, and repair that row. ≈+6 lines in her repair block;
  priced here only so the routing letter carries a number.

---

## 3 — Q3: DONE BELOW THE LINE — THE FINISH LANDS WHILE HE IS ON TASKS

### 3.1 Can he be there? Yes — three lawful ways

1. **Trunk, today:** navigation is ungated — bottom nav
   `MainActivity.kt:497–505` calls `viewModel.navigateTo(dest)`
   unconditionally; `navigateTo` (VM `:369–379`) has no session
   check (Band 20 §1, trunk table). He can sit on Tasks for the
   whole session.
2. **Built world, Band's lock notwithstanding:** Band's travel
   lock (20 §1.2–1.3) swallows nav *taps* while sealed — but a
   **routine-armed session** starts wherever he happens to be
   (`RoutineAlarmReceiver` arms sessions independently of the
   current destination; cf. Siegel 01 fork F-S1). If he is
   reading Tasks when a routine seals, he is on Tasks, sealed,
   at E0. Band's lock governs travel, not presence.
3. **§4.1 presupposes it:** the sealed Tasks law exists because
   he can be there — *"facts stay readable, verbs leave"*;
   §0 standard 2 makes navigation read-only while sealed, not
   impossible.

### 3.2 What renders — arrival in the same second as E0

Tasks holds **no per-destination snapshot**: `activeSession` is
collected once at host level (`MainActivity.kt:169`) and
`isSessionActive` is re-derived at every composition
(`:390`). Three instants, exhaustively:

| Arrival | First Tasks composition | Then |
|---|---|---|
| Already there before E0 | Sealed form (§4.1 spec; trunk partial, §3.3) | At the flip emission, **in-place unseal, no animation ceremony** — §4.1's own closing sentence. One recomposition: because every gate reads the single Boolean (§1.1), the flip is **atomic** — no frame exists with DONE back but the perforation still solid, or the running mark lingering over open verbs |
| After the emission, same second | **Open form, first frame.** There is no cached sealed state to thaw; `AnimatedContent` composes Tasks with the collector's current value | Nothing further — he never sees the sealed form, correctly: it was never true *while he was looking* |
| Same frame as the emission | At most **one sealed frame**, then the atomic flip above | The collector's latest value is applied at composition; a composition cannot render sealed *after* the collector holds `null`, because the parameter is an expression over that value, not a stored copy |

**Under Band's built lock**, a *tap-driven* arrival in the
E0 second is impossible by construction: E0→E1200 the tap is
the skip, E1200→rack end it is swallowed, travel releases at
rack end (E1480 happy / ≤E2080 faulted — Band 20 §1.3). The
earliest lawful arrival therefore composes **after** the flip
— row 2 of the table, open at first frame. The same-second
question collapses, in the built world, to the already-there
case (row 1) and the post-rack arrival (row 2). Both render
right.

### 3.3 Below the line (§4.6) — the finish moves nothing there

The foundation and its done rows are derived from **tasks**,
never from the session: `completedTasks` = tasks filtered
`COMPLETED`, sorted by `completedAtMillis` (VM `:182–185`).
The flip's copy (Repo `:1104–1119`) writes session fields only
— elapsed, `isActive`, `completedSuccessfully`, tamper,
`forgedArtifactId` — **no task row is written by a session
finish**. Therefore:

- At E0 the done section is pixel-identical before and after:
  same rows, same order; §4.1 already prints it while sealed
  (*"the foundation with its done rows still print"*).
- **The session's own task does not drop below the line at
  E0.** It stays in the ACTIVE block as fact (`RUNNING` →
  open ACTIVE) until his own DONE event (§4.5's transaction)
  moves it. A finish that struck or filed the task would be
  Linea 19 §2.2's lie in my screen's form: session elapsed ≠
  task done, in code and in spec.
- Trunk caveat, stated so the verdict isn't read as a trunk
  PASS: today's sealed Tasks is **partial** — grep-verified,
  `isSessionActive` occurs in `FocusSequencerScreen.kt` only
  at `:77, :98, :282, :306, :374, :399, :413, :488`; the
  composer block, sub-step rows, `SPLIT`/`CUE`/`DEL` carry no
  gate at all (the Critics' tear charges, Moss, stand). So
  today the E0 flip's *visible* delta on Tasks is small —
  perforation un-solids, `DONE — TEAR` appears, the running
  mark vanishes, `effectiveNight` un-forces (`:98`). The §4.1
  full form (composer/verbs absent, then returning) is that
  section's own build. **Slice E owes Tasks no flip wiring —
  the Boolean already lands; what is owed is more elements
  gating on it, and that is §4.1's wave, not E4/E6's.**

### 3.4 The one wiring that must NOT happen — fork F-Z1

Linea 19 §3 Layer 2 extends her VM detention gate to *"the
Tasks screen's own rows during detention"* — verbs composed
but refused while `finishState != IDLE`, i.e. until rest
(E3130). For the **Deck's** task line that is right: the Deck
is showing a frozen record under a printed receipt. For
**Tasks itself I dissent, and file it as a fork**:

- §4.1 is this screen's sealed-state law and it is explicit:
  *"Everything returns on session end, in place, with no
  animation ceremony."* Session end is E0. A verb that renders
  at E0 and refuses until E3130 is the dead-control costume
  §4.1 bans, worn in reverse.
- Detention detains the **receipt** (§5.7: *"detained
  ≤1.8s"*), not the user. Even Band — the strictest travel
  law in the slice — releases travel at **rack end**, while
  the receipt and sled run on to E3130 (20 §1.3): the studio's
  own pattern is that post-rack, he is free and the finish
  finishes without him.
- Practical shape of the error: he finishes a session, opens
  Tasks at rack end, taps DONE on the task he just worked —
  and for up to ~1.6 more seconds the app silently refuses the
  act it is displaying. That is a lie by form on the screen
  whose section motto is facts-readable, verbs-honest.

**F-Z1, for Ilya/Ren:** Tasks' content gate = the coarse edge
(`isSessionActive`), full stop; Linea's detention gate binds
the Deck task line and the VM's Deck-facing collectors only.
If Ren rules the other way, §4.1's closing sentence needs an
amendment in the open, not a quiet exception in a VM predicate.

---

## 4 — BUILD NOTES & PRICE (my joint only)

| Item | Cost |
|---|---|
| Tasks flip wiring for E4/E6 | **≈0** — the coarse edge already lands; no subscription to `finishState`/`ReceiptState` is to be added (F-Z1) |
| Desk-clock hide re-homed to the coarse ended-edge collector (§1.4) | ≈2, inside E4's VM share (amends Fach 25 region E: split `beginFinish` into a coarse `onSessionEnded` carrying AOD-off, and the FINISH-only driver carrying the pulse) |
| E4 build-ready text correction (§1.3: E0 = write instant; lateness claim) | 0 — words, Ilya's merge |
| Acceptance tests (§5) | ≈25 test lines |
| **My share total** | **≈2 production + ≈25 test** |

Excluded by name: §4.1's full sealed-form build for Tasks
(composer/verb gating — §3.3, another wave); Ruth's by-id
receipt repair (§2.2, her lane, ≈+6); Band's nav lock (20);
Linea's Deck snapshot/collector gate (19).

## 5 — ACCEPTANCE TESTS

- **T-Z1 (atomic unseal):** on Tasks at the flip, one
  composition diff: perforation, `DONE — TEAR`, running mark
  and `effectiveNight` all change in the same frame; no
  intermediate frame mixes sealed and open gates.
- **T-Z2 (arrival):** navigate to Tasks in the same second
  after the flip emission → first composition is the open
  form; zero sealed frames rendered.
- **T-Z3 (below the line):** screenshot-diff §4.6's section
  across E0 is empty; the session's task remains in the ACTIVE
  block until a DONE act.
- **T-Z4 (reopen isolation):** reopen the session's task at
  E0+500 (mid-detention): `ReceiptState` unchanged, sled
  trigger still fires at receipt+1000, № unchanged,
  `lastReceipt` strike does not re-fire; the task's done row
  leaves below-the-line in the same emission.
- **T-Z5 (edge split):** end a session by grace cancel with
  the desk clock visible → clock hides on the coarse edge;
  no pulse, no receipt, no № consumed. End by natural flip
  with the Shield winning the write race → clock still hides,
  finish still claimed exactly once.
- **T-Z6 (F-Z1, if ruled my way):** DONE on Tasks at
  rack-end+1ms commits; the repository records exactly one
  completion event for it.

## NEXT

- **What I want next:** rule F-Z1 with Ilya/Ren and, if it
  lands my way, sit with Linea to re-scope her Layer-2 gate's
  wording so the Deck's frozen record keeps its protection
  without the predicate ever reaching Tasks' rows — one
  sentence in her file, one in mine, same hour.
- **What I could do unassigned:** audit the remaining
  `isSessionActive` consumers app-wide (Shield screen's
  `ratchetWarningMessage` surface and the Guard header pill
  read the same coarse value) for any other surface quietly
  assuming "ended = finished" — the §1.4 split applied as a
  sweep, before a builder wires one of them to E4's
  discriminated edge by mistake.

— Zeile, Improvers · task rows
