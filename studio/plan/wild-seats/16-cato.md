# Cato — Wild, fashion

*Lens: closures, seams, fasteners, material finishes. Fashion's one law, applied to every element below: **a closure is honest only at its true travel.** A zip meshes continuously over a long throw. A buttonhole strains and seats over a short one. A friction buckle feeds slowly, slips, and catches. Each mechanism below is used only where its real physics matches the gesture's real duration and distance — and the zipper is rejected, with reasons, where its physics would lie.*

*Settled law obeyed throughout: no narrative wrapper (these are hardware names, not brands or ceremonies); Ledger dead; emergency hold banked at 15/30/45 with slip-drain; routines his; orange = COMMIT fill only on the Deck slide and Tasks START, zero orange in chrome; never green; depth = tone steps, hairlines, recess — never drop shadows; fonts Sora / Atkinson Hyperlegible / Anonymous Pro; night fills cap `#4A423C`, ground `#171412`.*

---

## Deck — slide-to-arm

### Chosen form — Interlock Tape (the zip, rebuilt honest)

A zipper pull travels the full track; two rows of teeth mesh shut behind it. Wave-1 proposed this and it survives — but only under three honesty constraints wave-1 didn't state, which are the actual design:

1. **Teeth encode nothing.** No minutes, no block count, no readiness. Tooth count is a function of track width alone. Mesh is a pure function of pull position — there is no per-tooth state anywhere in the system, so the picture can never disagree with the data.
2. **The arm event fires on the seat frame, never at a threshold crossing.** The session starts in the same frame the pull physically arrives in its garage. Picture and event coincide; there is no frame where the app is armed but the zip looks open, or vice versa.
3. **No motion without input.** Wave-1's swinging pull-tab is deleted: a pendulum that keeps moving after the hand stops is a lie in miniature. Grip ribs on the pull face (where the thumb actually pushes) replace it.

