# FEEL — THE END OF THE NIGHT: THE DAWN RAMP, THE STAND-DOWN, THE RETURN

DAEMMER, Feel Specialist 5, Wacht's cell — issue #13
(Sarthak-2007/Focus-app, branch `agent/issue-13`, clone
`builds/cells/issue-13`). Slice: how the Session Room ENDS —
A-3's dawn ramp (final 60 s, every descended alpha home exactly
at T-0), A-10's 03:30 stand-down (silence for the kind, the
routine-born receipt filed and never standing, the light out
as a farewell), and E-1's return (veil / Landing) as the
stand-down's other half. Audit authority:
`studio/plan/AUDIT-session-room.md` §3 A-3 + A-10, §4 E-1,
§5; cell spec `DESIGN-face-spec.md` §4, §9, §10.

STATE OF THE GROUND. Bauer's merge landed WHILE this audit
ran, in pieces, in front of me: my first read found
`finishPulses` still returning `true` for the stand-down kind
(lane form) and MainActivity passing the overlay none of its
new parameters; the final reads (overlay 21:52, ReceiptState
21:53, repository 21:54, VM 21:58, MainActivity 22:03 IST —
stable across the last two checks) found the full act wired.
Every verdict below is against that final state, re-read
fresh at the end. Files judged:
`ui/components/AirlockAndDisarmOverlays.kt`,
`ui/MonolithViewModel.kt`, `MainActivity.kt`,
`data/ReceiptState.kt`, `data/MonolithRepository.kt`,
`data/ReceiptFiledStore.kt`, `service/TactileSoundEngine.kt`.
Lane reference: `builds/flow-clock/Focus-app` (it has no dawn
ramp at all and `finishPulses(STANDDOWN) = true` — the merge
is judged against the audit, not against the lane's silence).
Samt's FEEL file (settle / descent / surrender) owns the
surrender-fade race (her S-1), the charger-disconnect stamp
(her S-2), the yield-into-lit-Deck (her S-3) and the
suspension curtain-drop (her D-3); where my slice touches
them I cross-reference, I do not re-file. No code touched by
this seat; no compile run, per orders.

## THE VERDICT IN ONE BREATH

The end of the night is BUILT, and its skeleton is right: the
ramp's arithmetic is the audit's formula to the letter, the
silence gate sits on the ceremony's calls (the only place it
can silence the haptic half), the receipt is born filed at
three independent doors, and the last visible act is a black
ground lifting off a Deck already wearing the veil — a
farewell in shape. But the dawn is keyed to the wrong instant
on the one span that owns the night: a routine SLEEP row's
span ends up to a minute before its window does, so the ramp
peaks early, the hero reads 00:00 under a sub-line still
promising ENDS 03:30, and the peak is spent standing still
before the stand-down arrives. Worse, the stand-down's
trigger is an emission, not the instant — on the nights the
session timer wins the deactivation race, no write is
guaranteed at or after 03:30 and the whole farewell (veil,
filing, clock-hide, keep-awake clear) can drift until an
unrelated write rescues it, leaving a fully dawned face
standing lit past its own ending. And the return grammar is
only half-composed: the veil is worn at noon and three hours
stale as readily as at 03:30, a night hand-dismissal still
lands on the full-light Deck, and the unveil itself flashes
before it dims. Eight defects; the act is one keying, one
trigger and one scrim away from being the best ending in the
building.

---

## §1 · THE DAWN RAMP — dawn, or a brightness setting?

### What is right, and why it feels right

- **The formula is the audit's, exactly.** `deskClockPrintAlphas`
  (Overlay:127–152): in the final 60 s,
  `dawn = (60 − spanRemainingSeconds) / 60f` and every descended
  alpha lerps descended → standing by it — hero 0.36 → 0.55,
  number lines 0.50 → 0.55, the tear rule riding the hero's
  alpha, the exit line derived by the door invariant
  (max(0.40, hero × 1.10), capped at its standing 0.58).
  DeskClockFlowTest:448–466 pins the midpoint (r = 30, f = 1 →
  hero 0.455). At r = 1 the hero stands at 0.5468 of a 0.55
  standing constant — the last 0.003 is spent by the finish
  exit itself, which is correct: full is reached AT T-0, as
  the light leaves, never displayed as a plateau. That is the
  audit's sentence — "reaching full exactly at T-0" — kept.
