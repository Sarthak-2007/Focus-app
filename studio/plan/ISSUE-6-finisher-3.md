# ISSUE #6 — FINISHER-3 · F16 + F18 AS ONE CHANGE: THE SEALED FRAME
## Finisher seat, Rost's cell · Fri Oct 30, 2026 · Paper only. Nothing compiled.
## Sources: AUDIT-deck.md §8/§9 (F16, F18) · ISSUE-6-designer.md §2/§3 (D8, D9, D10) · TactileDeckScreen.kt as it stands on agent/issue-6.

My job: the designer's skeleton (D8/D9) said *what* moves where.
This spec says *where everything stands, to the dp*, so the
builder cannot land the restructure and drift the frame. One
change, two halves: F16 (pinned stack → state-gated proof
drawer; TIME/ENDS never leaves the Deck) and F18 (the Fusion:
hero into the wheel hub; the dial seals in place as its own
clock; rim-ink progress). If either half lands without the
other, this spec has not landed.

Out of scope, named so nobody widens it: F6's STOP wording,
F7's ghost content, F12's ink values, F22's semantics build.
Where those items touch an address below, this spec fixes the
address only; their owners keep the rest.

---

## §1 · THE COMPOSITION LAW — Δy = 0

**Across the seal, across every state change in §3, and across
the drawer's open/shut, no named address in §2 moves. Not by
1dp, not for one frame, not during the 1050ms shot.** A state
change may change *what* an address prints, *whether* a
conditional element exists, and the *length of the scroll tail*
below the secondary slot. It may never change a coordinate in
§2. Transition-caused Δy is measured at scroll offset 0 (the
offset every state entry starts from; the shot already freezes
scroll, and that freeze now extends to every state entry frame).

Three corollaries, each aimed at a way this law dies quietly:

1. **One view, one life.** The wheel is a single
   `RotaryWheelView` instance, composed once, never swapped,
   never re-factored across the seal. What changes at the seal
   is its lock flag and its hub's print — not its existence.
2. **Growth goes up, never down.** Anything a state adds above
   the slide (the fault sentence) grows the pinned block
   *upward* into the scroll viewport. The pinned tail — slide,
   gap, imprint, bottom padding — is bottom-anchored as one
   rigid block; its distance from the frame's bottom edge is a
   constant, not a sum that varies by state.
3. **Print, not rig.** The drawer does not animate height. It
   lands at full open height in one frame and leaves in one
   frame (the strip's own law: density flips land in one frame,
   no easing, ever). A height tween would put the drawer's
   bottom edge — and everything the eye tracks against it — in
   motion for no informational gain.

---

## §2 · THE ADDRESS LEDGER — EVERY ADDRESS THAT MUST NOT MOVE

