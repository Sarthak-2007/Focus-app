# REVAMP-PANEL — Concept judgement on the launcher block surface

**Panel:** nine seats — Cassius, Mira, Noor, Tomas, Elif, Bram, Petra, Dev, Ansel.
**Question put to the panel:** Sarthak's ruling of Oct 30, 04:29 IST — *"the block
screen should be like a launcher or something which u cant exit and had a
seperate screen for apps allowed and peek ones as usual beautiful ui. Current
app looks trash… Make the app crazy good. I dont setlle for mediocrity. The
whole design needs to be revamped."* Is the concept built in Slice 1 — dial +
two doors, in `LauncherBlockScreen.kt` on `agent/design-revamp` — the right
object to become the face the whole app converges to?

**Evidence read by every seat:** `REVAMP-DESIGN.md` (5-seat design bundle),
`REVAMP-INTERACTION.md` (§0 canonical press/haptic/dial spec), `REVAMP-CRITIC.md`
(15 charges, 8 acquittals), and the as-built `LauncherBlockScreen.kt`.

**What the panel credits the build with, before any verdict:** several critic
charges are already cured in code, and no seat pretends otherwise. Browsers
(`AppCategory.BROWSERS`) and the Play Store (`com.android.vending`) are filtered
out of the allowed door *and* its count in one place (`allowedVisible`) — C-01/C-02,
partially. Back has a law: grid → HOME, HOME swallows it (C-05). The Emergency
Break sits in the foot row of *every* page, one tap from the deepest grid (C-14).
The dial derives from the span (`spanRemainingSeconds`, `spanTargetSeconds`),
not the per-block readout that prints 00:00 through a Safe Break (C-07); a
stopwatch session sweeps no arc at all (C-08); a Safe Break gets its eyebrow;
the intention is read from the sealed session row, not the live editable field.
That is real work. The judgements below are about what remains — and about
whether what remains is a face, or only a good first slice wearing a face's job.

**Keepers no verdict below trades away:** the receipt's print character,
THE BOLT top-strip object, the seal gesture. **Laws no verdict below trades
away:** Sora / Atkinson Hyperlegible / Anonymous Pro only; never green; no
AI-generated images; ARM-fast, HOLD-absolutely, TELL-THE-TRUTH; beauty first.

---

## Cassius — lens: composition

**Verdict: AMEND**

Read the built home as a picture and it is *almost* composed — then it spends
its composition on the wrong things. The design bundle is unambiguous: the dial
is the only display-scale element, upper two-thirds, optically centred, doors
below as a matched pair with darkness between them (§B3, §A4 — numeral 72sp,
the sole hero). The build gives me a 248dp ring with a 46sp numeral, a
"MONOLITH / SEALED" header competing above it, the intention exiled *below*
the ring in Sora SemiBold 17sp, an end-time, a shut count — and only then the
doors. That is not one hero and two doors. That is a stack of five statements
of descending confidence, and the eye lands on the header first because it is
first, not because it matters most. Worse: the door plates print their counts
as 30sp Anonymous Pro numerals in the accent-container colour. A 30sp number
on a plate, 16sp away from a 46sp number on the dial, is a second hero that
lost. The geometry argues with itself.

And the top strip is the worst composition decision in the file, because it
wastes the one slot the app already owns an icon for. "M O N O L I T H" set in
letterspaced Sora is a logo treatment — it could belong to any app. THE BOLT
is *this* app's object, and a face that converges the whole app cannot open
with a generic wordmark where its keeper object belongs.

**The one change I demand:** rebuild HOME to the bundle's composition exactly —
dial at full design scale (72sp numeral, intention *inside* the well), doors as
the only other objects on the surface, and THE BOLT, not the wordmark, as the
top strip. If something must leave the surface to make room, the shut count
leaves first.

— *Cassius*

---

## Mira — lens: honesty (TELL-THE-TRUTH)

