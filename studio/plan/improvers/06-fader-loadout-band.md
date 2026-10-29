# PLAN — Fader, Improvers · 06 · The Loadout Selector Band (Shield §1)

Owner: Fader (Improvers). Coordinator: Ilya. Neighbour: Marko (MaxImpl, loadout).
Element: `RombiKnobView`, `BlockitSuitcaseAndSwitchesViews.kt:172–300`, hosted
84dp in `ShieldMatrixScreen.kt:129–163`. Plan only — no code written.
Wave-1 ladder (`improvers/06-loadout-band.md`) is the floor. This plan starts
from what has actually shipped since, verified in code today — two of wave-1's
premises are dead and one of its fixes mutated on the way in.

---

## CURRENT — verified in code, Oct 29

**The ★ shipped — in intent, not in letters.** Wave-1 ★: marker →
constant cream `#FBF1EC` both modes. Code today (`:266–269`) draws the marker
with `tokOnControlFill()`, whose definition (`BlockitCoreViews.kt:132`) is
day `LIGHT_ON_PRIMARY #FBF1EC` / night `NIGHT_PRIMARY #FFE4CF`. Day is
pixel-identical to the ★. Night is **not** the pinned constant: it is the
night staircase's cream, which the staircase reserves **text-only**. The
functional repair is real — night contrast went from `#1B110D`-on-`#4A423C`
(≈2:1, a dark slit) to cream-on-`#4A423C` (≈8:1) — but the marker now spends
a text tone on a control, and any future edit to `NIGHT_PRIMARY` silently
re-colours the band's one state mark. Rung 1 re-pins it.

**The sequencing bug still lives — unchanged, line for line.** In
`onTouchEvent` (`:288–291`) `selectedLevel = nearest` runs at `ACTION_UP`;
the setter (`:177–184`) assigns `field` immediately and only then starts
`animateCapTo`. `onDraw` keys numeral colour and label colour to
`selectedLevel`, and the host's `onLevelChanged` → `onApplySmartLoadout`
fires synchronously in the same touch event. So typography flips and the
loadout applies at t=0 while the cap travels 280ms behind them: for the
whole glide the band says the new tier in type and the old tier in position.
Same on the programmatic path — the host `update` block assigning
`selectedLevel` (e.g. a routine firing) flips type instantly there too.

**Everything else wave-1 flagged is still as wave-1 found it:** fixed 280ms
`CurveB` for Δ1 and Δ2 alike; `ACTION_UP`-only touch, no press, no drag, no
cancel; landing haptic `fireFeel(8L)` fired at tap time, not arrival; marker
still 14dp (`trackY ±7dp`) in a 46×27dp cap, radius 8dp; track still a 2dp
line + 1dp echo, no slot; selected tick still drawn under the cap, invisible
at rest; no `contentDescription` anywhere in the view.

**Two wave-1 complaints are dead — killed by the system, not the band:**
(1) Night text separation (B2): the shipped staircase gives
`tokTextPrimary` night `#D2C6BB` vs `tokTextSecondary` `#9C8E84` — a real
step. Wave-1's band-local alpha hack (140–150/255) is obsolete; do not ship
it. (2) The slab (B4, night half): night `tokSurfaceVariant` is now the
recess token `#100E0C`, below ground `#171412` — the slab already sits in
the floor at night. Its remaining fault is only that it has no inner
recess lines of its own.

**One new fault since wave-1 — the real fonts broke the numerals.** Real
weights shipped: `paintMono` is now true Anonymous Pro Bold, `paintWord`
true Atkinson Bold. But `onDraw` still sets `paintMono.isFakeBoldText =
isSel` — fake-bold smear layered on a real bold for the selected numeral,
real bold for the unselected ones. Numerals now differ by *smear*, not
weight. Labels still all draw in `paintWord` (bold); `paintWordSub`
(regular) exists and is never used here. The weight fix is now cheaper
than wave-1 priced it: it is a paint swap, nothing else.

