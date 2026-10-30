# ISSUE-67 · Wave-3 Report — Audit R1 · architecture (5 findings)

Seat-bundle: 20 seats carried by the issue-67 cell (lead cluster C9: NACHLASS).
Branch: `agent/issue-67` · Base at seat time: `origin/main` e29acdf ·
Rebased onto current main 73aaeae before merge (merge order 04:56 IST).
Repo: Sarthak-2007/Focus-app.

---

## 1. Verification first — audit vs CURRENT tree (measured, not assumed)

The audit read main @8438f5a. Re-measured at base e29acdf before any cut:

| Metric | Audit (@8438f5a) | Measured @e29acdf | Verdict |
|---|---|---|---|
| MonolithRepository.kt | 2,932 lines | **2,932** | holds exactly |
| MonolithViewModel.kt | 2,548 lines | **2,548** | holds exactly |
| BlockitCoreViews.kt | 2,416 lines | **2,416** | holds exactly |
| Repository service imports | 9 (`com.example.service.*`) | **9** (Accessibility, DeviceAdminReceiver, Shield, RoutineScheduler, TactileSoundEngine, UsageAccess, Warrant, WatchItemId, WatchStore) | holds |
| Repository → concrete services driven | ShieldService.protecting:633→(626ff), startShield:1099, RoutineScheduler.rearmChain:913, A11y `.instance`, NotificationListener `.instance` (fully-qualified), lazy TactileSoundEngine:1831 | **all present** (standingWarrant, watchGrantQuery, reseedRoutinesV1, materialiseRoutineSession, breakSoundEngine) | holds |
| Services construct Repository | ShieldService:81, RoutineHeartbeat:92, SessionSealTransition:317 | **all present, verbatim** | holds |
| Services → DAO direct | A11y:134,193 · Boot:36 · NotificationListener:71 · ReleaseRetry:72 | **all present** | holds |
| Repository re-fetches DB | 1382, 1423, 1482, 2226 | **4 sites present** | holds |
| Services → MainActivity | A11y:10, Shield:16 imports | **both present** (3 intent sites) | holds |
| ViewModel holds | `appContext: Context` + `val soundEngine: TactileSoundEngine` @203-206 | **present @204/206** | holds |
| Repository framework refs | ~80 claimed (grep incl. comments) | **33 code-line hits** (AppOps/DevicePolicy/UsageStats/Audio/PowerManager/Settings/getSharedPreferences) + 6 getSharedPreferences sites (189, 455, 464, 477, 487, 495, 500) | holds in substance |
| Package | namespace `com.example`, applicationId `com.aistudio.monolith.kxtpzr`, no domain package | **exact** — packages: com.example, .data (15 files), .service (13), .ui.* | holds |
| Service package junk drawer | WatchStore holds Warrant/UsageAccess/WatchItemId models; TactileSoundEngine is an audio engine; SoundEffectsSetting is a prefs object | **exact** | holds |

No finding was stale. Nothing struck through as "no longer holds".

## 2. What was done — the largest SAFE, CI-provable surgical set

Three batched commits (each pushed immediately; see §6 for CI):

### (c) Domain slice — NEW package `com.example.domain`
- `domain/FocusDomain.kt` (124 lines, **zero imports beyond Kotlin**): five pure
  derivations extracted verbatim from the Repository — package categorisation,
  peek-cooldown arithmetic, held-apps count, 25-minute-block derivation,
  completed-task summary formatting. Repository delegates at all five sites;
  the Android parts (clock reads, DAO reads, enum mapping) stay in the
  repository, where they belong.
- `FocusDomainTest.kt`: 6 pure-JVM tests pinning the shipped behaviour
  (bucket precedence, cooldown cap/countdown/floor, held count, block floor,
  summary format) — the move is provably a move, not a change.

### (a) Circular edge broken — `RepositoryServicePort`
- `data/RepositoryServicePort.kt` (87 lines): the port, in the data layer,
  following the codebase's own `ArmSessionPort`/`RepositoryArmSessionPort`
  precedent. Facts (accessibilityBound, notificationListenerBound,
  shieldProtecting) + verbs (startShield, routine-chain cancel/rearm,
  playBreakLatch) + `UnboundRepositoryServicePort` (service-less default —
  the semantics test processes always had; production never uses it).
