# FEEL — THE DOOR'S SOUND & HAPTIC SCORE (Issue #11)

Seat: TON, Feel Specialist 3, Tor's cell · For: Tor (lead)
Slice: **SOUND + HAPTICS only** — FIX-7c and every beat around it.
Ground read, whole: `studio/plan/AUDIT-held-door.md` (FIX-7 in
full, Proposals Disposed last bullet — Schwelle's knock at
HOLD 00 and Schliff's per-second detent, filed raw to this
bench); `docs/tor-plan.md` item 5; `docs/designer-skeleton.md`
(§2 arrival row, §5 the peek zone, §8 state matrix);
`docs/feel-5-druck.md` (§1 arrival, D-F11, D-F17);
`docs/innovator-proposals.md` (P-2c, P-3a);
`service/TactileSoundEngine.kt` whole; the door's live call
sites in `ui/MonolithViewModel.kt` and
`ui/components/AirlockAndDisarmOverlays.kt` as shipped in this
clone. **Nobody compiled.** Every number below is either quoted
from the engine, quoted from the audit, or derived in place —
and where I set a new number, the derivation is printed beside
it so Tor can strike the reasoning, not just the result.

---

## 0. Headline

FIX-7c fixes the arrival and stops. It names one beat of a
visit that has six. Left as the audit leaves it, the fixed
door would sound like this: a correct knock at the door, then
five seconds of unruled time, then a chip that arms in
silence, then — on the act the door exists to encourage — the
**session seal's own voice at full weight**, borrowed by
RETURN (`playHarmonicSeal(arming = true)`, VM `transmute­
ImpulseAndReturn`), while taking the peek sounds like every
light switch in the app (`playMechanicalLatch(true)`, the
same call that announces arrivals today). One beat re-voiced,
the rest still wearing other rooms' clothes.

This file is the whole score. Its laws, stated once:

> **L-1 · One door, one pitch.** Every sound this door makes
> sits on the door pitch FIX-7c sets — **300 Hz** — except the
> exit to Disarm, which steps down to 240 Hz for reasons
> specced in §5.6. Identity between beats is carried by
> duration, gain, chord, and haptic shape — never by a new
> pitch the man must learn at 1 AM.
>
> **L-2 · The door is never the loudest thing in the app.**
> The door's ceiling is FIX-7c's own gain: **0.60**. The
> seal's legacy recipe stands at 0.62, the completion and
> tear pulses at 0.68, and the Seal Throw — a 1,000 ms
> rendered buffer with a room tail — outweighs everything on
> total energy regardless of peak. No door beat meets or
> exceeds any of them. No door haptic spends amplitude **255**:
> full force stays the seal's alone (the Throw's own twin
> uses it; the door's strongest segment is 215).
>
> **L-3 · Sound never grades the man.** The arrival beat is
> identical for every path (APP_TIER / SHIELD_APP / SNIPER /
> SITE / UNINSTALL_GUARD / UNKNOWN), every tier, every count
> of tonight's returns, and — if Innovator P-5 ships — for a
> homecoming arrival exactly as for a fresh reach. The taper
> may lengthen his wall; it may not lengthen, deepen, or
> multiply his knock. A sound that varies with his record is
> a score wearing a voice.
>
> **L-4 · Silence is a specced beat, not an absence of spec.**
> Every SILENCE ruling below names both channels (audio **and**
> haptic), its span, and its reason. The countdown's silence
> is the longest deliberate sound this door makes.
>
> **L-5 · Sounds live where they live today: in the
> ViewModel, at the state change.** Every door sound in the
> shipped tree fires from a VM function (`triggerAirlock­
> Intercept`, `start2MinutePeek`, `transmuteImpulseAndReturn`);
> the overlay composable owns no engine call and must gain
> none. A sound fired from a recomposition can double-fire;
> a sound fired at a state transition cannot. This law decides
> two implementation points below (§5.3, §5.6).

---

## 1. The instrument — the engine as it stands

All door transients run through the engine's one legacy lane:
`enqueueTransient(fundamentalHz, durationMs, gain, chordRatio)`
→ a serial FIFO queue → a float-PCM synth with the 4-partial
weights **{0.6, 0.28, 0.37, 2.2}**, body/tail dual decay
(exp(−0.0114) / exp(−0.0313) per sample), clamp ±0.95, and a
transient audio-focus request (`AUDIOFOCUS_GAIN_TRANSIENT_
MAY_DUCK`) per command. Pitch 2 is the fundamental × 2 ×
`chordRatio`; partials 3 and 4 are fixed at ×3 and ×4.7. The
door score uses **this lane only**: the Seal Throw's dedicated
lane (pre-rendered buffer, queue purge, lane epoch) is the
Throw's by law and no door sound may route through it,
prime it, or purge for it.

The existing voices, exhaustively — the vocabulary this score
is allowed to speak, with the numbers the builder will check
against (`TactileSoundEngine.kt`):

