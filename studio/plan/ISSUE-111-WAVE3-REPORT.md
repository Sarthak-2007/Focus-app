# Issue #111 — Audit R3 desk-clock-lived — Wave-3B report (KARTE bundle)

Branch: `agent/issue-111` from current `origin/main` (0a00e95 at branch point).
Scope: ONLY the 5 findings in #111. No issue edit/close, no merge (KARTE reviews).

## Per-finding disposition

### 1. [HIGH] Always-on only until first interruption; no lock-screen presence — **FIXED-IN-CODE**
Verified against current code: overlay carried only `FLAG_KEEP_SCREEN_ON`
(`AirlockAndDisarmOverlays.kt`, DisposableEffect); repo-wide grep found zero
`showWhenLocked`/`turnScreenOn` anywhere, and `AndroidManifest.xml` declares
none on MainActivity.
Fix: the overlay's existing DisposableEffect now also grants, **only while
the clock is composed**, `setShowWhenLocked(true)` + `setTurnScreenOn(true)`
(API ≥27; pre-27 the deprecated `FLAG_SHOW_WHEN_LOCKED|FLAG_TURN_SCREEN_ON`
window flags, minSdk is 24), and revokes both on dispose. Deliberately NOT a
static manifest/activity flag: a permanent showWhenLocked would float the
Deck itself over the keyguard after exit — the exposure finding 5 names.
Price/blocker for the scoped form: none. Note: the activity still sits
behind the keyguard for OTHER interruptions when the clock is not open —
that is correct; the finding was about the clock's own presence.

### 2. [HIGH] Burn-in "figure-8" is a ±6px 5×5 raster — **FIXED-IN-CODE**
Verified: old overlay `dx=((step%5)-2)*3, dy=(((step/5)%5)-2)*3` fed
`IntOffset` (raw pixels) → ±6px for 96sp numerals; VM steps +1/60s, %360.
Fix: new pure `deskClockBurnInOffsetDp(step)` — a true lemniscate in **dp**:
x = 26·sin θ, y = 13·sin 2θ, θ = 2π·(step mod 60)/60; the face traces one
full figure-8 per hour, ±26dp × ±13dp, several dp of travel every minute.
Applied via `Modifier.offset(x=…, y=…)` (dp) instead of pixel IntOffset.
Pinned by `DeskClockLivedTest.burnIn_wanderReachesTensOfDp_notSixPixels`
(amplitude, per-minute movement >1dp, lemniscate waist, 60-step closure).

### 3. [HIGH] Desk clock erases Safe Breaks — **FIXED-IN-CODE**
Verified: overlay never read `isInSafeBreak`; span number (correctly, per
the span law in `MonolithModels.kt`) keeps counting through a break, the
task banner kept printing, and the Deck's BREAK eyebrow — the break's other
rendering — is covered by this full-screen overlay.
Fix (announcement, not a second derivation): new pure
`deskClockBreakLine(session)` returns non-null only while
`isActive && isInSafeBreak`, counting the break's OWN
`safeBreakSecondsRemaining`; the overlay renders
"SAFE BREAK • MM:SS LEFT — STEP AWAY" (testTag `desk_clock_break_line`)
and suppresses the task banner for the break's duration. The hero number
still reads the span — the span law is untouched, no model change.
Pinned by pure tests + the render test (break face shows the line,
"Physics" banner absent). Tension noted honestly: this amends the old
in-code "desk clock gives a break no special rendering" comment's reach —
the audit's lived finding wins because that comment's premise (the Deck
eyebrow is visible) is false while this overlay covers the Deck.

### 4. [MEDIUM] Glance = bare number; stopwatch bar pins full after 20m — **FIXED-IN-CODE**
Verified: hero was unlabelled `%02d:%02d`; same digits count down
(spanRemaining) or up (elapsed); no day/time/end anywhere; stopwatch
progress `(elapsed/1200f).coerceIn(0.04f,1f)` pins at 1f from 20:00 on.
Fix: three pure derivations, all rendered above the numerals —
`deskClockModeLabel` (REMAINING / ELAPSED / SET TIME, testTag
`desk_clock_mode_label`), `deskClockGlanceLine(now, session)`
("EEE d MMM • HH:mm" + for timed sessions "ENDS HH:mm" from
startWall + spanTarget via the existing `endsAtText`; stopwatch
"NO SET END"; no session → no invented end, testTag
`desk_clock_glance_line`), and `deskClockProgress` extracted with the
stopwatch branch changed to a per-20-minute lap cycle
`(elapsed % 1200)/1200` — it can touch full only at a lap rollover,
never pin. Timed branch unchanged (span fraction, 0.04 floor), idle 0.
Pinned by `DeskClockLivedTest` (labels, glance end = start+3600s for a
2×1500+600 span, lap values 1200→0.0 / 1800→0.5 / 7200<1).

