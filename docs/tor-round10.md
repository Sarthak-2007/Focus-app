# Tor — Round 10 direction (Politur's wording table) + R-2 ruled

Source: docs/finisher-3-politur.md — the 20-row master wording
table is the face's final word surface. Builder: strings come
from that table, never from memory of the audit.

## Adopted whole

- Badge: m + remainder-s, no zero-padding — 60s→`1m`, 90s→
  `1m30s`, 120s→`2m`, 600s→`10m`, 45s→`45s`, 125s→`2m5s`;
  0s unrepresentable (PEEK IS OFF prints instead).
- Gate sequences from Politur's table exactly: wall =
  min(5 + prior booked intercepts of this package this
  session, 10); 1st visit 0:05 with memory silent; 2nd 0:06;
  3rd 0:07; cap engages at the 6th (0:10); from the 7th the
  wall stays 0:10 while the ordinal keeps counting.
  **`PEEK OPENS IN 0:00` is unrepresentable.**
- Druck's `THIRD ↔ 0:08` pairing was off by one (third visit
  = 2 priors = 7s) — tests are written from Politur's
  sequences, never from Druck's example. Recorded as a caught
  error, with thanks.
- Memory line final string: `{ORDINAL} TIME THIS SESSION`.
  TONIGHT revives only if the count derivation is ever
  re-scoped to a night (round 2's ruling, now final form).
- Break line: `BREAK AT {HH:mm} OPENS THIS APP`, three truth
  conditions per the table; P-3c's `— NO PEEK SPENT` coda is
  forbidden (a mid-craving comparison).
- Existing price tests encode the dead sentence
  ("TAKING THE PEEK STARTS…") and must be re-pointed to the
  re-timed one.

## Tor's calls

- **Price at n=0: silence.** A zero cooldown is no cooldown;
  printing "A 0 MINUTES COOLDOWN STARTS" would announce an
  event that never happens. If peekCooldownMillis == 0 the
  price line does not print — the same discipline as the
  no-session silence. Non-whole-minute cooldowns print by
  ceiling, never floor (Politur's guard).
- **R-2 RULED (the deferred caps question): the caps register
  stands** — instruments and sentences both, on this face,
  this pass. Data slots (his intention, app names, domains)
  are verbatim-only exceptions. Druck's two-voice proposal
  (D-F8) is a cross-face wording-book question for Hale's
  register and Ren's ratification; it is recorded as a
  dissent in Politur §12 and does NOT ship on this face by
  familiarity. The honest exit keeps its caps; its quietness
  is carried by form and position (Ruhe), not by case.

## State of the direction

Rounds 1–10 are the complete build spec for FIX-3…FIX-8 plus
adopted innovations. What remains: the builder's DECLARED
close, the four conformance re-walks (Glanz, Kante, Fein,
Letzte), the critic's verification, and Tor's integration.

— Tor