| Voice | Call site in engine | Pitch | Dur | Gain | Chord | Haptic waveform (timings ms / amplitudes) |
|---|---|---|---|---|---|---|
| Rotary detent | `playRotaryDetent` :629 | param (280–1480, def 680) | 6 ms | 0.38 | 1.0 | [0,7,5] / [0,180,70] |
| Firm click | `playFirmClick` :644 | 620 Hz | 12 ms | 0.50 | 1.1 | [0,13,20,8] / [0,225,0,135] |
| Seat knock | `playSeatKnock` :665 | param (160–420, def 300) | param+6 (10–44) | 0.22+0.30·a | 0.9 | [0,s,s/2] / [0,255a,89a] |
| Latch, engaging | `playMechanicalLatch(true)` :683 | 760 Hz | 16 ms | 0.54 | 1.2 | [0,11,26,9] / [0,215,0,150] |
| Latch, releasing | `playMechanicalLatch(false)` :683 | 520 Hz | 16 ms | 0.54 | 0.9 | [0,9,22,10] / [0,165,0,195] |
| Seal, arming (legacy) | `playHarmonicSeal(true)` :707 | 360 Hz | 48 ms | **0.62** | 1.5 | [0,24,38,16] / [0,255,110,175] |
| Seal, disarming (legacy) | `playHarmonicSeal(false)` :707 | 480 Hz | 48 ms | **0.62** | 1.5 | [0,18,30,12] / [0,200,90,140] |
| Seal Throw | `playSealThrow` :767 | 360 Hz body + bolt + room | **1,000 ms** buffer | 0.62 body | 1.5 | [0,24,38,16] / [0,255,**0**,175] |
| Tear pulse | `playTearPulse` :817 | 240 Hz | 40 ms | **0.68** | 1.21 | [0,8,14,8] / [0,160,0,110] |
| Completion pulse | `playCompletionPulse` :833 | 185 Hz | 64 ms | **0.68** | 1.33 | [0,10,24,10,24,14] / [0,175,0,125,0,80] |

The chord grammar already in the tree — this score obeys it
rather than inventing a second one:

- **1.5 = binding.** The seal binding a session (legacy recipe
  and Throw body alike). FIX-7c puts the door's arrival in
  this family, correctly: the door is the seal, encountered
  from the other side.
- **1.2 / 0.9 = the latch pair.** Engaging strikes sharp
  (1.2); releasing lands heavier on the seat (0.9) — and the
  seat knock, the app's other "coming to rest" voice, is 0.9
  too. **0.9 is the engine's release interval.**
- **1.0 = travel.** Detents: motion without commitment.
- **1.33 / 1.21 = one-offs with owners** (completion, tear).
  The door borrows neither.

Two gate facts bind every beat below:

- **Sound fact OFF** (`SoundEffectsSetting`): `enqueue­
  Transient` drops the command; haptics are untouched. The
  Throw's own comment states the law — *"muting loses timbre,
  never the event"* (S0). The door inherits it whole: with
  sound OFF, every beat below is **haptic-only, at the specced
  waveform**; with haptics disabled (`hapticsEnabled`), audio
  stands alone. Neither gate changes the countdown's silence:
  that silence is the score, not an outage.
- **Each transient pays an audio-focus transaction.** A beat
  is not free even when quiet. This is a counted cost in §5.2.

---

## 2. The door as shipped — the before-score

What a visit sounds like today, from the clone (all call
sites in `MonolithViewModel.kt` unless noted):

| Beat | Today | Voice spent |
|---|---|---|
| Arrival | `triggerAirlockIntercept` :2321 → `playMechanicalLatch(true)` | 760 Hz / 16 ms / 0.54 / 1.2 + latch-engaged haptic |
| Countdown 5→0 | overlay `LaunchedEffect` delay loop | **silent** (unruled — silence by accident) |
| Zero / chip arms | conditional composition at `countdownSecs == 0` | **silent** |
| Withheld line prints | same conditional, 5 s late (FIX-6 cures) | **silent** |
| RETURN press | `transmuteImpulseAndReturn` → `playHarmonicSeal(true)` | 360 Hz / 48 ms / **0.62** / 1.5 + seal-arming haptic — **the visit's heaviest sound, on the goodbye** (the audit's FIX-7c charge, verified in this clone) |
| PEEK press (grant) | `start2MinutePeek` :2398 → `playMechanicalLatch(true)` | identical to today's arrival — taking the lease sounds like flipping any switch in the app |
| PEEK press (refused) | re-derivation fails, function returns | silent (acquitted — arithmetic does not apologise; §5.5 keeps this) |
| Reason chip tap | `selectDistractionReason` → `playRotaryDetent(580)` | dies with FIX-3b's dead wires — §6 |
| Particle tap | `incrementAirlockParticlePulse` → detent, pitch rising per tap | dies with FIX-3b — §6 |
| Exit to Disarm | **route does not exist** (FIX-8 builds it). Nearest kin: `requestDisarmChallenge` from the Deck → `playMechanicalLatch(false)` 520/16/0.54/0.9, or `playHarmonicSeal(false)` on the stopwatch-past-floor shortcut | unruled for this face |

Three defects, in severity order — the first is the audit's,
the other two are what the audit's single-beat fix would have
left standing:

1. **The inversion (FIX-7c's charge).** Arrival 0.54 for 16 ms;
   RETURN 0.62 for 48 ms. The door whispers its presence and
   shouts its dismissal. Energy ratio is worse than the gains
   suggest: the seal recipe's 48 ms is three ticks' worth of
   the latch's 16.
2. **RETURN spends the seal's voice.** `playHarmonicSeal` is
   not a generic "heavy good" sound — in this app it *means*
   a session sealing or unsealing. Spending it on a door exit
   teaches the ear that the seal clunk is cheap, and the day
   the real seal sounds, it has been pre-spent a hundred
   times at doors. (The same theft, smaller: `end2Minute­
   PeekEarly` also spends `playHarmonicSeal(true)` — that is
   the pill face's verb, outside my slice; flagged in §7 for
   Tor to route, not ruled here.)
3. **The peek has no voice of its own.** Grant = the generic
   engaging latch, the identical call used by toggles and
   settings across the app. The one act on this face that
   *spends* something sounds like the acts that spend nothing.

---

## 3. The night law — why this score is quiet on purpose

This door's canonical visit is 01:0x, in a sealed session, in
a house where other people are asleep. Three physical facts
govern, and every ruling below cites at least one:

- **A phone speaker at 1 AM is not a private channel.** In a
  silent room, a 44 ms transient at gain 0.60 is plainly
  audible through a door; a *repeated* transient becomes the
  room's clock. The score therefore caps the visit at **three
  audible events** (§4) and spends its longest span on
  silence.
- **Haptics are the near channel; audio is the far channel.**
  A waveform in a held hand is felt by one man. The same
  waveform with the phone lying on a hard nightstand is a
  percussion instrument. Haptic amplitudes below are set for
  the *held* case and kept short and low enough that the
  nightstand case stays a tap, never a buzz-saw: no door
  segment exceeds 215, no door waveform totals more than
  46 ms of vibration, and the countdown carries **no haptic
  at all** — a per-second buzz on furniture is the single
  worst sound this door could make, worse than any speaker
  tick at the same gain.
- **Pitch carries through walls in proportion to its height;
  weight does not require loudness.** The door pitch (300 Hz)
  sits a perfect fifth below the seal's body (360 Hz — the
  audit's "door pitch" is a *lowering*, and FIX-7c's
  chordRatio 1.5 on 300 Hz puts partial 2 at 900 Hz, exactly
  where the seal's recipe also concentrates its second voice:
  the two sounds are family, distinguishable by pitch centre
  and length, never by one being an alarm version of the
  other).

No day/night gain variant is specced. The engine has no
ambient gain law anywhere — the Throw's ethos is that two
seals cannot differ — and this score extends it: **two
arrivals cannot differ by the clock.** The score is written
quiet enough for 1 AM and stands unchanged at 1 PM.

---

## 4. The score at a glance

One visit, every beat, in order. Detail and derivation in §5.

| # | Beat | Audio | Haptic | § |
|---|---|---|---|---|
| 1 | **Arrival** (all paths) | **300 Hz · 44 ms · 0.60 · chord 1.5** (FIX-7c verbatim) | latch-engaging, kept verbatim | 5.1 |
| 2 | **Countdown seconds** (wall 5–10, incl. last second) | **SILENCE** | **SILENCE** | 5.2 |
| 3 | **Zero — offer arms** (and held→offer resolution) | **300 Hz · 44 ms · 0.30 · chord 1.5** | detent-weight single tick | 5.3 |
| 3′ | Zero on a held door where nothing arms | **SILENCE** | **SILENCE** | 5.3 |
| 4 | **RETURN press** | **300 Hz · 28 ms · 0.48 · chord 0.9** | latch-releasing, kept verbatim | 5.4 |
| 5 | **PEEK taken** (grant honoured) | **300 Hz · 32 ms · 0.50 · chord 1.5** | latch-engaging, kept verbatim | 5.5 |
| 5′ | PEEK refused at grant | **SILENCE** | **SILENCE** | 5.5 |
| 6 | **Exit to Disarm** (confirmed handoff only) | **240 Hz · 40 ms · 0.42 · chord 0.9** | latch-releasing, kept verbatim | 5.6 |
| 6′ | KEEP FOCUSING — peel back to this door | **SILENCE** (no second arrival) | **SILENCE** | 5.6 |

**Visit budget:** at most **3 audible events** (1, then 3,
then exactly one of 4 / 5 / 6), totalling at most **120 ms of
sound** (44 + 44 + 32) across a visit that lasts at minimum
the wall's 5 seconds. A visit where he simply returns inside
two seconds — Blank's first man — is **two events, 88 ms**,
and the second is quieter than the first. The door speaks
less than any other face in the app except the AOD clock,
which is silent by design. That is the correct ranking for
the face that interrupts.

---

## 5. Beat by beat

### 5.1 Arrival — FIX-7c, adopted verbatim, with its wiring specced

