# 07 — Reihe (Improvers) · Routine Rows, Shield — PLAN WAVE

Element: `RoutineRow` + ROUTINES section, `ShieldMatrixScreen.kt`. Scope is the row's
share of "routines are fully his": what the collapsed row claims, what its editor
offers, and the row-level surfaces for create / duplicate / rename / delete /
pause-until. Firing machinery and full flows are Sten's; every word the row prints
about that machinery is mine, and the row prints nothing the machinery can't swear to.

Sound vocabulary (existing engine, no new assets): **detent** = `playRotaryDetent`
— waveform [0, 7, 5] ms / amps [0, 180, 70], transient 680 Hz, 6 ms, gain 0.38 ·
**firm click** = `playFirmClick` — [0, 13, 20, 8] / [0, 225, 0, 135], 620 Hz, 12 ms,
0.5 · **latch on/off** = `playMechanicalLatch` — on [0, 11, 26, 9] / [0, 215, 0, 150],
760 Hz, 16 ms, 0.54; off [0, 9, 22, 10] / [0, 165, 0, 195], 520 Hz, 16 ms, 0.54.
Curves: A = cubic-bezier(0.2, 0, 0, 1), B = (0.05, 0.7, 0.1, 1), C = (0.3, 0, 0.8, 0.15);
durations only from {180, 260, 280, 420, 520} ms.
Tokens: day ground `#E4DFDA`, recess `#D8D1CB`, raised `#FAF6F0`, ink `#252323`,
on-ink `#FBF1EC`, muted `#5F5856`, hairline `#75615B5A` · night ground `#171412`,
recess `#100E0C` (below ground), raised `#362C27`, control fill `#4A423C`, text
`#D2C6BB` / `#9C8E84` / `#776A60`, on-fill cream `#FFE4CF`. Type: Sora for names,
Anonymous Pro for every numeral, label and state word. No green anywhere; orange
spent nowhere on this element (see CONFLICTS 1).

---

## CURRENT — verified in code, Oct 29

**What exists.** `RoutineEntity(id, name, startMinutes, endMinutes, daysMask,
loadoutName, enabled)` — those seven fields are the whole truth the row may draw on;
there is no pause field, no miss count, no attendance, no seed-origin marker.
ViewModel offers exactly four operations — toggle, cycle loadout (STUDY→WORK→SLEEP),
±30 min time adjust (rejects raw spans outside 30–720 min), day-bit toggle — each
persisted via `repository.updateRoutine` + `RoutineScheduler.scheduleRoutine`.
DAO: `getAllRoutines` (ORDER BY startMinutes ASC), `getRoutineById`,
`insertRoutines` (plural, seed-only), `updateRoutine`. **No single insert, no
delete, no rename path anywhere in the app** — `insertRoutines` is called once, from
`ensureSeededAndSyncPackages`, when the table is empty. Seeds today:
`Morning Deep Work 09:00–13:00 MON–FRI · STUDY` and `Night Wind-Down 22:30–07:00
DAILY · SLEEP` — neither is his day.

**The row today.** Time column 54dp (start AnonymousPro Bold 15sp over
`→ end` 11sp muted); name Sora Bold 16sp over meta `DAYS · LOADOUT` AnonymousPro
12sp muted; ON/OFF word-cap 48×32dp, radius 8, ink fill when on. Editor is a bare
`if (expanded)`: two rows of 42dp chips (START −30/+30, END −30/+30, loadout cycle
chip; seven single-letter day chips M T W T F S S) that overflow a 360dp phone
(~368dp of content in ~336dp), start under the time column, and appear by teleport.

**Corrections to my wave-1 file, from re-reading the code (nobody re-buys these):**
(1) Sound is *not* absent — the ViewModel plays latch/firm-click/detent on every
routine operation. What is silent is the row's own gesture: expand/collapse is local
`remember` state with no callback, so opening the editor — the gesture that reveals
every control — has no sound, no haptic, no motion. (2) Sort order is *not* broken —
the DAO query sorts by startMinutes; the UI merely trusts it instead of guaranteeing
it, so it is one DAO edit away from silently lying. (3) The day Shield render in
`shots/` is a stale build (name-first rows, "TAP TO EDIT", header word
`RUNS ITSELF`) — it does not depict current code; the night render `n02_shield.png`
does. No render of the expanded editor exists in either set. `RUNS ITSELF` is dead
language under the no-idea verdict and must never ship on this section again; the
current header (`2 ACTIVE`) is honest.

