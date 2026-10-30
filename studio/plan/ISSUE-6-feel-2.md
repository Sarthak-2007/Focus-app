# ISSUE #6 — FEEL-2 (SOUND) — THE DECK'S COMPLETE SOUND MAP
## Cell: Rost (lead) · FEEL-2 seat · Fri Oct 30, 2026
## Status: DECLARED, paper only. Nothing compiled. Nothing committed (Rost commits).
## Report to Rost, never to the user. This file re-opens no audit ruling.

Read whole, as briefed: `AUDIT-deck.md` (the Deck's job, §1;
the drawer law, §3/§8; F6/F16/F18), `TactileSoundEngine.kt`
whole, every `TactileSoundEngine` call site reachable from the
Deck in `BlockitCoreViews.kt` (wheel + slide), `TactileDeckScreen.kt`,
`MonolithViewModel.kt` (the Deck's callbacks), `ArmProvingController.kt`,
`SessionSealTransition.kt`, `WatchStore.kt` (the warrant), and
`ISSUE-6-innovator.md` P5. Sibling filings read and honoured:
FEEL-1 (TOUCH) owns haptic waveforms, travel, and coast physics;
FEEL-5 owns tempo. Where one engine call fires haptic + PCM
together — which is every call in this engine — FEEL-1 specs the
waveform half and this file specs the acoustic half (pitch,
gain, duration, and whether the event may sound at all). A voice
is named once, in the file that owns its channel; §11 collects
the two reconciliations Rost must ratify because the channels
share call sites.

**The one law this file adds:** a Deck sound earns its place
only if it reports a fact the hand or eye does not already have
at that instant — a crossing, a landing, a refusal, a proof.
A sound that repeats what the gesture already knows, arrives
after its fact has expired, or celebrates an act instead of
reporting it, **performs instead of informs**, and is killed
below by name. The Deck's machinery is unusually honest in
print (audit §5 acquittals); its sound, audited end to end, is
not — it speaks twice at its busiest surface, sings a legacy
seal-voice for unsealing, and lets one overlay tick sixty times
for one fact. All three are fixed on paper here.

---

## §0 · ENGINE TRUTH — WHAT CAN SOUND, AND THROUGH WHICH LANE

Two lanes exist, and the difference between them is the whole
discipline:

- **The legacy queue.** One serial `LinkedBlockingQueue`
  (capacity 64, `offer` — full means silently dropped), one
  writer thread, float-PCM transients synthesised per command
  (4 partials, weights {0.6, 0.28, 0.37, 2.2}, dual exponential
  decays). Every ordinary Deck sound travels this lane, in
  order, at up to ~160 ms of buffer per command. It can lag,
  and a lagging position sound is a lying one (§2, Law 3).
- **The seal lane.** The Throw's own retained buffer and its
  own `AudioTrack`, pre-rendered at prime (the latch), purged
  of all pre-latch backlog by a sequence barrier, fired by one
  `play()` at t=0. It never queues, never mixes, never waits.
  This lane exists because the seal sound is a proof, and a
  proof that arrives late is not one (§4).

The voices (engine constants, verified in
`TactileSoundEngine.kt`):

| Voice | Call | Acoustic form | Haptic twin |
|---|---|---|---|
| Detent | `playRotaryDetent(freq)` | 6 ms, gain 0.38, f clamped 280–1480 Hz | 7 ms @180 + 5 ms @70 |
| Latch | `playMechanicalLatch(engaged)` | 16 ms, gain 0.54, 760 Hz engage / 520 Hz release, chord 1.2 / 0.9 | two-stage strike–gap–seat |
| Firm click | `playFirmClick()` | 12 ms, gain 0.50, 620 Hz, chord 1.1 | 13 ms @225, breath, 8 ms @135 |
| Seat knock | `playSeatKnock(dur, amp, pitch)` | (dur+6) ms, gain 0.22+0.30·amp, pitch clamped 160–420 Hz, chord 0.9 | strike dur @amp + half-length tail @35% |
| Harmonic seal (legacy) | `playHarmonicSeal(arming)` | 48 ms, gain 0.62, 360 Hz arm / 480 Hz disarm, chord 1.5 | 24/38/16 thud-through |
| **The Throw** | `playSealThrow()` | 1000 ms, zero parameters: 360 Hz body (partials as above, gain 0.62), bolt of filtered noise at +30 ms at −6 dB, room combs at −12 dB wet, tail fading from 900 ms; golden SHA-256 `d204b95a…`, bit-identical at every seal | [0, 24, 38, 16] @ [0, 255, **0**, 175] — the legacy seal's skeleton with the middle zeroed: it breathes through the gap |
| Completion pulse | `playCompletionPulse()` | 64 ms, gain 0.68, 185 Hz, chord 1.33 | three softening ticks |
| Tear pulse | `playTearPulse()` | 40 ms, gain 0.68, 240 Hz, chord 1.21 | ripping pair |

Gates, as built: the persisted fact `SoundEffectsSetting`
(default ON) gates every PCM path and is re-read at every
engine's birth and at every seal-transition bind (Truth Car 3,
UNA-03). With sound OFF the Throw still fires its haptic —
**muting loses timbre, never the event** (the engine's own S0
law). Haptics ride a separate var (`hapticsEnabled`, born true)
that no settings surface in this tree writes — flagged in §11
as neighbour debt, not priced here. Every legacy command also
requests transient audio focus (`MAY_DUCK`) at poll time —
per command, including per detent in a fast drag (§9, A5).

`playHarmonicSeal` and `playTearPulse` have **no lawful Deck
spend** after this file: the Throw is the Deck's only
seal-family sound (§4), the tear belongs to Tasks' counterfoil,
and the harmonic seal's Deck-reachable uses are all disarms
(§7). Both voices stay in the engine for their other owners;
the Deck simply never speaks them again.

---

## §1 · CENSUS — EVERY SOUND THE DECK MAKES TODAY

