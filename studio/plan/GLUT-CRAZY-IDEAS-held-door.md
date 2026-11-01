# GLUT — CRAZY IDEAS FOR THE HELD DOOR (issue #11)

Author: Glut, crazy-ideas seat (dedicated-100 programme), working flat
under Ren, feeding Tor's #11 cell. Filed Oct 30, 2026.
Status: IDEAS ONLY. Nothing here is implemented, compiled, or ruled.
Every idea carries a build sketch + rough price so the cell can kill
it on cost, not on vibes. Kill freely — that is what I am for.

## WHERE I START FROM

The audit's verdict stands as my floor: the face today is "a wall
with good manners." Sarthak's ruling pushes further: the block
screen should be a launcher-like surface you cannot exit, with a
separate screen for allowed apps and peek apps. So the question I
was seated to answer is not "how do we decorate the intercept?" —
it is: **what is this door at its peak, when it stops apologising
for existing?**

My answer, in one line: **the door is not a screen in the app. For
the length of a session, the door IS the phone.** Everything below
is a way of making that true — the door as a place you live in,
the hold as an ordeal your body pays, the peek as a physical
object, the exit as a ceremony.

Laws I did not break while going crazy (the cell does not have to
re-check these): RETURN is never gated and never moves. Family
calls never touch this door. A blocked death stays silent — no
idea below adds sound, print, or ceremony to a refusal. No green
anywhere. No AI-generated images — every visual below is type,
geometry, or deterministic rendering. Every number an idea prints
is derived from an anchored end or a booked count, never invented.
No remaining-duration prints (Baseline law 1) — where an idea
brushes against that law, I flag the collision myself.

---

## CLUSTER A — THE DOOR AS A PLACE

### A1. THE VESTIBULE (the launcher, taken seriously)
Stop thinking "block screen with allowed apps listed." The door
becomes a **vestibule**: a small room you step into every time you
reach for anything during a session. The room has exactly three
walls:
- **The wall you came for** — the held app, present as a sealed
  door with its name as a nameplate. Not greyed out. Present,
  heavy, shut.
- **The open doors** — allowed apps, rendered as actual open
  doorways (light spilling from the frame, one tap walks through).
- **The service hatch** — peek apps, behind a hatch with the lease
  price printed on the hatch itself.
HOME during a session lands in the vestibule. There is no other
home. That is what "un-exitable launcher" means when you mean it:
not a screen you can't leave, a *room that is the whole building*
until the session ends.
*Build sketch:* the door face becomes the session's home surface;
allowed/peek split into the two side surfaces Sarthak already
ruled (separate screen for allowed + peek). Intercept = arriving
in the vestibule facing the sealed door you knocked on.
*Price:* LARGE — this is the Leaf recomposition grown to full
scope. It swallows FIX-6's skeleton whole. ~1 face rewritten +
2 new surfaces, VM mostly reused (offer matrix, leases, taper all
already derive correctly).

### A2. FROST GLASS
Blocked apps are not icons with a badge. They are shapes behind
**frosted glass** — recognisable, unreachable, blurred by a frost
layer that is *thicker the more times you have knocked tonight*.
First knock: light mist. Fifth knock: deep frost, only a smear of
the app's colour. The frost never melts during the session (that
would print a countdown by other means — law 1 collision avoided
by design, not by luck). It clears only when the session ends.
The metaphor does the talking a lecture never could: *the more
you press against it, the less of it you can even see.*
*Build sketch:* deterministic procedural frost (noise density =
f(booked intercepts of this package this session) — the same
count the taper already books). No image assets.
*Price:* MED — one custom draw layer + the count already exists.

