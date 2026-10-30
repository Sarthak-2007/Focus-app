# FASSUNG — TECHNICAL EXECUTION — Issue #6 (Deck audit fixes F1–F22)

**Role:** technical-execution specialist, Deck + Shield surfaces.
**Status:** DECLARED — spec only, no `app/src/` edits, nothing compiled.
**Read before writing:** issue #6 + all 7 comments; `AUDIT-deck.md` §9
(F1–F22); `ISSUE-6-verdict.md`; `ISSUE-6-WAVE2-REPORT.md` incl. addenda;
`ISSUE-6-finisher-3.md` (F16/F18 skeleton); the Deck host
(`TactileDeckScreen.kt`), the Canvas views (`BlockitCoreViews.kt`), and the
session entity (`MonolithModels.kt`).

Wave-2's status stands and I do not re-price what is closed (F1, F3, F11b,
C3, and the addendum-3 CODE-NOW pool). This file prices the **mechanism**
under what remains, in the merge order ANTLITZ proposed (F17 table → F4/F5 →
F16+F18 → F22), and corrects two prices the audit guessed without the code.

Prices: **S** = a sitting's edit · **M** = a lane (~½–1 builder-day) ·
**L** = a restructure.

---

## 1 · The fact that reprices F16 + F18: the wheel is not Compose

`RotaryWheelView` is a custom Android `View`
(`BlockitCoreViews.kt:371`, `onDraw` at :570, ~2,400-line file of Canvas
views) hosted in the Deck via `AndroidView`. There is no Compose dial to
restyle. Both restructure items are therefore **View surgery + Compose
overlay work**, and anyone who prices them as Compose recomposition will
under-price them.

### F18 — Fusion (hero into the hub; dial as its own clock; rim-ink progress)

Build it as three separable mechanisms:

1. **Hub print channel — use a Compose overlay, NOT Canvas text.**
   Finisher-3 §7 flag: the hero glyph today is a Compose `Text`
   (`DialClockTextStyle`, Type.kt) while `RotaryWheelView.onDraw` draws the
   hub as pure geometry — "no numeral, no word". Two ways to give the hub a
   voice:
   - *Canvas `drawText`* inside `onDraw`: needs paint typeface loading,
     manual centring by optical centre, manual font-scale behaviour — and
     the glyph then lives inside the hub's press-translated group unless
     drawn outside the `canvas.translate` block (:853–854), which is exactly
     the trap finisher-3 flag 6 names.
   - *Compose overlay* (recommended): the hosting `Box` already stacks the
     `AndroidView`; add the glyph + sub-line as Compose `Text`s aligned by
     centre offset (ADDR-HUB-GLYPH / ADDR-HUB-SUBLINE dp addresses from
     finisher-3). The overlay is **outside the press translation by
     construction** (flag 6 solved for free), keeps the house type styles
     and semantics, and costs no paint plumbing. Hit-geometry stays in the
     View (overlay Texts are non-interactive by default — set no clickable;
     they must also be `importantForAccessibility` merged into the hub node,
     see F22).
   **Price: M** for the overlay + state-by-state print table (idle dialled /
   proving frozen / running ticking / grace / break / FLOW count-up).
2. **Rim ink — one new View property, stepped by the session clock.**
   `var rimProgress: Float` on `RotaryWheelView` + `invalidate()` in its
   setter; `onDraw` adds one `drawArc` (start 108°, the rim's own
   convention; sweep = 360° × progress, per finisher-3 §4). The host feeds
   it from the **same `progressFraction` F3 just made honest**
   (elapsed/target, no floors, FLOW = no arc). Updates arrive on the
   active-session Flow's cadence — the arc steps per emission (per-second
   class), it does not sweep continuously. That is the honest motion for a
   clock that ticks in seconds; do not add a frame animator to fake
   smoothness. **Price: S** (property + arc + host wiring in the existing
   `AndroidView.update` block — with the guard in §3 below).
3. **Hero retirement + Δy = 0.** Deleting the hero card moves the wheel up;
   finisher-3's composition law is that the hub glyph's address does not
   move between idle and running (Δy = 0). In overlay terms: the glyph's
   offset is a constant, only its *content* changes
   (`AnimatedContent` on the hub state, house curves, no positional
   animation). **Price: S–M**, most of it deleting carefully (the hero
   carries the grace/break/finish frozen readouts — each needs its hub
   row in the print table before the card dies).

**F18 total: L as the audit said — confirmed, but the risk sits in the
print table and the deletions, not in the arc.**

### F16 — the proof drawer (strip + sentence become state-gated)

