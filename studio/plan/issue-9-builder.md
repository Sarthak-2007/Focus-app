# IMPLEMENTATION DOSSIER — Issue #9 (Main-app pass, Park audit fixes)

*Repo: Sarthak-2007/Focus-app · Cell: Hof (lead) · Role: Builder*
*Base: local trunk `~/workspace/clone-inspect/Focus-app` @ `3ea05bd`
("Batch 9 tests: compose ParkDrawer call sites…")*
*Builder clone: `~/workspace/clone-inspect/builds/cells/issue-9-builder/`,
branch `agent/issue-9-builder`. Hof's clone was not touched.*
*Source audit: `studio/plan/AUDIT-park.md` (read from the wt-ci copy —
the trunk clone at 3ea05bd does not carry `studio/`; charges P-1, P-6,
P-7 and merge list §4 items 1, 5, 9 are the ones in scope here).*
*Status of everything below: DECLARED — paper + one isolated change.
NOBODY compiled anything, and nobody will until Ren says so.*

---

## 0 · Scope — exactly three items, and what is NOT in this dossier

| # | Issue item | Audit charge | Verdict in this dossier |
|---|---|---|---|
| 1 | PARK gains a **CHECK** verb (never re-proves today) | P-1, §4.1 | Design-complete sketch, §2 below. Not coded — it touches 3 files + a new test and deserves Hof's review first. |
| 2 | Cold-open Guard line prints **`GUARD — NOT CHECKED`**, never blank | P-6, §4.5 | **IMPLEMENTED in the builder clone, DECLARED-UNCOMPILED** (§3). |
| 3 | **`FIRED AS SET` comes off the spec plate** (W-11) unless earned | P-7, §4.9 (clause only) | **Already satisfied on trunk — no code change** (§4). Verified, with the regression test named. |

Explicitly **out of scope** (do not let this dossier be read as doing them):

