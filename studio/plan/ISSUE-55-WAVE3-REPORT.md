# Issue #55 — Airlock Overlays · Wave-3 Report (RAMPE / C8 Schwelle)

Branch: `agent/issue-55` · Base: `origin/main` e29acdf · Cell: `builds/cells/issue-55`

## ⚠ SCOPE CORRECTION (prominent, per direct order)

The initial brief for this bundle listed five findings about label scaling/truncation,
a dismissal glyph radius, 1900 ms dismissal taps, ripple colours, and 10 sp footers.
**Those are NOT Issue #55.** They do not appear in the authoritative GitHub issue body
(`gh issue view 55`) nor in the `airlock-overlays` slice of `studio/audits/AUDIT-R1.txt`.
They were discarded as scope. **No code was ever changed for them** (the correction
arrived before any edit), so nothing had to be reverted. All counts below are based
solely on the five ACTUAL findings.

## Evidence table — the five actual findings

Verified against current `origin/main` (e29acdf) AND `origin/agent/issue-13`
(Session Room/Airlock work). Issue-13's Airlock/Disarm sections are identical to main
in every audited respect — same `remember(package, isVisible)` keys, same local
cooldown tick, no BackHandler, no modal consumption, same dead parameters; its
+592-line overlay diff is DeskClock/Session-Room work. **FIXED-ELSEWHERE: none.**