Coordinates: `yD` = dp from the Deck's top edge; `yA` = dp from
the top of the 740dp app frame (yA = yD + 48; the header band
owns yA 0–48, the rail yA 688–740, the Deck the 640dp between).
`XC` = frame centre; `L` = 12, `R` = W − 12 (the standing
content margins, TactileDeckScreen.kt:540,931 — kept).
Host translation (e.g. a future status strip seated by issue
#4's cell) shifts every yD uniformly; it is a frame move, not
a state Δy, and is the only permitted uniform shift.

| Address | Box / centre | Notes |
|---|---|---|
| ADDR-HEADER | yA 0–48 | Host chrome. 47dp row + 1dp hairline at yA 47–48. |
| ADDR-RAIL | yA 688–740 | Host chrome, 52dp. PARK overlays consume 0 layout dp. |
| ADDR-SCROLL-ORIGIN | yD 0 | Scroll content origin; offset 0 at every state entry. |
| ADDR-WHEEL-BOX | x L–R, yD 8–296 | The 288dp wheel view. First scroll child; nothing above it, ever. |
| ADDR-WHEEL-CENTER | (XC, yD 152) / yA 200 | Wheel disc centre. outerR 129.6, socketR 136.6 at the 288dp seat. |
| ADDR-HUB-DISC | centre ADDR-WHEEL-CENTER, r 64.0 | innerHubR = outerR × 0.494, as drawn today. |
| ADDR-HUB-GLYPH | optical centre (XC, yD 152); line box yD 116–188 | The primary readout. 72sp line, DialClockTextStyle (Type.kt:63–70, lineHeight 72, centred trim). Positioned by its centre, never by a baseline a sub-line can push. |
| ADDR-HUB-SUBLINE | centre XC, yD 192–206 | Anonymous Pro 10sp in a 14dp box, hung +40dp from hub centre. It hangs *from* the centre; it never feeds back into the glyph's position. |
| ADDR-RIM-BAND | r 108.6–118.6 about ADDR-WHEEL-CENTER; start angle 108° | The major-tick band (ticks today: rOuter = outerR − 11, major rInner = outerR − 21). Progress ink lives here and nowhere else. |
| ADDR-FOOTLINE | centre XC, yD 304–320 | 16dp box, 8dp below the wheel box. TIME/ENDS lives here in **all** states (§4). |
| ADDR-SECONDARY-TOP | yD 340 | One anchor, two tenants: ghost row top and drawer top are the same line. 20dp below the foot line — the object gap (D12). |
| ADDR-GHOST | x L–R, yD 340–366 | 26dp row: ghost title (+ DONE cap, running). |
| ADDR-DRAWER-TOP | yD 340 | = ADDR-SECONDARY-TOP. Shut height is **0dp** — not reserved, not a slot (§5). |
| ADDR-SLIDE | x L–R, yD 512.5–584.5 / yA 560.5–632.5 | The 72dp register. Every tenant (slide, STOP chair, sled) fills it edge-to-edge, 72dp tall — a 58dp body centred in it is a different address and is forbidden. |
| ADDR-IMPRINT | x L–R, yD 592.5–632 / yA 640.5–680 | 39.5dp, 8dp below the slide. Internal: rule 1dp at 592.5, gap 2, rule 0.5dp at 595.5, gap 6, tenant box yD 602–622 (20dp), lip zone yD 622–626 (1px line at +2), trailing 6. Tenant strike origin: left margin, mid-height — as filed (E-8). |
| ADDR-FAULT-SENTENCE | x L–R, yD 482.5–504.5 | Pinned form only (§3, standing fault): 22dp, 8dp above the slide. Its growth is upward (§1.2): pinned top moves, ADDR-SLIDE does not. |
| ADDR-PINNED-TAIL | slide bottom → frame bottom = 55.5dp | Gap 8 + imprint 39.5 + bottom padding 8, beneath the slide; with the slide itself the rigid block is 127.5dp from slide top to frame bottom. Constant in every state, fault included. |

Pinned block totals: normal 135.5dp (yD 504.5–640: padding 8,
slide 72, gap 8, imprint 39.5, padding 8); fault 165.5dp
(yD 474.5–640: padding 8, sentence 22, gap 8, then the normal
tail untouched). The scroll viewport is what remains —
504.5dp normally, 474.5dp in fault. Fault eats scroll tail;
it never eats an address. (The designer's ~120dp figure is
this block's internal 119.5dp — slide + gap + imprint —
before its 16dp of outer padding.)

---

## §3 · PER-STATE FRAMES AT 740dp

All frames at scroll offset 0. Anything not listed for a state
is absent in that state — absence is composition, not a
placeholder. Brightness order in every frame: glyph > rim/hub
> controls > ground; fault is the one ruled inversion (drawer
and sentence outrank the instrument, D13).

### IDLE — "How long, and into what?"
- Wheel yD 8–296, live. Hub primary = true span (`1H 50M`,
  breaks priced in; `FLOW` in FLOW). Hub sub-line = the recipe:
  blocks = 1 → `1 × 50`; blocks > 1 → `3 × 50 · +10 BREAK`
  (both of D8's prints, one line, no fact dropped).
- Foot line: `ENDS HH:MM · PLANNED` (timed) / `NO END` (FLOW).
- Ghost row at ADDR-GHOST (title only, read-only). Drawer shut
  (0dp). No utilities, no sentence, BLOCK printed nowhere.
- Pinned: slide at ADDR-SLIDE (words live, D11), imprint
  unstruck at ADDR-IMPRINT. Whole frame fits without scroll —
  content ends at the ghost row, yD 366, inside a 504.5dp
  viewport. This is the acceptance test, not a hope (§6).

### PROVING — "Is it holding?"
- Wheel frozen exactly as dialled (lock on, physics halted);
  hub primary and sub-line frozen at the dialled values. No
  readout starts on an unproven seal.
- Drawer OPEN at ADDR-DRAWER-TOP, proving form, 53dp
  (yD 340–393, §5). Ghost displaced — uncomposed, not stacked,
  not faded beneath.
- Foot line: the press-latched planned ENDS (the existing
  PressLatch discipline, rehomed from the strip to the foot
  line). It unfreezes only on annulment or seal.
- Pinned unchanged: slide seated at ADDR-SLIDE, imprint
  latched from the press. No sentence (the machine is working).

### SEAL SHOT (t0 → t0+1050) — the ceremony frame
- Same geometry as PROVING, two changes of print only: at t0
  the hub primary becomes the ticking readout (MM:SS; FLOW
  counts up) at ADDR-HUB-GLYPH, and rim ink (§4) starts from
  the seal's elapsed. The drawer stays open through the shot.
- The ground recedes (F15 as D13 rules it): scroll ground,
  drawer, ghost's empty room, pinned ground — scale 0.98, blur
  peak 3dp, same curves, same 1050ms. **The instrument, the
  slide, and the imprint do not transform** — no scale, no
  translation, no blur. Their layout *and* optical addresses
  are §2's, in every shot frame.
- At t0+1050 the shot lands; the drawer holds **one settle
  beat = 400ms** (the imprint ink's own settle duration,
  MonolithCustomComponents.kt:822–830 — one clock family for
  the whole settle), then cuts shut and the ghost cuts back.
  Drawer gone at t0+1450, not one frame longer (D9).

### RUNNING — "How long left, and what's in the way?"
- Wheel sealed in place: same instance, lock now session-long.
  Hub primary = ticking MM:SS, same glyph, same address.
  Sub-line: blank in plain running (the readout is the whole
  hub print); grace and break give it work, below.
- Rim ink carries progress (§4). No track, no % — anywhere.
- Foot line: `ENDS HH:MM` from the stored end only (no
  PLANNED — running, it is a fact). FLOW: `NO END`.
- Ghost row + DONE at ADDR-GHOST. Utilities yD 386–428
  (20dp below the ghost row; two 42dp chips). Nothing else.
- Pinned: STOP chair fills ADDR-SLIDE edge-to-edge (72dp);
  imprint struck at ADDR-IMPRINT, tenant at full alpha.
- **Grace (first 10s):** the hub primary becomes the grace
  countdown; the session MM:SS drops to the sub-line for
  exactly those seconds. One swap of values, zero swap of
  addresses; the chair's print never carries the countdown.
- **Routine-born:** born into this frame, settled, no shot,
  no drawer. Foot line prefixed for its whole life:
  `SLEEP · ROUTINE — ENDS HH:MM` (name = the session's own).

### BREAK (running sub-state) — "How long is my break?"
- Hub primary = the break countdown — the largest print on
  the face answers this state's question (F5, at the Fusion's
  address). Hub sub-line = `BACK AT HH:MM`. Foot line still
  carries the session's `ENDS HH:MM` — TIME/ENDS does not
  vacate its tenancy for the break.
- END BREAK chip: full width, yD 440–482 (12dp below the
  utilities row — its standing seating, kept). All other
  geometry as RUNNING.

### JAMMED — "Is it holding?" (answer: not proven)
- As PROVING, with the drawer in jammed form, 81dp
  (yD 340–421, §5): the jam sentence prints **inside** the
  drawer, and nowhere else this frame. Instrument frozen.
  Pinned stack does **not** grow a second copy — one fact,
  one rendering.

### IDLE-FAULT — the wire is not LIVE, no attempt live
- Onset: the drawer opens in jammed form (81dp) with the
  refusal sentence inside it — fault gets the stage first.
- Standing (drawer collapsed after its settle beat, wire
  still not LIVE): drawer 0dp, ghost restored at ADDR-GHOST,
  and the sentence takes its pinned form at
  ADDR-FAULT-SENTENCE; the pinned block grows upward to
  165.5dp and the scroll viewport shrinks to 474.5dp.
  ADDR-SLIDE and ADDR-IMPRINT do not move (§1.2).
- The handoff is a cut, and the two sentence forms never
  co-compose in the same frame.

### FINISH-DETAINED / RECEIPT — the held frame
- The last RUNNING frame, frozen: hub readout and rim ink
  frozen at ADDR-HUB-GLYPH / ADDR-RIM-BAND, foot line frozen
  (the stored end, never re-derived), ghost row and utilities
  at their running addresses, inert.
- Slide — not the chair — seated at ADDR-SLIDE through
  detention and through the receipt; the sled's return is
  motion *inside* the slide's box and never moves the box.
- The receipt slip is an overlay: it consumes 0 layout dp and
  reflows nothing. When it clears, the next frame is IDLE,
  whole, at §2's addresses.

---

## §4 · THE FUSION'S TWO MECHANISMS, PINNED

**The hub readout (F18).** One print channel into
`RotaryWheelView`, three lives at one address: span (idle /
proving, frozen), readout (running / break / grace / finish),
`FLOW` (the mode as print, never a door). Requirements the
builder cannot trade away:

- The glyph is drawn **outside the hub's press-translated
  group** (see §7, flag 6): the 1dp press dip moves the hub
  disc; it must never move the readout. At the seal the press
  depth is already returning — a glyph riding it would travel
  up to 1dp across the exact frames Δy = 0 exists for.
- Single-press only, and only on the static span (idle /
  proving). Ghosts OFF on FLOW, on every ticking readout, on
  break and grace countdowns (D5).
- **Finisher's call — the tetrad wells retire as print.**
  The wells/bar ring sits at R40 about the hub centre
  (BlockitCoreViews.kt:929–981); the primary line box
  (yD 116–188) passes straight through the upper wells' seats,
  and the sub-line declares the count in words. Two renderings
  of one count, colliding in one disc: the wells and closure
  bars stop printing in the Fusion form. Their *hit geometry*
  is untouched — centre-cycle and well direct-set targets keep
  their radii (hub disc, R40 ring) as invisible press targets.
  If Rost reinstates the wells, the glyph cannot hold
  ADDR-HUB-GLYPH and this spec's law falls with it — the call
  is load-bearing, not taste.
- The hub's semantics value (F22's debt, D8/§5 of the
  designer) is this readout's string — one view, one name,
  one value, spoken from ADDR-HUB-GLYPH's print.

**Rim ink (F18/D4).** Progress is ink in ADDR-RIM-BAND:

- Arc centreline r 113.6, stroke 10dp (band r 108.6–118.6),
  butt cap (fraction endpoints exact, no cap overshoot),
  drawn *under* the tick strokes so the scale stays legible.
- Start 108° (the rim's own convention), sweep =
  324° × elapsed/target. The 36° dead sector never inks — a
  full ring would claim travel the wheel does not have.
- Truth terms (F3, carried into the replacement derivation):
  elapsed/target, **filling**, no floors, no inversion. The
  floored, inverted `progressFraction` (TactileDeckScreen.kt:
  304ff) dies with the track; nothing of it survives into the
  rim's derivation.
- FLOW: no arc, at any elapsed, ever. No denominator, no fill.
- Handover at the seal: idle's 4dp setting sweep (the dial's
  own trace, same band) retires as the ink enters. One band,
  one tenant per state — set-trace at idle, progress ink when
  sealed, never both in one frame.

**TIME/ENDS (F16's amendment, D9).** The foot line at
ADDR-FOOTLINE is its only home, in every state in §3 —
planned and labelled at idle, latched through proving,
stored-end fact when running, frozen when held. It never
enters the drawer, never returns to a strip cell, and no
second rendering of it exists on the face.

---

## §5 · THE PROOF DRAWER — OPEN / SHUT GEOMETRY (F16/D9)

- **Seat:** scroll column, x L–R, top edge at ADDR-DRAWER-TOP
  (yD 340). It takes the ghost row's room: while it exists,
  the ghost does not. It never takes pinned tenancy.
- **Shut:** height **0dp**. Not composed, or composed at 0 —
  either way it reserves nothing, prints nothing, and BLOCK
  is printed zero times on a healthy Deck.
- **Frame:** square corners (radius 0 — architecture, not a
  card), recess tone, 1dp vertical hairline between cells,
  cell content inset 10dp from the left — the ProofStripBand
  shell (MonolithCustomComponents.kt:644–704) reused as a
  body, minus its pinned life and minus its TIME cell.
- **Proving form — 53dp:** cells 41dp (yD 340–381) + seam
  12dp (yD 381–393). Two fact cells: **BLOCK** (55% of inner
  width) and **APPS** (45%) — BLOCK takes the reclaimed TIME
  width, because BLOCK is the drawer's question. Cell print:
  label at top+4, value 15sp / 20dp line height, as the strip
  cells print today. The seam survives as architecture only:
  its 1dp hairline at yD 381 and 11dp of empty beneath it —
  **the seam stamps do not ride it** (D14: they live in PARK
  now; a forwarding print is a new rendering and is refused).
  While the drawer is open, the imprint tenant stands latched
  and does not update: the drawer's APPS is the only *live*
  count print on the face.
- **Jammed form — 81dp:** proving form + 6dp gap + the
  sentence's 22dp inside the frame (yD 399–421), Atkinson
  Hyperlegible 15sp / 20dp, inset 10dp, wired to the pane that
  owns its lack. Fault gets more room than health (Dens's
  law) — the growth is the drawer's height and the pinned
  head's, never a named address.
- **Life:** exists only in PROVING, JAMMED, IDLE-FAULT onset,
  and the one 400ms settle beat after a proven seal (§3).
  Everywhere else it is 0dp. Dens's Two-Height Strip remains
  the priced fallback if Rost refuses the drawer at merge —
  recorded, not ruled, exactly as the designer left it.

---

## §6 · ACCEPTANCE — HOW THE MERGE PROVES IT (paper/render, no compile)

1. **Frame diff.** Layout bounds captured at IDLE, PROVING,
   t0, t0+1050, t0+1450, RUNNING, BREAK, JAMMED, standing
   IDLE-FAULT, FINISH-DETAINED: every §2 address identical
   in all ten. Any diff, any address, any frame — the change
   has not landed.
2. **Idle fit.** The IDLE frame composes whole inside the
   504.5dp viewport with scroll offset 0 and nothing clipped
   (content ends yD 366; pinned top yD 504.5).
3. **One instance.** The wheel view reference is identical
   before, during, and after the shot; its lock flag, once
   set at t0, is never cleared for the session's life.
4. **Drawer audit.** Shut height measures 0; proving 53;
   jammed 81. The string at ADDR-FOOTLINE is present in every
   state and absent from the drawer's subtree in all of them.
   BLOCK appears zero times on the healthy idle face.
5. **Rim audit.** Timed session at half elapsed: ink sweep
   162°. FLOW at any elapsed: no ink. No `%` text, no 6dp
   track, no hero panel, no standing eyebrow, no FLOW cap is
   composable anywhere in the Deck after the change — if any
   of the five still composes, the Fusion has not landed
   (D8's own test).
6. **Lamp test.** Idle and sealed frames read as a monotonic
   luminance staircase (glyph > rim/hub > controls > ground);
   the fault frame shows drawer/sentence on top (D13).

---

## §7 · WHERE THE CURRENT CODE MAKES Δy = 0 IMPOSSIBLE — FLAGS

Each flag names the mechanism, not a vibe. All must be cured
by the one change; none is curable by tuning values.

1. **The wheel is swapped out at settle.**
   TactileDeckScreen.kt:654 composes the wheel only while
   `!isSessionRunning || shotActive`; the else branch at
   :718–754 replaces it with a 6dp track and a % row. There is
   no frame in which a hub readout survives the seal, because
   the view that would carry it leaves composition at exactly
   that moment. Cure: §4's single-instance wheel.
2. **The hub has no print channel.** The hero glyph is a
   separate Compose `Box` above the wheel
   (TactileDeckScreen.kt:634–652, inside the card at
   :548–580), and `RotaryWheelView.onDraw` draws the hub as
   disc + core + tetrad wells — its own comment: "the count
   lives only as hub geometry — no numeral, no word"
   (BlockitCoreViews.kt:915–917; wells at :928–982). The
   view's setters take minutes, blocks, and lock — nothing
   else. ADDR-HUB-GLYPH does not exist in this code; it must
   be built, not moved.
3. **The vertigo transforms the instrument itself.**
   TactileDeckScreen.kt:563–577 applies scale 0.94,
   translationY −10dp, and blur up to 6dp to the instrument
   card during the shot. Layout bounds may hold; the rendered
   wheel and glyph travel up to 10dp and shrink 6% across the
   seal — Δy = 0 fails optically in every shot frame. Cure:
   F15 as §3 rules it — transform the ground, never the
   instrument.
4. **TIME/ENDS lives only inside the pinned band.**
   `ProofStripBand` is composed unconditionally in the pinned
   column (TactileDeckScreen.kt:933–942) and TIME is its
   third cell (MonolithCustomComponents.kt:662–667); the
   `timeCell` derivation has no other consumer on the Deck.
   Delete the band as-composed and TIME/ENDS leaves the Deck
   with it. Cure: split the band — foot line takes TIME
   (§4), drawer takes BLOCK/APPS (§5) — never a whole-band
   cut.
5. **The sentence's slot is in the pinned tail.**
   `RefusalSentenceSlot` is composed *below* the slide
   (TactileDeckScreen.kt:1036–1070, after the slide box at
   :944–1035) in a permanently reserved 22dp box
   (MonolithCustomComponents.kt:747). Because it sits in the
   tail, moving it to the pinned head (D10) changes the tail's
   length by 30dp (22 + its 8dp spacer) and drags the slide
   with it. Cure: §1.2's bottom-anchored tail — the sentence
   leaves the tail entirely; the tail's 55.5dp becomes a
   constant before any state is allowed to add a head.
6. **The slide's tenant does not fill its register.**
   TactileDeckScreen.kt:950 swaps the 72dp slide view for
   58dp chips (:1017–1032) centred in the 72dp box. The zone
   holds; the body moves ±7dp at both edges on every swap.
   Cure: every tenant of ADDR-SLIDE fills 72dp (§2).
7. **The lock is shot-scoped, not session-scoped.**
   TactileDeckScreen.kt:672 sets `isLockedSession =
   shotActive` — the lock is designed to release at settle,
   in the same frame the view is discarded (flag 1). A dial
   that seals in place as its own clock needs the lock held
   for the session's life on the surviving instance.
8. **The hub press translates the hub group.**
   BlockitCoreViews.kt:853–854 draws the hub disc, core, and
   wells under `canvas.translate(0f, hubPressDepth × 1dp)`,
   and the lock setter (:409ff) answers a seal by *animating
   the return*. Any glyph drawn inside that group inherits up
   to 1dp of vertical travel across press and seal. Cure:
   §4 — the readout is drawn outside the translated group.
9. **There is no drawer seat.** The only proof-band call site
   in the Deck is the pinned one (flag 4); the scroll column's
   task section (TactileDeckScreen.kt:760ff) has no
   conditional slot at the ghost's address. The easy build —
   gating the pinned band — would move ADDR-SLIDE on every
   open/shut. The drawer must be born in the scroll at
   ADDR-DRAWER-TOP (§5), where its height is paid by the
   scroll tail, which is the only currency Δy = 0 accepts.

What already holds, recorded so the builder doesn't break it
while curing the above: the pinned stack's bottom-anchoring
across the seal for the slide zone (the audit's §5 acquittal),
the hoisted wheel/slide view references, and the PressLatch
discipline — all three are load-bearing in this spec and are
kept by name.

---

## §8 · CLOSING

One change: the band dies as a tenant and is reborn as a
drawer; the panel dies and its glyph is reborn in the hub;
the slide, the imprint, and the foot line never move house
at all. After it lands, the Deck is one instrument on a
ground, a drawer that exists only while proof is the
question, and a pinned tail that no state can push around.
That is the gallery frame. Anything less is the skeleton
wearing the old room.

— FINISHER-3, Rost's cell, issue #6. DECLARED, uncompiled.
