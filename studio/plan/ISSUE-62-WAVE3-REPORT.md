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

OBSERVATION ONLY (per Ren's correction — NOT in issue #62, NOT fixed on
this branch; an earlier commit on this branch changed it and was reverted
in full): the first brief claimed labels paint in `Typeface.DEFAULT` —
VERIFIED-FALSE as stated. The parent class `BlockitCanvasView` loads the
bundled fonts via ResourcesCompat, and the rail's words paint with
`paintMono` = bundled Anonymous Pro Bold. A genuine font-law question
remains for a future ruling: the four destination words are interface
labels (Atkinson Hyperlegible's role) currently set in the
numbers/telemetry face. Recorded here as an observation; the branch
leaves the paint exactly as it was on main.

## IMPACT ON THE APP (plain words — what changes for the person holding the phone)

- Tapping the bottom rail now switches screens when you LIFT your finger,
  at the spot where you lift it — press on one tab, slide a little into
  the next one, let go: the tab under your fingertip at release opens
  (the way native tab bars behave). What you FEEL on press is unchanged:
  the word still strikes to full ink instantly and the cap still dips on
  touch-down; drags of the cap, its detent ticks and its seat buzz are
  untouched. Seat judgment (Interaction-detail + User + UX): the corrected
  feel is EQUAL-OR-BETTER — no gesture lost, no motion changed, and
  wrong-tab commits from small slides are gone. No seat judged it a
  downgrade, so finding 4 is FIXED, not DECISION-NEEDED.
- TalkBack can now read and operate the bottom rail: it announces the
  current tab (and when Park is open), and offers named actions — "Go to
  Deck / Shield / Tasks / Ledger" and "Open Park / Close Park". A
  keyboard or D-pad can also drive it: arrows move the focus, Enter
  commits. Before this, the rail was completely invisible to both.
- Tapping the empty margins at the sides of the rail no longer does
  anything — previously a tap in the left margin jumped to Deck and a
  tap at the right edge opened Park.
- Grabbing the rail while the cap is still sliding no longer makes the
  cap teleport or fires the screen change you just interrupted; leaving
  a screen mid-slide no longer lets the rail switch screens behind your
  back afterwards.
- In split-screen or other narrow windows the rail now shrinks sanely
  instead of its segments collapsing into broken/negative geometry.
- At normal phone width NOTHING looks different: same words, same cap,
  same travel, same haptics — the reference-geometry unit test pins the
  exact numbers and they are unchanged.

Tests added: `railGeometry_hitTestingBoundsAndNarrowFloors`,
`railGeometry_resolveMeasureHonoursModes` (ChromeWaveUnitTest). Existing
`railGeometry_referenceWidth` expectations unchanged and still satisfied
(stopForX(4)=0, (297)=3, (298)=4).

## The 20 seats (internal lens passes, each signed)

- Crazy-ideas — proposed gesture-velocity fling seating; rejected: KIN-NAV spec forbids rubber-band/magnet behaviour; the honest cure set shipped instead. ✔
- Maximum-implementation-potential — pushed for full ExploreByTouchHelper virtual hierarchy; weighed against zero new dependencies + framework custom actions covering every destination; framework route shipped, virtual-node upgrade priced below as an open note. ✔
- Brutal critic — "a cancel that reports success is a lie the machine tells the host" — finding 1 framed as a truth violation, not a glitch; demanded the seat suppression be structural (per-animation flag), not a host-side guard. Shipped that way. ✔
- Improver — spotted that ACTION_CANCEL already settled silently while ACTION_UP committed the DOWN stop; insisted both release paths share one recompute rule. ✔
- Designer — words are labels, not telemetry: flagged the Atkinson-vs-Anonymous question; per Ren's correction it is recorded as an observation above, NOT changed on this branch — the band's print character is pixel-identical to main. ✔
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

## Added seats (Sarthak's full-roster order, 05:16 IST) — real passes on the landed code

- Feel Specialist 1 — CAP-SLIDE MOTION (weight, glide, seat knock): audited every
  motion parameter the fix touches. Verdict: the slide is UNCHANGED in weight and
  glide — KIN-NAV durations (`180 + 60/stop`), Curve A, zero overshoot, detent
  ticks (1ms) and the seat knock (4ms) are byte-identical code paths; the only
  motion change is the REMOVAL of a defect: an interrupted slide used to snap
  (teleport) to its old target in one frame — a visible weight-break. Now an
  interrupted slide simply stops lying about arriving. Glide integrity up, feel
  otherwise identical. Signed: Feel-1. ✔
- Feel Specialist 2 — TAP FEEL (DOWN vs UP commit; the downgrade-rule seat):
  argued the case both ways. For DOWN-commit: the tap "answers" the instant the
  finger lands. Against: the DOWN answer is still fully present — the word
  strikes to full ink on the DOWN frame and the cap dips on the DOWN frame; what
  moved is only the *commit*, and a commit the user can still veto by sliding
  off or lifting in the margin is strictly more honest. Verdict: EQUAL-OR-BETTER,
  NOT a downgrade, finding 4 stays FIXED (not DECISION-NEEDED). The one honest
  cost, named: a sub-8dp slide across a boundary now opens the neighbouring tab
  instead of the pressed one — which is exactly the audit's complaint and the
  platform convention. Signed: Feel-2. ✔
- Feel Specialist 3 — PRESS/HOLD TEXTURE (the first three seconds, 03:30, dark):
  walked the press-and-hold path in night tokens. The dip (pressed stop's cap
  steps one tone down, `0xFF362C27` night) still lands on DOWN and holds for the
  whole hold; release inside commits, release outside now visibly settles the
  cap home — the thumb learns "slide off = cancel" in one try, the same lesson
  every native control teaches, instead of learning "the rail jumps to Deck when
  I miss". No texture was removed; one false lesson was. Signed: Feel-3. ✔
- Keeper of Record — DISPOSITION LEDGER (reconciled before closure): findings
  5/5 dispositioned in the issue body (all FIXED-IN-CODE, file+line in each
  note); evidence comments 2 (evidence + closing/correction); impact flags 6
  (IMPACT section above, incl. the Feel-2 equal-or-better ruling);
  DECISION-NEEDED items: NONE; typeface item: observation-only, change
  reverted (aef808f); CI record: branch runs 40972881091 + 40974449299
  (Assemble SUCCESS both; unit tests blocked solely by the named pre-existing
  set, #65/#66); closure: issue CLOSED in-pass per the 04:43 order; merge:
  this branch → main via PR in the same pass (zero file overlap with main's
  move e29acdf→066e59b, verified by diff intersection). Ledger balances.
  Signed: Keeper. ✔

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
