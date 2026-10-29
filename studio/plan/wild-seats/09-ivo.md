# Ivo — Wild, sound

**Assignment:** Tasks as an acoustic desk — duration caps, composer focus/commit, ADD, sub-step notes, final-step chord, delete/un-add. Fixed pitch sets only. No pitch formulas anywhere in this file: every pitch below is a literal member of a named set, chosen once, never computed from duration, position-in-day, or anything else.

**What the renders say (current, recomposed Tasks):** a recessed single-line composer field, four duration caps (15m / 25m / 45m / 90m) plus an ADD key, hairline queue rows with one expanded ACTIVE block, sub-steps numbered 01 / 02 / 03 in a right-margin Anonymous Pro column, and the verbs DONE / START / DEL under the active task. The horizon bar is dead as an object — its one true fact (queue total) now lives as the header numeral. This desk is built on exactly that screen: nothing below adds a visual object at rest, and nothing sounds during a sealed session. Tasks is rested-user territory; the whole desk is silent by construction once the Deck seals.

## The two fixed sets (the entire tuning of this screen)

All pitched sound on Tasks comes from two sets of wooden-bar voices. A bar voice = sine partials at ratios 1 / 2.76 / 5.40, body decay `exp(-0.055)`, 90–140 ms total, synthesised in the existing Float-PCM thread. No audio files. Levels are dB relative to `playHarmonicSeal` at gain 0.62 = 0 dB ref.

**CAP SET** — one pitch per duration cap, longer duration = lower pitch, learned as identities, never as a scale to calculate from:

| Cap | Pitch | Fundamental |
|---|---|---|
| 90m | D3 | 146.83 Hz (speaker carries it on its 2.76 partial, ≈405 Hz) |
| 45m | F3 | 174.61 Hz |
| 25m | A3 | 220.00 Hz |
| 15m | C4 | 261.63 Hz |

**STEP SET** — one pitch per sub-step ordinal, same wood, smaller bars, brighter:

| Sub-step | Pitch | Fundamental |
|---|---|---|
| 01 | E4 | 329.63 Hz |
| 02 | G4 | 392.00 Hz |
| 03 | B4 | 493.88 Hz |
| 04 | D5 | 587.33 Hz |
| 05 | E5 | 659.25 Hz |

A task with more than five sub-steps: steps 06 and beyond sound the Step-05 pitch through a deeper felt damper (decay ×0.6, −3 dB per reuse is *not* applied — same damped voice each time). No sixth pitch is ever invented. The set is the set.

**Global desk laws (every element below obeys these):**

- **Lock:** haptic and sound for one event issue from the same call site, haptic scheduled against the audio playback-head so the pair lands within **±8 ms**. Inside ±8 ms the brain reads one object; outside it reads a phone buzzing near a speaker.
- **One small room:** a single fixed algorithmic tail (≈18 ms pre-delay) is applied *only* to the final-step chord, taken from the shared engine at **−26 dB wet** (boundary agreed with Orson, who owns the room layer — see Mail note). Everything else on this desk is dry — taps happen on the desk, completion happens in the room.
- **Every sound draws its own picture.** Each event below has a visual echo in ink / one tonal step, so muting the desk never deletes information — it only removes the air. No echo uses orange; the screen's one orange remains the START fill, which never pulses, spawns, or moves with sound.
- **Night:** identical pitches, all levels −2 dB, chord room tail dried to −29 dB wet. Echoes are drawn in the night staircase tones (ground `#171412`, fills capped at `#4A423C`, cream as text tone only). Rooms are quieter at night; so is this one.
- **No fake quantity.** A pitch is an *identity* (this cap, this step), never a measurement. Duration stays printed on the cap; the ordinal stays printed in the margin. Sound confirms what position and print already said — it never becomes the only carrier of a fact.

---

## Tasks — duration caps

### Chosen form — four wooden blocks

