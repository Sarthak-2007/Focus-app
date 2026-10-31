# SCOUT FINDS 5 — Time & Dial Instruments
## Clocks, timers, chronographs, rotary dials, and time-progress displays at the highest craft level

**Best serves: GitHub Issue #6 — Deck**
Reason: the Deck is being redesigned so the dial *becomes* its own clock — showing session end and remaining time, not a timer widget bolted onto a clock face. Every find below is chosen for that exact fusion, plus Monolith's always-on desk-clock study display (elapsed + remaining, shifting against burn-in). Secondary value to #4 (the strip) is noted only where a find genuinely earns it.

Scout ground: time and dial instruments only. No generic focus-app roundups, no dashboard design.

---

## 1. Swiss Federal Railways Station Clock — Hans Hilfiker, 1944 / Mondaine

**Source:**
- https://ch.mondaine.com/en/blogs/news/die-geschichte-hinter-der-ikonischen-schweizer-bahnhofsuhr
- https://www.detailed.works/mondaine-swiss-railway-clock

**What makes it elite:**
Designed for one brutal test: a passenger sprinting across a crowded platform must read it instantly, at distance, in peripheral vision. Hilfiker stripped everything else away — bold black bar indices on white, geometric hands, no numerals competing for attention. The red second hand is not decoration: it is modelled on the dispatcher's signalling paddle ("Rote Kelle"), so the single colour accent *means* departure / authority. Hidden genius: the Stop-to-Go movement — the second hand sweeps a full revolution in slightly under 60 seconds, then *pauses* at 12 before jumping on. The clock visibly waits for the minute, which is why Swiss trains feel synchronised to it. It has survived 80+ years unchanged and sits in London's Design Museum as a working exhibit.

**What applies to Monolith — concretely:**
- **The 3-metre test for the Deck.** If session-end cannot be read from across the room with no numerals, the dial is too fussy. Bar indices, one hand weight for hours, one for remaining — nothing else earns a place.
- **One accent colour, one meaning.** Reserve Monolith's single accent exclusively for the *end point* — the tick / hand position where the block lifts. Never use that accent for branding, buttons, or decoration elsewhere on the Deck. Like the red paddle, it should come to mean "release."
- **Steal Stop-to-Go for the last minute.** In the final 60 seconds, let the remaining-time hand complete its sweep early and hold, trembling/paused at the top for the last ~1.5 seconds before the block ends. That pause makes the end feel *official* and synchronised, not like a timer that just ran out. It turns expiry into a ceremony.
- **AOD desk clock:** this is the face to clone for legibility — but inverted for OLED (dark ground, light indices). Its proportions already solve distance readability; do not redesign them, rescale them.

---

## 2. Braun Travel Alarm Clocks — Dieter Rams & Dietrich Lubs (AB1 / BNC001 lineage)

**Source:**
- https://www.cooperhewitt.org/2018/02/24/ahead-of-the-times/

**What makes it elite:**
Cooper Hewitt's account nails the discipline: Rams' rule was to "omit the unimportant in order to emphasize the important." Economic use of colour — every Braun clock shares the *same* yellow second hand and the *same* alarm sound, so the brand lives in two tiny, consistent signals, not a logo. Type is Akzidenz-Grotesk, chosen purely for legibility. Lubs even standardised the photography position (10:08) because it framed the dial most attractively. And the design considered the half-asleep user: a ridged side lets you find and kill the alarm by touch, in the dark, groggy. Apple famously borrowed this face for the iOS 7 Clock app — the strongest possible proof of transfer to a phone screen.

**What applies to Monolith — concretely:**
- **Two-signal identity for the dial-clock.** Pick (a) one hand colour used *only* for the seconds/remaining hand across Deck + AOD, and (b) one sound used *only* for session end. Nothing else in the app may use either. Consistency across years is what makes an instrument feel like an instrument.
- **Design for the groggy / resistant user, not the motivated one.** The person staring at the block screen at hour 3 is the equivalent of Rams' half-asleep user. The Deck must be operable and readable with zero reading: hand position alone tells "how much is left," the end tick alone tells "when."
- **Tactile ridges → haptic detents.** When the user drags the Deck dial to set a duration, add a haptic tick every 5 minutes and a heavier tick every 30 — the digital version of Braun's ridged edge. Setting time should be doable eyes-half-closed, by feel.
- **Set a canonical "hero" dial position** (the equivalent of 10:08) for all screenshots, the launcher preview, and the idle Deck — e.g. hands framing the Monolith wordmark symmetrically. Instruments look designed when their resting pose is designed.

