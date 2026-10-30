# Plan — Nabe (Improvers) · The Hub (Deck dial centre cap)

PLAN WAVE — plan only, no code. Coordinator: Ilya.
Freedom basis: Sarthak's order, Oct 29 — citations lifted, shape is mine.
What still binds: ARM ≤10s · HOLD absolute · TRUTH once, in human language ·
his three fonts (real weights now shipped) · warm palette, never green ·
Ledger dead · hold banked · routines his · Guard open (none of those four are mine).

---

## CURRENT — what the code actually renders today (verified, not briefed)

Read directly from `BlockitCoreViews.kt` (`RotaryWheelView.onDraw` §5,
`onTouchEvent`) and `TactileDeckScreen.kt`. The brief's premise is only
half true, and the untrue half is the dangerous one:

1. **The hub's text stack was NOT deleted.** The cap still draws two lines:
   `BLOCKS` (or `FLOW`) — Atkinson Hyperlegible Bold 9.5sp, `tokTextSecondary()`
   (day `#615B5A` / night `#9C8E84`), baseline cy −18dp — over the count via
   `drawMixedWordAndMono`: digit in Anonymous Pro Bold 24sp `tokPrimary()`
   (day `#252323` / night `#FFE4CF`), the `×` as a different family at 13sp in
   the secondary colour, baseline cy +6dp. Stack sits ~9dp above optical centre,
   unchanged from wave-1's measurement.
2. **The readout is NOT the total. It is the block length.** `primaryReadout`
   in `TactileDeckScreen` = `dialMinutes × 60` formatted `MM:SS` at 84sp —
   e.g. `25:00`. The total was demoted, not promoted: it lives in the 10.5sp
   caption underneath as `${dialMinutes * studyBlocksCount} MIN TOTAL`
   (e.g. `50 MIN TOTAL`). Synthesis item 16 says the readout is the TOTAL,
   live while dragging. Shipped code does the exact opposite.
3. **So the Deck currently shows the session length three times, in three
   sizes, saying three different things:** 84sp `25:00` (one block), 10.5sp
   `50 MIN TOTAL` (the session), hub `2×` (the multiplier), and the slide
   badge adds a fourth: `25M` (block length again). A tired user at 03:30
   reads the biggest number as the session. **That is wave-1's "arm 100
   believing 50" failure, still live:** set 25 × 4 and the hero number says
   25:00 while the arm commits 100 minutes.
4. **Geometry / touch, unchanged from wave-1:** drawn hub radius
   `outerR × 0.52` = 56.2dp (Ø 112.3dp at the 240dp view); touch recomputes
   with a different constant (`min × 0.46 × 0.52`) and an extra `× 0.85`,
   giving an effective tap radius of 48.8dp. The visible outer ring of the
   cap starts a wheel drag instead of cycling. Fires on **ACTION_DOWN**,
   instantly, no pressed state, no animator. Feel: `fireFeel(4L)` →
   `playMechanicalLatch(true)`, waveform `[0,11,26,9]ms @ [0,215,0,150]`,
   760Hz / 16ms / gain 0.54 transient.
5. **Range still disagrees with itself:** the view clamps
   `studyBlocksCount` to **1..6**; the Deck caller cycles **1..4**. Nothing
   on the cap shows either range.
6. **FLOW still lies silently:** `minutesValue < 10` → label `FLOW`, glyph
   `∞` — and the tap still cycles the now-invisible block count, which
   reappears when minutes rise again.
7. **What did change since wave-1 (credit where due):** real font weights
   are shipped (Anonymous Pro Bold and Atkinson Bold are true files; the
   synthesised-smear era is over), and the night staircase moved the tokens
   — hub fill `tokSurfaceBright()` is now day `#FAF6F0` / night `#362C27`,
   variant day `#D8D1CB` / night `#100E0C`, and the night label/count pair
   (`#9C8E84` vs `#FFE4CF`) separates again. The cap itself is still one flat
   fill, one 1dp hairline, one hard shadow (`0x26000000`, +2dp, no blur) —
   the emptiest object at the exact centre of the hero screen. While a
   session runs the wheel (and hub) is not drawn at all — the Deck swaps in
   a progress track. That stays true at every rung below.

**Governing judgement for this ladder:** fix the lie before the object.
A beautiful cap on a Deck whose hero number means the wrong thing is
decoration on a falsehood. Rung 1 is therefore the truth swap; every later
rung assumes it has landed.

---

## LADDER — 4 rungs, each independently shippable, each assumes only the ones above it

### Rung 1 — TRUTH, ONE HOME (numbers only; no geometry, no new feel)

