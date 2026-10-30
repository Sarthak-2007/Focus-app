# 13 — Taste, PLAN WAVE — Composer Keys (duration caps + ADD, Tasks)

Owner: Taste (Improvers). Boundary: the bank as an object — well, caps, skirts,
sounds. The field and *when* the bank appears (focus only) are Fach's.
Plan only — no code shipped with this file.

---

## CURRENT — verified in code today, not from wave-1 memory

Source: `FocusSequencerScreen.kt`, `KeyCap` (file end) + its call site in
Section 2; `inkControlFill()` / `onInkControlFill()` in
`MonolithCustomComponents.kt` 439–451; tokens in `theme/Color.kt` as rebuilt
by the night-staircase pass. Renders `shots/03_tasks.png` /
`shots/night/n03_tasks.png` agree with the code below but predate the token
rebuild — where render and code disagree, code wins.

**The wave-1 ★ question, answered: YES, still true.** The critic pass was
right. ADD is called as:

```
KeyCap(label = "ADD", selected = true, onClick = { submitTask() },
       modifier = Modifier.weight(1.2f))
```

`KeyCap` has exactly two visual branches, both driven by `selected` alone:

| Property | `selected = true` branch (= a selected duration = ADD, always) | `selected = false` |
|---|---|---|
| Fill | `inkControlFill()` — day `scheme.onBackground` `#2C2A29` (the *text* token, not ink `primary` `#252323`); night hard-coded `#4A423C` | `scheme.surface` = ground itself: day `#E4DFDA`, night `#171412` |
| Border | 1dp, same colour as fill — i.e. no edge | 1dp `outlineVariant` (day `#75615B5A`, night `#758F8888`) |
| Label | Anonymous Pro Bold 12sp, `onInkControlFill()` day `#FBF1EC` / night `#FFE4CF` | Anonymous Pro Bold 12sp, `onSurfaceVariant` day `#5F5856` / night `#776A60` |

So ADD is **pixel-identical to a selected duration cap** in fill, border,
label colour, typeface, size, height (36dp) and radius (9dp). The only
differences in the whole row are the word printed on it and its width
(weight 1.2f vs 1f ≈ 70dp vs 58dp at 360dp screen width). Four choices and
one verb, wearing one costume, two of them lit at all times. That is the
core defect and it is un-fixed.

Everything else wave-1 reported also still holds in code:

- Height **36dp** — under the 44–48dp touch floor. Radius **9dp** — matches
  neither the key freeze (8dp) nor panels (16dp) now in force.
- Unselected caps have **no fill of their own** (fill == background token in
  both themes): an outline and a number floating on the ground.
- Selection is carried by **fill value alone** — no depth, position, marker
  or type change. At night the label barely helps: unselected label is now
  the muted `#776A60` (the staircase rebuild fixed wave-1's near-identical
  creams — that sub-defect is gone), but state still rests on one channel.
- **No press state at all**: plain `.clickable {}`, no indication, no travel.
- **No haptic, no sound on duration select** (`onClick = { newTaskMinutes =
  mins }`, nothing else). ADD's only feedback is inherited: `submitTask()`
  fires `playMechanicalLatch(true)` (760Hz, 16ms, gain 0.54, chord 1.2;
  haptic waveform `[0, 11ms@215, 26ms gap, 9ms@150]`).
- **ADD with an empty title is a dead key in ink costume**: `submitTask()`
  guards on `clean.isNotEmpty()` and otherwise returns silently. Full
  promise, zero response.
- Five equal 8dp gaps: the grid asserts 5 peers; the semantics are 4 + 1.

What changed since wave-1 (context the ladder is priced against): night
ground is now `#171412`, recess `#100E0C`, raised `#362C27`, control cap
`#4A423C`; day `onSurfaceVariant` is `#5F5856`; radius freeze is 8dp keys /
16dp panels; the bank appears **only while the field is focused** (Fach's
collapse) — so the bank is now a transient instrument, not furniture, and
its entrance is part of its objecthood (Rung 4).

---

## LADDER — 4 rungs, each independently shippable, each leaves the row truthful

