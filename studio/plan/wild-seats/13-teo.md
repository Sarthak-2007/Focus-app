# Teo — Wild, nature

*Lens: natural accumulation / stratigraphy. Method note, because the critics were right to attack my wave-1 seat and I am not repeating its error: in wave 1 I drew sediment as a picture. Here nothing is drawn because it looks like rock. Every mark below exists because a real stratigraphic convention encodes a fact no other mark on Shield encodes — thickness is duration at a declared scale, grain density is consolidation state, a scoured seam is missing time, a pinch-out is a bed that was never laid. Where a texture would carry no data, none is drawn: no marbling, no rock colours, no landscape. Material truth over metaphor, enforced as a drawing rule.*

*Citation status, per Sarthak's direct order passed down verbatim: “the citation requirement is LIFTED. No seat needs to cite design references or anything else. Full freedom and creativity for you and all 20 seats — the reference library, wave-1 files, and renders are there if a seat wants them, never as a quota. What still binds is only what is HIS: the settled decisions (Ledger dead, banked hold, routines his, Guard open) and the three standards (ARM ≤10s, HOLD absolute, TRUTH once). Form, method, and ideas are yours.” Nothing below is cited. What binds below is only his settled law.*

---

## Shield — the 24-hour structure

### Chosen form: THE BEDDED DAY — a stratigraphic stack of today's routine windows

**What it is.** A single horizontal band at the top of Shield, above the routine rows, in which today's routine windows are drawn as sedimentary beds in one shared time axis. Beds are laid in advance by his routine definitions (the plan), and each bed *lithifies, thins, or erodes* as real attendance data arrives (the record). Plan and record are the same drawing at different hours — which is exactly how a cliff face works, and exactly why this is not a chart: a chart is redrawn from numbers; this stack is *changed by events* and never redrawn.

**Geometry — the axis is his day, not the calendar's.**
- Time axis runs left → right across the band's full content width, spanning **[03:30 → 03:30 next day)** — his wake-to-wake day. On this axis the SLEEP window (20:00–03:30) is one contiguous bed at the right end; nothing wraps, nothing splits. Micro-labels at both ends read `03:30` in Anonymous Pro 9sp, tone tier 3 (≈3.4:1). This is a truth decision, not a style one: a 00:00 axis would cut his night in half and call the halves two days.
- Horizontal scale: full 24h across the available width (≈13dp/hour at 320dp content width). A bed's left edge = its start, its width = its duration, both at this one scale, no exceptions.
- Vertical position = **deposition order**: the day's first window (FASTED 05:00–08:00) is the bottom bed; each later window stacks on top of the one before. Later sits on earlier, always — superposition is the one rule the drawing never breaks, so the stack literally grows upward as his day accumulates.
- Bed thickness = duration at a **declared second scale: 3dp per hour, minimum 6dp**, printed on the band itself in Anonymous Pro 9sp at the lower right: `BED ∝ TIME · 3DP/H`. Geological sections declare their vertical exaggeration on the drawing; an undeclared second scale would be a fake scale, and fake scale is banned. Indicative thicknesses on his reseeded day: FASTED 3h → 9dp, DAY BLOCKS 10.5h → 31dp, SLEEP 7.5h → 22dp; total stack ≈ 68dp including seams, band height 84dp. The day's mass is readable at a glance *because* the proportions are true: the day block is visibly the mountain; sleep is visibly the second mass; the fasted block is a seam of a thing by comparison — which is what his day actually looks like.

