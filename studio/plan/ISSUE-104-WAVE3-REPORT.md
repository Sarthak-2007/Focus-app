# Issue #104 — Audit R3 · safe-break-lived — Wave-3B report

Builder-verifier seat, branch `agent/issue-104`, base `origin/main` @ `0a00e95`.
Every finding re-verified against CURRENT main by symbol/content search
(audit line numbers were from an older tree; current lines cited).
**All 5 findings HELD. All 5 fixed in code. No local compile (per orders) —
CI on the branch is the verdict.**

---

## 1. [HIGH] Guard board prints PAUSED for interceptors that keep intercepting — HELD → FIXED (board now prints the truth)

Evidence (current main, pre-fix):
- `WatchStore.kt:469-474` — `deriveInterceptor` set `suspended = safeBreakActive`
  for every non-NOTIFICATIONS interceptor, deriving `InterceptorState.PAUSED`
  during a Safe Break; `ParkDrawer.kt:358-365` prints that word verbatim next
  to SHORTS + REELS and SOCIAL SITES.
- `MonolithAccessibilityService.kt:230` (sniper) and `:244-268` (URL blocker)
  never read `isInSafeBreak` — only a live peek lease gates them.
- **Documented Safe Break semantics in the tree** (the deciding fact):
  the accessibility service's own class doc, `MonolithAccessibilityService.kt:27-35` —
  the sniper "Remains strictly active even during Safe Breaks", blocked domains
  block "even during Safe Breaks", and during a break "Locked apps, Shorts,
  Reels, and blocked browser domains stay strictly locked"; only the per-app
  PEEK tier unlocks (`:277-280`).

Disposition: the enforcement side is the documented promise, so the LIE was
the board. Fix on the print side (not a downgrade — the interceptors, their
board lines, and the PAUSED vocabulary all remain; PAUSED is simply no longer
derived from a suspension that does not exist):
- `WatchStore.kt` — the `safeBreakActive` input, its field, and the suspension
  term in `deriveInterceptor` are deleted; derivation (`:461-484`) now returns
  RUNNING whenever setting-on + parents granted, in and out of a break.
  Comments at the inputs block and the derivation doc state the audit catch.
- `MonolithViewModel.kt:820-824` — the dead `safeBreak =` push into
  `updateInterceptorInputs` removed.
- `WatchStoreRobolectricTest.kt` — the PAUSED-under-break assertions replaced:
  the test now derives from settings + parents only and records why no
  suspension input exists.

User-facing: during a Safe Break the guard board now reads RUNNING for
SHORTS + REELS and SOCIAL SITES — which is what actually happens if he opens them.

## 2. [HIGH] Held notifications stay held through the whole Safe Break — HELD → FIXED (reality now matches the unlock)

Evidence: `MonolithNotificationListener.kt:82-108` (pre-fix) checked only
`isActive` + tier ≠ OPEN before digest-insert + `cancelNotification`;
`isInSafeBreak` was read nowhere in the file. A user chatting inside a
break-unlocked PEEK app (e.g. WhatsApp) got no notification for its messages.

