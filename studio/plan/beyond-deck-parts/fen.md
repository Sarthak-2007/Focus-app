# Fen — Wild, rituals · Phase 3 Beyond: the slide's gesture

*One mechanism, stated once, before the parts: the slide has three zones and
one commit rule. FREE 0–0.68 · DAMPED 0.68–0.84 (felt, reversible) · TAKEN
0.84–1.00 (powered, the machine's travel, not the hand's). **Commit = the
sled's displayed position crossing 0.84 under contact, or the tap-replay
crossing it under power.** There is no other commit rule in this file.
**t=0 = the proof-pawl frame**: the frame the state machine transitions
`SEATED → SEALED`, from which — and only from which — the session record,
the clock, the thock, and the ground recess all fire. Everything before
that frame is gesture; everything from it is session. Geometry, inherited
from my filed seat and held: track 248dp × 56dp, sled 68 × 48dp, travel
180dp. Opal's 264dp travel loses on the Deck (Item 2) whatever its Shield
merits. Accent: cap is neutral `#4A423C` until the gate, COMMIT `#F47442`
(night) only after it — colour confirms position, never leads.*

---

## 1. One mechanism, two entries — the tap is the drag, performed for him

### Beauty case, plain words

A tap that "also works" is a shortcut, and a shortcut is a second,
cheaper gesture living next to the real one — within a month the cheap
one is the gesture and the slide is a museum piece. So the tap here is
not a bypass. It is the same slide, compressed the way a signature is the
same name written faster: he presses and *holds his press*, and the
machine performs his slide for him, at the slide's own cadence, through
every stage he would have felt — the free run in silence, the tick at the
gate, the grain thickening, the orange arriving only where it should, the
latch knock inside the seat, the stillness, the thock pair. Nothing is
skipped and nothing is summarised. At 05:00, half-awake, he may not have
a clean 180dp sweep in his thumb; he always has a press. The press asks
the machine to do the travelling, and the machine does it *in his
gesture's clothes*, so the body learns one cadence from both entries —
the tap rehearses the drag even on the mornings he can't perform it.

### Technique spec

**Entry conditions (drag, unchanged in feel from my filed seat):** 1:1
travel, no easing — the hand is the curve. Free zone silent. Gate
crossing at 0.68, once per direction, 2dp hysteresis (tremor must not
re-tick a straddled gate): tick **6ms @ 0.45**. Damped zone: grain
**3ms @ 0.18 per 12dp**, sled drawn position lags pointer ≤6dp, spring
`dampingRatio 1.0, stiffness 900`, caught up within ~60ms of slowing.
Weight rendered, never enforced — my velocity-gated Kindling Slide stays
dead; there is no pace condition anywhere in either entry.