**The lies, in order of harm.**
1. **Ghost minutes.** `RoutineAlarmReceiver` on START: if no session is active it
   mints a full session for the window's whole duration — phone in a drawer, man
   asleep, minutes accrue and land in history as study. The row says nothing.
2. **Silent collision.** If a manual session *is* active, START does nothing and
   records nothing. The row still reads as if the routine ran its window.
3. **Duration is computable but unprinted.** `RoutineSchedule.durationMinutes()`
   exists and drives the scheduler; the meta line prints days and loadout only. The
   ★ from wave 1 is still open — verified at the call site.
4. **A lying helper.** `durationMinutes()` coerces to 15–720; `adjustRoutineTime`
   rejects spans outside 30–720. A window can exist whose printed length and run
   length disagree (see CONFLICTS 4).
5. **Live state exists and is unused.** `isActiveNow()` and `nextStartMillis()` are
   implemented; the row calls neither. At 10:00 the running routine and the one
   12 hours away are typographically identical.
6. **Disabling a live routine strands its session.** Toggle → `scheduleRoutine`
   cancels *both* boundaries and returns early when disabled — including the END
   alarm of a window running right now, so that session is never stood down by
   the routine that started it.
7. **Disabled reads at full shout.** Only the cap recolours; name and times keep
   full ink. Tone carries no state.

---

## LADDER — five rungs, each independently shippable

### Rung 1 — THE LINE TELLS THE TRUTH (reading rung; contains the ★)

No new controls, no schema, no new callbacks except one string parameter.

- **Meta line gains duration and keeps it raw:** `"DAILY · STUDY · 3H 00M"` —
  AnonymousPro 11.5sp, letterSpacing 0.4sp, muted. Duration is the *raw wrapped
  span* (end − start, +1440 if ≤ 0), formatted `3H` / `3H 30M` / `45M` — **not**
  `durationMinutes()`'s coerced output, so the row never prints a length the
  scheduler won't run (CONFLICTS 4). Under an hour and over 12h both print as-is.
- **Overnight marked:** end line becomes `→ 07:00 +1D` whenever the span wraps.
  Time column 54 → 58dp to hold it.
- **A state line is born** under the meta — AnonymousPro 11sp, ink when it reports
  the routine's own state, muted otherwise; exactly one of, by priority:
  `LIVE NOW — ENDS 08:00` (from `isActiveNow`, recomputed on composition + 60 s
  tick while Shield is visible) · `HELD — MANUAL SESSION` (active session exists
  and its profile is not `Routine: <name>`) · `NEXT · TODAY 05:00` /
  `NEXT · TOMORROW 05:00` / `NEXT · SAT 05:00` (from `nextStartMillis`) ·
  `NO DAYS SET` (mask 0) · `OFF`. Shield gains one new parameter,
  `activeSessionProfileName: String?`, fed from the existing active-session flow.
  The section header's right side stops counting (`2 ACTIVE`) and prints the
  single next event instead: `NEXT 05:00 FASTED BLOCK` — the section answers
  before any row is read.
- **Sorting guaranteed at the surface:** the section renders
  `routines.sortedBy { it.startMinutes }` regardless of DAO order.
- **Disabled dims:** name and start time drop to muted `#5F5856` (night `#776A60`);
  meta, end and state line at 60% alpha of muted. State readable from tone alone.
- **Text bounded:** name and meta `maxLines = 1` + ellipsis. Row is now a fixed
  module: padding vertical 8dp, collapsed height 60dp (three-line name column
  against the two-line time column, centred as one block).
- **First motion and first row-owned sound:** editor wrapped in
  `AnimatedVisibility` — enter `expandVertically` 260/A + `fadeIn` 180/A, exit
  `shrinkVertically` 260/A + `fadeOut` 180/A. Expand/collapse plays detent
  (row receives the `soundEngine` Shield already holds). A Canvas chevron
  (two 1.6dp strokes, 10×6dp, muted) at the name baseline rotates 0→180°,
  260/A. Day chips relabelled `MO TU WE TH FR SA SU` (≈50dp wide — the 48dp
  touch floor is met as a side effect).

