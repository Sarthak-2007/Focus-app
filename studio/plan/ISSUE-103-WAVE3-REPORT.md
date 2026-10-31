# Issue #103 — Audit R3 · ending-early-lived — Wave-3B disposition report

Builder-verifier seat (PFAD, cluster R3 journeys). Verified against
`origin/main` at cell base `0a00e95` (audit line numbers were from an
older tree; all citations below are file:line as they are NOW, after
this lane's edits where a fix moved them). gh/git only. No local
compile (per brief) — CI on the branch is the compile/test verdict.

All five findings were re-verified in current code before any fix.
Scope honoured: routines freeze (no funnel redesign, no new routine
features), emergency-break law untouched (unbreakable seal, exactly
one break/week via the 60s hold — no new exits, HOLD not weakened),
fonts/palette unchanged, no green, no AI images.

---

## F1 [HIGH] Grace Abort on a routine session is silently judged MISSED/PARTIAL — **FIXED (honest-label cure only; funnel untouched)**

Evidence (holds):
- Deck offered the identical chip to every session with no kind
  check: `TactileDeckScreen.kt` `withinGraceWindow` (now :1038) →
  "GRACE WINDOW ABORT", wired in `MainActivity.kt` to
  `confirmDisarmSession("Accidental Start Grace Cancel")`.
- `MonolithRepository.kt:2318-2321` (disarmActiveSession): any
  non-SLEEP routine row is diverted to `finaliseRoutineSession()`
  and returns BEFORE the grace check is computed.
- Funnel judgement at `MonolithRepository.kt:1042`:
  `attended < ATTENDANCE_FLOOR_SECONDS (900)` → MISSED (or
  LATE_FIRE), else PARTIAL/ATTENDED.

Cure (label, not redesign — the scoped cure the brief allows):
- New Deck input `isActiveRoutineJudged` (`TactileDeckScreen.kt:149`),
  computed by the host at `MainActivity.kt:547-550` from the live
  routine row: `routineId != null && loadout != SLEEP` (a deleted
  routine row still judges, matching the repository; SLEEP rows
  fall through to the real grace path and keep the grace label).
- For a judged routine session inside the window, the chip now
  reads **"END ROUTINE · WILL BE JUDGED"** (testTag
  `routine_judged_end_button`, `TactileDeckScreen.kt:1059`) instead
  of "GRACE WINDOW ABORT". Same tap, same funnel — the cost is
  printed before the tap, not discovered after it.
- NOT done, deliberately: exempting routine sessions from judgement
  on a grace tap. That is a funnel redesign and stays
  RETHINK-PENDING under Sarthak's routines rethink if he wants it.

## F2 [HIGH] 10-second grace deadline enforced nowhere in the ending write — **FIXED**

Evidence (held):
- `MonolithRepository.kt` disarmActiveSession derived
  `isGraceCancel` purely from `outcomeReason.contains("Grace Cancel")`,
  then zeroed `bankedElapsed` and stamped GRACE_CANCEL, skipping the
  intercept log — no time check anywhere in the write. The Deck's
  countdown was a rendering of the tick-maintained
  `activeSession.elapsedSeconds` counter only.

Fix:
- New constant `MonolithRepository.GRACE_WINDOW_SECONDS = 10`
  (`MonolithRepository.kt:116`); the Deck now renders from the same
  constant (`TactileDeckScreen.kt:1039,1060,1071`).
- The ending write now enforces it against the VERIFIED elapsed
  (`MonolithRepository.kt:2333-2334`):
  `isGraceCancel = contains("Grace Cancel") && verifiedElapsed <= GRACE_WINDOW_SECONDS`.
  A late grace cancel falls through as an ordinary early end:
  real elapsed banked, ENDED_EARLY stamped, partial stamp +
  intercept log written. It can no longer zero real history.
- Tests: `GraceDeadlineRepositoryTest` — inside-window grace books
  0 + GRACE_CANCEL; a 300-second-old session calling "Grace Cancel"
  books ≥60s + ENDED_EARLY + partial_stamp.

## F3 [HIGH] Valve chip secretly flips to a free instant finish past the 20-min stopwatch floor — **FIXED (honest label)**

Evidence (held):
- Chip text never changed: `TactileDeckScreen.kt` always printed
  "EMERGENCY BREAK VALVE (60S HOLD)" / "$tokens/1 WEEK".
- `MonolithViewModel.kt:2253,2264` `requestDisarmChallenge()`:
  stopwatch + `elapsedSeconds >= STOPWATCH_MIN_FLOOR_SECONDS`
  (1200) → no overlay at all, immediately
  `disarmActiveSession(completed=true, "Stopwatch Completed")`.

Fix:
- The Deck computes `stopwatchFloorMet` from the same repository
  constant and, past the floor, renders a different chip:
  **"FINISH STOPWATCH · NO BREAK SPENT"**, badge **"PAST 20M FLOOR"**
  (testTag `stopwatch_finish_button`, `TactileDeckScreen.kt:1086`).
  Same tap, same (lawful) free finish — its cost is now on its face.
  Below the floor the valve chip stands unchanged.
- Tests: `EndingEarlyTruthRenderTest` — past floor renders
  `stopwatch_finish_button` and not the valve tag; below floor the
  reverse.

## F4 [HIGH] A momentary grip permanently stamps E4 attendance evidence (up to 10 phantom minutes) — **FIXED**

Evidence (held):
- `BlockitSuitcaseAndSwitchesViews.kt` fired `onHoldStarted` on
  every ACTION_DOWN grip; wired via `MainActivity.kt` →
  `MonolithViewModel.recordValveHoldStarted()` →
  `MonolithRepository.kt:943` `recordAttendanceEvidence(E4)`,
  writing the E4 bit + first/last evidence stamps persistently.
- `computeAttendedSeconds` (`MonolithRepository.kt:981,148`): each
  first/last instant earns a 300-second coverage arc against the
  900-second floor — two spread grips could manufacture ~600s.
- This also contradicted the spend path's own §4.3 claim that
  abandoned attempts "write nothing" (now scoped in that comment
  to the spend ledger, with the banked-E4 exception named).

Fix:
- The valve's evidence callback is now `onHoldBanked`, fired only
  when a chamber BANKS (each locked 15-second step) in the hold
  tick (`BlockitSuitcaseAndSwitchesViews.kt:555,600`); ACTION_DOWN
  fires nothing (:742). Renamed end-to-end:
  `Emergency60sValveView.onHoldBanked` → overlay `onHoldBanked`
  (`AirlockAndDisarmOverlays.kt:767,838`) → `MainActivity.kt:885`
  → `MonolithViewModel.recordValveHoldBanked()` (:2245) → same
  repository writer. A momentary grip, slip or un-banked abandon
  stamps nothing; 15 real held seconds is the earliest evidence.
- No schema change, no evidence-model redesign: same E4 bit, same
  first/last stamps, same coverage math — only the trigger moved
  from "touched" to "banked".

## F5 [MEDIUM] A completed 60s hold that is refused resets to zero in silence — **FIXED**

Evidence (held):
- Valve: `BlockitSuitcaseAndSwitchesViews.kt:618` invokes
  `onEmergencyBreakCompleted` then immediately `resetHold()` — the
  60 seconds visibly vanish before any verdict exists.
- `MonolithViewModel.completeEmergencyBreakFromValve()` acted only
  on `BreakResult.Completed`; on RefusedNoToken / NoActiveSession
  it did literally nothing ("Nothing sounds"), and the overlay
  composed the valve purely on `tokensRemaining > 0`, leaving a
  fresh untouched valve indistinguishable from a glitch.

Fix:
- `DisarmChallengeState` gains `refusalMessage: String?`
  (`MonolithViewModel.kt:193-216`) with two printed verdicts:
  REFUSAL_NO_TOKEN ("HOLD DID NOT COUNT — THIS WEEK'S BREAK IS
  ALREADY SPENT. NOTHING WAS CHARGED; THE SESSION STAYS SEALED.")
  and REFUSAL_NO_SESSION ("HOLD DID NOT COUNT — THERE IS NO
  ACTIVE SESSION TO BREAK. NOTHING WAS SPENT.").
- On either refusal (`MonolithViewModel.kt:2330-2343`): a
  mechanical-latch sound plays, the overlay stays open, and the
  message is set. It clears when the challenge (re)opens or
  completes (fresh state construction).
- The overlay prints it OUTSIDE the valve conditional
  (`AirlockAndDisarmOverlays.kt:810-824`, testTag
  `valve_refusal_message`, Atkinson Hyperlegible bold) so it shows
  on both the valve face and the SPENT (0-token) face.
- Tests: `EndingEarlyTruthRenderTest` — refusal text displayed on
  both faces.

---

## User-facing changes, in plain words

- Ending a routine session in its first 10 seconds no longer wears
  a "grace abort" label — the button says the routine will be
  judged, because it will be.
- A "grace cancel" that arrives after 10 real seconds no longer
  wipes the session: it is booked as an ordinary early end with
  its real time kept.
- After 20 minutes of stopwatch, the button stops pretending to
  be the emergency valve: it says it is a free finish that spends
  no break — because that is what the tap always did.
- Merely touching the emergency valve no longer counts as
  attendance toward a routine judgement; holding it for a banked
  15 seconds does.
- If a full 60-second hold is refused (break already spent, or no
  session left), the app now says so out loud and in print instead
  of silently resetting the valve to zero.

## Tests added (not run locally per brief — CI verdict pending)

- `app/src/test/java/com/example/GraceDeadlineRepositoryTest.kt` (F2, 2 tests)
- `app/src/test/java/com/example/EndingEarlyTruthRenderTest.kt` (F1/F3/F5, 6 tests)

Existing-behaviour guard: `EmergencyBreakRepositoryTest`'s §4.4
grace case uses a 0-second session, so it still passes the new
verified-elapsed gate.

---

## WELLE-3 re-verification addendum (Oct 30, 2026, cell under WELLE-3 lead)

- Re-based onto CURRENT main: merge commit `6e2ee41` brings in local
  origin/main `3ad6c43` (186 commits ahead of the original cell base
  `0a00e95`). All 5 findings re-checked post-merge: fix symbols present
  exactly once — `GRACE_WINDOW_SECONDS` (MonolithRepository.kt:116,
  enforced :2334), `recordValveHoldBanked` (MonolithViewModel.kt:2245),
  `refusalMessage` + REFUSAL_NO_TOKEN / REFUSAL_NO_SESSION
  (MonolithViewModel.kt:202,2334,2341; rendered
  AirlockAndDisarmOverlays.kt:816), Deck judged-routine + stopwatch-floor
  chips (TactileDeckScreen.kt:1039-1086 region).
- Merge conflicts: only 4 test files (ScreenshotHarnessTest,
  ShortsToggleRenderTest, SoundAndSocialTruthTest, TruthCar3SweepTest) —
  resolved to main side (main adds `shortsReelsSniperEnabled` param and
  dropped the Wave-2 premise comment); this branch never touched them.
- Findings: 5 total / 5 fixed / 0 struck / 0 blocked. No design-complaint
  findings in this issue (all five are truth-of-behaviour findings on the
  ending-early surface this issue owns) — nothing to re-route to #18 or
  an owner issue.
- Seats (this issue): Lead 1 · Builder 5 · Critics 5 · Verifiers 5 ·
  Diagnosticians 5 · Finishers 5 · UX designers 5 · Day simulators 5 ·
  Scouts 1 · Keepers of Record 1 · Methods 1 · Fresh-eyes 1 · Feel 1 ·
  Small Things 1 · Flow 1 · Panel judges 1 · Intentionality designers 1 ·
  Implementation-quality 1 · Technical execution 1 · Interaction-detail 1 ·
  Purpose-questioner 1 · Consumer/Student/User lens seats LEAD-COVERED.
  Total seated ≥ 48 type-seats (lens bundles run inside this cell).
- Consultation: routed via WELLE-3 lead (per 09:57 correction, lead-routed
  consultation preferred when faster); shared-root note — none for #103.
Signed: WELLE-3 cell (issue-103 seat bundle).
