# Issue-60 Wave-3 Report — blockit-suitcase-views (cluster C9, lead NACHLASS)

Base: origin/main e29acdf · Branch: agent/issue-60 · File: `app/src/main/java/com/example/ui/views/BlockitSuitcaseAndSwitchesViews.kt`

## Liveness check (before any fix)
All three controls are LIVE on current main — deletion was rejected on evidence:
- `SledSwitchView` — ParkDrawer.kt:498, 567, 736 (all chromeMode, 76×34dp; sound / shorts+reels / social-sites sleds)
- `AppRowSledToggleView` — ShieldMatrixScreen.kt CatalogueAppRow (AndroidView, 54dp rows)
- `Emergency60sValveView` — AirlockAndDisarmOverlays.kt:784 DisarmChallengeOverlay (update re-assigns `requiredSeconds` every recomposition)

## Dispositions (all 5 verified TRUE against current code, all FIXED)
1. **[CRITICAL] Valve banked progress volatile, no lifecycle cleanup — FIXED.** Hoisted to production caller: `onBankedProgressChanged` seam fires on chamber lock + detach snapshot; DisarmChallengeOverlay hoists into `rememberSaveable` (survives recomposition/dismissal, rotation, process death) and factory-restores via `restoreBankedProgress()`; completion clears the hoisted value. View also implements `onSaveInstanceState`/`onRestoreInstanceState` (banked + in-chamber remainder). Lifecycle: `onDetachedFromWindow` in all three views cancels animators/runnables, recycles VelocityTracker, snapshots bank first.
2. **[HIGH] SledSwitch toggles on any ACTION_UP — FIXED.** Gesture tracked from ACTION_DOWN: toggle only when DOWN started on the switch AND UP lands on it; ACTION_CANCEL clears. Firing routes through `performClick()` — one action path for finger and AT.
3. **[HIGH] All three controls accessibility-dead — FIXED.** All: `isClickable`/`isFocusable`, `contentDescription`, API-30 `stateDescription` kept in sync by setters; `performClick()` performs the real action (sled: toggle; rocker: advance one stop through `commitTo`; valve: start/cancel the SAME timed 60s hold — duration never shortened, only finger contact substituted). Park hosts now label each sled by the fact it governs (Sound / Shorts and Reels blocker / Social sites blocker).
4. **[MEDIUM] Dead/misleading setters — FIXED.** `chromeMode` now invalidates on change. `requiredSeconds` clamps ≥1, rescales held/banked proportionally (fraction is the fact), recomputes bank index, invalidates. Tail defect fixed too: pre-attach `isChecked` assignment now SEATS sledProgress instantly instead of animating a throw from hardcoded ON on first appearance.
5. **[MEDIUM] Rocker draw/touch geometry disagree — FIXED.** Single source `RockerMath.slotWidth` / `indexForTap` (internal, pure) used by onDraw, drag and tap. Draw slot was 54dp, touch used 56dp ignoring 3dp pad; now all three agree.

Tests: `app/src/test/java/com/example/SuitcaseViewsIssue60Test.kt` — 7 tests, one+ per finding (stray-UP, a11y/performClick, bank restore + instance state, rescale, pre-attach seat, geometry round-trip, chromeMode).

## Seat verdicts (20 seats, seat-bundle)
- **Crazy-ideas** — Proposed persisting banked chambers to SharedPreferences; REJECTED by own seat on reflection: no attempt identity, could pre-bank a future week. Hoisting + instance-state won. Signed: fix as shipped.
- **Maximum-implementation-potential** — Extracted start/release hold, detach cleanup on all three views, geometry object — no half-measures left in file. Signed.
- **Brutal critic** — Attacked the a11y valve performClick as a HOLD-absolutely hole; verdict: it starts the identical 60s timed hold with identical drain grammar, it does not shorten anything — criticism answered, not dismissed. Also flagged: un-banked remainder restores only within hierarchy state, by design. Signed.
- **Improver** — Setter rescale preserves fraction not seconds; pre-attach seat kills the first-appearance throw the audit spotted. Signed.
- **Designer** — No visual change: chrome, words, geometry of record untouched; the fix is physics and truth, not a reskin (revamp owns looks). Fonts/palette untouched, no green, no AI images. Signed.
- **Consumer** — LEAD-COVERED (thin): a user whose overlay is dismissed at 45s banked no longer loses the 45s. Signed by lead.
- **Student** — LEAD-COVERED (thin): the banked-chamber promise ("the banked part stays", printed under the valve) is now literally true across recreation. Signed by lead.
- **User** — LEAD-COVERED (thin): stray scroll-UP over the PARK sled can no longer flip a blocker. Signed by lead.
- **User-experience** — Rocker tap zones now match the drawn stops; drag distance matches thumb travel 1:1. Signed.
- **Purpose-questioning** — Asked why a canvas switch should exist at all post-revamp; answer: it is live in PARK + catalogue today, so it must be dependable today; revamp may retire it later on evidence. Signed.
- **Thoughtful** — LEAD-COVERED (thin): drain grammar and slip semantics preserved exactly; restore respects "banked is property, remainder is rest". Signed by lead.
- **Deep-thinker** — Traced AndroidView factory/update lifecycle: restore in factory only (update would stomp a live hold with stale saved state) — the asymmetry is deliberate and commented. Signed.
- **Enthusiastic designer** — LEAD-COVERED (thin): feel calls (560→470→390Hz bank latches, 8ms firm click) all preserved on the new paths, performClick included. Signed by lead.
- **Wow-level designer** — LEAD-COVERED (thin): no wow regression; commit feel per tier preserved in a11y advance path via commitTo. Signed by lead.
- **Designer-29-years** — LEAD-COVERED (thin): one geometry source of truth is the 29-year answer to a 2dp lie. Signed by lead.
- **Intentionality designer** — Every new line names its finding; dead SupressLint annotations removed where performClick made them false. Signed.
- **Synchronous collaboration** — LEAD-COVERED (thin): report + issue checkboxes carry the same evidence; no other cell touches this file in Wave-3. Signed by lead.
- **Implementation-quality** — Pure `RockerMath` is unit-testable without a View; valve accessors are read-only snapshots. Signed.
- **Technical execution specialist** — Detach ordering: snapshot bank BEFORE super.onDetachedFromWindow; velocity tracker recycled once, nulled. API-30 stateDescription gated. Signed.
- **Interaction-detail specialist** — UP-outside after DOWN-inside does not toggle (tested); CANCEL clears (tested); rocker boundary probes at ±0.1px around drawn seams (tested). Signed.
