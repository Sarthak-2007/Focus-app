# Issue #16 — The Pill: LIGHT Specification

**Author:** Feel-2 · Light (issue-16 cell)
**Scope:** Colour, contrast, glow/shadow, and glance-legibility of the floating
2-minute-peek countdown pill over arbitrary app backgrounds (bright and dark).
**Sources read:**
- `builds/pill/Focus-app/app/src/main/java/com/example/service/PillTheme.kt`
- `builds/pill/Focus-app/app/src/main/java/com/example/service/PillFace.kt`
- `builds/pill/Focus-app/app/src/main/java/com/example/service/PillController.kt` (the actual renderer)
- `builds/pill/Focus-app/app/src/main/java/com/example/ui/theme/Color.kt` (trunk tokens, for reference)

**One-line verdict:** The figures are readable everywhere, but the pill's
*body* is not — over a black video the pill has no silhouette at all
(1.18:1), it carries zero shadow/glow, and its states (running / last
seconds / ending) differ **only in digits**, never in light. Fix is ~30
lines, priced in §6.

All contrast ratios below are WCAG relative-luminance ratios, computed
from the exact hex values in code.

---

## 1. What exists today (file:line)

### 1.1 The palette — `PillTheme.kt:36–42` (`PillPalettes.Default`)

| Role | Value | Decoded |
|---|---|---|
| Background | `0xE61F1A16` | `#1F1A16` warm near-black, **alpha 0xE6 = 90.2%** — 9.8% of the app beneath bleeds through |
| Border | `0xFF4A4038` | `#4A4038` dark warm taupe, opaque, drawn at **1dp** (`PillController.kt:186`, `buildWindow` stroke) |
| Figures (time) | `0xFFFFE4CF` | `#FFE4CF` cream, 16sp (`PillController.kt` `buildWindow`, time `TextView`) |
| Label (SESSION / BREAK / app name) | `0xFFB9ACA0` | `#B9ACA0` muted taupe-cream, **9sp** |
| Hand-back ✕ (Peek only) | `0xFFF47442` | `#F47442` hot orange, 15sp |

Shape: fully-round capsule (`cornerRadius = dp(999)`, `PillController.kt`
`buildWindow`), padding 14/8/12/8dp, window `PixelFormat.TRANSLUCENT`,
born at the right edge at 22% screen height, draggable with edge-snap.

### 1.2 Shadow / elevation / glow — **none exists**

Grepped `PillController.kt` and `PillTheme.kt` end to end:

- No `View.elevation`, no `outlineProvider`, no `translationZ`.
- No `Paint.setShadowLayer` / `TextView.setShadowLayer` on any text.
- The `GradientDrawable` background has colour + 1dp stroke only — no
  shadow, no inner glow, no gradient (it is flat despite the class name).

The pill is a flat cut-out. Its *only* separation mechanism from the app
beneath is (a) its own dark fill and (b) the 1dp border — and §2 shows
both fail on the dark side.

### 1.3 The palette is state-blind

`PillController.render()` (`PillController.kt:171–214`) reads
`PillTheme.palette` once and applies the **same four colours to every
face**. What changes between faces:

| Face | Label text | Figures | ✕ | Light change |
|---|---|---|---|---|
| `Session` | `SESSION · BLOCK n/m` / `SESSION · ELAPSED` | remaining / `+elapsed` | GONE | **none** |
| `Break` | `BREAK` | remaining | GONE | **none** |
| `Peek` | peeked app name, uppercased | remaining | VISIBLE | **none** |

`PillFace.kt` carries `remainingSeconds` on every face and the
`renderKey` changes every second — the machinery *could* switch light
on thresholds at zero extra cost — but nothing in the palette, the
derivation, or the renderer ever looks at the number for colour. A peek
at 01:59 and a peek at 00:03 are the **same object in the same light**;
only the digits move. The derivation (`PillFace.kt`, `PillDerivation`)
is pure time math and correctly stays that way — the state-light belongs
in the renderer/palette layer, not the derivation.

### 1.4 Route B note

The shade notification (`MonolithShieldService.kt`) prints the same
peek time as system-styled notification text. It has no light of its
own and inherits the shade's — out of this spec's scope, noted so
nobody mistakes it for a second pill face.

---

## 2. Contrast audit — does it survive white and black?

