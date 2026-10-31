# ISSUE #13 — THE SESSION ROOM MERGE: INNOVATIONS

Funke (Innovator), Wacht's cell — Oct 30, 2026.
Proposals only. No code is written, implied as written, or priced
as if the builder had already agreed. Wacht rules on adoption;
Wecker prices what survives against the lane in his hands.

SCOPE: Klinge's 19 panel items + Wacht's audit items A-1…A-12
(`studio/plan/AUDIT-session-room.md` §3) merged onto current main
from the flow-clock lane (`ui/DeskClockSettle.kt`, which does not
exist in main yet, and the cured print derivations in
`AirlockAndDisarmOverlays.kt`).

BINDING RULINGS TAKEN AS FIXED (Ren, via the cell brief):
V1 amended (~50 Hz sampling ONLY while armed-undecided; 7–14 Hz
band energy; three-state REST / HELD / UNKNOWN; HELD suppresses
BOTH raise paths; the 20 s backstop alone for UNKNOWN and
sensor-absent) · E-1 veil + Landing ratified · E-4 confirmed
(§C-2 stands, the clause is taught not printed) · V6 struck ·
M-4 amended (keep-awake ownership moves to the VM's session
machinery; surrender deadline = max(settle, last interaction,
last charger disconnect) + 10 min still).

READING OF THE GROUND, in one line each, because every proposal
below hangs off one of them:

- The settle machine is already pure (`tick(...) : Boolean`, all
  inputs as arguments) — and already at its expressive ceiling:
  a Boolean cannot carry the cause A-4 prices, nor a sensor
  verdict E-2 now demands.
- `DeskClockFlowTest` in the lane is 8 settle cases + 6 print
  cases, all hand-written, all UNRUN in the lane by its own
  header. §7 debt 10 asks for a table; debts 1, 2 and 8 ask for
  frames that today exist only as prose promises.
- In main, `finishPulses` still returns `true` for
  `ROUTINE_SLEEP_STANDDOWN` (ReceiptState.kt) and receipt
  dismissal is still `remember{}` host state (MainActivity) —
  A-10's five cures land in at least four files unless something
  gathers them first.
- The light law (A-2 floors, A-2(iv) door invariant, A-3 dawn
  ramp) is currently a set of named constants in the overlay.
  Constants cannot be property-tested; a function can.

---

## P-1 · THE BENCH, REPLAYED — a trace-fixture harness for the amended V1

**Mechanism.** Split the amended V1 discriminator out of any
Android type, exactly as `DeskClockSettleMachine` already splits
the time law: a pure classifier `classify(samples, orientation,
touch, proximity) -> REST | HELD | UNKNOWN` fed sample-by-sample
with monotonic stamps, plus a pure band-energy function
(7–14 Hz bandpass + running variance) that is the ONLY place the
band arithmetic lives. Vega's bench traces — desk, stand, pocket,
and the four traces E-2 / debt 9 added by name (braced-two-hand,
one-hand, chest-rest, lap-rest) — are committed as fixtures
(compact CSV or binary sample dumps, recorded once on a real
device at the ruled ~50 Hz) beside the test, not described in
prose. The harness replays each fixture through the classifier
and the settle machine together and asserts the full verdict
TIMELINE: desk/stand reach REST and raise on the sensor path;
the four held traces never raise — not on the sensor path, and
not at 20 s, not at 20 min — while their signature persists;
pocket acquits on proximity; a no-sensor fixture takes the 20 s
backstop alone. Thresholds become constants at the top of the
classifier, tuned against the fixtures, never against a night.

**What it buys.** Debt 9 is discharged in CI instead of on
Sarthak's nightstand, and debt 6's watched-raise captures gain
a machine-checked companion for every bench case. More than the
debts: E-2 convicted the original V1 on a trace — the in-hand
reader seized in ~4–8 s. Without replay, the amended V1 is the
same epistemic shape as the convicted one (a threshold asserted,
a phone hoped). With replay, every future threshold change —
and the looks programme will eventually ask for one — is a diff
against seven named bodies, not a new act of faith. This is also
the only proposal that makes "tuned" a checkable word in debt 9's
own sentence ("BEFORE any V1 threshold is called tuned").

**Price: MEDIUM.** Justification: the classifier is small and
pure, but recording seven honest 50 Hz fixtures and building the
replay loop is real bench time, not typing time — it costs a
device session, not a refactor.

**Risk.** Fixture overfitting: seven traces from one device and
one hand can tune thresholds that fail on another grip. Mitigation
priced into the mechanism, not added later: assert verdicts with
a margin band (each held fixture must stay HELD under ±20 %
band-energy perturbation of its own samples; each rest fixture
must stay REST under the same) — the harness tests the margin,
not the point. Second risk: fixture files rot silently when the
classifier's input shape changes; the harness must fail on a
malformed fixture, never skip it.

