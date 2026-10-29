# Rhea — Wild, toys

Lens: toy-like direct manipulation. A toy teaches itself in the hand — you grab the thing itself, it has weight, it resists a little, it lands with a clack, and when you let go the world has visibly changed. No points, no streaks, no confetti, no score. The satisfaction is the physics being honest: what you moved stays moved, what you finished is gone because you threw it there. Below: Tasks rows you can pick up, carry, and discharge; and the Deck's block-count turned from a readout into a stack of physical blocks you build and knock down. Everything in Monolith's materials only — tone steps, hairlines, recesses, no drop shadows, no green, orange only where it already lives (Deck slide, Tasks START).

---

## Deck — the hub / block-count as a manipulable object

### Chosen form — THE BLOCK STACK: the hub well holds a stack of slabs, one per block, and the stack *is* the count

**Mechanism and state/data source.** The hub's recessed circular well (night ground `#171412`, well floor one tone step below hub face, capped by the existing 1px top edge-line) stops being a passive readout and becomes a shallow tray. Inside it, seen side-on along the well's lower half, sits a stack of flat slabs — think wooden toy blocks stacked in a tray, drawn as Monolith slabs: rounded-rect bars, width 62% of well diameter, height 7dp each, ink/graphite fills alternating by exactly one tone step so each slab's edge is legible. **One slab = one 50-minute block** of the currently set duration — his actual working unit (~ten blocks a day). A partial remainder (duration not a multiple of 50) renders as one thinner slab on top, its height ∝ remainder/50 (min 2dp, max 6dp) — the stack never lies about the total: slab count + top-slab height is the duration, and the existing Anonymous Pro numeral beside the hub stays as the same fact in digits (one fact, two renderings *of the identical value* — numerals for reading, stack for touching; the stack appears nowhere else in the app).

The stack is manipulable in both directions, and both paths write to the same single source of truth — the Deck's duration state:

- **Add:** the existing wheel remains the fast path — crossing a 50-minute detent boundary drops a new slab into the well from 22dp above the stack top. Additionally, a single tap on the empty well area above the stack drops one slab (+50 min) — the object itself accepts the gesture, no wheel required.
- **Remove — the toy move:** grab the *top* slab and flick it sideways. It slides on the slab beneath it, tips (rotation ∝ slide distance, pivot at its trailing bottom corner, max 14°), and once its centre passes the well's rim it falls out of the tray — gravity drop 340ms, fading only in the last 60ms as it leaves the well's clip — and the duration loses 50 minutes. The partial top slab can be flicked too; flicking it removes just the remainder (duration rounds down to the whole blocks below it). This is the whole delight of the surface: setting three hours is stacking seven slabs; changing your mind is knocking the top one off the pile.

**Motion.** Slab drop-in: gravity ease-in² fall (~180ms for 22dp), landing bounce is a *stack event*, not a slab event — on impact the whole stack compresses 2dp and sways: damped oscillation of the stack's top x-offset, spring stiffness 260 / damping 13, initial amplitude ∝ stack height (a 2-slab stack barely trembles, ~1dp; a 10-slab stack sways ±4dp and takes ~900ms to settle — tall stacks are wobblier, like real ones). Flick-out: follows the finger 1:1 while in contact; on release, velocity carries (fling threshold 400dp/s; below it the slab springs back onto the stack, stiffness 500 / damping 26, one 4% overshoot — a failed flick is a wobble, not an error state). Wheel-driven changes batch: spinning fast queues slab drops at most one per 90ms so the well never becomes a slot machine. During a running session the stack is *not* shown manipulable — HOLD is absolute; the sealed Deck shows the stack at rest, spending one slab's tone per completed block is forbidden (that would be a second progress rendering competing with the timer — truth rule; the stack belongs to the arm path only).

**Haptics.** Detent/drop landing: `14 ms @ 0.7` wooden clack. Stack sway: silence (felt through the eyes). Grab top slab: `6 ms @ 0.4` lift tick. Slab sliding: one `4 ms @ 0.3` scrape tick per 12dp travelled. Successful flick-out: `10 ms @ 0.6` release + `8 ms @ 0.4` as it clears the rim. Failed flick spring-back: `12 ms @ 0.5` thud on re-landing. Tap-to-add: same drop clack as wheel path — one gesture vocabulary, one sound per physical event.

**Sound.** FLOAT-PCM synth only, wood vocabulary: drop = short lowpassed knock (fundamental ~190Hz, 40ms decay); flick-out = same knock pitched +15% with a 25ms noise scrape under the slide; spring-back = knock at −20%. No sound for sway, no sound for numerals changing. Silence is a valid state everywhere else on this surface.

