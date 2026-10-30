# FEEL — THE LANDING: WHAT THE NIGHT RETURN FEELS LIKE AT THE DECK

RUHE, Feel Specialist 2, Wacht's cell — issue #13
(Sarthak-2007/Focus-app, branch `agent/issue-13`, clone
`builds/cells/issue-13`). Slice: THE LANDING — E-1 as ratified
(AUDIT-session-room.md §4, DESIGN-face-spec.md §1.10 + §10,
scope-amended by §5 / Nacht A-N3): a night dismissal lands on
the Deck at the room's light law (0.55 over true black); the
first true verb blooms it to full light over 520 Curve C;
every control live from the first touch. By day the hand
return stands whole (A-N3 acquitted; the amendment is night
only).

STATE OF THE GROUND. Judged against the MERGED tree as it
stands in this clone's working tree at my read — not the
lane, not MERGE-NOTES' claims. Files re-read fresh:
`ui/MonolithViewModel.kt` (cited VM), `MainActivity.kt`
(cited MA), `ui/screens/TactileDeckScreen.kt` (cited Deck),
`ui/components/AirlockAndDisarmOverlays.kt` (cited Overlay).
Cross-read: FEEL-daemmer.md — DAE-5 / DAE-6 / DAE-7 touch
this slice. I do NOT re-file them; §3 below judges each one
against the fresh code (all three stand), extends two, and
dissents in part from one cure. Samt's file does not touch
the Landing. No code touched by this seat; no compile run,
per orders.

## THE VERDICT IN ONE BREATH

The Landing, as merged, is a veil accessory, not a return
law. It exists at exactly one door — behind the veil, after
a routine SLEEP stand-down (VM:1573, VM:380–385) — and at no
other: the night HAND dismissal, the very dismissal E-1's
Landing sentence was written for, still lands on the
full-light Deck, because `toggleAlwaysOnDeskClock(false)`
writes no dim anywhere (VM:1955–1977). Where the Landing
does exist, its trigger is not the ratified one: the bloom
fires on ANY interaction — the code says so itself, twice,
as an honesty fix (VM:356–357; MA:266–272) — so a man who
only meant to check the time, steady the phone, or scroll
to read spends the Landing exactly as surely as a man who
arms a session. Its weight is a pun, not a law: 0.55 in the
room is an ink alpha over true black; 0.55 on the Deck is a
transmission over whatever scheme the Deck happens to be
wearing, and on the stand-down path that scheme is already
DAY (MA:304, MA:547) — the same scrim therefore lands him
at two different absolute lights depending on which door he
came through. What is right is real, and I acquit it in §4:
the scrim never eats a touch, the bloom's 520 Curve C is the
right gesture, 0.55 is the right weight for a *working*
surface once the scheme underneath is night, and a pure
glance-and-leave — unveil, read, touch nothing, let the OS
darken it — is honoured today, by accident of ordering, and
must be protected on purpose by every cure below. Five
defects; the Landing is one discriminator, one trigger
definition, and one scheme-hold away from being law.

---

## §1 · THE FEEL SPEC — THE LANDING, SETTLED

### 1.1 Weights (keep, with the §1.4 condition)

| Surface | Weight | Form in code (target) |
|---|---|---|
| Veil (machine return, before his touch) | black scrim α **0.80** — transmission 0.20 | one scrim, phase VEILED (R-2) |
| Landing (after unveil, or after a night hand dismissal) | black scrim α **0.45** — transmission **0.55** | one scrim, phase LANDING |
| Open Deck (after the first true verb) | α **0.00** — full light | one scrim, phase OPEN |

**0.55 is the right landing weight — for a Deck, not for a
vigil face.** The room's hero can descend to 0.36 because
the room asks one thing of a man (read one figure, tap
anywhere). The Deck asks work: 11–13 sp print, dial figures,
strip densities, task names. A-2's own legality governs here
by analogy — a number too dim to read is a number withheld:
at transmission 0.36 the Deck's small print falls under the
same floor the audit set for the room's number lines, and
the Landing becomes a second veil he must pay a verb to
escape before he can even *read*. At 0.70+ the Landing is a
shrug — a 30 % dimming does not change what the light does
to a dark-adapted eye, and the whole grammar exists for
that eye. 0.55 is also the right *bloom distance*: 0.45 of
light returning in one 520 ms gesture is a door opening;
0.64 returning (from a 0.36 landing) is a climb. KEEP 0.55.
The defect is not the number — it is what the number is
applied over (R-4, §1.4).

