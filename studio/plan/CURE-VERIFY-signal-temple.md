# CURE VERIFICATION — Signal's night hero · Temple's day cures

**Verifier:** Noir (Designers), re-seated by Ren for this desk, Oct 29, 2026.
**Method:** reading only. No compiles, no renders, nothing written into
either lane. Every verdict cites file+line on both sides — the claim as
filed, the code as it stands tonight. A code verdict is not a pixel
verdict: under the wave's proof law the renders remain each designer's
own debt, and nothing below discharges one.

---

## 1. SIGNAL — the night hero (Klinge S-1; Ruth: TAXES, one fix from FITS)

**The claim under test.** The night hero rendered as a uniform #3B383E
grey belonging to no stated system. Signal was ordered to audit its
plate against the trunk container-family leak (Theme.kt never maps
Material3's surfaceBright / surfaceContainer* slots, so stock greys
fill them) and to fix it by the mapping if the leak was the cause.

**The premise, verified.** Trunk `Focus-app/app/src/main/java/com/example/
ui/theme/Theme.kt` contains **no occurrence** of surfaceBright,
surfaceDim, or any surfaceContainer* slot (grep over the file: zero
hits). The leak is real: in trunk, every one of those slots falls back
to Material3's stock values.

**The cure, in the lane's code** (`builds/signal/Focus-app/…/ui/theme/`):

- The FULL family is now mapped in **both** schemes — `Theme.kt:43–49`
  (light) and `Theme.kt:84–90` (dark) — under a comment naming the fix
  ("the leak fix, Noir's mapping, Oct 29… No stock slot survives").
  Dark-scheme values: surfaceBright = NightColorSurfaceBright;
  surfaceDim = NightColorBackground; surfaceContainerLowest = #000000;
  surfaceContainerLow = NightColorBackground; surfaceContainer =
  NightColorSurfaceBright; surfaceContainerHigh = #17171B;
  surfaceContainerHighest = #1C1C20.
- The night hero's colour now derives from a **stated token**, by a
  traceable chain:
  `ui/screens/TactileDeckScreen.kt:520` — the hero card takes
  `.background(scheme.surfaceBright)` (comment :517–519: "the hero card
  is the Deck's one raised surface — it takes surfaceBright");
  → `Theme.kt:84` — dark surfaceBright = NightColorSurfaceBright;
  → `ui/theme/Color.kt:43` — `NightColorSurfaceBright = Color(0xFF121214)`,
  a named token whose own comment states its law ("ONE BLACK FIELD…
  4/255 above ground"), the ground being NightColorBackground #0E0E10
  (`Color.kt:39`).
- The charged value **#3B383E appears nowhere** in the lane's main
  source (grep over `app/src/main/`: zero hits).

**VERDICT: CURE IN CODE.** The family is mapped, the values are stated
in the theme file itself, and the night hero — #121214 on a #0E0E10
ground, defined by its hairline rather than a grey slab — derives from
a named token through the mapped slot. What this verdict does **not**
do: convert Ruth's TAXES. That conversion is a render judgment and
rides on Signal's re-render, which remains Signal's to file.

---

## 2. TEMPLE — Ruth's condition-2 fixes (PRESENTATION-temple.md §1.4, §13)

Claims as filed (§1.4): both fixes "applied in the lane's code," the
proving re-render owed and not claimed. Verified one by one.

### (a) The dial's fine tier incised at the ink's own depth

Claim: mids by day → text primary (#2B2013); minors by day → the same
ink at 85% (was: secondary, and text-2 at 55%); unfilled hub wells by
day → the text ink at 45% (was: recess tone). Night branches
byte-identical. Sites named in the spec: BlockitCoreViews.kt tick
voices + tetrad wells.

In the code (`builds/temple/Focus-app/…/ui/views/BlockitCoreViews.kt`):

- **Mids, day:** `:789` — `isMid -> if (isNightMode) tokOnSurfaceVariant()
  else tokTextPrimary()`. The day ink is #2B2013 as claimed:
  `LIGHT_TEXT_PRIMARY = 0xFF2B2013` (:80), selected by `tokTextPrimary()`
  (:168); Compose twin `TokenTextColorPrimary = Color(0xFF2B2013)`
  (ui/theme/Color.kt:31).
- **Minors, day:** `:790–794` — the else arm splits by mode; the day arm
  is `(0xD9 shl 24) or (tokTextPrimary() and 0x00FFFFFF)`. 0xD9 = 217
  of 255 ≈ **85%** alpha of the text-primary ink. As claimed.
- **Unfilled wells, day:** colour `when` (:980–992) sends the unfilled
  day well to `tokTextPrimary()`; the alpha `when` gives
  `!filled && !isNightMode -> 0.45f` (:995). The text ink at **45%**.
  As claimed.
- **Night branches identical — method stated honestly.** The tree holds
  no phase-1 snapshot of Temple's lane, so I verified against trunk,
  the ancestor both share, by diffing the tick and well regions
  (trunk vs lane, same windows). Every night expression is
  character-identical to trunk's: majors at night 0xCC over
  tokOnSurface; mids at night `tokOnSurfaceVariant()` (:789, left arm);
  minors at night `(0x8C shl 24) or tokTextSecondary()` — trunk's old
  both-modes line, preserved as Temple's night arm; filled wells at
  night `tokOnSurface()` at 0.8f (:982); unfilled wells at night
  `tokSurfaceVariant()` (:989). The cure's two values (0xD9, 0.45f)
  occur **only** behind day arms (`else` / `!isNightMode`). Within
  that method's reach, the claim holds exactly: the fix touched the
  day branches and nothing at night.

**VERDICT (a): CURE IN CODE** — all three day voices at the claimed
ink and depths; night arms untouched by any cure value.

### (b) The hero plate's shadow step

Claim: slab deepened #EDDCAC → #E3C184 (Color.kt
TokenColorSurfaceBright + its mirror constant in BlockitCoreViews.kt),
and the hero Column casts a real shadow, 7dp, its own shape
(TactileDeckScreen.kt hero).

In the code:

- `ui/theme/Color.kt:19` — `TokenColorSurfaceBright = Color(0xFFE3C184)`;
  the token's own comment records the predecessor (#EDDCAC), the
  measured step (1.00 → 1.26), and the reason. Mirror constant:
  `BlockitCoreViews.kt:68` — `LIGHT_SURFACE_BRIGHT = 0xFFE3C184`.
  The hero actually spends this token: TactileDeckScreen.kt:523's
  comment states the hero card takes surfaceBright in both modes, and
  its background follows in the same modifier chain.
- `ui/screens/TactileDeckScreen.kt:521` — `.shadow(7.dp,
  RoundedCornerShape(10.dp))` on the hero Column, directly under its
  fix comment (:516–520: "a true shadow step — the slab sits ON the
  ground and casts"). **7dp, the card's own shape.** As claimed.

**VERDICT (b): CURE IN CODE** — token, mirror, spend, and shadow all
present at the claimed values.

**Temple overall:** both fixes are in code exactly as §1.4 files them,
including the honesty of §13 — code-applied, render-owed. The re-render
remains Temple's debt under the proof law; this desk confirms the code
half of the claim, which is the half that was mine to check.

---

*— Noir, Oct 29, 2026. Copies of each verdict filed in mail/signal/
and mail/temple/. My own discharge renders remain owed and struck —
nothing in this file touches that debt.*
