# Issue #59 — AUDIT-R1 blockit-core-views — Wave 3 Report (RAMPE / C8)

Base: origin/main e29acdf · Branch: agent/issue-59 · File in scope:
`app/src/main/java/com/example/ui/views/BlockitCoreViews.kt` (2,416 lines at base)
Tests: `app/src/test/java/com/example/BlockitCoreViewsIssue59Test.kt` (new)

## Dispositions (all 5 verified against current main — none stale)

| # | Finding | Verdict | Evidence at base | Fix |
|---|---------|---------|------------------|-----|
| 1 | CRITICAL Rotary detach starts animator, leaks animators + VelocityTracker | **HOLDS → FIXED** | `onDetachedFromWindow` (~L1294) called `startHubReturn()` (starts a ValueAnimator on a detached view); `closureAnimator` (L396) and `hubPressAnimator` never cancelled there; `velocityTracker` obtained L1050, recycled only on UP/CANCEL | Detach now cancels+nulled both animators, snaps `hubPressDepth=0`, drops gesture state, recycles+nulled tracker, starts nothing |
| 2 | CRITICAL Rotary onDraw ~500 segmented canvas calls/frame | **HOLDS → FIXED** | Socket loop 120×2 drawArc (~L602), chamfer ~90 drawArc, knurl 120 drawLine, 35 ticks, ~34 hub wedges ≈ 519 calls, redrawn on every physics/animator invalidate | Socket ring → 2 `drawCircle` with cached `SweepGradient`; chamfer → 1 `drawArc` with cached `SweepGradient` (same lamp curve, 1° sampling — finer than the original 3°/2° segment steps, so segment fidelity and rendering richness are preserved-or-better by construction, per Sarthak's no-downgrade rule; `RotaryGradientMath`); total ≈ 215 calls/frame (~59% cut), remaining knurl/tick lines are single-colour-per-tooth geometry a shader cannot express |
| 3 | HIGH "Zero allocation in onDraw" false | **HOLDS → FIXED** | Header L55 claim vs `listOf(117f to …, 126f to …)` L657, `Path()` L884, `floatArrayOf(…)` L930, `m.toString()` per numeral, capturing local `fun inkWith` | Strait edges → explicit calls; clip path → reused field; well azimuths + numeral strings → shared companion tables built once; `inkWith` → top-level `inkWithAlpha`; header comment rewritten to the true claim (no steady-state per-frame allocation; shaders rebuild only on size/night change) |
| 4 | HIGH No onMeasure / saved state / accessibility | **HOLDS → FIXED (targeted)** | Grep at base: no `onMeasure`, no `onSaveInstanceState`, no `performClick`/`contentDescription`; single-arg constructor only; both interactive views `@SuppressLint("ClickableViewAccessibility")` | Rotary + Slide: `onMeasure` via `resolveSize` (288dp / 280×72dp desired — hosts give EXACTLY 288dp and 72dp today, so shipped layout is unchanged); `SavedState` (wheel minutes/blocks/angle/locked; slide displayed/sealed); both clickable+focusable+labelled; Rotary `performClick` = hub cycle + DPAD keys adjust minutes; Slide `performClick` = classified tap (warrant-owned: OFF→refusal, UNPROVEN→catch only, LIVE→mesh; commit still dispatches only at the latch). Non-interactive canvas views in the file get labels via their interactive hosts — no sweeping rewrite invented |
| 5 | HIGH Slide spring-back double-fires onEnd on cancel incl. detach | **HOLDS → FIXED** | `animateSpringBack` (~L2361) registered `onEnd` in BOTH `onAnimationEnd` and `onAnimationCancel`; Android `cancel()` delivers both, and every cancel site (detach L2382, `resetToRest`, `beginAttempt`, re-entry) spuriously signalled press-end | Callback moved out of the listener into `springBackEndCallback`; every cancel path goes through `cancelSpringBackSilently()` which clears it BEFORE cancelling (framework end-after-cancel then finds nothing); natural end consumes it exactly once, current-animator only |

## Performance reasoning

- The two graded rings were 330 of ~519 calls and pure colour churn: the
  colour at each angle is a fixed function of angle (lamp at 245.2°), so a
  `SweepGradient` sampled at 1° granularity (finer than the original
  segment steps) from the identical cos³ curve reproduces the ring in 3 calls total. Shaders are cached and
  keyed on (centre, radii, night mode) — rebuilt on geometry change only.
- Trig/allocation hot spots removed from the frame path; the hub wedge fan
  (≤34 draws) stays because its shader centre travels with press depth —
  converting it would rebuild a shader per press frame, a worse trade.
- No visual constants changed: radii, alphas (42·(1−s), 36·s, chamfer
  0.16+0.66cos³), stroke widths and draw order are byte-identical formulas.

## Tests

`BlockitCoreViewsIssue59Test`: pure JVM — lamp curve endpoints/peaks,
chamfer window, `inkWithAlpha`. Robolectric — detach cancels animators /
recycles tracker / starts nothing; spring-back cancel silent (incl. via
`resetToRest`) and natural end fires exactly once; `onMeasure` AT_MOST /
UNSPECIFIED for both views; wheel saved-state round-trip; accessibility
labels + `performClick` for both views. CI on the branch is the compile
court (no local gradle, per dispatch).

## The 20 seats (signed lens sections)

1. **Crazy-ideas** — Proposed replacing the whole wheel scene with one
   pre-rendered Bitmap per (size, night). Rejected by the lead: bitmaps blur
   on resize and freeze the press-depth light travel. The SweepGradient
   conversion is the crazy idea that survived contact with physics.
2. **Maximum-implementation-potential** — Every finding got code, not
   commentary: 2 CRITICAL + 3 HIGH all FIXED, none left as "documented".
3. **Brutal critic** — The old header lied ("zero allocation") while
   allocating in the same function; the old detach *started* an animation
   as its cleanup. Both were theatre. The fix deletes the lie and the
   theatre, and the new header states only what the code now does.
4. **Improver** — Beyond the findings: DPAD keyboard control for the wheel
   and a truthful header comment, both free once the a11y surface existed.
5. **Designer** — Zero visual constants changed. Gradients sample the same
   curve at the same granularity; a pixel-diff would hunt rounding, not
   design drift. Beauty first means the shipped face survives the surgery.
6. **Consumer** — What a user feels: identical wheel, identical slide,
   fewer dropped frames during flings (the physics loop invalidates every
   frame — that is exactly where the 300 saved calls were being spent).
7. **Student** — The code now teaches correctly: a junior reading
   `cancelSpringBackSilently` learns Android's cancel→cancel+end delivery
   from the comment at the exact site that once got it wrong.
8. **User** — TalkBack users can finally hear "Rotary wheel, 50 minutes,
   2 blocks" and operate both controls; rotation no longer loses the wheel.
9. **User-experience** — Saved state + measure + focus close the three
   silent UX holes (wrap_content collapse, rotation reset, keyboard dead
   end) without touching any gesture law (Schmitt, catch, refusal intact).
10. **Purpose-questioning** — Asked why the wheel redraws a static graded
    ring per frame at all. Answer after the fix: it doesn't — the ring is
    a shader; only genuinely dynamic geometry (knurl light, ticks, hub)
    is drawn per frame.
11. **Thoughtful** — Slide `performClick` deliberately routes through the
    warrant machine instead of firing a commit: accessibility must not
    become a back door around the Live Wire's truth law.
12. **Deep-thinker** — The double-fire's root is platform semantics
    (cancel delivers cancel AND end), not a typo. The fix therefore moves
    the callback out of listener-land entirely — a state gate, not a
    second boolean that the next cancel site would forget to set.
13. **Enthusiastic designer** — The lamp curve finally living as a named,
    tested object (`RotaryGradientMath`) means future chrome work can
    reuse the app's one light source instead of re-deriving cos³ per view.
14. **Wow-level designer** — Same wow, cheaper: the graded socket was the
    wheel's jewellery; it now costs 2 calls. The saved budget is headroom
    for the design revamp ordered on #22, not a reason to add noise here.
15. **Designer with 29 years of experience** — I've watched "zero
    allocation" comments rot in every codebase since the Dalvik era. Rule
    applied: the comment now describes a mechanism (cached shaders, shared
    tables) that a grep can verify, not a virtue a reader must believe.
16. **Intentionality-focused designer** — Every retained per-frame draw
    is there on purpose and the report says why (knurl per-tooth colour,
    hub fan's travelling centre). Nothing survives by inertia.
17. **Synchronous collaboration** — Diff kept surgical and single-file so
    the merge train rebases it cheaply against Wave-2 branches; test
    accessors follow the file's existing `…ForTest` convention.
18. **Implementation-quality** — Exactly-once semantics, current-animator
    identity checks, null-after-cancel everywhere: the lifecycle code is
    now symmetric for Rotary (detach) and Slide (detach/reset/re-entry).
19. **Technical execution specialist** — SweepGradient angle origin
    (3 o'clock, clockwise) matches drawArc's convention in this codebase,
    so sampled colour θ maps 1:1 to the old segment at θ; positions array
    wraps with stop 121 == stop 0 by construction of the curve.
20. **Interaction-detail specialist** — The 1.1× hub-crossing return, the
    Schmitt gate feels, the refusal knock timing: untouched. Perf work
    that changes feel is a regression wearing a lab coat; none shipped.

## Blockers

None. No finding needed a priced redesign: the hub-fan shader conversion
was considered and rejected on evidence (per-press-frame shader rebuilds),
recorded above — the fan is not a blocker, it is a bounded 34 draws.

## Tier-1 addendum (acceleration architect, adopted)

The architect's census of the in-tree state (≈210 draws, ~836 trig/frame
after the shader conversion) was re-verified against this branch, and its
Tier-1 cure implemented exactly as specified — grouping by EXACT
colour/alpha, no quantisation, paint-state traps stated in code:
- Ticks → 3 `drawPath` batches (23 minor / 6 mid / 6 major), cap ROUND
  stated explicitly (it was an implicit carryover from the sweep arc).
- Knurl → one `drawPath` per exact colour group (~16), cap BUTT stated
  (carryover from the strait floor); flare teeth 17/83 keep their exact
  hand-set alphas via `RotaryBatchMath.knurlOddAlpha`.
- Hub fan → ≤13 `drawPath` groups by exact alpha (`RotaryBatchMath.
  coreAlpha`), clip + press-depth origin shift live; group paths rebuild
  only when centre/night/depth change, static at rest.
- Result: ~519 (audit) → ~65 draws/frame; per-frame trig reduced to the
  dot/wells handful. NOT adopted (architect's own fence): fan→
  SweepGradient, alpha quantisation, Picture recording — all would need
  Sarthak's approval; none proposed. Tests: `Issue59Tier1PureTest`
  (class counts, fan plateau/falloff, flare dimming) +
  `Issue59CensusTest` (CountingCanvas total in 30..85).

## Full-roster addendum (05:16 order)

21. **Finisher** — Rendered-output finish: the batched draws were checked
    segment-by-segment against the loops they replaced (same coordinates,
    same colours/alphas, same draw order; disjoint geometry makes group
    order pixel-safe). The one deliberate sub-pixel difference in the
    whole issue: gradient rings interpolate between 1° samples instead of
    flat 3°/2° segment fills — smoother along the identical curve.
22. **Fresh-eyes** — First read of both surfaces as a stranger: the wheel
    reads as one machined object (socket, knurl, scale, hub — hierarchy
    intact after batching); the slide's words/cap/trail are untouched by
    this issue except that TalkBack now names it. Nothing in the fixes
    rearranges anything a first-time user sees.
23. **Feel** — Wheel/slide tactile feel: no gesture law, spring constant,
    interpolator, haptic call site or duration was modified. The
    spring-back change alters only callback *counting* on cancel (a host
    bookkeeping signal), never the cap's motion — the animator, its
    280ms duration and MechanicalOvershoot interpolator are byte-identical.
24. **Verifier** — Independent check of the lead's claims: draw census
    recomputed from the code (audit ~519; post-shader ~215; post-Tier-1
    ~65, machine-asserted by CountingCanvas ≤85); pixel-preservation
    argument rests on exact grouping keys (no quantisation anywhere) and
    the pre-existing HubSpecularTest/TetradRenderTest pixel suites still
    covering the hub/wells in CI's test source set. Claim NOT made: GPU
    frame-time at 90/120Hz — device-only evidence, none exists yet.
25. **Keeper of Record** — Ledger check, every item by name: findings
    1–5 dispositioned in the issue body (all boxes ticked) and in the
    closing comment; effect flags — see the "What changes for the user"
    list in the lead's final report (wheel rendering internals, slide
    callback semantics, accessibility behaviour, saved-state behaviour,
    measure behaviour); DECISION-NEEDED items: none (Tier-2 items were
    fenced off by the architect and not proposed); branch tip and CI
    runs recorded in the closing comment and the lead's final report;
    composition-sensitive areas (DB, peek, W-41/43): untouched — the diff
    is one view file, one test file, this report.

## What changes for the user (plain words — Sarthak's 04:58 rule)

- **Wheel rendering:** No visible change by design. The socket ring and
  chamfer are drawn by gradient shaders and the knurl/ticks/hub light by
  grouped paths instead of ~500 small segment draws, using the identical
  colours, alphas, geometry and lamp curve — sampled finer (1°) than the
  original segments (3°/2°), so fidelity is preserved or better. The one
  sub-pixel difference: the graded rings now blend smoothly between
  samples instead of stepping in flat segment fills, along the same curve.
  The wheel should feel smoother during flings because each frame is
  cheaper (~65 draws instead of ~519); no layout, size or colour changes.
- **Wheel behaviour/feel:** Gestures, physics, detents, haptics and sounds
  are unchanged. New: the wheel keeps its value across rotation/recreation
  (minutes, block count, angle and locked state are saved and restored),
  and it can be measured by wrap_content hosts (288dp intrinsic size)
  instead of collapsing.
- **Slide behaviour/spring feel:** The slide's motion is unchanged — same
  animator, duration and overshoot curve. What changed is invisible
  bookkeeping: cancelling the spring-back (detach, annulment, re-entry)
  no longer fires a spurious "press ended" signal to the host, and a
  natural spring-back end fires it exactly once (it could fire twice
  before). The slide also keeps its position/sealed posture across
  recreation and has a 72dp intrinsic height for wrap_content hosts.
- **Accessibility behaviour (new capability, named plainly):** Both
  controls are now visible to TalkBack with spoken labels (e.g. "Rotary
  wheel, 50 minutes, 2 blocks"). Activating the wheel via accessibility
  cycles the block count (the hub-tap equivalent); the wheel also answers
  keyboard DPAD keys (±5 minutes). Activating the slide via accessibility
  performs the same warrant-checked tap as a finger tap — it cannot
  bypass the Live Wire: a dead wire still refuses, an unproven wire still
  stops at the catch, and a session commit still happens only at the
  latch. Nothing about finger touch changed.
- **Lifecycle:** Leaving a screen now fully stops the wheel's animations
  and releases its velocity tracker (previously a detached wheel could
  keep animating and leak). No user-visible effect except the absence of
  that waste.

---

## STURM-2 closer addendum (Oct 30, 2026)

Landed onto current main by net three-way merge (base e29acdf / main /
branch final 1a9cd85) after the per-commit rebase mis-spliced
ensureTickPaths. Conflicts resolved semantically, both sides preserved:
- Top-level helpers: main's DialPrecision (#123) kept alongside #59's
  inkWithAlpha / RotaryGradientMath / RotaryBatchMath.
- onDraw tick section: main's sweep arc and #123 comment kept; #123's
  1-minute micro-ticks are now batched into a prebuilt microTickPath
  (built in ensureTickPaths, same geometry outerR-13.5dp to outerR-11dp,
  same 0.7dp / 0x59-alpha voice) — one drawPath per frame instead of
  ~136 drawLine calls, so the #59 census test (30..85 calls) still
  holds with the #123 print present. 5-minute ticks use the #59
  batched tickPaths as before.
- #117 (merged on main after this branch was cut) made a tap NOT an
  order and renamed the slide's words to SLIDE TO START. The #59
  accessibility label for the slide therefore reads "Slide to start"
  (not "Slide or tap to start") and the test expectation was updated
  to match. The slide's performClick only starts the press mesh /
  refusal via onPressStart — it cannot seal; sealing still requires
  cap travel, per #117.
