# Luz — Wild, games

Game-feel for Tasks. One thesis, carried over from wave 1 and tightened: bland on this
screen is a *timing* problem. Nothing here adds an object, a word, a reward, or a colour.
It adds a timing contract — every tap answers on the way **down**, freezes only as long
as the event has earned, overshoots once, and settles. Four events, one escalating ladder:

| Event | Freeze | Total feel | Haptic peak | Orange |
|---|---|---|---|---|
| Sub-step strike | 60 ms | ~340 ms | 0.9 | none |
| Final sub-step | 110 ms | ~560 ms | 1.0, triple | none |
| START (active task) | 0 ms | ~480 ms to Deck-ready | 1.0, once | the fill itself |
| Whole-task DONE | 80 ms, row-local | ~700 ms drop + close-up | 1.0, scaled by minutes | none |

Shared input law for all four: **acknowledgement on pointer-DOWN, within one frame
(≤16 ms)** — the row is already reacting before the finger lifts. Commit fires on release
only where release is the meaning (START); everywhere else the tap is the act. During any
freeze, input is **queued, never dropped**: a second sub-step tap landing mid-strike is
buffered and fires its own strike the frame the first settles, with its freeze halved to
30 ms — rapid honest work feels like a run of cuts, not a queue of animations. A third
queued tap coalesces (state still flips, animation is a 90 ms plain strike, no freeze).
Freeze is a currency and this ladder is the whole budget; nothing else on Tasks may spend it.

---

## Tasks — sub-step completion

**Chosen form — the Buffered Strike (wave-1 Hit-Stop Strike, retuned for frequency).**

- **Mechanism / data source:** Purely local task state: the sub-step's `done` flag in the
  Tasks store. Tap target is the whole sub-step row inside the expanded ACTIVE block
  (hairline row, right-margin Anonymous Pro numeral), not the numeral alone — a 44 dp
  target the thumb can hit without aiming. State flips at tap; rendering is the drawn
  strike, never `TextDecoration.LineThrough`. Screen-level `HitStopState` holds
  `timeScale` (1 → 0) for Tasks' own animations only; scroll velocity at tap is stored
  and restored, never discarded.
- **The sequence:** (0) DOWN: numeral sinks one tone step and the row's hairline darkens
  — acknowledgement in ≤16 ms, before any freeze. (1) Freeze **60 ms, no curve,
  timeScale = 0** — down from wave 1's 90 ms: this tap happens ~30× a day across ten
  blocks, and 60 ms (4 frames at 60 Hz) is the shortest freeze that still reads as
  *impact* rather than lag; 90 ms at this frequency starts taxing the hand. (2) Strike
  wipe **120 ms, `CubicBezierEasing(0.7, 0, 0.2, 1)`**: the drawn line tears left→right
  at 52 % of text height, 1.5 dp, with a 2 dp triangular nib head tapering to the tail;
  80 % of the width in the first 55 ms, the last 20 % crawls. (3) Squash: row scaleY
  1.0 → 0.96 in 45 ms anchored at its baseline, spring back `spring(dampingRatio = 0.55,
  stiffness = 380)`, exactly one overshoot to 1.015, settled by ~170 ms. (4) The next
  unstruck sub-step nudges up 3 dp, 110 ms `FastOutSlowIn`, delayed 35 ms — cause and
  effect stay separate events. Total perceived ≈ **340 ms**.
- **Undo, because hands slip:** tapping a struck sub-step un-strikes it with the wipe
  reversed (right→left, 90 ms, no freeze, no knock — a correction is not an achievement
  and is never charged the celebration price). State flips identically; only the feel differs.
- **Haptics:** DOWN tick **5 ms @ 0.35** (contact). At strike completion (~t+185 ms):
  knock **13 ms @ 0.9** + **7 ms @ 0.3** immediately after — thock-and-settle; a single
  buzz reads as an error and is banned here. Un-strike: one **5 ms @ 0.3**.
- **Sound:** Explicit silence by default. If sound is on: one dry pen-scratch, 80 ms
  band-passed noise 2–4 kHz, attack 4 ms, fired at strike *start* (synced to the tear,
  not the finger). Silence during the freeze itself — the freeze is the point.
- **Depth / material:** No shadow, no elevation. The struck row drops exactly one tonal
  step darker (pressed in), the hairline below it lightens one step for 180 ms then
  returns; the strike line is drawn twice — ink line, plus the same line offset +1 dp
  one tone lighter beneath it, so the cut reads letterpressed. Night: same steps against
  the `#171412` staircase, line in the ≈5.6:1 text tier, never below it. **Zero orange** —
  START keeps the screen's only orange.

