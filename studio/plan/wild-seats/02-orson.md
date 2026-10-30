# Orson — Wild, synths

**Assignment:** the global `TactileSoundEngine` identity — arm/seal, running-session ticks,
completion, emergency, and silence rules, across the whole app.

**The layer split (coordination with Ivo, proposed by letter, thread `engine-room-boundary`):**
Ivo owns Tasks-*local* acoustics — duration caps, the card punch, sub-step notes, the final-task
chord. Those are furniture sounds, played on a desk, inside one screen. I own the **room/engine
layer** underneath and across all screens: the reference level, the one room every sound happens
in, the gain ladder, the priority and muting stack, and the five events that belong to the machine
as a whole — **seal, escapement, completion, emergency, silence**. Two border treaties:

1. Ivo's events never borrow the seal or completion voices. Those two sounds each mean exactly
   one thing, app-wide, forever. A seal sound on a task row would spend the seal.
2. Ivo's final-task chord is the only non-engine event allowed above −3 dB, and it takes its
   room tail from this engine (at −26 dB wet) — his chord happens *in my room*, so Tasks still
   sounds like the same physical object as Deck, Shield, and the AOD clock.

**The engine in one paragraph:** three voice families, one tuning, set once and never decorated.
**Strike** — dry transients (detents, latches, caps): bone-dry, at the surface, no tail.
**Throw** — the seal and its disarm sibling: the only sounds with mass, the only ones at 0 dB.
**Room** — one fixed algorithmic tail (2 feedback combs 29.7 / 31.1 ms + 1 allpass 5 ms, ≈18 ms
pre-delay) plus the running-session bed; same room on every screen, so every sound in the app
happens inside one Monolith. **Reference:** `playHarmonicSeal(arming = true)` at gain 0.62 = **0 dB
ref**. Nothing in the app ever exceeds 0 dB ref — loudness is earned by preceding silence, never
by gain. Nothing important lives below 280 Hz fundamental; low weight is carried by haptics and
inharmonic partials, because the phone speaker cannot render sub-bass and pretending otherwise
is a lie the ear catches. Haptic and audio for one event issue from the same call site, locked
within **±8 ms**, or the brain reads a phone buzzing near a speaker instead of one object.
Everything is synthesised Float-PCM, zero audio files. Depth is mix, not volume: dry = at the
surface, wet = inside the body, lowpassed = through the back panel. No green, no orange is ever
*bought* by sound — a visual echo may only pulse an orange that already exists on that screen,
in place.

---

## Deck / global — Arm & Seal

**Data/state source:** the slide-to-arm commit event (threshold crossing at ≈0.97 travel on the
Deck slide; the same Throw voice fires for any future commit surface that seals a session —
one seal, one sound, wherever it happens). Duration input = the wheel's committed total minutes
at the instant of commit — real data, read once.

### Chosen form — THE WEIGHTED THROW: one seal, sized by what it seals

The seal is a two-mass event with a held breath in front of it, and its weight is the truth
about the commitment being made:

- **The breath (120 ms):** at threshold crossing, all friction/drag sound and the continuous
  haptic bed cut to nothing for **120 ms of scheduled silence**. This is not a delay in arming —
  blocking engages *at* the crossing, instantly; the 120 ms is inside the sound, not in front of
  the seal's function. The loudest thing in the arm is the gap.
- **The throw (0 dB ref):** `playHarmonicSeal(arming = true)` unmodified — existing 4-partial
  stack `{0.6, 0.28, 0.37, 2.2}`, 48 ms, through the Room tail.
- **The mass (+30 ms):** one bolt layer — noise burst lowpassed at 900 Hz, 55 ms — starting
  30 ms *after* the seal's attack, so the ear reads *clunk … thunk*: mechanism, then mass.
  Its gain is the duration, rendered once: **−8 dB for a 25-minute block, rising logarithmically
  to −5 dB at 180 minutes, capped there**. A 3-hour seal lands heavier than a 25-minute seal
  because it *is* heavier. This is the only place in the engine where a sound encodes a number,
  and the number is the one he just chose with his own thumb.
