# DESIGN — THE SESSION ROOM'S THREE THRESHOLD SURFACES
## Cell issue-13 · Seat: SCHLEIER, Designer · Lead: Wacht
## Issue: Sarthak-2007/Focus-app #13 — Session Room
## Scope: the night veil · the Landing · the armed-undecided room state — nothing else

Filed: Oct 30, 2026. Design only — no code was touched by this seat.
Nobody compiles in this lane.

Authority, in order: Ren's rulings as handed to this seat —
**E-1 night veil + Landing RATIFIED** · **V1 as amended**
(~50 Hz sampling only while armed-undecided, 7–14 Hz tremor
band, three-state REST / HELD / UNKNOWN, **HELD suppresses
both raise paths**) · **V6 STRUCK** (no face for the night
return) · §C-2 stands (the exit line is never amended).
Sources composed, not re-filed: `AUDIT-session-room.md` §1/§3
(A-4, A-10) / §4 (E-1, E-2) / §7 · Vela's `DESIGN-face-spec.md`
§0/§1.2/§10 (this file is §10's deepening and §1.2's surface
half; where we differ, §9 says so) · Hauch's veil filing
(H-1…H-7) · Ruhe's Landing filing (R-1…R-5, §1.2 taxonomy)
· Dämmer's DAE-4…DAE-7 · Samt on V1 (F-1 dwell) · the
flow-clock settle machine (`DeskClockSettle.kt`) for the
state's mechanics.

Design law held throughout: hand-built only, no AI image,
no green or near-green anywhere (these surfaces add **no
colour at all** — see §1). Fonts are the house three —
Sora (display), Atkinson Hyperlegible (interface/body),
Anonymous Pro (telemetry/numbers) — and §2 explains why
these three surfaces set **no new type in any of them**.
Scale law: hero scale or label scale only, nothing
mid-size. These surfaces choose, deliberately, *neither* —
and that choice is the design, argued per surface.

---

## §0 · THE ONE IDEA — THE THRESHOLDS ARE ONE LIGHT GRAMMAR

The room's hours are designed (Vela's spec). Its thresholds
were three separate mechanisms wearing one night. This file
composes them as **one grammar of light, in five beats**:

```
ARMED-UNDECIDED ──raise──▶ THE ROOM STANDS (Vela's face)
      │                        │
      │ HELD: no raise,        │ stand-down (machine) ──▶ VEILED 0.80
      │ no face, no trace      │ hand dismissal (night) ─▶ LANDING 0.45
      ▼                        ▼
  the Deck, unchanged     first touch DOWN ──▶ LANDING 0.45
                          first true VERB ───▶ OPEN 0.00
```

One invariant governs every beat after a session is sealed:

> **Light only ever moves at his hand or at his verb —
> never at the machine's convenience, never upward on
> its own, and never in a step he did not start.**

