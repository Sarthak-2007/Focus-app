# Orson — Wild, synths · Phase 3 BEYOND: the Deck

*Owner: the sound engine's room layer and the seal's voice. Treaty stands:
Ivo owns Tasks-local acoustics; nothing below touches his desk. Filed plan
is the floor. Two corrections from Critics are adopted in this file, not
argued with — Moss's kill-shots (phase2-moss-vs-wild, V-5 / V-14) were both
right, and both were predicted by my own seat file's standards checks:*

- *The bolt's duration slope is CUT. Bolt gain is fixed at **−6 dB** at
  every duration, FLOW included. My filed check said: if the weighting ever
  reads as a score, cut the weighting, keep the bolt. A 3 dB slope no ear
  can invert is a score. Where §1–§3 below say "the bolt," they mean the
  fixed −6 dB bolt.*
- *The bank latch never lifts. In the valve, a slip's drain now renders as
  pitch rising and **halting at the banked floor** — the latch staying down
  is the mercy, heard as the drain stopping, not reversing. (Valve layer,
  noted here so the merge cannot re-import the lift from my filed seat.)*

---

## 1. The quiet-hours seal — THE LAID SEAL

### The beauty case, in plain words

At 05:00 he seals a three-hour fasted block in a house where other people
are asleep. The reference Throw at 0 dB on a phone speaker in a silent
house is not a private sound — it is a broadcast. Everyone in earshot
learns that Sarthak has armed his phone. The seal was specced for a room
he is alone in, and the most frequent arm of his day does not happen in
one.

The fix is not turning it down. A seal played quieter through the same
wet room tail sounds like a seal happening *in another room* — distant,
thinned, lesser. He would feel the app apologising at the exact moment
it should feel most certain. Distance is what reads as weakness, not
level.

So the dawn seal changes *where it happens*, not how much it matters.
The reference Throw is a mass dropped inside the body of the machine —
wet, ringing in the shared room. The Laid Seal is the same mass **set
down by hand, at the surface, against the palm**: dry, close, soft-edged,
with its weight moved out of the air and into the hand that laid it.
A whisper at the ear is not a weaker shout. It is a nearer voice, and
nearness is deliberateness. At 05:00 the seal should sound like a man
closing a heavy door *carefully* — which is a more deliberate act than
slamming it, and everyone who has ever lived with other people knows it
in their hands before they know it in words.

### What sets the level — the rule, and why it is a fact

**Not the clock.** A clock window (say 04:30–06:30) is a guess about his
life wearing a number. It would lay the seal for a manual 05:00 arm on a
morning he slept at a friend's empty flat, and it would throw at full
voice for a routine arm at 06:45 in a house still asleep. My filed
Silence Stack already bans clock-derived silence for exactly this
reason; a clock-derived *voice* is the same guess in a softer costume.

**The priming routine's identity is the fact.** The engine knows, from
the event store, three recorded things: the scheduler fired a routine
(`routineFireTimestamp` + routine id — the same records routine
attendance is already computed from), that firing laid the Deck out
(wheel preset, loadout seated — Fen's settled mechanism, no cocked-Deck
theatre), and no seal has landed since. The rule:

> **LAID SEAL iff** this arm's Deck was primed by a routine firing **and**
> that routine is the first routine to fire after the SLEEP routine's
> window closed **and** no seal has yet landed inside that routine's
> window.

In his registered day that conjunction is true of exactly one arm: the
05:00 FASTED BLOCK arm (SLEEP 20:00–03:30 closes, FASTED 05:00 fires,
Deck primed, first seal of the window). Every term is a stored record,
not an inference. Three corollaries, all deliberate:

- **A manual arm never gets the Laid Seal** — not at 05:00, not at
  03:00. If he cold-opens and arms by hand, no routine primed it; the
  engine has no fact to lay the seal on, so it throws. He is awake,
  acting unprimed, and the machine does not invent a sleeping house
  around him.
- **A second arm inside the FASTED window throws.** The first seal is
  the one that could wake the house *into* his morning; the routine's
  window has already been opened by a seal. (And the fasted block is
  normally one seal, 05:00–08:00 — the rule prices the real day.)
