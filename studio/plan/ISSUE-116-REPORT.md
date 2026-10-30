# Issue #116 — Audit R3b · night-half-asleep — Wave-3B report (WELLE-3 cell)

Branch: `agent/issue-116`, tip af906bf (pushed; remote-tracking matches).
Base: origin/main at branch time (0a00e95 signing fix on top of 68a3713).
All 5 findings re-verified against base code (AUDIT-R3B.md
"night-half-asleep" section) and against the branch diffs below.
Purpose check (SARTHAK-WORDS): Monolith is a BLOCKER for study blocks —
no fix weakens blocking; night behaviour only stops the app from
punishing the user outside/around the block. No finding was a design
complaint belonging to another surface; all 5 own this issue.

## Finding 1 — HIGH — Night mode hostage to a blocking routine — FIXED-IN-CODE
Verified at base: evaluateTheme() returned night only for an active
session or an OPEN SLEEP-routine window; no clock/system signal; manual
theme dead.
Fix (93b1f80): NEW `service/NightSignal.kt` — one signal (clock
20:00–06:00 + system dark mode) joins evaluateTheme precedence AFTER
session/SLEEP ownership: step (3) CLOCK/SYSTEM night → "AUTO · NIGHT —
CLOCK 20:00–06:00" / "AUTO · NIGHT — SYSTEM DARK MODE". Darkness no
longer requires accepting a blocking routine.

## Finding 2 — HIGH — Session/SLEEP ending at night cuts 0ms to light — FIXED-IN-CODE
Verified at base: 1-second re-derivation + "a 0ms cut, never a fade"
comment; a 01:00 session end or 03:30 SLEEP end flipped to the light
scheme within a second.
Fix (93b1f80): because NightSignal holds night until 06:00, no
boundary inside the night can cut to light at all — cured by
construction, not by a fade. The boastful comment is replaced with the
reasoning.

## Finding 3 — HIGH — Nightstand clock at full daytime brightness — FIXED-IN-CODE
Verified at base: zero screenBrightness/BRIGHTNESS_OVERRIDE hits in
the repo; desk clock rendered 96sp time + progress bar in full-alpha
cream under FLAG_KEEP_SCREEN_ON.
Fix (5ab7759): window brightness held at 5% while the clock shows
(DESK_CLOCK_SCREEN_BRIGHTNESS = 0.05f), released to
BRIGHTNESS_OVERRIDE_NONE on dismiss; time at 72% cream alpha
(DESK_CLOCK_TIME_ALPHA), progress bar at 50% (DESK_CLOCK_BAR_ALPHA).

## Finding 4 — MEDIUM — Vertigo-blur seal shot when arming in bed — FIXED-IN-CODE
Verified at base: 1050ms shot drove vertigo + up to 6dp blur on every
live arm, no night/reduced-motion exemption.
Fix (da93fa2): SealShotState.nightCalm — NightSignal sampled at arm
time skips vertigo + blur entirely at night; shot timing/ground step
unchanged, daytime shot untouched.

## Finding 5 — MEDIUM — No night/DND sound quieting — FIXED-IN-CODE (+ ruling)
Verified at base: sound default ON; fixed gains; zero
interruptionFilter/DND/hour checks anywhere.
Fix (2184645): quieting at the one gain gate in TactileSoundEngine —
DND silences clicks/knocks and softens seal to 0.35; clock night
scales ordinary sounds 0.40 / seal 0.65. Seal Throw buffer bit-identical;
softening only via track volume.
Follow-up (af906bf, f5b): the TEAR PULSE is exempt from the night/DND
gate (fullGainAlways) — Sarthak's Oct 30 ruling: "tear-pulse: Full at
all hours". This is his verdict applied, not a regression of f5.

## Tests
NEW NightSignalTest.kt (90 lines) + ChromeWaveUnitTest updates.
Diff total: 7 files, +324/−30.

## Counts
Findings: 5 (+1 ruling follow-up f5b) · FIXED-IN-CODE: 5 ·
VERIFIED-FALSE: 0 · STANDS-BLOCKED: 0.

## CI / PR state
Branch pushed (af906bf = remote-tracking tip at last successful
fetch). CI verdict, PR existence, #245 line and issue-body checkbox
edit could NOT be completed in this run: box egress to GitHub (and
the whole internet) timed out on every route from ~09:52 IST onward
(gh, curl, git ls-remote all 0 bytes). Named blocker for the lead:
restore egress, then (1) confirm/open PR for agent/issue-116,
(2) read branch CI — assemble green, unit tests vs the pre-existing
~33–39 failure floor (#166), no NEW failures, (3) merge on green,
read main CI, (4) update #245 same run, (5) tick the 5 checkboxes,
one evidence comment, close.
— WELLE-3 cell, signed.
