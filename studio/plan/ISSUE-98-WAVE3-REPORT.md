# Issue #98 — Wave 3 Report (Journey: ARMING-A-SESSION, AUDIT-R3)

Lead: #98 bundle lead under WEG, Wave-3. Base: origin/main @ ed91188
(Merge PR #179). Branch: agent/issue-98. All 5 findings verified
against CURRENT origin/main code before any fix (file + line evidence
below) — none were cured by Wave-2 #29 arm-proving or #7 shield work;
those lanes built the cascade this journey audits, they did not close
its truth gaps. Result: **5 FIXED-IN-CODE, 0 verified-false,
0 stands-blocked.**

## Dispositions

### 1. [HIGH] Session-length multiplier invisible — FIXED-IN-CODE
Verified: `TactileDeckScreen.kt:325` totalSpanMinutes = dial×blocks +
(n−1)×10; hero (~642–650) printed only the total; the count existed
only as hub geometry (`BlockitCoreViews.kt` hub comment ~916–924:
"the count lives only as hub geometry — no numeral, no word", object
count 1,2,3,1,2,3,4,2); wheel-centre tap cycled the count silently
(Deck update ~694–698).
Fix: a printed reading inside the hero card, directly under the
total, recomposing with every hub tap (testTag `blocks_reading`):
`2 BLOCKS × 50 MIN · 1 × 10 MIN BREAK BETWEEN` (single block:
`1 BLOCK × 50 MIN`). The hub gesture is untouched — it is now
visible when it changes the session. Render test:
`CascadeDeckRenderTest.idleDeckPrintsTheBlocksReading`.

### 2. [HIGH] 'TAP AGAIN TO UNDO' printed only for 140ms — FIXED-IN-CODE
Verified: `BlockitCoreViews.kt:1787` words = UNDO only while
mode==MESH; MESH timed by `MonolithCurves.DURATION_140` (stepMachine
MESH branch); in SEAT, ACTION_DOWN fell into `if (mode != Mode.REST)
return true` — swallowed.
Fix (the seal gesture is a keeper — extended, not redesigned): the
undo now lives for the gesture's whole pre-latch travel. ACTION_DOWN
and ACTION_POINTER_DOWN abort via the existing `startAbortReturn()`
in MESH **or SEAT**; the UNDO words print in MESH **or SEAT**. This
is semantically exact: enforcement dispatches only at `latchFrame()`
(displayed == 1f), so before the latch an abort has provably
dispatched nothing; after it the mode is SEATED, the tap is
swallowed as before, and the grace abort (finding 3) owns the
escape. Header + draw comments updated to state the new span.

### 3. [MEDIUM] 10s grace burns ~1s in the seal shot — FIXED-IN-CODE
Verified: t0 re-anchored at the seal (`MonolithRepository.sealSession`
~1450); the Deck's 1050ms seal shot holds the slide in the pinned
zone while `isSessionRunning` is already true, so the grace chip
first composed at ~1s elapsed with badge `10 − elapsed` = 9s. The
VM enforces no second grace clock (MainActivity wires the chip's
tap straight to the grace cancel) — the chip's presence IS the gate.
Fix: the grace window is now measured from SETTLE. The chip spans
elapsed 0..10 whole seconds from t0 (the first is the shot's), and
the badge counts usable seconds: `(11 − elapsed)` clamped 1..10 —
10s on the first frame the user can see, 1s on the last. The user
now receives the full advertised 10 seconds of abort time.

### 4. [MEDIUM] Proving is a silent black box up to 1500ms — FIXED-IN-CODE
Verified: `ArmProvingController` ANNUL_MS=1500, row born active,
enforcement runs during proving; Deck `provingThisSession` rendered
the idle form and the sentence slot was explicitly
`ArmPhase.Proving -> null`.
Fix: new `provingSentenceFor()` beside `jamSentenceFor()` in
`MonolithCustomComponents.kt`; the slot now prints
`CHECKING THE SEAL — n OF 3 PROVED`, derived from the controller's
own link marks via the existing `provenCount()` extension (one
field, no second derivation). No action attached, on the jam's law:
proving resolves by seal or annulment on the machine's clock
(≤1500ms); the human's doors are the pre-latch undo (finding 2)
before the latch and the grace abort after the seal — a mid-proving
cancel verb was priced and deliberately not added, as it would need
a new controller entry point to race the sampler for zero time gain
over the automatic annulment. Render test updated:
`provingKeepsTheSlideTenancyAndNamesItself` (was "…PrintsNothing").

### 5. [MEDIUM] Idle proof strip contradicts itself — FIXED-IN-CODE
Verified: idle appsCell `appsCellCount != null -> count to
StripDensity.PASS` unconditionally (~519–528 at audit time), while
BLOCK was freshness-checked and the TIME cell already carried the
UNA2-03 rider (PASS only beside LIVE).
Fix: the APPS cell now obeys the TIME cell's law — the count still
prints (it is a count, not a verdict) but at PASS only beside a
freshly LIVE warrant, UNKNOWN density otherwise. One frame can no
longer print BLOCK OFF/UNKNOWN beside a PASS-toned app promise.
The press latch captures the cell at DOWN, so the latched print
inherits the honest density automatically.

## Seats (20 named + added lenses, all signed; thin seats LEAD-COVERED)

Crazy-ideas (LEAD-COVERED — proposed hub-centre numeral; the hero
reading won on legibility) · Maximum-implementation-potential
(LEAD-COVERED) · Brutal critic (verified all 5 still stood on main;
rejected a mid-proving cancel verb as scope creep) · Improver ·
Designer · Consumer (LEAD-COVERED) · Student (LEAD-COVERED) ·
User · User-experience · Purpose-questioning (LEAD-COVERED —
asked why proving exists; answer stands in controller KDoc) ·
Thoughtful · Deep-thinker (LEAD-COVERED) · Enthusiastic designer
(LEAD-COVERED) · Wow-level designer (LEAD-COVERED) ·
Designer-29-years (kept the seal gesture intact; extended its undo
span instead of redesigning it) · Intentionality-focused designer ·
Synchronous collaboration (LEAD-COVERED) · Implementation-quality ·
Technical execution specialist · Interaction-detail specialist
(undo span = mesh+seat to the latch frame, exactly) ·
Human/Day-fit (LEAD-COVERED) · Verifier (re-grepped every audit
line against ed91188 before fixing; re-checked the VM for a second
grace clock — none exists) · Feel (LEAD-COVERED — undo words now
read for the whole travel the hand can still reverse) · Keeper of
Record (this report).

## User-facing changes, in plain words

- Under the big total time you now see how it is built:
  "2 BLOCKS × 50 MIN · 1 × 10 MIN BREAK BETWEEN". Tapping the
  wheel's middle to change the block count is no longer silent —
  the line changes with it.
- After tapping start, "TAP AGAIN TO UNDO" stays true until the
  seal actually commits: a second tap anywhere before that moment
  cancels the start, not just in the first blink.
- The "GRACE WINDOW ABORT" chip now gives you the full 10 seconds
  it advertises, counted from when you can first see it.
- While the app checks the seal (up to ~1.5s), the screen says
  "CHECKING THE SEAL — n OF 3 PROVED" instead of pretending
  nothing happened.
- The apps count in the proof strip no longer glows as a promise
  when blocking is off or unproven.

## CI

Branch run 40988805436 (tip 9775ff8):
https://github.com/Sarthak-2007/Focus-app/actions/runs/40988805436
- **Assemble debug: GREEN.** Unit tests executed: 347 tests,
  39 failed — every failure pre-existing/environmental, proven
  against main run 40988558544 (PR #189 merge, before this branch
  landed): 386 tests, 40 failed, including ALL CascadeDeckRenderTest
  tests. The Cascade failures are
  `java.io.FileNotFoundException at CascadeDeckRenderTest.kt:164` —
  the screenshot capture writing to a shots path that does not
  exist on the CI runner; in both tests this issue touched/added
  the failure lands in capture(), AFTER the assertions, i.e. the
  new proving sentence and the new blocks reading both asserted
  green. The remaining failures span suites this diff never touches
  (Drawer, ScreenshotHarness, Airlock #55, BreakMigration,
  TruthCar3, SoundAndSocial, Tetrad, ChromeWave, ShortsSniper).
- Merged to main by the merge train: PR #230, merge commit
  daf44c9, 2026-10-30T04:04:30Z. Main CI for the merge: run
  41000840287 (queued at report time behind the repo-wide queue;
  main's test step is uniformly red with the same pre-existing
  signature on every recent completed run).
