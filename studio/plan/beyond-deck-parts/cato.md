# Cato — Wild, fashion · Phase 3 GO-BEYOND: the Interlock Tape

*My layer: the slide's RENDERING. Filed spec (WILD-PLAN §1.4) is the floor:
track 56dp, full content width inside the 20dp margin; teeth 6×4dp at 8dp
pitch, meshed iff centre x < pullX − 6dp, count a function of track width
alone; teeth encode nothing; matte tape/unmeshed, satin +1 tone meshed;
pull 48×56dp, matte COMMIT orange `#F47442`, grip ribs, no swinging tab;
garage = final 10% recess, input 0.6×; assist spring (stiffness 400,
damping 16) over the final 8% in 110ms, 1.5dp overshoot; arm callback
fires on the SEAT FRAME; per-tooth haptics 5ms @ 0.35 / 0.28 alternating,
coalesced ≥12ms; seat knock 18ms @ 1.0; velocity-tracked zip sound
(Orson's layer). Night: ground `#171412`, recess `#100E0C`, raised
`#362C27`, cap `#4A423C`. Never green. Nothing below re-opens any of it
except where it says so, in the open.*

**The one definition this whole file stands on — t=0.** There are two
clocks and they must never be confused. The *gesture clock* G starts when
a gesture starts. The *session clock* S — the t=0 every elapsed number
in the app descends from — starts on **the seat frame: the first frame
rendered with travel = 1.0, the pull's leading edge against the garage
end-stops, the last tooth meshed.** The arm callback is invoked from
that frame's draw, the monotonic session start is read in that frame,
and nothing — no gate, no threshold, no dwell, no proof cascade — is
t=0. Moss's four clock-zeros (crank-release, proof-return, Fen's
post-dwell seal, Orson's threshold crossing) all describe frames that
are *not this one*. They die here. See the named fight with Fen, §1.4.

---

## 1. The tape under a tap — the driven mesh

### Beauty case, plain words

A tap is a man saying "close it" with one press instead of a throw. The
zip does not teleport shut for him — a zip that snaps closed by magic is
a button wearing a zip's skin, and the whole point of this tape is that
closing is something you *watch happen*: tooth after tooth giving up its
V-gap and locking, the satin line growing across the track. So the tap
orders the throw, and the tape performs the throw at zip speed — fast,
but every tooth still meshes in order, in the open, and it lands in the
exact garage, on the exact frame, that a dragged pull lands in. Two
gestures, one closing. The hand differs; the closure doesn't.

### Technique

**What counts as a tap (the commit order).** A pointer-down on the pull
sled itself (48×56dp — the Deck's one accent is the tap target; tapping
the bare track does nothing, a track is not a button) with displacement
< 8dp slop, released within 40–300ms. On release, the commit order is
issued. Before release the only rendering is the press: sled sinks 1dp
(recess deepens one hairline — its catch-line swaps from top edge to
bottom edge), grip ribs deepen. No tooth moves before the order: teeth
mesh behind a pull, and the pull has not moved.

**The driven run — two segments, the second one is the filed assist,
unchanged.**

- *Segment A — driven run, G0 → G0+140ms.* G0 = the release frame. A
  drive profile, cubic-bezier (0.30, 0.00, 0.20, 1.00), carries travel
  0 → 0.92. This is the hand's throw, mechanised: fast off the mark,
  already shedding speed as the garage approaches. Peak velocity ≈
  2.6 dp/ms at ≈55% travel (on the reference geometry below). It is not
  a spring and claims no physics — it is a *profile*, the same shape a
  practised thumb makes, and it never overshoots: a driven pull cannot
  arrive hotter than the garage can take.
- *Segment B — assist spring, G0+140ms → G0+250ms.* At travel 0.92 the
  filed mechanism takes over, byte for byte: assist spring stiffness
  400 / damping 16 over the final 8%, 110ms, 1.5dp overshoot into the
  end-stop crimps, input-equivalent garage scaling already implicit in
  the spring. **The seat frame is G0+250ms, and it is S0 — the same S0
  a drag reaches** when its own assist completes. The final 110ms of a
  tap and the final 110ms of a drag are the same code path, the same
  spring, the same frames. That identity — not a shared duration — is
  what "one t=0 for both" means.

**Reference geometry.** Screen 360dp wide → content width W = 320dp
(20dp margins). Sled 48dp → travel = 272dp; 92% = 250dp; garage begins
at 90% = 245dp. Teeth: centres from x = 12dp to x = W − 12dp at 8dp
pitch → N = floor((W − 24) / 8) + 1 = 38 teeth per row on this width.
The formula is the spec; the number is an example, because the count
follows the width alone and means nothing.

**What the thumb sees and feels, tap path:**

| t (gesture clock) | Travel | Eye | Hand / ear |
|---|---|---|---|
| G0 (0ms) | 0 | Sled releases its 1dp press-sink and starts to move. All teeth open, V-gaps full length. | Press tick dies. First tooth tick as tooth 1 meshes. |
| +50ms | ≈0.38 | Sled a third across; behind it the satin line — meshed teeth at +1 tone — already reads as a different material from the matte open tape ahead. | Tooth ticks arriving faster than the 12ms coalesce floor: fused fine rattle, alternating 5ms @ 0.35 / 0.28 sampled, never stacked. Zip sound (Orson): filtered noise, rate/gain tracking *driven* velocity — same synth as the drag, fed by the profile instead of the finger. It cannot run ahead of the pull because it is computed from pullX. |
| +110ms | ≈0.84 | Sled in the last quarter; the garage recess — wider, one step down, crimps visible — is the only thing ahead. ≈32 of 38 teeth meshed. | Rattle begins to space out as the profile sheds speed: the ear hears the deceleration before the eye does. |
| +140ms | 0.92 | Gate. Nothing changes visually — there is no gate rendering, the gate is a code seam, not a picture. | Assist spring engages; tick spacing now set by the spring alone. Identical frames to a drag from here on. |
| +250ms | 1.0 | **SEAT FRAME = S0.** Last tooth meshed (§2), sled against the crimps at 1.5dp overshoot, full satin line unbroken. State word flips on this frame, never before. | Seat knock **18ms @ 1.0**; zip sound ends in the dry clack (2ms transient + 40ms decay). Arm callback fires from this frame. |

**Abort — the tap keeps the buttonhole's conscience.** Any new
pointer-down on the track during the 250ms driven run aborts it: no
callback exists yet, nothing has committed, and the pull returns on the
cohort-wide buttonhole discipline — spring-back over **250ms** — teeth
un-meshing in reverse for free (mesh is derived from pullX; there is
nothing to un-record). Past the seat frame there is no abort, ever:
HOLD is absolute and the tape has no reverse gear on the Deck. An
aborted tap is a non-event in the data, exactly like a released drag.

**What it replaces and why that loses.** It replaces the assumption —
mine, filed — that the tape only ever moves under a dragging finger.
That assumption made the tap (Sarthak's settled verdict: TAP CAN
COMMIT) a second-class commit that would have needed its own
animation, its own seat, and in practice its own t=0. It also replaces
Ines's longer assist (her CurveB drives up to 35% of travel, 180/260ms,
from a 0.65 gate): her seat frame coincides with mine and her honesty
holds, but a 35% machine-run dilutes the garage — the deliberate final
8% is where this closure's meaning lives, and the tap path protects it
by arriving at 0.92 *before* the assist, not by stretching the assist.

