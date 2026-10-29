# 10 — Wort (Improvers) — Filter Words, PLAN WAVE

**Element:** `FilterWord` + its Row call site, Shield §4, `ShieldMatrixScreen.kt` —
`ALL` + `AppCategory.entries` (SHORT VIDEO / SOCIAL / GAMES / MESSAGING /
WEB & NEWS / OTHER APPS). Seven words. Coordinator: Ilya. Plan only, no code.

---

## CURRENT — verified against today's code, not wave-1's memory

Read line-by-line today (`ShieldMatrixScreen.kt:240–262` call site, `440–466`
composable; `Color.kt`; `Theme.kt`; `Type.kt`; `MonolithCurves.kt`).

**What the code does today — the dash, static:**

- `FilterWord` = `Column(clickable{ })`, centred, `spacedBy(3.dp)`. Word:
  Anonymous Pro **11sp**, `letterSpacing 1.sp`, Bold if selected else Normal,
  `scheme.onBackground` / `scheme.onSurfaceVariant`. Bar: `Box(18.dp × 2.dp)`,
  `onBackground` if selected else `Transparent`. **Fixed 18dp regardless of
  the word — dash, not underline. Static — no `animate*`, no indicator, no
  scroll-to-selected, no haptic, no sound, no pressed state.** Theme purges
  ripple app-wide (`NoRippleIndication`), so a tap today gives *zero* visible
  or felt confirmation in the row itself; only the list below changes.
- Row: `fillMaxWidth().horizontalScroll(rememberScrollState())`,
  `spacedBy(16.dp)`, no end padding, no fade, no overscroll spec. Row height
  is implicit ≈20dp — touch height ≈41% of the 48dp floor; `ALL` is ~23dp wide.
- Toggle semantics: tapping a selected category clears to `null` (= ALL);
  tapping ALL sets `null`. Exactly one selection exists at all times.

**What changed since wave-1 (two wave-1 premises are dead):**

1. **Wave-1's ★ never shipped.** The bar is still 18dp. Wave-1's ladder is
   0% landed on this element; R1 below starts from the dash, not from a
   finished underline.
2. **The night collapse wave-1 diagnosed is already fixed — globally, not
   by my row.** The shipped night staircase re-pointed the scheme:
   `onBackground` = `NightTextColorPrimary #D2C6BB` (11:1 on ground
   `#171412`), `onSurfaceVariant` = `NightColorOnSurfaceVariant #776A60`
   (3.5:1). Wave-1's night selected `#F8F5F5` and its row-local mute repair
   `#B3A396` are **obsolete** — the row now inherits a real value staircase
   from the scheme in both modes, and this plan adds **no row-local colour
   override at any rung**. Day likewise moved: unselected is now
   `#5F5856` (`TokenColorOnSurfaceVariant`, post-synthesis), not wave-1's
   `#787170`. Selected day `#2C2A29` on ground `#E4DFDA`.
3. **Real Anonymous Pro Bold/Regular are shipped** (`Type.kt`, true TTFs) —
   monospace, identical advances, so Bold↔Normal swaps cause zero reflow
   and zero indicator-target drift. The property wave-1 hoped for is now
   a fact the ladder leans on.

**The scroll is necessary — proven, not assumed.** At peak type (12sp,
0.8sp tracking, 0.6em advance ≈ 8.0dp/char): words total 432dp + 6×20dp
gaps = **552dp of content on a 388dp viewport** (412 − 24 page padding).
The only no-scroll alternative — the enum's short `code`s (VIDEO / CHAT /
WEB / TOOLS ≈ 344dp) — fits but fails TRUTH: `TOOLS` misnames Other Apps,
`VIDEO` drops the SHORT that defines the category. Honest full labels
scroll; therefore the scroll gets designed (R2–R3), not apologised for.
At rest ≈4½ words are clear (ALL → MESSAGING ends ~376dp), WEB & NEWS
sits at the right edge — today hard-clipped mid-word, the shipped bug
wave-1 named and nobody fixed.

---

## LADDER — 3 rungs, each independently shippable, each fully specced

Shared at every rung: Anonymous Pro only; no accent orange, no fill, no
container, no green; left edge of ALL = 12dp page inset = section header's
axis = search box's outer axis; counts live **only** in the header (`SHOWING n`) — no per-word
counts at any rung (a second count home violates TRUTH-once, and the list
one scroll below is the evidence anyway).

### R1 — The underline tells the truth (static correctness; one sitting)

