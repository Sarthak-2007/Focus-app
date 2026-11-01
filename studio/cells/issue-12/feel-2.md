# FEEL-2 — Sound — Issue #12: Tasks + F-1

**Issue:** Sarthak-2007/Focus-app #12 — Tasks + F-1
**Role:** Feel-2 · sound (issue #12 cell)
**Status:** DECLARED — sound spec only, uncompiled, no code written, no Gradle/tests run
**Sources:** `studio/plan/AUDIT-tasks.md` and `studio/plan/PLAN-issue-12-tasks-f1.md`
in the cell clone (branch `agent/issue-12`), plus the trunk sound machinery
read directly: `TactileSoundEngine.kt`, `SessionSealTransition.kt`,
`SoundEffectsSetting.kt`, `RoutineHeartbeat.kt`, `FocusSequencerScreen.kt`,
`MonolithViewModel.kt`, and the two test files that pin the seal's voice
(`SealThrowIdentityTest`) and the sound fact (`SoundAndSocialTruthTest`).

— Feel-2 · sound (issue #12 cell)

---

## 0. The governing law — one fact, one switch, no bypasses

Every sound in this filing obeys the sound truth batch-9 / Truth Car 3
already built. This filing adds no new setting and no second switch.

**The fact.** Sound on/off is ONE persisted boolean:
`SoundEffectsSetting` — SharedPreferences `monolith_sound_effects` /
`sound_effects_enabled`, default ON, written with `commit()` (never
`apply()`), and pushed to every live engine in the same call
(`pushSoundEnabledToLiveEngines`). Every `TactileSoundEngine` is *born*
from the fact at construction, and the seal transition re-reads it at
bind time (`SessionSealTransitions.get` → `resyncSoundFromPersistedFact`).
That second lock exists because of the exact failure this cell must never
recreate: a heartbeat-born process once bound a fresh default-ON engine
while PARK printed OFF — PARK could print OFF while a routine seal
sounded. The PARK drawer's `SOUND EFFECTS` ON/OFF print is this same
fact, not a copy of it.

**What this means for every sound below — builder law, not preference:**

1. **Route everything through `TactileSoundEngine`.** Its paths are the
   gate: `enqueueTransient` returns early when `soundEnabled` is false,
   the synth loop drops commands when false, `playSealThrow` skips its
   audio when false. A sound implemented through any other door —
   `ToneGenerator`, a notification/ringer-stream player, a raw audio
   file, a View click sound — is a truth violation by construction,
   however good it sounds: it would play while the setting says OFF.
2. **OFF means zero audio. No exceptions, no "important" sounds.** There
   is no seal-override, no queueing-override, no 05:00-override. If he
   set OFF, the 05:00 FASTED seal is audio-silent and that silence is
   the app telling the truth about his own setting.
3. **Haptics are not sound, and are not governed by this fact.** The
   engine's haptic gate (`hapticsEnabled`) is separate, and the house
   law already filed in `playSealThrow` is: *with sound off, haptic
   only — muting loses timbre, never the event.* This filing applies
   that law uniformly: under sound OFF, each event below keeps exactly
   the haptic twin it is specced with, and loses all of its audio.
4. **No per-sound toggles, no volume sliders in this pass.** One fact,
   one switch (PARK → SOUND EFFECTS). Per-sound switches would multiply
   the truth surfaces that have to agree; that is the UNA-03 bug shape
   wearing a settings screen.
5. **The toggle's own confirmation stays as built:** turning ON plays
   its detent *after* the fact already says ON; turning OFF plays
   nothing. A sound fired after OFF is committed would lie in the act
   of confirming silence. Do not "fix" the asymmetry.
6. **No clock-based night mode.** Nothing below ducks, softens, or
   changes character because it is 1 AM or 05:00. The night voices are
   quiet *at every hour* — quietness is in the voice, not in a schedule.
   A voice that changes by the clock can never be learned, and a
   schedule can be wrong; a signature cannot.

---

## 1. The day's sound map, before the detail

| When | Event | Sound |
|---|---|---|
| 20:00 | SLEEP window opens, session seals in | The Throw (S-2) — existing, unchanged |
| Night (~1 AM) | Typing in the composer, opening Tasks, scrolling the pad | **Silence** (S-5) |
| Night | Composer ADD — a leaf joins the foot | One quiet knock (S-3) |
| Night | FIRST key / CUE — a leaf becomes the head | The settling knock (S-3) |
| Night | Sub-step tap — strike / un-strike | The tear-tick, two weights (S-4) |
| 03:30 | SLEEP stand-down, window ends | **Silence** (S-6) — ruled, not omitted |
| 05:00 | FASTED BLOCK seals, intention from head leaf (F-1) | The Throw (S-2) — bit-identical, unchanged |
| Block's end | DONE — TEAR on a leaf | The tear (S-1) |

Two silences on that map are decisions, and §6–§7 argue them. Everything
else is one voice per act, learned once, the same at noon and at night.

---

## 2. S-1 — THE TEAR (the tear's own voice: paper, not a UI click)

**Filed as required — event, character, duration/envelope, off-switch:**

- **Event:** the TEAR tap on a leaf (`DONE — TEAR`, audit item 13 /
  fix F-7), and — in the next tear build only — the completion instant
  of ZIP-TEAR at its ruled 85% crossing. Nowhere else. The tear books
  at the tap (G-01, acquitted): completion is dispatched *first*, and
  this sound fires after the booking, over the already-booked fact,
  alongside the ghost's motion (260 ms today; 300 ms under the F-7
  respin). A tap that does not tear — sealed leaf, ghost re-tap —
  makes **no** tear sound. Sound after a failed act is a lie.
- **Character:** dry, close fibre giving way — paper, in three audible
  facts that separate it from every neighbouring voice:
  - **Not a click.** The rotary detent (680 Hz, 6 ms sine ping) and the
    firm click / mechanical latch family are struck-metal voices: one
    clean pitch, smooth decay. A tear has no clean pitch. Its energy
    is *noise with a low body*, and its decay is *uneven* — fibres give
    in small groups, not in an exponential curve.
  - **Not the completion pulse.** `playCompletionPulse` (185 Hz, 64 ms,
    chord 1.33, three softening ticks) is the machine winding down —
    that voice belongs to session finish machinery and must never be
    spent on a leaf (the ViewModel's own merge ruling already removed a
    seal-family misspend from this act for the same reason). The tear
    is *his hand finishing a task*, not the machine completing
    anything.
  - **Not the seal.** No room, no reverb tail, no second-scale
    presence. The tear happens at arm's length, in the hand; the seal
    happens to the room (§3). Dry and short is the whole distinction.
- **What exists today, and what changes:** `playTearPulse` is the right
  seed in the right place — 240 Hz, 40 ms, gain 0.68, chord 1.21
  against the completion pulse's 1.33, haptic `[0, 8, 14, 8]` /
  `[0, 160, 0, 110]` — directionally a tear (lower, shorter, a
  different chord, a two-part ripping haptic) but timbrally still the
  sine-partial family: heard alone, it reads as an electronic blip
  wearing a tear's name — the acoustic twin of the flaw Weiss measured
  in the tear's *motion* ("a fade in a tear's name", audit item 13).
  **The builder change is grain, not place, rank, or loudness:**
  rebuild the tear's audio as a *deterministic filtered-noise rip*
  using machinery the engine already proves in the seal's bolt layer
  (the frozen-seed xorshift32 noise source through a one-pole lowpass —
  no new synthesis technique enters the app for this):
  1. **Onset rupture, 0–~8 ms:** a brief broadband snap as the
     perforation first gives — noise lowpassed high (≈2–3 kHz),
     attack ≤2 ms, the sound's peak lives here and nowhere later.
  2. **Rip body, ~8–55 ms:** the lowpass drops to ≈900 Hz–1.2 kHz (the
     bolt's register) and the envelope decays *in 3–5 small steps* —
     deterministic amplitude notches from the same frozen PRNG, one
     per fibre-group — over an overall fall with τ ≈ 15–20 ms. A
     smooth sine decay here is a fail: smoothness is what makes it a
     blip.
  3. **A quiet sine body underneath** at the existing 240 Hz, gain well
     under the noise peak, so the voice still translates through a
     small phone speaker that underplays noise texture. This is a
     carrier, not the character.
  4. **Tail, ~55–90 ms:** last fibres — noise falls to silence by
     **≈90 ms total**, final 5 ms faded linearly to exact zero (the
     bolt layer's anti-click precedent). No room layer, ever.
  - **Total duration ≈90 ms** — longer than today's 40 ms blip,
    deliberately: it must be heard *starting* the ghost's 260–300 ms
    motion and finishing well inside it, the way a real tear's sound
    ends before the leaf has finished falling.
  - **Peak level:** no louder than today's `playTearPulse` (gain 0.68
    stays the ceiling; the engine clamp is untouched). The tear is the
    ritual's one celebration — celebration here means *texture*, never
    volume.
  - **Determinism:** the render is input-free — frozen seed, no clock,
    no `Random`, no leaf-dependent parameter. Every tear sounds the
    same on every leaf, every day. Real paper varies; an app voice is
    a signature, and a varying signature reads as different events.
    When the builder's first render lands, pin its hash by Siegel's
    method (the seal golden hash's precedent) so later drift turns a
    test red instead of shipping silently.
  - **Haptic twin:** keep today's pattern exactly — `[0, 8, 14, 8]` ms
    at `[0, 160, 0, 110]`: first fibres, a true gap, the parting. It
    already has the tear's shape; only the audio grain was wrong.
- **Off-switch:** SOUND EFFECTS OFF → zero audio (the transient is
  never enqueued); the haptic twin still fires, under the separate
  haptics gate (§0.3).

---

## 3. S-2 — THE SEAL AT 05:00 (the Throw, unchanged — present, never startling)

- **Event:** the FASTED BLOCK routine seal at 05:00 (window
  05:00–08:00, seed `startMinutes = 300`), whose intention F-1 now
  takes from the head leaf — and identically every other seal: a hand
  arm through the Deck, the DAY BLOCKS seal, the SLEEP seal-in at
  20:00. One event class, one sound.
- **Character — the ruling is: DO NOT TOUCH IT.** The seal has one
  voice, the Throw, and it is already the sound this section would
  have had to invent:
  - **It is a proof, rendered once and bit-identical.** 44,100 samples
    / 1,000 ms, a pure function of compile-time constants and a frozen
    seed, pinned by `SEAL_BUFFER_GOLDEN_SHA256`
    (`d204b95a…bcde4`). Sample 0 is the attack — zero pre-silence — so
    the sound *is* at t=0, the seal instant, never an approximation
    of it. `playSealThrow()` occurs exactly once in production code,
    as the last statement of `SessionSealTransition.executeSeal`
    (claim → seal the row → commit the marker → publish → Throw). A
    seal that cannot complete does not sound; a seal recovered from
    its marker is adopted *silently* and never re-sounds — the code's
    own words: a Throw then "would be a haunting, not a proof."
  - **Present:** a full second, not a tick. Low body (360 Hz
    fundamental), a bolt layer entering at +30 ms, and a room tail
    that sustains to −46 dB at 900 ms before fading linearly to exact
    zero. If his attention is on his breath when the attack lands, the
    tail is still there when it returns. He does not have to be
    listening *at* it; he has a second in which to notice it, from
    another room posture, eyes closed, phone face-down — and its
    haptic twin (`[0, 24, 38, 16]` / `[0, 255, 0, 175]`, with the true
    gap in the middle) carries it through a surface if the phone rests
    on one.
  - **Never startling:** startle is sudden *high* energy, at *high*
    level, *repeated*. The Throw is none of the three. Its dominant
    energy is low; the bolt is lowpassed at 900 Hz and sits −6 dB
    under the body; the one piercing partial (1,692 Hz) rides the fast
    tail decay and is texture in the first tens of milliseconds, never
    a sustained tone. Its gain (0.62, clamp ±0.95) is the same level
    he hears from a daytime hand-arm he chose himself — at 05:00 it
    is a *familiar* sound arriving, not a new one demanding decoding,
    and familiarity is the strongest anti-startle there is. And it
    fires **once**: no retry chime, no reminder, no escalation. The
    proving machinery's failures (jam re-dispatch, annulment) are
    silent — the machine never cries wolf in this register.
- **Duration/envelope, for the record (builder: verify, never
  re-derive):** attack at sample 0; body decay pair
  exp(−0.0114)/exp(−0.0313) per sample; bolt support exactly samples
  [1323, 3749) (+30 ms to +85 ms), τ 18 ms, 2 ms onset, 5 ms end fade;
  room from +18 ms pre-delay, wet send −12 dB; tail crosses −46 dB at
  sample 39,690 (900 ms) and fades linearly to zero at 1,000 ms.
- **Forbidden at 05:00 — each of these breaks the proof, not just
  the mood:**
  1. **No quieter/softer "meditation" variant, no ducking, no
     delay.** At 05:00 he is at the end of his meditation hour or in
     the first minutes of fasted study — hands off the phone either
     way. A variant would make routine seals and hand seals differ,
     killing bit-identity; a delay would move the sound off t=0 and
     make it *lie about when the seal landed*. The Throw's restraint
     is already built into its spectrum and its single firing.
  2. **No pre-announcement, no double sound.** Nothing sounds before
     the Throw "to warn him it is coming" — see the echo ruling (§8).
     The prime at latch purges pre-latch backlog from the queue
     precisely so nothing can sound ahead of, or delay, the Throw.
  3. **No intention-dependence.** F-1 changes the intention *string*
     only. The render takes no provenance, no routine name, no title,
     no queue state: an empty-queue FASTED seal (routine-name
     fallback) sounds bit-identically to a queue-named one, and two
     routines in one morning each sound the same Throw at their own
     seal. The sound proves *sealing happened*; it never grades
     *what* was queued.
  4. **No escalation if unheard.** If he is in the bath and misses it
     entirely, that is lawful: the Throw is proof for a present ear,
     not a summons. The printed word — `N TASKS · SEALED`, the solid
     perforation overprint — waits for his eyes. The app never
     repeats or raises a seal to make sure it was heard.
- **Off-switch:** SOUND EFFECTS OFF → `playSealThrow` fires its haptic
  twin and zero audio, on every arm path — guaranteed by the fact
  being re-read at bind time even when the heartbeat, not the UI,
  built the engine (§0). OFF is never overridden for a seal.
...[truncated 16868 chars]
---

## 4. S-3 — QUEUEING AT NIGHT (ADD and FIRST: quiet knocks, one voice per act)

The night errand (Muede's 1 AM walk, audit items 2/14, fix F-2): write
tomorrow's first task, put the phone down, sleep. Queueing sounds must
confirm the hand without waking the man — or anyone else in the room —
and they must be beneath notice at noon too (§0.6).

**A defect found on the way in — ADD currently speaks twice.** The
screen's `submitTask()` fires `playMechanicalLatch(true)`
(FocusSequencerScreen) and then `onAddTask` →
`MonolithViewModel.addTaskToQueue` fires `playRotaryDetent(680)` — a
latch and a detent stacked on one act, from two doors. One act, one
voice is the house law everywhere else (the Throw's single call site;
the break boundary's single owner). **Cure, priced inside F-2:** the
ADD voice lives in exactly one place — the ViewModel's add path, so
every door that adds a task speaks identically once — and the screen's
latch call is deleted with the old composer. Builder acceptance: one
ADD tap enqueues exactly one transient.

- **S-3a — ADD (a leaf joins the foot of the queue).**
  - **Event:** a successful composer ADD — the recomposed composer's
    end-key on the blank leaf at the pad's foot (F-2), including via
    the keyboard's Done action, which is the same submit.
  - **Character:** ONE quiet knock — a leaf set down on a pad, a
    knuckle's weight on a desk, once. Wood and paper register: low,
    round, unpitched-feeling. Explicitly *not* the voices it replaces:
    not the latch (two-stage struck metal, 760 Hz, gain 0.54 — a
    machine engaging, far too awake for this act) and not the detent
    (680 Hz ping — a UI click). The builder's route is the existing
    seat-knock family (`playSeatKnock`) at its quiet end — the pawl
    knock is already the app's low knock voice, parameterised by
    weight; ADD takes its lightest lawful weight rather than a new
    synth.
  - **Duration/envelope:** pitch ≈300 Hz; a soft attack (≈2 ms — a
    knock, not a click's hard edge); single decay, no second contact,
    no tail worth the name: zero by ≈40 ms. Gain at the family's low
    end (≈0.3) — audible at arm's length in a silent room, inaudible
    as an *event* from the next pillow. Haptic twin: one light tick,
    ≤8 ms at low amplitude (≈100) — felt in the holding hand,
    confirming the leaf landed before his eyes have to check.
  - **Repetition:** rapid successive ADDs (he queues several leaves)
    each knock once. At ≈40 ms per knock and human tap rates, knocks
    never stack into a chord; no lockout, no batching, no "queue
    full" sound is added.
  - **Off-switch:** SOUND EFFECTS OFF → zero audio; haptic tick only.
- **S-3b — FIRST (a leaf becomes the head) — and CUE, which is the
  same act.**
  - **Event:** the composer's FIRST key landing a new leaf at the
    head (F-2), and CUE on an existing leaf making it the head
    (audit item 14's split verb: CUE = make this leaf the head,
    STAY on Tasks). Two doors, one act — *a leaf becomes the head* —
    so one voice for both. (START is not this act: it arms via the
    Deck, and its sounds belong to the arm cascade and the Throw,
    not to this filing.)
  - **Character:** the ADD knock, *settled*: the same knock family,
    lower (≈260–280 Hz) and a touch firmer, followed ≈80 ms later by
    a second, half-weight contact — a leaf placed on top of the pad,
    then the palm's settle that says *this one is on top*. Two
    contacts, unmistakably one gesture. It must stay a *placing*
    sound: never the latch's engagement, never anything from the
    seal family (a head leaf is not a seal — the morning's seal is
    still hours away and has its own voice), and never a success
    figure of any kind. Nothing has succeeded; a leaf has been
    placed.
  - **Duration/envelope:** first contact as S-3a at ≈+6 dB over ADD's
    weight; gap ≈80 ms of true silence; second contact ≈half the
    first's gain, zero by ≈160 ms total. Haptic twin: the same
    two-beat shape in miniature — a light tick, a gap, a softer tick.
  - **Order law (G-01's house order, as with the tear):** the knock
    fires only for a promotion that actually takes. The existing
    guard stands — `promoteTaskToActive` refuses during a proving
    seal (`midSeal()`) *before* any sound — and that refusal is
    silent. A FIRST knock over an un-promoted leaf would be the
    queueing version of sounding an unsealed seal.
  - **Off-switch:** SOUND EFFECTS OFF → zero audio; haptic pair only.

## 5. S-4 — THE SUB-STEP STRIKE (the tear's own tick, at a step's size)

Audit item 10 / fix F-6: the strike's full-width tap target is a 1 AM
trap because it toggles *silently* — and today the ViewModel papers
over that silence with a rotary detent (640 Hz), a UI click doing a
paper act's job. The audit offers the strike "the tear's own sound
tick"; this section rules what that means, and takes the audit's
first branch (the tick), not the narrowed target — a narrowed target
is a precision demand on a half-asleep thumb, and the tick cures the
actual flaw, which is the *unconfirmed hand*.

- **Event:** a sub-step tap toggling its strike, in either direction,
  on an unsealed leaf. Under a seal the toggle is absent (F-5), so
  this sound *cannot* fire under a seal — its absence there is part
  of the seal's silence, and any strike sound from a sealed leaf
  would be a verb firing through the seal.
- **Character:** the tear's first slice — the same filtered-noise
  grain as S-1 (onset rupture + the first fibre steps), *not* the
  full rip. Kin, not equal: striking a step is nicking one fibre
  line on the leaf, tearing is parting the leaf. If the strike spent
  the full 90 ms tear, the ear could no longer tell a step from a
  finished task — the tear must stay the queue's only celebration,
  and the strike must stay a pencil-mark with a sound. The current
  detent is deleted from this act.
- **Duration/envelope:** strike **on**: the tear grain truncated —
  attack ≤2 ms, peak in the first 8 ms, zero by ≈30 ms, at ≈0.6× the
  tear's peak gain. Strike **off** (un-striking): the same grain at
  ≈0.35× gain, zero by ≈20 ms — a *lift*, lighter in the hand.
  Direction is carried by weight alone: no reverse-played sound, no
  second pitch, nothing that has to be decoded. Haptic: one light
  tick (≈6 ms) on the strike; none on the un-strike — the hand feels
  the asymmetry the ear hears.
- **Off-switch:** SOUND EFFECTS OFF → zero audio both directions;
  the strike's haptic tick only, as specced.

---

## 6. S-5 — THE NIGHT SILENCES (queueing acts that make no sound)

Ruled silent, deliberately — each silence protects the errand:

- **Typing in the composer.** No per-keystroke sound from the app,
  ever. His keyboard's own sounds are the system's business; Monolith
  adds none. A man writing on paper hears the paper, not the pen's
  commentary.
- **Opening Tasks, scrolling the pad, focusing the blank leaf.** No
  arrival sound, no scroll ticks on this screen's night errand. The
  screen is a pad left on a desk; picking up a pad is silent.
- **A blank ADD.** Tapping ADD/FIRST with an empty or whitespace
  title books nothing and sounds nothing — the existing guard
  (non-blank check before any sound) is law, kept in the recomposed
  composer. No error buzz, no refusal knock at 1 AM: a half-asleep
  fumble is answered by the leaf simply staying blank, not by
  punishment. (This is not the navigation-refusal knock's case; that
  grammar belongs to locked navigation, not to a blank form field.)
- **DEL's undo window expiring, the undo banner appearing or
  dismissing.** Furniture moving is silent; the banner is print.
- **Every F-1 machinery act.** The seal-instant head read, a queue
  reorder landing in the repository, the preview re-deriving — data
  moving is silent. The only sounds in this filing are sounds of
  *his hand* (S-1, S-3, S-4) and the one sound of *the seal*
  (S-2). Nothing else in the app has earned a voice on this screen.

## 7. S-6 — THE SLEEP STAND-DOWN AT 03:30 (ruled: SILENCE)

- **Event:** the SLEEP window (20:00–03:30, seed `endMinutes = 210`)
  ends; the heartbeat finds the active SLEEP session past its window
  end and stands it down — `deactivateRoutineSession`, unstamped,
  "a plain deactivation, never a judgement" (J2, RoutineHeartbeat
  step 2; PLAN-issue-12: SLEEP rows are never finalised/stamped and
  seal nothing new).
- **Character: none. Duration: zero. This is a ruling, not an
  omission** — three independent reasons, any one sufficient:
  1. **No act happened.** A stand-down is not a seal, not a finish,
     not a judgement — that is J2's whole content. Every sound in
     this app is the sound of an act; a sound here would fabricate
     one ("you slept" as a verdict — Monolith is a blocker, not a
     tracker, and it does not grade his night).
  2. **Its timing cannot be trusted to the minute.** The stand-down
     executes when the reconcile funnel next runs past the window
     end — chain alarm, boot, an opportunistic wake. A sound whose
     instant can drift is useless as a wake signal and worse than
     useless as an ambience: it is a random noise somewhere in his
     last sleep or after he is already up and in the bath. The app
     must never let a drifting housekeeping tick wear an alarm's
     clothes.
  3. **He wakes himself.** 03:30 is his anchor, kept by his own
     discipline; Monolith is not his alarm clock and must not train
     him to wait for its voice to start the day. The morning's first
     Monolith sound is the 05:00 Throw — the seal, an act — arriving
     after bath and into meditation/study, exactly where S-2 puts it.
- **The contrast that proves the rule:** the SLEEP *seal-in* at 20:00
  **does** sound — the Throw, bit-identical (RoutineHeartbeat step
  3b: "the SLEEP 20:00 seal sounds, bit-identically"). A seal is an
  act of holding and speaks; a stand-down is the guard stepping back
  in the night and does not announce itself. Opening speaks, release
  is silent — the same asymmetry as the sound toggle itself (§0.5).
- **Forbidden:** no "reverse Throw", no disarm variant
  (`playHarmonicSeal(arming = false)` and kin) on this path, no
  soft wake chime, no haptic either — a buzz on the nightstand at an
  unguaranteed minute is the same violation in another medium.
  Builder acceptance is a negative: the stand-down path contains no
  engine call of any kind. It contains none today; keep it that way.
- **Off-switch:** not applicable — there is no sound to switch off,
  under ON or OFF alike. That invariance is the point.

## 8. RULING — Feel-5's "echo" is refused at 05:00, absorbed at night

Feel-5 proposes one identical tick-haptic fired exactly twice a day:
when the head leaf is set at night (*it is set*), and identically
when the FASTED block seals at 05:00 (*it held*), "before any other
morning signal". As sound seat, I rule on its acoustics:

- **The 05:00 half is REFUSED.** The seal already has a morning
  voice — the Throw — and it is not an empty slot waiting for a
  ceremony: it is a bit-identical proof whose firing is the seal
  act's last statement at t=0. An echo tick *before* it would sound
  ahead of the proof (and the prime's queue-purge exists to prevent
  exactly that), a tick *with* it would be a second, unpinned voice
  mixed into a golden-hash render, and a tick *instead of* it would
  unmake the one-voice law that hand and routine arms share. There
  is no lawful place for it. *It held* is already perceptible
  without checking — that is what a one-second, room-tailed,
  haptic-twinned Throw is *for*.
- **The night half is ABSORBED, with its semantics changed.** What
  the night act needs is not a vow-mark but a confirmation of the
  hand, and S-3b already gives the head-landing act exactly that —
  with two deliberate differences: the FIRST knock fires *per act*,
  whenever the act happens (not once a day, not on leaving the
  screen), and it carries no ceremonial meaning the ear must learn.
  Sounds in this app confirm acts; they do not keep vows. The
  keeping is heard once, at 05:00, in the Throw alone.
- What Feel-5 got right stands and is adopted: an *unfelt* sync would
  stop being believed. The crossing *is* felt — at the hand at night
  (S-3b) and in the room in the morning (S-2) — by two voices that
  are different because the two acts are different.

## 9. Boundaries — what this filing does NOT re-sound

Named so no builder reads silence as licence to redesign them here:

- **DEL** (latch, disengaging) and **the undo banner's verbs**:
  untouched this pass. One flag for Ren's machinery queue, not this
  cell's price: UNDO today plays `playHarmonicSeal(arming = true)` —
  a seal-family voice spent on a task act, the same misspend class
  the ViewModel's own merge ruling already struck from task
  completion. When that is cured, the restored leaf should take
  S-3a's knock (a leaf set back down), never another seal.
- **REOPEN** (last-finished line): silent today, stays silent —
  correcting a mistaken finish is record-keeping, not an act with a
  voice.
- **START / CUE's current trunk sounds** (harmonic seal on the
  cue-and-navigate path): the audit splits the verb (item 14); the
  moment CUE becomes stay-on-Tasks head-making, it takes S-3b's
  voice per §4. START's arm-path sounds are the Deck cascade's
  business, ending in the Throw — not re-specced here.
- **HAND-SPLIT's line-add** (F-6, verify-only per the PLAN): when the
  builder wires blank ruled lines, a new line being added is a
  writing act — if it sounds at all, it takes S-3a at its quietest,
  never a detent and never the old fabricated-split's harmonic seal.
- **ZIP-TEAR's drag ticks** (next tear build, designer's §5.3): when
  built, tooth ticks are fragments of S-1's grain (≈10–15 ms,
  position-locked, finger-still = sound-still), completion is the
  full S-1, and the sub-threshold reseal retreat is **silent** —
  this seat narrows the designer's "reduced level or none" to
  *none*: a reseal is an un-happening, and an un-happening has no
  sound. Parked with the build, not priced in this pass.

## 10. Acceptance — how the builder proves this without a phone, and what only a phone can prove

1. **Golden hash unchanged:** `SEAL_BUFFER_GOLDEN_SHA256` still
   matches after this pass. Any diff in the seal render is an
   automatic fail, whatever it sounds like.
2. **Single call site preserved:** `playSealThrow()` still occurs
   exactly once in production code, inside `executeSeal`.
3. **OFF truth, per event:** with the fact set OFF — fresh engine
   *and* heartbeat-bound transition (the SoundAndSocialTruthTest
   shape) — S-1, S-3a, S-3b, S-4 enqueue/play zero audio; with ON,
   each fires exactly once per act. ADD's count is the regression
   test for the double-voice cure (§4).
4. **Negative greps:** no engine call on the SLEEP stand-down path;
   no non-engine audio API (`ToneGenerator`, raw-resource playback,
   View sound effects) anywhere in the new sound work.
5. **Determinism where specced:** S-1's render, rendered twice, is
   sample-identical; its hash is pinned from its first green run.
6. **Honest remainder (phone-only, stated now so nobody claims it
   early):** whether the tear reads as *fibre* and not synth,
   whether the knocks read as *wood*, and whether the Throw at 05:00
   registers through meditation without startling — none of that is
   verifiable off a device. This filing is DECLARED on those points,
   not proven, and the first on-phone pass may tune S-1/S-3 gains
   within their stated ceilings without returning for a re-rule.

**Close: DECLARED.** Sound spec only — no code written, nothing
compiled, no Gradle or tests run by this seat. F-1's machinery is
untouched; S-1 and the S-3 cure ride the F-2/F-6/F-7 work they are
filed under.

— Feel-2 · sound (issue #12 cell)

---

## Ready-to-post comment for issue #12 (AMT posts this)

```markdown
**FEEL-2 · sound — filed for issue #12** (full filing: `feel-2.md` in the cell work dir)

I read Amt's audit and plan against the trunk sound machinery (`TactileSoundEngine`, `SessionSealTransition`, `SoundEffectsSetting`, the seal identity + sound truth tests). Every sound below is filed as event / character / duration-envelope / off-switch, and every one is governed by the one persisted fact: SOUND EFFECTS (SharedPreferences, default ON, commit-written, pushed to every live engine, re-read at seal-bind). OFF means zero audio on every path below — no bypasses, no "important sound" override; haptics are a separate gate and survive as specced ("muting loses timbre, never the event"). No new settings, no per-sound toggles, no clock-based night mode — the night voices are quiet at every hour.

**S-1 · The tear gets a paper voice, not a click.** `playTearPulse` today is in the right place with the right rank (240 Hz / 40 ms / chord 1.21 vs the completion pulse's 1.33) but the wrong grain — a sine blip wearing a tear's name, the acoustic twin of Weiss's "fade in a tear's name" motion flaw. Builder change is grain only: deterministic filtered-noise rip built from the seal bolt's proven machinery (frozen-seed xorshift + one-pole lowpass) — onset rupture 0–8 ms, stepped fibre decay 8–55 ms, zero by ≈90 ms, dry (no room), peak no louder than today's gain, quiet 240 Hz carrier underneath, haptic twin unchanged. Input-free render, hash pinned from its first green run. It fires only after the booking (G-01 order), never for a tap that didn't tear.

**S-2 · The 05:00 FASTED seal: the Throw, untouched.** It is already present-but-never-startling — a full 1,000 ms, low 360 Hz body, bolt at +30 ms at −6 dB under it, room tail to −46 dB at 900 ms, fired exactly once, bit-identical on hand and routine arms, last statement of the seal act at t=0. Forbidden at 05:00: a softer "meditation" variant or any delay (breaks bit-identity and lies about t=0), any pre-sound, any intention-dependence (F-1 changes the string only — empty-queue and queue-named seals sound identical), and any repeat/escalation if unheard. If he misses it in the bath, the printed SEALED word waits; the sound is proof, not a summons.

**S-3 · Night queueing: quiet knocks — plus a defect.** ADD today speaks TWICE: the screen's `submitTask()` fires a mechanical latch, then `addTaskToQueue` fires a rotary detent. Cure inside F-2: one owner (the ViewModel add path), the screen's latch deleted; acceptance = one tap enqueues exactly one transient. The voice: ONE quiet knock from the seat-knock family at its lightest weight — ≈300 Hz, single decay, zero by ≈40 ms, arm's-length only. FIRST (and CUE — same act, a leaf becomes the head): the same knock lower and firmer plus a half-weight settle contact ≈80 ms later, zero by ≈160 ms — a leaf placed, then the palm settling it; never seal-family, never a success sound. Both fire only for acts that actually take (a mid-seal promotion refusal stays silent).

**S-4 · The sub-step strike takes the tear's tick** (audit F-6, first branch — not the narrowed target; precision demands are wrong at 1 AM). Same noise grain as the tear, truncated: strike-on zero by ≈30 ms at ≈0.6× tear peak, un-strike the same grain lighter (≈0.35×, ≈20 ms) — direction carried by weight alone. Today's 640 Hz detent on this act is deleted. Under a seal the toggle is absent (F-5), so this sound cannot fire through a seal.

**S-5 · Ruled silences at night:** typing, opening/scrolling Tasks, focusing the blank leaf, a blank ADD (no error buzz for a half-asleep fumble — the leaf just stays blank), banner furniture, and all F-1 machinery movement. Only his hand (S-1/S-3/S-4) and the seal (S-2) have voices on this screen.

**S-6 · The SLEEP stand-down at 03:30 is SILENCE — ruled, not omitted.** A stand-down is unstamped and never a judgement (J2), so a sound would fabricate a verdict on his night; its timing rides the reconcile funnel and can drift, so it can never lawfully wear an alarm's clothes; and he wakes himself — Monolith is not his alarm clock. No reverse-Throw, no disarm variant, no haptic. The contrast proves the rule: the SLEEP seal-in at 20:00 DOES sound the Throw bit-identically, because a seal is an act. Opening speaks; release is silent. Builder acceptance is a negative grep: no engine call on the stand-down path.

**Ruling on Feel-5's echo: refused at 05:00, absorbed at night.** A tick before/with/instead of the Throw has no lawful place in a golden-hash, last-statement, t=0 proof — "it held" is already perceptible without checking; that is what the Throw is for. The night half survives as S-3b's functional knock, changed in semantics: per act, not once a day. Sounds confirm acts; they don't keep vows — the keeping is heard once, at 05:00, in the Throw alone.

**Flags parked, not priced here:** UNDO currently spends a harmonic-seal voice on a task act (same misspend class already struck from task completion) — when cured, the restored leaf takes S-3a's knock. ZIP-TEAR's drag ticks, when that build comes: fragments of S-1's grain, completion = full S-1, reseal retreat silent.

**Acceptance:** seal golden hash unchanged; `playSealThrow()` still exactly once in production; OFF-truth per event in the SoundAndSocialTruthTest shape (fresh engine + heartbeat-bound transition); no non-engine audio APIs. Honest remainder: fibre-vs-synth, woodiness, and 05:00 startle are phone-only judgements — DECLARED here, not proven; first on-phone pass may tune S-1/S-3 gains within their ceilings without a re-rule.

Status: DECLARED — spec only, no code, nothing compiled.

— Feel-2 · sound (issue #12 cell)
```