**Ruling-conflict check.** None found. Sampling rate, band,
three-state verdict, HELD's double suppression and the backstop's
scope are Ren's amended V1 verbatim — the proposal changes only
WHERE the verdict is proven (a JVM replay instead of a phone).
Offline replay is not device sampling, so the "~50 Hz only while
armed-undecided" limit is untouched: no new sampling exists
anywhere in this proposal.

---

## P-2 · ONE TABLE, THREE DEBTS — the state × figure × expected-string generator

**Mechanism.** Debt 10's fixture table stops being a table
inside one test file and becomes the single declarative source
for the face's print law: one row per (state × figure) cell from
§1's state inventory — idle / running timed / multi-block /
break / last minute / stopwatch / SLEEP charger / SLEEP battery —
each row carrying its input figures (span remaining, elapsed,
block k of n, break seconds remaining, wall instant) and EVERY
expected string at once: hero, sub-line, block line, banner /
break line, exit line — including the expected ABSENCES (null
block line in a break per A-1(a); null banner at idle per A-8;
"NOW HH:MM" alone for stopwatch per A-6(ii); final block's
figureless "BLOCK n OF n" per A-1(c)). A tiny generator consumes
the table three ways: (1) parameterised cases in
`DeskClockFlowTest` asserting the derivations return exactly the
row's strings; (2) the §7 debt-8 census shot list — one named
capture task per row, generated, so Neu's assembled-census read
covers the same cells the unit tests cover, by construction;
(3) debts 1 and 2 fall out as rows, not favours: the
3601 / 3600 / 3599 hero-boundary rows and the 30 s-remaining
dual-line row (exit on top AND "ENDS HH:MM" alone below, A-6's
proof) are just rows whose expected strings differ by one second
of input.

**What it buys.** Three debts (10, 8, and the string half of
1 + 2) discharged by one artefact, and — the part the audit
doesn't demand — the three surfaces can never drift apart
again: today the unit tests, the census and the audit's §1
inventory are three separate transcriptions of the same law,
and the merge's whole history (A-6's double-ENDS near-miss was
a composition error, two correct transcriptions colliding) says
transcription is exactly where this room fails. A row added for
a future state forces all three consumers to notice it.

**Price: SMALL.** Justification: the derivations are already
pure internal functions with exactly these inputs (the lane's
test proves the call shapes); the work is transcription of §1 +
§3 into rows and a loop, not new machinery.

**Risk.** A row can encode the audit wrong and then prove the
wrong law green — the generator amplifies a transcription error
instead of catching it. Mitigation: rows cite their source item
in a comment column (A-1(c), A-6(ii), debt 1…) so Wacht's review
reads citations, not vibes; and the census read by Neu (debt 8)
remains a human read of real frames — the generator feeds it,
it does not replace it. That ordering matters and is stated
here so nobody "optimises" the human read away later.

**Ruling-conflict check.** None found. No string in any row may
contradict E-4 / §C-2 (the exit line is the standing constant,
no session-keeps-running clause — the generator must not be
used to smuggle it back as an "expected" variant), and no row
adds a verb or a surface: every cell is print the audit already
ruled. V6 stays struck; nothing here is a face.

---

## P-3 · HELD, PATIENTLY — duty-cycled probing with a set-down wake

**Mechanism.** The audit's own E-2 text prices the floor:
"duty-cycle the sampler 6 s in 30 s once HELD persists." This
proposal keeps that floor and adds the one thing it lacks — a
way back that is fast when the world changes and cheap while
it doesn't. Three parts, all inside the armed-undecided state:
(a) while HELD persists, probe windows run 6 s on / 24 s off at
the ruled ~50 Hz — the audit's numbers, unaltered; (b) during
each OFF window the sampler is not dead but demoted to a ~1 Hz
orientation-only watch (the cheapest lawful rate) whose ONLY
job is change detection: a set-down transient (an impact spike
followed by orientation settling to a new stable attitude) or
any touch / proximity change ends the off window immediately
and opens a full probe window at once; (c) the HELD verdict
LATCHES across off windows — an off window is ignorance, never
evidence: HELD can only be cleared by a sampled probe window
that actively classifies REST or UNKNOWN, never by elapsed
time, so no off window can leak the session into the 20 s
backstop. If probes keep returning HELD past ~5 minutes, the
off window stretches (24 s → 54 s → 114 s, capped) — a man
reading braced for an hour should not pay a 20 % sampler duty
for the whole hour — and any change-detection event collapses
the stretch back to the first tier instantly.

