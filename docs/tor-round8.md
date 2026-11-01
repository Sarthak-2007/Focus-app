# Tor — Round 8 direction (Daumen's thumb-travel spec)

Source: docs/feel-2-daumen.md. Builder: the touch layer of
FIX-6 is specced as follows.

## Ruled

- **The card is BOTTOM-anchored.** This supersedes the
  designer skeleton's §3.4 top-anchor. Ground: F-2 is a
  thumb defect, and the thumb's frame is screen space. Under
  top-anchoring, RETURN sits 314dp vs 168dp from the top
  across the two card heights — a 146dp cross-visit amnesia:
  the same door, a different address, depending on which
  branch he drew. Under bottom-anchoring RETURN's address is
  invariant across states, branches, and visits: **116–172dp
  from the safe-area bottom, centre 144dp**, inside the
  thumb's rest arc. The card grows upward into empty night;
  Glanz's 124dp-from-card-bottom invariant survives intact
  and now denotes a screen address too. Glanz re-verifies the
  inverted stack at his conformance pass; Druck's D-F1
  concurs.
- **T-LAW 3 is law:** a tap fires only the slab its
  pointer-down landed in. Slides cancel; they never retarget.
  (Tests T-V3 in Daumen's file.)
- **Slip floor:** ≥48dp of dead ground between RETURN and
  PEEK, always. The specced stack yields 78dp; a pointer at
  RETURN's centre must travel 106dp to dispatch on PEEK.
- **Press states, adopted whole:** acknowledgement on
  pointer-down within 90ms (MonolithKin PRESS vocabulary);
  fill-deepening + 2dp border drawn INSIDE unchanged bounds;
  `indication = null`; no scale, translation, or ripple.
  RETURN pressed fill **#F57F51** (accent +8% white; ink
  contrast rises 5.37→≈5.8:1). PEEK pressed fill **#443D37**.
  The slabs press in opposite value directions so touch alone
  says which is under the thumb. Cancel = deepening releases
  within 150ms; nothing fires, nothing books.
- **Honest exit:** 48dp effective touch target (invisible
  extension beyond the text), with a ghost-fill pressed
  state — a bare-text line must telegraph its press like
  everything else on this face.
- **Reserved zone discipline:** 0–298dp from the safe bottom
  is the door's touch territory; print inside it is dead to
  touch; the scrim and card shell are never clickable; no
  target sits within 116dp of the safe bottom (the gesture /
  fumble band is never in play).

## Verification

Daumen's T-V1–T-V7 are the builder's test list for this
layer — bounds equality across states AND branches, the
dead-ground tap grid, press-origin slide tests, press
timing, cancel cleanliness, second-pointer, edge safety.

— Tor
