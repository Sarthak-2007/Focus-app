# FEEL — MOTION SCORE & PACING VERDICTS · THE RECEPTION SHEET

Seat: **Atem (feel specialist, seat 4)**, Pforte's cell, issue #14.
Charge: motion and pacing only — how the sheet moves, and how
time feels while it stands in front of him. No code written,
nothing compiled, nothing run.

Read whole, in this tree (`agent/issue-14`):
`ReceptionSheet.kt` (the `fraction` Animatable, the 5 s
re-derive loop, the FIX dip, the key blocks as built),
`ui/theme/MonolithCurves.kt` (MonolithCurves + MonolithKin),
`ParkDrawer.kt` (the F-3 pair as built, lines 371–374, and the
twin 5 s loop), `AUDIT-setup-sheet.md` §1 item 26 + §3,
`DESIGN-composition.md` item 12c (F-6) + 12d (F-7), the tear's
prior build in `FocusSequencerScreen.kt` (LeafSlot ghost).

## §0 · THE VOCABULARY I AM ALLOWED — AND THE ONE I USE

No new curve is invented anywhere in this file. Every number
below is already named in the tree:

| Name | Value | Character |
|---|---|---|
| `MonolithKin.EasingSeat` = `MonolithCurves.EasingA` | CubicBezier(0.2, 0, 0, 1) | fast commitment, long exact settle, zero overshoot — a landing |
| `MonolithKin.EasingThrow` | CubicBezier(0.5, 0, 1, 0.4) | accelerates away — a dismissal, never a landing |
| `specA` / `specB` | EasingA / EasingB(0.05, 0.7, 0.1, 1) tweens | specA defaults 260, specB defaults 420 |
| `DRAWER_OPEN_MS` / `DRAWER_CLOSE_MS` | 280 / 160 | KIN-DRAWER: rise SEAT, leave THROW |
| Duration vocabulary | {110, 140, 180, 260, 280, 420, 520} | MonolithCurves header law |

One dormant pair, recorded so nobody cites it at me:
`PRESS_MS` 90 / `PRESS_BACK_MS` 150 exist in MonolithKin and
are used **nowhere** in the tree (grep, this cell). §5 rules
on what that means for the FIX dip.

---

## §1 · THE RISE AND THE LEAVE AS FELT TIME

### 1a · Rise — 280ms, EasingSeat. **ACQUIT, unchanged.**

`fraction` 1→0, `tween(DRAWER_OPEN_MS, EasingSeat)`, the full
screen travelling its own height. 280ms for ~760dp is fast —
and that is exactly why it reads as an instrument seating
itself and not as a page arriving. Curve A spends its speed
early: most of the distance is covered in the first half,
then the sheet decelerates for a long, exact approach into
register and stops dead — no overshoot, no bounce, no
settle-wobble. A slower rise (420, 520) over this distance
would read as floating paper; a faster one (180) would pop.
280 is the one duration in the vocabulary where a
full-screen object can arrive with intent and land like a
drawer closing on its runners.

Two supporting facts in the code, both felt: interactivity
is gated on `fraction.value == 0f` (`interactive = visible &&
settled`), so no key can be hit mid-travel — the sheet is
never tappable while it is still becoming; and there are
exactly two rest heights, so the eye is never asked to judge
a half-seated sheet. First-open happens once; 280ms of
ceremony for the founding document is not a tax, it is the
seat being taken. Keep.

### 1b · Plain leave — 160ms, EasingThrow. **ACQUIT, unchanged.**

BACK, the Shield door, a PARK reprint closing: 160 THROW.
Throw's curve accelerates away — the sheet does not land
anywhere, it is dismissed, and dismissal should be shorter
than arrival and feel like it costs nothing, because it
does: leaving stamps nothing. 160 is brisk without being a
cut. If the plain leave ever borrowed the tear (§1c) or the
280, leaving would start to feel like a decision — it is
explicitly not one. Keep.

### 1c · The DONE exit — total 420ms (stamp → tear → throw). **ACQUIT — breathes, on one arithmetic lock.**

The choreography as ruled in the audit §3 (Letzt F-1) and
already built once in this tree (the counterfoil leaf):
stamp books **first**, `playTearPulse` replaces the firm
click, then the tear phase — the block below the rule drops
10dp and fades over **180ms specA** while the tear rule's
dashes halve over **260ms specB**, everything above the rule
**motionless** — then the **160ms THROW**.