**Spec (the audit's numbers, unchanged):**

```
playDoorArrival()
  audio:  enqueueTransient(fundamentalHz = 300f, durationMs = 44,
                           gain = 0.60f, chordRatio = 1.5f)
  haptic: triggerWaveform(timings = [0, 11, 26, 9],
                          amplitudes = [0, 215, 0, 150],
                          fallbackMs = HAPTIC_SWITCH_FLIP_4MS)
          — the engaging latch's waveform, verbatim ("latch
            haptics kept", FIX-7c)
```

- **Do not retune `playMechanicalLatch`.** Its 760 Hz voice
  belongs to every switch and toggle in the app; the door's
  arrival is a *new, door-local* engine method that keeps the
  latch's hands and replaces its voice. One line changed at
  the call site (VM :2321: `playMechanicalLatch(true)` →
  `playDoorArrival()`), zero change to any other face's sound.
- **Timing.** The call stays where it sits: first statement of
  `triggerAirlockIntercept`, i.e. at state emission. Against
  FIX-7b's entry (fadeIn specA, 180 ms), the 44 ms knock
  begins as the face begins to fade and is finished well
  inside the fade — the door is *heard* shutting an instant
  before it is fully *seen*, which is the order real doors
  present in. Druck §1's verdict stands and this wiring is
  what keeps it true.
- **Parity (L-3).** One call site serves all five firing
  doors today; the score requires it stay that way. No
  per-path variant, no taper variant, no homecoming variant
  (P-5 changes a *print*; it is forbidden, by this score,
  from changing the knock — a gentler knock for the man the
  lease expired on would be the door editorialising, and the
  machinery cannot prove gentleness anyway).
- **Repeat arrivals.** A genuinely new intercept (FIX-4's
  onNewIntent route; P-4c's nonce) is a new arrival and pays
  the full beat. A re-fire that lands on the *same* standing
  visit (same nonce) must not re-sound: the knock belongs to
  the arrival, not to the intent plumbing. Builder keys the
  call to the nonce change, exactly as the overlay's effects
  are keyed.
- **Why 0.60 is right, once, for the record:** it is 0.28 dB
  under the seal's 0.62 — near enough to read as the same
  machine, far enough to never tie it — at 44 ms against the
  seal's 48, on a pitch a fifth below it. The arrival is the
  door's loudest beat because it is the door's only
  *involuntary* one: every later sound answers his own hand,
  and a sound answering a hand may be quieter, because the
  hand already knows.

### 5.2 The countdown seconds — SILENCE, ruled, with the two filed fragments adjudicated

**Ruling: SILENCE — audio and haptic — for every second of
the wall, at every wall length the taper can produce (5–10),
including the final second, and including the held branch's
cooldown re-derivation ticks, whose prints update per
recomposition (FIX-5) in the same silence.**

This bench was handed two raw fragments. They are ruled
separately, because they are different proposals wearing
similar clothes:

- **Schliff's per-second detent — REJECTED.** Four grounds,
  any one sufficient:
  1. *Night.* Five to ten speaker ticks (or worse, five to
     ten haptic pulses against furniture) at 1 AM, each
     paying a transient audio-focus transaction, to announce
     a number the gate line already prints, derived, in
     tabular figures. The detent adds volume, not
     information.
  2. *Attention.* Druck D-F7: movement is argument, and the
     only argument moving is the peek's. Sound is movement
     for the ear. A per-second detent *sonifies the approach
     display* — it trains the man to listen to the reward
     ripening, second by second, which is the waiting-room
     failure with a soundtrack. Silence is what lets his
     attention leave the gate and rest on RETURN, which sits
     live and unadvertised beneath it.
  3. *The taper interaction.* Wall length grows with his
     returns (5 + n, cap 10). Per-second ticks scale the
     sound with the count — the door would literally buzz
     more at the man who comes back most. That is the memory
     line's scolding risk (Druck D-F10) rendered in audio,
     where it cannot be sentence-cased or explained. Under
     silence, a longer wall is only a longer quiet — a priced
     breath, not a louder tally.
  4. *Precedent, read correctly.* The app does tick during
     two waits, and both prove the rule by their difference.
     The Emergency valve ticks per second because the man's
     hand is *on* it, holding — the ticks track **his own
     ongoing act**, effort feedback for effort being spent.
     The peek pill detents at 15-second marks (VM ticker,
     `playRotaryDetent(760)`, gain 0.38, 6 ms) during **his**
     lease, at one-fifteenth the density proposed here. The
     countdown is neither his act nor his lease; it is an
     imposed wait, and the app's imposed waits — the seal
     proving dwell, the desk clock's passing seconds — are
     silent. The door's countdown joins them.
- **Schwelle's knock at HOLD 00 — ADOPTED, reduced, as §5.3.**
  A single beat at zero is not a countdown sound; it is the
  offer's arrival, and it answers the one real defect of a
  silent wall: a man who is not watching the gate (eyes
  closed, head down — the actual postures of 1 AM) cannot
  tell the offer has armed without it. One knock at 00, at
  half the arrival's gain, closes that gap without opening
  the per-second door Schliff's fragment would have opened.

**The exception that is not one:** no anticipatory tick at
0:01. A penultimate tick would make §5.3's beat an echo of a
warning instead of an arrival, and would reintroduce
per-second logic through the last second. The wall is silent
*until* zero, not silent *except near* zero.

**Accessibility note, for Tor's record:** the zero beat is
this score's non-visual channel — offer-armed is the only
countdown fact a man not looking at the screen needs, and it
is the one fact the score sounds. Anything finer (seconds
remaining spoken) belongs to the system's screen reader, not
to the synth engine.

### 5.3 Zero — the offer arms

**Spec (adopts Druck D-F11's number exactly; pins its haptic,
which D-F11 left open):**

```
playDoorOfferArmed()
  audio:  enqueueTransient(fundamentalHz = 300f, durationMs = 44,
                           gain = 0.30f, chordRatio = 1.5f)
          — the arrival's recipe at exactly half gain:
            0.30 = 0.60 × 10^(−6.02/20), the engine's own
            −6 dB idiom (cf. BOLT_PEAK_GAIN's derivation)
  haptic: triggerWaveform(timings = [0, 7, 5],
                          amplitudes = [0, 150, 60],
                          fallbackMs = HAPTIC_ROTARY_TICK_1MS)
          — the detent's shape, softened: felt in a held
            hand, a tap on furniture, never a second latch
```

- **Where it fires (L-5).** At the *state transition*, in the
  VM's anchored derivation — the recomposition in which
  COUNTING becomes OFFER — and at **no other place**. The
  same beat, same numbers, fires on the FIX-5c resolution:
  a held door whose derived cooldown reaches 0 mid-visit and
  whose offer re-derives into Z1 fires `playDoorOfferArmed()`
  once, at that transition, whether the visit is five seconds
  old or five minutes. An offer arriving is an offer
  arriving; the score does not discount it for arriving late,
  and does not invent a "late" variant.
- **Where it never fires.** Held doors whose zero changes
  nothing (withheld stands before and after the wall — beat
  3′: SILENCE); never-owed doors (no wall, no zero, no beat);
  a recomposition that merely re-derives the same OFFER state
  (the transition fires it, re-derivation does not — this is
  why the call cannot live in the composable).
- **Why not the latch haptic at full weight:** arrival spent
  [215, 150] forty-four milliseconds ago, five to ten seconds
  back. Repeating it would read as a *second arrival* — the
  door knocking twice, the second time to announce a grey
  chip. D-F11's insight was that the offer should announce
  itself to the ear so the eyes are free; its price is that
  the announcement must be unmistakably *smaller* than the
  arrival. Half gain and a detent's hand achieve the size
  difference in both channels.
- **Refused-then-armed races:** grant-time re-derivation is
  untouched by this beat. If the offer arms (§5.3 sounds) and
  the man's tap is then refused by a changed world, §5.5's
  silence governs the refusal — the sequence knock-then-
  silence is honest: something *was* offered; arithmetic
  then declined to sell it.

### 5.4 RETURN's press — the seal's voice is returned to the seal

**Spec (new door-local voice; replaces `playHarmonic­
Seal(arming = true)` at `transmuteImpulseAndReturn`):**

```
playDoorReturn()
  audio:  enqueueTransient(fundamentalHz = 300f, durationMs = 28,
                           gain = 0.48f, chordRatio = 0.9f)
  haptic: triggerWaveform(timings = [0, 9, 22, 10],
                          amplitudes = [0, 165, 0, 195],
                          fallbackMs = HAPTIC_SWITCH_FLIP_4MS)
          — the releasing latch's waveform, verbatim
```

- **Chord 0.9 is the ruling's core.** In the engine's grammar
  (§1), 0.9 is the release interval — the releasing latch,
  the seat knock coming to rest. RETURN is the visit's one
  pure release: the door lets go of the man. It should sound
  like letting go, in the door's pitch, not like the session
  binding itself (1.5 / 360 Hz / the seal's haptic, which is
  what the shipped code spends). After this fix,
  `playHarmonicSeal` is heard at doors **never** — its two
  remaining lawful spends on this face's orbit are the
  Disarm face's own terminal acts (§5.6), where a session
  genuinely unseals.
- **Gain 0.48** = −1.94 dB under arrival, −2.22 dB under the
  seal recipe it replaces; **28 ms** sits between the latch's
  16 (a switch) and the seal's 48 (a binding) — a seat, not
  a snap and not a ceremony. The haptic's heavy second
  segment (195, the releasing latch's signature) puts the
  weight in the *hand*: he feels the landing more than the
  room hears it. At 1 AM that distribution — private weight,
  public quiet — is exactly right for the encouraged act.
- **No reward layer.** Druck §5: silence after return is
  correct, a congratulation would be a streak in a party
  hat. This beat is not a reward; it is the mechanical sound
  of the door's own latch releasing, fired at the state
  change as the face begins its (kept) 260 ms specA exit
  fade, and it is the visit's last sound. Nothing follows it
  — no completion pulse, no second segment, ever. If Tor is
  tempted to make RETURN warmer, the warmth is already here:
  it is the only beat whose haptic out-weighs its audio.
- **Double-tap:** input dies after the first tap (designer
  skeleton §8); the VM function early-returns on a dismissed
  state as shipped. One tap, one beat.

### 5.5 The peek taken — a binding, in the door's voice

**Spec (new door-local voice; replaces `playMechanical­
Latch(true)` at `start2MinutePeek` :2398):**

```
playDoorPeekTaken()
  audio:  enqueueTransient(fundamentalHz = 300f, durationMs = 32,
                           gain = 0.50f, chordRatio = 1.5f)
  haptic: triggerWaveform(timings = [0, 11, 26, 9],
                          amplitudes = [0, 215, 0, 150],
                          fallbackMs = HAPTIC_SWITCH_FLIP_4MS)
          — the engaging latch's waveform, verbatim (shared
            with arrival: the same hand-motion, a lock turning)
```

- **Chord 1.5, deliberately.** Taking the peek is the visit's
  second binding: a lease is granted and a cooldown is armed
  by the same act (FIX-8's re-timed price — the cooldown
  starts when the peek *ends* — is machinery this sound does
  not editorialise; the binding it marks is the lease). In
  the chord grammar, a spend that binds takes the binding
  interval; a release takes 0.9. RETURN 0.9 / PEEK 1.5 at the
  same pitch is the whole moral geometry of the face,
  audible in one comparison a half-asleep man can make
  without words: one of these acts closes something, the
  other opens his hand.
- **Gain 0.50, duration 32 ms:** above RETURN's 0.48 (the
  spend is the weightier act), below arrival's 0.60 (his own
  tap needs less announcing than the door's interruption),
  shorter than arrival (a key turning, not a door shutting).
  The identical haptic to arrival is the one repetition this
  score allows itself: arrival and grant are the door's two
  *lock* events, and the hand learns them as one gesture
  felt from opposite sides of the visit.
- **Placement.** The call replaces the latch call at its
  exact existing site — after the grant re-derivation has
  passed, at the state change that dismisses the door. The
  refused branch returns *before* it, as shipped: **a refused
  grant is SILENCE in both channels** (5′). The face prints
  no error (skeleton §8, acquitted); the score matches it —
  an error buzz would be an apology in another medium, and
  arithmetic does not apologise.
- **Hand-off.** This beat is the door score's last word on
  the peek. The pill's life — its 15-second detents at
  760 Hz / 0.38 (VM ticker) and `end2MinutePeekEarly`'s spend
  — is Pastille's face and another audit's property; §7 flags
  the one inconsistency there for Tor to route. Nothing in
  this score re-voices the pill.
- **Conditional annex — if Innovator P-3a (hold-to-take)
  ships.** The hold's fill detents fire `playRotaryDetent­
  (300.0)` — the detent voice *at door pitch* (lawful: the
  detent clamps 280–1480), gain 0.38, 6 ms, at the fill's
  quarter, half, and three-quarter marks only (three detents
  maximum; a detent at completion would collide with the
  grant beat). Completion fires `playDoorPeekTaken()` above,
  **superseding P-3a's proposed landing on `playMechanical­
  Latch(true)`** — the generic switch voice is what this
  whole score retires from the face, and the hold's
  completion is the last place it would have survived.
  Release-before-full is **SILENCE** in both channels (the
  120 ms drain is visual only): a hold abandoned must not
  buzz its disappointment, and a sound on release would
  teach the hand that letting go is also an event. If P-3a
  is struck, this annex falls with it and the tap-world spec
  above stands alone.

### 5.6 The exit to Disarm — gravity without volume

**Spec (new door-local voice; fired by the new door-origin
Disarm entry, on confirmed handoff only):**

```
playDoorExitToDisarm()
  audio:  enqueueTransient(fundamentalHz = 240f, durationMs = 40,
                           gain = 0.42f, chordRatio = 0.9f)
  haptic: triggerWaveform(timings = [0, 9, 22, 10],
                          amplitudes = [0, 165, 0, 195],
                          fallbackMs = HAPTIC_SWITCH_FLIP_4MS)
          — the releasing latch's waveform, verbatim
            (shared with RETURN: both are this door letting go;
             what differs is where he lands, and the pitch says it)
```

- **Why 240 Hz is the score's one pitch exception (L-1).**
  240 = 300 × 0.8 — a major third *down* from the door pitch,
  and a pitch the engine already owns (the tear pulse's
  fundamental): the app's established "paper giving way"
  register, low without being the completion pulse's 185 Hz
  cellar. The exit is the one beat that leaves the door's
  room entirely — it should sound like stepping *down* into
  a heavier room, and a pitch step is how a man hears a
  threshold without reading one. Druck D-F17 asked that the
  exit's handoff *feel* heavier than leaving by the slab
  from the first frame; motion is the designer's half of
  that ruling (the Disarm face's own entry), and this pitch
  step is its sonic half.
- **Gravity is duration and seat, not gain.** 40 ms is the
  door's longest transient, its haptic is the seat-heavy
  release waveform — and its gain, **0.42**, is the quietest
  press-beat in the score (−3.38 dB under the seal). A grave
  act at 1 AM does not need to be loud; it needs to be *low
  and final*. If any reviewer proposes raising this gain "to
  match its importance," this line is the refusal, pre-
  written: importance on this face is expressed downward.
- **Fires on handoff, not on tap (L-5's sharpest edge).**
  The designer skeleton's dead-end guard (§7 of the
  skeleton): if the Disarm face does not open, the airlock
  remains standing. The sound obeys the same guard — the
  beat fires in the VM action at the emission that opens
  the Disarm state, never in the tap handler. A tap that
  opens nothing sounds like nothing. (All other press beats
  share this shape in the shipped code — sound at the state
  change in the VM function — and the exit is held to it
  explicitly because its tap and its outcome are separated
  by a guard the other acts don't have.)
- **No double voice at the seam.** `requestDisarmChallenge`
  today plays `playMechanicalLatch(false)` on entry.
  Door-originated entry (Innovator P-2c's `fromDoor` flag —
  adopted here as the carrier for this sound law as well as
  its face law) **suppresses that latch**: `playDoorExit­
  ToDisarm()` *replaces* it, it does not precede it. Two
  release sounds a frame apart would read as a stutter in
  the machinery at the exact moment the machinery asks for
  his trust. Deck-originated entry keeps its existing latch —
  this score governs the door's exits, not the Deck's.
- **The stopwatch-past-floor shortcut, from the door.**
  P-2c's second trap: a door-originated request on a
  stopwatch past its floor currently disarms immediately,
  spending `playHarmonicSeal(false)`. Under P-2c the door-
  originated path always presents the face, so the shortcut
  cannot fire from here; if Tor strikes P-2c, this score's
  fallback ruling is: the exit beat still fires (the tap was
  honoured — a face *or* a completion follows), and the
  seal(false) that follows it is the Disarm machinery's own
  terminal sound, lawfully sequenced after the door's, not
  a double voice — the two sounds mark two different acts
  (leaving the door; ending the session). Either way the
  door's beat comes first and the session's sound, if any,
  is never the door's to suppress.
- **Beyond the seam — boundary, stated once.** The valve's
  per-second `fireFeel` ticks, its bank detents, and the
  terminal `playHarmonicSeal(arming = false)` (480 Hz /
  48 ms / 0.62 / 1.5) on completion are the Disarm face's
  existing score and are **not re-specced here**. Two
  consistency notes only: (i) the terminal 0.62 is lawful
  there precisely because it is not here — a session
  actually ending, after a deliberate 60-second hold, on a
  weekly token, is the one act in this orbit allowed the
  seal's weight; (ii) **KEEP FOCUSING's peel back to this
  door is SILENCE (6′)** — `dismissDisarmChallenge` is
  silent today and stays silent, and the door beneath
  (P-2a: never dismissed) must not re-fire its arrival beat
  when the Disarm card peels away. He never left the visit;
  the visit does not re-announce itself. If the builder's
  wiring dismisses and re-triggers the airlock on this path
  instead, the arrival beat keyed to the nonce (§5.1) is the
  guard — same nonce, no second knock — and the wiring
  should be fixed to P-2a regardless.

---

## 6. Sounds that die with FIX-3b — no sonic ghosts

FIX-3b kills `onSelectReason` / `onTransmuteParticleTap`
and their state. Their sounds die in the same pass, with no
replacements specced and none permitted:

- `selectDistractionReason`'s `playRotaryDetent(580)` — a
  survey instrument's click, on a face that no longer
  surveys.
- `incrementAirlockParticlePulse`'s rising detent
  (500 Hz + 35 per tap) — the craving-second theatre's last
  instrument. Its rising pitch was, in feel terms, a small
  slot machine bolted to the door; the audit killed the
  particles, and this score records that the *sound* of the
  particles is part of what was killed, so no future pass
  re-adds a "tap feedback" detent to this face by reflex.

General law, for the builder's grep: **after this score, the
only engine calls reachable from the Held Door's visit are
the five `playDoor*` methods of §5.** Any sixth call found on
the face is a finding, not a flourish.

---

## 7. Flags for Tor (outside my slice, routed not ruled)

1. **`end2MinutePeekEarly` spends `playHarmonicSeal(true)`**
   (VM, pill verb). The same seal-voice theft as §2 defect 2,
   one face over: ending a 2-minute lease early sounds like
   a session sealing. Pastille's pill audit owns the verb;
   if Tor wants a ruling of mine to carry: the lease's end
   is a release and wants the 0.9 family at lease pitch, at
   or under the door's RETURN gain. Not specced — not my
   face.
2. **The pill's 15-second detents** (760 Hz, gain 0.38) tick
   inside the blocked app during the lease — the man is
   awake and spending there, so §5.2's night argument does
   not transfer automatically. Recorded so the pill's audit
   prices them knowingly; no change proposed from this
   bench.
3. **`hapticsEnabled` has no persisted fact** comparable to
   `SoundEffectsSetting` — it is a bare engine var, born
   `true`. Every haptic in this score inherits that gate as
   it stands. If a future truth car finds the copies-disagree
   disease here (the UNA-03 shape), this score's waveforms
   are unaffected — they read the gate, wherever it lives.

---

## 8. Implementation map (builder-ready, paper only)

**New engine methods** (door-local section of
`TactileSoundEngine.kt`, beside the existing named voices).
Constants named once, in the companion, in the seal
constants' idiom:

```
DOOR_PITCH_HZ        = 300f    // FIX-7c
DOOR_EXIT_PITCH_HZ   = 240f    // 300 × 0.8 (§5.6)
DOOR_ARRIVAL_MS      = 44      DOOR_ARRIVAL_GAIN  = 0.60f
DOOR_OFFER_GAIN      = 0.30f   // arrival × 10^(−6.02/20)
DOOR_RETURN_MS       = 28      DOOR_RETURN_GAIN   = 0.48f
DOOR_PEEK_MS         = 32      DOOR_PEEK_GAIN     = 0.50f
DOOR_EXIT_MS         = 40      DOOR_EXIT_GAIN     = 0.42f
```

| Method | Audio (Hz / ms / gain / chord) | Haptic |
|---|---|---|
| `playDoorArrival()` | 300 / 44 / 0.60 / 1.5 | latch-engaging verbatim |
| `playDoorOfferArmed()` | 300 / 44 / 0.30 / 1.5 | [0,7,5] / [0,150,60], fb 1 ms |
| `playDoorReturn()` | 300 / 28 / 0.48 / 0.9 | latch-releasing verbatim |
| `playDoorPeekTaken()` | 300 / 32 / 0.50 / 1.5 | latch-engaging verbatim |
| `playDoorExitToDisarm()` | 240 / 40 / 0.42 / 0.9 | latch-releasing verbatim |

**Call-site changes (all in `MonolithViewModel.kt`):**

| Site | Change |
|---|---|
| `triggerAirlockIntercept` (arrival) | `playMechanicalLatch(true)` → `playDoorArrival()`; keyed to the visit's nonce (§5.1) |
| Wall-expiry / FIX-5c resolution transition | **new:** `playDoorOfferArmed()` at the COUNTING→OFFER / HELD→OFFER state transition, once (§5.3) |
| `transmuteImpulseAndReturn` | `playHarmonicSeal(true)` → `playDoorReturn()` |
| `start2MinutePeek` | `playMechanicalLatch(true)` → `playDoorPeekTaken()`, at its existing post-derivation site (§5.5) |
| Door-exit VM action (FIX-8 / P-2c `fromDoor`) | **new:** `playDoorExitToDisarm()` at the Disarm-open emission; `requestDisarmChallenge(fromDoor = true)` suppresses its `playMechanicalLatch(false)` (§5.6) |
| `selectDistractionReason`, `incrementAirlockParticlePulse` | deleted with FIX-3b — their engine calls die with them (§6) |

**Explicitly not changed:** `playMechanicalLatch`,
`playHarmonicSeal`, `playSealThrow`, `primeForSeal` and the
whole seal lane; the pill ticker; the valve's `fireFeel`
ladder. If a diff for this score touches any of those, the
diff is wrong.

---

## 9. Verification the builder can encode (paper, for Ren's compile)

1. **Constant pins.** Each `DOOR_*` constant equals its §8
   value; the gain ceiling asserts structurally:
   `max(DOOR_ARRIVAL_GAIN, DOOR_OFFER_GAIN, DOOR_RETURN_GAIN,
   DOOR_PEEK_GAIN, DOOR_EXIT_GAIN) == 0.60f` and `0.60f <
   0.62f` (the seal recipe's gain, read from
   `playHarmonicSeal`'s call — a comment-pinned literal is
   acceptable where a constant doesn't exist; the *ordering*
   is the law, per L-2).
2. **Call-site greps.** `playHarmonicSeal` no longer appears
   in `transmuteImpulseAndReturn`; `playMechanicalLatch`
   appears at neither door site; exactly the five
   `playDoor*` methods are reachable from the visit (§6's
   law); no engine call exists in
   `AirlockInterceptOverlay`'s composable body.
3. **Countdown silence.** Drive the wall state 5→1 (and a
   tapered 10→1) through the VM's derivation with a test
   engine: `pendingCommandCount()` (the engine's existing
   T2 seam) does not change across any per-second transition;
   it changes by exactly one at the COUNTING→OFFER
   transition, and by zero at a HELD zero where nothing arms.
4. **Gates.** With the sound fact OFF, all five methods
   enqueue nothing and the waveforms still fire (haptic-only,
   S0); with the fact ON, a full scripted visit (arrival →
   wall → offer → RETURN) enqueues exactly 3 commands, in
   order, with the §8 parameters.
5. **Nonce.** Two `triggerAirlockIntercept` calls with the
   same visit nonce enqueue one arrival command; a second
   call with a fresh nonce enqueues a second.

---

## 10. Charge sheet for Tor (rule each, or strike with reason)

| # | Ruling | Price |
|---|---|---|
| T-S1 | Arrival per FIX-7c verbatim, as a **new door-local** `playDoorArrival()` — latch haptics kept, `playMechanicalLatch` itself untouched | SMALL |
| T-S2 | Countdown **SILENCE** both channels, all seconds, all wall lengths; Schliff's per-second detent **rejected** (four grounds, §5.2) | SMALL (a non-call, plus test 3) |
| T-S3 | Schwelle's knock at 00 **adopted reduced** as the offer-armed beat: arrival recipe at half gain 0.30 + detent-weight haptic; fires at the state transition only, incl. FIX-5c resolutions | SMALL |
| T-S4 | RETURN re-voiced: `playDoorReturn()` 300/28/0.48/**0.9** + release-latch haptic; `playHarmonicSeal` retired from this face (seal-voice theft cured) | SMALL |
| T-S5 | PEEK taken re-voiced: `playDoorPeekTaken()` 300/32/0.50/**1.5** + engaging-latch haptic; refused grant stays silent | SMALL |
| T-S6 | P-3a annex (conditional): fill detents at door pitch 300 Hz; completion lands on T-S5, not the generic latch; release-before-full silent | SMALL (inside P-3a's price) |
| T-S7 | Exit to Disarm: `playDoorExitToDisarm()` 240/40/0.42/0.9, fired on confirmed handoff only; `fromDoor` suppresses the Disarm entry's own latch (no double voice); KEEP FOCUSING peel-back silent, no re-arrival | SMALL |
| T-S8 | Sound-never-grades law (L-3): one arrival for every path / tier / count / homecoming; no night gain variant | — (a refusal, filed) |
| T-S9 | FIX-3b's two detents die with their wires; the five-`playDoor*` grep is the face's sonic closed world | — (inside FIX-3's price) |

**The visit, after this score, in one breath:** the app
vanishes behind him, and a low knock — the seal's own recipe,
a fifth lower, a breath shorter — says the door was already
posted; then nothing, for five honest seconds or ten priced
ones, nothing at all, while a slab he can take at any moment
sits in silence below a gate that ticks only on paper; then,
if he has waited, a knock at half the first one's weight,
small enough to hear with his eyes closed and know it is
only an offer; and then his own hand chooses the visit's
last sound — a seat and a release if he returns, a key
turning if he takes the lease, a step down into a lower
room if he goes to end it — every one of them quieter than
the knock that stopped him, none of them the loudest thing
in the app, none of them waking the house. A door that
sounds like that does not need to shout EMERGENCY at
anyone. It has already been heard.

— TON, Feel Specialist 3 (sound + haptics), Tor's cell,
issue #11. Report to Tor; nobody compiled; nothing here
messages the user.

## NEXT (Law 10)

(a) **What I want to do next:** score the Disarm face's sixty
seconds as the continuation of this one — the valve's
per-second `fireFeel` ladder (a latch and a firm click
alternating under a holding hand for a full minute) has
never been read as a sound design, only inherited; the
seam at §5.6 means my exit beat is the first thing that
face's visitor hears, and what he hears second is currently
nobody's ruling. Same man, one threshold later.

(b) **What I could do, unassigned:** a one-page sonic
closed-world sweep for Hale's register — every face's
engine calls grepped against their audit's intent, in this
file's §9.2 style — so "which sounds may this face make"
becomes a checkable list per face instead of tribal
knowledge, starting with the pill (whose seal-voice spend,
§7 flag 1, is already waiting for an owner).
