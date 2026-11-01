# Tor — Round 11 direction (the critic's verification + N-charges)

Source: docs/critic-charges.md. Formal verification is now on
record: **FIX-1 CURED ON PAPER** (all symbols traced; the four
callers pass their paths; H-11's rider stands — compile proof
is Ren's), **FIX-2 CURED on both faces** (door and Deck read
the same span derivation; they cannot disagree).

## Ratified

- **FIX-1's deviation stands.** The shipped `leaseLive`
  derivation adds `resolvedTier == PEEK_2M` to the audit's
  literal spec. Stricter and more correct — a re-tiered
  LOCKED row with a stale lease stamp must not silence the
  sniper. Nobody "fixes" it back to the audit's text.

## Adopted into the build order

- **N-2 (S2) — the grant must not be session-blind.**
  `peekOfferFor` takes no session input, and
  `start2MinutePeek`'s only gate is its re-derivation: a door
  standing past its session's end prints a past instant as
  "SESSION ENDS…", and a peek tap still writes a lease +
  cooldown anchor and books "Used 2m Peek" — the next session
  inherits a cooldown for a peek taken in no session. Cures,
  all three:
  1. The offer derivation takes session liveness as an
     input; a dead session yields no offer and no peek line
     (Baseline law 4's shape: a door that owes nothing prints
     nothing).
  2. A past end instant never prints: the view model
     dismisses the door at session end rather than letting
     it stand over a corpse.
  3. The grant's re-derivation includes session-live; a dead
     session refuses the grant (no lease, no cooldown, no
     booking).
  Filed as FIX-5d in the closing mapping.
- **N-3 — the cure rewrites the comment too.** The overlay's
  comment at :574-576 claims the cooldown print "never
  quotes a number minutes stale" over the self-ticking loop
  FIX-5b charges. When FIX-5 lands, that comment is rewritten
  to describe the derivation that now exists — a comment
  asserting an absent property is a print that lies.
- **N-4** (MainActivity:131's fabricated "Short-Form Feed"
  hero name) is the same charge as Letzte's X-7 — already in
  round 7 (resolved label or package verbatim). Two seats,
  one cure.
- Noted for the builder's verification pass: F-2's jump is
  branch-dependent (~55dp offer branch, ~17dp withheld) —
  verify stationarity on BOTH branches; a cooldown door
  alone would understate the defect. The skeleton cures
  both by construction.

— Tor