*Depth/tokens:* flat throughout — dimming by text token only, no fills added;
live state carried by ink weight + the words, no new colour.

### Rung 2 — RESEED TO HIS REAL DAY (data rung)

Replaces the two factory seeds with his day and migrates without touching anything
he has edited.

- **New seeds:** `FASTED BLOCK 05:00–08:00 DAILY · STUDY` (3H) ·
  `DAY BLOCKS 09:00–19:30 DAILY · STUDY` (10H 30M) · `SLEEP 20:00–03:30 DAILY ·
  SLEEP` (7H 30M, +1D). The gaps are deliberate and printed by their absence:
  08:00–09:00 is gym and sadhana, phone free; 19:30–20:00 is the wind-down gap.
  Saturday test days stay DAILY — a test is a study block; if he arms a test
  session by hand first, Rung 5's HELD states it truthfully rather than a
  special Saturday rule.
- **Migration rule:** a stored row migrates **iff** its tuple
  (name, start, end, daysMask, loadoutName) exactly equals a factory seed tuple —
  which today can only mean untouched, since rename and create do not exist yet.
  Migration rewrites the tuple to the corresponding new seed (Morning Deep Work →
  FASTED BLOCK values under its *new* name `FASTED BLOCK`; Night Wind-Down →
  SLEEP) and preserves `enabled` exactly as he left it — a seed he switched off
  stays off. DAY BLOCKS is inserted fresh, enabled. Any tuple that differs in any
  field is his, and is not read twice.
- **One-shot guarantee:** the migration runs once, guarded by a `routinesReseededV2`
  flag on the single-row guard-settings record, and must land **before** Rung 4
  (once rename exists, tuple-matching can no longer prove "untouched").
  Alarms re-armed via `scheduleAll` in the same pass.
- **Feel:** none. This rung is invisible when it works. On first Shield open after
  migration the section header state line already says `NEXT …` against the new
  day — that is the whole announcement; no banner, no sound (passive change).

### Rung 3 — THE PANEL (editor rebuilt; the sled; HOLD made structural)

- **The word-cap becomes a sled** — one switch grammar with the app rows below.
  Track 52×32dp, radius 8, recess fill (day `#D8D1CB` / night `#100E0C`), no
  border. Thumb 26×26dp, radius 6, inset 3dp, travel 20dp in **200 ms / A**. ON:
  thumb right, ink fill (day `#252323` / night `#4A423C`) with one centred marker
  line 2×10dp in on-ink; the word `ON` printed in the empty left half,
  AnonymousPro Bold 9.5sp, letterSpacing 0.5sp, ink. OFF: thumb left, raised fill
  (day `#FAF6F0` / night `#362C27`), word `OFF` in the empty right half, muted.
  Position states it, the word confirms it. Hit box 52×48dp. Sound stays the
  ViewModel's latch (on 760 Hz / off 520 Hz) — one gesture, one owner.
- **Window bar:** 2dp vertical bar at the time column's left edge, height of the
  time block; enabled = ink (night `#4A423C`), disabled = recess, no-days =
  hairline. On expand it extends to span header + panel, height animated on the
  same spec as the panel — row and editor read as one riveted object.
- **Editor geometry:** hairline (hairline token) from the name axis to the right
  edge; container indented to the name axis — time column 56dp + gutter 12dp =
  controls start 80dp from screen edge, under the name, never under the times.
  Accordion: expanded id hoisted to the screen; opening one row closes any other.
  Expand motion upgrades to `expandVertically` **420/B** + `fadeIn` 180/A delayed
  60 ms; collapse stays 260/A. Panel rhythm: micro-label (AnonymousPro 10sp,
  letterSpacing 1sp, muted) → 6dp → control → 16dp → next group.
