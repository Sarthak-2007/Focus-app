# Issue #7 — Shield (audit fixes) — Feel Spec: HAUT

**Issue:** Sarthak-2007/Focus-app #7 — “Main-app pass — Shield audit fixes”
**Role:** HAUT, FEEL SPECIALIST 1, in VOGT's issue #7 cell
**Status:** DECLARED — design only. No app code written or edited, nothing
compiled, no tests run. Nobody compiles; Ren compiles only when Sarthak says.
**My slice:** the integrated choreography — how visual state, motion, sound
and touch fire *together* at one moment. Single-channel depth (a voice's
recipe, a curve's mathematics, a waveform's envelope) belongs to the other
feel specialists; everything below is specified only in terms of voices,
curves and durations that already exist, so their deep passes plug into
this score without re-choreographing it.

**Sources read:** `studio/plan/AUDIT-shield.md` in full (R-1…R-13);
`ui/screens/ShieldMatrixScreen.kt` (the issue-7 builder pass: status item
:211–226, `effectiveNight` :145, sled night feed :1040);
`ui/ShieldBlockingStatus.kt` (the four proofs — LIVE / NO_SESSION /
UNPROVEN / STALE — and the exact headlines);
`service/SessionSealTransition.kt` (the seal act; the Throw is its last
statement; dwell 210 ms, jam 500 ms, annul 1500 ms);
`service/TactileSoundEngine.kt` (the Throw: 1,000 ms buffer, bolt at
+30 ms, haptic twin `[0,24,38,16] @ [0,255,0,175]`, tail crossing −46 dB
at 900 ms and fading to silence at 1,000 ms; `playCompletionPulse`,
`playHarmonicSeal(false)`, `playMechanicalLatch`);
`ui/theme/MonolithCurves.kt` (the house vocabulary: durations
{110…520} ms, Curves A/B/C, SEAT = Curve A landing, THROW = dismissal);
`ui/theme/Color.kt` (zero-green tokens; ink `onBackground`, secondary
`onSurfaceVariant`, the one alert tone `error` / TokenAlert);
`MainActivity.kt` (`sealed` :243, `effectiveNight = darkTheme || sealed`
:256); `ui/MonolithViewModel.kt` (the finish clock's E700 completion
pulse, the disarm doors' `playHarmonicSeal(arming = false)`).

---

## 0. The moment I own

The audit's headline (R-5) put a sentence at the top of Shield:
**BLOCKING NOW** / **NOT BLOCKING**. The builder pass landed it as a
plain `Text` that swaps in one frame, in ink if blocking and in the
alert tone for *every* not-blocking state, with no motion, no sound
relationship, and no touch. A fact that important cannot arrive like a
label being re-printed. This spec makes the flip a designed moment:

> **Certainty without alarm. Weight without drama. The wall does not
> announce itself; it settles, and the room goes quiet around it.**

Two named movements, one sub-movement, one non-movement:

| Movement | Trigger | Character |
|---|---|---|
| **THE SETTLE** | proof → LIVE while Shield is composed | dusk completes, the sentence strikes in ink, the Throw decays — three beats, all rest by 1,000 ms |
| **THE RELEASE** | proof → NO_SESSION (lawful end or his own disarm) | the screen exhales: palette dawns back, the sentence re-strikes in a quiet tone, nothing strikes |
| **THE SLIP** | proof LIVE → UNPROVEN or STALE mid-session | one channel dissents (the sentence, in the alert tone); every other channel *holds* — no sound, no touch, palette stays night |
| **THE NON-EVENT** | a proving is annulled, a SAFE BREAK clause comes/goes, the screen is opened onto an already-standing wall | no choreography at all — see §5. Silence is also a specification |

The governing distinction, and the whole reason this slice exists:
**a seal, a re-proof, an ending, a disarm and a proof failure are five
different events that today would all look identical** — one text swap.
Choreography is what keeps them from lying about each other.

---

## 1. Ground truths the choreography must not break

1. **The Throw is frozen law.** One bit-identical 1-second voice, one
   call site (`SessionSealTransition.executeSeal`, its last statement),
   golden-hash pinned. Shield adds **no sound of its own** at any flip.
   My job is synchronisation and restraint, never a second voice.
2. **The sentence is print.** Per the tally's governed law (audit,
   standing charges): a line re-strikes whole, never partially, never
   rolls, never crossfades letter by letter. Every visual treatment
   below respects that — the *line* cuts; the *room* moves.
