# Fen — Wild, rituals

My wave-1 file built a liturgy. The law now kills liturgy, and the law is right: at 03:30 a paragraph dies and only the hand remembers. What survives of my seat is the part that was never really story — **cadence**: a fixed distance, a fixed resistance, a fixed terminal sound-shape, repeated identically until the body knows it without being told anything. No dawn. No tape. No Consent Beat. No kindling that judges your pace, no threshold that admits you, no ledger to stamp. Below: three cadences — daily, once-only, weekly — each pure mechanism: state, travel, curve, haptic, silence or sound. If a beat needs a name or a meaning to work, I have cut it.

Explicit rejections, up front, so they cannot creep back in through my own file:

- **Dawn wrapper — dead.** No first-light ramp, no daybreak symbolism on the first arm. The first arm of the day is mechanically identical to the tenth. His morning already has a dawn; the app doesn't need to perform one.
- **Tape / program wrapper — dead.** No authored day-document, no executed log as identity. Routines fire or don't; the record is a receipt line, not a scroll.
- **Consent Beat wrapper — dead.** No cocked Deck, no release-as-consent ceremony, no NOT TODAY declaration. The routine lays out the Deck (wheel preset, loadout seated) and the ordinary slide arms it — the same slide as any manual arm, no special gesture, no special meaning. One gesture vocabulary for every start.

---

## Deck — the daily slide-to-arm

### Chosen form — Two-Stage Weighted Slide: free run, damped latch

**The idea in one line:** the slide's cadence lives in its *last third*. The first two-thirds are free travel; the final third is weighted — denser haptics, a hair of visual lag — ending in a latch pocket, a 120 ms dwell, and one seal thock. Same distance, same gate, same knock-pair, every arm, fast or slow. The body learns the gate at 68% the way it learns a stair edge: not by being told, by tripping on it a hundred times until it never trips again.

**Mechanism and state / data source:**
- Data: session parameters already set before the slide exists — total duration from the wheel (block length × count, live total readout), loadout from the Shield sled position, active task if one is queued. The slide reads these; it sets nothing. Its only state is `slideProgress: Float 0..1` from the pointer, and the app-level transition `IDLE → SEALED`, which is the sole point where a session record is created (start timestamp from monotonic clock + wall time for the receipt).
- Geometry: track 248 dp wide × 56 dp tall, sled 68 × 48 dp, travel 180 dp. Commit gate at progress 0.68. Seal threshold at ≥0.97. Below 0.97 on release: no seal, no record, sled returns — a released slide is a non-event in the data, exactly as it should be.
- The damped zone (0.68–1.00) does **not** slow the pointer's authority: drag stays 1:1. Weight is *rendered*, not enforced — the sled's drawn position lags the pointer target by up to 6 dp through a stiff spring (below), recovering to zero lag at rest. A fast user arms fast; a slow user arms slow; both feel the same thickening. This is the deliberate inversion of my wave-1 Kindling Slide, which gated on velocity (120–420 dp/s band, gust failure) — that design taxed a rushed morning and failed a shaking hand. Rejected: cadence must be something the gesture *passes through*, never a test the gesture can fail.
- Gate crossing flips the sled fill from neutral control fill (capped `#4A423C` at night) to the COMMIT orange — the Deck slide is one of the two legal orange fills in the app. Colour confirms what position already said; it never leads.