Technique vocabulary used throughout: EasingA `cubic-bezier(0.2, 0, 0, 1)`,
EasingB `cubic-bezier(0.05, 0.7, 0.1, 1)`, MechanicalOvershoot(1.25) — all
already in `MonolithCurves.kt`. No drop shadows anywhere; depth is tone
steps, hairlines and skirt height only. Fonts are his three, as the code
now aliases them: durations in **Anonymous Pro** (numbers), ADD's word in
**Atkinson Hyperlegible** Bold (the interface face; code alias
`PlusJakartaSansFamily`), ADD's "+" glyph in Anonymous Pro so it sits at
numeral cap-height.

### Rung 1 — TRUTH: the verb stops impersonating a value (contains the ★)

No geometry change (36dp, 9dp, current weights/gaps all stay). Semantics only.

- **Selected duration leaves the ink family** (the ★, priced below). New
  selected-duration spec inside the existing `KeyCap` branches:
  - Day: fill flat `surfaceBright` `#FAF6F0`, label `#252323` (true ink),
    **2dp marker line** under the label, `#252323`, width = label width.
  - Night: fill flat recess `#100E0C`, label cream `#FFE4CF`, 2dp marker
    `#FFE4CF`.
  - Selection now reads three ways at once: tonal inversion vs neighbours,
    dark-on-light (day) / sunk-below-ground (night), and a printed marker.
    It survives glare and grayscale; fill is no longer the only witness.
- **ADD becomes the only ink key** — and true ink: `inkControlFill()`'s day
  branch returns `scheme.primary` `#252323` instead of `onBackground`
  `#2C2A29` (one-line helper fix; every other ink control in the app gets
  the same correction, which is a bonus, not a side effect to hide).
  Night ADD stays `#4A423C` / label `#FFE4CF` — the cap tone of the night
  staircase, already distinct from the selected cap's recess `#100E0C`.
- **ADD gets its own register:** label `"+ ADD"` — "+" Anonymous Pro Bold
  14sp, "ADD" Atkinson Hyperlegible Bold 12sp, letterSpacing 1.2sp.
  Durations split their label: numeral Anonymous Pro Bold 12sp + "m"
  9.5sp Bold, baseline-aligned. A value in the number face, a verb in the
  word face; the eye sorts them before it reads them.
- **ADD-idle state (title empty):** fill drops to `surfaceVariant` —
  day `#D8D1CB`, night raised `#362C27` — label to `onSurfaceVariant`
  (day `#5F5856`, night `#9C8E84` secondary text). ADD wears ink only when
  there is something to add; typing arms it (colour change instant, no
  animation — arming is a fact, not an event). The dead-key lie dies here:
  an idle ADD now *looks* idle.
- **Semantics:** durations in one `selectableGroup`, each `Role.RadioButton`;
  ADD `Role.Button`, contentDescription `"Add task, {minutes} minutes"`.
- *Motion:* none. *Haptic:* none added. *Sound:* unchanged (silent select,
  latch on submit). This rung is purely "stop lying" — and it is complete
  without Rungs 2–4.

### Rung 2 — HAND: press travel, skirt, and the Paperwork-That-Plays sound set

The caps start behaving like keys, and selection gets a voice — quietly.

- **Geometry:** height 36 → **44dp** (real touch target); radius 9 → **8dp**
  (the key freeze). Gap between 90m and ADD 8 → **12dp**; intra-duration
  gaps stay 8dp. Proximity now groups 4 + 1 before colour speaks.
- **Dome recipe (every cap, replaces flat fill + uniform border):** vertical
  2-stop gradient + 1dp top catch-light + bottom **skirt** (the key's side
  wall; skirt height = remaining travel).
  - Day unselected: `#FAF6F0 → #E4DFDA`; catch `#FFFFFF` @55%; skirt 2dp
    `#CFC6BE` ◆new (one step below `surfaceVariant`).
  - Night unselected: `#362C27 → #2B211C` ◆new bottom stop; catch
    `#FFE4CF` @14%; skirt 2dp `#100E0C`.
  - Selected (from Rung 1) stays flat and skirtless — it is the cap already
    at the bottom of its travel; dome vs flat is now a fourth state channel.
  - ADD armed: gradient day `#2C2A29 → #252323`, night `#57493F` ◆new →
    `#4A423C`; catch `onInk` @22% day / @18% night; skirt 3dp `#161311`
    (day `TokenBlockBarSurface`-family dark; night `NightBlockBarSurface`).
    ADD idle keeps Rung 1's flat dim fill, skirt 2dp in the cap's skirt tone.