**Runner-up — the Letterpress Press (no freeze).** Row presses straight down one tone
step over 60 ms `FastOutSlowIn`, strike fades in as a pre-drawn line over 90 ms, one
**8 ms @ 0.6** haptic, done in ~200 ms. Compact, honest, and wrong for this seat: without
the freeze there is no *impact*, and impact is the entire difference between checking a
box and ticking one with a pen that bit. It also scales badly — at the final step there
is nothing left to escalate *to*. Kept as the correct fallback for reduced-motion settings,
where it should replace the chosen form wholesale rather than merely shortening it.

**Guard fork:** Only the optional scratch sound touches Guard. If Guard survives as a
destination, the sound toggle stays there. If Guard dissolves, the toggle moves with the
rest of sound into the band-switch drawer — the strike itself (motion + haptics) is not
a setting and never waits on either outcome.

**Standards check:** Arm-path cost: zero — this is queue work, off the arm path, and the
buffering rule means it never delays a subsequent START tap by more than one settle
frame. Mid-session decisions: none — one tap, one deterministic outcome, no prompt,
no branch; during a sealed session Tasks is not the live surface anyway. Truth risk:
none — the strike renders exactly the stored `done` flag, the numeral and the row agree,
and un-strike is the same flag flipped, one rendering both ways.

---

## Tasks — final-step escalation

**Chosen form — the Closing Strike. Same strike, spent like it matters, and it does
not lie about what it finished.**

- **Mechanism / data source:** Trigger is derived, not stored: a sub-step strike whose
  completion makes `doneSteps == totalSteps` for the ACTIVE task. The escalation is a
  parameter change on the same state machine, not a second animation system.
- **The sequence:** Freeze **110 ms** (the longest freeze on this screen; only session-end
  anywhere in the app may match it). Strike wipe **190 ms**, same nib geometry at 1.25×
  line weight (1.9 dp). Squash deepens to 0.94 with the same spring, settled ~230 ms.
  Then — the escalation's signature — the parent task title does **not** strike. Instead
  its row's left margin numeral (`#01`) steps up one ink tier and the title's hairline
  closes: the hairline under the title draws in left→right over **160 ms
  `CubicBezierEasing(0.2, 0.9, 0.25, 1)`**, sealing the block of steps like a bracket.
  The steps are finished; the *task* is finished only when he presses DONE, and the
  rendering refuses to say otherwise. The START word does nothing at all — no pulse,
  no invitation. It was already there.
- **Haptics:** Strike knock pair as above, then after a 35 ms gap a third pulse
  **20 ms @ 1.0** timed to the hairline closing. Three pulses total; that third pulse is
  the entire escalation in the thumb, recognisable eyes-closed.
- **Sound:** Silence by default. If on: the scratch, plus one lower paper *settle*
  (low-passed noise, 60 ms, −8 dB under the scratch) on the hairline close. No chord,
  no strum, no pitch ladder — pitch systems belong to a sound-led design, and this
  screen's grammar is impact, not melody.
- **Depth / material:** As the sub-step strike, plus the title row takes the struck
  steps' recess: title block sits one tone step deeper once all steps are done, so a
  fully-stepped task reads as *set into* the panel, awaiting its DONE. No orange.

**Runner-up — the Parent Strike (my own wave-1 proposal, now rejected).** The parent
title receives the full strike at 1.4× duration. Compact spec: freeze 90 ms, title wipe
260 ms, triple haptic. It lost on truth, not feel: striking the title renders "task done"
in the app's strongest completion language while the task's DONE state is still false and
its DONE word still sits unpressed below. One fact, one rendering — the title may only
be struck by the act that actually completes the task (next section). Escalation that
lies is theatre.

**Standards check:** Arm-path cost: zero. Mid-session decisions: none — critically, the
escalation does **not** auto-complete the task, auto-start anything, or ask "done?":
auto-anything here would either mint a completion he didn't declare or open a prompt.
Truth risk: actively reduced versus wave 1 — steps-done and task-done now have visibly
different renderings (bracket + recess vs. strike + drop), so the two facts can never
be confused at a glance.

---

## Tasks — active-task START

**Chosen form — the Loaded Hand-Off. START is a press, not a hold; its job is to put
the session in his hand on Deck, loaded, in under half a second.**

- **Mechanism / data source:** START (the one orange COMMIT fill on this screen, on the
  ACTIVE block only) writes exactly two facts — active task id + its duration — into the
  Deck's pending load, then navigates. It does **not** arm, seal, or start a timer:
  the slide on Deck remains the sole arming gesture, so START can never become a second,
  cheaper arm path that bypasses the commitment gesture. Data source is the ACTIVE task's
  stored duration; nothing is recomputed or re-derived on the way.
