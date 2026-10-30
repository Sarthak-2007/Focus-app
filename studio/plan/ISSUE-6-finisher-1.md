# ISSUE #6 — FINISHER-1 · THE FINISH OF F10 / F11 / F13
## Finisher seat 1, Rost's cell · Fri Oct 30, 2026
## Status: DECLARED, uncompiled. Nobody compiles. No commit made.

My cluster, and only my cluster: **F10** (hero single-press),
**F11** (card shadow true), **F13** (key-cap radii registered) —
audit `studio/plan/AUDIT-deck.md` §3/§9, designer disposition
`studio/plan/ISSUE-6-designer.md` D5/D8/D13. Everything below is
priced **S** (the audit's own price for all three), lands on the
current room independent of the F16/F18 restructure, and changes
**no colour value anywhere** — F17's law (address only) binds
this whole file. No green or near-green is introduced; no image,
generated or otherwise, is involved.

Primary file: `app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt`
(1,148 lines at this seating). Tokens file:
`app/src/main/java/com/example/ui/theme/Color.kt`. Register file:
`app/src/main/java/com/example/ui/theme/Theme.kt`.
**All line numbers below are this seating's, grep-verified.
Where the builder map's numbers differ, mine govern** (see §0.1).

---

## §0 · CODE vs CLAIMS — READ THIS FIRST

The audit's claims for my cluster are true in substance and
wrong in three particulars. A builder working from the claims
alone would ship two of them wrong.

**0.1 — The builder map's line numbers are stale.**
`ISSUE-6-builder-map.md` cites the key-caps at its lines 718/825
and the hero card clip at ~574. Actual, this tree:
hero card clip **L551**, FLOW cap **L695/L697**, DONE cap
**L802/L804**, progress track **L724/L731**. The map was written
against a different seating of the file. Every line in this spec
was re-grepped today; build from these.

**0.2 — F10's triple print: TRUE, exactly as charged.**
Hero `Box` L634–652 composes the string three times:
shadow ghost `Text` at `offset(y = (-1).dp)` (L635–640),
catch ghost `Text` at `offset(y = 1.dp)` (L641–646),
glyph `Text` at y = 0 (L647–651). Ghost colours are raw hex at
L632–633, glyph ink raw hex at night at L631. Origin of the
values: `BEYOND-deck.md` D3, filed verbatim — the code is a
faithful build of a spec the audit has since overruled in count.

**0.3 — F11's dead shadow import: TRUE, and worse than charged.**
`import androidx.compose.ui.draw.shadow` sits at **L36** and a
repo-wide grep for `.shadow(` across `app/src/main/java` returns
**zero call sites — in the whole app, not just this file.** There
is also no elevation anywhere on the Deck. The hero card
(L548–578) is, in order: `clip(24.dp)` L551 → `background(scheme.
surfaceBright)` L557 → `border(1.dp, outlineVariant, 24.dp)`
L558 → vertigo `.then(…)` → `padding(16, 18)` L578. Its "lift"
is tone plus a hairline, full stop. The audit §2 line "lifted by
tone alone — its shadow import is dead" is exact.

**0.4 — F13's "two radii app-wide": FALSE as an app-wide claim,
TRUE as a Deck claim, and glossed by the builder map.**
- The register already exists and is correct: `Theme.kt` L80–87,
  `MonolithShapes` — 12dp (extraSmall/small/medium), 24dp
  (large/extraLarge), under the comment "Strictly two corner
  radii across the app: 12.dp (small) and 24.dp (large)". F13 is
  not a missing register; it is Deck non-compliance with one.
- On the Deck, the offenders are exactly the two 8dp caps
  (FLOW L695/L697, DONE L802/L804). The card's 24dp stands.
- But the Deck also carries **3dp** (progress track, L724/L731),
  which neither the audit's "two radii" sentence nor the builder
  map's F13 entry ("no other radius on this screen changes")
  honestly registers. §3.4 below registers it as a named
  exception instead of pretending it away.
- App-wide, two radii is **not** true today and this cluster
  does not make it true: `ParkDrawer.kt` 16dp top corners (L237)
  and 8dp (L430), `ShieldCapSelector.kt` 9dp (L135),
  `FocusSequencerScreen.kt` 9dp (L834/L836),
  `ShieldMatrixScreen.kt` 8dp (L712/L714) all stand outside the
  register. They are other screens' debt — filed here by name
  for Rost, **not** fixed in this cluster (§3.5).

**0.5 — The eyebrow's fake emboss is NOT F10.**
`TactileDeckScreen.kt` L601 carries a second, unrelated shadow:
the eyebrow `TextStyle(shadow = … Color(0x66FFFFFF), offset
(0f, 1f))` in day mode. That is Schein T1, sentenced under the
eyebrow items (audit §3 / F4 family), not the hero press. Do not
let a builder "fix the ghost" by touching L601, and do not let
F10's pass be credited with killing it. Flagged so the two
shadows are never confused in review.

**0.6 — Every ghost value is already a token wearing a hex
costume.** Decomposition, exact (alpha byte → fraction):

| Code (Deck) | Value | = Token | Alpha |
|---|---|---|---|
| L632 day shadow ghost | `0x99D8D1CB` | `TokenColorSurfaceVariant` #D8D1CB | 0x99 = 60% |
| L632 night shadow ghost | `0x80000000` | black | 0x80 = 50% |
| L633 day catch ghost *(dies)* | `0xB3FAF6F0` | `TokenColorSurfaceBright` #FAF6F0 | 0xB3 = 70% |
| L633 night catch ghost *(dies)* | `0x594A423C` | `NightColorSurfaceSheet` #4A423C | 0x59 = 35% |
| L631 night glyph *(not mine)* | `0xFFEDE3D8` | `NightColorOnSurface`, identical | 100% |

So F10's tokenisation is provably value-preserving: same bytes,
new address. The catch ghost's values are **not** tokenised —
a dying value gets no name (§1.2).

---

## §1 · F10 — HERO SINGLE-PRESS · S

### 1.1 The ruling, finished
The catch ghost **dies**. One ghost survives: the **shadow
ghost**, at its current offset, in its current values, under a
token name. The press is a deboss, and a deboss has a shadow
side and a catch side; keeping only the shadow side is what
makes it read as *pressed in* rather than *doubled*. The catch
ghost (the light fringe below the glyph) is the half that reads
as blur at arm's length and as a second strike up close — it is
also the half whose day value is the card's own bright tone at
70%, i.e. a halo, not a shadow. Shadow-only is the gallery form.

### 1.2 Exact values — nothing moves, one Text dies
- **Survivor:** the `Text` at L635–640, unchanged in every
  particular except its colour source (§1.3):
  - same `heroAnnotated` string, same `DialClockTextStyle`,
    same `heroTracking`;
  - offset **x = 0.dp, y = (−1).dp** — one direction, vertical
    only, exactly as today. No x drift, no blur, no scale.
- **Killed:** the `Text` at L641–646 entire, and the
  `heroCatchGhost` val at L633 entire. After this pass the
  identifier `heroCatchGhost` must not exist anywhere in the
  repo (acceptance §4.1, check 1).
- **Glyph:** the `Text` at L647–651 is untouched — same ink,
  same tracking, y = 0. The press never touches the glyph's own
  fill or contrast (BEYOND D3's floor, kept).
- **Resulting stack:** exactly **2** `Text`s in the hero `Box`:
  ghost at −1dp behind, glyph at 0. Draw order stays ghost
  first, glyph second.

### 1.3 Tokens — names, file, values
In `ui/theme/Color.kt`, in a new hero-press subsection placed
after the Night token block and before the semantic aliases
(house convention: `TokenColor…` day / `NightColor…` night):

```kotlin
// The hero press (F10): the single surviving shadow ghost of the
// Deck's numeral — a deboss's shadow side only. Values are the
// shipped composites, moved, not changed (F17 law).
val TokenColorHeroPressShadow = Color(0x99D8D1CB)   // surfaceVariant #D8D1CB at 60%
val NightColorHeroPressShadow = Color(0x80000000)   // black at 50%
```

Deck side (replacing L632, deleting L633):

```kotlin
val heroShadowGhost = if (effectiveNight) NightColorHeroPressShadow
                      else TokenColorHeroPressShadow
```

- **Selection key is `effectiveNight`** (L185:
  `isDarkTheme || isSessionRunning`) — the same key the glyph
  itself uses at L631. The ghost and its glyph change scheme in
  the same frame or the press tears mid-seal. Do **not** key
  the ghost on the scheme, and do not key it on `groundNight`:
  the hero reads night at t=0 (audit's night split); its ghost
  is part of the hero, not part of the ground. (Contrast F11,
  §2.3, where the opposite keying is correct — the two items
  run on two different clocks, deliberately.)
- Adjacent debt, named not taken: L631's night glyph hex is
  byte-identical to `NightColorOnSurface`. Substituting it is
  F17's sweep, not this item; if the F17 builder reaches it
  first, nothing in this spec changes.

### 1.4 When the ghost is OFF — the truth table
One derived flag, computed beside L630–633, gating composition:

```kotlin
val heroPressOn = !showRunningForm && !isStopwatchMode
```

| State | Hero prints | Ghost |
|---|---|---|
| Idle, timed span (`MIN` / `H` form) | static span | **ON** |
| Idle, FLOW (`isStopwatchMode`, dial < 10) | the word FLOW | **OFF** |
| Proving / jammed (idle form, span as dialled) | static span | **ON** (it is the set span, at rest) |
| Running, timed (ticking MM:SS) | `primaryReadout` | **OFF** |
| Running, FLOW (count-up) | ticking readout | **OFF** (both clauses bite) |
| Safe Break (hero = break countdown) | ticking readout | **OFF** |
| Grace window (post-F6: countdown in the hero) | ticking readout | **OFF** |
| Held finish (`heldFinish`, frozen readout) | frozen MM:SS | **OFF** — running-form print never carries the press, frozen or not |
| Seal shot (`shotActive`) | running form | **OFF** (inherited from `showRunningForm`; no special case) |
| Routine-born session | running from birth | **OFF** (inherited) |

Rules that bind the implementation:
- **Conditional composition, never camouflage.** When
  `heroPressOn` is false the ghost `Text` is *not composed* —
  not alpha-0, not `Color.Transparent`. A transparent ghost
  still costs a draw, still sits in the semantics tree, and
  makes the acceptance grep lie.
- The reason is the audit's and it is physical, not tasteful:
  a press does not strike a moving target. A ghost offset
  behind a numeral that changes every second is a permanent
  1dp misregistration — it reads as blur (Muede) and doubles
  the OLED dwell of the brightest pixels on a readout that
  stands for hours (audit §6, burn-in). FLOW's word is a mode
  label, not a struck quantity; it was never pressed.
- **Fusion migration (F18):** when the readout moves into the
  wheel hub (designer D5/D8), this exact flag and token pair
  move with it — hub span at idle ON, hub ticking readout OFF.
  F18's builder inherits `heroPressOn` as a named requirement,
  not a suggestion.

### 1.5 The fallback gate (kept, pre-agreed)
BEYOND D3's removal trial survives the audit and binds here:
on device, if the single shadow ghost still reads as blur at
arm's length, or slows the eye-order test by any measure,
**both** texts collapse to one — the ghost `Text` is deleted,
`TokenColorHeroPressShadow` / `NightColorHeroPressShadow` are
deleted with it (no orphan tokens), and the hero ships flat.
That is Dens's kill-both, held as fallback by the audit §10.
It is a device verdict for Ren/Sarthak, **not** builder
discretion: the builder ships single-press as specced.

---

## §2 · F11 — THE CARD GETS ITS SHADOW · S

### 2.1 The ruling, finished
The shadow **lands** — this spec's primary and only buildable
form. "Or stops claiming lift" is not a second option the
builder may pick; it is the pre-agreed consequence if the
device trial kills the shadow (§2.5), executed as a claim
deletion, by name, in the same pass that removes it.

### 2.2 Exact recipe — day
On the hero card `Column` (L548), `.shadow(…)` is inserted as
the **first** modifier after `.fillMaxWidth()`, before
`.clip(…)` at L551:

```kotlin
.shadow(
    elevation = if (groundNight) 0.dp else 2.dp,
    shape = RoundedCornerShape(24.dp),
    ambientColor = TokenColorPrimary.copy(alpha = 0.18f),
    spotColor = TokenColorPrimary.copy(alpha = 0.25f)
)
```

- **Elevation: 2.dp in day, exactly** — the audit's number, not
  a starting point for tuning. There is no second tuning round
  in this item; a 2dp that reads wrong triggers §2.5, not 3dp.
- **Shape: `RoundedCornerShape(24.dp)`**, identical to the
  card's clip/border shape (L551/L558). A shadow cast from any
  other shape prints a card that isn't the card.
- **Shadow colour: the warm ink, not the default.** Compose's
  default shadow is neutral black; on this app's warm porcelain
  ground (#E4DFDA family) a neutral shadow reads grey-cool —
  dirt, not shade. `TokenColorPrimary` (#252323, the ink token,
  `Color.kt` token 6) at **spot 25% / ambient 18%** keeps the
  shadow in the room's own hue family, spends no new hex, and
  is zero-green by construction. (Builder: `ambientColor` /
  `spotColor` are the `Modifier.shadow` parameters in the
  project's Compose UI version — confirm the parameter names
  against the BOM at build time; the values above are the spec,
  the spelling is the library's.)
- **The surface block is inseparable.** `shadow → clip →
  background → border` (L551–558) is one surface statement and
  moves as one in any later pass — in particular F15's
  vertigo relocation must take the shadow *with* the card
  surface it shades, or leave the whole block and say so. A
  shadow stranded on the ground while the card recedes would
  print a ghost of a card that left.

### 2.3 Night — tone-only, and the clock it runs on
- **Night recipe: elevation 0.dp.** No ambient/spot colour is
  evaluated at 0.dp; the card's lift at night is exactly what
  it honestly is today — `surfaceBright` tone step over the
  ground plus the 1dp `outlineVariant` hairline. No dark halo
  is simulated: black shade on the night ground (#171412
  family) is invisible where it lands and muddy where it
  grazes the card edge. Tone-only is not the cheap option
  here; it is the only true one.
- **Selection key is `groundNight`** (L245), **not**
  `effectiveNight` — the deliberate opposite of F10 (§1.3),
  and the reason is physical: the shadow belongs to the
  *room*, and the room steps to night at t=400 of the seal
  shot, on the ground's own step (the Deck's night split).
  Keyed on `effectiveNight`, the shadow would vanish at t=0
  while the ground under it is still day-lit for 400ms — a
  visible pop under a still-lit card. Keyed on `groundNight`,
  shadow and ground change in the same frame: in day the card
  stands in a lit room and casts; the frame the room goes
  dark, there is nothing left to cast into.
- In a *standing* dark theme both keys agree; the distinction
  exists only across the seal, which is exactly where a
  finisher earns the name.

### 2.4 The only-elevation law
This shadow makes the hero card the Deck's **only** elevated
surface — which is what the audit §2 inventory already claims
it is ("the Deck's only raised surface"). No other Deck
element takes `elevation > 0` in this pass or rides along
later without its own ruling: not the key-caps (F13 caps are
flat print with hairlines), not the chips, not the drawer.
One raised object per face is the finish; a second elevation
anywhere on this screen fails review on this line.

### 2.5 If the shadow dies on device — the claim dies with it
Trigger: on-device, the 2dp warm shadow reads as dirt, smear,
or float (the same removal-trial standard as §1.5). Then, in
one pass and no other:
1. the `.shadow(…)` call is removed **and** the L36 import is
   removed with it — the import does not get a second life as
   a dead promise;
2. the ESR-13 comment at L552–556 is rewritten to state the
   truth: the card is a tone-stepped panel, flat in both
   schemes, claiming no lift;
3. the word "raised" leaves the file header's instrument
   description in the F21 comment pass.
Until that verdict, the import is live, the claim is true, and
nobody pre-emptively softens either.

### 2.6 Fusion consequence (F18)
The Fusion kills the hero panel (designer D8): the wheel
stands on the ground and claims no lift. If F18 lands before
F11, F11 is **struck as moot** — do not shadow the hub, do not
shadow the wheel, do not carry the 2dp anywhere. If F11 lands
first (the expected order — it is landable on the current room
today), F18's builder deletes the shadow with the panel and
this section is spent. Either order converges; neither leaves
a shadow without a card.

---

## §3 · F13 — KEY-CAP RADII REGISTERED · S

### 3.1 The register (already law — `Theme.kt` L80–87)
Two corner radii, app register, `MonolithShapes`:
**small = 12.dp** (extraSmall/small/medium) ·
**large = 24.dp** (large/extraLarge). Every corner on the Deck
is one of these two, plus the single named exception at §3.4.
The comparison anchor already on this screen: the utilities'
`BlockitActionChip` (`MonolithCustomComponents.kt` L202/L204)
wears 12dp today — after this pass, every key-cap-shaped thing
on the Deck is visibly one family.

### 3.2 The two caps — exact changes
Both caps change **8.dp → 12.dp**, clip and border together:

| Cap | Site (clip / border) | Change |
|---|---|---|
| FLOW key-cap | L695 / L697 | `RoundedCornerShape(8.dp)` → `RoundedCornerShape(12.dp)`, both sites |
| DONE key-cap | L802 / L804 | `RoundedCornerShape(8.dp)` → `RoundedCornerShape(12.dp)`, both sites |

- **Pairing law:** a cap's clip and its border shape are the
  same value, always, changed in the same edit. A 12dp clip
  under an 8dp border (or the reverse) draws a doubled,
  mismatched corner — the single most visible defect this item
  could ship. Current code pairs them correctly at 8; the pass
  preserves the pairing at 12.
- **Nothing else about the caps changes:** padding stays
  `horizontal = 10.dp, vertical = 5.dp` (FLOW L702, DONE L809);
  fills, borders' widths and colours, text (Anonymous Pro Bold
  11sp, 1sp tracking) all stand. Radius does not change a box's
  size — the caps keep their exact footprints; only their
  corners grow rounder, to the register's arc. On a ~21dp-tall
  cap, 12dp reads near-pill: that is the register's intended
  consequence, and it is what makes DONE read as kin to the
  42dp-tall DESK CLOCK / HELD chips instead of as a smaller,
  sharper species.
- **House style:** write the literal `RoundedCornerShape(12.dp)`,
  matching every other site in the app (all registered sites
  use literals; `MonolithShapes` is the register of record,
  not a call-site indirection this codebase uses). The comment
  at each cap may cite the register; the value is what review
  checks.

### 3.3 Sequencing against F2 — both orders converge
F2 kills the FLOW cap as a control. Audit sequencing lets
F13 land before or after F2:
- **F13 first:** both rows of §3.2's table change; F2 later
  deletes the FLOW cap whole, radius and all.
- **F2 first:** the FLOW row is already gone; only DONE
  changes. The designer's "FLOW's successor print" (D13)
  needs no radius ruling: the successor is *print* (the hub /
  recipe line), not a cap body, and print has no corners.
Under no order does a third outcome exist. A reviewer who
finds the FLOW cap alive at 8dp after both items claim done
has found a miss, whichever order was flown.

### 3.4 The registered exception — the progress track's 3dp
The track at L724/L731 wears `RoundedCornerShape(3.dp)` on a
6dp-high bar. This is **not a corner radius**; it is a pill —
radius = height ÷ 2, the bar's ends fully round by geometry.
It is registered here, by name, as the Deck's sole exception:

> **Bar rule:** a fully-rounded progress bar takes radius =
> its height ÷ 2 and is exempt from the two-radii register.
> Any other radius value on this screen is a defect.

Pretending the register is "two, full stop" while a 3dp sits
in plain sight is how registers rot (audit §7: a law enforced
by comment is a wish — so the exception is written where the
law is). Post-Fusion (F18) the track dies and the exception
dies with it, automatically; the interim truthful track
(F3) keeps the pill.

### 3.5 Out of scope, filed by name (Rost's to route)
The register is app law but this cluster is Deck-scoped. The
following stand in violation elsewhere and are **not** touched
here: `ParkDrawer.kt` 16dp (L237), 8dp (L430), 2dp (L293);
`ShieldCapSelector.kt` 9dp (L135); `FocusSequencerScreen.kt`
9dp (L834/L836); `ShieldMatrixScreen.kt` 8dp (L712/L714).
Nobody may cite this file as evidence the app runs two radii;
it evidences the **Deck** does, after this pass. Routing the
rest is a separate order.

---

## §4 · ACCEPTANCE — CHECKS A REVIEWER RUNS BY EYE (AND GREP)

Paper/render checks only; nothing here requires a compile
beyond Ren's eventual build on Sarthak's word. A check that
cannot fail is not a check: each names its failure sight.

### 4.1 F10
1. **Grep:** `heroCatchGhost` — 0 hits repo-wide. `Text(` count
   inside the hero `Box` — exactly 2. `HeroPressShadow` —
   exactly 2 token definitions (Color.kt) + 1 Deck reference.
   *Failure sight: any surviving catch identifier, or a third
   Text wearing transparency.*
2. **Day, idle, timed span dialled (e.g. 2H 30M):** one soft
   darker fringe **above** the glyph's strokes only; nothing —
   no light edge, no halo — below them. Zoom a screenshot:
   the fringe is one-sided or the pass failed.
3. **Night / sealed scheme, idle span:** same one-sided fringe,
   darker (black at 50%); glyph itself unchanged in brightness.
4. **Dial below 10 (FLOW):** the word sits **flat** — no fringe
   on either side, at any dial position, screen recorded while
   dragging across the FLOW boundary: the ghost must pop
   neither in nor out mid-drag beyond the mode change itself.
5. **Running session, 10 seconds of the ticking readout:**
   sampled frames show a single clean numeral; no doubled top
   edge on any second. Same for a Safe Break countdown and
   for the held finish frame.
6. **Removal trial (the gate, §1.5):** arm's-length read on
   device. Blur = kill-both, executed as §1.5 spells, not
   retuned.

### 4.2 F11
1. **Grep:** `.shadow(` — exactly **1** call site in the app,
   in `TactileDeckScreen.kt`, on the hero card, before the
   clip. The L36 import is live (its only consumer). *Failure
   sight: a second elevation anywhere on the Deck (§2.4).*
2. **Day, Deck at rest:** the card visibly *stands off* the
   ground — a faint warm shade pooled at its bottom and side
   edges, none sitting on top of the card's face. Squint test:
   blur your eyes and the card is still the one object that
   lifts. If the shade reads grey or blue against the warm
   ground, the colour recipe (§2.2) was not followed.
3. **Night (dark theme, Deck at rest):** **no halo** — the
   card's edge is tone step + hairline only, as clean as
   today's build. Any visible dark bloom is a failure.
4. **Seal, watched frame by frame:** the shadow survives t=0
   (hero already night) and steps away **with** the ground at
   its night step — never before it. A shadow that vanishes
   while the room is still lit fails §2.3.
5. **Hierarchy (Lamp-adjacent):** at sealed night the shadow's
   absence keeps brightness order glyph > card > controls >
   ground; in day the shadow must not make the card read
   louder than the glyph it frames — if it does, that is the
   §2.5 trigger, judged on device, not adjusted in code.

### 4.3 F13
1. **Grep, Deck file:** `RoundedCornerShape(8.dp)` — **0 hits**.
   Full value census of the file: exactly {24.dp (card),
   12.dp (caps), 3.dp (track, §3.4 exception)} and nothing
   else. *Failure sight: any fourth value, or an 8 hiding in
   a border whose clip was changed (pairing law, §3.2).*
2. **Running state, one screenshot:** DONE's corners and the
   DESK CLOCK / HELD chips' corners are the **same arc
   family** — hold the screenshot at arm's length; the caps
   must read as one set of hardware. DONE still sits at its
   old footprint, same width and height as before the pass.
3. **Idle state:** if the FLOW cap still lives (F2 not yet
   landed), its corners match DONE's exactly; if F2 landed
   first, no cap-shaped remnant of it exists at any radius.
4. **Zoom, either cap:** one clean corner arc — no double
   edge, no corner where clip and border disagree.

### 4.4 The cluster, together
One idle day frame and one sealed night frame: numeral
pressed once and reading as print, card standing in day and
stepping by tone at night, every cap in the room wearing the
register's corner. That pair of frames is the gallery
standard for this cluster; anything less is a partial pass
reported as partial.

---

## §5 · BOUNDARIES, SEQUENCING, HAND-OFF

- Lands on the current room, in any order among the three,
  independent of F16/F18 (audit §9 sequencing). F17's sweep
  may absorb §1.3's token move; the values must survive it
  byte-identical.
- Not mine, not touched, not silently fixed: the eyebrow
  emboss (L601, §0.5), the night glyph hex (L631, F17's),
  the vertigo's modifier placement (F15's — §2.2 only binds
  the surface block's inseparability), the off-Deck radii
  (§3.5, Rost's routing), F12's utility ink (the *other*
  finisher's cluster — my §4.2 check 5 borrows its hierarchy,
  it does not set its values).
- Builder's whole edit surface for this cluster:
  `TactileDeckScreen.kt` (hero Box, card modifier chain, two
  caps) + `Color.kt` (two new token vals). If a build of this
  cluster touches a third file, it has left the cluster.

## §6 · FINISHER'S NEXT (unassigned, offered)
Two things I could take unasked if Rost seats me again:
(a) the same registration audit run over PARK's drawer radii
(§3.5's worst offender — a 16dp sheet corner inside a two-radii
app), priced S on paper; (b) Schein's Lamp Test written as a
standing three-frame checklist for the *whole* Deck, so F12's
and my hierarchy checks stop living in separate files. Neither
is started; both wait for a seat.

— FINISHER-1, Rost's cell, issue #6. Report to Rost.
DECLARED, uncompiled, uncommitted.