- **Motion — press is the selection animation:** press-in **70ms, EasingA**:
  travel (skirt height) → 0, label translates down by the same amount
  (2dp durations, 3dp ADD). Release of a non-committing press **180ms,
  EasingB + MechanicalOvershoot(1.25)** on travel only, overshoot capped at
  +0.5dp past flush. Selecting a duration: the pressed cap simply *stays*
  down (it becomes the flat selected cap); the outgoing cap releases on the
  release spec. No crossfade over 120ms anywhere. `onClick` fires on press
  commit, never gated on animation end.
- **Haptic:** duration select = one-shot **4ms @ amplitude 80** (a detent
  tick). Re-tapping the already-selected cap: **0ms, no motion, no sound** —
  nothing changed, nothing is faked. ADD idle press = one-shot **6ms @ 45**
  (a dull thud — travel with nothing behind it) + a focus request to the
  field (the remedy, not a scolding; seam with Fach, see Conflicts).
  ADD armed keeps its heavy two-stage feel, re-homed in the punch below.
- **Sound — fixed SETS, tabled constants, never a formula over minutes:**
  - Duration caps = wooden bars, one fixed pitch per cap, bigger bar =
    lower note: **15m → 784Hz, 25m → 698Hz, 45m → 587Hz, 90m → 494Hz**;
    each 14ms, gain 0.24, chordRatio 1.0 (dry single-bar transient; new
    engine method `playComposerBar(hz)` taking the value from a 4-entry
    constant table keyed by the cap, never computed). Low gain on purpose:
    selection is frequent and reversible; the bars are a murmur under the
    haptic tick, not a xylophone performance.
  - ADD armed = **card punch**, replacing the latch *for this action*
    (new engine method `playCardPunch()`): 210Hz fundamental, 30ms,
    gain 0.58, chordRatio 0.62 — a low paper thock, timbrally unrelated to
    the bars, so the ear files "chose" and "committed" in different
    families without learning anything. Haptic paired: waveform
    `[0, 10ms@230, 18ms gap, 6ms@140]`.
  - ADD idle: silent (the 6ms thud carries it).

### Rung 3 — WELL: the bank becomes one machined object (wave-1 peak, retokened)

- **Key well (durations only):** one recessed container, height **52dp**,
  radius **16dp** (panel freeze), fill one step below ground — day
  `#D8D1CB`, night recess `#100E0C`. Recess recipe: 1dp inner top line
  (day `#615B5A` @75%, night `#000000` @60%), 1dp inner bottom catch line
  (day `#FAF6F0` @60%, night `#362C27`). Padding 4dp on all sides.
