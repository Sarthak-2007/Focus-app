# USABILITY BUILD — Plan

Filed by LENKER (usability-build captain), Oct 30, 2026. One build Sarthak can
install over his current app and run his day on. Usability first; everything
else waits.

> "Current status of app is that i even cant use it. Its so trash"
> — Sarthak, Oct 30, 07:30 IST
>
> "Is it a scheduler app? Is it a task app? What the fuck is the app?"
> "Why would i be blocking 24/7…"
> — Sarthak, Oct 30, 09:01–09:21 IST. Settled: this is a BLOCKER for study
> blocks. Not scheduler, not task app, not tracker, not 24/7.
>
> "The apk should be installable on my current installed"
> — Sarthak, Oct 30, 05:51–05:56 IST

## 0. Live state at filing (verified Oct 30, by named seat or by LENKER)

- Main HEAD at seating: `e65fa4ae9422` (merge of PR #342, #149 grace fix).
  Main is moving fast (PR #238 merged 04:45Z); HEAD is re-frozen at
  composition time and recorded in the release notes.
- **Signing: SOLVED on main.** LENKER's own apksigner run on
  `monolith-batch9.apk` (sha256
  `793331dcee8c5be196b98eef576febd95ffc5aa97a8cec9eccb950ecf2695122`):
  signer cert SHA-256
  `bd15ad5fe9b6964464c0f867c7ae96f61ddd34093b3877f6032befa1d10f27ff`.
  Main's committed `debug.keystore` (blob `a3c045dc`, Konto `0a00e95`) is
  this key; workflow keystore step is verify-or-die against it (PR #238,
  issue #24, MERGED). Wrong-key era (FE:08…, artifacts 22:49Z–00:11Z) is
  closed. Hazard: the local trunk working copy still holds an untracked
  wrong key — local builds from that copy would fail install-over; CI
  builds are unaffected.
- **versionCode: NOT solved.** Main is still `versionCode = 1`. The cure
  (1→2, versionName 1.1) exists only on branch `agent/issue-23` (commit
  `36b1e25`), CI-proven per Cluster C1, **no PR opened**. Issue #25
  (keystore ignores + dead secrets plugin) is likewise branch-only, no PR.
- **Cold-start cure (BINDUNG): NOTHING LANDED.** No lane of that name
  exists. The live cure is local branch `agent/issue-7-coldstart`
  (Mechanism C, issue #7): core probe committed locally (`1b641aa`), the
  sentence-table half (deletes "CAN'T CHECK BLOCKING", grace silence,
  past-grace "BLOCKING IS OFF") **uncommitted in its working tree**; never
  pushed, no PR, no CI, zero tests. Stempel's predecessor cure is likewise
  unlanded (issue #15 checklist item still unchecked). Mechanism dispute
  on record: Mauer prescribed a persisted heartbeat + three-state readout;
  Mechanism C is an active probe + 4 s grace silence — must be ruled
  before composition, not during.
- **Test floor (#166): named.** Anchor: main run `40990863688`
  (rescue-build, job 123852662587): 398 tests, 40 failed = **37 floor +
  3 NOT floor** (see §4). Boden's cure is PR #360, green on its branch
  (run `40999955868`), **unmerged** — the build is judged against main's
  floor, not Boden's branch.
- **Receipt (BELEG) — extra charge on record:** for FLOW/stopwatch
  sessions the receipt prints a fabricated `PLANNED` figure from the
  span-target floor formula while the true `sealedElapsedSeconds` is
  printed nowhere (`studio/plan/RECEIPT-REVIEW.md:25`). The receipt gate
  (§3 Step 5) must inspect a FLOW receipt, not only a broken-session one.
- **Routines caution (Day-Simulator):** seeded routines are
  `enabled = true` by default (FASTED 05:00–08:00, DAY 09:00–19:30,
  SLEEP 20:00–03:30) while Sarthak has NOT confirmed routines ("im not
  sure if it even is needed", rethink pending). The build must not ship
  auto-sealing he didn't ask for as a silent default — flagged to Ren as
  a decision-shaped risk, not silently absorbed either way.

## 1. The Bar

This build ships only if all five are true on device, not on paper:

1. **No fog on open.** Cold open lands on a usable screen. No
   `CAN'T CHECK BLOCKING — TAP TO RETRY`, no retry loop, no dead end.
2. **Arms a study block fast.** A few taps, no setup detour. Block screen
   un-exitable by construction (launcher-type, Oct 30 08:20 ruling) —
   not exit-and-return.
3. **Blocking is real; every printed status is true.** If the UI says
   blocking/armed, the shield is actually holding. If it cannot verify,
   it says so plainly. No unearned status prints.
4. **Exit / emergency paths as ruled.** Sole emergency path: continuous
   60-second hold, exactly one per week, progress banked at 15/30/45.
   No other values, no extra paths.
5. **Installs over his current app, data kept.** Same package, same cert
   (bd15ad5f…27ff), higher versionCode. No uninstall, no clear-data.

Any one failing = not a build. See §6.

## 2. Inputs

| Input | Required state before composition |
|---|---|
| main HEAD | Re-frozen at composition; full SHA in release notes |
| Design revamp PR #167 | In main (history) |
| Install-over (was "SCHLUESSEL") | Keystore: DONE on main. versionCode: PR for #23 opened, CI-proven, merged. #25 merged alongside |
| Cold-start (was "BINDUNG") | `agent/issue-7-coldstart` finished (both halves committed), pushed, PR opened, tests ported/written, CI green, mechanism dispute ruled |
| Shield truth (ZEUGE) | Lane report pending re-seat; composes only on its CI-proven PR |
| Receipt (BELEG) | Lane report pending re-seat; composes only on CI-proven PRs; FLOW-PLANNED charge answered |

Rules: a lane composes only when its PR is CI-green under §4 and merged.
No cherry-picking around a red run. A lane not proven by composition time
is never silently absorbed — LENKER reports the omission to Ren with the
bar it touches (no cold-start proof = no claim on Bar 1; no shield-truth
proof = no claim on Bar 3).

## 3. Composition — steps and gates

In order; a failed gate stops the sequence.

1. **Freeze base** at re-checked main HEAD. Gate: SHA recorded; tree clean.
2. **versionCode PR (#23) + #25 merged.** Gate: CI green under §4;
   artifact versionCode strictly higher than installed (= 2).
3. **Cold-start PR merged.** Gate: CI green under §4; PR carries cold-open
   evidence (repeated cold opens, no fog state) and the ported residency
   tests pass.
4. **Shield-truth PR merged** (when its lane proves). Gate: evidence both
   directions — status printed only when verified holding; honest
   unknown/off when not.
5. **Receipt PR(s) merged.** Gate: W-41/W-43 evidence + FLOW receipt
   inspected: no fabricated PLANNED segment, true elapsed printed.
6. **Full build on composed tree via GitHub CI.** Gate: run green under
   §4 on the exact composed commit; artifact from that run; SHA, cert
   fingerprint, versionCode recorded.
7. **Bar check (§1) against evidence.** Any bar without evidence = failed
   = §6. Phone-only bars (cold open, arming speed, kick-out feel, soak)
   are named as phone-pending in the release notes — never upgraded to
   "proven" by a green CI run.

## 4. CI judgement — the named floor

Pass = green except exactly these reds, same name + same signature as
anchor run `40990863688`. A floor-named test failing with a new signature
is a NEW failure. Any other red is a failure.

**Floor (37):**
- ScreenshotHarnessTest (14): captureAllDestinations ·
  captureAllDestinationsNight · captureTasksEmpty · captureTasksEmptyNight ·
  hb4DetentionGuardTap · hb4JammedRootApp · parkSheetDay · parkSheetNight ·
  receiptSheetFaultedFabricated · receiptSlipDay · receiptSlipNight ·
  receiptSlipShieldGapDay · tcDrawerDetailsAndSubSteps · zzAccentCensus
- DrawerRenderTest (7): digestFrozen_day · digestFrozen_night ·
  digestLive_day · drawerEmpty_day · drawerEmpty_night ·
  drawerPopulated_day · drawerPopulated_night
- CascadeDeckRenderTest (5): jammedNamesTheUnansweringLink ·
  jammedNamesTheUnansweringLinkAtNight ·
  provingKeepsTheSlideTenancyAndPrintsNothing ·
  routineBornSessionRunsWithThePhaseIdle · sealedTakesTheRunningTenancy
- SoundAndSocialTruthTest (4): SocialBoardWordFollowsTheToggle ·
  SocialToggleRenderedAndWritesThrough · SoundEngineBirthOrder ·
  SoundSettingPersistsAcrossProcessDeath
- TruthCar3Sweep (3): una04_extractionRulesExcludeEverythingBothWays ·
  una04_fullBackupRulesExcludeEverything ·
  una06_factoryDefaults_biteOnlyWhenStored
- BreakMigrationTest (1): migration11to12_addsTakenColumn_withTruthfulDefault
- ShortsSniperTruthTest (1): v11File_migration1112_shortsColumnLands_defaultTrue_rowIntact
- TetradRenderTest (1): tetradClosures
- ChromeWaveUnitTest (1): warrantDerivation_matchesTheDeletedLiveWarrantTable

**NOT floor (3) — live failures needing their own disposition if they
appear:** AirlockOverlayIssue55RenderTest:
expiredCooldownShowsPeekChipNotWithheldLine ·
exitsStayReachableOnShortScreen · samePackageReInterceptResetsWallAndHidesChip

Known gap: #166's baseline claims a 15th ScreenshotHarness failure that
is unnamed in the issue and absent from the anchor run — flagged, not
filled in. If PR #360 (Boden) merges before composition, the floor is
re-anchored on the first post-merge main run and this section is amended.

## 5. Release

From the Gate 6/7 passed commit only.

1. Tag the composed commit `usability-test-<versionCode>`; the tag points
   at the Gate 6 commit, nothing else.
2. GitHub Release from that tag, titled and first-lined as a **TEST
   build**.
3. Artifact `monolith-usability-test-<versionCode>.apk` = the Gate 6 CI
   artifact itself, byte-identical, never a rebuild.
4. Release notes record: composed SHA (full), base main SHA, included PR
   numbers, cert fingerprint (full), versionCode, Gate 6 CI run link with
   the §4 floor exception named, phone-pending bars listed as
   phone-pending, install instruction (install over, do not uninstall,
   data kept).
5. **PR tracker #245 updated in the same run:** every composed PR marked
   MERGED with CI verdict, plus the release line. Nothing counts done
   until it shows MERGED on #245.

## 6. Kill criteria

Stop and report to Ren — no ship, no workaround, no re-label — on any of:

1. Unearned status print anywhere (screen, receipt, release note).
2. Cert mismatch vs bd15ad5f…27ff.
3. versionCode ≤ installed, or unverifiable.
4. Any non-floor red (§4), including a floor name with a new signature.
5. Fog still reachable on any cold-start path in the composed build.
6. Exit-path drift (anything but 60 s hold / one per week / banked
   15-30-45).
7. Data loss on install-over, or any uninstall/clear-data step required.
8. Any lane composed without a CI-proven merged PR.
9. Routines auto-sealing by default without Sarthak's confirmation —
   escalate to Ren as a decision, never silently ship or silently strip.

On kill: leave the tree, record failing gate + evidence + criterion
number on #245 and in the report to Ren. No partial release.