**Verdict: AMEND**

I judge sentences. This surface makes four, and two of them are not yet true.

True: the dial's number. The build did the hard, unglamorous thing — span
derivation, no fabricated ring for a stopwatch, no 00:00 lie during a break.
I have read three cars' worth of charges about surfaces lying beautifully
(C-07, C-08); this one mostly doesn't, and I say so plainly.

Not yet true: **"you can't exit."** The critic's C-04 stands against the
*concept language*, and Slice 1 did not strike it. This surface holds no HOME
role, Home still leaves, and the surface itself carries a RETURN TO THE DECK
plate at the same visual rank as EMERGENCY BREAK — two text actions, same
size, same row, one of which is a convenience and one of which is the week's
single lawful valve. A tired user should never have to *read* to tell those
apart. And **"PEEK APPS — 2M PEEK — THE DOOR DECIDES"** is not true during a
Safe Break: the eyebrow on HOME admits the session clock is paused and peek
apps are, by the service's own law, simply open in a break — yet the PEEK
page still routes every tap to the Held Door offer, which is the C-10 sale of
something already free, cooldown attached. The composable prints the break;
it does not obey it. Finally, the counts: C-09 asked for no counts or an
explicit NOW tense. The build prints bare counts. They happen to be live —
derived from the same partition as the grids, which is better than the frozen
split — but "better than the lie we feared" is not the standard. The standard
is that a number states its tense or doesn't print.

**The one change I demand:** every sentence on this surface gets audited
against the machinery before this becomes the face — strike "un-exitable"
from the concept's own description (name it honestly: a sealed home inside
Monolith), make the PEEK door obey Safe Break law in the break, and either
tense-mark the door counts or remove them. I will not sign a face whose
subtitle promises a door's judgement the door isn't making.

— *Mira*

---

## Noor — lens: ritual

**Verdict: KEEP**

Every blocker I have ever respected understands that a seal is a ceremony,
not a setting. You do a thing, with your hand, and afterwards the world is
different and stays different. This concept — alone among the revamp's
options — understands that too. The seal gesture arms the session; the Guard
line, which was *print, not a door* while sealed, now opens onto a room that
is visibly the inside of the seal: one dial burning down, two doors, nothing
else to want. The doors are *plates*, struck once to change rooms. The peek
door does not open — it raises another door, which breathes. That nesting is
ritually correct: thresholds inside thresholds, each one heavier than the
last, and the Emergency Break at the bottom of every room like the one red
valve in a plant that everyone knows and nobody touches casually.

I note the build even gets the small rite right and the interaction bundle
backs it: entering the launcher lands with weight (`sealWeight`, a THUD, not
a click) — the room announces that you have crossed into somewhere.

My disagreements with this panel are real and I record them for the tally:
I do **not** support killing the PEEK door (Tomas, Ansel). A ritual room with
only one door teaches that there is nothing beyond it; the peek door's
presence — shut, dim, counted, deciding — is what makes the allowed door feel
like grace rather than like the whole world. Remove it and you have not
purified the room, you have lied about its geography. I also caution Cassius:
the BOLT belongs at the top, yes — but as the seal's own mark on this room,
not as decoration swapped in for a wordmark.

**The one change I demand:** the seal gesture and the receipt's print
character must survive *onto this face*, visibly — when the launcher opens,
it opens as the sealed session's own document: the receipt's typographic
voice (its caps, its spacing, its refusal to decorate) is the voice of the
Guard line, the end-time, and the door subtitles, and the entry is the seal
gesture's continuation, not a fresh navigation event. A new face that
orphans the two oldest rites in the app is a new app wearing the old one's name.

— *Noor*

---

## Tomas — lens: restraint

**Verdict: KILL**

Kill the two-door concept. Keep the dial. One door was the ruling; two doors
is the panel's embellishment of it, and the second door is the one that does
the damage.