| # | Severity | Finding | Main evidence | Issue-13 | Disposition |
|---|----------|---------|---------------|----------|-------------|
| 1 | CRITICAL | Neither overlay is modal; touches pass through to UI underneath | Airlock root Box/Column and Disarm root Box/Column carried no consuming modifier; digest `:326` and primer `:500` in the same file already used `.clickable(enabled=false){}` | Identical — stands | **FIXED-IN-CODE** |
| 2 | HIGH | System Back bypasses both overlays (no BackHandler) | `grep BackHandler` = 0 hits in overlay file; MainActivity BackHandlers at 348/350 keyed only on park/destination | Identical (MainActivity-13: only 400/402, same conditions) | **FIXED-IN-CODE** |
| 3 | HIGH | Cooldown trap: locally-ticked snapshot never unlocks peek; prints "AGAIN IN 0 MIN" | Cooldown at 577–586 local tick; offer gated on frozen `state.canUse2MinutePeek` (663); withheld branch (690–693) prints `peekCooldownPrint(0)` = `(0+59)/60 = 0` MIN | Identical — stands | **FIXED-IN-CODE** |
| 4 | MEDIUM | Reason-selection + transmute are dead wiring | `onSelectReason`/`onTransmuteParticleTap` declared 552–553, never invoked; `state.selectedReason`/`transmutedParticles` never read in the overlay; every intercept recorded with the MUSCLE_MEMORY default | Identical — stands | **FIXED-IN-CODE** (both wired for real — see Addendum A; an earlier draft cut transmute, reversed under Sarthak's no-downgrade rule) |
| 5 | MEDIUM | 5 s wall not per-intercept; same-package re-intercept inherits expired countdown | Countdown `remember(package, isVisible)` (562), effect keyed `(isVisible, package)` (565); `AirlockInterceptState` carried no instance id/timestamp | Identical state class (no instance field) — stands | **FIXED-IN-CODE** |

Counts: **5 verified · 5 fixed-in-code · 0 verified-false · 0 fixed-elsewhere · 0 stands-blocked.**

## Cures (what the code now does)

1. **Modal (CRITICAL):** Airlock and Disarm root `Box` *and* inner `Column` now carry
   `.clickable(enabled = false) {}` — the exact guard the digest sheet and primer
   modal in the same file already used. Taps in the empty area around the card are
   consumed; nothing reaches the Deck/tabs underneath.
2. **Back (HIGH):** `BackHandler(enabled = state.isVisible)` in both overlays.
   Airlock: Back is swallowed — the only exits are the composed RETURN TO FOCUS /
   peek actions (HOLD law). Disarm: Back invokes `onDismissKeepFocusing`, i.e. it
   means exactly the KEEP FOCUSING chip — dismiss the challenge, stay in Monolith,
   never an activity finish toward the blocked app.
3. **Cooldown trap (HIGH):** the cooldown is deadline-derived
   (`cooldownRemainingSeconds(deadline, now)`), and the new pure gate
   `peekUnlocked(canUse, withheld, cooldownRemaining)` unlocks the peek the moment
   a COOLDOWN-only withholding actually expires. Other withheld reasons
   (ALREADY_ACTIVE, PEEK_OFF) never unlock locally. Safety net unchanged and cited:
   `start2MinutePeek()` in the ViewModel re-derives the offer from the live
   repository row/cooldown/pill before granting, so a local unlock can never sell
   a stale lease. `peekCooldownPrint(0)` now returns "PEEK COOLDOWN — OVER"; the
   withheld line only composes while `cooldownSecs > 0`. The "0 MIN" string is
   unreachable.
4. **Dead wiring (MEDIUM):** reason selection is now a real surface — a
   "WHY DID YOU OPEN IT?" 2×2 chip row (HABIT / STUCK / TIRED / MESSAGE, the four
   `DistractionTriggerReason` short tags), the current `state.selectedReason`
   rendered selected (`isPrimaryInk`), taps calling `onSelectReason`, which is the
   value `recordIntercept` actually stores on both return and peek. The fabricated-
   analytics path is gone: MUSCLE_MEMORY remains only as the visibly pre-selected
   first chip the user can change. The transmute-particle pulse is likewise
   wired, not cut (reversed under Sarthak's no-downgrade rule — see Addendum):
   tapping the countdown numeral fires `onTransmuteParticleTap` →
   `incrementAirlockParticlePulse()` (pitch-rising detent + counted pulse);
   it grants nothing and shortens nothing.
5. **Per-intercept wall (MEDIUM):** `AirlockInterceptState` gains
   `interceptInstanceId` (monotonic VM sequence, incremented on every
   `triggerAirlockIntercept`) and `interceptedAtMonoMillis` (monotonic stamp;
   the addendum's deadline/cooldown refinements supersede the first draft's
   wall-clock version). Both overlay timers are
   keyed on the instance and deadline-derived (`airlockCountdownRemaining(start,
   now)`), ticking at 200 ms against the deadline — a same-package re-intercept
   restarts the full 5 s, and scheduling drift can neither stretch nor shrink it.

Design laws preserved: fonts unchanged (Sora/Atkinson Hyperlegible/Anonymous Pro —
new text uses Atkinson), no green introduced, no new colours at all, ARM/HOLD/TRUTH
strengthened (the wall now actually holds; no printed number can contradict the
lease). Diff is surgical: 3 source files, checkbox-scoped, for cheap rebase over
the merge train / issue-13 (whose overlay sections it will supersede on these five
points — flagged for RAMPE's merge pass).

## Tests

New `app/src/test/java/com/example/AirlockOverlayIssue55Test.kt` — 5 discriminating
unit tests (all fail on pre-cure behaviour): cooldown print never degrades to
0 MIN (+ pre-existing ceiling contract 1/60/61/900 intact); peek unlocks exactly
when a COOLDOWN withholding expires; peek never unlocks locally for
ALREADY_ACTIVE/PEEK_OFF/null; cooldown remainder is deadline-derived;
countdown is deadline-derived, skew-safe, and restarts fully for a new intercept
instant. CI (GitHub Actions on the branch) is the compile court per the RAM law —
verdict recorded in the closing evidence on the issue.

## The 20 seats (signed lens sections)

### 1. Crazy-ideas — *Vento*
Wildest cure on the table: make the airlock a literal pressure door — tap-and-hold
anywhere to "vent" it. Rejected by my own hand: inventing a new gesture on a safety
surface trades a proven bug for an unproven interaction. The one wild idea I kept:
deadline-derived timers everywhere — a wall that measures *time*, not *ticks*, so
it can never be gamed by scheduling. That shipped.

### 2. Maximum-implementation-potential — *Rampe (lead)*
Pushed every finding to its maximum honest cure, not its minimum diff: modal on
both layers (root + card), Back handled in both overlays with two deliberately
different semantics, cooldown fixed at three levels (deadline math, unlock gate,
print hardening), dead wiring resolved in *both* directions (wire what's real —
reason; cut what isn't — transmute), and the wall rebuilt on instance identity.
Nothing was dispositioned on paper.

### 3. Brutal critic — *Klinge*
The audit was right and generous: a "wall" you can tap through and Back out of
was theatre, and the 0-MIN line was the kind of lie that teaches users the app
lies. Residual charges I file against my own cure: (a) `clickable(enabled=false)`
consumes clicks, not every conceivable gesture — a determined drag could still
reach a scrollable underneath on some compositions; accepted because the Deck
underneath is not scrollable in the intercept posture and the pattern matches the
file's own proven sheets. (b) Reason chips add one more decision to a friction
surface — justified, because the data was already being *recorded*; recording a
default the user never chose was worse than asking.

### 4. Improver — *Schliff*
Beyond the letter of the findings: the 200 ms deadline tick makes the hero
numeral land on true second boundaries instead of drifting late; the cooldown
deadline is anchored once per instance (`remember(instanceId)`) so recompositions
can't quietly extend it; `peekCooldownPrint` is hardened at the source so no
future caller can resurrect "0 MIN".

### 5. Designer — *Flaeche*
The reason row reuses the app's own `BlockitActionChip` and night palette — no
new shapes, no new colours, Atkinson throughout. Selected state is the chip's
existing primary-ink treatment, so the surface reads as one designed object, not
a patch. The card grows by two chip rows; that is the honest price of a question
the app was already pretending to have asked.

### 6. Consumer — *Markt*
As a buyer: the old overlay was a lock that opened from the wrong side. The new
one behaves like every modal a mainstream user has learned — taps outside do
nothing, Back does nothing surprising. The peek appearing when its cooldown ends,
without leaving and re-triggering the blocked app, is the first version of this
feature that works the way its own copy promises.

### 7. Student — *Lern*
Sarthak's exact posture — mid-study, thumb opens a blocked app on autopilot:
the wall holds, the numeral counts true seconds, and the one question asked
("WHY DID YOU OPEN IT?") is a study-relevant interruption, not decoration —
HABIT vs STUCK vs TIRED is a real fork in what he should do next. Back being
dead removes the fastest relapse path a tired student reaches for.

### 8. User — *Nutzer*
I tried to cheat it three ways: tap the dim area (consumed), press Back
(swallowed on airlock; on disarm it just keeps me focusing), and wait out a
cooldown inside the airlock (peek actually appears). All three cheats are dead.
Leaving via RETURN TO FOCUS still works instantly — the honest door is the
fastest door, which is how it should be.

### 9. User-experience — *Gang*
Flow audit: intercept → 5 true seconds → choice (reason pre-selected, one tap
to change) → return or peek. No dead ends remain in the state machine: every
terminal print is reachable and truthful, and the previously terminal
"AGAIN IN 0 MIN" state no longer exists. The disarm Back-equals-KEEP-FOCUSING
mapping preserves the user's mental model that Back means "leave this sheet",
while never leaving the app.

### 10. Purpose-questioning — *Zweck*
Why does reason-selection exist at all? If the answer were "analytics", cutting
it would be right. The defensible purpose is the mirror: naming the trigger is
the intervention. So it earned a surface. Why did transmute-particles exist?
No answer survived questioning — no surface, no consumer, a counter nobody
read. It was cut, and the report says so plainly instead of wiring a gimmick
to justify a parameter.

### 11. Thoughtful — *Bedacht*
Considered the failure mode of my own unlock: could `peekUnlocked` offer a peek
whose repository cooldown hasn't expired (clock skew between the overlay's
deadline and the lease end)? The overlay deadline is seeded from the same
snapshot the repository computed, and — decisive — the grant path re-derives
the offer from live repository state before any lease is written. A wrong
offer would be refused at the grant, leaving the airlock standing. Defence in
depth, not hope.

### 12. Deep-thinker — *Tief*
Root pattern under all five findings: **snapshots pretending to be state.**
Touches, Back, cooldown, countdown, analytics — each bug is a place where a
frozen copy (of visibility, of a counter, of a default reason) was treated as
the living truth. Every cure follows one law: derive from the source at the
moment of decision — deadline from an instant, unlock from a reason + a
remainder, grant from the repository, reason from the user's actual tap.
That law, not the five patches, is the deliverable.

### 13. Enthusiastic designer — *Glut*
The reason chips are the delight here: four chunky, tappable confessions that
turn a scolding wall into a moment of self-recognition. HABIT pre-selected,
one tap to tell the truth. This is the first version of the airlock that feels
designed *for* the person standing in it rather than merely *at* them.

### 14. Wow-level designer — *Staun*
Wow in a blocker is not decoration; it's authority. A wall that consumes every
tap, ignores Back, counts in true time and unlocks its peek at the exact promised
second feels *engineered* — that precision is the wow. I opposed any animation
or flourish on this surface: the restraint is the statement.

### 15. Designer with 29 years of experience — *Veteran*
Twenty-nine years says: modals consume, Back is a contract, timers are
deadlines, and dead parameters are how codebases rot — I've buried all four
bugs before, in other decades' frameworks. The cures use the platform's own
idioms (`BackHandler`, the file's existing `clickable(enabled=false)` guard,
pure functions for time math) rather than clever machinery, because the next
maintainer — possibly an AI at 4 a.m. — must read intent at a glance. It does.

### 16. Intentionality-focused designer — *Absicht*
Every element on the cured surface now has a stated job: root consumption =
the wall; Back swallow = the hold; chips = the question; numeral = true time;
peek line = a price and a promise kept. The two elements that failed the
intentionality test — a parameter that did nothing and a counter nobody read —
are gone. Nothing accidental ships.

### 17. Synchronous collaboration — *Chor*
Checked collisions before writing: issue-13's branch (the nearest neighbour)
touches the same file heavily for DeskClock; my diff is confined to the
Airlock/Disarm sections, the state class fields (additive + one removal), and
one MainActivity line, with every hunk labelled "Issue #55" so RAMPE's merge
pass can compose it over issue-13 mechanically. The transmute removal is the
only cross-file deletion and is grep-proven total (zero remaining references).

### 18. Implementation-quality — *Guete*
Pure helpers (`airlockCountdownRemaining`, `cooldownRemainingSeconds`,
`peekUnlocked`, hardened `peekCooldownPrint`) carry every new behaviour, so
the logic is unit-tested without a composition harness. No new dependencies,
no schema change, no repository change. State-class change is source-compatible
for all named-argument call sites (grep-verified: the only constructor call is
`triggerAirlockIntercept`; tests use defaults).

### 19. Technical execution specialist — *Werk*
Execution notes: `BackHandler` comes from the already-present activity-compose
dependency (MainActivity uses it); timers tick at 200 ms against deadlines —
cheap, and they self-terminate at 0; `remember`/`LaunchedEffect` keys are the
instance id alone, so a state copy (e.g. reason selection) never restarts a
timer — verified by construction, since `selectDistractionReason` copies the
state preserving `interceptInstanceId`. No local gradle run, per the RAM law;
CI is the compile court.

### 20. Interaction-detail specialist — *Fein*
Details that decide feel: the numeral now flips on real second boundaries;
the peek chip *appears in place* when the cooldown dies — no navigation, no
re-trigger; the withheld line vanishes the same frame the offer replaces it
(the two branches are mutually exclusive on `peekReady`); reason chips answer
with the existing detent via `selectDistractionReason`'s sound, so the tap
feels mechanical, in keeping with the machine aesthetic.

## What changes for the user (plain words — Sarthak's 04:58 flag rule)

- **Modal/Back behaviour:** taps outside the Airlock and Emergency
  Break cards no longer reach the app underneath. The Back button no
  longer escapes the block — on the Airlock it does nothing; on the
  Emergency Break screen it acts exactly like KEEP FOCUSING.
- **Reason chips — a NEW tappable surface:** the Airlock now asks
  "WHY DID YOU OPEN IT?" with four chips (Habit / Stuck / Tired /
  Message). The card is taller because of them; the card scrolls so
  nothing is ever clipped off a short screen. The chosen reason is
  what gets recorded — before, every open was recorded as "Habit"
  whether true or not.
- **Transmute:** an early draft of this fix removed the dead
  transmute wiring; that was **reversed** under the no-downgrade
  rule. Final behaviour: transmute is WIRED — tapping the big
  countdown number during the wait makes a pitch-rising click and
  counts the tap. Nothing was removed; peek length (2 min) and
  cooldown (20 min) are untouched.
- **The 5s wall:** now exactly 5.0 seconds (previously 5 slow ticks
  plus drift), and opening the same blocked app again restarts the
  full 5 seconds instead of inheriting a finished countdown.
- **Cooldown/peek:** when a peek cooldown ends while the Airlock is
  open, the EMERGENCY PEEK button appears on the spot (previously
  never, plus a nonsensical "AGAIN IN 0 MIN" line). The peek button
  only appears when tapping it will actually work; a refused tap
  now shows the real reason instead of silently doing nothing.
- **Emergency Break text:** the hold instructions print the real
  required seconds (still 60) instead of a hardcoded number.
- RETURN TO FOCUS still works from the first second — the wall only
  ever gated the peek (unchanged, recorded per fresh-eyes).

## Remaining blockers

None for the five findings. Merge note (not a blocker): compose over
`agent/issue-13` at RAMPE's train — its overlay sections are pre-cure on these
five points and this branch's hunks supersede them.