- **Mechanism:** per-word bar `18.dp` → bar fills its Column's width
  (`fillMaxWidth()` on the bar Box; the Column already sizes to the Text).
  Bar 2dp, square ends, ink / Transparent exactly as today — layout never
  shifts on selection. Type steps up one rank: **11sp → 12sp**,
  tracking **1sp → 0.8sp**, explicit `lineHeight 15.sp` locking the
  baseline across the weight swap. Gap 16 → **20dp**. Container height
  declared **38dp** (word top padding 9dp, 3dp word→bar gap): touch
  height 20 → 38dp; each word's click width = word width, **minimum
  48dp** — ALL (24dp of glyphs) pads on the trailing side only, glyphs
  stay left-anchored to the 12dp axis. Trailing `Spacer(32.dp)` inside
  the scroll so OTHER APPS can rest fully clear of the right edge.
- **Curve + ms:** none — nothing moves at R1, deliberately. Static
  correctness before motion.
- **Haptic:** none yet (arrives R3). **Sound:** silent (silent at every
  rung — defence at R3).
- **Depth / tokens:** flat print, zero depth. Day: selected `#2C2A29`,
  unselected `#5F5856`, ground `#E4DFDA`. Night: selected `#D2C6BB`,
  unselected `#776A60`, ground `#171412` — all via `scheme.*`, no literals.
- **Ships alone as:** the row finally reads finished in a screenshot,
  both modes, and meets the accessibility floor (38dp / 48dp).

### R2 — The bar travels on a printed scale (the mechanism)

Everything in R1, plus:

- **Mechanism:** per-word bars deleted; `FilterWord` becomes Text + click
  + bounds report (`onGloballyPositioned` → parent-relative bounds). One
  indicator lives in the Row's parent `Box` (38dp, full width). Beneath
  it, a **viewport-fixed baseline rule**: 1dp hairline spanning the full
  row width, top edge at container bottom − 1dp, drawn under the words.
  Printed **stop ticks** ride the scroll layer at the rule's height:
  1dp × 3dp hairline ticks centred under each word — the scale's stops,
  so the bar's arrival lands *on* a mark. Travelling bar: **3dp** tall,
  square ends, width = selected word's glyph width minus the trailing
  tracking unit (≈0.8dp — letterSpacing appends after the last glyph;
  the bar ends under the last glyph, not past it), bottom edge seated
  **on** the rule, no gap, no overlap. On selection change, `left` +
  `width` animate jointly; during row scroll the bar is **glued 1:1** to
  its word (target recomputed per frame, snapped, never animated).
  Tapping a partially clipped word animates scroll so the word rests
  fully inside the safe zone (viewport − 32dp leading edge) — never
  scrolls if already fully visible. On first composition with a
  non-ALL category pre-selected, the row starts scrolled to show it
  (no animation — placement, not motion).
- **Curve + ms:** bar travel `MonolithCurves.specA` — Curve A,
  cubic-bezier(0.2, 0, 0, 1) — **260ms + 40ms per intervening word,
  cap 420ms** (inside the {180, 260, 280, 420, 520} vocabulary). Word
  colour crossfade 180ms Curve A inside the same gesture; weight swaps
  at t=0. Scroll-into-view: 280ms Curve A, concurrent, bar target
  computed against the *destination* scroll offset so bar and row
  arrive together. Reduced-motion (`AnimatorDurationScale == 0`):
  everything snaps; static R2 = R1 + rule + ticks.
- **Haptic:** none yet. **Sound:** silent.
- **Depth / tokens:** rule + ticks = `scheme.outlineVariant` — day
  `#75615B5A`, night `#758F8888`. Bar = selected-word colour (day
  `#2C2A29` / night `#D2C6BB`). This is the row's only depth: one
  printed line, 1dp, shared with every hairline in the app.
- **Mid-travel state:** bar between words at interpolated left/width
  riding the rule; destination word Bold from t=0, origin returns to
  Normal at t=0 — weight is identity, the bar is the journey; the
  disagreement mid-flight reads as motion, not error.

### R3 — Edges, the hand, and the screen reader (boundary + feel)

Everything in R2, plus:

- **Mechanism — edges:** 28dp linear fades to ground colour over both
  viewport ends, viewport-fixed, `pointerInput`-transparent, drawn
  above the words. Right fade alpha 1 while scroll remains → 0 at max
  scroll; left mirrors at scroll 0. A word may be cut **only by a
  fade, never by a hard clip** again. Overscroll glow disabled
  (`LocalOverscrollConfiguration = null`) — the fades are the only
  boundary signal. **Off-screen stub:** if the selected word scrolls
  fully out of view horizontally, a 12dp × 3dp ink segment appears on
  the rule hard against that side's fade inner edge — "your mark is
  that way" — vanishing the moment any part of the real bar re-enters.
  State is never unknowable because the row scrolled.
