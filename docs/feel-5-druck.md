# FEEL — THE MAN AT THE DOOR (Issue #11)

Seat: DRUCK, Feel Specialist 5, Tor's cell · For: Tor (lead)
Audit walked: the **fixed** door — `studio/plan/AUDIT-held-door.md`
FIX-1…FIX-8 as specced in `docs/tor-plan.md` and laid out to the
dp in `docs/designer-skeleton.md` (fixed skeleton, 418dp peek-owing
card / 292dp never-owed, gate line, bare intention, price from
frame one, honest exit). The shipped face in
`AirlockAndDisarmOverlays.kt:548` was read only as the before.
Nobody compiled. This is a feel pass on paper, on the fixed design
as written — where the spec is silent on a beat, I say so; I do
not invent a kinder implementation than the one specced.

Method: one visit, walked second by second, as one specific man.
Not "the user." The man below.

---

## 0. The man, before the door

01:0x. Sealed session running. He is half-asleep in the exact
way the audit's Halbwach seat named and then, in my reading,
under-used: not tired-and-reasonable — *pre-verbal*. His thumb
found the app before his mind filed the request. He did not
decide to break anything. A craving arrived the way an itch
arrives, and the hand answered it.

What he can do in this state, honestly inventoried:

- He can **recognise** — a name, a shape, his own words. Fast,
  free, no arithmetic.
- He can **tap once** on a large, still thing. One act.
- He **cannot** do maths (an instant minus now is a sum, and a
  sum at 1 AM is a locked door of its own), cannot weigh a
  conditional sentence, cannot read small caps patiently, and
  cannot hold a neutral fact neutrally. Every line the door
  prints lands as either *permission*, *accusation*, or *noise*
  — there is no fourth reception available to him tonight.

That inventory is the instrument for everything below. A line
is not judged by what it says in the audit room at 01:35 with
coffee. It is judged by which of those three buckets it falls
into at 01:0x with a travelling thumb.

Blank's verdict on the *old* face — "a wall with good manners,
a help for the man who didn't mean it, a waiting room for the
man who did" — is the baseline. My headline on the *fixed*
face, up front so Tor does not have to dig for it:

> **The fix cures the wall. It does not yet cure the waiting
> room, and it adds a quiet lecture hall inside it.** The
> fixed door no longer moves under his thumb, no longer lies
> about its clock, no longer shouts EMERGENCY at a two-minute
> lease — all real, all below in §8, plainly. But counted as
> *speech*, the fixed face still talks six lines of system
> for one line of his, still makes him watch temptation count
> down toward him, and still files two of its truest sentences
> (the memory count, the price) in a register he can only hear
> as scolding and warning. Door: yes, now. Host: not yet.

Charges are numbered D-F1… so Tor can rule them one by one.
Every correction is specced to a number or a string, priced in
the audit's units, and checked against the Held Door Truth
Baseline in §9. Nothing below re-opens an acquittal: the offer
matrix, RETURN never gated, price before the act, Night
palette, withheld state-AND-reason, the card shell pending the
Leaf call — all stand. I argue about *voice, weight, order,
and timing inside those rulings*, never about their existence.

---

## 1. Second −1 → 0 — Arrival: he is stopped mid-gesture

**What the fixed door does.** FIX-7: `GLOBAL_ACTION_BACK`
before launch on app-tier and shield paths (the blocked app no
longer stands visible, reward delivered, through launch
latency); entry `fadeIn(specA 180ms)`, opacity only, full
skeleton composed at final geometry underneath; arrival voice
= the seal's recipe at door pitch, 300 Hz, 44 ms, gain 0.60,
chordRatio 1.5, latch haptics kept.

**What he feels.** This is the fix's cleanest emotional win
and I say so without hedging in §8. Before: a 420 ms ghost and
a 16 ms switch tick — the door *materialised*, apologetically,
while the feed he wanted was still on the glass behind it. Now:
the app is taken away *first*, then one low sound with weight
in it, then a face that is already whole. The sequence reads,
in the body, as: **someone was already here.** That is the
only arrival a held door is allowed to have. It does not
startle (44 ms at 0.60 is a knock, not an alarm) and it does
not sneak.

**Where it still fails — D-F1: the card lands at the top of
the world, his eyes and thumb live at the bottom.**
The skeleton top-anchors the card: top edge 20dp from the
safe area, RETURN's centre at 314dp from the top of the safe
area on a peek-owing visit (skeleton §3.4). On the tall phones
this man actually holds, that leaves the entire lower third —
often 250dp+ of Night ground — empty beneath the card, and it
puts every word he must read at the *far* end of the device
from the hand that is holding it, at the hour when his eyes
drop, literally, toward his lap. Top-anchoring was adopted to
kill the centred column's growth (F-2) and it does — but the
audit chose between *centred-and-moving* and *top-and-still*
and never priced *bottom-and-still*. A bottom-anchored card
(same fixed slot stack, same pixel-identical RETURN bounds
within the visit) would put the name, the gate, and the slab
inside the arc his half-asleep gaze already occupies, and
RETURN — the one slab he must hit — under the thumb that is
already travelling *down there*, not on a pilgrimage up the
glass. F-2's law is stationarity, not altitude; the skeleton
conflated the two.

*Correction D-F1 (SMALL, layout only):* anchor the card to
the bottom of the safe area with the same 20dp outer padding;
slot stack, heights, and RETURN-bounds-invariance tests
unchanged (assert against the bottom-anchored address).
If Tor keeps top-anchoring for cross-face grammar (the Disarm
face centres), file the cost honestly: on this face, altitude
is a tax paid by the sleepiest visitor on every visit, to buy
consistency with a face he may never open.