---

## 3. Junghans Kitchen Clock & Wall Clocks — Max Bill, 1956–1959

**Source:**
- https://junghans.de/en/news-presse/bright-and-friendly-then-as-now/
- https://revolutionwatch.com/max-bill-tells-time/

**What makes it elite:**
Bill's 1956 Küchenuhr is the only classic clock that is *two instruments in one body by design*: a wall clock **plus a built-in short timer**, and Bill insisted on a radical labelling split — in his own words, "the hours on the minute track — and the minute markers on the timer." He demanded numerals (against Bauhaus purists who wanted bare indices) because the kitchen clock "teaches children to tell the time, to read the first numbers, the order of hour and day." Bright, friendly, ceramic, unprecious — yet the typography he drew for it is still in production essentially unchanged, nearly 70 years later.

**What applies to Monolith — concretely:**
- **This is the direct blueprint for a dual-scale Deck dial.** Outer scale = wall-clock time (hours, numerals, where the session *ends* in real time). Inner scale = the short timer (minutes remaining, tick marks only). Two scales, two different jobs, two different labelling densities — exactly Bill's split. Do not put the same kind of mark on both rings; that is what makes dual dials unreadable.
- **Numerals only where learning matters.** Put numerals on the outer (clock) ring so the end time is unambiguous — "blocked until 4" must never require counting ticks. Keep the inner remaining ring numeral-free; proportion is felt there, not read.
- **Warmth is a feature, not a compromise.** Bill proved a precision instrument can be "bright and cheerful, like beautiful dishes." Monolith's study display runs for hours in a bedroom/desk environment — a dial that feels like a household object, not a cockpit gauge, is more likely to be left on and lived with.

---

## 4. Time Timer — The Disappearing Red Disc (Jan Rogers)

**Source:**
- https://www.timetimer.com/collections/all-1/products/time-timer-12-inch

**What makes it elite:**
The most influential visual-timer design ever made, and it contains *no numbers counting down at all*. You turn the centre knob counter-clockwise; a red disc covers the fraction of the next 60 minutes you have claimed. The disc then silently disappears clockwise. Remaining time is not a number to compute — it is an *area* to see. That is why it became the standard in classrooms and for ADHD/autism support: it works pre-numerate, at a glance, from the back of a room, with zero ticking (silence is a specified feature, the end alert is optional). 30+ years, essentially one idea, perfectly executed.

**What applies to Monolith — concretely:**
- **Remaining time as area, not digits, on the Deck.** The dial-clock should carry a filled sector (a wedge/disc segment) whose area *is* the time remaining in the session. As the session burns, the sector shrinks. A student mid-block should feel "about a third left" from the wedge before they ever read a number. Digits become the backup channel, not the primary one.
- **The knob gesture is the setting gesture.** Time Timer is set by physically rotating the disc to the duration. Mirror it: arming Monolith = rotating the Deck dial until the wedge covers the intended block, then it starts unwinding. Setting and display are the same object — no separate picker screen.
- **Silence by default.** No tick sounds, no per-minute nudges. The only sound in the whole system is the end signal (see Braun, Find 2). Anxiety comes from audible time; calm authority comes from visible time.
- **For sessions >60 min:** Time Timer's honest limit is one revolution. Solve it the instrument way — the wedge is "this hour," and a small revolution counter (dots / a numeral by the centre) shows how many full turns remain. Do not rescale the wedge to 4 hours; that destroys the at-a-glance proportion, which is the whole point.

---

## 5. Session — Pomodoro Timer with a Rotating Hand (stayinsession.com)

**Source:**
- https://stayinsession.com/learn/getting-started-with-session-pomodoro-app
- https://apprecs.com/ios/1521432881/session-pomodoro-focus-timer