**Mechanism / state source:** The cap bank appears only while the composer field is focused (the settled composer-collapse behaviour) and reads `TasksViewModel.selectedDuration`. Tapping a cap selects that duration and plays its CAP SET pitch at **−11 dB**, 90–140 ms bar voice. Re-tapping the already-selected cap plays the *same pitch* damped — felt layer over the bar, decay ×0.5, **−15 dB** — so "going down" and "already down" are audibly different states, matching what the cap's sunk selected rendering already shows. Selection state is never carried by sound alone; the sunk cap is the fact, the damper is the echo.

**Motion / visual echo:** The tapped cap does its existing skirt travel and sink; no new animation. On the audio onset frame the cap's printed duration label steps one tonal value darker and settles back over **140 ms** exponential fade — the label was struck, briefly. Selected cap: no fade (it is already sunk and dark); the damper is heard, not drawn.

**Haptics:** Strike: `[0, 8, 6] ms @ [0, 150, 55]` (0–255 scale) — a block tapped, not a button pressed. Damped re-tap: `[0, 5] ms @ [0, 90, 0]`. Lock ±8 ms.

**Depth / material:** Beech on a desk, bone dry, no room tail. The four caps are the mid-depth layer of the screen: softer and further than ADD's machinery, harder and nearer than the composer's paper.

### Runner-up — one honest click for all four caps

A single neutral firm click at one pitch for every cap, duration carried by print alone. Compact spec: existing `playFirmClick()` at −12 dB, `[0, 6] ms @ [0, 140, 0]`, no echo. **Why it lost:** it is truthful but mute — a mis-tap between 25m and 45m is silent, and the caps remain four identical buttons on the screen Sarthak called bland. The chosen form costs four fixed voices and buys eyes-free cap identity within a week of use.

*Also considered and rejected outright:* a continuous pitch glide mapped to duration. That is a pitch formula, it is banned for this screen, and it would make pitch pretend to measure minutes — a truth violation, not a style choice.

### Guard fork

The whole desk is gated by the single existing **SOUND EFFECTS** sled — one gate, one home, no new toggle invented. **If Guard stays a destination:** the sled stays in Guard's sound section; the desk reads the same boolean and is otherwise unchanged. **If Guard dissolves:** the sled moves with the other switches into Shield's settings drawer; the desk reads the same boolean from its new home and is otherwise unchanged. The desk itself never forks — only the switch's address changes, and that is his call. System silent / DND also gate the desk in full: composing tasks is never urgent enough to break silence, and with sound off every event below still fires its visual echo and haptic.

### Standards check

- **Arm-path cost:** zero. Caps are pre-session composition; tapping through all four caps adds no blocking time — each bar decays under the next gesture.
- **Mid-session decisions:** zero. The cap bank does not exist inside a sealed session.
- **Truth risk:** low. Pitch = identity of a printed, sunk control; the damped variant encodes only "already selected," which the sunk rendering already states.

---

## Tasks — composer focus and commit

### Chosen form — a card pulled from the stack, and returned

**Mechanism / state source:** Reads `FocusState` of the composer field and `composerText.isNotEmpty()`. Three events, three sounds, no fourth:

- **Focus (empty or existing draft):** a paper-slide — shaped noise, highpass sweep 900 → 2400 Hz, 140 ms, **−22 dB**. A fresh card pulled from the stack. Felt more than heard, which is the point.
- **Typing:** **silence, deliberately.** Monolith never fakes keyboard clicks — the system keyboard owns those, and doubling them is a lie about which object is sounding. Typing's feedback is purely visual (below).
- **Blur with an un-added draft (the commit that didn't happen):** the same paper-slide reversed, **−26 dB** — the card goes back into the stack. Critical truth condition: the draft text is *kept* (it is still in the field when he returns), so the sound is a soft return, never a discard, crumple, or delete sound. The sound says "put away," because that is what happened.

**Motion / visual echo:** On focus, the composer recess deepens one tonal step over **180 ms, linear** (paper doesn't ease). Each typed character does a letterpress settle: scale 103% → 100% plus one tonal step darker → settled, **90 ms**, ease-out quart — type that lands like type. On blur-abandon, the recess returns over 180 ms linear with the reversed slide.

**Haptics:** Focus: `[0, 4] ms @ [0, 70, 0]` — a fingertip's worth, or nothing on devices with weak actuators; never more. Typing: none. Blur-abandon: explicit silence — a buzz here would read as an error for a legal act.

**Depth / material:** Paper — the furthest, softest layer of the desk. The recess step is tone and a 1 px caught-light edge only; no shadow at any point.

### Runner-up — keystroke ticks with a commit ding

Per-character soft tick (bar voice at −24 dB) and a two-note confirmation when the field loses focus with text. **Why it lost:** the ticks fight the system keyboard's own sound and haptics, doubling every keystroke into two objects; the commit ding claims a task was created when blur commits nothing — ADD creates the task, the field merely holds a draft. A sound that overstates the state is a truth failure with good production values.

### Standards check

- **Arm-path cost:** zero; focus/blur are composition, and the slide never blocks the first keystroke.
- **Mid-session decisions:** zero; the composer is unreachable mid-session.
- **Truth risk:** low, guarded by the one hard rule above — the return sound must never imply the kept draft was thrown away.

---

## Tasks — ADD

### Chosen form — the card punch, and the row seats itself

**Mechanism / state source:** Fires on ADD tap when `composerText` is non-empty and a duration is selected; the new `Task` (title, selected duration, empty sub-step list) is inserted at the head of the queue and the queue-total header numeral updates. Two stages, one gesture:

- **Stage 1 — punch (t = 0):** sine drop 320 → 140 Hz in 30 ms plus a noise burst lowpassed at 700 Hz, **−4 dB**. The card is punched.
- **Stage 2 — slice (t = +90 ms):** noise through a bandpass sweeping 3000 → 1200 Hz, 70 ms, **−12 dB**. The card is cut free.
- **Seating (replaces the dead horizon-bar coupling):** the new row slides into the queue and, on the frame it lands, sounds the CAP SET pitch of *its own duration* at **−8 dB** — a 45m task lands at F3, the same voice as the 45m cap that made it. The two existing neighbour rows each knock once at **−20 dB** as they are pushed down one position. Train cars coupling, not a toast. The header numeral does not tick, count audibly, or celebrate: it simply shows the new total, because the total's one home is that numeral.

Empty-field ADD (no text): **no punch.** A single dull paper tap at −20 dB and the field shakes nothing, shows nothing new — the button was pressed and nothing was true to punch. (This same empty-ADD tap is the un-add gesture — see Delete section.)

**Motion / visual echo:** The new row enters on a spring (stiffness 300, damping ratio 0.72 — exactly one visible 2 dp overshoot, the shudder) over **320 ms**; the seating clack fires at the overshoot frame, not at animation start, so the sound is caused by the landing. A 1 px ripple crosses the new row's hairline in **140 ms** at the same frame. Composer text clears with no animation — punched is punched.

**Haptics:** Stage 1: `[0, 14, 30, 8] ms @ [0, 230, 0, 160]` — the 8 ms tail *is* the slice arriving in the hand. Seating: `[0, 7] ms @ [0, 185, 0]` at the overshoot frame. Empty ADD: `[0, 5] ms @ [0, 80, 0]` only. Lock ±8 ms throughout.

**Depth / material:** Metal-edged card machinery — the nearest, hardest attack on the screen. Dry; no room tail. The punch is the desk's loudest routine event and it is still 4 dB under the seal.

### Runner-up — single success chime

One pleasant two-note chime on every successful ADD, nothing on failure. **Why it lost:** a stock OS success sound wearing Monolith's clothes. It carries no duration identity (a 15m and a 90m task land identically), it cannot distinguish the empty-field failure except by absence, and "success chime" is the exact generic-notification grammar this machine exists to refuse. The punch costs two noise stages and one set lookup; the chime saves nothing worth saving.

### Standards check

- **Arm-path cost:** zero added — punch, slice, and seating all play under the user's next action; total ADD-to-ready-for-next-task is the existing insertion time.
- **Mid-session decisions:** zero.
- **Truth risk:** low. The seating pitch repeats the duration the cap already selected and the row already prints; the empty-ADD refusal sound prevents the one lie this button could tell (a punch with no card).

---

## Tasks — sub-step notes

### Chosen form — each step is its note, struck once, kept as a fossil

**Mechanism / state source:** Sub-steps exist only inside the expanded ACTIVE block; a queued task's steps are not shown and cannot sound — tapping a queued row expands it first, silently. Completing sub-step *n* (`subSteps[n].done: false → true`) plays STEP SET pitch *n* at **−13 dB**, 110 ms bar voice. Un-completing (`true → false`) plays the *same pitch* through the felt damper (decay ×0.5, **−17 dB**) — a mistake has a sound, and it is gentle, and it is recognisably the same note taken back, not a different "error" note. No pitch outside the set is ever produced for any step event.

**Motion / visual echo:** The drawn strikethrough tears across the step text in **140 ms** (fast nib, crawling tail), the row squashes 4% into its hairline and settles — the note's onset frame starts both. On completion only, the step's Anonymous Pro numeral gains a **waveform fossil**: a 14 dp-wide, 1 px hairline trace of that note's actual decay envelope, ink at 30%, drawn left-to-right in **110 ms** tracking the envelope, then static forever. A finished task *shows* its melody in the margin it already owns. Un-completing erases the fossil with the strike (90 ms fade) — the record never claims a step that is currently undone.

**Haptics:** `[0, 5] ms @ [0, 120, 0]` per completion, amplitude rising by step within the fixed pattern only in the sense that Step 05's strike is `[0, 5] ms @ [0, 152, 0]` — five fixed variants, one per set member, listed not computed. Un-complete: `[0, 4] ms @ [0, 80, 0]`. Lock ±8 ms; fossil drawing starts on the audio onset frame, so the fossil is drawn *by* the sound.

**Depth / material:** Small beech bars on the desk, dry. Fossils live on the existing hairline rows at 1 px — a record, not a container, card, or colour. Night: fossils in cream at reduced alpha on the `#171412` ground, never a fill.

### Runner-up — one tick for every step

The existing 5 ms detent tick for all completions, escalation reserved for the final step. **Why it lost:** progress becomes illegible by ear — step 01 of 03 and step 02 of 03 feel identical, and the final-step chord (next section) then arrives from nowhere instead of as the sum of notes already heard. The set costs five fixed voices; the tick saves four and spends the screen's one learnable melody.

### Standards check

- **Arm-path cost:** zero; steps are worked inside the ACTIVE block before or between sessions, never on the seal path.
- **Mid-session decisions:** zero introduced — completing a step is the work itself, not a settings branch; nothing here prompts, offers, or negotiates.
- **Truth risk:** low. The note encodes only the ordinal that is already printed beside the step; the fossil is erased on un-complete, so the visible record and the state cannot diverge.

---

## Tasks — final-step chord

### Chosen form — the task strums itself, then the stamp

**Mechanism / state source:** Fires exactly once, when a completion makes `subSteps.all { done }` true for a task that has at least one sub-step. All of *that task's own* STEP SET pitches — only the steps it actually has — sound together, staggered **12 ms apart, low to high** (a strum, not a stab), summed peak **−1 dB**, total decay ≈480 ms, through the desk's one use of the shared room tail (**−26 dB wet; −29 dB at night**). **160 ms** after the strum's onset, the stamp — a Tasks-local voice, never the session completion voice: a dry paper stamp (sine burst at 110 Hz, 70 ms decay, plus a filtered-noise press at −14 dB) at **−6 dB**. The seal and the completion pulse each mean exactly one thing app-wide; a finished *task* is not a finished *session*, so it does not borrow that voice. The task's row then leaves the ACTIVE position by the queue's normal DONE behaviour.

Two truth guards, both binding:

- Pressing **DONE** on a task whose steps are already all complete plays the stamp only — **no second chord.** The chord already sounded for that event; repeating it would count one completion twice.
- Pressing **DONE** with steps still open plays neither chord nor stamp — a single dull paper fold (noise, lowpass 900 Hz, 60 ms, −16 dB). A task closed incomplete sounds incomplete. Tasks with zero sub-steps get the stamp alone on DONE; there is no melody to strum and none is faked.

**Motion / visual echo:** Each fossil on the task's rows brightens one tonal step in strum order (12 ms stagger, matching the notes) and settles — the margin plays back what it recorded. At the stamp frame the task title's ink steps one value darker for **200 ms** (pressed in), then the row performs its normal exit. Nothing scales, bounces, or confetti's.

**Haptics:** Strum mirrored as `[0, 6, 12, 6, 12, 8] ms @ [0, 170, 0, 190, 0, 215]` for a three-step task (pattern extends by one 12 ms cell per additional note, capped at the five set members). Stamp: one heavy knock `[0, 22] ms @ [0, 235, 0]` plus a paper-settle `[0, 8] ms @ [0, 90, 0]` at +50 ms, landing after its 160 ms of quiet. Lock ±8 ms per note.

**Depth / material:** The desk's one moment of room depth — completion happens *in the room*, everything else happened *on the desk*. That contrast is the reward, and it is rationed to once per task by construction.

### Runner-up — completion pulse alone, louder

The session completion pulse boosted toward 0 dB as the whole reward. **Why it lost:** it spends the app's shared completion voice on a Tasks event, blurring the line between "a task's steps are done" and "a session ended" — two different facts that must never share one rendering at any strength. The strum is Tasks' own voice, built from pitches the user composed by working; the stamp that seals it is a local paper voice, and the completion pulse stays reserved for the one event it means.

### Guard fork

Same single SOUND EFFECTS gate as the rest of the desk (see Duration caps). One addition: the chord is the only Tasks sound loud enough (−1 dB) to matter across a room, so if the sled is off, its visual echo — fossils brightening in strum order, title pressed darker — must still run in full. It does, by the global law. No separate chord toggle is created under either Guard outcome.

### Standards check

- **Arm-path cost:** zero; fires after work, never before a seal.
- **Mid-session decisions:** zero; it concludes a task, it offers nothing.
- **Truth risk:** the highest on this desk, and fenced accordingly — the two no-repeat / no-fake guards above exist precisely so the loudest sound on the screen can never overstate completion. With them, risk is low.

---

## Tasks — delete and un-add

### Chosen form — the un-punch, and one honest way back

**Mechanism / state source:** DEL on a queued or active task (`Task` removed from the queue; `lastDeletedTask` + its index held in view-model state until the next successful ADD or 10 s pass, whichever first):

- **Delete:** the ADD punch played with its envelope reversed — decay first, attack last, an un-punch — at **−10 dB**, 130 ms, followed by the paper-withdraw slide (the focus slide's noise shape, bandpass 2400 → 900 Hz, 120 ms, −20 dB) as the row collapses. Deleting sounds like the making, undone — because it is the same machinery running backwards.
- **Un-add (restore):** tapping ADD with an *empty* field while a `lastDeletedTask` is held restores the task at its original index and plays the forward punch at **−9 dB** (stage 1 only, no slice — restored, not newly made) plus the seating clack at its own duration's CAP SET pitch. Once restored or expired, the buffer clears. No dialog, no snackbar, no text button: the gesture is the empty ADD, and its window is stated here so it is a rule, not a secret — and if Marlowe or Ren judge a hidden gesture undiscoverable, the fallback is a single quiet `RESTORED` affordance; the *sounds* above stand either way.

**Motion / visual echo:** Delete: row height collapses over **180 ms**, ease-in (things leave by falling, not fading), neighbours close the gap in the same 180 ms with no overshoot. Restore: row re-enters with the ADD seating spring (stiffness 300, ζ 0.72, one 2 dp overshoot, 320 ms). The header numeral updates on the same frame as each sound's seating/withdraw moment — number and noise never disagree about whether the task exists.

**Haptics:** Delete: `[0, 10, 20, 6] ms @ [0, 190, 0, 120]` — the punch's shape, softened at the edges to match the reversed envelope. Restore: `[0, 12] ms @ [0, 200, 0]` plus seating `[0, 7] ms @ [0, 185, 0]`. Lock ±8 ms.

**Depth / material:** Same card machinery as ADD, heard from the other side of the desk. Dry. No room, no drama — deletion in this app is a clerical act and sounds like one.

### Runner-up — stock trash whoosh with an undo snackbar

A generic descending whoosh on DEL and a timed text snackbar offering undo. **Why it lost:** the whoosh is every other app's bin, carrying no relationship to the punch that created the task; the snackbar adds a timed reading-and-deciding task to a screen whose user is composing at speed, and its countdown is a small mid-flow negotiation. The reversed punch is learnable in one hearing and the empty-ADD restore costs zero new chrome.

### Standards check

- **Arm-path cost:** zero.
- **Mid-session decisions:** zero — DEL does not exist for the running task mid-session; this governs queue composition only. The restore window asks for no decision: it is there if used, invisible if not.
- **Truth risk:** low. The buffer holds exactly one task, restores it to exactly its place, and expires silently; the numeral and the sounds move on the same frame, so the queue can never sound larger or smaller than it is.

---

## Too good to lose

**1. The START hand-off note.** When START is tapped on the ACTIVE task, before the Deck takes over, the task's Step-01 pitch (E4) sounds alone at −13 dB, dry, `[0, 5] ms @ [0, 120, 0]` — the first note of the melody he is about to perform, played as the door opens. It replaces the dead CUE preview with the same honesty attached to a verb that actually commits, costs one set lookup, and gives the Tasks → Deck crossing a single acoustic stitch. Unchosen only because START's behaviour belongs to the Deck seat's arm-path accounting, not this desk's — flagged for Marlowe rather than silently annexed.

**2. The hollow desk.** When the queue is empty, ADD's seating clack has nothing to seat into: stage 1 and 2 play, and the seating knock is replaced by the same CAP SET pitch at −18 dB with the room tail at −30 dB wet — the punch echoes slightly in an empty desk. Absence rendered as absence, acoustically: the desk sounds different when there is nothing on it, and that difference is true. Unchosen because an empty queue is a rare state in his ten-block day and the echo risks reading as a bug until it is learned; it deserves a deliberate yes, not a smuggle.

## Mail / wall note

Inbox was empty at start (only an empty `read/` subfolder). Three letters arrived during writing and were read before finishing, then moved to `read/`: two copies of thread `citation-requirement-lifted` (from Orson and from Mara, Wild) — Sarthak's order verbatim, already received via the coordinator and applied throughout (this file cites nothing); and one from Orson, thread `engine-room-boundary` — Orson owns the room/engine layer (0 dB reference, room tail, gain ladder); I own Tasks-local acoustics. **Accepted, and this file was amended to match:** the final chord takes the shared room tail at −26 dB wet, it is the desk's only event above −3 dB, and the task stamp no longer borrows the completion pulse — task completion and session completion now have separate voices. Reply sent to Orson's inbox on the boundary thread. Wall read in full (Ren's opening, Dev's collision tests, the mailroom notice, and Sarthak's no-concept verdict); no wall post appended.
