# ISSUE #81 — Audit R2 · tamper-routes — WELLE cell report

Repo Sarthak-2007/Focus-app · branch `agent/issue-81` · base origin/main `0a00e95`
Cell: `~/workspace/clone-inspect/builds/cells/issue-81` · No push, no local gradle, no GitHub writes (per brief).

**Headline: 5 findings verified against current code — all 5 HELD (none cured on main). 4 FIXED-IN-CODE, 1 split: F1's detectable half FIXED-IN-CODE, its recovery half STANDS-BLOCKED on a hard platform limit (named below). 12 new unit tests. 5 commits (F5's commit was blocked by a box-wide write wall at report time — see "Commit state").**

---

## Per-finding disposition

### F1 [CRITICAL] Clear-data / `pm clear` total + undetectable
**Disposition: FIXED-IN-CODE (detection) + STANDS-BLOCKED (recovery) — commit `c5f8ce2`**

Verified holding on current tree:
- Room DB built on `createDeviceProtectedStorageContext()` (MonolithDatabase.kt, `getInstance`) — app-private, wiped by clear-data.
- Every other store is app-private SharedPreferences (seal marker MonolithRepository.kt SEAL_PREFS_NAME, chrome/app-list prefs, DayWordStore, LoadoutStore, UndoDeleteStore).
- `allowBackup=false`; `res/xml/data_extraction_rules.xml` + `backup_rules.xml` exclude every domain for cloud backup and device transfer.
- `ensureSeededAndSyncPackages` read an empty DB exactly as a fresh install (reseed + `parkIntroPending()` true again). No surviving marker anywhere.

Fix (the detectable residue): `pm clear` wipes everything app-owned **except the AndroidKeyStore** — keystore entries are system-held per app UID, deleted on uninstall/factory-reset, **not** on clear-data. `CryptoIntegrityManager.hmacKeyExists()` (containsAlias, never generates) + `MonolithRepository.recordProvenanceIfResurrected()`, run inside the seed lock before any seeding write and before anything can sign (signing would generate the key and forge the residue): virgin DB (no sites / no guard settings / no routines / no session history) + pre-existing HMAC key ⇒ `TamperWitness.classifyProvenance` = RESURRECTED_UNPROVEN, stamped into `monolith_provenance` prefs and carried on `SystemGuardStatus.installProvenanceUnproven` / `repository.provenanceUnproven()`. A resurrected install now reports itself **UNPROVEN, never a clean first run**.

STANDS-BLOCKED (exact blocker): recovering — or even proving the existence of — the destroyed session is impossible for a normal (non-device-owner, non-system) app after `pm clear`. Every byte that recorded the session lived in storage the platform defines as wiped, and no API lets an app write outside its sandbox without a user-granted shared-storage permission Monolith neither has nor should silently take. Anyone claiming post-clear session recovery for a normal app is faking it. What ships is the strongest truthful residue: prior life is proven, provenance is UNPROVEN forever for that install's new data life.

### F2 [CRITICAL] Manual relaunch after force-stop/kill never restarts the shield
**Disposition: FIXED-IN-CODE — commit `c243cc7`**

Verified holding: exhaustive `startShield` call sites on current tree = MonolithBootReceiver.kt:53 (boot only), MonolithRepository.kt materialise path (routine only), SessionSealTransition.kt:242, ArmProvingController.kt:358/522 (arm/seal only). MainActivity.onCreate (DB/repository/soundEngine + intercept handling only), MonolithViewModel init (seed → RoutineHeartbeat RESUME tick → refreshSystemGuard), the per-second VM ticker (latchShieldGapIfStale + updateSessionProgress only), and RoutineHeartbeat.reconcileOnce step 4 (stopShield only) never re-raised it. Force-stop cancels the FGS + alarms and blocks broadcasts until manual launch — that launch walked through the one recovery door without re-raising the shield: `instance == null`, `protecting == false`, timer advancing, indefinitely.

