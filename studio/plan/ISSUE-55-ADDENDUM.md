# Issue #55 — Addendum (verifier + fresh-eyes + feel + finisher, same pass)

This addendum supersedes the main report wherever they differ (notably
Finding 4's transmute disposition: **wired, not cut**).

## Addendum A — verifier residuals cured (findings 3 + 5)

1. **Clock-source mismatch (F3):** state now carries
   `peekCooldownEndsAtMonoMillis` — the repository's own monotonic
   deadline (`getPeekCooldownDeadlineMono` = anchor + 20 min in
   `SystemClock.elapsedRealtime()` domain, new repository helper).
   The overlay derives remaining with ceiling math
   (`cooldownRemainingSeconds`, mirroring `peekSecondsRemaining`), so
   its unlock instant is the deadline itself, never before the
   repository's grant instant. The wall-clock reconstruction is gone.
2. **Pill race (F3):** `peekWithheldFor` (extracted, shared by the
   intercept path and the grant-refusal path) names ALREADY_ACTIVE
   before COOLDOWN — a door withheld for both never locally unlocks
   into a refused chip. The `withheld == COOLDOWN` guard in
   `peekUnlocked` is preserved exactly as the verifier required.
3. **Wall clock (F5):** the wall stamp is `interceptedAtMonoMillis`
   (`SystemClock.elapsedRealtime()`); a wall-clock jump has no input
   to the countdown math.
4. **Tests added for the gaps:** VM test (two same-package intercepts
   → strictly increasing instance ids, non-decreasing mono stamps);
   compose tests (standing COOLDOWN airlock with an expired deadline
   shows the peek chip and no withheld line, no re-trigger;
   same-package re-intercept resets the wall to 05 and hides the
   chip); boundary test (1500 ms remaining → ceiling 2, not floor 1);
   clock test (remaining depends only on monotonic stamps).

## Addendum B — fresh-eyes (history-blind seat)

- **A) Unscrollable card trap — FIXED-IN-CODE.** The wiring of the
  reason rows made the card able to outgrow short/landscape/
  large-font screens while Back is swallowed. Both Airlock and
  Disarm cards now scroll (`verticalScroll`); a compose test pins a
  300 dp-tall screen and scrolls RETURN and the peek chip into view.
- **B) Silent no-op peek — FIXED-IN-CODE.** `start2MinutePeek`'s
  refusal path now recomputes the offer from live facts and
  re-presents the truth in place (chip replaced by the real withheld
  reason), preserving the intercept instance/wall. No visible chip
  can silently do nothing.
- **C) Disarm valve / hardcoded seconds — PARTLY FIXED, REST NAMED.**
  The rule line now prints `state.requiredHoldSeconds` instead of a
  hardcoded 60. The valve-uncomposes-at-0 behaviour is pre-existing
  Ledger-Direct composition (the SPENT face and its body text do
  render — "This week's break is already spent"); reworking the
  ledger composition belongs to Konto's train — **named, not cured
  here**, so banked-seconds-mid-hold semantics stay with the owner
  of the valve composition.
- **D) Back feedback inconsistency — DECISION-NEEDED.** Airlock Back
  is a silent swallow; Disarm Back acts as KEEP FOCUSING. Unifying
  (sound/haptic on swallow, or identical semantics) is a feel/design
  call for Ren/Sarthak; not silently resolved.
- **E) The wall gates only the peek — RECORDED.** RETURN TO FOCUS is
  live from second 0; the 5 s gates the peek only, and no line says
  so. No wording was changed (any wording change is a design act).
  Recorded in the effect flags below.
- **F) No Airlock/Disarm/Park mutex — COMPOSITION.** RETURN can land
  in a still-open Park at the MainActivity root. Flagged for Konto's
  composition; not forced in this branch.
- **G) Accessibility — CROSS-REFERENCED.** Zero
  contentDescription/semantics in the overlay file; the countdown,
  chips and valve are invisible to TalkBack. This belongs to audit
  issue **#70 (usability-accessibility)**; named here, not ballooned
  into this branch.

## Addendum C — feel seat (snapshot fb0d40c, confirmed + updated)

Feel confirms: the wall is now exactly 5.0 s instead of
5×(1000 ms + overhead) drift; a same-app re-intercept restarts the
full wait; the cooldown visibly ends on screen (peek chip replaces
the cooldown line ≤200 ms after expiry; the "PEEK COOLDOWN — OVER"
string sits in a defensive branch and is never rendered); modal and
Back blocks are silent swallows (no sound/haptic/animation);
reason chips are a NEW tappable surface making the card taller
(mitigated by Addendum B-A scrolling).

## DECISION-NEEDED items (for Ren/Sarthak — not resolved silently)

