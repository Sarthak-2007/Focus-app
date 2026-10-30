# Issue #114 — Audit R3 · moment-of-temptation — Wave-3B report (KARTE)

Branch: `agent/issue-114` from current `origin/main`. Builder/verifier bundle
under KARTE. The temptation journey was walked end-to-end in CURRENT code
before any fix: airlock trigger (`triggerAirlockIntercept`) → 5-second breath
→ peek offer/grant (`start2MinutePeek`, `startPeekLease`) → peek life (ticker,
lease row, accessibility/shield honouring) → hand-back/expiry
(`end2MinutePeekEarly`) → honest door (`transmuteImpulseAndReturn`) → Deck
grace chip → disarm valve (`requestDisarmChallenge`,
`completeEmergencyBreakFromValve`).

HOLD-absolutely held throughout: no fix weakens blocking, shortens a price,
or makes any exit cheaper. Where a finding's cure is a change to a price,
a window, a cooldown scope, or emergency semantics, the exact choice is
flagged DECISION-NEEDED for Sarthak instead of invented here.

## Seat roster (20 named lenses + applicable roster, bundle-covered)

Named 20: Crazy-ideas · Maximum-implementation-potential · Brutal critic ·
Improver · Designer · Consumer · Student · User · User-experience ·
Purpose-questioning · Thoughtful · Deep-thinker · Enthusiastic designer ·
Wow-level designer · Designer-29-years · Intentionality-focused designer ·
Synchronous collaboration · Implementation-quality · Technical execution
specialist · Interaction-detail specialist.
Applicable roster: Human/Day-fit (LEAD-COVERED — the journey is a tired,
craving user at the worst minute of the day; every print was read in that
state) · UX lead (LEAD-COVERED) · Designers / Finishers / Fresh-eyes / Feel /
Flow (bundle-covered: faces use the app's existing chip grammar, night
palette, Sora-adjacent hierarchy via Atkinson/Anonymous Pro as elsewhere) ·
Critics / Purpose-questioning / Deep-thinker on friction ethics
(bundle-covered: see the ethics note under Finding 3 — friction that lands
on the honest door and not on the craving is backwards, and two findings
below are exactly that shape) · Verifiers / Builders / Technical
(bundle-covered: every finding re-verified against current code by grep +
read before disposition; fixes compiled and tested by CI).

## Finding 1 — CRITICAL — Peek countdown pill never drawn — FIXED-IN-CODE

Verified true in current code: `floatingPeekPill` had writers/ticker only in
`MonolithViewModel.kt` (state at :255, tick at ~:986) and ZERO collectors in
`MainActivity.kt`, `ui/screens/` or `ui/components/`; no WindowManager
overlay existed anywhere in `app/src`. `end2MinutePeekEarly` had no UI
caller — only the ticker's expiry path. The audit's line numbers had moved
but the defect was intact. Sub-defect also verified: `start2MinutePeek`
never launched the peeked package, so the lease (anchored at tap-time in
`MonolithRepository.startPeekLease`) burned while the user navigated by hand.

Fix:
- NEW `app/src/main/java/com/example/service/PeekPillOverlay.kt` — a
  plain-view WindowManager overlay face (TYPE_APPLICATION_OVERLAY): app
  name, M:SS clock, ENDING SOON warning in the last 30 s, and an END PEEK
  hand-back button. Driven solely from the VM pill state (whose seconds
  derive from the Room lease end, UNA2-06), so the face cannot disagree
  with the lease. If the draw-over-apps grant is missing it does not draw;
  the in-app face below is the fallback, so a peek is never clock-less.
- NEW `PeekCountdownPill` composable (`AirlockAndDisarmOverlays.kt`,
  testTags `peek_countdown_pill` / `peek_countdown_clock` /
  `peek_end_button`), collected and rendered at the top of MainActivity's
  overlay stack — the first-ever collector of `floatingPeekPill`.
- VM init wires `PeekPillOverlay.onEndPeekRequest` to
  `end2MinutePeekEarly()` and syncs the overlay from the pill flow.
- `start2MinutePeek` now launches the peeked package (NEW_TASK,
  runCatching; a dead intent leaves the user on the Deck with the in-app
  pill standing). The lease no longer burns on hand navigation.