Armed-undecided is the invariant before the room; the veil
and the Landing are the invariant after it. A man could
learn this grammar in one night and trust it from the
second (Neu's standard, audit §2) because there is only
one thing to learn: *the machine darkens; only he lightens.*

---

## §1 · MATERIALS — WHAT ALL THREE SURFACES ARE MADE OF

| Material | Value | Why |
|---|---|---|
| Scrim | pure black `#000000`, flat, uniform | The only material. A *tinted* scrim (warm, cream, accent-washed) would be a skin — V6 territory. Black is not a colour choice here; it is light subtraction, nothing added. |
| Ground beneath | the Deck in its **night scheme** — ground `#171412`, ink `NightColorPrimary #FFE4CF`, accent `#F47442` | The veil's 0.80 and the Landing's 0.55-transmission are calibrated against this pair (Hauch §2). While VEILED or LANDING is armed, the night scheme is **held** (§4.4) — the weights are properties of scrim × scheme, never of the scrim alone. |
| Colour added by these surfaces | **none** | No accent, no state colour, no green or near-green, no warning tone. A state that needs a colour to be understood has failed as choreography. |
| Texture / gradient / image | **none, ever** | No vignette, no radial falloff, no grain, no hand-drawn mark. A gradient on the veil would be staging; staging is a face; V6 is struck. The field is one uniform alpha, full-bleed, edge to edge. |
| Sound / haptic | **none, ever** | The clock path is silent both ways (A-9). The veil's arrival, the unveil, the Landing, the bloom: no click, no latch, no tick. Silence is the material the night is made of; these surfaces do not spend it. |

---

## §2 · TYPE — THE SURFACES SET NOTHING, ON PURPOSE

Three surfaces, zero new print. This is a positive design
decision per surface, not an omission:

- **The veil prints NOTHING.** No word, no time, no session
  name, no "tap to…". Any glyph on the veil is a face, and
  V6 is struck. A covered desk does not explain itself; the
  ghost of the Deck's own forms at 0.20 transmission (§3.2)
  already tells him *this is the app, covered* — orientation
  without a single decision offered. If a future seat wants
  a label here, the only lawful scale is label scale
  (Anonymous Pro Bold 11 sp, +2 sp tracking, `#FFE4CF` at
  the Landing's transmission) — and this seat's ruling is:
  not even that. The veil's eloquence is that it says
  nothing at 03:30.
- **The Landing prints nothing of its own.** What he reads
  on the Landing is the Deck's own print, in the Deck's own
  fonts (Sora display, Atkinson Hyperlegible body,
  Anonymous Pro figures), seen through 0.45 of black. The
  Landing must never re-typeset, duplicate, or summarise a
  single Deck element — a "landing line" would be a second
  Deck competing with the real one at half light.
- **Armed-undecided prints nothing new.** The Deck's seal
  state, exactly as the Deck already shows it, is the only
  trace that a session is armed. No badge, no dot, no
  "listening" label (§5.3). The state's numbers — 20 s,
  ~50 Hz, 7–14 Hz — are machinery (Nox A-N7) and machinery
  does not get a face.

**Accessibility consequence, designed not accidental:**
both scrims are **decorative in the semantics tree** —
no content description, not focusable, never announced.
Under the Landing every Deck control keeps its own label
and reachability (§4.5); under the veil, controls are
unreachable *by touch* (§3.4) and the veil itself offers
exactly one action — touch to unveil — which needs no
label because it is the whole surface.

---

## §3 · SURFACE 1 — THE NIGHT VEIL

*What it is: the machine turning the light down over the
Deck after the night's work, before he has asked for
anything. It is a garment, not a screen.*

### 3.1 Gate — when it may exist (all four, conjunctive)

1. **Kind:** the finish is `ROUTINE_SLEEP_STANDDOWN` alone
   (C-22 cured in wave-2; verified, not re-opened). No
   manual finish, no attended routine finish, no other
   routine kind ever veils.
2. **Cause:** machine return only. The veil has exactly
   one writer and no hand path can raise it — no chip, no
   dismissal, no navigation. A garment the hand can put on
   is a setting; this is not one.
3. **Freshness:** the return must be a *return* —
   **witnessed** in this process (the stand-down performed
   here, its arrival covered per §3.3), **or** cold-
   discovered **≤ 30 min** after the window's end AND
   seeded before the Deck's first frame of the new process
   (§3.3). Past 30 min cold: **no veil** — the record still
   files at any age (A-10(iv) untouched); only the garment
   expires. A veil that arrives late, over a lit morning
   Deck in his hand, is a blackout, not a courtesy
   (Hauch H-3, adopted).
4. **Light condition:** keep-awake already cleared. The
   veil holds nothing on; the OS timeout takes an
   untouched veiled Deck dark on its own clock. The veil
   asks for no extra minute of light.

*Named assumption (Hauch §1, recorded not charged):* the
gate trusts the kind, not a clock — if the SLEEP window is
ever edited into daylight, the veil follows the act it
belongs to. One discriminator, no second clock to disagree
with the stand-down itself.

### 3.2 Weight — 0.80, defended as the hinge

Black scrim α **0.80**, transmission **0.20** over the
night Deck:

- The Deck's ground (`#171412`) goes effectively out —
  0.20 × near-black is OLED-adjacent. The room disappears.
- The Deck's print **ghosts**: hero mass, sled, rail are
  present as shapes; 11–15 sp lines fall below comfortable
  reading. He can orient — *this is the app, covered, not
  a dead phone, not a stranger's screen* — without being
  handed a decision before his first deliberate touch.
- At 0.65 he would *read through* it and the first touch
  would unveil a room already used. A dimmer is not a veil.
- At 0.90+ the ghost dies and the surface is
  indistinguishable from screen-off — he would reach for
  the power key against a screen already on, and the
  veil's one message (*I turned the light down for you*)
  is replaced by a fault to diagnose half-awake.

**0.80 stands, named as a tuned constant of the pair
(scrim × night scheme).** Any future look that changes
the Deck's night ground re-opens this number; no other
change does.

### 3.3 Arrival — covered, or not at all

The veil **never animates in**. It is born under the
finish's held true-black ground (A-4: print fades 260
Curve A, ground holds through detention, ground fades
260 Curve A as it lifts) and is simply *already there*
at α 0.80 when the ground lifts. Black arrives over
black; its appearance is not an event.

Two composition laws make that true instead of lucky:

- **Z-order:** the one scrim (§6) composes **below** the
  desk-clock overlay in the host. While the room stands,
  its opaque ground covers the scrim completely; the
  scrim becomes visible exactly when the ground lifts —
  over the Deck alone. (Hauch H-2, adopted: as merged the
  veil sits *over* the clock and cuts the dawned print's
  own 260 ms fade by 80 % in one frame at E0 — a cut
  wearing a fade's clothes, on the one minute — 03:29,
  dawn ramp complete — a waking man is most likely to be
  watching.)
- **Cold seeding:** on the fresh-cold path (§3.1.3) the
  phase is set in the finish controller's first-emission
  handling, before the Deck composes its first frame. If
  a merge cannot guarantee that ordering, the fresh-cold
  case drops to **no veil** rather than a late veil:
  a missing veil costs a dimming; a late veil costs a
  blackout in his hand.

### 3.4 Touch — the veil ends on DOWN, and eats the touch

- The veil ends on the first pointer **DOWN** — any
  contact: tap, drag-start, palm, a thumb that lands and
  rests. "Until first touch" (E-1) means down, not a
  completed click: a click waits for UP, and a first
  contact that *moves* never becomes one — he would have
  to touch twice, deliberately, at the moment he is least
  deliberate. (Hauch H-4, adopted.)
- The unveiling touch is **consumed whole**: nothing
  beneath it fires. No verb can be smuggled through
  0.80 of black into an act he has not seen.
- **No indication.** No ripple, no press glow, no
  highlight of any kind — a raw down-handler draws
  nothing. The only light event at the unveil is the
  scrim's own descent (§6).
- The unveil is **one-way and idempotent**: a double-tap
  cannot skip the Landing or double-fire (§6's phase
  machine makes the second down a Landing contact, which
  spends nothing — §4.3).

### 3.5 What the veil is not

Not a lock (no `lockNow` — Ren's lean stands; it would
convert *screen sleeps* into *phone locked* and tax every
morning with an unlock he does not owe). Not a receipt
surface (the stand-down's slip is born filed, A-10(ii);
the receipt that *does* stand on other paths composes
**above** the scrim by design — wave-2's acquittal, kept:
the veil guards the Deck, and a finish's own paper is
the one surface allowed over it). Not a face (V6).

---

## §4 · SURFACE 2 — THE LANDING

*What it is: the Deck at the room's light law — the
half-light a night return lands in, where he may look
as long as he likes and owe nothing until he acts.*

### 4.1 Weight — transmission 0.55 (scrim α 0.45)

The Landing is the same one scrim (§6) at α **0.45**:
the Deck transmits **0.55** of its night-scheme light.

- **0.55 is a working weight, not a vigil weight.** The
  room's hero may descend to 0.36 because the room asks
  one thing (read one figure). The Deck asks work —
  11–13 sp print, dial figures, task names. Below ~0.50
  transmission its small print falls under the same
  legibility floor A-2 set for the room's number lines:
  a number too dim to read is a number withheld, and the
  Landing would become a second veil he must pay a verb
  to escape *before he can even read*.
- At 0.70+ it is a shrug — a 30 % dimming does not change
  what light does to a dark-adapted eye, and this grammar
  exists for that eye.
- 0.55 is also the right **bloom distance**: 0.45 of
  light returning in one 520 ms gesture is a door
  opening; more is a climb.

### 4.2 The two doors — the Landing exists at both

The Landing has **two lawful entries**, and designing
only one of them is the merged tree's standing defect
(DAE-5 / Ruhe R-1, adopted as design):

1. **Through the veil:** first touch DOWN on VEILED →
   LANDING (§3.4, §6).
2. **By his own hand:** a **night hand dismissal** of the
   standing room — the tap on the clock's face at 02:00 —
   lands on the Landing, never on the full-light Deck.
   The tap's object is the exit, not the light (Fen's
   original charge). The machine return got manners; the
   hand return must have the same ones.

