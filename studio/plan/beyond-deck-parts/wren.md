# BEYOND-DECK PART — Wren (Wild, cinema)

**Seat:** transition grammar + the Vertigo Seal. **Scope:** the crossing in,
the crossing back, and one shot I held back in wave 1 as too much.
**Grammar carried in, unchanged:** chrome is the camera body — nameplate
rail, nav rail, viewport hairline live outside every transition container
and never blur, slide, fade, or scale; body-text changes are hard cuts;
tokens RACK 280 · VERTIGO 1050/850 · HELD FRAME 1200 · SHEET 260/190 ·
CUT 0; one blur family (defocus + tone steps, no shadows); zero orange in
chrome; the commit fill is ink by t=200ms of any seal.

**The definition everything below hangs on — t=0.** t=0 is the *frame*,
not the gesture and not the animation: the first vsync on which the
session state flow reads SEALED, the chrome state word hard-cuts to
SEALED, and the timer is already counting from the commit instant.
Every timeline in this file is a pure function of monotonic time since
that frame. Nothing pauses for a lingering finger, nothing replays
frames a finger hid, nothing waits for a sound. Mailed to Orson as the
sync contract (§1, Sound).

---

## 1 — THE 1050, RE-THOUGHT: ONE SHOT, TWO ARRIVALS

### The beauty case, plain

The dolly-zoom was never a picture of the gesture. It is a picture of
the *fact*: one number refuses to move while the room loses interest in
everything else. A slide and a tap are two different ways of saying the
same sentence, and the sentence is what the shot photographs — so the
shot does not change. What changes is what each entry has already spent
by the time the shutter opens.

The drag spends **travel**. For a second or more the eye has been
hauled along the track, past the gates, teeth meshing, until the assist
spring takes the last 8% out of his thumb's hands. He arrives at t=0
already moving, already narrowed. The tap spends three quieter
currencies. It spends **occlusion**: at the exact instant of decision
the verb is hidden under his own finger, and what the lift reveals is
not the verb pressed but the verb *spent* — the drain already running.
It spends **depth**: a slide goes across the machine, a press goes
*into* it — 1dp of depression is travel on the only axis a seal
actually uses. And it spends **stillness**: the idle Deck has zero idle
motion, so the tap arrives out of total rest, and motion read against
rest is larger than the same motion read against travel. The drag's
build-up is manufactured. The tap's build-up is the silence it was
already standing in. Neither needs help from the shot.

And the 1050 itself: it stays, but it stops pretending to be 1050ms of
spectacle. Re-weighted into three acts — a quiet departure, the
recession, a quiet settle — its *perceived* spectacle is ~600ms and its
edges are nearly still. Ten times a day for years, what habituates is
the loud middle; the quiet ends are where the ritual lives. A seal you
can watch forever is not a shorter seal. It is a seal whose loud part
knows when to stop.

### Technique

**Pre-t=0 — DRAG (the seat).** Unchanged mechanisms, one new duty:
when the cap enters the final 8% and the 110ms assist spring takes
over, the zip/friction sound and the drag haptic bed **cut dead**.
That assist window *is* Orson's 120ms breath (110ms of spring, ±10ms
of scheduling — inside his ±8ms audio/haptic lock tolerance at the
seat frame, stated here so he can rule on it). Seat frame = t=0.