The Bolt (issue #4) now occupies the band, so F16's remainder on the Deck
is the **sentence slot**: health = collapsed, fault = the sentence stands.
Technique: the pinned column keeps the slot **always composed** with
`AnimatedVisibility(visible = fault, enter = expandVertically(tween(280,
EasingA)), exit = shrinkVertically(tween(160, EasingThrow)))` — the
Shield editor's ruled expand/collapse prices (280 A / 160 Throw) reused,
so the app has one drawer grammar. Contents = the existing
`RefusalSentenceSlot` (its cause derivation is also the Bolt's JAMMED
input — see my issue-4 spec §2; land the enum refactor once, feed both).
**Price: M. Risk:** pinned-height changes move the scroll column's top —
that is the design's intent (fault gets more room than health), but the
slide register below must **not** move: the pinned stack order is Bolt →
sentence slot → slide, and the slide is pinned beneath the scroll column,
so expansion eats scroll viewport, not slide position. Verify that layout
claim in the merge diff, not by eye on a render.

## 2 · C4 — the stored-end stamp (escalated; here is the actual machinery)

Critic's charge stands in code: the running END is **derived per frame** —
`endsAtText(nowFrame + activeSession.spanRemainingSeconds * 1000L)`. If
the per-second ticker stalls (process frozen, Doze), the printed end
slides with the stall. The entity already carries the precedent:
`windowStartMillis / windowEndMillis` — a **frozen** end, but only
routine sessions populate it; hand sessions have no stored end.

**Build:** one column `sealedEndWallMillis` (DB is at **v12** on the
issue-7 branch — this is migration 12→13, following the established
chain-test pattern in `RoutinesMigrationChainTest` /
`SliceEMigrationChainTest`):
- **Write at the seal:** in the repository's seal path, stamp
  `startWall + spanTargetSeconds × 1000` in the same transaction that
  captures the seal freeze.
- **Re-stamp at every break exit:** breaks taken early/late move the real
  end, and the repository already books `breakTakenSeconds` at break exit
  (`MonolithModels.kt` span arithmetic, ST-02) — re-stamp
  `end += (taken − assumed)` in that same write. Detention/finish freeze
  is a separate mechanism (`FinishState.frozenDisplaySeconds`) and must
  not read or write this column.
- **Read:** host prints the stamp when present; falls back to today's
  derivation for rows sealed before the migration (their stamp is 0 —
  print derivation, never a 1970 end).

**Price: M** (migration + two write points + read switch + migration-chain
test). **Risk:** a missed re-stamp path (e.g. Safe Break exit vs block
break exit are different code paths — census both before landing) prints a
*confidently wrong* end, which is worse than today's honestly-derived one.
The fallback law above is what keeps the change safe to land.

## 3 · C5 — shared machinery, counted once

The durable cure (post-seal shield re-check) is the same fix as issue #4's
B-C1: the fold consumes the VM's existing Shield derivation
(`computeShieldBlockingStatus`, refreshed per second). Priced in my
issue-4 spec §3 (S–M); **do not price or build it twice.** Wave-2's C5(b)
asymmetric degrade on this branch is subsumed by the fold at the merge
(see the merge warning there).

**AndroidView interop guard (applies to F18 and every Deck View change):**
the `AndroidView.update` block re-runs on recomposition; every setter
called there must be change-guarded (`if (view.x != new) view.x = new`),
or a per-second session emission triggers `invalidate()` storms through
setters that assign unconditionally. The existing views mostly follow
this; the new `rimProgress` setter must too (float equality is fine —
the value only changes per second).

## 4 · The remaining S/M items — technique in one line each

| Item | Mechanism | Price |
|---|---|---|
| F2 FLOW cap kill | Deletion of the key-cap as a control + F4's printed recipe as its successor; rides F16/F18 so the hero zone is touched once. Data: dialled blocks × minutes already host state. | S (with F18) |
| F4 eyebrow recipe | Pure print derivation from `dialMinutes / studyBlocksCount` host state; routine-born name from `activeSession.profileName` ("Routine: X" prefix — same parse as Shield's `shieldSessionLabel`). | S — landed in addendum 3; verify at merge |
| F5 break hero | `AnimatedContent` keyed on `isInSafeBreak`; countdown from `safeBreakSecondsRemaining` (the span clock, entity-carried), **not** per-block `remainingSeconds` — that misread is the defect. Under Fusion this becomes the hub print table's BREAK row — build it there, not twice. | S (as hub row) |
| F6 STOP chair | Layout only: chips `weight(1f)` across the 72 dp zone (addendum 3 did 58→72 dp width half); whole-session STOP verb + hero countdown rides the session Flow cadence. No new state. | S–M |
| F7 task ghost | Print-state change on existing task row data (`activeTask` Flow already collected host-side); sub-steps/QUEUE move is a Tasks-screen change — **outside this cell's files**, flag for Ren at merge. | M |
| F10 hero single-press | One tokenised shadow: `Modifier.shadow` is banned in this house's bar grammar — the ghost is a drawn offset shape (`drawBehind` / layered Box), deleted catch-ghost already in addendum 3. Under Fusion the press target is the hub — the ghost becomes the hub's single press token. | S (with F18) |
| F11 card shadow truth | Day: drawn 2 dp offset shape; night: tone step only. No elevation API. Rides F17 tokens. | S |
| F12 night ink step-down | Token value change (`inkControlFill` night → `#2A2320`) — addendum 3 landed the value half; the Lamp Test staircase verification is a render-battery item, not code. | S — verify at merge |
| F13 radii 8→12 dp | Constant sweep on key-caps (DONE landed addendum 3); the audit's "four other screens" violations are **not this issue's files** — census list to Ren, don't silently expand scope. | S |
| F14 group-by-silence + scroll fade | Spacing constants + a **vertical gradient mask** at the scroll's bottom edge: `Modifier.fadingEdge`-style via `graphicsLayer` + `Brush` mask on the scroll column — no new dependency. | S–M |
| F15 vertigo-to-ground | `graphicsLayer { scaleX = scaleY = 0.98f }` on the page behind + blur **only API 31+** (`RenderEffect.createBlurEffect`, `Modifier.graphicsLayer { renderEffect = … }`); **minSdk is 24**, so below 31 the ground recedes by scale + dim alone — gate it, never crash, never fake the blur. Curves/durations unchanged (1050 ms house set). On Sarthak's phone (Android 14) the full effect runs. | S–M |
| F17 token table | Mechanical sweep of raw hex in Deck/components/views into scheme/token roles (TON's table on issue #4 is the pattern). Gates F10/F12 credit — land first at merge, as ANTLITZ sequenced. | M |
| F19 receipt dismissal | `ReceiptSheet` scrim: replace scrim `clickable` dismiss with a shake (offset keyframe 180 ms, no dismiss) + dismiss only via slip-swipe (`detectVerticalDragGestures` threshold) / PARK / rail. Precedence: Receipt A7 lands before F19 (wave-1 ruling). Files: `ReceiptSheet.kt` + host — outside the Deck file, same branch is fine. | S–M |
| F20 rail PARK print | `MonolithRailView` is a Canvas View — the word is `drawText` in its `onDraw` **or** a Compose label in the rail's hosting Row; the rail host is Compose, so print it in Compose (house type, semantics free). Do not add a text paint to the View for one word. | S |
| F21 comment truth | Text-only sweep; addendum 3 did the Deck sections. | S — verify |
| 03:30 idle-evaluates-to-DAY (Feel-Licht) | **Evaluation-level, correctly refused as a Deck patch:** day/night evaluation lives in `MainActivity` (`effectiveNight = darkTheme \|\| sealed`, `groundNight`, Theme's `isFocusSessionActive`) — a Deck-local hack would fork the evaluation. If Ren rules it: one change at the host evaluation (time-aware idle), priced **M**, app-wide blast radius, every screen's night grammar re-verified. Not this cell's call. | M (Ren) |

## 5 · F22 — the accessibility debt, mechanised (so it stops being a slogan)

The Deck's three Canvas surfaces each need the Shield sled's treatment
(my finisher spec on issue #7, §4.3, is the pattern):

- **Wheel (`RotaryWheelView`):** one adjustable node — `RangeInfo`
  (5–180 min), scroll actions step the dial, custom action "Start" —
  set via the View's existing setters so TalkBack and touch share one
  commit path. Plus the hub overlay Texts merged into its node (F18 §1).
- **Slide (`SlideToStartView`):** one button node, custom action =
  the same latch path the finger triggers; state description carries
  the warrant word (LIVE/UNPROVEN/OFF) so the ear gets the gate the
  eye gets.
- **Rail (`MonolithRailView`):** four button nodes (virtual hierarchy —
  here it *is* worth it: four destinations, one View) or, cheaper,
  the rail's Compose host supplies four semantics nodes over the View's
  hit rects. Recommend the host route: no `ExploreByTouchHelper` in a
  2,400-line Canvas file to keep in sync.

**Price: L as the audit said — confirmed.** It is three different
mechanisms, not one pass. It can land **after** F16/F18 (the hub node's
semantics are part of F18's print table) but the issue does not close
without it — wave-2's carried-debt wording stands.

## 6 · Sequencing verdict for the merge (technical, for Ren)

1. F17 token table (gates F10/F12 credit).
2. Issue-4 fold + C5/B-C1 shield input (one machinery change, two issues).
3. C4 stored-end stamp (migration — land it in the same batch as any
   other DB change; DB is v12, this makes v13, one migration per batch
   is the cheap way to keep the chain tests readable).
4. F4/F5 as hub print rows **inside** F18, not before it.
5. F16 sentence drawer (consumes the JAMMED enum refactor from #4 §2).
6. F22 last, on the finished shapes.

## 7 · What I want next (unassigned capacity)

- A setter-guard lint for the Canvas views (unconditional `invalidate()`
  in `update`-called setters) — the interop storm in §3 is a class, not
  an incident.
- The F15 API-gate matrix written as a test (31+ blur / <31 scale-only)
  before F15 lands, so the fallback is proven, not hoped.

— Fassung, technical execution