The arithmetic that makes 420 true, stated so the builder
cannot drift it: the 180 and the 260 run **concurrently**
inside one 260ms tear phase; 260 + 160 = **420 total**.
Sequenced serially (180, then 260, then 160) the exit
becomes 600ms and this acquittal is void — that version
drags.

Why 420 breathes instead of dragging, at the one moment it
could afford to and the one moment it must not waste: the
fact is already booked before any motion starts (G-01
order — the tear here is a ghost over a booked fact, as in
the leaf), so none of the 420 is latency; all of it is
ceremony he can watch without waiting *for* anything. The
stillness above the rule is the pacing's real stroke: the
stub stays, the part below separates and falls, and the
160 THROW only starts once the separation has visibly
happened. Three beats — stamp, tear, gone — at 0 / 260 /
420. That is a breath, taken once, at the founding act.
Tear only when the press actually stamps; PARK reprints
keep §1b's plain 160. Keep, locked as above.

---

## §2 · THE STANDING SHEET — RULING ON THE 5-SECOND FLIP

**The problem, precisely.** While the sheet stands,
`nowMono` re-derives every 5,000ms (ReceptionSheet, the
`LaunchedEffect(visible)` loop). A granted door's proof ages
past the staleness bound and its print flips GRANTED →
STALE in front of him: word SET → UNPROVED, word colour
dim → full ink, the S-12 gloss line appears, a CHECK key
(48dp + 10dp spacer) appears, the total re-counts, the
Guard line reprints. As built, **all of it snaps in one
frame, including ~62dp of layout growth per flipping card**,
shoving everything below it down inside the scroll.

### RULING — the datum snaps; the furniture moves. No crossfade, no pulse, no sound.

**(a) The word, its colour, the total, the Guard line:
instant. 0ms. No transition of any kind.**
A measurement changing is not a door slamming — and it is
not a door fading, either. SET and UNPROVED are two discrete
readings of one instrument; there is no true intermediate
state between them, and a crossfade prints one anyway —
for ~half its duration the card would carry a blended,
half-legible word that is neither reading, i.e. a small lie
at exactly the moment the sheet is being most honest. A
digital readout does not dissolve between values; a gauge
that re-measures prints the new figure. Crossfade also
*announces*: it draws the eye and says "attend to this."
An aging proof is not an event in his life — nothing he did
caused it, nothing is asked of him (CHECK is offered, not
demanded; the total already prices it in the aged-proof
sentence). A pulse, a flash, or a sound on this flip would
be a nag wearing motion's clothes, and this sheet's law is
no nag. The 5 s tick itself is acquitted: it is a derivation
beat, not an animation — faster would make the document feel
nervous, slower would leave a stale reading standing as
fresh for longer than the truth allows.

**(b) The layout growth the flip causes: animated, on the
F-3 pair (§4).** The gloss line and the CHECK key are not
datum, they are furniture arriving — and furniture does not
teleport. Spec: gloss + key enter as **one**
`AnimatedVisibility` block, `expandVertically(tween(160,
EasingSeat)) + fadeIn(tween(160))` — 160ms, the exact pair
§4 acquits. Below-content then glides down over 160ms
instead of jumping 62dp between two frames. What he
perceives: the word has already changed (a) when the card
quietly grows its remedy underneath it. Reading order
preserved — fact first, furniture second, nothing blended.

**(c) Scroll: untouched.** No auto-scroll, no scroll
compensation, no anchoring correction on a standing flip.
He is reading a document; the sheet never moves his place
in it for a change he did not make. If the flipping card is
above his viewport, the animated growth shifts what he is
reading down by up to ~62dp over 160ms — a glide, legible,
over in a breath — and that is the worst case this ruling
accepts, deliberately: the alternative (silently rewriting
his scroll offset) is the sheet putting a hand on his side
of the page. If the flipping card is off-screen entirely,
nothing he can see moves except the total reprinting in
place — which is correct, and complete.

**(d) The reverse and sibling flips obey the same law.**
UNPROVED → SET with no excursion in between (a sweep lands):
word snaps, gloss + key leave on the F-3 exit pair (160
THROW, §4). UNMEASURED → any measured print: word snaps,
and — F-7 having reserved the 58dp key band — there is no
layout motion at all; that reservation is what makes (b)
rare instead of routine. One distinction, stated so it is
never blurred: a flip caused by **his return from a FIX
excursion** is not a standing flip — it is a verdict, it
arrives with the chit line and the firm click, and its card
follows §3's sequence. Aging is weather; a return is news.
They must never wear the same motion.