**Mechanism & state/data source**
- One `Canvas`, track height 56dp, full content width. Three passes: (1) tape — two parallel bands at panel tone −1 (recess), separated by the slot, with a 1px catch-line on the lower edge of each band; (2) teeth — instanced rounded rects 6×4dp at 8dp pitch along both band edges; tooth *i* is drawn meshed (rotated ±22° into interlock, centres offset half a pitch) iff its centre x < pullX − 6dp, otherwise drawn open and parallel with a visible V-gap; (3) pull — 48×56dp sled, COMMIT-orange fill, three vertical grip ribs drawn as recess hairlines on its face. No text inside the pull (position = power; the `SLIDE TO ARM` label lives at the track's margin, per state grammar).
- State is a single `Animatable<Float>` travel 0f–1f driven by pointer input. Teeth, pull, and specular are all pure functions of it — one drag loop, ~45 teeth, trivially 120Hz, nothing stored per tooth. Recomposition or a width change can recount teeth freely *because* they mean nothing.
- The garage: final 10% of track is a slightly wider recess with two squared end-stop crimps. Input scaling in the garage zone is 0.6× — arrival is deliberate, a fling decays into it instead of teleporting through it.
- Commit: crossing 92% travel with the pull still held or with forward velocity engages a completion spring that carries the pull the last 8% in 110ms; the arm callback is invoked **from the seat frame** at the end of that spring.

**Motion**
- While held: 1:1 with the finger, no easing — a zip follows the hand.
- Released below 92%: unzip return spring, stiffness 200, damping 23, ≈400ms. Teeth un-mesh in reverse order for free, because mesh is derived from pullX.
- Seat: the 110ms assist spring (stiffness 400, damping 16) with 1.5dp overshoot into the end-stops, then rest. No idle animation anywhere on this control.

**Haptics**
- Per tooth meshed: **5ms @ 0.35**, alternating teeth **5ms @ 0.28** — the left/right asymmetry is what makes it feel like a zip rather than a buzz. Ticks are scheduled on tooth-index change and coalesced to a minimum 12ms interval, so at full drag speed they fuse into a fine rattle instead of a stutter. Devices without amplitude control fall back to duration-only ticks at the same schedule.
- Garage seat: knock **18ms @ 1.0**. Unzip on release: ticks **4ms @ 0.2**, descending. Release before 30%: single dull **10ms @ 0.4**.

**Sound**
- Sound enabled: synthesized zip — filtered noise, rate and gain tracking finger speed 1:1; garage = one dry clack (2ms transient + 40ms decay). Generated, never a sample, so the sound physically cannot run ahead of the pull. Sound disabled: explicit silence; haptics carry the teeth.

**Depth / material finish**
- Two finishes only, and the finish *is* the state: tape and unmeshed teeth are **matte** (tape tone, flat); meshed teeth are **satin** (+1 tone step — interlocked metal catching light). The track visibly brightens behind the thumb as it closes. Pull is flat matte orange — the Deck's single COMMIT fill; tape, teeth, and garage are never orange.
- Depth is recess (tape −1 step + catch-lines) and tone, night and day, from the same relative steps; no shadows.

**Standards check**
- *Arm cost:* one drag, ≈1.2–2s of the ≤10s budget, zero taps before or after, zero waits. The garage resistance is inside the gesture, not added to it.
- *Mid-session decisions:* none — the control exists only pre-arm; once seated, the Deck leaves.
- *Truth risk:* the named risk is teeth being read as a scale. Mitigated by construction (no numerals on the track, tooth count width-dependent and therefore visibly arbitrary) and by the seat-frame firing rule. Residual risk: low.

### Runner-up — Buttonhole Travel (a button dragged the length of a slot)

A Ø32dp disc sled travels an elongated buttonhole slot; at 70% it meets a constriction, scales 0.93× squeezing through, then seats in a terminal hole. Compact spec: travel 1:1; constriction zone scales input 0.55×; seat spring 90ms, stiffness 400 / damping 14; haptics — strain ramp **30ms @ 0.4→0.65**, seat knock **14ms @ 1.0**; sound — one soft thock on seat, else silence; depth — slot recess −1, seated disc drops one tone step (in the surface).

**Why it lost:** a buttonhole is a point closure. Stretched over 200dp it gives feedback at exactly one moment (the squeeze) and silence for the other 90% of the journey — the thumb learns nothing about distance covered. Worse, the squeeze is the *whole* meaning of a buttonhole, and spending it mid-track to decorate a slide spends the one closure Shield needs at its true scale (below). The zip gives continuous, proportional feedback for the entire throw; the buttonhole gives one event. Long travel wants the continuous mechanism.

### Guard fork
Not structural. The zip's sound follows the app's single sound setting wherever that setting lives — if Guard survives as a screen, the toggle sits there; if Guard folds into Shield, it sits there. The mechanism does not move. *His call, unaffected by this element.*

---

## Shield — sleds

### Chosen form — Buttonhole & Button, at true scale

Each Shield sled (blocked app, blocked site, protection toggle) is fastened, not switched: a horn button travels a short slot and drops through a worked buttonhole. This is the buttonhole's real job — 36dp of travel, a strain, a seat, and a grip you can *see*: a button sitting inside stitched lips cannot be misread the way a coloured thumb on a track can.

**Mechanism & state/data source**
- Control: slot 76×40dp at recess tone −1. At its right end, the buttonhole: an oval 26×14dp drawn as two stitched lips (arcs, 1.5dp stroke, ink 60%) with bartacks — three short vertical strokes — reinforcing each end, because a real buttonhole's bartacks are what stop it tearing under load. The button: Ø32dp, four thread holes (1.5dp dots) joined by an X of thread at its centre.
- Gesture: drag 1:1, **or** tap to run the full animated fasten — both end at the same seat check. Release past 60% travel completes by spring; release before, it returns.
- **State source is the load-bearing spec:** displayed state = user intent **AND** blocker-service confirmation, read from the protection repository (service alive + item actually blocked). On a fasten attempt, if the service confirms within 250ms, the seat spring completes and the margin word flips. If it does not confirm, the button springs back to OPEN over 200ms and the word never flips. A button showing FASTENED over a dead accessibility service is the one lie this control must never tell — so the fastening is physically refused when the protection isn't real.
- State word at the row's margin, never inside the button: `FASTENED` / `OPEN` in Anonymous Pro caps. If the service dies globally mid-day, every button returns to OPEN — the screen then tells the truth at a glance, with no banner and no second rendering of the fact.
- Deliberately *not* added: a stitched seam appearing on fastened rows. Position + margin word already render the state under the state grammar; a seam would be a third rendering of one fact. Restraint recorded so nobody "improves" it back in.

**Motion**
- Fasten: 240ms total — translate on cubic-bezier (0.5, 0, 0.8, 0.4), decelerating into the hole; in the final 10dp (the strain zone) input scales 0.55× and the button scales to 0.93× on its travel axis, squeezing through; then a 90ms seat spring (stiffness 400, damping 14) pops it back to full size, seated, with 1dp overshoot.
- Unfasten: 200ms, same path reversed, snappier — unbuttoning is easier than buttoning, in life and here.
- State words flip on the arrival frame only, never mid-flight.

**Haptics**
- Strain (entering the hole): rising buzz **30ms, amplitude 0.4 → 0.65**.
- Seat: knock **14ms @ 1.0** + trailing **5ms @ 0.3** (button clicking against the bartack).
- Refused seat (no service confirmation): dull **10ms @ 0.4** as it springs home. Unfasten: **8ms @ 0.55**.

**Sound**
- Sound enabled: seat = deep, low-passed *thock* (≈50ms) — horn on fabric, the warmest sound in the app; Shield is the heavy outer layer and should sound like it. Sound disabled: explicit silence.

**Depth / material finish**
- Button finish: **matte horn** — warm tone +1 over the slot, its identity carried by texture (thread holes, X thread) rather than brightness, so it respects the night fill cap `#4A423C` without going anonymous. Seated, the button drops one tone step: genuinely *in* the surface, flush, the way a fastened button sits below the fabric plane. Slot is recess (−1 + catch-line on the lower lip). No orange anywhere in this control — Shield's chrome budget is zero, and fastening is not a commit.

**Standards check**
- *Arm cost:* zero on the daily path. Fastening is rested-user setup, done once and left; if a sled is toggled during an arm flow, one fasten costs one tap + 240ms.
- *Mid-session decisions:* none introduced. During a sealed session the sleds are inert — buttons remain seated as a *display* of state and accept no input until the session ends.
- *Truth risk:* the service-confirmation rule above is the mitigation; it converts the highest-risk toggle in the app into a self-verifying one. Residual risk: a service that confirms then dies silently between checks — bounded by the global OPEN reset on any failed heartbeat.

### Runner-up — Keeper-Strap Sled

The sled is a strap end that slides under a fixed keeper loop (a vertical band bridging the track); passing fully under the keeper is ON, withdrawn is OFF, with gritty friction ticks (**3ms @ 0.25** every 8dp) while it slides. Spec compactly: 1:1 drag, keeper band +1 tone step standing proud over the strap hairline, no spring seat (friction holds position), sound = one fabric *shhk* on pass-through if enabled.

**Why it lost:** a keeper restrains a strap; it does not *lock* anything. Halfway under the loop is a physically stable position, so the control admits a genuine in-between state for a binary fact — the thumb can't feel whether it's fastened, and the eye can't either. It also leaks the wrong metaphor: keepers are how you tidy a loose end, and on Shield that reads as *removal* (see Too Good To Lose, where the keeper gets its correct job). The buttonhole's seat is binary, felt, and visible.

### Guard fork
This element *is* the Guard fork's furniture, so both placements are specced:
- **Guard survives as its own screen:** buttonholes are its primary control, one per protection row, exactly as above.
- **Guard folds into Shield:** the identical control becomes the Shield sled cap, unchanged in size, motion, or state rule — only its home and its neighbours change.
The mechanism, the truth rule, and the 250ms confirmation window are identical under both outcomes. Placement is **his call**; fashion has no argument either way, because a buttonhole fastens the same on a coat front or a cuff.

---

## Emergency overlay — banked hold

### Chosen form — The Tension Strap (ladder-lock buckle)

The settled law for this element — 60 seconds total, progress banks at 15/30/45, a slip drains banked progress rather than zeroing it — reads like an invented progress-bar rule until you look for its hardware. It exists: webbing through a ladder-lock buckle. Feed a strap through under tension and it advances slowly, rib by rib. Let go and the strap slips back — but not to zero: it slides until the buckle's teeth bite at the last firm point. Drain-not-zero is not a UI policy here; it is what friction does. This is the only common fastener whose native physics *is* the banked hold.

**Mechanism & state/data source**
- Full-screen overlay, ground `#171412` at night (panel ground by day). One horizontal strap, 44dp tall, centred at 62% of screen height — thumb height. Left of centre, the buckle: a fixed frame (cream hairline outline, 1px catch-line on its top edge only). The strap's sealed segment runs from the buckle to the left edge, into the seal. Its free segment exits right and **grows** as the hold progresses — free-end length is the one and only rendering of progress.
- Data source: a single held-time accumulator driven by monotonic-clock timestamps while the owning pointer is down within a 28dp slop radius. Strap feed position = accumulator × 2.8dp/s; full feed = 168dp = 60s. Nothing else in the overlay derives from a different clock.
- The strap carries transverse woven ribs every 2.8dp — **one rib through the buckle per second of hold**. The scale is printed on the object itself; there is no countdown numeral, no percentage, no ring (one fact, one rendering).
- Banks: at 42 / 84 / 126dp of feed (15 / 30 / 45s) a **bartack band** — four dense stitches across the strap's width, in cream thread — passes through the buckle, and a notch tooth in the buckle frame drops with a knock. From then on that bartack is visible on the free segment: banked length is literally stitched length, readable at a glance.
- Slip: on release (or drift beyond slop), the strap slips back at 8.4dp/s (3× feed rate) until the last passed bartack reaches the buckle and catches — a hard stop. The accumulator drains to exactly the last bank (15/30/45) and no further. Re-pressing resumes the feed from the caught position.
- Completion: at 168dp the strap's end crimp exits the buckle entirely; the strap is loose, the seal is open, and the break-consumed callback fires on that frame — the same seat-frame discipline as the Deck zip.
- One line of words, shown once at entry, Atkinson Hyperlegible: *"Hold to open. This is this week's one break."* No "are you sure", no second step, no settings.
- Interruptions (call, backgrounding): the accumulator freezes — no gain (you cannot bank time you weren't holding) and no drain (an interruption is not a release; the buckle is treated as locked by the pause). Resuming requires a fresh press from the frozen position.

**Motion**
- Feed: **linear**, constant 2.8dp/s, while held. Easing would lie about time; a strap under steady pull moves at a steady rate.
- Drain: 200ms cubic-out ramp to full slip speed, then constant 8.4dp/s, ending in an abrupt stop at the bartack (no bounce — buckles don't bounce).
- Entry/exit of the overlay itself: 160ms fade of the ground only; the strap never animates in — it is simply there, at its true length, from the first frame.

**Haptics**
- Press-in: **12ms @ 0.6**. Each rib through the buckle: **3ms @ 0.22** — one per second, felt eyes-free; this is the same fact as the rib passing, in the hand rather than a second visual rendering.
- Bank (15/30/45): knock **18ms @ 1.0** + trailing **6ms @ 0.4** as the notch tooth drops.
- Drain: reverse rib ticks **3ms @ 0.18**, descending. Completion: strap-free *whoomp* **24ms @ 0.8** with falling envelope, then the buckle click.

**Sound**
- Sound enabled: a quiet webbing *shhk* whose gain is tied to strap velocity — it is silent when the strap is still, feeds while feeding, and slips while draining; the sound can never claim motion that isn't happening. Bank = one dry buckle click. Sound disabled: explicit silence; the rib and bank haptics carry everything.

**Depth / material finish**
- Strap: **matte webbing**, tone +1 over the overlay ground, ribs as recess hairlines — the cheapest possible depth, all tone. Buckle frame: satin edge (catch-line) against the strap's matte, the same matte/satin pairing as the Deck tape and teeth, one hardware family across the app. Bartack thread in cream.
- **Zero orange.** Orange is COMMIT; this overlay is the sanctioned *un*-commit, and colouring it orange would lie about what it is. Zero green, as everywhere.

**Standards check**
- *Arm cost:* none — this is off the arm path entirely.
- *Mid-session decisions:* this overlay is the one sanctioned exception the settled law itself defines; the form introduces no further decisions — one hold, one outcome, no branches, no negotiation text, no partial-credit offers.
- *Truth risk:* progress has exactly one source (the held-time accumulator) and exactly one rendering (strap length + its stitched banks). The interruption freeze rule closes the one real loophole (background time masquerading as held time). Residual risk: low.

### Runner-up — The Reverse Zip (Deck zipper, driven backwards under hold)

Hold drives the Deck pull backward, un-meshing teeth over 60s; quarter-points act as banks; a slip re-zips forward to the last quarter. Compact spec: hold-to-drive at constant rate, tooth ticks reversed (**4ms @ 0.2**), bank knocks at each quarter, drain = forward re-mesh at 3× rate to the last quarter stop; silence unless sound is enabled, when the unzip noise tracks pull velocity.

**Why it lost — the zipper verdict the assignment asked for:** the zip is honest on the Deck because a zip really does close in one continuous throw, at arm speed. Here both halves of that honesty fail. (1) *Scale lie:* a real zip opens in about a second; stretching its travel across 60 seconds makes the hardware claim a duration it doesn't have. (2) *Invented stops:* garment zips have exactly two resting states — open and shut. The three intermediate "quarter garages" this form needs do not exist on any zip; they would be theatre bolted onto hardware, precisely what the buckle doesn't need, because its banks are real teeth and real bartacks. (3) *Gesture collision:* teaching the thumb that the Deck's own track also undoes the seal blurs the one gesture that must stay unambiguous at 03:30. Separate hardware, separate meaning: zip = arm, buckle = break.

---

## Too good to lose

**1. Baste, Then Sew — the seam as Tasks' finish state.** Queued tasks are held by basting: long, loose, slightly wavy dashes (14dp dash / 10dp gap, 2.5dp stroke, ±1dp hand jitter seeded per row) down a left-margin rail — planning that *looks* temporary because it is. Starting a task rips its basting out (260ms, dashes vanishing sequentially top-to-bottom, ≈18ms per dash, cubic-bezier (0.7, 0, 0.3, 1); haptic tear pattern **6ms @ 0.5 / 6ms @ 0.4 / 8ms @ 0.6**). Completing it sews the permanent seam: tight machine dashes (5dp / 3dp) drawing at constant speed over 480ms — no easing, machines don't ease — ending in a bar-tack thud **16ms @ 0.9**. Not my assigned surface, but it is the seam half of this lens and the strongest single answer to "Tasks is bland" I have: provisional vs. made, rendered in thread.

**2. Keeper-Loop Removal — the keeper's correct job.** In Shield/Guard *lists* (blocked sites, blocked apps), removal — as opposed to toggling — is a keeper drag: press the row's keeper loop and drag left; the row's "strap" (its hairline + text) follows with 0.7 elastic lag, the keeper skews up to 4° under strain, gritty micro-ticks **3ms @ 0.25** every 8dp; past 64dp the strap slips free and the row unthreads upward in 180ms. Tap alone does nothing. Un-blocking something *should* cost a deliberate two-stage tug, and this keeps the keeper (rejected as a toggle above) doing the one thing it's genuinely for. Works identically whether the list lives in Guard or Shield — his call on the home, again.

---

## Mail / wall note

Inbox at start: empty. Two letters arrived mid-run — Orson and Mara, both relaying Sarthak's citation-lift order verbatim (already carried in my assignment); read and moved to `read/`. Sent: one letter to Ines (Maximum-Implementable, slide) — the zip's three honesty constraints and the seat-frame firing dependency; one letter to Yuki (Maximum-Implementable, sleds) — the buttonhole's service-confirmation rule. Posted: one signed entry on the Commons wall (05:17 IST) — closure honesty at true travel, and the reverse-zip rejection, for all seats.
