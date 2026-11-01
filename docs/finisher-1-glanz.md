# FINISHER 1 — FIX-6 SKELETON GEOMETRY, VERIFIED TO THE DP

Seat: **Glanz, Finisher 1**, Tor's cell, Issue #11 (Held Door audit fixes)
For: Tor (lead). Nobody compiles. No code written by this seat.
Ground read whole: `studio/plan/AUDIT-held-door.md` (FIX-6 as ruled);
`docs/designer-skeleton.md` (Schwelle's skeleton, the document under
finish); `docs/tor-plan.md`; the builder's tree as it stands —
`app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt`,
`MonolithCustomComponents.kt` (`BlockitActionChip` :170),
`theme/Type.kt`, `theme/Color.kt`, `theme/MonolithCurves.kt`, and the
shipped font files in `app/src/main/res/font/`.

**Builder-code status, stated plainly:** as of this filing the
builder's FIX-6 code has **not landed** in this clone. The overlay is
still the pre-fix face (centre-anchored `Box`, `Arrangement.spacedBy(16.dp)`,
conditional composition at `countdownSecs == 0`, 88sp hero numeral,
42dp chips) and `git status` shows no source modifications. So this
filing verifies the *ruled heights and the designer's skeleton*
against the real content, measured — and gives the builder a stack
that can be built and asserted exactly. A conformance pass over the
builder's code remains open and is mine to run when it lands (§9).

**Method:** every width below was measured from the shipped TTFs
(glyph advances via fontTools), not estimated. Font facts used
throughout, from the files themselves:

| Family | upem | Natural line height (hhea asc−desc+gap) | Advance facts |
|---|---|---|---|
| Atkinson Hyperlegible (Reg + Bold) | 1000 | **1.24 × fontSize** (950/−290/0) | proportional |
| Sora Bold | 1000 | **1.26 × fontSize** (970/−290/0) | proportional |
| Anonymous Pro (Reg + Bold) | 2048 | **1.00 × fontSize** (1675/−373/0) | **every glyph, incl. space, = 0.5459 em** — true monospace |

Card content width (the width every centred line wraps in) =
screen − 20dp outer padding ×2 − 24dp card padding ×2 = **screen − 88dp**:
232dp @320, 272dp @360, 305dp @393, 324dp @412. Verified width range
for all line-count claims: **320–412dp at font scale 1.0**, unless a
finding says otherwise.

---

## 1. Verdict on the ruled heights (110 / 34 / 0)

| Branch | Ruled | Verdict | Corrected value |
|---|---|---|---|
| Offer (counting + armed) | 110dp | **CONFIRMED** — contents measured in §3; internal split rebalanced, total unchanged (§2, G4) | **110dp** |
| Withheld | 34dp | **REJECTED** — fails on three independent grounds (§2, G1) | **110dp** |
| Neither owed | 0dp | **CONFIRMED** — Baseline law 4; zone absent, not blanked | **0dp** |

Corrected branch set for the builder: **peek-owing (offer, counting,
held — all three withheld variants) = 110dp; never-owed = 0dp.**
Card heights follow: **418dp peek-owing / 292dp never-owed**, in
every state of a visit, from frame one.

---

## 2. Corrections (each with its evidence)

### G1 — Withheld 34dp → 110dp. The audit's number does not survive its own fix list.

The designer reached the same correction (§3.3 of the skeleton) for
one reason; measurement adds two more. Any one of the three is enough.

1. **FIX-5c resolution growth.** A COOLDOWN withheld line must
   resolve mid-visit: at derived 0 the offer re-derives and the chip
   may stand up in its place. Under a 34dp withheld zone that swap
   grows the zone 34→110 = **+76dp and RETURN moves the full 76dp** —
   F-2's defect returning through the side door. (The skeleton's
   §3.3 says "≈38dp"; that is centre-anchor arithmetic. The skeleton
   itself adopts a **top-anchored** card in its §2, under which the
   full 76dp lands on RETURN. The correction is *stronger* than the
   designer stated — see G3.)