- `service/LiveRepositoryServicePort.kt` (53 lines): the live implementation
  on the service side — the one file that touches concrete services on the
  repository's behalf; every body a verbatim forward of the old call.
- Repository changes: constructor gains `servicePort` + injected `database`;
  concrete imports of MonolithAccessibilityService, MonolithShieldService,
  RoutineScheduler, TactileSoundEngine **deleted**; all call sites re-pointed
  to the port; the lazy break sound engine leaves the data layer (the live
  port holds it with identical lazy semantics); the 4 `getInstance` re-fetches
  replaced by the injected `db()`.
- Remaining data→service imports in the Repository: **9 → 5**, and the 5 are
  non-driven: `MonolithDeviceAdminReceiver` (a ComponentName *identity* for
  isAdminActive — never called/started/read) + the WatchStore model types
  (Warrant, UsageAccess, WatchItemId, WatchStore — derived facts read through
  a pure derivation, no service driven). Both remainders are documented in
  the port file itself.

### (a2) Service → MainActivity edge broken — `InterceptIntents`
- `service/InterceptIntents.kt` (68 lines): intercept + notification intents
  addressed by the activity's class name (`com.example.MainActivity`, the
  manifest's `.MainActivity`), with extras/flags **byte-identical** to the
  direct intents (intercept: NEW_TASK|SINGLE_TOP via addFlags; notification:
  flags SET to SINGLE_TOP only — the distinction is preserved on purpose).
- MonolithAccessibilityService and MonolithShieldService: `import
  com.example.MainActivity` **deleted**; service→MainActivity compile-time
  dependency is now **zero** across the service package.

### (b) Single construction point — `AppContainer`
- `AppContainer.kt` (55 lines, package com.example): owns database / DAO /
  repository (DAO + live port + same database instance) / soundEngine
  construction.
- Routed through it: MainActivity (was MainActivity.kt:83-85 hand-assembly),
  MonolithShieldService, RoutineHeartbeat, SessionSealTransitions provider.
  After: **zero** `MonolithRepository(` constructions and **zero**
  `MonolithDatabase.getInstance` calls remain in main sources outside
  AppContainer / the database's own singleton / the repository's documented
  null-fallback for direct test constructions.

### (d) Services bypassing the repository — worst offender routed
- MonolithShieldService's monitor loop (it already held a repository):
  `dao.getActiveSessionSnapshot()` → `repository.activeSessionSnapshot()`,
  both `dao.getAllAppsSnapshot()` → `repository.allAppsSnapshot()` (new
  one-line repository seam). The remaining direct-DAO services (A11y event
  pipeline, BootReceiver, NotificationListener, ReleaseRetryScheduler) keep
  DAO reads — they hold no repository and run in pre-repository boot/event
  contexts — but their DAO *construction* now routes through AppContainer
  (A11y ×2, Boot, Listener, ReleaseRetry). Full routing of those four is
  priced in §4, not faked here.

## 3. Per-finding disposition

1. **God classes — STANDS-BLOCKED (remainder).** Fixed part: 5 pure
   derivations (categorise, peek cooldown, held count, blocks25, task
   summary) + the sound engine left the Repository for the domain slice /
   service port. Measured remainder: Repository 2,932 → 2,941 lines (port
   documentation exceeds extraction — the class did not shrink; claiming
   otherwise would be theatre), ViewModel 2,548 (untouched), BlockitCoreViews
   2,416 (untouched). Splitting the god classes is a multi-week programme
   (§4), not a surgical pass. **Box stays unticked.**
2. **Circular data↔service coupling — FIXED for every driven edge.**
   Repository drives zero concrete services; services construct the
   repository only via AppContainer. Remaining non-driven references
   (DeviceAdminReceiver identity, WatchStore model types) are documented
   remainders, not the filed cycle. **Box ticked**, with the remainder
   written inline in the issue body.
3. **No DI / single source of truth — FIXED for construction; remainder
   noted.** One construction point (AppContainer); repository database
   injected, re-fetches deleted; DAO construction everywhere routes
   through the container. Remainder: four event/boot services still read
   DAOs directly (documented, priced §4) — the issue body says so inline.
   **Box ticked** with the remainder inline.