**ARM cost.** Tap path: 250ms total, one press, zero waits — the
fastest legal arm in the app, ≈0.25s of the ≤10s budget. Drag path
unchanged (≈1.2–2s). Neither path adds a tap, a confirmation, or a
retry.

**HOLD / TRUTH check.** HOLD: nothing here exists after S0; the abort
window closes *at* the seat frame, not after it. TRUTH: mesh remains a
pure function of pullX under both drivers (finger, profile) — one
rendering of position, no per-tooth state, teeth still encode nothing;
the state word flips only on the frame the state becomes true.

**Strongest case against my own proposal.** It breaks my own ruling —
*no motion without input* — in spirit if not in letter. The swinging
tab died because it kept moving after the hand stopped; the driven tape
moves for 250ms after the hand has not merely stopped but *left*. A
critic can fairly say I have built a very honest animation of a thing
that did not happen: nobody pulled this zip. My defence is narrow and
I state it as a limit, not a rebuttal: the tap IS the input — a
complete, deliberate order, issued on the accent itself — and the
motion is that order executing at the mechanism's own speed, never
idle, never ambient, never repeated. But if the studio judges that a
closure must be *moved*, not *ordered*, this section falls and the tap
should seat instantly on release with no driven run at all — an uglier
commit that at least never pretends a hand was on it. I flag that as
the live alternative for Marlowe's merge, priced: instant seat loses
the mesh, the rattle, the deceleration, and the shared final 110ms —
everything that makes tap and drag the same closure.

### 1.4 The named fight — Fen (gesture / cadence), on t=0

