# FINISH — GLAETTE (Finisher 1, Wacht's cell, issue #13)

Slice: **THE QUIET STATES — IDLE, ARMED-UNDECIDED, RUNNING
STOPWATCH** — idle hero honesty (P-11/P-17), idle's no-banner /
no-sub-line law (A-8, P-17), armed-undecided printing nothing
at all, the stopwatch sub-line "NOW HH:MM" alone (A-6(ii))
with no ENDS and no descent against an invented span, and
burn-in glide behaviour in these states. Gallery-standard
pass over the MERGED code, read fresh.

## STATE OF THE GROUND (read this first)

- Subject files in the issue-13 clone:
  `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt`
  and `app/src/main/java/com/example/ui/DeskClockSettle.kt`,
  with the VM's desk-clock beats in
  `app/src/main/java/com/example/ui/MonolithViewModel.kt`.
- The tree MOVED during this pass: the overlay went from
  1,320 lines / md5 `466ef016a4d1840a0501c6c941833d94` to
  **1,348 lines / md5 `5cd1848ce5a9b60bb5b419781398c86c`**
  as Politur's P-1/P-2 cures landed mid-audit (the hero label
  now exists — see VERIFIED CLEAN). All evidence below is
  cited against the FINAL md5, re-read after it settled
  (md5 stable across two checks 20 s apart; DeskClockSettle
  `2b4478823537022d37e70e84b772113f`, VM
  `da5009c77f06c7bf4484eaf5d1c50afe`).
- **No surgical edits were made by this seat** — logged
  deliberately: the subject file was rewritten by a sibling
  finisher during this audit, and other finishers' lanes
  (Kante, Glanz) were still open on the same file. Every cure
  below is exact and small; a finisher does not race his
  cellmates. All seven defects are filed for Wacht/Wecker
  to land.

## VERIFIED CLEAN (checked, not assumed)

- **Idle hero honesty (P-11).** `deskClockHeroText`
  (AirlockAndDisarmOverlays.kt, ~:631) returns
  `wallClockTimeText(nowWallMillis)` whenever the session is
  null or `!isActive`; the live branch reads the span (timed)
  or `elapsedSeconds` (stopwatch). The dial setting is never
  read in the body — the comment at :317 says so and a grep
  for `dialMinutes` in the file returns only the parameter
  and that comment (the parameter itself is G-6). The test
  `idleFacePrintsTheCurrentTime_notTheDialSetting` pins both
  the null and the inactive-row case at "03:30".
- **Idle label (K-8, post-Politur).** The label Text is now
  composed unconditionally above the hero (~:414) from
  `deskClockHeroLabel` (~:605): idle/inactive →
  `THE TIME NOW`, stopwatch → `ELAPSED`, timed → `LEFT`,
  at `alphas.numberLine` (standing 0.55 in idle — the
  fraction is 0, so idle never descends). Idle's hero wears
  a clock's clothes and says so.
- **Idle prints no sub-line (P-17).** The sub-line and block
  line sit inside `if (running)` (~:436), where
  `running = activeSession != null && activeSession.isActive`
  (:319). An inactive row kept in the flow cannot open the
  gate.
- **Idle prints no banner and wears no slot (A-8).** The
  banner slot is composed only inside `if (running)`
  (~:538), and `deskClockBannerText` is called only there.
  The VM's `activeTask` QUEUED fallback
  (MonolithViewModel.kt:544–548) still reaches the overlay
  as an argument, but on an idle face it is never read —
  the nightstand's one sentence cannot name a session that
  is not running. No fabricated session name prints in
  idle: the banner chain (task → intention → profileName
  floor, ~:745) is unreachable in this state.
- **Armed-undecided prints nothing at all.** The overlay's
  visibility flag is written true only by the settle tick
  (MonolithViewModel.kt:1213–1232), a hand raise
  (:1955 ff.), or the wake re-raise (:363–371). Before any
  of those, `visible = false` and the overlay early-returns
  at AirlockAndDisarmOverlays.kt:~313
  (`if (!visible && groundAlpha <= 0f && printAlpha <= 0f) return`)
  — no Box, no clickable layer, no figure. The state's
  numbers stay machinery: DeskClockSettle.kt's 20 s backstop
  (:~270, `SETTLE_MILLIS`), the 7–14 Hz band and the duty
  cycle are pure values with no print path. The three-state
  law holds in `tick`: HELD returns false before both raise
  paths (:~205), REST requires 2 sustained beats (Wave-2
  Samt F-1 — this supersedes DESIGN §1.2's "raises at once"
  wording; the code and its test
  `restRaisesOnTheSecondConsecutiveBeat_notTheFirst` agree,
  the spec sentence is stale, not the machine), UNKNOWN
  takes the 20 s backstop from
  `max(armStartMono, lastInteractionMono)` alone.
