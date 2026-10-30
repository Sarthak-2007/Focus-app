# ISSUE #56 — WAVE 3 REPORT · park-drawer (C8 Schwelle, lead RAMPE)

Branch `agent/issue-56` on `origin/main` e29acdf. Evidence source:
`studio/audits/AUDIT-R1.txt`, slice `park-drawer` (lines 1721–1768).

## Verdict table (every finding dispositioned)

| # | Sev | Finding | Disposition | Evidence |
|---|-----|---------|-------------|----------|
| 1 | HIGH | All three sleds bypass the interactive gate | **FIXED-IN-CODE** | Verified on main: `interactive` was used exactly once (FIX key); the Sound/Shorts/Social sleds were gated only by an `if (isSealed)` overlay (main ParkDrawer.kt ~517/586/755), and the composable survives until `fraction >= 1f`, so sleds stayed live through the 160ms close. Verified NOT cured on `agent/issue-9`: its ParkDrawer still gates sleds with `if (isSealed)` overlays and unconditional `onToggleRequested = { onToggle…() }`. Fix: all three sleds now receive the one `interactive` gate — touch-taking overlay whenever `!interactive`, plus the toggle callback itself checks `rememberUpdatedState(interactive)` at fire time (ParkDrawer.kt, this branch). |
| 2 | HIGH | Grip drag launches an unordered coroutine per drag event; 40% law reads possibly stale fraction | **FIXED-IN-CODE** | Verified on main (~250–275): `scope.launch { fraction.snapTo(…) }` per `onVerticalDrag`, `fraction.value > 0.4f` read at drag-end. Not touched by issue-9. Fix: drag accumulates synchronously into `gripDragFraction` via pure `gripDraggedFraction()`; the sheet renders the accumulator; exactly one ordered coroutine at drag-end/cancel seats the Animatable on the dragged value, then throws/seats. The 40% law (`PARK_DISMISS_FRACTION`) judges the accumulator. |
| 3 | MED | Sleds toggle on any ACTION_UP — a sheet scroll ending on a sled flips the setting | **FIXED-IN-CODE** | Verified on main: `SledSwitchView.onTouchEvent` (BlockitSuitcaseAndSwitchesViews.kt) fired `onToggleRequested` on any ACTION_UP — no DOWN tracking, bounds, or CANCEL. Not touched by issue-9 (no diff to that file). Fix at the root: the view now requires a complete press (DOWN inside → UP inside, CANCEL disarms), mirroring `AppRowSledToggleView`'s discipline in the same file. This also cures the same sled wherever else it is seated (e.g. Shield). Park's finding-1 gate is the second wall. |
| 4 | MED | FIX return verdict not bound to its trip — second press within 800ms judged/cleared by first trip's timer | **FIXED-IN-CODE** | Verified on main (~191–210): the effect read `fixPressedItem` only after `delay(800)`. Issue-9's release-commit (its 6237468) narrows the accidental-press window but does **not** bind the verdict to a trip — not FIXED-ELSEWHERE. Fix: every press advances `fixTrip`; the return effect captures `(trip, item)` before the delay and stands down unless `fixReturnStillOwns(...)` still holds after it. Merge note for the train: if issue-9's press-block rewrite lands on top, keep the `fixTrip++` line in the press handler. |
| 5 | LOW | Scrim stops blocking touches the instant dismissal starts, while visibly covering the screen | **FIXED-IN-CODE** | Verified on main: scrim clickable existed only `if (visible)`, while the sheet/scrim remain composed until `fraction >= 1f`. Not touched by issue-9. Fix: the scrim consumes touches whenever it is drawn (`fraction.value < 1f`); its click dismisses only while `visible`, so the close throw can no longer be tapped through. |

Counts: **5 verified · 5 FIXED-IN-CODE · 0 VERIFIED-FALSE · 0 FIXED-ELSEWHERE · 0 STANDS-BLOCKED.**

Tests added: `app/src/test/java/com/example/ParkDrawerGateTest.kt` —
`ParkGripDragMathTest` (in-order accumulation, clamping, the strict
>40% boundary, zero-height guard), `ParkFixReturnBindingTest`
(trip-binding truth table), `SledSwitchPressDisciplineTest`
(Robolectric, raw MotionEvents against the real view: UP-without-DOWN,
complete press fires exactly once, CANCEL disarms, slide-off and
started-outside never toggle).