2. **The memory line has no home at 34dp.** FIX-8's count line
   (designer §5.7) prints on held doors too, in the same Z3 slot.
   A 34dp withheld zone holds one 12.5sp line and nothing else:
   the count would need a second, branch-specific home below the
   zone — a slot that exists on held visits and not on offer
   visits, i.e. branch geometry again. At 110dp all peek-owing
   visits share one identical stack.
3. **34dp is one wrap away from clipping at real widths.** The
   longest withheld variant, `PEEK COOLDOWN — AGAIN IN 20 MIN`
   (31 chars, Atkinson 12.5sp), measures **221.4dp** — it fits the
   232dp content width of a 320dp screen by **10.6dp**, and wraps to
   2 lines at font scale 1.15 @320 and at 1.3 @360 (measured, §3).
   Two lines at the pinned 16sp line height = 32dp: inside 34dp
   with **1dp to spare per side and zero room for the slot's own
   breathing**; at any line height above 17sp it clips. Inside
   Z1's 48dp (§4) the same 2-line wrap fits with 8dp to spare at
   scale 1.0 and still fits at 1.3 (41.6dp ≤ 48dp). A gallery
   finish does not ship a slot that is 2dp from failure on a
   mainstream accessibility setting.

The alternative the designer priced — 34dp + suppress the FIX-5c
in-visit resolution — keeps a stale refusal standing after its
reason expires. Rejected for the same reason she gave.

**Tor's ratification is required**: this overturns a number printed
in the audit's FIX-6 and in `docs/tor-plan.md` item 1. Until Tor
rules, the builder builds **110 / 110 / 0** on this filing and the
designer's concurring §3.3 — two seats, one arithmetic.

### G2 — Never-owed RETURN centre: designer's 184dp is wrong. Correct: 168dp.

Skeleton §3.4: peek-owing RETURN centre 294dp from card top is
correct (arithmetic re-run in §4). For never-owed she subtracts
only the 110dp zone from 294 — but the 16dp gap after Z travels
with the branch too (her own total, 418 − 110 − 16 = 292dp, *does*
subtract it). RETURN top in the never-owed stack is
24+32+4+24+4+40+12 = **140dp**; centre = 140+28 = **168dp** from
card top, **188dp** from the top of the safe area. Any builder test
written against 184dp fails against a correct build — or worse,
passes against a stack with a phantom 16dp inserted somewhere.

### G3 — The symmetry the designer missed, which proves G2 and strengthens G1.

Distance from RETURN's centre to the **card's bottom edge** is
branch-invariant: peek-owing 418 − 294 = **124dp**; never-owed
292 − 168 = **124dp**. Bottom edge of the slab to card bottom:
**96dp** in both branches. That is what "RETURN bottom-anchored"
means on a two-height skeleton, and it only holds with 168, not
184 (184 would give 108). The tor-plan's "bottom-anchored" and the
skeleton's top-anchored card are the same geometry once card
height is fixed per branch — see the build rule in §5, item M5,
for which one is the *mechanism* and which is the *invariant*.

### G4 — Z's internal split rebalanced. Total stays 110dp; no ruled number changes.

Designer's split: Z1 48 / gap 6 / Z2 34 / gap 4 / Z3 18.
Measured problem: Z2's price is 2 lines at **every** verified
width (§3), and 2 lines at the pinned 16sp line height = 32dp
inside a 34dp slot — **1dp of breathing room per side**, the
tightest text slot on the face, on the sentence FIX-8 calls the
price he must be able to read. Meanwhile Z3's memory line
(Anonymous Pro Bold 10sp, line height 12sp per `PillBadgeTextStyle`)
sits in 18dp — 3dp per side — while the footer badge in the same
stack holds the *identical* style in 16dp. And the 6dp gap is the
only vertical gap on the face that is not a multiple of 4.

**Finisher's split (sum identical, 110dp):**