**What makes it elite:**
The best modern proof that a *dial*, not a form, should start a focus block. Session's own guide: "You can adjust Session duration or break duration by **rotating the timer hand**. Rotating clockwise will start a Session, and counter-clockwise will start a break." Direction encodes intent — one gesture, two meanings, no mode switch. Around that dial it builds a ritual other timers lack: state an intention (which is then shown *everywhere* — widget, Live Activity, blocker), take a breath, then focus; different background sounds per phase (focus / overflow / break) so you know where you are with the phone face-down; a gentle presence reminder every 10 minutes; reflection ("what did you learn?") logged at the end.

**What applies to Monolith — concretely:**
- **Direction-as-meaning for the Deck dial.** Clockwise rotation arms a block; counter-clockwise could preview/inspect the schedule without arming. If a gesture has a natural physical metaphor, never waste it on a settings menu.
- **Overflow is a first-class phase, not an error.** Session names and sounds the state *after* the timer ends but before you stop. Monolith needs the same honesty: when a block ends, the dial should visibly enter a distinct "ended — awaiting release" state, not silently flip to unblocked.
- **Intention on the dial face.** Session shows the session's intention in every surface. Put the current block's label (routine name / "NEET — Block 3") *inside* the Deck dial, small, at the centre — so the clock always answers both "how long?" and "what is this time for?"
- **Phase sound, sparingly:** one soft, non-alarm presence tone is permissible mid-block only if it is the *same* tone every time and never escalates. Session's lesson is recognition, not urgency.

---

## 6. A Million Times / ClockClock — Humans Since 1982

**Source:**
- https://www.fastcompany.com/3035893/this-kinetic-wall-of-clocks-is-utterly-hypnotic
- https://www.designboom.com/design/a-million-times-by-humans-since-1982/

**What makes it elite:**
288 identical two-hand analogue clocks, 576 independently motorised hands. Most of the time the hands choreograph abstract waves and patterns — then, on the minute, they snap into formation and the wall *becomes* a giant digital display spelling the time. The studio's own framing: the clocks are "locked in [their] functionality to show the time," yet between duties the hands are free to dance. It is exhibited worldwide (Cooper Hewitt, Vitra, Changi Airport at 504 clocks) because it solves the deepest problem of a time display: what should it do *between* the moments you actually read it?

**What applies to Monolith — concretely:**
- **This is the answer to the AOD burn-in problem — make motion the idle state, not a screensaver bolted on.** Monolith's desk-clock already shifts position against burn-in; go further, ClockClock-style: between minute changes, let the dial's hands/indices drift through a very slow, low-contrast choreography (a breathing wave around the ring) and *resolve* into the exact elapsed/remaining display on the minute. The display is never truly static, so burn-in protection and beauty become the same mechanism — pixels move because the artwork moves, not because a shift timer fired.
- **Keep the choreography amplitude tiny and slow.** A Million Times works at gallery distance; Monolith runs 40 cm from a studying user. Adapt the principle at 5% of the drama: sub-degree hand drift, brightness breathing within a narrow band. Hypnotic at a glance, invisible while concentrating.
- **Two truths, one surface.** The installation alternates abstract pattern ↔ digital time. Monolith's AOD can alternate the same way on a long cycle: clock face (elapsed) ↔ remaining wedge (Find 4) — same dial, two readings, cross-faded every minute, so neither layout ever sits still long enough to burn in.

---

## 7. QLOCKTWO — Biegert & Funk

**Source:**
- https://quillandpad.com/2020/01/05/word-for-word-qlocktwo-presents-a-new-approach-to-telling-the-time-reprise/
- https://www.designboom.com/design/biegert-funk-qlocktwo-2/

**What makes it elite:**
A clock with no hands, no numerals, and no digits: a 110-letter matrix lights up to *say* the time — "IT IS HALF PAST SEVEN" — in five-minute steps, with four corner dots adding the exact minutes in between. It won major international design prizes because it changes the user's relationship to time: you *read* a sentence, which takes a beat longer and lands calmer than glancing at digits. Precision is deliberately coarse (5 minutes) with an exactness escape hatch (the corner dots) sitting quietly at the edges. Auto-brightness via ambient sensor is built in — it dims itself for the room.