Fix: `RoutineHeartbeat.reconcileOnce` step 4b — after the finalise/materialise diffs, a session still live with `MonolithShieldService.instance == null` gets `startShield`. Every recovery trigger (RESUME on relaunch, BOOT, CHAIN_ALARM, OPPORTUNISTIC from the accessibility/listener services, SESSION_EDGE, CRUD) runs this same funnel pass, so every door now resurrects; the instance guard means a live shield is never re-started (a second onStartCommand would reset its protecting witness for nothing).

### F3 [HIGH] Safe Mode undetected; session clock freezes across Safe-Mode boots
**Disposition: FIXED-IN-CODE — commit `52229f7`**

Verified holding: `grep -rn -i 'safemode|safe mode'` over main + manifest + tests = 0 hits. `computeVerifiedSessionElapsed` reboot branch: tamper collapsed to `!hmacValid` (HMAC stays valid across Safe Mode), the >90s drift check ran only in the non-reboot branch, elapsed = prior + seconds-since-latest-boot only. Safe-Mode hours neither advanced nor convicted the session — a free, invisible freeze while preinstalled Chrome/YouTube still ran.

Fix: there is no in-Safe-Mode detection for a downloaded app (it cannot run there at all), so detection is retrospective at the first tick after return. `Settings.Global.BOOT_COUNT` increments on **every** boot, Safe Mode included; a normal reboot is witnessed promptly (boot receiver at LOCKED_BOOT_COMPLETED + shield tick re-anchor the row → delta exactly 1); a delta ≥ 2 against the row's anchor means a whole boot passed unwitnessed. `TamperWitness.unwitnessedBoot(anchored, current)` now feeds the reboot branch's tamper term (`!hmacValid || unwitnessed`), so the existing penalty ratchet (+15 min, once) and `tamperDetected` apply. Elapsed still credits witnessed time only — unproven hours are no longer silently forgiven; they cost the penalty and are on the record.

### F4 [HIGH] Usage Access revoked mid-session silently neuters the shield; finish still certifies ELAPSED_CLEAN
**Disposition: FIXED-IN-CODE — commit `11bb3b3`**

Verified holding: `queryForegroundBlockedApp` returns null when usage access is gone (MonolithRepository.kt, `if (!hasUsageStatsPermission() ...) return null`) — identical to "nothing blocked in foreground". Shield loop treated null as nothing-to-intercept, kept stamping the beat (`updateSessionProgress(stampShieldBeat = true)`) so the >10s stale-beat latch could never fire, kept flipping `protecting = true` (MonolithShieldService.kt), and the ending write stamped ELAPSED_CLEAN. `WatchStore.onShieldIteration` re-read only ACCESSIBILITY + NOTIFICATION_LISTENER; USAGE_ACCESS was re-measured only by the Deck-triggered sweep (Deck entry / resume / FIX return).

Fix, three layers:
1. Shield loop measures `hasUsageStatsPermission()` every iteration. Revoked ⇒ `MonolithRepository.latchShieldGapNow()` stamps `shieldGapSeen` on the row — deliberately **after** the progress write, because `dao.updateSession` is a full-row `@Update` and a latch set before it would be clobbered by the iteration's own pre-latch snapshot. Every later iteration re-reads the row fresh, so the latch persists into the finish classification. The `protecting` flip now also requires a sighted iteration.
2. `updateSessionProgress` outcome classification adds a fresh `!hasUsageStatsPermission()` term — a session completing on the very tick blindness begins still classifies ELAPSED_SHIELD_GAP.
3. `WatchStore.onShieldIteration(protecting, usageGranted)` stamps USAGE_ACCESS per iteration — the board re-measures it on the shield's 2s cadence during a session, not only at Deck triggers. Stop/destroy publications pass null and fabricate nothing.

### F5 [HIGH] adb/component-disable routes invisible
**Disposition: FIXED-IN-CODE — commit pending (write wall; working tree contains the full change — see "Commit state")**