**Discriminator (one, shared):** `themeEvaluation.isNight`
— a session is active, **or** an enabled SLEEP window
stands open — **OR** a night-return episode is already
open (§4.6). It covers the 22:00 in-session dismissal in
a dark room (session active) and the 02:00 dismissal
inside the SLEEP window; it refuses the 04:30 dismissal
after the stand-down, when he is up for the day and Nacht
A-N3's day acquittal governs whole. A local-hour window
(00:00–05:00) is the worse law — it misses the 22:00
in-session case, the exact case the charge was filed
from. **By day, no Landing, ever:** the day dismissal is
A-N3's whole, full-light return, untouched.

*Named, not charged (Ruhe R-1's note):* the session-
ending hand paths (disarm confirm, emergency-break
valve) end on the same Deck at night. Extending the same
entry to them is one condition each, but their return
carries a receipt/finish grammar this seat does not own.
Wacht's call; the discriminator above is written to be
reusable there unchanged.

### 4.3 Entry motion — snap at the hand door, descent at the veil door

- **Veil → Landing:** animated, 0.80 → 0.45 over
  **520 ms Curve C** (§6). The light rises — lawfully,
  monotonically, by his hand, to the Landing level and
  no higher.
- **Hand dismissal → Landing:** **no animation into the
  dim.** The scrim is at 0.45 on the first uncovered
  frame; the dismissal's own 180 EasingThrow exit covers
  the Deck while the throw runs. An animated 0 → 0.45
  entry would leave the Deck near-full for the throw's
  first frames and then *deepen* — a pulse, then a
  settle, the unveil-flash disease in miniature on the
  path he drives himself. The dimming he never sees
  happen reads as the room's law, not as an effect.

### 4.4 The scheme hold — 0.55 over *night*, or the number lies

While VEILED or LANDING is armed, the Deck composes in
its **night scheme**, whatever the theme evaluation says.
At the stand-down the evaluation has already flipped to
day (no session, SLEEP window closed), so an unheld
Landing is 0.55 over the *day* Deck — a brighter surface
than anything the room ever showed him, wearing the
room's number. The Landing does not re-skin the Deck; it
**holds the scheme the return began in** until the bloom
ends the return, when scheme and light are handed back
on the same frame. (Ruhe R-4, adopted. This also removes
the worst case of the unveil flash: the uncovered Deck
during any transition is night-scheme, never day.)

### 4.5 Touch — every control live, and the taxonomy of what blooms

The Landing's scrim **consumes no touch** (except while
VEILED, §3.4). Every Deck control is live from the first
touch, at every frame of every transition; no verb is
ever gated, delayed, or made to wait for light. The
bloom is an *effect* of the verb, never a toll before it.