- **Readout becomes the total, live.** `primaryReadout` = `dialMinutes ×
  studyBlocksCount`, formatted in human language: `< 60` → `50` with the
  caption carrying the unit; `≥ 60` → `1H 40` style (max case 180 × 4 =
  720 → `12H 00`, still fits 84sp at 4 glyphs + space). It updates on wheel
  drag and on hub cycle alike — one number, always the session length.
- **Caption becomes the factorisation, in words:** `2 BLOCKS OF 25 MIN`
  (Anonymous Pro, 10.5sp, 1sp tracking, `onSurfaceVariant`) — the factors'
  only home, at caption size, where factors belong. FLOW caption: `NO
  LIMIT` (unchanged). The string `MIN TOTAL` dies; the total needs no
  label because it is the hero.
- **Slide badge dies.** `subMonoBadge` blank in timed mode (the pinned
  slide sits under the readout's own panel — repeating the total there is
  a second rendering of the same fact at the moment of commitment);
  `FLOW` badge stays in flow mode (a mode, not a number).
- **Range + touch honesty:** view clamp `coerceIn(1, 6)` → `coerceIn(1, 4)`;
  touch radius = drawn radius (delete the `0.46` recompute and the `0.85`
  shrink — 56.2dp in, 56.2dp out).
- **Type optics (cheap, same commit):** stack optically centred — label
  baseline cy −18dp → cy −8dp, count baseline cy +6dp → cy +14dp; `2×`
  drawn entirely in Anonymous Pro Bold (digit 24sp, `×` 15sp, both
  `tokPrimary()`, shared baseline).
- **Technique:** mechanism = state recompute in `TactileDeckScreen` +
  two constant fixes in the view; motion = readout value cross-fade
  120ms, Curve A `cubic-bezier(0.2,0,0,1)` on cycle, no animation on drag
  (live means live); haptic = unchanged, latch waveform
  `[0,11,26,9]ms @ [0,215,0,150]` on ACTION_DOWN; sound = unchanged
  760Hz / 16ms / gain 0.54 transient; depth/tokens = unchanged (flat cap,
  `tokSurfaceBright`, hairline, `0x26` shadow +2dp), zero new tokens,
  zero orange, zero green.

### Rung 2 — A REAL BUTTON (press state, fire-on-release, cancellable)

- **Recognition:** ACTION_DOWN inside the drawn hub radius arms the press;
  ACTION_UP inside (≤300ms, ≤10dp travel) commits the cycle; slide-off,
  timeout, or ACTION_CANCEL restores without cycling and without feel. A
  thumb landing dead-centre to start a wheel drag no longer silently
  rewrites the session.
- **Technique:** mechanism = pressed flag + two `ValueAnimator`s on the
  existing paints; motion = press-in 90ms Curve A — cap scale 1 → 0.96
  about centre, fill drops one tonal step (day `#FAF6F0 → #E4DFDA`, night
  `#362C27 → #100E0C`-ward via `tokSurfaceVariant()`), shadow offset
  2dp → 0.5dp and alpha `0x26 → 0x14`; release 280ms Curve B
  `cubic-bezier(0.05,0.7,0.1,1)` with count cross-slide (old digit fades +
  drops 4dp in 90ms Curve A, new digit rises from −4dp over 180ms
  Curve B); haptic = travel tick on DOWN, one-shot 4ms @ amplitude 90,
  commit waveform `[0,6,18,5]ms @ [0,200,0,120]` on UP; sound = commit
  transient 12ms, gain 0.42, pitch laddered to the new count
  560 / 620 / 680 / 740Hz for 1 / 2 / 3 / 4 — the count is audible before
  it is read; wrap 4→1 = descending double-tick
  `[0,5,30,5]ms @ [0,190,0,140]`, 740 → 520Hz; depth/tokens = existing
  token steps only, no new colours; FLOW = press animates, commit does
  nothing (no waveform, no sound, no hidden state change — the silent
  FLOW cycle dies here).

### Rung 3 — THE MACHINED CAP, COUNT AS PIPS (type leaves the cap)

- **Geometry:** visual cap radius 56.2dp → **44dp (Ø 88dp, the minimum
  touch target)**, as `0.41 × outerR` so it survives wheel growth; visual
  radius = touch radius. The freed 12dp annulus returns to bare wheel face.