- **Live-Fire Strip** (AUDIT-park §5, issue #21, parked): CHECK in item 1
  is a *grant re-measurement* only. It does **not** call any
  `provePipelineNow()`, post a sacrificial decoy, or rehearse the
  notification listener. RUNNING stays unwitnessed after this issue —
  that is #21's whole job, not a gap in this one.
- **W-08 dynamic PARK plate** (batch-10, Ren's): the plate stays a
  static `private val` list. No state-driven plate text is proposed.
- The rest of AUDIT-park §4 (warrant line on the board §4.2, PARK
  headline `GUARD {n}/4` §4.3, age column + STALE half-ink §4.4, FIX
  staircase/escort §4.6, tier composition §4.7, LAST PROVED colophon
  §4.8, full plate cut to 4 lines §4.9-beyond-the-clause, sound/theme
  moves §4.10, padding/spacing §4.11, `doorLocked` gate §4.12) —
  none of it is in the issue-#9 brief as handed to this cell. Flagged
  for Hof: if any of those were meant to ride #9, they are not
  designed here.

---

## 1 · Current proof-state data flow (who computes what, where staleness lives)

### 1.1 The one store

`app/src/main/java/com/example/service/WatchStore.kt` — object
`WatchStore`, in-memory only ("Persistence: NONE this wave" is in its
own header; nothing touches Room, no DB version implication).

- Published face: `snapshots: StateFlow<WatchSnapshot>` where
  `WatchSnapshot(items: Map<WatchItemId, WatchMeasurement>,
  engineHeartbeatMono/Wall, engineProtecting, lastProvedWallMillis)`.
- Each `WatchMeasurement(item, perm|interceptor, measuredMono,
  measuredWall, sweepGen)` carries both clocks.
- **Staleness is derived at render, never stored:**
  `WatchStore.permPrintAt(snapshot, item, nowMono): PermPrint`
  returns `STALE` iff `perm == GRANTED && nowMono - measuredMono >
  STALE_AFTER_MS` (`const val STALE_AFTER_MS = 45_000L`). `MISSING`
  never stales; absent item / null perm → `UNMEASURED`.

### 1.2 Writers (the only ways a measurement is born)

1. **Lifecycle hooks** → `stampPermission(item, granted, gen = 0)`:
   `onAccessibilityBound` / `onNotificationListenerBound`
   (service connect/unbind), `onDeviceAdminChanged` (receiver).
2. **Shield loop** → `onShieldIteration(protecting)`:
   `MonolithShieldService` monitor loop calls it once per iteration
   (`delay(2000L)` cadence, only while a session's shield runs). It
   re-stamps ACCESSIBILITY + NOTIFICATION_LISTENER from
   `instance != null` and publishes the engine heartbeat.
3. **The grant-check sweep** → `WatchStore.runSweep(query)`:
   `++sweepGen`, sets `pendingSweepStartWall`, launches all six
   `WatchItemId.PERMISSIONS` queries in parallel on
   `Dispatchers.IO`; each landing calls
   `stampPermission(item, granted, gen)`.
   `publish()` (synchronized) re-derives the three interceptor rows
   on **every** publication and advances `lastProvedWallMillis` only
   when all six permission measurements carry `measuredWall >=
   pendingSweepStartWall` (a complete set from one sweep's era) —
   then clears `pendingSweepStartWall`.

The per-item query itself lives in the repository:
`MonolithRepository.watchGrantQuery(item)` (MonolithRepository.kt
~L645) — ACCESSIBILITY → `MonolithAccessibilityService.instance !=
null`; USAGE_ACCESS → `usageAccess() == UsageAccess.ALLOWED`;
DEVICE_ADMIN → `isDeviceAdminActive()`; NOTIFICATION_LISTENER →
`MonolithNotificationListener.instance != null`; OVERLAY →
`hasOverlayPermission()` (`Settings.canDrawOverlays`); BATTERY →
`isBatteryOptimizationIgnored()` (`PowerManager`).

`MonolithViewModel.runWatchSweep()` (MonolithViewModel.kt ~L591) is
the single UI-side entry: `WatchStore.runSweep { repository
.watchGrantQuery(it) }`.

### 1.3 Sweep triggers today — the P-1 hole, precisely

`runWatchSweep()` has exactly three triggers, all in
`MainActivity.kt` / ViewModel docs:

- **Deck entry** — `LaunchedEffect(currentDestination)` when
  destination becomes DECK: `WatchStore.noteResidencyStart()` +
  `viewModel.runWatchSweep()` (+ `onDeckOpenRetry()`).
- **`ON_RESUME`** — the `LifecycleEventObserver` in
  `MonolithRootApp`: `runWatchSweep()`, `parkReturnTick++`,
  `onAppResumed()`, `refreshDeckWarrant()`. (A FIX trip returns
  through this — "FIX return" is not a separate trigger, it *is*
  ON_RESUME.)
- Nothing else. In particular **PARK open is not a trigger**:
  `LaunchedEffect(parkOpen)` in MainActivity calls only
  `viewModel.refreshSystemGuard()` (MonolithViewModel.kt ~L2449),
  which fills `_systemGuardStatus` via
  `repository.getSystemGuardStatus()` (clock/HMAC/boot facts). Its
  own comment in MainActivity admits it: *"Its result renders
  NOWHERE — the board renders the Watch Store."* So the surface
  whose whole job is the machine's state opens, warms an unrelated
  diagnostic, and lets its own rows rot GRANTED → STALE at 45 s in
  front of the reader — PARK's internal 5 s `nowMono` beat
  (ParkDrawer.kt, `LaunchedEffect(visible)` + `delay(5_000L)`)
  re-derives the *print* faithfully and offers no verb to change it.

### 1.4 Who computes the Guard line

- Pure derivation: `guardLineFor(snapshot, nowMono)` in
  `app/src/main/java/com/example/ui/components/GuardLine.kt` over
  `WatchItemId.COUNTED` (ACCESSIBILITY, USAGE_ACCESS, DEVICE_ADMIN,
  NOTIFICATION_LISTENER). WHOLE → `GUARD 4/4`; LACK →
  `GUARD {n}/4 — {first lack in board order}` (+ ` STALE` suffix);
  UNMEASURED (any counted item unmeasured, or snapshot null) →
  **previously `null`** (see §3 for the change).
- Flow: `MonolithViewModel.guardLinePrint` (~L663) =
  `combine(WatchStore.snapshots, _sealFreeze, activeSession)` —
  sealed + freeze present → the freeze's captured line; otherwise
  `guardLineFor(snap, SystemClock.elapsedRealtime())`,
  `stateIn(Eagerly, null)`. Note the combine re-runs only on a
  flow emission, not on a clock tick: the header's stale transition
  rides the next store emission (pre-existing behaviour; §5 risk R-4).
- Render: `MonolithPullDownHeader(guardLine, isSealed, onGuardLineTap)`
  in `MonolithCustomComponents.kt` (~L252) prints the `Text` **only
  `if (guardLine != null)`** — a null print is literally an empty
  right side of the band. That conditional is why returning a
  non-null NOT CHECKED print (§3) needs no header change.
- Seal freeze: `SealFreeze.guardLine` is captured by two
  `guardLineFor(...)` call sites in MonolithViewModel init
  (session-birth collector ~L840, SealedRunning re-capture ~L881).

### 1.5 The warrant (context for CHECK — what CHECK must not touch)

`MonolithRepository.standingWarrant()` (~L626) gathers live facts
(a11y instance, `WatchStore.stampedThisResidency()`, `usageAccess()`,
shield protecting) → `WatchStore.deriveWarrant(...)`.
`stampedThisResidency` compares `MonolithAccessibilityService
.lastRootMono >= residencyStartMono`, and `residencyStartMono` is set
**only** by `noteResidencyStart()` (Deck entry). Consequence for
CHECK design: a CHECK that called `noteResidencyStart()` would
instantly un-prove LIVE until the a11y pipeline stamps a fresh root
event. CHECK therefore re-runs the *permission sweep only* (§2).

---

## 2 · Item 1 — PARK gains a CHECK verb (P-1 cure, AUDIT-park §4.1)

§4.1 is one line — *"Re-sweep on PARK open + CHECK key (P-1) — S"* —
and every mechanism it needs already exists. No new machinery, no
new query logic, no DB change.

### 2.1 Files / functions to change

| File | Change |
|---|---|
| `ui/components/ParkDrawer.kt` — `ParkDrawer(...)` | New parameter `onCheckClick: () -> Unit = {}` (defaulted — see R-3). New CHECK key composable block inside the sheet `Column`, directly after the board block and before the FIX `AnimatedVisibility` block. Local pressed state mirroring the FIX dip. |
| `MainActivity.kt` — `MonolithRootApp` | (a) `LaunchedEffect(parkOpen)`: add `viewModel.runWatchSweep()` beside the existing `refreshSystemGuard()` (keep both — different facts). (b) `ParkDrawer(...)` call site: pass `onCheckClick = { viewModel.runWatchSweep() }`. |
| `ui/MonolithViewModel.kt` | **No change.** `runWatchSweep()` is already the correct entry point; reusing it is the design. |
| `service/WatchStore.kt` | **No change for v1.** `runSweep` / `stampPermission` / `publish` already do the whole job, including LAST PROVED completion. |
| Tests (new/changed) | §6 test list, T-1…T-5. |

### 2.2 The CHECK key — concrete sketch

Placement and grammar follow the drawer's own laws (§8.10 material
law as documented in ParkDrawer's header: everything print except
the FIX key; the FIX key is "the board's only object"). CHECK is a
second object — that is a deliberate, audit-ordered exception of
exactly the same kind Truth Car 3 already seated for the SOCIAL
SITES sled; Hof should ratify the exception explicitly in the merge
note, not let it slip in silently.

```kotlin
// ParkDrawer.kt — new param (defaulted, after onFixClick):
onCheckClick: () -> Unit = {},

// Inside the sheet Column, after the board, before the FIX block:
var checkPressed by remember { mutableStateOf(false) }
LaunchedEffect(visible) { if (!visible) checkPressed = false }

if (!isSealed) {
    Spacer(modifier = Modifier.height(14.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .offset(y = if (checkPressed) 1.dp else 0.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(inkControlFill())
            .then(
                if (interactive) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            checkPressed = true
                            soundEngine?.playSeatKnock(
                                durationMs = 10,
                                amplitude01 = if (isNight) 0.06f else 0.1f,
                                pitchHz = 300f
                            )
                            onCheckClick()
                            tryAwaitRelease()
                            checkPressed = false
                        })
                    }
                } else Modifier
            )
            .testTag("park_check_key"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "CHECK",
            style = PrimaryButtonTextStyle,
            color = onInkControlFill()
        )
    }
}
```

Decisions baked into the sketch, with reasons:

- **Always present while unsealed** — not conditional on a lack or
  on staleness. A verb that appears only when the board already
  knows it's stale cannot cure the *unmeasured* cold board, and
  P-1's charge is that the surface offers no verb at all. (Contrast
  FIX, which is present only while a lack exists — that law is
  FIX's, not a house law.)
- **Absent while sealed** (`!isSealed`, same gate as FIX): under the
  seal PARK is print-only per the audit's acquittals ("sealed
  read-only degradation" stands). `interactive` already composes
  `visible && settled && !isSealed`, so mid-rise and sealed taps are
  both dead.
- **Label is the bare verb `CHECK`** — the board's grammar names
  actions plainly (`FIX — {NAME}` names its target because there is
  one; CHECK's target is the whole board). If Hof prefers the FIX
  symmetry, `CHECK — GUARD` is the fallback wording; do not invent
  a third form.
- **No `noteResidencyStart()`, no `refreshDeckWarrant()` call of its
  own** — the sweep's landings re-emit `snapshots`, and the VM's
  existing collector ("every emission re-reads the Deck's standing
  warrant") refreshes `deckWarrant` from them. One trigger, all
  surfaces follow.
- **Sound** reuses FIX's floor seat-knock, nothing new. There is no
  "success" sound: as with FIX's return, the *print changing* is the
  verdict — a row flipping STALE → GRANTED (or LAST PROVED advancing)
  is the receipt. Do not add a firm click; CHECK repaired nothing,
  it measured.

### 2.3 What CHECK re-derives, and where results persist

On tap (and on PARK open, via §2.1a):

1. `WatchStore.runSweep` dispatches all six
   `WatchItemId.PERMISSIONS` queries in parallel
   (`MonolithRepository.watchGrantQuery`, §1.2) — fresh platform
   reads, not cached booleans.
2. Each landing re-stamps that item's `WatchMeasurement`
   (mono + wall + this sweep's gen) via `publish()`, which also
   re-derives all three interceptor rows from (setting, parents,
   Safe Break suspension) — so SHORTS + REELS / SOCIAL SITES /
   NOTIFICATIONS prints refresh from the same act.
3. When the sixth landing of the era lands, `lastProvedWallMillis`
   advances — PARK's LAST PROVED telltale moves, the header Guard
   line recounts, both from the one snapshot. Partial landings print
   as they arrive; nothing waits for the set except LAST PROVED
   (the complete-set rule is acquitted by all six audit seats —
   do not loosen it for CHECK feedback).

**Persistence: in-memory only, deliberately.** Results live exactly
where every sweep result has always lived — `WatchStore._snapshots`.
No Room write, no migration, no new file. Process death returns the
board to unmeasured and the header to `GUARD — NOT CHECKED` (§3),
which is the honest cold state; the Readiness Log persistence is a
separate, already-filed programme (WatchStore header, Slice E) and
#9 must not smuggle it in.

**In-flight feedback (v1: none, on purpose).** `runSweep` is
fire-and-forget; there is no `sweepInFlight` flow today. Six local
platform reads land in tens of ms — the board visibly re-prints.
If Hof wants a pressed-until-proved state, the v2 shape is: count
landings for `gen == sweepGen` inside `stampPermission`, expose
`val sweepInFlight: StateFlow<Boolean>`, clear it on the sixth
landing or on `publish()`'s complete-set branch. Filed as an option
with its price (~25 lines + a race test), **not** built into the
sketch — v1 matches the audit's S pricing.

---

## 3 · Item 2 — `GUARD — NOT CHECKED` on cold open (P-6) — IMPLEMENTED, DECLARED-UNCOMPILED

Implemented in the builder clone (branch `agent/issue-9-builder`),
2 files, +19/−10. Not compiled, per standing orders.

### 3.1 The change

`ui/components/GuardLine.kt`:

- `guardLineFor()` no longer returns `null` for the unmeasured
  cases. Both early returns now return a new top-level constant:

```kotlin
val GUARD_NOT_CHECKED = GuardLinePrint("GUARD — NOT CHECKED")
```

  — for `snapshot == null` and for *any counted item UNMEASURED*.
  WHOLE and LACK branches are byte-for-byte unchanged; a measured
  set can never print NOT CHECKED, and a stale set still prints the
  LACK form with the ` STALE` suffix (stale is measured — it is not
  "not checked").
- The KDoc's UNMEASURED bullet is rewritten: the old text ("the line
  prints NOTHING…") now states the P-6 cure and keeps the original
  reasoning (blank read as fine; a fabricated `0/4` is the claim this
  state exists to prevent).

`app/src/test/java/com/example/ChromeWaveUnitTest.kt`:

- `guardLine_unmeasuredPrintsNothing` → `guardLine_unmeasuredPrintsNotChecked`:
  asserts the exact string `GUARD — NOT CHECKED` for the partial
  snapshot, for `null`, and for `WatchSnapshot.EMPTY`. The now-unused
  `assertNull` import is removed. No other test in the file changes.

### 3.2 Why this is the whole change (files deliberately NOT touched)

- `MonolithCustomComponents.kt` (`MonolithPullDownHeader`): prints
  `guardLine.text` whenever the print is non-null. A non-null NOT
  CHECKED print renders with zero header edits — same font, same
  testTag `header_guard_line`, same tap-to-PARK behaviour.
- `MonolithViewModel.kt`: `guardLinePrint`'s type stays
  `StateFlow<GuardLinePrint?>` and `guardLineFor` keeps its nullable
  return type, so the combine, both `SealFreeze` capture sites, and
  every collector compile-shape unchanged. Tightening the type to
  non-null is a separate cleanliness pass; do not couple it to #9.
- PARK's board itself is untouched: its name-only unmeasured print
  was explicitly acquitted by the audit (P-6: "the board's name-only
  unmeasured print is acquitted — it is the summary line that lied
  by silence").

### 3.3 One consequence Hof must rule on (not hidden)

The seal freeze captures `guardLineFor(...)` at session birth / at
SealedRunning. Before this change, a seal struck before any sweep
landed captured `null`, and `guardLinePrint` then *fell through* to
the live derivation for the whole session (its guard is
`freeze?.guardLine != null`). After this change, that same seal
captures `GUARD — NOT CHECKED` and the band holds it frozen for the
session's life. Both behaviours are defensible; the new one is
arguably the freeze law working as written ("the band freezes at
this print for the session's whole life"). In practice the Deck-entry
sweep precedes any hand arm, so a NOT CHECKED freeze requires a
routine-born session in a fresh process. Flagged, not silently
decided: if Hof wants the fall-through preserved, the one-line
variant is to keep NOT CHECKED out of the freeze (capture
`guardLineFor(...)?.takeIf { it != GUARD_NOT_CHECKED }`). The builder
recommends **accepting the freeze** — it is the honest print of the
seal frame.

---

## 4 · Item 3 — `FIRED AS SET` off the plate (P-7 / W-11) — ALREADY SATISFIED, NO CHANGE

Verified on the base trunk (3ea05bd), three ways:

1. **The string is gone from the plate.** `ParkDrawer.kt`
   `PLATE_BODY` (ParkDrawer.kt ~L107–122) prints
   `"ROUTINES: SET BY HAND ON SHIELD"` — no `FIRED AS SET` clause.
   A repo-wide grep for `FIRED AS SET` in `app/src/main` hits only
   the explanatory comment above that line.
2. **The strike is documented in-code with its return conditions**
   (UNA-05 comment, ParkDrawer.kt ~L113–119): falsified on the
   user's phone (Oct 29, 20:00 SLEEP no-fire); the clause "may print
   again only when Wache's F1–F5 are in the same build AND a live
   fire has been re-verified — that restoration is a merge-time act
   on that evidence, not before." That is exactly W-11's "unless
   earned," already encoded. **Nobody in this cell re-adds it, and
   the dossier proposes no edit to the comment or the line.**
3. **A regression test already stands guard:**
   `TruthCar3SweepTest.una05_plateDoesNotClaimFiredAsSet` asserts
   `ROUTINES: SET BY HAND ON SHIELD` exists and `FIRED AS SET`
   `assertDoesNotExist()` in the composed ParkDrawer.

Scope note for Hof: AUDIT-park §1.11/§4.9 also cut the plate's
COLOUR line and the `DATA: ON THIS PHONE ONLY` segment. Both are
**still present** in `PLATE_BODY` today. They are not in issue #9's
item 3 as briefed (the `FIRED AS SET` clause only) and the W-08
dynamic plate is batch-10/Ren's — this cell neither cuts them nor
designs their cut. If Hof rules them into #9, it's a 2-line
follow-up, not a design problem.

---

## 5 · Risks

- **R-1 · One-object material law.** CHECK makes the board carry a
  second object beside FIX (plus the already-excepted SOCIAL SITES
  sled). The audit ordered the verb (§4.1) but the exception should
  be named in the merge note, exactly as UNA-02's was (§2.2).
- **R-2 · Sweep overlap.** Double-tapping CHECK (or CHECK racing the
  new PARK-open re-sweep / an ON_RESUME sweep) runs two generations.
  Existing machinery tolerates it: completion is judged by wall-era
  (`measuredWall >= pendingSweepStartWall`), not by gen equality,
  and `publish()` is `@Synchronized`. Worst case is a redundant
  re-measurement, never a torn snapshot. The one wart: the second
  `runSweep` overwrites `pendingSweepStartWall`, so the first
  sweep's landings also count toward the second's set — semantically
  fine (they are fresher than its dispatch), but a verifier should
  run T-4 rather than take this paragraph's word for it.
- **R-3 · Call-site breakage.** `ParkDrawer(...)` has 6 test call
  sites (`TruthCar3SweepTest`, `SoundAndSocialTruthTest`,
  `ShortsToggleRenderTest`, `ScreenshotHarnessTest`) + the
  MainActivity site. The new `onCheckClick` parameter is therefore
  **defaulted** (`= {}`) and placed after `onFixClick`; a required
  parameter would break every render test for zero benefit.
  Defaulted no-op in tests means render tests do not exercise CHECK
  — T-5 exists for that reason.
- **R-4 · Header staleness lag (pre-existing, not created by #9).**
  `guardLinePrint` re-derives only on store emissions; with no
  session running, nothing emits on a cadence, so a GRANTED header
  can outlive its 45 s in wall time until the next emission (Deck
  entry / resume / a CHECK landing). CHECK improves this (its
  landings emit), but a full cure is a ticking derivation — out of
  #9's scope, named here so nobody later calls it a #9 regression.
- **R-5 · BIND-item laundering (audit RESIDUE, unchanged).** While a
  session runs, the shield loop re-stamps ACCESSIBILITY /
  NOTIFICATION_LISTENER from `instance != null` every 2 s, so CHECK's
  sweep values for those two items can be overwritten between tap
  and read by same-source stamps. Same source, same answer — no
  divergence — but CHECK does not make BIND proof stronger, only
  fresher for the four GRANT CHECK items. (The witnessed fix is
  #21's strip; see §0.)
- **R-6 · NOT CHECKED freeze.** §3.3 — needs Hof's one-line ruling.
- **R-7 · Wording exactness.** The audit's string is
  `GUARD — NOT CHECKED` (em dash U+2014, single spaces). The
  implemented constant and the test both use it verbatim; a verifier
  should grep the literal, not a paraphrase.

---

## 6 · Test list for the verifier (nothing here was run by the builder)

Unit (JUnit, `app/src/test`):

- **T-1 · `ChromeWaveUnitTest.guardLine_unmeasuredPrintsNotChecked`**
  (changed, in the builder clone): partial snapshot, `null`, and
  `WatchSnapshot.EMPTY` all print exactly `GUARD — NOT CHECKED`.
- **T-2 · `ChromeWaveUnitTest` remainder unchanged and green:**
  `guardLine_wholePrints4of4`, `guardLine_lackNamesTheFirstInBoardOrder`,
  `guardLine_staleNamesWithTheStaleSuffix` (stale must NOT collapse
  to NOT CHECKED), warrant-table and staleness tests untouched.
- **T-3 · `WatchStoreRobolectricTest.sweep_completesTheSetAndAdvancesLastProved`**
  + neighbours: the sweep contract CHECK rides on — complete set
  advances LAST PROVED, partial never does.
- **T-4 (new, for item 1)** · Overlapping sweeps: call
  `WatchStore.runSweep` twice back-to-back with a slow query for one
  item; assert no item is lost, `lastProvedWallMillis` advances
  exactly once the full era set has landed, and `permPrintAt` reads
  GRANTED for all six at the landing instant. (Use the existing
  `resetForTest()` seam + `awaitSnapshot` pattern from
  WatchStoreRobolectricTest.)

Compose / render:

- **T-5 (new, for item 1)** · ParkDrawer render test in the
  `TruthCar3SweepTest` pattern: unsealed + snapshot with a STALE row
  → node `park_check_key` exists and is labelled `CHECK`; sealed
  (`isSealed = true`) → `park_check_key` does not exist; tapping it
  invokes the passed `onCheckClick` exactly once. Plus: FIX key and
  CHECK key co-exist when a lack is present.
- **T-6 · `TruthCar3SweepTest.una05_plateDoesNotClaimFiredAsSet`** —
  item 3's standing guard; must stay green, unedited.
- **T-7 · Existing ParkDrawer suites** (`SoundAndSocialTruthTest`,
  `ShortsToggleRenderTest`, `ScreenshotHarnessTest`) compile and
  pass with the defaulted `onCheckClick` — proves R-3's default
  did its job.

Manual (phone, after Ren's build — the only place these can be proven):

- **M-1 · Cold open:** fresh process (force-stop, relaunch) → header
  reads `GUARD — NOT CHECKED` from the first frame until the
  Deck-entry sweep lands, then the real count. Never blank, never
  `0/4`.
- **M-2 · PARK re-proves:** open PARK, wait >45 s watching a GRANTED
  row go STALE, tap **CHECK** → rows re-print fresh, LAST PROVED
  advances to now. Repeat via close/reopen (the on-open re-sweep)
  without tapping.
- **M-3 · Revocation honesty:** revoke Usage Access in Settings
  while PARK is open, return, tap CHECK → row prints MISSING and
  the header recounts — CHECK must be able to make the board
  *worse*, or it proves nothing.
- **M-4 · Sealed PARK:** during a sealed session, open PARK (where
  reachable) → no CHECK key, no FIX key, sleds inert — print only.
- **M-5 · Plate:** PARK foot still prints
  `ROUTINES: SET BY HAND ON SHIELD` and no `FIRED AS SET` anywhere.

---

## 7 · Builder's summary of what exists after this dossier

- Item 2: **code in the builder clone**, DECLARED-UNCOMPILED
  (`GuardLine.kt`, `ChromeWaveUnitTest.kt`; `git diff` on branch
  `agent/issue-9-builder` shows the whole of it — 2 files).
- Item 1: **design only** — §2 is buildable as written; estimated
  S (ParkDrawer ~45 lines + MainActivity 2 lines + tests T-4/T-5).
  No ViewModel or WatchStore change needed.
- Item 3: **nothing to build** — verified already satisfied on
  trunk, with its regression test standing (T-6).
- Open for Hof: R-1 (name the second-object exception in the merge
  note), R-6 (NOT CHECKED seal-freeze: accept, or take the one-line
  fall-through variant), §4 scope note (COLOUR / DATA plate lines —
  in #9 or not), §2.2 fallback wording (`CHECK — GUARD`) if the bare
  verb is rejected.

— Builder, issue-9 cell (for Hof)
