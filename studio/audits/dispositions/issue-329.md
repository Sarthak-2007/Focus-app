# Issue #329 — Flipd Comparative audit (4 findings) — WELLE-7 cluster J disposition

Lead: Welle (cluster J, replacement — first lead died at bootstrap; verified
0 prior comments and no branch for #329 at claim time).
Verified against origin/main @ b7a52cc. All four findings describe defects
in **Flipd**; on every line Monolith is the opposite construction, so each
is dispositioned VERIFIED-FALSE as a Monolith loss (a recorded win).

Seats (12, weighted comparative/purpose): Purpose-questioner · Brutal
critic · Verifier · Builder · Implementation-quality · Designer ·
Consumer · Student · Day simulator · Technical execution · Fresh-eyes ·
Keeper of Record. All seats LEAD-COVERED in this cell's passes except
Verifier + Purpose-questioner, run as separate grep/code passes.

- F1 Diary vs evidence — VERIFIED-FALSE (Monolith win). Monolith sessions
  are sealed at arm time and HMAC-signed (`CryptoIntegrityManager.kt:116`
  `signSessionState`); the record latches shield-gap/tamper state
  (`MonolithModels.kt:356` `FocusSessionEntity`). There is no manual
  add/edit-session surface anywhere in `app/src/main/java` — DAO
  insert/update are called only by the session lifecycle in
  `MonolithRepository.kt` (startSession :1686). No leaderboard exists to
  rank anything.
- F2 No website blocking — VERIFIED-FALSE (Monolith win). Monolith ships
  a per-domain site blocker: rules persist as `BlockedSiteEntity`
  (`MonolithModels.kt:685`), enforced against the browser's actual URL
  by the accessibility door (`MonolithAccessibilityService.kt` class
  doc :26-33), user-editable in Shield.
- F3 No clock start / schedule engine — VERIFIED-FALSE (Monolith win).
  Monolith has a full routine engine: `RoutineEntity`
  (`MonolithModels.kt:691`, start/end minutes + daysMask + loadout
  binding) fired by `RoutineScheduler.kt:23` + RoutineAlarmReceiver +
  RoutineHeartbeat. (Routines are arm-windows for the blocker — the app
  remains a blocker, not a general scheduler product.)
- F4 ~90 hard-coded always-allowed — VERIFIED-FALSE (Monolith win).
  Every installed app carries a user-set LOCKED/PEEK/OPEN tier
  (`MonolithModels.kt:268` `BlockedAppEntity`); there is no vendor
  exemption list in code — the only `allowedApps` hits are a UI count of
  the user's own OPEN apps (`MainActivity.kt:1045`). Disclosure: #332
  adds a narrow SAFETY floor (dialer/telecom/keyboard/emergency can
  never be locked) — Spotify, Uber, payments and every content app
  remain fully lockable by the user, so this finding's substance stands.
