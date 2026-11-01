# Critic Charges — Issue #16, Pastille's cell (pill lane: finish verification, then merge)

Critic seat, scope unrestricted. Every charge carries evidence (file:line) and what would settle it.
No compiling, no test runs, no gradle were used — this is a paper audit of the lane as it stands.

Lane root abbreviated below as `PILL/` =
`~/workspace/clone-inspect/builds/pill/Focus-app/app/src/main/java/com/example/`
Tests abbreviated as `TEST/` = `.../app/src/test/java/com/example/`
Comparison trees: `B9/` = `~/workspace/clone-inspect/builds/merge-batch9/Focus-app/...`,
`T3/` = `~/workspace/clone-inspect/builds/truth-3-times/Focus-app/...` (Frist's Car 3 times lane).

The 18 tests in scope = 15 in `PillDerivationTest` + 3 in `PillLeaseRepositoryTest`.
Useful established fact before the charges: the logged run in `~/workspace/clone-inspect/builds/pill/test.log`
shows `:app:compileDebugUnitTestKotlin` and `:app:compileDebugKotlin` completing, then the Gradle
daemon disappearing inside `:app:testDebugUnitTest`. So the 18 tests **compile** against the lane;
what they have never had is an execution verdict.

---

## A. The verification plan itself

### C1 — BLOCKER — `test.sh` (and `build.sh`) can never run again as shipped: their queue script no longer exists
- **Claim:** Any re-run of the pill verification via the lane's own scripts exits 3 before Gradle is ever invoked. The "suite died 3× to the environment" story is now unreproducible-by-script: the script itself is dead.
- **Evidence:** `~/workspace/clone-inspect/builds/pill/test.sh:6` sets `Q=~/workspace/clone-inspect/design-refs/swarm/tools/queue.sh` and `test.sh:10` runs `bash "$Q" acquire pill … || { echo "ACQUIRE FAILED"; exit 3; }`. `ls` on that path: *No such file or directory* — the whole `design-refs/swarm/` tree was deleted on Oct 30 when collaboration moved to GitHub (the lane's `build.sh` has the identical dependency). Scratch still references it; nothing replaced it.
- **Severity:** Blocker for the "finish verification" half of this issue.
- **Settles it:** Point `Q` at a living queue script or delete the acquire/release steps from `test.sh`/`build.sh`, then one scripted run that reaches Gradle at all.

### C2 — HIGH — The only verdict route is the full suite; the pill's 18 tests are hostage to ~31 unrelated test files and to the RAM wall that already killed the daemon
- **Claim:** `test.sh` runs `gradle :app:testDebugUnitTest` with no `--tests` filter. A failure, hang, or daemon death anywhere in the other test files (Drawer, Routines, SliceE, WatchStore, ScreenshotHarness…) denies the pill its verdict, and the full-suite run is exactly the memory profile that died: the daemon-disappeared failure in `test.log` happened *during* `testDebugUnitTest` after all compile tasks passed.
- **Evidence:** `test.sh:16-17` (unfiltered `testDebugUnitTest`); `~/workspace/clone-inspect/builds/pill/test.log` tail — "The message received from the daemon indicates that the daemon has disappeared" immediately after `> Task :app:testDebugUnitTest` starts.
- **Severity:** High — verification design, not code.
- **Settles it:** A filtered run — `gradle :app:testDebugUnitTest --tests "com.example.PillDerivationTest" --tests "com.example.PillLeaseRepositoryTest"` — producing `TEST-*.xml` results for exactly the 18. The full suite is a separate, later question.

### C3 — HIGH — 0 of the 18 tests touch PillController, PillTheme, Route B, `start2MinutePeek`, or EditableTimesProvider — and the provider cannot be faked, because it is hard-wired
- **Claim:** The suite covers exactly two things: the pure derivation (15) and three repository lease writes (3). Everything this issue's merge flags actually risk — the overlay controller, the notification mouth, the grant→launch composition, the Frist provider contract — is untested in this lane. Worse, it *cannot* be tested at the seam: the repository constructs its provider privately, so no test can substitute a Frist-shaped provider (non-default minutes, 0, out-of-range row) without editing production code.
- **Evidence:** `PILL/data/MonolithRepository.kt:95` — `private val editableTimes = EditableTimesProvider(dao)` (no constructor parameter, no setter). Test inventory: `TEST/PillDerivationTest.kt` (derive only), `TEST/PillLeaseRepositoryTest.kt` (grant/zero/hand-back only). Proof the contract *is* testable: Frist's own lane has `T3/.../test/.../EditableTimesTruthTest.kt` with `ft3_leaseReadsTheSetting_andZeroGrantsNothing`, `ft4_midFlightImmovability`, `ft6_setterBoundsClamp` — the pill lane has no counterpart to any of them.
- **Severity:** High.
- **Settles it:** Make the provider injectable (constructor parameter with the current default) and port/add the contract tests (0-grants-nothing, clamp bounds, mid-flight immovability) against the pill lane before the merge, not after.

---

## B. Merge flag (a) — Suki's pre-grant guard + Pastille's post-grant launch at `start2MinutePeek`

### C4 — BLOCKER (for the flag as scoped) — The pill lane's `start2MinutePeek` has no guard at all, and the guard's inputs do not exist in this lane
- **Claim:** Flag (a) says the guard and the launch "must compose" at `start2MinutePeek`. In the pill lane there is nothing to compose with: its `start2MinutePeek` grants unconditionally. And Suki's guard cannot be dropped in — it reads `state.interceptPath` and the `InterceptPath` enum, neither of which exists anywhere in the pill lane's main source (grep for `interceptPath` over the whole lane: zero hits). The port surface is at least four pieces: the `InterceptPath` enum, the `AirlockInterceptState.interceptPath` field, the `triggerAirlockIntercept` path parameter, and the firing service's intent plumbing that carries the path (the pill lane's shield intercept intent carries only package/name/badge — `PILL/service/MonolithShieldService.kt:296-303`).
- **Evidence:** Pill lane grant with no check: `PILL/ui/MonolithViewModel.kt:2155-2192` (compare B9's guarded version at `B9/ui/MonolithViewModel.kt:2384-2416`). Guard signature: `B9/ui/MonolithViewModel.kt:141-151` (`peekOfferFor(path: InterceptPath, entity, cooldownLeftSeconds, pillActive)`). Pill lane state has no path field: `PILL/ui/MonolithViewModel.kt:118-132` vs `B9/ui/MonolithViewModel.kt:153-166`.
- **Severity:** Blocker — the flag as written understates the merge work and invites a paste that either fails to compile or silently defaults.
- **Settles it:** An explicit port list in the merge plan naming all four pieces, with the pill lane's `triggerAirlockIntercept` (`PILL/ui/MonolithViewModel.kt:2120-2144`) gaining the path parameter from its real callers.

### C5 — HIGH — The default-path trap: if the guard is ported without the plumbing, every peek is silently refused forever
- **Claim:** `InterceptPath.UNKNOWN.canHonourPeekLease` is `false` (only `APP_TIER` and `SHIELD_APP` honour a lease). The pill lane has no plumbing to set any other value, so a merge that adds the guard call with the state field defaulted ships a build where the PEEK button is offered by the airlock and every grant silently returns. No pill-lane test calls `start2MinutePeek`, so nothing in the 18 would catch it.
- **Evidence:** `B9/service/MonolithAccessibilityService.kt:46-58` (enum + `canHonourPeekLease get() = this == APP_TIER || this == SHIELD_APP`); default at `B9/ui/MonolithViewModel.kt:166` (`InterceptPath.UNKNOWN`); guard's early return at `B9/ui/MonolithViewModel.kt:2393-2395`.
- **Severity:** High.
- **Settles it:** A device or Robolectric test that drives a real intercept → airlock → grant for each firing path and asserts a lease row exists afterwards.

### C6 — HIGH — Guard *placement* inside the merged function is load-bearing, and the pill body makes the wrong placement easy
- **Claim:** The pill lane's `start2MinutePeek` performs synchronous, user-visible side effects *before* the coroutine that grants: it hides the airlock and activates `FloatingPeekPillState`. If the merged guard is pasted after those lines (the natural paste spot, just above `viewModelScope.launch`), a refused grant leaves a phantom floating pill counting down a lease that was never written — and when that phantom expires, the VM ticker calls `end2MinutePeekEarly()` → `recordPeekEnded()`, which **arms a 20-minute cooldown for a peek that never happened**.
- **Evidence:** Synchronous mutations: `PILL/ui/MonolithViewModel.kt:2159-2166`. Ticker expiry path: `PILL/ui/MonolithViewModel.kt:852-863` (`nextSec <= 0 → end2MinutePeekEarly()`), `2194-2202` (`recordPeekEnded`). Cooldown anchor is unconditional: `PILL/data/MonolithRepository.kt:1197-1204`. In B9 the guard sits *before* every mutation (`B9/ui/MonolithViewModel.kt:2386-2399`) — that ordering is part of the guard, not a style choice.
- **Severity:** High.
- **Settles it:** The merged function keeps B9's order verbatim — guard first, mutations only after it passes — plus a test that a refused grant changes no state and writes no cooldown.

### C7 — HIGH — Blank package defaults to WhatsApp: the pill body grants, launches, and logs a peek for an app that was never intercepted
- **Claim:** If `interceptedPackageName` is blank, the pill lane invents `com.whatsapp`, writes it a lease, launches it (ST-08), and records an intercept log saying "Used 2m Peek" for it. Suki's guard exists precisely to kill this ("a blank package no longer defaults to an unrelated app"). Taking the pill body wholesale because ST-08 lives in it regresses UNA2-01.
- **Evidence:** `PILL/ui/MonolithViewModel.kt:2157` — `val pkg = state.interceptedPackageName.ifBlank { "com.whatsapp" }`; launch + log at `2176-2190`. B9's contrasting text: `B9/ui/MonolithViewModel.kt:2386-2397` (`val pkg = state.interceptedPackageName`, no fallback).
- **Severity:** High.
- **Settles it:** The merged function takes `pkg` from state with no `ifBlank` invention, and the guard's `entity != null` requirement (B9:149) makes a blank package ungrantable.

### C8 — MEDIUM — The door's offer formula and Suki's grant formula disagree on a null entity, so the merged app can offer what it will refuse
- **Claim:** The pill lane computes `canPeek` at intercept time with `appEntity == null` counting as *peekable*; `peekOfferFor` counts a null entity as *refusal*. After a merge that keeps both formulas, the airlock can light the PEEK door for an unknown package and the grant will silently no-op — the exact stale-offer failure UNA2-01's second gate was built to prevent, reintroduced one layer up.
- **Evidence:** `PILL/ui/MonolithViewModel.kt:2128-2132` — `(appEntity == null || appEntity.resolvedTier == AppBlockTier.PEEK_2M) && cooldownLeft == 0 && !…isActive`; `B9/ui/MonolithViewModel.kt:147-151` — `entity != null && entity.resolvedTier == …`.
- **Severity:** Medium.
- **Settles it:** One formula: `triggerAirlockIntercept` computes `canUse2MinutePeek` by calling the same `peekOfferFor` the grant re-checks with.

### C9 — HIGH — A refused/zero grant is invisible to the caller: with Frist's `peekMinutes = 0` the pill flow still launches the app and logs "Used 2m Peek"
- **Claim:** `startPeekLease` returns `Unit` and silently no-ops when the provider says 0. `start2MinutePeek` cannot tell a grant from a refusal, so with peeks off at the source it still: hides the airlock, starts the floating pill, **launches the peeked app with no lease** (the shield then immediately re-intercepts it — the user is bounced out of the app they were just thrown into), and writes an intercept log claiming the peek was used.
- **Evidence:** Silent no-op: `PILL/data/MonolithRepository.kt:1180-1183` (`if (durationMillis <= 0L) return`). Unconditional launch + log: `PILL/ui/MonolithViewModel.kt:2167-2191` (ST-08 launch inside `runCatching`, `recordIntercept(…, outcome = "Used 2m Peek")` after it, regardless of outcome — the launch's own failure is also swallowed and still logged as used).
- **Severity:** High — this is the composition seam failing in its *specified* post-merge configuration (peekMinutes is user-editable 0–30).
- **Settles it:** `startPeekLease` returns whether it granted (or the VM reads the provider/cooldown state through the guard first); launch and intercept-log happen only on a proven grant.

---

## C. Merge flag (b) — Frist's EditableTimesProvider into Pastille's seam

### C10 — BLOCKER (for the flag as stated) — The break half of the seam is dead in the pill lane: `breakDurationSeconds()` has zero callers, and the break's length is hardcoded in three other places
- **Claim:** Swapping Frist's provider into the pill lane changes the peek's length and **nothing about breaks**, because nothing in the pill lane reads `breakDurationSeconds()`. The break's 600 s is baked at the block-finish write in `updateSessionProgress` and twice more in the span derivations. A merge executed exactly as flag (b) describes ships editable breakMinutes that silently do nothing.
- **Evidence:** Provider definition with no caller (grep over the lane's main source returns only the definition): `PILL/data/EditableTimes.kt:44`. Break stamp from the constant: `PILL/data/MonolithRepository.kt:1897` (`safeBreakSecondsRemaining = SAFE_BREAK_DURATION_SECONDS`, inside the progress machine, not session creation). Span hardcodes: `PILL/data/MonolithModels.kt:391` (`(totalStudyBlocks - 1) * 600`), `:395` (`cycle = effectiveTargetSeconds + 600`), `:398` (`600 - safeBreakSecondsRemaining`). Contrast T3, where the stamp is a real session field written at creation: `T3/data/MonolithRepository.kt:1371-1377` (`breakSeconds = editableTimes.breakDurationSeconds()`).
- **Severity:** Blocker for flag (b)'s "swaps unchanged" framing.
- **Settles it:** The merge plan lists the break wiring as its own work item: session `breakSeconds` stamp at creation, `:1897` and the three span sites reading the stamp — i.e. porting T3's consumer side, not just the provider file.

### C11 — HIGH — The provider file alone does not compile against the pill lane: it reads columns and a session field this tree doesn't have, on a DB version this tree isn't at
- **Claim:** Frist's provider reads `row.peekMinutes` / `row.breakMinutes` off `GuardSettingsEntity`. The pill lane's entity has neither field, the pill lane is DB v11 while Frist's columns arrive with the 11→12 migration, and the pill lane's `FocusSessionEntity` has no `breakSeconds` field at all (the only occurrence of the word in the lane is the seam's own comment promising it). "Swaps into the seam unchanged" is true of the provider's *signatures* (same class, same constructor, same two suspend functions — T3's header is honest about that) and false of the swap as a whole.
- **Evidence:** Frist provider: `T3/data/EditableTimes.kt:41-56`. Pill entity without the fields: `PILL/data/MonolithModels.kt:705-732`. DB versions: pill `PILL/data/MonolithDatabase.kt:25` (`version = 11`) vs T3 (`version = 12`, migration at `T3/data/MonolithDatabase.kt:197-198`). `breakSeconds` in the pill lane: only `PILL/data/EditableTimes.kt:12` (a comment).
- **Severity:** High.
- **Settles it:** The merge checklist names the full Car 3 payload — entity fields, MIGRATION_11_12, DB version bump, session stamp field — as prerequisites of the provider swap, verified by a compile of the merged tree before any test verdict is claimed.

### C12 — MEDIUM-HIGH — Merge-hunk hazard in the other direction: Frist's own `startPeekLease` dropped the one-live-lease zeroing
- **Claim:** The pill lane's grant zeroes every other row's lease; T3's grant does not call `clearOtherPeekLeases` at all. If the merger takes T3 repository hunks wholesale (they sit adjacent to the provider wiring), the pill lane's one-live-lease enforcement silently regresses while its derivation and tests still assume it.
- **Evidence:** Pill: `PILL/data/MonolithRepository.kt:1185` (`dao.clearOtherPeekLeases(packageName)` before the update). T3: `T3/data/MonolithRepository.kt:1200-1210` — the whole function is provider-read, `<= 0` return, single update; no clear call.
- **Severity:** Medium-high.
- **Settles it:** Diff discipline at merge: `MonolithRepository.kt` is merged hunk-by-hunk with `clearOtherPeekLeases` explicitly accounted for, and `grantZeroesEveryOtherRowsLease` (TEST/PillLeaseRepositoryTest.kt) stays in the verdict set.

---

## D. The leftover second clock — FloatingPeekPillState

### C13 — HIGH — The "dead contract's" VM pill is still alive, still counts decrements, and will truncate any Frist lease longer than 120 s
- **Claim:** `PillFace.kt`'s header says the counted-decrement state was the disease the derivation replaced. The pill lane never removed it: `start2MinutePeek` still seeds it from the hardcoded constant (bypassing the EditableTimes seam entirely), a 1 Hz VM ticker still decrements it, and at zero the ticker calls `end2MinutePeekEarly()` → `recordPeekEnded()`, **zeroing the real lease in storage**. Consequences by configuration: at today's 120 s it roughly shadows the lease; with Frist's peekMinutes set to anything above 2 (up to 30), the VM ticker kills the live lease at 120 s while the pill and shield — reading storage — believed it had minutes left. The editable-peek feature is capped at 2 minutes by a ghost.
- **Evidence:** Header's claim: `PILL/service/PillFace.kt:13-17`. Seed from the constant, not the provider: `PILL/ui/MonolithViewModel.kt:2161-2166` (`secondsRemaining = MonolithRepository.PEEK_DURATION_SECONDS`). Ticker: `PILL/ui/MonolithViewModel.kt:851-863`. Kill path: `2194-2202` → `PILL/data/MonolithRepository.kt:1197-1204` (zeroes `activePeekUntilElapsedRealtime`).
- **Severity:** High — it silently breaks the merged product's headline setting even if both merge flags are executed perfectly.
- **Settles it:** Either the VM state is deleted (the storage derivation is the only clock, as PillFace's header already claims) or the ticker derives from the stored lease end like everything else; plus a test with a >120 s provider proving the lease survives past 120 s.

### C14 — HIGH — A storage hand-back never reaches the VM state, so the cooldown gets re-anchored late and the door stays shut after the peek is already over
- **Claim:** The pill's ✕ and Route B's END PEEK both write storage only. The VM's `FloatingPeekPillState` keeps counting, `triggerAirlockIntercept` keeps reporting `canPeek = false` because of it, and when the ghost count finally expires it calls `recordPeekEnded` *again* — moving `lastPeekEndElapsedRealtime` from the true hand-back instant to the ghost's expiry, extending the 20-minute cooldown by up to the seconds remaining on the ghost. The cooldown function takes the later anchor, so the extension is real, not cosmetic.
- **Evidence:** Storage-only verbs: `PILL/service/PillController.kt:289-291`, `PILL/service/MonolithShieldService.kt:44-55`. VM never re-reads the lease into its state (no other writer of `_floatingPeekPill` besides grant/ticker/end — `PILL/ui/MonolithViewModel.kt:192-193, 852-863, 2161, 2195-2198`). Second anchor: `2199-2201`. Cooldown takes the max: `PILL/data/MonolithRepository.kt:1157` (`max(app.lastPeekEndElapsedRealtime, app.activePeekUntilElapsedRealtime)`).
- **Severity:** High.
- **Settles it:** Same fix as C13 (one clock, in storage); a test that hands back via `recordPeekEnded` mid-lease and asserts the cooldown anchor never moves afterwards.

---

## E. Route B — the shield notification

### C15 — HIGH — Route B never calls the derivation: "one derivation, two mouths" is false as implemented, and the mouths can tell different times
- **Claim:** `PillFace.kt` promises both surfaces print what the one function decides "so the glass and the shade can never tell two times". The shield service never calls `PillDerivation.derive` — it re-implements the priority inline ("Face priority mirrors PillDerivation") off *different inputs*: the row returned by the progress write rather than a stored-ends recomputation at the print instant. Concrete divergences: (i) Route A's session elapsed is recomputed from the stored (elapsed, lastTick) pair at its 1 s tick; Route B prints the progress machine's persisted `elapsedSeconds` at a 2 s grain — the two faces routinely differ by seconds. (ii) The timed face additionally sets a **system chronometer** counting to `startWallTimeMillis + effectiveTargetSeconds` — a third clock, computed from wall time, which assumes elapsed accrues wall-continuously from session start; after any shield gap (the very gaps this app elsewhere treats as provable events) the chronometer can reach zero while Route A still shows minutes remaining. (iii) Route B has no keyguard/telecom/own-package semantics at all — defensible for a notification, but it is a *different function*, and every future derivation fix must be made twice or the mouths drift.
- **Evidence:** The promise: `PILL/service/PillFace.kt:5-11`. The only `derive(` call in the lane's services is the controller's: `PILL/service/PillController.kt:122`. Route B's inline re-implementation: `PILL/service/MonolithShieldService.kt:160-233` (peek face at 176-196, break/stopwatch/timed text at 197-215). Chronometer: `MonolithShieldService.kt:222-232` and `317-324` (`setWhen(chronometerEndWallMillis)`, `setChronometerCountDown(true)`).
- **Severity:** High — this is the lane's own stated invariant, unmet.
- **Settles it:** Route B renders from a `PillFace` obtained from `PillDerivation.derive(...)` (chronometer end derived from that face), or the spec text in `PillFace.kt` is amended to admit two implementations — and a test samples both mouths at the same instant over the same rows and asserts equal printed seconds.

### C16 — MEDIUM-HIGH — END PEEK's failure modes: dead-service delivery, unconditional cooldown re-anchor, and a stopSelf race
- **Claim:** (i) The action's PendingIntent is `getService` — if the shield process/service is dead when the user taps END PEEK on a lingering notification, background service-start restrictions can refuse delivery with no error surface anywhere; the lease just keeps running. (ii) `recordPeekEnded` re-anchors the cooldown **unconditionally** — tapping a stale END PEEK after the lease already expired naturally moves the cooldown anchor from the granted end to the tap instant, extending the cooldown by the staleness. (iii) When the intent wakes a dead service, `stopSelf()` is called from inside a coroutine launched on a scope the service owns; a process reclaim between `onStartCommand` returning and the write landing loses the hand-back silently.
- **Evidence:** Action construction: `PILL/service/MonolithShieldService.kt:325-345` (`PendingIntent.getService`, request code `peekPackage.hashCode()`). Handler: `MonolithShieldService.kt:36-58`. Unconditional anchor: `PILL/data/MonolithRepository.kt:1197-1204` (no check that a lease is live). The lease test file even documents the verb as shared — `TEST/PillLeaseRepositoryTest.kt` header, case 3 — without testing the stale-tap case.
- **Severity:** Medium-high.
- **Settles it:** `recordPeekEnded` (or its callers) no-ops when `activePeekUntilElapsedRealtime == 0`/already past; a device test tapping END PEEK with the service killed; the hashCode request-code collision risk noted or replaced with a stable request code.

### C17 — HIGH — If the shield's monitor job dies, the pill keeps printing a living clock for a dead session
- **Claim:** The controller's founding promise is "if the service dies, the pill dies with it — a frozen pill printing a dead session's time would be a warrant lie". But the pill ticker and the monitor loop are *sibling jobs* under a SupervisorJob: the ticker wraps every tick in `runCatching`, the monitor loop has no error handling at all. One exception in the monitor loop (a Room hiccup, a heartbeat throw) kills blocking and the progress machine while the service process, the foreground notification, and the pill all live on — and because the derivation recomputes elapsed from monotonic stamps, the pill's session clock keeps *advancing*, looking healthier than ever, over a session nobody is enforcing or accruing.
- **Evidence:** Supervisor scope: `PILL/service/MonolithShieldService.kt:31`. Monitor launch with no try/catch: `MonolithShieldService.kt:121-123` and the loop body through `146` (`updateSessionProgress`). Ticker's asymmetric resilience: `PILL/service/PillController.kt:90-99` (`runCatching { tick() }`). The promise: `PILL/service/PillController.kt:41-47`. Elapsed keeps accruing from stamps regardless of the progress machine: `PILL/service/PillFace.kt:158-166` (`elapsedAt`).
- **Severity:** High — for an app whose third standard is TELL-THE-TRUTH, a confident clock over dead enforcement is the worst failure shape available.
- **Settles it:** The pill's tick checks the monitor loop is alive (or the monitor loop gets the same per-iteration `runCatching` + a death counter the pill reads as ABSENT); a fault-injection test that throws inside the loop and asserts the pill goes absent.

---

## F. PillController / derivation details

### C18 — MEDIUM-HIGH — The foreground safe-side decays: one good read poisons every later failed read, and the pill can draw over Monolith itself
- **Claim:** The controller resolves an unreadable foreground "to the safe side" only at birth. `lastForegroundPkg` updates solely on a non-null read, so after any successful read of another app, a later failure mode — usage-access revoked mid-session, query returning null — leaves the pill deriving faces against the *stale* package forever, including drawing the SESSION/PEEK face over Monolith's own UI if he returns to the app while reads fail. §2.4 Q1 is called "a hard rule" in the derivation's own test. The derivation's null contract (null foreground ⇒ not absent) is likewise untested.
- **Evidence:** `PILL/service/PillController.kt:80-85` (initial own-package), `:121` (`queryForegroundPackage()?.let { lastForegroundPkg = it }` — null leaves the stale value), `:122-129` (stale value passed as proven). Derivation's fall-through on null: `PILL/service/PillFace.kt:90-95`. Q1's status: `TEST/PillDerivationTest.kt:81-84`.
- **Severity:** Medium-high.
- **Settles it:** A staleness bound (N consecutive failed reads ⇒ resolve to own-package/absent), and a derivation test for `foregroundPkg = null`.

### C19 — MEDIUM — `onHandBack` races the ticker: the ✕ can be dropped, or fire against a face the glass isn't showing
- **Claim:** `currentFace` is assigned on the ticker thread *before* the face is rendered on Main. A tap on the visible Peek ✕ in the window between a newer derivation and its render is judged against the newer face: if it is no longer a Peek, the hand-back is silently dropped even though the glass showed the ✕; if it is a *different* Peek, the hand-back lands on a lease the user wasn't looking at.
- **Evidence:** Assignment order: `PILL/service/PillController.kt:130-131` (`currentFace = face` then `withContext(Dispatchers.Main) { render(face) }`). The verb: `PillController.kt:289-291` (`currentFace as? PillFace.Peek ?: return`).
- **Severity:** Medium (1-second window, but the ✕ is the pill's one verb).
- **Settles it:** Capture the face at render time (the rendered view carries the package it displays) and have the click handler use that snapshot.

### C20 — LOW-MEDIUM — The theme seam promises a typeface swap that no API allows, and a palette swap doesn't repaint until the clock moves
- **Claim:** `PillTheme`'s header says a look "may swap the figures' typeface through the same holder". The typeface is a private cached var with no writer other than its own loader — a look lane cannot swap it. And because `render` returns early when the renderKey is unchanged, writing a new palette mid-session does not repaint the visible pill until the printed second changes (bounded at ~1 s on time faces, but the *mechanism* is "the clock saved us", not the seam working).
- **Evidence:** Promise: `PILL/service/PillTheme.kt:10-14`. No setter: `PillTheme.kt:50-62` (`private var figuresTypeface`, assigned only inside `figures()`). Early return: `PILL/service/PillController.kt:143-144`.
- **Severity:** Low-medium — the seven look lanes are the consumers who will hit this.
- **Settles it:** A public typeface writer on `PillTheme` + invalidating `lastRenderKey` when the palette instance changes; a sentence in the lane notes correcting the header until then.

### C21 — LOW-MEDIUM — The ticker swallows every failure forever, with no log, no counter, no surface
- **Claim:** "The failure of one tick is silence for one second" is right for a transient race and wrong as a permanent condition: a persistent failure (DAO throwing every tick, a mapping error after a migration) makes the pill absent for the whole session with zero evidence anywhere — indistinguishable from "no session". For a warrant-grade app, a silently dead truth surface is a finding, not a footnote.
- **Evidence:** `PILL/service/PillController.kt:90-99` — `runCatching { tick() }` with the result discarded; no logging anywhere in the file.
- **Severity:** Low-medium.
- **Settles it:** Count consecutive tick failures; log the first and every Nth; optionally surface via WatchStore like other shield facts.

### C22 — LOW — Sentinel asymmetry between the two stored-pair derivations
- **Claim:** The break derivation's law is explicit that a stored tick of 0 is a legitimate anchor, *not* an unstamped sentinel; the session's `elapsedAt` treats `lastTick <= 0` as unstamped and credits nothing. Two functions claiming to mirror one law disagree at the boundary. The practical window is tiny (a session whose last progress stamp lands in the first millisecond after boot), but the comments assert a unity the code doesn't have.
- **Evidence:** `PILL/data/WallAccrual.kt:45-50` ("No 'unstamped' sentinel… 0 is a legitimate anchor value") vs `PILL/service/PillFace.kt:158-162` (`if (lastTick <= 0L) return session.elapsedSeconds`), whose comment at `:155-157` claims it "mirrors breakRemainingAfter's law".
- **Severity:** Low.
- **Settles it:** Align the comments or the code; one boundary test with `lastTick = 0, now = 5_000` for both faces.

### C23 — LOW — Body-tap RETURN TO FOCUS can silently no-op, and nothing would ever know
- **Claim:** `onBodyTap` starts MainActivity from the service's application context wrapped in `runCatching` with the result discarded. If the platform refuses the background launch (the overlay-touch exemption is exactly the kind of thing that varies by API level and OEM), the tap does nothing, forever, with no fallback and no signal. Paper cannot settle this one — flagging it so it is settled on a device rather than discovered by the user.
- **Evidence:** `PILL/service/PillController.kt:298-308`.
- **Severity:** Low (cosmetic surface, by the lane's own framing).
- **Settles it:** One manual device pass on the target phone: arm a session, open another app, tap the pill body.

---

## G. The tests themselves

### C24 — MEDIUM — Circular oracle: the break face's multi-sample test asserts the derivation against the very function the derivation calls
- **Claim:** `breakRemainingEqualsTheHouseDerivationAtEverySample` computes its expected values by calling `breakRemainingAfter(600, t0, …)` — the identical function `PillDerivation.derive` delegates to for the Break face. Any bug inside `breakRemainingAfter` (wrong clamp, wrong divisor, wrong sign) passes this test perfectly, on both sides at once. The test proves the wiring (derive passes its arguments through), not the arithmetic — while its name and placement claim the arithmetic.
- **Evidence:** Test: `TEST/PillDerivationTest.kt:114-126`. Delegation: `PILL/service/PillFace.kt:100-108`. The function under the oracle: `PILL/data/WallAccrual.kt:40-51`.
- **Severity:** Medium.
- **Settles it:** Expected values as literals (600, 599, 541, 540, 1, 0, 0 for the sampled offsets) computed by hand from the spec, not by the house function.

### C25 — MEDIUM — The "two clocks disproven" test replicates the implementation's formula as its oracle; the unreachable-state test tests the belt while the load-bearing braces go untested
- **Claim:** `faceEqualsStoredEndsAtEverySampledInstant` computes expected remaining with the same ceiling expression the implementation uses — a shared misunderstanding of the spec (say, ceiling vs floor) passes. Meanwhile `twoLiveLeasesResolveToTheLaterEnd` carefully tests a storage state the design says a grant makes impossible, while the actually-load-bearing properties — grant atomicity (C-risks below), renderKey distinctness, the null-foreground contract — have no tests at all.
- **Evidence:** Mirrored formula: `TEST/PillDerivationTest.kt:219-237` (`((expectedMs + 999L) / 1000L)`) vs `PILL/service/PillFace.kt:120-125`. Unreachable-state test: `TEST/PillDerivationTest.kt:207-217` with its own comment admitting "Storage can never hold two after a grant". No test in either file references `renderKey`.
- **Severity:** Medium.
- **Settles it:** Literal expected seconds in the sampling test; a renderKey test asserting two faces that print differently never share a key (and the controller's gate is exercised at least once).

### C26 — HIGH — The one-live-lease invariant rests on a non-atomic two-query grant, and nothing serialises concurrent grants
- **Claim:** "Storage can never hold two live fuses" is enforced by two separate DAO calls with no `@Transaction` and no mutex: clear-others, then update-self. Concurrent grants interleave into two live leases — grant A clears, grant B clears, A writes, B writes. Concurrency is not exotic here: the pill lane's `start2MinutePeek` is unguarded and re-invokable (double-tap, or a re-fired airlock), each call launching its own coroutine; and the app runs *multiple* `MonolithRepository` instances over the same DB (the shield service builds its own for END PEEK). With two live leases the surfaces disagree about reality: the derivation names only the later-ending app, while the shield's blocked-set admits *both* rows.
- **Evidence:** Two-call grant: `PILL/data/MonolithRepository.kt:1185-1190`; the DAO pair as independent queries: `PILL/data/MonolithDao.kt:31-41` (no `@Transaction` anywhere on this path). Re-invokable grant: `PILL/ui/MonolithViewModel.kt:2155-2167`. Second repository instance: `PILL/service/MonolithShieldService.kt:44-46`. Shield admits any live-lease row: `MonolithShieldService.kt:283-293`. Derivation arbitrates by max: `PILL/service/PillFace.kt:114-126`.
- **Severity:** High.
- **Settles it:** `@Transaction` on a DAO-level grant operation (or a repository mutex around the pair), and a concurrency test firing overlapping grants asserting exactly one live row.

### C27 — MEDIUM — The lease tests' timing assertions run on Robolectric's shadow clock, which does not advance while they assert "now"
- **Claim:** `handBackZeroesTheLeaseAndAnchorsCooldownToNow` brackets the call with `beforeEnd`/`afterEnd` readings of `SystemClock.elapsedRealtime()` and asserts the stamp lands between them. Under Robolectric that clock is scheduler-controlled and does not advance during a `runBlocking` DAO call, so the bracket can collapse to a single frozen value — the test then proves the stamp equals whatever the frozen clock said, which is a much weaker claim than "anchored to the hand-back instant", and it cannot catch an implementation that stamps any *derived-from-the-same-clock* value. The grant test's slack tells a similar story: `remainingMs in 100_000..120_000` passes a provider returning a flat 100 s. Both tests also run against the production singleton database rather than an isolated one, mitigated only by per-test package prefixes.
- **Evidence:** `TEST/PillLeaseRepositoryTest.kt:42` (`MonolithDatabase.getInstance(context)`), `:67` (the 100–120 s window), `:122-141` (the bracket assertion; class header at `:26-28` admits "the sandbox database is shared by the whole class").
- **Severity:** Medium.
- **Settles it:** Drive the shadow clock explicitly (`ShadowSystemClock.setElapsedRealtimeMillis` / advance between grant and hand-back) so "now" is a chosen, distinct value; tighten the grant window or read the provider's stated length as the oracle.

### C28 — LOW — Telecom coverage tests 2 of the 5 packages the derivation claims to honour
- **Claim:** `TELECOM_FOREGROUND_PACKAGES` lists five packages; the absent-on-telecom test exercises two. A typo or omission in the other three (`com.samsung.android.incallui`, `com.samsung.android.dialer`, `com.android.dialer`) ships undetected — on the user's Samsung-class device population, the untested ones are the plausible ones.
- **Evidence:** Set: `PILL/service/PillFace.kt:66-72`. Test: `TEST/PillDerivationTest.kt:87-91` (incallui + google dialer only).
- **Severity:** Low.
- **Settles it:** Iterate the set itself in the test, so the test and the list can never drift apart.

---

## Charge count: 28 (C1–C28)

Severity tally: 3 blockers (C1 — verification script dead; C4 — flag (a) as scoped;
C10 — flag (b)'s break half), 12 high (C2, C3, C5, C6, C7, C9, C11, C13, C14, C15, C17, C26),
3 medium-high (C12, C16, C18), 5 medium (C8, C19, C24, C25, C27), 2 low-medium (C20, C21),
3 low (C22, C23, C28).

**The single worst charge: C13.** Even if both merge flags are executed exactly as written —
guard composed, provider swapped — the leftover `FloatingPeekPillState` ticker still seeds from
the hardcoded 120 s constant and, on expiry, zeroes the real lease in storage. The moment Frist's
editable peekMinutes is set above 2, every peek is silently truncated to 2 minutes by a state
machine `PillFace.kt`'s own header declares dead, and none of the 18 tests can see it because
none of them touch the ViewModel. C14 is the same ghost charging a second price (cooldown
re-anchoring after a storage hand-back).