4. **Template package / no domain layer — SPLIT.** Domain layer: **FIXED**
   (`com.example.domain` exists, pure, tested, delegated-to). Package
   rename `com.example` → real package: **STANDS-BLOCKED** — it renames the
   namespace, every file's package, test packages, the manifest's
   `.MainActivity`, Room/KSP generated code paths, and the InterceptIntents
   class-name constant in one atomic sweep; a partial rename breaks
   everything including provider/manifest resolution, and it cannot be
   proven safe incrementally by CI in one pass. Priced plan in §4.
   Junk-drawer remainder: WatchStore's domain models still live in
   `service/` (moving them is the same sweep class as the rename).
   **Box stays unticked**, dispositions inline.
5. **Layering inverted — FIXED for the two filed service edges; remainder
   STANDS-BLOCKED.** Services → MainActivity: **zero** (InterceptIntents).
   Repository → sound engine: removed (port). Repository's Android-framework
   work (33 framework code refs, prefs, permission queries) and the
   ViewModel's Context + soundEngine: **remain** — unwinding them means
   re-homing every permission/prefs query behind facades and re-plumbing
   the ViewModel's construction, a programme (§4), not a surgical pass.
   **Box stays unticked**, dispositions inline.

## 4. Priced, sequenced refactor plan for the remainders

1. **Repository split (finding 1+5)** — carve by existing section banners:
   SessionMachine, ReceiptMachine, TaskQueue, RoutineOps, GuardQueries
   (framework facade), each behind the DAO + port, Repository becomes a
   facade delegating. ~5 slices × 1 day each, tests-first per slice
   (the repository test suite is the net). Sequence: GuardQueries first
   (removes all framework refs), then TaskQueue, Receipt, Routines, Session.
2. **ViewModel split (finding 1+5)** — move `soundEngine` behind the same
   port family (a UiSoundPort owned by AppContainer), drop `appContext`
   by injecting the 4 call sites' needs (package name, scheduler verbs via
   port). ~2 days. Then split state holders per destination. ~3 days.
3. **BlockitCoreViews split (finding 1)** — mechanical file split by view
   family (2416 lines, no state coupling across families found in census).
   ~1 day, screenshot harness as net.
4. **Event-service repository routing (finding 3 remainder)** — give A11y /
   Boot / Listener / ReleaseRetry repository reads via AppContainer where
   a repository exists in their process context; keep DAO only where boot
   ordering forbids it, documented per site. ~1 day.
5. **Package rename (finding 4)** — one dedicated change, nothing else in
   it: Android Studio package rename or scripted sweep over main+test+
   manifest+InterceptIntents.MAIN_ACTIVITY_CLASS, then full CI + a phone
   install-over-install test (applicationId unchanged, so data survives;
   the risk is compile/manifest, which CI proves). ~0.5 day + bench.
6. **WatchStore model relocation (finding 4 remainder)** — move Warrant /
   UsageAccess / WatchItemId / WatchSnapshot to `com.example.domain` as
   part of (5) or standalone with import-only diffs. ~0.5 day.

Total remainder programme: ~9–10 focused days, sequenced as above.

## 5. Behaviour-effect flags (Sarthak's 04:58 rule — plain words, per change)

- **Domain extraction (c): NO user-visible effect.** Same formulas, pinned
  by new tests written from the shipped behaviour.
- **Service port (a): NO user-visible effect intended.** Every live body
  forwards the exact old call. One internal difference, stated plainly:
  in service-less processes (unit tests) the port is the unbound no-op
  instead of try/catch-wrapped concrete calls that already no-oped there.
  Production always gets the live port via AppContainer.
- **Intercept seam (a2): NO user-visible effect intended.** Same target
  activity, same extras, same flags — the notification intent keeps its
  distinct SINGLE_TOP-only flag semantics. The intent is now addressed by
  class *name* instead of class *reference*: if the activity class were
  renamed without updating `InterceptIntents.MAIN_ACTIVITY_CLASS`, the
  door would fail to open — that coupling is documented in the file.
