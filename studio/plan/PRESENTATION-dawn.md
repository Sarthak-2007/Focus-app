# PRESENTATION — DAWN

*Dawn, Designers (light/materials), working directly under Ren.
Phase 2, on Sarthak's orders of Oct 29, ~20:22–20:25 IST: the design
thing includes EVERYTHING — what information is presented, how it is
used, how it is selected; feel is designed, not emergent; everything
intentional, no defaults, no accidents; full freedom to add, with
machinery costs priced. Truth laws and his verdicts bind throughout.
Build gate stands: this is a spec, nothing here ships early.*

*Evidence base: the merged trunk as reskinned in lane `builds/dawn/`
(phase 1), read line by line — TactileDeckScreen.kt (1,066 lines),
ShieldMatrixScreen.kt (576), GuardLine.kt, ReceiptSheet.kt,
ParkDrawer.kt, the desk-clock overlay, plus plan/DRAWER-REVIEW.md,
plan/RECEIPT-REVIEW.md and plan/ROUTINE-NOFIRE-AUDIT.md.*

---

## §0 The death charge, answered in design

The charge against DAWN's bright face: *a big cream panel — the old
app restyled.* It was fair. Phase 1's bright Deck carried its glass
reading on translucency alone while the hero pane (95% opaque) and
the wheel's body (fully opaque socket + disc) stayed slabs.

Phase 2's answer is built, not argued — **the dissolve**, prototyped
in-lane (`DawnGlass.kt`, `RotaryWheelView` in BlockitCoreViews.kt):

- The hero pane opens: day alphas 0xF2/0xCF → 0xD8/0x8C. The readout
  zone keeps its body; the pane's lower half thins until the sky
  itself is the panel's bottom edge.
- The wheel's BODY goes glass by day: socket at alpha 148, disc at
  188, so the dawn gradient shows *through the instrument*. Every
  MARK on it — teeth, numerals, wells, the lamp's specular — stays
  fully opaque. The panel reading dies; the instrument remains.
- Night is untouched: after dark the glass was already honest.

Proof: lane renders `builds/dawn/shots-own/phase2_deck_bright.png`
and `phase2_deck_dark.png` (BUILD SUCCESSFUL, this lane, this
code), against phase 1's set. Judging note, stated before anyone
else has to: legibility was never the bright Deck's problem — plum
ink measures 14.07:1 on the hero glass's top composite and 13.19:1
at the bottom stop (computed from the shipped tokens). The problem
was identity.

**Addendum, written after looking at my own render — the dissolve
as built is a HALF-dissolve, and I won't overclaim it.** What the
phase-2 frame proves: the hero pane's lower half now melts into
the sky (its bottom edge is gradient, not slab), and the wheel's
rim and socket read as glass — the sky's rose shows through the
instrument's edge. What it does not yet prove: the disc's centre
still reads pale, because the disc token itself is a cream at 74%
opacity over a pale sky, and 74% of cream is cream. The remaining
step is priced and named, not hidden: re-voice the disc token as
true glass (alpha ≈ 140, tinted from the sky rather than from the
surface family) and step the dial numerals up from the muted tier
to full ink so they hold over a moving gradient. One token, one
tier change, one more render. If the panel wants the full
dissolve, that is the exact price; if it kills DAWN on the
half-dissolve, the step dies with it and nothing was oversold.

## §1 The grammar — laws every section below obeys

1. **The sky is the ground; glass is the only material; ink is
   plum.** Nothing sits on a card when it can float on light.
2. **Light pools at the top and thins downward.** Prominence is
   closeness to the light. The hero lives at the top; furniture
   sinks; the foot of the screen is the palest, quietest zone.
3. **One fact, one rendering.** (Studio law, restated as DAWN law —
   it decides several verdicts below.)
4. **Arming is a light-step.** The shipped behaviour — a running
   session forces the night scheme, the ground stepping at t=400 of
   the seal shot — IS dawn grammar, claimed as the world's central
   metaphor: you arm, and the sun drops; the session is a held
   night; the finish lets the light back in. DAWN does not invent
   this. DAWN names it, and designs everything else to agree.
5. **Records print on paper; controls float on sky.** The receipt
   and the Drawer are the world's only paper — opaque, warm-white,
   kept. Everything else is glass.
6. **Rose is spent once per screen, on the thing the hand commits
   with** — the slide cap, and (the one named exception) the
   ratchet alarm flood, because an alarm is also a commitment: the
   machine committing to refuse. Rose never decorates.
7. **DAWN never shouts.** No exclamation, no encouragement copy, no
   colour floods for emphasis. Emphasis is size, weight, and light.

## §2 (a) WHAT is presented — the census

### Deck — every element, its fact, its verdict