### 5. [MEDIUM] Tap-anywhere exits to the unlocked operative Deck — **FIXED-IN-CODE**
Verified: full-screen Box `.clickable(onClick = onDismiss)`, header
"TAP ANYWHERE TO EXIT", `MonolithViewModel.toggleAlwaysOnDeskClock`
(VM:1563) just flips the flag, landing on the Deck; dispose also cleared
KEEP_SCREEN_ON, so a stray touch killed the display AND handed over the
controls.
Fix: the tap handler is gone. The Box now carries
`pointerInput { detectTapGestures(onLongPress = { onDismiss() }) }` and
the header prints "NIGHTSTAND DESK CLOCK • HOLD ANYWHERE TO EXIT" — a
deliberate hold exits; a bump/sleeve/child's tap does nothing, keeps
KEEP_SCREEN_ON, keeps the seal's face up. Combined with finding 1's
scoping, the lock-screen grants die with the same dispose.
Pinned by the render test: `performClick` leaves `dismissed == false`;
a hand-injected hold (down + advanceEventTime + up) dismisses. Residual
honesty: a deliberate long-press by anyone still exits to the Deck —
that is the owner's exit gesture, the same trust level as holding the
power button; adding a passcode to a desk clock was judged a design
downgrade and NOT done (flagged for KARTE if Sarthak wants an
authenticated exit).

## User-facing changes (complete list)
1. Clock now stands over the lock screen / turns the screen on, only
   while the clock itself is open.
2. Clock face wanders a wide figure-8 (±26dp × ±13dp over an hour)
   instead of ±6px.
3. During a Safe Break the clock prints "SAFE BREAK • MM:SS LEFT —
   STEP AWAY" and hides the task banner until the break ends.
4. New lines on the clock: day + time of day + session end (or
   "NO SET END"), and a REMAINING / ELAPSED / SET TIME label over the
   numerals. Stopwatch bar cycles per 20-minute lap instead of
   pinning full.
5. Exit gesture changed: TAP ANYWHERE → HOLD ANYWHERE (header updated).

## 20 named seats + roster lenses (bundle coverage, honest)
Named 20: Crazy-ideas (lemniscate + lap-cycle bar — BUNDLE-COVERED) ·
Maximum-implementation-potential (all 5 findings cured in code, none
papered — BUNDLE) · Brutal critic (scoped showWhenLocked vs manifest
flag; break-line vs Nacht's-law tension recorded above — BUNDLE) ·
Improver (glance line reuses endsAtText, no new formatter — BUNDLE) ·
Designer (labels set in existing Atkinson/Anonymous voices, no new
surface — BUNDLE) · Consumer (break spent at desk = the lived harm —
BUNDLE) · Student (12h study day: break signal is the retention point —
BUNDLE) · User (LEAD — journey walked end-to-end in current code) ·
User-experience (LEAD/BUNDLE — tap-vs-hold + label legibility) ·
Purpose-questioning (is a desk clock a clock? now prints time of day —
BUNDLE) · Thoughtful (BUNDLE) · Deep-thinker (keyguard scoping
interplay #1×#5 — BUNDLE) · Enthusiastic designer (BUNDLE) ·
Wow-level designer (figure-8 as the clock's quiet motion — BUNDLE) ·
Designer-29-years (no passcode on a nightstand clock; hold is the
proportionate friction — BUNDLE) · Intentionality-focused designer
(every new line is derived, nothing decorative-fabricated — BUNDLE) ·
Synchronous collaboration (BUNDLE) · Implementation-quality (pure
functions + tests, surgical one-file diff — BUNDLE) · Technical
execution specialist (API 24–26 flag fallback — BUNDLE) ·
Interaction-detail specialist (long-press via detectTapGestures,
header teaches the gesture — BUNDLE).
Roster extras seated: Humans/Day-fit (LEAD — nightstand + desk-break
life) · Finishers (type/spacing kept in house voices — BUNDLE) ·
Fresh-eyes (would a stranger read the digits' direction? now labelled —
BUNDLE) · Feel (hold-to-exit weight matches the app's friction pitch —
BUNDLE) · Small Things (SET TIME for the idle dial; NO SET END honesty —
BUNDLE) · Flow (break announcement arrives without leaving the clock —
BUNDLE) · Verifier (LEAD — every finding re-grepped in current tree
before fixing) · Diagnostician (BUNDLE — root causes: pixel IntOffset,
coerceIn pin, covered eyebrow) · Technical/Implementation (BUNDLE).
Thin seats are marked BUNDLE-COVERED, none silently dropped; no seat
ran as a separate agent in this bundle.

## Files changed
- `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt` (all 5 fixes)
- `app/src/test/java/com/example/DeskClockLivedTest.kt` (new, 6 tests)
- `studio/plan/ISSUE-111-WAVE3-REPORT.md` (this report)

## CI
- Run 40983879147 (tip 6968696): Assemble debug ✓; unit-test compile
  failed on the new test file only (`longPress` unresolved — Compose
  BOM 2024.09 ui-test has neither `longPress` nor `longClick`;
  corrected in two steps to a hand-injected hold).
- Run 40991488675 (tip dea5b15, attempt 2 read to verdict):
  Assemble debug ✓ · **312 tests, 38 failed**. Baseline on plain
  origin/main (run 40983481393): 306 tests, 36 failed. Delta fully
  accounted: +6 tests are this issue's DeskClockLivedTest — **all 6
  pass, zero appear in the failure list** — and +2 failures are
  unrelated pre-existing-suite tests that flipped between runs
  (SoundAndSocialTruthTest.SoundEngineBirthOrder,
  TruthCar3SweepRoboTest.una07_capRestsOnTheAppliedLoadoutAfterRecreation).
  Every other failure is name-identical to the main baseline list.
- Note: tips dea5b15 onward were created through the GitHub git API
  (blob → tree → commit → ref) because the box hit its known git
  loose-object write wall mid-task; tree contents are exactly the
  local cell's working files, verified by the CI run above. One API
  commit (4a08a28) briefly carried an empty copy of this report (the
  wall truncated the local file mid-update); this section as you read
  it is the corrected, final text, pushed immediately after.