| Sub-slot | Designer | **Glanz (build this)** | Reason |
|---|---|---|---|
| Z1 ACTION | 48 | **48** | chip height is law (F-4); gate stack and withheld both sized against it (§4) |
| gap | 6 | **4** | joins the face's 4dp gap family |
| Z2 PRICE | 34 | **38** | 2 × 16sp lines = 32dp, now 3dp breathing per side |
| gap | 4 | **4** | unchanged |
| Z3 MEMORY | 18 | **16** | identical construction to footer slot F (16dp / 12sp line height) directly below it in the same column |
| **Total** | **110** | **110** | card heights 418 / 292 unchanged |

### G5 — Withheld alignment contradiction in the skeleton: ruled CENTRED.

Skeleton §3.3 says the withheld content "sits top-aligned in Z1";
its §5.5 says "centred in Z1". Both cannot be built. **Ruled:
vertically centred in Z1's 48dp.** The withheld line crossfades
with the chip on the FIX-5c resolution path (§5.5 of the skeleton);
the chip's label is centred in the same 48dp, so the eye's anchor
(the centre of Z1) is identical before and after the swap.
Top-alignment would drop the resolved chip's label 14dp below the
line it replaced — a 14dp visual jump inside Z1 on the exact frame
the skeleton exists to make still.

### G6 — The draining rule's placement, as specced, is unbuildable. Exact geometry here.

Skeleton §5.2 places the rule "6dp below the text baseline box" —
baseline-relative placement is not a number a builder can assert,
and a rule laid out by a `Column` under an unpinned text will move
whenever the text's metrics do. The gate stack is re-specced as
fixed boxes inside Z1 (48dp), symmetric top/bottom:

| Element | Offset from Z1 top | Height |
|---|---|---|
| top margin | 0 | 10dp |
| gate text box (`PEEK OPENS IN {M:SS}`, Anonymous Pro Bold 15sp, line height 18sp, maxLines 1, centred) | 10dp | 22dp |
| gap | 32dp | 4dp |
| **draining rule** | **36dp** | **2dp** |
| bottom margin | 38dp | 10dp |

- **Rule top edge = Z1 top + 36dp, in every state, at every font
  scale** — because every element above it is a fixed-height box,
  not a measured text. This is the assertable form of the designer's
  intent (her stack total, 20+6+2 centred, lands the rule within
  2dp of this; the 2dp is the price of making it exact).
- Rule width: full content width (screen − 88dp), track
  `NightColorPrimary` @ α0.20 across the whole width, always
  composed. Fill `NightColorPrimaryContainer` #F47442, **left edge
  fixed at content left**, width = derived fraction × content
  width, draining right-to-left. **Square corners (0dp radius) on
  track and fill** — a rounded fill morphs its cap geometry as the
  fraction shrinks; a square fill is linear to the last pixel.
- **No tween/animation on the fraction.** The fraction at every
  frame equals `wallRemaining / wallTotal` from the anchored
  derivation (skeleton §5.8). With the 1s redraw ticker it steps
  once per derived second — a detent, not a lag. An
  `animateFloatAsState` on this value would print a fraction the
  machinery does not currently hold, which on this face is a lie
  with good easing.

---

## 3. Measured content vs the reserved slots

All at font scale 1.0, Atkinson 12.5sp unless stated. "Lines" =
greedy word-wrap at the stated content width, from measured glyph
advances.