Design/truth laws preserved: no visual change at rest (gate overlays
are invisible touch-takers, exactly like the seal overlay they
generalise); fonts/palette untouched; no new surfaces, words, or
sounds; ARM/HOLD/TELL-THE-TRUTH strengthened — a control that can
fire while its sheet is moving was printing a lie about being
operable, and a scrim that looks like a wall but passes touches was
the same lie in the other direction.

---

## The 20 seats

### 1. Crazy-ideas — signed, Crazy-ideas seat
My opening bid was to abolish the drag entirely: make the grip a
two-state throw switch and let KIN-DRAWER's 280/160 tempos do all
travel, so no drag state could ever be stale. I withdraw it myself:
the 1:1 grip drag is the drawer's one piece of direct manipulation,
and Sarthak's feel law favours the hand owning the sheet. The cure
kept the drag and killed only its concurrency — synchronous
accumulation is the crazy-simple version of the same idea: the
pointer thread is already a perfect sequencer, we just stopped
paying coroutines to re-race what was never concurrent. Second idea,
filed for the train: render the 40% line as a hairline tick on the
grip at drag time. Not built — it adds print to a furniture surface
without a ruling; the law itself is now honest, which was the issue.

### 2. Maximum-implementation-potential — signed, Maximum-implementation-potential seat
I pushed for the maximal version of each cure, not the minimal:
finding 1 got two walls (overlay + callback gate via
`rememberUpdatedState`, so even a touch that reaches the AndroidView
through an interop crack dies at the callback); finding 3 got fixed
in the shared view, curing Shield's sleds in the same stroke instead
of patching Park alone; finding 2's resolution path is a single
coroutine whose internal order (snap → clear accumulator → dismiss
or animate) is provable by reading one block. Where I stopped: no
`isEnabled` plumbing on the AndroidViews — a disabled View changes
its own touch dispatch in ways that interact with the Compose
scroller, and two proven walls beat a third unproven one.

### 3. Brutal critic — signed, Brutal critic seat
The audit understated finding 1. "Rows are inoperable mid-rise" is
printed in this file's own header spec while three persisted-setting
switches sat live through rise, throw, and close — that is not a
bug, it is the spec lying in its own house, the exact failure the
TELL-THE-TRUTH standard exists for. Finding 3 is worse than MEDIUM
in spirit: a scroll — the most common gesture on this sheet — could
rewrite the Shorts+Reels sniper and the browser blocker. And the
FIX timer (finding 4) could acquit a door the user was still
standing in front of in Settings. I challenged the bundle to call
any of these VERIFIED-FALSE; none survived contact with the code.
I also challenged the fix for finding 2: does cancelling the
accumulator on `!visible` strand the sheet mid-air? Read it: drag
state only exists while the grip detector (keyed on `visible`) is
alive, and drag-end resolves before dismissal begins. It holds.

### 4. Improver — signed, Improver seat
Beyond dispositioning, three quiet improvements rode along, each
inside a finding's blast radius: (a) drag-cancel now re-seats the
sheet from the accumulated position — the old body left the
Animatable wherever the last raced snap happened to land; (b) the
dismiss threshold is now a named constant (`PARK_DISMISS_FRACTION`)
with a boundary test, instead of a magic 0.4f inside a callback;
(c) `ParkSocialSitesRow`'s bespoke `isSealed` parameter became the
shared `interactive` gate, so the next reader sees one gate type on
this sheet, not two dialects. I deliberately did not "improve" the
FIX key's DOWN-commit in this branch: issue-9 owns that change
(release-commit), and duplicating it here would buy a merge conflict,
not quality.

### 5. Designer — signed, Designer seat
Nothing on this sheet looks different at rest, and that is the
design decision: the findings were all temporal, so the cures are
temporal. The one surface I examined hardest is the sled overlay —
generalising the seal's invisible touch-taker means that during the
280ms rise a user's tap on a sled now lands on nothing instead of
flipping a setting; there is no visual affordance for "not yet",
which matches the header law ("rows are inoperable mid-rise" is a
state, not a decoration). If a future finisher wants a tell, the
honest one is the margin word's existing 260ms delayed flip — the
sheet already speaks in delayed print. No new ink, no new motion
curves, radii and plate untouched.

