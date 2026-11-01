# Issue #10 — Receipt (audit fixes) · FEEL SPECIALIST 5 — PAPER CHARACTER & MATERIAL

**Seat:** Feel Specialist 5 (paper character & material), Zettel's cell for GitHub issue #10.
**Domain:** what the slip *is* — stock, ink, light. Not its words (Designer), not its rhythm
(A6's seat), not its gesture (A8's seat). Where those seats' rulings touch the material
(№ ink, item 2; statements' voice, item 16; anchor, item 17), this spec states only the
material consequence and cross-refers.
**Audit scope:** `AUDIT-receipt.md` items **13, 15, 16 (material half), 17** + the binding
acquittals that protect the printed character: mono ledger Anonymous Pro 11sp at 1sp
tracking; **the slip is the brightest object in every frame**; the footer's night ink
follows the slip's ink law (item 13's guard, frame n08).
**Code read (this clone, branch `agent/issue-10`):** `ui/components/ReceiptSheet.kt`,
`ui/theme/Color.kt`, `ui/theme/Theme.kt`, `ui/theme/Type.kt`. All line citations are to
this clone. Contrast figures are WCAG relative-luminance ratios computed from the hexes
cited — arithmetic, not renders. Nothing here compiles, builds, or tests — paper only.

---

## 0. The one material law

**Paper does not change when the room does.** A printed slip pulled at 09:50 and a printed
slip pulled at 01:00 are the same stock carrying the same ink; the night slip reads
*brighter* only because the room around it went dark. The slip is therefore the one surface
in the app whose stock and ink are **frame-invariant** — it never takes a night variant,
never inverts, never greys. Every ruling below is this law applied. It is also the only
reading under which the audit's two acquittals can both be true at once: the footer cannot
"follow the slip's ink law" (item 13) if the slip has no ink law of its own, and the slip
cannot be "the brightest object in every frame" if its brightness is borrowed from
whichever scheme happens to be active.

---

## 1. What the slip is made of today — the evidence

The sheet paints itself from the active scheme, three reads in `ReceiptSheet.kt`:

| Read | Site | Day scheme resolves to | Night scheme resolves to |
|---|---|---|---|
| Stock: `.background(scheme.surfaceBright)` | `ReceiptSheet.kt:78` | **stock Material grey-white `#FEF7FF`** — not a Monolith token (see §1.1) | **stock Material grey `#3B383E`** — not a Monolith token |
| Ledger/title ink: `scheme.onBackground` | `:96, :151, :163, :195` | `TokenTextColorPrimary` `#2C2A29` (`Theme.kt:31`) | `NightTextColorPrimary` `#D2C6BB` (`Theme.kt:56`) |
| Secondary ink (№, footer, dim lines): `scheme.onSurfaceVariant` | `:108, :180, :195` | `TokenColorOnSurfaceVariant` `#5F5856` (`Theme.kt:35`) | `NightColorOnSurfaceVariant` `#776A60` (`Theme.kt:60`) |

Plus the edge: `.border(1.dp, scheme.outlineVariant, …)` at `:79`, corner
`RoundedCornerShape(24.dp)` at `:77`, **no shadow anywhere** (no `shadow()` modifier in
the file — lift today is brightness delta + border alone). Scrim:
`Color.Black.copy(alpha = 0.12f)` at `:69`, under a host `padding(12.dp)` at `:71`.

### 1.1 The stock is not Monolith's — the container leak, verified in this clone

`Theme.kt` maps **no member** of the Material3 `surfaceBright / surfaceDim /
surfaceContainer*` family in either scheme (grep over the file: zero hits;
`WarmTactileStudioColorScheme` at `Theme.kt:17`, `SealedVaultNightColorScheme` at
`Theme.kt:42`). So `scheme.surfaceBright` falls back to Material3's baseline defaults.
Verified against the shipped library this project builds with (Compose BOM 2024.09.00 →
material3-android 1.3.0, AAR in the local Gradle cache; `ColorLightTokens.SurfaceBright =
PaletteTokens.Neutral98`, `ColorDarkTokens.SurfaceBright = PaletteTokens.Neutral24`):

- **Day stock as rendered: `#FEF7FF`** (254, 247, 255), relative luminance **0.948** — a
  cool lavender-white, the only "paper" in the app whose blue channel beats its red. Day
  frames pass *by accident*: the luminance is near the intended paper's, the hue is not.