Verified holding: `grep -rn 'ACTION_PACKAGE|getComponentEnabledSetting|COMPONENT_ENABLED'` = 0 hits. Manifest receivers were only boot / routine-alarm / release-retry / device-admin. The WatchStore sweep queried only the six permission items (MonolithRepository.watchGrantQuery). `pm disable` on any component killed enforcement with no Settings window for the accessibility text guard to see and no surface reporting it.

Fix:
- `MonolithRepository.disabledCriticalComponents()` — fresh `PackageManager.getComponentEnabledSetting` for MonolithShieldService, MonolithAccessibilityService, MonolithNotificationListener, MonolithBootReceiver. Pure state law in `TamperWitness.componentDisabled`: DEFAULT/ENABLED pass; DISABLED / DISABLED_USER / DISABLED_UNTIL_USED convict; an unreadable state counts as enabled (no evidence, no conviction).
- `RoutineHeartbeat` step 4c — every reconcile pass with a live session and a disabled critical component latches the session's shield gap via `latchShieldGapNow()`; re-latched each pass while the component stays down. The loss is written on the session's own record; its finish cannot certify clean.
- Manifest + MonolithBootReceiver: `ACTION_PACKAGE_CHANGED` (package scheme) intent-filter added, routed to a forced heartbeat tick (Source.PACKAGE_REPLACED). The receiver is only a doorbell that shortens time-to-notice; the heartbeat poll is the proof.

Known residue (stated, not hidden): `settings put secure enabled_accessibility_services ''` and `appops set GET_USAGE_STATS ignore` are not component disables — the first is already witnessed by the a11y BIND stamps (instance death → MISSING on the board, every shield iteration), the second is exactly F4's revocation and is now latched by F4's fix.

---

## Tests

`app/src/test/java/com/example/TamperWitnessTest.kt` (new, 12 tests, plain JUnit over the pure laws):
- Provenance matrix: virgin+key ⇒ RESURRECTED_UNPROVEN; virgin+no key ⇒ FRESH; intact+key ⇒ FRESH; intact+no key ⇒ FRESH.
- Unwitnessed boot: delta 0 ⇒ false; delta 1 ⇒ false (normal witnessed reboot); delta 2 ⇒ true; delta many ⇒ true; unanchored (0) ⇒ false; backwards counter ⇒ false.
- Component state: DEFAULT(0)/ENABLED(1) ⇒ false; DISABLED(2)/DISABLED_USER(3)/DISABLED_UNTIL_USED(4) ⇒ true.

