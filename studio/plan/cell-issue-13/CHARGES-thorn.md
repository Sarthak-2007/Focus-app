# ISSUE #13 — SESSION ROOM MERGE: THORN'S CHARGES
Thorn (brutal critic, #13) — Oct 30, 2026. Scope unrestricted.
Every charge carries file/line evidence or it does not file.
Tree audited: branch agent/issue-13 @ 3fadf72 plus the live
uncommitted overlay/test edits in the cell tree, read Oct 30.
Salz's C-1…C-26 (CHARGES.md) are NOT re-filed; where wave-2
claims a cure, I verified it in code — acquittals at the end.
These charges are against what stands NOW.

## T-1 · CRITICAL — HELD's duty cycle cancels itself in the integrated system. ★ WORST CHARGE.
The ruling (V1 amended): once HELD persists, the sampler
duty-cycles 6 s in every 30 s. In the integrated system that
never happens — the off-window lasts exactly one ticker beat.
Chain, all in tree:
1. DeskClockSettle.kt:110-120 — sensorSamplingActive returns
   false inside the duty off-window (cyclePos ≥ 6 s).
2. MonolithViewModel.kt:467-475 — updateTremorSampler reacts
   to shouldSample=false by UNREGISTERING the listener and
   resetting tremorVerdict = UNKNOWN, tremorSeeded = false,
   tremorBandEnergy = 0.
3. DeskClockSettle.kt:177-183 — tick() receiving UNKNOWN for
   a session it held as HELD CLEARS heldSessionId/
   heldSinceMono and re-arms the session fresh; the backstop
   branch then re-stamps armStartMono = now.
4. Next beat: sensorSamplingActive sees heldSessionId != id
   and returns TRUE (DeskClockSettle.kt:112) — the sampler
   re-registers, rebuilds its estimate within the second
   (50 events against a 0.95-EMA), and the next HELD tick
   restamps heldSinceMono from zero.
Net effect: the 30 s duty clock can never accumulate; the
sampler runs ~continuously all night at SENSOR_DELAY_GAME
while the design's power story (and MIP-07's own duty table,
studio/max-impl/issue-13-session-room-max.md §3) prices it
at 20 %. Worse, every ~32 s the estimator is torn down and
re-seeded, and a fresh 20 s backstop countdown is armed and
cancelled — the machinery churns exactly where it claims
to rest.
The proof illusion: DeskClockFlowTest
heldDutyCyclesTheSampler_sixSecondsInThirty tests
sensorSamplingActive in isolation, feeding HELD by hand on
every tick. The feedback loop that kills the cycle —
unregister → UNKNOWN → held-clear → re-register — exists
only between the VM and the machine, and no test spans it.
Cure: the duty state must live where the verdict cannot be
reset by the gate it controls — e.g. the VM keeps the last
verdict through a duty-off window instead of resetting to
UNKNOWN, or the machine, not the sampler's registration,
owns the off-window (verdict HELD latched until a duty-on
window re-reads it). Add the integrated scenario to the
test: HELD at T, assert listener state and heldSince at
T+35 s and T+65 s.