- **Caps inside:** height **44dp**, radius **8dp**, pitch **4dp** (a key
  grid's tight pitch, not button spacing), equal weights. Gradient, catch
  and skirt exactly as Rung 2 — the dome was designed for this well.
- **Selected cap = sunk flush with the well floor:** fill *equals* the well
  fill (day `#D8D1CB`, night `#100E0C`), skirt 0, catch gone, label at full
  strength (day `#252323`, night `#FFE4CF`), 2dp marker under the numeral
  in the label colour. Depth says "latched down"; marker and label confirm.
  State is position first, colour second — in that order.
- **ADD, outside the well:** gap well → ADD **12dp**; ADD height **52dp**
  (matches the well — the row is one 52dp-tall instrument), radius **8dp**,
  weight 1.2f retained, dome/skirt/punch exactly as Rung 2. Total row
  height 36 → 52dp; gap to the field above 10 → 8dp (field + bank read as
  one assembly; the section gap below does the separating).
- *Motion/haptic/sound:* inherited unchanged from Rung 2 — press-in 70ms
  EasingA, release 180ms EasingB + overshoot 1.25 (≤ +0.5dp), 4ms @80 tick,
  bar set, punch. One addition: ADD successful submit holds at bottom
  **90ms** before releasing on the release spec — the punch's second stage
  lands while the key is still down. The queue row appearing below is the
  confirmation; no flash, no ripple.

### Rung 4 — TRAY: the bank's entrance is part of the object

The bank now exists only while the field is focused (Fach owns *when*).
This rung owns *how it arrives* — past wave-1, which assumed a static row.

- **Entrance:** well + ADD rise together as one tray: translateY +8dp → 0
  and alpha 0 → 1, **260ms, EasingB**, no overshoot (a tray slides into its
  slot; it does not bounce). Skirts render at full height from frame 1 —
  the tray arrives already built. **Hit targets are live at final geometry
  from t = 0**; only the drawing travels. A tap at t = 40ms lands exactly
  where the cap will rest. The entrance can never tax a task entry.
- **Exit (field blurs / task submitted-and-dismissed):** reverse, **180ms,
  EasingA**, translateY 0 → +6dp, alpha → 0, then unmount. Shorter and
  quieter than the entrance — leaving is not an event.
- **One light pass on entrance only:** as the tray seats, the caps'
  catch-lights brighten in sequence left → right, ADD last: each +25%
  alpha over its Rung 2 value for 90ms, staggered 28ms, total sweep
  ≤ 230ms, inside the 260ms entrance. Light fixed in meaning: it encodes
  nothing (no readiness, no state) — it is the tray catching the room light
  as it slides in, and it never repeats while the bank stays open.
- **Reduced motion:** with system animator scale = 0, entrance/exit become
  instant alpha swaps (0ms travel, 120ms alpha, EasingA) and the light
  pass is skipped entirely. Press travel collapses to a 0ms state swap;
  haptics and sounds are untouched (they are the feedback then).
- *Haptic/sound on entrance/exit:* none. The bank appearing is the field's
  event, not the bank's act; a sound here would bill every task entry for
  opening the drawer.

---

## ★ — priced

**★ Rung 1's first bullet alone: the selected duration's fill leaves the ink
family** — day selected → flat `surfaceBright` `#FAF6F0` + ink label + 2dp
ink marker; night selected → flat recess `#100E0C` + cream label + 2dp
cream marker. ADD's call site, fill and helper are not touched (the
`inkControlFill()` day-token correction rides along as a separate one-line
fix, not counted in the price).

Price: two branch values + one 2dp `Box` inside the existing `KeyCap`;
no new composable, no geometry, no motion, no new tokens (every value
above is an existing token in the rebuilt palette). One sitting, most of
it verification: both themes, glare/grayscale check, TalkBack pass.
Kills the element's core defect outright — after it, the row has exactly
one dark key and it is the verb — and every later rung assumes it: a domed
or welled impersonator would still be an impersonator.

## PEAK (≤8 lines)

Four domed caps sit in one recessed well; the selected cap is sunk flush
with the well floor, marker under its numeral — chosen by depth, not colour.
Beside the well, taller than any cap and standing alone, one ink key: + ADD.
Caps murmur on a fixed set of wooden bars (784/698/587/494Hz); ADD punches
card (210Hz thock) — choosing and committing never sound alike.
ADD stays ink, never orange: orange is START, the act that leaves this
screen and begins HOLD; ADD only enqueues, in bursts, reversibly.
An orange ADD would train the eye to ignore the one colour that must mean
"session starts," and would shout over the field during focused entry.
Ink is the action colour *inside* a screen; orange is the exit. ADD is ink.

## STANDARDS CHECK

- **ARM ≤10s — adding a task costs: focus field (bank appears, Fach) →
  type → [optional: one duration tap] → ADD.** My keys add **zero taps and
  zero gated time** over today: the 25m default persists across adds, so a
  repeat entry is field + ADD only; press animations (70ms down) never
  gate `onClick`; the Rung 4 entrance (260ms) is visual-only with hit
  targets live at final geometry from t = 0. Key-side contribution to the
  arm path: ~0.0s. Nothing in the bank starts, previews or delays a session.
- **HOLD absolute:** the bank lives on Tasks, outside any session; no rung
  adds a mid-session surface, prompt or decision. ADD enqueues only — it
  cannot arm, and its ink (not orange) costume says so.