**Depth/material.** Slabs sit *inside* the well: the well's front lip (a ground-tone band across the stack's bottom 18%) occludes the lowest slab, selling "in a tray." Depth is tone steps and the recess, never shadow. Night: slabs in blush-cream ink steps on the `#171412` staircase, fills capped at `#4A423C`. Zero orange added — the Deck slide keeps its ration.

**Standards check.** *ARM:* wheel path unchanged (≤10s intact); tap-to-add and flick are optional parallel paths, never required — ceremony never taxes the arm path because stacking *is* the duration-setting he already does. *HOLD:* stack is inert once sealed; introduces zero mid-session decisions. *TRUTH:* slab count ≡ duration state, single source; no decorative readiness, no second progress rendering during the session.

### Runner-up — THE MAINSPRING COIL (from my wave-1 seat)

A spiral coil inside the hub winds tighter per detent as duration is set; un-armed, it creeps back and surrenders the time. Compact spec: Archimedean spiral in the hub recess, pitch 5dp→1.8dp across 0–90 min, detent ratchet clicks rising 180→420Hz, creep-back in 5-min steps at 600ms each. **Why it lost:** you never touch the coil itself — it is a beautiful *readout* of tension, not a manipulable object, so it fails this seat's assignment on its own terms. Worse, its creep-back is a machine taking time away from a rested user who set it deliberately, and its 10-second held-breath state sits directly on the arm path. The stack keeps the spring's honesty (stored time you can see) and hands it to the fingers.

**Guard fork.** The stack lives on the Deck only, under both Guard outcomes. If Guard survives as its own surface, it gets no stack, no mini-count, no echo — one fact, one home. If Guard is dissolved, nothing about the stack changes. His call either way; the hub is unaffected.

---

## Tasks — drag / reorder / completion physics

### Chosen form — PUCK ROWS: every task is a solid tile you pick up, carry, and discharge off the edge

**Mechanism and state/data source.** Each task row is rendered as a solid tile — a full-width puck resting in the list's shallow vertical track (track floor one tone step below screen ground, hairline rails left and right). The tile holds exactly what the row holds today (title, duration, START for the active task — orange stays on START and nowhere else). Three physical gestures, all writing to the one task store (order index + completion timestamp in the existing DB; the horizon bar re-derives from remaining durations on every change — it is the same numbers, re-rendered, never a second total):

- **Pick up (reorder):** long-press 180ms (or grab the tile's left thumb-pad, 24dp wide, no hold needed) lifts the tile: it takes one tone step *lighter* (lift = lightening, never shadow), and a gap of exactly tile-height opens beneath it as neighbours part. Drag is 1:1 with the finger, with a trailing spring on the tile's *tilt* only (rotation ±3° ∝ horizontal velocity — the tile leans into the carry like a held tray). Neighbours displace by spring (stiffness 420 / damping 30) when the carried tile's centre crosses their midpoint — they *move out of the way*, they never swap-pop or teleport. Release: order commits at the drop frame, persisted immediately. A fast release flings: residual velocity bumps the tile one slot further if it crosses the next midpoint within 120ms, with a two-tile chain nudge (each bumped tile shifts 6dp and springs back) — momentum is felt, order stays exactly where physics put it.
- **Complete — the discharge:** drag a tile horizontally right. Past 40% of tile width, a recessed discharge chute is revealed at the right edge (chute floor two tone steps down, one hairline gate line). Past 72%, the gesture *arms*: gate tick, tile tone steps up once. Release past 72% and the tile lets go — it accelerates into the chute (ease-in, 260ms), compresses to 30% width against the chute's end wall, and is gone. The task is complete *because it left*; completion writes at the release frame. The gap closes by spring (stiffness 350 / damping 26), tiles above falling in sequence with a 30ms stagger per tile of distance — you watch the list settle like a shelf after a book is pulled. Release before 72%: the tile springs home (stiffness 500 / damping 24) and nothing is recorded — an abandoned completion leaves zero trace, which is the truth of it.
- **Sub-steps** stay taps inside the opened task, unchanged — no physics tax on small facts.

**No gamification, stated plainly:** completing a tile earns nothing — no points, no count-up animation, no streak, no sound of reward. The tile leaves, the list settles, the horizon bar shortens because there is less day left to plan. The satisfaction is the discharge clack and the shelf settling. That's the toy: a thing you throw away properly.

**Motion summary.** Lift: 120ms ease-out tone step + gap opening on the neighbour springs above. Carry: 1:1, tilt lag spring stiffness 300 / damping 20. Drop settle: one overshoot ≤4% then rest (~280ms). Discharge: described above; the compress at the end wall is 90ms with a 1dp end-wall flex. Gap-close: gravity-feeling ease-in on the first tile, springs after.

**Haptics.** Lift: `10 ms @ 0.5`. Each neighbour parted while carrying: `5 ms @ 0.35`. Fling chain-bump: `7 ms @ 0.45` per bump. Chute reveal at 40%: silence (eyes only). Arm at 72%: `8 ms @ 0.6` gate tick. Discharge impact at end wall: `18 ms @ 0.9` + `6 ms @ 0.4` aftershock at +45ms — the loudest haptic on Tasks, fired once per completion. Spring-home from an abandoned discharge: `9 ms @ 0.4`. Gap-close landings: `6 ms @ 0.3` per tile, capped at three audible tiers so a long list doesn't buzz.

**Sound.** PCM synth, wood-and-one-metal vocabulary: parting tiles = soft clicks (1.8kHz partial, 25ms); discharge = low knock on release + one bright clack at the end wall (3-partial stack, full gain) — the only bright sound on this screen, once per completion. Carrying is silent. Sound off = haptics carry everything; nothing is sound-only information.

**Depth/material.** Tiles rest in the track recess; a lifted tile is *above* the track by tone, not shadow, and its lower edge-line brightens one step. The chute is a true recess (two tone steps, hairline gate). Night: cream tiles on the staircase ground, fills capped `#4A423C`, chute floor `#171412`-adjacent step. No green, no new colour anywhere.

**Standards check.** *ARM:* Tasks is off the arm path; nothing here touches Deck timing. *HOLD:* all of this lives outside a sealed session; mid-session the list is not presented as manipulable. Zero decisions added anywhere. *TRUTH:* order and completion persist at the gesture's commit frame; abandoned gestures record nothing; horizon = same store re-rendered. No fake counts — there is no completion counter on this surface at all (Ledger is dead; today's completions get one truthful line on the session-end receipt, nowhere else).