- **The shape is LINEAR, and linear is right here.** The
  descent it mirrors is linear in span-elapsed fraction; the
  audit's claim is that the two are ONE gesture, down across
  the night, up at the end. Shaping the ramp with Curve C
  (0.3, 0, 0.8, 0.15 — it holds low, then rushes) would spend
  the last ~15 s surging and break the symmetry: a man awake
  for a manual finish, counting seconds with the hero, would
  feel the room lurch at T-10 instead of coming up with him.
  The ramp is not a sunrise simulation. It is the descent,
  reversed, at the speed the end demands.
- **The exit line dawns early, and that is correct.** It
  reaches its standing 0.58 at hero ≈ 0.527, around r ≈ 7,
  and waits there. The door is ready before the room is —
  the one element whose job is the next minute arrives first.
  Acquitted by design, named so nobody "fixes" it into sync.
- **The stopwatch takes no ramp** (Overlay:142's
  `!isStopwatch` guard). It has no T-0; a dawn without an end
  would be a lie. Honest.
- **A raise inside the final minute cannot restart the
  ramp**: the suspension branch (Overlay:138–141) lifts the
  alphas to standing first, and the ramp then lerps
  standing → standing — a no-op. The wake re-raise at 03:29
  finds the face already at dawn's destination. Correct
  composition of A-2(iii) with A-3, unprompted.

### Defects

**DAE-1 (HIGH) — On the SLEEP span, dawn is keyed to the
span's arithmetic, not to the stand-down's instant — it
peaks up to a minute early and the face contradicts itself
inside its own last minute.**
The ramp (and the hero) read `spanRemainingSeconds`
(Overlay:297–300 → MonolithModels:428–429:
`spanTargetSeconds − spanElapsedSeconds`). For a routine
row, `spanTargetSeconds` is built from a target that is the
window's remainder IN WHOLE FLOORED MINUTES at
materialisation: `remainderMinutes = ((end − nowMillis) /
60_000).toInt()` (MonolithModels:652, 669), carried as
`durationMinutes = window.remainderMinutes`
(RoutineDerive:167) into `startSession`'s `targetSecs`.
Floor loss: 0–59 s, expected ~30 s, every night, by
construction. So on the SLEEP span the face's T-0 lands up
to a minute BEFORE 03:30 — while ENDS, correctly, derives
from `windowEndMillis` (Overlay:526–527, A-12 C-1's cure,
landed). Inside the last minute the room therefore prints
two ends at once: hero counting to a 00:00 that arrives
early, sub-line still promising `ENDS 03:30`. Then one of
two feels, both wrong: the ramp completes and the face
stands at FULL standing alpha, hero 00:00, for up to ~59 s
— dawn's climax spent as a lit plateau, the peak no longer
coincident with the act — or (penalty/drift the other way)
the stand-down amputates the ramp mid-return. A-3's whole
claim is coincidence: information, legibility and the end
at one instant. On the span A-3 matters most, the merge
built the coincidence for manual spans and missed it for
the routine one.
CURE (exact): key the last minute for routine-born rows to
the same instant ENDS already reads. At the
`deskClockPrintAlphas` call site (Overlay:297–303), pass
`spanRemainingSeconds =` for `routineId != null &&
windowEndMillis > 0`: `((windowEndMillis − nowWallMillis)
/ 1000).toInt().coerceAtLeast(0)`, else the span value as
now. `deskClockEndsAtMillis` (Overlay:526) is the existing
owner of this derivation — read the remaining from it, do
not grow a second one. The hero's zero and the ramp's full
then land on 03:30 with the stand-down, because they are
the same number ENDS prints. SMALL (one call-site branch).
Proof: §5(b).