### A3. THE PLATFORM
The door as a **station platform at night**. One departure board.
One line on it: the next break, at its derived instant, and which
apps the break opens (the errand kernel, FIX-8, promoted from a
sub-line to the room's whole furniture). You are not being denied;
you are *early for a train that is definitely coming*. Under the
board, a bench — RETURN sits on the bench. Nothing else is on the
platform. No ads, no posters, no noise. The boredom is the design:
a platform is a place built entirely out of waiting, and it makes
waiting feel like a normal human activity instead of a punishment.
*Build sketch:* re-skin of the existing face around the break
instant; sessions with no scheduled break print NO SET DEPARTURE
and the bench only (silence where nothing is owed — existing law).
*Price:* SMALL–MED — composition + print changes only, machinery
untouched.

### A4. THE NAMEPLATE IS YOURS
On a real door, the nameplate says who lives there. On this door,
the nameplate slot — directly under the held app's name — carries
**the intention in your own words**, set like engraved brass:
not a label, not "ACTIVE INTENTION:", just your sentence, cut
into the door you are standing in front of. (FIX-6 already moves
the intention to this slot; I am proposing we treat it
*typographically as engraving* — letter-spaced, inset shadow, the
one warm material on a night face.) The door stops being the app's
property and becomes yours: you are not reading a warning, you are
reading your own handwriting on your own door.
*Price:* SMALL — pure type treatment on an existing slot.

---

## CLUSTER B — THE HOLD AS A PHYSICAL ORDEAL

The 60-second emergency hold already banks at 15/30/45. Good
bones. But a finger resting on glass is not an ordeal — it is a
waiting room with a thumb. These make the body pay, honestly,
in ways that are over when they are over.

### B1. THE STILLNESS ORDEAL (my strongest single idea)
The emergency hold stops being a press and becomes **stillness**:
put the phone down, face up, and keep it perfectly still for 60
seconds. The gyroscope is the referee. Banking continues only
while the device is still; a tremor, a grab, a table-bump
**pauses** banking — never resets it (reset is cruelty theatre;
pause is physics). At 15/30/45 the banked marks land with the
seal-voice at door pitch. Pick the phone up early and the door
simply tells the truth: HELD 0:37 OF 1:00 — BANKED 30 — and
RETURN, exactly where it always is.
Why it works: you cannot scroll, tap, or fidget your way through
it. Your hands are *empty*. The craving has nothing to hold onto
because *you* are holding nothing. It turns the emergency exit
into the one genuinely meditative object in the app.
*Build sketch:* SensorManager rotation-vector / accel variance
threshold, sampled in the existing hold state machine; banking
logic unchanged, its *input* changes from press-state to
stillness-state. Needs a non-sensor fallback (accessibility /
no-gyro devices): the classic press-hold remains the fallback,
chosen automatically, never as a settings quiz.
*Price:* MED — ~150–250 LOC + one on-device feel calibration.
Risk: threshold tuning is everything; too tight is rage, too
loose is a lie. Budget one phone-proof pass.

### B2. THE WEIGHT (haptic strain)
If the hold stays a press, make the press *heavy*. Across the 60
seconds, the haptic envelope under the thumb ramps like strain:
a low, slow pulse at 0s that deepens and quickens slightly as the
banked marks pass — the door feels heavier the longer you push,
the way a real deadlift gets honest near the top. At 45 the
pulse is a heartbeat — yours, borrowed. Release and the weight
drops instantly: silence, no sound of failure, the number you
banked printed plainly.
*Build sketch:* amplitude envelope on the existing hold loop
(extends wave-2 proposal P-A's ramp from the 600ms chip to the
60s valve). Haptic-only when sound is off, per existing law.
*Price:* SMALL — envelope table + wiring, ~60–90 LOC.

### B3. THE TWO-HAND OATH
The emergency hold requires **two thumbs, opposite corners**,
held simultaneously. Not harder to *want* — impossible to do
absent-mindedly, impossible to do one-handed in a crowd, in a
pocket, half-asleep. It converts an impulse into a posture: both
hands, full attention, a small bow over the phone. Banking marks
unchanged. TalkBack bypass preserved exactly as the single-hold
bypass is today.
*Price:* SMALL — second pointer tracking on the existing gesture;
the ordeal is free because the hardware already counts fingers.

### B4. YOU ARE PUSHING THE APP SHUT
Every hold visual in every blocker fills a bar toward *opening*.
Invert it: during the 60 seconds, you watch the held app's door
**close** — a heavy leaf swinging shut by millimetres under your
thumb, the app's light narrowing to a slit, then to a line, then
gone. The emergency hold still spends at 60 (the valve's law is
machinery, not mine to rewrite) — but the *story your eyes are
told* is that effort closes doors, it does not pry them open.
For RETURN, the same leaf, already shut, with your intention
engraved on it (A4). One object, two directions, zero bars.
*Price:* MED — one animated leaf composition replacing the fill;
state machine untouched.

### B5. THE BREATH LOCK
The hold screen breathes: a single ring that expands for 4,
holds for 4, falls for 4 — and the banking only accrues while
your press stays *on* the ring's surface as it moves. You are not
watching a timer; you are keeping contact with a breathing thing
for 60 seconds. Nobody has ever rage-quit a breathing exercise at
second 40 and felt proud of the craving that made them. If the
press lifts, the ring exhales and waits — no scolding, no reset
of banked marks.
*Price:* MED — gesture-tracking against an animated target;
banking accrual gates on contact, marks unchanged.

---

## CLUSTER C — THE PEEK AS A PHYSICAL OBJECT

### C1. THE BRASS KEY
A granted peek lease mints a **key**: a small brass key rendered
at the top of the peeked app's surface for the lease's whole life.
Not a countdown chip — an object you are *carrying*. When the
lease ends, the key is returned: it slides back into the door's
keyhole on the Held Door face the next time you see it ("THE KEY
CAME BACK" is a homecoming print the code already half-prints as
"THE PEEK ENDED" — give it the object's grammar). Keys are never
counted, never scored, never shown as a collection. A key is
borrowed or it is returned; there is no third state, and that
binary is the whole emotional payload.
*Price:* SMALL–MED — one drawn object + lease-state wiring that
already exists.

### C2. THE PORTHOLE
A peek does not open the app. It opens a **porthole in the door**:
a circular window, and through it the app, live, but framed —
you can look, scroll inside the circle, and the circle is the
lease made visible. In the lease's last stretch the glass fogs
from the rim inward (fog derives from the lease's anchored end —
a *closing*, not a duration print; the cell should check this
against law 1 the way I checked A2 — fog shows an end approaching
without ever printing how long is left, same as a draining rule
already does). When the fog closes, the porthole is a door again.
Why: a peek that feels *looked through* rather than *let into*
never becomes a visit. You cannot move into a porthole.
*Price:* LARGE — real surface compositing/clipping work; the
riskiest build in this file and the one with the highest ceiling.
Flag for Ren's call before anyone specs it.

### C3. THE DRAINED PEEK
The peeked app renders **without its reward colours**: the feed
in bone, ink, and one accent — the app's own lurid palette
desaturated for the lease's duration (per-app filter on the
peeked surface only). The information is all there; the *candy*
is not. A 2-minute peek at a drained feed is a tool; a 2-minute
peek at the full-sugar feed is a relapse with a timer on it.
*Collision, flagged by me:* system-level colour manipulation of
another app's surface may not be reachable from the accessibility
route — if the platform says no, this dies on reachability, not
on merit, and the porthole (C2) inherits its intent.
*Price:* UNKNOWN until a reachability probe exists; probe priced
SMALL, build priced after the probe answers.

### C4. THE PEEK LEAVES A MARK ON THE DOOR, NOT ON YOU
When a peek is taken, the *door* remembers it visibly: the next
time that app is held tonight, its sealed door carries one scuff
— a scratch across the nameplate's corner, drawn, permanent for
the session, cumulative. Not a score. Not a count printed at you.
Wear. The door is an object that has been used, and its wear is
simply true (the count already books itself for the taper; this
only lets the face *show* what the ledger already knows, in
matter instead of numbers). Pairs with A2's frost: this door's
whole memory is physical, never numerical.
*Price:* SMALL — paint layer driven by the existing per-package
session count.

---

## CLUSTER D — THE HONEST EXIT, AS THEATRE

The audit shipped the honest exit as one quiet footer line:
"END THE SESSION INSTEAD." Correct, and deliberately small. Here
is what it grows into when it is allowed to be a *ceremony* —
because leaving a sealed session SHOULD feel like leaving a
building, not like dismissing a dialog.

### D1. THE STAIRWELL
Ending a session early is a **descent**: three landings, each
printing one true thing derived from the session's own record —
the seal's start instant, the intention in your words, the next
session already scheduled (or NO NEXT SESSION SCHEDULED, printed
just as plainly). One step per landing, taken by a deliberate
press, no timers, no "are you sure." At the bottom: daylight —
the phone is a phone again. The DisarmChallengeOverlay already
carries the gravity machinery; the stairwell is its *architecture*.
*Price:* MED — recomposition of the existing disarm flow into
three fixed frames; no new state, no new spends.

### D2. THE REGISTER
At the bottom of the stairwell sits a **register book** — the
Drawer, opened to tonight's page, with tonight's line already
ruled and waiting. Ending early writes the line in front of you:
the instant, the held count, the word ENDED. You watch it being
written. No reason is asked (the audit struck the survey; I am
not resurrecting it) — the register records *that*, never *why*.
A man who has watched his own handwriting enter a permanent book
treats the next seal differently. That is the entire mechanism:
witnessed permanence.
*Price:* SMALL–MED — the Drawer and its booking exist; this is a
choreographed reveal at the exit seam.

### D3. THE DOOR CLOSES BEHIND YOU (session end, both kinds)
Whether a session ends on schedule or by the valve, the last
thing the app does is show the door **closing and going quiet** —
the leaf from B4, the seal-voice at its lowest, then the
vestibule dissolves into the ordinary launcher. No summary, no
grade, no total (the app is a blocker, not a tracker — totals
live in YPT/GMC, never here). Just a door, closed, and the day
on the other side of it. Ritual endings are what separate a
machine you *use* from a place you *inhabit* — and Sarthak keeps
the tools whose ritual he respects (the Blockit lesson).
*Price:* SMALL — one exit composition reusing B4's leaf.

---

## CLUSTER E — WILDCARDS (one paragraph each, priced honestly)

**E1. THE MIRROR.** During the emergency hold only, the door's
centre is a mirror (front camera preview, on-device, never
recorded, never recognised, never stored — a preview surface and
nothing else). Sixty seconds of looking at the face that is about
to break the seal. Most cravings cannot survive being *watched by
their owner*. Price: MED + a permission story that must be
airtight; if the permission smells wrong, kill it — the idea is
worth less than the trust. **E2. YOUR OWN VOICE.** At arm time —
calm, on the Deck, never at the door — you may record the
intention in your actual voice. At the door it plays back once,
quietly, before any offer arms. Nobody argues with a recording of
themselves being serious. Price: MED (capture at arm, playback at
door, storage in the app's own vault). **E3. THE BELL MARKS.**
Replace the gate line's numerals during the taper wall with three
bell-strokes at the banked marks — sound carries the count so the
eyes can stay on the intention. Numerals remain in the price
lines where money-truth lives. Price: SMALL. **E4. THE SECOND
KNOCK.** If the same app is knocked again within a short window
after a RETURN (derived from booked intercept instants), the door
opens *already honest*: no wall, no wait — it prints "SECOND
KNOCK" and puts the honest exit at full rank beside RETURN,
because a man on his second knock in a minute is not browsing,
he is negotiating, and the door should negotiate with the exit,
not the peek. Price: SMALL — one derivation over existing
bookings + a rank swap in the fixed skeleton (stationarity law
untouched: the exit row already exists in both frames). **E5.
GRAVITY.** In the vestibule (A1), held apps are *below*: the room
is drawn in a slight perspective with the sealed doors on the
lower wall, allowed doors at eye level — you literally look down
at what you came for and across at what is open. Subtle, drawn,
no assets. Price: SMALL on top of A1, impossible without it.

---

## MY OWN RANKING (so the cell knows where I'd spend first)

1. **B1 Stillness** — the only idea here that changes what the
   craving *is*, not what it looks at.
2. **A1 Vestibule** — Sarthak's launcher ruling with its chest
   out; the Leaf was the sketch, this is the building.
3. **D1+D2 Stairwell/Register** — the exit is where resentment is
   born; make it the most beautiful room in the app.
4. **C1 Brass Key** — cheapest object with real emotional mass.
5. **A2 Frost** — the door's memory, in matter instead of numbers.
Kill C3 if the probe says unreachable; park C2 until Ren rules;
E1 lives or dies on its permission story.

— Glut, crazy-ideas