**What spends the Landing — the commit taxonomy**
(Ruhe §1.2, adopted whole as this surface's law):

- **CONTACT — never blooms.** Any touch-down, brush,
  palm, grip; a scroll or fling, whole or in tail; a drag
  before its commit point (the arm slide before the
  latch; a press dragged off and released); focus
  entering a field; Back; any key event; the unveil tap
  itself; the tap that dismisses the clock itself.
- **READING — never blooms.** Scrolling the Deck to read
  it; opening PARK to read its board (its *toggles* are
  verbs; its opening is reading); opening the digest to
  read; and the **glance-and-leave round-trip**: raising
  the DESK CLOCK chip to re-check the room and dismissing
  it again — the dismissal *re-lands* under the episode
  rule (§4.6). A man who only meant to check the time
  spends nothing. That case is the grammar's
  proof-of-intent and every line of this file is written
  to keep it passing.
- **VERB — blooms, exactly once, at commit.** Navigation
  (`navigateTo`); the arm latch; a disarm request;
  receipt dismissal (a persisted write, A-10(iii) — a
  verb, not a glance); task / checklist commits (complete,
  toggle, promote, defer, add-on submit, reopen); preset /
  profile / blocks selection; the dial's first *actual*
  change (bloom inside the change-guard, not on touch);
  the intention field's first *changed character* (on a
  real diff, not on focus); `endSafeBreakEarly`; any PARK /
  Shield toggle or tier change reached from the Deck.

A verb dragged back (slide released below the gate,
press released outside, dial touched but not turned) has
not committed: **no bloom**, and the Landing stands for
the next, realer attempt.

### 4.6 The episode and the expiry — a Landing is a visit, not a tint

- **Episode.** A night-return episode opens at the veil
  or at a night hand dismissal, and stays open until the
  first true verb blooms it or the expiry (§4.6.2) ends
  it. Inside an open episode, re-landing is judged by
  the episode, not by a re-evaluated clock: the 03:36
  chip dismissal — five minutes after the stand-down,
  SLEEP window already closed, evaluation already saying
  DAY — is still the same dark return, and the light law
  does not change mid-episode.
- **Expiry — 30 minutes.** The Landing (and an untouched
  veil) is stamped when armed. On resume, if it is armed
  and NOT (a session is active OR a SLEEP window stands
  open OR it was armed < 30 min ago), it clears
  **instantly — snap to OPEN, no bloom, no gesture**.
  Thirty minutes is the morning's own scale (bath and
  meditation sit inside it); a return older than that is
  a new visit, not a return, and housekeeping must not
  be choreographed as a bloom in full daylight.