Fen's filed slide (seat 08) and mine cannot both be the Deck's slide,
and the difference is not taste, it is one frame. Fen: latch arrival at
0.97 (knock 14ms @ 0.85), a 90ms snap, then a **120ms dwell while
latched, and only then the seal fires**. Under Fen's clock the zip sits
visibly closed — latched, knocked, at rest — for 120ms while the app is
*not yet armed*. That is precisely the frame my law exists to forbid: a
picture of a closure the fact hasn't made. His t=0 is the end of a
dwell; mine is the seat frame; Moss counted both, and he was right to.

My proposed settlement, mailed to Fen with this file:

1. **t=0 = the seat frame, for both our specs.** His dwell moves to
   *after* S0, where it becomes what his own completion beat already
   knows how to be: stillness that follows a fact instead of preceding
   it. The 120ms of quiet survives — as the first 120ms of §2's
   post-seat hold, not as a pre-commit antechamber.
2. **His knock-pair survives, re-timed.** Latch knock (14ms @ 0.85) and
   seal thock (18ms @ 1.0, echo 7ms @ 0.30 at +55ms) collapse into my
   seat knock's frame: one knock, 18ms @ 1.0, *on* S0, with his echo at
   S0+55ms as the pair's smaller shadow. Two knocks 120ms apart said
   "arrived, then committed" — two events. One knock and its echo say
   one event, heard fully. His completion pair (+120ms interval) stays
   untouched, so arm and end still read as different weights.
3. **His 0.68 gate and neutral-until-gate sled fill do not survive on
   the tape.** The pull is matte COMMIT orange from rest — the slide is
   the Deck's one accent, and a sled that *becomes* orange at 68%
   renders "committing" as a colour change ahead of the fact (colour
   leading position is the lie his own file warns against). His gate
   tick (6ms @ 0.45 at 0.68) may live as haptic grain only, inside my
   per-tooth schedule — felt, never drawn.
4. Geometry (his 248dp track / 68dp sled against my full-width track /
   48dp sled) is Marlowe's merge call, not settled here — but t=0 is
   not geometry, and it is settled here if he signs.

If Fen refuses (1), the fight goes to Marlowe named, unresolved, with
both clocks printed. I do not split the difference: a dwell averaged to
60ms is still a frame where the picture and the fact disagree.

---

## 2. The seat frame as climax — the 300ms the Deck exists for

### Beauty case, plain words

Every app has one instant it is *for*. Ours is not the wheel turning or
the numbers set — it is the frame the zip finishes. For 110ms before
it, everything in the app is decelerating toward one point; on it,
five channels — picture, knock, clack, word, state — land on the same
frame and nowhere else in the app do they ever coincide; for 190ms
after it, the app does the bravest thing a screen can do: nothing.
Stillness after a knock is what a real door feels like in the hand.
This section spends the tape's entire spectacle budget inside those
300ms and not one frame outside them.

### Technique

**Before the seat (S0−110ms → S0): the approach is a deceleration you
can hear.** The assist spring's own physics does the work — no added
curve. Tooth ticks, scheduled on tooth-index change, naturally space
wider as spring velocity falls; the coalesce floor (12ms) stops
mattering and individual ticks separate: rattle → ticks → one tick →
knock. Orson's zip noise tracks the same velocity down into the dry
clack. The eye's version of the same deceleration is new, below.

**Progressive mesh — the one rendering change to the filed tape.**
Filed, a tooth flips from open to meshed the frame pullX passes it:
binary, 60Hz-poppy at close range. Beyond: a tooth's interlock angle is
a continuous pure function of pullX — as the pull's leading edge
crosses the 6dp window centred on the tooth, the tooth rotates
0° → ±22° (upper row −22°, lower +22°, centres offsetting half a pitch
across the same window). Still no per-tooth state; the function gained
a window, not a memory. Every tooth now visibly *gets taken* by the
pull instead of blinking shut — and the function pays for the climax:

**The last tooth.** The final pair, at the garage mouth, is crossed by
the pull across the whole assist: its window is the last 6dp of
travel, which the spring covers in the final ≈35ms. So the last tooth
rotates slowly, against the spring's dying velocity, and reaches full
±22° interlock *on the seat frame itself* — the mesh completing is not
near the climax, it IS the climax's picture. On S0 its rotation stops
at the same instant the sled meets the crimps: two contacts, one frame.