The background's 90.2% alpha means the *effective* fill depends on
what's underneath. Effective fill = `#1F1A16` blended at 0.902 over the
underlay:

| Underlay | Effective fill | Figures `#FFE4CF` | Label `#B9ACA0` | ✕ `#F47442` | **Pill silhouette vs underlay** |
|---|---|---|---|---|---|
| White page `#FFFFFF` | `#35302D` | **10.70:1** ✅ | 5.88:1 ⚠️ | 4.60:1 ⚠️ | **13.03:1** ✅ |
| Black video `#000000` | `#1C1714` | **14.59:1** ✅ | 8.01:1 ✅ | 6.28:1 ✅ | **1.18:1** ❌ |
| Mid grey `#808080` | `#292420` | 12.61:1 ✅ | 6.93:1 ✅ | 5.43:1 ✅ | 3.89:1 ⚠️ |
| Bright yellow `#FFEB3B` | `#352E1A` | 11.08:1 ✅ | 6.08:1 ✅ | 4.77:1 ⚠️ | 11.05:1 ✅ |

### Verdicts

- **Over a white page: READABLE, silhouette strong.** The dark fill
  does all the work; worst element is the ✕ at 4.60:1 (passes 4.5:1
  for large glyphs, no margin) and the 9sp label at 5.88:1 — numerically
  passing, practically weak at 9sp in peripheral glance.
- **Over a black video: FIGURES READABLE, PILL INVISIBLE.** Text floats
  at 14.59:1, but the capsule's edge against the video is **1.18:1** —
  below any detection threshold. The user sees disembodied digits, not
  an object; the ✕'s tap target has no visible boundary, and the drag
  affordance (grab *the pill*) has nothing to grab. This is the pill's
  single worst light defect: its home case is peeking into video/social
  apps, which are disproportionately dark.
- **The border does no work anywhere.** `#4A4038` vs the fill is
  1.29:1 (over white) to 1.76:1 (over black) — it is a decoration line
  *inside* the pill's own luminance band, not a separator. Against the
  underlay it never gets a chance: the fill's edge reaches the underlay
  first, and on dark underlays both are dark.
- **Busy/colourful backgrounds are the unmeasured middle.** Over mid
  grey the silhouette is only 3.89:1; over photographic content with
  dark patches the edge will locally vanish patch-by-patch. A floating
  object over *arbitrary* content cannot rely on fill-vs-underlay luck
  at all — it needs a self-carried separation system (§5).

---

## 3. Defect register

| # | Defect | Evidence | Severity |
|---|---|---|---|
| D1 | **Dark-on-dark silhouette loss** — pill body undetectable over black/dark apps | 1.18:1 eff. fill vs `#000000` (§2) | **Critical** — fails TELL-THE-TRUTH: the object must read as an object |
| D2 | **Alpha bleed (0xE6)** — 9.8% underlay bleed makes contrast underlay-dependent (figures swing 10.7→14.6:1) and lets saturated underlays tint the fill | `PillTheme.kt:37` | High |
| D3 | **⚠️ GREEN-ADJACENT VIA BLEED (standing law flag):** over a bright cyan/teal video frame the blended fill computes to `#1C2E2D` — **G (46) > R (28)**, a green-dominant surface the palette never chose. The palette itself contains no green-dominant value (checked all five: G < max(R,B) in every one ✅), but the alpha manufactures one from the underlay. Removing the alpha (Fix F1) kills this by construction | blend math, §2 method | High — law-relevant |
| D4 | **No shadow, elevation, or glow of any kind** — flat cut-out; on light-midtone underlays there is no depth cue at all | grep: zero hits in renderer (§1.2) | High |
| D5 | **States are light-identical** — running / last-seconds / ending differ only in digits (+ label word, + ✕ presence on Peek). No glanceable urgency signal; the last 10 seconds of a 2-minute fuse look exactly like the first 10 | `PillController.kt:171–214` (§1.3) | High — the peek fuse is the pill's reason to exist |
| D6 | **Label tier too small for its contrast** — 9sp at worst-case 5.88:1; the label is what distinguishes SESSION vs BREAK vs *which app you're peeking* | `PillController.kt` `buildWindow` | Medium |
| D7 | **Off-standing-palette accent** — ✕ uses trunk night accent `#F47442`; the standing palette's amber is `#E47E35` (6.02:1 on the fill vs current 6.10:1 — parity). Not a contrast defect; a coherence one, fixable for one constant | `PillTheme.kt:41` vs standing palette | Low |