| Element | The fact it carries | Verdict |
|---|---|---|
| Header: MONOLITH + GUARD n/4 — first lack | Whether the four permissions can hold, and the first that can't | **KEEP**, words untouched. Unmeasured prints nothing — the blank is the honest state, keep the law. |
| Eyebrow: TOTAL / SESSION RUNNING / BREAK · N MIN | Which tense the hero speaks; during a break, the break's whole minutes | **KEEP.** The break's only rendering lives here — exactly one home, correct. |
| Hero readout | The true span (breaks priced in: n × block + (n−1) × 10), or the running clock | **KEEP**, protected size. The one number the app exists to say. |
| Press ghosts (±1dp, peach/white by day) | Nothing — simulated print misregistration | **REMOVE** (§6.1). Glass does not misregister. |
| The wheel | The chosen minutes (FLOW + 10–180, 5-min grain) | **KEEP**, body dissolved (§0). The Deck's altar. |
| Hub wells (×8) | The block count | **KEEP.** Count as light: lit wells = blocks. |
| FLOW key | Stopwatch mode is available / active | **KEEP.** A verb wearing a key's body — correct grammar. |
| Progress track (running) | The session's fraction | **KEEP the track, REMOVE the % numeral** (§6.2 — the fraction's second rendering). In FLOW the track is a rising horizon with no terminal promise: its denominator is a reference, and the drawing must stop implying a whole. |
| NOW line + task title | The session's declared work | **KEEP + PROMOTE.** At the glance this is the second thing read, after the readout; it already sits there. Sub-steps stay behind the tap. |
| DONE key | The verb that finishes the task | **KEEP.** |
| QUEUE n | How many tasks wait | **KEEP.** A count is a fact; it opens Tasks. |
| DESK CLOCK chip | The nightstand form exists | **KEEP** (running only, post-settle — correct: utilities never compete with the seal). |
| HELD chip + count | Held notifications wait, and how many | **KEEP.** The count is captured, not live — it never pretends. |
| Proof strip: BLOCK · APPS · TIME | The warrant's verdict, the held count, the end time — frozen at the seal, latched at the press | **KEEP, all three cells.** This is the machine's sworn statement; DAWN changes nothing about what it swears. |
| Seam stamps (BIND HELD · THIS VISIT / PROOF: …) | Whether the bind was witnessed this visit | **KEEP.** |
| Slide + hint | The commitment control | **KEEP.** The hint is set in the secondary tier (6.22:1 at night, Salz-verified) and priced as chosen. |
| Grace chip / valve chip | The two lawful exits, with the token count | **KEEP.** The valve's badge ("1/1 WEEK") is a fact; keep it mono, keep it plain. |
| Refusal sentence slot | Why the machine refuses, with its one action | **KEEP.** The machine's only prose voice; in DAWN it prints on the sky, unboxed, in Atkinson — prose should not wear armour. |
| Imprint (n APPS WILL SHUT → HELD) | The vow, struck at the seal | **KEEP.** The signature of the whole app. Over a dead wire the box stays empty — the register's worst lie is refused here, keep the refusal. |

**Finding: the Deck's information set is complete.** Nothing is
missing from it and, after the two removals above, nothing is
wasted. DAWN adds no element to the Deck. Completeness is a
finding, not a failure of imagination — the Deck was built by a
year of rulings; phase 2's work there is the dissolve and the
feel (§5), not more furniture.

### Shield — every element, its fact, its verdict