- **TIME group — stepper wells:** START and END as two stacked clusters, 40dp
  high: label cell 44dp (`START` / `END +1D` when wrapped), then a recessed well
  (recess fill, radius 8) of `[ −30 zone 44dp | value min 64dp, AnonymousPro Bold
  15sp, centred | +30 zone 44dp ]` with 1dp hairline dividers. The value lives
  inside the control; header times update from the same state in the same frame.
  Each step: detent. **Hold-to-repeat:** after 350 ms held, steps repeat every
  90 ms, one detent per step (reads as a ratchet, not a buzz); release seats the
  final value with one firm click. Caption printed under the pair, letterpressed:
  `STEP 30M · HOLD TO REPEAT`, AnonymousPro 8.5sp, muted at 70%.
- **DAYS group — segmented strip:** full editor width, 40dp, radius 8, hairline
  border, seven equal cells split by hairlines, `MO…SU` AnonymousPro 11sp.
  Selected cell: ink fill, on-ink, Bold. Unselected: transparent, muted. Tap:
  detent + fill crossfade 180/A. Right side of the label line carries the presets
  as words — `DAILY · MON–FRI · CLEAR` — ink when the mask equals the preset,
  muted otherwise; a preset tap diffs the mask through the existing day callback
  and plays **one** detent for the whole preset.
- **LOADOUT group — 3-stop strip:** identical construction, cells
  `STUDY / WORK / SLEEP`; selection invokes the existing cycle callback
  (k − j) mod 3 times — data layer untouched. One firm click per change.
  Label line's right side prints the stop's number as the knob band prints it:
  `01 / 02 / 03`, AnonymousPro 10sp muted.
- **HOLD made structural — editing a live routine:** if `isActiveNow`, the panel
  opens under a printed line in ink: `RUNNING NOW — CHANGES APPLY FROM THE NEXT
  WINDOW`. Time/day/loadout edits stage in panel-local state and commit only on
  panel close, written as the *next* configuration; the running window's alarms
  and session are never mutated mid-flight. Toggling the sled OFF while live
  prints, in place of the state line, `WILL NOT FIRE AGAIN — THIS WINDOW RUNS TO
  08:00`, and the scheduler must keep that window's END boundary armed exactly
  once (fixes CURRENT lie 6 — mechanism Sten's, the promise mine, and the row
  does not print it until the mechanism keeps it).

### Rung 4 — THE FRONT DOOR (create / duplicate / rename / delete / pause-until)

Row-level surfaces only; Sten owns the flows underneath. Data needs: single-row
`insertRoutine` returning the id, `deleteRoutineById`, and the pause fields in
Rung 5's schema — this rung's UI ships against those three calls.

- **Create:** section header gains a right-aligned word `+ NEW` (AnonymousPro
  11sp Bold, ink, 48dp hit height). Tap: firm click, and a **draft row is born in
  place** at its chronological position, already expanded — bar at hairline
  token, name field live with `NEW ROUTINE` selected-all (typing replaces it),
  defaults `05:00–06:00 · DAILY · STUDY`, panel identical to Rung 3's. The draft
  exists only in UI state: no DB row, no alarm, and cancelling it leaves zero
  trace anywhere. The sled position holds two margin words instead: `SAVE`
  (ink, Bold) and, in the panel footer, `CANCEL` (muted). SAVE: latch-on sound,
  insert fires, the bar crossfades hairline → ink 260/A, `SAVE` is replaced by
  the sled in its ON finish (no motion — it was never off), panel stays open.
  Three taps plus a name, inside ten seconds, without leaving Shield.
- **Duplicate:** panel footer word `DUPLICATE` (AnonymousPro 10sp, muted →
  ink while pressed). Tap: firm click; the copy is inserted with identical
  fields and name + ` COPY`, born expanded directly beneath the original,
  original's panel closing on the same 260/A spec — focus visibly moves to the
  copy, which is the thing he now edits.
- **Rename:** panel's first group is `NAME` — a field in Sora SemiBold 15sp,
  ink, over a single hairline, holding the current name in its stored case
  (the row shouts it uppercase; the field shows what is actually stored).
  Commits on IME-done or blur, only if non-blank and changed: firm click.
  Blank commit reverts to the stored name with a detent pitched down to
  520 Hz — a refusal the hand can hear. A rename of a live routine is a
  next-window change under Rung 3's rule and says so in the same printed line.
