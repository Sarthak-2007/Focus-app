# ISSUE #82 — WELLE REPORT · Audit R2 · clock-attacks (LEAD-COVERED: Welle personally; the spawn pool was saturated by the fleet-wide pace order, so the lead worked this issue directly and the lens sections below are the lead's signed passes)

Base: origin/main 0a00e95. All 5 findings verified against current code BEFORE fixing — every one HELD (none pre-cured on main).

## Per-finding dispositions

1. **[CRITICAL] Shield-gap verdict pure wall-clock — FIXED-IN-CODE**
   Evidence it held: `MonolithRepository.kt:1794-1802` latchShieldGapIfStale compared raw `nowWallMillis - beatRef` against the 10 s law; finish stamp at `:2066-2070` used `System.currentTimeMillis()` the same way. The beat is wall-only (`MonolithModels.kt:351`).
   Cure: both tests now run on the monotonic-implied now — `ClockHonesty.impliedNowMillis` (session start advanced by honest elapsed, mirroring computeVerifiedSessionElapsed's same-boot/post-reboot shapes). `latchShieldGapIfStale` now takes `nowElapsedRealtimeMillis` (sole caller MonolithViewModel.kt:972 updated to `SystemClock.elapsedRealtime()`). An 11 s forward jump forges nothing; a rollback launders nothing. No schema change (the cure needed no mono beat column — the session's own anchors suffice).

2. **[CRITICAL] Clock jump materialises + poisons a future routine window — FIXED-IN-CODE**
   Evidence it held: `MonolithBootReceiver.kt:60-66` routes ACTION_TIME_CHANGED to `RoutineHeartbeat.tick(force=true)`; reconcileOnce read `System.currentTimeMillis()` raw and fed `RoutineDerive.derive` (`RoutineDerive.kt:124-178`: unclaimed containing window → DesiredSession; claim = "no second mint, ever"; finaliseDue stamps MISSED).
   Cure: the reconcile's single "now" choke point now reads `repository.honestNowMillis()` — a forward jump leaves the honest now behind in real time, so no future window contains it, nothing materialises, nothing finalises early; TIME_CHANGED still triggers a reconcile, it just can't forge one. No routine-model change (the 05:07 rethink freeze is respected — this is a truth fix at the clock source, model-agnostic).

3. **[CRITICAL] Weekly emergency token infinitely renewable by date travel — FIXED-IN-CODE**
   Evidence it held: `MonolithRepository.kt:2226-2250` completeEmergencyBreak computed `MonolithWeek.weekAnchorMillis(System.currentTimeMillis())` and refused only on a spend booked against that travelled anchor; `MonolithWeek.kt:29-33` derives the anchor purely from the supplied wall instant.
   Cure: the anchor (and the spend's own timestamp) now derive from `honestNowMillis()` in completeEmergencyBreak, and the live remaining-count flow (`weekAnchorTickFlow`) anchors on the honest week too — date travel in either direction leaves the token in the real week: a spend this week refuses every travelled re-ask.

4. **[HIGH] Future-dated routine edit suppresses MISSED — FIXED-IN-CODE**
   Evidence it held: `MonolithViewModel.kt:1506` stamped `scheduleEditedAtMillis = System.currentTimeMillis()` raw; `RoutineDerive.kt:226-228` returns MISSED only when `closed.startMillis > scheduleEditedAtMillis`.
   Cure: the stamp is now `repository.honestNowMillis()` — a +30-day jump stamps the edit at the honest (real) instant, inside the tolerance band of real time, so the horizon gate compares real against real.

5. **[HIGH] 91-second blip permanently ratchets the 15-min tamper penalty — FIXED-IN-CODE**
   Evidence it held: `MonolithRepository.kt:1947` `penaltyAdd = if (tamper && penaltySecondsAdded == 0) PENALTY else penaltySecondsAdded` — once written, no path ever zeroed it, though every later tick recomputed drift ≈ 0.
   Cure: the penalty is split by conviction kind. HMAC failure (forged state — the real conviction) still sets it once and never unwinds. Clock-drift-only tamper applies the penalty only WHILE the drift persists and unwinds to 0 on the first clean tick (`penaltyAdd = when { !hmacValid -> sticky; tamper -> PENALTY; else -> 0 }`). Elapsed time is monotonic-anchored, so drift alone never gained the user anything; the Register M-1/M-2 note (penalty never cleared at a BLOCK BOUNDARY) is untouched — this unwind fires on a clean tick, not at a boundary, and every block still prices the penalty while it stands.

## The anchor (shared cure for F2/F3/F4)

New `app/src/main/java/com/example/data/ClockHonesty.kt`: persisted (wall, elapsedRealtime, bootCount) anchor in SharedPreferences (`monolith_clock_anchor`, no DB migration). Agreement within 120 s → wall believed, anchor refreshed. Beyond → honest now = anchor wall + monotonic delta. `MonolithRepository.honestNowMillis()` is the single door.
Stated limit (in code, not hidden): the anchor re-bases at face value on the first read of each boot — travel done while the device is off, before the first tick of the new boot, anchors as truth. Session verdicts keep their own per-session anchors with the same accepted shape.

## Tests

`app/src/test/java/com/example/ClockHonestyTest.kt` — 9 pure JUnit tests: face-value anchoring, agreeing clocks, +7-day jump, −7-day rollback, within-tolerance correction, new-boot re-anchor, forged-gap immunity, rollback-proof real gap, post-reboot implied-now shape. DECLARED, uncompiled locally per lane law — CI is the verdict.

## Lens sections (lead-signed)

- **Verifier (Welle):** every finding re-derived from current code at the cited lines before any edit; line numbers above are the base-tree lines where the flaw sat.
- **Brutal critic (Welle):** the residual attack is real — power off, travel, boot, spend before the first heartbeat tick, and the token/week logic anchors wrong for that boot. It is narrower than the original hole (which needed no reboot and worked mid-session, repeatedly) and it is written in the code where anyone can attack it next. The shield-gap cure trusts the session's start anchors; a row whose start anchors were themselves written under a travelled clock inherits that travel — same accepted shape as computeVerifiedSessionElapsed.
- **Diagnostician (Welle):** root cause across all five: four separate features each read `System.currentTimeMillis()` as if it were evidence. The cure is one door (`honestNowMillis`) + one pure module, so the next feature that needs "now" has exactly one honest place to get it.
- **Technical execution specialist (Welle):** diffs are surgical; no schema change, no routine-model change, no new dependencies; verifySessionState now runs twice per progress tick (once inside computeVerifiedSessionElapsed, once for the penalty split) — one extra HMAC over a small state blob per 1–2 s tick, negligible.
- **Consumer (Welle):** user-facing effects in plain words — (1) changing the phone's date/time can no longer fake a "shield was off" stain on a finished session, nor hide a real one; (2) the one-emergency-break-per-week rule now holds against date tricks; (3) a routine can no longer be secretly born early or marked missed by clock games; (4) an accidental time change mid-session no longer adds a permanent 15 minutes to the lock — the penalty now lasts only as long as the clock stays wrong (a forged-state penalty still never lifts). No visual or feature changes; nothing removed; no downgrade.