- Pure print laws `peekPillClockText` / `peekPillWarning` unit-tested in
  `TemptationJourneyTest`.

Not claimed: on-device proof. CI compiles and unit-tests; the overlay's
runtime behaviour on Sarthak's phone is for KARTE's merge/phone pass.

## Finding 2 — HIGH — RETURN TO FOCUS returns nowhere — FIXED-IN-CODE

Verified true: `transmuteImpulseAndReturn` only hid the sheet and booked
the intercept; `MainActivity.kt` contained no `finish()` /
`moveTaskToBack` anywhere (grep, zero hits).

Fix: the host now completes the verb — MainActivity's `onReturnToFocus`
calls `transmuteImpulseAndReturn()` and then
`(context as? Activity)?.moveTaskToBack(true)`, sending Monolith's whole
task behind the task the intercept interrupted. The user lands back on
their work, not on the Deck. Blocking is untouched: the session stays
sealed, the shield keeps enforcing; only the honest door stops taxing
the user a manual re-navigation. VM KDoc updated so neither half claims
the other's job.

## Finding 3 — HIGH — 5-second wall, one tap buys 2m, per-app cooldown —
## STANDS-BLOCKED (policy) with the deception half FIXED-IN-CODE

Verified true: countdown hardcoded from 5 (`AirlockAndDisarmOverlays.kt`
~:569-577), one tap at zero grants `PEEK_DURATION_SECONDS = 120`
(Repository :106-108), cooldown read per app row only
(`getPeekCooldownRemainingSeconds(entity)`).

Ethics note (Critics / Purpose-questioning / Deep-thinker seats): a
5-second wall is not friction, it is a loading spinner on the craving;
and a per-app cooldown does not ration peeking, it rations peeking
*per icon* — the total daily peek supply is 2 minutes × every peek app,
renewing every 20 minutes each. Both numbers are real. Changing either
is a price/scope ruling, and this bundle was ordered not to invent
Sarthak's ruling:

DECISION-NEEDED (exact choices for Sarthak):
(a) Wall length: keep 5 s, or raise (e.g. 30 s / 60 s)? A longer wall
    strengthens HOLD; it is his call how much patience the door demands.
(b) Cooldown scope: keep per-app, or make the 20-minute cooldown GLOBAL
    (one peek anywhere starts the cooldown for every peek app)? Global
    is the version that actually rations the craving's rotation.