Fix: new pure rule `notificationHeldDuringSession(isInSafeBreak, tier)`
(`MonolithNotificationListener.kt:34-35`, used at `:91`): during a Safe Break,
PEEK-tier notifications are NOT captured or cancelled — they post normally,
mirroring the accessibility service's Safe Break rule for the same apps.
LOCKED apps stay held through the break, exactly as they stay locked; OPEN
was never held. The capture interceptor itself keeps running (the Watch
Store's NOTIFICATIONS line stays truthfully RUNNING).

User-facing: messages to a PEEK app during the break now arrive with their
normal shade/banner instead of silently accumulating in the HELD digest.

## 3. [HIGH] Break ends as an unannounced hard cut, no usable final countdown — HELD → FIXED

Evidence: expiry flip in `MonolithRepository.kt:1991-2020` (break branch of
`updateSessionProgress`) crossed via `advancePastBreak` (`:1846-1888`) whose
only boundary signal was `breakBoundaryKnock` (`:1872`) — a sound-gated latch
click; the shield notification is IMPORTANCE_LOW + `setOnlyAlertOnce`
(`MonolithShieldService.kt` channel `:203-213`, builder pre-fix). Deck eyebrow
`TactileDeckScreen.kt:615-616` printed `BREAK · ${(remaining + 59) / 60} MIN` —
the entire final minute read "1 MIN".

Fixes:
- **Warning + end announcement**: new pure transition machine
  `service/BreakAnnouncer.kt` (STARTED / FINAL_MINUTE / ENDED, once each,
  session-reset, mid-break-start safe), fed every shield iteration in
  `MonolithShieldService.kt:122-141`. FINAL_MINUTE fires at the first tick
  with ≤ 60 s left; ENDED fires on the flip (expiry and end-early alike).
  Delivery is `postBreakAlert` (`:274`) on a new IMPORTANCE_HIGH channel
  (`monolith_break_alert_channel`, id 4097) with a vibration one-shot —
  heads-up + buzz, in whatever app he is in; auto-cancel, 90 s timeout.
  Information only: the alert opens the app, never an exit (HOLD-absolutely).
- **Usable final countdown on the Deck**: `breakEyebrowText`
  (`TactileDeckScreen.kt:94-97`, used `:633`) prints exact seconds —
  "BREAK · 1:01 LEFT", "BREAK · 0:59 LEFT" — never a rounded-up minute.

## 4. [MEDIUM] Break starts in silence; its clock runs unannounced — HELD → FIXED (announcement; clock semantics deliberately unchanged)

Evidence: entry flip `MonolithRepository.kt:2022-2040` fired only
`breakBoundaryKnock(false)` (`:2039`); the countdown is wall-derived from the
stored (remaining, lastTick) pair (`WallAccrual.kt:23-52`,
`breakRemainingAfter`), so process-death gaps count as elapsed break.

Fix: the same announcer (#3) fires STARTED on the first shield tick that sees
the break — the shield loop is alive in every app, screen on or off, unlike a
UI observer — posting "SAFE BREAK STARTED · Break clock 10:00 — PEEK apps are
open. Locked apps, Shorts, Reels and blocked sites stay shut." plus a buzz.
A shield (re)start mid-break announces STARTED with the true remaining clock
rather than staying silent.
The wall-derived clock itself is NOT changed: it is Bus's documented fix for
the per-call double-drain, and a break really does elapse while the process
is dead. What was missing was never the clock — it was the announcement,
which now exists at both ends. The repository knock stays as the in-app
sound layer; it is no longer the only signal.

## 5. [MEDIUM] Desk clock renders the break invisible — HELD → FIXED

Evidence: `AirlockAndDisarmOverlays.kt:99-125` (pre-fix) — hero read
`spanRemainingSeconds` straight through the break and the comment stated the
law: "a Safe Break earns no special rendering (Nacht's law…)". No BREAK word,
no break countdown, nothing changed at the break's end.

Fix (the desk clock is a KEPT study display — elapsed + remaining is its job):
- Hero: during a Safe Break the big clock IS the break countdown
  (`safeBreakSecondsRemaining`, `AirlockAndDisarmOverlays.kt:113`); outside a
  break it returns to span remaining (stopwatch unchanged).
- New break line (testTag `desk_clock_break_line`, rendered at `:180`):
  `deskClockBreakLine` (`:277-286`) prints "SAFE BREAK · BLOCK n RESUMES AT
  00:00" (Anonymous Pro, cream — no green), clamped to the session's own
  block count; null outside a break.
- At the break's end the line vanishes and the hero returns to the span —
  the change is visible from across the room. The span progress bar is
  unchanged (it tells session time, in and out of a break).

Fonts: all new prints use the standing families (Anonymous Pro /
Atkinson Hyperlegible already imported in both files). No images added.

---

## Tests

- New `app/src/test/java/com/example/SafeBreakTruthTest.kt` (7 tests):
  notification hold/release matrix (#2); announcer lifecycle — start,
  final-minute once, end, end-early, mid-break start without duplicate
  warning, next-break re-arm, per-session reset (#3/#4); Deck eyebrow
  second-precision incl. 61 s / 59 s / negatives (#3); desk-clock break
  line presence + block clamp (#5).
- Updated `WatchStoreRobolectricTest.kt` (#1): suspension assertions removed
  with the lying input; derivation covered from settings + parents only.
- Not compiled locally per Wave-3 orders — CI on `agent/issue-104` is the
  verdict to read.

## Adjacent observation (not fixed — outside this issue's findings)

The notification listener also holds a PEEK app's notifications during a
live 2-minute peek *lease* outside a Safe Break, while the app is lawfully
open. Same shape as #2 but a different, shorter unlock; flagged for PFAD
to file or fold into a follow-up rather than silently expanding this fix.

---

## WELLE-3 re-verification addendum (Oct 30, 2026, cell under WELLE-3 lead)

- Re-based onto CURRENT main: merge commit `2fbfdba` brings in local
  origin/main `3ad6c43` (186 commits ahead of the original cell base
  `0a00e95`). All 5 findings re-checked post-merge: `BreakAnnouncer`
  (service/BreakAnnouncer.kt:33, single definition),
  `notificationHeldDuringSession` (MonolithNotificationListener.kt:34,91),
  `breakEyebrowText` (TactileDeckScreen.kt:94), desk-clock break line in
  AirlockAndDisarmOverlays.kt; WatchStore/MonolithViewModel carry no
  `safeBreak` suspension input anymore (grep: zero refs).
- Merge conflicts: only the same 4 shared test files as the sibling
  cells — resolved to main side; this branch never touched them.
- Findings: 5 total / 5 fixed / 0 struck / 0 blocked. Finding 5 (desk
  clock renders break invisible) is a truth/rendering fix on the Safe
  Break surface this issue owns — not a design complaint for re-routing;
  no #18 / owner-issue routing needed for any finding here.
- Seats (this issue): Lead 1 · Builder 5 · Critics 5 · Verifiers 5 ·
  Diagnosticians 5 · Finishers 5 · UX designers 5 · Day simulators 5 ·
  Scouts 1 · Keepers of Record 1 · Methods 1 · Fresh-eyes 1 · Feel 1 ·
  Small Things 1 · Flow 1 · Panel judges 1 · Intentionality designers 1 ·
  Implementation-quality 1 · Technical execution 1 · Interaction-detail 1 ·
  Purpose-questioner 1 · Consumer/Student/User lens seats LEAD-COVERED.
  Total seated ≥ 48 type-seats (lens bundles run inside this cell).
- Consultation: routed via WELLE-3 lead (09:57 correction). Adjacent
  observation in the main report (PEEK-lease notification hold outside a
  Safe Break) stays flagged, not silently expanded.
Signed: WELLE-3 cell (issue-104 seat bundle).
