# ISSUE #6 — FINISHER-2 SPEC: F12 · F14 · F17 (gallery standard)
## Finisher seat 2, Rost's cell · Fri Oct 30, 2026
## Status: DECLARED, paper only. Nothing compiled. Nothing committed.

My cluster, and only my cluster: **F12** (utilities unlit at
night), **F14** (group-by-silence spacing + scroll bottom-edge
fade), **F17** (tokenise the light). Sources: `AUDIT-deck.md`
§6 (Schein's finish findings) + §9 (F12/F14/F17), the Innovator's
F12 flag (`ISSUE-6-innovator.md`, Wrong-Target Flags — adopted
whole in §1), Designer D6/D12/D13 (`ISSUE-6-designer.md`).

Tree as read this shift, grep-verified, branch `agent/issue-6`
(F1's cut landed in this tree — Deck is the post-cut file):
`TactileDeckScreen.kt` **1,125 lines** · `MonolithCustomComponents.kt`
**897** · `BlockitCoreViews.kt` **2,416** · theme `Color.kt` **209** ·
`Theme.kt` **124**. **Every line number below is this shift's grep;
where the builder map's numbers differ, mine govern.** Luminance
figures are WCAG relative luminance, computed from the token
values in `Color.kt` — the method is stated in §1.1 so any seat
can re-derive every number in this file.

---

## §0 · CODE vs CLAIMS — READ THIS FIRST

The audit's three items are true in substance and wrong or
incomplete in five particulars. A builder working from the
claims alone ships F12 wrong and F17 half-done.

**0.1 — F12's hex is the least of F12's problems: the utilities
row has no single fill to step down.** Section 3
(`TactileDeckScreen.kt:878–916`) composes two chips with two
different bodies:
- `DESK CLOCK` (L883–889) is `isPrimaryInk = true` → night fill
  `inkControlFill()` = `#4A423C` (`MonolithCustomComponents.kt:315`),
  ratio **1.86:1 over ground** — brighter than the hero card
  `#362C27` at **1.35:1**. This is Schein's convicted mass.
- `HELD` (L890–895) is `isPrimaryInk = false` → night fill
  `scheme.surfaceVariant` = `#100E0C`, the *recess* — luminance
  **below ground** (L 0.0045 vs ground 0.0073). It is not too
  bright; it is a hole where a control should be.
So the row today is one floodlight and one pit, side by side.
"Step the fill down" cures one chip and leaves the other below
the ground the staircase says controls must stand on. F12's
real job is to give the row **one control rank** (§2).

**0.2 — `inkControlFill()` is app-wide; F12 must not touch it.**
The same resolver fills chips and selected states in
`ParkDrawer.kt:431`, `ShieldCapSelector.kt:136`,
`FocusSequencerScreen.kt:275/835`, `ShieldMatrixScreen.kt:713`,
the Deck's own FLOW cap (Deck L696–697), progress fill (L740)
and the pinned GRACE/VALVE chips (L1008/1023), and its Canvas
twin `tokControlFill()` is the slide accent's terminal phase
(`BlockitCoreViews.kt:151`). Stepping `inkControlFill()` down
"for F12" silently re-lights four other screens and the slide.
F12 therefore gets a **Deck-local resolver** (§2.2); the shared
fill is out of this cluster's reach, by law.

**0.3 — The builder map undercounts F17 by an order of
magnitude.** Its F17 entry names "5 raw-hex sites" in components
and a handful in the Deck. The real census (§4, every line):
**71 colour-literal occurrences on 67 lines** across the three
F17 files — Deck 6, Components 6, `BlockitCoreViews.kt` 59 —
plus **12 more hexes in comments** in the views file. The bulk
is not stray sites: it is a **complete parallel palette** —
`BlockitCoreViews.kt:64–103` re-declares all 38 scheme values
as `LIGHT_*`/`NIGHT_*` int constants, a second source of truth
living one directory away from `Color.kt`.

**0.4 — The parallel palette has already drifted.** 37 of its
38 values match `Color.kt` exactly. One does not:
`LIGHT_ON_SURFACE_VARIANT` (views L76) = `0xFF787170`, while
`Color.kt`'s `TokenColorOnSurfaceVariant` = `0xFF5F5856`. The
views file knows — its own comment at L774–775 discusses the
two spellings of this tier and keeps both. This is F17's whole
charge in miniature, already happened. Resolution is a Rost
decision, flagged as **D-F17-1** in §5.3; this spec does not
pick a winner silently.

**0.5 — F14's fade already exists — one floor down, where it
does nothing for the Deck.** The host carries the studio's seam
fade at `MainActivity.kt:652–669`: a 24dp `Transparent →
scheme.background` gradient above the rail. But Deck content
never reaches the rail — the scroll viewport's bottom edge is
the **pinned stack's top edge** (Deck L928). Content is
guillotined there, inside the Deck, where the host fade cannot
see it. F14's fade is a Deck-internal twin of the host's, same
24dp, same gradient, different seam (§3.3). A builder who
"finds the fade already done" has found the wrong seam.

---

## §1 · THE LAW — THE LAMP TEST STAIRCASE, RULED AS RATIOS

Adopting the Innovator's flag as this cluster's constitution:
**F12 prescribing `#2A2320` cures the disease with the disease.**
A luminance *relationship* frozen into one value drifts the
first time a look re-tones the card. What is ruled here is the
staircase itself — bands any scheme's tokens must satisfy,
checked mechanically. One scheme's current values appear only
as worked examples proving the bands are attainable.

### 1.1 · Method (so the test is a test, not a vibe)

- For any colour, `L` = WCAG relative luminance.
  `CR(a,b) = (L_hi + 0.05) / (L_lo + 0.05)`.
  `R(x) = CR(x, ground)` — every mass is anchored to its own
  frame's ground.
- A **mass** is a filled area ≥ 24dp in both dimensions (cards,
  chips, drawer cells, the slide track). Masses obey the
  staircase. **Print** (text, numerals, ticks, markers, hairlines,
  dots < 24dp) obeys the print tiers in §1.3 — a 1dp marker line
  in cream is not a floodlight and must not be measured as one.
- Alpha colours are measured **composited** over what they
  actually sit on, never as raw literals.
- The three frames are rendered to pure luminance (Schein's
  Lamp Test, endorsed by the audit §8 and Designer D13) and the
  named regions in §1.4 are sampled. No eyeballing: each frame
  passes or fails on the sampled order + bands.

### 1.2 · The night staircase (masses) — the ruled bands

Order: **glyph > card > controls > ground**, strict.

| Rank | Band (R vs night ground) | Current instance | R today |
|---|---|---|---|
| Glyph ink (hero/hub numeral) | **12.0 – 16.0** | `#EDE3D8` (`onSurface`) | 14.48 ✓ |
| Card (the one raised surface) | **1.28 – 1.45** | `#362C27` (`surfaceBright`) | 1.35 ✓ |
| **Controls (chips, key-caps)** | **1.10 – 1.24** | audit's `#2A2320` is one instance | 1.19 ✓ |
| Ground | 1.00 by definition | `#171412` (`background`) | 1.00 |
| Recess (sockets, insets) | L **below** ground, CR 1.02 – 1.10 | `#100E0C` (`surfaceVariant`) | 1.05 ✓ |

Two further constraints, both load-bearing:
- **Separation floor:** adjacent mass ranks differ by
  CR ≥ **1.08**. Below that, two tones merge at arm's length
  and the staircase is a rumour. (The instance above gives
  card/control = 1.35/1.19 = **1.14** ✓, control/ground =
  **1.19** ✓.)
- **The sheet is not a control tone.** `#4A423C` (R **1.86**,
  `surfaceSheet` / night `secondaryContainer` / today's
  `inkControlFill`) sits *above* the card band. At night it is
  licensed for exactly two things: the hub disc the staircase
  already assigns it, and **fault masses** (§1.4, frame 3).
  Any other night mass at sheet luminance fails the frame,
  whatever token it arrived by. This sentence, not any hex,
  is F12.

### 1.3 · Print tiers (night, on ground unless stated)

Monotonic, and separately from masses — print may outrank
masses (the glyph is print, and it is rank 1):

| Tier | Band (R vs ground) | Instance | R |
|---|---|---|---|
| Primary text | 9.0 – 12.5 | `#D2C6BB` | 10.95 ✓ |
| Secondary text | 4.5 – 7.0 | `#9C8E84` | 5.78 ✓ |
| Muted print | 2.8 – 4.2 | `#776A60` | 3.51 ✓ |
| Label ON a control fill | CR ≥ 7.0 vs the fill | cream `#FFE4CF` on any in-band control fill | 12.1 – 13.7 ✓ |
| Secondary badge ON a control fill | CR ≥ 4.5 vs the fill | `#9C8E84` on the §1.2 instance | 4.86 ✓ |

### 1.4 · The three frames — regions, expected order, verdict today

**FRAME 1 — IDLE.** Regions: hero glyph · hero card fill ·
FLOW/DONE key-cap fill · ground margin.
- *Night idle:* glyph 14.48 > card 1.35 > caps **FAIL in both
  cap states** — inactive caps sit at recess (below ground,
  §0.1's disease in a second room) and the active FLOW cap
  sits at sheet 1.86 (above card). The caps are F2/F7/F13's
  bodies, not mine; §5.4 files the zero-price extension of
  the control token to them for Rost's ruling.
- *Day idle:* **PASS, acquitted, no change.** Day polarity is
  contrast-distance, not luminance ascent: glyph ink
  `#2C2A29` CR 10.79 vs ground · card `#FAF6F0` CR 1.23 on the
  light side · recess `#D8D1CB` CR 1.14 on the dark side ·
  ground `#E4DFDA`. Day bands, for any future look: ink/print
  CR ≥ 8 vs ground; card CR 1.15 – 1.35 light-side; recess
  CR 1.08 – 1.20 dark-side. F12 is a night charge; the day
  frame is recorded so no lane "fixes" it.

**FRAME 2 — SEALED (running, night forced).** Regions:
readout glyph · hero card fill · DESK CLOCK fill · HELD fill ·
END BREAK fill (break frames) · ground.
- Today: **FAIL** — DESK CLOCK at 1.86 outranks the card at
  1.35; HELD at recess is below ground (§0.1). This is the
  frame Schein convicted (audit §6).
- After §2 lands: glyph 14.48 > card 1.35 > utilities at
  control band 1.10 – 1.24 > ground 1.00 — **PASS**, with one
  named exception pending: the STOP chair (pinned zone) still
  takes the sheet fill via `isPrimaryInk`. The chair is F6's
  surface and §0.2 forbids me the shared resolver; whether
  the chair's resting fill adopts the control band or stands
  as the sealed frame's licensed second exception (it is the
  session's one live verb) is **Rost's call, flagged in §5.4**
  — this spec does not pass the frame by hiding it.

**FRAME 3 — FAULT.** The deliberate inversion (Designer D13):
fault outranks the instrument, by design, and the frame is
checked for exactly that shape. Regions: refusal-sentence
ink · drawer/FAULT-cell mass · hero glyph · card · utilities ·
ground. Ruled order: **fault print = glyph band (12 – 16) ·
fault mass R 1.70 – 2.00** (today's sheet at 1.86 ✓ — the one
place sheet luminance is the *point*) **> glyph > card >
controls (still 1.10 – 1.24 — a fault must never lift the
utilities with it) > ground.** A fault frame in which the
chips brighten, or the fault mass sinks to card rank, fails.

### 1.5 · What the Lamp Test is not

It does not price hue, the one licensed accent (slide cap at
rest, `primaryContainer` — an exception by hue and singularity,
already law, untouched here), or the seal shot's transient
frames (the shot is motion; the test samples rest states:
idle, settled-sealed, standing fault).

---

## §2 · F12 — UNLIGHT THE UTILITIES (S, as priced)

### 2.1 · The ruling in one paragraph

At night, every Section-3 utility body — `DESK CLOCK`, `HELD`,
and `END BREAK` — takes **one fill: the Deck control rank of
§1.2** (R 1.10 – 1.24 vs ground, ≥ 1.08 under the card). Labels
keep the cream label ink (§1.3, CR ≥ 12 on any in-band fill);
HELD's count badge keeps secondary ink (§1.3, CR ≥ 4.5).
Nothing else about the chips changes: 42dp height, 12dp radius
(Components L201 — already registered), gating, words, wiring.
Day fills are byte-identical to today.

### 2.2 · The mechanism — a Deck-local resolver, a chip tone

1. `Color.kt` gains the control-step token pair (§5.1):
   day realises the step as ink (day polarity), night as a
   new token whose value must satisfy §1.2's control band.
   The audit's `#2A2320` (R 1.19) is the filed first instance
   **as a token definition in `Color.kt`** — lawful there;
   the law that binds future looks is the band, not the value.
2. `MonolithCustomComponents.kt`, beside `inkControlFill()`
   (L311–322), gains:
   `deckUtilityFill()` = day → `inkControlFill()`;
   night → the night control-step token.
   `inkControlFill()` itself is **not edited in value** (§0.2).
3. `BlockitActionChip` (Components L168–226) gains one
   parameter, defaulted so every existing caller is untouched:
   `isQuietUtility: Boolean = false`; its fill `when` gains,
   above `isPrimaryInk`: `isQuietUtility -> deckUtilityFill()`.
   Foreground for the quiet tone = `onInkControlFill()`
   (cream at night; day ink's label by day).
4. Deck Section 3 passes the tone at exactly three call sites:
   `DESK CLOCK` (L883 — replaces `isPrimaryInk = true`),
   `HELD` (L890 — gains the tone; its fill *rises* from recess
   to control rank, §0.1), `END BREAK` (L908 — replaces
   `isPrimaryInk = true`).

### 2.3 · END BREAK, decided, not ducked

END BREAK is "the Deck's one forward verb" during a break
(Deck L897–906 comment) — the one utility a man may *want*
loud. It still takes the control rank. Its prominence in the
break frame comes from address and area (full width, alone in
its row, under a hub counting down at glyph rank), not from
outshining the instrument; a verb that must out-brighten the
break countdown to be found is a labelling failure, and its
label is already the plainest on the screen. If Rost wants
the break's verb excepted, the exception is one parameter
flip at L908 — priced here so the decision is visible, not
smuggled.

### 2.4 · Explicitly NOT F12 (named so no builder "helps")

The pinned GRACE/VALVE chips (Deck L1008/1023 — F6's chair),
the FLOW cap (L696 — F2 kills it; its successor print is the
Designer's D2), DONE (L802 area — F7/F13), the progress fill
(L740 — F3/F18), the strip's FAULT cap (Components
`stripToneColors`, L555–563 — frame-3 licensed, §1.4), the
wheel reader dot and hub disc (sheet by the staircase's own
assignment, `BlockitCoreViews.kt` passim). Each keeps its
fill; F17 (§4) changes only their colours' *addresses*.

### 2.5 · Acceptance

Frame 2 sampled per §1.4 passes with all three utilities in
band; frame 1 night shows the utilities absent (gating
unchanged — they are settled-running furniture); a grep shows
no Section-3 call site passing `isPrimaryInk = true` any
longer; day screenshot diff = zero.

---

## §3 · F14 — GROUP BY SILENCE + THE SCROLL'S BOTTOM FADE (S)

### 3.1 · The spacing census — what the Deck actually charges today

Scroll column (Deck L532–541): outer padding **12h / 8v**,
`spacedBy(12.dp)` between **every** top-level object — the
instrument, the task line and the utilities all stand the
same 12dp apart; nothing groups, nothing separates. Inside
objects: instrument card internal `spacedBy(10)` (L579) over
padding 16h/18v (L578); task column internal `spacedBy(6)`
(L762), its rows `spacedBy(10)` (L766), sub-step rows 3dp
vertical padding (L836 area); utilities row internal
`spacedBy(12)` between the two chips (L881), with `END BREAK`
composed directly under the row — inheriting whatever the
outer column charges. Pinned stack (L928–943, L1036): padding
12h/8v, strip→slide `Spacer(6)`, slide→sentence `Spacer(8)`.

### 3.2 · The re-price — every gap, old → new

Rule (Designer D12, priced concretely): **silence groups —
gaps inside an object ≤ 12dp, gaps between objects = 20dp,
and no rule, box or hairline is added to say what the silence
already says.**

| Gap | Today | Ruled | Why |
|---|---|---|---|
| Scroll column, object ↔ object | 12 (L541) | **20** | the between-object price |
| Instrument card internals | 10 (L579) | 10, stands | already inside-price; no internal gap in the card may exceed 10 |
| Card padding | 16h / 18v (L578) | stands | the card's own room, not a gap |
| Task column internals | 6 / rows 10 (L762/766) | stands | inside one object |
| Sub-step rows | 3v padding | stands | inside the task object |
| Utilities row, chip ↔ chip | 12 (L881) | 12, stands | inside the utilities object |
| Utilities row ↔ END BREAK | inherits outer 12 → would inherit 20 | **12** | same room (Deck's own ST-02 seating) — see the wrap below |
| Pinned stack internals | 6 / 8 (L943/1036) | stands, named exception | the pinned machine groups by Δy = 0 adjacency law, not by scroll silence; F16 re-seats it wholesale |

Implementation shape, exact: the scroll column's
`spacedBy(12.dp)` (L541) becomes `spacedBy(20.dp)`; Section 3's
bare `Row` + conditional chip (L878–916) is wrapped in a
`Column(verticalArrangement = Arrangement.spacedBy(12.dp))`
so the utilities room keeps its inside-price under the new
outer price. No other spacing literal in the file changes.
Post-F16/F18 the same table governs the Fusion room: hub ↔
ghost line ↔ utilities are objects at 20; nothing inside the
wheel's own composition is this item's business.

Acceptance: from layout inspection, no two top-level scroll
objects stand closer than 20dp; no gap inside any object
exceeds 12dp; the idle Deck still fits whole at 740dp
(Designer's §2 test — the +8dp × 2 object gaps are paid by
F16's returned ~120dp, and on the current room by the scroll
itself; if a render shows the task line pushed below the
fold at idle on the *current* room, that is F16's absence,
not an F14 defect — recorded so the frames are not confused).

### 3.3 · The bottom-edge fade — the Deck's own seam

Form, adopting the studio seam standard whole (F-04 /
CHROME C9 §10.1, already built at host level, §0.5):
- A **24dp** gradient, `Brush.verticalGradient(Color.Transparent
  → scheme.background)` — scheme token + the named constant
  `Color.Transparent`; **no hex is born in this item** (F17's
  gate, §5.5, includes the fade).
- Placement: the scroll column (Deck L532) is wrapped in a
  `Box(Modifier.weight(1f))`; the fade is a child `Box`,
  `Alignment.BottomCenter`, `fillMaxWidth`, `height(24.dp)`,
  drawn **above** the scroll content and **below nothing** —
  it belongs to the viewport, not the content: it does not
  scroll, does not move during the seal shot (ground family;
  F15's ground recession carries it as ground), carries no
  content, takes no touch (no clickable, no semantics).
- Content clearance: the scroll column's bottom padding goes
  8 → **32dp** (24 fade + 8 air, mirroring the host's
  arithmetic at its own seam), via
  `padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 32.dp)`
  replacing L540's symmetric form — so the last object can
  scroll fully clear of the fade and rest uncut.
- It is **not** a second host fade and must not merge with
  `MainActivity.kt:652–669`: that one dissolves content into
  the rail seam on every screen; this one dissolves Deck
  content into the pinned stack's seam, a seam that exists
  only on this screen. Two seams, two fades, one language.

Acceptance: mid-scroll, the last visible object dissolves
through the gradient — no hard clip line at the viewport's
bottom edge in any state (idle, drawer-open, utilities
standing); at max scroll the final object stands fully
opaque above the fade; the fade is invisible against ground
at rest (its bottom stop *is* the ground token, so there is
no band to see).

---

## §4 · F17 — THE FULL RAW-HEX CENSUS (file + line, this tree)

Method: `grep -nE '0x[0-9A-Fa-f]{6,}|#[0-9A-Fa-f]{6,}'` over
the three F17 files named by the audit/builder map —
`TactileDeckScreen.kt`, `MonolithCustomComponents.kt`,
`BlockitCoreViews.kt` — read site by site. **71 colour-literal
occurrences on 67 lines**, + 12 comment hexes (§4.4).
Disposition key: **[slot]** = an existing scheme slot/token
already names this value — use it; **[new]** = new token in
§5.1; **[shade]** = pure black/white + alpha — §5.2's shade
family; **[mask]** = RGB channel mask, named, never a colour;
**[die]** = the element is killed by another F-item — the
address changes only to a token marked for death with it.

### 4.1 · `TactileDeckScreen.kt` — 6 occurrences, 4 lines

| Line | Literal | Site | Disposition |
|---|---|---|---|
| 601 | `0x66FFFFFF` | eyebrow emboss shadow, day branch (night branch is `Color.Transparent` — a named constant, legal) | **[new]** `DeckEmbossWhite` (white @ 0x66) |
| 631 | `0xFFEDE3D8` | `heroGlyph`, night branch | **[slot]** this *is* night `onSurface` — the line becomes `if (effectiveNight) scheme.onSurface else scheme.onBackground`; the hex duplicates a slot the file already holds |
| 632 | `0x80000000` | `heroShadowGhost`, night | **[new]** `NightDeckHeroGhost` (black @ 0x80) |
| 632 | `0x99D8D1CB` | `heroShadowGhost`, day | **[new]** `TokenDeckHeroGhost` (= `surfaceVariant` @ 0x99, as a token) |
| 633 | `0x594A423C` | `heroCatchGhost`, night | **[die]** `NightDeckHeroGhostCatch` — tokenised for address only, definition carries "dies with F10"; F10's lane deletes token + site together |
| 633 | `0xB3FAF6F0` | `heroCatchGhost`, day | **[die]** `TokenDeckHeroGhostCatch` (same death note) |

### 4.2 · `MonolithCustomComponents.kt` — 6 occurrences, 5 lines

| Line | Literal | Site | Disposition |
|---|---|---|---|
| 193 | `0xFFFBF1EC` | `BlockitActionChip` fg, `isAccentArmed` | **[slot]** `TokenColorOnPrimary` (import; value identical — the unconditional both-modes use is today's behaviour, preserved; see §5.4 flag) |
| 315 | `0xFF4A423C` | `inkControlFill()`, night branch | **[slot]** `NightColorSurfaceSheet` — already imported in this file (L50); value unchanged (§0.2: the *resolver* stays) |
| 321 | `0xFFFFE4CF` | `onInkControlFill()`, night | **[slot]** `NightColorPrimary` |
| 321 | `0xFFFBF1EC` | `onInkControlFill()`, day | **[slot]** `TokenColorOnPrimary` |
| 560 | `0xFF9C8E84` | `stripToneColors()` `ordinaryInk`, night | **[slot]** `NightTextColorSecondary` — the token exists; this file simply never imported it |
| 562 | `0xFF9C8E84` | `stripToneColors()` `secondaryInk`, night | **[slot]** `NightTextColorSecondary` (same) |

### 4.3 · `BlockitCoreViews.kt` — 59 occurrences

**(a) The parallel palette, L64–103 — 38 occurrences, one per
line.** Every line is `const val <NAME> = <hex>` in
`BlockitCanvasView`'s companion. Disposition for all 38:
**the table is deleted**; the `tok*()` resolvers (L126–176)
derive from `Color.kt` via `.toArgb()` (§5.2) — one palette,
one home. Line-by-line, with its `Color.kt` owner:

| Line | Const = value | Owner token in Color.kt |
|---|---|---|
| 64 | LIGHT_BG `0xFFE4DFDA` | TokenColorBackground |
| 65 | LIGHT_SURFACE `0xFFE4DFDA` | TokenColorSurface |
| 66 | LIGHT_SURFACE_SHEET `0xFFE4DFDA` | TokenColorSurfaceSheet |
| 67 | LIGHT_SURFACE_VARIANT `0xFFD8D1CB` | TokenColorSurfaceVariant |
| 68 | LIGHT_SURFACE_BRIGHT `0xFFFAF6F0` | TokenColorSurfaceBright |
| 69 | LIGHT_PRIMARY `0xFF252323` | TokenColorPrimary |
| 70 | LIGHT_ON_PRIMARY `0xFFFBF1EC` | TokenColorOnPrimary |
| 71 | LIGHT_PRIMARY_CONTAINER `0xFFF55314` | TokenColorPrimaryContainer |
| 72 | LIGHT_ON_PRIMARY_CONTAINER `0xFF471602` | TokenColorOnPrimaryContainer |
| 73 | LIGHT_SECONDARY_CONTAINER `0xFF615B5A` | TokenColorSecondaryContainer |
| 74 | LIGHT_ON_SECONDARY_CONTAINER `0xFFF4EFEE` | TokenColorOnSecondaryContainer |
| 75 | LIGHT_ON_SURFACE `0xFF4E4948` | TokenColorOnSurface |
| 76 | LIGHT_ON_SURFACE_VARIANT `0xFF787170` | TokenColorOnSurfaceVariant — **DRIFT, §0.4 / D-F17-1** (owner value is `0xFF5F5856`) |
| 77 | LIGHT_OUTLINE `0xFF615B5A` | TokenColorOutline |
| 78 | LIGHT_OUTLINE_VARIANT `0x75615B5A` | TokenColorOutlineVariant |
| 79 | LIGHT_BLOCK_BAR `0xFF2E2A26` | TokenBlockBarSurface |
| 80 | LIGHT_TEXT_PRIMARY `0xFF2C2A29` | TokenTextColorPrimary |
| 81 | LIGHT_TEXT_SECONDARY `0xFF615B5A` | TokenTextColorSecondary |
| 82 | LIGHT_ALERT `0xFFD63E06` | TokenAlert |
| 85 | NIGHT_BG `0xFF171412` | NightColorBackground |
| 86 | NIGHT_SURFACE `0xFF171412` | NightColorSurface |
| 87 | NIGHT_SURFACE_SHEET `0xFF4A423C` | NightColorSurfaceSheet |
| 88 | NIGHT_SURFACE_VARIANT `0xFF100E0C` | NightColorSurfaceVariant |
| 89 | NIGHT_SURFACE_BRIGHT `0xFF362C27` | NightColorSurfaceBright |
| 90 | NIGHT_PRIMARY `0xFFFFE4CF` | NightColorPrimary |
| 91 | NIGHT_ON_PRIMARY `0xFF1B110D` | NightColorOnPrimary |
| 92 | NIGHT_PRIMARY_CONTAINER `0xFFF47442` | NightColorPrimaryContainer |
| 93 | NIGHT_ON_PRIMARY_CONTAINER `0xFF471602` | NightColorOnPrimaryContainer |
| 94 | NIGHT_SECONDARY_CONTAINER `0xFF4A423C` | NightColorSecondaryContainer |
| 95 | NIGHT_ON_SECONDARY_CONTAINER `0xFFFFE4CF` | NightColorOnSecondaryContainer |
| 96 | NIGHT_ON_SURFACE `0xFFEDE3D8` | NightColorOnSurface |
| 97 | NIGHT_ON_SURFACE_VARIANT `0xFF776A60` | NightColorOnSurfaceVariant |
| 98 | NIGHT_OUTLINE `0xFF807A7A` | NightColorOutline |
| 99 | NIGHT_OUTLINE_VARIANT `0x758F8888` | NightColorOutlineVariant |
| 100 | NIGHT_BLOCK_BAR `0xFF161311` | NightBlockBarSurface |
| 101 | NIGHT_TEXT_PRIMARY `0xFFD2C6BB` | NightTextColorPrimary |
| 102 | NIGHT_TEXT_SECONDARY `0xFF9C8E84` | NightTextColorSecondary |
| 103 | NIGHT_ALERT `0xFFFF8A5C` | NightAlert |

Test safety, paper-verified: a repo grep finds **no test or
other file referencing the `LIGHT_*`/`NIGHT_*` consts** — the
table's only consumers are this file's own resolvers. The
deletion is internal to one file.

**(b) The resolver that bypasses itself, L135 — 1 occurrence.**
`tokControlFill()` night branch is the raw `0xFF4A423C`
although the companion's own `NIGHT_SURFACE_SHEET` names it
two dozen lines above. Disposition: **[slot]** — under the
§5.2 derivation it becomes `NightColorSurfaceSheet.toArgb()`;
value unchanged (this resolver is *not* F12's, §0.2).

**(c) Paint sites, L609–1820 — 20 occurrences.**

| Line | Literal | Site | Disposition |
|---|---|---|---|
| 609 | `0x000000` | socket shading, black at computed alpha | **[shade]** `SHADE_BLACK_RGB`, alpha fn stands |
| 611 | `0x00FFFFFF` | socket sheen, white at computed alpha | **[shade]** `SHADE_WHITE_RGB` |
| 638 | `0x00FFF6EC` | chamfer catch, lamp tint at computed alpha | **[new]** `tokLampTint()` (§5.1 — **no token for this value exists anywhere today**) |
| 652 | `0xFF100E0C` | Strait channel floor, night | **[slot]** `tokSurfaceVariant()` — the file's own resolver, bypassed at this one site |
| 657 | `0x33FFFFFF` | Strait edge, white @ 0x33 | **[shade]** `ALPHA_STRAIT_EDGE_WHITE` over `SHADE_WHITE_RGB` |
| 657 | `0x2E000000` | Strait edge, black @ 0x2E | **[shade]** `ALPHA_STRAIT_EDGE_BLACK` over `SHADE_BLACK_RGB` |
| 689 | `0x2E000000` | knurl dark teeth | **[shade]** `ALPHA_TOOTH_BLACK` over `SHADE_BLACK_RGB` |
| 705 | `0x00FFFFFF` | knurl specular, white at computed alpha | **[shade]** `SHADE_WHITE_RGB` |
| 734 | `0x00FFFFFF` | night sweep = `tokOnSurface()` @ 0xB3, mask operand | **[mask]** `RGB_MASK`; alpha → `ALPHA_SWEEP_NIGHT` |
| 779 | `0x00FFFFFF` | major ticks night = `tokOnSurface()` @ 0xCC, mask | **[mask]** `RGB_MASK`; alpha → `ALPHA_TICK_MAJOR_NIGHT` |
| 782 | `0x00FFFFFF` | minor ticks = `tokTextSecondary()` @ 0x8C, mask | **[mask]** `RGB_MASK`; alpha → `ALPHA_TICK_MINOR` |
| 819 | `0x42000000` | slider-dot contact shadow | **[shade]** `ALPHA_DOT_SHADOW` over `SHADE_BLACK_RGB` |
| 833 | `0x00FFFFFF` | hub dimple = `tokBg()` @ 0x66, mask | **[mask]** `RGB_MASK`; alpha → `ALPHA_DIMPLE` |
| 838 | `0xFFFBF1EC` | wheel pip marker line, both modes (deliberate, per its comment) | **[slot]** `tokMarkerCreamDayFixed()` = `TokenColorOnPrimary`, both modes — value preserved (§5.4 flag) |
| 851 | `0x26000000` | hub contact shadow | **[shade]** `ALPHA_HUB_SHADOW` over `SHADE_BLACK_RGB` |
| 900 | `0x00FFF6EC` | hub core specular, lamp tint at computed alpha | **[new]** `tokLampTint()` (as L638) |
| 933 | `0x00FFFFFF` | `inkWith()` helper, mask operand | **[mask]** `RGB_MASK` — the helper's only hex; it was never a colour |
| 1804 | `0x4D000000` | slide cap contact shadow | **[shade]** `ALPHA_CAP_SHADOW` over `SHADE_BLACK_RGB` |
| 1811 | `0x66FFFFFF` | slide cap lit top edge | **[shade]** `ALPHA_CAP_EDGE_WHITE` over `SHADE_WHITE_RGB` |
| 1820 | `0xE6FFE4CF` | slide MARKER (C11), cream @ 0xE6, both modes | **[slot]** `ALPHA_MARKER` over `tokMarkerCreamNightFixed()` = `NightColorPrimary` — value preserved (§5.4 flag) |

Not counted, ruled legal so the gate (§5.5) doesn't chase
them: alpha-byte literals under 6 hex digits (`0xFF shl 24`
at L158, `0xB3/0xCC/0x8C/0x66 shl 24` at L734/779/782/833 —
these become the named `ALPHA_*` consts above when their
sites are swept), and channel extractions (`and 0xFF`, L155).

### 4.4 · Comment hexes — 12 occurrences, 11 lines, views file only

Comments are this studio's memory (audit §5.8); a hex in a
comment is a value a future builder copies. All rewrite to
token names in the same pass: L52 (`#F55314`/`#F47442` →
the accent token, day/night), L138 (`#4A423C` → the sheet
token), L626 (`#FFF6EC` → `TokenLampTint`), L774–775
(`#615B5A` / `#5F5856` — the drift's own discussion; it is
rewritten by D-F17-1's outcome, §5.3), L811 (`#F55314` →
accent token), L832 (`#FBF1EC` → `TokenColorOnPrimary`),
L857 (`#362C27` → `NightColorSurfaceBright`), L870
(`#FFF6EC` → `TokenLampTint`), L963 (`#FFE4CF` →
`NightColorPrimary`), L1817 (`#FFE4CF` → same). Deck and
Components carry **zero** comment hexes — verified, not assumed.

### 4.5 · Out of F17's scope — sightings filed for Rost, NOT swept here

The audit's F17 names Deck / components / views; the builder
map prices the three files above. The same disease stands in
Deck-adjacent files this cluster does not touch — recorded so
the sweep's boundary is a decision, not an oversight:
- `BlockitTowerFadersAndCatalogViews.kt`: L128 `0xFFA79587`
  (a night cap with **no token anywhere**), L212 `0xFF574D45`,
  L213 `0xFF6B5D52`, L214 `0xFF7F6E60` (a three-step night
  staircase invented in place), L220 `0xE6FFE4CF`. These will
  break a look exactly as F17 charges; they are the sweep's
  next ring, another lane's price.
- `BlockitSuitcaseAndSwitchesViews.kt`: L128/131/143/148 and
  repeats at L371/374/384/402 — the §5.2 shade family covers
  every one by shape.
- `MonolithRailView.kt`: L218, L251 (day `0xFF171616` — a
  near-black with no token), L259. Host chrome — issue #4 /
  F20's territory.
- `ShieldCapSelector.kt:64` `Color(0xFFFBF1EC)` — Shield's file.
- Clean, verified zero: `GuardLine.kt`, `ParkDrawer.kt`,
  `ReceiptSheet.kt`, `AirlockAndDisarmOverlays.kt` — proof
  the discipline is possible in this codebase.
- `HubSpecularTest.kt:59` carries raw hex in test code — the
  gate in §5.5 is a main-source gate; tests are named here so
  nobody "discovers" them mid-build.

---

## §5 · F17 — THE TOKEN VOCABULARY + THE SWEEP

### 5.1 · New tokens (all born in `ui/theme/Color.kt`, the one home)

Following the file's own convention (`TokenColor*` day /
`NightColor*` night definitions, semantic aliases after):

| Token | Definition rule | Serves |
|---|---|---|
| `TokenDeckControlFill` | day: alias of `TokenColorPrimary` (day controls are ink — polarity, §1.4) | F12 day |
| `NightDeckControlFill` | night: a new definition; **its value must satisfy §1.2's control band (R 1.10 – 1.24, ≥ 1.08 under card)** — first instance the audit's `#2A2320` (R 1.19) | F12 night |
| `TokenDeckHeroGhost` / `NightDeckHeroGhost` | day `surfaceVariant` @ 0x99 / night black @ 0x80 — today's L632 values, addressed | F10's surviving ghost |
| `TokenDeckHeroGhostCatch` / `NightDeckHeroGhostCatch` | today's L633 values, addressed; **definition comment: "dies with F10 — do not build on this token"** | F10's condemned ghost |
| `DeckEmbossWhite` | white @ 0x66 (today's L601) | eyebrow emboss |
| `TokenLampTint` | `#FFF6EC`, **mode-invariant** (it is a light colour, not a surface) — no token for it exists today | views L638/900 |

Reused, no new token: every §4 **[slot]** row — the scheme
and `Color.kt` already name them; Components L193/315/321/
560/562 and Deck L631 are imports and slot references, not
definitions.

### 5.2 · The Canvas derivation (kills the parallel palette)

`BlockitCanvasView`'s companion table (L64–103) is deleted.
Each `tok*()` resolver derives from the Compose token:

```kotlin
protected fun tokBg(): Int =
    (if (isNightMode) NightColorBackground else TokenColorBackground).toArgb()
```

— and so on for all nineteen, plus `tokLampTint()`,
`tokMarkerCreamDayFixed()` (→ `TokenColorOnPrimary`),
`tokMarkerCreamNightFixed()` (→ `NightColorPrimary`).
Resolver names and call sites do not change; only their
bodies' source does. The shade family lives in the same
companion as named constants, replacing §4.3(c)'s **[shade]**
and **[mask]** rows: `SHADE_BLACK_RGB = 0x000000`,
`SHADE_WHITE_RGB = 0x00FFFFFF`, `RGB_MASK = 0x00FFFFFF`, and
one `ALPHA_*` const per role (`ALPHA_DOT_SHADOW = 0x42`,
`ALPHA_HUB_SHADOW = 0x26`, `ALPHA_CAP_SHADOW = 0x4D`,
`ALPHA_CAP_EDGE_WHITE = 0x66`, `ALPHA_STRAIT_EDGE_WHITE = 0x33`,
`ALPHA_STRAIT_EDGE_BLACK = 0x2E`, `ALPHA_TOOTH_BLACK = 0x2E`,
`ALPHA_SWEEP_NIGHT = 0xB3`, `ALPHA_TICK_MAJOR_NIGHT = 0xCC`,
`ALPHA_TICK_MINOR = 0x8C`, `ALPHA_DIMPLE = 0x66`,
`ALPHA_MARKER = 0xE6`). A shade site then reads
`(ALPHA_DOT_SHADOW shl 24) or SHADE_BLACK_RGB` — the alpha is
the design variable and it is the thing named; pure black and
pure white are physics, not palette.

### 5.3 · D-F17-1 — the one value decision (Rost rules before build)

F17's standing law is *no colour value changes, only its
address* (builder map). The drift at views L76 makes that
law unsatisfiable as written: two addresses hold two values
(`0xFF787170` Canvas / `0xFF5F5856` Compose) for one tier, and
a single source means one of them moves. **Recommendation:**
`Color.kt` wins — it is the scheme of record, every Compose
surface already renders `0xFF5F5856`, and the views comment
(L774–775) itself treats the Compose spelling as the spec's.
Effect if ruled so: Canvas `tokOnSurfaceVariant()` consumers
(mid ticks and minor print in the wheel) deepen by that one
step on device; nothing else in either file changes value.
If Rost rules the other way, `Color.kt`'s token changes and
every Compose surface shifts instead — the wider blast
radius, priced here so the choice is informed. Either way
the decision is recorded in the token's comment, and L774–775's
comment is rewritten to state the outcome, not the schism.

### 5.4 · Two preserved inconsistencies, flagged not fixed

Faithful tokenisation (no value change) preserves two
one-name-two-values splits; naming them is this spec's job,
curing them is not F17's:
- **Marker cream:** the wheel pip's marker line is day cream
  `0xFFFBF1EC` in *both* modes (L838, deliberate per comment);
  the slide's MARKER is night cream `0xFFFFE4CF` in *both*
  modes (L1820, C11). Two elements, one word "marker", two
  creams. Tokens above keep both; a unification ruling
  belongs to whoever next touches C8/C11.
- **Accent-armed chip ink** (Components L193): day cream in
  both modes on the accent container, whose night value is a
  different orange. Tokenised as-is; the night pairing's
  contrast is a chrome-law question, not a Deck one.
- **Key-cap fills** (from §1.4 frame 1): FLOW/DONE caps sit
  outside the control band at night in both states (recess /
  sheet). Extending `NightDeckControlFill` to those two call
  sites is zero-price (same token, two references) and would
  make frame 1 pass whole — but the caps' bodies belong to
  F2/F7/F13. **Filed for Rost: extend F12's token to the caps,
  or accept frame 1 as a permanent two-exception frame.**

### 5.5 · The grep gate (the sweep's definition of done)

```
grep -rnE '0x[0-9A-Fa-f]{6,}|#[0-9A-Fa-f]{6,}' \
  app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt \
  app/src/main/java/com/example/ui/components/MonolithCustomComponents.kt \
  app/src/main/java/com/example/ui/views/BlockitCoreViews.kt
```
must return hits **only** inside the §5.2 shade/mask constant
definitions (`SHADE_BLACK_RGB`, `SHADE_WHITE_RGB`, `RGB_MASK`)
— every other line in the three files is a token name, a
scheme slot, or a named constant (`Color.Transparent`).
Plus: the same grep over `ui/theme/Color.kt` returns only
token definitions (its lawful home). A merge that adds a
hit outside those homes fails review on this gate alone —
the census is re-runnable, so the sweep cannot quietly
regrow (the Innovator's P2 lesson, applied to light).

### 5.6 · Merge order

F17 precedes any look touching this screen (audit §9) and
**the `NightDeckControlFill` definition precedes F12's
landing** — if F12 lands first, it births the token in
`Color.kt` itself and F17 must not re-define it. F14's fade
is token-clean from birth (§3.3) and may land any time.
F10 deletes the catch-ghost token with its site (§4.1);
F18's hub inherits the glyph slot and the §1 bands unchanged
— the Fusion changes the numeral's address, never its rank.

---

## §6 · CLUSTER ACCEPTANCE — ALL THREE, ONE CHECKLIST

1. Lamp Test: frames 1–3 sampled per §1.4 — sealed passes
   (chair exception as ruled), fault passes in its inverted
   shape, day idle untouched.
2. F12: §2.5's four checks.
3. F14: §3.2's gap measurements + §3.3's three fade checks.
4. F17: §5.5's gate returns only the named homes; a value
   diff of the sweep (old literal vs new token, site by site
   from §4's tables) is empty except the single D-F17-1
   outcome, recorded.
5. No file outside the cluster's reach was edited in value:
   `inkControlFill()`, `tokControlFill()`, the strip tones
   and the slide's paints read exactly as before — by token
   name now, by the same values.

— FINISHER-2, Rost's cell, issue #6. DECLARED, uncompiled,
uncommitted. File: `studio/plan/ISSUE-6-finisher-2.md`.