**Pre-t=0 — TAP (the commit control).** Finger-down: the control
depresses its existing 1dp, its marker steps one tone darker under the
finger (Orson's struck-then-fading mark, moved pre-commit and kept).
That is the whole prime. It is user-timed — the system adds 0ms — and
fully reversible: slide off the control and the prime reverses, no
seal sounds, no state moves; retreat never sounds or looks like a
small seal. Commit fires on the tap's commit frame; that frame is
t=0. There is **no tap-only pre-roll and no tap-only first frame**:
a pre-roll is either latency after the commit (a lie about when HOLD
began, and ARM spent on theatre) or a different opening frame (a
breach of the settled one-t=0). Refused, by name.

**The shot — identical from t=0, both entries.** One `vertigoT`,
monotonic from t=0. Transforms sample easing
`CubicBezier(0.5, 0, 0.85, 0.35)`; blur samples the shared blur curve
`CubicBezier(0.32, 0, 0.18, 1)` over the same clock — two pure
functions of one time, so picture and defocus cannot desync.

| t (ms) | Readout | Wheel | Task card + chips | Ring field / ground | Commit fill |
|---|---|---|---|---|---|
| 0 | scale **1.0**; counting from t=0 | 1.0 · y+0 · α1.0 · blur 0 | 1.0 · ±0 · α1.0 · blur 0 | spacing ×1.00 | orange, drain starts |
| 0–180 *Departure* | 1.0; container padding begins growing | →0.985 · +3dp · α0.97 · blur 0.7dp | →0.997 · ±1.5dp · α0.96 · blur 0.5dp | →×1.03 | 4 discrete tone steps at 0/50/100/150 → ink |
| 180–780 *Recession* | 1.0; neighbours vacate around it | →0.83 · +17dp · α0.58 · blur 5.5dp | →0.952 · ±8.5dp · α0.12 · blur 4dp | →×1.31; **ground drops one tone step at t=400, one frame, a cut inside the dolly** | ink (zero orange from t=200) |
| 780–1050 *Settle* | 1.0, full size, running | →**0.80 · +20dp · α0.50** · blur 6dp | →0.94 · ±10dp · **α0 — gone, not greyed** | →**×1.38**, final | ink |
| 1050 | settle knock, shot ends | as filed in the running state | — | — | — |

Ground tone step at t=400 is one existing staircase step toward the
night ground `#171412` (day: one step down the day staircase) — tone
moves in steps in this grammar, never gradients, and a step *inside* a
move is a cut the eye feels as the room's light going, once.

**Haptics.** The t=0 tick of my wave-1 train is **deleted** — Orson's
seal waveform owns that frame and two strikes on one instant read as a
stutter, not a chord. Train now: 170 / 320 / 460 / 580 / 680 / 760ms,
amplitudes 0.28→0.12 decaying, spacing tightening (something receding).
Settle knock at 1050: 16ms @ 0.7.

**Sound — sync points against Orson's seal (mailed to him, thread
`vertigo-t0-sync`).**
- **−120→0 (drag):** breath = the assist window above. **(tap):**
  breath = the idle Deck's standing silence (his S4 void) — already
  true, nothing scheduled.