Traced call-site by call-site. "Deck-reachable" means the Deck's
own views, its Compose body, or the ViewModel functions its
callbacks invoke. Verdicts point at the section that rules them.

### The wheel (`RotaryWheelView`, via `fireFeel` / `fireWeightedKnock`)

| # | Event | What sounds today | Verdict |
|---|---|---|---|
| W-a | Rim drag, ordinary 5-min crossing | Detent at the view's ladder, f = 340 + ((v−5)/175)·540 → 340–880 Hz — **plus** a second detent from the ViewModel (§1, D-a) | KEEP the view's; KILL the VM's (A1) |
| W-b | Rim drag, %15 stop | `fireFeel(4L)` → the **latch voice at fixed 760 Hz**; the ladder pitch is passed and discarded (the `fireFeel` contract admits this in its own comment) | FIX — pitch is the information; the stop must keep it (A2) |
| W-c | Landing on the 10 shore | Detent @ 340 Hz | KEEP |
| W-d | Landing on FLOW, by drag | Seat knock 14 ms @ 0.8 / 300 Hz | KEEP |
| W-e | Landing on FLOW, by coast | Detent @ 340 Hz — a different voice for the same landing as W-d | FIX — one landing, one voice: the gate knock, both entries (A2 note) |
| W-f | Strait toll (coast crossing ≥180°/s) | Seat knock 10 ms @ 0.5 / 280 Hz | KEEP |
| W-g | Seat arrival after coast/spring (travelled ≥2°) | Seat knock by count: 16 ms @ 0.85 / 320 Hz → 24 ms @ 1.0 / 230 Hz at 8 blocks | KEEP — mass is the information |
| W-h | Hub commit (set / cycle blocks) | Latch (loose) or firm click (closure) from the view — **plus** a VM detent @ 640 Hz from `setStudyBlocksCount` | KEEP the view's; KILL the VM's (A1) |
| W-i | FLOW key-cap tap (dies with F2) | VM latch via `selectStudyPreset` **plus** VM detent via `setDialMinutes(5)` | Dies with the cap (F2); no successor sound |

### The doubling defect (the census's headline)

| # | Source | What sounds | Evidence |
|---|---|---|---|
| D-a | `MonolithViewModel.setDialMinutes` — wired as the Deck's `onDialMinutesChanged` | `playRotaryDetent(440 + minutes·2.8)` on **every** dial change | At dial 50 the view sings 479 Hz and the VM sings 580 Hz for the same crossing, back-to-back through the serial queue: every detent on the Deck's busiest surface is a two-note flam, and a fast drag queues dozens of doubled ticks that keep playing after the finger stops |
| D-b | `MonolithViewModel.setStudyBlocksCount` — wired as `onSetStudyBlocksCount` | `playRotaryDetent(640)` on every hub commit | Stacked on W-h's latch/click: the commit speaks twice in two voices |

