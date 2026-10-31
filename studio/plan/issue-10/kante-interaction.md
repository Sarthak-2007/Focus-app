# ISSUE #10 — KANTE: INTERACTION DETAIL FOR THE RECEIPT

*Kante, interaction-detail specialist, dedicated-100 programme.
Ground: this cell's `ReceiptSheet.kt`, Feel 1 (arrival/feed),
Feel 2 (tear & edge, B1 proposal), Feel 3 (settle/dwell/cut),
Feel 4 (sound map), Feel 5 (paper character). Paper only —
nobody compiles. Fonts/palette law untouched; stock/ink per
Feel 5 (`#FAF6F0`, two densities, frame-invariant).*

*This is the surface Sarthak explicitly loved (❤️, batch-7
render) — its print character is a proven keeper, so my seat's
rule here is stricter than anywhere: I add physics only where
the seats left a default running, and I touch nothing they
measured. Settled here already, NOT re-opened: feed 280ms /
Curve A / 56dp / no alpha (Feel 1); settle = the curve's tail;
dwell 2000ms, DOWN-judged, invisible (Feel 3); dismissal = 0ms
cut (Feel 3); the whole lifecycle silent except the E-clock's
machine voices (Feel 4); tear geometry/stick-slip/teeth (Feel 2
§2, B1, unruled). What follows is the layer underneath those:
the slip's own scroll, its press behaviour, the feed's slot,
and the tear's haptic waveform stated to the millisecond.*

---

## K10-1 · THE SLIP'S SCROLL — PAPER UNDER A THUMB (PATCH-READY)

A tall slip scrolls (`ReceiptSheet.kt:147`, `verticalScroll(
rememberScrollState())` — stock everything). Unspecced until
now; a 20-line deflection slip is the case that matters.

1. **Weight:** the paper scrolls 1:1 under the finger with a
   heavy fling — Compose `ScrollState` fling via
   `Modifier.flingBehavior` equivalent for scrollable: decay
   friction multiplier **1.5× stock** (same exponential-decay
   family as the Drawer spec, one notch heavier than PARK's
   sheet, lighter than nothing else moves here): a full fling
   travels ≈ **1.4× the slip's visible height** and is under
   100 px/s within ≈ **600ms**. Thermal paper on a table does
   not glide across the room when flicked; it slides and stops.
2. **Overscroll: zero.** No glow, no stretch at head or foot.
   The foot is the paper's end (and, if B1 lands, its tear
   zone) — a rubber-band at the foot would flex the perforation
   for free, a lie about the material. Hard stop, both ends.
3. **The head is the arrival face.** Scroll offset is 0 at
   settle and returns to 0 across recomposition/re-stand of the
   same slip (key the ScrollState on `receipt.sessionId`):
   nobody meets a receipt already scrolled to its footer.
   Dismissal forgets the offset entirely — the next slip is a
   new sheet, born at its head.
4. **Scroll never dismisses, never tears.** A downward fling on
   the paper is spent entirely on scroll travel/decay; it arms
   nothing. (B1's tear grip zone is the tab + bottom 24dp of the
   body per Feel 2 §2.5.2 — inside that zone the drag is the
   tear's, outside it the scroll's; the boundary is spatial,
   never velocity-based, so a fast scroll starting above the
   zone can never "become" a tear mid-flight.)
5. No scrollbar, no scroll sound, no scroll haptic, at any
   velocity. The paper is read, not operated.

## K10-2 · PRESS BEHAVIOUR ON THE PAPER — CERTIFIED NOTHING (SPEC)

Feel 3 §3.3 rules flinchlessness as feel; this makes it a
buildable press spec:

- The paper carries **no indication of any kind**: no ripple
  (`indication = null` wherever a clickable/swallow exists on
  the Column), no press dip (0dp), no ink step, no scale
  (1.000 at every frame of every press), no shadow change.
  Pressing the slip must be pixel-indistinguishable from not
  pressing it, in frame grabs taken at DOWN+16ms, DOWN+100ms,
  and UP.
- The faulted slip's explicit dismissal act (paper DOWN,
  post-dwell, Feel 3 §4.3) inherits this exactly: the most
  deliberate press in the receipt's life gets the same nothing.
  Its weight is in WHERE the finger must go, never in a
  reaction. Any builder who adds a "acknowledgement" dip to
  the faulted paper-DOWN has moved the acknowledgement from
  the act to the surface — refused by name.
- The scrim zone likewise: no ripple on the room. Dismissal is
  a cut (Feel 3 §4.2); a ripple would still be spreading when
  the frame is already gone — an artefact of a dead surface.

## K10-3 · THE FEED NEEDS A SLOT — CLIP AT THE ANCHOR (PATCH-READY)

Feel 1 feeds the paper up 56dp into its bottom anchor. With the
A5 contact anchor (0dp bottom/sides) the screen's own edge is
the slot and the feed reads correctly. But until A5 lands — and
on any host that keeps padding — the displaced paper's lower
56dp would paint OVER the room/nav band as a floating sheet
sliding up a wall, not paper emerging from a machine. Close it
in the sheet, independent of A5:

- The host `Box` clips the paper's container to the region
  **above the anchor line** (`clipToBounds` on the Box, with
  the paper bottom-anchored inside it): during the feed, exactly
  the paper above its rest bottom edge is visible; the 56dp of
  travel happens behind the slot line. First visible frame: the
  slip's head and first lines already above the line, opaque,
  rising — emergence, not approach.