**DAE-2 (MEDIUM) — The ramp is assigned, not animated: in
the one watched minute of the night, the light moves in
per-second steps while every other light motion in the
room is frame-animated.**
The alphas are plain values — `Color.copy(alpha = …)` at
composition (Overlay, hero/sub/block/banner/exit seats) —
recomputed when the session row re-emits (≈1 s ticker
writes, ≈2 s shield beats: two unsynchronised drivers, so
the steps land unevenly, 0.003 then 0.006). Every other
light event in this room is an `Animatable` on a named
curve: entry print, exit print, surrender fade, the Landing
bloom in the host. The descent can afford assignment —
Nacht's A-N5 acquits it: it is an unseen honesty, a state
the face is FOUND in. The dawn cannot: the last minute of a
timed span is the one minute a finishing man is reliably
WATCHING (he is awake; the hero is counting seconds; that
is what the minute is for). Watched, a stepped 0.19 return
reads as a settings value being incremented; animated, the
same endpoints read as light arriving. The arithmetic is
dawn; the motion is a brightness setting.
CURE (exact): animate the three composed alphas in the
overlay — `animateFloatAsState(target, tween(520,
EasingC))` for hero / numberLine / exit, fed by
`deskClockPrintAlphas`' outputs exactly as now. The
per-second retarget chase (lag < one step, 0.003) is
invisible; the steps disappear into continuous motion.
This one change also carries Samt's D-3 cure's effect at
the suspension lift (her priced function change stands as
the alternative; the seats should land ONE of them, not
both — Wacht merges). SMALL. Proof: §5(b) frames at 250 ms
offsets inside one ramp second — alphas must differ
between them.

**An honest naming (not a defect): the dawn is a hero
event.** A-2's floors mean the number lines only ever fell
0.55 → 0.50; their dawn is a 10 % relative lift, a whisper.
"Information and legibility peak together" is true, but
the peak a man sees is the hero's +53 % relative return
and the rule's with it. Nobody should sell the small
print's ramp as more than it is — and nobody should
"deepen" the numbers' descent to give them a dawn; A-2's
floor is a legality (a number too dim to read is a number
withheld), not a staging choice.

---

## §2 · THE STAND-DOWN — is the silence complete?

### The census: every voice at the 03:30 seam, accounted

| Voice | State at stand-down | Ground |
|---|---|---|
| E0 settle knock | **SILENT** | ceremony launch skipped whole, VM:1521–1537 |
| E700 completion pulse | **SILENT** | same gate; also `finishPulses → false`, ReceiptState:120–124 |
| E880 / E970 knock pair | **SILENT** | inside the same skipped launch, VM:1533–1535 |
| Haptic halves of all three | **SILENT** | gate is on the CALLS — see below |
| Shield stop (`stopShield`) | silent | no sound calls in MonolithShieldService |
| Release-retry alarm (+30 s, faulted only) | silent | ReleaseRetryScheduler: CPU wake only, no voice, no surface |
| Routine heartbeat finalise/deactivate | silent | no sound calls in RoutineHeartbeat / finalise funnel |
| Break-boundary knock | cannot occur | SLEEP rows are single-block (Moench MA-1); no break exists inside the span |
| Deck held frame (detention) | mute | no `play*` anywhere in TactileDeckScreen |
| Receipt sheet | never composes | filed before any emission — §2 acquittals |
| Desk-clock chip latch (A-9) | deleted from the clock path | VM:1856ff, toggle is silent both ways |
| FASTED seal Throw ≈ 05:00 | sounds, unwitnessed | audit §4 E-5 ruled LEAVE IT; a different seam, named so it is never re-filed as a stand-down leak |

**Why the landed gate is the only lawful form — and it
landed.** `playSeatKnock` fires `triggerWaveform` FIRST
(TactileSoundEngine:665–676), and `triggerWaveform` is
gated by `hapticsEnabled` only (TSE:556–557), NOT by the
sound toggle. So `finishPulses = false` alone — the lane's
form — would have silenced only the E700 AUDIO and left
the E0 knock, the pair, and every haptic half buzzing
against a sleeping man's nightstand, in the dark, with
sound "off". The merge gates the CALLS: one
`ceremonySounds = kind != ROUTINE_SLEEP_STANDDOWN`
(VM:1521) wraps the entire ceremony launch, so no knock is
ever invoked and both halves die together. This is
A-10(i)'s parenthetical — "the gate sits on the CALLS" —
implemented as written. The machine does not applaud
sleep. Acquitted, and defended in the closing.