### 1.2 The trigger — what "the first true verb" means

A TRUE VERB is the first handler invocation that **commits
a change beyond the Deck's presentation**. Three classes:

- **CONTACT — never blooms.** Any touch-down, brush, palm,
  grip; the tail or the whole of a scroll/fling; a drag
  before its commit point (the arm slide before the latch,
  a press dragged off and released); focus entering a
  field; Back; any key; the veil-unveil tap itself; the
  tap that dismisses the clock itself. Contact is the hand
  arriving, not the hand deciding.
- **READING — never blooms.** Scrolling the Deck to read
  it; opening PARK to read its board; opening the digest
  to read it; dismissing a standing receipt is *not* here
  (it writes the filed fact, A-10(iii) — it is a verb);
  raising the DESK CLOCK chip to check the room and
  dismissing it again — a reading round-trip, and the
  second dismissal re-lands (§1.5).
- **VERB — blooms, exactly once, at commit.** Navigation
  to another destination (`navigateTo`); the arm latch
  (`armMonolithSession`); a disarm request; task and
  checklist commits (`completeTaskAndAdvance` /
  `toggleChecklistItem`, promote, defer, add-on-submit,
  reopen); preset / profile / blocks selection
  (`selectStudyPreset`, `selectAndApplyProfile`,
  `applySmartLoadout`, `setStudyBlocksCount`); the dial's
  first actual change (`setDialMinutes` already guards on
  change — bloom inside that guard); the intention field's
  first changed character (`setIntentionText` on a real
  diff, not on focus); `endSafeBreakEarly`; any PARK /
  Shield toggle or tier change reached from the Deck's
  residency; `dismissReceipt` (a persisted write, not a
  glance).

The bloom is **an effect of the verb, not a toll before
it**: the verb executes on its own frame, under light that
is already rising, and is never gated, delayed, or made to
wait for the 520 ms. The merged scrim already guarantees
the mechanical half of this (§4) — the cure must not
trade it away.

### 1.3 The curve — one scrim, three phases, monotonic light

One full-screen black scrim; one animated alpha; three
targets — VEILED 0.80 / LANDING 0.45 / OPEN 0.00 — every
transition `tween(520, EasingC)` (Curve C =
cubic-bezier(0.3, 0, 0.8, 0.15), MonolithCurves.kt:64),
with ONE exception:

- **Veil → Landing** (his unveil tap): 0.80 → 0.45 over
  520 Curve C. The light *rises* here, lawfully — he asked
  to see — but it rises monotonically and by his hand.
  This is the only lawful rise before a verb.
- **Landing → Open** (first true verb): 0.45 → 0.00 over
  520 Curve C. KEEP as merged (MA:273–278) — Curve C holds
  low, then delivers: the light arrives as the verb's
  world arrives, not before he has committed to it.
- **Hand dismissal → Landing**: NO animation into the dim.
  The scrim is at 0.45 on the first uncovered frame. The
  clock's 180 EasingThrow exit (Overlay:374; A-4) covers
  the Deck while the throw runs; an animated 0 → 0.45
  entry would leave the Deck near-full for the throw's
  first frames and then *deepen* — a pulse, then a settle,
  the DAE-6 disease in miniature, on the path he drives
  himself. Snap, covered by the throw, is invisible; the
  dimming he never sees happen is the dimming that feels
  like the room's law rather than an effect.
- **Expiry → Open** (R-5): instant clear, no bloom. A
  stale Landing lifting at noon is housekeeping, not a
  gesture, and must not be choreographed as one.

**The Landing's one invariant** (Daemmer's, endorsed and
extended to every path): after a night return begins, the
Deck's light never goes up except (a) monotonically at his
unveil tap, to the Landing level and no higher, or (b) at
his first true verb.

### 1.4 The condition — the Landing is a night state

