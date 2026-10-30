# Issue #102 — Audit R3 · peek-lived — Wave-3B builder-verifier report

Verified against origin/main @ 0a00e95 (cell branch `agent/issue-102`).
Audit line numbers are from an older tree; all citations below are
file:line as they are NOW (pre-fix tree first, then the fix).

All five findings HELD on current main — none was cured by the
Wave-2 (#11) held-door work (that work fixed the offer's truth —
which doors may sell a peek, UNA2-01/06 — but never drew the pill,
never enforced expiry, and never handed off).

## 1. [CRITICAL] Floating countdown pill never exists — FIXED-IN-CODE

Pre-fix evidence: `floatingPeekPill` (MonolithViewModel.kt:255-256)
had zero collectors in the whole tree (grep: only the VM itself);
MainActivity collected ~45 flows but never this one; no composable
or view rendered it; `end2MinutePeekEarly` (MonolithViewModel.kt:2418
pre-fix) had exactly one caller, the VM expiry ticker (line 994).

Fix:
- New `service/PeekPillOverlay.kt`: a draggable, edge-snapping
  WindowManager system-overlay pill ("PEEK" + M:SS countdown +
  ✕), drawn by the shield service over the peeked app itself —
  the place the user actually is during the 2 minutes. Draws only
  when `Settings.canDrawOverlays` is granted (the permission is
  already declared and already part of the Guard checks).
- New `ui/components/PeekPill.kt` `FloatingPeekPill`: the same
  pill's in-Monolith Compose face, collected and rendered in
  MainActivity (collect at MainActivity.kt, render after the
  Disarm overlay), its ✕ wired to `viewModel::end2MinutePeekEarly`
  — that function's first UI caller.