- **Input timing, the part that makes it feel instant:** acknowledgement on DOWN —
  the orange fill compresses: scale 0.98 about its centre + fill steps one tone deeper,
  within one frame. Commit on **release inside the bounds**; release outside cancels
  with a plain 120 ms spring back and no haptic (a slid-off press is a changed mind,
  not an error). Press duration is **ignored entirely** — no hold-to-charge, no
  long-press variant, no double-tap anything. A tap is a tap at 40 ms and at 400 ms.
- **The sequence on commit:** (1) The ACTIVE block lifts: it rises 6 dp and its tone
  steps up one toward the light while the queue rows below it step down half a tone,
  **180 ms `CubicBezierEasing(0.2, 0.7, 0.3, 1)`** — the chosen task separates from the
  list by value, not by a card or a shadow. (2) Hand-off overlap: the block holds its
  lifted state for **60 ms** (a beat of *loaded*, not loading — there is no spinner,
  ever), while the screen transition begins beneath it; the title is the shared element
  that stays sharp and travels. (3) Arrival on Deck with wheel and readout already set
  to the task's duration and the task as the NOW line; the slide sits at rest, untouched,
  waiting for his thumb. Tap-to-Deck-ready ≈ **480 ms**.
- **Haptics:** DOWN: nothing (the visual compression is the acknowledgement; a haptic
  here would spend the event before it happens). On commit: one knock **16 ms @ 1.0**,
  fired at the lift, not at the tap. Cancel: silence. On Deck arrival: silence — the
  slide's own gate ticks will speak when he drags; two confirmations for one act is noise.
- **Sound:** Explicit silence, in all sound modes. START hands over to the seal, and the
  seal already owns the arm sound. A sound here would be a trailer for another sound.
- **Depth / material:** Orange is a fill only, exactly as locked: the START fill, full
  saturation throughout the press — no orange text, no orange numeral, no orange
  anywhere else on Tasks, including during this animation (the lifted block's tone shift
  is achromatic). Recess discipline holds: lift is told by tone step + offset, never shadow.

**Runner-up — Press-and-Hold Charge.** Hold START for 350 ms while the fill drains
left→right like a fuse; release early and it springs back. Lost three ways: it taxes
the arm path (350 ms + Deck slide, every block, ten blocks a day, for zero information);
it duplicates the slide's job — commitment-by-effort is the slide's entire meaning, and
two commitment gestures in one path make both feel arbitrary; and a hold is the worst
input at 05:00 with cold hands. The slide is the hold. START stays a tap.

**Guard fork:** Not relevant — START reads no Guard state, shows no readiness, and must
never grow a permission check or a NO-GO branch under either Guard outcome. If the phone
cannot block, that fact belongs to the arm/seal path where it has always lived, not
tacked onto this tap.

**Standards check:** Arm-path cost: START + slide together ≈ 480 ms hand-off + one slide
— comfortably inside ARM ≤10 s, and START adds no wait, no hold, no confirmation.
Mid-session decisions: none; START exists only pre-seal, and it decides nothing the
rested user didn't already decide by making the task ACTIVE. Truth risk: the one real
one in this file, and it is designed out — START must never render as "session started."
It renders as *loaded*: Deck arrives unsealed, slide at rest, no elapsed second ticking.
If any build makes START start the clock, that build is lying and the hand-off is void.

---

## Tasks — whole-task completion

**Chosen form — the Ballast Drop. DONE is the heaviest event on the screen, and it is
paid in weight, not in ceremony.**

- **Mechanism / data source:** DONE on any queued or active task writes the task's
  `done` flag and its completion order; the header's DONE count is derived from that
  same store — one fact, and it changes **at the landing, not at the tap** (below).
  The horizon bar is dead as an object under settled law, so nothing here re-renders a
  chart of the list; the queue itself is the only picture of the queue.
- **The sequence:** (0) DOWN acknowledgement: DONE word sinks one tone step, ≤16 ms.
  (1) Row-local hold **80 ms, no curve** — hit-stop, but scoped to the row: the rest of
  the screen (scroll, other rows) does *not* freeze. A whole task ending shouldn't stop
  the world; it should stop *itself*, one beat, like something being let go of.
  (2) The title strikes — here, finally, the full drawn strike on the title itself,
  **150 ms**, same nib, concurrent with the first 4 dp of fall so the row dies falling,
  not before it. (3) The drop: the row leaves layout flow and falls to below the queue's
  foundation line (the heavy hairline under the last row, where done tasks rest struck
  and quiet) with **ease-in only** — gravity curve `CubicBezierEasing(0.5, 0, 0.9, 0.4)`,
  duration by distance, **280–460 ms**; an ease-out fall is the single most fake curve
  in UI and is banned here. Rotation: none (a rotating row is a cartoon; a translating
  row is stock being shelved). (4) Landing: the row settles into the done zone with a
  2 dp compression of the done stack, and **only on this frame** does the header count
  increment — count and row change in the same frame or the screen holds two versions
  of the truth for a third of a second. (5) Close-up: rows below the vacated slot rise
  to close the gap on `spring(dampingRatio = 0.62, stiffness = 300)`, ~320 ms, starting
  at the landing frame, not at the tap — the queue closes because the weight left,
  visibly caused, not because a list refreshed.