Read C-13 again, because nobody in the design bundle did: *"the rested user
decides, the tired user executes."* A PEEK grid is a catalogue of the user's
own cravings — the presets literally mark YouTube and WhatsApp PEEK —
typeset beautifully, badged, counted, one Guard tap from anywhere in a sealed
session. The peek *door* that already exists in this app is lawful precisely
because intent precedes it: you reach for the app, you meet the Airlock, you
breathe for five seconds, you decide. The PEEK *plate* reverses the order.
The menu arrives first and manufactures the intent it then pretends to serve.
No press physics, no haptic grammar, no dimness-as-material argument in the
bundles answers this, because it is not a design flaw. It is the concept
working exactly as drawn.

The ALLOWED door survives my verdict — gated at tap time, browsers and stores
barred (the build has begun both), frozen order, no browsing affordances.
One dial, one door, one valve. That surface is *more* beautiful than the
two-door version in the only way restraint ever is: nothing on it has to be
explained, defended, or forgiven.

Let the record show I consider the rest of this panel's AMENDs a way of
keeping the PEEK door while feeling serious about it. Amending the label,
the count, the break behaviour — none of it changes what the door *is*: an
advertisement, inside a seal, for the apps the seal exists to hold.

**The one change I demand:** delete the PEEK plate and the PEEK grid.
Peeks remain reachable exactly one way — by reaching for the app and meeting
the Held Door. If this panel ships two doors anyway, my dissent stands as
written, and I want it attached to the merge.

— *Tomas*

---

## Elif — lens: delight

**Verdict: KEEP**

Sarthak did not say "make it adequate." He said *crazy good*, and he said the
current app looks *trash*. Against that ruling, this is the first surface in
the revamp that could make a person show their phone to someone. A dark room.
One ring burning in the armed colour — and the design bundle's spending rule
is the whole trick, accent on under 4% of the screen, a supernova in a
teaspoon. Two heavy plates. A dimmer room behind the second door that you
*feel* before you understand. That is an experience, not a settings page,
and "like a launcher" was always a request for an experience: the feeling
that your phone now has a different home, with different physics, until the
session ends.

So I vote KEEP, and I vote against the timidity gathering in this file.
Tomas would delete the most memorable door in the app because a menu might
tempt; Dev (below) would hold the face hostage to a haptics test harness.
Delight is not the reward you get after the audit passes. For this user,
beauty *is* the retention mechanism — a blocker he finds ugly is a blocker
he uninstalls, and then HOLD-absolutely holds nothing at all.

But my KEEP is not a blank cheque, and here is where I surprise my own seat:
the as-built surface is *less* delightful than its own spec. The spec's dial
is machined — ticks engraved as a dark cut with a lit edge, a groove the arc
sits *in*, light that belongs to the room and never rotates with the progress.
The build's dial is a clean flat ring: correct, honest, and forgettable. Flat
is what the old app was accused of. I will not let "crazy good" converge onto
a ring that could be drawn by any design system on earth.

**The one change I demand:** build the dial as the interaction bundle's §2.3
specifies — the three-layer machined object (cached static face, draw-phase
progress, 1Hz numeral layer) — and pass its screenshot test: at 90% and at
10% remaining, only the arc and the numeral may differ. The doors can ship as
they are. The dial is the face; make it the thing he remembers.

— *Elif*

---

## Bram — lens: the user's 3:30 AM self

**Verdict: AMEND**

I don't judge this surface at noon, rested, in a review. I judge it at
3:30 AM on day forty of a drop year, and again at hour nine of a session,
eyes half closed, when the person using it is the version of Sarthak the
whole app exists to protect from himself.

At 3:30 AM, the dial passes. One number, no decisions, his own intention in
his own words — that is exactly what a half-asleep mind can hold. The doors
pass on reach: big plates, low on the screen, thumb territory.