- Both faces derive their countdown from the Room lease's stored
  end (VM state via UNA2-06 derivation; overlay via the shield
  watcher's row read) — never a second clock.
- The overlay ✕ (MonolithShieldService.onPeekPillEndTapped) makes
  the same `recordPeekEnded` write as the VM's early end and
  brings the airlock up over the peeked app: the hand-back.

## 2. [CRITICAL] At 2:00 nothing cuts you off — FIXED-IN-CODE

Pre-fix evidence: MonolithRepository.kt:564-582 (pre-fix)
`queryForegroundBlockedApp` filtered `lastTimeUsed >= end - 8_000`
— arithmetically blind to a user who entered at peek start;
MonolithAccessibilityService re-checked the lease only inside
onAccessibilityEvent and its config subscribes only to
typeWindowStateChanged|typeWindowContentChanged — a still screen
(video/photo/reading) generates neither.

Fix:
- New `MonolithRepository.queryCurrentForegroundPackage()`
  (MonolithRepository.kt:585): reads the usage-events stream —
  the package of the most recent foreground-resume event in a
  30-minute window (`FOREGROUND_EVENTS_WINDOW_MILLIS`, line 114).
  A resume names the foreground until another app resumes over
  it, so a still-screen user is still correctly seen.
- `queryForegroundBlockedApp` (line 609) consults the events read
  FIRST, keeping the legacy 8-second stats read as fallback.
- New shield-service peek watcher `startPeekWatcher`
  (MonolithShieldService.kt:235): a 1-second beat that observes
  the live lease; on the tick the lease dies, if the foreground
  package is still the peeked package (`peekExpiryShouldEvict`,
  PeekPillOverlay.kt:230 — pure, unit-tested), it launches the
  intercept door over the user. That launch IS the 2:00 cut-off.

## 3. [HIGH] Tapping the peek strands you inside Monolith — FIXED-IN-CODE

Pre-fix evidence: `start2MinutePeek` (MonolithViewModel.kt:2384
pre-fix) hid the airlock, set pill state, wrote the lease — and
stopped. The only `getLaunchIntentForPackage` in the tree was
MainActivity's launcher for OPEN-tier apps, not the peek grant.

Fix: the grant's own coroutine now launches the intercepted
package AFTER `repository.startPeekLease(pkg)` lands
(MonolithViewModel.kt, the HAND-OFF block at ~2415), so the
enforcing doors read a live lease when the app foregrounds and
honour it instead of re-intercepting mid-hand-off. A package with
no launch intent leaves the user in Monolith with the pill
running and its ✕ to hand back — never a lease burning invisibly.

## 4. [MEDIUM] Return from a peek unexplained for 5 seconds — FIXED-IN-CODE

Pre-fix evidence: AirlockAndDisarmOverlays.kt gated BOTH the
offer block (line 663 pre-fix) and the withheld-reason block
(line 690 pre-fix) on `countdownSecs == 0`, with countdownSecs
re-seeded to 5 on every intercept (lines 560-571 pre-fix).

Fix: the ACTION stays behind the wall (the peek chip still
renders only at countdown 0 — the 5-second breathing delay is
the door's design, HOLD law: the cure is explanation, not a
weaker wall), but the EXPLANATION no longer waits: the price
line renders whenever a peek is offered, and the withheld-reason
block (AirlockAndDisarmOverlays.kt:720) is no longer gated on
the countdown — a user yanked back at expiry sees
"PEEK COOLDOWN — AGAIN IN N MIN" from the first frame.

## 5. [LOW] 'EMERGENCY PEEK' borrows the weekly token's name — FIXED-IN-CODE

Pre-fix evidence: the chip printed "EMERGENCY PEEK" and the only
term was `peekPriceText` (cooldown only) — nothing said the peek
does not spend the EMERGENCY BREAK or end the session.

Fix: new pure `peekTermsText()` (PeekPill.kt:40) —
"THE PEEK DOES NOT SPEND YOUR EMERGENCY BREAK AND DOES NOT END
THE SESSION" — printed on the offer directly under the price
line (AirlockAndDisarmOverlays.kt:706), from the first frame
(per finding 4's ungating).

## Tests

New `app/src/test/java/com/example/PeekLivedTest.kt` (5 tests):
expiry-eviction decision (still-inside vs left vs unknown vs
blank), peek terms wording, pill countdown format. Not compiled
locally (RAM law) — CI on the branch is the proof.

## User-facing behaviour changes (plain words)

- Taking a peek now takes you straight into the app you peeked.
- A small pill with the countdown floats over the peeked app;
  tapping its ✕ ends the peek early and brings you back to the
  focus door. The same pill shows inside Monolith itself.
- At 2:00 you are now actually cut off: the focus door appears
  over the app even if you never scrolled or tapped.
- After a peek, the door immediately says a peek cooldown is
  running and for how long — no more unexplained 5-second wall.
- The peek offer now states plainly that a peek does not spend
  your weekly emergency break and does not end your session.

## ADDENDUM — main-merge composition (WELLE-3 cell, post-PR #351)

Merging current origin/main (67e312f, incl. #99 via PR #235) produced 3
conflicts, composed — not chosen:
- MainActivity.kt: #99 had already landed the floatingPeekPill collect;
  #102's render call composes on top. One collector, one render.
- MonolithRepository.kt: BOTH foreground reads kept — #102's
  queryCurrentForegroundPackage() (usage-events) now runs FIRST inside
  #99's two-arg queryForegroundBlockedApp (expired-peek freshness
  waiver retained as fallback).
- AirlockAndDisarmOverlays.kt: withheld-reason gate composed as
  `if (!peekReady)` — #99's unlock derivation, without the countdown
  gate (finding 4: reason visible from frame one).
- MonolithViewModel.kt: finding 3's hand-off is #99 F4's grant-time
  launch (already on main); #102's duplicate post-lease launch was
  removed in composition. F3 disposition: FIXED (by #99 on main +
  verified present here), not duplicated.
CI on the pre-merge branch (run 41012395725): assemble GREEN after
edbcb43 (missing layout.offset import, run 40985759415's only error);
309 tests, 37 failed — all 37 in the known pre-existing classes
(Boden floor, #166), zero PeekLivedTest failures, no new failures.