- **Glance-and-leave, protected.** Unveil → read → no
  contact → OS timeout: no bloom, no ask, the Landing
  simply stands until the screen sleeps. There is **no
  idle decay** against a reading man while he is still
  in the episode.

---

## §5 · SURFACE 3 — THE ARMED-UNDECIDED ROOM STATE

*What it is: the twenty seconds — or the held hour —
between the seal and the room. The session is armed; the
machine has not yet been given the room; V1 is listening.
The design question is what the room shows while the
verdict is undecided. The answer: the room does not exist
yet, and the Deck must not pretend otherwise.*

### 5.1 The state, exactly

Armed-undecided runs from the seal (or from the last
interaction after it) until one of its four exits (§5.5).
Inside it, V1 as amended samples the accelerometer at
~50 Hz (`SENSOR_DELAY_GAME`) — **only** inside this
state — and reads band energy 7–14 Hz into a three-state
verdict: **REST** (tremor band at the noise floor — the
phone is on furniture; a living hand shakes, furniture
does not) · **HELD** (band above floor, however still the
orientation) · **UNKNOWN** (bag, car, no signature either
way; sensor absent). The time path (20 s backstop from
max(seal, last interaction)) runs beside it. **HELD
suppresses both paths** while the signature persists.
This seat designs what that state *shows*; the thresholds
are machinery and are not re-specced here.

### 5.2 What stands on the surface: the Deck, unchanged

- The Deck composes **exactly as it did the frame before
  the seal**, at full standing light. No dim, no scrim, no
  scheme change, no held breath. The pre-raise minutes are
  *his working seconds* — the settle exists because a face
  that seizes the screen at the Throw would fight the hand
  that just armed it (the settle machine's own law). Any
  visual "winding down" toward the room would spend those
  seconds on theatre and teach him the room is coming on a
  schedule it is not coming on (HELD can postpone it past
  any schedule — §5.3).
- **Every Deck verb stays live, and using one is free.**
  An interaction restamps the time path (the backstop
  re-arms from the last interaction) and feeds the
  sampler's world, but it is never taxed, warned about, or
  framed as "disturbing" anything. There is no wrong move
  in this state.
- The seal's own standing confirmation on the Deck is the
  state's only print (§2). Nothing is added to it.

### 5.3 What must NEVER show — the honesty list

Each of these is a lie in a specific, nameable way:

1. **No countdown to the raise.** Not a number, not a
   shrinking bar, not a fading edge. The raise time is
   genuinely undecidable — REST can fire it in ~2 s,
   HELD can hold it for an hour — and any progress figure
   would print a promise the machine is not making. This
   is the ceiling-minute disease in temporal form: a
   pattern cannot acquit a false number.
2. **No listening indicator.** No dot, pulse, ear, wave,
   or "sensing" label. The sampler is not a feature he
   operates; showing it would (a) invite him to perform
   stillness for the machine — the braced-reader seizure
   V1-as-amended exists to cure, now self-inflicted —
   and (b) read as surveillance on a face whose whole
   programme is prints that don't lie about enforcement.
   The honest surface for a sensor that must not be
   gamed is no surface.
3. **No HELD state, shown.** If HELD ever printed —
   "STILL HELD", a paused icon, a frozen countdown — the
   reader's correct experience (*nothing happens, the
   Deck is mine, the session is running*) would become a
   negotiation with the phone. HELD's silence **is** the
   design (Samt): suppression with no surface, a refusal
   that requires no event. A held phone is not an error
   state and must never be dressed as one.
4. **No difference between the three sub-states (§5.4),
   ever.** LISTENING, HELD-persisting and BACKSTOP-ONLY
   are indistinguishable on the surface — deliberately.
   The moment they differ visibly, the surface is leaking
   the verdict, and items 2–3 are breached by the back
   door.

### 5.4 The three sub-states — one face (none), three machines

| Sub-state | Machine inside it | Surface |
|---|---|---|
| **LISTENING** — verdict forming, < 20 s since seal/last interaction, no verdict latched | sampler at ~50 Hz, continuous; bandpass warming (verdict is UNKNOWN until real band content exists — a verdict is never born HELD or REST from an unfilled window) | the Deck, unchanged |
| **HELD-PERSISTING** — HELD signature persists ≥ 30 s | sampler **duty-cycled 6 s in 30 s** (V1 as amended); both raise paths suppressed for as long as the signature persists — minutes, the length of a chapter, without limit | the Deck, unchanged |
| **BACKSTOP-ONLY** — UNKNOWN (bag, car, proximity-covered) or sensor absent | sampler off or ignored; the 20 s backstop runs **alone**, exactly as scoped — it is the only path, and it fires at 20 s from max(seal, last interaction) | the Deck, unchanged |