The view owns position; the ViewModel owns state. **Position
sounds belong to the view that moves; state changes are
silent.** This is P2's one-mouth law in the acoustic channel,
and A1 (§10) enforces it with a grep gate, not a comment
(Vega's U-3 applies to sound exactly as to print).

### The slide (`SlideToStartView`)

| # | Event | What sounds today | Verdict |
|---|---|---|---|
| S-a | DOWN beginning any attempt | Latch (engage) — `fireFeel(4L)` in `beginAttempt` | KEEP — the attempt's opening fact; FEEL-1 concurs |
| S-b | Gate Schmitt crossing (0.68 up / 0.66 retreat) | Detent @ 620 Hz up / 340 Hz retreat; wire re-read *before* the feel; suppressed whole on a dead read | KEEP — the model citizen: sound gated on a fresh truth (§2, Law 5) |
| S-c | Brush (<90 ms, not an order) | Dull knock 10 ms @ 0.55 / 220 Hz + 1 dp sink | KEEP |
| S-d | Refusal (OFF at stop / at catch / at UP / latch-frame) | Refusal knock 12 ms @ 0.70 / 220 Hz → 300 ms hold → universal return spring | KEEP — one voice for every refusal door (§6) |
| S-e | Seat knock at displayed 0.97, before the latch at 1.00 | Seat knock 14 ms @ 0.85 / 320 Hz | KEEP; its stutter against a latch-frame refusal is FEEL-1's S4 (haptic timing) — the acoustic half adopts the same merge (§11) |
| S-f | Sled return home; sealed slide touched | **Silence, by construction** — no `fireFeel` exists on the return path; the sealed gate consumes and drops | KEEP AS FENCE — any lane adding a knock here re-opens this file with Rost |

### The cascade, the seal, the fault family (ViewModel / controller)

| # | Event | What sounds today | Verdict |
|---|---|---|---|
| C-a | Prime (at latch) | Nothing — audio focus paid, barrier set, Throw pre-rendered in the latch→t=0 window | KEEP silent — preparation is not an event |
| C-b | Proving (links answering) | Nothing | KEEP silent — §4; per-link ticks are killed before birth (§9) |
| C-c | **t=0, proof complete** | **The Throw, once, as `executeSeal`'s last statement** | THE LAW — §4 |
| C-d | Jam at latch+500 ms | Seat-knock voice 20 ms @ 1.0 / 320 Hz, **×3 at 90 ms spacing** (`playJamHaptics`) | KEEP — fault's repetition budget, spent once (§6); add queue purge (A6) |
| C-e | Annulment at latch+1500 ms / seal refused at its gate | Nothing; the prime is cancelled, the past-tense sentence stands 10 s | KEEP silent — the jam already spoke for this attempt; a second fault sound at the consequence is alarm stacking (§6) |
| C-f | Wire recovery (sentence slot clears after the 500 ms stability beat) | Hand-back tick: seat knock 5 ms @ 0.4 / 320 Hz | KEEP — health's return gets exactly one small tick; the edge/non-edge asymmetry is the design (§6) |

### The chair and its consequences (today: two swapping chips)

| # | Event | What sounds today | Verdict |
|---|---|---|---|
| H-a | GRACE chip tap → `confirmDisarmSession` | **Harmonic seal (disarm), 480 Hz / 48 ms** — the legacy *seal* voice, spent on unsealing | REPLACE — latch-release (§7) |
| H-b | VALVE chip tap, FLOW past its 20-min floor → clean completion | Harmonic seal (disarm) | REPLACE — latch-release (§7) |
| H-c | VALVE chip tap, challenge opens | Latch (release) from `requestDisarmChallenge` | KILL — opening a challenge spends nothing and must sound like it (§7) |
| H-d | Valve hold, **every second for 60 s** | Latch or firm click, alternating by `sec % 5` — ~60 sounds | **KILL** — §7; the file's worst performer |
| H-e | Valve hold, quarter banks (15/30/45 s) | Detent stepping 560/470/390 Hz **and** a firm click, fired together | KEEP one: the pitched detent alone (A7) |
| H-f | Valve hold completes (view) | `fireFeel(10L)` → firm click, *before* the VM's completion verdict | KILL at the view; the completion sounds once, from the VM, only on `Completed` (§7) |
| H-g | Valve completion (VM `completeEmergencyBreakFromValve`, on `Completed` only) | Harmonic seal (disarm); on `RefusedNoToken` — silence, correctly | REPLACE voice with latch-release; KEEP the refusal silence — it is the build-6 law's mirror: no spend, no sound |
| H-h | Drain ends at the banked floor | Firm click | KEEP — "banked, safe to release" is a fact |

### The rest of the Deck's body

| # | Event | What sounds today | Verdict |
|---|---|---|---|
| R-a | DONE cap → `completeTaskAndAdvance` | Nothing (merge ruling in its own comment: the seal voice was misspent here once; the tear owns task completion, on Tasks) | KEEP silent — a task finishing is not a Deck event; F7's ghost keeps it silent in running too |
| R-b | Sub-step toggle | VM detent @ 640 Hz | Dies with F7's eviction of sub-steps to Tasks; do not re-home it |
| R-c | DESK CLOCK / HELD chips open their sheets | VM latch via `toggleAlwaysOnDeskClock` / `toggleNotificationDigestSheet` | KILL (A8) — a sheet appearing is its own report; the latch voice is reserved for mechanical engagements, and these are doors |
| R-d | END BREAK | Nothing | KEEP silent — block/break boundaries never sound (§2, Law 4) |
| R-e | Session finish while the Deck stands (the E-clock, VM) | E0 settle knock 24 ms @ 1.0; E700 completion pulse **only** for pulse-lawful kinds (death-booked finishes stand empty at E700); E880 knock pair | KEEP AS FENCE — machinery (Nocke §3), recorded not re-ruled (audit §11); the completion pulse's only lawful Deck spend is E700 |
| R-f | Receipt shown; slip dismissed | Nothing; the sled return is S-f's silence | KEEP silent — F19's scrim-shake stays visual; a knock would punish a stray tap |
| R-g | Rail navigation away (QUEUE door et al.) | VM detent @ 540 Hz in `navigateTo` | Host chrome, not Deck-owned; the QUEUE door dies with F7 and its detent dies with it. No Deck ruling |

---

## §2 · THE LAWS

Six laws govern every row of the map in §§3–8. A proposed
sound that cannot cite its law does not ship.

**Law 1 — The Throw sounds only at t=0 proof (the standing
build-6 law).** The seal sound is a proof, not an intention:
it fires exactly once per session, inside
`SessionSealTransition.executeSeal`, as the act's last
statement — after claim, row re-anchor, and marker commit
have all landed. A seal that cannot complete does not sound:
`sealRow` returning false, an annulled proving, a refused
latch — all silent, forever, for that attempt. The pre-proof
seal the old arm path played was deleted for exactly this
reason ("a seal sound before proof is intent wearing proof's
clothes," Opal §F2, preserved in the VM's own comment), and
the cascade's header states the surviving law verbatim: the
seal sound fires at t=0 — *its only arm-path call.*
Corollaries: (a) the Throw takes **zero parameters** — no
provenance, duration, or clock can enter it, so two seals
cannot differ, and no look, routine, or door may re-voice it
(the FEEL law's fraud clause: per-world seal sounds are
fraud; the golden hash is the enforcement); (b) routine-born
and restored seals sound it identically — a seal is a seal
whichever door armed it; (c) recovery adopts a marker-proved
seal **silently** — re-sounding a Throw for a seal already
heard would be a haunting, not a proof; (d) nothing else on
the Deck may borrow the seal family — disarms speak
latch-release (§8), finishes speak the E-clock (§9), and
`playHarmonicSeal` is evicted from the Deck whole.

**Law 2 — One event, one sound, one mouth.** Every fact that
sounds does so once, from the surface that owns the fact.
Doubling is the acoustic form of the six-prints disease: the
wheel flam (D-a/D-b) is two renderings of one crossing and
dies by the same ruling as the strip's second BLOCK cell.
Enforcement is mechanical: a grep gate in the ESR family
fails a merge where `setDialMinutes` or `setStudyBlocksCount`
contains a `play*` call (A1).

**Law 3 — Position sounds are perishable.** A detent reports
where the wheel *is*. Through the serial queue a detent can
be tens of milliseconds old at poll; a backlog from a fast
drag plays on after the finger stops, reporting positions
the wheel has left — sound lying about the present. Rule:
legacy detent commands carry their enqueue stamp (the `seq`
machinery already exists for the seal barrier); at poll, a
detent older than **50 ms** is dropped, never played. Knocks,
latches, and the fault family are *event* sounds, not
position sounds, and are exempt — a refusal must sound even
if it sounds late, because its fact has not expired. The
Throw is exempt by construction (its own lane).

**Law 4 — The Deck never sounds the passage of time.** No
per-second ticks, no boundary chimes, no progress pings.
Time on this machine is print (the readout, the rim ink) and
nothing else. The valve's per-second ticking (H-d) is the
standing violation and dies in §7; break and block boundaries
(R-d), the running countdown, and FLOW's open elapsed time
are silent today and stay silent by law. A man sealed into a
session should hear the room go quiet, not acquire a clock
that ticks at him.

**Law 5 — Sound follows the freshness-checked truth.** Any
sound that encodes the wire encodes `WatchStore.printState`
— never the raw wire — so a stale LIVE feels and sounds
UNSURE, exactly as it prints (P1/P2's discipline in the
acoustic channel). The gate tick's built behaviour is the
precedent: the wire is re-read *before* the feel, and a dead
read suppresses the sound whole (S-b). No sound may assert a
health the print would not claim in the same frame.

**Law 6 — Every acoustic fact has a non-acoustic twin.**
Sound is a redundant channel by design (Innovator P5's own
constitution): every row of this map has a print, haptic, or
motion twin standing in the same instant, F22's semantics
announce the wire classes in words, and with sound OFF the
Deck loses timbre and nothing else. A sound that is the only
carrier of its fact is a defect on arrival.

---

## §3 · THE WHEEL — SOUND MAP, BY WIRE STATE (P5, ACOUSTIC HALF)

The wheel is the Deck's only continuous sound: dozens of
detents per setting gesture, at 1 AM, eyes half shut. That is
why P5's felt truth belongs here and only here — the class is
learned over a drag, not decoded from one beep. FEEL-1 §7
judged P5 SHIP (wheel only) and spec'd the touch half; this
section specs the acoustic half of the same enum, on the same
constants, so hand and ear can never disagree.

**The class.** `WireFeel { SOLID, UNSURE, REFUSED }`, a pure
function of the freshness-checked print state (Law 5):
SOLID = freshly LIVE · UNSURE = UNPROVEN, including stale
LIVE · REFUSED = OFF. The wheel view holds no wire today —
the Deck passes the class in its `update` pass, beside
`isNightMode`, from the `deckWarrant` it already derives.
Class changes apply only at warrant edges, and mid-drag at
the **next detent crossing, never mid-detent** (Innovator's
clause, adopted). The feel sleeps while the wheel is locked
or a session runs — a sealed wire drop is §6's latched print
and the interrupt path, never a sound gimmick. FLOW's seat
has no detents and carries no class; its landing knock is
positional (W-d) in all classes.

**The mapping — pitch only.** Loudness never encodes the
wire (gain is the family's, constant per voice); duration
never encodes it either. One channel, one variable:

| Class | Ordinary detent (per 5-min crossing) | %15 major stop | What the hand learns |
|---|---|---|---|
| SOLID | Ladder as built: 340→880 Hz by value, 6 ms, gain 0.38 | Ladder pitch, **kept** — duration steps to 10 ms at the same pitch (A2) | The scale sings its rise; higher is longer, and the pitch *is* the readback |
| UNSURE | Whole ladder × **0.72** → ≈245–635 Hz, same steps, same gain | Ladder × 0.72, 10 ms — the major's relative step survives, lowered with the scale | The same song in a lower register: counting, but the machine will not promise — heard within two or three crossings, before the slide is touched |
| REFUSED | Ladder **frozen flat at 240 Hz** at every stop | Flat 240 Hz, ordinary duration — the major's step dies too | The scale stops singing its rise. The wheel still turns and still ticks (he may still mean FLOW, still mean to look) but value no longer sounds like progress |

**What does not scale.** Every discrete knock keeps its built
pitch in all three classes — seat knock by count (W-g),
strait toll (W-f), FLOW gate knock (W-d, now both entries),
hub commits (W-h). Their pitches already encode mass and
position; re-pitching them by wire would collide with the
refusal family (an UNSURE-scaled gate knock at ~215 Hz is
indistinguishable from the 220 Hz refusal) and would give one
fact two encodings on one gesture — the felt dialect of the
six-prints disease, which FEEL-1 §7 rejected on the slide and
this file rejects on the wheel. **The detent ladder is the
wire's only acoustic carrier; everything else stays literal.**
FEEL-1's UNSURE seat-knock pitch shift (−60 Hz) and REFUSED
seat replacement are therefore read as *haptic-waveform*
terms only (duration/amplitude), the acoustic pitch constant —
reconciliation R-1, §11, for Rost's ratification.

**The rattle.** FEEL-1's UNSURE rattle (a second tick at
−40%, +18 ms, on every other detent) lives in the **haptic
channel only**. An acoustic rattle would double every other
tick's sound — the doubling disease (Law 2) wearing a spec —
and the ear does not need it: the ×0.72 drop is already
unmissable over a drag. One rattle, in the hand; one pitch
drop, in the ear.

**Echo marks (P7) — the special knock is KILLED before
birth.** P7 proposes a different knock when a detent passes
an echo mark. That knock reports nothing the snap and the
seat do not: passing a familiar span is not an event, and a
nostalgia chime is performance in its purest form. Echo
pass-over sounds the **ordinary detent at ladder pitch**;
landing and seating on an echo span sounds the ordinary seat
knock. P7 ships visual + magnetic, acoustically silent as a
feature. (Price effect: none — a deletion from P7's sketch,
not an addition.)

**Also in this section's fence:** the end-stop knock and the
FLOW-hub dull knock are FEEL-1's W1/H1 (priced there, S each);
their acoustic form is the dull knock they already name
(10 ms @ 0.55 / 220 Hz, `playSeatKnock`) — adopted here as
the map's rows, not re-priced.

**Kill criteria (shared with FEEL-1 §7).** The device pass
judges both channels together: 9 shuffled trials sorted by
hand and ear, ≥8/9 per class ships; below that the class
collapses to SOLID-only — the factor and the flat are
constants behind the one enum, so killing the mapping is
deleting a function, not a mechanism. Tuning steps offered
at the pass: factor 0.85 / 0.72 / 0.60.

---

## §4 · THE SEAL AND THE PROVING — THE QUIET BEFORE ONE SOUND

The arm path's sound budget, whole: the DOWN latch (S-a),
the gate ticks (S-b), the seat knock at 0.97 (S-e), the
latch knock's lawful survival of the prime's barrier — and
then silence, until one of exactly two sounds ends the
attempt: the Throw (proof) or the jam strikes (fault). The
prime itself is silent (C-a); the render, the focus
transaction, and the queue purge are preparation, and
preparation that sounds would be the machine clearing its
throat.

**Proving is silent (C-b), and the silence is load-bearing.**
Three links answering could be ticked off one by one — and
must not be: per-link ticks would perform progress the man
cannot act on (there is no verb during proving), would hand
him a countdown whose failure at tick two he can only watch,
and would spend the quiet that makes the Throw land as an
event. The drawer (F16) shows proving; nothing sounds it.
The dwell floor (t=0 never before latch+210 ms) and the
500 ms jam deadline pass in that silence, which is why both
of the sounds that end it are unmistakable.

**The Throw, as built, is adopted without amendment** —
1000 ms against the seal shot's 1050 ms, so the sound *is*
the shot's span, neither outliving the ceremony nor ending
before it. Its recipe (body / bolt at +30 ms / room, §0) is
frozen by its golden hash; any lane that nudges a constant
turns the identity test red instead of shipping a drifted
voice. One voice, one lane, one call site in production
(`executeSeal`), grep-gated (T-R2). **No price — a fence.**

**The latch knock and the barrier, for the record:** the
prime captures its sequence barrier *after* the seat/latch
knocks have fired, so the knock that reports the gesture's
end stands at the barrier and lawfully survives the purge,
while everything queued before it is dropped. Pre-latch
backlog never delays the Throw (Law 1's machinery). The
stale-detent rule (Law 3) must never be implemented in a way
that eats this knock: it is an event sound, exempt by kind.

---

## §5 · THE SLIDE — SOUND MAP BY MODE AND WIRE

The slide already carries the wire in its travel (OFF stop
0.65, UNPROVEN ceiling at the catch 0.84, LIVE free) — hard,
semantic, acquitted ("truth you can feel," Fremd). Its sounds
therefore stay **literal**: no WireFeel pitch is applied
anywhere in this section. The map, by state:

| State | Sound | Why it earns its place |
|---|---|---|
| DOWN (any attempt) | Latch engage (S-a) | The attempt exists from this frame; the latch is its opening fact |
| Drag, gate up-cross | Detent 620 Hz (S-b) | The commitment threshold, crossed — the gesture's one detent |
| Drag, gate retreat-cross | Detent 340 Hz (S-b) | The same threshold, uncrossed — pitched down because the act is retreat |
| Drag, ceiling/stop arrivals (UNPROVEN/OFF) | **Nothing new** — FEEL-1's S2 wall knock is the touch filing; acoustically it is the same single knock call, adopted, not duplicated here | A second acoustic wall sound would double one boundary (Law 2) |
| Brush | Dull knock 10 @ 0.55 / 220 (S-c) | "A contact that was not an order" — the family's not-an-order voice |
| Tap → mesh | Nothing at mesh start; the seat spring's knock at 0.97 (S-e) is the tap's first sound | The mesh is machine motion toward a verdict not yet read; sounding its start would promise the verdict |
| Second touch (abort) | FEEL-1's S3 brush knock, one call, both channels | The undo registers in the hand and ear at the frame it happens, not when the cap lands |
| Seat, displayed ≥0.97 | Seat knock 14 @ 0.85 / 320 (S-e) | Travel predicate, in-step, *before* the latch — the last gesture fact before the machine owns the outcome |
| Latch frame, wire LIVE | Nothing further from the slide — the cascade owns the next sound (§4) | The slide's last word is the knock; the proof's first is the Throw |
| Latch frame, wire dead | Refusal sequence (S-d) — merged with S-e per §11 R-2 when they collide | §6 |
| Refusal (any door) | Knock 12 @ 0.70 / 220 → 300 ms hold → return spring (S-d) | §6 |
| Seated / sealed ghost | Silence; touches consumed and dropped (S-f) | A sealed slide that answers pokes teaches poking |
| Sled return (and an adopted grab mid-return) | Silence, by construction (S-f) | The machine tidying up is not an event in the man's life |

Under the Fusion (F18) nothing in this section moves: the
slide keeps its address and its voices; the wheel's lock at
the seal adds no sound — the Throw covers the lock, and a
second "locking" knock at the same instant would be the
doubling disease at the app's most sacred frame.

---

## §6 · REFUSAL AND FAULT — DOES FAULT GET MORE SOUND?

The audit's drawer law (Dens, adopted): **fault gets more
room than health.** The brief asks whether the same holds in
sound. **Ruled: yes — in three currencies, and never in a
fourth.**

1. **Repetition.** A health event sounds once (a detent, a
   knock, a latch). The jam sounds **three** strikes of the
   heaviest knock the family owns (20 ms @ 1.0 / 320 Hz, 90 ms
   apart, C-d). Three is the budget: enough to be unmistakable
   through sleep, few enough to stay a report. Nothing on the
   Deck sounds four times for one fact, and no fault sound
   loops, retries, or nags — the retry is the machine's, and
   it is silent.
2. **Room.** The refusal is knock + **300 ms hold** + return
   spring — the machine dwells on the refusal the way the
   fault sentence (§3 audit) is promoted above the slide and
   the annulment sentence stands 10 s against health's 500 ms
   stability fade. Fault's sounds are longer in the ear for
   the same reason fault's print is bigger on the glass: the
   one moment the machine must not be ignored is the moment
   it says no.
3. **Exclusivity.** At the jam, the legacy queue is purged
   before the strikes (A6) — fault speaks alone, the way the
   Throw does. Health never purges anything; it queues behind
   whatever is playing and accepts Law 3's expiry.
4. **Never loudness, never pitch alarm.** Fault's gains stay
   inside the family ceiling (refusal knock ≈0.43, jam strike
   0.52 — both under the Throw's 0.62 body). Fault sounds are
   *lower and heavier and longer*, never shriller, never
   rising, never siren-shaped. An alarm register would train
   the man to silence the app; a heavy knock trains him to
   read the sentence. The refusal family's 220 Hz floor is
   the register: dull, low, final.

**The health side of the asymmetry, stated so it survives:**
steady health sounds **nothing** — no standing tone, no
periodic proof, no "still blocking" tick (Law 4). Health's
*changes* sound once each: the Throw (proof achieved), the
hand-back tick (wire recovered, C-f — 5 ms @ 0.4, the
smallest sound in the map, deliberately: recovery is good
news and good news whispers on this machine). The distance
between the hand-back tick and the jam's three strikes *is*
the fault/health hierarchy, and it must survive every future
lane: if a fix ever makes a health sound as heavy as a fault
sound, the hierarchy inverts and this file is re-opened.

**Per-door rulings:**

- **Slide refusals** (stop, catch, UP-frame, latch-frame):
  one voice (S-d) at every door — a refusal that changed
  voice by door would be four dialects of *no* (FEEL-1 §8's
  grammar, acoustic form). The latch-frame stutter (seat
  knock at 0.97, refusal knock a frame later) merges per
  FEEL-1's S4: within 150 ms, one heavier close (16 ms @
  0.85 / 180 Hz) replaces the pair — adopted here as the
  acoustic ruling too (R-2, §11).
- **Jam:** the triple strike, above. It is the drawer's
  sound as well as the cascade's (the drawer map, §7):
  no separate drawer-open sound exists or may be added.
- **Annulment / seal refused at its gate:** silent (C-e).
  The jam already spent this attempt's fault budget at
  +500 ms; the annulment at +1500 ms is a consequence, and
  consequences do not re-sound their cause. Its record is
  print (the 10 s past-tense sentence) and the slide's
  silent reset home.
- **Idle wire death** (the wire drops to OFF while he is
  *not* touching the Deck — the one fault edge with no
  gesture to carry it): **one** refusal knock (12 @ 0.70 /
  220 Hz) at the edge, latched until the wire heals (A9).
  Without it, the fault's only announcement is print he may
  not be looking at — the 1 AM case exactly. An edge into
  UNPROVEN at idle sounds **nothing**: absence of proof is
  not an observed death, and unsure must never alarm
  (Law 5's asymmetry — OFF rests on a directly observed
  dead signal; UNPROVEN claims nothing and sounds nothing).

---

## §7 · THE DRAWER (F16) — A ROOM THAT OPENS IN SILENCE

The proof drawer is the audit's replacement for the standing
strip: it exists only while proof is live business
(proving / jammed / faulted / one settle beat), and in health
it is not composed at all. Its complete sound map is three
lines, and the brevity is the ruling:

| Drawer event | Sound | Why |
|---|---|---|
| Opens for PROVING | Silence (§4) | Proving is silent; the drawer's opening is proving's face, not a new event |
| Opens / stands for JAMMED | The jam triple strike (C-d) — already fired at the deadline that opens it | The drawer gets fault's full sound budget *through the jam's voice*; a drawer-open sound on top would double the fault's arrival (Law 2) |
| Opens at idle-fault (wire not LIVE, no live attempt) | A9's single edge knock, on the OFF edge only; silence for UNPROVEN | §6 — the knock is the edge's, not the furniture's |
| Settle beat ends; drawer closes; wire heals | The hand-back tick (C-f), unchanged voice, its trigger moved with the sentence it already serves | Health's return already has its one small sound; the drawer closing must not acquire a second, "shutting" sound — furniture does not sound |
| FIX / RETRY taps inside the sentence | Silence | The tap leaves for a system pane or starts a sweep; the *result* returns through the drawer's own states (jam strikes, edge knock, or hand-back tick). A tap sound here would report the tap, which the finger already knows |

**Killed before birth, by name:** a drawer slide-out
whoosh/knock, a per-cell proof tick as BLOCK/APPS/TIME land,
a "proof complete" chime distinct from the Throw (proof
completing *is* the seal when sealing, and is the hand-back
tick's silence-adjacent nothing when it is only a sweep
healing at idle — a chime would mint a second seal-family
sound and break Law 1's corollary (d)).

Dens's Two-Height Strip (the priced floor if the drawer is
refused at merge) inherits this section unchanged: heights
are print room; the sound map does not depend on which body
carries the fault.

---

## §8 · THE STOP CHAIR AND THE VALVE — ONE VERB, PRICED IN THE EAR

F6 rules the form (one chair, one verb STOP, whole session,
full 72 dp, price in its own words, countdown in the hero).
FEEL-1 §6 specs the press (hub dip physics; era haptics:
firm click in grace, weighted knock priced). This section
rules the **acoustic chain the tap sets off**, including the
overlay the priced tap opens — because today that chain is
where the Deck's sound discipline goes to die.

**The tap itself** sounds FEEL-1's voices, which are engine
calls and therefore sound as they buzz — adopted acoustically
without amendment: grace era = firm click (620 Hz, light,
free, matching a verb that costs nothing); priced era =
weighted knock at the low end (seat-knock family, ~230 Hz —
the price felt and heard before it is read). Zero tokens
remaining: FEEL-1's dull knock, nothing opens. Era crossing
with no finger down: silence — the hero's countdown owns
that moment; a buzz at 10 s from nowhere is an alarm
without a cause.

**The completion must sound like an unlatching, not a
sealing.** Today every Deck-reachable disarm path spends
`playHarmonicSeal(arming = false)` (H-a, H-b, H-g): the
legacy *seal* voice — 48 ms of the Throw's own body family —
fired for the act of *unsealing*, by a function whose name
says SEAL. It performs gravity the act has not earned and,
worse, it blurs Law 1: a man who learns the seal family's
sound as "sessions end" will hear the Throw as an ending
too. Ruled: **every Deck-reachable disarm completion
sounds `playMechanicalLatch(false)`** — 520 Hz, the heavier
release seat, the voice whose semantics are literally
*unlatching* — fired once, and only after the disarm
transaction commits:

- Grace cancel (`confirmDisarmSession` via the chair):
  harmonic seal → **latch-release**, at the same call site.
- Valve completion (`completeEmergencyBreakFromValve`,
  `Completed` branch only): harmonic seal → **latch-release**.
  The `RefusedNoToken` branch stays **silent** — the build-6
  law's exact mirror: no spend, no sound; a refusal that
  sounded like a completion would be the map's worst lie.
- FLOW clean completion (past the 20-min floor, inside
  `requestDisarmChallenge`): harmonic seal →
  **latch-release**, same reasoning — it is a completion
  the repository has accepted, not a seal.
- `confirmDisarmSession`'s other callers ride the same
  function; the swap is at the function, so no Deck path can
  keep the old voice by accident.

**Opening the challenge sounds nothing (H-c KILLED).** The
priced tap already sounded at the chair; the overlay's
appearance is its own report; and a latch at open would
perform a commitment the 60-second hold has not yet made.
The challenge opens in the silence the chair's knock leaves.

**The hold's sixty ticks die (H-d KILLED, A7).** The valve
today fires a latch or firm click **every second** of the
hold, plus a double-fired bank sound at each quarter — over
sixty sounds to report one fact ("still holding") that the
holding finger knows better than the speaker does. This is
Law 4's standing violation and the brief's *performs instead
of informs* in its purest form: it performs endurance. The
hold's surviving sound map:

| Hold event | Sound | Why |
|---|---|---|
| Each second | **Nothing** (KILLED) | Time never ticks (Law 4); the chamber's print fills |
| Quarter banks at 15/30/45 s | **One** pitched detent, stepping down 560 → 470 → 390 Hz (the built pitches, the firm-click twin KILLED) | A bank is a fact the finger cannot feel — held time becoming *kept* time. Descending pitch = the chamber filling; three sounds per hold is the whole budget |
| Release before a bank / drain in progress | Nothing | Draining is the absence of an event |
| Drain reaches the banked floor | Firm click (H-h, KEEP) | "What you banked is safe; you may let go" — a release permission the hand cannot derive |
| Hold completes (60 s) | **Nothing from the view** (H-f KILLED); the VM's latch-release (§8 above) sounds once, after the spend commits | Completion is a transaction verdict, not a view milestone; the view firing first (as today) sounds a success the repository might still refuse |

Priced STOP chain: A7 (valve diet + completion de-voicing)
**S**; the disarm voice swap **S** (four call sites, one
function family, no new voice). Both ride inside F6's lane
if Rost sequences them there; each stands alone.

---

## §9 · RUNNING, FINISH, RECEIPT — THE LONG QUIET

A sealed session runs for hours on a screen visited for
minutes. Its sound law is the inverse of the set gesture's:
**almost nothing, almost never.**

- **Running Deck:** no sound for the countdown, the block
  boundaries, the break's start or end, or END BREAK (R-d).
  The break's arrival is the hero becoming the break (F5);
  a chime at every boundary would be Law 4's violation on a
  50-minute period instead of a 1-second one.
- **Utilities** (DESK CLOCK / HELD): their opening latches
  are KILLED (A8, §1 R-c). The sheets are doors; doors on
  this Deck do not sound — the slide and the chair are the
  verbs, and a latch spent on opening a digest devalues the
  latch that reports an engagement.
- **DONE:** silent (R-a), in idle's eviction and running's
  ghost alike.
- **Finish (the E-clock, R-e):** fenced as built — E0
  settle knock, E700 completion pulse for pulse-lawful
  kinds only, E880 knock pair, fired against the finish
  clock whether or not the Deck is composed. Two fences
  this file adds: (a) the completion pulse may never be
  spent on the Deck for any other act (its E700 spend is
  the voice's sole Deck licence; the tear pulse likewise
  has no Deck licence at all); (b) a death-booked finish's
  empty E700 slot is **silence by law**, not a gap a lane
  may fill with a "consolation" knock — the empty slot is
  how the machine says the shield gapped, and it says it
  by not singing.
- **Receipt and sled return:** silence (R-f, S-f). The
  return's 650 ms travel home is the session's last motion
  and it is mute; F19's shake on a stray scrim tap is
  visual only.

---

## §10 · KILL LIST — SOUNDS THAT PERFORM INSTEAD OF INFORM

Consolidated, each with its executioner. "Kill" means the
call is deleted or never written; none of these is a
volume reduction.

| # | Sound | Why it performs | Disposition |
|---|---|---|---|
| K1 | VM detent under every dial change (D-a) + VM detent under every hub commit (D-b) | Repeats the view's report in a second pitch — applause for a crossing already announced | DELETE both calls; grep-gated (A1) |
| K2 | Valve per-second ticks, ~60 per hold (H-d) | Performs endurance; reports a fact the holding finger owns | DELETE the per-second branch whole (A7) |
| K3 | Valve bank double-fire: detent + firm click together (H-e) | Two voices for one bank — emphasis, not information | DELETE the firm-click half (A7) |
| K4 | Harmonic seal spent on disarms (H-a/H-b/H-g) | Borrows the seal's gravity for unsealing; blurs Law 1 | REPLACE with latch-release at completion only (§8) |
| K5 | Latch at challenge open (H-c) | Performs a commitment not yet made | DELETE |
| K6 | Valve view's completion click (H-f) | Sounds a verdict before the repository returns it | DELETE at the view; VM sounds the verdict (§8) |
| K7 | Utility-sheet latches (R-c) | Dress a door as an engagement | DELETE both Deck-path calls (A8) |
| K8 | Queued detents older than 50 ms | Report positions the wheel has left — the past, performed as the present | DROP at poll (A4) |
| K9 | P7's echo pass-over knock | Reports familiarity; familiarity is not an event | NEVER WRITTEN (§3) |
| K10 | Per-link proving ticks; drawer-open sounds; proof-complete chime | Perform progress with no verb attached; mint a second seal-family sound | NEVER WRITTEN (§§4, 7) |
| K11 | Per-command audio-focus requests under a drag | Not a sound but its tax: ducking his audio dozens of times per gesture performs cost, not information | Coalesce: one focus hold per burst, abandoned after ~500 ms idle (A5, device-verify) |

**Flagged, not killed (outside this cell's Deck):**
`playHarmonicSeal(arming = true)` is spent by ~10 non-Deck
ViewModel paths (loadouts, profiles, routines) on acts that
are not seals. That is the same dialect debt one screen over;
it is other cells' ground and the app-wide seal-voice audit
is Rost's to commission. Recorded here so the Deck's
eviction is not mistaken for the app's cure.

---

## §11 · RECONCILIATIONS FOR ROST

Two places where this file and FEEL-1 touch the same call
and read it differently. Both are stated, neither is hidden:

- **R-1 · WireFeel and the discrete knocks.** FEEL-1 §7's
  table shifts the seat knock's pitch under UNSURE (−60 Hz)
  and replaces it under REFUSED. `playSeatKnock` fires
  haptic + PCM from one call with one pitch argument, so
  the shift cannot be haptic-only without an engine split.
  This file rules the acoustic pitch constant (§3) to avoid
  the refusal-family collision, and reads FEEL-1's terms as
  waveform terms (duration/amplitude). If Rost wants the
  pitch shift in *both* channels, the collision must be
  answered first (the refusal would need a new floor);
  if he wants it in neither, FEEL-1's table loses two
  cells. The detent ladder — the channel both files agree
  carries the class — is unaffected either way.
- **R-2 · The latch-frame stutter.** FEEL-1's S4 merges
  the seat/refusal knock pair into one heavier close
  (16 ms @ 0.85 / 180 Hz) when they land <150 ms apart.
  Adopted here as the acoustic ruling unchanged — one
  item, one price (FEEL-1's S), two channels served. Not
  re-priced below.

**Neighbour debt, flagged unpriced:** `hapticsEnabled` has
no persisted setting and no surface in this tree (sound has
`SoundEffectsSetting`; haptics has a var born true). Whether
the man can ever mute the haptic twins is a PARK/settings
question for another cell; this map assumes both gates as
built.

---

## PRICE LEDGER

Audit prices kept (S = a sitting's edit · M = a lane ·
L = a restructure). Items marked *fence* cost nothing and
forbid something; items marked *adopted* are priced in
their owning file and not counted again here.

| Item | What | Price |
|---|---|---|
| Record | Engine voices, Throw, gate ticks, refusal family, jam strikes, hand-back tick, sled/seal silence — as built, named, fenced | — |
| A1 | De-double the wheel: delete VM detents in `setDialMinutes` + `setStudyBlocksCount`; ESR-family grep gate on both functions | **S** |
| A2 | Majors keep pitch: `playRotaryDetent` gains a duration term (default 6 ms — one engine seam, shared with FEEL-1's rattle question); %15 stops route to the detent at ladder pitch, 10 ms; FLOW coast landing takes the gate knock (W-e unification) | **S** |
| A3 | WireFeel, acoustic half: `WireFeel` from `printState`, passed to the wheel beside `isNightMode`; ladder ×0.72 / flat 240 Hz; knocks unscaled | Inside FEEL-1 F1's **M** (one enum, one lane, two files — never double-priced); standalone increment **S** if the lanes split |
| A4 | Detent perishability: stamp at enqueue, drop detents >50 ms old at poll (event sounds exempt) | **S** |
| A5 | Audio-focus coalescing for the legacy lane (one hold per burst) | **S**, device-verify |
| A6 | Jam exclusivity: purge the legacy queue before the triple strike | **S** |
| A7 | Valve diet: per-second ticks + bank double-fire + view completion click deleted; banks keep one descending detent | **S** |
| A8 | STOP acoustic chain: disarm completions → latch-release (grace / valve / FLOW-floor paths); challenge-open latch deleted | **S** |
| A9 | Utilities silenced: Deck-path latch calls for DESK CLOCK / HELD opens deleted | **S** |
| A10 | Idle-OFF edge knock: one refusal knock per edge into OFF at idle, latched until heal; UNPROVEN edges silent | **S** |
| Fences | Throw bit-identity + t=0-only (Law 1); proving silence; sled/sealed silence; E700's sole Deck licence; time never ticks (Law 4) | *fence* |
| Adopted | FEEL-1 W1/H1/S2/S3 voices; S4 merge (R-2); chair press voices (§8) | priced in FEEL-1 / F6, not here |

**Tests (pure JVM, the established patterns):** the
`WireFeel` mapping (`printState` → class → pitch factor /
flat) as a truth table beside `SlideGestureMath`'s tests;
the perishability predicate (stamp age × sound kind →
play/drop) as a pure function the synth loop consults;
the A1 grep gate in the ESR family (a merge re-adding a
`play*` call to either VM function fails review
mechanically, not by memory). The WireFeel kill criteria
run on device with FEEL-1 §7's pass — one pass, both
channels, Sarthak's hand and ear as the judges.

Nothing here touches DB (v12 stands), the header Guard
line (issue #4's cell), or the seal machinery's verdicts —
only which of its moments may sound, and in what voice.

— FEEL-2 (SOUND), Rost's cell, issue #6. DECLARED,
uncompiled, uncommitted. File: `studio/plan/ISSUE-6-feel-2.md`.
