# ISSUE #112 — WAVE 3B REPORT (KARTE cell, builder/verifier bundle)

**Issue:** Audit R3 · sound-haptics-lived — 5 findings · **Branch:** `agent/issue-112`
**Base:** origin/main `0a00e95` · **Cell:** `builds/cells/issue-112`
**Standing decisions honoured:** quiet gate knocks untouched (seat-knock gates,
FLOW shore/toll voices, finish E0/E880 knocks unchanged); no arming sound added;
the seal still speaks ONLY the Throw (Slice-E law); the completion pulse keeps
its one lawful spend (finish E700) — finding 4 changes only whether it can be
silently dropped, not where it may be spent.

## Per-finding dispositions

### F1 [HIGH] SOUND ON indistinguishable from OFF on a muted phone — **FIXED-IN-CODE**
Walked current code: grep over `app/src/main` found ZERO reads of ringer mode,
stream volume, interruption filter, or output routing — the audit held exactly.
Fix, in three parts:
- **New fact:** `service/AudibilityProbe.kt` — pure `classify(ringerMode,
  musicStreamVolume, interruptionFilter)` + Android `read()`. Truth table,
  each candidate gate checked against what actually gates THIS engine
  (USAGE_ASSISTANCE_SONIFICATION → music stream): volume 0 ⇒ SILENT_VOLUME;
  DND NONE or ALARMS ⇒ SILENT_DND; ringer SILENT/VIBRATE is READ but
  deliberately non-deciding (it cannot mute the music stream on stock
  Android — printing silence for it would be the same lie, reversed);
  output routing never silences (headphones still reach the ears).
  Unreadable service ⇒ AUDIBLE (no unproven silence printed).
- **Print:** PARK's SOUND row now reads `ON · SILENT` when the fact is ON but
  the phone is provably unhearable (`ParkDrawer.kt`, read at composition —
  the drawer is transient, print is as-of-open). Engine seam:
  `TactileSoundEngine.currentAudibility()`.
- **Marker honesty:** `SessionSealTransition.writeSealMarker` comment no
  longer claims a seal "already heard": the marker testifies the seal landed
  and the Throw FIRED — its haptic always fires (S0), only timbre rides on
  phone state. Recovery behaviour unchanged (re-sounding a landed seal late
  would be a haunting, per F-K1).

### F2 [HIGH] Every PARK sled flip sounds twice; sound sled asymmetric — **FIXED-IN-CODE**
Walked current code: `SledSwitchView.onTouchEvent` fired `fireFeel(8L)` →
`playFirmClick` on every ACTION_UP, THEN invoked the handler, whose own voice
followed (detent 720 for sound-ON, latch for shorts/browser/social). The view
fired under the OLD gate: muting clicked at full volume, unmuting's click was
swallowed, plus the detent on top. Held exactly.
Fix — **single-voice law**: the sled fires NO feel of its own
(`BlockitSuitcaseAndSwitchesViews.kt`); the handler is the flip's one voice,
fired AFTER the fact lands. `toggleSound` now speaks `playMechanicalLatch(next)`
(the same latch the other three sled handlers already spoke — no new voice
invented): turning ON it sounds; turning OFF its sound is gated by the fact
just written while its haptic still fires — governed by the state the flip
CREATES, symmetric by construction. All SledSwitchView consumers route through
VM toggle handlers (PARK sound/shorts/social rows); no orphaned silent sled.

### F3 [MEDIUM] Seal clunk spent on housekeeping — **FIXED-IN-CODE**
Census of `playHarmonicSeal` in current code: arming=true at undo (1799),
split (1968), auto-split (2002), cue (2020), smart loadout (2099), add app
(2135), apply profile (2142), create profile (2176), peek end (2421),
transmute (2430) — all housekeeping; the audit held and its list was complete
for arming=true except transmute, which the audit's F5 line range covered.
All ten now speak `playMechanicalLatch` (peek end: the release shape — see
F5). Remaining spends are exactly the DISARM family with arming=false —
`requestDisarmChallenge`, `confirmDisarmSession`,
`completeEmergencyBreakFromValve` — which the engine's own doc defines as
this voice's disarm shape: lawful, kept. The reservation law is now written
on `playHarmonicSeal` itself. The real seal never used this voice (it speaks
the Throw via SessionSealTransition) — unchanged.