Proximity-covered (pocket, face-down in cloth) forces
UNKNOWN — never REST, never HELD: a pocket knows nothing
about hands or furniture, and must not be allowed to
testify. The backstop it falls to is E-2's scoped
behaviour, recorded here, not re-litigated.

**Battery honesty, as design:** ~50 Hz sampling is the
state's one real cost, and its containment is part of the
surface design because a state that drinks the battery
will be felt by nightfall even if it is never seen: full-
rate sampling exists *only* inside LISTENING/HELD-active
windows, duty-cycles down in HELD-PERSISTING, and is
**off** in BACKSTOP-ONLY and from the beat any exit
(§5.5) fires. The sampler's whole life is this state;
nothing outside the state may keep it alive (the wave-2
`onCleared` release stands as the boundary case).

### 5.5 The four exits — and what each one shows

1. **REST raise (sensor path).** REST must be
   **sustained, not sampled**: the verdict holds across a
   **1.5 s dwell** of consecutive REST beats before it
   raises (Samt F-1, cured in wave-2 as a 2-beat dwell at
   ticker grain — this seat fixes the *felt* requirement,
   the dwell's length in beats is the merge's arithmetic).
   A single trough in a braced reader's breath is not
   furniture. On raise: the arrival is **ENTRY, WATCHED**
   (A-4 — a V1 set-down fire is eyes-on by construction:
   ground 180 Curve A, print 420 Curve B at +180, total
   600 ms). The room arrives in the watched grammar
   because the man who set the phone down two seconds ago
   may still be looking at it.
2. **Backstop raise (time path).** UNKNOWN / sensor-absent
   at 20 s. Same WATCHED entry — the fire is < 10 s-scale
   from his last possible interaction in every realistic
   case, and A-4's discriminator (last-interaction stamp)
   decides; where the stamp says unwatched (≥ 10 s), the
   unwatched entry applies. The surface does not choose;
   the cause does (A-4 stands whole).
3. **Hand decision.** He raises the room himself (DESK
   CLOCK chip) or the session is decided by any hand
   raise/dismissal under Law 2: the sampler unregisters
   the same beat, the state ends without trace, and the
   machine does not argue with the hand for the rest of
   the session. A dismissal is **persisted** (V2): process
   death never resurrects a dismissed face into a new
   armed-undecided.
4. **Session death.** Finish or disarm inside the state:
   the arm dies with the session (the settle machine's
   own no-session rule). No raise, no farewell, no
   sampler leak into whatever comes next. If the finish
   is the night stand-down, the return grammar takes over
   from §3 — the two grammars never overlap on one frame:
   armed-undecided ends *before* VEILED begins, and the
   held black of the finish seam (A-4) is the only thing
   that ever stands between them.

