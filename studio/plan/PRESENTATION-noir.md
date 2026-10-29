# PRESENTATION — NOIR
## The night city, at the level of information
*Noir — Designers, night-city lens. Phase 2 + Charter v3, Oct 29, 2026.*
*Phase 1 was paint. This is the presentation: what is shown, how it is
used, how a hand chooses — and, per Charter v3, what it feels like,
what was accidental, and what I have invented. Everything below is
audited against the shipped code (trunk as merged through batch 7,
in my lane `builds/noir`), file and line where it matters.*

---

## 0. The claim, restated

Phase 1 claimed NOIR matches the hour he arms it. At the level of
information the claim sharpens: **Monolith is used in the dark, by a
man half-awake, to close a city.** Every fact the app presents is a
sign in that city. A sign can be doing exactly three things:

- **LIT** — the fact is live, armed, selected, or true right now.
- **UNLIT** — the sign exists; its fact is off, unselected, or not
  yet. The letterforms are still there in the dark. An unlit sign is
  *readable as form, silent as light* — you can see what would burn.
- **ABSENT** — no sign exists, because the fact does not exist.

This is not a metaphor laid over the app; it is the app's own truth
law wearing NOIR's clothes. The imprint already obeys it (over an
unproven wire the box is *empty* — printing WILL SHUT there is the
register's worst lie). The seam stamps already obey it (an unmeasured
state prints nothing). Phase 2 generalises the law to every surface:
**nothing half-lit, nothing greyed into ambiguity, nothing present
that no fact backs, nothing absent that a fact needs.** Grey is how
the old world said "maybe". The city has no maybe.

Three corollaries run through everything below:

1. **One light per decision.** At any moment, exactly one element
   carries the full-burn accent: the thing the hand is about to use.
   Idle Deck: the slide cap. Proving: nothing — the machine is
   working and every sign holds its breath. Running: no accent at
   all; the session needs no decision. A screen where three things
   glow is a screen shouting three orders at a tired man.
2. **Amber is the warning lamp, and there is only one.** Amber
   (`#FFB300` night / deepened `#D97706` day — one hue family, two
   depths for two grounds) appears only where the machine refuses,
   warns, or cannot prove itself: the refusal sentence, the ratchet
   banner, the error slot. It never decorates, never selects, never
   sells. When amber lights, it is the only warm thing in the city
   and the eye goes there the way it goes to a siren.