**D-F2: the 180 ms fade is right; what fades in is too much
at once.** Under the fade, the *entire* skeleton arrives in
one instant — name, intention, two session lines, gate, price,
memory, slab, exit, badge: ten elements in the time it takes
to blink. A pre-verbal man does not read a face in parallel;
he lands on the loudest thing and the rest becomes weather.
Today the loudest thing is correctly the name (24sp Sora, the
face's one hero — right, §8). But the second-loudest thing is
the accent slab, and the third is whichever small line his
eye snags — usually the price, because it is the longest.
There is no *sequence* to his first second, and a door that
cannot sequence its greeting lectures by simultaneity: all
its sentences arrive with the same timestamp, so they read as
one paragraph of system speech (see §6, the talk ratio).

*Correction D-F2 (SMALL):* keep the single 180 ms card fade
for geometry (nothing moves, ever), but stagger **ink, not
layout**: name + RETURN at full α from frame one; the small
speech (H3 lines, price, memory) crossfades from α0 to its
specced α0.92 across the *second* 180 ms, in place, inside
slots already composed. He meets the name and the way back
first, alone, for one breath; the door's explanations arrive
a beat later, demoted by time the way they are already
demoted by size. No slot, height, or print changes; stationarity
tests unaffected (opacity is not bounds).

---

## 2. Second 0 → 1 — Recognition: the name, and his own words

**What the fixed door does.** H1: the app's real name, Sora
Bold 24sp, one line, ellipsis (guard prints what he actually
opened; site door prints the browser's name with the domain
as sub-line; sniper keeps the feed name — C-8/B-7, all correct
facts in the slot that used to hold a fiction). H2 directly
under it: his intention, **bare** — the `ACTIVE INTENTION:`
prefix dead — Atkinson 16sp, α0.92, one line, ellipsis. H3
under that: `SESSION ENDS {HH:mm}` at 12.5sp.

**What he feels.** The name first, alone at display size,
is the single most humane decision in the fixed design. At
the craving second he does not need to be told what he did;
he needs to *see* what he reached for, named plainly, and
feel the small drop of being caught accurately. The old
eyebrow (`AIRLOCK INTERCEPTION • {badge}`) spoke before the
name and spoke about the *system*. Now the first word on the
face is the fact of his own act. Keep. (§8.)

**D-F3: his intention is truncated by the face that exists
to honour it.** H2 is `maxLines = 1`, ellipsis, inside a 24dp
slot (skeleton §3.1). Intentions are written calm, at arming
time, in sentences: *"Finish the mock before I touch my phone
again"* is 48 characters — at 16sp across a card's content
width it does not fit one line. So on the visits where his
own words matter most, the door prints half of them and an
ellipsis — it *edits him, mid-sentence, at the exact moment
it presents his words as the reason to stay*. In his bucket
system this lands worse than noise: it reads as the system
quoting him carelessly, and a man who feels misquoted stops
listening to the quote. The one-line cap was F-9's cure for
*app names* (correct — names are labels). It was applied to
the intention by slot symmetry, and the intention is not a
label; it is the only sentence on the face he authored.

*Correction D-F3 (SMALL):* H2 wraps to **2 lines** (slot
24dp → 44dp, card heights 418 → 438 / 292 → 312, RETURN
addresses shift once, at frame one, invariant within the
visit — the skeleton law is untouched). Ellipsis only after
the second line. If Tor refuses the height, the fallback is
not one line: it is auto-shrink to a 13sp floor before any
ellipsis is permitted. Either way the rule to file is:
**the door never cuts the man's own sentence before it cuts
its own** — and today its own price gets two lines (§5.6 of
the skeleton sizes Z2 at 34dp for exactly that wrap) while
his gets one. That allocation, read cold, is the fixed
face's priorities printed in dp. Reverse it.

**D-F4: bare is honest and, at 1 AM, unattributed.** Killing
`ACTIVE INTENTION:` was right — the prefix was a form label,
bureaucracy clearing its throat before his words could speak.
But stripped of any mark, his intention at 16sp in the same
cream as everything else is visually *indistinguishable from
system copy*. Half-asleep, he may not recognise the sentence
as his — and an unrecognised intention does no work; it is
just a fourth line of door-speech (worsening §6). The cure is
not the prefix back. It is a typographic signature: his words
are the face's only **non-caps, sentence-case, regular-weight
human sentence** if D-F8 (§6) is adopted — in a face of
Anonymous Pro badges and capped notices, one line set like a
person wrote it *is* the attribution, and it costs no label,
no slot, no words.

*Correction D-F4:* set H2 in sentence case exactly as he
typed it (no `uppercase()` transform anywhere on this slot —
verify none is inherited), Atkinson Regular, and let D-F8's
case discipline do the attribution. If his stored intention
is itself all-caps, print it verbatim anyway: verbatim is
the attribution.

**D-F5: `SESSION ENDS {HH:mm}` is the face's most useful
fact set in its least readable clothes.** H3 line 1: 12.5sp,
α0.92, third slot down. For the decision he is actually
making — *is this worth it, tonight* — the end instant is
the load-bearing fact, and the Baseline rightly forbids the
one form he could use without thinking (a remaining
duration). What remains is an instant he must subtract from
a now he must first look up, at 1 AM, in 12.5sp. I do not
propose breaking Law 1; Halbwach's "8 MIN LEFT" was struck
and stays struck. I file the cost plainly so it is a *chosen*
cost, not an invisible one: **the door tells this truth in
the one dialect its visitor cannot speak fluently.** The
mitigations available inside the law are size and company:
the instant deserves H2-adjacent weight, not body tier.

*Correction D-F5 (SMALL):* H3 line 1 to **14sp** (slot block
40dp → 44dp, absorbed with D-F3's re-measure in one pass),
and — only if Ren rules in Halbwach's companion from the
audit's Ren's Calls — the companion (`THE FEED WILL STILL BE
THERE AT {HH:mm}`) is the true feel-cure and should take
line 2 *in preference to* the break appointment (D-F6):
it speaks to the craving's actual lie ("now or never") in
derived, duration-free words, while the appointment speaks
to his calendar. If Ren rules neither, line 2 stays empty
and the emptiness is preferable to a second system sentence.

---

## 3. Second 1 → 5/10 — The wait: where the waiting room survives

This is the longest beat of the visit and the one the fix
changed least *experientially*, for all it changed
structurally. RETURN is live, still, tappable — acquitted,
six seats, and felt: a man who didn't mean it is gone inside
two seconds and never meets anything in this section. What
follows is only for the man who did mean it — Blank's second
man, the one the old face made wait in a room. The fixed face
makes him wait in a *better-appointed* room. Itemised:

**What stands in front of him while he waits** (offer branch,
COUNTING state, skeleton §8): gate line `PEEK OPENS IN
{M:SS}` 15sp Anonymous Pro Bold over a 2dp rule draining
in accent; below it in Z2, from frame one, the price:
`WHEN THE PEEK ENDS, A 20 MINUTES COOLDOWN STARTS ON THIS
APP` (caps as specced, two lines); below that in Z3, if
applicable, the memory: `{ORDINAL} TIME THIS SESSION`,
10sp, letter-spaced; and, on PEEK doors with a scheduled
break, H3 line 2 already printed the appointment:
`BREAK AT {HH:mm} OPENS THIS APP`.

**D-F6: the wait has three futures in it, and all three
point away from RETURN.** Count the temporal offers the
counting face makes: (i) a peek in 5–10 seconds, (ii) a
break at a printed instant, (iii) a session end at a printed
instant. Every dated thing on the face is a thing he can
*wait for or want*, and the only undated thing is RETURN —
which asks him to act now, unrewarded, against three clocks
all running in his favour if he just stands here. The
break-appointment kernel (P-3's kernel, SHIPS in FIX-8) is
truthful and, on the Deck, kind. *On this face, during the
wall*, it is a second temptation with a later timestamp:
it teaches him the visit can be waited out profitably in
two currencies. A man who learns "standing at the door
earns things" will stand at the door.

*Correction D-F6 (SMALL):* the appointment line prints
**at offer** (when the wall expires and Z1 crossfades to the
chip), not from frame one — it is information for the man
who is about to choose between lease and return, not a
companion for the wait itself. It cannot print earlier
without functioning as a lure; it loses nothing by arriving
with the choice it informs. (Innovator P-3c's stronger
wording — "NO PEEK SPENT" — compounds the lure and should
not be adopted on this face whatever Tor rules on 3a/3b:
a line that prices the free option against the lease is a
comparison staged at the craving second, against the spirit
of Funke's endorsed never-list even where its letter —
mid-craving comparisons between *sessions/days* — is not
literally breached.)

**D-F7: he watches the peek come toward him. The gate
counts *down to the reward*.** Demoting the 88sp numeral to
a captioned 15sp gate line (Conflicts 1) is a genuine cure
for the old obscenity — a hero countdown that gated only
surrender while pretending to gate the room. But feel
honesty requires naming what survives the demotion: a
ticking `0:05 → 0:00` over a rule that *drains in the
accent colour* is still, second by second, an approach
display. His attention — the scarcest thing he owns tonight
— is parked by design on the one slot that will contain the
peek, watching it ripen. The rule's metaphor makes it
worse: draining reads as *emptying toward release*, the
sand running out *for* him. Nobody designed the wait to
return his attention to RETURN; RETURN simply sits below,
static, saying nothing, losing an attentional contest to
the only moving thing on the face. **Movement is argument,
and the only argument moving is the peek's.**

*Corrections D-F7 (SMALL, pick at least the first):*
(a) The draining rule fills in **cream** (`NightColorPrimary`
α0.92), not accent — the skeleton spends the face's only
accent fill on RETURN's slab and then spends accent *again*
on the temptation's progress bar, the one place the accent
budget (skeleton §2: "the accent buys RETURN alone") is
quietly broken by its own spec. Take the accent off the
gate; a grey-cream rule still drains truthfully.
(b) The gate figures update on the anchored derivation
(FIX-5's cure — right), but the *rule* need not animate
per-frame: step it per second with the figures. A smooth
drain is a progress bar, seductive; a stepped rule is a
clock, factual. Clocks bore; bars beckon. On this face,
boring is a virtue (see §7).
(c) If Tor wants one stronger cut: freeze the gate line's
figures for the first half of the wall and show only the
rule — no. Filed and withdrawn in the same breath: a gate
that hides its derived number is a vagueness the Baseline
would rightly call a lie of omission. (a)+(b) stand.

**D-F8 (the lecture charge, stated once, here, because the
wait is where it accumulates): the fixed face speaks
entirely in caps, and caps are a notice board.** Session
line, gate line, price (two lines), withheld lines, memory
line, exit line, badge — every system sentence on the face
is uppercase; his intention is the only sentence-case text,
and only by accident of storage. Uppercase at 12.5sp across
two lines is not "statement rank"; at 1 AM it is *posted
regulations*. The content of each line is honest — the
audit purged every lie — but honesty in a shouting register
is still a lecture, and a man being lectured by a door
stops hearing a door and starts hearing an institution he
can resent, argue with, or wait out. Resentment is a
craving's best ally: it converts "I reached" into "it
lectured me again," and the second story is one he can
win by taking the peek.

*Correction D-F8 (SMALL, strings/typography only):* system
**body** lines (H3 session + appointment, Z2 price, Z1
withheld lines, X exit) set in sentence case, Atkinson, as
ordinary sentences — `"When the peek ends, a 20 minutes
cooldown starts on this app."` — while the machine's true
badges stay machine register (gate line, memory line,
footer badge: Anonymous Pro, caps, letterspaced — they are
instruments, and instruments may look like instruments).
One face, two voices, each in its place: the *instrument*
ticks in caps; the *sentences* speak at room volume. This
single change does more against the lecture than any
wording edit on the list, and it breaks no truth law: every
derivation, ceiling, and instant is untouched.

**D-F9: the price, standing from frame one, is read as a
threat during the wait and as a price only at the offer.**
I-2 (price before temptation) ships and I do not re-open
it — a price revealed *after* the chip arms would be a trap,
and the audit is right. But note the dual life the sentence
now leads: for 5–10 seconds it is the longest text on the
face, a future conditional about a punishment-adjacent
consequence ("a cooldown starts on this app" — *on this
app*, the machine's territory marked), read by a man who
has not yet been offered anything. It functions, during
COUNTING, as the door's opening argument against him —
delivered before he has spoken. At OFFER the identical
sentence becomes fair dealing. Same words, two weathers.
The correction is not timing (forbidden by the acquittal)
and not wording (FIX-8's re-timing of the clause is correct
and kept). It is D-F8's register cut plus Z2's demotion:
sentence case, and Z2 seated *below* Z3's memory slot in
visual weight — no. Simpler and sufficient: sentence case
(D-F8) and 12sp (from 12.5sp) for Z2 alone, so that during
the wait the face's reading order is name → his words →
gate → slab, with the price present, legible, and plainly
the fine print it honestly is. Fine print that shouts is a
threat; fine print that speaks is a term.

**D-F10: the memory line scolds, whatever its intentions.**
`THIRD TIME THIS SESSION` — Funke's never-list, endorsed by
the audit, permits "counts it can prove," and the count is
provable (Innovator P-4b derives it honestly from the
session-window ledger; the fabrication risk is cured). I
attack nothing in its truth. I file what it *does*: an
ordinal, letterspaced, in the gate line's family, standing
from frame one for the whole wait, is experienced by its
subject as a tally kept *on him* — the door keeping score
of his returns in the tone of a register being read aloud.
The taper already *acts* on the count (wall 5+n, cap 10);
the print adds no information he can use — he cannot act
on being third — it only witnesses him to himself, at the
hour he is least able to bear witness neutrally. The audit
struck P-5's witness line as "a score in witness clothing"
and then shipped the count line, which is the same garment
in a smaller size.

*Correction D-F10 (SMALL):* make the count **explain the
gate instead of witnessing the man** — print it only when
it is doing work, i.e. when the taper has actually lengthened
this wall beyond the base 5 (`priorIntercepts ≥ 1`), and
bind it syntactically to the gate it justifies, one line,
instrument register: `PEEK OPENS IN 0:08 — THIRD TIME THIS
SESSION` is still two facts; the gallery form is the count
as the gate's *reason*, set as a subordinate second line in
Z3 in **sentence case at body tier**: `"Third time this
session — the wait is longer."` Hmm — "the wait is longer"
states the rule, which FIX-8 already demands ("never silent
about its rule") and which the bare ordinal does *not*:
today a returning man sees a longer number and a tally, and
must infer the connection himself, at 1 AM. Print the rule,
in a sentence, once, attached to the count that triggers it
— that is information; the naked ordinal is testimony. On a
first return (wall = 6) the line earns its slot; the count
alone never did. If Tor holds the audit's exact string, at
minimum adopt the gating: no print when the wall is the
base 5 and the ordinal would be `FIRST`/`SECOND` theatre
with no rule to explain. Silence where the count changes
nothing is the Baseline's own discipline (Law 4's spirit)
applied to memory.

**The wait, summed:** after D-F6/F7/F8/F10 the counting face
says, in order: a name, his sentence, an end instant, a
cream stepped rule with a small ticking gate, a slab. That
is a door with a lock turning. As specced today it says
six things and three of them argue. The wait's length is
not the problem — five honest seconds is a breath; ten, on
a third return, is a *priced* breath, if the price is
explained (D-F10) rather than tallied.

---

## 4. Second 5/10 — The offer, or the refusal

### 4a. The offer arms

**What happens.** One recomposition: gate stack crossfades
(180 ms, in place, Z1's bounds unmoved) into the PEEK chip
— 48dp, neutral `NightColorSecondaryContainer` fill, label
`PEEK` + derived honest badge (`2m`, `1m30s` — FIX-8's cure
of the truncating badge), EMERGENCY dead and gone.

**What he feels.** The death of EMERGENCY PEEK is the
offer's great mercy (§8): the lease no longer borrows the
weekly valve's gravity, and the chip's plainness tells the
truth about the act's size. But plainness has a feel cost
the spec did not price: after a watched wait (D-F7), the
reward arrives as a **grey slab**. The emotional shape is
bait-and-diminish — tension built by a draining accent
rule (pre-D-F7a), discharged into the face's dullest
surface. Two bad sequels follow. The man who takes it
feels faintly cheated by the smallness, and carries that
cheat into the peek itself ("this is all?") — a peek that
starts as disappointment lasts its full lease and ends
in a fresh reach. The man who *doesn't* take it has still
been made to wait for a grey chip, and the wait retroactively
reads as the door stalling him for nothing — the waiting
room again, now with a anticlimax at the end of it.

*Correction D-F11 (SMALL):* the chip keeps its neutral fill
and its verb — but on arming, and only on arming, it receives
**one** arrival beat: the seal recipe at door pitch already
specified for arrival (FIX-7c), replayed at half gain
(0.30) as a single latch tick, plus the crossfade the spec
already has. Sound, not colour: the offer *announces itself
to the ear* so his eyes need not have been watching the
gate for it (which also loosens D-F7's attentional trap —
a man who can hear the offer arrive is free to look at
RETURN during the wait). No fill change, no accent, no new
voice in the engine's vocabulary.

**D-F12: a tap is too little body for a lease (endorsing
Innovator P-3a, with a feel rider).** P-3a's 600 ms
hold-to-take is, in feel terms, the offer beat the fixed
design lacks: RETURN = a tap (free), PEEK = a held turn
(priced in his own hand), END = a 60-second valve (grave)
is the first *grammar of weights* the door has ever had,
and a man reads weights in his thumb faster than sentences
in his eyes. I endorse it for Tor's ruling with one rider
from this seat: the hold's microcopy `HOLD TO TAKE THE
PEEK` must be sentence case per D-F8 and must live *inside*
Z2's slot only while the offer stands, replacing nothing —
the price stays standing beneath it; a hold that hides the
price at the moment of taking would un-ship I-2 at the
exact second it matters. If Tor strikes P-3a, D-F12 falls
with it and the tap stands — recorded here as acceptable,
not equal.

### 4b. The refusal stands (held doors)

**What happens.** Withheld lines print from second 0 (FIX-6,
SECOND 0 — a known refusal is never withheld; right, §8):
`PEEK COOLDOWN — AGAIN IN {n} MIN` (derived per
recomposition from the stored end, FIX-5; `AGAIN IN 0 MIN`
unrepresentable, expiry re-derives the offer in place),
`A PEEK IS ALREADY RUNNING`, `PEEK IS OFF`.

**What he feels.** Honesty immediate is better than honesty
delayed — the old face made him serve the full wall before
learning there was nothing behind it, which was the single
cruellest timing in the shipped door. The fixed timing is
right. The *voice* is not:

- **D-F13: `PEEK IS OFF` is a slammed door in four words.**
  It states a configuration as if it were a verdict, offers
  no shape of the world in which it differs, and — uniquely
  on this face — it is a refusal with **no fact in it**: no
  instant, no derivation, nothing for his mind to set down.
  The other two withheld lines carry their reason (a running
  lease, a cooling interval with an end the machinery can
  prove). This one carries only the system's preference,
  in caps, at body tier. In his buckets: pure accusation's
  cousin — *denial*. Correction (SMALL, wording, for Hale/
  Tor): the line is the settings fact and should wear the
  settings' own name for the act — if the toggle that
  governs it has a user-facing label anywhere in the tree,
  the withheld line quotes that label's state verbatim
  rather than inventing a door-register paraphrase; and
  under D-F8 it prints in sentence case: `"Peek is off."`
  A full stop is doing real work in that string: it ends
  the sentence the way a fact ends, instead of a notice
  trailing into the room's silence. (If the audit's Hale
  register requires caps for this line, the full-stop point
  survives on its own.)
- **D-F14: the cooldown line counts in minutes what he
  feels in seconds — correctly, and coldly.** Ceiling
  minutes are the Baseline's form and the arithmetic is
  acquitted; I file only that `AGAIN IN 20 MIN`, standing
  alone in a 110dp zone (skeleton §3.3: withheld held at
  full zone height, content top-aligned in Z1, the rest
  "honest empty card"), leaves **62dp of vacancy** beneath
  the refusal (see §7 on the void). The refusal is one
  line; the room it stands in is three. A refused man
  staring at an empty zone experiences the emptiness as
  the door having nothing more to say to him — which is
  true, and reads as contempt. Correction: on the held
  branch, Z2 (empty-held today) carries the **session line's
  repetition is forbidden** (no duplicated prints) — what
  it may carry, truthfully and without new machinery, is
  nothing; so instead: **vertically centre the withheld
  line within the full 110dp Z** rather than top-aligning
  it in Z1, on held visits only. Same slot, same bounds,
  same stationarity; the refusal stands in the middle of
  its room like a statement on a door, not a sticky note
  in a warehouse. (The skeleton's §5.5 resolution crossfade
  is unaffected: the chip fades into Z1's bounds from a
  centred line — a 24dp internal drift of *content* during
  a crossfade, bounds of every slot unmoved; if Schliff
  judges even this drift a breach, top-alignment stands
  and D-F14 is recorded as priced-and-refused, not hidden.)

---

## 5. The exit — three ways out, three weathers

**RETURN (the way back).** 56dp slab, the face's only accent
fill, label ink #471602 at 5.37:1 (F-3's cure of the old
2.55:1 cream-on-vermilion — the most important word on the
face was its least legible; now its most). Live from frame
one, never dimmed, never re-labelled. **This is the fixed
door's moral centre and it is right** (§8). Two feel notes,
neither a charge against its existence:

- *D-F15:* the label `RETURN TO FOCUS` is an imperative in
  the system's voice, and "focus" is the system's noun for
  what he was doing — his word for it, at 1 AM, is the
  intention in H2, or sleep, or the mock. The door commands
  him back to an abstraction while offering him a concrete
  thing one slot above. I do **not** propose rewording the
  app's established verb (cross-face consistency binds; the
  Disarm face's `KEEP FOCUSING` is its pair). I file the
  asymmetry once: the peek is named by what he *gets*
  (a peek, a badge, a price), the return is named by what
  the *system* gets. If a future wording pass ever opens
  this face's verbs, the return's label is where the man's
  own vocabulary (his intention, verbatim, is already on
  the face) should be consulted first. No price; a note
  for Hale's book.
- The exit fade (260 ms, specA, skeleton frozen) is right:
  he is not waved off, spun, or congratulated. Which leads
  to the one beat after RETURN that nobody specced —
  **what he returns *to*.** The door vanishes onto the
  session's own face, mid-state, with no acknowledgement
  that a visit occurred (the ledger books `RETURNED AT
  DOOR` — FIX-3c, the honest outcome — but no surface ever
  speaks it back to him, and rightly: a congratulation
  would be a streak in a party hat). Silence after return
  is correct. Filed as correct, deliberately: a feel pass
  that demands a reward beat here has not understood the
  Baseline. The absence *is* the respect.

**END THE SESSION INSTEAD (the honest exit).** FIX-8,
Halbwach I-4: one quiet line under RETURN, 36dp target,
routing to the Disarm face; a route, not a spend. Its
*existence* is the fixed design's bravest sentence — a door
that shows its own override stops being resented as a jailer
(§8). Its *wording and threshold* carry two risks:

- *D-F16:* `INSTEAD` presumes a comparison he may not be
  making — it frames the tap as choosing the session's death
  *over* the return, in the door's voice, at the door's
  suggestion. For the man who came to end it (they exist:
  the session was armed in anger, or the night has genuinely
  broken), the line is a relief. For the craving man, the
  line *introduces* an option his craving had not yet
  priced — the door itself proposes escalation, quietly,
  in footer type. The audit weighed this and shipped it;
  I record the feel price so the ship is knowing: every
  visit now contains a standing suggestion to end the
  session, authored by the app. Correction is register,
  not removal (SMALL): sentence case per D-F8 —
  `"End the session instead"` reads as a door on the room's
  wall; the caps version reads as a third notice. Keep the
  word `instead`; in sentence case it describes, in caps
  it argues.
- *D-F17:* the exit promises a door and delivers a wall —
  honestly, but without warning. He taps a *text line* and
  arrives at a 60-second hold valve and a consequence
  sentence. The gravity lives on the valve's face (the
  audit's stated design: "the valve's own face carries the
  gravity") — and Innovator P-2's wiring (door stays standing
  beneath the Disarm card; KEEP FOCUSING peels back to this
  door, untouched) makes the round trip safe and unbooked,
  which I endorse as the feel-correct mechanics: he can
  *look* at ending without being processed by looking.
  What neither the audit nor P-2 gives him is any signal,
  on *this* face, that the exit has a price of its own —
  so the tap feels costless and the valve feels like a
  bait-switch, and bait-switch is a trust injury even when
  every individual face told the truth. Correction
  (SMALL): no price text is added (a price on the exit
  line would make it a spend, against the ruling) — the
  signal is *kinetic*: the exit line's tap uses the Disarm
  face's own entry (its `specC` today) rather than any
  fade shared with RETURN, so the handoff *feels* heavier
  than leaving by the slab from the first frame. Different
  doors should sound different in the hand; today the spec
  gives the exit RETURN's weather. (If P-2 ships, its 2a
  invariant — never a frame with no door standing — is the
  load-bearing half of this correction and must not be
  traded away for a cleaner transition.)

**TAKING THE PEEK (the way through).** Covered in §4a
(D-F11/F12). One exit-side note: on grant, the skeleton
freezes and fades and the pill takes over — a lease whose
life is another face's audit. From *this* face's side, the
honesty is complete: price stood before the act, badge
equals lease, cooldown clock starts when the sentence said
it starts (at the peek's end — FIX-8's re-timing is the
offer's quiet masterpiece; §8). A refused grant leaves the
door standing with no error print and no apology — the
skeleton calls this "the acquitted machinery's silent
refusal," and feel concurs: arithmetic does not owe him a
speech. Correct as specced.

---

## 6. Cross-cutting — the talk ratio (the lecture, measured)

Stack the fixed face's speech by author, offer branch, at
the moment the wall expires:

| Author | Lines | Words (approx.) |
|---|---|---|
| The system | session line, break appointment, gate line, price (2 lines), memory line, footer badge | ~30 |
| The man | his intention (1 line, truncated — D-F3) | ≤ 8 visible |

Six system sentences to one (cut) sentence of his. Every
system sentence is true — the audit saw to that, and truth
was the audit's whole commission. **But a face can be
entirely truthful and still be a lecture, because a lecture
is not a falsehood; it is a ratio.** The old face lectured
with falsehoods mixed in (a fabricated reason, a wrong
clock, a siren word). The fixed face lectures with facts
alone, in caps, simultaneously, for the whole wait. My
corrections D-F2 (sequence the ink), D-F6 (appointment at
offer), D-F8 (sentences at room volume), D-F9 (price as
fine print), D-F10 (count only when it explains) together
bring the counting face to: name, his sentence, one instant,
one ticking instrument — and the rest arriving with the
choice it belongs to. No ruling is broken in that
reduction; what is removed is *simultaneity and volume*,
never content. Tor: if you rule nothing else from this
file, rule D-F8 and D-F6; they are the lecture.

---

## 7. Cross-cutting — the void, and boredom as a material

Two honest emptinesses in the fixed design deserve one
paragraph each, because at 1 AM emptiness is not neutral —
a craving abhors a vacuum and furnishes it with the feed.

**The 110dp held zone (§4b, D-F14)** and **the ground below
a top-anchored 292dp never-owed card**: on a LOCKED/guard/
site visit the card is 292dp at the top of a tall Night
field; below it, hundreds of dp of #171412 with nothing in
it, for as long as he stands there. For never-owed doors
there is no wall, no offer, nothing to wait *for* — and yet
the composition leaves him in a tall empty room with a
small card, which reads, bodily, as *the antechamber of
something*, and antechambers invite waiting. These visits
should feel the *shortest* in the app: name, his words,
RETURN, exit, done. D-F1 (bottom anchor) cures most of it —
a bottom-anchored short card leaves the void *above*, where
his gaze isn't, and puts the card in the hand's country.
If D-F1 is refused, the fallback for never-owed visits
only: centre the 292dp card vertically (stationarity is
within-visit; branch is known at frame one) so the void is
split into two margins and reads as a *card on a ground*,
the app's ordinary grammar, instead of a notice pinned at
the top of an empty hall.

**Boredom, defended.** One thing the fixed face gets right
that a gallery pass might be tempted to "improve": during
the wait, *nothing entertains*. No breathing orb, no
particle transmutation (the dead wires die in FIX-3b —
and with them the last of the shipped face's craving-second
theatre), no ambient motion except a draining rule. That
flatness is load-bearing. The wait is supposed to be
slightly boring; boredom is the one sensation that makes
RETURN's slab look interesting by comparison. Every
correction above was checked against this: D-F7b steps the
rule precisely to make it *more* clock and *less* show.
Any future "delight" proposal for the counting state
should be refused at this paragraph.

---

## 8. What the fixed design gets right — plainly, no hedging

A feel pass that only complains is worthless; the brief
says so and it is true. These are right, and several are
right in ways the audit itself under-praised:

1. **RETURN live, still, and first-class from frame one.**
   Six seats acquitted it; feel *celebrates* it. The old
   face's RETURN jumped 55dp onto the peek at the moment
   of decision — a physical betrayal. The fixed RETURN
   never moves, never dims, never waits for permission.
   A man who didn't mean it is home in under two seconds
   and the door has cost him nothing but a knock of sound.
   This alone converts Blank's first man from "helped,
   despite" to simply helped.
2. **The arrival seam (FIX-7).** BACK before launch, the
   blocked app gone before the door speaks, 180 ms not
   420 ms, a sound with a body in it. The door now arrives
   like something that was *posted*, not something that
   *popped up*. (§1.)
3. **The numeral's death.** An 88sp countdown was the old
   face's confession that it thought of itself as a timer
   the man must serve. A 15sp captioned gate line that
   names only what it gates — the peek, never the stay —
   is the truth at the size the truth deserves. (§3,
   D-F7 qualifies its colour and motion, not its rank.)
4. **EMERGENCY PEEK's death.** The face no longer screams
   a siren word for a two-minute lease while the real
   emergency (the weekly valve) sits one face away at
   footer volume. Proportion is honesty you can hear.
   (§4a, §5.)
5. **Price before the act, re-timed to the machine's real
   anchor (FIX-8).** "When the peek ends, a cooldown
   starts" is the rare sentence that got *more* honest and
   *more* fair in the same edit. Nobody is ambushed by
   this door's arithmetic anymore.
6. **A known refusal prints at second 0.** The old timing
   (serve the wall, *then* learn) was cruelty by schedule.
   FIX-6's SECOND 0 rule is mercy by schedule. (§4b.)
7. **The ledger stops inventing him (FIX-3).** UNSTATED
   unless stated, RETURNED AT DOOR for what was observed,
   blank for blank. He will never read this face's
   bookkeeping — and that is exactly why it matters to
   feel: a door that lies *about* him in its diary is a
   door whose politeness is theatre. This one's manners
   are now load-bearing.
8. **The honest exit exists.** A held door that prints
   its own override, unhidden, under its main verb, has
   given up the jailer's last pleasure. Men stop rattling
   doors that show them the way out. (§5, D-F16/F17 are
   about its voice, never its presence.)
9. **Night, kept.** Conflicts 3 ruled correctly: the door
   matches the sealed building it locks. At 1 AM this face
   does not wound his eyes, and the F-3/F-5/F-10 contrast
   cures answer the daylight evidence without surrendering
   the night. The 5.37:1 RETURN label in particular: the
   most important word is now the most legible. That is
   care, in numbers.

---

## 9. Truth-law compliance of every correction above

Held Door Truth Baseline, checked per correction — no
correction may print, imply, or stage what the machinery
cannot prove at the frame it prints:

- **No remaining durations:** none proposed. D-F5 enlarges
  an *instant*; Halbwach's struck duration rewrite stays
  struck; the gate line's `M:SS` is the audit's own specced
  gate print (a wall the door itself enforces and anchors),
  untouched in derivation.
- **No predictions:** none. D-F6 *delays* a derived
  appointment print to the offer moment; the appointment
  remains the session schedule's own fact. The companion
  line in D-F5 is flagged Ren's-call, exactly as the audit
  left it, and is derived from the same end instant.
- **No streaks:** D-F10 moves *away* from streak-register
  — the session count prints only as the explanation of a
  lengthened gate, with its rule stated; no day counts, no
  consecutive anything, no history beyond this session's
  booked intercepts of this package (P-4b's honest source).
- **No mid-craving comparisons:** D-F6 explicitly refuses
  P-3c's "NO PEEK SPENT" framing for this face; nothing
  above prices one option against another, ranks tonight
  against other nights, or prints a "usually/typically."
- **Derived, never literal:** every number in every
  proposed string (gate, count, instant, badge, price)
  keeps its FIX-5/FIX-8 derivation. D-F7b changes an
  animation's stepping, not a value. D-F11 changes a gain,
  not a fact.
- **Silence where nothing is owed:** D-F10's gating and
  §7's never-owed treatment both *extend* Law 4's spirit;
  no correction adds a print to a door that does not owe
  one. The never-owed face gains no new line from this file.

---

## 10. Charge sheet for Tor (rule each, or strike with reason)

| # | Charge (one line) | Correction | Price |
|---|---|---|---|
| D-F1 | Card top-anchored; reading + RETURN live far from thumb/eyes at 1 AM | Bottom-anchor, same stack, same invariance tests | SMALL |
| D-F2 | Whole skeleton's ink arrives in one instant; no sequence to the greeting | Ink stagger: small speech +180 ms in place, name + RETURN at frame one | SMALL |
| D-F3 | His intention ellipsised at 1 line while the price gets 2 | H2 wraps to 2 lines (+20dp) or 13sp shrink floor before ellipsis | SMALL |
| D-F4 | Bare intention is unattributed at 1 AM; reads as system copy | Sentence case verbatim + D-F8's two-voice discipline as the attribution | SMALL |
| D-F5 | `SESSION ENDS {HH:mm}` — most useful fact, least readable tier | H3 line 1 to 14sp; companion line (Ren's call) preferred over appointment in line 2 | SMALL |
| D-F6 | Break appointment stands during the wait = a second, later temptation | Appointment prints at offer, not from frame one; refuse P-3c's "NO PEEK SPENT" on this face | SMALL |
| D-F7 | Gate's accent draining rule is an approach display for the reward | (a) Rule drains in cream, accent buys RETURN alone; (b) rule steps per second with the figures | SMALL |
| D-F8 | Every system sentence in caps = posted regulations; honest content, lecturing register | Body lines to sentence case; badges/gate/memory stay instrument caps | SMALL |
| D-F9 | Price during COUNTING reads as threat, at OFFER as terms — same words, two weathers | D-F8 register + Z2 to 12sp (present, legible, fine print that speaks) | SMALL |
| D-F10 | Memory ordinal witnesses the man; explains nothing he can act on | Print only when taper lengthened the wall, bound to the gate, rule stated in sentence case | SMALL |
| D-F11 | Offer arrives as a grey slab after a watched wait; eyes must have been watching | Half-gain latch tick at arming (existing FIX-7c voice); no colour change | SMALL |
| D-F12 | A tap is too little body for a lease (endorses Innovator P-3a) | 600 ms hold-to-take; microcopy in Z2, price stays standing during the hold | SMALL–MED (P-3a's price) |
| D-F13 | `PEEK IS OFF` — a refusal with no fact in it, in notice register | Quote the governing toggle's own state label verbatim; sentence case, full stop | SMALL |
| D-F14 | Held refusal top-aligned in a 110dp zone; 62dp of vacancy reads as contempt | Centre the withheld line within Z on held visits; bounds unchanged | SMALL |
| D-F15 | `RETURN TO FOCUS` names the system's noun, not his (note for Hale, no change proposed) | Recorded only | — |
| D-F16 | `END THE SESSION INSTEAD` in caps argues; in sentence case it describes | Sentence case per D-F8; word and placement kept | SMALL |
| D-F17 | Exit tap feels costless; the valve then feels like a bait-switch | Exit handoff takes the Disarm face's own heavier entry; adopt P-2's never-no-door-standing invariant | SMALL |

Unpriced remainder, stated once: every dp added by D-F3/D-F5
must be re-measured into the skeleton's §3.4 arithmetic in a
single pass and the stationarity assertions (§11 of the
skeleton) re-pinned to the new addresses — the invariance,
not the addresses, is the law this file never trades.

**The visit, after all corrections, in one breath:** the app
vanishes, a low knock, and at the bottom of the glass a name
he recognises and his own sentence, whole; underneath, when
his session ends, in type he can read without waking fully;
a small cream rule stepping down beside a quiet gate; a
price in the voice of a term, not a threat; a reason, not a
tally, if his wait is longer tonight; then a tick he can
hear, a plain chip he must hold for a heartbeat to take, a
slab that never moved waiting the whole time, and under it,
in a sentence, the way to end it all if ending is the truth.
That is a door a man can be *met* at. The fixed design built
nine-tenths of it; this file is the last tenth, and the last
tenth is the difference between being processed and being met.

— DRUCK, Feel Specialist 5, Tor's cell, issue #11.
Report to Tor; nobody compiled; nothing here messages the user.

## NEXT (Law 10)

(a) **What I want to do next:** walk the *Disarm* face the
same way, as the same man five seconds later — he arrives
there through D-F17's handoff carrying this door's weather
with him, and nobody has yet read the valve's 60 seconds as
an emotional arc (the audit's own rider already converges
three seats on that face wanting its own audit seat). The
Held Door's exit and the Disarm face's entry are one beat,
and it is currently owned by no one.

(b) **What I could do, unassigned:** a register sweep —
D-F8's two-voice discipline (instruments in caps, sentences
at room volume) applied cold to the Deck, the pill, and the
shield notification (rider C-13's surface) — filed as a
one-page voice table for Hale's wording book, so the next
face is not lectured into existence one audit at a time.