- **Count as geometry:** `BLOCKS` and the numeral are deleted from the cap.
  Four pips on the lower arc — Ø 4.5dp, arc radius 21dp… (at this rung:
  straight row, cy +24dp, 9dp spacing) — filled = `tokPrimary()`, unfilled
  = 1.1dp hollow `tokHairline()`. Four positions are the range; filled
  positions are the value; readable in peripheral vision, in greyscale,
  at night. Centre of the cap: the count digit is gone — a single centre
  screw (Ø 7dp, slot at 35°) is the thumb's anchor.
- **Technique:** mechanism = stepped disc in a groove — groove ring
  r 44 → 40.5dp filled `tokSurfaceVariant()` (day `#D8D1CB` / night
  `#100E0C`), cap disc r 40.5dp `tokSurfaceBright()` (day `#FAF6F0` /
  night `#362C27`), 1dp hairline, lit top arc 1.2dp `0x40FFFFFF` day /
  `0x24FFFFFF` night, contact shadow `0x1F000000` +1.5dp under the cap
  only; three face screws Ø 5dp at r 34dp, 270° / 30° / 150°, slots
  unaligned (20° / 115° / 65°), fill one step below cap; motion = Rung 2
  press/release verbatim, plus pip pop — newly filled pip scales
  0 → 1.35 → 1 in 180ms Curve B, wrap drains pips right-to-left at 45ms
  stagger, 90ms each Curve A (≈315ms total wrap event — the wrap is
  *seen*, it is where the cycle teaches its shape); haptic = Rung 2
  waveforms verbatim; sound = Rung 2 ladder verbatim, transient shortened
  to 10ms / gain 0.40; depth/tokens = three tonal steps (face / groove /
  cap), no new tokens, zero accent on the cap in every state including
  wrap; FLOW = pips all hollow at 40% alpha, centre screw replaced by
  `∞` Anonymous Pro Bold 15sp `tokTextSecondary()` at 80% — the cap reads
  switched off, because it is; accessibility =
  `contentDescription = "Blocks: N of 4. Tap to change."`, updated per
  cycle (pips are never the only channel — caption + description are the
  redundant channels).

### Rung 4 — THE INDEXING CAP (count as position; pips retire)