### Defects

**DAE-3 (HIGH) — The stand-down's trigger is an emission,
not the instant: on timer-wins nights no write is
guaranteed at or after 03:30, and the farewell drifts
while a fully dawned face stands lit.**
The finish controller judges finishes only on
`sessionHistory` emissions (VM:1415ff). The stand-down
kind is the one kind whose predicate is a function of
`now` on an unchanging row (`windowEndMillis in
1..nowWallMillis`, ReceiptState:94–96). Two writers can
deactivate the SLEEP row, and they race inside the floor
window DAE-1 opened: the progress ticker flips
`isActive = !allBlocksCompleted` at the floored span end
(MonolithRepository:2106 — up to ~59 s BEFORE the window),
while the heartbeat deactivates at the window's end
(Repository:1075–1090). Both writers guard on `isActive`;
the loser writes NOTHING. When the timer wins — the
expected case — the flip's own emission is judged too
early (window not yet reached, kind = null), and after it
there is no active session for any ticker to write: no
further sessions-table write is guaranteed at or after
03:30. The finalise funnel's SLEEP branch also writes only
`if (session.isActive)` (Repository, finaliseRoutineSession)
— already false. So E0 — the clock's hide, the veil, the
receipt's filing, the keep-awake clear — waits for an
unrelated write (the next routine's materialisation, a
morning interaction) and can arrive tens of minutes late.
Nothing sounds in the meantime; the silence holds. What
breaks is the LIGHT and the ACT: the room dawns, reaches
full standing alpha at its false T-0, and then simply
STAYS — lit, finished, unended — past the ending it was
built to perform. A farewell that depends on a later
accident is a shutdown postponed.
CURE (exact): give the one time-gated kind a clock. The
VM's per-second ticker already runs all night; on each
beat, re-run the controller's existing per-row check
against `sessionHistory.value`'s latest inactive
SLEEP-routine row (extract the check from
`startFinishController` into one function both call
sites share — no second predicate). `finishKindOf` is
pure and cheap; the stand-down then fires at windowEnd +
≤ 1 s on every night, whichever writer won. SMALL.
Proof: §5(a) must be run on the timer-wins race, not only
the clean one.

**DAE-4 (MEDIUM) — The veil is set for every routine-born
finish, witnessed or not, day or night: it stands at noon
and three hours stale with the same face it wears at
03:30.**
`onFinishE0` sets `_nightVeil = true` for
`isRoutineBornKind(kind)` (VM:1472–1475) BEFORE the
witnessed split, unconditionally. Three consequences.
(a) A routine STUDY span ending at noon veils the midday
Deck — E-1's grammar is the NIGHT return (DESIGN §10's
heading; Nacht's veil is priced for a dark bedroom), and
a black blanket at noon under full light reads as a
malfunction, not a courtesy. (b) A branch-U discovery at
07:12 — process born after bath and meditation, stand-
down 3 h 42 m cold — veils a morning Deck over an act
nobody is returning FROM; the veil means "the machine
just turned the light down for you", and worn stale it
is a tap he owes for nothing. (c) Filed receipts mean
that on these paths there is not even a slip beneath the
veil to justify a covered first surface.
CURE (exact): set the veil only when the return is a
return — `witnessed &&` the Deck's own night condition
(local hour in E-3's 00:00–05:00 window, or the VM's
existing night evaluation if Wacht prefers one
discriminator for veil + Landing, see DAE-5). One
condition at VM:1473. ZERO–SMALL.

---

## §3 · THE RETURN — what is the last thing the room does?

The act as merged, on the designed night (charger in,
clock standing, heartbeat wins — DAE-3's other nights are
named there):