- **Delete:** panel footer's last word, `DELETE`, muted. First tap arms it
  inline: the word becomes `DELETE — TAP AGAIN · ITS RECORD GOES WITH IT`
  (AnonymousPro 10sp, ink, Bold) for 4 s, then decays back over 260/A. Second
  tap inside the window: latch-off sound, the row collapses (260/A shrink +
  fade), then delete + alarm-cancel fire — in that order, so the screen never
  shows a row for a routine that no longer exists. **A live routine cannot be
  deleted:** the footer instead prints a disabled word, `RUNNING — DELETE AFTER
  08:00`, muted at 60%. Attendance dies with the row, and the confirm line is
  where that is stated — once, before it happens.
- **Pause-until:** panel group `PAUSE`, a 3-cell strip in the DAYS strip's
  construction — `SKIP NEXT` / `UNTIL TOMORROW` / `PICK DATE` — plus, when PICK
  DATE is chosen, a horizontal strip of the next 14 days (day cells 40dp,
  date numeral AnonymousPro Bold 13sp over weekday 9sp muted; selected cell ink
  fill). Semantics printed, not implied: SKIP NEXT = exactly one firing is
  skipped, then the routine re-arms itself; UNTIL TOMORROW = paused until
  03:30, his day-start; PICK DATE = paused until 03:30 on the chosen date.
  Selection: firm click. **Paused row state:** tone = disabled dimming; bar =
  hairline; the sled is replaced by the word `PAUSED` (AnonymousPro Bold 11sp,
  muted, 48dp hit box) — tapping it resumes immediately (latch-on; state line
  reprints NEXT). State line while paused: `PAUSED UNTIL FRI 31 OCT — WILL NOT
  FIRE` / `PAUSED — SKIPS NEXT FIRING ONLY`. Resuming from inside the panel is
  the PAUSE group's word `RESUME NOW`, ink Bold. **Auto-resume is silent** —
  no sound, no banner; the row simply reads armed again on the next tick.
  Pausing a live routine is allowed and prints `PAUSED FROM THE NEXT WINDOW —
  THIS ONE RUNS TO 08:00`.

### Rung 5 — NO GHOSTS (attendance truth; the row stops lying about the past)

The contract mailed to Sten, stated here as row behaviour. Schema additions on
`RoutineEntity`: `pausedUntilMillis` (0 = not paused; `Long.MAX_VALUE` = paused
until woken), `consecutiveMisses`, `lastOutcome` + `lastOutcomeDay`. Attendance
storage is a rolling record of the last 7 firings per routine (outcome + day) —
its **only** home is this row (see CONFLICTS 2).

- **Firing lifecycle, as printed:** at a START boundary nothing is minted. The
  state line becomes `FIRED 05:00 — WAITING FOR YOU` for a **10-minute grace**.
  A presence signal inside grace (unlock with hand-on-phone evidence: Monolith
  foregrounded, a shield intercept, the Deck touched) mints the session *at
  that moment*, and the line becomes `LIVE — STARTED 05:07 · ENDS 08:00`.
  Minutes exist only from presence. This is not a consent ritual and adds no
  gesture: a man who picks up his phone inside his own routine window has
  started; a phone nobody touches has not, and the record finally agrees.
- **HELD:** manual session active at the boundary → no mint, no queue, no
  extension; attendance records HELD; state line `HELD — MANUAL SESSION` for
  the window's length. HELD is never a miss and never advances the counter.
  The window's END never disarms a session whose profile is not its own.
- **MISSED:** grace expires with no presence → no session row is written
  anywhere, zero minutes exist, attendance records MISSED, `consecutiveMisses`
  +1, and the state line carries the news until the next state change:
  `MISSED TODAY — NO ONE THERE`. If presence arrives *after* grace but inside
  the window, the routine does not retro-mint: the line reads `MISSED 05:00 —
  START IT BY HAND IF YOU WANT IT`. Truth over helpfulness.
- **3 in a row → auto-pause:** on the third consecutive MISS the routine sets
  paused-until-woken and the sled is replaced by the word `WAKE` (ink, Bold).
  State line: `PAUSED ITSELF — MISSED 3 IN A ROW · 27 28 29 OCT`. Tapping WAKE:
  latch-on, counter resets, NEXT reprints. The note stays in the panel footer
  until he wakes it — it is never silently cleared by a KEPT firing, because
  auto-pause means no more firings happen until he acts.