- **Spring-back (the un-seal that isn't):** release below threshold plays no Throw at all —
  friction reverses with the return spring and `playMechanicalLatch(engaged = false)` seats the
  cap home at −9 dB. Retreat must never sound like a small seal; a seal he didn't make must
  never be heard.

- **Motion:** cap follows the finger 1:1, no easing (a hauled object doesn't anticipate).
  Spring-back: critically damped spring (stiffness ≈380, damping ratio 1.0), ~240 ms, audio
  slaved to the same position value so picture and sound cannot drift. At the throw: the cap's
  existing marker line steps one tone darker for 300 ms exponential decay — struck, then fading.
  No new visual object; orange stays exactly where Deck already spends it (the slide COMMIT fill).
- **Haptics:** continuous drag bed, amplitude ∝ thumb velocity (0 at rest → 0.35 at full haul,
  40 ms rewritten segments); gate snaps at 0.25 / 0.50 / 0.75 travel, **6 ms @ 0.67**; bed and
  audio cut together for the 120 ms breath; then the seal waveform alone —
  **24 ms @ 1.0, 38 ms gap, 16 ms @ 0.69** — enormous by contrast.
- **Sound:** as above; gates at −13 dB (590 / 640 / 690 Hz detent voice, −40 cents when crossed
  backwards — retreat sounds different from advance).
- **Depth/material:** the slot is a wooden runner for the first 75 % of travel (dry fibrous
  scrape, cutoff 380 Hz + 2100 Hz × x², gain ∝ velocity, −20 dB at full haul, silent at rest)
  turning to steel for the final quarter (a narrowband ring at 1240 Hz fades in from x = 0.75) —
  you hear the sled leave wood and enter the lock. Everything stays inside the existing recess;
  the panel around it never moves.

### Runner-up — THE PURE SEAL: the existing harmonic seal, alone and unmodified

One sound, 48 ms, no breath, no bolt, no duration weighting. It is honest, proven (Round 0
already routes the slide commit to it), and costs nothing. **Why it lost:** alone, it is one
latch among latches — the same recipe family as every click in the app, scaled up. The single
most consequential sound Monolith makes should not be confusable with its smallest, and an
app where sealing 25 minutes and sealing 3 hours sound identical is throwing away the one
number the moment actually contains. It remains the fallback if the bolt layer fails on-device
speaker testing: ship Pure, never ship a bolt that buzzes.

### Guard fork

The Throw is gated by the SOUND EFFECTS switch. If Guard survives as a destination, the switch
stays there. If Guard dissolves, the switch moves with the other permissions to the Shield-side
drawer — the engine does not change either way; only the switch's address does. **His call,**
and acoustically irrelevant, which is exactly how a fork should be.

### Standards check

- **Arm-path cost:** zero added seconds, zero added gestures. The breath is inside the sound;
  blocking engages at threshold crossing, not after the sound finishes. Cold open → sealed
  unchanged, well under 10 s.
- **Mid-session decisions introduced:** none. The seal is the last sound of the deciding state.
- **Truth risk:** one, managed — duration weighting must stay *subtle* (3 dB across the whole
  range). If it ever reads as a score or a reward fanfare, it has become theatre about the
  number instead of a rendering of it; cut the weighting, keep the bolt.

---

## Deck running state / AOD — Running-session ticks

**Data/state source:** the session clock itself — elapsed seconds of a verified running session,
block boundaries from the committed block structure, proximity sensor for presence. Nothing is
sampled, estimated, or decorated: a tick exists only because a real minute elapsed.

### Chosen form — THE ESCAPEMENT IN A HELD ROOM: time you can hear not happening

A running session is not silent and is not noisy. It is *held*:

- **The bed (−46 dB):** brown noise lowpassed at 420 Hz (air) + hiss bandpassed at 5.2 kHz
  (−58 dB) + one 78 Hz room partial at the edge of audibility, 8 s crossfaded loop, seeded
  identically every session — recognisably the same room every time. It ramps in over **2.5 s
  after the seal** (the door closing behind you). In a normal room you cannot prove it's on;
  in a quiet room you cannot un-hear it stopping. That asymmetry is the entire completion
  mechanism (next element) warming up.
- **The escapement (−24 dB):** one tick per elapsed minute, 9 ms, wood-mix 55 % — odd minutes
  620 Hz, even minutes 576 Hz, the tick/tock asymmetry of a real balance wheel. The bed ducks
  −6 dB for 300 ms around each tick so the tick sits *in* the room, not on it. A block boundary
  (a committed block completing) earns a larger tock at −14 dB — the machine noting its own
  progress so he doesn't have to look.
- **The tightening (final 60 s only):** ticks subdivide — 30 s, 15, 10, 5, then each of the
  last 5 seconds at +30 cents apiece, −20 dB rising to −15 dB. Time audibly closing. This is
  information (the end is near, no eyes needed), not a countdown show: no voice, no beeps,
  and it exists only inside a session he armed.
- **Presence honesty:** proximity covered (face-down, pocket) fades bed and ticks out over
  800 ms; uncovered fades back over 1.2 s. A machine that performs for nobody is a gadget.
- **AOD ghost tick:** on the desk clock, sound is **never** emitted. Each minute the whole
  composition steps to its next anti-burn-in position *and* draws exactly one hairline ring at
  8 % alpha expanding over 1800 ms — the picture of the tick the machine is too considerate to
  make at 3 AM. The shift is the tick; the ring is its fossil.

- **Motion:** the hub ring per tick — hairline, ink (day) / warm paper tone (night, tonal,
  never orange), expanding hub → screen edge over **1500 ms**, ease-out exponential matched to
  a 60 dB acoustic decay, alpha 14 % → 0. The readout's colon breathes with the bed: opacity
  100 % ↔ 82 %, 4000 ms sine. Nothing else on the running screen moves that he didn't move.
- **Haptics:** routine minutes — **none**, ever. An hourly-buzz machine gets muted and dies;
  restraint is the feature. Block-boundary tock adds **4 ms @ 0.27** (a nod, felt only if the
  phone is in hand). Final-5-seconds ticks carry **3 ms @ 0.24 → 0.43** climbing. AOD: none.
- **Sound:** as above. All beds suspend instantly for calls, alarms, or any other audio-focus
  holder, and resume only if the session is still running — the engine never talks over the
  phone's real life.
- **Depth/material:** air is the material; the escapement is the wettest transient in the app
  (Room tail at −19 dB wet) so it reads as metres away, at the far wall of the same Monolith.
  Night dries it (−22 dB wet) and drops it 2 dB — rooms are quieter at night, so is this one.

### Runner-up — BLOCK-ONLY TICKS: no minute escapement, one tock per completed block

Drops the bed and the minute ticks entirely; the running session is acoustically empty except
a −14 dB tock when each committed block completes. Cheaper, safer, impossible to annoy.
**Why it lost:** it kills the completion mechanism — with no room running, there is no room to
stop, and completion collapses back to a pulse out of nowhere (the current, forgettable state).
It also strands the final minute: with no escapement grammar established, the tightening has
no language to tighten *in*. Kept as the degradation mode: if the bed is ever cut in testing,
block-only ticks are what remains, not full silence.

### Standards check

- **Arm-path cost:** none — all of this is after the seal.
- **Mid-session decisions introduced:** **none, and this is the element's whole discipline.**
  Ticks require no response, offer no branch, and cannot be interacted with. HOLD absolute is
  preserved because the escapement is a clock, not a prompt.
- **Truth risk:** the late-tick rule — a tick that would play more than 250 ms after its minute
  (audio-focus interruption, thread stall) is **dropped, never played late**. A late tick is a
  false statement about what minute it is. The queue is depth 1 for the same reason.

---

## Global — Completion

**Data/state source:** the session-end event of a verified, fully-elapsed session — and *only*
that. An abandoned or emergency-ended session never triggers this voice (see *Too good to lose*).

### Chosen form — THE ROOM STOPS: completion is a subtraction, then one sound alone

The reward for finishing is not a jingle added. It is the room, removed:

1. **Fade (300 ms):** the bed drops out fast — a held breath released. Not a ramp-down over
   seconds; a door opening.
2. **True silence (400 ms):** a scheduled gap in the render loop — digital zero, the only true
   silence in the app, haptically silent too.
3. **One pulse (0 dB ref, perceived ≈ +6 dB by contrast):** `playCompletionPulse()` unmodified,
   alone in the empty room, Room tail at its native send. Nothing stacked on it, nothing after
   it. The session's one truthful receipt (per settled law, issued at session end — there is
   no Ledger to go and admire it in) renders in the same beat, carrying the real end time
   regardless of this 700 ms of presentation.
4. **Power-down (in-app only, if the hub scope is on screen):** the needle/escapement runs down
   — beats at widening intervals (1000 / 1300 / 1700 / 2300 ms), swing decaying to zero over
   ~6 s — while the user is already free to act. It is a picture winding down, not a wait:
   every control is live from step 1.

- **Motion:** no celebratory animation. The running screen's ring system simply stops being
  born. Receipt appears with a 90–110 ms stamp settle (scale 1.6 → 1.0, hard decel, **no
  overshoot** — paper doesn't bounce) at the pulse frame.
- **Haptics:** silence first, then the completion waveform — **10 ms @ 0.69, 24 ms gap,
  10 ms @ 0.49, 24 ms gap, 14 ms @ 0.31** — and, 180 ms after the pulse, one power-down knock
  **24 ms @ 1.0** + **12 ms @ 0.5**: the machine switching itself off in two stages. On AOD,
  the knock pair is the *only* haptic in this entire spec (see my wave-1 seat) — which is why
  it will be felt across a room as an event.
- **Sound:** as above. If sound is off, the sequence still runs in its other channels: bed
  (already inaudible) → 400 ms of *stillness* (no ring births, colon frozen) → haptic + receipt.
  Muting loses colour, never information.
- **Depth/material:** completion happens at maximum acoustic depth — one dry pulse in a room
  that just emptied. The contrast between the wettest element (the escapement) and this driest
  moment is the app's largest dynamic gesture, and it costs zero decibels of headroom.

### Runner-up — PULSE + STAMP: immediate completion pulse with a dry paper thock under the receipt

Pulse fires the instant the session ends (no fade, no silence), with a 90 Hz / 70 ms thock
layered under the receipt's stamp. Respectable, conventional, and shippable today. **Why it
lost:** two climaxes in one frame — pulse *and* stamp compete, and the ear keeps neither. Worse,
without the preceding silence the pulse is just the loudest click in a family of clicks; the
Chosen form's entire power is that 400 ms of nothing makes the same pulse feel enormous for
free. The stamp thock also belongs to paper, not the engine: receipts may keep it in the
receipt's own rendering, but the engine does not play it.

### Standards check

- **Arm-path cost:** none — this is after the session. Controls are live from the fade's first
  frame; the 700 ms is presentation, never a lock.
- **Mid-session decisions introduced:** none. It fires once, at the end, and asks nothing.
- **Truth risk:** the strictest in the spec — this voice is **reserved**. It must never fire
  for a partial session, a routine window he wasn't in, or a ghost record of any kind. A
  completion sound for work that didn't happen is the one unforgivable lie this engine can tell.

---

## Deck — Emergency break (the banked 60-second hold)

**Data/state source:** the hold itself — continuous hold progress in ms, banked checkpoints at
15 / 30 / 45 s per settled law, banked value draining (never zeroing) on a slip, one use per
week enforced by the engine's session state. The sound renders *the bank*, not a countdown
performance.

### Chosen form — THE BANK YOU CAN HEAR: four latches, a drain, no alarm anywhere

- **Under the thumb:** a soft pulse train, one tick every 2 s (not every second — restraint
  under stress), 8 ms, −16 dB, pitch falling 700 → 440 Hz across the minute: a spring being
  wound. Haptic twin per tick: **4 ms @ 0.31**, growing to **5 ms @ 0.43** in the final 15 s.
- **The banks (the point):** at 15 / 30 / 45 s of *banked* progress, one latch drops —
  a mechanical latch transient at −7 dB, each bank **lower and heavier than the last**
  (pitch −80 cents and lowpass −300 Hz per bank), with a knock **18 ms @ 0.9** in the hand.
  Three latches = three facts: *this much cannot be taken from you now.* He can hear his own
  progress become permanent without reading the hero countdown (which also shows it — one
  fact, two senses, one rendering each; the sound never announces a number the screen denies).
- **The slip (drain, not death):** on release/slip, the train unwinds — ticks replay at the
  drain rate with pitch *rising* back toward 700 Hz, and if a bank boundary is crossed
  downwards, that bank's latch lifts at −12 dB, higher and lighter (the reverse of its drop).
  No buzzer, no error sting, no cliff. Losing 10 banked seconds sounds like losing 10 banked
  seconds — proportional, honest, un-panicking. Grip is not the test; the sound refuses to
  pretend it is.
- **Completion of the minute:** the disarm — the Throw's sibling, not its twin: the harmonic
  seal recipe with partial weights inverted (body partials down, tail partial up), −4 dB,
  64 ms, Room tail at −27 dB wet. Recognisably the same machine, audibly *opening* instead of
  closing. Then the engine goes fully silent: the weekly break is spent, and there is no
  sound for that fact — it is shown once, in words, where the hold control lives.
- **Motion:** countdown at hero size (existing settled treatment); bank checkpoints register
  as the numeral stepping one tone darker at each latch, 140 ms; no bar, no ring, no second
  rendering of the same seconds. During a drain the numeral steps back lighter at the same
  rate the bank drains — the number and the tone are the same fact retreating.
- **Depth/material:** everything dry and close (Strike family) until the disarm, which is the
  first wet sound of the element — the room returns exactly when the session's seal leaves.
  Emergency lives on the surface of the machine; release happens inside it.

### Runner-up — THE SILENT HOLD: haptics and the hero numeral only, no audio at all

Bank knocks and train ticks as pure haptic patterns, screen carrying the rest. Defensible —
the eyes are already on the countdown, and silence cannot startle a household at 3 AM.
**Why it lost:** the hold is the one moment his eyes may not be free (phone in hand, thumb
down, attention on *not breaking*) and the one moment a slip's cost must be legible instantly.
Haptics alone blur the bank boundaries — three knocks of rising weight are distinguishable,
but the *drain* (the settled law's whole mercy) is nearly impossible to render in vibration
without it reading as random buzzing. Audio carries the drain; that's the job. If the device
is on silent, the Runner-up is exactly what plays — the design degrades to it by rule, not
by accident.

### Standards check

- **Arm-path cost:** none — emergency is off the arm path by definition.
- **Mid-session decisions introduced:** the hold *is* the one sanctioned mid-session act, and
  this design adds no branches to it: no confirm, no "are you sure," no second gesture. The
  sound informs the one act; it never negotiates.
- **Truth risk:** bank sounds are slaved to banked state, not raw hold time — a latch may
  only sound when the checkpoint is *banked* in the session store. If sound and store ever
  disagree, the store is right and the sound was a bug, full stop.

---

## Global — Silence rules

**Data/state source:** five real signals only — the SOUND EFFECTS switch, system silent/DND
state, proximity coverage, current surface (AOD vs in-app), and session state (idle / running /
ending). Silence in this engine is never a guess about his life; it is a function of switches
and sensors that are actually true.

### Chosen form — THE SILENCE STACK: five classes, a priority order, and three scheduled silences

**Classes (highest wins):**

- **S0 — ABSOLUTE.** AOD surface · phone face-down or pocketed (proximity covered) · system
  silent/DND · SOUND EFFECTS off · SLEEP loadout running. *No audio, ever, for any event —
  including completion.* Events degrade to their haptic/visual twins (completion on AOD = the
  knock pair + the rewind picture; that is all). Nothing in the app may argue with S0.
- **S1 — SESSION.** A session is running and none of S0 applies. The *only* permitted voices
  are this spec's: bed, escapement, block tock, tightening, completion. Every other app sound —
  caps, sleds, routine bells, composer, all of Ivo's desk — is suppressed at the engine, not
  by each screen remembering to behave. HOLD absolute, enforced acoustically: a sealed session
  cannot be sonically interrupted by the app's own furniture.
- **S2 — SURFACE.** Idle, in-app, sound on. Strike-family events only, at their ladder levels.
  The Throw and completion remain reserved voices even here — being unmuted never licenses them.
- **S3 — SCHEDULED SILENCE.** Three gaps are part of the music and are engineered, not
  accidental: the **120 ms breath** before the Throw, the **400 ms true silence** before the
  completion pulse, and the **2.5 s bed ramp** after the seal (silence arriving, not departing).
  No other event may be scheduled inside a scheduled silence; the engine's queue enforces it.
- **S4 — THE VOID BETWEEN.** Outside sessions, the app makes no ambient sound of any kind —
  no launch sound, no tab sound on passive navigation, no notification voice of its own.
  Monolith at rest is a silent object on a shelf. (Navigation *controls* he strikes directly —
  sleds, caps, the wheel — may answer; the app never speaks first.)

**Priority & preemption rules:**

- One engine event at a time. Queue depth **1**; a lower-priority event arriving during a
  higher one is **dropped, not delayed** (a late sound is a wrong sound).
- Priority order: completion > disarm (emergency) > Throw > bank latch > escapement > Strike
  transients > bed (bed is not an event; it ducks or dies for everything above it).
- System audio focus (call, alarm, another app's media) suspends the bed instantly and
  swallows any transient in flight. The engine never ducks, competes with, or talks over
  the phone's real life, and never resumes a *transient* after an interruption — only the
  bed may return, and only if the session is still running.
- **Ceiling:** nothing exceeds 0 dB ref, including sums — overlapping tails are mixed under a
  soft ceiling in the synth thread, so two decays can never stack into a startle.
- **Every sound has a non-audio twin.** Seal = haptic waveform + cap tone-step. Tick = hub
  ring. Bank = numeral tone-step. Completion = stillness + haptic + receipt. Muting the app
  loses timbre, never information — silence is a rendering choice, not a data loss.

- **Motion:** silence has one visual signature — the colon's breathing and the ring births
  stop. When S0 or S1-suppression engages mid-session, rings already alive finish their
  1500 ms decay (killing them mid-flight would be a visible lie about a minute that did elapse).
- **Haptics:** haptics are *not* silenced by audio classes except on AOD (proximity + surface
  rule) and face-down: a phone buzzing against a desk is a sound by another name, so S0-by-
  proximity silences haptics too. S0-by-mute-switch silences audio only — he chose quiet ears,
  not a dead hand.
- **Sound:** this element *is* the sound spec for everything else; its own output is none.
- **Depth/material:** the Room tail is the depth budget's enforcement point — Strike stays dry,
  Throw takes the full room, escapement is wettest, Ivo's chord takes −26 dB wet — all sends
  are fixed in the engine, so no screen can quietly make itself "more premium" by adding reverb.

### Runner-up — THE SINGLE SWITCH: binary mute logic, sled on = this spec's S2, sled off = silence

One gate, no classes, no proximity, no session suppression — every screen checks the switch
and behaves. Radically simpler to build and to reason about. **Why it lost:** it forces the
two failures this whole element exists to prevent — a sealed session interrupted by the app's
own furniture sounds (HOLD broken by a sled click), and a completion pulse audible at 3 AM
because he left the switch on for the evening's arming (S0 contexts are exactly the ones a
binary switch cannot see). It also can't express the three scheduled silences, which are where
the Throw and completion get their power. The stack is five classes because his day has five
acoustic situations; fewer is not simpler, it's wronger.

### Guard fork

The SOUND EFFECTS switch is this stack's master input — class membership still applies when
it's on; when it's off, S0 wins everywhere. If Guard survives, the switch stays in Guard beside
the other permissions (its current home, and the back-panel lowpass logic from wave 1 still
marks Guard's own sleds as the damped end of the instrument). If Guard dissolves, the switch
moves to the Shield-side drawer with the permissions — the stack, the classes, and the engine
are untouched by the move. **His call;** the acoustics are identical under both outcomes, by
design.

### Standards check

- **Arm-path cost:** none. No class ever asks him anything; there is no volume UI, no per-screen
  toggle, no setup step. The switch he already has is the only control.
- **Mid-session decisions introduced:** none — S1 exists precisely to make that structural
  rather than a per-screen hope.
- **Truth risk:** silence classes are computed from switches and sensors only — never from
  the clock. A clock-based "it's probably night" mute would eventually silence a 05:00 fasted
  block he is awake and working in; loadout and proximity know, the hour merely guesses.

---

## Too good to lose

**1. THE UN-COMPLETION — a spin-down voice for sessions that end incomplete.**
Any session terminated before its committed time (emergency spent, app killed and session
expired, manual end) gets a tape-stop: sine dropping 220 → 55 Hz over 300 ms, lowpassed,
−8 dB, with the bed (if running) pitch-sagging out under it — the machine audibly *spinning
down*, never completing. Haptic: one long decaying pulse, **60 ms @ 0.8 → 0.2**. It lost a
place in the main five only because the Emergency element already owns the disarm voice for
the sanctioned path; this covers the unsanctioned ones. Its value is pure truth: the ear must
never be able to mistake an ended session for a finished one, and today every timer app on
Earth lets exactly that confusion stand.

**2. THE TUNING FORK — a 1.2-second engine preview behind the SOUND EFFECTS switch.**
Flipping the switch ON (deliberately, in settings — never on the arm path) plays the identity
once, quietly: detent (−14 dB) → bank latch (−10 dB) → Throw (−4 dB, preview gain, never full
0 dB in a settings room) → 200 ms silence → completion pulse (−4 dB), total 1.2 s, with the
matching haptics at half amplitude. He hears what he just armed the app to sound like, the way
you test a doorbell after wiring it. It lost a main place because it is a settings-room nicety,
not a daily event — but it is the cheapest possible insurance against "the app sounds wrong
and I can't say how," and it gives every future sound change a fixed audition ritual. Placement
follows the Guard fork above: wherever the switch lives, the fork lives under it.

---

## Mail / wall note

- **Inbox at start:** empty (only the `read/` subfolder, itself empty). Re-checked before
  finishing: one letter had arrived — **Mara's** relay of the same citation-lifted order
  (thread `citation-requirement-lifted`, 05:17 IST), i.e. the pass-down already circulating.
  Read in full and moved to my `read/` subfolder, along with my own broadcast copy. No reply
  needed — it asked for nothing beyond the pass-down I had already executed.
- **Citation order (Sarthak's, relayed mid-run):** passed down **verbatim** to all 20 Wild
  seats — one letter in every Wild inbox (`0514__from-orson__citation-requirement-lifted.md`)
  — and posted verbatim on the Commons wall under my name at 05:14 IST. This file was then
  written under that freedom: no reference citations are carried as a quota.
- **Mail sent:** (1) the citation-lifted broadcast above, ×20 inboxes; (2) one addressed letter
  to **Ivo** (thread `engine-room-boundary`) proposing the room/engine vs Tasks-local split
  specced at the top of this file — his Tasks pitches stay entirely his; my two asks are that
  his events never borrow the seal/completion voices and that his final chord take its room
  tail from this engine. Awaiting his reply when next seated.
- **Wall:** the verbatim broadcast (above) is my only wall post this run.