---

## 4. State legibility today — the glance test

Cover the digits and ask what the light alone says:

- **Session vs Break vs Peek:** indistinguishable by light. Peek is
  betrayed only by the ✕ appearing and the label naming an app.
- **Peek 01:30 vs Peek 00:05:** indistinguishable by light.
- **Anything ending:** nothing ends *in light* — the pill simply
  vanishes at zero (derivation returns the next face / `Absent`).
  A disappearance is an event, but an unannounced one.

For a 2-minute fuse whose entire job is "you are on borrowed time,"
the light must carry the fuse, not just print it.

---

## 5. The finished light — specification

Principles: (1) the pill carries its own separation — never borrowed
from the underlay; (2) one fill, fully opaque, so every contrast number
below is a constant, not a range; (3) state is readable with the digits
covered; (4) every value is either an existing trunk token or from the
standing palette (`#F04FCB, #493EA4, #E2BC39, #C84481, #E47E35,
#377ED0, #34323B`); (5) **no green or near-green anywhere** — every
proposed value verified G < max(R,B).

### F1 — Opaque fill (kills D2, D3)

- `backgroundArgb`: `0xE61F1A16` → **`0xFF1F1A16`**. Same hue, alpha
  FF. The "app ghosts through at the edges" idea in the
  `PillTheme.kt:20–21` comment is the defect, not a feature, on a
  floating object: ghosting is what the *border + shadow* are for.
- Result: figures become a constant **14.17:1**, label (with F3)
  **9.69:1**, on every underlay, forever.

### F2 — Self-carried separation: midtone keyline + real shadow (kills D1, D4)

A single dark halo cannot work (dark halo dies on black; light halo
dies on white). The separator must sit at **mid luminance**, where it
is visible against both poles:

- **Keyline:** border `0xFF4A4038` @1dp → **`0xFF8A7A68` @1.5dp**
  (warm taupe, G 122 < R 138 ✅). Its numbers: **4.16:1 vs the fill,
  4.15:1 vs pure white, 5.06:1 vs pure black** — the one stroke reads
  against both extremes and every midtone between, because it is
  never the same darkness as anything it touches.
- **Shadow:** set `elevation = 10dp` on the root view in
  `buildWindow` (overlay windows on API 26+ render the framework
  shadow; the pill's window type is already `TYPE_APPLICATION_OVERLAY`
  on O+). The shadow is the depth cue on light and mid backgrounds —
  it darkens the *underlay*, so even a same-value patch of photo
  content gets locally separated. On black the shadow is invisible
  and the keyline carries alone — which is exactly the division of
  labour: **shadow for the light side, keyline for the dark side.**
- No glow on the base state. Glow is spent on the ending state (F4),
  where it means something; an always-on glow is just noise.

### F3 — Text tiers (kills D6, D7)

- Figures: keep `#FFE4CF` @16sp — 14.17:1 post-F1, nothing to fix.
- Label: `#B9ACA0` → **`#CFC0AE`** (9.69:1 on the fill, G 192 < R 207
  ✅) and 9sp → **10sp**. The label carries the face's identity
  (SESSION / BREAK / app name); it must survive a peripheral glance.
- ✕ accent: `#F47442` → **`#E47E35`** (standing-palette amber,
  6.02:1 on the fill — parity with today's 6.10:1, coherence gained
  for free). Applies to base state; ending state overrides per F4.

### F4 — State light (kills D5)

Thresholds ride the existing per-second `renderKey` churn — the
renderer already re-runs every second, so these cost no timers, no
derivation change (`PillFace.kt` untouched). Scope: **Peek** (the
fuse) in full; Session/Break get the final-seconds tier only, so a
session's end is also glanceable.