### F4 [MEDIUM] Completion pulse silently deleted past 900 ms — **FIXED-IN-CODE**
Walked `MonolithViewModel` finish job (was 1241-1262): E0 knock, delay to
E700, pulse only `if (elapsed < 900L)` — the code's own comment admitted
"Death-booked: the E700 slot stands EMPTY." Held exactly.
Fix: the pulse is **owed, not windowed**. New pure seam
`completionPulseDelayMs(elapsed) = (700 − elapsed).coerceAtLeast(0)`; the job
delays by it and then fires unconditionally — late fires late, never dropped,
and still inside the one sequential E-clock job so it can never overtake the
E880 knock pair.

### F5 [MEDIUM] Peek buzzes every 15 s from an invisible app, then "seals" — **FIXED-IN-CODE**
Walked the 1-second ticker (was 984-999): sounding `playRotaryDetent(760)`
at every 15 s boundary while the user is by definition inside the peeked
app; expiry called `end2MinutePeekEarly()` whose voice was the arming seal
clunk. Both halves held.
Fix: warnings are **felt, twice, never heard** — pure seam
`peekWarningTickDue(s) = (s == 30 || s == 15)`; the ticker calls the new
haptic-only `TactileSoundEngine.playPeekWarningTick()` (enqueues nothing).
Two warnings keep expiry from ambushing the user; seven sounding detents
from an invisible app are gone. Peek end (hand-back AND expiry share
`end2MinutePeekEarly`) now speaks `playMechanicalLatch(false)` — the door
re-latching, a release — never the seal voice for the opposite event.

## Tests — `app/src/test/java/com/example/SoundHapticsLivedTest.kt` (10 cases)
- `AudibilityTruthTable` — the full classify table incl. the ringer
  non-deciding cases. · `EngineAudibilityReadsThePhonesVolume` (Robolectric
  ShadowAudioManager 0 → SILENT_VOLUME, 6 → AUDIBLE).
  · `ParkPrintsSilentWhenThePhoneCannotBeHeard` — compose: volume 0 prints
  `ON · SILENT`; volume 6 prints plain `ON`.
- `SledViewFiresNoVoiceOfItsOwn` — ACTION_UP reaches the handler once and
  enqueues ZERO transients. · `SoundToggleSpeaksExactlyOncePerFlipUnderTheNewFact`
  — OFF enqueues no sounding voice under the new fact; ON enqueues exactly
  one, signature latch (760 Hz × 705) — trunk's detent was (720 × 264) under
  a swallowed firm click.
- `CueingATaskSpeaksTheLatchNeverTheSealClunk` — voice-signature proof via
  the new engine seams (`enqueuedTransientCountForTest`,
  `lastEnqueuedTransientForTest`): latch (760 × 705), not clunk (360 × 2116).
- `CompletionPulseDelayClampsToLateNeverDropped` — 900 ms and 5 000 ms
  lateness still fire (delay 0).
- `PeekWarningCadenceIsThirtyAndFifteenOnly` · `PeekWarningTickSpendsNoVoice`.

## The 20 named seats + roster (bundle-signed)
This cell ran as ONE builder/verifier bundle under KARTE (Wave-3B pattern);
every seat below was applied as a lens during the walk/fix and is signed by
the bundle lead. Seats marked LEAD-COVERED were exercised by the lead in that
lens, not by a separate agent — stated honestly per dispatch law.

- **Feel specialists (roster, LEAD for lived verification):** walked each
  sound as lived — the F2 double-click, F3 vocabulary theft, F5 invisible
  buzzing are feel defects before they are code defects; their verdicts set
  the fix shapes (one voice, reservation, felt-not-heard).
