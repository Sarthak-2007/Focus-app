# FEEL — RAISE ENERGY: THE FOUR ARRIVALS

SCHIMMER, Feel Specialist 3, Wacht's cell — issue #13
(Sarthak-2007/Focus-app, branch `agent/issue-13`, clone
`builds/cells/issue-13`). Slice: how the Session Room's
RAISE feels — the four cause-keyed arrivals of audit §3
A-4 (UNWATCHED / WATCHED / HAND / SEEDED), as they stand
AFTER the wave-2 cure pass. Files judged, re-read fresh:
`ui/components/AirlockAndDisarmOverlays.kt` (entry block
:203–309, ground seat :376, print layer :387),
`ui/MonolithViewModel.kt` (cause stamping :1223–1231, wake
re-raise :368–371, chip raise :1967–1972, visible flag's
birth :249–250), `ui/DeskClockSettle.kt` (the raise law
that feeds the causes), `ui/theme/MonolithCurves.kt`
(:55 EasingA, :63 EasingB, :64 EasingC), `MainActivity.kt`
(overlay seat :795–806). Every timing figure below is the
code's own cubic-bezier arithmetic, computed from the
shipped control points — not an impression of the numbers
in the table. No code touched; no compile run, per orders.

## THE VERDICT IN ONE BREATH

Salz's C-15 is CURED — verified, not trusted: ground and
print now launch in parallel child coroutines on every
row, both delays run from the same t=0, and the totals
land on the table exactly (660 / 600 / 440). The watched
black slab is dead: completed black with no print lasts
~0 ms by construction and print is legible at ~206 ms —
a blink, not a death-and-resurrection. But the four
arrivals do not yet feel like four causes. Measured the
way the eye measures — time to legibility, not time to
alpha 1.0 — the table's 660 / 600 / 440 compresses to
~650 / ~331 / ~320 / cut: THREE feels, not four. WATCHED
and HAND share an identical first 180 ms and reach 90 %
print within 11 ms of each other; their 160 ms of nominal
difference lives entirely in the sub-perceptual tail
above alpha 0.90, where at the face's 0.55 standing alpha
nobody alive can see it. And SEEDED, the arrival whose
whole law is "never fade," is the one arrival whose
frame one is not guaranteed: its alphas are born at 0
and snapped by an effect, its flag is born false and
raised by a 1 s ticker, and the audit's own no-flash
conditions (initial state, black window background) were
never built — so the cold open can show the Deck first
and then cut, which is a fade's opposite wearing a
snap's name. Two defects block the feel; two more are
named costs. The breath and the blink are real. The
door does not yet know it was knocked on, and the cut
does not yet know it must be first.

---

## §0 · C-15, VERIFIED AGAINST THE CURRENT CODE

The wave-1 disease was serial `animateTo`: the print's
delay started only after the ground finished, every row
ran ~40 % long, and WATCHED sat ~360 ms on completed
black. The current entry block (Overlay :205–265) runs
each row as two `launch {}` children of the one
`LaunchedEffect(visible, entryCause)`:

- UNWATCHED — `launch` ground 260 A (:228); `launch`
  print 520 C, delay 140 (:229–235). Total 660 ms.
- WATCHED — `launch` ground 180 A (:241); `launch`
  print 420 B, delay 180 (:242–248). Total 600 ms.
- HAND — `launch` ground 180 A (:252); `launch` print
  260 A, delay 180 (:253–259). Total 440 ms.
- Both exits are parallel the same way (finish :276–283,
  dismissal :288–299 — "together" is now literally true).

Totals match A-4's table to the millisecond. The cure
is real and this seat acquits it without remainder.
What follows is not C-15 reopened; it is what the cured
timings FEEL like once they finally run as written —
the question wave 1's bug made unaskable.

A note on method, because everything below turns on it:
the eye does not read a fade's duration, it reads its
TIME TO LEGIBILITY and its SHAPE. A tween's last stretch
from alpha 0.90 → 1.00, multiplied by the print's lawful
0.55 standing alpha (Overlay :387 composes
`printAlpha × surrenderAlpha`, then per-element alphas
0.55 / 0.58), is a luminance step of ~0.05 — below
notice on a night face. So each arrival has two totals:
the table's, and the felt one (delay + time for its
curve to reach 0.90). The curves, from
`MonolithCurves.kt`'s own points:

- Curve A (0.2, 0, 0, 1) — hard ease-out: 0.50 at 20 %
  of duration, 0.90 at 54 %, then a long polish tail.