**The naming lie still lives, and it is the band's worst truth fault.**
Band prints `DEEP`; host maps level 1 → `SmartLoadout.WORK_SPRINT`
(`ShieldMatrixScreen.kt:154`); the routine rows translate the same value
back to `WORK` (`:305`). One tier, three names, two of them on the same
screen. The tiers are STUDY / DEEP / SLEEP — that is what routines load and
what manual arms load — so DEEP is the canonical print and WORK is the
misstatement. Rung 2 fixes it at the rendering layer; the enum is untouched.

---

## LADDER — 4 rungs, each independently shippable, in order

### Rung 1 — Re-pin the truth marks (in-view only, one sitting)

Mechanism: three paint-level corrections in `onDraw`, no geometry, no host
change, no motion change.

- **Marker re-pin:** marker colour = literal `LIGHT_ON_PRIMARY #FBF1EC` in
  both modes (replace `tokOnControlFill()` at the marker draw only — the
  token stays correct for cap *text* elsewhere). Marker length 14dp →
  **20dp** (`trackY ±10dp`, 3.5dp clear each end of the 27dp cap), width
  stays 3dp, round caps stay. `#FFE4CF` returns to text-only duty.
- **Real weights, no smear:** delete `isFakeBoldText` entirely. Numerals:
  selected `paintMono` (true bold), unselected `paintMonoSub` (true
  regular), 9sp both. Labels: selected `paintWord` (bold), unselected
  `paintWordSub` (regular), 11.5sp both. Colours unchanged — day
  `#2C2A29`/`#615B5A`, night `#D2C6BB`/`#9C8E84` (staircase tokens, no
  alpha tricks).
- **Same-stop tap stops being dead:** tapping the seated stop fires
  `fireFeel(1L, pitch)` — pitch 500 / 590 / 680Hz for stops 1/2/3.
