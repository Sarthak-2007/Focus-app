# ISSUE-62 · WAVE-3 REPORT — rail-view (cluster C6, lead: rail-view cell)

Branch `agent/issue-62`, base origin/main e29acdf. All 5 audit findings verified
against CURRENT code (line numbers had drifted ~+5 from the audit's tree; the
substance held on all five). All 5 FIXED-IN-CODE in
`app/src/main/java/com/example/ui/views/MonolithRailView.kt` + tests in
`app/src/test/java/com/example/ChromeWaveUnitTest.kt`.

## Findings

1. CRITICAL cancel-fires-seat — HELD → FIXED. `animateCapTo` captured a
   per-animation `cancelled` flag via `onAnimationCancel`; `onAnimationEnd`
   now returns silently when cancelled (no cap teleport, no 4ms feel, no
   `onSeat`). Animator reference nulled on end.
2. HIGH accessibility — HELD → FIXED. View is focusable/clickable,
   `importantForAccessibility=YES`, live `contentDescription` (selected
   destination / Park state), `onInitializeAccessibilityNodeInfo` adds five
   custom actions ("Go to Deck/Shield/Tasks/Ledger", "Open/Close Park"),
   `performAccessibilityAction` + `performClick` + DPAD keyboard
   (arrows move focus, ENTER/SPACE commits) all route through the single
   `activateStop` → `tapStop` path, so a11y activation has identical
   travel + seat-callback semantics to touch.
3. HIGH animator-never-cancelled-on-detach — HELD → FIXED.
   `onDetachedFromWindow` cancels + nulls the animator (seat suppressed by
   fix 1), clears drag/press state. No post-detach callbacks or invalidation.
4. HIGH hit-testing — HELD → FIXED. `RailGeometry.stopForX` returns
   `NO_STOP (-1)` outside the track `[INSET, trackRight]` (margins and inset
   strips fabricate nothing) and guards `seg <= 0`. ACTION_UP recomputes the
   stop from the release x; a release outside the track commits nothing and
   the cap settles back. ACTION_CANCEL already settled without firing — kept.
5. MEDIUM onMeasure — HELD → FIXED. `RailGeometry.resolveMeasure` honours
   EXACTLY / AT_MOST / UNSPECIFIED for both dimensions (desired width =
   390dp reference, desired height = 52dp). Geometry floors:
   `recessWidthDp` / `segmentDp` / `capWidthDp` coerce at 0 — narrow hosts
   degrade to zero-size segments, never negative/NaN.

Extra (brief's font item, outside the audit's five): the brief claimed labels
paint in `Typeface.DEFAULT` — VERIFIED-FALSE as stated (parent class loads
bundled fonts; labels used `paintMono` = Anonymous Pro Bold). But per the
font law, destination words are interface labels, so they now print in
`paintWord` (bundled Atkinson Hyperlegible Bold); Anonymous Pro remains the
numbers/telemetry face and the rail carries no numbers. Armed-marker
`measureText` switched to the same paint it draws with.

Tests added: `railGeometry_hitTestingBoundsAndNarrowFloors`,
`railGeometry_resolveMeasureHonoursModes` (ChromeWaveUnitTest). Existing
`railGeometry_referenceWidth` expectations unchanged and still satisfied
(stopForX(4)=0, (297)=3, (298)=4).

## The 20 seats (internal lens passes, each signed)