- **AppContainer (b): NO user-visible effect.** Same singleton database,
  same DAO, same construction values; the repository now receives the
  database it used to re-fetch (the same instance).
- **Shield loop routing (d): NO user-visible effect.** Same reads, through
  the repository the loop already held.
- **Nothing was weakened, dropped, or traded for CI.** No test edited, no
  check removed, no feature reduced. (04:58 rule 2 compliance.)

## 6. CI evidence

- Pre-rebase runs 40972969764 / 40973433567 (base e29acdf): assembleDebug
  GREEN; unit-test step failed at **test compilation** in
  ScreenshotHarnessTest / ShortsToggleRenderTest / SoundAndSocialTruthTest /
  TruthCar3* — call sites missing `socialSitesEnabled` /
  `shortsReelsSniperEnabled` params. Those files and the composables are
  untouched by this branch: the failure was **pre-existing on main
  @e29acdf**, and issue-2's landing on main (f789cef + test-call-site
  fixes, merged as 73aaeae) fixed exactly those files.
- This branch was therefore rebased onto 73aaeae (clean, no conflicts) and
  re-pushed; the final branch CI run and the post-merge main CI run are
  recorded in the closing issue comment (run URLs + verdicts).

### Final CI story (addendum, recorded at closure)

- Batch (a2) initially failed assemble on two defects of THIS branch —
  recorded, not hidden: `InterceptIntents` used a corrected spelling
  `EXTRA_INTERCEPTED_BADGE` where the codebase's constant is
  `EXTRA_INTERCEPT_BADGE`, and an inline comment ate a comma in
  ShieldService's notification PendingIntent. Both fixed in aa4793a's
  parent commit; no test or behaviour was touched to get green.