Not run locally (brief forbids local gradle); CI on the lane's PR is the proof gate. Existing tests were checked for blast radius: `ExampleRobolectricTest`'s HMAC test uses the same boot count for anchor and read (delta 0, non-reboot branch — unaffected); no test calls `onShieldIteration` (new parameter has a default); `SystemGuardStatus` gains a defaulted field (the ViewModel's hand-built instance at MonolithViewModel.kt:394 still compiles).

## Commit state

| Commit | Finding | Status |
|---|---|---|
| `c5f8ce2` | F1 provenance | committed |
| `c243cc7` | F2 shield resurrection | committed |
| `52229f7` | F3 unwitnessed boot | committed |
| `11bb3b3` | F4 usage-access blindness | committed |
| (pending) | F5 component-disable | **working tree, uncommitted at report time** — box-wide write wall (`fatal: unable to write loose object file: Operation not permitted` on every git object write; even `dd` into `.git/objects` wrote 0 bytes; ~184 MB RAM free with the hatch daemon at 3 GB RSS). The 6 changed files are intact on disk: AndroidManifest.xml, MonolithRepository.kt, TamperWitness.kt, MonolithBootReceiver.kt, RoutineHeartbeat.kt, TamperWitnessTest.kt (F5 portions). Retry `git add -A && git commit` when the wall lifts; nothing else in F5 is outstanding. |

Nothing pushed. No gradle run locally. No GitHub touched.

---

## Signed lenses

**Verifier** — Every audit line was re-checked against the current tree at `0a00e95` + this lane's commits, by grep and by reading the cited spans at their current line positions. All five findings held as written; zero were cured on main; zero were downgraded. The two traps I caught in my own fixes before shipping them: (1) F1's keystore check must run before any signing, or the app forges its own residue — it sits inside the seed lock, first act; (2) F4's latch must land after the progress write, or the full-row `@Update` erases it with the iteration's own snapshot. Both are documented in the code at the exact spot a future editor would otherwise "simplify" them away. — *Verifier*

**Brutal critic** — What this lane did NOT do, plainly: F1 still cannot name the session that was destroyed, and never will on this platform — if anyone sells that as solved, they're lying. F3's delta-≥2 law will convict a man who reboots twice in a row quickly for innocent reasons (dead battery boot-loop, an update that double-boots): it stamps tamper + 15 minutes for what might be honesty. I accept it because the alternative — unwitnessed boots pass clean — is the hole itself, but it is a false-positive risk with a real cost and it should be watched on the phone. F5's witness depends on heartbeat passes running; disable the boot receiver AND force-stop, and detection waits for the next manual launch (which the F2 resurrection then covers). The gap latch is also still racy against an in-flight VM-ticker write carrying a pre-latch snapshot — pre-existing Slice E architecture, mitigated by per-pass re-latching, not eliminated. — *Brutal critic*

**Diagnostician** — Root cause of the kill/relaunch chain (F2): the app had exactly four shield-raising doors (boot, routine materialise, arm, seal) and the one door force-stop leaves open — manual relaunch — was never wired to any of them, because the relaunch path was built as a *UI restore* (seed, reconcile routines, refresh guard chips) by people thinking about what the user sees, not what the session needs. The heartbeat funnel already ran on that path (RESUME tick); it simply had a stop-only shield step. One funnel, one step (4b), every door fixed — including doors nobody had enumerated, because they all pass through reconcileOnce. Same shape in F4: the beat proved the shield *process* lived, and every consumer silently upgraded "process alive" to "door working". The fix separates the two facts at the source (sight measured per iteration) instead of patching each consumer. — *Diagnostician*

**Technical execution specialist** — Changes are surgical and ride existing machinery throughout: the seed lock (F1), the reconcile funnel (F2/F5), the reboot branch's existing tamper term + penalty ratchet (F3), the existing `shieldGapSeen` latch + DAO targeted UPDATE + outcome classifier (F4/F5), the existing per-iteration WatchStore publication (F4). No schema change, no migration, no new entity, no new permission. All new decision logic is in one pure object (`TamperWitness`, data/) with JVM tests, following the WallAccrual/RoutineDerive precedent. Compile risk points, self-audited: new imports in MonolithRepository (PackageManager, MonolithBootReceiver, MonolithNotificationListener — all same-project, no cycles: service→data references already exist in both directions in this codebase), defaulted parameters/fields keep all other call sites source-compatible, `Intent.ACTION_PACKAGE_CHANGED` is a public constant. CI must still prove the build; nothing here was compiled locally, per orders. — *Technical execution specialist*

**Consumer (plain words)** — What changes for Sarthak: (1) Wipe the app's data mid-session and it no longer wakes up pretending to be a brand-new install — it knows it lived before and flies an UNPROVEN flag; the dead session itself is gone for good, no app on Android can undo that, and this one now says so instead of hiding it. (2) Force-stop or kill Monolith, reopen it, and the shield actually comes back up for the session that was running — before, the timer kept counting with nothing blocking anything. (3) Sneaking into Safe Mode to use blocked apps now costs the tamper penalty and is written on the session, instead of freezing the clock for free. (4) Turning off Usage Access mid-session no longer produces a fake "clean finish" — the session is stamped as having a shield gap, and the app stops claiming it's protecting when it can't see. (5) Disabling Monolith's pieces with adb tricks now leaves a mark on the live session instead of silently unblocking everything. — *Consumer*