- **Night stock as rendered: `#3B383E`** (59, 56, 62), luminance **0.041** — the exact
  value the studio already caught sampling the night hero (Klinge's pixel charge;
  `CURE-VERIFY-signal-temple.md`, `AUDIT-spec-machinery-claims.md`: the trunk container
  leak, TRUE, three lanes independently). The receipt was never exempted from that leak —
  it is the same slot read at `ReceiptSheet.kt:78`.

The token the code *means* to use exists and is named for exactly this job:
`TokenColorSurfaceBright = #FAF6F0` (`Color.kt:15`, "The ONE raised card… Pure Crisp
Raised", alias `WarmIvoryPaper`, `Color.kt:141`). It is simply never wired to the slot
the slip reads.

---

## 2. STOCK — the paper itself

- **S2.1 — One stock, both frames: `#FAF6F0`** (`TokenColorSurfaceBright`, `Color.kt:15`;
  RGB 250/246/240, luminance **0.925**). Warm white — red highest, blue lowest; satisfies
  the zero-green rule (246 < 250). This is the day paper Sarthak loved; the ruling is that
  night gets *the same paper*, not a night paper.
- **S2.2 — The slip stops reading the scheme for its stock.** `ReceiptSheet.kt:78` paints
  the paper token directly (recommended: name the read at the sheet —
  `TokenColorSurfaceBright`, or its alias `WarmIvoryPaper` — so the intent survives
  refactors). **Warning to the builder, priced here because it will bite:** a global
  surface-family mapping is owed elsewhere in the app (the Deck hero's cure), and the
  staircase-correct dark value for that slot is `NightColorSurfaceBright #362C27`
  (`Color.kt:36`). If that mapping lands and the slip still reads `scheme.surfaceBright`,
  the slip silently re-darkens into furniture. The slip's exemption from the night
  staircase is deliberate and permanent: the staircase grades *the room's hardware*; the
  slip is not hardware, it is a document lying on it.
- **S2.3 — Flat fill, nothing on the stock.** No gradient, no texture, no noise, no image
  of any kind (none exists today; keep it so). A gradient would make every ink ratio in
  §3 position-dependent and unauditable; texture is a picture of paper, and this app's
  paper is paper by colour and light alone.
- **S2.4 — Never wrap the slip in Material3 `Surface`.** Today the slip is a raw
  `Column + background` (`ReceiptSheet.kt:76–82`), so no tonal-elevation overlay touches
  it. `Surface` would wash the stock toward the scheme's tint at elevation — at night,
  toward the night primary — re-introducing frame-dependence through the back door. The
  shadow in §5 is a modifier on the column, never a `Surface`.

**Stock against ground (the step the eye actually reads):**

| Frame | Ground | Scrimmed ground (§6) | Paper `#FAF6F0` vs scrimmed ground |
|---|---|---|---|
| Day | `#E4DFDA` (`Color.kt:11`), L 0.743 | `#C9C4C0`, L 0.557 | **1.61 : 1** |
| Night | `#171412` (`Color.kt:32`), L 0.0073 | `#141210`, L 0.0062 | **17.36 : 1** |

Today's night step, for the record: stock `#3B383E` vs scrimmed ground = **1.62 : 1** —
the slip currently separates from a night room no better than from a day room, which is
precisely backwards for paper under a dark room.

---

## 3. INK — density law on the paper

Ink on paper is **flat, full-opacity, and two-density**. Both densities are existing day
tokens; after §4's cure they are the slip's inks in *both* frames.

| Tier | Token | On `#FAF6F0` | Used by (as specced across this issue) |
|---|---|---|---|
| **Primary ink** | `TokenTextColorPrimary` `#2C2A29` (`Color.kt:27`) | **13.27 : 1** | Ledger lines (`ReceiptMonoLine`, `ReceiptSheet.kt:186–195`: Anonymous Pro 11sp / 1sp — acquitted, untouched); title (Atkinson Bold 15sp, `:90–97`); the two machine statements, in Anonymous Pro Bold 13sp / 1sp per item 16 (voice is A6's seat; the *ink* is this tier); № — item 2 already rules the № takes the title's ink, never greyer, so it leaves the secondary tier below |
| **Secondary ink** | `TokenColorOnSurfaceVariant` `#5F5856` (`Color.kt:23`) | **6.47 : 1** | The footer (Anonymous Pro 9sp / 0.25sp, `:172–180` — size and tracking are the standing measured fit, untouched). Nothing else after item 2 moves the № and item 11's one-ink cure (A10) retires the dim deflection tier |

- **S3.1 — The floor: nothing prints on paper below the secondary tier.** 6.47 : 1 is the
  faintest lawful ink on this stock. Any proposed third tier must be measured against
  `#FAF6F0` and land at or above it, in both frames, or it does not print.
- **S3.2 — No alpha ink on paper.** Density is chosen by token, never by opacity: the
  secondary ink at 60% over the stock composites to ≈ (157, 151, 148) = **2.68 : 1**, and
  even the primary ink at 60% falls to **3.87 : 1** — both below the floor, and both
  unmeasurable from the code that prints them. (The hairline alpha at
  `Color.kt:24`, `TokenColorOutlineVariant` 0x75, is a border colour, not an ink; §5
  removes the slip's border anyway.)
- **S3.3 — No accent on paper.** No orange tier exists on the slip today and none is
  added: a receipt's ink is one hue of near-black at two densities. The single accent
  (`#F55314` day / `#F47442` night) stays in the room, where §7 shows it also stays
  *dimmer than the paper* — that ordering is part of the material.
- **S3.4 — Density comes from weight before colour.** Where a line must strike harder
  (the total, the statements), it takes Bold in the primary ink — the typewriter's
  double-strike — never a darker invented hex. `#2C2A29` at 13.27 : 1 has no darker
  lawful step to need.

---

## 4. THE NIGHT-INK CURE (item 13's guard, frame n08)

### 4.1 Diagnosis — two stacked causes, both in the scheme reads

Frame n08's footer is near-illegible because **both** halves of the ratio collapse at
night, independently:

1. **The ink flips to a colour designed to be muted.** At night the footer reads
   `scheme.onSurfaceVariant` = `NightColorOnSurfaceVariant #776A60` (`Theme.kt:60` →
   `Color.kt:44`), a tier whose own comment prices it at 3.5 : 1 *over the night
   ground* — it is the room's whisper voice. On the raised night stock it was never
   priced at all.
2. **The stock isn't the stock.** Via §1.1's leak, the night paper is `#3B383E`.

Measured, as rendered today at night: footer ink `#776A60` on stock `#3B383E` =
**2.20 : 1** at 9sp — the audit's "near-illegible" is arithmetic, not taste. Note the
trap: fixing only the leak (mapping the slot to the intended `NightColorSurfaceBright
#362C27`) leaves the footer at **2.60 : 1** — still illegible. Fixing only the ink
(cream `#FFE4CF` on the stock grey) reaches 11.16 : 1 but leaves a grey card carrying
light print — a screen, not a slip, and §7's acquittal still dead. Neither half-cure is
a cure.

### 4.2 The cure — the slip's ink law, stated once

> **On the slip, stock and ink are constants, not scheme reads.** The slip paints
> `TokenColorSurfaceBright #FAF6F0` and prints in `TokenTextColorPrimary #2C2A29` /
> `TokenColorOnSurfaceVariant #5F5856` in **both** schemes. The scheme still governs
> everything around the slip — ground, scrim behaviour, the room's own surfaces — and
> nothing on the paper.

Result, measured: the night footer becomes **6.47 : 1**, the night ledger **13.27 : 1**
— not "improved for night" but *identical to day, digit for digit*, because it is the
same print object. The footer's one-line discipline (item 13: split at its own clause
boundary, never wrap mid-clause) is unaffected — the cure changes no size, tracking, or
word; the HOURS noun cure is A3's line, not this seat's.

### 4.3 Alternatives considered and rejected, with their numbers

| Alternative night stock | Best lawful footer ink on it | Footer ratio | Why it dies |
|---|---|---|---|
| `NightColorSurfaceBright #362C27` (the staircase step) + night inks | `NightTextColorSecondary #9C8E84` | 4.28 : 1 | Legibility marginal at 9sp — and the acquittal dies first: stock luminance 0.027 vs the night accent's 0.321. The slip would be ~12× dimmer than the room's accent. Furniture, not paper. |
| `NightColorSurfaceSheet #4A423C` (the cap tone) + night inks | `NightTextColorSecondary #9C8E84` | 3.10 : 1 | Worse ink *and* still not brightest (L 0.057). A sheet tone is hardware finish; hardware is what the slip is lying on. |
| `NightColorPrimary #FFE4CF` (cream) as stock + its dark ink `#1B110D` | — (ledger pair) | 15.23 : 1 | Legible, and still rejected: it makes the paper peach-tinted and frame-dependent — two papers, one per room. Paper that changes colour at night is a display. Identity, not contrast, kills it. |

---

## 5. EDGE & SHADOW — item 15's lift

Today: 24dp radius (`ReceiptSheet.kt:77`), 1dp `outlineVariant` border (`:79`), no
shadow. The border is doing all the separation work, and it is the digest sheet's
card-edge inherited whole (audit item 15). The cure (A5 carries the shape change; this
section prices its material):

- **S5.1 — Radius 3dp, all four corners.** Thermal stock is cut square; 3dp is the nick
  that keeps a corner from biting, not a curve. (The torn bottom edge is proposal-tier,
  group B — not specced here, not pre-built.)
- **S5.2 — Border: deleted, no substitute.** No hairline, no inner stroke, no
  `outlineVariant` in any alpha. The edge is defined by the luminance step (§2 table)
  plus the shadow below — a printed slip on a table has no drawn outline; it has a
  thickness shadow and a brightness the table doesn't have.
- **S5.3 — Shadow spec:** `Modifier.shadow(elevation = 8.dp, shape =
  RoundedCornerShape(3.dp), clip = false, ambientColor = Black @ 0.16, spotColor =
  Black @ 0.28)`, placed **before** `.clip(…).background(…)` in the slip's modifier
  chain (the current chain at `:77–79` becomes shadow → clip → background; a shadow
  applied after the background paints nothing outside the clip). Colour: pure neutral
  black at those alphas — never tinted warm, never the accent. 8dp elevation is the
  smallest cast that survives the day frame's weak luminance step (1.61 : 1): the day
  slip sits *on* a bright room, so its lift must be cast, not glowed.
- **S5.4 — The asymmetry is correct, state it so nobody "fixes" it:** in the day frame
  the lift is **shadow-led** (paper/ground step only 1.61 : 1; the shadow does the
  separating). In the night frame the same shadow is nearly invisible — black cast onto
  a scrimmed ground of L 0.006 composites to ≈ (14, 12, 11), a whisper — and the lift is
  **luminance-led** (17.36 : 1). Same object, same shadow values both frames; which
  mechanism carries the lift swaps with the room. Do not raise the night shadow to
  compensate: a visible halo around night paper would read as glow, and paper does
  not glow — it is simply the brightest matte thing in the room.
- **S5.5 — Contact anchor consequence (item 17, A5's geometry):** when the slip anchors
  to 0dp at bottom and sides, the shadow's cast survives only along the **top edge**
  (and the top corners' first few dp of the sides) — which is exactly where a fed
  sheet's shadow falls. The bottom/side shadow being off-frame is not a loss; do not
  re-float the slip to "show" it.

---

## 6. SCRIM — item 17, the room behind at 0.12

The scrim is **kept at 0.12** (binding acquittal — Muede, Druck; Aug's 0.28 was not
adopted and is not re-opened here). Materially, `Color.Black.copy(alpha = 0.12f)` at
`ReceiptSheet.kt:69` is a *defocus*, not a dimmer switch, and its arithmetic is:

| Room pixel | Unscrimmed | Under 0.12 black | Luminance kept |
|---|---|---|---|
| Day ground `#E4DFDA` | L 0.743 | `#C9C4C0`, L 0.557 | 75% |
| Night ground `#171412` | L 0.0073 | `#141210`, L 0.0062 | 85% |
| Night accent `#F47442` (room's brightest furniture) | L 0.321 | (215, 102, 58), L ≈ 0.24 | ≈75% |

- **S6.1 — Why 0.12 is the right weight for *this* object:** every room pixel keeps 88%
  of its channel values, so the room's internal relationships survive — he can still
  see where he is (continuity is the point of the aperture sheet) — while the room
  drops just far enough that the paper's step (§2) and cast (§5) own the frame. At
  0.28 the night ground would fall to L ≈ 0.0038 and the day room would read as a
  modal shutdown; that is a different room's law (the digest's 0.65, named at
  `ReceiptSheet.kt:41–44`, likewise stays in its own room).
- **S6.2 — Layer order is material law:** scrim behind slip, always. The scrim is the
  host `Box`'s own background (`:69`) and the slip is its child, so paper pixels never
  pass through it — the paper is *unscrimmed by construction*. Any restructure that
  overlays a full-screen scrim above the slip taxes the paper 12% and breaks §7 in
  both frames; treat that ordering as load-bearing in review.
- **S6.3 — The shadow lands on the scrimmed room.** §5's composites are computed over
  the scrimmed grounds above, not the raw grounds — the slip's cast and the room's
  defocus stack, and both are already priced into §5.4's asymmetry.
- **S6.4 — Scrim colour stays pure black.** A warm-tinted scrim would recolour the
  room behind the slip; black at 0.12 only lowers it. (Pure black also trivially
  satisfies the never-green rule, as does every colour named in this spec — all hexes
  above were checked: green channel < max(red, blue) throughout.)

---

## 7. BRIGHTEST OBJECT — the acquittal, audited in luminance

Relative luminance of every candidate "brightest thing" in frame, scrim applied to
room objects, paper unscrimmed per §6.2:

| Object | Day frame | Night frame, today | Night frame, cured |
|---|---|---|---|
| **The slip** | stock `#FEF7FF` as rendered: 0.948 (intended `#FAF6F0`: **0.925**) | stock `#3B383E`: **0.041** | `#FAF6F0`: **0.925** |
| Room accent | `#F55314`: 0.257 | `#F47442` scrimmed: ≈ 0.24 | ≈ 0.24 |
| Room ground (scrimmed) | 0.557 | 0.0062 | 0.0062 |

Day passes today by the stock accident of §1.1. **Night fails outright today:** the
room's own accent is ~6× more luminous than the slip — the "receipt" is a grey card in
a dark room with a brighter lamp behind it. After the cure the slip is the brightest
object in both frames by a wide, boring margin (0.925 vs 0.557 day / 0.24 night), which
is what an acquittal should look like in numbers: not close.

---

## 8. Builder's acceptance — pixel checks (renders are the builder's proof, not mine)

1. **Stock sample:** slip interior, ≥20dp from any glyph or edge, samples exactly
   `#FAF6F0` in a day frame **and** in a night (n08-class) frame. Any other value —
   `#FEF7FF`, `#3B383E`, `#362C27` — is a fail with the cause named in §1.1/§2.2.
2. **Footer sample (night frame):** footer glyph cores sample `#5F5856` on `#FAF6F0`
   (anti-aliased edge pixels excluded). `#776A60` anywhere on the paper is a fail.
3. **No-flip sweep:** every ink colour sampled on the paper is identical between the
   day and night frames of the same slip.
4. **Edge sample:** no 1dp outline ring around the slip in either frame; day frame
   shows a cast shadow darkening the scrimmed ground along the top edge; night frame
   shows the luminance step doing the work with no halo.
5. **Room sample:** 20dp above the slip's top edge, ground samples `#C9C4C0` (day) /
   `#141210` (night) — scrim at 0.12, neither raised nor lowered, and behind the slip,
   never over it.
6. **Order check:** accent visible in the room behind samples dimmer than the paper
   (luminance ordering of §7 holds in the actual render, both frames).

## 9. What this spec deliberately does not touch

- Words, figures, and line order — Designer + A1–A4/A10/A11 seats. Footer text and its
  clause-split guard are A3/item 13's wording seat; §4 cures only its ink.
- Rhythm, section rules, spacing — A6/item 16. §3 supplies that seat its ink tier and
  nothing else.
- Arrival feed, dismissal, anchor geometry — A7/A8/A5. §5.5/§6 state only their
  material consequences.
- The Deck hero and the global surface-family mapping — another lane's cure. §2.2
  flags the one interaction that lane must not break: whatever the dark scheme's
  `surfaceBright` becomes, the slip does not read it.
- Fonts: nothing here adds, removes, or re-weights a face. Ledger stays Anonymous Pro,
  title stays Atkinson Hyperlegible, Sora stays off the paper — it is the app's display
  face, not the paper's.
- No new hex is introduced anywhere in this spec. Every colour named already exists in
  `Color.kt`; the cure is a re-wiring of reads at `ReceiptSheet.kt:78` (stock) and the
  ink reads at `:96/:108/:151/:163/:180/:195` (to the paper constants), plus §5's
  shadow/border swap at `:77–79`. No schema, no state, no migration.

— Feel Specialist 5 (paper character & material), Zettel's cell (issue #10). Paper only; nothing compiled.