> 03:29:0x–03:30:00 — the ramp brings the face to full
> standing alpha (DAE-1's keying aside). **E0**: the row
> deactivates; the ceremony is skipped whole (silence);
> the shield is commanded down, silently; the veil is
> armed UNDER the clock's ground (VM:1474 — set at E0,
> covered, so its own appearance is never an event). The
> dawned print fades 260 Curve A over held true black —
> the face dies at its brightest, which is the audit's
> ruled shape (A-4 / Nacht N-S3), not a sunset; acquitted:
> a slower death would fight the detention clock, and
> the slowness already happened — it was the ramp.
> **Detention (≤ 1,800 ms)**: the Deck's held frame
> freezes beneath the ground, mute, unseen. The receipt
> is claimed — a № is minted — and filed in the same act
> (Repository:1729); it is never emitted
> (VM:1598–1603), never rehydrated
> (Repository:1773–1777). **Ground lift** at +1,200 + 260:
> the black comes off a Deck already wearing the veil at
> 0.80 — the first uncovered surface of the morning is a
> dimmed question, not a lit desk. Keep-awake clears on
> the next ticker beat (the clock is no longer visible);
> the OS timeout then takes the veiled Deck dark on its
> own clock. **The last thing the ROOM does is cover the
> light. Everything after that is the OS, or his hand.**

Verdict: that IS a farewell — the room leaves the Deck
darker than it found it and asks nothing. On battery (face
already yielded before 03:30) it is better still: nothing
stands, nothing moves, the purest form of the act —
nothing happens, visibly, at all, and the record exists
in the morning. The defects below are the three seams
where the farewell can still turn into a shutdown or a
blanket.

### Defects

**DAE-5 (MEDIUM) — The Landing exists only behind the
veil: a night HAND dismissal still lands on the
full-light Deck.**
E-1's Landing is promised for the night dismissal as
well as the machine return (DESIGN §1.10, §10; audit §1:
"hand dismissal → Deck (night: E-1's Landing)"). As
merged, `_deckLandingDim` is set in exactly one place —
`unveilNightReturn` (VM:351–356). `toggleAlwaysOnDeskClock(false)`
(VM:1856ff) sets no dim. So the man who taps the clock
away at 02:00 — the tap Fen's charge was about: its
object is the exit, not the light — gets the 180 Throw
into a Deck at full light, the precise flash the whole
grammar exists to prevent, on the path he himself
drives. The machine return got manners; the hand return
did not.
CURE (exact): in `toggleAlwaysOnDeskClock(false)`, when
the dismissal is a night dismissal — a session live, or
the same night discriminator DAE-4 adopts (one
discriminator, both call sites) — set
`_deckLandingDim = true`. The host already blooms it
away on the VM's clear (MainActivity:267–273). SMALL.

**DAE-6 (MEDIUM) — The unveil flashes: veil and Landing
are two uncomposed scrims, and their sum goes UP in
light at the first touch before it comes down.**
The veil is a boolean box — black at 0.80, composed or
gone, no animation (MainActivity:809–817). The Landing
is a SEPARATE `animateFloatAsState` scrim, 0 → 0.45 over
520 Curve C (MainActivity:267–273, composed at :804–808).
At the unveil tap: veil 0.80 vanishes in one frame;
Landing dim starts at 0 and climbs to 0.45. For the first
~200–300 ms the Deck stands at near-full light — a
pulse of brightness at the first touch of the morning —
before settling to the Landing's level. The composed
grammar (DESIGN §10) is ONE descent of light:
0.80 → 0.45 → (first verb) → 0. As merged it is
0.80 → ~0 → 0.45 → 0: a blink inside the farewell.
CURE (exact): one scrim, one animated value, three
targets. Replace the two boxes with a single full-screen
scrim whose alpha is `animateFloatAsState` (tween 520,
EasingC) over a VM night-return phase — VEILED 0.80 /
LANDING 0.45 / OPEN 0f — with the veil's touch target
riding the same box while VEILED. Every transition of
the return then moves monotonically in light, which is
the whole grammar in one invariant: after the stand-
down, the light never goes up except at his verb.
SMALL–MEDIUM (host recomposition + a VM enum).