- Curve + ms: unchanged — 280ms, CurveB `PathInterpolator(0.05, 0.7, 0.1, 1.0)`.
- Haptic: same-stop tick = rotary detent, waveform `[0, 7, 5]` ms at
  amplitudes `[0, 180, 70]` (engine's `playRotaryDetent`); no-engine
  fallback 1ms one-shot at `DEFAULT_AMPLITUDE`. Change-landing haptic
  stays where it is at this rung (t=0 — the debt Rung 2 pays).
- Sound: same-stop = detent transient at the stop's pitch, 6ms, gain 0.38.
  Nothing else changes.
- Depth / tokens: depth recipe untouched (slab fill + 1dp hairline; 2dp
  slit `0x2E000000` + 1dp echo `0x2EFFFFFF`; cap `tokControlFill()` day
  `#252323` / night `#4A423C`, contact shadow `0x38000000` +2dp, lit edge
  `0x59FFFFFF` 1.1dp). Accent: none. Green: none.

### Rung 2 — Land, then speak (the sequencing fix + the naming fix)

Mechanism: split the one lying field into two honest ones. `appliedLevel`
drives typography and the callback; `targetLevel` drives the cap. Touch
sets the target; **the animator's end listener** — and nothing else —
promotes target → applied, flips typography, and fires `onLevelChanged`.
Type therefore always names the tier actually loaded, position always
names where the cap is, and the two never contradict: mid-travel, type =
applied truth, position = intent. (This also gives drag in Rung 4 its
safety for free: sweeping across DEEP applies nothing until SLEEP seats.)

- **Motion:** CurveB `(0.05, 0.7, 0.1, 1.0)`, distance-proportional —
  Δ1 stop (0.45 frac) = **280ms**, Δ2 stops (0.90 frac) = **420ms** —
  constant peak speed instead of constant time. Seat: 3-keyframe holder,
  88% at target + sign × 0.006 frac (≈2dp past), 100% at target.
- **Passing detent:** on a Δ2 travel, when `capFrac` crosses 0.50, fire
  once: `fireFeel(1L, 590f)`.
- **Haptic:** passing = detent waveform `[0, 7, 5]` ms @ `[0, 180, 70]`.
  Landing (animator end, new stop only) = firm click, waveform
  `[0, 13, 20, 8]` ms @ `[0, 225, 0, 135]`, moved from tap time to
  arrival. Same-stop tap keeps Rung 1's detent.
- **Sound:** passing = detent 590Hz, 6ms, gain 0.38. Landing = firm-click
  transient 620Hz, 12ms, gain 0.5, chord ratio 1.1. No press sound.
- **Programmatic travel is silent:** when the host sets the level (a
  routine fired; he never touched the band) the cap travels on the same
  curves/durations, typography still flips only on land — but **no
  haptic, no sound**. The band never makes noise for a change no hand
  made, and a routine seating SLEEP at 20:00 costs the arm path nothing:
  the cap is already seated when he wakes.
- **Naming:** every user-visible rendering of `WORK_SPRINT` becomes
  `DEEP` — the Shield routine translator (`ShieldMatrixScreen.kt:305`)
  and the routine editor's loadout labels. Internal enum, stored
  `loadoutName` strings, and persistence untouched (no migration). After
  this rung the band, the routine rows, and the routine editor print one
  name for one tier, everywhere.
- Depth / tokens: unchanged from Rung 1.
- Host change: none to the callback contract — `onLevelChanged` simply
  fires ≤420ms later, at land. That delay *is* the fix.

### Rung 3 — The slot (the band becomes a machine part)

Mechanism: give the cap a channel to live in and a cap worth grabbing.
Host height 84dp → **88dp**; `trackY = h × 0.5`.

- **Slot:** rounded rect `30dp → w−30dp`, **9dp tall**, radius 4.5dp,
  centred on `trackY`. Fill: day `#C9C0B8`; night `#0B0908` (band-local
  literal — the staircase has no sub-recess token below `#100E0C`, and
  `#161311` blockBar is *lighter* than the night slab, so it cannot be
  the slot; one literal, named, not a new system token). Inner lines:
  top shadow 1.4dp `0x2E000000` (night `0x59000000`), bottom light 1dp
  `0x2EFFFFFF` (night `0x1AFFFFFF`), inset 4.5dp at the ends.
- **Detent notches:** 2dp × 2dp, `0x40000000`, cut into the slot's lower
  inner edge at each stop frac — the seats are visible when empty.
- **Scale leaves the slot:** stop tick moves below the channel,
  `trackY +8dp → +13dp`, 1.4dp, plus letterpress twin at +0.8dp in
  `0x2EFFFFFF` — never covered by the cap again; the full scale reads at
  rest. Column lock per stop: numeral baseline `trackY −19dp`, tick,
  label baseline `trackY +27dp`, one x for all three.
- **Slab recess:** add the sibling sleds' pair inside the slab — 1.4dp
  `0x2E000000` top-inner (inset 12dp), 1dp `0x2EFFFFFF` bottom-inner
  (night `0x1AFFFFFF`).
- **Cap:** **52dp × 30dp**, radius **6dp**. Construction, back to front:
  in-slot occlusion (cap width +8dp × 9dp, `0x2E000000`, radius 4.5dp);
  contact shadow +2.5dp, `0x38000000`; skirt (full rect, day `#1B1A1A` /
  night `#3A332E`); face inset bottom 3dp in `tokControlFill()` (day
  `#252323` / night `#4A423C`); lit top edge 1.1dp `0x59FFFFFF`,
  `capCx ±16dp`; two grip grooves at `capCx ±10dp`, 1dp `0x1F000000`
  (night `0x33000000`) + 0.8dp-offset `0x14FFFFFF` twin, span
  `trackY ±8dp`; marker on top — 3dp, `#FBF1EC`, `trackY ±11dp`.
- Curve + ms / haptic / sound: exactly Rung 2's program. Travelling
  shadow smears −3dp × sign(Δ) against travel direction.
- Depth / tokens: as listed; all tonal, no blur, no elevation, no
  gradient. Accent: none — Shield stays at 0 orange.

### Rung 4 — The hand (guarded drag; tap stays primary)

Mechanism: direct manipulation with scroll-safe guardrails, inheriting
Rung 2's land-then-apply sequencing.

- **Engage:** DOWN on the cap rect expanded 12dp, or anywhere in the slot
  span ±12dp vertically; drag engages only after 8dp horizontal-dominant
  movement (|Δx| > 8dp and |Δx| > 1.5 × |Δy|). On engage:
  `parent?.requestDisallowInterceptTouchEvent(true)` (the pattern the
  emergency valve in this same file already uses); released on
  UP/CANCEL. Before the threshold, the gesture belongs to the list.
- **Follow:** `capFrac` = finger frac 1:1, clamped 0.02–0.98; beyond the
  end stops, excess damped ×0.25, hard cap +0.035 frac (rubber band).
  Magnetism: within 0.045 frac of a stop, drawn frac =
  stop + (raw − stop) × 0.55 — the cap hesitates in each groove.
  Typography keeps showing `appliedLevel` throughout; the stop within
  0.10 frac of the cap previews at primary colour, regular weight.
- **Press state (DOWN, pre-drag):** cap group +1dp y, contact shadow
  tightens +2.5dp → +1dp, marker 3dp → 3.5dp. Silent.
- **Release:** snap animator **180ms**, CurveA
  `PathInterpolator(0.2, 0.0, 0.0, 1.0)` to nearest stop; seat keyframe
  (Rung 2's) only if snap distance > 0.10 frac. Apply on snap end, per
  Rung 2. Release on the applied stop, or vertical exit >24dp beyond the
  slab (cancel): return home, 260ms CurveA if cancelled / 180ms if
  released, **no apply**.
- **Haptic:** press — none (a press may become a scroll). Passing a stop
  mid-drag — detent `[0, 7, 5]` ms @ `[0, 180, 70]`, `fireFeel(1L,
  pitch)` at the crossed stop's pitch, 0.02-frac re-arm band so a resting
  boundary cannot machine-gun. End-wall entry — latch once per entry,
  `fireFeel(4L)`: waveform `[0, 11, 26, 9]` ms @ `[0, 215, 0, 150]`.
  Landing — firm click `[0, 13, 20, 8]` ms @ `[0, 225, 0, 135]` at snap
  end. Return — detent only, home stop's pitch, no click.
- **Sound:** passing = detent at crossed pitch (500/590/680Hz), 6ms,
  gain 0.38; wall = latch transient 760Hz, 16ms, gain 0.54; landing =
  firm click 620Hz, 12ms, gain 0.5; return = detent at gain 0.38, no
  click layered. Press and preview: silent.
- **Accessibility (closes a total gap — the view has none today):**
  `contentDescription = "Loadout, <DEEP>, 2 of 3"` set on every landing;
  change announced at landing only, never per passing stop.
- Depth / tokens: Rung 3's recipe, plus drag shadow: contact shadow
  x-offset −3dp × sign(velocity) while a MOVE arrived within the last
  100ms, relaxing to 0 after. If the intercept guardrails cannot be
  built exactly as specced, ship Rung 3 tap-only — a drag that fights
  the scroll is worse than no drag.

---

## ★ — priced

**★ Re-pin the marker to the literal constant `#FBF1EC` (Rung 1, first
bullet).**
- **Price:** one colour assignment at one draw call
  (`BlockitSuitcaseAndSwitchesViews.kt:269`). No new token, no host
  change, no test surface beyond the existing day/night Shield renders.
  Day renders are pixel-identical before and after.
- **Return:** the band's single state mark stops riding a text token.
  Night marker contrast stays ≈8:1 on the `#4A423C` cap, `#FFE4CF`
  returns to its staircase duty (text only), and the X1 lesson — *a
  control's mark must be pinned to the fill it sits on, not inherited
  from whatever a semantic token inverts to next* — is made structural
  instead of remembered. It is also the foundation every rung above
  assumes; Rungs 2–4 all spec the marker as `#FBF1EC`.
- Runner-up, for the record: deleting `isFakeBoldText` + the paint swap
  (3 lines) fixes every numeral and label on every render. It loses
  only because it improves a distinction; the ★ repairs a dependency
  that can silently break again.

---

## PEAK (≤8 lines)

1. Rungs 1–4 composed, plus: slot 10dp tall (radius 5dp), cap **54×30dp**.
2. Grip moves to the flanks: 3 grooves per flank zone (±14dp → ±25dp, 3.5dp pitch); the centre field carries the marker and nothing else.
3. All print letterpressed (light pass `0x2EFFFFFF` @ alpha 90 first, main pass over it); the marker is never letterpressed — it sits *on* the object, print sits *in* the panel.
4. End ticks inside the slot ends (`trackY ±2dp`, `0x38000000`): the mechanical limits the cap can never reach are printed, so the full range — including how far the cap did *not* go — is always visible.
5. Lit edge obeys a fixed light: its bright segment shifts ≤3dp as a pure function of cap position — light carries no data, encodes nothing, and therefore never lies.
6. One name (DEEP), one accent count (zero), one truth order (land → type → apply), silent when a routine seats it.
7. Acceptance: selection readable from cap position alone, in grayscale, mid-drag, at arm's length, day and night — or it does not ship.

---

## STANDARDS CHECK

- **ARM ≤10s:** the band is not on the Deck arm path; it is a rested-user
  Shield surface. Its own cost: one tap, travel ≤420ms (Δ2), apply at
  land — total added latency before the tier is true ≤0.42s, no press
  state, no confirmation, no second gesture at any rung. Drag is an
  addition, never the only route; tap remains byte-for-byte primary.
  Programmatic seating by routines costs 0s and 0 gestures.
- **HOLD absolute:** no rung adds a mid-session decision, a lock, a
  disabled state, or an override prompt to the band. Changing tier
  mid-session keeps today's contract exactly; the band invents no
  ratchet of its own.
- **TRUTH once:** position is the selection (readable in grayscale,
  mid-drag, from the cap alone); typography names the *applied* tier
  and flips only when the cap lands (Rung 2 kills the 280ms
  self-contradiction); one tier has one name on every surface (DEEP,
  Rung 2); no travelled-fill, no seat bar, no second rendering of the
  loaded tier anywhere in the band. Light at PEAK is a pure function of
  position and renders no fact.
- **His picks:** Sora / Atkinson Hyperlegible / Anonymous Pro at their
  real shipped weights only (fake-bold deleted, Rung 1); warm palette
  throughout, **zero green** in every hex specced; night staircase used
  as shipped — ground `#171412`, recess `#100E0C`, raised `#362C27`,
  cap `#4A423C`, text `#D2C6BB` / `#9C8E84` / `#776A60`, cream
  `#FFE4CF` text-only (protected by the ★ re-pin).
- **Routines are his:** the band loads exactly the tiers routines load —
  STUDY / DEEP / SLEEP — and Rung 2 makes the band *stop misstating*
  which one is loaded; it changes no routine behaviour, timing, or
  firing. Ledger: not presumed either way; nothing here touches it.

## CONFLICTS

- **Marko (MaxImpl 03, loadout cap) — mailed.** Three direct collisions:
  (1) his orange seat bar under the selected name spends Shield's one
  accent inside the band — rejected here: position already renders the
  selection, a bar under the word is a second rendering of the same
  fact, and Shield's budget stays 0. (2) His 104dp host vs 88dp here —
  density: the band is furniture above a list; 88dp buys the slot and
  the column lock, 104dp buys air. (3) His night marker as a dark
  engraved groove (`tokOnPrimary()` `#1B110D`) — that spec predates the
  shipped Round 0 fix; the groove reading was tried by the code and
  measured ≈2:1. Cream won, in code, already.
- **Marko (MaxImpl 05 §3.3, routine mini-selector) — mailed.** He prints
  `WORK` in routine rows and argues "WORK wins because the row already
  prints WORK." Inverted: the row printing WORK is the misstatement,
  not the authority. This wave's brief fixes the tiers as STUDY / DEEP /
  SLEEP; Rung 2 renames the *renderings* (Shield translator + routine
  editor labels) to DEEP and leaves his sled mechanism, his 260ms
  CurveA throw, and the stored enum untouched. His mini-selector's top
  step does not change; its labels do.
- **Accent budget:** house position is Shield = 0 orange. **Not broken**
  at any rung — no stated reason could beat position-as-state on this
  control, so none is offered.
- **Opal's pre-seating point (Commons):** agreed and absorbed — Rung 2's
  silent programmatic travel is exactly "the rested user seated it; the
  tired user never pays for it." No conflict.
- **Mara's light law (Commons):** PEAK line 5 is written to obey it —
  the specular shift is a function of position only and encodes no
  state beyond what position already shows. No conflict.