3. **Palette today is claim-keyed, not proof-keyed.**
   `effectiveNight = darkTheme || sealed`, and `sealed` is the session
   row's `isActive` claim. HAUT's ruling: **this stays.** Night falling
   is the room honouring an *intention* (a session exists); the
   sentence is the *proof* channel. Two channels, two facts, no
   cross-channel lie — provided the night *falls* instead of snapping
   (§2, beat 1) and *holds* through a SLIP (§4).
4. **The colour defect in the landed pass.** The status item paints
   every not-blocking proof in `scheme.error`. A phone with no session
   at 15:00 is not an error; painting it alert trains him to ignore
   the alert tone before the day it matters (UNPROVEN / STALE). The
   tone map in §2/§3/§4 is part of this spec, not a nit.
5. **Zero green, zero overshoot in the status zone.** Certainty is
   carried by ink and night, never a traffic-light colour (the token
   law forbids green outright). `MechanicalOvershoot` and
   `MechanicalAnticipateOvershoot` are banned from everything in this
   spec: a wall that bounces is a wall performing. SEAT only —
   travel lands, never overshoots (MonolithKin).

---

## 2. THE SETTLE — proof flips to LIVE (BLOCKING NOW)

Fires only on a proof *change observed while Shield is composed*
(§5 for the already-standing case). t = 0 is the frame the new
`ShieldBlockingStatus` first composes.