- **t=0:** the Throw attacks on the SEALED frame, 0 dB ref, locked
  within ±8ms of that vsync. Not before (the sound would announce a
  seal the state hasn't made), not after (ditto, reversed).
- **t=+30:** bolt layer, gain rendering the committed duration
  (−8 dB @ 25min → −5 dB @ 180min) — his, unchanged.
- **t=170–760:** my tick train is haptic-only; no audio event of his
  may be scheduled inside it except the bed's arrival below.
- **t=1050:** settle knock (haptic) coincides with the **start** of
  his bed's 2.5s ramp to −46 dB. My wave-1 in-shot room tone
  (−30→−23 dB across the shot) is **withdrawn**: two rooms for one
  seal is one room too many, and his is the real one. The shot is
  silent; the room arrives as it lands.
- First escapement tick remains a state event at 60s elapsed,
  untouched by anything here. Reduced motion as filed: VERTIGO →
  200ms peripheral fade, readout static, all sync points collapse to
  t=0 Throw + settle knock at 200.

### ARM / HOLD / TRUTH

- **ARM:** added input latency **0ms** on both entries. Input dies at
  t=0 because the state flips at t=0; the 1050ms runs parallel to a
  timer already counting and a session already blocking. The tap's
  prime is the user's own press duration; the drag's breath is inside
  the assist spring he was already travelling. Nothing in this item
  can be shortened by impatience because nothing in it is a wait.
- **HOLD:** the end state is the filed running Deck — no settings, no
  nav, no prompts. Both entries land in the identical sealed regime;
  no entry-specific surface exists to become a door.
- **TRUTH:** one t=0, one state source, timer counting truthfully
  from the commit instant through every frame — including frames a
  lingering tap-finger occludes, which are skipped by the clock, never
  replayed. The orange is shown spent by t=200 because it *is* spent.
  The shot's duration encodes nothing: 25 minutes and 3 hours get the
  identical 1050; the one number in the moment is carried by Orson's
  bolt gain, in his channel, by his rule.

### What it replaces, and why that loses

It replaces my filed Vertigo in two places: the flat easing read as
1050ms of uniform spectacle, and the wave-1 room tone under the shot.
The filed version loses on habituation — uniform spectacle is the
first thing a daily eye learns to ignore, and an ignored seal is just
a delay with good taste — and on the double room, which made the seal
happen in two acoustic places at once.

### Strongest case against this proposal

The tap's user has just voted for speed with the fastest gesture the
app allows, and this hands him its longest look. "Input dies at t=0"
is an engineer's defence; the man experiences 1050ms of cinema
standing between his tap and his settled screen, ten times a day, and
no act structure changes the arithmetic that a 400ms seal would
*feel* more respectful of the tap. If he ever says the tap feels
slower than the slide, this item — not his perception — is wrong, and
the honest cut is a tap-entry VERTIGO at 700ms sharing t=0 and the
held frame, which I will spec the day he says it and not before:
perceived slowness is his call to make, not mine to pre-empt.

---

## 2 — THE REVERSE CROSSING: THE ROOM RETURNS BY RACK, NOT BY DOLLY

### The beauty case, plain

The filed finish runs my film backwards: the room rushes back along
the exact path it left by. It is elegant and it is a lie of form. You
cannot un-see a session by rewinding it — the man who comes back to
the Deck is not the man who left it, and the Deck he comes back to is
not the Deck he left: time has passed in it, things were deflected in
it, a receipt is waiting in it. A reverse dolly claims restoration.
Nothing was restored; something was *completed*.

So the finish refuses the dolly and keeps the faith underneath it.
Sealing was motion — the room falling away. Returning is **stillness,
then sight**: the held frame holds (that part of my filed exit was
always right), and then the picture *cuts* — to the whole idle Deck,
already composed, already true, sitting defocused like a room your
eyes haven't adjusted to — and the machine's eye racks it sharp.
That is how you actually return to a room after hours inside one
thought: not by travelling backwards through the door, but by the
light coming up on a place that was there all along. And it rhymes
where rhyme is honest: the readout's scale never breaks 1.0 across
the cut, so the one thing he sealed *for* is the one thing continuous
between the two worlds. The number stays; the room changes. That is
the entire philosophy of the app in one cut.

The emergency exit keeps the splice — 0ms, a tear, a plain statement —
and gains by contrast: now the two exits don't merely differ, they
speak different film languages. A finish is a scene change. A break
is torn film. Nobody will ever mistake one for the other at 3 AM,
which was the point of separating them in the first place.

### Technique

Namespace: **E0** = the frame the session state flips
RUNNING→COMPLETED (Fen's persisted, once-ever transition — the beat
is keyed to it, written before any animation, as filed).

| t (ms) | Picture | Sound / haptic sync (Orson's Room Stops) |
|---|---|---|
| E0 | Held frame begins: final total alone at full giant size, task micro-line under it (`PHYSICS — 50 MIN`), nothing else. | Settle knock 24ms @ 1.0. Bed fade begins (300ms). |
| E0–300 | Total stillness. No element moves, including the colon. | Bed → out. |
| E300–700 | Stillness continues. | **True silence** — digital zero, haptically silent too. |
| E700 | Picture *unchanged* — the pulse is heard inside the stillness, not illustrated by it. | Completion pulse alone, 0 dB ref, the reserved voice, verified-completed sessions only. |
| E700–1200 | Hold completes its filed 1200ms. (Pia's held `00:00`/final-total beat and Fen's ≈1.2s envelope are this same held moment — specced once, here.) | Silence. Power-down knock pair per Orson (+180ms after pulse: 24ms @ 1.0, 12ms @ 0.5) lands inside the hold. |
| **E1200** | **CUT, 0ms.** Whole idle Deck, fully composed at final positions: wheel at rest scale 1.0, task line and NEXT line true, receipt facts present in their filed home — rendered **defocused**: blur 9dp, scale 1.012, one tone step down. Ring field: gone with the running Deck, at the cut, not faded. Readout/total scale continuous at 1.0 through the cut. | — |
| E1200–1480 | **RACK, 280ms** — the inter-screen token, unmodified: primary plane (wheel + slide) sharpens from E1200, secondary (task line, receipt line) at E1250, tertiary meta at E1300; settle hunt 1.0→0.9985→1.0 in the last 50ms; blur 9→0 on `CubicBezier(0.32, 0, 0.18, 1)`. | Silence. Landing tick 6ms @ 0.35 at ≈E1430 (blur passing 1dp) — focus found. |
| E1480 | Nav detent jumps (0ms, chrome's only motion) to Deck. Idle Deck live. | Hand-off frame: the receipt owner's mechanism (Emil's strike / aperture sheet per the merged plan) takes its t=0 here — not re-specced by me. |

**Skip law:** one tap anywhere during the hold jumps straight to the
E1200 cut (filed behaviour, kept) — worst-case return collapses to
280ms of rack. Taps during the rack are navigation taps and behave as
in any rack: a reverse tap reverses it; nothing queues a surprise.

**The banked valve, explicitly:** untouched. The emergency exit does
**not** get this return — no cut-to-defocus, no rack, no softening.
At 60 banked it is the filed splice: 0ms, +2dp one-frame slip, idle
Deck sharp *immediately*, the plain statement in the viewport, the
receipt labelled `ENDED BY EMERGENCY BREAK`. A forgiving focus-pull
on a broken session would be the reverse-vertigo lie wearing my new
clothes. Refused.

### ARM / HOLD / TRUTH

- **ARM:** the return is not on the arm path — but priced honestly
  anyway: a man re-arming the instant a session ends waits E1480
  worst case before the idle Deck is live, 280ms if he taps. Added to
  the filed worst-case arm (~6.1s) the total stays inside 10s with
  seconds to spare, and the wait is a *held frame he can skip*, not a
  lock: the session state has been COMPLETED since E0 and every fact
  is already true and already stored.
- **HOLD:** no decision is offered at any point — no "keep going?",
  no confirm, no menu. The hold asks nothing; the rack asks nothing;
  the first live control is the idle Deck's own.
- **TRUTH:** the completion pulse and the held total belong only to
  verified fully-elapsed sessions (Orson's reservation, Fen's
  persisted once-ever transition — both inherited, untouched). The
  Deck at the cut renders *current* truth — post-session deflections
  in the chrome rail, the real receipt — never the pre-seal Deck
  restored from a snapshot. The rack's blurred 280ms renders the same
  state objects the sharp frame does; no number tweens, counts, or
  exists in two forms.

### What it replaces, and why that loses

It replaces the filed **reverse Vertigo (850ms)** as Phase 2 of the
completion exit. The reverse loses three ways: it lies about
restoration (above); it duplicates a mechanism — the app already owns
a grammar for *arriving somewhere*, the rack, and the finish is an
arrival, not a departure played backwards; and it spends its 850ms on
travel the eye has already seen reversed once a day for the life of
the app, the single most habituating shot in the system. The held
frame (1200ms) is kept whole — it was the best part of the filed
exit, and this proposal exists partly to give it a worthy successor
instead of a rewind.

### Strongest case against this proposal

Symmetry is not nothing. Entry and exit as mirror shots is the oldest
ritual grammar there is — doors in temples are hung to swing both
ways — and a user who has sealed 500 times may feel the reverse
dolly in his body as *the* completion gesture, the machine exhaling
along the path it inhaled. My cut+rack is colder, more editorial,
more *mine*. It also introduces a 280ms window in which the returning
Deck is illegible, at the exact moment a depleted man may want to
read his total's context — mitigated (the total itself was just held
alone for 1200ms, legible throughout) but real. If the merged plan
judges the mirror worth more than the argument, the reverse Vertigo
stands as filed and this item dies cleanly — it is an argued refusal,
not a load-bearing one.

---

## 3 — THE BEYOND I HELD BACK: THE SLOW PUSH-IN (ONE SESSION, ONE SHOT)

### The beauty case, plain

Here is what I didn't file in wave 1 because it sounded like a
gimmick: **the Vertigo never actually ends.** What lands at the
settle knock is only the part of the push-in fast enough to see. For
the rest of the session — on the minute beats the machine already
keeps — the ring field goes on opening, one invisible step at a time,
until by the hour mark the room around the number is a third wider
than the room he sealed in. No single step can be seen. The
beginning and the end, placed side by side, are unmistakably
different places. A session stops being a screen the app sits on for
three hours and becomes *one unbroken shot* — the longest take in the
app, taken at the speed of the work itself. Cinema's deepest trick
was never the cut or the dolly. It is duration: a frame held so long
it changes meaning. His sessions are the only surface in this app
with real duration. Everything else is a moment; this is the one
place Monolith can make a film instead of an interface — so make the
film.

### Technique

- **Carrier:** the ring field only. Readout, wheel ghost, task line,
  chrome: untouched, at filed values, forever. Rings are hairlines
  in existing tone steps, clipped by the viewport hairline as filed.
- **Law:** ring spacing factor S is a pure function of **true elapsed
  minutes** from the persisted session clock:
  S(0 at settle) = **1.38** → S(60min) = **1.62**, multiplicative step
  ×1.00268 per elapsed minute (outer-ring radius grows ≈0.5dp/min at
  R≈190 — below step perception, masked by the minute's numeral
  jump). **At 60 minutes S saturates at 1.62 and never moves again** —
  a 61-minute and a 180-minute session end in the identical room.
- **Stepping, never sweeping:** S updates exactly once per elapsed
  minute, on the same frame as Pia's jumping-minute (her 140ms
  numeral beat) and Orson's escapement tick. Between beats the field
  is as still as everything else on the running Deck. There is no
  continuous motion anywhere in this item.
- **Derivation, not animation:** S is computed from elapsed state on
  every render — backgrounded, killed, or AOD-interrupted sessions
  resume at the correct S in one hard frame, with no catch-up travel.
  On the AOD surface the push-in **does not run** (AOD is flat, Orson's
  ghost ring owns that minute there); returning in-app shows the true
  current S instantly. The two surfaces never disagree because neither
  animates toward the other.
- **Blur / tone:** none, at any point. No blur change, no tone change,
  no alpha change — density falls only because geometry widens; outer
  rings exit the frame as spacing grows, so the field also *thins*.
  The end-of-session room is emptier as well as wider.
- **Sound / haptics:** none of its own, ever. The step rides the
  escapement's frame; it does not duck, add to, or re-time it.

### ARM / HOLD / TRUTH

- **ARM:** zero. Entirely post-seal.
- **HOLD:** no surface, no control, no prompt, no continuous motion
  to attend to. Pia's stillness discipline survives in the only form
  that matters: between beats, nothing moves — the beats themselves
  were already sanctioned motion before this item existed.
- **TRUTH:** S encodes *elapsed minutes only* — never fraction of
  session, never remaining, never total. It cannot be read as a
  progress bar because it does not know how long the session is, and
  after 60 minutes it stops knowing anything at all (saturation is
  the honesty mechanism: a meter that saturates at an hour is a
  mood, not an instrument). It is atmosphere derived from a true
  number, like Orson's bolt gain — rendered in geometry instead of
  decibels, and lossy by design. **Kill-gate, pre-agreed:** in a
  blind test, if viewers can estimate elapsed time from ring spacing
  better than chance, it has become a second rendering of the clock
  and it dies under one-fact-one-rendering. No appeal.

### What it replaces, and why that loses

It replaces the filed post-settle ring field frozen at ×1.38 for the
whole session. The frozen field loses because a field that explodes
for one second and then poses for three hours reads as a *poster of*
depth, not depth — the running Deck's background confessing it was
only ever a transition effect. The push-in is the filed field taken
at its word: if the room really fell away, it keeps falling, at the
speed rooms actually change around a man who is working.

### Strongest case against this proposal

Two charges, both serious. **Pia's:** the running display's power is
that it is *dead* between beats — a man glancing at minute 40 should
meet the exact machine he left at minute 39, and any drift, however
stepped, makes the running Deck a thing that changes behind his back;
focus tools die by accumulated cleverness at exactly this altitude.
**The Critics', pre-empted but not answered:** one-fact-one-rendering
— elapsed already has a rendering, and my "lossy mood, not a meter"
defence is precisely what every second rendering in this app's
history has claimed on its way in (the deflection scopes made the
same argument in five homes). The kill-gate is my only honest answer:
the item ships with its execution test attached, and if it fails the
test it was, in fact, a progress bar in a costume. I held this back
in wave 1 for both reasons. I am filing it now because Phase 3 asked
for the shot I held back — not because the charges got weaker.

---

## Spectacle-budget note (for Sana's S2 table)

Freezes spent by this part: **0** — the ledger stays at four. Idle
motion added: one stepped frame per elapsed minute inside a session,
riding an existing sanctioned beat (§3 — flagged as a fight with the
"zero idle motion" line, argued there: motion *on a state beat* is
not idle motion, but the table should record the claim, not hide it).
Gate-to-session: unchanged on every item.

## Mail / wall note

Inbox empty at start and at wrap-up (the two citation-relay letters
from wave 1 sit in `read/`, answered by execution then). One letter
sent: to **Orson** (thread `vertigo-t0-sync`) — the t=0 definition,
the breath-as-assist-window proposal, the bed-ramp start at the
settle knock, and the withdrawal of my wave-1 in-shot room tone in
favour of his room. Wall signed on filing.

— Wren, Wild (cinema), Phase 3