### 6. Consumer — signed, Consumer seat
I read these as a buyer: three of the five findings are "the
product did something I didn't ask for" — a setting flipped by a
scroll, a drawer judged my repair trip before I made it, taps
leaking through a dimmed screen onto whatever was underneath. That
is the failure class that makes a person uninstall a blocker: a
discipline tool must be more predictable than the impulses it
guards. After the fix, every state-changing surface on this sheet
requires a deliberate, complete, settled gesture. That is the
consumer promise of the whole app, kept on its fiddliest screen.

### 7. Student — signed, Student seat
Walking this as the NEET student at 5 AM, half awake, opening PARK
to check why Shorts are still reachable: the old sheet could punish
that visit — scroll the board, land on the sniper sled, and the
setting flips without a sound he connects to his finger. He would
conclude the app "doesn't work", when in fact it worked against
him. The cured sheet makes his morning check read-only by default
and deliberate by exception, which is exactly the cognitive budget
a drop-year student has for a settings drawer: zero. The FIX-trip
binding matters to him too — he makes that Settings trip between
study blocks; a verdict that fires early reads as "STILL BROKEN"
or false comfort, both cost him a second trip.

### 8. User — signed, User seat
As the daily user I care about three moments: flicking the drawer
shut by its grip (now judged on where my finger actually left the
sheet — a fast flick past 40% dismisses, reliably, instead of
racing snap coroutines), tapping FIX and going to Settings (my
second attempt is no longer graded by my first attempt's clock),
and dismissing by scrim (during the close animation my stray tap
hits the scrim, not the live screen behind it). All three were
coin-flips before; all three are deterministic now. Nothing I
learned about using this drawer changed — it just stopped
disobeying.

### 9. User-experience — signed, User-experience seat
The UX defect pattern here is *gesture ambiguity*: one physical
gesture (a scroll ending, a drag, a tap during animation) mapped
to two different intentions. The cures all follow one rule —
**a gesture's meaning is fixed by where it started and the state
the surface was in when it started**. Sled: press must start on
the sled. Grip: the whole drag is one accumulated intention,
judged once at its end. FIX: the verdict belongs to the trip that
started it. Scrim: while it is visible it is a wall, full stop.
That rule is now consistent across the sheet, which is why these
five read as one fix, not five patches.

### 10. Purpose-questioning — signed, Purpose-questioning seat
I asked why PARK has sleds at all — the board's job is proof
(GRANTED/MISSING/STALE), and settings could live on Shield. The
answer is in the file's own history: UNA-01/UNA-02 seated the
sniper and social sleds here because the board *prints* those
facts, and a printed fact you cannot act on, on the surface that
proves it, forces a screen-change for a one-bit decision. The
sleds earn their place — which is precisely why their gatings
mattered: a proof surface whose controls fire accidentally
corrupts the very facts it proves. Purpose kept, hazard removed.
I also questioned finding 5's LOW: a scrim you can tap through
during close is a 160ms window onto an armed blocker UI — low
probability, but the fix cost was one condition, so it stands.

### 11. Thoughtful — signed, Thoughtful seat
The careful choice in this bundle was what *not* to merge from
issue-9. Its Park branch is 450 lines of new surface (CHECK key,
warrant line, staircase, verdict print) written against the same
file; importing any of it to claim FIXED-ELSEWHERE would have
inflated this branch's diff and its rebase cost, and — checked
line by line — it cures none of the five findings anyway: its
sleds still hang on `isSealed` alone, its grip is byte-identical
to main's, its FIX timer still reads state after the delay. The
thoughtful move was five small, separable cures and a written
merge note (keep `fixTrip++` when #9's press block lands), so the
train composes instead of colliding.

### 12. Deep-thinker — signed, Deep-thinker seat
Finding 2 deserves the deep read. `Animatable` serialises its own
mutations through a MutatorMutex, so the per-event `snapTo` calls
could not corrupt the value mid-write — the race was subtler:
launched coroutines on the main dispatcher *usually* run in launch
order, but "usually" is doing all the work, and `onDragEnd`'s read
raced the queue either way. The deep fix is recognising that
pointer events are already totally ordered by the input pipeline;
the correct concurrency model for a drag is no concurrency. The
accumulator pattern (state write per event, one resolution
coroutine per gesture) is the general form — it is also how
finding 4 was cured: the return effect's flaw was reading mutable
state across a suspension point, and the cure was capturing the
identity before the suspension. Both findings are the same
theorem: never read across a gap what you could have captured
before it.

### 13. Enthusiastic designer — signed, Enthusiastic designer seat
I love that the grip survived. A 1:1 sheet drag with a growing
24→28dp handle is the most tactile thing in this app, and the
tempting "safe" redesign (buttons, switches, animated-only travel)
would have traded the app's analog-machine soul for correctness.
We kept the soul and fixed the bookkeeping — the drag *feels*
identical, it just keeps its accounts in one ledger now. Same for
the sleds: the mechanical throw, the 8ms firm click, the chrome
track — untouched. Enthusiasm, disciplined: every cure in this
branch is invisible, and the sheet is more itself for it.

### 14. Wow-level designer — signed, Wow-level designer seat
Wow on a settings drawer is not spectacle; it is the moment a fast
flick dismisses *every time* and a scroll *never* betrays you —
precision reads as luxury. Before this branch, PARK's wow was
borrowed: it looked mechanical but its gestures were probabilistic.
Now the mechanics are mechanically true: the fraction your finger
sees is the fraction the law judges (the renderer and the judge
read the same accumulator — a detail I specifically verified,
because a fix that judged the accumulator but rendered the
Animatable would have introduced a visible lag lie). That
render/judge identity is the wow detail here, and it cost one
`effectiveFraction` derivation used by both offset and scrim.

### 15. Designer with 29 years of experience — signed, Designer with 29 years of experience seat
Twenty-nine years says: every one of these bugs is a classic.
Toggle-on-any-UP is the 1990s checkbox bug. Per-event async writes
to one animated value is the 2000s "thread per mouse-move" bug.
A modal whose backdrop stops eating clicks mid-dismiss is the
2010s dialog bug. The patterns that survive decades are the ones
applied here: complete-press semantics (down+up, same widget,
cancel-safe — the discipline the sibling rocker view already had,
which is why I insisted the fix live in the shared view, not in
Park), single-writer state, and capture-before-await. I also
vouched for restraint: no debounce timers, no gesture timeouts —
time-based patches on event bugs rot; state-based ones don't.

### 16. Intentionality-focused designer — signed, Intentionality-focused designer seat
Every element on this sheet now has exactly one intention channel:
the FIX key fires only from a settled sheet, the sleds only from a
complete on-sled press on a settled sheet, the grip only ever
means "move the sheet", the scrim only ever means "wall, or
dismiss when tapped while open". Before, intention leaked: a
scroll intended "read the board" and the sled heard "change the
setting". The design charter for this app says everything
intentional, no accidents — these five findings were the sheet's
remaining accidents, and each cure names the intention it protects
in its comment, so a future editor can tell a deliberate gate from
a missing feature.

### 17. Synchronous collaboration — signed, Synchronous collaboration seat
This bundle's collaboration surface was threefold and all of it is
in writing: (1) with the issue-9 cell — I read its branch before
writing a line, established it cures none of the five, and left it
a merge note instead of a conflict (its press-block rewrite must
carry `fixTrip++`); (2) with RAMPE/Ren — dispositions use the
wave's exact vocabulary, counts up top, blockers named (none);
(3) with the merge train — the diff is confined to ParkDrawer.kt,
one shared view, one new test file, and this report, each hunk
traceable to a finding number in its comment, so a rebase reviewer
can drop or keep hunks per finding. No comment parade on the
issue: one closing evidence comment, per the 04:43 order.

### 18. Implementation-quality — signed, Implementation-quality seat
Code quality specifics I signed off on: the pure helpers
(`gripDraggedFraction`, `fixReturnStillOwns`) are `internal`,
side-effect-free, and table-tested — the two raciest behaviours in
the file are now the two most testable. `rememberUpdatedState` is
used for the sled callbacks because AndroidView `update` blocks
only re-run on recomposition with changed inputs; the State
indirection makes the gate value live at fire time, which is the
correctness point of finding 1, not a style flourish. No new
dependencies, no signature changes to `ParkDrawer` itself, no
behaviour change at rest. The one wart I accept knowingly:
`return@detectVerticalDragGestures` relies on the implicit lambda
label — standard Kotlin, and CI will arbitrate.

### 19. Technical execution specialist — signed, Technical execution specialist seat
Execution checklist, verified in order: base e29acdf confirmed
(`git rev-parse HEAD` before edits); audit slice read from
AUDIT-R1.txt lines 1721–1768, not from the issue's garbled title
text; issue-9 diff enumerated file-by-file before any
FIXED-ELSEWHERE claim was rejected; each fix landed as a targeted
edit with the finding number in its comment. Compile risk points,
self-audited: implicit-label return in the drag detector (valid
Kotlin), smart-cast on the local `dragged` val, `WatchItemId`
list indexing in tests (PERMISSIONS is `List<WatchItemId>`,
WatchStore.kt:93). No local gradle run, per the RAM law — CI on
the branch is the compile court, and its verdict is recorded in
the closing comment before this issue closes.

### 20. Interaction-detail specialist — signed, Interaction-detail specialist seat
Details, one per finding: (1) during the drawer's *rise*, a sled
tap previously toggled AND the margin word flipped 260ms later on
the delayed-print timer — you could watch a setting change on a
sheet you hadn't been given yet; both layers are gated now.
(2) `onDragCancel` is not dead code anymore — a system-interrupted
drag re-seats instead of abandoning the fraction. (3) The sled's
bounds check uses the view's own laid-out size, so a press in the
76×34 chrome bounds counts and the fat-finger slide-off doesn't.
(4) The FIX gate leaves the seat-knock on DOWN untouched — feel
stays immediate; only the verdict waits for its own trip.
(5) Scrim alpha and scrim touchability now share one derivation
(`fraction`), so what you see is exactly what blocks — the
interaction-detail definition of an honest overlay.

---

## Added roster seats (Sarthak's 05:16 full-roster order)

### 21. Finisher — signed, Finisher seat (bundle's own seat, marked as such)
Finish-pass over the final diff, line by line, hunting the defects
that survive a green assemble: (a) the scrim's `remember {
MutableInteractionSource() }` sits inside the same conditional
modifier branch the original code used — no new composition risk
introduced; (b) `onDragCancel` and `onDragEnd` both resolve through
the identical snap→clear→animate order, so there is no path where
the accumulator outlives its gesture; (c) the sled overlays are the
seal overlay's exact geometry (76×34, same Box), so no layout shift
exists between sealed and merely-unsettled states; (d) the one
finish defect found and fixed during this pass: drag-cancel in the
first draft still launched its re-seat even when the drawer had
been dismissed mid-drag — the shipped code checks `visible` inside
that coroutine before animating. Verdict: the diff is finished, not
merely complete — every hunk carries its finding number, and no
hunk touches a surface its finding didn't name (the three
test-compile unblocks are the single exception, separately
committed and separately explained below).

### 22. Fresh-eyes — signed, Fresh-eyes seat (bundle's own seat, marked as such)
Read cold, as a stranger to the audit: the sheet now has exactly
one sentence of interaction law — *nothing on this sheet acts
unless the sheet is settled, open, and unsealed, and every gesture
is judged by where it started and what it accumulated, never by a
timer or a stray release.* A stranger's three probes: (1) Can I
scroll the board by starting on a sled? Yes — the sled disarms on
CANCEL and the scroll proceeds; the scroll just can't *end* as a
toggle. (2) Does gating the sleds during the grip drag trap the
sheet? No — the overlays cover only the 76×34 sled boxes; the grip
and the rest of the sheet keep their gestures. (3) Does the FIX key
feel dead during the rise? It reads as print until settled, the
same face the sleds now wear — one behaviour, learned once. No
contradiction found between the header spec ("Rows are inoperable
mid-rise") and the shipped behaviour — for the first time, the
spec is simply true.

### 23. Feel — signed, Feel seat (EXTERNAL read-only seat, evidence received via RAMPE, snapshot agent/issue-56@24b982b)
**PASS — no DECISION-NEEDED.** The feel seat verified every
canonical gesture is byte-identical in duration/easing/sound/
haptic against the pre-fix surface: sled press 8ms `fireFeel` +
260ms throw; FIX seat-knock 10ms at DOWN, verdict at +800ms,
success-only firm click; rise 280ms EasingSeat, close 160ms
EasingThrow, 0.4 dismiss fraction. Its six plain-words effect
flags are adopted verbatim in the Keeper record below, and the
bundle confirms the final code (24b982b) matches the snapshot it
audited on every one of those numbers — the only later commit is
the test-compile unblock, which touches no gesture path.

---

## Keeper of Record — record check (every item accounted for by name)

**Findings (5/5 dispositioned):**
1. HIGH sleds bypass interactive gate — FIXED-IN-CODE (ParkDrawer.kt gate + callback).
2. HIGH grip per-event coroutine / stale 40% read — FIXED-IN-CODE (synchronous accumulator, `gripDraggedFraction`, `PARK_DISMISS_FRACTION`).
3. MED sled toggles on any ACTION_UP — FIXED-IN-CODE (SledSwitchView complete-press discipline).
4. MED FIX verdict not bound to its trip — FIXED-IN-CODE (`fixTrip`, `fixReturnStillOwns`). Merge note filed: issue-9's press-block rewrite must carry `fixTrip++`.
5. LOW scrim stops blocking at dismissal start — FIXED-IN-CODE (blocks while `fraction < 1f`).

**VERIFIED-FALSE: none. FIXED-ELSEWHERE: none** (issue-9 branch
checked file-by-file; it cures none of the five).
**DECISION-NEEDED: none** — no cure here downgrades anything (see
effect flags: every change removes an *accidental* effect; every
deliberate gesture keeps its exact timing, travel, and sound).
**STANDS-BLOCKED: none.**

**What changes for the user (plain words, per the 04:58 flags rule):**
1. Dragging Park shut by its grip now follows your finger exactly, and letting go past 40% dismisses every time — before, a fast flick could be misjudged and re-seat the sheet.
2. If a grip drag is interrupted (system gesture, interruption), the sheet now slides back to open instead of being left hanging mid-air.
3. While Park is closing, the dimmed background now blocks taps for the whole close — taps can no longer fall through the visible dim onto the screen underneath.
4. Park's three switches (Sound, Shorts+Reels, Social Sites) can no longer be flipped while the sheet is rising, being dragged, closing, or sealed — they only work once Park is fully open and still.
5. A switch now only flips on a real press that starts and ends on the switch itself. Ending a scroll on top of a switch, sliding off it, or having the scroll interrupted no longer changes any setting, silently or otherwise.
6. The FIX button's verdict now belongs to the trip you actually took: pressing FIX again quickly no longer lets the first trip's timer judge (or clear) the second press — each press is judged on its own return, at the same +800ms as before.
Nothing else changes: Park looks identical, opens/closes at the same speeds (280ms/160ms), sled throw and sounds unchanged, FIX still knocks on press and clicks only on a proven repair.

**Branch & commits:** `agent/issue-56`, base e29acdf —
5c0f0a9 (the five cures + ParkDrawerGateTest + this report),
24b982b (test-compile unblock, pre-existing breakage: ParkDrawer
sled-parameter defaults — the production caller in MainActivity
passes every argument explicitly, so runtime is unchanged;
TruthCar3SweepTest dead imports; TruthCar3CaptureBodyTest SDK 36
StatusBarNotification arity).

**CI runs accounted for:**
- Run 40973510133 (5c0f0a9): Assemble debug **GREEN**. Unit tests
  failed at *compilation* — all errors in five pre-existing test
  files (ScreenshotHarnessTest, ShortsToggleRenderTest,
  SoundAndSocialTruthTest, TruthCar3SweepTest,
  TruthCar3CaptureBodyTest), zero in this issue's new tests; the
  same test-compile breakage exists on main itself (main's own
  runs 40969279638 / 40970102642 are failure).
- Run 40975044540 (24b982b): Assemble debug **GREEN**; unit tests
  **executed** — 148 completed, 114 passed, 34 failed, every
  failure named: 32 Robolectric `classMethod` environment failures
  (`UnsupportedOperationException at DefaultSdkProvider.java:170`)
  hitting Robolectric classes indiscriminately — 31 pre-existing
  classes plus this issue's SledSwitchPressDisciplineTest, whose
  pure-logic siblings (ParkGripDragMathTest, ParkFixReturnBindingTest,
  6 tests) **passed**; plus 2 pre-existing assertion failures in
  code this issue does not touch
  (ChromeWaveUnitTest.warrantDerivation_matchesTheDeletedLiveWarrantTable;
  TruthCar3SweepTest.una06_factoryDefaults_biteOnlyWhenStored —
  browser-domain matching). These 34 are the suite's first
  execution since its compilation broke on main; none is caused
  by, or in, a #56 code path.

*Report filed by the Issue #56 bundle (C8 Schwelle, lead RAMPE).
Closure evidence (branch tip + CI run URL) is posted on issue #56
itself, per Sarthak's 04:43 no-procrastination order.*