- Curve B (0.05, 0.7, 0.1, 1) — a lunge: 0.50 at 6 %,
  0.90 at 36 %; nearly two-thirds of its span is tail.
- Curve C (0.3, 0, 0.8, 0.15) — a held breath in the
  mathematics themselves: 0.10 at 41 %, 0.50 at 81 %,
  0.90 at 97 %. Almost everything happens at the end.

---

## §1 · UNWATCHED — the held breath (ground 260 A, print 520 C at +140; felt ≈ 660 ms)

Beat by beat, for a witness who is not there — which is
the row's premise (machine fire ≥ 10 s after the last
interaction, VM :1227–1228's else-branch):

- **0–70 ms.** The lit Deck sinks fast (ground A is at
  0.64 by 70 ms). Nobody watches this; it exists so a
  later glance never catches a blend.
- **70–260 ms.** The ground completes (0.99 at ~217 ms).
  Print is still exactly 0 — its delay runs to 140 and
  Curve C's head is nearly flat besides.
- **140–353 ms.** The black beat, and it is LONG: print
  does not reach even 0.10 until ~353 ms absolute. Pure
  black, then a ghost (print × 0.55 ≈ 0.05 at 400 ms).
- **353–561 ms.** The ghost thickens slowly — 0.15 at
  400, 0.33 at 500. This is developing, not appearing:
  a photograph in a tray.
- **561–660 ms.** The breath releases. Curve C spends
  its last fifth going 0.50 → 1.00 (0.90 at ~647 ms);
  the face arrives all at once at the end, the way a
  held breath is let out — not gradually, but over.

**Feel verdict: a distinct cause, honestly built.** The
apparent no-blend violation (print starts at +140,
ground not done until 260) is no violation the eye can
catch: print is imperceptible until ~353 ms, by which
time the ground has been complete for ~90 ms. Curve C's
invisible head buys the parallelism. For the true
unwatched case this is the best arrival in the table —
the room does not switch on, it condenses.

**Its named cost (FE-3, below):** the discriminator is
a clock, not a gaze. §4 prices it.

## §2 · WATCHED — the blink (ground 180 A, print 420 B at +180; felt ≈ 331 ms)

Beat by beat, eyes on the phone (any machine fire
< 10 s after the last interaction, VM :1226–1227 —
including V1's set-down fire, the man who just put the
phone down and is still looking at it):

- **0–97 ms.** The Deck dives (ground A at 0.90 by
  97 ms). Because he is watching, this reads as the
  room deliberately darkening — a lid lowering, tracked
  by the eye as one intentional motion.
- **97–180 ms.** The ground settles to 1.00 (0.99 at
  ~150 ms). Print still 0.
- **180 ms.** The black point: ground complete, print
  at zero — for exactly one instant. There is NO slab:
  the interval "ground done AND print imperceptible"
  is ~0–3 ms, because Curve B leaves the gate at a
  lunge (0.10 within ~3 ms of its start, 0.50 within
  ~26 ms).