**What it buys.** The audit's flat 6-in-30 leaves two bills
unpaid: a five-hour held reading still burns a fifth of full
sampling all night, and — worse — the flat cycle's worst case
is a man who was HELD, sets the phone down, and waits up to
30 s in the dark for a raise the sensor path owed him in ~2 s
(the desk trace acquits fast; the duty cycle would re-convict
slowly). The set-down wake pays the second bill (typical
set-down → probe → REST → raise in one probe window), the
stretch pays the first. Both keep E-2's physical insight —
the hand shakes, furniture doesn't — as the only discriminator.

**Price: MEDIUM.** Justification: the windowing and latch are
small pure state (and P-1's harness replays them for free if
both land), but the set-down transient signature is a second
classifier judgement that needs its own bench traces — two or
three set-down recordings added to P-1's fixture set — before
anyone should trust it at 03:00.

**Risk.** The change detector is the risk: a false wake (a
nudge, a bed shifting) costs one 6 s probe — cheap and silent.
A MISSED set-down costs the full capped off window (~2 min at
the deepest tier) before the next probe — a slow raise, never
a false one; the failure direction is lawful (the room is late,
not wrong) but it must be named, because at the deepest tier it
feels like V1 not working. Mitigation: the stretch tiers apply
only while orientation stays within the held attitude's cone;
drift out of the cone is itself a change event. If Wacht judges
even that too clever, the proposal degrades gracefully to the
audit's flat 6-in-30 with parts (b) and (c) only — that reduced
form is still stronger than the floor, and is priced SMALL.

