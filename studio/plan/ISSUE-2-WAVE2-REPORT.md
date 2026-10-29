# ISSUE #2 — WAVE-2 REPORT

**Issue:** Fix the batch-9 test call-sites so the unit tests compile and run
**Repo / branch:** Sarthak-2007/Focus-app · `agent/issue-2`
**Cell lead:** Fuge (Cluster A, flat under Ren)
**Wave-1 base:** commit `8a256a4` (DECLARED, uncompiled, pushed)
**Wave-2 state: DECLARED, uncompiled.** Nothing in this cell was
compiled or run through Gradle, per standing law. Ren compiles only
on Sarthak's explicit word.

---

## 1 · LEAD VERDICT (Fuge)

Wave-1's fix is **correct as far as static evidence can establish**,
and Wave-2's deeper hunt found **nothing hiding behind it**:

- All 5 stale `ParkDrawer` call-sites supply every required named
  argument of the current production signature
  (`ParkDrawer.kt:127–144`, 15 required + defaulted `modifier`) —
  verified independently by three methods: Fuge's own balanced-paren
  parser, the Technical Execution cluster's line-by-line re-reads,
  and the new census script (§3).
- The `StatusBarNotification` call supplies SDK 36's only public
  (10-arg, @Deprecated) constructor form.
- A full static sweep of all 43 test files across four categories —
  (1) test calls vs production signatures, (2) imports that don't
  exist as top-level functions, (3) framework-constructor arity
  under SDK 36, (4) deleted/renamed production symbols — found
  **zero further errors** (Technical Execution + Implementation-
  Quality sections, §5.3).
- The one genuinely open item is unchanged and now labelled in the
  test file itself: the E-1/E-2 import deletion is **UNPROVEN**
  until a compile settles it (Risk 1). Static evidence supports it
  (sibling files call `.assertExists()` as members without those
  imports), but support is not a verdict.

**What remains for this issue to close:** exactly the plan's §5
gate — Ren's compile on Sarthak's word, then the GitHub Actions
run on this branch (which itself waits on issue #3's CI cure: the
test step has never executed in CI because CI dies at signing).

## 2 · WHAT WAVE-2 CHANGED (this branch, on top of 8a256a4)

Comments only in test code — no assertion, argument, or test name
touched:

1. **Premise comments** at the four scaffolding sites that carried a
   bare `true` with no in-file reason (curing Finisher-Light's
   FIX-FIRST): `ScreenshotHarnessTest.kt`,
   `ShortsToggleRenderTest.kt`, and both `TruthCar3SweepTest.kt`
   sites now state: social pair is scaffolding, `true` is the
   persisted default, the toggle is not under test in that file.