**DAE-7 (LOW) — The bloom fires on ANY interaction, not
on the first true verb.**
`noteUserInteraction` clears `_deckLandingDim`
unconditionally (VM:329–332), and the Activity calls it
from `onUserInteraction` — any touch-down anywhere: a
brush rolling over in bed, the tail of a scroll, an
accidental palm on the veiled morning Deck after
unveiling. §10's law is "the first TRUE VERB blooms it"
— navigation, an arm, a control with intent. As merged,
the Landing's protection lasts exactly until the first
contact of any kind, which in a bed is not a verb.
CURE (exact): stop clearing the dim in
`noteUserInteraction` (it keeps the interaction stamp
and the wake path); clear it in the verb handlers —
`navigateTo`, PARK's open, the arm/chip verbs — via one
`bloomNightLanding()` the VM exposes. SMALL.

**DAE-8 (LOW) — The finish ground releases on a fixed
1,200 ms delay, not on issuance: a 20–340 ms window of
uncovered, unpapered Deck inside the seam.**
EXIT, FINISH fades the ground with
`tween(260, delayMillis = 1200)` (Overlay:236–241) —
gone by +1,460 ms. Issuance lands at Detention's
formula, ∈ [1,480, 1,800] ms (ReceiptState, Detention).
A-4's pin is simultaneity: "the ground fades 260 Curve A
AS the receipt fades in (280 Curve C)". As merged the
ground leaves first and the slip arrives after — for a
faulted finish, 340 ms of lit Deck standing in a dark
room with nothing on it. At the STAND-DOWN the veil
covers this gap completely (acquitted for the kind —
the veil is already composed beneath); the defect bites
the manual finish, the awake man's ending, which is
also this slice's dawn audience. Named LOW because 340 ms
of a dim night-scheme Deck is a blink, not a flash —
but the pin was written in simultaneity and the merge
should keep it in simultaneity.
CURE (exact): release the ground from the issuance
event, not the clock: the VM raises a ground-release
fact when the finish issues (`emitReceipt`, and at
filed-issuance for routine kinds); the overlay animates
the ground out (260 Curve A) when it flips, retaining a
2,200 ms failsafe delay so a halted chain can never trap
the face under black. SMALL.

---

## §4 · ACQUITTALS (feel, this slice)

- **A-10(ii), born filed — verified at three doors.**
  Publish files the slip in the claim act itself
  (Repository:1727–1730, `ReceiptFiledStore` beside the
  pointer, commit()-durable, no schema change); emission
  refuses routine-born kinds AND any filed session
  (VM:1598–1603); rehydration refuses the filed session
  (Repository:1773–1777). The № exists, the record
  ("SLEPT THROUGH", Repository:1697) exists, the Drawer
  can hold it — filed is a posture, not a deletion. The
  slip's absence from the morning's first surface is
  total.
- **A-10(iii), dismissal writes through.** The host's
  `remember{}` dismissal is GONE from the receipt seat:
  MainActivity:843–854 routes dismissal to
  `viewModel.dismissReceipt` (VM:1652–1658), which writes
  the same persisted filed fact. Process death can no
  longer resurrect a slip he put away. (The host still
  carries a `dismissedReceiptSessionId` remember for the
  PARK/nav side-effects at MainActivity:310, 349 — it no
  longer gates the slip's standing; the filed fact does.
  Named so a future reader doesn't mistake the leftover
  for the mechanism.)
- **A-10(iv), the cliff dies for stored-end kinds.**
  `receiptCliffExempt` (ReceiptState:143–146) = the
  routine-born kinds; the controller files them at any
  age (VM:1435–1437); witnessed manual finishes keep
  their 10-minute cliff. Truthful filing, as ruled.
- **The receipt that DOES stand still arrives like
  paper**: ReceiptSheet fades in on `specC` — 280 Curve
  C (ReceiptSheet:60–64) — matching A-4's "receipt fades
  in (280 Curve C)" against the ground's 260 Curve A.
  For witnessed finishes the seam's last motion is the
  right one; DAE-8 is about its timing, not its shape.