**Ruling-conflict check.** Checked line by line against the
amended V1, because this proposal lives inside it: sampling
above ~1 Hz happens ONLY while armed-undecided and only inside
probe windows (the 1 Hz off-watch is orientation bookkeeping,
not band sampling — if Ren reads "~50 Hz only while
armed-undecided" as "no sensor above idle rate outside probe
windows", part (b)'s watch must be ruled in explicitly or cut;
flagged here rather than assumed). HELD suppresses both raise
paths throughout — part (c)'s latch exists precisely so the
duty cycle cannot become a third, accidental path. The 20 s
backstop remains alone for UNKNOWN / sensor-absent: an off
window never manufactures an UNKNOWN. No conflict with E-1,
E-4, V6 or M-4 (the sampler's life ends at the raise, where
M-4's machinery begins).

---

## P-4 · THE STAND-DOWN AS ONE REDUCER — A-10 with fewer moving parts

**Mechanism.** A-10 as filed is five cures in four-plus files:
a gate on `finishPulses`' callers, a persisted filed-marker, a
persisted dismissal write, an age-cliff exception, and the
keep-awake move with its deadline arithmetic. Instead of five
edits that must agree, one pure reducer in the VM's session
machinery: `standDown(kind, events, clocks, now) -> Effects`,
where the inputs are the finish kind (from the existing
`finishKindOf`), the receipt's birth class (routine-born or
hand-witnessed), dismissal events, and the three clocks M-4
names (settle stamp, last-interaction stamp, last
charger-disconnect stamp), and the effects are a closed list —
Pulse | NoPulse, StandReceipt | FileReceipt, DismissedPersisted,
KeepAwakeUntil(mono), FadePrintAt(trueOff − 520 ms). Every A-10
clause becomes one branch of this function and nothing else
implements any part of it: `finishPulses` keeps its signature
but its stand-down answer comes from the reducer's table; the
filed-marker and the dismissal write become the SAME persisted
fact (one boolean, "this receipt does not stand", written by
birth for routine kinds and by the hand for the rest — which is
what A-10(iii) already rules in prose: "one mechanism, two
writers"); the age cliff consults the reducer's stored-end
test instead of re-deriving it. The composable's
`DisposableEffect` keeps NO part of the law: it renders the
flag the VM's machinery owns, per M-4.

**What it buys.** The stand-down is the one act in the merge
that can only be witnessed live at 03:30 by a sleeping man —
debt 7's cured-seam frame is a photograph of an absence, the
weakest proof in §7. As a pure reducer, the whole act becomes
JVM-testable tonight: feed it the 03:30 sequence (SLEEP row,
windowEnd reached, charger connected and again disconnected at
03:00) and assert NoPulse, FileReceipt, and a surrender
deadline of max(...) + 10 min — including A-10(v)'s own worked
example, the 03:00 unplug that must not kill the face at a
stale deadline, and the plug-back-in that cancels before the
deadline and does not resurrect after it. Five scattered edits
can each be right and compose wrong (the audit's §2 verdict on
the thresholds generally); one function cannot compose against
itself.

**Price: MEDIUM.** Justification: more than any single A-10
clause (each honestly SMALL as filed) because it asks Wecker
to gather before he edits — but less than the five clauses'
true composed price, which includes the integration debugging
the per-clause prices don't carry. Net claim: MEDIUM once,
instead of 4 × SMALL + one SMALL–MEDIUM + an unpriced seam.

**Risk.** Gathering is itself a move: code Wecker knows gets
re-homed, and a reducer with an effects list can tempt future
hands to route unrelated finish behaviour through it until it
becomes a god function. Mitigation: the effects list is closed
(the five above, named in the merge note) and the reducer owns
the stand-down kind and the keep-awake deadline ONLY — witnessed
manual finishes keep their existing paths untouched, as A-10
itself scopes them. Second risk: collapsing filed-marker +
dismissal into one persisted boolean changes a representation
A-10(ii) describes as "one persisted filed-marker beside the
standing pointer" — see the conflict check.

**Ruling-conflict check.** Two honest flags, no silent ones.
(1) A-10(ii)'s LETTER puts the marker beside the pointer; this
proposal stores one fact instead of two. The SPIRIT — routine-
born slips are born filed; rehydration and composition both
refuse them; dismissal of any receipt persists — is preserved
exactly, and A-10(iii) already rules the two writers share "the
same persisted non-standing fact", which is the proposal's
single boolean read literally. Still: representation differs
from the filed wording, so Wacht's nod is required; if refused,
the reducer stands unchanged with the marker kept as its
storage — the mechanism does not depend on the collapse.
(2) §3's price total says "no DB change": the persisted fact
must live where A-10 already puts it (the guard_settings
store the pointer already uses), NOT in a new Room column —
if the merge finds the pointer is DB-backed after all, this
proposal's price rises and Wacht must re-rule before it lands.
M-4 is implemented, not amended: ownership in the VM's session
machinery and the deadline formula are copied verbatim.

---

## P-5 · THE VERDICT CARRIES ITS CAUSE — `tick` returns why, not just whether

**Mechanism.** `DeskClockSettleMachine.tick` returns Boolean
today. A-4 then needs the raise's CAUSE (UNWATCHED / WATCHED /
HAND / SEEDED) at the composition, A-2(iii) needs the raise to
stamp the interaction clock and suspend the descent for 60 s,
and A-10(v) needs the battery-yield wake to re-arm V1 — three
consumers, each forced to re-derive from the same inputs
(lastInteractionMono, session start vs process birth, visible)
WHY the machine fired, with three chances to disagree with the
machine's own reasoning. Change the verdict type instead:
`tick(...) -> Verdict`, where Verdict is `None` or
`Raise(cause)` and cause is decided in the branch that already
knows it — the cold-open branch is SEEDED by construction; the
deadline branch compares now − lastInteractionMono against
A-4's 10 s witness line and answers UNWATCHED or WATCHED; a
visible-beat answers HAND (and still raises nothing — the cause
records the decision the machine already makes silently);
P-1's classifier, when present, adds SENSOR-REST as the cause
for the tremor path so A-4's watched table covers it explicitly
instead of by analogy. The VM's single flag write (§2.6's one
write, in the existing ticker) is unchanged — it now also
carries the cause beside the flag, which is exactly the "one
cause carried beside the flag" A-4 prices.

**What it buys.** A-4's composition stops being a second,
shadow settle machine living in the overlay's animation
choices. The cause table becomes unit-testable through the
existing settle tests (each current case gains one assertion:
the cause it already implies — the cold-open case expects
SEEDED, the settle case with a fresh hand stamp expects
WATCHED). And the seeded path's no-flash proof (A-4: frame one
at full lawful alpha, captured from process start) gets a
deterministic trigger to capture against instead of a race.

**Price: SMALL.** Justification: one sealed return type and
one extra field carried beside an existing flag write; the
branch structure of `tick` does not change — each branch just
names what it already knows. The lane's 8 settle tests change
mechanically.

**Risk.** Cause leakage upward: if the cause escapes the raise
choreography into print (a face that behaves observably
different per cause beyond its ruled motion/light), the room
gains a dialect Neu's cold read warned about — behaviour the
man must learn that nobody taught. Guard, stated for the merge
note: cause may select ONLY A-4's motion rows, A-2(iii)'s
descent suspension, and E-1's return grammar — never a string,
never an alpha outside the light law. Second, smaller risk:
HAND as a non-raising verdict could confuse a future reader
into writing the flag on it; the type must make Raise the only
flag-writing case, with HandDecision a distinct case name.