- The scrim is NOT clipped and NOT translated (Feel 1 §2.2
  already moves it outside the animated container): it stands
  at 0.12 from frame 0 across the whole room, including below
  the slot line — the room dims; only the paper is slotted.
- Restored slips: offset 0, clip inert, zero frames affected.

## K10-4 · THE TEAR — HAPTIC WAVEFORM TO THE MILLISECOND (B1-DEPENDENT)

*Proposal tier only — spends nothing unless Ren rules B1 in.
Feel 2 §2.6 set the voices (per-cluster tick 5ms @ 0.45,
amplitude seeded 0.35–0.55, full `playTearPulse()` at
separation); this states them as waveforms an implementer can
type, and adds the two joints Feel 2 left qualitative.*

1. **Per-cluster tick (9×):** `VibrationEffect.createOneShot(5,
   amp)` with `amp = round(255 × a)`, `a` seeded per cluster in
   **0.35–0.55** (Feel 2) → amplitude range **89–140**. Tick
   onset within **1 frame (≤16ms)** of its cluster's visual
   break; a tick that lands a frame late reads as the NEXT
   cluster's, and the stick-slip rhythm (the whole feel) smears.
   Audio twin per Feel 2: faint tick, same instant, pitch fixed.
2. **Resistance curve refinement (amends Feel 2 §2.5.2's flat
   0.55):** the tab's follow ratio is not constant — paper
   stiffens as bridges part. Ratio by clusters broken:
   **0.62 (clusters 0–2) → 0.55 (3–5) → 0.46 (6–8)**. Finger
   travel to commitment (cluster 5) ≈ 52–58dp — inside Feel 2's
   56dp budget; total tab travel at commitment ≈ 31dp as filed.
   A constant ratio feels like a drawer on rails; the falling
   ratio is the felt difference between *pulling* and *tearing*.
3. **Point-of-no-return haptics:** crossing cluster 5 fires
   NO special tick — commitment must not be announced by a
   bigger buzz (that would teach him to drag to the buzz, a
   game mechanic). The only felt change past commitment is
   that letting go no longer springs the tab back: the
   auto-run (35ms/cluster) ticks continue at the seeded amps.
4. **Separation:** `playTearPulse()` verbatim — haptic timings
   `(0, 8, 14, 8)ms` / amplitudes `(0, 160, 0, 110)` — onset in
   the same frame as the final cluster's break, never layered
   with any other voice (Feel 2 §2.6, Feel 4 row 8's prohibition
   stands until B1 rules the tear INTO dismissal; this spec is
   what that ruling would spend).
5. **Abort (release before cluster 5):** the 180ms Curve A
   spring-back carries **zero** ticks — including no tick at
   the moment the tab re-seats. Re-inking clusters are silent
   in both channels (Feel 2). A re-seat tick would book the
   abort as an event; it is a non-event by design.
6. **Reduced motion (Feel 2 §2.5.5):** waveform unchanged —
   ticks and separation pulse still fire; only the visuals
   yield. The fact of tearing is felt even when it is not seen.

## K10-5 · PRINT STRIKE — THE ONE DETAIL NOBODY SEATED (SPEC-ONLY)

The slip's character Sarthak loved is typographic before it is
material, and two rendering facts protect it; stated so a
refactor cannot quietly spend them:

1. **Every glyph at final value from feed frame 0** (Feel 1 §1.4)
   — extended: also at final WEIGHT and final tracking. No
   variable-font weight ramp, no letter-spacing settle, no
   "ink darkening" pass over the first 300ms. A receipt is
   struck once, whole, by the machine that issued it; a slip
   whose print visibly *develops* is a photograph, not a print.
2. **Text renders on integer dp boundaries at rest:** the paper
   Column's rest offset is exactly 0 (Feel 3 §2.3) — keep the
   feed's final frame snapped to offset 0.0, never 0.2dp of
   residual animator value: Anonymous Pro at 11sp/1sp on a
   sub-pixel offset greys at the stems, and the loved crispness
   of the ledger is precisely stem contrast. Acceptance: stem
   pixel columns of the settled slip sample at the two lawful
   ink densities (Feel 5 §3), not at blended thirds.

## ACCEPTANCE, BY FINGER, EYE, AND FRAME GRAB

1. Tall slip: flick the paper — ≈1.4 visible heights of travel,
   dead inside ~600ms, hard stops at head and foot, no glow;
   re-stand the same slip (PARK round trip) and it meets you at
   its head.
2. Press the paper anywhere, any duration, faulted or clean:
   frame grabs at DOWN+16/+100ms and UP are pixel-identical to
   rest. The scrim press likewise — then the cut, with no
   ripple artefact in the last standing frame.
3. Feed with A5 reverted (12dp host padding): the rising slip
   is cut by the anchor line — at no frame does paper paint
   below its rest bottom edge; the scrim covers the full room
   from frame 0.
4. (B1, if ruled) Tear slowly: nine felt ticks, each within a
   frame of its visible cluster break, amps audibly/felt
   uneven (89–140) but same-pitched; past cluster 5 the pull
   palpably stiffens (ratio 0.46); separation pulse lands with
   the final break; abort at cluster 3 springs back in total
   silence and re-seats without a tick.
5. Settled slip, macro frame grab: ledger stems at full ink
   density — no grey third density anywhere in the print.

— Kante, interaction detail