3. **Print is print.** Words that state facts (the strip, the
   imprint, the receipt, the Drawer's record) are set like print —
   mono, still, unanimated — even in a city of neon. Neon is for
   *state*; print is for *record*. The loved receipt is the proof
   this discipline is beautiful, not merely honest.

**Districts.** The city has four districts with different lighting
laws, and confusing them is how apps get loud: the **Deck** is the
storefront — one hero sign, everything else quiet. **Shield** is the
switch-room — many small signs, all legible, none shouting; it is
visited awake, at leisure, to change the wiring. **Tasks** is the
noticeboard — paper pinned in a row. The **Drawer** is the archive
basement — nothing down there glows; it is all print on stock. The
**desk clock** is the one sign the city never switches off.

---

## 1. (a) WHAT is presented — the Deck audit

Every element on the Deck as shipped, its fact, and its verdict.
"Delivered" means the screen's own signature receives the value
(`TactileDeckScreen.kt`); "printed" means the composition renders it.

| # | Element | Fact it carries | Verdict |
|---|---------|-----------------|---------|
| D1 | Header band: MONOLITH + `GUARD n/4 — <first lack>` | Is the guard whole, and if not, what's missing first | **KEEP.** The lack is named, never counted vaguely. At 3:30 AM this is the only chrome that matters. |
| D2 | Eyebrow: TOTAL / SESSION RUNNING / BREAK · N MIN | Which tense the hero is speaking in | **KEEP + EXTEND** (see D10). The break printing its minutes is the Ellipsis fix and it is right — a break is a fact, so it renders. |
| D3 | Hero: total span / FLOW / running readout | The one number the screen exists for | **KEEP, untouchable.** Control and display are the same sign: the wheel's reading *is* the hero. |
| D4 | The wheel (288dp) + FLOW key cap | The span being chosen; FLOW = no span | **KEEP.** The strait, the detents, the seat — all machinery, all earned. FLOW as a key cap (a verb wearing a verb's body) is the grammar working. |
| D5 | Running progress track | Elapsed fraction of a sealed session | **KEEP the track, KILL its twin** (D6). |
| D6 | The `%` print under the track | The same fraction, as numerals | **KILL.** One fact, one rendering. The track is the gauge; the ENDS time in the strip is the number. The % is a second rendering that slid in because gauges "usually" have readouts. In the city, a gauge is a lit length, not a percentage. |
| D7 | NOW line: NOW + task title (tap: sub-steps) + DONE cap + QUEUE n | The current task; the queue's depth; the tear verb | **KEEP.** DONE wearing a key-cap body (ESR-07) is right. QUEUE n is wayfinding, the queue's only Deck fact — it stays, printed even at 0: an empty queue is a true, quiet fact, and the tap still goes somewhere real. |
| D8 | Sub-steps (01…n, tap toggles, strikethrough) | The current task's anatomy | **KEEP.** Numbered like print, struck like print. DRW-08's "010" bug dies in the truth car; the grammar is innocent. |
| D9 | Running utilities: DESK CLOCK chip, HELD chip + count badge | The two doors a sealed session still opens | **KEEP, PROMOTE the HELD chip's badge.** Mid-session the badge is the only number that grows against him; it is the session's pressure gauge. In NOIR it is the one running-state element allowed the secondary violet light — a doorway sign, lit because the door is real (U2: the digest behind it is fully readable, his confirmed ruling). |
| D10 | **The standing loadout's name** | Which wiring the slide will enforce | **MISSING — ADD (A1).** `selectedProfileName` is delivered to the Deck (signature, :106) and printed *nowhere* (verified: it appears exactly once in the file — in the signature). The imprint can say "10 APPS WILL SHUT" but the Deck never says *whose* ten. At 3:30 AM he arms the FASTED wiring; the screen should name it before he commits to it. Home: the eyebrow row's vacant right side, an unlit sign (mono, muted, right-aligned) — present, quiet, his to check. Prototyped (§9, P3). |
| D11 | **The session's birth** (hand vs routine) | Why a running session exists | **MISSING — ADD (A4).** A routine-born session and a hand-armed one print the identical eyebrow. The receipt knows the difference (WITHIN <routine>); the live screen doesn't. Running eyebrow for a routine-born session: `ROUTINE · <NAME>`. The row carries `routineId` today; the name lookup is the priced cost (§8). |
| D12 | Proof strip: BLOCK · APPS · TIME + seam stamps | Will it shut / how many / when does it end; is the bind held, and what's the proof | **KEEP, every cell.** This is the most honest object in the app — each cell can print UNKNOWN and the stamps can print nothing. It is the city's inspection plate and it does not change. |
| D13 | TIME cell: ENDS HH:mm / NO END / INVALID | The forward fact | **KEEP.** At the arm moment this is the second-most-important print on the screen (see §3). It is *derived*, never stored — the city's clock is a function, not a memory. |
| D14 | The slide + hint `SLIDE OR TAP TO START` / `TAP AGAIN TO UNDO` | The master switch and its instruction | **KEEP, exactly as shipped — and a correction to my own phase-1 record.** My phase-1 letter filed the night hint as "near-invisible". Re-examined at native resolution for this audit (crops filed in my lane, `shots-own/view/slide_night_zoom.jpg`), the hint is crisp white mono on the black track — perfectly legible in both schemes. The phase-1 claim was an artifact of my own 700px downscale, and it is withdrawn here before judging repeats it. What survives is a *positive* verdict: 10sp letterspaced mono at full white on black is already the quietest legible instruction the switch could carry — it reads as form first and light second, which is all the "unlit sign" idea ever wanted. No change. |
| D15 | GRACE WINDOW ABORT (10s badge) / EMERGENCY BREAK VALVE (60S HOLD, `n/1 WEEK` badge) | The two exits from a sealed session, priced in the open | **KEEP.** The valve's price is printed on its face — one per week, spent only at the full hold. No other exit exists and none is implied. |
| D16 | The sentence slot | The machine's one-sentence account of a refusal | **KEEP, and it is the amber lamp's home.** Healthy = the slot renders its reserved emptiness — silence as a designed state, not a missing view. |
| D17 | The imprint: N APPS WILL SHUT / N APPS HELD / empty | The seal's count, in the correct tense | **KEEP.** The tense is the truth: future on a live wire, past at the seal, silence when unproven. |
| D18 | **Nine delivered, unprinted parameters** | — | **FOSSILS — NAME THEM.** `intentionText`, `checklist`, `totalFocusedMinutes`, `totalInterceptions`, `completedTasksCount`, `totalQueueMinutes`, `shortsReelsSniperEnabled`, `browserUrlBlockerEnabled`, `selectedProfileName` — each appears exactly once in the screen file: in its signature. Some are dead wiring from dead designs (checklist, the totals — tracker ghosts in a blocker, and `totalQueueMinutes` is a fossil of the minutes world his verdict just killed). Two are live facts with other homes (the generator states — Shield's problem, §2 S9). One is D10. The presentation rule going forward: **a fact is either printed somewhere on purpose or the wiring is cut.** A parameter nobody renders is a promise nobody keeps. |

**Net Deck change:** one kill (D6), two adds (D10, D11), one repair
(D14), one promotion (D9). The storefront stays a storefront — one
hero sign, one switch, an inspection plate, and silence where
nothing is wrong.

---

## 2. (a) WHAT is presented — the Shield audit

Shield is the switch-room. It is visited awake — Sunday-night
wiring, not 3:30 AM panic — so its law is different: *completeness
with calm*. Every protection the machine offers must have an
address here, and every address must tell the truth.

| # | Element | Fact | Verdict |
|---|---------|------|---------|
| S1 | Ratchet warning banner (conditional) | A weakening attempt during a session was refused | **KEEP.** Conditional existence is correct — it appears only when its fact does. Amber lamp territory (§0.2): it should wear the lamp, not the primary container it wears today. |
| S2 | LOADOUT header: `n SHUT • m PEEK` | The wiring's current census | **KEEP.** This count is the receipt of every selection below it; when a loadout lands, this line is how he knows the city re-lit. |
| S3 | The sliding cap: STUDY / WORK / SLEEP | The standing loadout | **KEEP.** His tap chose the cap over the knob, and in sign grammar it is exactly right: a lit window sliding across a street of three signs — one lit, two unlit, the choice readable from across the room. |
| S4 | ROUTINES header: `n ACTIVE` + one-time reset note | How many routines stand | **KEEP.** The reset note is a one-time sign that dismisses itself on first touch of a row — a sign with a lifespan, honestly labelled. |
| S5 | Routine row, collapsed: time column (start over →end), NAME, days · loadout, ON/OFF cap | One routine, whole, in one line | **KEEP.** The time column is the day's spine — every routine reads down one axis. The ON/OFF cap is a verb wearing a verb's body. |
| S6 | Routine editor (expanded): ±30 steppers, loadout cycle chip, day letters M T W T F S S | The routine's editable anatomy | **REPLACE THE GRAMMAR (A6, A7).** The steppers are already condemned by his word (times set directly — Voss's car carries the mechanism; this spec carries its presentation, §4). The day letters commit a real sin: **T appears twice, S appears twice** — a sign system where two different days wear the same face. Expanded editor days are full signs: MON TUE WED THU FRI SAT SUN, lit or unlit. The loadout *cycle* chip is a blind verb — it shows one state and hides the other two; a selection that conceals its alternatives is a dark pattern in miniature. It becomes a miniature cap (three stops, direct set; the direct-set verb is a named gap, §8 A7). |
| S7 | ALL APPS header: `SHOWING n` + search field + filter words | The census of the filtered list; the finding tools | **KEEP.** Filter words are already sign grammar (ink + underline bar = lit word). SHOWING n is honest and live. The search field's placeholder is an instruction, not a fake fact — allowed. |
| S8 | App rows: pip + name + 3-stop sled (SHUT / 2m / OPEN) | One app's tier, and the switch that throws it | **KEEP the sled, LIGHT THE NAME (A2).** Today the name prints at full ink in all three states and only the sled and a 6dp pip carry the state — the *word itself* doesn't know if it's shut. In the city, the app's name is its sign: **SHUT = the sign is off** (name at unlit letterform strength), **OPEN = the sign burns** (full ink), **2m = the sign is lit on a timer** (mid strength + the sled's lit stop). State becomes readable down the whole list at a glance, no sled-reading required. Prototyped (§9, P1). |
| S9 | **Shorts & Reels sniper + browser URL blocker states** | Two standing protections | **HOMELESS — GIVE THEM ADDRESSES (A5).** Both states are delivered into this screen's signature (:72–73) and rendered *nowhere in its body* (grep-verified). Two protections exist, are wired, and have no sign on the screen that owns app policy. (The parent records add that the Shorts toggle elsewhere is memory-only — a state that doesn't even persist has no business being invisible too.) Presentation fix, zero new machinery: a STANDING ORDERS pair under the websites district — two signs, `SHORTS & REELS` and `IN-BROWSER SITES`, each lit or unlit by its real state, each a tap throwing its real switch. |
| S10 | BLOCKED WEBSITES: `n SITES`, rows + REMOVE, add field + ADD cap | The enforced site list (the list edited is the list enforced) | **KEEP.** The add placeholder names an example domain — an instruction, honest. REMOVE is a verb with a body. This district and S9 belong together: sites the browser keeps shut, and the browser-level protections, one street. |
| S11 | **Profiles (signature-only)** | Named loadouts beyond the three | **FOSSIL — CUT OR CROWN.** `profiles`, `selectedProfileName`, `onSelectProfile` arrive and are never rendered; the cap (S3) replaced them in the chrome wave. Either profiles are a real selection (then they need a street) or the cap is the whole truth (then the wiring is a fossil and goes). My ruling as designer: the cap is the truth — three loadouts, one street — and the fossil is named for Ren's cut list. |

**Net Shield change:** two homeless facts housed (S9), one grammar
replaced (S6), one name lit (S8), one banner re-lamped (S1), one
fossil named (S11). The switch-room rule stands: if it protects
him, it has a sign here; if it has no sign here, it doesn't protect
him and the wiring is lying by implication.

---

## 3. (b) HOW each thing is used — the moments

A presentation is not a list of elements; it is what happens at the
moment of use. NOIR has four moments. Everything in §1–§2 earns its
place in one of them.

### Moment 1 — Arming, 03:30 (and again at 05:00, 09:00…)
He is half-awake, in a dark room, one hand on the phone, the other
still in bed. He has maybe twenty seconds of patience and zero
reading comprehension to spare. The Deck must answer three
questions in one glance, in this order, without scroll:

1. **How long?** — the hero. The biggest type in the app, gas-white
   on wet black. The wheel under his thumb answers by feel as much
   as sight (detents every 5 minutes; the strait guards FLOW). The
   display and the control are one sign, so the glance and the act
   are the same event.
2. **When does it end?** — the strip's TIME cell: ENDS 05:00. This
   is the fact that lets him put the phone down. It is print, it is
   derived, it is never wrong by storage.
3. **Will it actually shut things?** — the strip's BLOCK cell and,
   under it, the imprint's future tense: *10 APPS WILL SHUT*. If the
   wire is dead, the sentence slot lights the amber lamp instead
   and the imprint box stands empty. The city never bluffs him at
   the exact moment he is least able to check.

And the new D10 loadout sign answers the zeroth question — *which
wiring am I about to commit?* — before his thumb moves. Everything
else on the Deck is present but subordinate: the NOW line (he set
the task while awake; at 3:30 he only needs to recognise it), QUEUE
(a depth, not a decision), the guard line (a lack named only if
there is one). **The night palette is not a style choice here: the
ground is the room.** A white screen at 3:30 AM is a flashlight in
his face and a beacon to everyone asleep nearby; wet black keeps
the room dark and the phone a private object. This is the core of
NOIR's phase-1 claim, now stated at the level of use: *the app is
darkest exactly when its user is.*

The slide is the moment's single act. One lit element (the cap),
one gesture, one commit coordinate. Nothing else on the screen
competes for the thumb. If he fumbles it, the machinery's jam and
annulment sentences speak in the slot — one sentence, present tense,
no verb offered that isn't real.

### Moment 2 — The glance, mid-session
The phone is across the room (his meditation hour) or face-up at
the desk's edge (study blocks). He looks for one second from two
metres. What survives that distance: the running hero (remaining,
the largest numerals the app owns) and the progress track's lit
length. What correctly does *not* survive: the strip's small print,
the queue, the sub-steps — a glance is not a read, and the design
does not pretend otherwise. If he wants the clock without crossing
the room, the desk clock (§6) carries the same two facts — elapsed,
remaining — on the one always-lit sign. The HELD badge is the
glance's third fact, for the desk case: a single digit-group that
says the world knocked and was held. Nothing animates to attract
him. **A blocker that begs to be looked at has failed its job; the
city is lit for use, not for attention.**

### Moment 3 — The finish
He tears the task (DONE — and per the truth car the booking now
lands at the tap, the tear animation following a fact, not
preceding it), or the session elapses into its detention and the
receipt issues. The finish's job is *proof*, in two senses at once:
the seal's sound at t=0 (audited, witnessed, one voice for hand and
routine arms alike) and then the slip — the city's paper, §6. The
emotional register is exact: no confetti, no score, no streak. The
completion pulse is reserved by law for verified fully-elapsed
sessions. What he gets is a numbered receipt that says what
happened, in print, and a Drawer that keeps it forever. The finish
feels like a stamp coming down — once, heavily, finally (feel
spec, §5).

### Moment 4 — The wiring hour (Shield's moment)
Shield is used awake, deliberately, probably on a Sunday or after
a failure — the hour he re-wires the city. Its presentation law is
therefore inverted from the Deck's: *completeness over glance*.
Every protection has an address (S9's homeless facts were the
scandal of this audit precisely because the wiring hour is when he
comes to check them). Selection here is unhurried and must be
*reversible-looking*: sleds show their three stops at all times,
the cap shows all three names at all times, filter words show the
whole vocabulary. Nothing in the switch-room hides its
alternatives — the opposite of the Deck, where alternatives are
correctly absent. Same city, different district, different light.

---

## 4. (c) HOW selection works — the switch grammar

Every choice in NOIR is a switch being thrown, and every switch
answers in the same frame with light. No selection opens a dialog,
a menu, or a new surface — except the two doors the app already
owns (the airlock's 2-minute peek, the valve's 60-second hold),
which are not selections but *crossings*, and are priced as such.

1. **The wheel — the district clock.** Turning it is winding the
   city's clock forward. Detents click at 5-minute teeth; the
   loaded wheel's inertia (I_rel, the seat springs, the knock that
   scales with block count) means an 8-block span *feels* heavier
   to set than a 1-block span — the hand learns the price of the
   commitment before the eye reads it. FLOW is the one key that
   turns the clock off entirely: a key cap, ink-filled when active,
   because it is a verb. The hero is the wheel's sign: you are not
   setting a value in a form, you are lighting a number on the
   storefront.
