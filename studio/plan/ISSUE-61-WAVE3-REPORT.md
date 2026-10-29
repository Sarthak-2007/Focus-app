# Issue #61 — Wave 3 Report (blockit-tower-views) · Cluster C9 · Lead: NACHLASS

Branch: `agent/issue-61` · Base: `origin/main` e29acdf
File: `app/src/main/java/com/example/ui/views/BlockitTowerFadersAndCatalogViews.kt`

## Verification against current main (before any fix)

- **TowerStreakView: DEAD.** `grep -rn TowerStreakView` on main: definition + ONE unused
  import (`AirlockAndDisarmOverlays.kt:62`). No construction site anywhere. Design revamp
  is in flight, so deletion — not repair of a surface nobody sees — is the truthful fix.
- **F1 CONFIRMED:** `totalBricksCount` clamped 1..70 (old :26-28); `totalFloors` coerced
  to max 8 (old :53) → at most 8×7=56 slots drawn; crown condition `brickNumber == bricks`
  unreachable for 57..70.
- **F2 CONFIRMED:** old :143-151 `onTouchEvent` returned `true` unconditionally and on
  ACTION_UP invoked `onBrickTapped(totalBricksCount)` — no hit-test, no brick identity.
  No caller ever set the callback.
- **F3 CONFIRMED:** fabricated default `totalBricksCount = 19` in dead code.
- **TaskHorizonBarView: LIVE** (`FocusSequencerScreen.kt:189`, testTag `total_horizon_bar`).
- **F4 CONFIRMED:** `estimatedMinutes.coerceAtLeast(10)` in both total and weight (old :194/:198).
- **F5 CONFIRMED:** `availW` subtracted `gap*(size-1)` and was coerced UP to ≥40dp while
  segment count was unbounded — many tasks → segments march past the right edge.

## Dispositions

| # | Finding | Disposition | Evidence |
|---|---------|-------------|----------|
| 1 | Tower lies above 56 bricks [CRITICAL] | **FIXED by deletion** | TowerStreakView removed entirely; no capacity claim can exist without the view. grep: zero code references remain. |
| 2 | onBrickTapped fake [HIGH] | **FIXED by deletion** | Callback + touch-consuming `onTouchEvent` removed with the view; evidence showed no consumer ever existed, so removal (not a hit-test for nobody) is the honest route per brief. |
| 3 | Dead code + fabricated default 19 [HIGH] | **FIXED by deletion** | Class + unused import in AirlockAndDisarmOverlays.kt removed; fabricated default dies with it. |
| 4 | Horizon 10-min padding [HIGH] | **FIXED** | New pure `HorizonLayout.plan`: widths ∝ real minutes; sub-minimum / 0-min segments pinned to exactly minMarkerW and flagged `isMinMarker`, rendered as outline-only markers (visibly distinct, no minute figure) — readability without fake duration. Proven by `HorizonLayoutTruthTest.proportional_noSilentTenMinuteFloor` + `proportional_exactRatioWhenAboveMinimum`. |
| 5 | Horizon off-canvas overflow [MEDIUM] | **FIXED** | Capacity = how many min-markers+gaps fit; tail aggregates into ONE overflow segment (honest tail-minute sum, "+N" label); invariant sum(widths)+gaps ≤ availW asserted in `manyTasks_degradeToHonestOverflow_neverExceedCanvas` (100 tasks @360px); renderer additionally clamps every rect to `w-pad`. |

## Seat verdicts (20 seats, seat-bundle under NACHLASS)

- **Crazy-ideas** — Signed. Proposed deleting the tower outright and a "+N more" tail segment; both adopted. Wilder idea (tower as launcher) deferred to the revamp, not this truth slice.
- **Maximum-implementation-potential** — Signed. Extraction of `HorizonLayout` as pure testable maths is the max-leverage move: truth becomes provable without a Canvas.
- **Brutal critic** — Signed. The old tower was a lie with a paintbrush: 70 claimed, 56 drawn, crown conditional on an impossible value, tap handler reporting the total as if it were a brick. Deletion is the only verdict it earned.
- **Improver** — Signed. Water-filling allocator replaces ad-hoc padding; invariant is now stated in code and tested.
- **Designer** — Signed. Min-markers read as outline ticks vs solid segments — the distinction is visual, not a comment. Fonts untouched (Sora/Atkinson/Anonymous Pro via existing tokens); no green introduced; no AI images.
- **Consumer** — LEAD-COVERED (thin): a consumer never saw the tower (dead) and now sees horizon widths that match the numbers printed on them.
- **Student** — LEAD-COVERED (thin): nothing student-facing changes except the queue bar stops overstating short tasks.
- **User** — Signed. Short tasks no longer look 10× bigger than they are; a 1-minute task stops stealing bar from a 100-minute one.
- **User-experience** — Signed. Overflow "+N" keeps the queue legible at 100 tasks instead of bleeding off-screen; touches are no longer swallowed by a dead view (it is gone, and it was never in a layout anyway).
- **Purpose-questioning** — Signed. Asked why keep a streak tower with no data source: no answer existed. Purpose restored by removal; a future tower must be born wired to real stats.
- **Thoughtful** — LEAD-COVERED (thin): deletion over repair chosen deliberately, with the reasoning recorded in the file header for the revamp team.
- **Deep-thinker** — Signed. Root pattern across all 5 findings: presentation invented data (count, tap identity, streak, duration, layout). Fix pattern: one source of truth (`HorizonLayout`) or no surface at all.
- **Enthusiastic designer** — Signed. The outline-marker language gives the revamp a honest visual vocabulary for "present but unmeasured."
- **Wow-level designer** — LEAD-COVERED (thin): no wow claimed for a deletion + a bar; the win is that nothing on screen lies, which is the precondition for wow.
- **Designer-29-years** — Signed. Twenty-nine years says: dead code with a fabricated default is not a placeholder, it is a future bug report. Cut it.
- **Intentionality designer** — Signed. Every remaining pixel in the horizon bar is intentional: solid = measured duration, outline = presence marker, "+N" = counted tail.
- **Synchronous collaboration** — LEAD-COVERED (thin): no cross-issue conflicts found; Airlock import removal is the only touch outside the target file.
- **Implementation-quality** — Signed. Pure-function layout, 5 unit tests covering ratio, floor, zero, overflow, and degenerate inputs; no local compile claimed — CI decides.
- **Technical execution specialist** — Signed. Commit hygiene: batched commits, early push, CI read to verdict; grep proof of zero remaining references included above.
- **Interaction-detail specialist** — Signed. F2's touch theft is moot post-deletion (view was never laid out), stated plainly rather than dramatised; horizon keeps its existing night-mode marker line untouched.

## Tests

`app/src/test/java/com/example/HorizonLayoutTruthTest.kt` — 5 tests (see Dispositions).
CI verdict + run URL: recorded in the closing issue comment.
