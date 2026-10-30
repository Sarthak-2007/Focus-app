# Issue #78 — Audit R2 · ipc-intents — WELLE cell report

Cell: `builds/cells/issue-78`, branch `agent/issue-78`, base `0a00e95` (current origin/main at seating).
Code commit: **99ca645** — "Issue #78 (Audit R2 ipc-intents): pin Settings intents, persist intercept consumption + revalidate Airlock, fail-closed shield dispatch" (7 files, +607/−52; not pushed, not compiled locally — CI is the compile court).
Tests added: `app/src/test/java/com/example/Issue78IpcIntentsTest.kt` (10 tests, Robolectric runner).

Counts: **fixed 3 · verified-false 0 · blocked 0.**

---

## Finding 1 — [MEDIUM] FIX-pane Settings navigation uses unpinned implicit intents

**Disposition: FIXED-IN-CODE (99ca645)**

Verified against the current tree first (audit lines still exact on 0a00e95):

- `MainActivity.kt` `openFixPane` (pre-fix L371–414) built bare implicit intents: `ACTION_ACCESSIBILITY_SETTINGS` (L375), `ACTION_USAGE_ACCESS_SETTINGS` (L377), raw-string `"android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"` (L392), `ACTION_MANAGE_OVERLAY_PERMISSION` (L396–400), fallback `ACTION_SECURITY_SETTINGS` (L378 in audit numbering / the DEVICE_ADMIN fallback), `ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` + fallback `ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS`; Deck duplicates at pre-fix L558/L561.
- Repo grep pre-fix: `setPackage` appeared **nowhere** in `app/src/main/java`; the only explicit target was the DEVICE_ADMIN `ComponentName("com.android.settings", …)` pane. The finding held in full.

Cure:

- New top-level helper `pinnedSettingsIntent(action, data)` (`MainActivity.kt:83–86`) — every Settings intent is built through it and pinned with `setPackage("com.android.settings")` (`SYSTEM_SETTINGS_PACKAGE`, L73). Data-carrying panes (overlay, battery-request) keep their `package:` Uri.
- All 8 FIX-pane routes + both fallbacks + both Deck fix routes now go through the helper; a post-fix grep for `Intent(Settings.` / `Intent("android.settings…` returns only call sites *inside* `pinnedSettingsIntent(...)`.
- Discriminating test: `settingsIntentsArePinnedToTheSystemSettingsApp` asserts `package == "com.android.settings"` and Uri preservation (pre-cure the package was `null`).

## Finding 2 — [MEDIUM] Intercept intent consumed only in memory (2 of 4 extras), process-death redelivery resurrects a stale Airlock, never revalidated

**Disposition: FIXED-IN-CODE (99ca645)**

Verified against the current tree first:

- Pre-fix `MainActivity.kt:101–103` ran `handleIncomingInterceptIntent` only from `LaunchedEffect(Unit)`; the handler (L128–146) removed only `EXTRA_INTERCEPTED_PACKAGE` and `EXTRA_INTERCEPT_PATH` (NAME and BADGE never removed), and only on the in-memory copy — the platform-retained launching intent is redelivered intact after process death.
- Pre-fix `MonolithViewModel.kt` `triggerAirlockIntercept` (L2315 ff.) set `_airlockState.isVisible = true` unconditionally — `activeSession` was read only to borrow its intention text; no session/tier/lease requirement existed. The finding held in full.

Cure (three layers):