- **Mechanism — press:** pressed state via `collectIsPressedAsState`
  (ripple is purged app-wide; this is custom, not indication). Pressed
  unselected word takes full ink colour *without* the bar — a preview
  of selection; reverts on release-off / cancel. Pressed selected word:
  unchanged (already at ceiling). Drag on the row scrolls, tap selects
  — scrub-select is refused on the record: one gesture, one job.
- **Curve + ms:** fade alphas 180ms Curve A, thresholded at 2dp from
  each scroll end. Press colour: instant on DOWN (state answers on
  DOWN, per the app's state grammar), revert 180ms Curve A on release.
- **Haptic — through the engine, never raw `VibrationEffect`:** two new
  public one-method calls on `TactileSoundEngine` reusing its private
  `triggerWaveform`, haptic-only, no transient enqueued. Select a new
  category: waveform `[0, 8] @ [0, 110]` — one 8ms tick at amplitude
  110, fired at travel start. Clear to ALL (tapping the selected
  category): `[0, 4] @ [0, 110]` — the shorter tick *is* the difference
  between choosing and clearing. No-op re-tap of ALL, scroll, and
  press-down fire nothing. Fallback if Ilya refuses the engine diff:
  existing `triggerOneShotHaptic(8L)` / `(4L)` — same durations, house
  `DEFAULT_AMPLITUDE`. Priced: ~15 lines in the engine + 2 call sites.
- **Sound: silent, specified.** No moving part seats or latches when a
  printed word is tapped; filter taps repeat while scanning (2–3 in a
  second) and a per-tap click becomes chatter that trains muting; the
  confirmation budget is already spent correctly — bar (eye) + 8ms
  tick (hand) + list recomposing (consequence). No sound API is built.
- **Depth / tokens:** fades = ground → transparent (day `#E4DFDA`,
  night `#171412`). Stub = bar colour. Nothing else added.
- **Accessibility, completed:** semantics per word — `selectable`,
  selected state announced, `contentDescription = "Filter, <label>,
  <n> of 7"`; the row exposes tab-like behaviour to TalkBack. Touch
  floor from R1 holds (38dp height × ≥48dp width). Night unselected
  `#776A60` at 3.5:1 is the muted *tier* by design — the selected word
  (11:1 + Bold + 3dp bar on a rule) is the unmissable one; unselected
  words are read at tap distance, not across the room.

---

## ★ — priced

**★ Bar width `18.dp` → `fillMaxWidth()` (the bar fills the Column the
Text already sizes).** Price: **1 line changed, ~5 minutes, no new
composable, state, token, or test impact** (`FilterWord` carries no
testTag; screenshot diff only). It converts the shipped lie — a centred
dash covering ~21% of SHORT VIDEO, ~25% of MESSAGING — into the
underline whose extent *is* the selection, working in both modes at
zero risk, and it is the precondition for R2: an 18dp travelling bar
would just be a travelling dash. (This is R1's first line; it also
ships entirely alone.)

---

## PEAK — ≤8 lines

The Printed Scale: seven honest words in Anonymous Pro 12sp on a 1dp
rule with a printed stop under each word; one 3dp ink bar, exactly as
wide as its word, travels the rule (Curve A, 260ms + 40ms/word, cap
420ms) and seats on its stop; words crossfade, weight is identity.
Edges fade, never clip; a scrolled-away selection leaves a 12dp stub
on the rule pointing home. Select ticks 8ms @ 110, clearing ticks
4ms @ 110, sound never. Selected = #2C2A29 / night #D2C6BB, Bold,
barred — unmissable at a glance, in grayscale, in peripheral vision.

---

## STANDARDS CHECK

- **ARM ≤10s — honest accounting.** This row is *not* on the Deck arm
  path; it spends Shield setup time, and I won't dress it as arm time.
  Its own cost: visible word = 1 tap (~0.3s); clipped word = 1 flick +
  tap (~0.8s), with R2's scroll-into-view removing the second aim.
  What it buys: the unfiltered list is 12+ sled rows at 66dp pitch
  (~800dp of hunting); a filter collapses it to the 2–6 rows being
  worked. Net: for targeted sled work it saves 1–2 vertical flicks per
  use at ≤1 horizontal flick of cost; for ALL→category→ALL browsing
  it is roughly neutral, and its real value there is certainty, not
  speed. No rung adds a step, a dialog, or a confirmation to any path.
- **HOLD absolute.** Untouched. The row changes no session state, asks
  nothing mid-session, and its night behaviour during a session is
  inherited from the forced night scheme with no special case.
- **TRUTH once.** Selected word is unmissable by three agreeing
  channels (value + real Bold + travelling 3dp bar on a rule) — one
  fact, one rendering *system*, no competing second marker. Counts
  live in exactly one place: the header's `SHOWING n`. The code-label
  shortcut (TOOLS for Other Apps) was tested and refused as a lie.
  Known residual gap, flagged not hidden: with a category selected,
  the header still *titles* the section "ALL APPS" while `SHOWING n`
  counts the filtered subset — the title is Fenja's section furniture;
  mailed to her (below), not silently fixed from my seat.
- **His picks.** Anonymous Pro is the element's only voice (real
  Bold/Regular, identical advances, no reflow); warm palette only,
  zero orange, **never green**; night uses the shipped staircase
  (#D2C6BB / #776A60 on #171412) with no row-local override.
- **Accessibility floor.** 38dp touch height, 48dp minimum width —
  wave-1's floor kept and now actually met (today: ~20dp × ~23dp).

---

## CONFLICTS (+ mail sent)

- **Fenja (search)** — *no axis move.* My PEAK keeps ALL's left edge on
  the shared 12dp page inset (her box's outer edge, the header's edge).
  Two coordination points mailed: (1) her PEAK sets window→filter gap
  at 10dp; the `Spacer` between us lives in the shared §4 item — I
  accept 10dp, flagged so we don't both edit it; (2) the "ALL APPS"
  header-title truth gap above — hers to rule on as section owner.
  **Mail sent** to `mail/fenja/`.
- **Schlitten (app sleds)** — *no conflict; his geometry corrected by
  his letter mid-run.* His plan-wave spec supersedes his wave-1 PEAK:
  rows 54 → **52dp**, pitch 66 → **64dp**, and app names move to
  **36dp** for the 28dp icon replacing the pip — names do *not* join
  my 12dp axis (his icon column's left edge does; the name inset is
  his internal choice and nothing in my row depends on it). My 38dp
  container is fixed-height, so my fades and stub can never overlay
  his first row. He also flags: group-by-state headers are the list
  module's rendering, not his rows' (his rows carry no group state) —
  my plan specs no group headers; noted for Ilya. And his boundary —
  a Ledger deflection count, if it lands on Shield, belongs in the
  header zone, never per-row — matches my counts-in-header-only rule
  exactly. **Mail sent** to `mail/schlitten/` (axis letter + reply
  correcting it under his `row-geometry-seam` thread).
- **Synthesis vs Fenja — settled by Fenja's plan, and it creates my
  one postscript spec.** Her plan-wave Rung 4 splits §4's single
  LazyColumn item: the search field becomes a `stickyHeader` (36dp
  visual / 48dp touch) and **my category row scrolls away with the
  section header**. That resolves the synthesis's sticky lean in her
  favour (field-only sticky; both-sticky ≈74dp of permanent furniture
  fails the ARM spirit — agreed). But it opens the vertical version
  of the off-screen problem: query + category are combined filters,
  so a scrolled, filtered list can carry an active category visible
  *nowhere*. **Postscript spec — the stuck-state stub (my call, her
  invitation):** when the §4 header item has scrolled above the
  sticky field AND a non-ALL category is active, a stub strip rides
  the sticky field's bottom edge: the selected label in Anonymous
  Pro 10sp Bold caps at the 12dp axis, underlined by a 3dp ink
  segment exactly as wide as that mini-label — my R3 off-screen stub
  rotated 90° of circumstance, same grammar. Strip height 14dp,
  appears/disappears 180ms Curve A, no haptic, no sound. Tapping it
  scrolls the list back to the filter row — it is a *return*, not a
  second control: it never changes selection, and it never coexists
  with the row itself (stub exists only while the row is off-screen),
  so TRUTH-once holds. Query-only filtering raises no stub — the
  query lives in the sticky field, already visible; the category is
  the only state the row owns. Placement of the mark is on Fenja's
  furniture by her invitation in `mail/fenja/`'s letter; geometry of
  her header is untouched by it.
- **Copy dependency (Fenja, acknowledged):** her no-results block
  prints my labels verbatim ("No apps in <CATEGORY>."). My ladder
  renames and re-cases **nothing** — labels stay `cat.label.uppercase()`
  exactly as shipped — so her block inherits stable strings.
- **Wave-1 self-conflict, resolved:** wave-1's row-local night mute
  `#B3A396` and `#F8F5F5` selected tone are superseded by the shipped
  staircase; this plan deletes both rather than layering over them.

*Inbox check: `mail/wort/` empty at start and before finishing — no
letters waiting. Commons post appended alongside this file.*