## T-2 · HIGH — the surrender yields EVERY span, not the SLEEP span the ruling scopes it to.
Ren's ruling, as carried in MERGE-PLAN and the audit §1
inventory: the yield state exists under "SLEEP, BATTERY"
alone — M-4 as amended is a SLEEP span on battery yielding
after settle + 10 min still. The code:
MonolithViewModel.kt:1270 — the surrender branch's only
gates are `_isAlwaysOnDeskClockVisible && clockSession !=
null` and !plugged. No isSleepLoadout check, no timed
check. Wave-2 gated the DESCENT to the SLEEP span (Samt
D-1, VM's deskClockDescentApplies) and left its twin
ungated.
Consequences, by derivation: a hand-armed 3-hour study span
on battery, phone set down (REST raise, settle stamp T) —
the face yields at T+10 min and prints nothing for the
remaining hours of the vigil it exists to display. A
stopwatch (no span end at all) yields the same way. A clock
raised BY HAND on battery (toggleAlwaysOnDeskClock sets
deskClockSettleStampMono = the hand stamp) is killed by
the machine 10 minutes after the hand's own decision —
settle law 2 ("a hand decision decides the session") has
an unruled exception written in the ticker.
Cure: gate the yield on the same isSleepLoadout derivation
the descent now uses, or take the widening to Ren as a
ruling — what cannot stand is the silent scope difference
between the ruling's SLEEP and the code's EVERYTHING.

## T-3 · HIGH — the "7–14 Hz band" is a ~3.5–7 Hz band at the rate actually registered; V1-as-amended is not in the tree.
MonolithViewModel.kt:447-448 — the bandpass is two
one-poles with per-sample coefficients 0.36 (slow) and
0.58 (fast); event.timestamp is never read anywhere in
the listener (VM:414-455), so the poles are fixed per
SAMPLE, not per second. At the registered rate
(SENSOR_DELAY_GAME, ~50 Hz, VM:464-465), a one-pole's
cutoff is −ln(1−α)/(2π·dt): slow ≈ 3.55 Hz, fast ≈
6.9 Hz. The difference signal therefore peaks in
~3.5–7 Hz — voluntary sway and breathing's country —
while physiological tremor's core (8–12 Hz) is
attenuated by BOTH poles. The coefficients would be
right at ~100 Hz, a rate nobody registered. The code
comment (VM:~430-437) claims "below ~7 Hz / below
~14 Hz" — false at its own registration.
Corroboration from inside the house: MIP-07
(studio/max-impl/issue-13-session-room-max.md:98-99)
convicts the same numbers: "The shipped '7–14 Hz band'
is a ~3.5–7 Hz band: it sits on voluntary sway and
breathing." A spec proposing the fix is not the fix;
the merged code still ships the convicted estimator,
and its verdict still rests on TREMOR_FLOOR_UNCALIBRATED
(DeskClockSettle.kt, "no bench has tuned it") — an
uncalibrated floor under a mis-centred band. Fen's
amendment was ratified on physical evidence; what
shipped is a sway discriminator wearing its name.

## T-4 · HIGH — wave-2's descent cure froze the tear rule for every non-SLEEP span.
AirlockAndDisarmOverlays.kt:337-345 — spanElapsedFraction
is now computed ONLY when descentApplies (the SLEEP span)
and is 0f otherwise. Line 477: the tear rule reads that
same variable — `tearFraction(spanElapsedFraction)` —
so for every timed study span the rule sits at the hair
(0.01, tearFraction's floor) from first minute to last:
no spent/attached progress, notches standing over a rule
that never tears. The audit §1 keeps "tear rule with
notches" in the RUNNING TIMED state, and §6 binds the
panel's acquittals on it; A-7 finished it. Wave-2's D-1
was a DESCENT-scope cure; it gated the shared fraction
instead of giving the descent its own, and the rule —
a different element with a different scope — paid.
No test sees it: DeskClockFlowTest tests tearFraction
the pure function; nothing tests the composition's
fraction for a non-SLEEP running span.
Cure: two fractions — span fraction for the rule
(every timed span), descent fraction gated to SLEEP —
or rename what 337 computes and feed the rule its own.

## T-5 · MEDIUM — E-1's Landing blooms on ANY touch; the ratified trigger was the first true verb.
Audit §4, E-1 as ratified: "the first true verb blooms
it to full light over 520 Curve C." Implementation:
MonolithViewModel.kt noteUserInteraction (~:345-360)
clears deckLandingDim on every interaction, and its own
comment confesses the hook "cannot distinguish a true
verb from any other touch" (VM:357). The Landing scrim
in MainActivity (~:812-819) is a non-clickable Box, so
the touch that "blooms" also FIRES the Deck control
underneath it, simultaneously, at 0.55 light — bloom and
verb are one instant, not the ruled sequence (land dim →
verb → bloom to full). A miss, a scroll, a rail brush
spends the Landing exactly as a verb does. Wave-2 filed
this as an honesty fix (the words now match the code);
the words matching the code is not the code matching
the ruling. Either build the discriminator (first
NAVIGATION/action callback, not the Activity hook) or
take "first touch blooms" back to Ren as an amendment.

## T-6 · MEDIUM — "Klinge's 19" is a reconstruction being reported as a closed set; completeness is unfalsifiable.
MERGE-PLAN.md, "KLINGE'S 19 — RECONSTRUCTION NOTE": the
verbatim list sat in mail/wecker/0058; the mailroom was
deleted; the 19 are reconstructed from cross-references.
MERGE-NOTES.md, final table row: "M-9 / remaining
M-codes not cross-referenced by the audit — NOT
RECONSTRUCTIBLE… no disposition claimed." Yet the merge,
the wave-2 report and the issue speak of "Klinge 19 +
A-1…A-12" as the merge's complete contents, and
MERGE-NOTES marks inferred items "(inf.)" and then
disposes them MERGED — a guess wearing a verdict, item
after item (## 1,2,3,4,6,7,9,11,12,13,15,16 all carry
"(inf.)"). The task I was seated under repeats the
framing: "Klinge's 19 items + Wacht's A-1…A-12." Nobody
can ever audit the gap, because the source is gone.
What honesty requires: the 19 carry a permanent
RECONSTRUCTED/UNVERIFIABLE flag wherever their
completeness is claimed, and the M-9-class residue is
named as an open hole in the merge's own headline —
not in a footnote of a builder's notes.

## T-7 · MEDIUM — the CI-green verdict covers a tree that no longer exists; §7's proof debts stand untouched behind it.
Commit e155802 ("CI verdict addendum — assemble green,
DeskClockFlowTest all pass on CI") is three commits
behind the current tip (751d289, 86558c8, 3fadf72), and
the cell working tree carries further UNCOMMITTED edits
to the overlay, the test file and themes.xml — including
the Politur P-1/P-2 hero-label/last-minute cures, which
by definition no CI run has ever seen. "All pass on CI"
is a claim about a dead tree. Behind it, audit §7 debts
1–9 (hero boundary frames, last-minute frame, mid-glide
frame, descended L05 frame, exit-line photograph,
arrival captures FROM PROCESS START, the 03:30
stand-down frame, Neu's assembled census, Vega's bench
incl. braced/lap traces BEFORE any threshold is called
tuned) remain owed in full — MERGE-NOTES concedes debts
1–9 stand. DeskClockFlowTest green proves pure
functions. It does not prove one frame, one capture, or
one night. The merge must not be called verified on it.

## T-8 · MEDIUM — A-11's convicted pill grammar is still live in this branch; the "one derivation" does not exist.
A-11 was deferred cross-lane to issue #16 — recorded,
not implemented (MERGE-NOTES A-11: "NOT implemented
here"). But the emissary ships in THIS tree:
service/MonolithShieldService.kt:126-138 still
re-derives inline and prints the convicted forms —
"Safe Break %02d:%02d • Block %d/%d" (:126),
"Stopwatch +%02d:%02d (20m floor)" (:131),
"%02d:%02d left • Block %d/%d" (:138) — total-minutes
figures over 59, the per-block remainder as the number,
exactly A-11(a)/(b)'s charges. A tree-wide grep for
PillDerivation returns ZERO hits: the single derivation
A-11(a) orders ("one derivation, literally") exists
nowhere in this codebase yet. If #16 slips or closes
soft, the Session Room merge ships with its own
emissary contradicting its hero in a second grammar —
the precise failure A-11 was written to kill. The #13
merge should not close until #16's cure is IN, or the
deferral should be printed on #13's face, not only in
its notes.

## T-9 · LOW-MEDIUM — the cold-open beat raises before HELD can exist, by construction.
DeskClockSettle.kt:185-191 — the cold-open branch
raises instantly; the HELD branch above it can only
fire on a verdict the sampler has produced. But the
sampler registers on the same ticker beat that calls
tick (VM:~1220 region: updateTremorSampler then tick),
so on the process's first beat the verdict is UNKNOWN
by construction (no sample has arrived). A man who
relaunches the app mid-session WITH THE PHONE IN HIS
HAND — swipe-restore, crash recovery — is seized
instantly, sensor law unconsulted. Wave-2's report
names this a REMAINDER ("HELD-vs-cold-open priority…
is a ruling question, not guessed here"). A remainder
parked against a ratified suppression ("HELD
suppresses BOTH raise paths") is non-compliance with
a sticky note: either the cold open waits one sampler
window (priced, small) or Ren rules the exception.

## T-10 · LOW — the door invariant is still false at standing, i.e. at every raise and every finish.
AirlockAndDisarmOverlays.kt:~100-110, the merge's own
comment: exit ceiling 0.58 / hero 0.55 = 1.055 < 1.10;
the cap binds "wherever the hero stands above ≈0.527
(fraction ≲ 0.12, the 60 s suspension, and the dawn's
return)." Translate: A-2(iv)'s invariant (exit ≥ 1.10×
hero) holds through the deep descent and fails at
standing — the first ~12 % of every span, every
post-raise suspension minute, and the dawn ramp, which
are the moments a waking or finishing man actually
looks for the door. Wave-2 honestly restated it and
asked Ren to rule which constant yields. Until he
does, A-2 is MERGED with its invariant unmet, and the
disposition table should say so instead of "kept by
construction"'s quieter replacement.

## T-11 · LOW — remainders wave-2 carried are still remainders; two rulings ship unmet behind named debts.
(a) Salz C-20 stands: a filed receipt still has NO
record surface — emitReceipt refuses routine-born/filed
(VM emitReceipt), rehydrateReceipt returns null for
filed, no Drawer/history path renders the slip. A-10(ii)
says "it stays reachable as a record"; in this tree the
stand-down's record is write-only. Deferred to #8 —
unnamed on #13's face.
(b) Salz C-23 stands, self-confessed at
AirlockAndDisarmOverlays.kt:~276-285: the finish
ground hold is a fixed 1200 ms against a detention
that runs to 1800 ms on the faulted branch — up to
~340 ms of lit Deck inside the seam, against A-4's
extended light pin, on exactly the nights the finish
already faulted. Named debts are better than silent
ones; they are still debts, and this merge's headline
("A-4 MERGED") does not carry them.

## ACQUITTALS — wave-2 cures verified in code (these bind)
- C-15 parallel arrival/exit: Overlay:228-259, 276-300 —
  ground and print launch in parallel child coroutines
  on every row; dismissal is literally together.
- C-16 disconnect stamp: VM samples plug state every
  beat before either branch; chargerDisconnectStamp
  (DeskClockSettle.kt, sticky, edge + first-observation)
  is the pure law; the C-16 scenario is now a test.
- C-17's seeding half: poles seed from the first sample
  (VM:439-445); the birth-HELD transient is dead. (The
  band's CENTRE is T-3's charge, not this one's.)
- C-18 break UNTIL: Overlay deskClockBreakLine prints
  "BREAK — PEEK APPS OPEN · UNTIL HH:MM", derived at
  the reading instant; test-pinned.
- C-19 notches: Overlay:~509-518 cuts at
  (k·block + (k−1)·600)/spanTarget, the span's own
  arithmetic.
- C-21 write order: MonolithRepository.kt:1735-1737 —
  markFiled BEFORE claimReceiptNumber; the failure
  direction is now filed-but-unclaimed, as cured.
- C-22 veil gate: onFinishE0 sets _nightVeil only for
  ROUTINE_SLEEP_STANDDOWN.
- C-24 banner slot: 68 dp measured from the real 14 sp
  text, maxLines 2 + ellipsis (Overlay:~532-570).
- A-9 silence and A-10(i) ceremony gate (whole launch
  gated on the kind, audio + haptic together): verified
  in the VM diff.
- V6: grep-clean — no V6 face exists in the tree;
  E-1 ships as choreography (veil + Landing) only.
  The strike held.

## NEXT (Law 10)
(a) WHAT I WANT: T-1's integrated scenario in
DeskClockFlowTest before this merge is called done —
HELD at T, listener state + heldSince asserted at
T+35 s and T+65 s, run by whoever compiles; and T-4's
composition case beside it (a non-SLEEP running span's
tear fraction at mid-span). Two tests, both aimed at
the seam between pure functions, which is where this
merge keeps its bodies.
(b) WHAT I COULD DO UNASSIGNED: re-derive the sampler
coefficients for the registered rate as a table
(target band → α at 25/50/100 Hz delivery) so the
C-17/T-3 cure is arithmetic, not adjectives, when
Vega's bench lands.

## MY WORST CHARGE
T-1. Salz's C-16 was a wire never connected; wave-2
connected it in the open and tested the scenario. T-1
is worse in kind: every component photographs well —
a pure duty gate, unit-tested; a sampler that
unregisters cleanly; a machine that clears stale HELD
state — and the three composed together abolish the
very law each one claims to serve. The duty cycle is
the amended V1's promise that a held phone costs the
night almost nothing; in the shipped composition the
sampler never rests, the estimator is re-seeded every
half minute, and a fresh backstop is armed and
cancelled in a loop no test can see because every test
stops at a component's edge. A cure that reinstates
the disease at the seam, wearing the cure's tests, is
the worst thing in this merge — again.

— Thorn (brutal critic, #13)