- **Attendance, printed once:** panel footer, one line, AnonymousPro 10sp
  muted: `LAST 7 · KEPT 5 · HELD 1 · MISSED 1`. KEPT = presence inside grace.
  The collapsed state line owns only the *latest single outcome* (as current
  state); the footer owns the aggregate — neither re-renders the other.
- **SLEEP is exempt from attendance:** a lockdown has no one to attend. Its
  footer truth is binary and factual: `HELD THE NIGHT` (shield ran the window)
  or `DID NOT RUN — PHONE OFF`. SLEEP never mints study minutes, never misses,
  never auto-pauses.
- **Sound discipline:** firing, grace, miss, held and auto-pause are passive
  events — **zero sound, zero haptics, ever.** Only his hand sounds: WAKE
  (latch-on), resume (latch-on). Pre-Rung-5 ghost minutes already sitting in
  session history are history's lie, not this row's; attendance counting starts
  at this rung's ship date and the footer says so for its first week:
  `COUNTING FROM 29 OCT`.

---

## ★ — PRICED

**Print the raw duration as the meta line's third segment —
`DAILY · STUDY · 3H 00M`.**

Price: one format function (~10 lines), one call-site change in `RoutineRow`,
one overnight test (SLEEP 20:00 → 03:30 must read `7H 30M`; FASTED `3H`;
DAY BLOCKS `10H 30M`). **≈15 minutes**, zero schema, zero callbacks, zero layout
risk (the meta line has the width; Rung 1's ellipsis protects the rhythm),
revertible in one commit. It ships *inside* Rung 1 but stands alone if the rung
stalls. Return: every glance at every routine, forever, stops requiring mental
arithmetic — the two times become a window with a length, and the overnight
riddle (22:30 → 07:00) defuses itself the moment `8H 30M` sits beside it. It is
also the precondition for triage: a list that states how much of his day each
routine owns is the list the state line, the bar and the pause words then rank.

---

## PEAK

The section is a departure board for one man's day: three rows, time-sorted, each one line that answers *what fires, when, as what, for how long* — and, when it matters, *what actually happened last time*.
The live row is the heaviest object on Shield by form alone: a 4dp ink bar at full text brightness and the words `LIVE — ENDS 08:00`; no colour is spent, because a signal that burns 7½ hours a night is not a signal.
Open a row and the panel is milled, not spilled: wells that hold their own values, strips that print every stop, hold-to-repeat that ratchets under the thumb.
Nothing on the row is decorative and nothing is missing: create is a word in the header, delete confesses its cost before it acts, pause says the date it ends.
And the record underneath is finally innocent: no minute exists that nobody lived, no miss is hidden, no collision is silent.
At 03:30, half asleep, one glance tells him the whole day — and every word of it is true.

(8 lines.)

---

## STANDARDS CHECK — the strictest in the cohort, because this element lied most

**ARM ≤ 10 s.** Shield is not on the arm path and this element adds nothing to
it: no dialog, no gate, no new step between opening the app and sliding. What
the row owes the standard is the *pre-arm answer*: from Shield opening, the
next firing (what, when, as what, how long) is readable in one glance at the
section header + first state line — under 2 s, no tap. Creating a routine from
`+ NEW` to saved is three taps and a name, inside 10 s, without leaving the
screen. If any rung's interaction cannot be done inside 10 s by a practised
hand at 03:30, it does not ship in that rung.

**HOLD absolute.** Four rulings, all structural, none advisory: (1) a running
window's configuration is immutable — edits stage and apply from the next
window, printed as such; (2) switching a live routine OFF keeps its END
boundary armed and its session untouched — the row prints `THIS WINDOW RUNS
TO 08:00` and the machinery must keep that sentence true; (3) a live routine
cannot be deleted; (4) a routine's END never disarms a manual session, and a
HELD routine consumes nothing from the banked hold — no break spent, no
counter moved (boundary agreed with Zug-II by mail). No state in this plan
asks the man for a decision mid-session; pause and delete decisions happen
in the panel, in cold blood, or not at all.

**TRUTH once, in human language.** Every fact has exactly one home: duration →
meta line · next firing → state line (mirrored once in the section header as
the section's answer, same computed value, same words) · latest outcome →
state line · 7-firing aggregate → panel footer · pause expiry → state line.
Nothing here is re-rendered by any tab: attendance is the row's, full stop.
Every string in this plan is printed verbatim and is the whole vocabulary —
no codes, no icons standing in for words, no `RUNS ITSELF`, no word the app
cannot make true (`WAITING FOR YOU` is printed only while the grace clock is
actually running; `WILL NOT FIRE` only while the pause is actually in force).
Ghost test, enumerated: a session minute can be minted by (a) manual arm —
his hand, fine; (b) routine START — gated by presence-or-HELD from Rung 5,
and until Rung 5 ships this row's state line must not claim a routine
*ran* — Rung 1's vocabulary deliberately says only LIVE / NEXT / HELD,
never KEPT; (c) boot/init rescheduling — `scheduleAll` arms alarms only,
mints nothing, verified. Coercion test: printed duration is the raw span,
so print and execution cannot diverge silently (CONFLICTS 4).

---

## CONFLICTS

1. **Live bar in accent (my wave-1 PEAK) vs Shield's zero-orange budget —
   DECIDED: the accent is dropped; the bar is redesigned.** Wave 1 spent day
   `#F55314` / night `#F47442` on the live routine's bar. I withdraw it, on
   three grounds: orange in this app is the COMMIT colour (the Deck slide)
   and a schedule fact is not a commitment; SLEEP is live 7½ hours every
   night, so the "accent" would burn for a third of every day — wallpaper,
   not signal, and a shouting orange rail on the 03:30 night screen; and the
   fact is fully carried by form — bar 2dp → **4dp**, fill stepping to the
   brightest text tier (day ink `#252323` held, night `#776A60`-tier bar →
   `#D2C6BB`), plus the ink words `LIVE — ENDS 08:00`. Transition on the
   minute tick: width + fill over 420/B, **silent** (passive change). Budget
   was a house position, not his law — but here the house position is also
   the better design, so it stands without needing his ruling. If Sarthak
   ever wants one orange event on Shield, it is his to spend, not mine.
2. **Ledger's fate reopened (wall, 05:15) vs this brief's "Ledger dead."**
   My element is safe under both verdicts and I flag it rather than resolve
   it: attendance's only home is the row (Rung 5). If the Ledger returns, it
   may not re-render attendance — one fact, one rendering — so the row's
   footer stays the record regardless. No rung blocks on the decision.
3. **DAY BLOCKS DAILY vs Saturday tests.** Synthesis specced DAILY; his
   Saturdays are test days. Kept DAILY: a test is sat inside a study block,
   and if he arms the test session by hand first, HELD states exactly that.
   A Saturday exception would be a rule he never stated.
4. **`durationMinutes()` coerces 15–720; `adjustRoutineTime` clamps 30–720.**
   The row prints the raw span (Rung 1), so the print can never be the lying
   party — but the scheduler still runs the coerced number until the two
   bounds agree. Sten's call which bound is real; mailed.
5. **Wave-1 findings corrected by code, for the record:** routine sound
   exists (ViewModel layer — only expand/collapse was silent); DAO order is
   sorted (the UI now guarantees it anyway); the day Shield render is a
   stale build and its `RUNS ITSELF` header is banned language.
6. **Guard is his open pick.** Nothing in any rung touches Guard, assumes
   its dissolution, or moves routine surfaces into it. Create, pause and
   attendance all live on Shield, where the rows already are.

**Mail sent:** Sten (`mail/sten/0541__from-reihe__routine-row-contract.md`) —
the Rung 5 presence-grace / HELD / auto-pause contract, the row-local front
door (pushback on any separate create screen), the schema asks, and the
coercion mismatch. Zug-II (`mail/zug-ii/0541__from-reihe__held-is-not-your-valve.md`)
— HELD consumes no banked hold; a banked-break ending is never a routine miss.
Inbox checked at start (empty) and before finishing.