---

## §3 · F-6 — THE GRANTED CARD'S COLLAPSE, 89dp → 37dp

### RULING — ANIMATED. Two phases, key first, 340ms total when he is there to see it.

Instant collapse would spend F-6's whole point. The audit
ships F-6 because "the sheet visibly completes as he works"
— a card that is simply *already folded* the next time he
looks delivers the shorter sheet but none of the
completion; he would have to infer that something finished.
The fold is the receipt for the excursion. It is animated.

DESIGN 12c's sequencing instinct is right and I keep its
first phase; its second step ("then the card recomposes
collapsed") leaves a residual snap of the remaining ~30dp+
of body, and a smaller snap is still a snap — phase 2 below
cures it. This is the one place this file amends a sibling
seat's draft, and the reason is named: sequencing key-first
prevents one-frame snapping *of the key*; it does not by
itself un-snap the body.

**Phase 1 — the key leaves: 160ms THROW.** Exactly the F-3
exit pair: `shrinkVertically(tween(160, EasingThrow)) +
fadeOut(tween(160))` on the FIX/CHECK key block. A spent
key is a dismissal — THROW is its honest curve.

**Phase 2 — the body seats into its ruled line: 180ms
SEAT.** The job line (and escort/gloss, if any stand) fold
away and the card settles to the 37dp ruled line:
`shrinkVertically(tween(180, EasingSeat)) + fadeOut(tween(180))`
on the body block, EasingSeat because this motion *is* a
landing — the card arriving at its final, finished form,
the same curve the sheet itself rose on, at the vocabulary's
180. Phases run **sequentially** (1 completes, then 2):
run together, the height math is unreadable and the fold
reads as a general collapse; run in order, he sees *key
spent → line ruled* — cause, then record. Total after the
verdict is already on screen: **160 + 180 = 340ms** —
inside the 420 the house already spends on its biggest
ceremony, paid once per door, four times at most.

**Scroll, during and around the fold — three rules.**
1. The chit's auto-scroll (Funke's mechanism, audit §3)
   runs **first and finishes before Phase 1 starts**: an
   `animateScrollTo` at **260ms specA** (`tween(260,
   EasingSeat)`) bringing the visited card fully into view.
   Scroll and fold never run concurrently — a target that
   shrinks while the page is still travelling to it makes
   the scroll chase its own destination.
2. During Phases 1–2 the scroll value is **held fixed** —
   no compensation, no follow. The 52dp the fold frees is
   *supposed* to pull everything below it up toward him;
   that rising foot is F-6's K-2 cure happening in front of
   his eyes. Compensating it away would animate the benefit
   out of existence.
3. If the visited card is **not on screen** and no chit
   scroll is running (the standing-flip path can never
   reach F-6 — it produces STALE, not GRANTED — so this is
   the sweep-lands-off-screen case): collapse **instantly,
   0ms**. Motion nobody can see is not ceremony, it is a
   delayed layout shift waiting to surprise his reading
   position when he scrolls back. Fold it behind his back;
   when he arrives, the line is simply ruled.

The chit-printing exception in DESIGN 12c (the collapsed
line grows the chit line beneath the SET row for one
printing, 37 → 54dp) inherits this section's grammar if it
ships: that growth is furniture, 160ms SEAT expand per
§2(b) — never a snap, never a scroll.

---

## §4 · F-3 — THE KEY VISIBILITY PAIR, 160 / 160

### RULING — ACQUIT, exactly as built in PARK. Copy it byte-for-byte; do not "fix" the symmetry.

The pair (ParkDrawer 371–374, to be spent on the Reception
FIX/CHECK blocks): enter `expandVertically(tween(160,
EasingSeat)) + fadeIn(tween(160))`; exit
`shrinkVertically(tween(160, EasingThrow)) + fadeOut(tween(160))`.

The charge to answer: the sheet's own grammar is 280 in /
160 out — is a 160 in a broken rhyme? No. **In this house
the seat/throw distinction lives in the curve, not the
duration.** The sheet proves it: 280 and 160 differ there
because a full screen of travel and a dismissal are
different *distances*, and MonolithKin already scales
duration with distance elsewhere (KIN-NAV: 180 + 60 per
stop crossed). A key block grows 58dp, roughly a thirteenth
of the sheet's travel; giving it the sheet's 280 would not
make it grander, it would make it swell — local furniture
arriving in slow motion, the one motion that reads as the
interface hesitating. At 160 the block blooms at a *slower
speed per dp* than the sheet rises — present, unhurried,
local. Correct proportion, correct clock.