**The catch (replaces latch-at-0.97 as a demand on the hand):** when
*displayed* progress crosses **0.84** under contact, the catch engages:
the sled is taken. A **110ms powered seat** (Cato's spring, `stiffness
400, damping 16`, ≤1.5dp overshoot into the end-stop) carries it
0.84 → 1.00 whether the finger lifts or stays. Grain continues under
power as the sled passes each 12dp (~2 grains). The **latch knock
14ms @ 0.85 fires inside the seat at displayed 0.97** — 0.97 survives as
an event in the machine's travel, not a place a tired thumb must reach.
Then **dwell 120ms** of latched stillness. Then the seal invocation:
commit callback + blocking cascade dispatched on that frame, in that
order — never the thock before the callback (Prima's charge against
Zug-II's A1 is adopted as law here).

**The tap entry — full timeline (finger down = ms 0):**

| ms | event |
|---|---|
| 0 | Touch lands inside the track bounds, single pointer, centroid inside. Cap rises one tone step (picked up), marker 3dp → 4dp. Pickup tick **4ms @ 0.30**. No orange. Rosa's parallel checks dispatch *now* — the tap gives the truth layer ~780ms of parallel run, more than most drags do. |
| 0–180 | **Discrimination window.** The press must prove itself (below). Visual: the destination seat-print (hairline outline at the right inset) inks in over 180ms linear — the goal drawing itself is the only progress shown; the sled does not move. A press that becomes a drag in this window converts seamlessly: control hands to the drag path at progress 0, no penalty, no restart. |
| 180 | Window passed: eligibility tick **4ms @ 0.35**, marker → 5dp briefly. From here the replay runs. Release from here until the catch is an *abort*: sled returns on the universal return spring (`dampingRatio 0.78, stiffness 260`, ~320ms), silent — the exact feel of a drag released early. One vocabulary for "not committed," both entries. |
| 180–370 | **Free-run replay:** sled 0 → 0.68 in 190ms, `cubic-bezier(0.3, 0, 0.6, 1)` — a hand's profile: quick off the mark, settling toward the gate. Silence, per the free zone's law. |
| 370 | **Gate crossing:** tick **6ms @ 0.45**, cap fill flips to COMMIT orange — same frame, same haptic as a drag's gate. |
| 370–550 | **Damped-zone replay:** 0.68 → 0.84 in 180ms, `cubic-bezier(0.2, 0, 0.3, 1)` (decelerating — weight is time here), grain **3ms @ 0.18 per 12dp** (~4 grains), marker line trails the sled's leading edge by up to 6dp — the visual lag, rendered on the one mark the eye tracks, since there is no pointer to lag behind. |
| 550 | **Catch at 0.84.** From this frame the finger is free: lift or stay, the seat runs — exactly as a drag past the catch is taken. |
| 550–660 | **Powered seat** 0.84 → 1.00, 110ms, Cato's spring as above; latch knock **14ms @ 0.85** at displayed 0.97 (~620ms). |
| 660–780 | **Dwell:** 120ms latched stillness. The hand feels the stop before the app commits — the daily beat, present in the tap at full size, not folded away. |
| 780 | **Seal invocation frame.** Commit callback fires; if proof is already in hand (the normal case — checks have run since ms 0), this frame *is* t=0 (below). Thock pair: **18ms @ 1.0 + echo 7ms @ 0.30 at +55ms**; sound: harmonic thock, sine ~180Hz + one inharmonic partial, exp. decay ~220ms, low gain. Deck ground steps one tone darker over 250ms linear. |

Total tap, finger-down to seal invocation: **780ms** — faster than a
typical drag (0.7–1.4s), and it earns the speed honestly: it spends none
of it skipping a stage.

**Discrimination — how a brush does not seal (stated honestly):**
Android gives this app no trustworthy force signal — `getPressure()` is
OEM-dependent, frequently constant, and I will not build a safety
property on a sensor reading I cannot verify on his device. The
discrimination is therefore three conjunctive, checkable facts:
**(1) duration** — contact sustained ≥180ms before the replay may start;
a brush, flick, pocket-swipe or palm-graze lives at 30–120ms and is gone
before eligibility. **(2) stillness** — centroid displacement ≤8dp
across the window (fatigued tremor allowance is ±4dp, Item 2; a brush is
by definition travelling, and >8dp converts to drag or aborts, never to
tap). **(3) place** — contact begins inside the track, single-pointer;
a second pointer cancels the entry outright. Residual risk, named: a
stationary object pressing the exposed track for a full ~550ms
(through the catch) on an unlocked, foregrounded phone *will* seal.
That is not a flaw in the discrimination; it is the definition of intent
the hardware can support, and it is the same residual the drag carries
(a sustained accidental push seals too). What the spec refuses to do is
pretend a pressure threshold exists that would close it.

**Proof, proving, and the one t=0:** Checks run in parallel from
touch-down in *both* entries (Rosa's layer, unchanged in substance).
Deadline moved: the interlock is evaluated **at the catch**, not at
0.65 — the checks get the whole gesture, the refusal lands at the last
frame refusal is still free. Three outcomes at/after invocation:
proof complete → **t=0 = that frame**, record + clock + thock fire from
the `SEALED` transition (sound fired by the state transition, never by
an animation — Linea's discipline, via Prima). Proof incomplete at
780ms → state `SEATED-PROVING`: sled stays seated and held (Opal's
hold-bar geometry), no thock, no clock, no record; pawl frame, whenever
it lands within the 1500ms timeout, is t=0. Proof *fails* or times out
→ refusal: one dull knock **10ms @ 0.40**, the track's line states the
reason in Rosa's words (`BLOCKING IS OFF — NOTHING WILL BE HELD`),
sled returns on the universal spring after a 300ms refusal dwell. t=0
never occurred; no session exists; nothing is rounded up. Rosa's
crank-release clock, Orson's threshold clock, and my own filed
post-dwell clock all die here: the first two start a timer for a session
not yet proven, mine started it 210ms after a latch that might still be
refused. Opal's pawl was always the only honest zero; this spec keeps
his definition and moves the proving *earlier* (parallel from touch,
not serial after latch) so the pawl normally coincides with the seal
invocation instead of trailing it by up to 1.8s of uncounted limbo.

**ARM cost:** drag path unchanged, slide ≤1.7s worst; tap path 780ms
to invocation. Cold-open-to-sealed stays inside the honest ~6.1s
worst-case re-price Moss established — both entries add **0ms** of
serial wait to it; the tap's discrimination window runs concurrently
with the checks, not before them. **≤10s: PASS with the same margin as
filed.**
**HOLD check:** zero decisions introduced. The tap is not a dialog;
past the catch neither entry can be un-committed by the hand, and
during `SEATED-PROVING` the drawbar is inert.
**TRUTH check:** one commit rule, one t=0, sound and record both fired
by the state transition they celebrate. A released-before-catch gesture
in either entry mints nothing.

**What it replaces, and why that loses:** (a) *Tap-as-shortcut* (any
design where the tap skips the seal sequence) — Sarthak's verdict says
the tap runs "the same gate and seal sequence"; a shortcut tap violates
the verdict's letter and its spirit, and splits the body's learning
across two cadences. (b) *Designers' release-commit at displayed 0.82*
— release is the noisiest instant of the whole gesture (lift-off roll
moves the contact 3–6dp as pressure unloads); measuring commitment at
lift-off measures the tremor, not the intent. The catch measures under
contact, while the finger still damps the sled. (c) *My filed
latch-at-0.97* — see Item 2; I am striking my own number.

**Strongest case against this proposal:** the tap is not really a tap.
550ms of sustained, still press before the catch is a press-and-hold
wearing a tap's name, and a user who quick-taps the slide the way every
other app trained him — down, up, 90ms — gets *nothing*: no seal, no
feedback beyond a pickup tick, and on a dark morning possibly the belief
that the app ignored him, followed by a second, harder jab that becomes
a drag and aborts. The discrimination window buys its safety with the
exact currency the tap was meant to save — immediacy — and the replay,
however faithful, is the machine performing a gesture *at* a man who
asked to just start. If device testing shows his real taps landing
under 180ms and his frustration rising, the honest fix is shortening
the window toward 140ms and accepting the wider brush risk — not
pretending the trade isn't there.

---

## 2. The gate, decided by the hand — one zone, not four numbers

### Beauty case, plain words

Four numbers fought because each was answering a different question and
all four were called "the gate." A hand doesn't experience questions; it
experiences a road: a free stretch, a stretch that thickens, and a point
past which the road carries you. The beauty here is that the road's
shape matches the arm that's travelling it at 05:00 and at 19:30. Early
travel is where a half-awake thumb is strong and accurate, so the free
run lives there. The thickening starts while he still has authority, so
he *feels* the decision approaching instead of discovering it. And the
commit point sits before the far end, where the thumb is stretched,
shaking, and slamming into the track's end — the machine takes the last
stretch because the last stretch is where the hand is worst. The gate
is not a line he must hit. It is a pocket he enters, placed where his
body is still his.

### Technique spec — the body arithmetic

Design allowances (stated as allowances, not measurements — they are
the tolerances the geometry must absorb, and device testing may revise
them): comfortable one-handed thumb sweep without a grip shift ≈
25–35mm; fatigued / half-awake positional noise ±4dp at rest, rising to
±8dp at full thumb extension; lift-off roll 3–6dp.

- **Travel 180dp ≈ 28.6mm** — inside the no-grip-shift sweep. Opal's
  264dp ≈ 42mm exits it: the last fifth of his drawbar is travelled
  with a shifted grip, i.e. with the least stable hand configuration,
  at the exact end where his latch lives. On the Deck, that loses.
- **Gate (resistance onset) at 0.68 = 122dp ≈ 19.4mm.** Mid-arc, where
  control authority is highest — a tick learned here is learned by the
  strongest version of his thumb, and the weakest version recognises it.
  Designers' warn at 0.68 agrees; where they lose is making 0.68 *only*
  a warning and starting the real events at 0.80: a damped zone of
  0.80–0.97 is 31dp, crossed at ordinary drag speeds (300–600dp/s) in
  50–100ms — two grain ticks, no time to feel weight at all. From 0.68
  the hand-travelled damped stretch to the catch is 29dp plus the
  powered 29dp under grain ≈ 4–5 grains over ~250–400ms: a texture, not
  a blip. Humans' HPA-24 single gate at 0.65 is the right instinct
  (one number the hand can learn) with the wrong job assigned: 0.65 is
  where travel should start *costing feel*, not where commitment
  completes — magnet-complete from 0.40 commits exploratory drags, and
  at 05:00 a man steadying the phone against the track has made a
  0.40 drag he never meant as a vow.
- **Catch at 0.84 = 151dp ≈ 24.0mm.** The decisive placement. It sits
  inside the authority zone (before the final quartile where extension
  tremor doubles), and it converts the remaining 29dp — the worst
  29dp of the road — into machine travel. Crossing detection is
  monotonic and position-based, so the full ±8dp tremor allowance is
  absorbed: he does not have to *hold* any position, only to *pass*
  one, once, under contact. Contrast the filed latch at **0.97 =
  175dp**: 5.4dp from the end wall — a target narrower than the tremor
  band at that extension, demanding arrival precision from the hand at
  its least precise, and paying for near-misses with the end-wall slam
  read as a latch. I filed that number; the hand strikes it.
- **The zone, as the spec's final vocabulary** (adopting Wild §11.2's
  demand for one vocabulary, completing it): `FREE 0–0.68` ·
  `DAMPED 0.68–0.84` · `TAKEN 0.84–1.00`. The words *warn / catch /
  commit* as three separate thresholds are struck. Rosa's 0.65
  interlock loses its coordinate and keeps its function — refusal is
  evaluated at the catch (Item 1), the last free frame, with the whole
  gesture's worth of parallel check-time behind it instead of a
  mid-travel deadline the checks might miss while still healthy.
  The shipped build's 0.82 gate event is honoured as ancestry: 0.84 is
  within one grain-step (12dp ≈ 0.067) of it, so the hand that learned
  the shipped slide relearns nothing — the catch lives where the firm
  click always fired; what changed is that the click now *takes* him.

**Curves / haptics / sound / lag:** as Item 1 — gate tick 6ms @ 0.45
(2dp hysteresis), grain 3ms @ 0.18 / 12dp in both zones' travel
(hand or powered), visual lag ≤6dp in DAMPED only (spring 1.0 / 900),
seat 110ms (400 / 16), latch knock 14ms @ 0.85 at 0.97-in-the-seat,
dwell 120ms, thock pair 18ms @ 1.0 + 7ms @ 0.30 @ +55ms, seal sound
~180Hz / ~220ms decay. Silence in FREE, both entries, always.

**ARM cost:** the catch *saves* time against every rival: hand travel
required drops from 175dp (0.97) to 151dp, and the powered seat (110ms)
replaces the slowest, most-corrected final stretch of a tired drag.
Net slide cost ≤ filed 1.7s; typical drag-to-invocation ~0.9–1.2s.
**HOLD check:** no decision lives in any zone; DAMPED is feel, not a
branch — crossing it asks nothing, resting in it changes nothing.
**TRUTH check:** displayed progress is the commit input — the same
position value the eye sees is the value the catch reads, lag included
(lag ≤6dp means the catch can fire at most 6dp "early" against the
pointer, and it fires on the *rendered* sled, which is the sled he is
looking at; the pointer is not a displayed fact and cannot be lied
about).

**What it replaces, and why that loses:** the four-number composite
(§1.4 as merged) — a builder handed it must silently choose which seat
to disobey (Moss V-4), and a hand handed it must learn a gate that is
simultaneously a warning, a catch, a commit and a latch. Numbers that
name different events survive only as *events* (0.97 as the seat's
internal knock), never as rival thresholds.

**Strongest case against this proposal:** 0.84 is, in the end, another
number I chose, and the body arithmetic above is argued from allowances
I set — a sceptic (Moss, correctly) will note that ±8dp tremor and the
25–35mm sweep are design tolerances, not measurements of *his* hand on
*his* phone, and that moving the catch 24dp earlier than the filed
latch also moves it 24dp closer to an accidental full-push: a drag that
overshoots on a wet or startled morning now seals where the old 0.97
would have forgiven it. The mitigation is real (DAMPED's grain is
250+ms of felt warning before the catch) but it is a mitigation of a
risk this proposal itself enlarges. If his device ever logs gesture
telemetry, the first month of real drags — not this file — should be
allowed to move the catch, once, within 0.80–0.88, and then never again.

---

## 3. Beyond, of my choosing — The Sled Goes Home

*Neighbour territory to Terminal Stillness, which is mine: Stillness owns
the session's last 400ms. This owns the minute after — the slide's
return journey, which every plan so far leaves as a rendering detail.*

### Beauty case, plain words

All day the gesture is a round trip that only he performs: he pushes the
sled shut in the morning, and it simply… is somewhere else next time he
looks. Unfinished. The beyond: when a session truly ends — after the
stillness, after the one thock, after the receipt has landed — the sled
travels home by itself, slowly, along the same road, through the same
gate, in reverse. The machine sets its own part back on the bench. He
watches the road he will travel tomorrow get walked backwards by the
thing he armed, and the gate tick sounds in his hand at the same
distance from home it always sits — teaching the road a second time
each day, in the other direction, at the moment he is most depleted
and least able to be taught anything on purpose. A day that begins with
his hand closing the slide ends with the slide opening itself. Nothing
is asked of him. That asymmetry — he closes, it opens — is the whole
ritual content of this app, and it needs no name, no dawn, no story.
Just a part, going home, at letter-speed.

### Technique spec

- **Trigger:** the persisted `COMPLETED` transition (Terminal
  Stillness's state machine) — never an animation callback. Sequence:
  zero → 400ms stillness → thock pair (20ms @ 1.0 + 9ms @ 0.40 @
  +120ms) with ground restore → receipt lands → **+1000ms after the
  thock**, the return begins. Once-only guard is the beat's own
  `beatPlayedForSessionId`: if the app was dead at zero, the sled
  simply renders at home on next open and the return never plays —
  a return for a session he didn't watch end would be theatre.
- **Travel:** seated position (1.00) → home (0), **650ms,
  `cubic-bezier(0.3, 0, 0.5, 1)`** — deliberately *not* the universal
  return spring (0.78 / 260, ~320ms). That spring means "not committed"
  everywhere in this app, and this sled *was* committed, completely;
  the two returns must never feel alike in the hand or the eye. 650ms
  is the Week Sled's weekly tempo borrowed for a daily part: heavy,
  unhurried, machinery resetting (the weekly return keeps its own
  spring; tempo is the shared accent, not the curve).
- **Haptics, reverse road:** grain **3ms @ 0.18 per 12dp** through the
  damped zone in reverse (0.84 → 0.68 — the zone is a place, it grains
  both ways); at the gate position, the gate tick mirrored: **6ms @
  0.45**, once; below 0.68, silence, arriving home with a soft seat
  **8ms @ 0.50** — the bench knock, the quietest knock in the file,
  because nothing is being decided. Cap fill throughout the return:
  neutral — the orange drained at seal, per the running-state law; the
  return carries no accent, because returning is not committing.
- **Sound:** explicit silence. The session's one thock has sounded;
  a second sound event would spend Terminal Stillness's scarcity.
  The return is for the eye and, if the phone is in hand, the palm.
- **Interrupt:** the return never blocks anything. A touch on the sled
  during the return captures it at its current position and hands
  control to the drag path (progress = current displayed position) —
  the next arm can begin *from the road*, mid-return, with no snap
  home and no wait. If he arms again, the return is simply over,
  unrecorded, unremarked.
- **Depth / material:** sled flush in the recess throughout, marker
  line constant; the track's travelled-floor tone step (the half-step
  lift behind the sled during a drag) unwinds in reverse with it —
  the road visibly un-travels.

**ARM cost:** 0ms. Post-session, off every path; the interrupt rule
means it cannot even delay a re-arm by a frame.
**HOLD check:** zero decisions; it renders a completed fact and offers
nothing — no re-arm prompt, no "again?", the sled arriving home is not
an invitation, it is a part at rest.
**TRUTH check:** fires only from the persisted COMPLETED transition,
once per session, skipped silently when unwitnessed. It claims one
thing — *the session ended* — in motion, a fact already stated by the
receipt line in type. (One fact, two renderings? No: the receipt states
the session; the return states the *control's* state — sled home =
Deck ready — which the receipt does not carry. Different facts, one
home each.)

**What it replaces, and why that loses:** the implicit default in every
filed plan — the sled silently re-rendering at home whenever the Deck
next composes. That default loses because it makes the app's central
object teleport: a part he seated at 05:00 is inexplicably back at rest
at 19:30, and a machine whose parts move when unobserved is a machine
whose parts mean less when observed. Physical continuity is the
slide's whole claim to being an object rather than a widget.

**Strongest case against this proposal:** it spends motion — the
scarcest currency in this app — on a moment nobody needs. Terminal
Stillness works *because* the app finally stops; a sled that sets off
on a 650ms journey a second later re-opens the scene the stillness
closed, and on the worst evenings it will read, however I swear
otherwise, as the app tidying up after him or quietly asking for
another session. There is also a plain mechanical insult in it: he
ended depleted, and the freshest-moving thing on the screen is the
machine. If it ships and his eye goes to the receipt and never to the
sled, this beyond is a paragraph about a gesture nobody saw — and it
should be cut without mourning, because the stillness it neighbours
was always the stronger idea.

---

*Filed: Fen — Wild, rituals. Velocity-gating remains dead by my own
hand. My filed 0.97 latch is struck by this file; my gate, grain, dwell
and thock pair stand.*

**Addendum — Cato's seat-frame claim (mail, thread `seat-frame-t-zero`,
answered and escalated to Marlowe):** Cato proposes t=0 = the seat
frame, the dwell moved after t=0, the knock-pair collapsed onto the
seat, orange from rest, and a 140ms driven tap run. Refused in full,
in his inbox: the seat renders INTENT, the pawl renders PROOF, and a
clock started at the seat counts unblocked time as sealed on any
morning the cascade fails after it; the dwell counts no time and is
therefore not a second clock; orange-from-rest breaks the locked
COMMIT-only accent and Nacht's zero-orange-until-the-gate eye order.
Both clocks are printed in the reply letter, named as ours, for the
merge to rule on.