**The light on the seat (Mara's rig, unmodified).** Mara's source is
fixed in space — L = normalize(−0.42, −0.91), upper left, forever — and
her sled band lags travel (`bandOffset = −0.35 × travelFromRest`).
Follow that function to the end of the track: at travel = 1.0 the
band's centre has counter-slid 0.35 × 272dp ≈ 95dp behind rest — it has
travelled *off the sled's lit face entirely*, clipped by the sled's own
trailing edge. So on the seat frame the moving light leaves the mover.
What remains lit is the result: the completed satin line, 38 teeth at
+1 tone, unbroken from margin to garage — the brightest horizontal on
the Deck, achieved by *completion*, not by any brightness change. The
specular hands off from the pull to the closed tape. No animator does
this; it falls out of her function and my geometry agreeing, which is
why it is honest. (If Mara's band is ever re-anchored per-stop rather
than from rest, this hand-off must be re-derived, not assumed.)

**On the seat frame (S0), all of it, once:**

- Last tooth reaches full interlock; sled at 1.5dp overshoot contact.
- Seat knock 18ms @ 1.0 — the largest haptic amplitude on the Deck.
- Zip clack transient (Orson) — the only percussive sound on the Deck.
- Arm callback invoked from this frame's draw; session monotonic clock
  read here. This is t=0 (§1).
- State word flips (`SLIDE TO ARM` margin grammar → the running
  state's first rendering) on this frame, never a frame earlier.
- Fen's ground step begins: Deck ground steps one tone darker
  (`#171412` → the recess step) over 250ms linear — *begins*, post-fact.

**After the seat (S0 → S0+190ms): the hold.** Nothing on the tape
moves. The echo knock lands at S0+55ms (7ms @ 0.30 — Fen's pair,
re-timed per §1.4). The overshoot recovers its 1.5dp across the spring's
own settle (≈S0+80ms) — the only post-seat motion, and it is the spring
finishing, not an animation starting. From S0+80 to S0+190 the track is
utterly still: a closed zip, a sunken sled (§3), a man already inside
the session. The next thing that ever moves on this screen is the
timer, and it moves because time does.

**What the eye gets here that it gets nowhere else:** the only
continuous rotation on the Deck (a tooth being taken), the only frame
where light changes ownership (sled → tape), the only completed
material line (full satin), the only five-channel coincidence, and the
only deliberate stillness. Each is a consequence of the mechanism, not
an effect laid on it — remove the zip and every one of them is
unbuildable, which is the test of whether spectacle belongs.

**What it replaces and why that loses.** The filed binary tooth flip
and the filed bare seat (knock + callback, nothing visual at the
instant itself). The filed seat was *correct* and completely unmarked:
the most important frame in the app looked like every other frame of
the drag, one more tooth among 38. A climax the eye can't find is a
spec the user never feels.

**ARM cost.** Zero added milliseconds. The 110ms before and 190ms
after are the filed assist and dead time the Deck already spends; the
ground step runs parallel to a session that has already started. The
seat does not delay the arm — the seat *is* the arm.

**HOLD / TRUTH check.** HOLD: the post-seat hold introduces no input
surface, no prompt, no motion that invites touch. TRUTH: progressive
mesh is still a pure function of pullX (teeth encode nothing — a tooth
at 11° says "the pull is here," which position already says once);
light carries no data (Mara's law — the hand-off is geometry, not a
signal anyone is asked to read); the word flips with the fact.

**Strongest case against my own proposal.** Progressive rotation is a
lie about zips, told by the fashion seat: real zip teeth do not rotate
gracefully through 22° — they snap past each other, closer to my filed
binary flip than to this glide. I am trading mechanism-truth for
legibility: at 8dp pitch and 60–120Hz the binary flip genuinely does
read as a shimmer, and the glide genuinely does read as meshing — but
"reads as" is a designer's argument, not a machinist's, and my whole
seat file is built on the machinist's side of that line. If Marlowe
rules mechanism over legibility, §2's rotation dies and the last-tooth
climax must be carried by the light hand-off and the knock alone —
which still stand, and the seat is still the peak. I can afford to
lose this one; I am not sure I should win it.

---

## 3. My beyond — the handle dissolves: no way back, rendered

### Beauty case, plain words

Here is the idea I filed around and never filed. When the zip seats,
the app is sealed — HOLD is absolute, there is no un-arm, no pause
negotiation, no door back. But the filed tape leaves the orange pull
sitting in its garage for the whole session: a handle, attached to a
closed zip, inviting the one gesture the law forbids. Every other
surface in this app renders its facts; the seated tape renders a
*fiction* — that this can be pulled back. So: on the seat, the handle
stops being a handle. The sled sinks flush into the garage and its
orange steps down through the night tones until the track is one
closed, handle-less seam — a zip you can see is shut and can also see
cannot be grabbed. The seal you cannot reopen should *look* like a
seal you cannot reopen. That is the most beautiful sentence this
control can say, and it says it in hardware, not words.

### Technique

- **S0 (seat frame):** exactly as §2 — sled orange, at the crimps,
  knock, callback. The commit must be *seen* to land in the accent
  before anything dissolves; dissolving early would eat the climax.
- **S0 → S0+160ms, two coupled moves, both stepped, never
  interpolated** (tone is a locked token or a named step, never a
  blend — the H-7 discipline):
  1. *Sink:* the sled drops 2dp into the garage recess over 160ms,
     cubic-bezier (0.5, 0, 0.8, 0.4) — its top face ends flush with the
     tape bands' plane; its catch-line migrates from its top edge to
     its bottom edge (it is now lit as a recess, not a raised part).
  2. *Step-down:* the sled fill steps through four locked night tones
     at 40ms intervals — `#F47442` (COMMIT) → a named mid token at the
     cap step's warm edge → `#362C27` (raised) → `#4A423C` (cap), where
     it rests. Four discrete frames of colour, like a hot thing cooling
     in clicks, not a fade. The grip ribs, drawn as recess hairlines,
     stay visible throughout — at cap tone they read as the garage's
     crimp marks, the sled's ghost in the hardware.
- **Resting sealed state:** the track is an unbroken line — matte tape,
  satin meshed teeth, and at its end a flush cap-toned plate where the
  handle was. There is no orange anywhere on the Deck while sealed:
  the accent was the *act* of committing, and the act is over. (Tasks
  START keeps its own orange; it is a different act.)
- **Return:** on session end (Fen's completion frame, after his 400ms
  stillness, riding his ground-restore), the sled steps back up through
  the same four tones in reverse and rises 2dp over 160ms, and the
  tape un-meshes as the *state* resets it to travel 0 — restoration is
  a state render, not a gesture and not an animation anyone waits for:
  the next arm starts from the ordinary idle Deck, sled orange at rest,
  exactly as on day one.
- **Sound / haptics:** none added. The sink is silent; the seat knock
  already happened. A click at S0+160 would mint a second event after
  the one event, and this file's whole religion is one event.

**What it replaces and why that loses.** The filed resting state: the
orange sled parked in the garage for the session's whole length. It
loses on truth — it renders an affordance that does not exist — and on
the accent law read strictly: orange is the COMMIT *fill*, the colour
of an action available or completing; a parked orange sled an hour
into a sealed session is chrome wearing the accent, which the budget
forbids everywhere else. The dissolve is the accent law enforced on my
own favourite object.

**ARM cost.** Zero. Everything here is post-seat and parallel; the
next arm never waits for a restore (restore completes inside the
completion beat's 600ms ground-restore, which is off every path).

**HOLD / TRUTH check.** HOLD: this is HOLD rendered — it removes a
false affordance and adds no real one; the sealed tape accepts no
input, and now it doesn't look like it might. TRUTH: one fact (sealed,
not reopenable from here) gets its one rendering (no handle); the
session's running state keeps its own renderings (numerals, words) and
none of them is duplicated by the seam — the seam says *how it closed*,
the numerals say *how long*, and neither borrows the other's job.

**Strongest case against my own proposal.** Recognition. The orange
sled is the Deck's landmark — the thing a barely-awake man finds by
peripheral vision at 03:30 — and I am proposing the app hide its own
protagonist for the entire session and re-materialise it afterwards.
If he glances mid-session to check *is it armed*, the honest answer
"yes, and here's the running timer" is carried by the numerals — but
the *control he armed with* has vanished, and a design that makes its
hero disappear must be very sure the fact it gains (no false handle)
outweighs the familiarity it spends. On day 200 he will know the seam
means sealed the way he knows a buttoned coat is buttoned. On day 2,
he may just think the slide broke. I judge the trade worth it — a
closure that lies about being openable is the one lie my lens exists
to kill — but it is a trade, and Sana's composition pass should grade
the sealed Deck with and without the plate before Marlowe locks it.

---

*Filed: plan/beyond-deck-parts/cato.md. Mail: Fen (seat frame / t=0 —
settlement proposed, fight named if refused). Inbox at wrap-up: three
letters (Ines, Moss, Yuki), all read and moved to `read/`; Ines's
assist divergence answered in §1 above, Yuki's and Moss's letters
concern Shield/the valve — outside this file's Deck scope, their
agreements stand as recorded in my seat file. Wall: signed on filing.*

*— Cato, Wild (fashion)*
