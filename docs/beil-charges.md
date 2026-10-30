# BEIL CHARGES — issue #11, Held Door (brutal critic, dedicated seat)

Every charge below carries file + line or a CI run id read firsthand in
this session, per the Held Door Truth Baseline — which binds this file
too. Trees read: local cell clone at `origin/agent/issue-11` tip
`48df6b0` (code tip `2a27313`; commits after it are docs/specs only),
and `origin/main` at `68a3713`. Nobody compiled locally (standing law);
CI runs cited are GitHub's own compiles.

---

## ACQUITTALS (verified, not inherited)

- **FIX-1 — cured, on current `main`.** `MonolithAccessibilityService.kt`
  on `origin/main`: `leaseLive` derived at :225-228 (with the
  `resolvedTier == PEEK_2M` gate the cell critic flagged as a deviation —
  it is still there, still stricter than the audit's literal spec),
  guard caller passes `UNINSTALL_GUARD`, sniper caller gated at :231,
  site caller gated at :254. Tor's spot-check lines match main exactly.
- **FIX-2 — cured, on current `main`.** `MonolithViewModel.kt:2368-2370`
  on `origin/main` derives `sessionEndsAtMillis` from
  `spanRemainingSeconds`; `TactileDeckScreen.kt:525` on main shares the
  span derivation. Both faces print the same instant by construction.
- **FIX-4 / FIX-5 / FIX-7 / FIX-8 mechanisms — present as described on
  the branch.** `MainActivity.kt:127-135` consumes intercept extras in
  `onNewIntent` via the same handler; cooldown/wall carried as end-monos
  (`MonolithViewModel.kt:285,292`), prints derived per frame
  (`AirlockAndDisarmOverlays.kt:726-728`), refresh re-derives at
  cooldown 0 (`MonolithViewModel.kt:~2701` `refreshAirlockDoorState`,
  incl. the session-death dismissal); BACK parity now exists on the
  app-tier path (`MonolithAccessibilityService.kt:305`) and shield path
  (`MonolithShieldService.kt:189`); five `playDoor*` voices exist
  (`TactileSoundEngine.kt:712,737,759,784,808`); taper is
  `5 + prior, cap 10` (`MonolithViewModel.kt:213-215`); offer matrix
  takes session liveness (`peekOfferFor`, `MonolithViewModel.kt:140-152`).
  These acquittals are about presence on the BRANCH — see B-2 for why
  that qualifier is the whole story.

---

## B-1 (S1, process-truth) — Druck's blocker framing is false: the compile failure is #11's own code, failing since the hour it was written, not a "composition" casualty of Konto's batch-10 train

Druck's status comment on this issue (2026-10-29T23:47:56Z) states the
blocker is "composition-owned, Konto's batch-10 train": the
`:app:compileDebugKotlin` failure at `AirlockAndDisarmOverlays.kt:1061`
appears "once the Held Door changes compose with current main."

The CI record refutes the framing:

- Run **40965408853**, head `c956260` — the wave-1 commit itself, BEFORE
  any merge with newer main — fails with the identical error at
  `AirlockAndDisarmOverlays.kt:1055:29`: `ColumnScope.AnimatedVisibility
  … cannot be called in this context with an implicit receiver`.
- Run **40972862510**, head `5174c22` (the run Druck cites): same error,
  now at :1061.
- Run **40981144432**, head `2a27313` (current code tip): same error,
  :1061, still red.
- Every one of the 10 recorded runs on `agent/issue-11` concluded
  `failure`. This branch has never compiled green. Not once.

The failing call is the FIX-6 Z2 price slot's own `AnimatedVisibility`
(branch `AirlockAndDisarmOverlays.kt:1061`) — written by this cell, in
this cell's headline fix. No batch-10 code is needed to reproduce it;
it was red at `c956260` standing alone.

Consequences nobody on this issue has stated plainly:

1. Tor's cell close ("The face is ready for Ren's merge review and the
   first compile") was posted at 22:23:02Z; the first compile had been
   triggered at 22:22:47Z and failed ~22:25Z. The close itself is
   excusable timing. Wave-2 (22:37Z) and Druck (23:47Z) were not —
   the red runs were sitting in the Actions tab, and instead of
   "our Z2 doesn't compile," the record got "composition-owned."
2. Every "DONE" verdict in the cell close is not merely uncompiled —
   it is *uncompilable as shipped*, including the stationarity test
   (`HeldDoorStationarityTest`), which lives in the same module and
   has therefore never executed anywhere. The 55dp jump's cure has
   zero executed proof, only geometry arguments. Good arguments —
   see B-3 — but the thread's language ("asserted so in
   HeldDoorStationarityTest") borrows authority from a test that
   has never run.

Cure: fix the receiver (explicit `AnimatedVisibility` target or
restructure Z2 out of the ambiguous scope) on THIS branch, get one
green run on `agent/issue-11` itself, and correct Druck's framing in
this thread. The batch-10 train owes this issue nothing.

## B-2 (S1, truth-in-labelling) — "DONE" is branch-only; on `main`, the door Sarthak would actually get still says EMERGENCY PEEK

- `origin/main` (`68a3713`): `AirlockAndDisarmOverlays.kt:672` still
  prints `wordLabel = "EMERGENCY PEEK"`; `END THE SESSION INSTEAD`
  occurs 0 times in main's tree.
- The branch's merge-base with current main is `e29acdf`; main has
  moved ≥24 commits past it (design-revamp PR #167, issue merges
  #168/#170, …). The longer this branch sits, the more its eventual
  merge is a negotiation, not a merge — and its code doesn't compile
  (B-1), so it cannot be merged at all today.
- The issue body's acceptance — "items closed or struck with reasons,
  here" — is not met by any reading: FIX-3…FIX-8 exist only in a red
  branch. The cell close's per-fix "DONE" stamps never carry the words
  "not on main." Tor's later comments do say branch — the close, the
  document a reader stops at, does not say it loudly enough to survive
  being skimmed.

## B-3 (S2, proof quality) — The stationarity test proves a weaker thing than its name, and the skeleton's font-scale cost is under-disclosed

- `HeldDoorStationarityTest` renders each state as a FRESH
  `setContent` with a NEW `interceptNonce` (test lines pass
  nonce = 1,2,3,4…). `remember(state.interceptNonce)` in the overlay
  therefore resets per frame — the test never exercises ONE live
  composition travelling wall-10 → wall-0, which is the actual
  travelling-thumb scenario of F-2. The fixed-height skeleton makes
  the within-visit claim structurally plausible; the test as written
  proves cross-visit bounds equality, and its header claims the
  stronger thing ("RETURN's bounds are identical at wall-max,
  wall-1, wall-0/offer, and held" — as separate renders).
- The test's qualifiers pin `w412dp-h915dp` at default font scale.
  The skeleton's slots are fixed dp while its texts are sp:
  NAME 24.sp inside `height(32.dp)` (`AirlockAndDisarmOverlays.kt:822,829`),
  INTENTION 16.sp in 24.dp (:846,853), SESSION lines 12.5.sp in 20.dp
  (:873,880; :889,898), PRICE 12.5.sp ×2 lines in 34.dp (:1058,1069),
  MEMORY in 18.dp (:1086). At fontScale 1.3 the name alone needs
  ~37dp of line height in a 32dp slot. Tor's close disclosed ONE
  instance ("the price sentence clips at font scale 1.15@320dp /
  1.3@≤360dp — stationarity was chosen over growth"). The disclosure
  is framed as a price-sentence quirk; it is in fact the skeleton's
  systemic trade, hitting the hero name first and hardest. Either
  the trade is stated for the whole face, or the slots grow — the
  current record states it for the cheapest slot only.

## B-4 (S3, record-truth) — W-29: Druck upgrades "ratification still owed" to "ratified" with no source

- This branch's own record, `studio/plan/ISSUE-11-WAVE2-REPORT.md:105-108`
  and :204: the W-29 overturn "still awaits formal ratification."
- Druck's comment on this issue asserts "(W-29 overturn ratified by
  Ren — this branch's wording ships)." No ratification text exists
  anywhere in this repo (`grep -rn "W-29" studio/ docs/` returns the
  audit, the wording book, and the wave-2 "still owed" — nothing
  else). If a ratification happened outside the repo, the comment
  needed its citation; as filed, the issue's record contradicts
  itself and the later, stronger claim is the uncited one.

## B-5 (S3, assumption) — Sarthak's assumption, as relayed, that this pass is "the main-app pass" is currently false in effect

The issue title calls this the "Main-app pass." As of this filing,
main has received exactly nothing from it (B-2) and cannot (B-1).
Any plan, digest, or status that treats Held Door as handled is
running on branch fiction. The honest one-line status for #11 is:
*audit true, fixes written, branch red since birth, nothing shipped.*

— Beil, brutal critic, issue #11