**The wake re-raise is a fifth door, named so it is not
built wrong:** after the battery yield (A-10(v)), the wake
tap raises **by hand, at once** (a hand decision under
Law 2 — wave-2's corrected wording stands) and the
machine **re-arms** for the next auto-raise. A re-armed
machine re-enters *this* state — LISTENING, sampler live,
HELD able to suppress again. A wake path built time-only
would re-open the seizure fork on the second raise of
every battery night.

### 5.6 Duration — the state has no expected length

Armed-undecided may last ~2 s (set down on the desk,
REST sustained), 20 s (the backstop), or the length of a
braced read (HELD, unbounded). All three are the state
working, not the state failing at different speeds, and
the surface (§5.2) is identical across all of them —
which is precisely why it can be trusted: **a state whose
face never changes cannot be caught lying about how long
it will last.** The one number the state owes him — that
the session *is* running — is already on the Deck, in the
seal's own print, from the second he armed it.

---

## §6 · THE ONE SCRIM — THE COMPOSITION THAT JOINS §3 AND §4

The veil and the Landing are **one object**: a single
full-screen black scrim, one animated alpha, three phase
targets, driven by one VM night-return phase. Two boxes
with two animators is the merged defect (DAE-6 / Hauch
H-1 / Ruhe R-2 — adopted here as the design's centre):

```
phase     scrim α   transmission   entered by
OPEN      0.00      1.00           first true verb · expiry
VEILED    0.80      0.20           stand-down E0 (covered, §3.3)
LANDING   0.45      0.55           unveil DOWN · night hand dismissal
```

Transition table — every leg priced, every leg monotonic:

| Leg | Motion | Curve / duration | Touch during |
|---|---|---|---|
| → VEILED | **none** — born covered at 0.80 (§3.3) | — | clock overlay above it; Deck input already guarded by the standing room |
| VEILED → LANDING | animate 0.80 → 0.45 | 520 ms, Curve C (0.3, 0, 0.8, 0.15) | starts on pointer DOWN (§3.4); the scrim consumes input in VEILED only |
| → LANDING (hand door) | **snap** to 0.45, covered by the 180 EasingThrow | — | Deck live from the first uncovered frame |
| LANDING → OPEN | animate 0.45 → 0 | 520 ms, Curve C | fires at the verb's commit (§4.5); the verb executes on its own frame, never waits |
| any → OPEN (expiry) | **snap** to 0 (§4.6) | — | housekeeping, not a gesture |

**The invariant, testable in one line:** after the
stand-down, composite light never rises except (a)
monotonically at his unveil touch, to the Landing level
and no higher, or (b) at his first true verb. The merged
two-box sum — 0.80 → ~0 → 0.45 → 0, ~300 ms at or near
full (day-scheme) light inside the first touch of the
morning — is the exact failure this composition exists
to make impossible: a fade that overshoots its target to
the screen's maximum is not an approximation of the
grammar; it is its opposite at its centre.

One curve for light returning (520 Curve C — the room's
own: the descent's resume and the surrender's farewell
speak it) serves both animated legs. A second curve would
make the two legs feel like two mechanisms, and they are
one cloth.

---

## §7 · THE WHOLE ARC — ONE NIGHT, EVERY BEAT

For the merge table, the three surfaces in sequence
(03:30-AM use; SLEEP span armed the evening before):

| Beat | Surface | Light | His part |
|---|---|---|---|
| Seal (evening) | armed-undecided, LISTENING | Deck, full, unchanged | sets the phone down, or keeps reading |
| +~2 s, phone on desk | → the room, ENTRY WATCHED | room at standing α | none — REST was sustained |
| (or) the held read | armed-undecided, HELD-PERSISTING | Deck, full, unchanged, for as long as he reads | none — HELD asks nothing, shows nothing |
| The vigil | the room (Vela's face) | descent, floors, dawn ramp — hers | a glance asks nothing |
| Battery night | the room yields (A-10(v)) | 520 Curve C to black | wake tap = hand raise; machine re-arms (§5.5) |
| 03:30 stand-down | silence; receipt born filed; → VEILED | held black, then Deck at 0.20 | none — he is asleep; that is the point |
| First touch | → LANDING | 0.20 → 0.55, 520 Curve C, from his DOWN | one touch, spent on the unveil alone |
| Looking | LANDING stands | 0.55 over night scheme, indefinitely | scroll, chip round-trip, steadying hand — all free |
| First verb | → OPEN | 0.55 → 1.00, 520 Curve C | the verb itself, ungated |
| (or) no verb, > 30 min | expiry → OPEN | snap, unchoreographed | none — it is morning; the visit ended |

---

## §8 · PROOF DEBTS THIS DESIGN OWES (to audit §7, extended)

Frames, not impressions — each is pass/fail on sight:

1. **The unveil, monotonic:** the designed night (charger,
   SLEEP stand-down, dark room), frames at DOWN +0 / +130 /
   +260 / +520 ms. Measured transmission never exceeds
   0.55 after the first frame and is monotonic across the
   sequence. Any frame above 0.55 fails the merge — that
   frame *is* the flash.
2. **The covered arrival:** E0 / +130 / +260 ms with the
   man awake at 03:29, dawn complete: the print's fade
   traces 260 Curve A unaided — no frame where its
   luminance has dropped beyond the fade's own value at
   that offset (the veil must be provably *under* the
   room, §3.3).
3. **The two Landing doors:** (a) night hand dismissal at
   02:00 — frames at throw +0 / +90 / +180 ms: Deck
   uncovered at exactly 0.55 transmission, night scheme,
   no brighter frame; (b) the same dismissal by day: full
   light, no scrim, A-N3 whole.
4. **The glance that spends nothing:** unveil, then a
   scroll, a steadying second hand, and a chip raise +
   dismissal — transmission still 0.55 after each; then a
   verb: 0.55 → 1.00 over 520 Curve C, monotonic.
5. **The cold morning:** process killed before 03:30,
   first open at +20 min and at +2 h. At +20 min: a veil
   standing at frame one, or none — never a veil arriving
   over a composed Deck. At +2 h: no veil, no Landing,
   the filed record reachable and standing nowhere.
6. **Armed-undecided, the invisible state:** (a) a braced
   two-hand read of 10 minutes from the seal: zero raises,
   zero surface changes, sampler duty-cycled (bench
   trace, §7 debt 9 extended); (b) set-down on desk /
   stand / chest-rest / lap-rest: REST dwell, then the
   WATCHED entry inside its 600 ms total; (c) pocket /
   proximity-covered: UNKNOWN, backstop only, no REST
   seizure; (d) disarm at +10 s undecided: sampler
   unregistered the same beat (lifecycle assertion in
   the settle tests).
7. **The taxonomy as a test table:** contact and reading
   cases (scroll stamp, chip raise, PARK open, Back,
   volume key) leave the phase at LANDING; verb cases
   (arm, navigate, preset change, dial change, intention
   diff, receipt dismissal) move it to OPEN; the unveil
   DOWN does not self-bloom. Zero coverage of this grammar
   exists in the tree today; this table is owed as tests,
   not as a reading.

---

## §9 · RELATION TO VELA'S SPEC — WHAT THIS FILE DEEPENS, WHAT IT MOVES

Vela's `DESIGN-face-spec.md` is the face's merge spec and
stands. This file is the threshold spec for the three
surfaces Wacht's brief gave this seat. Agreements are
assumed; the moves are named:

- **§10 deepened, not replaced:** veil 0.80 and Landing
  0.55-transmission, the composed grammar (veil → touch →
  Landing → verb → bloom), and the lockNow exclusion are
  hers and Ren's; §3/§4/§6 here fix the gate's freshness,
  the z-order, the DOWN contract, the hand door, the
  scheme hold, the verb taxonomy, the episode and the
  expiry that §10 left open — each traced to the seat
  that filed it (Hauch, Ruhe, Dämmer).
- **§1.2's surface half:** her armed-undecided paragraph
  rules the machinery (correctly — Nox A-N7). §5 here
  adds the half a face spec cannot state from inside the
  face: what the *Deck* shows while the state runs, and
  the honesty list of what must never show.
- **One dissent, recorded:** Ruhe dissents in part from
  Dämmer's verb list (PARK's *open* and the chip are
  reading, not verbs; the taxonomy must also cover
  Deck-local commits or a man could work a whole session
  in the dim). This file adopts **Ruhe's taxonomy**
  (§4.5) as the one law; Dämmer's charge (bloom-on-any-
  interaction) is upheld by both, and the seats differ
  only on the cure's list. Wacht merges if the cell
  wants a second ruling; the design as filed is Ruhe's.

## §10 · PRICE

Print layer and host composition only: one scrim where
two boxes stood + a VM phase enum (SMALL–MEDIUM) · one
z-order block move (ZERO–SMALL) · the veil gate's
freshness condition + seeding order (SMALL) · DOWN
contract + no indication (folds into the scrim) · the
hand-door phase write (SMALL) · the bloom moved from the
interaction hook to the verb handlers, one function +
its call sites (SMALL) · the scheme hold, one condition
at two call sites (SMALL) · the expiry stamp + resume
check (ZERO–SMALL) · armed-undecided: no surface work at
all — a dwell already cured in wave-2, a duty cycle
already in the amended V1, and a list of things not to
build. **No DB change, no new surface, no new verb on any
face, no new colour, no new type, no sound.**

---

## CLOSING — THE ONE DECISION I DEFEND HARDEST

**These three surfaces print nothing, add no colour, and
make exactly one promise: light moves only at his hand
or his verb.** Every defect the cell found in the
thresholds — the flashing unveil, the noon blanket, the
Landing that existed at one door and bloomed at a brush,
the seizure V1-as-amended exists to cure — is the same
defect wearing different hours: the machine spending
light, or attention, that the man did not spend himself.
The cure is never a better effect. It is fewer objects
(one scrim), stricter doors (a fresh return, both
Landing entries), and a sensor that refuses to be seen
(an undecided state with no face at all). A study
instrument at 3:30 AM earns trust the way the room's
hours already do — by being calm, dark, and honest at
exactly the moments he is least able to check its work.

## NEXT (Law 10)

(a) **What I want:** the merge to take §6's one-scrim
composition and §8's frame debts as a single item — the
composition without the photographed proof is the
two-box mistake repeated with better intentions.
(b) **What I could do unassigned:** the same
threshold-grammar pass for the *day* returns — the
finish seam's ground release is still clock-timed
against issuance (Dämmer DAE-8), and the day dismissal
is currently acquitted whole (A-N3) by a panel that
never walked it as a threshold in its own right.

— SCHLEIER, Designer (Session Room threshold surfaces),
Wacht's cell, Oct 30, 2026