1. **Wall anchor (Feel):** the wall is anchored to the INTERCEPT
   instant (deadline truth) — service→overlay latency subtracts from
   the visible wall, so the first frame can already read 04.
   Alternative: anchor at the first visible frame (always a full
   visible 5 s, plus latency). Currently in force: **intercept
   instant**, named in a code comment at the stamp site.
2. **Back feedback (Fresh-eyes D):** unify Airlock/Disarm Back feel,
   or keep the deliberate difference? Currently: as cured (swallow
   vs KEEP FOCUSING), difference recorded.

## What changes for the user (plain words — Sarthak's flag rule)

- Taps outside the Airlock/Disarm cards no longer reach the app
  underneath — the block screen is a real modal now.
- The Back button no longer escapes the block: on the Airlock it
  does nothing; on the Emergency Break screen it acts exactly like
  KEEP FOCUSING.
- The 5-second wait is now exactly 5 seconds (no drift), and opening
  the same blocked app again restarts the full 5 seconds.
- When a peek cooldown ends while the Airlock is open, the
  EMERGENCY PEEK button now actually appears there and then — no
  more leaving and re-opening the app, and no more "AGAIN IN 0 MIN".
- The peek button only appears when tapping it will work; if a peek
  is refused, the screen shows the real reason instead of nothing.
- New: a "WHY DID YOU OPEN IT?" row (Habit / Stuck / Tired /
  Message) on the Airlock — the chosen reason is what gets recorded
  (previously every open was recorded as "Habit" regardless).
- New: tapping the big countdown number during the wait makes a
  small pitch-rising click and counts the tap (the previously
  dead transmute pulse, now wired to the numeral). It changes
  nothing else — no time is bought or lost.
- The Airlock/Disarm cards now scroll on short screens, so RETURN
  and PEEK are always reachable.
- The Emergency Break instructions now print the real required
  hold seconds (still 60 today) instead of a hardcoded number.
- RETURN TO FOCUS still works from the first second — the 5-second
  wall only ever gated the peek (unchanged behaviour, recorded per
  fresh-eyes E).
- Nothing was removed: peek length (2 min) and cooldown (20 min)
  are untouched; reason selection and transmute both survive, wired.

## Finisher CI attribution (FIX-FIRST item 0)

- Run 40973866686 (tip 277cdb5): **Assemble debug SUCCESS**; step
  "Unit tests" FAILED at **test compilation** — 5 pre-existing stale
  test files on main (ScreenshotHarnessTest, ShortsToggleRenderTest,
  SoundAndSocialTruthTest, TruthCar3CaptureBodyTest,
  TruthCar3SweepTest). Cured by cherry-picking issue-13's verbatim
  adoption of issue-2's call-site fixes (fb0d40c).
- Run 40974687791 (tip fb0d40c): **Assemble debug SUCCESS**; tests
  EXECUTED — **146 tests completed, 33 failed**, all pre-existing
  and other-scope: 31 Robolectric `classMethod` failures with
  `UnsupportedOperationException at DefaultSdkProvider.java:170`
  (Robolectric cannot serve SDK 36 in this runner — infrastructure,
  every Robolectric class on main alike), plus 2 named assertions:
  `ChromeWaveUnitTest.warrantDerivation_matchesTheDeletedLiveWarrantTable`
  and `TruthCar3SweepTest.una06_factoryDefaults_biteOnlyWhenStored`.
  No #55 test failed; the #55 plain-JUnit suite passed.
- A fresh run on the final tip (with Addenda A/B) is the closing
  evidence; numbers recorded in the issue's closing comment.

## Composition hazards (Keeper of Record check)

- **#13 overlay hazard (prominent):** `agent/issue-13` rewrites
  AirlockAndDisarmOverlays.kt massively (+608/−107) carrying the
  IDENTICAL buggy timer code. Whichever branch merges second
  conflicts on these hunks; resolving by taking #13's overlay
  section would silently resurrect findings 3 and 5. The #55
  instance-keying, monotonic deadlines and unlock gate must be
  re-applied on top of #13's version. **COMPOSITION-HELD note for
  Konto's train — not force-merged here.** (Main's design-revamp,
  merged 066e59b, kept `onTransmuteParticleTap` and touched only
  adjacent Airlock hunks; this branch's signature is back to base
  shape, so the revamp composes cleanly.)
- DB migrations, `start2MinutePeek` grant machinery beyond the
  refusal-refresh, and W-41/43 pieces stay with Konto's train.
- Keeper check: 5/5 actual findings dispositioned above; user-effect
  flags listed; 2 DECISION-NEEDED items named; composition hazards
  named; branch tip + CI runs named in the closing comment.