| Print | Measured width (unwrapped) | @232 (320) | @272 (360) | @305 (393) | @324 (412) | Slot verdict |
|---|---|---|---|---|---|---|
| PRICE, new wording: `WHEN THE PEEK ENDS, A 20 MINUTES COOLDOWN STARTS ON THIS APP` (60 chars) | 419.5dp | 2 lines | 2 | 2 | 2 | Z2 must hold 2 lines — 38dp per G4 ✓ (designer's "wraps to 2 lines" confirmed at all widths) |
| PRICE, `1 MINUTE` variant (58 chars) | 402.1dp | 2 | 2 | 2 | 2 | same ✓ |
| WITHHELD cooldown, worst case `…AGAIN IN 20 MIN` (31 chars) | 221.4dp | 1 (10.6dp spare) | 1 | 1 | 1 | 1 line in Z1 @1.0; 2-line wrap absorbed by 48dp at larger scales (G1) |
| WITHHELD `A PEEK IS ALREADY RUNNING` | 173.3dp | 1 | 1 | 1 | 1 | ✓ |
| WITHHELD `PEEK IS OFF` | 71.9dp | 1 | 1 | 1 | 1 | ✓ |
| SESSION `SESSION ENDS 20:01` | 121.5dp | 1 | 1 | 1 | 1 | H3 line box 20dp ✓ |
| SESSION `SESSION RUNNING · NO SET END` | 192.3dp | 1 | 1 | 1 | 1 | ✓ |
| BREAK kernel `BREAK AT 20:01 OPENS THIS APP` | 192.0dp | 1 | 1 | 1 | 1 | H3 line 2 ✓ |
| SITE domain, worst realistic `a-very-long-subdomain.example.co.uk` | 207.0dp | 1 | 1 | 1 | 1 | H3 line 2 ✓ at 1.0; maxLines/ellipsis still mandatory (§4) — longer domains exist |
| GATE `PEEK OPENS IN 0:05` / `0:10` / `1:00` (Anon Bold 15sp) | **147.4dp, identical for all three** | 1 | 1 | 1 | 1 | **zero jitter, proven from the font file**: Anonymous Pro's advance is 0.5459em for every glyph including space and colon, and every reachable wall value (taper cap 10s) prints in the same 18 characters. The gate line's centre cannot move by a fraction of a dp across the whole countdown. Schliff's A-4 holds by construction. |
| MEMORY, worst plausible ordinal `TWENTY-FIRST TIME THIS SESSION` (Anon Bold 10sp, letterSpacing +2.0) | 221.8dp | 1 (10.2dp spare) | 1 | 1 | 1 | Z3 16dp ✓ through the 21st return of a session; beyond that, maxLines 1 + ellipsis (§4) — visible truncation, never a wrap into a 16dp slot |
| EXIT `END THE SESSION INSTEAD` (Atkinson Bold 12.5sp) | 168.9dp | 1 | 1 | 1 | 1 | X slot ✓ |
| RETURN label `RETURN TO FOCUS` (Sora Bold 15sp, +0.5 tracking) | 155.4dp | 1 | 1 | 1 | 1 | 56dp slab ✓ |
| NAME, longest realistic `WhatsApp Business` (Sora Bold 24sp) | 247.7dp | fits 232? **No — ellipsises** | 1 | 1 | 1 | H1 maxLines 1 + ellipsis (F-9) is load-bearing at 320dp, not decorative |

**Corroboration of the original defect's size:** re-measuring the
pre-fix growth from the current code — chip 42 + spacedBy gap 16 +
price 2 lines ≈31 + spacedBy gap 16 ≈ **105dp**, against Schliff's
filed ≈110dp and the audit's "RETURN jumped ≈55dp" (half of the
growth, centre-anchored). Within measurement tolerance of both.
The defect was real and sized honestly; the skeleton's growth is 0.

**Where the slots honestly fail (immobility holds; legibility is
the casualty — Tor to note, designer §11.8 already flags the class):**

- Font scale 1.15 @320dp: price wraps to **3 lines** (measured);
  Z2 holds 2 → third line clipped.
- Font scale 1.3 @320 and @360: price wraps to **3 lines** → clipped
  to 2. @393 it stays 2 lines but the 2-line block (41.6dp at the
  scaled line height) exceeds Z2's 38dp → glyph clipping top/bottom.
- Below 320dp width at scale 1.0 (280/300dp fold cover screens):
  price wraps to **3 lines** at scale 1.0 → clipped.
- In every case above, **no slot, sibling, or RETURN moves by any
  amount** — the fixed boxes clip their own contents (§5, M1).
  The skeleton law outranks the price's completeness at
  accessibility scales; growing the card to rescue it would
  re-open F-2 for every user to save a sentence for some. I record
  it as a known casualty, not a licence to grow anything.

