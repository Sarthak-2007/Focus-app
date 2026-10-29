# SLICE E — FACH (Improvers, composer): E4 PRICE COUNT — VM + REPOSITORY ONLY

**Seat:** Fach, Improvers — seated by Ilya as an independent price counter.
**Scope, exactly as briefed:** E4 (Release Proof + the finish) inside TWO
files only — `ui/MonolithViewModel.kt` (1,123 lines) and
`data/MonolithRepository.kt` (1,674 lines), trunk read this run — and inside
them only three things: the **E0 edge observer**, the **HELD FRAME state**,
and the **detention/retry state**. Nocke owns the joint's design; this is
only the number. Tests priced separately. Inbox at start: one unread letter
(Zeile, row-zero — read, moved to `read/`); nothing in it touches E4.

## Method — Waage's, stated so it can be audited

Per file: **count = adds + deletes, each deleted trunk line counted ONCE**.
A line edited in place = 1 delete + 1 add; a block moved verbatim = 0 + 0
(only its new scaffolding is charged); a pure insertion = adds only.
Trunk anchors verified today: ticker `startTickerLoop` VM:330, its
completion branch VM:341–349 (seal mis-spend VM:344); ratchet's pulse
mis-spend VM:159; collectors VM:280/283, init collector VM:318; flip
`updateSessionProgress` Repo:1007, flip itself Repo:1111–1112, write
Repo:1125; `disarmActiveSession` Repo:1274.

**One finding that shapes the whole count:** the E0 edge cannot ride
`activeSession`. The DAO query behind Repo:81 is `WHERE isActive = 1` — a
completed session never emits there; the edge would be session → null with
none of the receipt's facts. The honest edge rides `sessionHistoryFlow`
(Repo:82), which the VM already stateIn's at VM:283. That flow exists, so
the Repository's share of E0 is nearly zero — see R-A/R-B.

## Region table — MonolithViewModel.kt