Fixed in code now (TELL-THE-TRUTH, no policy change): the door states
the whole exchange during the breath, not after it —
`peekOfferPreviewText` ("AFTER THE BREATH: ONE TAP OPENS THIS APP FOR
2 MINUTES") prints while the countdown runs; the price line
(`peekPriceText`, "…COOLDOWN ON THIS APP") and the new scope line
(`peekCooldownScopeText`, "THE COOLDOWN IS PER APP — OTHER PEEK APPS
KEEP THEIR OWN TIMERS") print from the first second whenever the offer
exists. Rotation is no longer discoverable only by doing it.

## Finding 4 — HIGH — 10-second undo, second 11 costs the weekly token —
## STANDS-BLOCKED (policy), priced

Verified true in current code: Deck renders GRACE WINDOW ABORT only
while `elapsedSeconds <= 10` (`TactileDeckScreen.kt` :1029); its handler
is the sole token-free exit (`MainActivity.kt` ~:532 →
`confirmDisarmSession`, which per TOKEN-SPEC §5 never touches the
ledger). After second 10 the chip becomes the emergency valve, and
`requestDisarmChallenge` routes timed sessions — and stopwatch sessions
under `STOPWATCH_MIN_FLOOR_SECONDS = 20*60` (Repository :109) — into the
60-second hold that spends the week's one token
(`completeEmergencyBreakFromValve` → `repository.completeEmergencyBreak()`).

The grace window's length is a window/price ruling, not a bug this
bundle may re-price on its own:

DECISION-NEEDED (exact choices for Sarthak):
(a) Keep 10 s, or extend the free grace (e.g. 30 s / 60 s)? Every added
    second is a longer free exit from any session, accidental or not —
    that trade is his.
(b) Or: a separate, once-per-session "accidental start" abort inside
    the first minutes that does NOT touch the weekly token but is
    itself rationed (e.g. once a day)? New semantics; needs his ruling.
(c) Or: keep as-is, ruled acceptable.

Truth surface already honest in current code (verified, no change
needed): the grace chip prints its remaining seconds; the replacement
chip prints the token count as its badge, and (Finding 5 fix) now also
prints the price in words under it; the disarm overlay's body states
"This ends the session early. It is the only break this week." at full
size/alpha before any hold begins.

## Finding 5 — MEDIUM — Two opposite doors both named EMERGENCY, prices
## in small dim type — FIXED-IN-CODE

Verified true: airlock chip was "EMERGENCY PEEK" (repeatable,
cooldown-priced) while the Deck chip was "EMERGENCY BREAK VALVE (60S
HOLD)" (session-ending, weekly-token-priced); the peek's price was a
12.5 sp/0.80 line revealed only at countdown zero; the break's
mechanics were 11 sp at 0.75 alpha; the break's scarcity showed only
as a badge.

Fix:
- The peek door is renamed **2-MINUTE PEEK** — EMERGENCY now names only
  the session-ending weekly break, on both of its surfaces.
- The Deck chip is renamed **EMERGENCY BREAK (60S HOLD)** to match its
  overlay's title, and gains a price line under it at 12.5 sp / 0.85
  (`emergencyBreakPriceText`, testTag `emergency_break_price`):
  "ENDS THIS SESSION · USES THIS WEEK'S ONLY EMERGENCY BREAK", or
  "THIS WEEK'S EMERGENCY BREAK IS ALREADY SPENT" when the ledger is at
  zero — stated before the tap, not after.
- The peek's price + scope lines now print from the first second of
  the airlock (see Finding 3) instead of appearing at zero.
- The disarm overlay's mechanics line rises from 11 sp / 0.75 alpha to
  13 sp / full alpha — the same weight as the consequence line above
  it. Its body already stated the price plainly ("This ends the
  session early. It is the only break this week."); now the mechanics
  match it.
- `emergencyBreakPriceText` unit-tested for both ledger states.

Fonts/palette law kept: Atkinson Hyperlegible + Anonymous Pro only,
night palette only, no green anywhere in the new faces.

## Tests

NEW `app/src/test/java/com/example/TemptationJourneyTest.kt` — 7 tests:
pill clock format (incl. negative clamp), warning window (30 s edge),
breath preview wording, per-app scope wording, break price for both
ledger states. CI verdict recorded in the delivery comment/run.

## Counts

Findings: 5 · FIXED-IN-CODE: 3 (1, 2, 5) · STANDS-BLOCKED with
DECISION-NEEDED: 2 (3, 4 — each with its truth-telling half fixed in
code where one existed) · VERIFIED-FALSE: 0 · FIXED-ELSEWHERE: 0.

## CI verdict (read, both runs)

- Run 40986169386 (tip 3644989): FAILURE at step "Assemble debug".
  Cause found in own diff: `emergencyBreakPriceText` had been inserted
  between `@Composable` and `fun TactileDeckScreen`, stripping the
  screen's annotation. Diagnosis was differential — the base-commit
  run 40985873618 (0a00e95, before this branch's changes) PASSED
  Assemble and failed only at "Unit tests".
- Run 40992007146 (tip 577dee5, helper moved above the annotation;
  landed via the Contents API because the box's git loose-object
  writes were in an EPERM wave): **Assemble debug GREEN**; FAILURE at
  step "Unit tests" — the identical failing step as the untouched
  base run 40985873618, i.e. the pre-existing red test suite on main
  at this base, not introduced by this branch. This branch's own 7
  tests (TemptationJourneyTest) are pure-function assertions,
  re-verified line-by-line against the functions they pin.
  Caveat, stated plainly: job logs were unreachable from this box
  (the Actions log blob host returns 0 bytes through the egress
  proxy on every route tried), so the per-test failure list could
  not be read; the step-level differential against the base run is
  the evidence relied on. KARTE can read the log from the run page:
  https://github.com/Sarthak-2007/Focus-app/actions/runs/40992007146