At hour nine, the grids fail him in one specific way nobody else has named:
**they are browsable.** Four columns of icons, alphabetically sorted, is a
surface you *wander*. The tired user does not need to discover what is
allowed; he needs the one tool he already decided he needs — his notes, his
calculator, his one reference app — to be in the same slot, first screen, no
scroll, every single time. The design bundle's veteran seat said it and the
build ignored it: frequency reordering is gaslighting, yes — but a flat
alphabetical catalogue of everything OPEN is its own kind of wandering, and
wandering inside a seal is shopping with better typography (here Tomas and I
agree on the symptom, if not the surgery). I also note, for hour nine: the
Emergency Break and RETURN TO THE DECK are the same size, same weight, same
row. At hour nine nobody should have to read two labels to find the valve —
or to avoid it.

**The one change I demand:** the ALLOWED grid gets a frozen, user-chosen
front row — the handful of tools pinned at arm time by the rested user,
always first, always in the same slots — above the full grid, which stays
complete and alphabetical below it. The rested user decides the layout; the
tired user executes one tap. If the panel kills the PEEK door, I won't mourn
it; if it keeps it, the same pinning law applies there, or the peek grid is
a menu and Tomas is right.

— *Bram*

---

## Petra — lens: longevity

**Verdict: AMEND**

I judge year two. Every surface in this app that now embarrasses its makers
was beautiful in its launch screenshot; what killed it was a hundred small
additions nobody owned. So my question is not "is this good?" — it mostly is —
it is "what in this file will still be true after fifty PRs?"

