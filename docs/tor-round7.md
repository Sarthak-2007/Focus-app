# Tor — Round 7 direction (Fein's truth table + Letzte's matrix)

Sources: docs/finisher-4-fein.md (C-01…C-12, Cases A/B/C),
docs/finisher-5-letzte.md (X-1…X-11). Builder: both files are
part of the FIX-3/FIX-5/FIX-6/FIX-7 spec now.

## Fein — rulings on the two corrections that needed Tor's word

- **C-11 RULED IN FULL: book at arrival, outcome updated in
  place.** Intercepts currently book only if they resolve, and
  timestamps stamp the resolution — so the taper would count
  resolutions while printing arrivals, and the receipt reads a
  late instant as the reach. Arrival row at trigger (arrival
  timestamp, package, path), outcome written into the same row
  when known. The cheaper half-measure is rejected: half-true
  counts are how this face got audited. This also cures L-10
  and L-11.
- **C-07 RULED: a live lease outranks its own cooldown** for
  the leased package in the withheld derivation (armed the
  moment C-06's re-derivation lands).
- The rest of Fein's corrections are mechanical and adopted
  whole, including: **C-01 kills "Habit" at ALL THREE layers**
  — VM state default, recordIntercept's parameter (Repo:2109),
  AND the entity column default (Models:459) the audit missed —
  plus disarmActiveSession's system-row insert (Repo:2347)
  passes UNSTATED explicitly. Kill only two and Habit survives
  on every system row.
- FIX-5's tests pin Fein's Cases A/B/C (floor-then-ceiling
  minute theft; the ≤999ms early peek through both gates;
  the standing "AGAIN IN 0 MIN").
- `splitActiveTaskAndReturn` — dead but armed, books "Stuck"
  + a YouTube fallback, no caller in tree: dies under FIX-3b's
  dead-wire doctrine.
- Closing grep receipt for FIX-3, run by builder AND Fein:
  `"Habit"|"Stayed Focused"|MUSCLE_MEMORY|com.zhiliaoapp.musically`
  → zero hits in app/src/main.
- L-09 (phantomReachesBlocked increments on a taken peek) is
  the C-13 rider's source — notification surface, Ren's to
  seat elsewhere; noted in the closing comment, not fixed here.

## Letzte — rulings

- **X-3 confirmed:** the "Locked"→"Blocked" sweep is FOUR
  sites — VM:157, VM:2318, MainActivity:133, ShieldService:185.
  Swept by the book, the shield door is the only door that
  could still print "Locked". All four change.
- **X-1 acquitted by ruling:** the footer badge speaks two
  grammars (tier on the honouring doors, door-identity on
  guard/sniper/site) because both answer the same question —
  by what law am I stopped — with the applicable law. Not a
  defect; no change.
- **X-8 resolved by round 4:** RETURN's two addresses (294 /
  168 from card top) are one address under Glanz's invariant —
  124dp from card bottom in both branches. Uniform.
- **X-9 classified correct:** the taper memory line prints
  only where a peek offer can exist (app tier + shield). The
  other doors' silence is Baseline law 4, not an omission.
- **X-4a:** the site door resolves its browser name via
  PackageManager's application label for the browser package;
  unresolvable → package name verbatim (same rule as the
  guard door). **X-4b** (domain lost to the ledger) is NOT
  cured here — no schema change in this cell; flagged to Ren
  as a batch-10 migration candidate in the closing comment.
- **X-5:** FIX-3e stands in the order (round 1) — and the
  FIX-4 onNewIntent route must not resurrect the composer-
  draft fallback it kills.
- **X-10 — shield BACK parity mechanism:** the shield service
  has no performGlobalAction route; route the BACK through
  the accessibility service (the blocking stack already
  depends on it) before the shield door launches. If a path
  exists where no such route can fire, say so plainly in the
  DECLARED close — never fake the parity.
- **X-7:** the UNKNOWN door's "Short-Form Feed" name fiction
  dies under the same name-slot law: resolved label or
  package verbatim.

— Tor