- **Crazy-ideas:** proposed printing audibility by re-voicing the Throw when
  muted — REJECTED by the bundle (would break B-5 bit-identity); the truth
  belongs in PARK's print, not in a second seal voice.
- **Maximum-implementation-potential / MaxImpl (roster):** pushed F1 from
  "check exists" to a full classify+print+surface fix in one pass.
- **Brutal critic:** attacked the first F1 draft for classifying ringer
  SILENT as inaudible — unproven on this engine's stream; the probe now
  documents ringer as read-but-non-deciding instead of printing a guess.
- **Improver:** F4's clamp (late-fires-late) over the first draft's wider
  2 s window — a bigger window is the same bug with a later funeral.
- **Designer / Enthusiastic designer / Wow-level designer:** PARK's
  `ON · SILENT` word — one word added to the state's one rendering, no new
  furniture, no colour change (palette untouched, no green anywhere).
- **Designer with 29 years of experience:** insisted the sled's silence be
  total (no fallback preview click): a control that clicks without toggling
  lies; previews without a handler now make no sound at all.
- **Consumer / Student / User / Humans-Day-fit (roster):** the lived test —
  a student on a muted phone in a library must be able to SEE that ON is
  currently silent; F1's print exists for exactly that glance.
- **User-experience / Interaction-detail specialist:** F2 ordering detail —
  the voice must land after the fact, or the sound sled can never be
  symmetric; F5's 30/15 warnings timed to be useful, not nagging.
- **Purpose-questioning:** asked why the peek warns at all — answer kept it
  (expiry must not ambush), but cut it from seven sounding detents to two
  felt ticks.
- **Thoughtful / Deep-thinker:** F1's recovery-marker nuance — "heard" was
  the false word, not the adopt-silently behaviour; comment fixed, F-K1 law
  untouched.
- **Intentionality-focused designer:** F3 is an intentionality defect — a
  voice's meaning is its spend history; the reservation law is now written
  where the next spender will read it (on the function itself).
- **Synchronous collaboration:** all five fixes share two seams
  (`AudibilityProbe`, the engine's enqueue signatures) so the tests read
  one vocabulary; no fix reaches into another's surface.
- **Implementation-quality / Technical execution specialist / Technical +
  Implementation (roster):** pure seams for every decision
  (`classify`, `completionPulseDelayMs`, `peekWarningTickDue`) — all
  unit-testable off-device; Android reads isolated to one function each.
- **Verifier / Diagnostician (roster):** every finding re-walked against
  CURRENT origin/main before any fix (line numbers had drifted from the
  audit's tree; all five held on substance, census extended by one site).
- **Finishers / Fresh-eyes (roster):** LEAD-COVERED — final diff read
  cold for stray spends: repo-wide grep confirms no remaining
  `playHarmonicSeal(arming = true)` / `(true)` call site anywhere.
- **Critics / Wild-Innovators / Small Things / Flow / Keepers of Record
  (roster):** LEAD-COVERED — Small Things owns the marker-word catch
  ("already heard"); Keepers own this report's census numbers.
- **Lead / Builder (cell types):** this bundle — walk, fixes, tests, CI.

## User-facing changes
- PARK sound row tells the truth on a muted/DND phone: `ON · SILENT`.
- Every sled flip: exactly one sound/feel, same shape both directions.
- Undo, task splits, cueing, loadouts, profiles: latch click instead of the
  lock-in clunk (the clunk now means the session boundary only).
- Session finish: the completion pulse always sounds, even on a slow phone.
- Peeks: no more buzzing sounds from nowhere — two felt warning ticks
  (30 s, 15 s left), and the peek's end clicks the door shut instead of
  sounding a seal.

## CI
Pushed early; verdict recorded in the closing evidence below (updated on
the branch once read).

## DECISION-NEEDED
None. (One priced note, not a decision: PARK's audibility print is read at
composition — if the user changes volume with the drawer held open, the word
refreshes on the next recomposition/open, not live. A live receiver would
cost a broadcast registration in the drawer; the bundle judged as-of-open
truth sufficient for a transient sheet.)