1. **Unique firing id.** Both firing doors mint `EXTRA_INTERCEPT_ID` (UUID) per firing — `MonolithShieldService.kt:284` (shield monitor) and `MonolithAccessibilityService.kt:432` (`launchInterceptScreen`). Extra declared at `MonolithShieldService.kt:393`.
2. **Persisted consumption.** New `InterceptConsumptionStore` (`service/InterceptConsumptionStore.kt:31`): SharedPreferences-backed, entries pruned to the newest 64 within 48 h. `MainActivity.handleIncomingInterceptIntent` (L147–189) now removes **all five** extras, checks `isConsumed(id)`, and `markConsumed(id)` — a synchronous `commit()` — **before** any showing; an already-consumed redelivery is dropped, and a delivery whose consumption cannot be persisted is dropped too (fail closed; the live door re-fires if the user is actually in the blocked app). Id-less deliveries (pre-cure or foreign — MainActivity is exported) are fingerprinted from their four extras (`InterceptConsumptionStore.fingerprint`), so the same stale copy redelivered is the same fingerprint.
3. **Revalidation before showing.** New VM entry `triggerAirlockInterceptFromIntent` (`MonolithViewModel.kt:2363`) re-derives the showing from one-shot store reads (`repository.activeSessionSnapshot()`, new `repository.appsSnapshot()`) through the pure `airlockIntentRevalidated` (`MonolithViewModel.kt:173`): no active session → no Airlock on any door; on APP_TIER/SHIELD_APP the package must still hold a non-OPEN tier, must not be free under the Safe Break's peek pass, and must not hold a live peek lease. SNIPER/SITE/GUARD/UNKNOWN doors intercept packages that legitimately have no row or an OPEN row (browser, Settings), so for them the live session is the whole gate — their peek offer was already always off via `peekOfferFor`. The warm in-app entry (`triggerAirlockIntercept`, used by the Launcher) applies the same predicate against the landed session flow.
- Discriminating tests: persisted consumption recognised on a fresh disk re-read; fingerprint stability; prune/encode round-trip; the full revalidation matrix per door.

## Finding 3 — [LOW] ShieldService `onStartCommand` fail-open: any non-STOP intent treated as START, resets the protection witness

**Disposition: FIXED-IN-CODE (99ca645)**

Verified against the current tree first: pre-fix `MonolithShieldService.kt:36–77` checked only `action == ACTION_STOP_SHIELD`; every other delivery — unknown actions and the null intent of a START_STICKY restart — fell into the full start path, resetting `protecting=false / protectingSessionId=-1 / protectingMono=0L` (L48–50) and calling `startForeground` before any session was read. The finding held in full.

Cure:

- Pure dispatch `shieldDispatchFor(action, monitorRunning, hasLiveSession)` (`MonolithShieldService.kt:44`), consumed by the rewritten `onStartCommand` (L79):
  - `ACTION_STOP_SHIELD` → STOP (unchanged stop path), `START_NOT_STICKY`.
  - `ACTION_START_SHIELD` → START_FULL when no loop runs; KEEP_RUNNING when one does.
  - **null (sticky restart)** → the live session is re-derived first (`liveActiveSessionId()`: one-shot Room snapshot, fail-closed on read error). Live session + no loop → START_FULL (legitimate sticky restart preserved); live session + running loop → KEEP_RUNNING; **no live session → STOP_SELF**: `stopSelf()`, no foreground, no witness reset, `START_NOT_STICKY`.
  - **unknown action** → KEEP_RUNNING if a loop is live (ignored; loop, foreground and witness untouched), else STOP_SELF.
- KEEP_RUNNING never restarts the monitor loop and never resets the witness — the single exception is a *session change* (witness attests the old session id): it is reset so the running loop re-proves the new session on its next iteration. Foreground is re-asserted via `promoteToForeground()` because a `startForegroundService` delivery carries its own promote deadline; re-posting the standing notification moves no protection state.
- START_FULL keeps the original Slice D witness reset — it is now reachable only when no loop is running, so the witness it resets attests no live loop.
- Discriminating tests: the full dispatch matrix, including the typo'd-action and both null-intent session cases.

---

## Signed lens sections

### Verifier — evidence quality
Every finding was re-verified line-by-line against the seated tree at 0a00e95 before any edit: all three held with the audit's file+line evidence intact (no drift despite the merges since the audit baseline). Post-fix evidence is equally concrete: helper/dispatch/store symbols with line numbers above, a post-fix grep showing no unpinned Settings construction survives, and 10 new tests that each name the pre-cure behaviour they discriminate. What I did **not** verify: compilation and test execution — per lane law nothing was compiled locally; CI on the eventual PR is the verdict. Honest gap: no on-device check that every OEM Settings build accepts all pinned actions (see Brutal critic).