---

## 4. The final stack — every value, builder-ready

Mechanism for every row: a fixed-height `Box`/`Modifier.height()`,
`clipToBounds`, content vertically centred, text `textAlign =
Center` and full content width unless stated. Line-height law for
the whole face: **lineHeight = smallest whole sp ≥ the family's
natural line height at that size** (Atkinson 12.5→16, 16→20;
Sora 15→20, 24→31; Anonymous Pro 10→12, 15→18), so no text block
ever exceeds its slot at scale 1.0, and every slot carries ≥2dp
of total slack around its text block — H1 is the sole exception
at 1dp (Sora's natural 30.24dp in a 32dp slot; do not set H1's
lineHeight above 31sp).

| # | Element | Height | Gap after | Content spec |
|---|---|---|---|---|
| — | outer padding (all sides, after safe-area insets — §5 M6) | 20dp | — | card top edge = safe-area top + 20dp |
| — | card top padding | 24dp | — | shell: clip 24dp, `NightColorSurfaceBright` #362C27, border 1dp `NightColorPrimary` @ α**0.38** (draws inside bounds — no layout effect) |
| H1 | NAME | **32dp** | 4dp | Sora Bold 24sp, lineHeight 31sp, cream #FFE4CF, maxLines 1, ellipsis |
| H2 | INTENTION | **24dp** | 4dp | Atkinson Regular 16sp, lineHeight 20sp, α0.92, maxLines 1, ellipsis; blank → composed empty at 24dp |
| H3 | SESSION BLOCK | **40dp** | 12dp | two line boxes of **20dp each, 0dp gap between**; each Atkinson Regular 12.5sp, lineHeight 16sp, α0.92, maxLines 1, ellipsis (ellipsis added for line 2's domain — the skeleton specced it only for H1/H2; a fact may truncate visibly, it may not wrap the block to 60dp); either line empty-held independently |
| Z | PEEK ZONE (peek-owing branches only) | **110dp** | 16dp | fixed `Box`, clipToBounds; internal split directly below |
| Z1 | ACTION | **48dp** | 4dp | tenant by state (§5): gate stack (geometry per G6) / PEEK chip 48dp / withheld text box full 48dp, centred, Atkinson Regular 12.5sp, lineHeight 16sp, α0.92, **maxLines 2**, no ellipsis (a refusal prints whole or not at all) |
| Z2 | PRICE | **38dp** | 4dp | Atkinson Regular 12.5sp, lineHeight 16sp, α0.92, maxLines 2, overflow Clip; offer branch prints from frame one; held branch empty-held |
| Z3 | MEMORY | **16dp** | — (Z ends) | Anonymous Pro Bold 10sp, lineHeight 12sp, letterSpacing +2.0, cream @ α0.92, maxLines 1, ellipsis; count 0 → empty-held |
| R | RETURN slab | **56dp** | 12dp | fill `NightColorPrimaryContainer` #F47442, corner 12dp; label `RETURN TO FOCUS`, Sora Bold 15sp, lineHeight 20sp, letterSpacing +0.5, ink `NightColorOnPrimaryContainer` **#471602** |
| X | HONEST EXIT | **36dp** | 8dp | full slot is the tap target; `END THE SESSION INSTEAD`, Atkinson Bold 12.5sp, lineHeight 16sp, α0.92, maxLines 1 |
| F | FOOTER BADGE | **16dp** | — | Anonymous Pro Bold 10sp, lineHeight 12sp, letterSpacing +2.0, `NightTextColorSecondary` #9C8E84, maxLines 1 |
| — | card bottom padding | 24dp | — | |

**Totals (assert these, §6):** peek-owing card **418dp**,
RETURN top **266dp** / centre **294dp** from card top (314dp from
safe-area top); never-owed card **292dp**, RETURN top **140dp** /
centre **168dp** from card top (188dp from safe-area top);
**RETURN centre 124dp and slab bottom edge 96dp from card bottom
in both branches** (G3). PEEK chip: 48dp, corner 12dp, fill
`NightColorSecondaryContainer` #4A423C, border 1dp `NightColorPrimary`
@ α0.38, label `PEEK` Sora Bold 15sp + derived badge Anonymous Pro
Bold 15sp, one space between, lineHeight 20sp.

**Fades (the only motion inside the skeleton):** overlay entry
`fadeIn(specA(180))`; Z1 tenant swaps (gate→chip at wall expiry,
withheld→chip on FIX-5c resolution) crossfade **180ms specA**,
opacity over identical bounds; Z2 price fade-in 180ms specA on the
resolution path only (it is empty-held in the held branch);
overlay exit `fadeOut(specA(260))`. **Everything else updates
hard**: gate figures, cooldown digits, and the rule fraction swap
per derived second with no fade and no tween — a 180ms fade on a
1s tick would hold the gate line at partial alpha for a fifth of
its life, and a faded digit is a misread digit waiting to happen.

---

## 5. Movement register — every remaining way anything could move by 1dp, and its kill

- **M1 — Text outgrowing its slot (font scale, long strings, wrap).**
  Kill: every slot above is a fixed-height box with `clipToBounds`;
  layout height comes from the box, never from measured text. Text
  may clip *inside* its own box (§3 casualties); it cannot push a
  sibling. A slot implemented as bare `Text` in the column, however
  carefully styled, fails this filing.
- **M2 — `Arrangement.spacedBy` anywhere in the card column or
  inside H3/Z.** It collapses around absent children — it is the
  original defect's mechanism (pre-fix code: `spacedBy(16.dp)`).
  All spacing is the explicit gaps in §4. Zero occurrences allowed
  in this composable's card.
- **M3 — Conditional composition.** The pre-fix `?.let` session
  line and `if (isNotBlank)` intention are the pattern. Every §4
  slot composes in every state of its branch — empty-held where it
  has nothing to print. The only branch is Z itself (110 vs 0),
  decided once at frame one from state that cannot change branch
  mid-visit, with the FIX-5c tenant swap contained *inside* Z1.
- **M4 — `AnimatedVisibility` inside Z.** Its default enter/exit
  also animates size. Tenant swaps are opacity crossfades over
  fixed bounds (manual alpha or `Crossfade` inside the fixed Z1
  box) — never `AnimatedVisibility` below the overlay root.
- **M5 — Bottom-anchoring implemented as a mechanism.** A
  `Spacer(Modifier.weight(1f))` above RETURN, or
  `Arrangement.Bottom`, makes RETURN's position a *consequence* of
  total height: any future slot error is absorbed by moving
  everything above it, including the name. The mechanism is the
  **top-down fixed stack of §4**; the bottom distances (124dp /
  96dp, G3) are **asserted invariants**, not the thing that
  produces the position. Small-height viewports confirm the
  choice: top-anchored, RETURN's slab is fully on screen down to
  a 342dp-tall viewport (314 + 28); bottom-anchoring would push
  H1 off the top of the same screen instead.
- **M6 — Safe-area insets.** The absolute addresses (314 / 188dp)
  hold only if the 20dp outer padding is applied **after**
  safe-drawing insets (`windowInsetsPadding(WindowInsets.safeDrawing)`
  or the activity's equivalent). Without it, RETURN's absolute
  position varies by status-bar height across devices — not across
  states, but the skeleton's published address would be fiction on
  any device whose inset differs from the test device's. The
  card-relative addresses (294 / 168 / 124) are inset-independent
  and are the ones §6 asserts.
- **M7 — Reusing `BlockitActionChip` for RETURN/PEEK.** It
  hardcodes `height(42.dp)`, 12sp labels, and (accent branch) cream
  ink #FBF1EC — three violations in one reuse (56/48dp, 15sp,
  #471602). Do not rely on a caller-side `Modifier.height()` to
  rescue it; the door's slabs are door-specific composables (or a
  parameterised chip), and §6 asserts their measured node heights.
- **M8 — Digit-count changes in centred prints.** Cooldown
  `20 MIN` → `9 MIN` shortens the withheld line by ≈7.5dp; taper
  and badge values are per-visit constants. Kill: every such print
  is full-content-width with `textAlign = Center` inside its fixed
  box — the centre is the box's centre, so digits may come and go
  and the centre does not move. A `wrapContentWidth` centred row
  would pass the eye test and fail this: its box would breathe.
  (Gate line is exempt by proof, §3: its width is constant.)
- **M9 — The rule's width rounding.** Fraction × content width
  rounds to whole pixels (≈0.25–0.5dp at common densities). That is
  *inside* Z1, is the drain working as designed, and moves no
  sibling and no edge that anything else is anchored to. Recorded
  so nobody "fixes" it with an animation (G6).

Nothing else on this face can move. If the builder finds a tenth
mover, it files to Tor before it ships around it.

---

## 6. Assertions the builder encodes (extending skeleton §11)

1. RETURN node bounds (`airlock_return_button`) identical across
   COUNTING (wall = cap, wall = 1), OFFER, and HELD-COOLDOWN —
   **and** its height measures **56dp** (not 42 — M7), PEEK node
   height **48dp** in OFFER.
2. RETURN centre-to-card-bottom = **124dp** in a peek-owing visit
   and in a never-owed visit (G3's cross-branch invariant).
3. Card content height: peek-owing **418dp** stack / never-owed
   **292dp**; Z present at **110dp from the first frame** in
   COUNTING; Z absent (not 0-height-with-gaps: gap after Z is also
   0) in never-owed.
4. Gate rule: its node's top = Z1 top + **36dp**, height **2dp**,
   in COUNTING at wall = cap and wall = 1 (G6).
5. At font scale **1.3**, assertions 1–3 still pass exactly
   (contents may clip; geometry may not move — M1).
6. Price composed in COUNTING before the chip exists; withheld
   line composed at first frame in HELD; `AGAIN IN 0 MIN` never
   composes (skeleton §11.5/11.6, restated because Z's geometry
   is what makes them free).

---

## 7. What I did not touch

Contrast values, wording, the taper's arithmetic, state additions
(skeleton §10), and the Disarm face's 22-vs-24dp padding (F-14
rider) are other seats' filings; I cite their numbers only where
geometry depends on them. The footer-on-peek-tier question
(skeleton §12.4) and the `THIS SESSION` wording (§12.2) remain
Tor's — neither moves a single dp under any ruling, because both
tenants are fixed-slot, maxLines 1, and measured to fit (§3).

## 8. Open for Tor

1. **Ratify G1** (withheld 110dp) against the audit's printed 34dp
   — the one ruling in this filing that overturns a number above
   my seat.
2. Note the §3 font-scale casualties (price clipping at 1.15 @320,
   1.3 @≤360, and below 320dp width) as accepted, or commission a
   separate accessibility-scale treatment — as a new design, never
   as growth in this skeleton.

— **Glanz**, Finisher 1, Tor's cell, Issue #11. Paper only;
nothing compiled, nothing built. Every number above is either
ruled, measured from the shipped fonts, or flagged as my
correction with its evidence attached.

## 9. Next / unassigned

- **What I want to do next:** the conformance pass over the
  builder's FIX-6 code the moment it lands in this clone — §4's
  table and §6's assertions checked line by line against
  `AirlockAndDisarmOverlays.kt`, with any deviation filed as a
  numbered finding to Tor before the cell closes.
- **What I could do unassigned:** run this same reserved-height
  verification over the Disarm face (it shares the card shell and
  has the F-14 padding discrepancy already filed against it), and
  carry the §3 measurement method — real font files, real strings,
  real widths — to the other faces' fixed slots (the pill and the
  Deck's sentence slot are the two whose reserved heights are
  currently asserted rather than measured).