- **Mechanism:** the whole cap face becomes a 4-position selector. Each
  committed tap rotates the cap (screws, centre slot, one index notch —
  8dp × 2dp ink line at the cap's rim) by **90°**, settling into a detent.
  Four index marks are printed on the static groove rim at 90° intervals,
  offset so count 1 sits at top. Count is read as *where the notch
  points* — one mark, one position, no enumeration at all. This is the
  rung that tests whether position beats quantity on this object.
- **Technique:** mechanism = rotation state on the cap layer only (groove,
  shadow, wheel static); motion = step 260ms Curve B +
  `MechanicalOvershoot(1.25)` confined to the final 60ms; wrap 4→1 is a
  single −270° sweep, 420ms Curve A into Curve B, marks passing in
  sequence — the long way back is the wrap's signature and cannot be
  mistaken for a step; haptic = per-detent waveform
  `[0,5,14,4]ms @ [0,190,0,110]` at each 90° boundary crossed (wrap =
  three detents + seat `[0,190,0,150,0,110]`), travel tick on DOWN as
  Rung 2; sound = detent transient at each boundary, pitch ladder
  580 / 660 / 740 / 820Hz by landing count, 10ms, gain 0.40; wrap descends
  740 → 520Hz across the sweep; depth/tokens = Rung 3 stack unchanged,
  index marks in `tokHairline()`, notch in `tokPrimary()`; FLOW = cap
  rotates to bottom dead-centre and parks, notch dimmed to 40%, tap inert
  (press animation only). Honest risk, stated: position must be *learned*
  (which clock point is 3?) where pips are *counted* at first sight —
  that risk is exactly what the Peak answers.

---

## ★ BEST-RATIO CHANGE (priced)

**★ Rung 1's truth swap, alone.** Cost: ~25 lines in
`TactileDeckScreen.kt` (readout recompute + human format + caption string
+ badge blanking) and 2 constant fixes in `RotaryWheelView` (clamp 1..4,
touch = drawn radius) — half a day including the format edge cases
(FLOW, 5-minute floor, 12H 00 ceiling). Zero new tokens, zero assets,
zero animation work. Return: the "arm 100 believing 50" failure — the
only defect in this element that can make a man commit to a session
length he never chose — is dead, and every later rung stops decorating a
lie. Wave-1's ★ was the press state; it is Rung 2 here and it matters —
but a button that feels honest while its screen lies is worse than a
mute button on a truthful screen, so the order flips.

---

## PEAK (≤8 lines)

The cap is a **segmented bezel**: Ø 64dp visual (r 32dp, `0.30 × outerR`)
seated in a Ø 73dp recess, touch Ø 88dp, the 32–44dp annulus a dead rest
zone that neither cycles nor starts a drag — a thumb can land on the
hand-anchor with no consequence. Its rim is four 78° arc segments split
by 12° gaps; filled segments in `tokPrimary()` ink *are* the block count
— quantity as construction, subitised at arm's length, larger and more
peripheral than any pip row, no type, no numeral, no label, ever.
Centre is bare: one screw, slot 35°, two-tone machined sheen (base =
exact midpoint of `surface`/`surfaceBright`, day and night), zero orange.
Commit sweep: the next segment fills clockwise over 180ms Curve B with a
5dp-deepening shadow pass; wrap drains counter-clockwise, 45ms stagger.
Feel: DOWN travel tick 4ms @ 90 silent; commit waveform
`[0,5,14,4]ms @ [0,190,0,110]`, transient 10ms / gain 0.40 pitched
580 / 660 / 740 / 820Hz by count, wrap descending 740 → 520Hz.
FLOW: all segments hollow at 40%, centre screw yields to a dim `∞` —
cap off. It beats Rung 4's position (learned) and Rung 3's pips (small)
on the only test that matters at 03:30: *how many, at a glance, half-asleep.*
Priced: ≈120 lines of `onDraw` rework + 2 animators + one
`playHubCycle(count, wrap)` engine variant + Rung 1's Deck changes;
no new tokens (midpoints of existing pairs only), no new fonts, ≈1.5 days
with night pass and the accessibility description.

---

## STANDARDS CHECK

- **ARM ≤10s:** the hub's worst case is 3 taps from the default count;
  each tap commits on release and the next DOWN is accepted immediately
  (animators never gate touch) — ≤1.2s of hub cost inside a full arm of
  wheel-set (~3s) + taps + slide (~1s), comfortably under 10s at every
  rung. At peak, the rest annulus removes the mis-tap recovery loop
  entirely (today a centre mis-tap costs a corrective cycle of taps).
- **HOLD absolute:** the hub is not rendered while a session runs (Deck
  swaps the wheel for the progress track — verified in code) and no rung
  adds a running state, a mid-session edit, or a decision. The banked
  60-second hold is untouched and not mine.
- **TRUTH once, human language:** after Rung 1, the session length exists
  as exactly one big number (the readout, = total, live), its factors as
  one caption sentence (`2 BLOCKS OF 25 MIN`), its count as one geometric
  rendering on the cap, and nowhere else — slide badge blank, hub type
  gone by Rung 3. Count and total can no longer disagree: the total is
  *computed from* the count in the same recompute, and FLOW's tap is
  inert at every rung from Rung 2 up, so no hidden state can drift.
- **His picks:** no new type at peak (geometry only); where type remains
  (Rungs 1–2, caption, FLOW glyph) it is Anonymous Pro / Atkinson
  Hyperlegible in the real shipped weights. Palette = existing warm
  tokens and their exact midpoints only; zero orange on the cap in all
  states; **never green** — no green value appears in any rung.

## CONFLICTS

- **Rad (Improvers, wheel — mail/rad/) — MAILED.** Peak + Rung 3 change
  the wheel's centre geometry (cap Ø 112.3 → 88 → 64dp) and peak's rest
  annulus changes touch dispatch at the centre: downs at 32–44dp radius
  must neither cycle nor start a drag. His wheel face gains the freed
  annulus; his drag-start logic loses the centre. Letter sent (see below).
- **Dario (MaxImpl, wheel spec — mail/dario/) — MAILED.** Same geometry
  change lands in his wheel spec: centre radii are re-specified as
  fractions of `outerR` (0.41 touch / 0.30 visual at peak) and the hub
  touch zone stops being a hole in his drag surface. Letter sent.
- **Synthesis item 16 vs shipped code — FLAGGED, resolved by Rung 1.**
  The synthesis already legislated readout = TOTAL; the code ships the
  opposite. This plan treats the synthesis as the intent and the code as
  the bug; Ilya to confirm no Deck owner holds the reverse brief.
- **Slide badge (Zug-II / Ines territory) — FLAGGED, not mailed.**
  Rung 1 blanks the timed-mode badge. If the slide's owner needs a badge
  for FLOW-adjacent states, the badge field stays — only its timed-mode
  number dies. Needs slide-owner sign-off before Rung 1 ships.
- **Ledger / hold / routines / Guard:** no contact. Ledger stays dead,
  the banked hold stays banked, routines stay his, Guard stays his open
  pick. Nothing in this plan touches any of them.

— Nabe, Improvers, hub
