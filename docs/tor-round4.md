# Tor — Round 4 direction (Glanz's measured geometry)

Source: docs/finisher-1-glanz.md — real-font measurement
(fontTools on the shipped TTFs), not assertion. Builder: these
numbers supersede the designer's where they differ.

## Ratified

- Reserved heights: **110 offer / 110 withheld / 0 neither**;
  card 418dp peek-owing / 292dp never-owed. (Converges with
  Tor's round-2 ratification of the withheld 110.)
- **G2/G3:** the designer's never-owed RETURN centre (184dp)
  was arithmetic-wrong → **168dp**. The invariant to encode:
  **RETURN centre = 124dp from card bottom in BOTH branches**
  (96dp slab-bottom-to-card-bottom). Encode the invariant, not
  two absolutes.
- **G4:** Z internal split rebalanced, total unchanged:
  **Z1 48 / gap 4 / Z2 38 / gap 4 / Z3 16 = 110**.
- **G5:** withheld zone content alignment = **centred**
  (designer's self-contradiction resolved).
- **G6:** rule placement, exactly: gate text box 22dp at
  Z1+10; gap 4; **rule top = Z1+36dp, 2dp high**, square
  corners, left-anchored fill, no tween on the fraction.
- Glanz's movement register (M1–M9) binds the implementation:
  spacedBy ban, conditional-composition ban inside Z, no
  AnimatedVisibility inside Z, no weight-spacer bottom-anchor
  as mechanism, the BlockitActionChip 42dp/12sp/cream-ink
  reuse trap (RETURN/PEEK are new slabs, not that chip).

## Recorded casualty (not a defect to engineer away)

At font scale 1.15 on 320dp width, 1.3 on ≤360dp, and below
320dp width, the price sentence clips inside its reserved
slot. Stationarity holds regardless — that is the honest
trade, and it goes in the closing comment verbatim.

## Owed

Glanz's conformance pass over the FIX-6 code the moment it
lands (§4 table + §6 assertions, line by line).

— Tor
