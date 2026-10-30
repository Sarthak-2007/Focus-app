# Scout Finds 2 — Analog-Machine & Physical-Object UI

**Scout:** Apparat (Scout 2)
**Hunting ground:** Interfaces that behave like physical machines and objects — lab instruments, tape machines, latches, dials, cranks, and physical keys.
**Best serves:** **GitHub Issue #4 — the Deck top-strip redesign**, first and foremost. The top strip is where Monolith most needs to become an instrument face: state readable at a glance, from form / position / light, not from a text dump. Secondarily serves the block-screen / launcher redesign and the arming ritual.

**Monolith constraints applied throughout:** one calm physical instrument, not an app dashboard; state shown by form, not text dumps; **never green, never AI-generated images**; type only in Sora (display), Atkinson Hyperlegible (body), Anonymous Pro (telemetry / numbers). Colour below means Monolith's own palette accents used as *code* — e.g. `#E47E35`, `#E2BC39`, `#F04FCB`, `#377ED0`, `#493EA4` — never decoration.

---

## 1. Teenage Engineering OP-1 field + the Field System — colour as code, screen obeys the hardware

**Sources:**
- https://abduzeedo.com/teenage-engineering-masterclass-product-design

**What makes it elite:**
A white aluminium slab where nothing is decorated and nothing is hidden. Every key carries a number or a tiny pictogram (scissors, metronome, loop). The four rotary encoders are colour-coded — dark blue, ochre, grey, and one loud orange — and the screen graphics use *the same colours for the same parameters*, so hand and eye never translate. The on-screen tape deck draws two reels turning over a red playhead line: the software follows the same mechanical logic as the hardware. For the field update the team redrew every screen and cut a new font so even menus sit on the same grid as the hardware. The wider Field System (OP-1 field, TX-6, TP-7, CM-15) speaks one language across five machines: same small printed labels, same materials, same grid — instruments borrowed from one laboratory.