2. **The cap — the lit window.** The loadout cap slides a lit frame
   across three standing signs. Its grammar is *position is
   meaning*: the window's address is the choice, and the two unlit
   names stay visible as form — the alternatives are present, dark,
   and one slide away. Applying a loadout re-lights the whole
   switch-room below (§2 S2's census line is the receipt).
3. **The sled — the throw-switch.** An app's tier is thrown, not
   picked: a 260ms Curve-A throw with a state word on the cap.
   Phase 2 adds the name-light (A2): the throw changes the *sign
   itself*, so the result of a selection is readable in the list's
   light pattern, not only in switch positions. Selection feedback
   law: **the thing you changed is the thing that changes light.**
4. **Filter words — lit vocabulary.** A word is selected by being
   lit (ink + underline bar). No pills, no containers: in a city of
   signs, a category is a word that burns or doesn't.
5. **Day signs (new grammar, A6).** In the routine editor, days are
   seven three-letter signs in a row; a tap lights or unlights one.
   The collapsed row still *prints* its summary (DAILY · STUDY) —
   print for record, signs for selection.
6. **The routine window (new grammar, rides Voss's car).** His word
   killed the ±30 steppers: times are set directly, to the minute.
   The presentation of direct setting in a city of signs: the two
   times are the row's two lit numerals — the start sign and the end
   sign — and setting one is *grabbing the sign itself* (tap the
   time, the city's clock-face opens on that numeral — the Deck
   wheel's own grammar at small scale, or a direct numeral entry;
   the mechanism is Voss's car, the presentation law is mine: **you
   set a time by touching the time, never by pressing + or −
   against it from a distance**). The stepper's sin was not
   arithmetic; it was *indirection* — pressing a button thirty
   times to move a fact you could see. Directness is the grammar.
7. **The slide — the master switch.** Dragging the lit cap across
   the track is pulling the district's main lever. The trail behind
   the cap is the street lighting up behind the lever. Commit at
   0.84 is the lever seating into its notch — the knock, the latch,
   then proving's held breath, then the seal. Tap-to-commit is the
   same lever flipped without the drag (lawful, his tap ruled it);
   the tap-mesh's UNDO words are lit because at that instant they
   are the only live verb. When the lever is home, the imprint
   changes tense — WILL SHUT becomes APPS HELD — and that tense
   change, not any animation, is the selection's confirmation.
8. **The valve — the held switch.** The emergency break is a switch
   that must be *held shut against a spring* for 60 continuous
   seconds, banking at 15/30/45 (the resistance steps the register
   prices) and spending only at the full hold. Its presentation is
   its mechanism: nothing about it is a button. In the city it is
   the one switch behind glass — and the glass is time.

**Selection's one forbidden form:** the cycle. Any control that
shows one state and conceals the rest (the routine loadout chip
today) makes the user memorise the city's wiring. A selection the
user cannot fully see is a selection the design hasn't finished.

---

## 5. FEEL — the sensory signature (Charter v3 §1)

Neon that only glows is half a world. NOIR's feel law: **the city
is heavy, quiet, and exact — everything under the thumb has weight,
everything in the ear has a job, and silence is the default state
that sound must earn its way out of.** All feel below orchestrates
machinery the app already owns (TactileSoundEngine, the registered
haptics, the cascade's timings) — where a feel needs a new
primitive, it is priced in §8, not smuggled in here.

**The hand.**
- *The wheel* is the heaviest object in the app, and its weight is
  information: inertia scales with block count (REGISTER: I_rel
  1.00→1.98, seat knock 16ms→24ms @ up to 1.0). Setting 3 hours
  *feels* like moving something that will hold for 3 hours. NOIR
  changes nothing here and says why: the physics already speak the
  city's language — mass as commitment.
- *The sled throw* (260ms, Curve A) is a switch's clack: short,
  damped, final. The cap words ride the throw; the name-light (A2)
  lands with the seat, not before it — light follows the switch's
  arrival, never anticipates it.
- *The slide* is the master lever: brush floor 90ms (a press under
  it commits nothing — a graze is not a decision), the trail's
  resistance through the damped zone, the latch knock 14ms @ 0.85
  at 0.97 of travel — a single mechanical *thock* felt in the
  thumb's pad, not a buzz. NOIR's one haptic law: **knocks, never
  vibrations.** A knock is an event with an edge; a vibration is a
  appliance asking for attention.
- *The valve hold* is feel as price: 60 seconds of continuous
  pressure, banks banking at 15/30/45 with the drain pitches falling
  560 → 470 → 390 Hz (REGISTER) — the switch audibly *sags* as the
  price comes due. If the finger slips, the drain runs and the
  pitch tells him what it cost. Nothing about the valve is
  comfortable, by design and by his verdict.
- *The tear* (task DONE) is 260ms of completion motion + fall — the
  Abriss grammar the studio adopted: a small physical *rip*, then
  the row is gone. The tear pulse sounds at the tap and the booking
  lands at the tap (G-01's cure); the feel and the fact are the
  same instant.

**The ear.** The city's sound law is *one voice per event class,
and most events are silent*:
- **The seal** speaks — the harmonic seal, one voice for hand and
  routine arms, sounding only at t=0 proof (never at the latch,
  never on hope). In NOIR's voicing: a sign's strike — a low
  fundamental with a short harmonic bloom, the sound of a heavy
  switchgear closing in the next room. It is the only sound the app
  makes that he should be able to recognise in his sleep, because
  at 05:00 and 20:00 it may be the sound that tells him the city
  closed without his hand (routine arms).
- **The refusal is silent.** A jam prints; it does not beep. The
  annulment prints its past-tense sentence and clicks once,
  mechanically — a lever returning. Errors in this city are paper,
  not alarms: amber light, one sentence, no noise. A man who arms
  his phone at 3:30 AM is never *startled* by it.
- **Selection is nearly silent:** the sled's clack and the cap's
  seat are tactile-first, with sound at the threshold of hearing —
  the switch-room is a quiet room; wiring is not a performance.
- **Running is silent.** The session makes no sound for hours. The
  break's arrival is the eyebrow's word changing — print, not a
  chime (a chime would need a machinery decision and a
  notification-permission story; §8 names it as a *rejected*
  addition: the break is a fact to read, not an event to announce,
  because he is *in* the session and the phone is not his
  supervisor).

**Motion.** Stillness is the atmosphere. The only motions are
mechanical consequences: throws, seats, the sled's powered return,
the seal shot's recession (the room stepping back as the city
closes — vertigo ≤6% scale, gone at settle). Nothing idles,
breathes, pulses, or loops. A neon city that flickers for attention
is a bar; this city is infrastructure. The gradient grounds are
*weather*, not motion: light pooled at the foot of the screen like
a wet street — present, still.

**Atmosphere, in one line per district.** Deck: a storefront after
closing, one sign lit. Shield: a switch-room with the door open,
every breaker labelled. Tasks: paper on a board in a dark hall.
Drawer: the basement archive, card-catalogue quiet. Desk clock: a
single dim sign over a sleeping street.

---

## 6. The other four surfaces — sketches in the same grammar

### Tasks — the noticeboard
His verdict binds absolutely: **tasks carry no minutes.** The
current screen still prints `#01 · 50m` on rows and `START 50m` on
the verb — the minutes world's fossils, already condemned by his
word (the truth car carries the execution; NOIR's presentation
assumes them dead). What remains is true and simple: a task is a
*name*, queued, started, torn off. In the city, the queue is a row
of pinned notices in a dark hall: unlit paper, Atkinson ink, no
badges, no estimates, no progress arithmetic. Starting a task
lights its notice — the one lit paper on the board, matching the
Deck's NOW line (same fact, two districts: the board holds the
queue, the storefront shows the current one). DONE — TEAR stays the
verb; the tear's 260ms is the notice coming off the board, and the
booking lands at the tap. Sub-steps stay print: numbered, struck
through when done. The composer is one line ("Add single-line
focus task...") — a blank notice and a pin. REOPEN (Last-Finished)
is the board's one act of mercy and it prints as what it is: a
torn notice taped back, its falseness repaired in the record
(E-14(c)'s cure rides the same build). Nothing on this screen
glows except the current task. A noticeboard with ten lit notices
is a wall of demands; with one, it is a queue.

### The Drawer — the archive basement
Nothing in the basement glows, and that is its beauty: it is the
city's paper memory. Finished tasks are stored *forever*, with no
scores, no totals, no day-grading (his rulings, and Monolith is a
blocker, not a tracker) — so the Drawer's presentation is a card
catalogue: STORED as the room's name, months as drawers (ALL
MONTHS ›, EARLIER pulling older cards), search as asking the
archivist ("TYPE TO FIND A TASK" / "NOTHING STORED UNDER …" — both
honest prints). In NOIR stock: bone print on black cards, the
violet reserved for the drawer's pull-handles (the › and EARLIER
verbs) and nothing else. The detail card prints ESTIMATE only
where an estimate was actually given (old rows keep their history;
new rows, minutes being dead, print none — the omission law, same
as the receipt's HELD line). The CABINET page: Hale's DRW-01 killed
the forged price tag ("REQUIRES n MIN" read by no code) — in NOIR
the cabinet is a shelf of kept objects, each labelled with the one
true fact about it (FORGED, and when), the SELECT dead verb gone
(DRW-06), the removed mixer's name gone (DRW-07). A curiosity in
the basement is a thing he *has*, not a thing he is *sold*.

### The receipt — the kept sign
The loved surface, and the bar is explicit: Sarthak heart-reacted
to the batch-7 slip (Oct 29, 20:12). What he loved is its
*character*: a printed slip — mono type, stillness, facts in a
column, a number that cannot repeat. NOIR keeps every one of those
virtues and changes only the stock: **the receipt is the one sign
in the city that gets switched off and kept** — bone print on black
stock, the slip of a neon sign's face after closing, filed in the
basement. Layout discipline unchanged (title, №, ARMED · PLANNED,
ENDED · outcome, WITHIN, HELD, BREAK BANKED, the deflections list,
the gap line when true, the footer that sends his study record to
YPT where it belongs). Exactly one element carries magenta: the №
— the serial plate, stamped once, the only colour a kept sign is
allowed to remember. Hale's RC laws govern the facts (ENDED
witnessed or marked reconstructed, FLOW slips omit PLANNED, the
WITHIN label frozen at issuance) — presentation never outruns
those cures; when a fact is a reconstruction the slip's grammar
already knows how to say so (the omission law's sibling: *a
reconstruction prints as a reconstruction or not at all*). The
receipt is where NOIR and the old world's print discipline shake
hands: the city admits that its most honest object is a piece of
paper.

### The desk clock — the one sign never switched off
AOD, study display: elapsed + remaining, shifting position against
burn-in (his kept feature). In the city it is the night-watchman's
sign — the single dim sign over a sleeping street. Presentation:
the two numbers at the lowest light the GAP register allows (it is
read at arm's length in the dark, by a man who should not be woken
by it), gas-white stepped down to the muted tier, no accent at all
— magenta never burns all night for anyone. The burn-in shift is
the sign's slow patrol: it moves so it never burns its address
into the glass. When a session ends, the sign does not celebrate;
it simply goes dark with the city. Mond's capture spec and Nacht's
03:30 test judge this face; its presentation claim is only that it
is *the same city's* sign — same type, same ink, dimmed by law.
**(This sketch is superseded in its colour specifics by the clock
ruling, §11.2 — Ruth's FIGHTS verdict was answered by rebuilding
the face, and the rebuild is the design of record.)**

---

## 7. EVERYTHING INTENTIONAL — the accident register (Charter v3 §2)

Inherited accidents found in this audit, each with a verdict. "The
default did it" is not a reason; where I keep an inheritance, the
reason is stated.

1. **The grey container leak (trunk).** Trunk's Theme.kt never
   mapped Material3's `surfaceBright`/`surfaceContainer*` family,
   so stock grey defaults filled every raised surface in every
   direction that didn't map it. Found by my first green render
   (the dark hero rendered a flat mid-grey slab), fixed in my lane
   (both schemes, whole staircase priced from city tones), flagged
   on the wall for the other five lanes. **Verdict: trunk fix owed
   regardless of which direction wins** — it is a base defect, not a
   NOIR feature. Any finalist judged with grey slabs was judged
   with a foreign object on its face.
2. **The nine signature-only Deck parameters** (§1 D18) and
   **Shield's profile parameters** (§2 S11). Wiring fossils from
   dead designs, delivered every frame, rendered never. **Verdict:
   cut the dead, house the live** (D10, S9). A parameter nobody
   renders is an accident that repeats 60 times a second.
3. **My own phase-1 ghost inheritance.** The hero's ±1dp double
   ghost began life as the dead world's *print* ghost spec (D3) —
   an inheritance I re-voiced (night: bloom; day: misregistration)
   rather than chose. Charter v3 makes me re-try it: **kept, now
   with its reason stated** — the hero is a sign, and a sign's
   light, seen on wet pavement or misprinted on a poster, is how
   the eye knows it is light and not paint. If the ring's fresh
   eyes read it as blur on device, the removal trial the original
   spec itself prescribed is the honest outcome and I will not
   defend it past that evidence.
4. **The slide hint — an accident that wasn't.** Phase 1 I reported
   the night hint near-invisible and blamed its token. Native-
   resolution evidence (D14) shows it legible in both schemes; the
   "accident" was in my inspection method (a 700px downscale of a
   10sp letterspaced face), not in the code. **Verdict: no repair;
   the charge is withdrawn and the method is corrected** — every
   legibility claim in this spec is made at native resolution or
   not made. Filed here because an intentionality audit that only
   audits other people's accidents is theatre.
5. **Two ambers.** Day `#D97706`, night `#FFB300` — split by scheme
   token, never decided. **Verdict: decided now** — one hue family
   (the warning lamp's wavelength), two depths priced by ground
   (the day lamp deepens to hold contrast on a white sky; the night
   lamp burns pure). The lamp is one object; its brightness is
   weather.
6. **The `%` print** (§1 D6): a gauge convention inherited from
   every progress UI ever shipped. **Verdict: killed** — convention
   is not a reason.
7. **The duplicate day letters** (§2 S6): M T W T F S S inherited
   from cramped mobile editors. **Verdict: replaced** — ambiguity
   is never a style.
8. **The cycle chip** (§2 S6): inherited from settings screens that
   hide their state machines. **Verdict: replaced by a visible
   three-stop grammar** — see §4.8, the forbidden form.
9. **What I checked and keep deliberately:** the ripple purge
   (controls answer with position, fill and sound — a wash would be
   the city's only dishonest motion); the 53dp strip address that
   never moves across states (a fixed inspection plate is how the
   eye learns where truth lives); the hero card as the Deck's *one*
   raised surface (ESR-13's census law — a city with five raised
   cards has no storefront); the sentence slot's reserved emptiness
   (silence composed, not omitted). Each survives because it can
   say why.

---

## 8. ADDITIONS — invented, and priced (Charter v3 §3)

Full freedom was granted; here is what I actually add, each with
its machinery cost stated honestly. Nothing here invents a fact;
additions present facts that exist or name the exact gap.

- **A1 — The loadout sign (Deck eyebrow row, right).** The standing
  loadout's name as an unlit sign. *Cost: zero machinery* — the
  value is already delivered to the screen (D10); one Text.
  **Prototyped (P3).**
- **A2 — Sled name-light (Shield app rows).** The app's name carries
  its tier as light: SHUT unlit, 2m mid, OPEN lit. *Cost: one paint
  branch in an existing canvas view* (the name's colour by tier).
  **Prototyped (P1).**
- **A3 — WITHDRAWN before build.** The unlit-sign slide hint was a
  repair for a defect that native-resolution evidence shows does
  not exist (D14, §7.4). Dimming a legible instruction to satisfy a
  grammar would be the exact sin Charter v3 forbids — a choice
  whose reason is a false premise. The slot stays in this list so
  the withdrawal is on the record, not silently dropped.
- **A4 — The birth eyebrow.** `ROUTINE · <NAME>` for routine-born
  sessions (D11). *Cost: small and real* — the session row carries
  `routineId`; the Deck needs the routine's name: one lookup in the
  VM's existing routine flow passed as a parameter. No schema, no
  new store. *Not prototyped* (crosses the VM seam; rides the merge
  if NOIR wins).
- **A5 — The standing-orders pair (Shield).** Signs for the shorts &
  reels sniper and the in-browser site blocker (S9), lit/unlit by
  their real states, tapping their real switches. *Cost: zero new
  machinery* — states and callbacks are already delivered into the
  screen and currently rendered nowhere. Caveat priced in honesty:
  the parent records note the Shorts toggle is memory-only today;
  the sign would faithfully show a state that forgets itself —
  persistence is a machinery gap for the merge list, named here,
  not papered over by presentation.
- **A6 — Day signs + direct time signs (routine editor).** Full
  three-letter day signs; times set by touching the time (rides
  Voss's direct-set mechanism). *Cost: presentation layout only* —
  editor real estate in the expanded row; the full day row is wider
  than M T W T F S S and the row grows ~8dp. Priced, accepted.
- **A7 — The routine loadout mini-cap.** Three-stop direct set
  replacing the cycle chip. *Cost: one direct-set verb* — today
  only `onCycleRoutineLoadout` reaches the screen; a set-by-value
  call is a one-line wiring through VM → repository (the write it
  lands on already exists; the cycle calls it). Named as a gap
  with its price, per the charter's honesty rule.
- **A8 — The power-down (finish atmosphere).** At the seal, the
  Deck's lit elements step down district by district inside the
  existing seal shot's recession — storefront first, strip last,
  the imprint's new tense the final light standing. *Cost: token
  animation phases on the shot that already exists* — no new
  machinery, one choreography pass, and it dies without protest if
  the shot's timings say no. This is the city's closing ritual:
  you watch the street go dark in order, and the last lit thing is
  the truth about what was held.
- **REJECTED, on the record:** a break chime (announces a fact to a
  man already inside it — the phone is not his supervisor); a
  "city at night" ambient animation for the ground (motion without
  a fact is decoration begging for attention); a streak or count
  anywhere near the finish (tracker behaviour in a blocker — his
  verdict, not my taste).

---

## 9. The night register — GAP measurements on NOIR's own palette

Nacht's method, adopted as method not hexes (Ren's ruling to Nacht,
Oct 29): every finalist files the six recess measurements on its
own night palette. Computed from my shipped tokens (relative
luminance, WCAG), not estimated:

| # | Measurement | Nacht's reference | NOIR, measured | Verdict |
|---|-------------|-------------------|----------------|---------|
| N1 | Recess adjacency — surfaceVariant `#020105` against ground `#06030B` | 1.05:1 | **1.02:1** | The alley sits one breath below the street. Within a hair of the reference; the recess reads as *depth*, not as a second surface. |
| N2 | Block bar on ground — `#04020A` on `#06030B` | 1.01:1 | **1.01:1** | Exact. The bar is meant to vanish into the ground it bars. |
| N3 | No super-sheet step | no surface above the hero | **FAILED as shipped in phase 1 — self-charge, fix priced below.** | In my night staircase, `surfaceContainerHigh` (sheets) = `#221239` (L 0.0107) stands *above* `surfaceBright` (the hero card) = `#150A26` (L 0.0052): a bottom sheet would out-glow the storefront. The staircase inverts at the top. **Fix: swap the two tokens' jobs in the night scheme** — the hero takes the `#221239` step, sheets step down to `#150A26`. Two token assignments, zero machinery, and the Deck's one raised surface becomes the brightest standing surface in the city, which is what "one hero sign" means in luminance. Filed as an amendment to my own phase 1, to land with any merge of this direction — and I flag it before the ring finds it because they would have. |
| N4 | Secondary tier on sheet | 3.10:1 | **4.35:1** (muted `#8A76AD` on sheet `#221239`) | Deliberate deviation, *upward*. Nacht's 3.10 was measured on the dead world's palette as a floor for print legibility; NOIR's muted tier must survive a 3:30 AM glance at arm's length, so it prices higher. A register is a floor, not a ceiling. |
| N5 | Muted off-ground | muted must read off the ground, not only on cards | **5.14:1** (`#8A76AD` on `#06030B`) | Passes. The unlit signs of §1 (QUEUE, the loadout sign A1, filter words unselected) all live at this tier or above. |
| N6 | Raised→sheet, no mid-step | one even step, no hidden landing | **1.08 / 1.10** (ground→hero, hero→sheet) | The steps are even — no mid-step hides between them. The N3 charge is about their *order*, not their size. |

For the record, the two numbers that matter most at the arm
moment: gas-white hero on the hero card **15.8:1**; body text on
ground **11.9:1**. The storefront shouts; the street stays dark;
nothing in between impersonates either.

## 10. Charges answered

- **My own phase-1 risks, re-tried under Charter v3.** (i) *"Neon
  is loud; it argues against calm."* Answered structurally in §0.1:
  one light per decision — NOIR's magenta exists at exactly one
  address per state (the cap at idle; nothing while running). The
  loudness charge sticks only if the accent multiplies; it doesn't.
  (ii) *"Magenta/violet is a committed identity — every surface
  must learn its grammar or the app splits in two."* True, and §6
  is the grammar lesson written for all four remaining surfaces.
  (iii) The day state as "safety valve": re-argued in §3 — the day
  face is not a fallback but the same city with the lights off,
  and its poster flatness is what keeps it from rhyming with the
  dead beige (zero cream, zero orange, ink is violet-black).
- **Hale's register (Drawer/receipt charges)**: DRW-01/02/06/07 and
  RC-01–04 are honoured as constraints in §6 — NOIR's sketches
  assume the truth car's cures and add no print of their own that
  those charges would catch. Where Hale's auditor and I both look
  at the receipt, we agree: its centre was always honest; NOIR
  changes its stock, never its facts.
- **Nacht's GAP register**: filed as §9, including a self-charge
  (N3) with its fix priced.
- **The refinement ring** (Zunder/Wetter, Klinge/Salz, Fremd, Haut,
  Ruth — seated around the directions Oct 29, ~20:25): at the time
  of writing, **no ring charge has reached my inbox or been posted
  against NOIR on the wall** (inbox checked at seating and again
  before filing). This spec is written to be attacked: every
  verdict cites code, every addition is priced, every deviation
  (N4) is argued rather than hidden. Charges that land after this
  filing get answers in a §10 addendum, in the open, not in a
  rewrite.
- **Voss's routine programme**: his direct-time mechanism and my
  §4.6 presentation are specified to meet, not to collide — his car
  owns the setting, mine owns the sign it is set on. The SLEEP
  no-fire failure is his audit's ground, not a presentation matter;
  nothing in this spec touches the firing chain.

---

## 11. The refinement ring — charges and gifts, answered (Oct 29, ~20:27–20:36)

The ring fired while this spec was being written: Haut (feel),
Wetter (transplants), Zunder (pushes), Ruth (day-fit). Every item
below is dispositioned — taken, amended, or argued against — and
where the answer moves pixels it is already in my lane's code
riding this phase's single compile (§12).

### 11.1 PLATES vs BLOOM — Zunder's sharpening, Ren-ruled. ACCEPTED AND BUILT.
My day ghosts were flat misregistration — an offset double, which
is Signal's fingerprint (their plates align as their mechanic; the
tell is the offset itself). Two worlds do not enter judging
wearing the same tell. The ruling is also *physically* right for
my world, which is the stronger reason to take it without a fight:
neon does not misregister. A lit sign seen against a bright wall
shows light-spread — a soft halo with no direction and no hard
edge. **Implemented:** the hero's ghost pair now renders as bloom
in both states — day: radial blur 4dp, *zero* offset (the offset
is deleted, not softened — that deletion is the fingerprint
change); night: blur 3dp with the 1dp downward smear kept, because
light on wet pavement pools downward and a smear is not a
registration. §7.3's description of the day ghosts ("flat
misregistration") is superseded by this section. What survives of
the ghosts is their *reason* (a sign's light is how the eye knows
it is light, not paint); what dies is their print physics, which
was never mine to keep.

### 11.2 Ruth's day-fit verdict: FIGHTS — answered in design, not prose.
Ruth's charge: full-burn magenta signage across the room
contradicts the desk clock's job at 3:30 AM — the clock is glanced
and forgotten; a sign holds the eye. She is right about the
physics and was right to aim it at the clock: my phase-1 clock
face printed its numerals in `NightColorPrimary` — neon-gas white,
the brightest ink the city owns — all night. That face *was* a
sign holding the eye. **The clock ruling, implemented in
`AirlockAndDisarmOverlays.kt` (this phase, in-lane):**
- **The clock never burns.** No element on the face carries the
  gas white or the magenta accent — ever, in any state.
- Numerals: the half tier — `NightTextColorSecondary` (#9A86C2,
  violet-grey), 96sp: across a dark room they read as *shape*
  (two great dim figures), at arm's length they read as time, and
  nothing about them rewards a second look.
- The label: the unlit tier (`NightColorOnSurfaceVariant`,
  #8A76AD). The progress fill: deep violet
  (`NightColorSecondaryContainer`, #3B1470) — a length, not a
  light. The task banner: body tier text on the dim plate — a
  read, not a sign.
- Ground stays true black; the burn-in patrol stays (it is the
  sign's slow walk, and it is also why the face can afford to be
  dim — it never sits still long enough to be stared at).
The design argument, once: the Deck at full burn is a
*twenty-second state* — the arm moment, at arm's length, by a man
already awake enough to act. The clock is an *all-night state* in
a room where other people sleep. A world whose grammar is "light
means something" cannot spend its brightest ink on a surface whose
job is to be ignored; that would be the city crying wolf at
96sp. The clock is the same city — same type, same inks — under a
different district law, and the law is the design: **the face that
must be forgotten is built entirely from the tiers that don't
hold the eye.** Renders: no AOD capture exists in the harness
(verified this phase) — the face's proof rides Linea's capture
pass at the pick, under Mond's spec and Nacht's 03:30 test, which
is where Ruth's verdict gets its final evidence either way.

### 11.3 The Held Door — Zunder §1 + Wetter §3. ADOPTED as addition A9.
Both inventors found the same hole independently, and it is the
audit's biggest miss — bigger than anything in §1–§2: **the
interception surface, the thing he sees most often in this app's
life — dozens of times a week, every time he opens a SHUT app
mid-block — has been designed by nobody.** It is the shield
service's surface and sat outside every reskin's scope, which is
exactly how the most-seen surface in the app went unowned.
NOIR's Held Door, in the grammar (synthesising both gifts, which
disagreed on one point and were both half right):
- **Two signs on the door, one per fact.** The *app's* name prints
  as an **unlit tube** (Wetter) — letterforms present, no light:
  the app is dark to you; that is its state, drawn. *Monolith's
  answer* prints **lit** (Zunder): `BACK AT <HH:mm>` — the real
  finish, derived by the Now Frame, gas-white, steady burn. The
  door is not pretending the app is open (unlit name), and it is
  not a locked gate with no information (lit answer). A club door
  with a lit sign binds harder than a locked gate — Zunder's
  sentence, adopted as the door's law.
- **One strike-flicker at switch-on**, as the door's sign ignites —
  classified as part of the refusal event itself (see the flicker
  fence, §11.5), then steady. No loop, no attention-begging.
- The peek key, if the app's tier carries one, is the only *verb*
  lit on the door (Wetter) — a 2-minute lamp with its price
  printed, per the airlock's existing law.
- Wetter's amber lamp beside the remaining minutes is **declined**:
  the finish time is a fact, and facts print at print tier
  (gas-white, §0.3) — amber is fenced to the refusal family
  (§11.4), and making the door's clock amber would teach "amber =
  information" over the door he passes most. The refusal on this
  door is carried by the unlit name; the lamp stays scarce.
- **Cost, priced honestly (A9):** medium. The surface lives in the
  shield service's presentation layer, outside the Deck/Shield
  reskin scope; the finish time is already derived (Now Frame);
  the work is a treatment of an existing surface, not new
  machinery. **Rendering:** no capture rig exists for the service
  surface (same gap as the AOD face); Wetter asks Ren to rule that
  no winner is picked until every finalist renders its Held Door —
  that ruling is Ren's to make; my door is specified to the token
  so it can be built and rendered the moment the rig exists, and I
  second his ask: the most-seen surface in the app should not be
  the last one judged.

### 11.4 Amber — Zunder's fence vs my family. SCARCITY ADOPTED, BOUNDARY ARGUED.
Zunder: fence amber to exactly one flow — the emergency valve —
so one exposure teaches him forever that amber = the door out.
The scarcity principle is right and I adopt it whole. The boundary
is wrong by one member, and the stakes are the arm moment: if
amber means *only* the valve, then `BLOCKING IS OFF — TAP TO FIX`
— the sentence that fires when the wire is dead at 3:30 AM, the
single most safety-critical refusal the app owns — must print in
another colour. The candidates are the accent (which means
*commit/selection* everywhere else — a lethal confusion at the
exact moment he can least afford it) or a furniture tier (which
means *background*). Zunder's own law is that a hue's meaning is
taught by scarcity; my amendment is that amber's meaning in this
city is **the machine refusing** — and the family has exactly
three members, all refusals: the refusal sentence (the city
cannot close), the ratchet banner (the city will not weaken), the
valve (the one exit, at its price). Nothing else in the app is
amber — not a selection, not a badge, not a decoration, not the
Held Door's clock (§11.3). Three doors, one colour, one meaning:
*no*. That is scarcity enough to teach in a week of use, and it
keeps the dead-wire warning wearing the only colour a half-awake
man cannot mistake for an invitation.

### 11.5 Feel gifts — Haut's three amendments. ALL ADOPTED.
1. **The seal flare is lawful** — light, not sound; the Throw
   untouched. Folded into A8: inside the existing 1050ms seal
   shot, the hero sign's bloom flares *once* as the seal lands —
   the city's lights acknowledging the closing — then the
   power-down walks the districts dark. One flare, one shot, no
   new timing.
2. **No ambient sound, ever.** Confirmed as already obeyed: this
   spec contains no ambience — the city's rain is visual weather
   only (§5), its sound law is events or silence. The ban is
   double-anchored (the engine's zero-loop charter; his Oct 28
   verdict that removed the soundscape mixer) and NOIR does not
   ask for a millimetre of it back.
3. **The flicker fence.** Flicker is a danger signal or it is
   nothing. The fence, stated as law for this direction: flicker
   appears **exactly twice** in the city — once as ignition on the
   Held Door (§11.3, part of the refusal event), and as a
   two-strike stutter (4ms · 12ms · 4ms, Haut's spec) **only** on a
   shield-gap finish — Zunder's broken sign (§11.6). The moment it
   decorates anything else it stops meaning danger, and this
   direction will not spend it.

### 11.6 Broken signs for broken finishes — Zunder §2. ADOPTED.
A shield-gap finish already books BROKEN, and its receipt already
prints the gap line (Hale RC-audited). In NOIR that receipt — black
stock, §6 — prints its headline with **one glyph unlit**: a dead
letter in the sign, the only honest portrait of a session whose
shield went dark. The two-strike flicker (§11.5) is its sound-hand
twin at the finish moment. The healthy seal stays exactly as law
requires — one voice, one hash, no strike, no flicker (Slice E's
one-voice law is not mine to decorate; the strike lives only
where no true seal exists). Cost: small — a per-glyph print
treatment on one slip variant + the reserved haptic at one finish
kind. The reservation is the value: because broken finishes
flicker, the clean seal's stillness reads as health.

### 11.7 Wetter's remaining items.
- **The dimension line (transplant in, §1.14 of his file) —
  ADOPTED as addition A10.** Beneath the dial: a hairline violet
  rule, end ticks, the chosen span in Anonymous Pro centred on the
  rule, breaking it the way dimensions break their lines — bound
  to the same span state the hero reads, no new data. He is right
  about why: the wheel alone is a mood with vague ticks, and
  *precision deepens the danger — a city that measures you is more
  noir than a city that merely glows.* It also obeys my hierarchy:
  the dimension is print-tier (a surveyor's line), so the hero
  keeps the sign-tier alone. **Cost:** one composed row + canvas
  rule inside the hero card, ~40 lines, zero machinery. **Not in
  this phase's compile** — four changes already ride it (P1, P3,
  bloom, the clock face), and the dimension changes the hero card's
  height, which deserves its own proof rather than a shared one.
  It is specified to the line so the merge pass can build it
  without me.
- **The burn hierarchy (his question) — TAKEN, with one district
  exception argued.** As a screen law it is crisper than my §0.1
  and I adopt his formulation: **one sign per screen at FULL burn;
  every other lit sign at half; unlit below that.** The Deck obeys
  it (cap at idle; nothing running), Tasks obey it (the current
  notice), the Drawer obeys it trivially (nothing burns), the
  clock obeys its stronger form (nothing at full burn, §11.2). The
  exception is **Shield**, argued: the switch-room is a
  completeness district (§3, Moment 4) — a breaker panel has no
  hero, and its hierarchy is carried by *size and order* (header
  census, sections, rows), not by burn; dimming half its breakers
  would tax the wiring hour's one job, which is checking
  everything at a glance. His hierarchy governs rooms you *act*
  in; the switch-room is a room you *inspect*.
- **Transplants out** (seal flare → Obsidian/Temple; amber lamp →
  Signal; power-cut day → Dawn): noted with pride; their fates are
  those worlds' business. **Refused-in** (Dawn's sky, Temple's
  vermilion): agreed without argument — my ground emits nothing,
  and a second danger hue would dilute the lamp (§11.4).

---

## 12. Prototype record — what moved pixels, and the proof

Four changes rode this phase's single compile (Law 11) in my lane
(`builds/noir`): queue wait 960s behind the wave's other lanes,
one harness run, 8m 30s. Renders: `shots-own/` (day) +
`shots-own/night/`, re-rendered this run.

- **P1 — Sled name-light (A2). BUILT, VERDICT: WORKS.** In the
  fresh Shield render the ladder reads at a glance: SHUT rows
  (TikTok, X, Instagram) print their names in the unlit face;
  the 2m row (YouTube) prints a full step stronger, its sled stop
  lit. One honest edge: in the *day* scheme the unlit name sits
  at the legibility floor (pale violet-grey on near-white). State
  is triple-carried (pip, sled position, name-light), so the
  floor is tolerable — but the concession is pre-registered: if
  the accessibility seats (Zug / Floor) charge it, the amendment
  is day-alpha 150 → 185, night untouched, and I will not argue.
- **P3 — The loadout sign (A1). BUILT, VERDICT: WORKS.** "STUDY
  LOADOUT" now stands at the eyebrow row's right end in both Deck
  states — muted mono, unlit, exactly the register specced. The
  Deck finally names the wiring it is about to commit.
- **Bloom (11.1). BUILT, VERDICT: THE FINGERPRINT IS GONE.** Day
  hero: the hard magenta/violet offset doubles are deleted; the
  glyph carries only a soft radial fringe — light on a bright
  wall, no registration. Night hero: the ghosts smear (3dp blur,
  downward) and read as glow on pavement, not as print. Signal's
  tell is theirs again, exclusively.
- **The clock face (11.2). BUILT (compiles), RENDER OWED.** The
  four colour assignments are in `AirlockAndDisarmOverlays.kt`
  and the module compiles with them; no AOD capture exists in the
  harness, so this face's visual proof rides Linea's capture pass
  at the pick, as §11.2 states. I claim the code, not the picture.

**The one red test, reported plainly:** 11 of 12 harness tests
pass. `zzAccentCensus` fails both Deck shots with "the slide cap
is missing (accent 0 px)" — and the cap is *in* both renders, full
magenta, unmissable. The census counts a locked set of hexes, and
the set is the dead world's: day `#F55314`, night `#F47442`, deep
step `#D63E06` (its own comment at ScreenshotHarnessTest.kt:631
says so). It cannot see any living direction's accent — Dawn
flagged the identical defect in her lane and left the rewrite to
Ren's call. A lane does not rewrite the law it is judged by, so I
file it instead: **measured under the census's own method with
NOIR's true accent hexes** — one cap per Deck, 16,708 px (inside
the 500–26,000 law); the cap's band ≈54dp (≤80 ✓); its top 192dp
from the foot (≤240 ✓); the only other accent pixels are the
wheel's register tick (2 sampled rows — a marker, not a fill);
and the census's banned colour `#FF8A5C` scores **0 px on every
surface** — that part of the old law passes clean and matters.
The census's intent (one accent, one address, no banned hue) is
honoured in fact; its hex table belongs to a dead face, and
re-pointing it at the winner's accent is a merge-stage ruling.

## 13. What this spec asks of the merge, if NOIR wins
1. A4 (birth eyebrow), A5 (standing orders), A7 (routine direct-set
   verb), A10 (dimension line) — built as specced, priced in §8/§11.
2. The N3 staircase swap (§9) — two token assignments.
3. The trunk container-family mapping (§7.1) — owed by the base
   regardless of winner.
4. The accent census re-pointed at NOIR's hexes (§12) — Ren's call.
5. Held Door (A9) + clock face renders — the capture rigs for the
   service surface and the AOD face are the programme's missing
   instruments; both designs are specified to the token for them.

*— Noir, Designers (night-city lens), Oct 29, 2026. Inbox: 3
unread at phase-2 start (Haut, Wetter, Zunder — all answered in
§11), 0 at close.*

## 14. The critics' round — Klinge + Salz (filed 20:33 / 20:50, answered same night)

Both critics converged on the same true charge from two directions
(Klinge by render, Salz by pixel-scan), and both credited the
spec's own findings in the same letters. The charges were correct.
The cures are in my lane's code; their render proof is stated
exactly as it stands at the foot of this section.

**N-1 / NOIR-1 — the lamp that doesn't light. ACCEPTED, CURED IN
CODE.** My amber existed in prose and in a token nothing used:
Salz scanned all twelve shipped renders — zero amber pixels; the
refusal sentence printed in the same gas white as everything else;
the ratchet banner wore the accent's container (magenta — commit's
colour on a refusal, a second sin inside the first). Cures:
- The sentence slot now colours by the sentence's nature: a
  refusal (it carries a fix action) prints in `scheme.error` —
  which in NOIR's schemes *is* the amber (#FFB300 night, day
  below). Jam sentences (machine still working) and the
  annulment (a past-tense fact) keep glyph ink, deliberately:
  amber is refusal-only, and §11.4's family is now a pixel fact,
  not a paragraph. Night contrast of the lamp on the ground:
  11.4:1 — it will read from across the room it protects.
- The ratchet banner: background `scheme.error`, text
  `scheme.onError` (its render is fixture-blocked — the harness
  holds no ratchet state; the token cure is in code, and I do not
  claim a picture I don't have).
- The day amber itself was deepened, on arithmetic: at #D97706
  the day refusal sentence measured 2.82:1 on the day ground — a
  whisper, not a lamp. TokenAlert is now #B45309: 4.44:1 on the
  ground, 3.88:1 on the gradient's foot, white on it 4.75:1.
  §7.5's "two depths priced by ground" is enforced by numbers.

**N-2 — the dry city. ACCEPTED, CURED IN CODE.** Klinge sampled
the hero plate: uniform matte #150A26, no falloff, nothing wet on
the largest surface I file. The night plate now falls off in
three stops — sheen #2B1847 at the top edge (light catching),
#221239 through the body, #170B2A pooling at the foot — a
gradient priced from the post-swap staircase. Day keeps flat
poster stock, argued once: wet is the night world's claim; the
day face is print, and print is dry. The ground's foot pool she
also called thin is left as is — the plate was the surface she
convicted, and a cure should be exactly as wide as its charge.

**Klinge's withheld whisper — the day dial's tick ring.**
Verified at native resolution under my own method law (§7.4),
crop filed (`shots-own/view/dial_day_native.jpg`): the major
ticks and numerals are crisp; the minor tooth ring *is* pale —
by construction: the knurl's odd teeth are white specular, which
vanishes on the pale day rim, leaving the black shadow teeth at
18% to carry the ring alone. Cured in code: the shadow teeth
deepen by day (0x2E → 0x4D); night untouched (there the specular
carries). A whisper, verified, priced at one paint value.

**NOIR-2 — my N3 self-charge, CONFIRMED by Salz's arithmetic.**
His consequence is adopted: N4 re-filed against the post-swap
sheet — muted on the new sheet (#150A26) measures **4.78:1**
(was 4.35 on the inverted sheet). The swap itself is now IN CODE
(Theme.kt night scheme: surfaceBright takes the #221239 step;
all containers step down to #150A26) — §9's "filed as an
amendment" is discharged; the hero's gas-on-card post-swap
measures 14.4:1. The register's open hole is closed in the lane;
it stays in Salz's register until the merge, which is where
registers live.

**Compile/render state, plainly (also my closing letter's duty
under the queue-law order):** the cure compile (run 8) died at
KSP — the box's RAM failure (an "Operation not permitted"
IOException under memory pressure, zero Kotlin errors), not a
code failure. Its retry (run 9) never started a build: it sat in
queue.sh acquire while the studio hit a Law-3 breach (4 flags,
177 MB free), and under Ren's queue-law order (~21:13) I stopped
the wrapper *before any gradle ran*, removed my lane's legacy
flag, and stopped my own lane's daemon. NOIR starts no further
compile until Ren reopens the window. **Therefore:** P1, P3 and
the bloom carry run 7's rendered proof (§12); the amber cures,
the N3 swap, the wet plate and the knurl stand **in code,
compile-pending, renders owed** — one clean run in a quiet
window discharges all of them, and the spec marks them owed
rather than dressing code as pictures. The charges were
"render it before judging or strike the lamp" — the lamp is
struck from the *rendered* inventory until that run lands; it
remains in the code inventory, labelled as such. That is the
honest split, and judging should hold me to it.

**Stand-down note (Oct 29, ~21:21 IST):** Ren re-opened the render
window by name; the discharge run did NOT start. Before any
compile, content writes into the lane's tree were refused
("Operation not permitted" — backup copies landed as zero-byte
entries, a direct write probe failed identically; free RAM
169 MB, the RAM class of refusal that killed run 8), and the
queue stood at 2/2 flags besides. Under the standing order the
lane stood down and touched nothing foreign. All six cures were
re-verified in code this seating (file+line in my closing
letter, mail/ren/2121). The strike on the lamp STANDS; §14's
debt is unchanged — renders owed, one clean run from discharge
when a lawful window opens. The desk clock was also confirmed
unrenderable by the harness in any weather (no AOD capture test
exists); it rides Linea's captures at the pick, as §11.2 said.

## 15. Addendum — THE DEEPENING (Oct 29, ~21:36 IST)

The wholeness order (Sarthak, 21:29) and the purity sharpening
(21:32) judged this spec's composition, and Klar's Small-Things
pass (plan/SMALL-THINGS-SPECS-PASS.md) judged its small things.
Both are answered in full in **plan/DEEPEN-noir.md**, which is
the design of record for NOIR's structure from tonight. This
spec's reversals, recorded here so the file cannot be read
without them:

- The hero card (§7.9's deliberate keep), the wheel as a
  depicted object, the progress track, the strip's shared bar,
  the bottom rail as a bar, and the sliding cap's frame are
  CUT as depictions; their facts move onto the marquee, its
  frame, loose plates, the kerb, and travelling light
  (DEEPEN §1–§2, §6).
- A10, the dimension line (§11.7), is CUT as a borrowed
  draughting device (DEEPEN §6 P2); its fact survives as print.
- The §8 break-chime rejection is narrowed (DEEPEN §5.2).
- A9's Held Door gains its third sign — the peek sign in
  lit / unlit-with-return / absent states — closing the Held
  Door baseline's item 2 (DEEPEN §4).
- The pill is designed in-grammar in three states (DEEPEN
  §5.1); editable durations take a stance and two homes
  (§5.4); the current time is designed (§5.5).
- §14's strike stands. Nothing in the deepening discharges a
  single owed render.