| State | Condition | Fill | Figures | Label | Keyline | ✕ |
|---|---|---|---|---|---|---|
| **Running** | Peek > 15s; Session/Break > 15s | `#1F1A16` | `#FFE4CF` | `#CFC0AE` | `#8A7A68` | `#E47E35` |
| **Last seconds** | 15s ≥ remaining > 5s | `#1F1A16` (unchanged) | **`#E2BC39`** gold — 9.43:1 | **`#E2BC39`** | **`#E2BC39`** @1.5dp | `#E47E35` |
| **Ending** | remaining ≤ 5s | **`#493EA4`** violet flood — cream on it 6.88:1 | `#FFE4CF` | `#FFE4CF` | **`#E2BC39`** — 4.58:1 vs the violet fill | `#FFE4CF` |

Reading the table as a user: gold creeping into the figures means
*the fuse is short*; the whole pill flooding violet means *it's over
now, hand it back*. Both read with the digits covered, from across
the room, over any background (violet fill vs white 8.26:1-class
silhouette, vs black carried by the gold keyline at 9.4:1-class;
shadow still active underneath).

Notes / rejected alternatives:

- **No red flood for ending.** The trunk killed its red alert
  (`Color.kt`, `TokenAlert` history) in favour of the single orange
  hue family; more importantly the standing palette offers violet
  `#493EA4`, which floods unmistakably without reading as an error
  dialog. Gold-on-violet and cream-on-violet both verified above.
- **No pink `#F04FCB` figures** (5.49:1 on the base fill — passes, but
  strictly worse than gold's 9.43:1 for the tier whose whole job is
  urgency-at-a-glance). Pink stays available to the look lanes.
- **No blue `#377ED0`** anywhere in state light (4.15:1 on the fill —
  the weakest standing-palette value on this ground).
- **Optional, priced separately (F4+): a 5-second ending pulse** —
  keyline alpha oscillating 255↔140 at ~1.6Hz during Ending only.
  It is the single strongest "ending now" cue, but it is *motion*,
  and motion belongs to the cell's Feel/motion judgement, not light's.
  Specified here so the colour side is ready if Pastille takes it;
  not part of the base price.

### F5 — What does NOT change

- `PillFace.kt` / `PillDerivation`: untouched. State thresholds are a
  *rendering* read of `remainingSeconds`, which every face already
  carries.
- Geometry, drag, snap, hand-back, window lifecycle: untouched.
- `PillTheme.palette` remains the single holder — F4 is three
  palettes (Running / LastSeconds / Ending) in `PillPalettes` plus a
  threshold pick in the renderer, so a look lane can still redress
  all three in one place.

---

## 6. Price

| Item | Change | Size |
|---|---|---|
| F1 opaque fill | 1 constant in `PillTheme.kt` (+ comment correction) | 2 lines |
| F2 keyline | border constant + stroke width 1dp→1.5dp in `buildWindow` | 2 lines |
| F2 shadow | `elevation` on root view in `buildWindow` | 1–3 lines |
| F3 label/accent | 2 constants + 1 text-size | 3 lines |
| F4 state palettes | +2 `PillPalette` entries in `PillPalettes`; threshold branch in `render()` selecting palette by face + `remainingSeconds` before the existing colour application | ~25 lines |
| **Total** | **2 files touched (`PillTheme.kt`, `PillController.kt`), 0 new files, 0 schema, 0 machinery** | **~35 lines** |
| F4+ pulse (optional) | `ValueAnimator` on stroke alpha, Ending only, cancelled on face change via existing `removeWindow`/re-render path | +~20 lines |

Risks: (a) framework shadow on overlay windows varies by OEM skin —
if a device swallows it, the keyline still carries both poles alone,
so the spec degrades gracefully, never below today's state;
(b) the gold/violet state colours are new surface for the seven look
lanes to redress — accepted by design, since all three palettes live
in `PillPalettes` next to `Default`.

## 7. Acceptance checks (for whoever verifies on-device)

1. Peek over a full-screen black video: capsule edge visible at arm's
   length; ✕ target bounded. (D1)
2. Peek over a white page and over a bright cyan/teal video frame:
   fill reads warm near-black, **no teal/green cast**. (D2/D3)
3. Peek at 00:16 vs 00:14 with digits covered: the 00:14 pill is
   visibly gold-lit. At 00:05: visibly violet. (D5)
4. Session and Break pills over both poles: unchanged base light,
   gold tier in their final 15s.
5. Label (app name / SESSION / BREAK) readable in a peripheral glance
   at arm's length over white and over black. (D6)

— Feel-2 · Light (issue-16 cell)