**Ruling-conflict check.** None found. §2.6's discipline (one
flag write, in the existing ticker, the machine holds no flag)
is preserved — the machine still holds no flag and writes
nothing; it answers a richer question, once. E-1's veil +
Landing consume the cause on the return side; nothing here
re-opens V6 (no face is proposed — the Landing's light law is
E-1's, already ratified) and nothing touches E-4. The 10 s
witness line is A-4's own constant, quoted, not re-derived.

---

## P-6 · THE LIGHT LAW AS A FUNCTION — A-2 + A-3, property-proven

**Mechanism.** Replace the overlay's scattered alpha constants
for the running face with one pure function:
`deskClockLight(spanFraction, secondsRemaining, secondsSinceRaise)
-> Alphas(hero, rule, smallLines, banner, exit)`, implementing
exactly A-2 and A-3 and nothing else: hero and rule descend
0.55 → 0.36 over span-elapsed fraction (A-2(ii)'s named
variable, as the function's first argument); every
number-carrying small line floors at 0.50; the exit line keeps
its excepted 0.40; any raise or re-raise suspends the descent
for 60 s from standing alphas, then resumes from the
span-progress value (A-2(iii)); in the final 60 s every
descended alpha lerps back to its standing constant, reaching
full exactly at T-0 (A-3). On top of the function, a property
test sweeps the input space (fraction 0→1 in fine steps ×
the ramp window × the suspension window) asserting the laws
as invariants, not examples: small-line alpha never below
0.50 while running; the door invariant in Mond's ratio form —
exit luminance ≥ 1.10 × hero-print luminance at EVERY point
(A-2(iv), the form his arithmetic actually proved); descent
monotonic in fraction outside suspension and ramp; ramp
continuous with the descent at 60 s remaining and exact at 0.

**What it buys.** A-2(iv) exists because two separately
acquitted constants (0.58 / 0.55) violated the invariant when
composed — the audit caught that one by hand, once. The looks
programme will re-skin this face repeatedly after the merge;
every re-skin that touches an alpha re-opens the same class of
bug, and no panel will be seated per re-skin. A property test
is the panel that stays. It also pre-discharges the numeric
half of debt 4 (the descended-state L05 frame then proves the
photographed legibility of numbers already proven lawful,
instead of discovering the law in a photograph) and gives
debt 5's exit-line photograph a predicted value to be checked
against.

**Price: SMALL.** Justification: the constants and their target
values are all already ruled and named in A-2 / A-3 — the work
is gathering them into one function and writing the sweep;
no value changes, no new behaviour.

**Risk.** A function this central invites the E-3 extension
(night-keyed descent for hand-armed spans) to sneak in as "just
another input". Resisted explicitly: E-3 is recommended by the
audit for the merge AFTER this one, and this function's inputs
deliberately exclude wall-clock night progress — adding that
input later is the extension's own priced diff, visible as
such. Second risk: property tests over floats invite tolerance
fiddling until green; the merge note should fix tolerances
(±0.005 alpha) before the first run, not after the first red.

**Ruling-conflict check.** None found. Every constant is quoted
from A-2 / A-3 (floors 0.50, exit exception 0.40, descent
0.55 → 0.36, ratio 1.10, 60 s suspension, 60 s ramp); the
standing alphas the ramp returns to are the panel's acquitted
constants (§6, Nox A-N6 / the standing-alpha acquittal). E-3 is
not implemented early. No green, no new hue, no skin: this is
arithmetic about the existing face, and V6's strike is
unaffected.

---

## CLOSING — THE SINGLE STRONGEST PROPOSAL

**P-1, the replayed bench.** Every other item in this merge is
provable by reading: strings can be asserted, alphas computed,
receipts inspected in a store. The amended V1 is the one
mechanism whose truth lives in a human hand at 50 Hz — and it
is the merge's only CONVICTED item (E-2: the original seized a
braced reader in ~4–8 s, faster than the seizure it existed to
cure). An unproven discriminator does not fail in CI; it fails
at 03:00, in the dark, on the one user, by taking his screen
while he reads — the exact harm the whole lane exists to
prevent, wearing the cure's name, which is Fen's sentence and
the audit's. P-1 moves that failure to a fixture file where it
costs an afternoon. It also compounds: P-3's duty cycle and
set-down wake are only safe to adopt because P-1 can replay
them, and P-5's SENSOR-REST cause is only meaningful if the
classifier behind it is proven. Build the bench first; the
rest of the merge's inventions stand on it.

— Funke, Innovator, Wacht's cell (issue #13), Oct 30, 2026.
No commit, no push, no post: Wacht carries this to the repo.