- **Active-task special case:** if the dropped task was ACTIVE, the next queued task
  does **not** auto-promote during the drop. Promotion happens after the close-up
  settles (+120 ms), as its own small event: it expands, steps up into the light tone,
  its sub-steps unfold. Two events, sequenced — collapse them into one and neither reads.
- **Haptics:** Nothing at tap, nothing during the fall. Landing knock scaled by the
  task's own minutes — the duration he set is the mass he feels: 15 m: **10 ms @ 0.55**;
  25 m: **14 ms @ 0.75**; 45 m: **17 ms @ 0.9**; 90 m: **21 ms @ 1.0** + **8 ms @ 0.4**
  at +30 ms (heavy things land twice — body, then settle). Plus one shared **6 ms @ 0.3**
  at the close-up's compression peak. Undo (tapping the done row's title area restores
  it): the row rises back over 260 ms ease-out with a single **6 ms @ 0.35** — return
  is quiet, like everything else that corrects a mistake in this file.
- **Sound:** Silence by default. If on: one low thud at landing only (sine burst ~120 Hz,
  70 ms decay + breath of low-passed noise), gain by the same minutes scale. No sound
  at tap, none during the fall — the knock *is* the landing, in both senses.
- **Depth / material:** The falling row keeps its exact flat hairline rendering in
  flight — flat thing, real physics; the contrast is the delight. Done zone sits one
  tone step below the queue ground under its 3 dp foundation line; struck titles there
  sit in the lowest text tier that stays legible (≈3.4:1 night floor — quiet, never
  unreadable). No orange anywhere in this event.

**Runner-up — the Instant Ledger Drop.** DONE flips state, the row crossfades out in
120 ms, the count increments immediately, close-up is a 150 ms linear reflow. Compact,
cheap, and it is exactly the "text just greys out" blandness this whole assignment
exists to kill, one level up. It also spends its truth cheaply: count-first-row-later
ordering means the header briefly claims a completion the list hasn't shown. Lost on
both counts.

**Guard fork:** Not relevant — no sound-mode dependency beyond the optional thud, which
follows the same fork as the sub-step scratch (Guard destination if it survives, band
drawer if it dissolves).

**Standards check:** Arm-path cost: zero; DONE is between-block queue work. Mid-session
decisions: none — no confirmation, no "start the next one?" prompt, no auto-promotion
mid-animation, no undo toast (the done row itself, sitting struck under the foundation
line, *is* the undo affordance — state as position, not a snackbar). Truth risk: the
landing-frame rule for the count, and the title strike reserved exclusively for this
act, keep every rendering of "done" in lockstep with the one stored flag.

---

## Too good to lose

**1. The ten-second tightening (running state's last minute, borrowed back).** In the
final 60 seconds of a running session, minute ticks stay silent — but entering the last
10 seconds, each second gets an escalating micro-tick, **4 ms @ 0.3 rising to @ 0.8**,
haptic only, no visual change, no sound. Anticipation rationed to the only moment time
actually tightens. It belongs to whoever owns the running state; it should not be spent
on Tasks, and it should not be lost just because it isn't mine to place.

**2. Coyote forgiveness on the Deck slide, restated as an input-timing law.** Release
at ≥85 % of slide travel arms; release at 70–85 % with velocity toward the end arms;
only a slow deliberate release below 85 % recoils. The user never notices it — he simply
finds that arming "always works when I mean it." Every commit gesture in the app should
be audited against this rule: forgiveness invisible, punishment impossible for intent.
RAW FRAGMENT as applied beyond the slide — I have not specced it for other controls,
and it should not be generalised blind.

## Mail / wall note

Inbox was empty at start; one letter arrived mid-run from Orson (Wild) relaying the
citation-requirement lift — the same order my parent had already passed to me directly,
so nothing in my work changed on its account. Read and filed into `read/`. No mail sent.
One signed thought appended to the Commons wall: the final-step truth correction —
the parent title must not be struck by its last sub-step, because steps-done and
task-done are two facts and the strike is reserved for the second.
