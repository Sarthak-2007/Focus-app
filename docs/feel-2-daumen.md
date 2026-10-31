# FEEL — THUMB TRAVEL + TOUCH (Issue #11)

Seat: **DAUMEN, Feel Specialist 2**, Tor's cell · For: Tor (lead)
Slice: the thumb only — where it rests, how far it travels, what
it can and cannot hit by accident, and how a slab answers a press.
Not the wall's tempo (ATEM's), not the man's inner weather
(DRUCK's), not the skeleton's slot order (Designer's — adopted
whole; this file prices its *altitude and its targets*).
Ground read, whole or in the relevant part:
`studio/plan/AUDIT-held-door.md` (FIX-6, F-2 — the founding
defect); `docs/designer-skeleton.md` (§3 slot stack, §3.4
addresses, §5 Z-zone internals, §6 RETURN, §7 honest exit);
`docs/feel-1-atem.md` §4.2 (input honesty at the arm);
`docs/feel-5-druck.md` D-F1 (bottom-anchor charge) and D-F12
(hold-to-take endorsement);
`app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt`
(`AirlockInterceptOverlay` as shipped, :550–720);
`app/src/main/java/com/example/ui/components/MonolithCustomComponents.kt`
(`BlockitActionChip` :170–224 — the chip both targets are built
from today); `app/src/main/java/com/example/ui/MonolithViewModel.kt`
(`transmuteImpulseAndReturn` :2428 — RETURN's commit sound);
`app/src/main/java/com/example/ui/theme/MonolithCurves.kt`
(`MonolithKin` — the house PRESS vocabulary);
`app/src/main/java/com/example/MainActivity.kt` (:412,
`WindowInsets.safeDrawing`).
**Nobody compiles.** Numbers quoted from the audit/skeleton are
cited; numbers that are mine are flagged for Tor's ratification.

---

## 0. The one-sentence verdict

> **RETURN must be the shortest journey on the face, PEEK the
> longest, and the distance between them must be dead ground a
> slip cannot cross.** The fixed skeleton already orders the
> stack correctly; this file anchors it to the bottom of the
> glass — where the thumb already lives — and prices every gap,
> target, and press state in the reserved zone so that the only
> way to take a peek is to *travel* to it, on purpose, in full.

Three laws carry the spec:

- **T-LAW 1 — ONE ADDRESS, FOREVER.** RETURN's bounds are
  identical in every state of a visit (the skeleton's law),
  in every branch of a visit (peek-owing and never-owed alike),
  and on every visit. Bottom-anchoring is what buys the second
  and third clauses; top-anchoring cannot (§2).
- **T-LAW 2 — GAPS ARE GRADED BY THE COST OF CONVERSION.** The
  dead ground between two targets is sized by what a mistaken
  crossing would buy: a lease (PEEK) gets 78dp of dead ground;
  a reversible, unbooked route (the honest exit) gets a 12dp
  visual band with a 6dp effective floor. There is no uniform
  gap on this face, because there is no uniform sin.
- **T-LAW 3 — A TAP FIRES ONLY THE SLAB ITS POINTER-DOWN
  LANDED IN.** Press origin is destiny. A press that starts on
  RETURN and slides 130dp north before release fires nothing;
  a press that starts in dead ground and slides onto a slab
  fires nothing. Travel during a press is a cancellation in
  progress, never a retargeting.

---

## 1. What the shipped code does to a thumb today (the ground truth)

- Both targets are the same component at the same size:
  `BlockitActionChip` hardcodes `.height(42.dp)`
  (`MonolithCustomComponents.kt`, in the chip's modifier chain)
  — RETURN and EMERGENCY PEEK are 42dp twins, undifferentiated
  to the hand, 6dp under the platform's 48dp minimum touch
  target before the audit's 56/48 ruling is even consulted.
- The chip's touch handling is a bare
  `.clickable(onClick = onClick)` (:205): no
  `MutableInteractionSource`, no pressed state, no indication
  override. The only press feedback the shipped door has ever
  offered is the default Material ripple — a light wash that
  (a) is a light effect on a face whose other seats ban light
  theatrics, and (b) blooms *under the thumb pad that is
  covering the slab*, i.e. it is addressed to a witness who is
  not there. The man pressing gets nothing he can see or feel
  at the edges of his own finger.
- RETURN is composed *above* the peek branch in a centred,
  `Arrangement.spacedBy(16.dp)` column
  (`AirlockAndDisarmOverlays.kt` :595–665): at
  `countdownSecs == 0` the peek chip and price insert *below*
  RETURN (:663ff), the centred card grows ≈110dp, and RETURN
  jumps ≈55dp — more than its own 42dp height — under a thumb
  already travelling to it, onto EMERGENCY PEEK (F-2). The
  founding defect is, in touch terms, precise: **the target
  vacated its address at the decision instant, and the reward
  moved into the vacated address's landing zone.** Any spec
  below that does not make that sequence geometrically
  impossible has failed, whatever else it does.

---

## 2. The anchor — why bottom, and what it does to the addresses

The skeleton (§2, §3.4) top-anchors the card and files RETURN's
centre at **314dp from the top of the safe area** (peek-owing)
and **168dp** (never-owed). Within a visit both are stationary,
and the skeleton law holds. But read as *touch*, top-anchoring
has two costs the skeleton did not price:

1. **Cross-branch amnesia.** The thumb's muscle memory is built
   across visits, not within one. A man who learns RETURN at
   314-from-top on peek-owing nights and finds it at 168 on
   LOCKED nights has learned *no address at all* — he must aim
   visually, eyes fully engaged, every visit, at 1 AM. The
   146dp branch difference is F-2's disease at a slower clock:
   the target is not where the hand remembers.
2. **Altitude.** On a large phone (≈780–820dp of safe height),
   314-from-top is ≈470–500dp from the bottom — outside the
   one-handed rest arc (§3), reachable only by a full thumb
   extension or a grip shift. DRUCK's D-F1 files the same
   charge from the eye's side; from the thumb's side it is
   worse: a grip shift mid-craving is a fumble risk, a second
   hand recruited, a phone re-seated in the palm — friction
   applied to exactly the act the door exists to make free,
   and *not* applied to waiting (waiting needs no reach).

**Ruling applied (per my brief: RETURN is bottom-anchored;
DRUCK D-F1 concurs): the card anchors to the bottom of the
safe area**, same 20dp outer padding, same slot stack, same
heights. Everything below the peek zone is then positioned by
arithmetic from the bottom, and Z — the only branch — grows
*upward, away from every target*, when it exists. RETURN's
address becomes branch-invariant by construction, not by
assertion: there is nothing between RETURN and the bottom of
the glass whose height can vary.

*Consequence flagged for Tor:* skeleton §3.4's two addresses
(314 / 168 from top) are superseded by §4's single address
below. The stationarity tests in skeleton §11 survive
unchanged in form — they should be re-pinned to assert
equality *across branches* as well as across states, which
top-anchoring could never have passed. If Tor instead holds
top-anchoring for cross-face grammar, §4's distances-from-top
are the skeleton's, and the 78dp gap / press / dead-ground
specs below stand untouched — but the cross-branch charge
should be filed as priced-and-refused, not left invisible.

---

## 3. The thumb's real journey (the map)

One hand, large phone, bottom grip — the grip this app is used
in at 1 AM. The thumb pivots from the palm's lower corner; its
comfortable arc, no grip shift, no stretch, covers roughly the
bottom 40% of the glass. Full-width slabs (RETURN and PEEK are
both full content width, skeleton §5.3/§6) collapse aiming to
one dimension: lateral error is absorbed by the slab's width,
so every number below is a *vertical* distance. That is itself
a design mercy worth stating: the face asks the thumb a
one-dimensional question.

| Phase | Where the thumb is | What the spec must honour |
|---|---|---|
| **A — The triggering tap** | On the blocked thing itself: a launcher icon (position is his home screen's, unknowable from this tree), a feed surface mid-scroll (sniper doors — thumb already in the lower-right scroll zone), a link in a browser (site doors). It has just completed a tap or a flick when the door fires. | Nothing. The door does not get to choose this position; it only inherits its aftermath — a thumb in motion, a decision not yet formed. |
| **B — Arrival + reading (≈0–1s)** | Retracts off the glass to the rest arc: hovering over the bottom ≈60–180dp band of the screen, lower corner side. The eyes do the work in this phase; the thumb waits *where it will be needed*, because hands pre-position toward the body's next likely act without being told. | In this band, from the bottom up, he will find: dead ground, the footer print (not a target), the honest exit, dead ground, **RETURN**. The rest arc and the reserved zone are the same territory — §4's addresses are laid out inside it deliberately. Note the one hard consequence: there is almost no *safe set-down* inside the card's lower stack — a thumb that lands, lands on a target. That is acceptable because the nearest, largest landing is the safe one (RETURN) and the other landing in the arc (the exit) is reversible and books nothing (skeleton §7). Parking is a hover on this face; the spec does not pretend otherwise. |
| **C — The wait (1s → wall end, 5–10s)** | Pre-positions. The wall (5s base, 10s tapered cap) is an eternity against a ~quarter-second ballistic movement: whatever he decides, his thumb has time to be *already there* before he acts. In the shipped door this is the phase that was betrayed — the thumb travelled during C toward RETURN's address, and the address moved at the end of C. In the fixed door the thumb drifts to RETURN's centre (§4: 144dp from the safe bottom) and hovers, or rests its decision unmade. | RETURN live and tappable from frame one (acquitted, six seats) is what makes C honest: a thumb that settles *onto* the slab at any point in C has simply returned early. There is no armed state to wait for on the safe side of the face. |
| **D — The decision press** | For RETURN: a press where it already hovers — travel ≈0–40dp, the shortest act on the face. For PEEK: a deliberate extension **130dp centre-to-centre** upward, crossing 78dp of dead ground containing the price and the memory line — the thumb passes *over the price* on its way to the thing priced, which is the correct order of information and effort both. | The asymmetry *is* the design: the way back costs no travel, the way through costs a journey. Any future change that shortens D-for-PEEK (moving the chip south, enlarging it into the buffer) is spending the door's core protection and must be priced as such. |
| **E — Press, commit, or cancel** | Down on a slab (§6 states), then up inside it (commit), up outside it (cancel — door stands, nothing books), or a slide (T-LAW 3: travel cancels, never retargets). | After a RETURN commit the face exits (260ms, skeleton §8) and the visit's touch story is over. There is no second act for a stray second finger to perform (§6.4). |

**Left hand / right hand:** irrelevant by construction — full-
width slabs mean the pivot corner changes nothing vertical.
**Two-handed use:** rarer at this hour, and strictly easier;
no spec branch is needed. **The sniper case deserves one
line:** a feed-scroll thumb arrives with *momentum habits* —
it flicks. The door's arrival seam (FIX-7, BACK before launch,
180ms fade) must not convert a residual flick into a scroll or
a tap on this face; the overlay composes no scrollable and its
targets require a fresh pointer-down inside their bounds
(T-LAW 3), so a carried-over gesture dies on dead ground or on
the scrim. Builder to keep it so: no `clickable` on the scrim
or card shell, ever — a stray tap outside the three targets
does nothing, books nothing, dismisses nothing.

---

## 4. The touch spec — positions from the screen bottom

All positions are **dp from the bottom of the safe area**
(`WindowInsets.safeDrawing`, MainActivity :412). Distance from
the *physical* bottom edge of any given device = the device's
bottom safe inset + the figure below; the spec never hardcodes
an inset. Card bottom edge sits 20dp above the safe bottom
(the shipped Box's 20dp padding, kept). Stack arithmetic is
the skeleton's §3.1 read from the bottom: card bottom padding
24, footer 16, gap 8, exit 36, gap 12, RETURN 56, gap 16,
then Z (110, peek-owing) whose internals run Z3 18 / gap 4 /
Z2 34 / gap 6 / Z1 48 from its bottom.

| Element | Bottom edge | Top edge | Centre | Status |
|---|---|---|---|---|
| Card bottom edge | 20 | — | — | anchor |
| Footer badge (print, **not a target**) | 44 | 60 | — | dead to touch |
| **HONEST EXIT** (visual slot) | 68 | 104 | 86 | target, §5.3 |
| Dead band (visual) | 104 | 116 | — | 12dp, T-LAW 2 |
| **RETURN slab** | **116** | **172** | **144** | target, §5.1 |
| Dead buffer (gap 16 + Z3 18 + gap 4 + Z2 34 + gap 6) | 172 | 250 | — | 78dp, §5.2 |
| **PEEK chip** / gate stack (same bounds, skeleton §5.4) | **250** | **298** | **274** | target when armed, §5.2 |
| Card top edge (peek-owing branch, 418dp card) | — | 438 | — | — |

Laws these numbers encode:

- **RETURN centre 144dp from the safe bottom** — inside the
  rest arc of §3 Phase B, reachable with zero grip shift and
  zero extension, identical in the never-owed branch (where
  the card is simply shorter *above* RETURN: card top at
  20 + 292 = 312, every figure in this table unchanged).
- **No target lives within 116dp of the safe bottom.** The
  system gesture zone and the fumble band at the glass's edge
  are never in play; the lowest target (the exit, a deliberate
  act) still sits 68dp up. Nothing on this face can be fired
  by a palm edge, a grip adjustment, or a home-gesture that
  starts too high.
- **Lateral margins:** targets begin 44dp from each screen
  edge (20 outer + 24 card padding) and span the content
  width — on a 360dp-wide phone, 272dp wide; on a narrow
  320dp phone, 232dp. Horizontal fat-finger error is a
  non-event on this face; §5's margins are all vertical,
  because the risk is all vertical.

---

## 5. Gaps, slip geometry, and fat-finger margins

### 5.0 The patch model (assumptions stated, not smuggled)

An adult thumb pad, pressed flat by a half-asleep man leaning
on the glass, makes a contact patch up to ≈48dp tall — the
reason 48dp is the platform's minimum target in the first
place. Two facts discipline everything below: (1) Compose
dispatches a tap by the pointer's position, not by the patch's
*extent* — a patch that visually overlaps a neighbouring slab
while its centre sits in dead ground fires **nothing**; (2) all
slip claims are therefore made twice, once for the dispatch
point and once for the patch, and must hold under both.

### 5.1 RETURN (56dp slab)

- **Size:** 56dp tall (FIX-6 ruling) — the platform minimum
  plus 8dp of forgiveness, spent on the one target that must
  never be missed. A centred worst-case patch (48) fits
  *entirely inside the slab* with 4dp to spare top and bottom:
  a perfectly aimed fat thumb touches nothing else, even
  visually.
- **Dispatch tolerance:** the pointer may sit anywhere within
  ±28dp of centre vertically and still fire RETURN. To fire
  *anything else* it must then cross dead ground (§5.2/§5.3).
- **Miss cost:** a short press lands in the 12dp dead band or
  on the exit (reversible, unbooked — §5.3); a long press
  lands in the 78dp buffer (dead — price/memory print, no
  target). Every neighbouring outcome of a botched RETURN is
  either nothing or an undoable route. There is no direction
  in which a failed return becomes a lease.

### 5.2 RETURN ↔ PEEK — the conversion that must never happen

- **Edge-to-edge dead ground: 78dp** (16 gap + Z's Z3 18 +
  gap 4 + Z2 34 + gap 6). **Centre-to-centre: 130dp.**
- **Dispatch math:** a pointer aimed at RETURN's centre must
  travel **106dp** upward — 28 to RETURN's top edge, then the
  full 78 of dead ground — before it can dispatch on PEEK.
  106dp is 1.9× RETURN's own height. A slip, a tremor, a
  jolt, a half-asleep sag is a single-digit-dp event; the
  conversion distance is an order of magnitude larger and is,
  not coincidentally, the deliberate extension of §3 Phase D.
  **Minimum law for any future layout: RETURN↔PEEK dead
  ground ≥ 48dp** (one full minimum-target of nothing). The
  specced 78 carries 30dp of headroom above the law; a
  proposal that spends it must name what it bought.
- **Patch math:** worst-case patch centred on RETURN's top
  edge reaches 24dp into the buffer and stops **54dp short**
  of PEEK's bottom edge. Even the *shadow* of a return press
  cannot touch the chip.
- **Temporal guard, restated in touch terms:** the chip does
  not exist as a target before the arm, and at the arm it
  fades in *inside Z1's long-reserved bounds* (skeleton §5.4;
  ATEM §4.2: tappable from the arm instant). The thumb's
  Phase-C position is 130dp away from those bounds. The
  shipped defect — a target materialising *at* the travelling
  thumb's destination — is impossible twice over: the
  destination never moves, and the materialisation happens
  nowhere near it.
- **Slide guard (T-LAW 3):** a press begun on RETURN, slid
  north across the whole buffer, and released on the armed
  chip fires *neither* — RETURN's press cancels on exit, and
  PEEK never received a pointer-down. The journey to PEEK
  must begin as a journey to PEEK.

### 5.3 RETURN ↔ HONEST EXIT — the small gap, defended

- **Visual dead band: 12dp** (skeleton §3.1). The exit's
  visual slot is 36dp — below the 48dp platform minimum for a
  target. As a *deliberate* act it may be smaller than a
  reflexive one, but 36 is too small to defend as specced.
- **My correction (SMALL, for Tor):** the exit's *effective*
  target extends 6dp into the gap above it and 6dp into the
  gap below it (toward the footer, which is print and can
  never be fired): **effective bounds 62–110 from the safe
  bottom, effective height 48dp**, visual slot unchanged, no
  slot moves. Consequences, priced honestly: the *effective*
  dead band between RETURN and the exit shrinks 12 → **6dp**,
  which becomes the face's minimum effective gap and its
  floor law (no effective gap on this face < 6dp). The
  asymmetry against §5.2's 78dp is T-LAW 2 working as
  intended: crossing *this* band buys a reversible route to a
  face whose first word is its own consequence, which books
  nothing and whose KEEP FOCUSING returns him unharmed
  (skeleton §7). The cost of the worst slip on the face is
  therefore: one extra face to close. The cost of the §5.2
  slip would be a lease, a cooldown, and a ledger entry —
  hence 78 there and 6 here, and any reviewer who calls the
  asymmetry inconsistent has not priced the conversions.
- **Patch math:** worst-case patch centred on RETURN's bottom
  edge reaches 24dp down: across the 12dp band and 12dp into
  the exit's *visual* slot — but dispatch follows the pointer,
  which sits in the band: nothing fires. The patch overlap is
  a visual event only, and the door does not grade visuals.

### 5.4 The reserved zone, as a whole

The **reserved zone** is the territory from the safe bottom
to PEEK's top edge: **0–298dp**, containing three targets, two
dead bands, one 78dp buffer, and nothing else that can be
fired. Rules of the zone:

- No other clickable — scrim, card shell, price, memory line,
  footer, gate stack *as a stack* — may ever be composed in
  it. Print is dead to touch throughout.
- The zone is identical in every visit state; COUNTING,
  OFFER, HELD, and NEVER-OWED differ only in Z1's tenant and
  Z's presence *above* RETURN (skeleton §8), never in any
  address at or below RETURN.
- System intrusions (a notification shade pull, a gesture)
  are the platform's business; within the app's control, the
  zone's geometry is asserted by test (§7), not by care.

---

## 6. Press states — how a slab answers a finger

### 6.0 The principle

A press must be acknowledged **on pointer-down, in the slab's
body, by a change the pressing man can perceive at the edges
of his own thumb** — not by sound (the visit's sounds belong
to arrivals and commits, per FIX-7c and the VM's existing
wiring), not by light (no ripple, no glow, no shadow bloom),
and never by movement (a slab that shifts, scales, or dips in
translation under a press has broken the address law at the
one moment the address matters most — mid-aim). The answer
is a *deepening*: the slab's fill changes value and its border
draws in, bounds pixel-identical, like a mechanical key's
cap darkening against its bezel. The house already owns this
tempo: `MonolithKin.PRESS_MS` (dip within 90ms) and
`PRESS_BACK_MS` (return ≤150ms) are the FIX key's press
vocabulary; the door's slabs speak it unchanged.

### 6.1 RETURN — state table

| State | Fill | Border | Label | Timing / notes |
|---|---|---|---|---|
| **Idle** | `NightColorPrimaryContainer` #F47442 | 1dp, `NightColorPrimary` α0.38 | #471602 (5.37:1, F-3) | as specced by the skeleton §6 |
| **Pressed** (pointer down, inside bounds) | accent lightened one step: **#F57F51** (accent + 8% white; my computed value — ink contrast *rises* to ≈5.8:1, so the deepening never costs legibility) | **2dp, drawn inside the bounds**, #471602 at full strength — the bezel closes around the key | unchanged, #471602 | reached within **90ms** of pointer-down on `EasingA`; fill+border only — no scale, no translation, no ripple (`indication = null`, explicit `MutableInteractionSource` + `collectIsPressedAsState`) |
| **Commit** (pointer up, inside bounds) | — | — | — | fires `onReturnToFocus` exactly once; the existing commit voice stands untouched (`transmuteImpulseAndReturn` plays the harmonic seal, VM :2430 — a sound for the *act chosen*, which this spec neither adds to nor moves to press-down); face exits per skeleton §8; input dead after first commit (no double-booking, skeleton §8's RETURNING rule, extended by §6.4) |
| **Cancelled** (pointer slides out of bounds, or up outside) | returns to Idle values | returns to 1dp α0.38 | unchanged | within **150ms** of the exit/up; **nothing fires, nothing books, the door stands.** The pressed deepening visibly *releasing* as his finger leaves the slab is the cancellation's whole message — he feels the key come back up under a departing thumb |
| **Disabled** | — | — | — | **does not exist.** RETURN is never disabled, dimmed, or gated (acquitted, six seats); there is no state row to build |

### 6.2 PEEK chip — state table

| State | Spec |
|---|---|
| **Not yet armed** | No chip, no target: Z1 holds the gate stack, which is print. There is no disabled-chip state and no ghost target; a tap in Z1's bounds during the wall lands on dead print and does nothing. |
| **Idle (armed)** | 48dp, fill `NightColorSecondaryContainer` #4A423C, border 1dp α0.38, label cream (skeleton §5.3). 48dp is the platform minimum met *exactly*: no expansion of its effective bounds is permitted in any direction — upward is card, downward spends §5.2's buffer. **Effective = visual, 250–298.** |
| **Pressed** | Fill deepens one step: **#443D37** (chip fill + 8% black; my computed value — label contrast ≈8.8:1, up from ≈8.1). Border 2dp inside bounds, cream at α0.92. Same 90ms / `EasingA`, no scale, no translation, no ripple. The neutral chip presses *darker* where the accent slab presses *lighter* — each moves away from the face's mid-tones toward its own pole, so a man can tell *which* slab is under his thumb from its edge alone, without reading. That difference is deliberate and is the only place on the face where touch carries identity. |
| **Commit / Cancelled** | As §6.1: commit fires `onTake2MinutePeek` once (grant re-derivation at tap is the acquitted machinery's, untouched); cancel is silent, total, and leaves the chip standing armed. If DRUCK D-F12 / the Innovator's hold-to-take is ruled in by Tor, this table's Pressed row is where its progress must live — inside the chip's bounds, in its fill — and the 90ms dip becomes the hold's first frame; nothing in §§4–5 changes. I take no position on the hold here beyond that compatibility. |

### 6.3 HONEST EXIT — press state

Bare text has no fill to deepen; its acknowledgement is the
ghost of a slab: while pressed, a #4A423C fill at the exit's
*effective* bounds (§5.3) fades in behind the text at 90ms,
corner 12dp, text unchanged; on release or slide-off it is
gone within 150ms. A man must be able to feel that he has
touched the exit before he has taken it — a bare-text target
with no pressed state is a trap disguised as a footnote, and
this line, of all lines, must never fire unannounced-feeling.

### 6.4 Multi-touch and stray-pointer law

- The first pointer-down in a slab owns that slab's press.
  Additional pointers — a second finger, a palm edge, the
  other hand arriving — change nothing: no re-trigger, no
  cancel, no commit. Commit/cancel is decided solely by the
  owning pointer's up position.
- Two slabs can never be pressed into a chord: a pointer in
  RETURN and another in PEEK fire whichever *owning* pointer
  lifts inside its bounds first, exactly once, and the face's
  exit ends the visit before a second commit can land.
- No target on this face responds to hover, long-press, or
  drag-in (T-LAW 3). Long-press on RETURN is a press held and
  then either committed or cancelled by the same up-position
  rule; the door adds no long-press behaviour of its own.

---

## 7. Verification the builder can encode (paper, for Ren's compile)

- **T-V1 One address.** Compose peek-owing and never-owed
  visits, in COUNTING, OFFER, and HELD; assert
  `airlock_return_button`'s bounds-from-safe-bottom equal
  (116, 172) in all six, and `airlock_peek_button`'s equal
  (250, 298) wherever composed. This is T-LAW 1 as an
  assertion, and it is the test F-2 can never regress past
  again.
- **T-V2 Dead-ground grid.** Synthetic taps on a 6dp grid
  across the reserved zone: fires RETURN only inside
  (116–172), PEEK only inside (250–298) when armed, the exit
  only inside (62–110); every grid point in (172–250),
  (110–116), and below 62 fires nothing.
- **T-V3 Press origin.** Pointer-down inside RETURN, move to
  PEEK's centre, up: assert neither callback fired. Mirror
  image (down on PEEK, up on RETURN): neither fired.
  Down in the buffer, slide onto RETURN, up: nothing fired.
- **T-V4 Press honesty.** On pointer-down on RETURN: pressed
  state (fill #F57F51, 2dp internal border) is composed
  within 90ms; bounds during press equal bounds at idle
  exactly; no ripple indication is installed on either slab
  or the exit (assert `indication == null` / no default
  ripple node in the chip's semantics).
- **T-V5 Cancel cleanliness.** Press RETURN, slide out, up:
  no callback, no booking call, overlay still visible,
  pressed state cleared within 150ms.
- **T-V6 Second pointer.** Down on RETURN (pointer 1), down
  on PEEK (pointer 2), up pointer 1 inside RETURN: exactly
  one RETURN commit; the visit ends; pointer 2's later up
  fires nothing.
- **T-V7 Edge safety.** Assert no clickable node on the face
  intersects the bottom 62dp of the safe area, and the scrim
  and card shell carry no `clickable` at all.

---

## 8. Rulings this seat needs from Tor

1. **§2 — bottom anchor ratified, skeleton §3.4 superseded.**
   My brief rules RETURN bottom-anchored and DRUCK D-F1
   concurs; the Designer's addresses were written top-
   anchored. Tor to record which anchor the builder builds,
   and §11/T-V1 re-pinned accordingly. Everything else in
   this file is anchor-independent except §4's table.
2. **§5.3 — the exit's effective target, 36 → 48dp by
   invisible extension** (visual slot untouched; effective
   RETURN↔EXIT gap becomes 6dp and is filed as the face's
   floor). The alternative Tor may rule: keep 36 effective
   and record a deliberate sub-minimum target on the face's
   most consequential line. I recommend against, in writing.
3. **§6.1/§6.2 — the pressed fills** (#F57F51 / #443D37,
   computed blends, contrast-checked above) and the
   border-inside press cue, replacing the shipped default
   ripple on all three targets. If Tor prefers a single
   press direction for both slabs, the identity-by-edge
   argument in §6.2 falls and I ask that its loss be noted.

---

## NEXT (Law 10)

(a) **What I want to do next:** take this same travel map to
the *taken* peek's homecoming — the floating pill that
carries the lease and the re-intercept when it expires. A
man returning from a peek has his thumb wherever the blocked
app left it, mid-feed, mid-gesture; nobody has yet priced
where the pill sits against that thumb, or whether the
door he re-meets is at the address his hand remembers from
the way in. T-LAW 1 should bind the round trip, not just
the visit.

(b) **What I could do, unassigned:** a reserved-zone audit
of the Disarm face in this file's terms — the 60-second
valve is the app's one *sustained*-contact target, and it
raises the questions this face never had to: patch drift
over a full minute of holding, grip fatigue changing the
contact centre, and what its slip geometry owes a thumb
that is tired *because the target made it tired*. The
valve's banked-quarters law means a slip there is priced
in seconds, not callbacks — a genuinely different touch
spec, ready to be written.

— DAUMEN, Feel Specialist 2, Tor's cell, Issue #11.
Report to Tor; nobody compiled; the user was not messaged.