- Main moved twice more during the pass (issue-2 → 73aaeae; then #35/#47/
  #61/#62/#17/design-revamp → 892da87). Second rebase conflicted only in
  ReleaseRetryScheduler (resolved by keeping #47's targeted read verbatim
  and applying only the container routing) and surfaced one NEW
  getInstance site #47 had added in RoutineHeartbeat
  (recoverChainAfterFailure) — routed through AppContainer in c90849e.
- Baseline comparison (the honest yardstick): main @73aaeae's own run
  (40973388591) executed 258 tests with **37 failures** — the baseline is
  red. This branch's run at the same base (40978204767) executed 264 with
  38: the delta was two one-run flakes (RoutinesFinalise history test —
  a UTC-midnight day-bucket flake: main ran 23:46 UTC, this run crossed
  00:00 UTC mid-suite; TruthCar3 una07 — an
  UncaughtExceptionsBeforeTest pollution flake) and one baseline failure
  flipping to pass (SoundEngineBirthOrder).
- **Final branch run 40984790641 (tip c90849e, base 892da87): assemble
  debug GREEN; 321 tests executed, 37 failed — the failing set is
  name-for-name identical to the main baseline set.** None of the
  intervening lanes' new tests fail; all 6 new FocusDomainTest tests
  pass. The 37 baseline failures are other lanes' territory (incl. the
  DB v11→v12 migration tests, which the merge order reserves to
  Konto's train — flagged, not forced).

### Second-rebase addendum (main → 357debc, before merge)

- Main landed #48/#55/#132/#17/#132 among others; MonolithRepository.kt
  itself changed. Rebase conflicts: MainActivity (resolved: #48's new
  WatchStore.bind kept, construction via AppContainer) and the
  categoriser — **issue #132 independently extracted the same
  categorizePackage function into AppCategorizer on main, as a
  superset (old buckets verbatim + MUSIC token category)**. Keeping
  this branch's FocusDomain version would have downgraded #132, so
  the domain slice defers: categorizePackage delegates to
  AppCategorizer, FocusDomain keeps the remaining four derivations,
  FocusDomainTest drops to 5 tests. One classifier, never two — the
  convergence is also recorded in both files' comments.
- Post-rebase census re-run on the final tree: Repository service
  imports still 5 (non-driven only), servicePort used at 9 sites,
  getInstance outside container/DB/fallback = 0, Repository
  constructions outside AppContainer = 0, service→MainActivity = 0,
  FocusDomain delegates = 4. Repository stands at 2,981 lines on the
  new main (other lanes' growth, not this branch's).

## 7. Seat verdicts (20 seats, signed)

- **Crazy-ideas** — Proposed replacing the intercept Intent entirely with a
  bound-service doorbell; rejected by own seat: the class-name seam achieves
  the decoupling with zero protocol change. The rename-trap is the only
  cost, and it is documented where it lives. — *Crazy-ideas*
- **Maximum-implementation-potential** — The port + container + domain slice
  is the maximum set that stays surgical; everything beyond it is the §4
  programme. Pushing further in one pass would have traded correctness for
  architecture points — refused. — *Maximum-implementation-potential*
- **Brutal critic** — The Repository GREW by 9 lines net. Anyone calling
  this "the god class is fixed" is lying; finding 1 stands blocked and this
  report says so in the open. The extractions are real but small. — *Brutal critic*
- **Improver** — Every seam added (port, container, domain, intercept) is
  the kind the next slice can build on without rework; §4 sequences them.
  The unbound-port default keeps 35 test constructions untouched. — *Improver*
- **Designer** — LEAD-COVERED: no visual surface touched; nothing in this
  branch changes a pixel. — *Designer (lead-covered)*
- **Consumer** — LEAD-COVERED: app behaviour, sounds, doors, notifications
  unchanged per §5; nothing a user could feel was altered. — *Consumer (lead-covered)*
- **Student** — LEAD-COVERED: no study-flow behaviour touched. — *Student (lead-covered)*
- **User** — LEAD-COVERED: zero user-visible change; §5 is the proof list.
  — *User (lead-covered)*
- **User-experience** — LEAD-COVERED: intercept door and notification tap
  behave identically (same intent, extras, flags). — *User-experience (lead-covered)*
- **Purpose-questioning** — Asked why decouple at all if the classes stay
  huge: because the cycle, not the size, is what made every other fix
  dangerous. The cycle is broken; size is priced honestly. — *Purpose-questioning*
- **Thoughtful** — The DeviceAdminReceiver and WatchStore remainders are
  named in code comments at the seam, not buried; a future reader meets
  the debt where it lives. — *Thoughtful*
- **Deep-thinker** — The port's unbound default is the load-bearing choice:
  it makes the data layer honest about service-less processes instead of
  hiding try/catch "safe ignores" inside production calls. — *Deep-thinker*
- **Enthusiastic designer** — LEAD-COVERED: a domain package finally exists
  to design future slices against. — *Enthusiastic designer (lead-covered)*
- **Wow-level designer** — LEAD-COVERED: no wow claimed for plumbing; the
  wow is that CI, not adjectives, arbitrates §6. — *Wow-level designer (lead-covered)*
- **Designer-29-years** — LEAD-COVERED: the ArmSessionPort precedent was
  followed rather than inventing a new pattern — 29 years says consistency
  beats novelty in a codebase's seams. — *Designer-29-years (lead-covered)*
- **Intentionality designer** — Every new file opens with the finding it
  answers and the behaviour it refuses to change; intention is written
  where the next agent will trip over it. — *Intentionality designer*
- **Synchronous collaboration** — Rebased onto the moved main the moment
  its advance was seen (issue-2's test fixes were the exact pre-existing
  failure); no work was stranded on the stale base. — *Synchronous collaboration*
- **Implementation-quality** — Batched commits, each pushed and CI-guarded;
  no test weakened, no check dropped, no behaviour traded (04:58 rule 2).
  The one red encountered was proven pre-existing and inherited-fixed via
  rebase, not patched around. — *Implementation-quality*
- **Technical execution specialist** — Verified by grep census before and
  after: service imports 9→5 (non-driven only), service→MainActivity 2→0,
  scattered constructions → 1 container, DB re-fetches 4→0. — *Technical execution specialist*
- **Interaction-detail specialist** — Checked the two flag semantics by
  hand: intercept addFlags NEW_TASK|SINGLE_TOP vs notification flags=
  SINGLE_TOP — preserved distinctly in the seam; a merged helper would
  have silently changed the notification tap. — *Interaction-detail specialist*