While VEILED or LANDING is armed, the Deck composes in
its **night scheme**, whatever the theme evaluation says.
0.55 is a weight only against a known ground: over the
night scheme it rhymes with the room's law; over the day
scheme (the stand-down path's actual ground, R-4) it is a
brighter surface than anything the room ever showed him,
wearing the room's number. The Landing does not re-skin
the Deck — it holds the scheme the return began in, until
the bloom ends the return.

### 1.5 The discriminator — what counts as "night"

One discriminator, the house's own, already computed:
`themeEvaluation.isNight` (VM:70–98) — a session is
active, **or** an enabled SLEEP window stands open. It
covers the cases that matter and refuses the ones that
don't: a 22:00 dismissal mid-session in a dark room is
night (session active); a 02:00 idle dismissal inside the
SLEEP window is night; a 04:30 idle dismissal after the
stand-down is *not* — he is up for the day (bath,
meditation), and A-N3's day acquittal governs it. A local
hour window (00:00–05:00, E-3's) is the worse law: it
misses the 22:00 in-session dismissal, the exact dark-room
case Fen's charge was about. Plus one stickiness rule: a
**night-return episode** opened by the veil or by a night
hand dismissal stays open — for re-landing purposes —
until the first true verb blooms it or R-5's expiry ends
it. Inside an open episode, a chip raise + dismissal
re-lands even if the evaluation has since flipped (the
03:36 re-dismissal, five minutes after the stand-down,
when the SLEEP window has closed and the evaluation
already says DAY: it is still the same dark return, and
the light law does not change mid-episode).

---

## §2 · THE FOUR QUESTIONS, ANSWERED FROM THE CODE

**Q1 — Any interaction, or the first committed action?**
Any interaction. `noteUserInteraction()` clears the dim
unconditionally (VM:358–361), and the Activity calls it
from `onUserInteraction()` — every key, touch, or
trackball event dispatched to the activity (MA:79–82).
The code knows: *"the hook cannot distinguish a true verb
from any other touch"* (VM:356–357); *"this code does not
claim it can"* (MA:266–272). E-1 as ratified — *the first
true verb blooms it* — is therefore **not implemented**;
what is implemented is *the first contact after the
unveil blooms it*. Since the unveil tap itself stamps
before it sets the dim (the DOWN stamps
`noteUserInteraction` while the dim is still false; the
click on UP then calls `unveilNightReturn`), the Landing
survives its own unveiling — and dies at the next contact
of any kind, verb or not.

**Q2 — Does the bloom ever fire under a man who only
meant to check the time?** Yes — under almost every
embodiment of that intent except the perfectly still one.
He unveils, reads the Landing, and then: steadies the
phone with a second hand (contact — bloom); scrolls to
find the strip's time cell (Deck:353–390 — a touch-down —
bloom); taps the DESK CLOCK chip to re-check the room,
reads it, dismisses it (the chip tap bloomed the Landing
before the clock even rose — and the dismissal lands him
on a now-full Deck for his walk back); presses Back in
the dark (a key event — bloom). The one intent the code
honours is motionless reading: unveil, look, touch
nothing, let the OS timeout take the veiled-then-landed
Deck dark (keep-awake is already cleared on this path).
That case passes today and is the feel the whole grammar
promises — a glance that asks nothing and spends nothing.
A trigger that spends the Landing on a steadying hand has
the economics exactly backwards: it taxes looking and
subsidises acting, at the hour the app exists to protect
looking.

**Q3 — Is 0.55 the right landing weight?** Yes as a
transmission, no as a claim. See §1.1 for the defence and
R-4 for the scheme defect: as merged, "0.55 over true
black" (VM:325–329's comment) is true of nothing — the
scrim is 0.45 black over the Deck's *current scheme*
(MA:813–819), and the Deck's scheme at the stand-down is
day. The number stays; the ground under it must be held
to night (§1.4) before the number means what the ruling
says.

**Q4 — Does the Landing exist for a night HAND
dismissal, or only behind the veil?** Only behind the
veil. `_deckLandingDim` has exactly one writer of `true`
in the tree: `unveilNightReturn` (VM:380–385). The hand
dismissal path — Overlay:374's tap → MA:805 →
`toggleAlwaysOnDeskClock(false)` (VM:1955–1977) — writes
the visibility flag and the settle machine's hand
decision, and nothing else. The other hand paths that
hide the face (`confirmDisarmSession`,
`completeEmergencyBreakFromValve` — session-ending acts,
VM:2683 / VM:2710 in this read) likewise write no dim;
I charge the clock dismissal only, and name the
session-ending paths as Wacht's call in R-1's note.
DESIGN §1.10 already rules this case ("hand dismissal…
by night: the LANDING"), and MERGE-NOTES' "E-1 MERGED
(veil + Landing)" overclaims it: what merged is
veil-*gated* Landing. A-N3 stands scope-amended on paper
and unamended in code.

---

## §3 · DAEMMER'S THREE — JUDGED, NOT RE-FILED

**DAE-5 (night hand dismissal lands full-light) —
UPHELD, still live in the fresh read.** Verified above
(Q4): the toggle writes no dim. Daemmer's cure (set the
dim in `toggleAlwaysOnDeskClock(false)` under a night
condition) is right in shape; R-1 below settles the
condition he left open ("a session live, or the same
night discriminator DAE-4 adopts") — it is
`themeEvaluation.isNight` OR an open night-return
episode, one discriminator shared with the veil's
remaining question, and adds the snap-entry rule (§1.3)
his cure does not mention: without it, the cured path
imports DAE-6's pulse onto the hand path.

**DAE-6 (the unveil flashes) — UPHELD, and extended.**
His sequence is exact (veil boolean box MA:820–827 dies
in a frame; Landing `animateFloatAsState` MA:273–278
starts at 0 and climbs to 0.45): composed light goes
0.20 → ~1.00 → 0.55 at the first touch of the morning.
Extension, from MA:304 + MA:547: on the stand-down path
the uncovered Deck during that flash is in its **day
scheme** — `darkTheme` is already false once the SLEEP
window has closed and no session is active, and the Deck
receives `isDarkTheme = darkTheme` raw. The flash is
therefore not "near-full light" (Daemmer's words) but
the brightest surface the app can show, at 03:30, in a
dark bedroom, on every unveil, guaranteed. His cure
(one scrim, three targets) is the right cure; R-2
adopts it whole and adds §1.4's scheme-hold, without
which the cured scrim still composes its phases over a
day-lit Deck and the Landing phase itself stands at a
day-scheme 0.55 — too bright even with the flash gone.

**DAE-7 (bloom on any interaction) — UPHELD in charge,
DISSENTED from in part of the cure.** The charge is Q1
above, verbatim true. But his verb list —
"`navigateTo`, PARK's open, the arm/chip verbs" — is
under-inclusive in one direction and over-inclusive in
another. Under: if only those clear the dim, a man can
run a whole Deck working session in the dim — change
preset, set blocks, complete and defer tasks, pick a
profile, type an intention — and never bloom, because he
never navigated; the Landing stops being a threshold and
becomes a tint he works under indefinitely, which no
reading of E-1 supports. Over: PARK's *open* is a reading
act (its board is a readout; §1.2 puts its toggles in
VERB and its opening in READING), and "the chip verbs"
includes the DESK CLOCK chip, whose raise-to-read is the
glance-and-leave round-trip (§2, Q2) — blooming on it
spends the Landing on the exact intent E-1 protects.
R-3 replaces his list with §1.2's commit taxonomy. The
seats should land ONE trigger definition; mine is filed
as the Landing slice's, his as the dawn slice's — Wacht
merges, and I state plainly that on the taxonomy, not
the charge, we differ.

---

## §4 · ACQUITTALS (feel, this slice)

- **Every control is live from the first touch —
  TRUE, and by construction.** The Landing scrim is a
  plain `Box` with a background and *no* pointer input
  (MA:813–819): it draws over the Deck and intercepts
  nothing, so a verb's touch passes through it, commits
  on its normal frame, and the bloom rises around the
  act. There is no 520 ms lockout anywhere in this path.
  This is E-1's third clause kept exactly, and it binds
  every cure: the single scrim of R-2 must likewise
  carry no touch target except while VEILED (the veil's
  own tap), and no cure may gate a verb on the bloom.
- **The bloom's gesture — 520 ms, Curve C (MA:273–278).**
  The duration is the room's own (the descent's and the
  surrender's curve, DESIGN §3/§9): the Deck comes back
  in the room's handwriting, not in a UI default. Keep.
- **The veil's weight, 0.80 (MA:820–827).** Before his
  touch the machine owes him near-nothing of the Deck;
  0.20 transmission is a covered desk, not a dimmed one,
  and the step 0.80 → 0.45 at his tap is a real unveil,
  not a fade between two dimnesses. Keep — as a phase of
  the one scrim (R-2), not as a box.
- **Glance-and-leave, motionless form.** Unveil → read →
  no contact → OS timeout: no bloom, no ask, the Landing
  simply stands until the screen sleeps. Acquitted as
  the grammar's proof-of-intent — and every cure in §5
  is written to keep this case passing.
- **No lockNow.** The absence stands (VM:325–329's note;
  Ren's lean): the Landing never converts a return into
  an unlock he does not owe. Nothing in this slice
  re-opens it.

---

## §5 · DEFECTS — file:line, exact cures

**R-1 (HIGH) — The Landing does not exist for the night
hand dismissal.** Evidence: Q4 / DAE-5 above —
`toggleAlwaysOnDeskClock(false)` (VM:1955–1977) writes
no dim; `unveilNightReturn` (VM:380–385) is the dim's
only `true` writer. The dismissal he performs himself,
in the dark, mid-vigil — tap the room away at 02:00 to
check the Deck — is the return E-1 was filed from (Fen:
the tap's object is the exit, not the light), and it is
the one return with no manners.
CURE (exact): in `toggleAlwaysOnDeskClock(false)`, after
the visibility write: `if (themeEvaluation.value.isNight
|| nightReturnEpisodeOpen) viewModelScope-free set
_nightReturnEpisode = true; _deckLandingDim = true`.
`themeEvaluation` is `Eagerly`-started (VM:687–702), so
the read is live at the tap. Host side: this arming must
SNAP the scrim (§1.3) — see R-2's Animatable form, which
carries a `snap` entry for this writer. SMALL.
Note for Wacht, not charged: the session-ending hand
paths (VM:2683, VM:2710) end on the same Deck at night;
extending the same line to them is one condition each,
but their return carries a receipt/finish grammar this
slice does not own.

**R-2 (MEDIUM) — The return's light is non-monotonic:
two scrims, one guaranteed flash (DAE-6, adopted and
extended in §3).** Evidence: MA:813–819 (Landing box,
animated 0 → 0.45) composed beside MA:820–827 (veil box,
boolean 0.80); VM:380–385 flips both in one call.
CURE (exact, Daemmer's, restated as motions): delete
both boxes; one `Box` whose alpha is an
`Animatable`-driven value over a VM night-return phase —
VEILED 0.80 / LANDING 0.45 / OPEN 0f: unveil animates
0.80 → 0.45 (`tween(520, EasingC)`), verb animates
0.45 → 0f (same spec), hand-arming (R-1) `snapTo(0.45f)`
under the throw, expiry (R-5) `snapTo(0f)`. The touch
target rides the box only in VEILED. SMALL–MEDIUM
(host recomposition + a VM enum — Daemmer's price,
kept).

**R-3 (MEDIUM) — The bloom's trigger is contact, not
commit (DAE-7's charge, this slice's taxonomy).**
Evidence: VM:358–361 (unconditional clear), MA:79–82
(the app-wide hook), and the code's own admissions
(VM:356–357; MA:266–272).
CURE (exact): (1) remove the clear from
`noteUserInteraction` — the stamp and the wake re-raise
in that function are untouched; (2) add
`fun bloomNightLanding()` to the VM — clears
`_deckLandingDim` and closes the night-return episode —
and call it at the head of the §1.2 VERB handlers:
`navigateTo`, `armMonolithSession`,
`requestDisarmChallenge`, `dismissReceipt`,
`completeTaskAndAdvance`, `toggleChecklistItem`,
`promoteTaskToActive`, `deferTask`,
`selectStudyPreset`, `selectAndApplyProfile`,
`applySmartLoadout`, `setStudyBlocksCount`,
`endSafeBreakEarly`, and inside the changed-guards of
`setDialMinutes` and `setIntentionText`. Explicitly NOT
in: `toggleAlwaysOnDeskClock` (either way),
`openPark`/`closePark`, `toggleNotificationDigestSheet`
(open), any scroll/focus path. One function, ~15
one-line call sites, no handler's own behaviour changed.
SMALL. Proof: §6(c).

**R-4 (MEDIUM) — The Landing's weight is
scheme-dependent: at the stand-down it is 0.55 over the
DAY Deck.** Evidence: `effectiveNight = darkTheme ||
sealed` (MA:304) is already false at the return (no
session, SLEEP window closed — VM:70–98's evaluation);
the Deck takes `isDarkTheme = darkTheme` (MA:547); the
scrim multiplies whatever results (MA:813–819). The
in-session landing (night scheme) and the stand-down
landing (day scheme) differ by the whole scheme gap
while claiming one law — and DAE-6's flash inherits the
worse of the two.
CURE (exact): while the night return is armed, hold
night: pass `isDarkTheme = darkTheme || nightVeil ||
deckLandingDim` to `TactileDeckScreen` (MA:547) and
OR the same condition into `effectiveNight` (MA:304)
for the header/PARK/Shield neighbours that compose
during the return. One condition, two call sites; the
bloom (dim → false) hands the scheme back to the
evaluation on the same frame the light completes its
rise. SMALL.

**R-5 (LOW) — The Landing has no expiry: armed in the
dark, it stands at noon; and it lives only in process
memory.** Evidence: `_nightVeil` / `_deckLandingDim`
are bare `MutableStateFlow(false)` (VM:330–331,
VM:343–344) with no timestamp, no resume re-check, no
persistence. Two feels follow. (a) In-process: unveil at
03:35, glance, leave by Home (no dispatched touch — no
bloom), return at noon — the Deck stands at 0.55 under a
dim nobody armed this side of breakfast, and his first
touch performs a 520 ms "bloom" in full daylight:
housekeeping wearing a gesture's clothes. (b)
Cross-process: a process death overnight erases the
veil; the morning return then depends entirely on the
finish controller's branch-U re-derivation re-arming it
on the cold open — a race against the first composed
frames that nothing in this merge stages or tests.
CURE (exact): stamp the arming (`landingArmedWallMillis`
beside the flows); in MainActivity's existing ON_RESUME
observer (the PARK sweep's), call
`viewModel.expireNightReturnIfStale()`: if armed and NOT
(session active OR SLEEP window open OR armed < 30 min
ago), clear veil + dim instantly (R-2's snap). The
30 min grace is the morning's own scale — bath and
meditation sit inside it; a return older than that is a
new visit, not a return. For (b): no persistence in
this cure — instead, the proof debt in §6(d) is owed:
if the cold-open race shows uncovered frames, the veil
must compose from the first frame (a persisted
one-bit marker beside ReceiptFiledStore is the priced
fallback, named, not built). ZERO–SMALL for (a).

**(Not a defect — a boundary, named.)** The battery-yield
wake (VM:1297's hide; `noteUserInteraction`'s re-raise)
never passes the Deck, so no Landing question arises
there; and a dismissal of an *idle, day* clock is A-N3's
acquitted whole return — R-1's condition leaves it
untouched by construction.

---

## §6 · EDGE CASES — THE SLICE'S OWN CHECKLIST, RULED

- **Glance-and-leave (unveil, read, no contact):**
  honoured today (§4); honoured under the cures — no
  verb, no bloom, OS timeout darkens the landed Deck.
  The Landing does not decay while he is still in the
  episode (no idle timer against a reading man).
- **Glance-and-leave (with a steadying touch, a scroll,
  or a chip round-trip):** broken today (R-3); honoured
  under §1.2 — contact and reading spend nothing; the
  chip dismissal re-lands under the episode rule (§1.5).
- **Immediate verb (unveil → arm tap in one motion):**
  the verb's DOWN is contact (no bloom), its latch is
  the verb (bloom at commit, §1.2); the control was live
  through the dim at every frame (§4). He never waits
  for light to be allowed to act — the light follows
  the act. Under today's code the same case *appears*
  to work; it works there because the DOWN spent the
  bloom early, which is also why the aborted verb below
  fails there.
- **Aborted verb (slide dragged back, press released
  outside, dial touched but not turned):** no commit,
  no bloom (R-3); the Landing stands for the next,
  realer attempt. Today: spent at the first DOWN.
- **Second dismissal the same night:** (i) after a
  bloom — a fresh episode is judged fresh:
  `isNight` at the second dismissal governs (a session
  still active re-arms; R-1). (ii) *Inside* an unbloomed
  episode — chip raise, read, dismiss — re-lands by the
  episode rule even across the evaluation's flip
  (§1.5's 03:36 case). (iii) After R-5 expiry — it is
  morning; the dismissal is a day dismissal, A-N3 whole.
- **Veil never touched:** the OS timeout takes the
  veiled Deck dark (keep-awake cleared); the veil state
  outlives the darkness and greets his actual first
  touch whenever it comes, subject to R-5's staleness
  check on resume.
- **Back under the veil / under the Landing:** Back is
  contact — no bloom, no unveil. If it leaves the app,
  R-5 judges the return; the return itself never opens
  at full light *inside* a live episode.

## §7 · PROOF DEBTS THIS SLICE ADDS (to audit §7)

(a) **The hand-dismissal frame, at night** (R-1's debt;
extends Daemmer §5(c)): SLEEP span standing, tap to
dismiss, frames at throw +0 / +90 / +180 ms — the Deck
uncovered at exactly transmission 0.55, night scheme,
no frame brighter. The same sequence by day: full
light, no scrim, A-N3 whole.
(b) **The commit taxonomy, as a test table** (R-3):
there is today *zero* test coverage of this grammar —
no test file in `app/src/test/` names the veil, the
Landing, the dim, or the unveil (grep-verified at my
read). Owed: a VM-level table — contact cases (scroll
stamp, chip raise, PARK open) leave `deckLandingDim`
true; verb cases (arm, navigate, preset, dial change,
intention diff) clear it; the unveil stamp does not
self-clear.
(c) **The glance that spends nothing** (extends
Daemmer §5(c)): unveil, then a scroll and a chip
round-trip, frames after each — transmission still
0.55 throughout; then a verb — 0.55 → 0 over 520
Curve C, monotonic at tap +0 / +130 / +260 / +520 ms.
(d) **The cold morning open** (R-5(b)): process killed
after the stand-down, cold start — count the uncovered
full-light Deck frames before the veil composes; the
debt is discharged only at zero, or the persisted
marker fallback is priced to Wacht with the number.

## §8 · PRICE SUMMARY

R-1 SMALL · R-2 SMALL–MEDIUM · R-3 SMALL · R-4 SMALL ·
R-5 ZERO–SMALL. No DB change, no new surface, no new
verb on any face; every cure is a condition at an
existing writer, one scrim where two stood, one scheme
OR, or a clear moved from a hook to the handlers that
already commit the app. Judged against Daemmer: DAE-5
upheld (cure settled in R-1), DAE-6 upheld + extended
(cure adopted in R-2, scheme half in R-4), DAE-7 upheld
in charge, cure replaced in R-3 (§3's dissent stands
for Wacht's merge).

## CLOSING — THE ONE FEEL DECISION I DEFEND HARDEST

**The bloom belongs to the verb, never to the touch.**
I will defend this against the implementation's honest
shortcut — *the hook cannot distinguish* — even though
the shortcut is sincerely labelled, because the
distinction is not a technicality in E-1; it is E-1.
The Landing exists for a man in the dark whose intent
has not yet become an act: checking the time, reading
the strip, steadying the phone, raising the room to
look at it and putting it back. Every one of those is
contact or reading, and under the merged trigger every
one of them is taxed at the full price of a commitment
— the light he was being protected from, delivered by
his own exploring hand, before he has decided anything.
A return grammar that cannot tell looking from doing
will teach him, within a week of nights, that the dim
surface is a trap that springs at a touch — and he will
stop trusting the dim, then stop unveiling gently, then
stop noticing the grammar at all. Move the clear to the
commit, and the Landing becomes what it was ruled to
be: a room at half light that waits — through his
glance, his scroll, his second thoughts — and comes up
only when he does something. The light follows the act.
That order is the whole feel of the night return, and
it is worth fifteen one-line call sites.

— Ruhe, Feel Specialist 2 (the Landing), Wacht's cell,
Oct 30, 2026. Next seat inherits R-1 + R-3 as the pair
that must land together: a Landing that exists for the
hand but still blooms at a touch is only half a return.