**States — every bed is in exactly one, driven only by data.**
Data sources: routine definitions (id, start, end, active days, loadout, enabled) from the routines store; sealed-session intervals from the session log; the clock. No other input exists.
1. **UNLAID (routine OFF, or not scheduled today).** The bed *pinches out*: two 1dp hairlines converge across its time interval to a single line and separate again — the stratigraphic drawing of a bed that thins to nothing. Not greyed, not dashed (greying is an editor's state; dashed is the Section strip's state, and this band does not borrow it). A pinched bed contributes 0dp to the stack height.
2. **LOOSE (laid, window not yet closed).** Drawn at full planned thickness. Fill = its loadout's tone step (below). Grain: deterministic 1dp dots, seed = routine id + date, at ±1 tone step inside the fill, **~55% density**, and a ragged top edge (path, ±1dp jitter, seed-fixed). Loose means unconsolidated — the grain count is the state, not decoration.
3. **LITHIFIED (window closed, attended).** At window close, coverage = sealed-session minutes inside the window ÷ window minutes (for SLEEP: minutes the device spent sealed inside the window). The bed compacts to `planned thickness × max(coverage, 0.35 floor for any attendance > 0)` — the floor exists because a bed that was entered at all is not the same fact as a bed never entered; below the floor the thickness would lie by implying absence. Grain compacts to 100% density, top edge flattens to a hard 1dp hairline one step darker than the fill. A fully attended bed and an 80%-covered bed differ by exactly their true difference in thickness — no rounding to "done."
4. **ERODED (window closed, zero attendance).** The bed is removed. What remains is a **scoured seam**: one 2dp wavy hairline (fixed ±1dp wave, seed = routine id) at the bed's former base, and every bed above it settles down by the removed thickness. A missed routine does not leave a hole or a red mark — it leaves an erosion surface and a shorter day. This is the honesty centre of the piece: the stack at 21:00 is shorter on a bad day, by the true amount, and nobody has to read a number to feel it.
5. **FAULTED (two ON windows overlap in time).** Where the upper bed's interval overlaps the lower's, the lower bed's overlapped segment is displaced down 3dp and a single diagonal hairline (the fault) cuts both at the overlap's start edge. No label, no colour — the rows below already carry the conflict in words (`HELD — MANUAL SESSION` convention); the band shows the same fact geometrically, once, in its own home. Fixing the routine in the rows un-faults the band on the next data pass.

**The now-line.** One vertical 1dp hairline in cream ink (text tier 1), spanning the stack's height at the current time's x position, topped by a 3dp tick on the band's top edge. It crosses whichever bed is live. **Explicitly not orange**: orange is a COMMIT fill on Deck's slide and Tasks' START and appears nowhere in chrome or on Shield — a previous Shield proposal spent orange on a now-line; that spend is dead under current law and this band does not revive it. The line advances once per minute in a 600ms linear step (see Motion).

**Loadout tones — tone steps only, no hues.** STUDY = the band's mid surface step; DEEP = one step darker than STUDY; SLEEP = one step paler than STUDY. All fills stay inside the locked night staircase (ground `#171412`, steps ≥1.35:1, no fill above `#4A423C` at night) and the equivalent day steps. Loadout is also printed in words on the rows below; the tone here carries *grouping* (beds of a kind read as one geology), which is a different fact from the word, so this is not a duplicate rendering.