### Runner-up — THE MARBLE RUN (from my wave-1 seat, reworked)

Tasks as steel balls in a scalloped horizontal track where the horizon bar sits: drag a ball along the track to reorder; flick it down through an end gate to complete, ball falling a recessed chute with a tray clack. Compact spec: ball diameter ∝ duration, 1-D integrator along scallops, restitution 0.92, drop-in 380ms overshoot landing. **Why it lost:** (1) diameter-∝-duration distorts — two equal tasks read identical and a 90-minute task becomes an un-draggable boulder that eats the track; the puck keeps every task the same grabbable size and lets the *numeral* carry duration truthfully. (2) Reorder becomes horizontal-only, fighting the vertical list the tasks still need for titles and sub-steps — you'd get a toy track *plus* a list, two renderings of one queue. (3) On a 10-task day the track overflows and starts scrolling sideways, the worst toy failure: the object stops fitting in the hand. The discharge clack survives — it was stolen into the chosen form's end-wall impact.

**Guard fork.** Not relevant to Tasks mechanics — the list manipulates identically whether Guard is a separate surface or dissolved. If Guard is preserved, no task tile, count, or chute appears there. His call; no argument from this lens.

---

## Too good to lose

1. **Newton's cradle sub-steps** (wave-1): inside an opened task, sub-step numerals hang as cradle balls on hairlines; marking one DONE swings it into the row and the *next* sub-step's ball kicks out as the live one (pendulum integrator, ~700ms period, strike haptic `5 ms @ 0.6`). Momentum instead of checkmarks for "what's next." It lost the main slot only because row-level physics had to land first; it composes cleanly on top of puck rows later.
2. **The spent-slab tray** — *with a hard truth fence:* discharged tiles land as a thin stack of flat spent slabs at the very bottom of the Tasks screen, today only, wiped at day rollover, no number attached, no history, no archive. It's the physical remainder of the day you finished — a pile, not a Ledger. If it ever grows a count, a yesterday, or a second home, kill it: that would be the Vault wearing a tray costume.

---

## Mail / wall note

Inbox was empty at start; at the final check two letters had arrived — from Orson (05:14) and Mara (05:17), both passing down the same citation-lift order verbatim. Both read and moved to my inbox's `read/` subfolder; no reply needed (the order was already in my brief and is passed down in my wall post), and I sent no mail of my own. I posted one signed entry to the Commons wall passing down Sarthak's citation-lift order verbatim (as instructed) and noting the two forms above in one line each. Wave-1 file `swarm/wild/15-toys.md` read and used freely — the Mainspring and Marble Run return as runner-ups with their loss reasons stated, per the brief's freedom (no citations required or given).