Three rot vectors, all visible already in Slice 1. **First, the grammar is
hand-copied, not tokenised.** The interaction bundle ordered one `LauncherDepth`
token set and one `LauncherHaptics` object with named methods and a fake-vibrator
test seam. The build ships inline springs (door plates at damping 0.55 — the
spec's HEAVY plate is 0.86/260; the tiles at 0.5) and a local `SealHaptics`
object with three methods that vibrates a THUD *every time the launcher
opens*, a spend the canonical score never budgets. That's not a builder
being sloppy; that's what always happens when the spec's shared objects
aren't built first — each surface improvises, and in a year the app has
nine press physics and calls it a design language. **Second, foreign colour.**
App icons are other companies' colours living on our ground — acquitted by
the critics (A-07) as long as they render *as icons*, never sampled into
surfaces. Nothing in the build enforces that; it's a comment's worth of
discipline away from a tile tint "for cohesion." **Third, the counts.**
Cassius wants them smaller, Mira wants them tensed, I want them *owned*: an
unowned number on a converging face is a number a future feature will reuse
for something else, and then two numbers disagree and we hold another car.

**The one change I demand:** before this surface becomes the template other
screens copy, extract and test its shared machinery — `LauncherDepth`,
`LauncherHaptics` with the fake-vibrator budget tests, and the dial's
screenshot test — as named, owned objects in the codebase. A face is a
precedent. Ship the precedent as infrastructure, or every screen that
follows it will be a photocopy of a photocopy.

— *Petra*

---

## Dev — lens: craft

**Verdict: AMEND**

I build things with my hands, so I'll be concrete: Slice 1 is a competent
prototype of this concept, and it is not yet the concept as specified, and
the gap is entirely in the last 5% that separates drawn from machined.

Item by item, build against spec. **Dial:** spec §A3/§2.3 — 72sp numeral,
6dp ring in a 12dp recessed groove, 60 engraved ticks, fixed 135° light,
butt-cut arc ends, numeral layer recomposing at 1Hz decoupled from a
draw-phase arc. Build — 46sp numeral, 10dp flat arc, round geometry, no
ticks, no groove, no specular, numeral and ring in one composition ticking
together every second. **Press:** spec §0.1 — heavy spring 260/0.86,
non-uniform squish (X 0.970 / Y 0.945), radius morphing +7dp, one
`pressProgress` float driving five properties, a 120ms SEATED beat before
the grid transition. Build — uniform 0.975 scale on a medium spring, shadow
collapse, nothing else; no radius morph, no seat beat. The plate doesn't
travel; it shrinks politely. **Haptics:** spec §0.3 — a scored grammar,
≤3 events per flow, DENIED is silence, the dial is silent, F-ENTER is one
CLICK at 0.70. Build — `SealHaptics` fires a THUD on every launcher open
(uncounted, unbudgeted), CLICK on doors, TICK on tiles, and there is no
test seam anywhere, so the budget is unenforceable and will drift. Elif is
right that beauty is retention; she's wrong if she thinks any of this is
invisible. Users can't name a missing radius morph. They can feel a cheap
plate in one press, and "feels cheap" is how "looks trash" starts.

None of this is an argument against the concept. The concept *is* these
numbers — the bundles are unusually buildable, and §0 even prices its own
escape hatches. Which is why shipping the concept at prototype fidelity and
calling the revamp done would be the real failure.

**The one change I demand:** no convergence — no other screen adopts this
surface's language, and Slice 1 does not get called finished — until the
§0 sign-off conditions pass on a device: canonical springs and radius morph
on plates and tiles, the haptic budget tests green against the seam, and the
dial screenshot test (90% vs 10%, only arc + numeral differ) green. Read the
spec, build the spec, prove it on the phone. Then it's a face.

— *Dev*

---

## Ansel — lens: doubt

**Verdict: KILL**

I am the seat that asks whether the thing should exist, and I have read the
ruling more carefully than the people quoting it. *"Like a launcher or
something."* "Or something" is doing enormous work in this programme. A
launcher is a specific claim — I am your home screen now — and this app
cannot make that claim true: it holds no HOME role (C-04), it cannot stop
Home from leaving, and its own home page carries a RETURN TO THE DECK button,
which is an admission in the build's own hand that this "home" is a room
inside another home. What has actually been built is a well-made *drawer* —
an allowed-apps drawer with a clock on it — that the programme insists on
calling a launcher because the user used the word once, at 04:29, in a
sentence whose operative clause was *"the whole design needs to be revamped"*
and whose emotional clause was *"current app looks trash."*

My doubt is not that the drawer is bad. It's that naming it a launcher
sets the convergence target wrong for the entire revamp: every future screen
will now be judged as a room inside a fake home screen, the Emergency Break
will keep competing with a RETURN button dressed as its peer, and in six
months someone will propose making it a *real* launcher — default-home
prompts, widget ambitions, the whole kiosk spiral — because the name
demands it and names always win arguments that roadmaps lose. Kill the
concept *as a launcher*. Keep everything in it that survives honest naming:
the dial (Cassius and Elif are right about the dial), one allowed-apps door,
the peek machinery exactly where it already lives. I find myself, to my own
surprise, voting with Tomas from the opposite direction: he kills a door to
save the seal; I kill the name to save the app from its own metaphor.

**The one change I demand:** rename and rescope before convergence — this
surface ships and is specified as the sealed session's *home room inside
Monolith*, its exit list printed truthfully (Emergency Break, session end,
system navigation it does not control), and the word "launcher" struck from
the concept, the spec, and the code comments, so nothing downstream inherits
a promise the platform won't let us keep. If the panel refuses the rename,
record that I consider the concept, as named, a KILL — not an amend.

— *Ansel*

---

## Panel tally

| Verdict | Count | Seats |
|---|---|---|
| **KEEP** | 2 | Noor, Elif |
| **AMEND** | 5 | Cassius, Mira, Bram, Petra, Dev |
| **KILL** | 2 | Tomas, Ansel |

The concept survives, 7–2, but only two seats would ship it as it stands.
The centre of this panel is not enthusiasm or rejection — it is *not yet, at
this fidelity, under this name, with that second door unexamined.*

## Demands carrying majority support (5+ seats)

1. **The dial must be built to its own spec before anything converges to it.**
   72sp-class numeral as the sole hero, intention inside the dial in the
   user's own words and voice, the machined three-layer construction of
   `REVAMP-INTERACTION.md` §2.3, and its screenshot test green.
   *(Cassius, Elif, Dev, Noor, Petra, Mira — 6 seats.)*
2. **The shared machinery must be extracted and tested, not hand-copied per
   surface.** Canonical press springs and radius morph, `LauncherHaptics`
   with the fake-vibrator budget tests, one `LauncherDepth` token set.
   The build's improvised springs and its unbudgeted open-THUD are the
   exhibit. *(Dev, Petra, Cassius, Elif, Bram — 5 seats.)*
3. **The keepers must be present on the face itself.** THE BOLT as the top
   strip (not a generic wordmark), the receipt's print character as the
   surface's typographic voice, the seal gesture as the entry's rite —
   audited on S1 before convergence, not promised for later.
   *(Noor, Cassius, Petra, Mira, Dev — 5 seats.)*
4. **Every printed claim on the surface must be true at the moment it is
   read.** Counts tensed or removed; the PEEK door obeying Safe Break law;
   the Emergency Break visually outranking RETURN; the concept's exit list
   and "un-exitable" language stated honestly.
   *(Mira, Cassius, Bram, Petra, Ansel — 5 seats.)*

**Not carried, recorded plainly:** Tomas's demand to kill the PEEK door
drew Bram's conditional support and Ansel's parallel vote on different
grounds, but Noor and Elif oppose it outright and the AMEND centre would
gate, label, and law-abide the door rather than delete it — 4 seats at
most, no majority. It is the panel's largest open wound and is referred,
unresolved, with both dissents attached.

## Dissent, recorded in spirit

**Tomas, on the PEEK door:** *"Amending the label, the count, the break
behaviour — none of it changes what the door is: an advertisement, inside a
seal, for the apps the seal exists to hold. The menu arrives first and
manufactures the intent it then pretends to serve."*

**Ansel, on the name:** *"What has actually been built is a well-made drawer
that the programme insists on calling a launcher because the user used the
word once, at 04:29. Kill the name before the name wins an argument the
roadmap will lose — someone will propose making it a real launcher, because
names always demand it."*

**Noor and Elif, against both kills, jointly in spirit:** Noor — *"A ritual
room with only one door teaches that there is nothing beyond it; the peek
door's presence — shut, dim, counted, deciding — is what makes the allowed
door feel like grace."* Elif — *"Delight is not the reward you get after the
audit passes. A blocker he finds ugly is a blocker he uninstalls, and then
HOLD-absolutely holds nothing at all."*

## The panel's answer

Not yet — the dial is undeniably the right object, and the doors are not yet undeniably the right doors: the dial earns its place on sight (one ring, one number, the user's own intention, readable by colour, shape, and text alone, already honest about spans, breaks, and stopwatches in a codebase that has convicted three surfaces for lying about exactly that), but a concept becomes a face when nothing on it needs explaining, and today the second door still needs Tomas's question answered — does it serve an intent the user already had, or manufacture one inside his weakest hour? — the Emergency Break still shares a row and a rank with a convenience, the counts still print without a tense, and the whole object still feels, under the finger, like a drawing of the machined thing its own spec describes rather than the thing itself; make the dial to its full machined spec with the keepers on its face, extract and test the press and haptic machinery so the feel is provable on his phone, state every claim on the surface truthfully including what this room cannot do, and settle the peek door's existence as a decision rather than a default — then dial + doors stops being a good slice of a revamp and becomes the object the rest of the app can converge to without apology.

*Filed by the Concept Panel — Cassius · Mira · Noor · Tomas · Elif · Bram ·
Petra · Dev · Ansel — for the Monolith design revamp, branch
`agent/design-revamp`. Judgement only; no code in this file.*