### Brutal critic — what's still weak
- **OEM risk on Finding 1:** pinning assumes the Settings app is `com.android.settings` on the user's phone. True for AOSP/Pixel and Samsung's settings fork keeps the same package, but a minority OEM build could route a pane (notably notification-listener settings) through a differently-packaged component; the FIX tap would then fail silently inside the existing `runCatching` — a dead FIX button is worse than a resolver in that one case. Accepted per the brief's explicit cure, but it wants a phone check on Sarthak's actual device before this is called proven.
- **Finding 2's consumption is marked before the async revalidation completes.** If the VM's snapshot read throws (DB locked), the firing is consumed *and* dropped — the user standing in the blocked app gets no Airlock from that firing. The shield/a11y doors re-fire on their next pass, so this self-heals, but the first post-death frame can land on the Deck instead of the Airlock.
- **`liveActiveSessionId()` uses `runBlocking` on the service's main thread** for null deliveries and keep-running checks. It is one Room snapshot and rare, but it is still a blocking DB read on the main thread; a wedged DB would stall `onStartCommand`.
- **Adjacent hole left alone (out of scope, flagged not fixed):** `onNewIntent` still only calls `setIntent(intent)` — an intercept delivered to the already-running activity is stored but never consumed by the `LaunchedEffect(Unit)` handler. That was Round 1's separate title ("onNewIntent drops intercept intents"), not one of #78's three findings, and fixing it belongs to that issue's cell to avoid double-ownership. WELLE should confirm it is tracked elsewhere.
- The legacy-fingerprint path means a *foreign* id-less intent with invented extras can burn its own fingerprint once; the session/tier revalidation behind it is the real gate, but the fingerprint itself proves nothing about origin.

### Technical execution specialist — fix soundness
- Finding 1: single construction point (`pinnedSettingsIntent`) — no route can drift back unpinned without bypassing the only helper; `Intent(action, data)` + `setPackage` preserves the exact previous action/Uri semantics.
- Finding 2: consumption-before-showing with `commit()` (not `apply()`) is the correct ordering — the disk record exists before the UI state moves, mirroring the seal-marker discipline elsewhere in this codebase. Marking is idempotent (re-mark filters the old entry), pruning bounds growth, and the fingerprint fallback covers exactly the id-less population that can still exist. Revalidation reads snapshots, not flows, so a cold-start flow lag cannot resurrect — or wrongly kill — a firing; the pure predicate is shared by the intent and warm entries, so the two doors cannot diverge.
- Finding 3: the dispatch is a pure function of (action, loop state, live-session fact) and the side-effecting body is a flat `when` over its four outcomes; STOP and START_FULL bodies are byte-identical to the pre-cure paths they preserve, moved not rewritten. The witness is reset in exactly two places now: START_FULL (no loop running) and KEEP_RUNNING-on-session-change (witness provably attesting a dead session). `monitorJob` is an instance field and Android services are per-process singletons, so the loop-state read is the service's own truth.
- No schema change, no manifest change, no UI code touched; fonts/palette/image laws not engaged.

### Consumer — user-facing effect, in plain words
- Tapping FIX for any permission (accessibility, usage access, overlay, battery, notifications, device admin) now always opens the phone's real Settings page — another app can no longer slip its own fake page in between. On a normal phone nothing looks or feels different.
- The block screen (Airlock) can no longer pop up again from an old, already-handled block after the app's process was killed and restored — and it will not appear at all if the focus session has already ended or the app was since allowed. In one rare edge, during a Safe Break a peek-app tap in the sealed launcher now does nothing instead of showing the Airlock (the app is free during the break anyway; before, the Airlock could still offer a peek there).
- The shield service no longer restarts its protection proof from zero when it gets a duplicate or stray start signal, and after a system restart of the service it only resumes guarding if a focus session is genuinely still running — otherwise it quietly stops instead of showing a "protecting you" notification with nothing behind it.
- No screens redesigned, no features removed, no new visible UI.

— Issue-78 cell (WELLE cluster), agent/issue-78 @ 99ca645