2. **UNPROVEN note** in `TruthCar3SweepTest.kt` beside the Compose
   test imports (curing Feel-Instant-Read's recommendation): names
   the member-function bet behind the deleted imports and the exact
   remedy if the compile disagrees (restore both imports, record
   Class E as misdiagnosis).
3. **New tool:** `studio/tools/check_test_callsites.py` — the
   anti-drift census (§3).

## 3 · NEW MECHANISM — THE ANTI-DRIFT CENSUS SCRIPT

Proposed independently by two Wave-2 seats (Crazy-ideas Proposal 1;
Maximum-implementation-potential's Drift Sentinel) and built by
Fuge in this wave:

- `studio/tools/check_test_callsites.py` (Python, stdlib only, no
  Gradle, <5 s): parses every production declaration in
  `app/src/main`, checks every named-arg test call-site against its
  current signature (missing-required and unknown-arg), checks
  `StatusBarNotification` positional arity == 10 (SDK 36), and
  checks the two banned top-level imports stay absent.
- **Proven, not just written:** it prints PASS on the current tree,
  and in negative tests it caught both seeded regressions (a
  removed `socialSitesEnabled` argument; a restored banned import).
  Its own first runs caught three parser bugs in itself (lambda
  arrows misread as generics; comments inside signatures parsing
  as phantom parameters) — those are fixed and documented in the
  file, per TELL-THE-TRUTH.
- Stated limits (in the file): it is not a compiler; type drift,
  overload resolution, and Robolectric runtime behaviour are
  invisible to it. First compile remains the second census.
- Price already paid: ~1 h. Suggested adoption (for Ren): run it
  pre-commit and as an early CI step once issue #3 closes.

Also proposed, not built (recorded for Ren): Signature Fossils
(Crazy-ideas P2), a `ParkDrawerFixture` single-point test fixture
(Maximum-implementation-potential), vendored BOM member-list import
settlement (same seat).

## 4 · CRITIC CHARGES — ACCEPTED, NOT BURIED

Brutal critic (§5.2) + Finisher Typography/Print (§5.1) converge:

- **Charge 1 (accepted):** wave-1's commit message states the
  import deletion as settled fact; the plan in the same commit
  states it UNPROVEN. The permanent record overclaimed. This
  report and the issue comment (§7) name the discrepancy; the
  forward rule (Improver #5) is adopted in this cell: DECLARED
  commits carry UNPROVEN inline for any judgement call.
- **Charge 2 (accepted):** plan §4's word "neutral" was wrong for
  B-1 — `shorts = true` there is a load-bearing premise, as the
  commit's own comment proves. Wave-2's premise comments (§2.1)
  are the correction applied at the remaining sites.
- **Charge 3 (accepted):** "all five" was a grep census, not a
  verdict. Wave-2 answers it with the wider four-category sweep
  (§5.3) — still static, and still labelled as such.
- **Charge 5 (accepted):** wave-1 proposed nothing against
  recurrence. §3 is the answer.
- **Known cosmetic, deferred (Finisher Motion):** in B/C files the
  appended pair is split across the call (value in signature
  position, callback at the tail). Compile-equivalent; normalising
  is a next-pass tidy, not a blocker.

## 5 · SEAT SECTIONS (30 seats: 5 Finishers, 5 Feel, 20 named)

### 5.1 Finishers ×5 + Feel specialists ×5

#### Finisher — Surface
**Verdict: PASS-DECLARED — every stale surface closed, no production surface touched.**
All five `ParkDrawer(` sites now supply every required named argument, spelled exactly as in production: `ScreenshotHarnessTest.kt:333` (social pair at :351-352), `ShortsToggleRenderTest.kt:59` (social pair at :73-74, real `shortsReelsSniperEnabled = storedShorts` at :65 untouched), `SoundAndSocialTruthTest.kt:374` (shorts pair split :380/:389, real social wiring `socialOn` / `vm.toggleBrowserUrlBlocker()` at :382-383 untouched), `TruthCar3SweepTest.kt:301` and `:337` (both pairs). Class D also closed: `TruthCar3CaptureBodyTest.kt:76-80` passes 10 positional arguments in the documented SDK 36 order. Diff touches 5 test files + the plan only — zero production files, zero assertions, zero test names changed.
— *Signed: Finisher (Surface), FUGE cell, Issue #2*

#### Finisher — Motion
**Verdict: PASS-DECLARED, one placement inconsistency flagged (cosmetic, not a compile risk).**
The motion of the fix — append-only, change nothing else — is honoured everywhere, but the append lands differently per file: A-sites append in production order at the tail; B- and C-sites split a pair across the call (`SoundAndSocialTruthTest.kt` value :380, callback :389; `TruthCar3SweepTest.kt` :307 vs :314, :343 vs :350). Named arguments make this compile-equivalent. Recommend (next pass, not a blocker): normalise B/C to value+callback adjacency.
— *Signed: Finisher (Motion), FUGE cell, Issue #2*

#### Finisher — Light
**Verdict: FIX-FIRST (documentation) — the two load-bearing comments are excellent; two sites are left in the dark.**
[Lead note: the two dark sites named here — `ScreenshotHarnessTest.kt` and both `TruthCar3SweepTest.kt` sites — were cured in Wave-2 §2.1 with premise comments after this seat read commit 8a256a4.]
Where a comment was added, it genuinely illuminates: `SoundAndSocialTruthTest.kt:369-373` proves `shortsReelsSniperEnabled = true` is a premise (sniper OFF would print OFF from the first frame and break the pre-toggle assertion — verified against `ParkDrawer.kt:553-559`), and `TruthCar3CaptureBodyTest.kt:70-75` explains the third `0`. But `ScreenshotHarnessTest.kt:351-352` and both `TruthCar3SweepTest.kt` sites added bare `true` / `{}` with no in-file word on why `true` is neutral; in the harness (which renders a fabricated snapshot) plan Risk 3 cannot be dismissed from the file itself.
— *Signed: Finisher (Light), FUGE cell, Issue #2*

#### Finisher — Sound / Haptics
**Verdict: PASS-DECLARED — no sound path disturbed, no-op callbacks are the honest choice.**
No test's sound wiring was touched: `ScreenshotHarnessTest.kt` keeps its real `soundEngine`, `SoundAndSocialTruthTest.kt:381` keeps `vm.soundEngine` beside `soundEnabled = true`, `TruthCar3SweepTest.kt` keeps `soundEngine = null`. Every newly added callback is `{}` — correct finish for toggles not under test. The one real callback in context, `SoundAndSocialTruthTest.kt:383` (`vm.toggleBrowserUrlBlocker()`), predates the fix and is preserved verbatim.
— *Signed: Finisher (Sound/Haptics), FUGE cell, Issue #2*

#### Finisher — Typography / Print
**Verdict: FIX-FIRST (one overclaim in print).**
Code print is consistent: trailing commas correct at every edited tail, indentation and named-argument spelling uniform. The print defect is in the commit message of 8a256a4: it states the deleted imports were "bogus… (member functions, not extensions)" as settled fact, while the plan it commits (§6 Risk 1) requires UNPROVEN until a compile settles it. Static evidence supports the deletion — sibling `CascadeDeckRenderTest.kt` calls `.assertExists()`/`.assertDoesNotExist()` importing only `assertIsDisplayed` — but support is not a verdict. Flagged, not fatal: the plan file inside the same commit states the truth correctly.
— *Signed: Finisher (Typography/Print), FUGE cell, Issue #2*

#### Feel — Weight
**Verdict: PASS-DECLARED.** The whole fix weighs +37/−8 across five files (excluding the plan): two lines per A/B site, four per C site, one argument plus comment for D. Nothing restructured, renamed, or "improved" on the way past. The only perceptible extra weight is the split-pair placement in B/C files.
— *Signed: Feel Specialist (Weight), FUGE cell, Issue #2*

#### Feel — Light
**Verdict: PASS-DECLARED — a failing reader is led to the reason, not just the result, at 3 of 5 sites.**
`ShortsToggleRenderTest.kt:33-38` (new header paragraph) pre-answers why `socialSitesEnabled = true` doesn't pollute the OFF count — verified statically: with `snapshot = null` the social board word is `null` and its sled is a canvas `SledSwitchView` drawing no text, so the OFF count still isolates the sniper word. `SoundAndSocialTruthTest.kt:369-373` and `TruthCar3CaptureBodyTest.kt:70-75` likewise read as explanations, not filler. Dim spots were the un-commented sites (cured in Wave-2 §2.1).
— *Signed: Feel Specialist (Light), FUGE cell, Issue #2*

#### Feel — Timing
**Verdict: PASS-DECLARED.** A reader hits the *why* before the *what* where it counts: comments sit directly above the calls in `SoundAndSocialTruthTest.kt:369` and `TruthCar3CaptureBodyTest.kt:70`. The first compile's timing is not disguised anywhere (plan §5/§6): errors hidden behind the census sites (Risk 2) and the Robolectric runtime half of the SDK 36 constructor (Risk 4) are explicitly future discoveries.
— *Signed: Feel Specialist (Timing), FUGE cell, Issue #2*

#### Feel — Touch + Sound
**Verdict: PASS-DECLARED.** All added callbacks are the same tactile shape — `{}` — at all five sites, so a reader learns once that "this toggle does nothing here". Contrast preserved where touch is real: `SoundAndSocialTruthTest.kt:383` wires the social sled to the ViewModel; `ShortsToggleRenderTest.kt:65` feeds the sniper from `storedShorts`. No added value enables a sled that would fire sound in a render-only test.
— *Signed: Feel Specialist (Touch+Sound), FUGE cell, Issue #2*

#### Feel — Instant-Read
**Verdict: CONDITIONAL PASS-DECLARED.**
In one pass a new reader gets the whole story; the failure signal is clear *if the compile is red for the known reason*: plan §6 Risk 1 predicts the exact shape and remedy. What did not read instantly was the open question from the test file itself — `TruthCar3SweepTest.kt` showed two missing imports and four calls with no in-file note. [Lead note: cured in Wave-2 §2.2 — the UNPROVEN note now sits beside the imports.] The E-1/E-2 import deletion remains the single UNPROVEN judgement; 8a256a4 stays DECLARED until Ren's compile + GitHub Actions agree, per plan §5.
— *Signed: Feel Specialist (Instant-Read), FUGE cell, Issue #2*

### 5.2 The named twenty (part 1: Crazy-ideas → Purpose-questioning)

#### Crazy-ideas — signed: Crazy-ideas, FUGE cell, Issue #2
The compile is the most expensive census instrument in the studio — stop using it as one.
**Proposal 1 — Call-Site Census Script** (spec: parse `ParkDrawer`'s production signature; brace-match every test/main call; diff required-vs-supplied per site in the plan §2 census format; arity table for `StatusBarNotification` (sdk36=10); flag non-allowlisted `androidx.compose.ui.test` imports). Price: ~2h, <1s to run, 0 Gradle. [Lead note: BUILT in Wave-2 in generalised form — §3, `studio/tools/check_test_callsites.py`.]
**Proposal 2 — Signature Fossil:** on every production composable change, the lane commits a one-line `studio/signatures/<Name>.sig`; diverging lanes conflict at merge time, before test generations diverge. Price: 10 lines of script + a habit.

#### Maximum-implementation-potential — signed: Maximum-implementation-potential, FUGE cell, Issue #2
Push the fix to its ceiling: make this *class* of failure structurally impossible. **Drift Sentinel** (extractor producing signatures for every `@Composable` + hand-built framework constructors; checker verifying required/unknown named args, positional arity, and imports against a vendored Compose BOM member list — settling E-type disputes on paper). **Test-fixture builder:** one `ParkDrawerFixture.kt` in the test source set with the neutral/premise defaults in one place; future production parameters are added in exactly one test file. Respects plan §1: no defaults in production. Price: Sentinel ~300 lines + BOM list, one afternoon; fixture ~60 lines + one more touch of the 5 sites (DECLARED, same gate).

#### Brutal critic — signed: Brutal critic, FUGE cell, Issue #2
**Charge 1** — Commit `8a256a4`'s message states as fact what plan §6 Risk 1 states as unproven (the import deletion). Both cannot be true; TELL-THE-TRUTH fails at the commit-message layer. **Charge 2** — "Neutral values" was wrong: the commit's own comment in `SoundAndSocialTruthTest.kt` proves `shorts = true` is a load-bearing premise, filed in plan §4 as "scaffolding". **Charge 3** — "All five" is a grep, not a verdict (plan §2's own census limit). **Charge 4** — Count fog: issue says +20/−8, clone diff +24/−8, commit stat +446/−8 of which 409 lines are the plan itself. **Charge 5** — The system: five sites went stale because two lanes each added a required pair and no lane owned the test source set; wave-1's 409-line plan proposed nothing that stops batch-10 repeating it. What survives attack: scope discipline (§1) is correct and was held; the four-leg acceptance bar (§5) is honest; Risk 1's plan text is a model of declared uncertainty — which is exactly why Charge 1 lands.
[Lead responses filed in §4 of this report.]

#### Improver — signed: Improver, FUGE cell, Issue #2
1. Re-label premises at all sites [done, §2.1]. 2. Name the E-1/E-2 settlement in advance on the issue [done in §7 comment text]. 3. Letter any new compile error (Class F+) before fixing it — no silent second-round fixes [adopted as cell rule]. 4. Adopt the census script as plan addendum [done, §3]. 5. Future DECLARED commits carry UNPROVEN inline for judgement calls [adopted].

#### Designer — signed: Designer, FUGE cell, Issue #2
This issue's design surface is text a tired developer reads at 03:00. The census table (§2 of the plan) is the best-designed artefact in the cell — keep the format as studio template. Premise comments are typography for reasoning, set exactly where the eye lands. One flaw: a 409-line plan carrying a 20-line fix buries the fix; a one-page head sheet with risks as annex would serve the gate reader better. No green, no images, no production UI — palette/font law correctly not forced onto a test-only issue.

#### Consumer — signed: Consumer, FUGE cell, Issue #2
The app shipped (batch-9) but its receipt didn't: the consumer holds a build whose unit suite has never run green in this generation. Wave-1's real delivery is the refusal to call the build verified until two witnesses (Ren's compile, GitHub Actions) agree. The cost is delay concentrated at the gate; until that word, every issue leaning on the suite (#5 above all) is consuming credit, not proof.

#### Student — signed: Student, FUGE cell, Issue #2
A clean lesson in three ideas: (1) interface drift — required parameters added in independent lanes broke every older-generation caller, silently until compile; (2) platform drift is the same disease — `StatusBarNotification` 9→10 args under SDK 36; (3) an unresolvable import is an error, an unused one only a warning — the E-1/E-2 question turns on that plus whether `assertExists` is a member of `SemanticsNodeInteraction` in BOM 2024.09.00. The honest gap — only a compile resolves Risk 1 — is itself the lesson: know which questions are empirical.

#### User — signed: User, FUGE cell, Issue #2
I never see a test compile error — I see its shadow: batch-10 can't be honestly assembled on a red suite, and Truth Car 3's claims sit unverified on my actual blocker. Wave-1 did the one thing I need: it did not touch production (no defaults on `ParkDrawer` to appease stale tests), so the app I run is unchanged while its paperwork is repaired. Keep the gate; when I say build, I want §5's suite counts posted, not a thumbs-up.

#### User-experience — signed: User-experience, FUGE cell, Issue #2
Before wave-1, failure was archaeology: six error sites in compiler order, interleaved. After: a checklist — census, per-file appends, a four-leg acceptance bar, exactly one flagged unknown. Two frictions remain: (1) verdict latency — DECLARED work sits inert until an external gate fires; the census script is precisely a latency fix, a <1s near-verdict on demand; (2) the commit-message overclaim is a UX defect for the future reader. Fix the label, cut the latency, and this issue goes from exemplary-on-paper to exemplary end-to-end.

#### Purpose-questioning — signed: Purpose-questioning, FUGE cell, Issue #2
Why fix tests for a drawer? (1) Instrumental: the suite is the studio's only machine-checkable truth surface; a suite that doesn't compile proves nothing, so every other issue's verification inherits this red. (2) Constitutive: the cheap fix — production defaults — was refused because it would make the code lie about its own contract; the purpose of this issue is that refusal, not the ten appended lines. (3) The uncomfortable one: is a 409-line plan + a wave-2 programme proportionate to +24/−8? Only if the recurrence mechanisms (census script, fossils, fixture) get adopted — otherwise the studio built a cathedral around a pothole it will step in again on batch-10. Verdict: the repair is justified; the ceremony is justified only as the last time this failure is repaired by hand.

### 5.3 The named twenty (part 2: Thoughtful → Interaction-detail) — the technical hunt

#### Thoughtful — signed: Thoughtful, FUGE cell, Issue #2
The fix's quiet virtue is that it preserves *why* each test exists. Where a value is load-bearing, a premise comment says so — a future reader who "tidies" that `true` to `false` meets the warning before the failure. No assertion, expected string, or test name changed anywhere: the tests still mean exactly what they meant; they can simply be heard again.

#### Deep-thinker — signed: Deep-thinker, FUGE cell, Issue #2
The root cause is generational: three lanes wrote tests against three generations of `ParkDrawer`, and platform code drifted the same way under `StatusBarNotification`. Name-and-arity sweeps cannot see *type* drift (an `Int` where a `Long` now sits, a reordered same-type positional pair). The honest close remains plan §5: first compile = second census; any new error lettered into the census before it is fixed, never fixed silently.

#### Enthusiastic designer — signed: Enthusiastic designer, FUGE cell, Issue #2
What this issue unblocks is pictures. Two repaired sites live in render harnesses (`ScreenshotHarnessTest.kt:333`, `ShortsToggleRenderTest.kt:59`) and PARK is the surface Sarthak touches daily. A test source set that doesn't compile produces no frames at all; now every drawer call-site speaks the full current signature, including the social-sites row — the newest piece of the design is finally inside the render evidence.

#### Wow-level designer — signed: Wow-level designer, FUGE cell, Issue #2
The wow is invisible when it works: the drawer's three sleds — sound, sniper, social — render in tests exactly as the composed production signature demands, no defaults smuggled in. `ShortsToggleRenderTest`'s OFF-count premise was re-proven for the new social row (null snapshot → null board word; chrome sled draws no text): the count still isolates one sled. A render test whose premise is proven in its own header is the standard every other render test should be held to.

#### Designer-29-years — signed: Designer with 29 years of experience, FUGE cell, Issue #2
Two rules this commit obeys: **never bend the production contract to appease stale tests** (defaults on `ParkDrawer` would have hidden which hosts actually wire the toggles), and a bare `0` inserted into a positional constructor call carries a comment naming what it is (`score`, SDK 36's tenth argument) and why. Keep both habits; they are the whole craft at this scale.

#### Intentionality-focused designer — signed: Intentionality-focused designer, FUGE cell, Issue #2
Every added value was *chosen* and is auditable: scaffolding toggles get persisted defaults where unexercised; real wiring is untouched where a toggle is exercised (`socialOn` → `vm.toggleBrowserUrlBlocker()`; sniper from `storedShorts`). Nothing defaulted in production, nothing randomised, no assertion loosened. Each line can answer "why this value, here?" — and where the answer is subtle, a premise comment answers in advance.

#### Synchronous collaboration — signed: Synchronous collaboration, FUGE cell, Issue #2
Wave-1's plan carried census, fix map, and the one named open risk, so Wave-2 seats could audit a finished commit instead of re-deriving it. The two-witness close (Ren's gated compile, then the Actions run on the pushed branch) remains the single synchronisation point — until both speak, every verdict here, including "none found", is DECLARED, not proven. No seat messaged Sarthak; this report goes to Ren only.

#### Implementation-quality — signed: Implementation-quality, FUGE cell, Issue #2
Scope: all 43 test files, all 49 production files, plus `androidTest`. Per category: (1) test calls vs signatures — none found beyond the 5 fixed sites; (2) imports — every `com.example.*` import resolves (0 missing); Compose UI-test imports audited one by one (`assertCountEquals`, `assertIsDisplayed`, `performClick`, `performTextInput`, `performTouchInput`, `click`, `onNodeWithText/Tag`, `onAllNodesWithText` are genuine top-level extensions — **not** the same disease as the deleted member-function imports, which stand as the only member-import case in the suite); (3) framework constructors under SDK 36 — none beyond the fixed `StatusBarNotification`; (4) deleted/renamed symbols — none; all `dao.`/`vm.`/`repository.` receiver calls resolve, including `repository.breakBoundaryKnock` (`internal var`, `MonolithRepository.kt:1827`, visible to tests as friend module). Residual blind spots stated plainly: argument *type* mismatches, overload resolution, Room/kapt-generated code — not decidable by this sweep; they belong to the gated compile.

#### Technical execution specialist — signed: Technical execution specialist, FUGE cell, Issue #2
**(1) Call-sites — NONE FOUND beyond the 5 fixed.** All 5 `ParkDrawer` sites pass all 15 required arguments by name. Every other production composable called from tests checked the same way: `TactileDeckScreen` at `CascadeDeckRenderTest.kt:88–137` — clean (read line-by-line; automated parsers truncate at nested-lambda defaults and falsely flag this site); `CapturedNotificationsDigestSheet` (`AirlockAndDisarmOverlays.kt:297`) at `DrawerRenderTest.kt:293` — clean; `ReceiptSheet` (`ReceiptSheet.kt:56`) at `ScreenshotHarnessTest.kt:626` — clean; `MonolithRootApp` (`MainActivity.kt:156`) at two sites — clean; `MyApplicationTheme` at 8 sites — trailing lambda supplies `content`, clean; `ShieldMatrixScreen`/`DrawerScreen`/`FocusSequencerScreen`/`ShieldCapSelector` — zero test call-sites exist. Data-class constructors checked (entities, `Warrant` at `WatchStore.kt`) — clean. Look-alikes cleared by reading: `controller.latch()` (test-local, `ArmProvingControllerTest.kt:187`); `derive(...)` (test-local wrapper, `RoutineDeriveTest.kt:75–81`); `insertSession(...)` (test-local DAO wrapper); `matchBlockedDomain(urlText, domains)` both positional args supplied; `TestScope.tick` test-defined.
**(2) Non-existent top-level imports — NONE FOUND** (see Implementation-quality).
**(3) Framework constructors vs SDK 36 — NONE FOUND beyond the fixed site.** Fixed call now supplies 10 positional args matching SDK 36's only public (@Deprecated) constructor. All other framework constructions use arity-stable forms (`ResolveInfo()`/`ActivityInfo()` no-arg, `Intent(ACTION_MAIN, null)`, `Notification.Builder(context, "truth3")`, `Canvas(bitmap)`, `MotionEvent.obtain` 6-arg). Build config: compileSdk 36.1, targetSdk 36, Compose BOM 2024.09.00.
**(4) Deleted/renamed symbols — NONE FOUND.** Zero `com.example.*` test imports fail to resolve; zero enum/object member references absent from production.
**Verdict: no further compile errors found by static sweep in any of the four categories.** Static only — the first gated compile remains the second census (plan §6 Risk 2).

#### Interaction-detail specialist — signed: Interaction-detail specialist, FUGE cell, Issue #2
The repaired sites are precisely the interaction-detail tests: `ShortsToggleRenderTest` (sniper sled prints its stored fact — OFF → exactly one "OFF" node; ON → zero) and `SoundAndSocialTruthTest` (social sled driven through `performTouchInput { click(center) }`, using the genuine top-level `click` extension). Because these tests never compiled, the sled's throw-and-print behaviour — the one interaction that must never lie about its state — had no running witness. It has one again, on paper. The social sled being a canvas view that draws no text is exactly why the OFF-count isolation holds.

## 6 · DECISIONS

- **DECISION-FOR-SARTHAK:** none in this issue. The closing gate is
  his standing build word, not a new decision.
- **For Ren:** (a) adopt the census script into the pre-commit / CI
  flow; (b) rule on Signature Fossils + `ParkDrawerFixture`
  proposals (priced in §5.2); (c) when the first compile runs,
  letter any new error as Class F+ before fixing (Improver #3).

## 7 · GITHUB COMMENT — EXACT TEXT (posting status in the lead's final report to Ren)

```text
Wave-2 on #2 — Fuge (Cluster A lead), reporting to Ren.

State: DECLARED, uncompiled (standing law — Ren compiles on
Sarthak's word). On top of wave-1's 8a256a4:

1. DEEPER HUNT, CLEAN. A static sweep of all 43 test files in four
   categories (call-sites vs production signatures; imports that
   aren't top-level functions; framework-constructor arity under
   SDK 36; deleted/renamed symbols) found NO further compile
   errors behind the five fixed sites. The 5 ParkDrawer sites pass
   all 15 required arguments by name; every com.example import
   resolves; the StatusBarNotification call matches SDK 36's only
   public 10-arg constructor. Blind spots stated: type drift and
   overload resolution are invisible to static sweeps — the first
   compile is the second census, and any new error gets lettered
   (Class F+) before it is fixed.

2. ANTI-DRIFT CENSUS, BUILT. studio/tools/check_test_callsites.py
   (stdlib-only, no Gradle) re-checks all of the above in seconds.
   It prints PASS on this branch and, in negative tests, caught
   both seeded regressions (a removed argument; a restored banned
   import). Proposed to Crazy-ideas and Max-impl independently;
   built here.

3. PAPERWORK CURED. Premise comments added at the four sites that
   carried a bare `true` with no in-file reason; an UNPROVEN note
   now sits in TruthCar3SweepTest naming the one open judgement:
   the deleted assertExists/assertDoesNotExist imports are member
   functions, not extensions — supported by sibling files, NOT
   proven until compile. Wave-1's commit message overstated this
   as settled; the plan in the same commit had it right, and this
   comment is the correction on the issue itself.

Report: studio/plan/ISSUE-2-WAVE2-REPORT.md (30 seats).
What remains: Ren's compile + the Actions run (which waits on
#3's signing cure). Nothing here needs Sarthak except his
standing build word.

— Fuge
```

## 8 · WHAT FUGE WANTS TO DO NEXT (Cluster A, issue #2 side)

- Sit in on the first compile as census-taker: letter every error
  it surfaces (Class F+) against this report's "none found" claim,
  so the static sweep's accuracy becomes a measured number.
- Build the `ParkDrawerFixture` + Signature Fossil prototypes on
  this branch (DECLARED) if Ren adopts them, so batch-10 is the
  first batch that cannot drift silently.

— Fuge, Cluster A lead, Oct 30 2026 (Wave-2)