- **180–206 ms.** Print is at half strength. The hero
  is legible (~206 ms from the takeover's start).
- **206–331 ms.** Print climbs 0.50 → 0.90. The face
  is, to any human measure, THERE.
- **331–600 ms.** The tail: 0.90 → 1.00 over 269 ms
  nobody can see. The table calls this part of the
  arrival; the eye filed the arrival as finished
  four frames after the black point.

**Feel verdict: a blink — the brief's question,
answered.** Death-and-resurrection needs two things:
a black long enough to register as an ENDING, and a
return slow enough to register as a new beginning.
Pre-cure WATCHED had both (~360 ms of slab, then a
print crawling in). Cured WATCHED has neither: dark-
to-legible is ~206 ms, inside one eyelid blink's
envelope (100–400 ms), and the ground's own 180 ms
fade means the eye never loses the thread of the
surface — it watches the same room close its eyes and
open them. Salz's charge is not merely fixed in
duration; it is fixed in kind. Acquitted, and the
acquittal is this seat's second-hardest position
(§6 gives the hardest).

One shadow, priced in §4 as FE-4: because B is a
lunge, WATCHED's felt tempo is set almost entirely
by its 180 ms delay. Its 420 ms duration is a
fiction the tail tells the table.

## §3 · HAND — the door that answers (ground 180 A, print 260 A at +180; felt ≈ 320 ms)

Beat by beat, the DESK CLOCK chip tapped on the Deck
(VM :1967–1968 stamps HAND before the flag flips, so
cause and visibility change atomically — that much is
right):

- **0–180 ms.** IDENTICAL to WATCHED, frame for frame:
  same ground (180 A), same delay, same black point.
  A man who just tapped a chip and a man whose phone
  was seized by the settle machine have, for the
  first 180 ms, the same experience.
- **180–232 ms.** Print A leaves slower than B did
  (0.50 at +52 ms, vs B's +26) — for ~50 ms HAND is
  actually BEHIND the machine arrival it should
  outrun.
- **232–320 ms.** Print 0.50 → 0.90 (0.90 at ~320 ms
  absolute — 11 ms BEFORE WATCHED's 331; a coin
  flip, not a difference).
- **320–440 ms.** Tail, invisible as all tails are.

**Feel verdict: this is the defect the slice exists
to file (FE-1).** The ground does answer instantly —
the screen visibly reacts within a frame of the tap,
so the tap never feels dead. What HAND lacks is any
signature of AGENCY. The one arrival a man CAUSES,
deliberately, with a verb he chose, is choreographed
as a slightly shorter copy of the arrival that
happens TO him. Nacht's original instinct (print at
+0, a snap of response) was amended away on the
no-blend law, and the amendment's cost was never
priced in feel: at +180 the door's answer and the
machine's blink share a birthday. He cannot feel the
difference between "I opened the clock" and "the
clock took the screen" until both are already over —
and a cause that can only be identified in the tail
is, perceptually, not a cause.

## §4 · SEEDED — the cut (no fade; frame one at full lawful alpha)

Two births feed this row, and they must be judged
separately:

**(a) The wake re-raise** (battery-yield return,
VM :368–371): the overlay never left composition; its
animatables sit at 0 after the yield's exit; his tap
flips visible with cause SEEDED and the effect snaps
both alphas to 1 (`snapTo`, Overlay :213–214) in the
same frame batch. **This SEEDED truly never fades.**
It cuts from whatever the yield left standing to the
full lawful face — 0.55 print, 0.58 exit, the light
cure's numbers, not raw 1.0 (Overlay :387 and the
standing constants) — and a summoned face is allowed
to cut: he knocked, it is there. Acquitted. (Samt's
S-4 owns the tap being spent twice; not re-filed.)

**(b) The cold open** (settle machine's cold branch →
`lastRaiseWasColdOpen` → SEEDED, VM :1224–1225):
**this SEEDED's "frame one" is not the process's
frame one, and the code guarantees nothing about it.**
Three gaps, compounding (FE-2):

1. The flag is born false (VM :249–250) and the raise
   waits for the per-second ticker's first deciding
   beat. Until then the process composes the ordinary
   app — the Deck, at Deck light. The audit's SEEDED
   row says "the clock is the first surface"; the
   shipped cold open's first surface is the Deck,
   for up to a ticker grain.
2. The overlay's alphas are born at 0
   (`remember { Animatable(0f) }`, Overlay :203–204)
   and become 1 only when `LaunchedEffect` runs its
   `snapTo` (:205, :213–214). An effect-driven snap
   is a promise about the SECOND composition, not a
   property of the first frame drawn with
   `visible = true` — that frame is composed with
   ground 0 / print 0 (transparent ground at :376,
   invisible print at :387), and whether it is ever
   presented is a race the code does not close.
3. The audit's own verification conditions for this
   row — "the seed composes as AnimatedVisibility's
   INITIAL state; the window background on the seeded
   path is #000000; the no-flash proof is captured
   from process start" (AUDIT §3 A-4) — are simply
   absent: there is no initial-state seeding anywhere
   in the overlay, and no window-background write
   anywhere in `MainActivity.kt` (grep: the only
   window touch is the keep-awake flag, :258–263).