- **Keep-awake's ownership genuinely moved** (VM flow →
  MainActivity:258–263 applies/clears the flag; the
  overlay touches no flag) — the face can no longer
  strand the screen on by dying wrong. The surrender's
  own defects are Samt's S-1/S-2/S-3; from this slice I
  record only that at the stand-down itself the flag is
  already clear or clears within one beat of the hide —
  the farewell is never prolonged by a stuck screen.
- **The battery stand-down is the best night the room
  has**: yielded dark before 03:30, E0 changes nothing
  visible at all — no print, no motion, no sound — and
  the morning finds a filed record and a veiled Deck.
  Nothing to cure; protect it from DAE-3's cure regressing
  it (the ticker re-check must not raise or light
  anything — it only judges).

## §5 · PROOF DEBTS THIS SLICE ADDS (to audit §7)

(a) **The stand-down record, on the timer-wins race**
(extends §7.7): force the floored-span deactivation to
win (materialise the SLEEP row ≥ 30 s into its window),
then record 03:29:30–03:31:00 — audio + haptic capture
silent throughout, E0 within 1 s of window end, clock
hidden, veil standing, Deck clean of any slip. The
audit's debt 7 as written can pass on the heartbeat-wins
night and prove nothing about the other one.
(b) **Dawn frames for a routine SLEEP span** (extends
§7.2/§7.8): frames at r = 60 / 30 / 1 s on the span
DAE-1's cure keys — sub-line `ENDS 03:30`, hero and ramp
arriving at the same 03:30, no frame in which the hero
reads 00:00 while ENDS still promises a future minute;
plus two frames 250 ms apart inside one ramp second
proving DAE-2's motion is continuous, not stepped.
(c) **The unveil, photographed** (E-1's debt, made
measurable): frames at tap +0 / +130 / +260 / +520 ms —
the Deck's luminance must never exceed the Landing
level (55 %) at any of them (DAE-6), and the same
sequence after a night hand-dismissal (DAE-5) must start
AT the Landing level, not at full light.

## §6 · PRICE SUMMARY

DAE-1 SMALL · DAE-2 SMALL · DAE-3 SMALL · DAE-4
ZERO–SMALL · DAE-5 SMALL · DAE-6 SMALL–MEDIUM · DAE-7
SMALL · DAE-8 SMALL. No DB change, no new surface, no
verb added anywhere; every cure is a keying, a trigger, a
condition, or one scrim where two stood. Cross-owned, not
priced here: Samt S-1 (scheduled surrender fade), S-2
(plug-edge in both branches), S-3 (yield composes black),
D-3 (suspension resume curve — DAE-2's animation absorbs
its effect if Wacht lands mine instead).

## CLOSING — THE ONE FEEL DECISION I DEFEND HARDEST

**On a routine span, dawn and the stand-down are ONE
instant — `windowEndMillis` — and every element of the
last minute keys to it: ENDS, the hero's zero, the ramp's
full, and E0 itself.** I will defend this against the
span-arithmetic reading even though the span reading is
internally consistent, because the man never meets the
span's arithmetic — he meets a face that promises 03:30
in its sub-line. A dawn that peaks at 03:29:20 under that
promise is not an approximation of the ruling; it is the
face spending its one climax on a minute it invented,
then standing at full light, finished and unended, while
the real ending happens later to a room that has nothing
left to give it. The audit's sentence was "information
and legibility peak together" — together WITH THE END
is the whole of it. Key the dawn to the stand-down's own
instant, and the end of the night is a single gesture:
the light comes all the way back exactly as the machine,
in silence, puts it out. That is a farewell. Anything
else is a brightness setting and a shutdown, sixty
seconds apart.

— Daemmer, Feel Specialist 5 (dawn + stand-down),
Wacht's cell, Oct 30, 2026. Inbox zero at my close; next
seat inherits DAE-1/DAE-3 as the pair that must land
together — a dawn keyed to an instant the trigger can
still miss is only half a cure.