| Beat | Window | Channel | Specification |
|---|---|---|---|
| 1 — DUSK | t−ε → t+520 ms | visual / motion | If the night palette has not already fallen (it falls at the session claim, which precedes proof by the proving window — 210 ms dwell to 1,500 ms annul line), its change is **animated, never snapped**: background, surfaces and the sleds' night feed crossfade over **520 ms, Curve B** (`specB(520)` — the house's slow settle). A lightswitch snap reads as a UI theme toggle; a 520 ms dusk reads as a room changing state. Where the palette already fell during proving, this beat is spent and the Settle is beats 2–3 only — the choreography degrades gracefully, it never double-fires. |
| 2 — STRIKE | one frame at t | visual (print) | The sentence **re-strikes whole**: `NOT BLOCKING — …` out, `BLOCKING NOW — SLEEP SESSION · ENDS 03:30` in, same frame, no fade, no slide, no per-word stagger. Tone: `scheme.onBackground` (full ink). Anonymous Pro Bold 13 sp as landed — the voice does not change, the *certainty* does. |
| 3 — DECAY | t → t+1,000 ms | sound + touch | If this flip is the seal itself, the Throw is already sounding (it fires from `executeSeal` on whatever screen he stands; if he stands here, it is this moment's sound) — its shape *is* the beat: body attack, bolt at +30 ms, haptic strike 24 ms / gap 38 ms / seat 16 ms, room tail crossing −46 dB at 900 ms, exact silence at 1,000 ms. Shield adds **nothing**: no latch, no click, no second haptic. If the flip is **not** the seal (see SILENT RE-PROOF below), this beat is *empty by design*. |

**The first-second contract:** all motion is finished by 520 ms; the
last ~480 ms of the second is pure decay — the picture already still
while the sound finishes settling. By t+1,000 ms every channel is at
rest and in agreement: night room, ink sentence, silence. He should
not be able to say when it “finished.” That is the feel of a wall,
as opposed to a notification.

**SILENT RE-PROOF (a named variant, not an improvisation).** If proof
returns to LIVE after a SLIP — the shield beat resumes, a witness
re-binds — no seal has occurred and the Throw must not sound again for
a session that was never re-sealed (the transition's claim law already
guarantees this; Shield mirrors it in feel). Choreography: beat 2
only, plus the 520 ms Curve B settle if any tone crossfade is needed.
**No sound. No haptic.** A correction is stated, not celebrated.
Sounding an event here would teach him that proof flickers are
sealings — the exact confusion the status sentence exists to kill.

**What the SETTLE never does:** no pulse, breath or loop on BLOCKING
NOW, ever — a standing wall does not throb; throbbing is the alarm
register. No checkmark, no drawing shield glyph, no accent-orange
spend on the sentence (the one accent stays rare; ink is stronger).
No haptic from the Shield composable under any branch — the only
touch in this movement is the Throw's own twin, fired by the engine.

---

## 3. THE RELEASE — proof flips to NO_SESSION (NOT BLOCKING)

Two doors lead here and they share one visual movement; only their
sound differs, and in both cases the sound is **already owned
elsewhere** — Shield adds none.

| Beat | Window | Specification |
|---|---|---|
| 1 — EXHALE | 0 → 420 ms | The night palette **dawns back** over **420 ms, Curve C** (`specC(420)`) — Curve C's long tail is an exhale, not a landing; the room lets go more slowly than it took hold (520 up / 420 down is wrong way round on purpose: taking hold should feel *decided*, letting go should feel *unhurried*, and neither should feel symmetrical-mechanical). Sleds' night feed follows the same tween. |
| 2 — RESTRIKE | one frame at 0 | The sentence re-strikes whole to `NOT BLOCKING — NO SESSION RUNNING` in **`scheme.onSurfaceVariant` (secondary tone) — not the alert tone.** Nothing is wrong; the wall is simply down, as scheduled or as he ordered. This is the tone-map fix of §1.4 applied to the common case. |
| 3 — SOUND (not Shield's) | as fired by its owner | *Lawful end* (timer/window expiry): the finish clock's E700 `playCompletionPulse` — three softening ticks, the machine winding down — is the release's voice, already shipped. *His own disarm / valve doors:* `playHarmonicSeal(arming = false)` from the VM doors, already shipped. Shield's composable fires **no** sound and **no** haptic at this flip under any branch; a second voice here would double-speak doors that already speak. |

Register: relief without celebration. The completion pulse softens
*downward* in amplitude by design (175 → 125 → 80); the visual exhale
matches it — this is the one place the app is allowed to feel like a
held breath let out, because something genuinely ended.

---

## 4. THE SLIP — LIVE → UNPROVEN or STALE mid-session

The most important movement in this spec, because it is the one a
lazy pass would choreograph as an alarm — and an alarm here would be
a lie of proportion: the session still stands, his rules are
unchanged; what failed is *proof*, and proof failures in this app
are usually machinery (a beat gap, a rebind) that heals itself.

- **Sentence:** re-strikes whole, in the **alert tone** — this is
  the *only* not-blocking state that earns `scheme.error`
  (`NOT BLOCKING — SLEEP SESSION RUNNING, ENFORCEMENT NOT PROVEN` /
  `NOT BLOCKING — SHIELD PROOF STALE`). One frame, no flash, no
  blink, no repeat — a printed word, held steadily. A flashing alert
  claims urgency; a held alert claims a fact.
- **Palette:** **holds night.** The session claim is still true, so
  the room stays as the session left it. Dawning back here would
  print “all clear” with the whole screen while the sentence says
  the opposite — a cross-channel lie, the precise defect class this
  audit exists to kill.
- **Sound:** none. **Touch:** none. **Motion:** none anywhere on the
  screen. The SLIP is a change of *testimony*, and testimony changes
  in print only.
- **Recovery** is SILENT RE-PROOF (§2): if the beat resumes, the
  sentence returns to ink just as quietly. A flickering proof at a
  bad-moment boundary may thus toggle the sentence's tone a few
  times; it will never once make a noise, move, or vibrate. Print
  may flicker under a flickering fact — that is honesty. Sound and
  motion may not: that would be the app panicking on his behalf.

---

## 5. THE NON-EVENTS — specified silence

1. **Found on entry.** He opens Shield onto a wall that is already
   standing (the commonest case: routines seal while the phone is in
   his pocket). **No transition replays.** First composition renders
   the settled state directly — ink sentence, night palette, rest.
   A replayed Settle for an event he didn't witness is theatre, and
   theatre spent daily becomes noise he stops believing. (Builder
   shape: the choreography keys on a *change in proof while
   composed*; the initial value is the rest state, not an animation
   source.)
2. **Annulled proving.** A session that dies before t=0 (the
   transition's 1,500 ms annulment) never sealed and must never
   *feel* sealed or unsealed: if Shield showed the UNPROVEN sentence
   during proving, its replacement by NO_SESSION takes the RELEASE's
   print (secondary tone) with **no palette exhale** (night never
   lawfully fell for a wall that never stood — if it fell at the
   claim, it dawns back on the same 420 ms Curve C, silently) and
   **no sound** — the transition already guarantees no Throw; Shield
   adds no consolation voice. A non-event gets non-event feel.
3. **SAFE BREAK sub-flip.** `· SAFE BREAK — PEEK APPS OPEN` arriving
   or leaving inside a BLOCKING headline is a *clause* changing, not
   a state changing: whole-line re-strike only. No tone change (the
   proof is still LIVE), no sound, no motion. If the clause ever got
   the Settle's treatment, a 2-minute peek would feel like a wall
   falling and rising — the fastest way to make him distrust the
   sentence.
4. **The ENDS time never ticks.** The headline's `ENDS HH:MM` is a
   frozen derivation from the session's own numbers; it changes only
   when the underlying fact changes, by re-strike. No per-second
   anything on Shield. Stillness is this screen's resting state.

---

## 6. Cross-channel score — the whole flip at a glance

| Event | Sentence | Tone | Palette | Motion | Sound | Touch |
|---|---|---|---|---|---|---|
| Seal → LIVE | re-strike | ink | dusk 520 ms / B (if not already fallen) | that tween only | **the Throw** (engine's, not Shield's) | the Throw's twin only |
| Re-proof → LIVE | re-strike | ink | holds night | none | — | — |
| Lawful end | re-strike | secondary | dawn 420 ms / C | that tween only | completion pulse (finish clock's) | its own |
| His disarm | re-strike | secondary | dawn 420 ms / C | that tween only | harmonic seal, disarm (VM door's) | its own |
| Proof lost (SLIP) | re-strike | **alert — the only alert** | **holds night** | none | — | — |
| Annulled proving | re-strike | secondary | dawn 420 ms / C if it fell | that tween only | — | — |
| Found on entry | — (initial) | as derived | as derived | **none** | — | — |
| SAFE BREAK clause | re-strike | unchanged | unchanged | none | — | — |

One row, one personality. No two rows share a full signature, and no
row borrows another's sound — that is TELL-THE-TRUTH applied to feel:
each event is recognisable with the screen face-down (sound/touch
rows) or across the room (palette rows) or in one glance (sentence
rows), and none can be mistaken for a different fact.

---

## 7. Semantics & reduced motion (rides audit R-11)

- The status item is a **live region**: on any proof change while
  composed, TalkBack announces the new sentence exactly once, as
  printed. The palette tweens are decoration and are never described.
  An announcement is the aural Strike — it obeys the same law as the
  visual one: whole sentence, once, no repetition on re-proof
  flicker beyond the actual state changes.
- **Reduced motion** (system animator scale 0 / remove-animations):
  both tweens collapse to hard cuts — dusk and dawn become instant,
  as the sentence already is. Sound and touch are **unchanged**:
  they are the event itself, not its clothing, and the Throw's
  haptic twin must survive every accessibility setting that leaves
  haptics enabled at all (the engine's S0 law: muting loses timbre,
  never the event).
- Haptics-off / sound-off users lose no *information*: every event in
  §6 remains fully specified in print and palette alone.

---

## 8. Anti-laws — what no future pass may add to this moment

1. No Shield-local seal or unseal sound. Ever. (Frozen Throw, §1.1.)
2. No pulse, shimmer, or breathing on BLOCKING NOW.
3. No overshoot / anticipation curves in the status zone; SEAT and
   the two named tweens only.
4. No green anywhere near this sentence — certainty is ink.
5. No alert tone for NO_SESSION; alert is UNPROVEN/STALE's alone.
6. No replayed transitions on entry, tab return, or recomposition.
7. No haptic fired by the Shield composable at any proof change.
8. No partial-line animation of the sentence (no word-by-word
   reveals, no typing, no digit rolls — the print law).

---

## 9. Price & builder notes (DECLARED — no code written by HAUT)

Small; one screen, no engine, no schema, no new voices or curves.

- **Core (≈½ builder-day):** in `ShieldMatrixScreen.kt` — (a) the
  tone map on the existing status item (ink / secondary / alert per
  proof, §1.4); (b) previous-proof memory in the composable so the
  choreography keys on *observed change* only (§5.1); (c) the
  background/sled night feed driven through `animateColorAsState` /
  an `Animatable` on `specB(520)` up and `specC(420)` down instead of
  the instant `effectiveNight` boolean swap; (d) live-region
  semantics on the status item (rides R-11's semantics pass).
- **Watch item, not a blocker:** the sleds are AndroidViews fed a
  boolean `isNightMode`; if crossfading *inside* the sled view is
  costly, land the tween on the Compose surfaces (background,
  headers, rows' containers) first and let the sleds cut at the
  tween's midpoint frame — a 260 ms offset inside a 520 ms dusk is
  below perception as a mismatch, and the spec's contract (§2,
  first-second) still holds. The single-channel motion specialist
  may overrule this with a costed alternative.
- **Sync honesty:** Shield learns LIVE from the witness flow, which
  may land a frame or a beat after the Throw fires from the Deck
  door. The choreography is deliberately keyed to the *state change
  as composed*, not to the Throw's clock, so any such offset reads
  as one event (overlapping envelopes), never as two. Do not attempt
  frame-locking to the seal; it buys nothing perceptible and couples
  the screen to machinery it should only witness.
- **Verification (device, at Ren's build):** (1) arm from the Deck
  while standing on Shield — dusk, strike, Throw, rest by 1 s;
  (2) kill the shield service mid-session — sentence alone dissents,
  room holds night, silence; restore it — quiet re-proof; (3) let a
  session end on-screen — exhale + completion pulse, secondary tone;
  (4) cold-open Shield during a routine session — settled state, no
  replay; (5) animator scale 0 — all cuts, sounds intact.

---

## 10. What I want next / unassigned

- The Deck's arm cascade and Shield's Settle are two views of one
  event; a joint pass (HAUT + the Deck's feel owner) should confirm
  the 210 ms dwell reads as one held breath across both screens,
  not two waits.
- The SLIP's held-alert print deserves a wording look from the truth
  seats: `ENFORCEMENT NOT PROVEN` is exact but long at 1 AM; if a
  shorter true word exists, the choreography is indifferent to it —
  print is print. I do not change the words here; they are
  `ShieldBlockingStatus.kt`'s, already derived and tested.
- Unassigned and offered: the same five-event score (§6) generalised
  as the template for every future status flip in the app (PARK's
  sniper/blocker states are the obvious next tenants — audit R-9
  moved their switches there; their feel is currently a costume
  button, per Vega S-4).

— HAUT, FEEL SPECIALIST 1, VOGT's issue #7 cell. DECLARED, not built.

---

## Appendix — draft GitHub comment for issue #7 (for VOGT to post)

— HAUT (FEEL), for VOGT's issue #7 cell.

The integrated feel spec for the Shield status flip is filed at
`studio/cells/issue-7/feel-haut.md` (DECLARED — design only, no code,
nothing compiled).

The headline: BLOCKING NOW must not arrive like a re-printed label.
The flip is choreographed as five distinguishable events, because
today all five would look identical — one text swap:

- **THE SETTLE** (seal → LIVE): the night palette *falls* over
  520 ms Curve B instead of snapping, the sentence re-strikes whole
  in full ink (print law — the line cuts, the room moves), and the
  only sound/touch is the seal Throw itself, fired by
  `SessionSealTransition` as always. Shield adds no voice of its
  own — the Throw is frozen law. All motion rests by 520 ms; the
  Throw's tail decays to silence at 1,000 ms. Certainty without
  alarm, weight without drama.
- **THE RELEASE** (lawful end / his disarm): palette dawns back
  over 420 ms Curve C, sentence re-strikes in the *secondary* tone —
  and a defect is filed with it: the landed pass paints every
  NOT BLOCKING state in `scheme.error`. No-session is not an error.
- **THE SLIP** (proof lost mid-session → UNPROVEN/STALE): the only
  state that earns the alert tone. Sentence alone dissents; palette
  holds night, no sound, no haptic, no motion — dawning back would
  print “all clear” while the sentence says otherwise.
- **SILENT RE-PROOF**: proof returning is stated, never re-sounded.
  The Throw must not fire twice for one seal, in feel any more than
  in code.
- **NON-EVENTS specified as silence**: no transition replays when
  Shield is opened onto an already-standing wall; an annulled
  proving gets no seal or unseal feel; the SAFE BREAK clause changes
  by re-strike only, so a 2-minute peek never feels like the wall
  falling.

Anti-laws pinned: no pulse on BLOCKING NOW, no overshoot in the
status zone, no green, no Shield-local seal sound, no composable
haptics at proof changes, no partial-line animation. TalkBack gets
the flip as a live-region announcement of the sentence itself;
reduced-motion collapses the tweens to cuts and leaves sound/touch
untouched. Priced small (≈½ builder-day, one screen, existing voices
and curves only), with device verifications listed in the spec.

— HAUT