**Mechanism / build sketch.** One `Canvas` in a recessed tray (tray = one tone step below Shield's ground, 1dp light-catch on its bottom edge only, 8dp corners per the radius freeze; beds themselves square-cornered, clipped to the tray). Redraw triggers are data events only: routine CRUD, window open/close, session seal, minute tick for the now-line. Grain dot positions are precomputed per bed per date from the seed — recomposition never re-rolls them. The band is **non-interactive**: no taps, no long-press, no tooltips. It is a drawing of record; all control lives in the rows beneath it, where it already lives.

**Motion.**
- *Laying a bed* (routine created or re-enabled, rested state only): grains fade in loose over 350ms, `FastOutSlowIn`; thickness grows from the 2dp seam to full over 400ms, `CubicBezier(0.3, 0, 0.2, 1)` — sediment settles, it does not bounce. No overshoot anywhere in this band, ever.
- *Lithification* (window closes attended): 650ms, gravity curve `CubicBezier(0.55, 0, 1, 0.45)` — thickness interpolates planned → compacted, grain density 55% → 100% locked to the same progress, top edge flattens over the final 150ms. One event, one curve.
- *Erosion* (window closes unattended): 500ms on the same gravity curve — the bed thins to its seam, top edge taking the scoured wave as it falls; beds above settle down over 450ms, staggered 30ms per bed from nearest upward. The day visibly shortens. Nothing flashes.
- *Now-line:* steps once per minute, 600ms `LinearOutSlowIn`. Between steps, dead still.
- *Everything else:* still. Settled beds never animate. Geology that idles is a screensaver; this band moves only when the day changes.

**Haptics.** Window-close events (lithify/erode) fire **only if Shield is on screen at that moment** — a haptic for an unseen drawing is noise taxed on a man who isn't looking. When visible: lithify = one packing knock, 18ms @ 0.7; erode = two falling ticks, 6ms @ 0.4, 70ms gap, 6ms @ 0.25 (material leaving, lighter than arriving). Laying a bed from his own edit: the rows' existing edit haptics govern; the band adds none. All other times: explicit silence.

**Sound.** None, in all states, unconditionally. A stratigraphic record that beeps has misunderstood itself.

**Depth / material.** Recess, never shadow: tray one step below ground, beds flush inlays inside it (finish changes and hairlines only), seams and faults are 1–2dp lines drawn in the step below the bed they bound. No elevation, no drop shadow, no texture beyond the seeded grain whose density is data.

**Guard fork.** The band is routines-only; blocking state never enters it, under either outcome.
- *If Guard dissolves:* websites and permissions land in Shield's list modules below the rows — the band sits above all of it, untouched, and Shield's order becomes: band → routine rows → apps → websites. No element moves into the band.
- *If Guard stays a destination:* Shield keeps band → rows → apps exactly as specced. The band's content, height, and states are identical.
Either way the fork costs this element nothing, which is itself the lens-based note for his call: a 24-hour structure that only ever contained routines cannot be used as an argument for keeping a fifth screen — **his call**, unaffected by this seat.

**Standards check.**
- *ARM ≤10s:* the band is not on the arm path and adds zero gestures, waits, or reads to it. Arming still happens on Deck exactly as before. Cost: **0s**.
- *HOLD absolute:* the band introduces zero mid-session decisions. It accepts no input at any time; during a session its state changes are machine-driven (window close, seal events) and demand nothing. Decisions introduced: **0**.
- *TRUTH once:* sources are the routines store and the sealed-session log — the same sources the rows use, rendered here as structure, there as words and controls (different facts: geometry vs. state/config). No totals are printed on the band; no minutes are duplicated; a ghost routine (fired, unattended) is physically incapable of drawing a lithified bed, because coverage is computed from sealed intervals only — the drawing cannot mint a session he was not in. The one invented-looking number in the piece, the thickness scale, is printed on the band itself. Truth risk: **the coverage floor (0.35)** — it is a deliberate, disclosed compression: any attendance > 0 draws at least 35% thickness so "entered" never renders as "absent." It overstates thin attendance's thickness; the alternative (pure proportion) understates presence to invisibility at low coverage. I take the floor and name it here rather than hide it.

### Runner-up: THE BOREHOLE — one core, read downward

**Form.** A vertical core column, 36dp wide, running down Shield's left margin beside the routine rows, top = 03:30, bottom = 03:30 next day, depth at one true scale (≈11dp/hour, ≈264dp for the full day, scrolling with the page). Each routine window is a stratum band across the core at its true depth interval, thickness = duration at the *same* scale — no second scale to declare, which is this form's one real advantage. States borrow the driller's honesty code, the most truthful record-keeping convention in field science: attended = solid core; partial = **fractured core** (the band split by two diagonal fracture hairlines, fragments offset 1dp); missed = **core loss** — the interval comes up empty and is filled with a plain spacer block one tone below ground, because drillers mark lost core with a spacer and a written loss, they never fabricate the missing rock. Off = the interval is simply not drilled: tray ground, no outline.

**Compact spec.** Motion: none except a 1-minute now-depth tick stepping down the core's right edge (600ms linear). Haptics: none. Sound: none. Depth: core sits in a recessed slot one step below ground; strata flush; the core-loss spacer is the only element *below* ground tone — absence rendered as literal depth. Data: identical sources to the chosen form.

**Why it lost.** Three reasons, in order of weight. (1) **Duplication:** a vertical time strip registered against the routine rows is the same geometry family as the architecture seat's Section strip already carried into synthesis for Shield — transposing my material onto their column adds a dialect, not a form. The chosen stack's transposed geometry (time horizontal, order vertical) is genuinely a different drawing. (2) **It cannot show overlap:** two windows at the same depth collide in one 36dp column — one must hide the other, and a drawing that hides a conflict has failed the truth standard at exactly the fact that matters. The bedded stack faults overlaps into visibility. (3) **Viewport cost:** ≈264dp of margin column pushes Shield's apps list below the fold on first view, taxing the screen's working half for its record half. The chosen band spends 84dp once, at the top, and gives the rest back.

---

## Tasks — the day accumulating

**No form specced, deliberately.** My assignment allows a Tasks rendering *only if it does not duplicate another rendering*, and every stratigraphic version I can build fails that test twice over:

1. It duplicates **my own wave-1 Sediment Horizon** (completed blocks settling as strata in the Tasks horizon bar) — same lens, same surface, same encoding. Re-speccing it under a new name would be exactly the dressed-up repetition this wave exists to kill.
2. Worse, it would break TRUTH-once against a settled decision: the horizon bar died and its one true fact — the queue's total — moved to the 44sp header numeral. A strata stack on Tasks would re-render accumulated time that the header numeral already owns, in a second home, on the same screen.

The day's accumulation already has its one home in this plan: Shield's bedded stack records what the day *became*; the Tasks header numeral states what the queue *holds*. A third rendering of accumulation, however beautiful, is a lie of emphasis. I decline the element rather than duplicate it — and flag for the coordinator: if any other seat's Tasks proposal turns out to be an accumulation drawing in disguise, it should face the same test.

---

## Too good to lose

**1. The trace fossil (for the Bedded Day, if a second pass ever opens).** The day's longest sealed session leaves one mark inside its bed: a small filled ellipse (6×4dp, one tone step darker than the fill) at the x position of its start time, interrupting the grain. Real strata preserve trace fossils — evidence an organism passed through, preserved in the layer it disturbed. One mark per day, no label, no count. Spec: drawn at day close (20:00 or last window close, whichever is later) from the session log's longest interval; motion none (it is discovered, not performed); haptics/sound none; cost to all three standards zero — it prints no number and duplicates no rendering, since no other surface records *where in a bed the day's deepest work sits*. It lost only because the chosen form is already at its information limit for one band; a fossil is a second sentence in a drawing that currently speaks one.

**2. Core-loss spacer on the DAY CLOSED receipt (the runner-up's best part, salvaged).** When a routine window closes with zero attendance, the end-of-day receipt — the session-end receipt's day sibling, the one truthful receipt the settled law allows — carries one extra hairline row in Atkinson: `05:00 FASTED — NO CORE`, in place of any duration figure. Drillers write the loss into the log; they do not write a plausible rock. Spec: text only, no icon, no colour; generated from the same coverage computation as the band's erosion, so band and receipt can never disagree; appears once, on that receipt only, never in a list or archive (Ledger is dead and stays dead). It gives the eroded seam a name in words at the one moment words are already being read — and nowhere else.

---

## Mail / wall note

Inbox held two letters, both relaying Sarthak's citation-lift order verbatim — Orson's (read at start) and Mara's (arrived mid-work, read before finishing) — the same order carried in my assignment. Both read in full and moved to `mail/teo/read/`. I am appending one signed note to the Commons wall: the transposed-geometry claim (time horizontal, deposition order vertical) and the Tasks non-duplication verdict, so the architecture seat and any Tasks seat can attack both before the coordinator merges. No other mail sent.