- **TRUTH once:** selected is unmistakable on three independent channels —
  sunk depth (skirt 0, flush with well floor), the 2dp printed marker, and
  full-strength label — readable in grayscale and peripheral vision. ADD
  can never be mistaken for a duration: different object (standalone, not
  in the well), different typeface register, a "+" glyph, the row's only
  ink fill, and a punch where caps have bars. A fact (which duration) is
  rendered once, by the caps; see Conflicts for the one duplicate risk,
  which is Fach's echo, not the bank's.
- **His bindings respected:** three fonts only (Anonymous Pro / Atkinson
  Hyperlegible / Sora family untouched elsewhere); warm palette, no green
  anywhere in any table above; night values taken from the rebuilt
  staircase (recess `#100E0C`, raised `#362C27`, cap `#4A423C`) — no wave-1
  night hex survives.

## CONFLICTS (+ mail sent)

1. **Fach (composer field) — submit sound must be one sound.** IME `Done`
   and ADD both call `submitTask()`; today both fire the latch. Rung 2
   re-homes ADD to the card punch. If the latch stays on the `Done` path,
   one action owns two sounds — a lie of form. My position: the punch
   becomes *the* submit sound on both paths (it lives in `submitTask()`,
   not in `KeyCap`). Mailed Fach.
2. **Fach — his wave-1 minutes echo duplicates my fact.** His +2 echoes the
   selected minutes inside the field (`· 25m`). With the bank visible on
   focus, that renders the duration twice in one glance. State grammar
   allows margin readouts, but this one sits *inside* another control.
   My position: the echo dies, or survives only as ADD's contentDescription.
   His call on his element; flagged, not decided here. Mailed Fach.
3. **Fach — idle-ADD remedy needs his field.** Rung 2's idle press requests
   focus on the field (his `FocusRequester`). One-line seam on his side;
   if he declines, the idle press degrades to thud-only and stays truthful.
   Mailed Fach.
4. **Alba (MaxImpl composer) — top step mailed** as ordered: the ★ (selected
   leaves ink; ADD sole ink key), with the Rung 2 punch flagged as the one
   piece her build must not implement as a per-key sound (it belongs to
   `submitTask()`). Mailed Alba.
5. **Radius freeze vs wave-1:** wave-1 specced 12dp caps / 10dp in the well
   under the old two-radii rule. The synthesis freeze (8dp keys / 16dp
   panels) supersedes it; this plan follows the freeze throughout.
7. **Fach fork — ADD's form is undecided at source level (letter received
   05:24 IST, after this plan was drafted; replied same thread).** Fach's
   collapse plan (plan/improvers/12-fach-composer-field.md) removes ADD
   from the bank entirely: ADD becomes TEXT in his sticky line in both
   collapsed and expanded states, and the bank is a pure 4-value radio
   group in a 52dp slot he frames. Synthesis item 28 supports both
   readings — its main clause says "ADD as text," its bank parenthetical
   says "ADD the only ink key" — and my brief assigns the ADD key to me.
   **Escalated to Ilya; not resolved here.** What is settled regardless:
   (a) Fach's focus guarantee is accepted outright — caps are
   non-focusable and never call `clearFocus`; a focus drop mid-entry
   collapsing the bank is a failure this plan specs against; (b) the
   submit punch lives in `submitTask()` under either ruling; (c) if the
   fork resolves Fach's way, Rungs 1–2 apply to the caps unchanged, the
   ★ gets cleaner (no ink verb remains in the bank to impersonate), and
   the ADD cap specs convert: his ADD text keeps ink-when-armed /
   dim-when-empty, and Rungs 3–4 re-base onto his 52dp slot dimensions.
   My position, argued to Fach: a text word cannot honestly carry a punch
   sound or travel, the verb belongs beside its values in expanded state,
   and collapsed ADD-text can never submit (unfocused = empty), so it
   renders a verb that isn't there.
6. **Not mine, deliberately untouched:** when the bank appears (Fach);
   queue-row landing after ADD (Zeile); the duration *set* {15, 25, 45, 90}
   itself — four bars is the instrument's range; a fifth/custom value is a
   data decision, not a key-design one.