So the cold SEEDED does not fade — it does something
ruder: it can FLASH the lit app and then cut to the
vigil. A fade would at least be a transition. Mond's
law ("the face fades only on its first arrival in a
session") is kept in the letter by a row whose first
arrival in the PROCESS may be preceded by a surface
that is not the face at all.

---

## §5 · DEFECTS, WITH EXACT CURES

**FE-1 (HIGH) — WATCHED and HAND are one arrival at
two durations; the table's distinction lives in the
invisible tail.**
Evidence: identical ground + delay (Overlay :241 vs
:252, both 180 A / delay 180); felt totals from the
shipped curves — WATCHED reaches print 0.90 at
180 + 151 = ~331 ms, HAND at 180 + 140 = ~320 ms.
The nominal 600 vs 440 gap is spent above alpha
0.90, i.e. below a 0.05 luminance step at standing
alpha. Two of A-4's four causes are perceptually
one cause.
CURE (exact, and it respects the no-blend LAW while
amending the table's letter — Wacht/Ren rule the
amendment): HAND's print delay 180 → 120, duration
and curve unchanged (260 A; new total 380, felt
~260). The law's intent is that print never be
VISIBLE over a lit Deck, and at +120 the print is
still invisible while the ground finishes: print A
reaches 0.05 at +16 ms (absolute 136, ground 0.973)
and 0.10 at +22 ms (absolute 142, ground 0.983);
by the time print can be seen, black is complete
for any eye. HAND then separates from WATCHED where
separation is felt — legibility at ~172 ms vs
~206, 0.90 at ~260 vs ~331 — and the door answers
~60 ms sooner in the only currency the hand spends.
One literal at Overlay :256 (`delayMillis = 180` →
`120`) plus the row's comment. If the +180 letter is
held absolute, the fallback cure is HAND's print to
Curve B at 180 ms (total 360, 0.90 at ~245): it
separates the middles but keeps a dead 180 ms under
a deliberate tap — strictly worse, named so the
choice is Ren's with both prices showing.

**FE-2 (HIGH) — SEEDED (cold open) has no frame-one
guarantee: born-transparent animatables, an
effect-driven snap, a ticker-late flag, and the
audit's black-window condition unbuilt.**
Evidence: Overlay :203–205 (alphas born 0, snap in
`LaunchedEffect`), :311/:376/:387 (the first
visible frame composes at alpha 0); VM :249–250
(flag born false), :1224–1231 (raise on the ticker
beat); MainActivity — no window background set
anywhere; AUDIT §3 A-4's three verification
conditions unimplemented.
CURE (exact, three small pieces, no new surface):
(1) Make the snap STRUCTURAL: the VM increments a
`deskClockRaiseGeneration` at every raise write
(ticker raise, wake, chip); the overlay keys its
animatables on it —
`remember(gen) { Animatable(if (seededRaise) 1f else 0f) }`
— so a SEEDED raise's FIRST composition already
carries 1f and no effect is trusted with frame one.
The VM already knows the cause before it flips
visible (it writes cause first at :1223–1230 and
:368–369); it passes `entryStartsAtFull` beside the
cause so the overlay needn't guess.
(2) Close the pre-raise window: while the process
has not yet had its first settle decision AND a
live session exists at first composition
(`activeSession` is already a VM flow the host
collects), MainActivity composes a plain true-black
box over the Deck — the same composition Samt's S-3
cure needs after a yield; one black-gate, two
masters. The cold open then starts on black, and
SEEDED's cut is black → face, never Deck → face.
(3) Set the window background to #000000 for the
seeded path, as the audit wrote, so even the
pre-composition surface cannot flash the theme's
background. Proof: audit §7 debt 6's cold-open
capture FROM PROCESS START, re-run against (1)–(3)
— zero non-black frames before the face.

**FE-3 (MEDIUM, named cost — cure deferred by
design) — The 10 s witness line is a feel cliff,
and UNWATCHED's black is the longest in the table.**
Evidence: VM :1223–1228 discriminates on
`clockNowMono − lastInteraction < 10_000` alone.
UNWATCHED's print stays under 0.10 until ~353 ms
(§1) — longer pure black than the watched row the
slab-charge was written about. A man who sets the
phone down and glances back at second 11 watches an
unwatched arrival's last 300 ms and reads slowness;
at second 9 he would have gotten the blink. The
premise "unwatched = nobody sees the black" is
statistical, not true, and near the boundary it is
false exactly when a glance is likeliest (just
after putting it down).
No cure filed for this merge: shortening Curve C's
head would spend the held breath, the one arrival
this seat acquits without remainder, to soften a
boundary case. Named so it is a decision: if
first-night proof (§5 debts) shows boundary
glances reading as death, the cure is a third
machine row (RECENT: ground 180 A, print 520 C at
+140 — WATCHED's ground under UNWATCHED's breath),
priced SMALL, and it belongs to Ren, not to a
builder guessing at the table.

**FE-4 (LOW) — The ground carries no cause
signature; all differentiation is delegated to the
print, which is why FE-1 could happen silently.**
Evidence: every ground in the table is Curve A
(Overlay :228, :241, :252); WATCHED and HAND grounds
are the same curve AND the same 180 ms. Ground is
physics (the room darkening) and print is voice —
a defensible split — but it means any future edit
that equalises two print delays silently merges
two causes, with no test and no comment to catch
it. CURE: words, zero code — pin the felt totals
beside the entry table in the overlay's header:
"felt (to 0.90): UNWATCHED ~647 · WATCHED ~331 ·
HAND ~320 — if two felt totals meet, two causes
have merged." The DeskClockFlowTest table should
assert the FELT ordering (UNWATCHED > WATCHED >
HAND > SEEDED = 0), not only the nominal totals.

**FE-5 (LOW) — A re-raise inside the dismissal's
180 ms Throw inherits the full arrival duration
from a mid-flight alpha.**
Evidence: the animatables are shared across entry
and exit (Overlay :203–204); `animateTo` on a
retarget restarts the full tween span from the
current value, so a chip re-tap ~100 ms into a
dismissal pays a fresh 440 ms to travel half the
distance. Rare (it needs a deliberate double-tap
against the clock), harmless (nothing lands
wrong), and felt as the door being briefly heavy.
CURE: none filed; if FE-1's cure lands, re-price —
a 380 ms HAND makes the heaviness shorter than
the notice threshold.

### Acquittals inside the slice

- The print arrives as ONE photograph: a single
  `printAlpha` drives the whole column (Overlay
  :387) — no element staggers, no cascade. A vigil
  should condense whole, and it does.
- The tap is live during the black point: the Box's
  `clickable` (Overlay :377) is not gated on alpha,
  so a tap on the black dismisses. A black that
  swallows the verb would feel like death; this
  one is a door painted black. Acquitted.
- Cause and visibility change atomically on every
  raise path (VM writes cause before the flag at
  :1223–1231, :368–371, :1967–1972): no raise can
  borrow the previous raise's choreography for a
  frame. The `LaunchedEffect(visible, entryCause)`
  keying (Overlay :205) is therefore safe, not a
  replay hazard.
- UNWATCHED's overlap (print delay 140 < ground
  260) never blends visibly — §1's arithmetic.
  Parallelism bought with Curve C's invisible head
  is honest engineering, not a fudge.