**What applies to Monolith — concretely:**
- **A words-mode for the AOD desk clock.** Offer the study display as a sentence — e.g. a line under the dial reading in words when the block ends / how much remains, refreshed in 5-minute steps. Students clock-watch digits; a sentence that only changes every 5 minutes actively discourages checking and lowers the temperature of a long block. Digits stay available on tap — coarse by default, exact on demand, exactly QLOCKTWO's hierarchy.
- **Corner dots as a design pattern.** Four quiet dots at the Deck's corners can carry the sub-state nobody should have to open a menu for (e.g. dots = quarters of the session elapsed, or parachutes remaining) — peripheral, learnable in a day, invisible to anyone who hasn't learned them. Keep them out of the dial itself.
- **Ambient auto-dim is non-negotiable for an always-on display.** QLOCKTWO treats the light sensor as part of the clock, not a phone setting. Monolith's AOD should own its brightness curve (dimmer at night, never full-bright static white) — this is also burn-in and battery protection, not just comfort.

---

## 8. The Rotary Cellphone — Justine Haupt

**Source:**
- https://www.designboom.com/technology/rotary-cellphone-justine-haupt-02-13-2020/

**What makes it elite:**
An astronomy instrumentation engineer built herself a working mobile phone around a real rotary dial from a Trimline phone — because, in her words, she wanted something "entirely mine, personal, and absolutely tactile" in "a finicky, annoying, touchscreen world." The dial is slow on purpose: dialling is a deliberate, resistant, click-return act with no backspace — a wrong digit means starting over, so you commit *before* you act. Supporting details are equally considered: a physical sliding power switch (no long-press ambiguity), an e-ink display for caller ID (readable, near-zero power, no glow), an LED bar graph for battery/signal, and mechanical speed-dial buttons for the people called most — the dial is reserved for the intentional act, shortcuts for the routine ones.

**What applies to Monolith — concretely:**
- **Arming a block should feel like rotary dialling, not tapping "Start."** The Deck dial gesture should have resistance built into the interaction: a full, deliberate rotation (with detents, see Find 2) that cannot be completed accidentally in a pocket or with a stray tap, and — Haupt's hardest lesson — no instant undo inside the gesture itself. Commitment devices fail when starting them is as frictionless as the distraction they block.
- **Presets as "speed-dial buttons."** Routines are Monolith's speed-dial: fixed, physical-feeling preset positions around or under the dial for the blocks started every day (morning block, night block), while the free dial remains for one-off durations. Routine = one press-and-hold; custom = rotate. Never force the ritual for the routine, never make the custom as easy as the routine.
- **E-ink thinking for the AOD:** Haupt chose e-ink because it informs without glowing or demanding attention. Translate to OLED as: the desk-clock should read as *printed*, not *lit* — matte, low-luminance, no pulsing elements, no badges begging to be tapped. A study display that glows like an app will be treated like an app; one that reads like paper becomes furniture.
- **A physical-feeling off/on truth.** The sliding switch always tells the truth about state. Monolith's dial must do the same: from hand/wedge position alone — blocked, in break, ended-awaiting-release (Find 5) — with no reliance on a status label. If you have to read text to know whether you're blocked, the dial has failed.

---

## Cross-cutting spec — what the 8 finds agree on

1. **Proportion before numbers.** Railway clock (indices), Time Timer (area), QLOCKTWO (words) all make the *felt* amount of time primary and the exact figure secondary. Deck order of information: wedge/hand position → end tick → words → digits last.
2. **One accent, one meaning, forever.** Braun's yellow hand + single alarm sound; Hilfiker's red paddle. Monolith: one accent reserved for "release/end," one end sound, used nowhere else.
3. **Dual scales must do dual jobs.** Max Bill's split (hours on one track, minutes on the other) is the only honest way to show wall time *and* remaining time on one dial.
4. **Motion is the burn-in strategy.** Humans Since 1982 + QLOCKTWO's auto-dim: choreograph slow drift and resolve on the minute, own the brightness curve — don't rely on a crude pixel-shift alone.
5. **Arming is a ritual with resistance.** Rotary dial, Session's rotate-to-start, Time Timer's knob: setting time and showing time are the same physical object, and starting a block should cost a deliberate gesture.

— Zeiger (Scout 5)