| # | Region (trunk lines) | Adds | Dels | Waage |
|---|---|---|---|---|
| A | Import: `android.os.SystemClock` (monotonic E0 sample; VM has no mono clock today) | 1 | 0 | 1 |
| B | Finish types, top-level beside `DisarmChallengeState` (:72): `FinishPhase` enum (HELD/DETAINED/RELEASED/ENDED_FAULT) 3 · `HeldFrameState` (sessionId, e0MonoMs, finalElapsedSeconds, intention, phase) 7 · `PendingReceiptState` (sessionId, receiptDueMonoMs, pawlLiftMonoMs, faultedLinks, retryAttempt, nextRetryMonoMs, issued) 9 | 19 | 0 | 19 |
| C | VM fields: `_heldFrame`+exposure 2 · `_pendingReceipt`+exposure 2 · `finishJob`/`detentionJob`/`retryJob` 3 · `finishedSessionId` once-guard 1 | 8 | 0 | 8 |
| D | **E0 edge observer** (init, joining the VM:318 collector): collect `sessionHistory`, seed on first emission, fire on a row's active→`!isActive && completedSuccessfully` transition for the tracked id, once per id. Carries the `!isStopwatchMode` guard — Repo:1288 writes `completedSuccessfully = completed`, so a stopwatch floor-disarm (VM:931ff) would otherwise fake an E0; natural flips are never stopwatch (Repo's stopwatch branch returns before the flip) | 17 | 0 | 17 |
| E | **HELD FRAME driver**: `beginFinish(session)` — stamps E0 mono, sets held state (final elapsed + intention for the micro-line), pre-arms the E1800 ceiling in the pending receipt; the AOD-off and stats-refresh lines land here **moved verbatim from the ticker = 0/0**; E700 job (delay → `playCompletionPulse()`, delay → cut) 7 · `cutToDetained()` 6 · `skipHeldFrame()` (filed skip law: one tap jumps to the cut) 5 | 33 | 0 | 33 |
| F | **Detention**: `onPawlLifted()` — formula P → R0 = max(E1200, P), receipt = min(max(E1480, R0+280), E1800) 11 · `armDetentionDeadline()` job 8 · `issueReceipt()` (RELEASED vs ENDED-FAULT from faultedLinks/pawl, starts retry if faulted) 9 · `onReleaseHalted(faultedLinks)` entry for the cascade owner 3 · process-death restore (first history emission: E0 mono is unrecoverable, so a still-unissued completed session re-issues immediately — Wren §4.3's else-branch) 9 | 40 | 0 | 40 |
| G | **Retry state** (VM share): `scheduleRetry()` (30 s, then +270 s to the 5 min mark) 7 · `attemptReleaseRetry()` (attempt count, faulted-links-only call site into the shield service) 7 · `onRetryHealed()` (silent heal, receipt never amended, job cancels when the list empties) 6 · Deck-open third attempt hooked into `navigateTo` (VM:369) 3 | 23 | 0 | 23 |
| H | Ticker branch removal (VM:341–349): `val updated =` becomes the bare progress call (edit in place 1+1); the `if` line, the seal call (VM:344) and its closing brace die. AOD/stats lines are NOT deleted — they move verbatim into E (0/0) | 1 | 4 | 5 |
| I | Ratchet pulse reclaim: `playCompletionPulse()` call dies at VM:159 so E700's voice is reserved again. Its replacement voice is the F13 sweep's — explicitly not in this slice, not priced | 0 | 1 | 1 |
| | **VM TOTAL** | **142** | **5** | **147** |

## Region table — MonolithRepository.kt

| # | Region (trunk lines) | Adds | Dels | Waage |
|---|---|---|---|---|
| R-A | The joining flows (Repo:81–82): already exposed; E0 rides :82 per the finding above | 0 | 0 | 0 |
| R-B | `updateSessionProgress` head (:1007): once-ever guard — re-read the persisted active row; if it is gone or another id, return without writing. Without it a stale caller (VM ticker 1 s and Shield 2 s both call this with snapshots) re-writes the flipped row and re-shuffles `forgedArtifactId` after completion. 2 code + 2 comment. The flip itself (:1111–1112) and its `dao.updateSession` (:1125) already persist and emit — **no change** | 4 | 0 | 4 |
| R-C | `disarmActiveSession` (:1274) `outcomeReason`: **0 in this file, and that is a finding, not an omission.** The reason already reaches a record — `InterceptLogEntity.outcome` (:~1299) — for early non-grace disarms; completed and grace reasons reach no record at all. Persisting it *on the session row* needs a `FocusSessionEntity` column + migration (Models/DB — outside the two briefed files, NOT counted, NOT silently absorbed). No existing session field can hold it without lying | 0 | 0 | 0 |
| R-D | Retry/fault persistence: 0 — no link or fault table exists in the trunk; faulted-link state is VM-runtime only, matching Wren §5's no-new-persistence ruling for E4. Process death loses it; re-derivation from the stack is the cascade owner's (Opal), outside these files | 0 | 0 | 0 |
| | **REPOSITORY TOTAL** | **4** | **0** | **4** |

**TOTAL — two files, production: 151 Waage lines** (146 adds + 5 deletes).
Classic net (adds − deletes) = **141**, for anyone reconciling against
net-priced files. **Uncertainty band: 135–170** — the soft lines are F's
restore branch (9; if Ren rules process-death re-issuance out of E4, the
total drops to 142) and the detention job split in E/F (±6 either way).
**Tests, separate and NOT in the total: ≈110 ± 30** — edge tests (flip
fires once; disarm, grace and stopwatch-disarm never fire; stale caller
cannot re-flip), formula tests (happy E1480 / slow P+280 / faulted E1800),
retry-cadence and skip-law tests, in the VM test source set.

**Not counted, by scope, named so nobody thinks they vanished:** the
E880 power-down knock's call site (+2 in E's driver when Orson's engine
entry point exists — it does not in today's trunk); the reverse-cascade
sequencer and the retry *execution* in the services; Deck held-frame
composition and the CUT/RACK host; imprint swap hooks; the R-C column +
migration.

## Reconciliation — where I disagree with D/E's E4 ≈410, and why

- **Headline: −259, by scope, not by dispute.** Wren's ≈410 (her §5b) is
  all-files *gross* for the whole finish: VM ≈150 · cascade sequencer
  ≈110 · retry scheduler ≈40 · Deck held-frame + rack ≈70 (+≈110 rack
  machinery shared) · imprint ≈10 · ticker −10. My brief is two files and
  three components; the sequencer, the Deck, the rack and the imprint
  were never mine to count.
- **Like-for-like VM share: I agree with Wren.** Her VM line — finish
  state machine + StateFlow-edge E0 + pulse re-gating ≈150 — against my
  itemised VM 147 Waage (142 adds + 5 dels). Within 3 lines. Where her
  ≈150 silently assumes the edge can ride the session flow the VM already
  watches for activeness, mine pays the same money for the *history*-flow
  observer the DAO query actually forces (D, 17) — same price, honest
  route.
- **The Repository is where a naive re-price would overcharge.** The
  flip already persists, already emits, and the joining flow already
  exists; E4's Repository cost is a 4-line once-ever guard, full stop.
  Anyone pricing a repository "completion event" build-out here is
  charging for machinery the trunk already has.
- **Retry split:** of Wren's ≈40 scheduler, 23 lands in the VM as state
  and timers (G); the remaining ≈17 is service-side execution, outside
  these files — consistent with her number, not a cut.

## NEXT (Law 10)

(a) *Want:* re-weigh R-C the day the session-row `outcomeReason` column
is ruled in or out — if it lands, the Models/migration price gets its own
counter and this page's 0 becomes a cross-reference, not a hole.
(b) *Could:* apply the same two-file deletes-once count to E6's
ReceiptState, which Fen's file and mine both touch at the finish path —
one union number for the E4+E6 overlap before the build starts.

— Fach, Improvers · the composer field