And the symmetric 160s are a feature: the block leaves on
the same clock it can arrive on, so a door oscillating at a
boundary (grant lands, proof ages, grant lands) breathes at
one period instead of lurching between two. Enter SEAT /
exit THROW supplies all the asymmetry the semantics need —
arriving is a landing, leaving is a dismissal; the shared
160 is the shared *size of the thing*, which does not
change with direction.

Two implementation notes, both protective: the fades in
PARK's pair carry **no named easing** (`tween(160)`, the
default) — copy that too; adding EasingSeat to the fade
would double-ease the pair against PARK and fork the
pattern. And spend the constant as PARK does —
`DRAWER_CLOSE_MS` for both directions. The name is a
drawer word doing key work, which is untidy and irrelevant:
 minting a `KEY_MS = 160` token to rename the same number
would fork the vocabulary this audit exists to keep single.
No new name, no new number.

This pair is load-bearing beyond F-3: §2(b) and §3 both
spend it. One pair, three surfaces, one felt grammar for
"a key arrives / a key is spent" everywhere on the sheet.

---

## §5 · THE FIX DIP — A NOTE, BECAUSE IT IS NOT MOTION

As built, the dip is a state, not an animation: the FIX
key's `offset(y = if (fixPressed) 1.dp else 0.dp)` toggles
instantly on press, holds for the whole Settings absence,
and clears after the return verdict (800ms post-return
delay, then `fixPressed = false` — the delay and the verdict
are the mechanism seat's; the dip's *feel* is mine).

**Ruling: keep it instant, and keep anyone from
"improving" it.** 1dp at this density is a tactile print,
perceived as contact, not travel — tweening it over
`PRESS_MS` 90 would spread one pixel across ~5 frames of
sub-pixel mush and read as nothing at all, while *claiming*
a press motion the eye never received. The snap down says
"pressed"; the long hold says "still out"; the snap back on
verdict says "judged." That is the whole sentence, and it
is grammatical. `PRESS_MS` / `PRESS_BACK_MS` stay dormant
in MonolithKin; no spec for this sheet may cite them as the
dip's duration — the dip has no duration, and that is its
correctness, not a gap.

---

## §6 · THE SCORE, ON ONE TABLE

| # | Motion | Spec | Verdict |
|---|---|---|---|
| 1 | Sheet rise | 280ms, EasingSeat (Curve A) | ACQUIT |
| 2 | Plain leave (BACK / Shield / reprint) | 160ms, EasingThrow | ACQUIT |
| 3 | DONE exit | stamp first · tear phase 260ms (drop+fade 180 specA ∥ dashes halve 260 specB, above-rule still) · 160ms THROW = **420 total** | ACQUIT, arithmetic locked: tear sub-motions concurrent, never serial |
| 4 | Standing flip — datum (word, colour, total, Guard line) | **0ms, instant.** No crossfade, no pulse, no sound | RULED (§2a) |
| 5 | Standing flip — furniture (gloss + CHECK key) | 160ms, expand EasingSeat + fadeIn, F-3 enter pair · scroll untouched | RULED (§2b–c) |
| 6 | F-6 collapse | scroll-to-card 260ms specA (chit only, first) → key exit 160ms THROW → body fold 180ms EasingSeat = 340ms after verdict · scroll held during fold · 0ms if off-screen | RULED — animated (§3) |
| 7 | F-3 key pair | 160 SEAT in / 160 THROW out, fades uneased, `DRAWER_CLOSE_MS` both ways | ACQUIT (§4) |
| 8 | FIX dip | 1dp offset, instant, held for the absence; `PRESS_MS`/`PRESS_BACK_MS` not cited | ACQUIT as a state, not a motion (§5) |

New curves invented: **none.** New durations: **none.**
Every number above is 0, 160, 180, 260, 280, or 420, on
EasingSeat, EasingThrow, specA, or specB — the tree's own
words, spent in the tree's own grammar: SEAT for landings,
THROW for dismissals, silence for measurements.

— Atem (feel), issue #14 cell