| Element | The fact it carries | Verdict |
|---|---|---|
| Ratchet banner | "You tried to weaken a running shield; refused" | **KEEP.** The one rose flood (grammar §1.6, named exception). |
| LOADOUT header: n SHUT • n PEEK | The arsenal's census under the current posture | **KEEP.** |
| The sliding cap (STUDY/WORK/SLEEP) | The whole posture, chosen at once | **KEEP** (§4). His pick; the grammar agrees. |
| ROUTINES header: n ACTIVE | How many routines stand armed | **KEEP.** |
| **(absent)** | When the routine chain last proved itself alive | **ADD** — "LAST ROUTINE CHECK HH:MM" under the ROUTINES header, from the stamp that already exists in the DAO. This is Voss's fix F5 (plan/ROUTINE-NOFIRE-AUDIT.md), priced there; DAWN gives it its home and its law: no stamp → the line prints nothing (the Guard law). After tonight, a silent chain must be *visible* silence. |
| Reset note | "Your schedule was reset to your day" (one-time) | **KEEP** until it dies of its own condition. |
| Routine rows (collapsed) | Start over end, name, days · loadout, ON/OFF | **KEEP the calm line.** The time column is the day's spine: one axis, every routine — the screen's best idea, already built. |
| Routine editor (expanded) | ±30 steppers, loadout cycle, day letters | **REPLACE the steppers** — they die with Voss's direct-setting fix, and DAWN blesses the death: steppers made time a negotiation, 30 minutes at a time, for a man whose blocks start at 05:00 sharp and 20:00 sharp. Direct setting designed in §4. Rename / create / delete get bodies in §4 too. |
| Search field | Narrows the arsenal by name | **KEEP.** |
| Category words | Narrows by kind | **KEEP.** Words with an ink underline — the right grammar for a filter (a filter is not a commitment; it must never wear the cap's body). |
| App rows + sleds | Each app's tier: SHUT / 2m / OPEN | **KEEP the sleds. ADD the pin mark:** a row set by hand reads in full ink; a row the loadout set reads in the muted tier, with one legend line at the section's foot — "BOLD = SET BY YOU". **Price corrected per Salz DAWN-2:** provenance is stored NOWHERE today — the entity stores one tier and `resolvedTier` is its parse, not a second fact. The mark needs a small origin fact written at both writers (the row's own set, and applySmartLoadout). Real machinery, priced in §7 A3 as amended — not zero. |
| **(absent)** | The Shorts & Reels sniper — enforced, invisible | **ADD a home:** a one-row section under LOADOUT — "SHORTS & REELS" + a two-stop sled. It acts inside apps, so it sits with the arsenal, not with sites. **Named machinery debt:** its state is memory-only today; a sled that resets at cold start would lie every morning. If DAWN wins, the toggle persists like the browser flag — a small repository flag, priced here so nobody discovers it later. |
| BLOCKED WEBSITES: n SITES, rows, REMOVE keys, add field | The enforced domain list | **KEEP all. MOVE its master switch here:** the browser blocker toggle exists and renders nowhere; it belongs at this section's header, beside the count it governs — a list and its switch in one place, so the section can never again be on while its switch is off, invisibly. |
| PARK contents (sound, theme, Guard board) | Settings + the four permissions, itemised | **KEEP**, out of this commission's scope except: PARK's board rows obey §9's sheet law. |

## §3 (b) HOW each thing is used — the three moments

First, the honest shape of his day, because presentation that
ignores it is decoration: routines arm themselves (FASTED 05:00,
DAY 09:00, SLEEP 20:00). The hand touches this app at three kinds
of moment, and DAWN designs for those, not for a generic user:

**1. The glance (many times a day, one second long).** Morning:
did the machine keep its promise? Mid-session: how long is left,
what am I on, is anything held? The glance hierarchy is therefore:
readout → NOW line → proof strip, in that order, at those sizes,
and DAWN's token tiers (§9) exist to keep exactly three things
readable at arm's length in the dark and relegate everything else.
The wheel's monument size is not vanity: at the glance, the dial
is the room's clock-face, and a beautiful instrument is checked
the way a beautiful watch is checked — often, and with pleasure.
That is the retention mechanism he named: prettiness.

**2. The setup (evenings, Sundays, minutes long).** Shield's job.
Here DAWN's bright face works — this is the day face's real
employment, and I judge it by this job: calm, unhurried, every
consequence visible before it is chosen (the census line changes
as the cap moves; the pin marks show what is his). Setup ends with
the cap or a sled; nothing in setup is irreversible except
deleting a routine, which is why delete alone gets a confirm
sentence (§4).

**3. The commitment (rare, seconds long, one-way).** The slide.
Everything in DAWN bends toward making this moment feel like what
it is: the gate tick in the finger at 0.68, the latch knock at
0.97 of travel, then the light-step and the one chord. §5 scores
it. A hand-arm is rare because routines carry the day — DAWN
treats that rarity as correct: the ritual stays special because
it is not spent ten times a day.

**The temperament question, answered straight.** Can morning
light command at 3:30 AM? Yes — the way morning itself commands:
not by shouting but by *arriving, on schedule, indifferent to
negotiation.* DAWN's steel is structural, not chromatic:
(i) the light-step is one-way — once sealed, no control on the
Deck can lighten the world; the only exits are the finish and the
60-second valve, both priced in time, the one currency he respects;
(ii) rose is spent like a coal, never decoration, so its two
appearances (cap, alarm) are obeyed instantly;
(iii) the type never asks — monument numerals, verbs in caps, zero
encouragement copy anywhere in the world.
Where DAWN borrows steel, it says so: the command in the hand is
carried by haptics and the latch (§5), not by the palette; and at
3:30 the face he actually meets is the *night* glass and the desk
clock — the bright face is never on duty at 3:30, and I do not
pretend otherwise. Night commands; day configures. The progression
sky (§7, A5) is what makes them one face instead of two apps.

## §4 (c) HOW selection works — five acts, one mechanism each

DAWN's taxonomy. Every choice in the app is one of five acts; each
act has exactly one mechanism, app-wide, so the hand learns five
things in its life with this app, not fifty.

1. **TURN — a quantity with a grain.** *The wheel.* Session minutes
   (grain 5) turn on the Deck's wheel: drag with weight, flick to
   coast, the seat spring lands it, the knock deepens with the
   block load. Blocks are the wheel's wells: tap a well, the count
   is set — a quantity chosen by pointing at its own picture.
   **Routine times join this act** (below).
2. **SLED — a state among named stops.** App tiers (SHUT / 2m /
   OPEN), sound, the two switches homed in §2, routine ON/OFF.
   A sled is dragged or tapped to its stop; it clicks per stop
   passed and lands heavier at the far stop. DAWN ruling: the
   routine row's ON/OFF cap becomes the same two-stop sled as
   PARK's sound — a state is a sled everywhere, no exceptions.
3. **CAP — a whole posture, one of three.** The loadout selector.
   The cap slides; the arsenal below re-deals. Presentation asks
   one refinement: the rows settle in a single quiet cascade
   (a ~120ms stagger, presentation-only) so the eye sees the
   posture *arrive* rather than finding it changed.
4. **KEY — a verb, pressed once.** DONE, FLOW, ADD, REMOVE, FIX,
   the day letters. Keys are raised glass; a key that is *on*
   fills with ink. A verb never slides and never turns.
5. **SLIDE — a commitment.** The arm, alone. The only selection
   that cannot be undone by its own gesture; the only one wearing
   rose; the only one the machine answers with a chord.

**The routine time-setter (new, designed for Voss's verb set).**
Tap a routine's time column and a glass sheet rises — the sheet
tone, topmost step — headed with the routine's name and which end
is being set ("START — FASTED BLOCK"). The sheet holds the time at
hero size in Anonymous Pro over a single minute-wheel: the Deck
wheel's little brother, one ring, grain 1 minute, the readout
rolling through midnight as it turns — one wheel, one day, turn
to the minute. SET is a key; CANCEL is a word. No steppers, no
typing, no arithmetic: the test Voss was set ("set 05:40 without
arithmetic") passes by construction. Rename: tap the name in the
expanded row → the name becomes a glass field in place, ink caret
(a caret is furniture, never accent — chrome law). Create: a
"+ NEW ROUTINE" key at the section's foot — a verb wearing a body,
not a floating button (DAWN has no floating buttons; nothing
floats but light). Delete: a plain DELETE word inside the expanded
row; tapping it gives the row one sentence — "DELETE SLEEP? IT
WILL NOT ARM." — with DELETE as a key and KEEP as a word. A
destructive verb earns a sentence, never a red flood.

**Ordering (Tasks) is a sixth act, used once: LIFT.** Long-press,
the row rises, light pools under it, it settles where it is let
go. §8 uses it; nowhere else needs it.

## §5 FEEL — the sensory signature, scored moment by moment

The engine already owns a voice (a latch, a harmonic seal, a tear,
bank pitches); DAWN's job under Charter v3 is to make every sound,
tick and silence a *decision with a reason*. The vocabulary is
four haptic words and one rule of pitch:

- **tick** = selection moved (wheel teeth, sled stops, cap travel)
- **knock** = a value seated (wheel seat — deepening with block
  count, as shipped — sled's far stop, cap's arrival)
- **thock** = a commitment booked (the tear, the receipt, a sled
  thrown to SHUT)
- **swell** = the seal, once — a single long haptic under the chord
- **Pitch law:** everything in DAWN holds or rises — except the
  emergency valve, whose banks drain 560 → 470 → 390 Hz as shipped.
  Descent is surrender, and it is the only descent in the app.

| Moment | Touch | Sound | Motion / light | Silence, chosen |
|---|---|---|---|---|
| Wheel turn | fine glass ticks under the finger | tick train, granular | the dial turns with real weight (one integrator, as shipped) | — |
| Crossing the strait to FLOW | the ticks *stop* — teeth are cut there | **none** — the hush IS the strait | the channel floor drops a tone (as shipped) | the gap in the tick train tells the hand it crossed |
| Wheel seat | knock, 16ms at one block → 24ms at eight | one seat knock | two overshoots, ≤520ms, then stillness | after the seat: nothing. A set value does not fidget. |
| Cap slide (loadout) | travel ticks, arrival knock | a low wooden slide, latch at arrival | rows re-deal in the 120ms cascade (§4.3) | — |
| Sled throw | click per stop, heavy landing at SHUT | click … thock at the far stop | the sled's own travel, nothing else moves | — |
| The slide (arm) | grain rising to the gate; the gate tick at 0.68 in the finger; latch knock at 0.97 | one latch | the cap crosses; prints latch at DOWN and hold | **proving is silent** — the machine is working; a working machine does not narrate |
| **The seal (t=0)** | the swell, once | **the harmonic seal — the only chord in the app** | the ground steps down at t=400; the hero lands by 1050ms; scroll dies for exactly the shot's span | before t=0: no sound at all. The chord is believed because nothing preceded it. |
| Jam | nothing | **none** | the sentence prints | a jam is a fact, not an alarm. The phone does not scold. |
| Annulment | one reverse tick | one quiet reverse tick | the slide is ordered home | an un-happening is quiet by law |
| Mid-session | — | **none, by design** | the track advances; the sky holds its night | hours of it. The app's loudest statement is that it says nothing for three hours. (Break notes are an addition, §7 A4 — opt-in by the existing sound sled.) |
| Task DONE (the tear) | a short resistance, then release | one tear pulse — paper, once | the row settles 260ms as the booking lands (the booking now precedes the sound — Truth Car 2's G-01 order, which DAWN seconds: feel must never precede fact) | no jingle, no echo |
| Finish (detention) | stillness | **none** — the held frame is a held breath | every print frozen on the frozen figures | ~a second of it, on purpose |
| Receipt issued | one thock | one low thock, once — completion fires once because it is a persisted transition, not an animation (Fen's cadence law, adopted) | **the light returns** (§7 A1): the ground crossfades toward day over the sled's own 1050ms home | after the thock: nothing. The slip is read in quiet. |
| Valve (60s hold) | continuous pressure, banks catching at 15/30/45 | falling pitches, 560→470→390 | the bar drains; grace 800ms, then 1×, then 2× | the token spends at 60.0 exactly, in the same act as the disarm — no fanfare for surrender |
| Desk clock | — | none, ever | numerals drift ±6dp against burn-in; the horizon line fills | a clock that ticks at a sleeping man is a failed clock |
| Routine self-arm (05:00, 20:00) | — | the seal voice, as shipped and benched — presentation takes no position on its machinery (Voss's audit owns the chain) | the world is already night when he looks | DAWN's law, stated for the record: a self-arm never *adds* a sound the hand-arm doesn't make; the machine does not perform for an empty room |

**How morning light feels commanding at 3:30 AM, in the hand:**
the phone is a cold glass object in a dark room. He slides; the
grain resists; the gate ticks *in the finger*; the latch knocks;
then the room's one screen gets **darker and quieter** and stays
that way, and a single swell under a single chord says the machine
has taken the watch. Command, in DAWN, is the feeling of a door
closing softly and not opening again — weight, finality, and then
hours of silence. Anything louder would be theatre.

## §6 EVERYTHING INTENTIONAL — the accident audit

Inherited accidents found in DAWN's own surfaces, each disposed:

1. **The hero's press ghosts** (two ±1dp misregistration copies;
   day catch-ghost #EAC4AC — literally peach, the dead skin's DNA
   in my own render). Print-shop mannerism inherited from the
   machine era. **Disposition: die, both modes.** Glass does not
   misregister; DAWN's numerals are single renderings.
2. **The % numeral beside the progress track** — the fraction
   rendered twice (bar + number), a habit, not a decision.
   **Disposition: die.** The bar is the rendering.
3. **The eyebrow's day shadow** (a 1dp white drop-shadow faking
   print lift, in TactileDeckScreen's eyebrow style).
   **Disposition: die with the ghosts.** DAWN type sits on light
   by contrast (14:1, §0), not by shadow.
4. **Two enforced switches with no surface** (Shorts & Reels,
   browser blocker — wired, persisted-or-not, rendered nowhere).
   **Disposition: homed** (§2). An invisible setting is an
   accident that behaves like a lie.
5. **The routine ±30 steppers** — inherited from routines v1,
   already sentenced by his "you cant even edit them". Their
   replacement is designed, §4. **Disposition: die, replaced.**
6. **Ripple.** Purged in chrome (Round 1c) before DAWN existed;
   audited in-lane: no ripple survives in Deck/Shield.
   **Disposition: confirmed dead; DAWN's presses answer with a 6%
   ink wash or nothing.**
7. **The 12dp spacing / 24dp hero radius grid** — inherited.
   **Disposition: ratified, now with a reason:** the radius is the
   glass pane's corner, the spacing is the grout between panes.
   Chosen, from today.
8. **Anonymous Pro's tabular figures** — inherited with the font.
   **Disposition: ratified with reason:** numerals that never
   change width are why the running readout doesn't jitter. This
   is load-bearing feel, kept on purpose.
9. **The slide hint's dimness at night** — measured, not guessed:
   the hint sits in the secondary tier at **6.22:1** on the
   night ground (measured; corrected per Salz DAWN-3 — an
   earlier draft carried the token comment's stale 5.8 figure,
   which reproduces nowhere). **Disposition: chosen.** A hint
   should be findable, not loud; 6.22:1 is the tier working as
   designed, and the disposition stands more honestly on the
   true number.
10. **The ratchet banner's rose flood** — the one flood in a
    no-flood world. **Disposition: ratified as the named
    exception** (grammar §1.6): an alarm is the machine
    committing to refuse; it may spend the rose once.
11. **Default scroll behaviour** — the Deck scrolls, Shield lists.
    **Disposition: ratified:** the Deck is one instrument (scroll
    is reading), Shield is an arsenal (a list is searching). The
    seal kills the Deck's scroll for exactly 1050ms and not a
    millisecond longer — that precision is the standard the rest
    is held to.
12. **Sound on typing** (search, site field, rename): none exists.
    **Disposition: ratified silence.** Keyboards are not
    instruments.

## §7 ADDITIONS — invented, each priced honestly

**A1. The light-return at the finish.** When the receipt issues,
the ground crossfades from the session's night toward the day sky
over the sled's own 1050ms return, so the machine lets go of the
room at the same speed it lets go of the slide. *Cost:
presentation-only — the Deck already recomposes on the receipt
state; ~30 lines, one brush transition, no machinery, no data.*
The single most DAWN moment in this spec, and the cheapest.

**A2. LAST ROUTINE CHECK HH:MM** (§2). *Cost: not mine to price —
Voss's F5 in the no-fire fix spec; the stamp already exists in
the DAO. Presentation cost: one mono line + the blank-when-
unmeasured law.*

**A3. Pin marks on app rows** (§2). *Cost, corrected per Salz
DAWN-2 (the original "zero machinery" was fantasy and is
withdrawn): provenance is not a stored fact — one tier column
exists and the loadout writer overwrites it wholesale. The mark
needs an origin fact: one append-only column on the apps table
(hand / loadout), written at the row's set and at
applySmartLoadout, plus its migration in the chain's next
version. Small, real machinery — a schema change, two write
sites, one comparison in the row view, one legend line. The
addition survives its true price; the zero does not.*

**A4. Break notes.** One soft glass note at a Safe Break's start,
one at its end — for a man whose eyes are on a book and whose
phone is across the desk, the break should be audible, not just
printed on an eyebrow he isn't looking at. Obeys the existing
SOUND EFFECTS sled (no new state). *Cost: small machinery — the
break transitions are already observed by the view-model for the
eyebrow print; a sound call at each transition, ~20 lines, plus
a harness assertion that no note fires mid-block.*

**A5. The progression sky.** The sky brush keys to the phase of
*his* day: pre-dawn night glass (03:30–05:00), the sunrise stops
(FASTED), full day (DAY), the ember hour (19:30–20:00), banked
dark (SLEEP) — five stop-sets of the one gradient, chosen by the
device clock, overridden by the session's night step exactly as
today. The app wears the same sky he studies under; the "two
apps" objection (night commands / day configures) dissolves into
one continuous face. *Cost: presentation-only — a when() on the
hour over five gradient stop-sets, ~60 lines, all token work,
mine. The fact it prints (the time of day) is read from the
device clock — a real fact, lawfully printed.*

**A6. The routine time-setter sheet** (§4). *Cost: the largest
item here — a new sheet + a one-ring wheel variant; call it a
builder-day. Data path is Voss's (absolute set replacing ±30);
this spec prices only the presentation and says so.*

**Refused additions, for the record:** streaks, day-strips,
counts of days, any weekly rhythm rendered as a score — the
Drawer stores and does not grade (his verdict), and a rhythm
graphic is a ledger in disguise. DAWN invents nothing that
keeps score.

## §8 The other surfaces, in DAWN's grammar

**Tasks.** The sky ground (the tower-fader browns in phase 1's
Tasks render are outside the phase-1 commission and die in the
token pass). The HORIZON bar **dies**: its proportion was priced
in task minutes, and his verdict ("remove minutes — it has no
use") killed its only input; a bar proportioned on nothing would
be the screen's one lie. Its height goes to the queue. The
composer: a name field and an ADD key — no duration bank, ever
(the bank is sentenced; §2's grammar merely buries it). Queue
rows are glass slips: the title in Atkinson, sub-steps as a row
of small wells lit as they are done (a count rendered as light —
the fraction exists, so it may render; the *minutes* never did
again). The active row carries the ink. Reorder is LIFT (§4).
Completing a task keeps the adopted tear motion; in DAWN the
tear's second half is light: the row fogs and lifts upward as it
leaves for the Drawer — booked first, sounded second (G-01's
order), 260ms as specced by the Finishers.

**The Drawer.** The archive is *indoors*: DAWN's Drawer is the
one surface where the sky is covered — deep violet cabinet tones
even at noon, because a drawer you open rarely should feel like
opening furniture, not like another screen. Stored tasks print
as paper slips (grammar §1.5): title, the day it finished, its
sub-steps struck as they were left. Months are index words, not
cards. Search is a glass field at the head. The day's declared
word stands at the Drawer's head like a stamp on the cabinet
door — it may print (REST/ILL/FAMILY are the closed honest list);
it is never graded, never totalled. The cabinet of curios: a
forged piece is a paper slip *in* the cabinet; possession is the
state, so there is no SELECT and no "HARMONIC SYNTH UNLOCKED"
(both already sentenced as dead prints, DRW-06/07 — DAWN's
grammar merely notes they could never have lived here: a verb
with nothing consuming it is a lie in any material). The fake
REQUIRES line dies with Truth Car 2 (DRW-01); DAWN prints the
true rule in the same mono: one piece per finished session.

**The receipt.** The loved surface, and DAWN's treatment is
mostly *protection*. Every field stays; every omission law stays
(the HELD line absent when uncaptured; BREAK BANKED only when
true; FLOW's PLANNED dies with RC-02, which DAWN seconds — a
stopwatch has no plan and its slip must stop inventing one; ENDED
prints witnessed times only once RC-01 lands, and DAWN's paper
does not get to look more certain than its facts). What changes
is the surround: the slip issues over the returning light (A1),
on dawn paper — warm-white stock, plum ink, and **rose never
touches paper** (the № stays ink; a number is a fact, not a
celebration). The receipt is the sunrise, printed — the one
object in the app he keeps. The bar for DAWN was set by his ❤️:
at least this good. In this grammar it is the *only* opaque
thing in a glass world, and that is exactly why it reads as
precious.

**The desk clock.** DAWN's home ground, and mostly already
right: true black, span-remaining in 96sp Anonymous Pro, the
task banner beneath, the ±6dp burn-in drift. Three DAWN rulings:
(i) the numerals take the night glyph tone (#DDD3F8) — they
already inherit the night tokens in-lane; keep, and no ghosts,
ever, on the clock face; (ii) the 16dp progress *trough* is
machine-era furniture — DAWN replaces it with a 2dp **horizon
line** of light under the numerals, filling left to right: the
session as a sunrise that hasn't happened yet. Presentation-only;
the fraction is the shipped span derivation, unchanged;
(iii) the eyebrow ("NIGHTSTAND DESK CLOCK • TAP ANYWHERE TO
EXIT") drops to the muted tier — instructions for a door he
already knows should whisper. On Nacht's AOD luminance law:
DAWN files no compliance claim from a token — the frame's total
emission is a capture's business, and Linea's captures at the
pick (OPEN-LOOPS L05) are the court. DAWN's bid is thin-stroke
type, one horizon line, and nothing else lit.

## §9 The GAP register — DAWN night, measured (Nacht's method)

Computed from the shipped night tokens (relative luminance,
sRGB), not asserted. Filed because the method was adopted for
every finalist; DAWN files early and plainly.

| # | Pair | DAWN night | Reading |
|---|---|---|---|
| N1 | recess ↔ ground (#0E0A22 / #171131) | **1.07 : 1** | Shallow — the same class Nacht flagged in the old night (1.05). At this depth luminance ratios compress toward 1 no matter the token (a priced deepening to #05030F buys only 1.13). DAWN's honest mitigation: recesses are *wells only* (search, add-site), always hairline-edged, and never a step the eye must read as elevation. Named, not hidden. |
| N2 | block bar on ground (#0C0719 / #171131) | **1.10 : 1** | The bar is print, not a step: its text carries 11.26 : 1. The band is read by its words and its seam stamps, never by its lift. |
| N3 | sheet over raised (#463A70 / #2D2254) | **1.43 : 1** | A real super-sheet step exists (the old night had none). Sheets sit decisively on top. |
| N4 | secondary text on sheet (#9C90C6 / #463A70) | **3.44 : 1** | **Too weak, and DAWN rules on itself:** night sheets carry ONE text tier — primary (6.23 : 1 on sheet). The secondary tier is abolished on sheets; it lives on the ground only, where it measures 6.22 : 1. Nacht found "no secondary tier on sheet" as a defect of the old skin; DAWN adopts it as law rather than repeat it as an accident. |
| N5 | muted off-ground (#8F84BA / #171131) | **5.32 : 1** | A true muted tier exists off-ground (the old skin's collapsed entirely). Furniture can whisper without vanishing. |
| N6 | staircase: raised/ground **1.27**, sheet/raised **1.43** | — | Two honest steps, no phantom mid-step. The staircase is shallower than the old locked 1.35 first step; DAWN accepts the trade — the night ground is brighter by design (the light is *in* the glass), and the glyph tier (10.04 : 1 on raised) pays for it. |

Day side, for symmetry: ink on the hero glass measures 14.07 : 1
(top composite) and 13.19 : 1 (bottom composite, after the
dissolve's thinning — recomputed on the phase-2 brush). The
bright face's risk was never reading. It was resemblance, and
§0 answers it.

## §10 What DAWN refuses

- No scores, streaks, totals or grades — anywhere, invented or
  inherited. The Drawer stores; YPT and GMC keep his records;
  Monolith blocks. (His verdicts, restated as presentation law.)
- No minutes on tasks. No plan on a stopwatch's receipt. No
  requirement on a curio that no code enforces. No verb with
  nothing consuming it. Nothing printed that no fact backs.
- No green, ever — every DAWN token satisfies G < max(R,B),
  audited in phase 1 and unchanged by the dissolve (alphas moved;
  hues did not).
- No AI imagery, no stock pictures of sunrises. The sky is a
  gradient the code draws; the light is arithmetic or it is
  nothing.

## Risks, plainly

- **Rose discipline is the whole ballgame.** If rose ever spreads
  past the cap, the alarm and the №-that-isn't, DAWN goes pink and
  dies cute. The fence is grammar §1.6; hold it in every future
  surface or the direction rots from inside.
- **The day face can still lose on resemblance** if the panel's
  judges read any pale glass as porcelain. The dissolve is my
  answer; the render is the argument; if it fails his eye, it
  fails — I would rather lose honestly than warm the tokens and
  call it a new app.
- **Gentleness remains the standing question** until the finish's
  light-return and the seal's swell are felt on his phone. Renders
  cannot prove feel. F-G1's gates, on device, are the court.

— Dawn, Oct 29, ~20:40 IST

## §11 REFINEMENT — the ring's fire, answered (Oct 29, evening)

Four letters landed during this commission (Haut, Wetter, Zunder,
Klinge). Every charge is answered here in words, tokens, or a
render. Where I amended, the amendment is in the lane's code, not
just in this file.

### Klinge (Critics) — charges D-1…D-4

- **D-1 CORPSE RHYME (hero #FFF9F0 vs the corpse's #FAF6F0).**
  The phase-1 measurement is conceded — the pane was porcelain's
  neighbour. The answer is the phase-2 render
  (`phase2_deck_bright.png`, built and viewed after Klinge
  sampled): panel thinned, rim glassed. Her sub-charge — that my
  §0's cited proof predated the commission — was true of the file
  she sampled and is corrected in §0's addendum, which also
  confesses the half-dissolve rather than dressing it. The
  remaining full-dissolve step was priced there; the ring then
  priced it again (Zunder 3), and I took it — see the amendment
  round below.
- **D-2 TAN FURNITURE (#F2D2B4 on the strip, FLOW chip, sled
  tracks, search field).** TRUE, and unanswerable in words — a
  beige deeper than the corpse's own tint, on everyday furniture,
  given no reason by either filing. **Amended in tokens, both
  families** (the Compose token and the canvas-view constant
  carry duplicates): `surfaceVariant` #F2D2B4 → **#E3CBDA**,
  rose-lavender mist — the sky's own shadow. Every surface it
  dressed changes in one move, because one token was the
  accident's whole extent.
- **D-3 FEEL FRAUD (the phase-1 sentence: warmth "drains out of
  every surface, so nothing reads cream").** **The sentence is
  retracted.** D-1/D-2 are its measurements: warmth had drained
  from the ink and the ground while the furniture kept the
  corpse's warmth, and the sentence claimed the whole. A letter
  may be optimistic; it may not be false — the correction of
  record lives here and in my closing letter to Ren, since sent
  mail is not rewritten. The amendments in this round are what
  make the retracted sentence approach true; they do not
  un-write it.
- **D-4 THE BRIGHTENING EMBER (night accent #F2728C lighter than
  the day flame #D9385E, sold as "the last coal").** Conceded as
  written: embers do not brighten, and the metaphor covered a
  token change I had not admitted. **Amended in tokens, both
  families:** #F2728C → **#E85A7C**, stepped down toward the day
  flame. It remains one step lighter than the day token for one
  stated, checkable reason — the cap is the commitment control
  and must be findable on a near-black ground in a dark room —
  and the metaphor is replaced by that reason wherever the token
  is documented. Poetry that hides a decision is a lie with good
  diction; the decision now stands in the open.

### Haut (feel) — three amendments, all taken

1. **Ramped onsets re-specified.** The seal's swell is no longer
   an amplitude ramp: it is a **two-beat timing pattern** (short
   40ms — gap 60ms — longer 180ms), amplitude shading it only.
   Shape by time, colour by amplitude, per the feel law — cheap
   ERM motors that cannot start at 25% amplitude will still
   deliver the *shape*, which is the information. §5's swell row
   is amended by this paragraph wherever the two disagree.
2. **The seat knock has a floor: amplitude ≥ 0.7**, however
   gentle everything else gets. A proof event he cannot feel at
   3:30 AM is a lie of omission — accepted whole, and written
   into §5's vocabulary: knock = duration scales with load
   (16→24ms, as shipped), amplitude never below 0.7.
3. **The sky is a feature, not a token** — accepted, and merged
   with Zunder's turn (below): A5 is re-priced as a feature
   riding real state.

### Zunder (Wild) — three pushes

1. **THE SKY KEEPS HIS HOURS — taken, with his turn.** A5 as
   filed keyed the sky to the clock; Zunder's version keys it to
   his **routine windows** — the phases change when FASTED / DAY
   / SLEEP arm, because the app already holds those windows. At
   20:00 the glass goes ember-dusk because SLEEP arms, even when
   the real sky disagrees: a hundred apps tint by time of day;
   only this one knows his blocks. A5 is amended accordingly and
   re-priced: presentation riding the routine state the app
   already publishes — a feature's price, not a token's, and
   DAWN's strongest feel claim, argued as such. It also sharpens
   §3's steel: at 3:30 the world is not dawn at all — it is the
   held breath before it, near-black violet; brightness is a
   promise the fasted block keeps.
2. **DEFROST — taken, fenced.** Frost that clears as the session
   elapses is this material's native verb, and no rival direction
   can say it. Fence, for the glance's sake: the frost lives on
   the hero pane's **rim only** and retreats outward; the readout
   zone is never frosted, at any fraction. Frost remaining =
   span remaining, read peripherally, zero digits. Filed as
   addition **A7**. *Cost: medium, stated plainly — a radial
   mask driven by the verified elapsed fraction the Now Frame
   already publishes; presentation plus one mask, no new data.*
3. **KILL THE PANEL — taken.** My own phase-1 letter named this
   the second commission; the ring has now commissioned it twice
   over. In the amendment round: the hero brush's day stops go
   to **0x59 → 0x00** (a breath of pooled light at the top,
   nothing at the foot), the disc alpha drops **188 → 140**, and
   the dial numerals step up to **full ink** by day so they hold
   on glass over a moving gradient. The hairline edge stays —
   etched, not framed (Wetter's etching, §below, is what it
   becomes). If DAWN falls, it falls as a sky.

### Wetter (Wild, cross-breeding) — transplants and the Held Door

1. **The power cut (Noir's day principle) — REFUSED, with
   respect and a reason.** Flattening the day hero into print
   would give DAWN a borrowed day: flat graphic daylight is
   Signal's and Noir's grammar, and a direction that survives by
   adopting its rival's day principle has no day of its own.
   DAWN's day answer is the opposite move — not power off, but
   *glass opened*: transparency, not flatness. The risk Wetter
   names (flat = surrender) is real for his version; the risk of
   mine (pale = porcelain) is the one §0 and the amendment round
   are busy answering in renders. One of us is wrong in a way
   only his eye settles; the disagreement is recorded, not
   smoothed.
2. **Frost depth — TAKEN.** One frost strength for hero, strip
   and pills was a topography failure. DAWN now runs a depth
   order: the hero is the clearest glass on screen (post
   panel-kill, nearly clear), the proof strip sinks into the
   deepest frost (it is a *record* — records sit deep, §1.5's
   paper in glass form), controls between. Two frost levels
   minimum, implemented as the hero brush + the strip's glass
   fill in the winner's pass; the D-2 token already moves the
   strip's furniture into the sky's family in this round.
3. **Etch the small print — TAKEN.** Blueprint's discipline,
   translated: hairline rules etched under Shield's sections,
   secondary text at **one fixed softer plum token** (the alpha
   habit dies — an alpha is a mood, a token is a decision), and
   figures in Anonymous Pro aligned like dimensions. Spec-level
   adoption here; the token pass is priced with the winner's
   Shield pass.
4. **THE HELD DOOR — the challenge is accepted, and Wetter is
   right that it was undesigned.** The interception is the
   surface he sees most: dozens of times a week, mid-block, he
   opens a SHUT app and meets the shield's refusal. DAWN's Held
   Door, designed now: **the blocked app behind frosted glass** —
   the full screen goes to deep frost over the sky; behind the
   frost, the app's own colours blur, present and untouchable;
   etched on the frost, in order: the app's name as a fact
   ("INSTAGRAM — SHUT"), the remaining span at hero size (the
   Now Frame's figure, the same one the Deck swears by), the
   NOW task line, and one verb — the way back. Nothing else is
   tappable; longing is shown its object and its price in the
   same frame, kindly, without a lecture. *Machinery honesty:
   the intercept overlay exists (the shield service draws it);
   this is its presentation — but my harness cannot render a
   service overlay, so DAWN files this as spec, with the render
   OWED at the winner's stage, not faked here. Whether every
   finalist must render its Held Door before a pick is Ren's
   ruling to make; DAWN's answer to the challenge is filed
   either way.*

### The amendment round (code in lane; render retried, then owed)

In the lane, after the ring's letters: hero day stops 0xD8/0x8C
→ 0x59/0x00 (panel-kill); disc alpha 188 → 140; dial numerals to
full ink by day; `surfaceVariant` #F2D2B4 → #E3CBDA (both token
families); night accent #F2728C → #E85A7C (both families).
Render status, stated exactly: the first proof run (the
half-dissolve) is on disk — `phase2_deck_bright.png`,
`phase2_deck_dark.png`, BUILD SUCCESSFUL. The amendment round's
own render was queued behind Signal/Temple/Obsidian builds and
its queue claim timed out at 25 minutes on a strangled box; a
retry is running as this is filed, and until its frames land the
amendments stand as **code in the lane, unrendered** — the same
honest state Obsidian filed his deepening in, and for the same
reason: I do not point at pixels I have not seen. When the retry
lands, the harness overwrites `shots-own/01_deck.png`,
`02_shield.png` and `night/n01_deck.png` in place; the verdict
line and the `phase2b_*` archival copies follow from whoever
reads them first — Ren's cull or my next seating.

## §12 SALZ — four charges, answered (Oct 29, later)

Salz verified §9's GAP six as reproducing exactly and the N4
self-ruling as law used correctly — recorded with thanks. The
four charges:

- **DAWN-1 (the slip's colours).** Agreed and closed: the
  receipt sheet reads the colour scheme, so DAWN's tokens
  recolour the slip; my phase-1 "survives intact" was true of
  its machinery and false of its colours. §8 argues the dawn
  paper openly, which is the correction. The record stands
  corrected here too.
- **DAWN-2 (the pin marks' price).** **The charge is right and
  the zero is withdrawn.** Provenance is stored nowhere; the
  entity keeps one tier and the loadout writer overwrites it.
  A3 is repriced in place (§2, §7): an append-only origin
  column, two write sites, one migration. The addition is
  good; its price is now honest. A price I invented because it
  was convenient is the same species as a fact I invented —
  noted against myself.
- **DAWN-3 (the stray 5.8).** Corrected wherever it travels:
  §2's slide row, §6.9, and both stale token comments in the
  lane now carry the measured **6.22:1**. The disposition that
  stood on the wrong number (the hint, chosen) survives on the
  right one.
- **DAWN-4 (name the receipt's voice).** Named, from the
  shipped code: issuance speaks on the finish's E-clock — a
  seat knock at E0, **the completion pulse at E700 only where
  `finishPulses(kind, row)` admits it** (the register's
  reservation, implemented in the data layer: verified
  fully-elapsed finishes; death-booked finishes stand with the
  E700 slot EMPTY), and a knock pair at E880. So the voice I
  called "one low thock" is three placed sounds, and the pulse
  among them already obeys the law Salz cites — it does not
  spend on aborted or shield-gap finishes. §5's finish row is
  corrected by this paragraph: the thock is the E0 knock; the
  pulse is the register's reserved note, spent lawfully or not
  at all; surrender gets knocks and silence where the pulse
  would be. DAWN ratifies the shipped law and strikes its own
  simplification.

Two observations, both taken:
- **The §4.3 cascade gets a total-span cap:** the stagger is
  specified so the LAST row settles within ~300ms regardless of
  count — per-row delay shrinks as the arsenal grows. A wave
  that takes a second is a slow wave; the cap is now in the
  build note.
- **A5's naming drift** belongs to the clock-keyed version Salz
  read. A5-as-amended (§11, Zunder) keys the sky to the arm
  *state*, not to clock bands wearing routine names — so a
  retimed routine moves the sky with it, because the state is
  the fact. The drift dies with the version that carried it;
  recorded so the merge doesn't resurrect the clock form.

— Dawn, Oct 29, ~21:10 IST (spec §§0–12 complete)
