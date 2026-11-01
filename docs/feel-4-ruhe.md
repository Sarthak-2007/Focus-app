# FEEL — THE EXIT MOMENT (Issue #11)

Seat: **RUHE, Feel Specialist 4**, Tor's cell · For: Tor (lead)
Slice: the exit only — FIX-8's honest exit line
(`END THE SESSION INSTEAD`) under RETURN, the handoff into the
Disarm face, the first second on that face, the way back via
`KEEP FOCUSING`, and the plain RETURN dismissal. Not the wall
(Atem's), not the skeleton's slot stack as such (Designer's),
not the valve's 60 seconds (another seat's, when it is seated).
Ground read, whole or in the relevant part:
`studio/plan/AUDIT-held-door.md` (FIX-7, FIX-8, the honest exit's
own paragraph; Halbwach I-4);
`docs/designer-skeleton.md` §2, §3.1, §6, §7, §8 (shell, slot
stack X at 36dp, RETURN slab, the §7 wiring, the state matrix's
RETURNING / EXIT TAPPED rows);
`docs/innovator-proposals.md` P-2 (the door turns — standing
beneath, the never-no-door law, the stopwatch trap, completion
clearing the door, the 48dp row);
`docs/feel-5-druck.md` §5 (D-F16 register, D-F17 the bait-switch);
`app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt`
(`AirlockInterceptOverlay` :550 — entry `fadeIn(specB())` today,
exit `fadeOut(specA())`; `DisarmChallengeOverlay` :732 — entry
`fadeIn(specC())`, exit `fadeOut(specA())`, ground
`NightColorBackground` @ α0.94 :745, card padding 22dp);
`app/src/main/java/com/example/ui/MonolithViewModel.kt`
(`requestDisarmChallenge` :2236, `dismissDisarmChallenge` :2263,
`confirmDisarmSession`, `completeEmergencyBreakFromValve`,
`transmuteImpulseAndReturn` — RETURN's handler);
`app/src/main/java/com/example/MainActivity.kt` :793–808 (both
overlays composed in one host, Disarm **after** Airlock);
`ui/theme/MonolithCurves.kt` (`specA` = `tween(EasingA)`,
default 260ms; `EasingA` = CubicBezier(0.2, 0, 0, 1); `specC` =
280ms on `EasingC` = CubicBezier(0.3, 0, 0.8, 0.15));
`ui/views/BlockitSuitcaseAndSwitchesViews.kt` :532
(`Emergency60sValveView` — banks every 15s, drains what is not
banked, sounds only once a hold is actually gripped).
**Nobody compiles.** Every number is quoted from the audit,
the skeleton, or the code, or flagged as mine for Tor.

---

## 0. The one-sentence verdict

> **Leaving must feel like the door was never locked from the
> inside.** Both exits — RETURN, and the quiet line beneath
> it — are present from the first frame, neither is announced,
> neither is punished, and neither ever makes him pass through
> an empty moment to reach the other side. A door he can leave
> two ways without a gap, a lecture, or a trick is a door he
> stops testing.

Three laws carry the slice. Everything below is application.

- **E-LAW 1 — NEVER NO DOOR.** From the tap on the exit line
  until he is standing on the Disarm face — and on every path
  back — there is **no frame** in which neither face stands:
  no blocked app, no Deck, no home screen, no bare Night
  ground. A single gap frame delivers the reward the whole
  door exists to withhold (it is FIX-7a's sin, committed at
  the exit instead of the entrance), and it teaches the worst
  lesson this face can teach: *the small line is a hole.*
- **E-LAW 2 — QUIET IS A FORM, NEVER A HIDING.** The exit line
  is quiet because it has no fill, no border, no badge, no
  accent and the last position in the reading order — never
  because it is dimmed, shrunk, delayed, or placed where a
  tired eye will miss it. An exit he has to hunt for is a
  secret; an exit that shouts is a suggestion. The line must
  be **findable in one glance, ignorable for a whole visit.**
- **E-LAW 3 — A LOOK COSTS NOTHING.** Going to the Disarm face
  and coming back books nothing, spends nothing, lengthens
  nothing, and changes nothing he left behind. The round trip
  is a man turning a door's other face toward himself to read
  it — if looking were priced, he would learn never to look,
  and the exit would rot into the one thing it must never be:
  a commitment device wearing a footer's clothes.

---

## 1. The footer line's presence — weight, alpha, distance

### 1.1 What stands (the Designer's §7, adopted)

Slot **X**, in the fixed stack, below RETURN in every visit
and every state — COUNTING, OFFER, HELD, NEVER-OWED:

| Property | Spec | Source |
|---|---|---|
| Print | `END THE SESSION INSTEAD` | FIX-8, verbatim |
| Type | Atkinson Hyperlegible **Bold, 12.5sp**, centred | Designer §7 |
| Ink | `NightColorPrimary` #FFE4CF @ **α0.92** | Designer §7, F-10's body alpha |
| Dress | No fill, no border, no icon, no badge, no chip shape | Designer §7 |
| Position | RETURN slab (56dp) → **12dp gap** → **X slot** → **8dp gap** → footer badge word (16dp) | Designer §3.1 |
| Life | Composed at frame one, live in every state, never gated by the wall, the taper, the offer, or the withheld state | extends §8's matrix |

### 1.2 Why α0.92 and Bold — the two easy wrong answers, refused

There are two temptations for a "quiet" line, and both are
failures of nerve:

- **Dim it** (α0.6, or the footer badge's
  `NightTextColorSecondary` #9C8E84). That makes the exit
  *harder to find than the badge word that names the door's
  machinery* — the one line on the face that is for *him*
  would sit in a quieter tier than the system's own label.
  At 1 AM, under pressure, a dimmed exit does not read as
  discretion; it reads as the app being slightly ashamed of
  its own override, and a man who has to squint for the way
  out feels exactly as jailed as a man who is given none.
  α0.92 is the face's ordinary body alpha (F-10 raised every
  body line to it for legibility); the exit is a body line
  and takes the body alpha. **Its quietness is spent entirely
  on form and position (§1.3–1.4), where quietness is honest.**
- **Soften the weight** (Regular, or sentence case by my own
  hand). Bold at 12.5sp is not loud — RETURN is Sora Bold
  15sp on the face's only accent fill; against that, a bold
  cream word on bare card is a murmur with its consonants
  intact. Regular at this size, centred under a 56dp slab,
  thins toward the badge row and blurs into it. On Druck's
  D-F16/D-F8 register proposal (sentence case for body lines):
  that is a whole-face voice ruling for Tor, and if it lands,
  this line follows its class — I do not unilaterally
  re-case an audit-verbatim string from a feel seat. Recorded
  at §6, R-2.

### 1.3 Distance from RETURN — the 12dp is the whole argument

The gap arithmetic is small and it is doing all the work of
"never competes":

- **12dp below RETURN** — three times the card's tight
  internal gaps (4dp under the name, 4dp under the intention),
  less than the 16dp that separates the peek zone from RETURN.
  Read as grouping: RETURN belongs to the *action stratum*
  above it (it sits 16dp under the zone it answers); the exit
  line belongs to the *footer stratum* below it (badge word
  8dp beneath it, card padding beneath that). The eye groups
  the line with the footer, not with the slab — it is
  furniture at the door's foot, not a second button under
  the first.
- **It is last in the reading order that matters.** A man
  reading top-down meets: the name of what he reached for,
  his own words, when the session ends, the gate or the
  offer, the price, and then the loudest object on the face —
  RETURN. Only a man who has passed RETURN without taking it
  arrives at the exit line. That is precisely the man the
  line exists for. It never intercepts the first decision;
  it answers the second one.
- **Its target must not touch RETURN's.** RETURN's slab is
  56dp; a travelling thumb that overshoots its bottom edge
  falls into 12dp of dead card before it can reach the exit
  slot. That dead band is a safety margin, and it must stay
  dead: no part of the exit's touch target may borrow it.
  An accidental tap on RETURN costs nothing; an accidental
  tap here changes faces. The asymmetry of the two errors is
  why the gap exists at its size and not at 4dp symmetry.

### 1.4 The one geometry change I ask for — the slot grows downward

The skeleton prices X at **36dp** (Designer §7); the Innovator
prices its touch row at **48dp** (P-2e) and, on feel, the
Innovator is right: under urge pressure, with a shaking hand
at 1 AM, 36dp is a precision instrument, and this line is the
one deliberate act on the face that is currently *harder to
hit than it is to mean*. The reason this seat can ask for the
change without touching the skeleton law is the stack's own
order:

> **X sits below RETURN. Growing X moves nothing above it.**
> RETURN's top (266dp in the peek-owing card), its centre,
> its bounds — pixel-identical. The growth is paid entirely
> by the card's bottom edge: card heights 418 → **430dp**
> (peek-owing) and 292 → **304dp** (never-owed); the footer
> badge drops 12dp; F-2 cannot regress through this slot
> because no thumb is ever travelling *downward past* RETURN
> toward a control that the growth displaces mid-visit — the
> branch is fixed at frame one, within-visit bounds constant.

Spec, for Tor's ratification (§6, R-1): **X slot 48dp**, the
12.5sp glyph line centred in it, the full slot height and the
full content width tappable, the 12dp gap above and the 8dp
gap below unchanged. If Tor holds 36dp, the rest of this file
stands unamended — 36dp is viable, 48dp is kind. What is not
viable at either size: a target the size of the glyphs.

### 1.5 What the line never does

- **Never arrives late.** Not faded in at wall-end, not
  revealed after a failed peek, not offered as a consolation
  when the withheld line prints. A line that *appears* reads
  as the door suggesting it, at the moment it appears. It
  stands from frame one so that at every second of the visit
  it is a fact he already knows, not an idea the door just
  had. (Druck's D-F16 names the residual cost honestly — a
  standing suggestion exists on every visit. Frame-one
  permanence is also the mitigation: a permanent fixture is
  furniture; a timed reveal would be a pitch.)
- **Never animates.** No pulse, no underline sweep on hover
  of the eye, no emphasis change when the wall expires or
  the chip arms. Its register at wall = max and at OFFER is
  the same register.
- **Never carries a price, a count, or a consequence.** No
  badge, no "(1 THIS WEEK)", no token arithmetic, no
  preview of the valve. The audit's sentence is the law of
  this slice: *a route, not a spend; the valve's own face
  carries the gravity* (§3.3 answers Druck's D-F17 — the
  gravity must be *felt arriving*, since it may not be
  *printed here*).
- **Never disables.** On a spent week, on a stopwatch, on a
  NEVER-OWED door — the line stands and routes, and the
  Disarm face itself carries which world he is in (§3.4).
  A dead or absent exit on exactly the visits where ending
  feels most necessary would be the face lying by omission
  about its own override.

---

## 2. The tap — what the first 100ms promise

The tap on X calls the door-originated Disarm entry
(Innovator P-2c's `requestDisarmChallenge(fromDoor = true)`,
§6 R-3). In the first instant, three things happen and a
fourth conspicuously does not:

1. **The latch voice sounds** — `requestDisarmChallenge`
   already plays `playMechanicalLatch(false)` (VM :2256).
   Keep exactly that voice, no new sound. It is the right
   register by accident of the tree and by design here: a
   latch is a door *turning*, not a seal breaking and not an
   alarm. RETURN's voice is the harmonic seal
   (`transmuteImpulseAndReturn`, arming = true); the exit's
   voice must never borrow it — the seal is the sound of
   staying, and a man must be able to tell the two taps apart
   with his eyes closed.
2. **The Disarm state is set visible in the same emission** —
   and the airlock state is **not** cleared (see §3.1; this
   is where I depart from Designer §7, deliberately).
3. **Input on the airlock dies** — it is now the face beneath;
   its slots must not answer touches through the incoming
   ground. (Its *time* does not die — §4.3.)
4. **Nothing is booked.** No intercept outcome, no reason,
   no count. The ledger's silence here is E-LAW 3's proof:
   either function in the current tree writes nothing on this
   path, and the spec forbids adding a write.

No press animation is added to the line itself — no dip, no
colour flash on the text. A bare text line that flashes when
touched has dressed itself as a button at the exact moment
of use; the *world's* response (the latch, the dimming, §3)
is the feedback, and it begins in the same frame as the tap,
so the tap never feels unregistered.

---

## 3. The transition into the Disarm face

### 3.1 The ruling: the door stands beneath (P-2 over §7)

Designer §7 wires the exit as *(a) dismiss the airlock, then
(b) `requestDisarmChallenge()`*, reasoning that dismissing
first keeps exactly one door standing. The Innovator's P-2a
wires the opposite: **never hide the airlock**; the Disarm
overlay composes over it (`MainActivity` already composes
Disarm *after* Airlock, :801 after :793 — the z-order the
handoff needs exists in the tree today), and `KEEP FOCUSING`
peels it away again.

**This seat rules for P-2, and asks Tor to overturn §7's
wiring on feel grounds that are also truth grounds:**

- Dismiss-first, executed honestly, is a **sequence**: the
  airlock's 260ms exit fade, *then* the Disarm face's entry.
  However it is overlapped in implementation, its felt shape
  is *one door closing and another opening* — a corridor
  between rooms, and in the corridor there is a moment when
  what stands is neither face: Monolith's own screens under
  a fading card. That moment breaks E-LAW 1 in spirit even
  where it keeps its letter (what shows through is the Deck,
  not the blocked app — the app was taken away by FIX-7a's
  BACK at arrival — but "not the blocked app" is a lawyer's
  pass; the man's felt experience is *the door left before
  the next one came*).
- Standing-beneath makes the handoff a **turn, not a trip**:
  one continuous surface event, no departure, no arrival
  from nowhere. He does not go anywhere. The room he is in
  gains a second face in front of the first, the way a man
  turns a signet over in his hand. That is the audit's own
  metaphor made mechanical — *the valve's own face* — a
  face of this door, not a destination elsewhere in the app.
- It makes the return path (§4) free of every cost a
  re-entry would charge: no second arrival sound, no second
  entry fade, no wall re-performed. §7's wiring would have
  to rebuild all of that on the way back, or strand him on
  the Deck when he chooses `KEEP FOCUSING` — which would
  make looking at the exit a one-way door in practice,
  whatever the copy says. (From the Deck, a dismissed
  airlock cannot un-dismiss; the intercept is gone, its
  anchored derivations with it.)

§7's stated fear — two doors standing at once — is answered
by the tree's own mitigation, which P-2 already cites: the
Disarm ground is `NightColorBackground` @ **α0.94**. The
airlock beneath is not a second readable face; it is a 6%
silhouette (§3.2). One door speaks; the other is present the
way a wall is present behind a curtain.

### 3.2 The 280ms, frame by frame in feeling

The Disarm overlay's shipped entry is `fadeIn(specC())` —
**280ms on `EasingC`**. Druck's D-F17 asks that the exit
handoff feel *heavier* than leaving by the slab; the shipped
specC already is the heavier motion in the house vocabulary
(280ms, the slow-settling C curve, against RETURN's 260ms
specA dismissal and the door's own 180ms specA entry). **Keep
specC for this transition, unchanged.** Do not unify it down
to the airlock's 180ms: a man should feel, in his hand and
his peripheral vision, that he has turned toward the grave
face before he has read a word of it. The motion is the
warning the line itself is forbidden to print (§1.5) — this
is the answer to D-F17's bait-switch charge: the tap is not
costless in *feel*, only in fact. It costs 280ms of growing
dark, and that is the honest price signal.

The single `AnimatedVisibility` fade carries the whole
overlay — ground and card together — and that unity is the
spec:

| Window | What happens | What it must feel like |
|---|---|---|
| 0ms | Tap; latch (§2); Disarm state visible; airlock input dead | Registered instantly. No dead tap, no second tap needed, no double-compose on a repeated tap |
| 0–280ms | The α0.94 Night ground fades in **over the still-standing airlock**. Luminance barely moves — both grounds are #171412 — the change is *legibility draining* out of the face behind: name, slab, gate soften to a silhouette. The Disarm card fades in centred, at final geometry, opacity only — no slide, no scale, no rise | A room darkening, not a page turning. Nothing travels, so nothing can be followed by the eye away from where the new face will stand. By ~140ms he can already tell *something graver is in front*; by 280ms the old face is furniture behind glass |
| 280ms | Entry complete; the face is whole: title, consequence, valve, rule line, `KEEP FOCUSING` (§3.3) | It was never partial. There is no staggered reveal on this face either — a consequence sentence that types in would be theatre |

Three bans, each load-bearing:

- **No crossfade of the airlock out.** Its `isVisible` never
  toggles in this transition, so its exit fade never runs.
  If a future hand "tidies" the two fades into a matched
  out/in pair, the corridor of §3.1 returns. The airlock's
  alpha is reduced *only* by the ground laid over it.
- **No gap frame at the seam either end.** At tap + 0 the
  airlock stands at full strength; at 280ms the Disarm face
  stands at full strength; at every frame between, the
  composite of the two is opaque Night. This is testable
  (V-1) and it is the transition's entire claim to honesty.
- **No second sound.** The latch at the tap is the
  transition's only voice. Nothing lands at 280ms — no
  settle tick, no valve hum, no "you are now on the serious
  screen" cue. The face's arrival is visual, and its silence
  after the latch is the first taste of §3.3's discipline:
  this face does not perform gravity, it waits.

Input during the 280ms: the Disarm face's controls arm at
the fade's end. 280ms is below the time a man needs to read
even the title; no honest act is delayed by it — contrast
Atem's arm-instant ruling for the peek chip, where the man
has *waited out a printed wall* and a dead fade would extend
it. Here no wall was printed, so nothing is owed mid-fade.
The valve in particular must not accept a grip early: a hold
begun on a half-faded valve is a hold begun on a rumour, and
the valve's own banking arithmetic deserves a whole face.

### 3.3 The first second on the Disarm face (280ms → ~1.3s)

He arrives carrying the Held Door's weather: mid-craving or
mid-decision, thumb hot, having just chosen the gravest line
on the previous face. The Disarm face's first second must
spend itself on **one transfer: from being stopped, to being
asked.** In order of his eye, on the shipped face
(`DisarmChallengeOverlay` :732):

1. **The centred card.** First, geometry speaks: the Held
   Door's card was top-anchored in the fixed skeleton; this
   card is **centred** (`contentAlignment = Center`, outer
   padding 16dp). The shift is a step *inward* — the new face
   is smaller, closer to the hand, surrounded on all sides by
   the dimmed silhouette of the door he came from. It reads
   as an inner room, not a next screen. Keep the centring;
   do not "fix" it to the door's top anchor for consistency.
   The inconsistency *is* the depth cue. (The card's padding
   — 22dp here against the door's 24dp, the audit rider's
   F-14 — is invisible across this seam because the cards
   never share a frame at full strength; it is not this
   slice's to spend.)
2. **`EMERGENCY BREAK`** — the title, Atkinson Bold 12sp,
   footer-sized and unhurried. This is the word the Held Door
   was forbidden: FIX-8 killed `EMERGENCY PEEK` so that
   *emergency* would mean one thing in this app, and here,
   in the first second, he collects that discipline's
   dividend — the word lands at full weight precisely
   because it was never spent on a two-minute lease. Its
   small size is correct: gravity at 12sp, stated; gravity
   at display size would be the door shouting at a man who
   has just walked toward it voluntarily.
3. **The consequence sentence** — 13sp, the largest body text
   on the face, and the only sentence he must read in the
   first second: *"This ends the session early. It is the
   only break this week."* Two facts, no verb addressed to
   him, no instruction. It must be fully legible the moment
   the fade completes — it is composed under the fade at
   final geometry (the overlay composes whole), so the
   first second contains no reading that starts late.
4. **The valve, inert** — `Emergency60sValveView`, 68dp,
   full width, the face's largest object and its only
   mechanism. In the first second it does nothing, and its
   doing nothing is the face's central kindness: **no clock
   on this face runs until he starts it.** The Held Door ran
   a wall *at* him; this face's 60 seconds exist only under
   his hand. No auto-arm, no pulsing invitation, no progress
   ghost. The rule line beneath it (11sp, α0.75 — the one
   deliberately quieter print on the face, mechanics in
   footnote register, correctly) tells him the terms of the
   grip: *"Hold to fill 60 seconds. Every 15 seconds banks.
   If your hand slips, what isn't banked drains away. The
   banked part stays."*
5. **`KEEP FOCUSING`** — the full-width chip at the card's
   foot, present in the same first glance, not revealed
   after the consequence has had time to work on him. A man
   who must hunt for the way back experiences the face as a
   trap with a valve in it. The exit from *this* face obeys
   E-LAW 2 exactly as the exit from the last one did: the
   return is standing, legible, and unembarrassed from the
   first frame.

And the first second's **silence**: after the latch at the
tap, the face makes no sound and no haptic in its first
second — the valve's own voices (its per-second and banking
`fireFeel` calls in the view) exist only once a grip is
taken, and `onHoldStarted` fires only on that grip. A face
that hummed, ticked, or breathed on arrival would be
applying pressure in the one second he is deciding whether
pressure is warranted. The Held Door's discipline holds
here doubled: the graver the face, the quieter its waiting.

Net feeling of the first second, in one line: **he has not
been taken anywhere; the serious version of the same door
has turned to face him, told him the price in one sentence,
shown him a mechanism that will not start itself, and left
the way back standing open beside it.**

### 3.4 The two other bodies he can meet in that first second

Both are handled by the face as shipped, and both matter to
this slice because the exit line promises *a face*, never a
specific body:

- **The SPENT face** (`tokensRemaining == 0`): the valve is
  simply not composed; the body reads *"This week's break
  is already spent."* and `KEEP FOCUSING` is the card's only
  act. First-second feel: a fact and a way back, in a small
  card — this must land as *information*, never as a second
  refusal stacked on the Held Door's. It does, if two things
  hold: the handoff's 280ms gravity (§3.2) is *not* shortened
  for this body — arriving gravely at a plain fact is
  honest, arriving with a trapdoor snap would mock him —
  and §4's return is exactly as instant and unbooked as
  from the live face. He came to look at ending; the answer
  is that ending was already spent, by him, earlier this
  week. The face that tells him so owes him a dignified,
  immediate way back, and §4 pays it.
- **The stopwatch clean-completion world** (P-2c's trap):
  as shipped, `requestDisarmChallenge` from a stopwatch past
  its floor **ends the session silently, with no face at
  all** (VM :2243–2254). From the Deck that is arguably a
  feature; *from this line it is a catastrophe in feel and
  in truth*: the quietest tap on the Held Door would become
  the easiest session-end in the app — no consequence
  sentence, no valve, no gravity, the session simply gone
  under a footer line's tap, quite possibly under an
  overshoot meant for RETURN (§1.3's error asymmetry, cashed
  in the worst direction). This seat endorses P-2c without
  amendment: **the door-originated entry always presents
  the face**; where clean completion is lawful, the face
  carries a derived body saying so and a deliberate slab
  between the line and the act. Tor to rule (R-3). Until it
  is ruled, the route must not ship door-originated on the
  unguarded function: a route that can end a session from a
  footer tap with zero intervening face fails the audit's
  own description of itself ("the valve's own face carries
  the gravity" — there would be no face).

---

## 4. The way back — `KEEP FOCUSING` from the door's Disarm face

### 4.1 The peel (0–260ms)

He taps `KEEP FOCUSING` → `dismissDisarmChallenge`
(VM :2263) sets the Disarm state invisible → the overlay's
shipped exit runs: `fadeOut(specA())`, **260ms, `EasingA`**.

Because of §3.1's ruling, this fade is a **reveal, not a
journey**:

- The α0.94 ground thins over the airlock that **never
  left**. Across the 260ms the silhouette behind the ground
  sharpens back into the face: name, intention, gate or
  chip, price, RETURN, and the exit line itself — every slot
  at the bounds it held when he turned away. Nothing
  re-enters, because nothing exited. The Held Door's own
  `AnimatedVisibility` never toggled, so **its entry fade
  does not replay and its arrival voice does not re-sound.**
  A returning man must not be *arrived at* twice: a second
  300Hz latch-and-fade would tell him the visit restarted,
  and a restarted visit would invite him to re-decide things
  the door already settled.
- `dismissDisarmChallenge` plays **no sound** in the tree
  today. Keep it silent. The two directions of this handoff
  are voiced asymmetrically on purpose: turning *toward*
  ending carries a latch; turning *back toward focus*
  carries nothing but the visual return. Sound on the way
  back would either celebrate (a seal — unearned, and a
  reward cue paired with resisting, Atem's conditioning
  argument mirrored) or warn (absurd). Silence is the only
  honest voice for "nothing happened, and that was allowed."
- At fade end he is standing **exactly where §4.2 specifies**
  — not on the Deck, not on a refreshed door, not on a
  summary of his near-miss. The Disarm card is gone; no
  residue of it (no dim, no lingering border) survives the
  260ms.

### 4.2 The state of the door he returns to

Everything below follows from standing-beneath plus the
anchored derivations (FIX-5), and each line is a promise the
feel depends on:

- **RETURN is live at the frame the fade completes** — it
  never stopped being composed; its input re-arms as the
  Disarm ground clears. No warm-up, no re-enable delay.
- **The wall told the truth while he was away.** The gate's
  end is a stored monotonic instant, not a paused counter.
  If the peek armed during the round trip, the chip is
  standing when the ground clears — crossfaded inside Z1 by
  the skeleton's own rule, discovered by him as a fact, not
  announced. If it has not, the gate reads its true
  remainder. The door does not re-perform elapsed time, does
  not refund the seconds he spent reading the consequence
  sentence, and does not charge them either: the round trip
  cost him exactly the real seconds it took, which the
  anchored clock already owned — nothing was added for
  looking (§3.4's lesson would otherwise return as arithmetic).
- **The taper did not move.** The count is booked intercepts
  of this package in this session; the round trip books no
  intercept (E-LAW 3), so the wall he returns to is the wall
  he left, lengthened by nothing.
- **No print acknowledges the excursion.** No "WELCOME
  BACK", no changed line, no residue in the ledger that the
  Drawer could later quote as a near-end. He looked at the
  end of his session the way a man looks over a railing;
  the door's position is that a look is not an event.

### 4.3 If he completes the valve instead

Out of this slice's centre, one seam is mine to close: on
completion, `completeEmergencyBreakFromValve` (and
`confirmDisarmSession` on its path) hides the Disarm face
and stops the shield — and as shipped, **leaves the airlock
state visible over a session that no longer exists**
(P-2d). With the door standing beneath, that is no longer an
edge case; it is this route's main exit. Spec (endorsing
P-2d): both completion paths clear the airlock state **in
the same emission** as the Disarm state. Felt consequence,
and it is a good one: the airlock is at 6% silhouette under
the ground at that moment, so clearing it is invisible
inside the Disarm face's own 260ms exit fade — he watches
one face leave and finds the session's aftermath (Deck /
receipt, the seal's disarming voice from the completion
path), never a zombie door printing a dead session's end
instant. One fade, one departure, no double dismissal.

---

## 5. The plain RETURN exit — the last 260ms

### 5.1 The mechanics, as ruled and as shipped

Tap on RETURN → `transmuteImpulseAndReturn`: the harmonic
seal sounds **at the tap** (`playHarmonicSeal(arming = true)`),
the airlock state goes invisible, and the overlay exits with
`fadeOut(specA())` — **specA 260ms on `EasingA`
(CubicBezier(0.2, 0, 0, 1))**, the duration FIX-7b froze
("Exit keeps specA 260ms"). The skeleton's RETURNING state
(§8) holds the whole face **frozen** for the fade's length:
every slot's bounds and contents as at the tap, input dead
after the first tap (no double-booking — the ledger writes
`RETURNED AT DOOR` once, FIX-3c), no individual element
animating. Beneath the fade stands Monolith's own session
face — never the blocked app, which was taken away at
arrival by FIX-7a's BACK and is not returned by this door;
RETURN is a dismissal of the *door*, not a release of the
*app*, and the shield stays armed on the session he returns
to.

### 5.2 What those 260ms communicate

An exit fade is the last sentence a face speaks. This one
says five things, in the order he receives them:

1. **"Registered."** (0ms) The seal sounds at the tap, before
   a single frame has faded. He never spends any part of the
   260ms wondering whether the tap landed — the confirmation
   is acoustic and instant, the picture merely follows it.
   Contrast the peek's arm (Atem's slice), where the door
   owes him silence: there, *he* waited on the door; here,
   the door waits on no one — certainty is owed immediately
   because the act was his.
2. **"No argument."** (0–100ms, the curve's fast
   commitment) `EasingA` front-loads the fade: the face gives
   up most of its presence early. A door that faded slowly,
   evenly, wistfully — 420ms of specB ghost, say — would be
   a door visibly reluctant to let him go, performing one
   last half-second of hold. 260ms with a fast head says the
   door releases at the speed of his decision. Its brevity
   relative to everything else in the visit (a 5–10s wall,
   a 60s valve behind the other exit) is the proportion made
   temporal: leaving well is the cheapest act in the app,
   priced in milliseconds accordingly.
3. **"Nothing is being converted."** (the whole fade) The
   face dissolves *as itself*: the card does not shrink into
   a checkmark, the slab does not flash or linger, the gate
   does not resolve into a verdict, no element outlives the
   others. A uniform opacity over frozen geometry is the
   only exit that cannot be misread as a result screen. What
   he did was return; the fade refuses to upgrade it into an
   achievement mid-departure.
4. **"RETURN leaves too."** (mid-fade) The accent slab — the
   face's loudest object, the thing he just pressed — fades
   at the same rate as the footer badge. It does not stay lit
   as a trophy of the correct choice. For the length of the
   fade there is no hierarchy left to read, which is the
   truth of the moment: the decision is made, the face's
   internal arguments are over, all of it is going together.
5. **"And now, nothing."** (260ms) The fade ends on the
   session's own face, mid-state, with **no acknowledgement
   surface**: no banner, no toast, no "urge stopped" print,
   nothing that narrates the visit back to him. The ledger
   books `RETURNED AT DOOR` where he will never be lectured
   by it. The absence after the door is the respect after
   the door — a man who turned back at a held door has not
   earned a ceremony, and giving him one would tax the next
   return with the expectation of it. The 260ms are the whole
   of the door's goodbye; after them, the app behaves as if
   the visit were a held breath, released.

### 5.3 The three departures, distinguished in one table

The visit has three ways out and they must never feel alike:

| Departure | Voice at tap | Motion | What stands after | Booked |
|---|---|---|---|---|
| RETURN | Harmonic seal (arming) | Whole face, `fadeOut` specA **260ms**, frozen | The session's own face; shield armed; blocked app still blocked | `RETURNED AT DOOR` (FIX-3c) |
| Exit line → Disarm | Mechanical latch | Door stands; Disarm `fadeIn` specC **280ms** over it (§3.2) | The Disarm face, door silhouetted beneath | Nothing |
| Disarm → `KEEP FOCUSING` | Silence | Disarm `fadeOut` specA **260ms**, revealing the standing door (§4.1) | The Held Door, exactly as left | Nothing |

Read down the voice column: staying is *sealed*, turning is
*latched*, returning-from-looking is *silent*. Three acts, three
registers, none borrowed. A man learns this door the way he
learns a house — by the sounds its doors make — and every sound
tells the truth about which act it was.

---

## 6. Rulings this seat needs from Tor

1. **R-1 — X slot 36dp → 48dp** (§1.4), card heights
   418 → 430 / 292 → 304, RETURN's bounds provably unmoved
   (the growth is downstream of it). The Innovator's P-2e
   priced the row; the Designer priced the slot; this seat
   prices the reconciliation. If Tor holds 36dp, §1.4's
   fallback line stands and nothing else in this file moves.
2. **R-2 — Register of the exit line** (§1.2): caps verbatim
   per the audit, or sentence case if Druck's D-F8 two-voice
   ruling lands face-wide. Mine is a class question, not a
   line question — I ask Tor to rule it with D-F8, once.
3. **R-3 — The door-originated Disarm entry** (§3.1, §3.4):
   P-2's standing-beneath wiring **over** Designer §7's
   dismiss-first, and P-2c's `fromDoor` guard (the face
   always presents from the door; the stopwatch silent
   completion never fires from this line) **as a condition
   of shipping the route at all**. P-2d's same-emission
   airlock clear on completion (§4.3) travels with it.
4. **R-4 — specC kept for the handoff** (§3.2): the Disarm
   face's shipped 280ms `specC` entry is the exit handoff's
   motion, deliberately *not* unified with the airlock's
   180ms `specA` entry. If a future pass unifies overlay
   entries app-wide, this transition's heaviness must be
   re-bought some other way or D-F17's bait-switch charge
   reopens.

---

## 7. Verification the builder can encode (paper, for Ren's compile)

- **V-1 No gap frame.** Drive the exit tap; sample every
  composed frame across the following 280ms: at least one of
  {airlock, disarm} is composed at every frame, and the
  airlock's `isVisible` is never observed `false` while the
  disarm's is `true` on this route. E-LAW 1 as an assertion.
- **V-2 Presence from frame one.** In COUNTING, OFFER, HELD
  and NEVER-OWED, the exit line is composed at the first
  frame, with bounds 12dp below `airlock_return_button`'s
  bottom edge and its slot height as ruled (R-1); assert its
  bounds are identical across all four states (joining the
  Designer's §11 RETURN-immobility test in one harness).
- **V-3 The look books nothing.** Exit tap followed by
  `KEEP FOCUSING`: assert no `recordIntercept` call, no
  token-ledger write, and no change to the airlock's wall
  anchor, taper count, or slot bounds across the round trip.
- **V-4 Reveal, not re-arrival.** Across the §4 round trip,
  assert the airlock's entry fade runs zero times and the
  arrival voice sounds zero additional times (sound-engine
  call log over the overlay's visibility transitions).
- **V-5 RETURN's 260.** On RETURN tap: seal call precedes
  the first faded frame; the exit transition spec is
  `specA(260)`; every slot's bounds at fade start equal
  their bounds at fade end (frozen); a second tap during
  the fade writes no second ledger row.
- **V-6 The trap is shut.** Door-originated Disarm request
  on a stopwatch session past its floor composes the Disarm
  face (R-3's body) and disarms nothing; on a spent week it
  composes the SPENT body with `KEEP FOCUSING` live.
- **V-7 Completion leaves no door.** Complete the valve from
  a door-originated visit: in the same state emission, both
  overlays read invisible; no frame composes the airlock
  over the ended session.

---

## NEXT (Law 10)

(a) **What I want to do next:** spec the *completion*
departure from this same seat — the valve finishing at 60s,
the Disarm face leaving, and what a man should find standing
where his session was (Deck, receipt, silence) in its first
three seconds. §4.3 closed the seam mechanically; nobody has
read the *aftermath* as a feeling, and it is the one exit in
the app he will experience at his lowest — the session is
over, by his own hand, at 1 AM. The door's last duty is how
it leaves a man standing in that.

(b) **What I could do, unassigned:** a departure audit of the
app's other overlays on this file's three-register table
(§5.3) — the desk clock's tap-anywhere dismissal and the
frozen digest's CLOSE — checking that every face's exit
voice, duration, and residue tell the truth about the act
that closed it, filed as a one-page table for Tor and Hale.
Faces are remembered by how they let go; at the moment only
this door's letting-go has been specced.

— RUHE, Feel Specialist 4, Tor's cell, Issue #11.
Report to Tor; nobody compiled; the user was not messaged.