**What applies to Monolith — concretely:**
- **Deck top-strip (#4): make it an OP-1 tape window.** A thin horizontal tape lane across the top of the Deck: two small reels (deterministic SVG / Compose shapes, no images) and a single accent playhead line. Reel rotation and playhead position *are* the session progress. Armed = reels turning slowly; peek = one reel slack; finished = playhead at the end stop. No "Session in progress — 47 minutes remaining" sentence needed.
- **Colour-as-code rule:** pick ONE accent per function and never reuse it. E.g. orange `#E47E35` = the one dangerous control (emergency break) and that colour appears *only* there — exactly OP-1's single orange knob. If a colour means two things, it means nothing.
- **One grid for Deck, block screen, and allowed-apps screen.** Same label size, spacing, and Anonymous Pro numerals, so switching screens feels like turning the same machine over, not opening three apps.

---

## 2. Teenage Engineering TP-7 — a moving part that *is* the status display

**Sources:**
- https://store.moma.org/en-eg/products/teenage-engineering-tp-7-field-audio-recorder

**What makes it elite:**
The centrepiece is a motorised tape reel on ball bearings. It is not decoration: it scrubs, navigates menus, gives gentle visual feedback during playback / recording — and you pause it by *putting a finger on the reel and stopping it*, the way you stop a real tape machine. A side rocker does fast-forward (press top edge) and rewind (press bottom edge), positioned so index / middle finger / thumb / pinky each fall on a different control without looking. One physical object carries state, control, and feedback at once.

**What applies to Monolith — concretely:**
- **The top-strip reel should be touchable, not just visible.** On the Deck, dragging across the top-strip tape lane scrubs session time like tape; on the block screen, a press-and-hold *on the reel itself* is the natural home for the 60-second emergency-break hold — finger stopping a moving reel is a metaphor a user understands in their body, no instruction text required.
- **State by motion, not labels:** reel spinning = holding; reel stopped by a finger = you are intervening; reel absent / parked = disarmed. Stillness vs. motion is readable peripherally.
- **Ergonomic assignment:** one control per finger-zone / screen edge, learnable blind. Monolith's block screen should need no looking-down precision — big edge zones, each with exactly one job.

---

## 3. Braun ET66 calculator + T3 radio (Rams / Lubs) — colour only where function lives, controls in zones

**Sources:**
- https://www.fastcompany.com/1672632/w_750
- https://www.broadstreetreview.com/articles/philadelphia-museum-of-art-presents-dieter-rams-principled-design

**What makes it elite:**
The ET66 (1987, Rams with Dietrich Lubs, now in MoMA's collection) is entirely black except for the buttons that matter: red for "off", black-on-yellow for "equals" — the most-used button gets nature's highest contrast. Buttons are round and convex so fingertips find them; polished number keys vs. matte function keys separate zones by touch and light, not just print; the back is ribbed against slipping, with an instruction panel you never need because the layout is self-explanatory. The T3 radio does the same with one perforated speaker square and one circular dial. Rams' rule behind both: as little design as possible, honest, thorough to the last detail — clear geometry, clear separation of control zones, subtle colour used only for orientation.

**What applies to Monolith — concretely:**
- **Deck top-strip (#4) zoning:** three physically distinct zones and nothing else — left: state (reel / lamp), centre: time (Anonymous Pro, one number pair), right: the single accent control. No fourth zone. If a proposed element fits no zone, it does not go in the strip.
- **One high-contrast button rule:** the Arm control gets the ET66 "equals" treatment — the only black-on-`#E2BC39` (or equivalent highest-contrast) element in the whole app. Everything else stays muted. Note: the ET66's historical green function grouping must **not** be copied — Monolith is never green; translate that grouping into matte-vs-polished surface difference instead of colour.
- **Differentiate by finish, not hue:** allowed apps = polished / raised tiles; peek apps = matte / recessed tiles. A user can read the Deck by light and shadow, in peripheral vision, without reading a word.

---

## 4. Blockit — the Suitcase — packing as the arming ritual

**Sources:**
- https://www.appbrain.com/app/blockit-%7C-stop-phone-addiction/com.hypenet.focused

**What makes it elite:**
Blockit — the direct competitor Monolith already learns from — refuses the word "blocklist." You *pack a suitcase*: the few apps you keep go into the case, a duration is picked with a wheel, the session starts, and anything not packed brings you straight back to the session screen. Its features are all physical objects — The Suitcase, The Parachute, The Tower — and reviewers single out the design ("one of the coolest app designs I've ever come across") as the reason it retains where plain blockers do not. The metaphor does real work: packing is a deliberate, finite, closable act, and a closed case is visibly closed.

**What applies to Monolith — concretely:**
- **Close the case on the Deck.** The last step of arming should be a latch / closing gesture on the top strip itself: drag the strip's latch shut (with a haptic clunk) and the strip physically changes form — lower profile, labels gone, only reel + time remain. Arming is not complete until the case *looks* closed. This is the #4 strip earning its place: it is the lid of the machine.
- **Packing, not listing, for allowed apps:** allowed apps are items placed *inside* a bounded container with visible capacity (Blockit's bag logic), not an infinite scrollable checklist. Full is full — adding one means seeing the case get full.
- **Keep object-names, kill system-names:** Suitcase / Parachute prove users remember and talk about objects. Monolith's elements should be named as parts of one instrument (Deck, Reel, Latch, Bag) — never "module", "widget", or "card".

---

## 5. Nest Learning Thermostat — the whole device is one dial and one button

**Sources:**
- http://www.core77.com/posts/20860/the-nest-learning-thermostat-20860
- https://www.electronicdesign.com/blog/altembedded-6/embedded/elegant-thermostat-designed-internet-74582

**What makes it elite:**
Nest threw out the touchscreen menu tree. The outer stainless ring *rotates* to browse / adjust and the *whole unit pushes in* as one big button to select, with audible clicks. That is the entire interface. It wakes by proximity when you approach and darkens when you leave (later, Farsight shows one large thing from across the room). Temperature state is shown by the face itself changing — the display is one number, huge, in a ring — because Nest understood a thermostat is read from 3 metres away, mid-passing, not studied. Rotation = continuous change, push = commitment: two gestures, zero modes.

**What applies to Monolith — concretely:**
- **Duration-setting on the Deck should be a dial, not a picker list.** A semicircular dial occupying the Deck's main face: drag around the ring to wind session length, with detent haptics at 15-minute stops and Anonymous Pro numerals that grow as you approach round hours. Tap the centre — the whole dial — to arm. Two gestures total, like Nest.
- **Top-strip (#4) as Farsight:** when a session is holding, the strip should be readable across the room — one oversized elapsed / remaining figure and the reel, nothing else. If it cannot be read at arm's length-plus, it is too cluttered.
- **Proximity calm:** block screen dims to near-black when the phone is face-down / still, and wakes to full instrument face on pickup. A holding session should mostly be *dark and quiet* — presence, not nagging.

---

## 6. Panic Playdate (designed with Teenage Engineering) — one crank for time itself

**Sources:**
- https://www.macstories.net/news/panic-reveals-plans-to-sell-a-handheld-gaming-system-called-the-playdate-in-2020/
- https://www.wallpaper.com/technology/panic-playdate-console-instant-cult-gaming-object

**What makes it elite:**
A tiny yellow handheld, black-and-white screen, D-pad, two buttons — and a folding metal crank in the side that is *not* for charging. It is a controller. In its signature game, cranking forward moves a character forward in time and cranking backward reverses time: the physical direction *is* the meaning. The crank tucks away flush when unused, has real tension as it locks open, and the buttons are deliberately ultra-clicky. Panic did not force every game to use it — it is employed when it makes sense and ignored when it doesn't, which is why it stayed delightful instead of becoming a gimmick tax.

**What applies to Monolith — concretely:**
- **A crank gesture for the one reversible thing: setting / extending time.** On the Deck, a circular crank-scrub at the dial's edge — clockwise winds time *in* (longer session), counter-clockwise winds it *back out* — with ratchet haptics per detent. Direction carries meaning, so no +/- buttons and no explanatory text.
- **Fold-away principle for the emergency break:** like the crank, the break control should be *stored* — recessed, two-step to deploy (slide to reveal, then hold) — visible enough to trust, tucked enough to never hit by accident. A dangerous control that is always exposed invites both accidents and temptation.
- **Restraint rule:** use the mechanical gesture only where it is the meaning (time, arming). Do not crank-ify settings, lists, or navigation. One signature motion, used sparingly, becomes Monolith's identity; five become a theme park.

---

## 7. Nothing Phone Glyph Interface — state as a strip of light, readable face-down

**Sources:**
- https://www.xda-developers.com/nothing-phone-1-glyph-interface/
- https://www.makeuseof.com/glyph-interface-nothing-phone/

**What makes it elite:**
Nothing put the notification system on the *back* of the phone as shaped LED strips under a transparent panel: distinct light patterns per contact / event, a bottom strip that works as a charging progress bar, a central ring for wireless charging. The insight is behavioural — a phone in focus time sits face-down and silent, so the interface moves to where the user actually is. State becomes pattern, position, and fill level. Notably Blockit itself integrates with Nothing's Glyph — even a competitor treats this strip as a real focus surface. Glyph also proves a strip can carry identity: the phone is recognisable from across a room by its light pattern alone.

**What applies to Monolith — concretely:**
- **This is the most direct #4 precedent: the Deck top-strip IS Monolith's Glyph.** A segmented bar of, say, 10 deterministic segments (drawn shapes, not images) that fills as a session progresses and has distinct *patterns* per state: slow fill = holding; full strip pulsing once = session complete; two end segments only = peek in use; alternating segments = routine about to arm. Patterns must be learnable in a day and never need a legend afterwards.
- **Flip-to-calm:** when the phone is face-down during a session, the block screen should reduce to just the strip pattern on a black field — the Glyph lesson translated to a front screen. Nothing else competes for attention.
- **Per-routine patterns, not per-app noise:** give each routine its own strip rhythm (e.g. Morning = fill left-to-right, Night = fill centre-out), so *which* machine is running is recognisable without reading its name. Identity by form, again.

---

## 8. Brick — the key is a separate physical object, so quitting costs a walk

**Sources:**
- https://www.fastcompany.com/91611256/people-are-bricking-their-smartphones-on-purpose-to-stop-doomscrolling

**What makes it elite:**
Brick is a small grey NFC cube, about 2 inches, with no battery and no screen. You choose apps in the companion app, tap the phone to the cube to block them — and to unblock them you must tap the cube again. Its whole thesis is that every software-only blocker fails at the same point: the bypass button lives on the same screen as the temptation. Brick moves the key into the physical world. Leave the cube at home and the block is absolute while you're out; keep it in another room and quitting costs standing up and walking. It ships with a small, finite allowance of emergency unblocks — scarcity makes even the escape hatch feel physical.

**What applies to Monolith — concretely:**
- **Give Monolith a software version of the separated key: the Deck's latch and the block screen's key must never be the same surface.** Arming happens on the Deck; disarming early should require leaving the block screen entirely and performing the full crank / hold ritual in a different place in the app — friction by *distance travelled through the interface*, Brick's walk across the room translated to screens.
- **Finite, visible emergency allowance:** show remaining emergency breaks in the top strip as physical tokens — e.g. small Anonymous Pro pips / notches in the strip that are *consumed* and stay visibly missing for the week. A token you can see disappearing behaves like Brick's finite allowance; a buried "1 / week" text line does not.
- **Optional real-key upgrade path (invention, priced for the Lead):** Monolith could support *any* NFC tag (a card kept in a study bag / another room) as an optional arm / disarm key, the way Brick-compatible workflows already use generic tags. No hardware to ship, no cost beyond NFC read on arm / break; it turns the user's desk object into part of the instrument. Flagging as an option, not a recommendation to build blind — needs a decision on whether Monolith stays software-pure.

---

## Synthesis for the Lead — what to steal first for #4

1. **Strip = tape window + segmented light bar combined:** OP-1 / TP-7 reel-and-playhead on the left, Nothing-style segmented fill across the middle, ET66-style single high-contrast latch on the right. Three zones, no text sentence, readable across a room (Nest Farsight test).
2. **State by form — the strip's states:** turning reel + filling segments = holding; parked reel + full segments = complete; end-segments only = peek; missing token pip = emergency break spent. If a state needs a caption, the form is wrong.
3. **Arming is a closing:** the strip's latch shutting (Blockit case logic + Playdate fold-away restraint) is the moment a session becomes real — and the early-exit key deliberately lives on a *different* surface (Brick's separated key).

— Apparat (Scout 2)