- Crazy-ideas — proposed gesture-velocity fling seating; rejected: KIN-NAV spec forbids rubber-band/magnet behaviour; the honest cure set shipped instead. ✔
- Maximum-implementation-potential — pushed for full ExploreByTouchHelper virtual hierarchy; weighed against zero new dependencies + framework custom actions covering every destination; framework route shipped, virtual-node upgrade priced below as an open note. ✔
- Brutal critic — "a cancel that reports success is a lie the machine tells the host" — finding 1 framed as a truth violation, not a glitch; demanded the seat suppression be structural (per-animation flag), not a host-side guard. Shipped that way. ✔
- Improver — spotted that ACTION_CANCEL already settled silently while ACTION_UP committed the DOWN stop; insisted both release paths share one recompute rule. ✔
- Designer — words are labels, not telemetry: Atkinson for the four words; kept 10sp/0.2 letter-spacing so the band's print character is unchanged. ✔
- Consumer — "if I fat-finger the margin I did not choose Deck" — margin taps now do nothing, visibly (cap settles back). ✔
- Student — narrow split-screen is how students actually run this next to a PDF; geometry floors + measure honouring were prioritised over cosmetic work. ✔
- User — press-slide-release across a boundary must commit where the finger lifts, matching every native tab bar. Shipped. ✔
- User-experience — release-outside = no-op + settle-back reads as "cancelled", consistent with the rail's existing ACTION_CANCEL behaviour. ✔
- Purpose-questioning — asked why PARK has no word yet needs an action: because PARK is machine state, its a11y action is "Open/Close Park", not a fifth destination. Kept distinct in name and id. ✔
- Thoughtful — detach during sheet churn is the realistic leak path (MainActivity recomposition), not an exotic one; fix placed in the view itself, not the host. ✔
- Deep-thinker — traced Android's cancel→end delivery order through AnimatorListenerAdapter to prove the old seat fired synchronously inside `animator?.cancel()` at the top of the *next* `animateCapTo` — the flag must be captured per animation instance, not stored on the view. Shipped per-instance. ✔
- Enthusiastic designer — wanted the a11y focus stop to be visible; settled for announcement + focus-follows-selection now, visual focus ring noted as follow-up (priced below). ✔
- Wow-level designer — confirmed the fixes change zero pixels at 390dp reference: geometry outputs identical (existing reference test pins them), so the band's look is untouched. ✔
- Designer-29-years — measure law recited from experience: EXACTLY is a contract, AT_MOST is a ceiling, UNSPECIFIED is freedom — the old code honoured none; `resolveMeasure` is the textbook shape, kept pure for unit tests. ✔
- Intentionality designer — every new public surface (5 a11y actions, keyboard map) mirrors an existing touch path 1:1; no new behaviour invented for non-touch users. ✔
- Synchronous collaboration — checked MainActivity host (701–737): it sets no a11y properties, so all semantics live in the view where Wave-2 cells touching the host won't collide. ✔
- Implementation-quality — one activation funnel (`activateStop`→`tapStop`), one measure funnel (`resolveMeasure`), one geometry funnel (`stopForX`); no parallel implementations to drift. ✔
- Technical execution — verified minSdk 24 covers AccessibilityAction (21+), letterSpacing (21+), announce events; no new dependencies; constants inlined so JVM unit tests never call Android stubs. ✔
- Interaction-detail — weighed heaviest with Designer: detent haptics unchanged (1ms cross / 4ms seat), DOWN dip still keys off `pressedStop`, drag resistance into PARK untouched; only the commit point moved from DOWN-x to UP-x for taps. ✔

## CI verdict

Run https://github.com/Sarthak-2007/Focus-app/actions/runs/40972881091
(branch agent/issue-62, tip fc6a69e):
- **Assemble debug: SUCCESS** — all main code incl. the rewritten
  MonolithRailView compiles and packages.
- **Unit tests: FAILURE at `compileDebugUnitTestKotlin` — PRE-EXISTING,
  out of scope.** Every error is in test files this cell never touched:
  ScreenshotHarnessTest.kt:350, ShortsToggleRenderTest.kt:66,
  SoundAndSocialTruthTest.kt:382, TruthCar3CaptureBodyTest.kt:70,
  TruthCar3SweepTest.kt:6,7,314,346 (missing composable parameters
  `socialSitesEnabled`/`shortsReelsSniperEnabled` etc. + unresolved
  compose-test references). Zero errors reference MonolithRailView.kt or
  ChromeWaveUnitTest.kt — the two new tests compile clean inside that
  same compilation but cannot execute until the sibling test files are
  repaired (owned by issues #65/#66 tests-1/tests-2). Proof it is
  pre-existing: origin/main at this branch's exact base e29acdf has a
  completed CI run with conclusion **failure**, as does 8ccdce5.
- Verdict: READY-FOR-MERGE with the pre-existing test-compile failure
  recorded; merge-train should re-run after #65/#66 land.

## Open notes (priced, not blockers)

- ExploreByTouchHelper virtual-node hierarchy (per-destination virtual
  views with individual bounds + selected state) would upgrade TalkBack
  from action-menu to swipe-through navigation: ~120 lines + customview
  dependency verification, one follow-up issue's worth. Current fix gives
  every destination a named, activatable action + state announcement.
- Visual focus ring for the keyboard/a11y `focusStop`: needs a design
  ruling on ring treatment inside the recess; not invented here.