**Motion:**
- Drag: 1:1, no easing — the hand is the curve. Sled draw-lag in the damped zone: spring `dampingRatio 1.0, stiffness 900` chasing the pointer — critically damped, no overshoot, max ~6 dp behind at speed, caught up within ~60 ms of slowing.
- Latch snap (0.97 → 1.00): 90 ms, cubic-bezier (0.2, 0.0, 0.0, 1.0). Then **dwell: 120 ms of stillness** while latched, before the seal fires — the hand feels the stop *before* the app commits. This dwell is the daily beat; it is short enough to never register as waiting and long enough to register as arrival.
- Early release return: spring `dampingRatio 0.78, stiffness 260`, ~320 ms home. This exact spring signature is reused by every sled return in the app (see Too Good To Lose #2) so “not committed” feels identical everywhere.
- On seal: Deck ground steps one tone darker over 250 ms linear — the running state's tonal recess, functional state signalling, not a room “settling for prayer.” No other element moves.

**Haptics (ms / amplitude):**
- Free run (0–0.68): silence. Nothing. The absence is what makes the gate audible to the hand.
- Gate crossing at 0.68, once per crossing per direction: tick 6 ms @ 0.45.
- Damped zone: grain — 3 ms @ 0.18 every 12 dp of travel. Felt as thickening texture, never as discrete events.
- Latch arrival (0.97): knock 14 ms @ 0.85.
- Seal, after the 120 ms dwell: thock 18 ms @ 1.0, echo 7 ms @ 0.30 at +55 ms. This knock-pair — heavy, then its smaller shadow — is the arm signature. It recurs nowhere else at this interval (the completion pair below uses +120 ms and a lower sound; the hand distinguishes them within a week).

**Sound:** Explicit silence during the drag. At seal only: a short harmonic thock — sine fundamental ~180 Hz with one inharmonic partial, exponential decay ~220 ms, low gain — a latch, not a bell. Bells imply ceremony; this is joinery. Sound off: the haptic pair carries the beat completely; nothing is lost.

**Depth / material:** Track is a recess: one tonal step below the Deck ground (night ground `#171412`, recess below it, per the locked staircase), closed by a 1 px lighter hairline on the top edge where light would catch. Sled sits flush in the recess, one step above the slot floor, carrying a single cream marker line — the only high-contrast mark on the control, so position reads in peripheral vision. Travelled portion of the slot floor lifts one half-step behind the sled. No drop shadows anywhere; all depth is tone steps and the hairline.

### Runner-up — Slide-and-Sink: full travel, then press the sled down and hold

**Compact spec:** Slide 1:1 over 180 dp with a single gate tick at 0.9 (4 ms @ 0.4); at full travel the sled becomes pressable — press-and-hold 300 ms: sled sinks one tonal step over 80 ms `FastOut`, ticks at 100/200/300 ms (3 ms @ 0.25 each), seal knock 16 ms @ 0.95 on hold completion; sound: same thock, delayed to hold end. Depth: sink rendered as recess-deepening + bottom hairline swap, no shadow.

**Why it lost:** It is two motor programs (translate, then press) where the chosen form is one. The +300 ms hold and the press-while-gripping coordination are a real tax at 03:30 with a cold, shaky thumb, and every added gesture is a new failure mode (lift at 280 ms = nothing happened, and the user cannot tell why). It buys no truth — full travel alone already proves intent — so its cost is pure ceremony wearing a safety costume. The chosen form's dwell gets the same “arrival” feeling for free, inside the single gesture.

### Guard fork

Not relevant — the slide lives on Deck, identically, under both Guard outcomes.

### Standards check

- **Arm-path cost:** One gesture. Typical gesture 0.7–1.4 s + 90 ms snap + 120 ms dwell ≈ **≤1.7 s for the slide itself**; cold-open-to-sealed worst case (open ~1 s + wheel already preset by routine or unchanged from last session + slide) **well under the 10 s ceiling**, with ≥4 s to spare even when the wheel must be re-set by hand. No waits, no pace conditions, no retries by design.
- **Mid-session decisions introduced:** Zero — this element ends at the seal; after it, HOLD is absolute.
- **Truth risk:** Minimal and structural. No session record exists before `SEALED`; a released slide mints nothing; no readiness, charge, or warmth state is displayed anywhere, because none is real data.

---

## Deck — the once-only completion beat

### Chosen form — Terminal Stillness, then One Thock

**The idea in one line:** a session ends exactly once, and the ending is marked first by *nothing* — 400 ms of absolute stillness — then by a single low thock and a single receipt line. Scarcity is the mechanism: because this beat can never fire twice for one session, and never fires for anything smaller than a full session ending, it stays legible on day 200 the way a notification sound does not.

**Mechanism and state / data source:**
- State machine with a persisted spine: `RUNNING → COMPLETING → COMPLETED`, keyed by `sessionId`. The transition to `COMPLETED` writes `sessionId + completedAt + actualElapsed` (monotonic delta, so early-ended sessions tell the truth) to the session store **in the same transaction that stops the timer, before any animation runs**. The beat (stillness + thock + receipt) is a rendering of that already-committed transition, guarded by a persisted `beatPlayedForSessionId`. Recomposition, rotation, process death, reopening the app: none can re-fire it. If the app was dead at zero, the next open renders the session already `COMPLETED` with its receipt line present and the beat silently skipped — once-only in fact, not once-only-if-the-animation-happened-to-run.
- At zero the numerals freeze at `00:00`. No negative counting, no overtime prompt, no “extend?” — HOLD ends; it does not negotiate its own extension.
- The receipt is one line, the session's only rendering, set below the readout in Anonymous Pro, letterpressed: `50 MIN · STUDY · ENDED 05:50`. For a session ended via the banked emergency break and not resumed to term: `ENDED EARLY · 31 MIN OF 50 · ENDED 04:41` — actual elapsed, planned duration, plain words. The receipt states this session only: **no day total, no week total, no cumulative figure** — YPT owns study totals; Monolith's receipt duplicating them would be one fact in two homes. The receipt persists on Deck until the next arm replaces it or the user navigates away. No button is required to dismiss, file, confirm, or close it.

**Motion:**
- 0 ms at zero: freeze. **400 ms: nothing moves, nothing sounds.** This held stillness is beat one — after fifty minutes of a creeping timer, a screen that stops entirely is the loudest event available, and it costs zero assets.
- +400 ms: thock (below) as the Deck ground steps back up one tone to idle over 600 ms `LinearOutSlowIn` (the exact reverse of the arm's 250 ms recess, slower — endings are allowed the extra 350 ms because they are not on any path).
- Receipt line: fades/rises 6 dp over 200 ms `FastOut`, beginning with the tone restore. No bounce, no overshoot, anywhere in this element, ever.

**Haptics:**
- During the 400 ms stillness: silence — deliberately. A vibration at zero would fill the beat and halve it.
- At +400 ms: knock 20 ms @ 1.0, second knock 9 ms @ 0.40 at **+120 ms**. Same family as the arm pair, wider interval, heavier first strike: start reads as *latch-and-shadow*, end reads as *weight set down*. 
- Intra-session block completions are explicitly **not** this beat: a block boundary gets only a 6 ms @ 0.35 tick and the hub pip advancing. If blocks knocked like endings, endings would mean nothing by the third block of day one.

**Sound:** One low thock at +400 ms — sine fundamental ~120 Hz plus a ~312 Hz partial, exponential decay ~900 ms, low gain, played exactly once, never looped, no melody, no bell family. If sound is off, stillness + haptic pair is the complete beat; the design does not degrade.

**Depth / material:** No orange — completion is not a COMMIT, and the accent budget stays clean. No card, no sheet, no stamp. The receipt is type on the Deck ground with one hairline ruled above it (drawn 0→100% width over 300 ms as the line lands): letterpress, flush, part of the panel. Ground tone restore is the only “material event.”

### Runner-up — Held Close: press-and-hold the receipt to file the session

**Compact spec:** At zero, receipt line appears with an outlined END disc (54 dp); press-and-hold 800 ms (grain ticks 3 ms @ 0.2 at thirds, disc fills one tone step progressively) files the session: settle knock 18 ms @ 1.0, entry steps one tone darker, receipt marked closed. Un-held sessions remain at 70% tone in a pending state until held.

**Why it lost:** It is my wave-1 ledger stamp with the ledger filed off, and it fails on every settled law at once. It demands a deliberate act from the user at the single most depleted moment of the session — the rested-user-decides / tired-user-executes law inverted. It creates an unfiled backlog, which is a Ledger wearing a fake moustache (tab killed; collections dead). Worst, it poisons the record: if filing requires the hold, an un-held session is either falsely incomplete or the hold is decorative — decorative is readiness theatre, falsely incomplete is a lie. The chosen form records truth at zero with zero asks, and gets its weight from stillness instead of labour.

### Guard fork

Not relevant — the beat and its receipt live on Deck under both Guard outcomes. Nothing about completion belongs in Guard in either world.

### Standards check

- **Arm-path cost:** Zero. The beat fires after a session, never on the path to one. Its 400 ms stillness taxes nothing; the next arm is available the instant the tone restore begins and does not wait for the receipt animation.
- **Mid-session decisions introduced:** Zero. The beat is post-HOLD; it presents no choices — no extend, no file, no rate, no dismiss-or-else. The emergency-break early-end branch is a state the settled banked-break law already creates; this element only renders its truth.
- **Truth risk:** The main risk in this whole file, engineered shut: persisted idempotent completion (no double-fire, no ghost replay), actual-elapsed rendering for early ends (no planned-duration fiction), and a receipt scoped to this session only (no duplicated totals competing with YPT).

---

## Shield — the weekly rhythm: the break-bank sled

### Chosen form — Week Sled: the bank returning is the week's only beat

**The idea in one line:** Monolith has exactly one true weekly fact — the banked emergency break: 60 seconds, banked in 15/30/45 checkpoints, one use per week. Every other “weekly rhythm” I could invent (streaks, strips, totals) either duplicates YPT or builds a collection the law has killed. So the weekly cadence is this object's physical cycle: a small sled in Shield that sits spent all week after its one use, and — on the first open of a new week — travels back to full in a single 650 ms return, ticking past the same checkpoints it banks by. The week has a felt edge because one real object comes back, once, and only when it was actually gone.

**Mechanism and state / data source:**
- Sole data source: the break-bank store — `allowanceWeekId` (ISO week, Monday 00:00 local), `bankedSeconds` progress, `usedThisWeek` flag. The sled renders this store and nothing else. No attendance inference, no session counting, no study data.
- Object: horizontal mini-track 160 dp × 36 dp in Shield, directly above the routine rows (the week's resource read next to the week's pattern — one column, one glance). Scale printed *on* the track: hairline ticks at the 15 / 30 / 45 / 60 positions, labelled in Anonymous Pro micro-caps beneath (`15 · 30 · 45 · 60`). Sled 34 dp wide, flush in its recess; **position is the balance** — hard right stop = 60 s available, hard left stop = spent. State readable from silhouette across the room; the numerals merely confirm.
- Consumption (during a session, via the settled emergency-break hold on Deck): the Deck during a break shows only the break's own countdown as giant numerals — that is a different fact (time left *in this break*) in its rightful home. The bank's balance is never re-rendered on Deck; its one home is this sled. On next Shield view after a use, the sled already sits at its drained position — it does not animate its loss for an audience.
- Weekly return: on first app open where `currentWeekId ≠ allowanceWeekId`: if the sled was spent or partially banked, it animates to full over **650 ms, spring `dampingRatio 0.85, stiffness 110`** — slightly underdamped-suppressed, heavy, machinery resetting. It ticks as it passes each printed checkpoint. If the break was never used, **nothing animates and nothing announces**: the sled was already full; a new week over an untouched bank is a non-event, and the app says so by silence. No notification, no badge, no banner, ever — the return is seen when he next looks at Shield, at letter-speed, and that is fast enough for a fact that cannot be acted on mid-session anyway.
- The reset is never on the arm path: if the week's first act is a Deck arm, the arm proceeds untouched; the sled's return happens whenever Shield is next composed, using the already-rolled store value.

**Motion:** Weekly return 650 ms spring as above (the only spring of this size in the app — weekly things move weekly: slower, heavier). All other sled changes are instant state renders (position set from store on composition, no animation) — animating consumption would be theatre. Day-to-day, the object is still; its stillness is what makes the one weekly travel mean something.

**Haptics:** During the weekly return: tick 4 ms @ 0.25 as the sled passes each of 15/30/45, arrival knock 12 ms @ 0.60 at the full stop. Consumption: no haptic from this element (the break hold has its own settled feel; two owners, one gesture is a bug). Otherwise: silence.

**Sound:** Explicit silence, total, for this element — including the weekly return. A weekly event that chirps becomes a chore alarm inside a month; a weekly event you *notice in the hand and eye when you arrive* stays an object. The knock above is haptic, not audio.

**Depth / material:** Mini-track recessed one step below the Shield ground, 1 px top hairline, sled flush, fill capped at `#4A423C` (night), marker line constant cream. Checkpoint ticks are hairlines printed into the slot floor. **No orange** (the bank is not a COMMIT; orange in Shield chrome is banned outright) and, as everywhere, no green, no shadows.

### Runner-up — Seven-Day Strip: one cell per day, filled by sessions run

**Compact spec:** A 7-cell hairline strip in Shield (Mon–Sun), each day's cell filling one tone step when ≥1 session ran that day (source: session store), today’s cell recessed; week rollover clears the strip in a 400 ms left-to-right wipe; silent, no haptics.

**Why it lost:** It is a streak with better typography. It duplicates the study record YPT already owns (one fact, two homes — and this home would be the worse one, since Monolith's session store cannot know about study done outside the app, making empty cells *false* on exactly the days he studied unarmed). It manufactures a zero-day event the law doesn't ask for, invites day-100 collection-thinking straight through the door the Ledger's death closed, and its weekly wipe is pure theatre over data it shouldn't hold. The Week Sled survives because its underlying fact (the bank) is Monolith's own, exists nowhere else, and would need a home even if nobody cared about rhythm.

### Guard fork

**His call — placement stated for both outcomes:**
- **If Guard is dissolved:** the Week Sled stays exactly as specced in Shield, and the bank's policy facts (reset day, one-per-week) live as one plain text line in the Shield footer area, state-as-words: `BREAK BANK · RESETS MONDAY · 1 PER WEEK`. No settings object is drawn for them.
- **If Guard is kept as a destination:** the sled readout **still stays in Shield** — it is a weekly instrument he should pass daily, not a setting he visits. Guard holds only the policy controls (e.g., reset-day choice, if he ever wants one; default Monday), and must never render a second seconds figure or a duplicate sled. One bank, one rendering, one home — under either structure.

### Standards check

- **Arm-path cost:** Zero. The sled is never on Deck, never gates a slide, and its weekly return animation neither blocks nor precedes an arm.
- **Mid-session decisions introduced:** Zero new ones. The emergency break it meters is the settled banked exception to HOLD, created by Sarthak's own law; this element adds no prompts, no mid-session balance display, and no negotiation surface — the balance is viewable only in Shield, where no session is running.
- **Truth risk:** Low, by scoping. Position derives directly from the bank store — the same store the break hold debits — with printed ticks at exactly the banked checkpoints (15/30/45/60), so the rendering cannot drift from the mechanism. The rejected runner-up's risk (false empty days) is precisely what this scoping avoids: the sled claims nothing about study, attendance, or effort. It says one thing — *the break is there, or it is spent* — and that thing is true.

---

## Too good to lose

**1. The block pip sub-beat (Deck running state).** Unchosen above only because the completion beat's scarcity argument quarantined it — but between blocks inside a session, something must mark the joints, and this is the right size: when a block completes and the next begins, the hub's count pip advances one position (position = count, no numeral change anywhere), with a single 6 ms @ 0.35 tick and an 80 ms crossfade of the giant readout from the finished block's `00:00` to the next block's full duration. Motion: none beyond the crossfade — the wheel does not re-spin, the ground does not move. Sound: silence. It gives a ten-block day a felt internal rhythm — ten small joints, one ending — without spending the terminal thock ten times and bankrupting it.

**2. The universal return spring (all sleds, app-wide).** The arm slide's early-release return — spring `dampingRatio 0.78, stiffness 260`, ~320 ms — adopted verbatim as *the* return curve for the loadout sled, the Week Sled's manual interactions, and any future sled: every “not committed / going home” motion in the app feels like the same physical object being set back on the same bench. One curve, learned once, recognised everywhere; commitment motions differ per control, but *uncommitment* is one gesture family. Cost: nothing. It is a token, not a feature — which is exactly why it would be a shame to lose it in a per-screen rebuild.

---

## Mail / wall note

Inbox: **empty at start** (`mail/fen/` held only its empty `read/` subfolder). During the run, two relay letters arrived — from Orson (05:14) and Mara (05:17), both carrying Sarthak's citation-lift order, which had already reached me directly; both read before finishing and moved to `read/`. The deliverable accordingly cites no references anywhere — not as compliance theatre, but because none of these mechanisms needed one. Mail sent: **none** — no thought in this file belonged to one agent rather than the deliverable. Wall: read in full before writing (Ren's verdict entry — *the app has no idea* — is the ground this file stands on, and Dev's collision demand that deviation be free shaped the slide's refusal to gate on pace). One signed note appended to the Commons with the cadence argument in short form.