- **UNKNOWN defaults to the Throw.** If priming state cannot be read
  (store miss, race at first composition), the seal renders at reference.
  Quiet on a guess would be the engine lying about why it whispered;
  loud on a fact-gap is merely the filed behaviour. Fail toward the
  spec he already approved, never toward the new one.

Precedence: **S0 still wins absolutely.** If DND / system silent /
SOUND EFFECTS off / proximity applies at 05:00, there is no Laid Seal
audio at all — the haptic twin below carries the whole event, exactly
as S0 already degrades every other voice. The Laid Seal is a *voice
variant inside* S2/S1 rules, not a new silence class and not an S0
exception. The 20:00 SLEEP arm is untouched: it is S0 already.

### Technique spec

Identity preserved first: same `playHarmonicSeal(arming = true)`, same
4-partial stack `{0.6, 0.28, 0.37, 2.2}`, same tuning. One seal, one
sound, wherever and whenever it happens — the Laid Seal is the Throw
*placed differently*, never a second seal voice (treaty law: a voice
means one thing). What changes is placement, edge, and where the mass
sits:

| Layer | Reference Throw (0 dB ref = gain 0.62) | Laid Seal |
|---|---|---|
| Breath (scheduled pre-seal silence) | 120 ms | **200 ms** — the house's silence is deeper; the held breath matches it. Still inside the sound; blocking engages at t=0 regardless (§2). |
| Seal body gain | 0 dB (0.62) | **−14 dB** (≈0.124). Physical target, stated plainly: clearly audible to the holder at arm's length in a silent room; not intelligible as an event through a closed door. |
| Seal attack | ~3 ms, 48 ms body | **12 ms attack ramp, 72 ms body.** A laid object has no snap. Slower onset is the single largest anti-startle move available — startle lives in the attack, not the level. |
| Spectrum | open, partial 4 at full weight | lowpass **2.4 kHz**; partial 4 (the 2.2 bright partial) an extra **−8 dB**. Brightness is what carries and what startles; the pitch identity lives in the lower partials and survives intact. Nothing moves below the 280 Hz fundamental floor — the speaker still never pretends to sub-bass. |
| Room send | native Room tail (2 combs 29.7/31.1 ms + allpass 5 ms, ≈18 ms pre-delay) | wet send **−26 dB** — near-dry. Per the engine's depth law, dry = at the surface. The reference seal sounds *inside the Monolith*; the laid seal sounds *in the hand*. Proximity replaces loudness as the weight cue. |
| Bolt layer | +30 ms, noise LP 900 Hz, 55 ms, **−6 dB fixed** (slope cut, above) | **+18 ms, 70 ms, −10 dB absolute** — only 4 dB below its reference level while the body drops 14. Lowpassed 900 Hz noise does not carry through doors; it is the one layer that can stay heavy safely. Result: at dawn the ear hears mostly *mass*, the pitch-identity riding under it. The ratio inverts; the weight doesn't. |
| Cap visual twin | marker line one tone darker, 300 ms exp decay | same step, **500 ms decay** — the struck look lingers longer at dawn, matching the longer haptic and slower attack. Orange exactly where Deck already spends it; nothing new drawn. |
| Ceiling / queue | soft ceiling at 0 dB ref, queue depth 1 | unchanged. The Laid Seal sums far under ceiling by construction. Late = dropped, as ever. |

**The haptic twin carries what the sound steps down.** Reference seal
haptic: **24 ms @ 1.0, 38 ms gap, 16 ms @ 0.69** (~78 ms total). Laid
Seal haptic:

- **36 ms @ 1.0** (the body, laid — longer, not louder; amplitude is
  already at the motor's honest maximum and pretending past it is a lie)
- **30 ms gap**
- **22 ms @ 0.80** (the shadow strike, weightier than reference's 0.69)
- **under the bolt at +18 ms: a 60 ms @ 0.35 swell**, envelope following
  the bolt's noise shape — the mass arriving in the palm as a swell, not
  a buzz.

Total haptic event ≈ **200 ms**, against the reference's ~78 ms. The
hand receives *more* event at dawn while the room receives less. That
asymmetry is the whole design: deliberateness is duration in the hand,
discretion is level in the air. Audio/haptic issue from the same call
site, locked within the engine's standing **±8 ms**, or the palm and
the ear stop believing it's one object.

**Non-audio completeness (Silence Stack law — every sound has a twin):**
under full S0 at dawn, haptic + 500 ms cap step carry 100% of the
event's information (sealed / not sealed). Nothing about the Laid Seal
exists only in audio.

### ARM cost

**Zero.** The level is read from store facts already in memory at
composition (priming routine id is a Deck state, not a query added to
the arm path). The 200 ms breath is scheduled silence inside the seal's
presentation; blocking engages at t=0 = the seal frame per §2, identically
for both voices. No gesture added, no setting added — there is no volume
UI and no per-routine toggle to configure; the routine he already
registered is the entire input.

### HOLD / TRUTH check

- **HOLD:** post-commit rendering only; introduces no mid-session
  anything. S1 session suppression unchanged.
- **TRUTH:** the voice encodes no number and no score — bolt is fixed
  (slope cut). What the variant *renders* is a placement, and placement
  in this engine has always been a rendering condition, not a fact claim
  (the filed escapement already dries and drops 2 dB at night; the Laid
  Seal is that precedent made honest by keying it to a stored fact
  instead of an ambience). It fires only at the t=0 state transition,
  after cascade proof — a quiet seal for a session that didn't seal
  would be exactly as much a lie as a loud one, and §2's gate forbids
  both. One residual discipline: the variant must never be *shown* —
  no "quiet mode" label, no icon, no settings line naming it. The
  moment the app prints that it whispered, the whisper becomes a
  feature he manages; unlabelled, it is simply how the seal sounds at
  dawn, the way a careful man simply sounds at dawn.

### What it replaces, and why that loses

It replaces **the single-voice Throw at all hours** (my filed spec: the
reference seal, unmodified, for every arm). That loses on the filed
spec's own terms: my Silence Stack exists because "his day has five
acoustic situations" — and then priced the *most frequent situation of
his day* with the voice designed for an empty room. A binary answer
(full voice or S0) forces the 05:00 arm into a choice between
broadcasting and muting the app's single most consequential sound.
It also replaces the naive alternative — **a global −14 dB "night
gain" on the seal** — which loses as argued above: same wet tail,
lower level = a seal in another room = lesser. Gain is the lazy axis;
placement is the true one.

### Strongest case against this proposal

The routine-identity rule is only as honest as the routine registry is
stable. If he edits FASTED to 06:30, or deletes and re-registers it, or
a future routine (a 04:00 meditation block) becomes the first-after-
SLEEP firing, the Laid Seal follows the *rule*, not his intention —
it will lay the seal for an arm he experiences as ordinary morning, or
throw at 05:00 on the one morning the routine failed to fire and he
armed the primed-looking Deck by hand. The rule cannot hear the house;
it can only read the store, and there will be mornings the store's story
and the house's story differ. My defence is the UNKNOWN→Throw default
and the conjunction's narrowness — but narrow is not never. If on-device
weeks show the mismatch is felt even once as *the app whispering for no
reason*, the variant should die and the Pure Seal fallback logic applies:
ship the reference voice at all hours rather than a dawn voice that
guesses wrong audibly. A seal that is occasionally too loud is a
nuisance; a seal whose behaviour he cannot predict is a machine he
stops trusting at 05:00 — and 05:00 is the whole game.

---

## 2. The tap's throw — one t=0, two entries, same seat instant

### The beauty case, in plain words

His verdict settled it: TAP CAN COMMIT, the tap runs the same seal
sequence as the drag. But my filed Throw was timed against a *journey* —
a drag gives the machine 1.2–2 seconds of travel to hide its work in:
Rosa's checks run parallel with the thumb, Fen's dwell gives arrival a
beat, my breath cuts a bed that exists because the thumb has been
moving. A tap has no journey. It is a press: down, up, done — and if the
seal simply fires on the up, the tap feels like a *button that makes
the seal noise*, cheaper than the drag in exactly the way he will feel
every morning he uses it.

The tap deserves the same ending the drag gets, arrived at honestly.
The drag's last 210 ms — snap, seat, dwell, breath, seal — is a cadence
the body already knows. The tap can have that identical cadence if its
travel happens along the axis a press actually has: **down**. The sled
doesn't fake sliding sideways (motion without input is a lie — Cato's
and Wren's law, and mine). It *sinks*: pressed one tone step into its
recess over the same 90 ms the drag's snap takes, sits in Fen's dwell —
which is my breath; they were always the same 120 ms wearing two
layers' names — and seals on the same frame definition the drag seals
on. Two entries, one terminal cadence, one t=0. The tap is not the
drag's shortcut. It is the drag's last third, performed by a press.

### The one t=0 (the settlement Moss's charge demands)

Phase 2 proved the composite defined the seal's start four ways —
crank-release, threshold crossing, post-dwell, proof-pawl. Here is the
single definition the sound layer will obey and asks the merge to adopt:

> **t=0 is the frame the `IDLE → SEALED` transition writes** — the same
> transaction that creates the session record, starts the clock, and
> engages blocking. The seal's attack fires **on that frame**, issued by
> the state transition, never by an animation, a threshold, or a spring.
> (Prima's template, credited: Linea's receipt discipline — sound fired
> by the state transition — is the slide's law too.)

Everything before t=0 is *approach*, and approach may render only facts
that have already happened (latch contact, press depth). Everything the
old composite let leak before t=0 is struck from my layer: blocking does
**not** engage at threshold crossing (my filed line, withdrawn — it made
the session exist 210 ms before its own seal and its own record), and no
seal partial sounds before the write. If cascade proof fails inside the
dwell, there is no seal, no record, and the sled springs home — the
interlock refuses *silently in the Throw's voice*, because the Throw
never spoke.

### Full timelines (both entries end at the same t=0)

**DRAG entry** — intent point = latch arrival at 0.97 travel:

| t | Event |
|---|---|
| −210 ms | Latch arrival (Fen): knock **14 ms @ 0.85** — a Strike transient rendering a contact that has happened. Drag audio bed and grain haptics still live. |
| −210 → −120 | Snap spring, **90 ms** (Fen's figure governs the Deck slide). Sled seats in the garage. |
| −120 ms | **Seat frame.** Dwell begins. At this frame the engine cuts drag bed + grain + sled audio to zero — **the dwell and the breath are one 120 ms interval**, Fen's stillness in the hand, my scheduled silence (S3) in the ear. Cascade (Rosa's parallel checks, running since early travel) takes its final proof inside this window. |
| −120 → 0 | Dwell / breath. Nothing sounds. Nothing moves. Proof completes or refuses. |
| **t=0** | Proof true → `SEALED` writes: blocking engages, clock starts, **seal attack fires** (Throw or Laid Seal per §1). Bolt at **+30 ms** (Laid: +18 ms). Bed arrival per §3. |
| refusal | No t=0 exists. Spring-back on Fen's return spring (dampingRatio 0.78, stiffness 260, ~320 ms), `playMechanicalLatch(engaged = false)` at −9 dB seats the cap home. A seal he didn't make is never heard — filed law, kept. |

Drag audio during travel, one merge ruling from my layer (Moss V-2:
my drag bed cannot coexist with Fen's explicit drag silence): **Fen's
free-run silence is adopted for 0 → 0.68** (the absence is what makes
the gate felt), and my filed runner survives only inside the damped
zone — fibrous scrape (cutoff 380 Hz + 2100 Hz × x², gain ∝ velocity,
−20 dB at full haul) and the 1240 Hz steel ring fading in from 0.75.
The breath then has something real to cut for its final 120 ms, and the
first two-thirds of the slide are as silent as Fen specced. One drag
voice: his silence, then my material, then nothing, then the seal.

**TAP entry** — intent point = press DOWN:

| t | Event |
|---|---|
| −210 ms | **DOWN.** Press registers (real press state — the drawn sled depresses). Two things start on this frame, not on UP: (1) Rosa's cascade fires immediately — the tap has no travel to hide the checks in, so the press itself is their window; (2) press transient, Strike family, **8 ms @ 0.60**, dry — a fact (contact) that has happened. |
| −210 → −120 | **Press-settle, 90 ms** — the snap's equal. The sled sinks one tone step into its recess (z-travel, the press's true travel; no x-travel is drawn or implied). Haptic: continuous light press bed, **0.25 amplitude**, so the hand feels held weight, not a click. |
| −120 ms | **Dwell gate.** The press enters the same 120 ms dwell/breath as the drag's seat frame. All press audio cuts; the scheduled silence is sample-identical in function to the drag's. Cascade proof completes inside this window (typical case: already true — see ARM below). |
| **t=0** | Identical to drag: `SEALED` writes, blocking engages, clock starts, seal attack — same voice selection (§1), same gains, same bolt offset, same haptic waveform. **A listener with eyes closed cannot tell which entry sealed the session. That is the acceptance test.** |
| Early UP (< 90 ms press, i.e. UP before −120) | **Cancel.** A brush, not a press. Sled springs back on the universal return spring, one dull Strike **10 ms @ 0.40**, no dwell entered, no seal, no record. This mirrors the drag's release-before-latch = non-event rule, at the same cadence boundary: **before the dwell, releasable; inside the dwell, committed** (proof permitting). One rule, both entries. |
| UP inside the dwell (−120 → 0) | Commit is locked; the finger leaving changes nothing — the drag's hand is likewise past the latch. Seal fires at t=0. |
| Proof late | If the cascade hasn't proven by t=0, **the dwell stretches**: sled stays sunk, silence holds, seal waits for the write. The breath lengthening is the only visible/audible sign, and it is truthful — the machine is, in fact, not yet able to seal. Proof failure → refusal path as drag. |

**Numbers mailed to Fen and Cato with this file** (letters in their
inboxes, thread `one-t-zero`): t=0 = the SEALED write frame; terminal
cadence **90 + 120 = 210 ms** for both entries; dwell ≡ breath (one
interval, two layers); bolt **+30 ms** from seal attack (+18 Laid);
Fen's 90 ms snap governs over Cato's 110 ms assist spring **on the Deck
slide** (Cato's 110 stands on his zip's other surfaces — but the Deck
seat frame and the sound frame must share one number, and the dwell
arithmetic is Fen's); tap cascade starts at **DOWN**, cancel boundary
at **90 ms** of press.

### ARM cost

**~0 added, priced honestly for both entries.** Drag: the 210 ms cadence
was already inside Fen's ≤1.7 s slide figure (snap + dwell were his);
unifying the breath into the dwell *removes* my filed extra 120 ms
rather than adding anything. Tap: 210 ms total from DOWN to sealed on
the typical path — against a 10 s budget and a drag's ~1.2–2 s, the tap
remains the faster entry, and its cadence is presentation the user can
feel but never waits *behind* (the press is the wait; there is no
spinner, no lock). Worst case is symmetric and stated, not hidden:
cascade worst case (~2.5 s per Rosa's pricing) stretches the dwell for
**both** entries — in the drag it hides inside travel, in the tap it
shows as a longer held press. The tap's honesty tax is that its worst
case is visible. Total worst-case arm stays ≈6.1 s per the corrected
Phase-2 accounting — inside budget, claim unfudged.

### HOLD / TRUTH check

- **HOLD:** the tap introduces no new decision — it *is* the arm
  decision, one gesture, no confirm step, no "tap again to be sure."
  The 90 ms cancel window is not a decision prompt; it is the press's
  physics, identical in spirit to releasing a slide early.
- **TRUTH:** no sound before the write (the Zug-II sin — seal 260 ms
  before its callback — is designed out at the definition level, not
  patched). The press-settle draws only the travel the finger truly
  makes. The stretched dwell never claims a seal is coming; silence
  plus a sunk sled claims only *not yet*, which is true. If proof
  refuses, both entries refuse identically — a tap must never be the
  entry that seals what a drag would have refused; same gate, same
  cascade, same interlock, per his verdict's own words.

### What it replaces, and why that loses

It replaces **my filed Throw timing** (breath starts at threshold
crossing; blocking already engaged during the breath) and **any
tap-fires-seal-on-UP scheme**. The filed timing loses because it splits
the session's birth across 210 ms — blocking live, record unwritten,
seal pending — and three other seats filled that gap with three other
births; Moss counted four. A centre gesture with four t=0s has none.
The on-UP scheme loses because it spends the seal on a finger *leaving*
— the least committed instant of a press — and skips the cascade's
window entirely, which is how you get a seal sounded for a session the
blocker hasn't proven: celebration before the fact, the one lie this
engine is forbidden to tell at any gain.

### Strongest case against this proposal

The 90 ms cancel window cuts both ways. A deliberate but *fast* tap —
the practised flick of a man who has armed this phone two hundred
times, contact well under 90 ms — reads as a brush and cancels. The
drag has no equivalent failure: travel can't be too fast (Fen killed
the pace gate deliberately, and this spec reintroduces a pace floor
through the back door, on the entry most likely to be used at 05:00
half-asleep). Mitigations exist — drop the floor to ~50 ms, or commit
on any DOWN whose UP lands after cascade proof regardless of duration —
but each thins the brush/commit distinction that keeps a pocket-press
or a fumbled pickup from sealing a three-hour block. My call stands at
90 ms for one reason: it is the drag's own snap duration, so the hand
learns *one* terminal number for the whole control. If his real taps
measure faster on-device, the number must move to his hand, not the
argument — spec the floor as a token (`TAP_COMMIT_FLOOR_MS`, default
90), measure, retune once, freeze.

---

## 3. The beyond I held back — THE ROOM IS THE THROW'S TAIL

### The beauty case, in plain words

Every session in Monolith begins the same way in my filed spec: the
seal fires, and then — as a separate, engineered event — the room fades
in under it over 2.5 seconds, a seeded bed loop crossfaded up like a
stagehand raising a fader. It works. It is also, if I'm honest, two
events pretending to be one: a door slamming, then a room being
*provided*.

Here is the version I didn't file because it sounded like a concept,
and the law kills concepts: **there is no bed arrival. The seal's own
decay is the room arriving.** The Throw already renders through the
shared Room tail — two feedback combs and an allpass. Let the tail do
what a real room does: the sound you made in it *becomes* the air of
it. The comb feedback is set so the seal's decay doesn't die at 48 ms
plus a tail — it falls, and falls, and at −46 dB it is *caught*: the
seeded bed crossfades in underneath at exactly the level the tail has
reached, and by 2.5 seconds the room is the same seeded room every
session ends up in. But for the first second of every session, the air
he is sitting in is literally the sound of his own throw, still
decaying. He didn't enter a room the app prepared. **The room is what
his seal left behind.** Slam the door of a real stairwell and the
reverb you stand in for the next second is your slam. Monolith should
work like the stairwell, not like the theatre.

No metaphor is printed, no copy names it, nothing on screen says the
room remembers. It is a send level and a feedback coefficient. If he
never consciously notices — and on most mornings he won't — the
sessions will still feel *continuous* in a way the fader version never
quite does, because the ear is very good at knowing when a room and the
sound in it are the same physics, and very good at smelling a crossfade
it wasn't told about.

### Technique spec

- At t=0 the seal renders through the Room tail as filed (combs
  29.7 / 31.1 ms, allpass 5 ms, ≈18 ms pre-delay). One change: the comb
  feedback path gains a **1.8 kHz lowpass and a raised sustain
  coefficient**, tuned so the Throw's tail decays from its natural level
  to **−46 dB (bed level) in ≈900 ms** instead of dying at its filed
  ~300 ms of audibility. The bolt's lowpassed noise (900 Hz) feeds the
  same combs — the mass's decay is part of the arriving air.
- **Catch, not ramp:** the seeded bed loop (brown noise LP 420 Hz + hiss
  BP 5.2 kHz at −58 dB + 78 Hz partial — filed recipe, unchanged) begins
  a level-triggered crossfade when the tail's measured level crosses
  **−46 dB**, completing by **2.5 s post-seal** at the latest. The bed
  never rises *above* the tail it replaces at any instant: at every
  sample, room energy = max(tail, bed fade), never the sum — no arrival
  swell, no moment where the room gets louder after the seal. Monotonic
  decay into held level. The S3 "2.5 s bed ramp" scheduled silence is
  amended, not deleted: the interval, the duration, and its function
  (silence *arriving*) are identical; only the mechanism changes from
  fader to physics.
- **Laid Seal interaction (§1):** the dawn seal's wet send is −26 dB, so
  its tail reaches −46 dB fast — catch window compresses: crossfade may
  begin at **400 ms minimum**, completing on the same 2.5 s bound. The
  dawn room arrives mostly *silent*, which is correct: a laid seal
  leaves a quieter room, and the escapement's first tick at minute one
  lands in air that was never raised. Rule: catch is level-triggered
  with a 400 ms floor and a 2.5 s ceiling, for both voices — one rule,
  no per-voice timing table.
- **Escapement unchanged.** First tick at the first elapsed minute,
  −24 dB, bed ducking −6 dB/300 ms as filed. By then the room is fully
  the seeded room; the tail's individuality lasts one second and is
  gone — which is why this is physics and not a signature. A signature
  repeats; a decay doesn't.
- **Completion unchanged, and strengthened:** the Room Stops still fades
  the bed 300 ms into 400 ms of true silence. It now also means
  something tighter: the room that stops is the room the throw made.
  Subtraction lands harder when the thing subtracted was *yours*.
- **Failure gate (Pure-Seal precedent):** comb sustain is the risk —
  on the phone's speaker, a raised feedback coefficient near 29.7 ms
  comb spacing can colour toward a faint metallic ring (~34 Hz comb
  fundamental and its harmonics, mostly below speaker range, but the
  1.8 kHz loop lowpass region can buzz on a bad unit). On-device test:
  if the sustained tail rings, buzzes, or reads as a *pitch* at any
  point in its decay, the sustain coefficient drops to filed values
  and the fader ramp ships. Never ship a ringing room.

- **Motion twin:** none added. The filed hub-ring births and colon
  breathing already render the room's presence; the tail-carry changes
  no visual. (A visual echo of the decay would be a second rendering of
  the seal — refused.)
- **Haptics:** none. The arriving room has no haptic twin because it
  claims nothing the seal's own haptic didn't already carry; inventing
  one would double-render the event in a third channel.

### ARM cost

**Zero, structurally.** This is post-seal rendering — a feedback
coefficient and a crossfade trigger inside the synth thread. The arm
path, the seal frame, and the 10 s budget never see it. It costs no
gesture, no wait, no setting.

### HOLD / TRUTH check

- **HOLD:** the arriving room asks nothing, offers nothing, cannot be
  interacted with — it is the S1 session bed arriving by a more honest
  route. Suppression, proximity fade (800 ms out / 1.2 s in), and
  audio-focus rules apply to tail and bed identically; a proximity
  cover during the catch kills both under one fade, because by then
  they are one signal path, which is the point.
- **TRUTH:** the tail renders only the seal that actually fired — its
  gains, its voice variant (§1's laid tail is genuinely quieter air,
  because the laid seal genuinely sent less to the room). No state is
  encoded: duration is not in the tail (bolt is fixed-gain; the tail
  carries the bolt's *timbre*, not a number), FLOW's tail is
  indistinguishable in kind from a 25-minute tail. The room does not
  remember *what* he sealed. It is simply, physically, the room his
  sealing made — the same claim a stairwell makes, which is no claim
  at all.

### What it replaces, and why that loses

It replaces **the 2.5 s fader bed ramp** (filed S3 scheduled silence,
third gap). The fader loses because it is a discontinuity the ear can
catch on a quiet morning: seal decays in one acoustic, room rises in
another, and for a moment there are two rooms — the one dying and the
one being provided. Every other element of this engine works to make
Monolith feel like *one object in one room*; the fader is the one place
the filed spec builds the room out of stage machinery. The tail-carry
costs one coefficient and one trigger to remove that seam entirely.

### Strongest case against this proposal

It spends the engine's most precious asset — the seal's brevity — on an
effect whose success condition is that nobody notices it. The Throw is
48 ms of authority precisely because it *ends*; stretching its energy
across a second of session time risks smearing the one gesture whose
clean edge the whole day is built on, and if the catch is mistuned by
even a few dB the first minute of every session carries a faint,
unnameable *wrongness* — worse than the fader's honest seam, because a
seam can be pointed at and a smear cannot. There is also a real
indulgence in it: I am asking the build to tune comb feedback on a
phone speaker, per the failure gate, for a payoff that lives entirely
below conscious hearing. If the merge must cut one thing in this file,
cut this — §1 protects a sleeping household and §2 settles a four-way
war over the centre gesture's birth; this section only makes the room
true. But "only" is doing work in that sentence. The fader is the last
piece of theatre in the engine, and theatre is what we were told to go
beyond.

---

*Filed by Orson — Wild, synths. Room layer only; Ivo's desk untouched.
Corrections adopted in-file: bolt fixed at −6 dB (Moss V-14), bank latch
never lifts (Moss V-5), drag free-run silence adopted from Fen (Moss
V-2), blocking-at-threshold withdrawn (§2). Letters: Fen + Cato,
thread `one-t-zero`.*