## §6 · PROOF DEBTS THIS SLICE ADDS (to audit §7)

1. Arrival captures at 60 fps for all four rows,
   read for FELT totals, not nominal ones: frames
   at each row's legibility instant (~353 ghost /
   ~206 / ~172-post-FE-1 / frame one) plus the
   frame BEFORE each (the black point must be a
   point in WATCHED and HAND, a held breath in
   UNWATCHED, nonexistent in SEEDED).
2. The cold open FROM PROCESS START (extends debt
   6): process born over a live session — every
   frame from the first composed frame must be
   black or the face. One Deck frame fails FE-2's
   cure, however fast it passes.
3. The boundary glance (FE-3's evidence path): a
   watched capture of an UNWATCHED raise begun
   11 s after set-down, read cold by Neu's seat —
   if it reads as slowness rather than breath, the
   RECENT row returns to Ren with the evidence.

## §7 · PRICE SUMMARY

FE-1 ZERO–SMALL (one literal + comment; the
amendment ruling is Ren's) · FE-2 SMALL (one
generation counter, one remember keying, one black
gate shared with Samt S-3, one window-background
line) · FE-3 named cost, no cure this merge ·
FE-4 ZERO (words + one test assertion) · FE-5 no
cure filed. No new surface, no DB change, no verb
added; the arrival table gains no row without
Ren's word (FE-3's RECENT is a proposal, not a
filing).

---

## CLOSING — THE ONE FEEL DECISION I DEFEND HARDEST

**A cause the eye cannot tell from another cause is
not a cause — and right now HAND, the one arrival
the man himself causes, is indistinguishable from
WATCHED, the arrival that happens to him.** I will
defend FE-1's cure — HAND's print delay 180 → 120,
inside the no-blend law's perceptual truth — against
the table's letter, against "440 already differs
from 600," against every argument from nominal
totals. A-4's whole ruling was that motion is keyed
to WHY the face rose. Keying that cannot be felt is
bookkeeping. The difference between 600 and 440
lives above alpha 0.90, in a tail that at this
face's lawful dimness no retina will ever report;
the difference between "I opened it" and "it took
the screen" is the difference between a tool and an
interruption, and it is decided in the first
200 ms — the exact window where the two rows are
currently frame-identical. Give the door its 60 ms
of head start and the four arrivals become what the
audit promised: a breath nobody ordered, a blink
somebody half-saw, a door answering its knock, and
a cut that was always already there. Until then the
table has four rows and the room has three feelings,
and the missing one is the man's own hand.

— SCHIMMER, Feel Specialist 3 (Wacht's cell, issue
#13), Oct 30, 2026. No commit, no push, no post:
Wacht carries this to the repo.
