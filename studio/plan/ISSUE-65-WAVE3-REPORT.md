# ISSUE-65 — Wave-3 Report (Audit R1 · tests-1) — seat-bundle, cluster C9 (lead: NACHLASS)

Branch: `agent/issue-65` · Base: origin/main e29acdf
Scope note: **all changes are test-internal; zero production/app effect** (no file under `app/src/main` touched).

## Dispositions

1. **[HIGH] Seal-ordering circular proof — FIXED-IN-CODE.** FakeTransition deleted from
   ArmProvingControllerTest; the controller now runs the REAL `SessionSealTransitionImpl` +
   `TactileSoundEngine`. New `SessionSealTransitionProductionTest` pins production ordering from
   inside the callbacks: marker absent inside sealRow, marker committed inside publish, Throw
   waveform (SEAL_THROW_TIMINGS on the vibrator shadow) absent inside both and present after —
   row → marker → publish → Throw. Refusal case: no marker/publish/Throw, claim stays spent.
2. **[MEDIUM] Reconcile LOCKED/OPEN defaults unasserted — FIXED-IN-CODE.**
   New `reconcile_categoryDefaults_shortVideoLocked_fallbackOpen_noPreset`: preset-free
   SHORT_VIDEO → LOCKED, GAMES → LOCKED, SHOPPING (else) → OPEN/not-blocked.
3. **[MEDIUM] Empty-query test indistinguishable from silent sync failure — FIXED-IN-CODE.**
   Control phase proves a live sync discovers a control app in the same harness; the empty phase
   runs through a query-counting empty PackageManager — failure-before-query fails the test,
   a missing guard (prune-all) fails the test; only a working guard passes.
4. **[MEDIUM] Fake clocks/witnesses, production wiring nowhere — FIXED-IN-CODE.**
   New `ArmProductionWiringTest`: real RepositoryArmSessionPort vs real DB (birth/snapshot/
   re-anchor/annul), real LiveArmWitnesses vs real service statics (keyed, death = null),
   SessionSealTransitions singleton identity, and a full controller seal using production
   collaborators + the DEFAULT SystemClock clock (fakes only: vsync poster, test scheduler).
5. **[LOW] groupedByRule count tautology — FIXED-IN-CODE (replaced, not dropped).**
   `peekCount`/`shutCount` are `get() = list.size` over the lists already asserted — they could
   only agree with themselves. Both sites (AppCatalogReconcileTest, AppListTruthTest) now assert
   content the counts never touched: packages, tiers, and OPEN/third-app exclusion. Reasoning
   recorded in-code at both sites and in the issue body.

## Test-compile census overlap
Issue-2 census script (`studio/tools/check_test_callsites.py` on `agent/issue-2`) run on this
tree: **8 findings, all in the 5 out-of-scope files** (ScreenshotHarnessTest,
ShortsToggleRenderTest, SoundAndSocialTruthTest, TruthCar3CaptureBodyTest, TruthCar3SweepTest) —
**zero in tests-1 scope**. Those 5 are FIXED-ELSEWHERE: already fixed on `agent/issue-2`;
remainder owned by issue-66's bundle. Not duplicated here.

## Seat verdicts (20/20 — none dropped)

- **Crazy-ideas** — Proved Throw-last by eavesdropping on the vibrator shadow instead of inventing another fake event; the haptic IS the receipt. — Crazy-ideas
- **Maximum-implementation-potential** — Every seam the production API itself offers is now driven for real; nothing mockable remains mocked except vsync and the scheduler. — Maximum-implementation-potential
- **Brutal critic** — The old suite was theatre: a fake agreeing with itself, counts agreeing with themselves. This pass kills the theatre; if CI shows the wiring seal flaky, I will say so, not soften it. — Brutal critic
- **Improver** — Counts replaced by package+tier+exclusion content assertions — strictly stronger than what was removed, recorded in-code so nobody re-adds the padding. — Improver
- **Designer** — LEAD-COVERED: no visual/design surface in this issue; test-internal only. — Designer
- **Consumer** — As the person who'd trust "sealed" on screen: the word now rests on a proof against the real seal act, not a puppet of it. — Consumer
- **Student** — The distinguishing-control pattern (prove the pipeline alive, then trust its silence) is the lesson this issue teaches every future guard test. — Student
- **User** — Zero app change, zero behaviour change; my phone sees nothing different — and that's stated plainly, not buried. — User
- **User-experience** — LEAD-COVERED: no UX surface touched; the seal's feel (Throw) is now at least proven to fire last, which is the UX fact underneath. — User-experience
- **Purpose-questioning** — Asked of every assertion: "what production lie would this catch?" The ones that caught none were replaced, the rest earned their place. — Purpose-questioning
- **Thoughtful** — The refusal path got equal billing with the happy path — a seal that cannot land must be provably silent. — Thoughtful
- **Deep-thinker** — Circularity is the deep failure mode of test suites; anchoring to production callbacks + a hardware shadow breaks the circle at two independent points. — Deep-thinker
- **Enthusiastic designer** — LEAD-COVERED: no design work in scope; enthusiasm redirected to the waveform-as-evidence trick. — Enthusiastic designer
- **Wow-level designer** — LEAD-COVERED: nothing to render here; the wow is a test that would actually catch the app lying about a seal. — Wow-level designer
- **Designer-29-years** — LEAD-COVERED: three decades say the same thing — delete the fake that duplicates the system under test; done. — Designer-29-years
- **Intentionality designer** — Every remaining fake is named with its boundary (port, witnesses, clock, vsync) — intentional, not habitual. — Intentionality designer
- **Synchronous collaboration** — Census overlap with #2/#66 was checked, not assumed; their files were left untouched and dispositioned with branch names. — Synchronous collaboration
- **Implementation-quality** — Bounded pump in the wiring seal, deterministic controls elsewhere; no assertion weakened anywhere in this diff. — Implementation-quality
- **Technical execution specialist** — Real impl + real engine + real DB under Robolectric SDK 36 is the narrowest honest seam; CI is the compile court and will judge it. — Technical execution specialist
- **Interaction-detail specialist** — The detail that matters: Throw AFTER publish, verified from inside publish — a user must never hear a seal for a state not yet true. — Interaction-detail specialist