- **Stopwatch sub-line (A-6(ii)).** `deskClockSubLine`
  :675–676: the stopwatch branch is FIRST —
  `if (session.isStopwatchMode) return deskClockNowLine(nowWallMillis)`
  — "NOW HH:MM" alone, before the last-minute promotion and
  before ENDS is ever derived. No ENDS, at any elapsed, in
  any minute. The face table's stopwatch case
  (DeskClockFlowTest, `faceTable_…`: hero "2:05", sub-line
  "NOW 21:47") pins it.
- **Stopwatch: no invented span, anywhere.** The tear rule
  is gated `running && isStopwatchMode != true` (~:469);
  `deskClockBlockLine` returns null for stopwatch (:~698);
  the descent fraction's `when` excludes stopwatch
  explicitly (~:336), so `spanElapsedFraction = 0f` and the
  alphas stand at the standing constants all night;
  `deskClockPrintAlphas`' dawn ramp is guarded
  `!isStopwatch` (:155) even though the call site passes a
  span-remaining for the row. The Wave-2 D-2 disease (a
  fraction against an invented 1,200 s span) has no path
  back in this state. Stopwatch hero grammar is L1
  (Politur's C-8 verification stands): 7,549 s → "2:05",
  never "125:49".
- **Glide placement.** The burn-in offset is applied once,
  at the Column only (AirlockAndDisarmOverlays.kt:382) —
  Mond J3's condition holds: one rigid grid, the tear
  rule's dashes cannot moiré against a second one. The
  step cadence is one step per 60 ticker beats
  (MonolithViewModel.kt:1183–1185), in every state,
  session or none. (What the step is MEASURED in, and how
  it MOVES, are G-1/G-2.)

## DEFECTS — 7

### G-1 · HIGH — the burn-in glide is measured in pixels, not dp: on his phone the grid shrinks ~2.7× ★ WORST

AirlockAndDisarmOverlays.kt:364–365 + :382:

```kotlin
val dx = ((burnInStep % 5) - 2) * 3
val dy = (((burnInStep / 5) % 5) - 2) * 3
…
.offset { IntOffset(dx, dy) }
```

`IntOffset` is PIXELS. The ruled grid (DESIGN-face-spec §0;
audit A-5's own words: "the burn-in law spends 520 ms to
move this column **3 dp**") is a 3 dp step on a
{−6, −3, 0, +3, +6} **dp** grid. The code steps 3 px: on an
xxhdpi phone (≈2.6–3.0× density) that is ≈1.0–1.1 dp per
step and a whole grid ≈4–4.6 dp wide instead of 12 dp.
The quiet states are exactly where this bites: idle and
stopwatch faces stand the longest, at standing alpha, on
an OLED panel — the glide exists to keep the same pixels
from holding the same print, and at one-third amplitude
the hero's strokes re-light nearly the same pixels every
minute, all night. Same disease as Schliff's F-5 (the tear
rule's dash pattern in raw px), one seat over: two unit
systems in one face, and the px one is always the quiet
one. No test can see it — `burnInStep` arithmetic is pure
Int math either way.
CURE (exact): keep dx/dy as dp counts and spend them as dp:

```kotlin
// in the Column modifier, replacing .offset { IntOffset(dx, dy) }:
.offset(x = dx.dp, y = dy.dp)
```

(`dp` is already imported; the `IntOffset` import at :47
then has no other use in the file — grep-verified — and
dies with the cure.)

### G-2 · MEDIUM — the glide does not glide: the column teleports in 0 ms, where the room's law moves it in 520 Curve C

The offset above is a plain derived value: when
`burnInStep` flips, the column recomposes at the new
position instantly. Grep of the overlay: the only
`Animatable`s are ground (:203), print (:204) and the
surrender fade (:303); there is no `animateIntOffsetAsState`
/ `animateFloatAsState` anywhere in the file. Against that,
the room's character for this exact motion is written
three times: audit §2 (Nacht — "glide + descent share
520 Curve C"), audit A-5 ("the burn-in law spends 520 ms
to move this column 3 dp"), DESIGN §3 ("the descent and
the glide share 520 Curve C's character"). Every other
motion on this face is frame-animated on a named curve;
the one motion that repeats every minute, all night, in
the quiet states, is a cut. A 3-unit jump (12 at the wrap,
G-5) at the minute tick is the only movement a sleeping
room ever makes — it should arrive like light, not like
a setting being flipped. (This is Politur P-3's class —
assigned-not-animated — on the glide instead of the dawn
alphas; the two cures are independent and both owed.)
CURE (exact, after G-1 so the target is real):

```kotlin
val glide by animateIntOffsetAsState(
    targetValue = IntOffset(
        with(density) { dx.dp.roundToPx() },
        with(density) { dy.dp.roundToPx() }
    ),
    animationSpec = tween(520, easing = MonolithCurves.EasingC),
    label = "desk_clock_burn_in_glide"
)
// Column: .offset { glide }
```

### G-3 · MEDIUM — the idle clock's only guaranteed repaint is the burn-in step, whose phase is the VM's birth, not the wall minute: THE TIME NOW can be a minute stale, every minute

AirlockAndDisarmOverlays.kt:318 captures
`val nowWallMillis = System.currentTimeMillis()` once per
composition, and the idle hero is `HH:MM` of that stamp.
In the running states the session row re-emits every
second (the VM ticker's `updateSessionProgress`), so the
hero is row-driven and fresh. Idle has no row. Nothing in
the overlay drives its own recomposition, and the only
input guaranteed to change under an idle face is
`burnInStep` — one step per 60 ticker beats
(MonolithViewModel.kt:1183–1185), a cadence whose phase is
wherever the ticker happened to start, never aligned to a
wall-minute boundary. Consequence, by construction: the
idle hero turns its minute somewhere inside the real
minute, up to ~60 s late — a nightstand clock that reads
03:29 for the first seconds (up to the first minute) of
03:30, every hour, all night. It is the idle state's only
job to be the time; a clock that is wrong on a phase lag
is a finish defect, not a rounding note.
CURE (exact, print-layer, overlay-local): while the face
is visible and NOT running, keep a minute-boundary clock
in the composition —

```kotlin
var idleNowMillis by remember { mutableStateOf(System.currentTimeMillis()) }
LaunchedEffect(visible, running) {
    if (visible && !running) {
        while (true) {
            val now = System.currentTimeMillis()
            idleNowMillis = now
            // sleep to 50 ms past the next wall-minute turn
            delay(60_000L - (now % 60_000L) + 50L)
        }
    }
}
// hero/label derivations read idleNowMillis when !running,
// nowWallMillis otherwise (one `faceNowMillis` val).
```

(Equivalent: a VM minute tick aligned to the wall clock.
What must die is the dependence on an unaligned 60-beat
counter for a wall-clock reading.)

### G-4 · MEDIUM — the A-10(v) keep-awake move silently narrowed: a hand-raised IDLE clock no longer keeps the screen awake and dies at the OS timeout

MonolithViewModel.kt:1270 sets `_deskClockKeepAwake` only
under `visible && clockSession != null`; :1316–1320 then
forces it false on every beat where `clockSession == null`
("No session, no hold"). Before the merge the face itself
held `FLAG_KEEP_SCREEN_ON` whenever it was visible, idle
included (MERGE-NOTES: the overlay's keep-awake
`DisposableEffect` is DELETED under A-10(v)). The move to
the VM's *session* machinery was ruled for sessions; idle
is not a session, and it fell out of the law without any
audit line pricing it: hand-raise the clock from the DESK
CLOCK chip with nothing running — §1.1's KEEP state, the
chip's whole purpose — and the OS screen timeout (his,
likely 30 s–2 min) kills the nightstand mid-glance-chain,
every time. A desk clock that cannot stay up is not
standing in its state.
CURE (exact): in the VM ticker, the keep-awake branch
keys on visibility for the hand-raised no-session case,
with no surrender deadline (there is no session to
surrender):

```kotlin
if (_isAlwaysOnDeskClockVisible.value &&
    (clockSession != null ||
        _deskClockEntryCause.value == DeskClockEntryCause.HAND)) {
    _deskClockKeepAwake.value = true
    …
}
// and the clockSession == null clear at :1316 must exempt
// the visible hand-raised idle case.
```

If Wacht instead rules that idle SHOULD die by the OS
timeout, that ruling needs one written line in the audit's
state inventory — today the inventory says KEEP, and the
code quietly does otherwise.

### G-5 · LOW — the glide's path is a raster with a corner-to-corner wrap, not the figure-8 its own name claims

The step mapping (AirlockAndDisarmOverlays.kt:364–365)
walks the 5×5 grid row-major: at each row end dx jumps
+6 → −6 (a 12-unit leap, twice the ruled step, across the
whole face), and at the 25-step wrap dy jumps +6 → −6
with it — a full diagonal teleport of the column once
every 25 minutes, in the states that stand all night.
With G-2 uncured it is instant; with G-2 cured it is the
longest sweep the face ever makes, on a 520 ms curve
meant for a 3 dp step. The VM's own name for the counter
is `burnInFigure8MinuteStep` (MonolithViewModel.kt:479–480)
— a figure-8 path is what the name promises; a raster is
what the mapping walks.
CURE (exact): serpentine the traversal so consecutive
cells are always one step apart —

```kotlin
val row = (burnInStep / 5) % 5
val colRaw = burnInStep % 5
val col = if (row % 2 == 0) colRaw else 4 - colRaw
val dx = (col - 2) * 3
val dy = (row - 2) * 3
```

(the 25-step wrap still returns corner-to-corner once per
cycle; ping-ponging `row` the same way closes even that —
either is honest; the row-end leap is the charge.)

### G-6 · LOW — the idle lie's plumbing survives: `dialMinutes` is still a parameter of the face

AirlockAndDisarmOverlays.kt:188 declares
`dialMinutes: Int`; :317's comment admits "dialMinutes is
no longer read here at all"; MainActivity.kt:799 still
collects and passes it. The print is honest today
(VERIFIED CLEAN), but the face's contract still declares
the composer's dial an input to the nightstand — the
exact wire the ST-01/FL-03 lie ran on, left in the wall
for the next hand to reconnect in one line. A finished
file does not keep the dead input of a killed lie in its
signature.
CURE (exact): delete the parameter at :188 and the
`dialMinutes = dialMinutes,` argument at
MainActivity.kt:799 (the VM's own `_dialMinutes` flow is
the composer's and stays; only the face's door closes).

### G-7 · LOW — the cell's own documents contradict each other on whether a dismissal persists (armed-undecided's aftermath)

DESIGN-face-spec.md §1.2 states: "A dismissal is
persisted (V2 + M-4): process death never resurrects a
dismissed face." DeskClockSettle.kt's header states the
opposite, as design: "The dismissal bit is in-memory by
design" and FLOW §2.2's "one acceptable roughness: a cold
process may re-raise once over a session a warmer process
saw dismissed" — and the code is the header's
(`decidedSessionId` is a plain field, DeskClockSettle.kt
:~89; MERGE-NOTES Klinge #12 records the in-memory bit as
the merged disposition). Both documents ship in the same
merge. User-visible consequence: kill the process during
armed-undecided after a dismissal and the face the hand
already dismissed raises once more — under the spec's
sentence that is a resurrection bug; under the machine's
sentence it is priced behaviour. One of them is false on
the merged tree, and a finisher cannot tell Wacht which
law he merged.
CURE (exact, Wacht's call, one line either way): strike
the persisted sentence from DESIGN §1.2 to match the
machine's stated law, OR persist `decidedSessionId`
(the ReceiptFiledStore pattern already in this merge)
and strike the header's roughness. What must die is the
contradiction, filed here so it dies on the record.

## CROSS-REFERENCE (verified present, owned elsewhere — not counted)

- **The running banner still trusts the QUEUED fallback.**
  Inside `if (running)`, the banner reads `activeTask`,
  whose VM derivation falls back ACTIVE → QUEUED
  (MonolithViewModel.kt:544–548): a routine SLEEP session
  running beside a queued hand task would print the queued
  task's title ahead of the row's own `profileName` floor.
  Idle is safe (the gate above), so this is not charged
  here — it belongs to the banner/SLEEP seats (Schliff /
  Kante), flagged so A-8's cure is not read as complete at
  the derivation, only at the idle gate.

## EDITS MADE BY THIS SEAT

None — logged deliberately (see STATE OF THE GROUND):
the subject file changed under this audit as Politur's
lane landed, and sibling finishers were still seated on
it. Every cure above is copy-exact and lands in minutes
once Wacht calls them. No compile was run (standing
rule); no commit, push or GitHub post — Wacht carries
those.

## CLOSE

**Defect count: 7** (1 HIGH, 3 MEDIUM, 3 LOW), plus 1
cross-reference owned by other seats.
**Worst: G-1** — the burn-in glide steps 3 *pixels* where
the room's law rules 3 *dp*. In the quiet states — the
faces that stand longest, at standing alpha, on OLED —
the protection the whole glide exists for runs at roughly
a third of its ruled amplitude, and nothing in the tree
can catch it: the arithmetic is unit-blind, the tests are
unit-blind, and the face looks, at a glance, like it moves.

What I want next: G-1 + G-2 landed as one cure (the glide
in dp, on 520 Curve C) and one §7-census frame of the idle
face shot a full minute apart, twice — the grid visibly
walked, the minute honestly turned.
What I could do unassigned: the same unit audit (px spent
as dp) across the Deck's offsets and the pill's geometry,
where Schliff's F-5 pattern suggests G-1 is a dialect,
not a slip.

— Glaette, Finisher 1 (Idle · Armed-Undecided · Stopwatch),
Wacht's cell, Oct 30, 2026
