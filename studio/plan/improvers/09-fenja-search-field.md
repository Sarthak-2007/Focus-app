# Fenja — Improvers, search field (Shield, ALL APPS) — PLAN WAVE

Owner: Fenja (Improvers, search). Coordinator: Ilya. Plan documents only — no code.
Element: the filter field in §4 of `ShieldMatrixScreen.kt` — `Box` + `BasicTextField`
under the `ALL APPS` header, above the category words and the app list.

---

## CURRENT — re-verified against today's code and tokens (not wave-1's)

| Property | Value now | Changed since wave-1? |
|---|---|---|
| Container | `Box`, full width, 40dp, `clip(RoundedCornerShape(12.dp))`, `background(scheme.surfaceVariant)`, 12dp horizontal padding, `CenterStart`. Comment above it reads `// Recessed 12dp search bar` | No |
| Recess lines | **None.** No `border()`, no inner lines, no shading. The comment claims a recess; the code draws a flat fill in a rounded clip. Wave-1's central finding **still true** — every sled track in `BlockitSuitcaseAndSwitchesViews.kt` draws the pair (top `0x2E000000`, bottom `0x2EFFFFFF`, 1.4dp); this field draws neither | No |
| Fill, day | `surfaceVariant` `#D8D1CB` on ground `#E4DFDA` — one step down | No |
| Fill, night | `surfaceVariant` `#100E0C` on ground `#171412` — **below ground: a true recess** | **YES — staircase landed.** Wave-1's `#272320` inversion is dead. All night values below are re-derived from `#171412` ground / `#100E0C` recess / raised `#362C27` / cap `#4A423C`, never from wave-1 hexes |
| Placeholder | Overlaid sibling `Text` (not `decorationBox`), `"Filter apps…"`, Atkinson (via `PlusJakartaSansFamily` alias), 13sp, Regular, `scheme.onSurfaceVariant` | Text/tokens changed, structure didn't |
| Placeholder colour | Day `#5F5856` (≈4.6:1 on the fill); night `#776A60` (≈3.8:1 on `#100E0C`) | **YES.** Wave-1's night collapse (`#FAE4D3` vs input near-white) is **fixed by the staircase tokens** — night input is now `#D2C6BB` (≈11:1). Wave-1's 60%-alpha placeholder fix is **obsolete and harmful**: applied today it would cut 3.8:1 to ≈2.2:1. Do not ship it |
| Input text | `BasicTextField`, single line, Atkinson 13sp **Bold**, `scheme.onBackground` — day `#2C2A29`, night `#D2C6BB` | Night ink changed |
| Caret | `cursorBrush = SolidColor(scheme.primaryContainer)` — day `#F55314`, night `#F47442` | No. Decision on this below |
| Keyboard / focus | No `keyboardOptions`, no `keyboardActions`, no focus state, no interaction source | No |
| Clear / trailing | None. Exit from a query is backspace-by-backspace | No |
| Count | Header only: `SHOWING n` from `filteredApps.size`. Correct, and it stays the only home (TRUTH once) | No |
| No-results state | **Does not exist.** Empty `filteredApps` renders nothing; header reads `SHOWING 0` over a blank column | No |
| Motion / haptic / sound | None on this path. `soundEngine` reaches the screen and is never called by the field | No |
| Type weights available | Atkinson ships **Regular + Bold only** (real files, Round 0b). Wave-1's "input → Medium" rung is **unbuildable** — Medium resolves to a neighbour weight. This plan never specs Medium for Atkinson | Constraint learned |
| Test tag | `shield_search_input` on the `BasicTextField` — preserved at every rung | — |

What the renders show (`02_shield.png` / `night/n02_shield.png`): a full-width
40dp slab carrying one short sentence in its left quarter, the widest empty
filled area in the section, reading as an imported text field on an instrument
panel — in the night render a near-black bar that at least now *sinks*
(staircase) instead of smudging. `SHOWING 12`: twelve apps. That number
drives the Standards Check below.

**The caret decision (stated, mine): the caret goes ink at Rung 1 and never
comes back.** Day `#2C2A29`, night `#D2C6BB` (`scheme.onBackground` both
modes). Reason on merits, not house law: a caret is a piece of the text — it
marks where the next character lands, it commits nothing, selects nothing,
arms nothing. Orange in this app is spent on commits. A blinking orange caret
makes the least committal object on the screen its loudest while focused, and
on Shield — whose day body otherwise holds zero accent below the nameplate —
it spends the whole ration on a 2dp text mark. Ink caret, both modes, every
rung, no exceptions for focus state.

**Silence, re-argued on merits (wave-1 defended it; it survives re-trial):**
sound stays **fully silent at every rung, including clear**. The engine's
synth voices are modelled physical transients — detent, latch, seal — each
answering "what just moved?" Nothing moves in a text field. A focus or key
click would be the highest-frequency sound in the app attached to the event
with the least physical referent, and per-key app ticks would double the
keyboard's own feedback, which is the user's OS setting to choose. Haptics
are a different ledger: exactly **one** haptic enters at Rung 2 (clear,
4ms) — argued there. Focus and keystrokes stay haptic-free: the keyboard
arriving is already the feedback for focus, and a filter has no detents to
tick through.

---

## LADDER — four rungs, each independently shippable, in order

House vocabulary for all rungs: curves only from `MonolithCurves` — A
`cubic-bezier(0.2, 0, 0, 1)`, B `cubic-bezier(0.05, 0.7, 0.1, 1)`, C
`cubic-bezier(0.3, 0, 0.8, 0.15)`; durations only from {180, 260, 280, 420,
520}ms. Haptics only via `TactileSoundEngine.triggerOneShotHaptic(ms)` at
`DEFAULT_AMPLITUDE`. Depth is always fill-step + alpha edge lines — never
shadow, never elevation, never a border stroke. Fonts: Atkinson (entry),
Anonymous Pro (printed matter), Sora never (display only). No green
anywhere; every hex below is warm-checked (G < max(R,B)).

### Rung 1 — Make the recess true (≈10 lines, no new state, no new tokens)

- **Depth — the ★ lines (priced in §★):** two 1dp `Box` overlays inside the
  existing container. Top: `Color(0x2E000000)`, aligned top, offset 1dp from
  the edge, inset 12dp from each end (terminating inside the 12dp corner
  radius). Bottom: `Color(0x2EFFFFFF)`, offset 1dp from the bottom edge,
  same insets. Mechanism: pooled shadow on the edge light cannot reach +
  caught light on the edge it grazes = a sink the eye reads without any
  shadow pass. Alpha black/white are mode-agnostic — one edit is correct on
  day `#D8D1CB` and night `#100E0C` alike, and at night the dark top line is
  what finally separates the recess's top edge from the `#171412` ground.
- **Caret:** `SolidColor(scheme.primaryContainer)` → `SolidColor(scheme.onBackground)`
  (decision above). One line.
- **Type:** placeholder and input both pinned to 13sp / lineHeight 18sp so
  the baseline cannot jump. Weights stay Regular (hint) / Bold (entry) at
  this rung — the weight delta is the empty/filled signal for one more rung;
  Rung 3 dissolves it properly by species, not by splitting a weight
  Atkinson doesn't have.
- **Keyboard:** `KeyboardOptions(imeAction = Done, capitalization = Words,
  autoCorrectEnabled = false)` + `KeyboardActions(onDone = { clearFocus() })`.
  Mechanism: a filter is a lookup, not prose — autocorrect fighting an app
  name is pure latency on the ARM-adjacent path, and Done must dismiss
  because today nothing does.
- **Motion:** none added (0ms). Filtering stays instant per keystroke.
  **Haptic:** none. **Sound:** silent (defended above).
- **States:** empty/focused/filled visually as today, plus true edges;
  no-results still unhandled — named debt, paid at Rung 2.

### Rung 2 — An exit and a focus state (decorationBox, first state)

- **Geometry:** height stays 40dp; radius 12 → **8dp** (the routine cap in
  this same file already uses 8dp — the field stops being a card, becomes a
  window). Content start 14dp; **trailing zone 40dp reserved at all times**
  so text never reflows when the clear affordance appears.
- **Structure:** the overlaid-placeholder hack is replaced by
  `BasicTextField(decorationBox = …)` — placeholder, inner field, line pair
  and trailing zone composed in one `Box`. Every later state is one
  composition, not z-stacked siblings.
- **Depth / focus sink:** fill animates one half-step deeper on focus —
  day `#D8D1CB` → `#CDC3BC` (halfway toward outline `#615B5A`); night
  `#100E0C` → `#0B0A09` (halfway toward true black; the staircase floor —
  derived, since no token sits below the recess). Simultaneously the top
  line's alpha goes `0x2E` → `0x47` (18% → 28%). A window deepens; it does
  not glow, ring, or grow a border.
- **Clear affordance:** when the query is non-empty, the trailing zone shows
  `✕`, Atkinson Bold 15sp, centred in a 40×40dp target, `onSearchQueryChanged("")`,
  present focused or not (an unfocused filter you must focus before clearing
  is two taps for one intent). Tone: day `#5F5856`, pressed → `#2C2A29`;
  night `#776A60`, pressed → `#D2C6BB`.
- **No-results state (arrives here):** when `filteredApps` is empty and
  (query non-empty OR a category is selected), §5 renders one item instead
  of zero: `"NO MATCH"` — Anonymous Pro Bold 11sp, letterSpacing 1sp, ink —
  over the cause in Atkinson 13sp `onSurfaceVariant`: `Nothing installed
  answers to "<query>".` / `No apps in <CATEGORY>.` Block 64dp, left-aligned
  to the **app rows' name axis** — 18dp as shipped; if Schlitten's icon rung
  lands (name axis → 36dp), this block follows his axis, because it speaks
  where app names would have stood. No card, no hairline, no button — the ✕ in
  the field is the exit. **No numeral appears in this block** (TRUTH once:
  the header's `SHOWING 0` already said it).
- **Motion:** focus gain/loss — fill + top-line alpha, **Curve A, 180ms**,
  simultaneous. Clear glyph — `AnimatedVisibility` fade, **Curve A, 180ms**
  in/out, no slide (printed matter appears; controls arrive). Clear tap does
  **not** drop focus or dismiss the keyboard — clearing is mid-task.
- **Haptic:** exactly one — clear tap fires `triggerOneShotHaptic(4L)`:
  **4ms @ DEFAULT_AMPLITUDE**, the engine's switch-flip one-shot. Why this
  one event earns it: clearing destroys context (the query) and its
  consequence — the full list restoring — happens *below* the field, outside
  the fovea while the thumb is at the top of the screen; the tick bridges
  that gap. Focus: none. Keystrokes: none.
- **Sound:** silent, including on clear. If an implementation cannot fire
  the haptic path without a synth voice, drop the call entirely rather than
  ship a sound.

### Rung 3 — Printed voice, list choreography (the placeholder stops impersonating text)

- **Type — species separation:** placeholder becomes printed matter:
  `"FILTER APPS…"`, **Anonymous Pro Bold 11.5sp, letterSpacing 1sp**,
  uppercase — the same voice as the `FilterWord` row directly beneath it
  (Anonymous Pro 11sp / 1sp) and the header above it. Tokens as shipped, no
  alpha tricks: day `#5F5856`, night `#776A60`. A mono tracked caps hint can
  never be mistaken for typed Atkinson text in either mode — the
  placeholder-as-content failure is closed by species, not by dimming.
  Input rises 13 → **14sp Bold, lineHeight 20sp** — the size class the app
  rows set names at — so eye flicking query ↔ rows never changes species.
  Caret stays ink.
- **Depth/tokens:** Rung 2 values unchanged; top line rests at 28% inside
  this deeper-reading field (the shading scales with the claimed depth).
- **Motion — the list:** §5 items gain `Modifier.animateItem()` — fade-in /
  fade-out / placement all **Curve A, 260ms**. Rows glide to their filtered
  positions instead of popping per keystroke. Filtering itself is **never
  debounced** — the query computes instantly; only the visuals are smoothed,
  so this rung costs the ARM path 0ms. Header `SHOWING n` gets an
  `AnimatedContent` tick: old numeral slides up 4dp and fades, new one
  enters identically, **Curve B, 180ms** — a readout that ticks reads as an
  instrument; a number that teleports reads as a bug.
- **Motion — no-results:** the Rung 2 block fades in **Curve A, 180ms,
  delayed 120ms** (visual delay only) so a fast typist passing through a
  zero-match prefix never sees it flicker.
- **Haptic / sound:** unchanged from Rung 2 — clear tick (4ms @
  DEFAULT_AMPLITUDE) only; sound silent.
- **States:** empty-unfocused (printed hint, base fill, lines 28%/18%) ·
  empty-focused (sunk fill, top line 28%, ink caret) · filled-unfocused
  (query 14sp, ✕ present) · filled-focused (both) · no-results (field
  unchanged; block per Rung 2 copy rules).

### Rung 4 — Demotion: the sticky 36 (the ARM rung)

- **Geometry:** §4's single LazyColumn item splits. Header + category words
  scroll as today; the field becomes a **`stickyHeader`** item. Visual
  height 40 → **36dp**, radius 8dp. Split mechanics, priced honestly: the
  root `LazyColumn` spaces items by 12dp, so once split, the internal 8dp
  spacers are replaced by **12dp gaps** (header → field, field → words) —
  in-system per the spacing set {4, 8, 12, 20, 32, 48}, and it supersedes
  wave-1's 10dp window→row gap, which is in no system. One spacer owner:
  the root's `spacedBy`, nobody else edits it (agreed with Wort by mail). Touch height stays honest: a **48dp**
  hit area via 6dp of transparent extension above and below the visual
  recess (into its own sticky strip's padding) — the eye gets 36, the thumb
  gets 48.
- **Sticky strip:** the field sits in a strip padded 0dp top / 8dp bottom on
  `scheme.background`, so rows slide *under an opaque ground*, never through
  the recess. A 1dp hairline (`outlineVariant` — day `#75615B5A`, night
  `#758F8888`) fades in at the strip's bottom edge **only while stuck**,
  alpha **Curve A, 180ms** driven by the stuck state — the one signal that
  the window is now riding the panel.
- **Wort's stub, accommodated (by mail, his spec, my strip):** while the §4
  header is scrolled above the stuck field AND a non-ALL category is active,
  Wort's 14dp mini-label strip (selected category, Anonymous Pro 10sp Bold
  caps at the 12dp axis, 3dp ink segment under it, Curve A 180ms in/out,
  tap = scroll back to his row — a return, never a second control) rides
  **inside my sticky item, directly under the field, above the hairline**.
  Stuck height is therefore 36dp normally, 36 + 14dp while the stub shows;
  the hairline always closes the strip, stub or not. Query-only filtering
  raises no stub — the query is already visible in the field. His geometry
  to maintain; my strip reserves the space and asks nothing else of it.
- **Why sticky at all:** at long list lengths, re-filtering without
  scroll-to-top saves ≈1.5–2.5s per use (two to three swipes back up). At
  12 apps the list is ≈1.5 viewports and stickiness simply never engages —
  this rung is inert exactly where it isn't needed and pays exactly where
  it is. It also cuts 4dp of furniture from the first viewport on every
  Shield visit, forever.
- **Motion:** sticking itself has no animation (it is the scroll's nature,
  1:1 with the finger); hairline fade Curve A 180ms as above. All Rung 3
  choreography unchanged. **Haptic:** none added. **Sound:** silent.
- **Coordination:** this split moves Wort's row out of the field's item and
  lets Schlitten's rows pass under the strip — both mailed (see Conflicts).

---

## ★ BEST-RATIO CHANGE (priced)

**★ Rung 1's line pair, alone: top 1dp `Color(0x2E000000)` + bottom 1dp
`Color(0x2EFFFFFF)`, inset 12dp from the field's ends, offset 1dp from its
edges. Price: ≈6 lines of Compose, 0 new tokens, 0 new state, 0 behaviour
change, `shield_search_input` untouched, correct in both modes from one
edit (alpha constants are mode-agnostic).**

Why this beats the other one-liners, priced honestly: the ink caret is one
line and I want it in the same commit if possible — but it is visible only
during the seconds a session spends focused, while the lines work in every
screenshot, every state, forever, and they repair the element's defining
lie: a comment and a fill claiming "recessed" with none of a recess's
optics. It is also the app's own grammar copied, not invented — the
identical constants already recess every sled track and rocker on this
screen. Ship ★ alone and the field stops looking imported; the rest of the
ladder is elevation, this is truth.

---

## PEAK (8 lines)

1. A 36dp sticky window milled into the panel (48dp touch), radius 8dp: fill day `#CFC6BE` (second step down) / night `#100E0C` (the staircase recess token itself — already the floor step), edge pair at 28% top / 18% bottom.
2. No placeholder string, ever: a permanent printed `FILTER` — Anonymous Pro Bold 10sp, letterSpacing 1.2sp, letterpress tone (day `#615B5A`, night derived from `#776A60` tier) — then a 1dp vertical rule 20dp tall, then the entry axis; empty reads as labelled darkness, never as content.
3. Entry: Atkinson Bold 15sp, ink (day `#2C2A29` / night `#D2C6BB`); caret ink in both modes — a caret is text, not a commit; orange appears nowhere in this element in any state.
4. `CLEAR` as a printed word (Anonymous Pro Bold 10sp, ls 1sp) in a permanently reserved 48dp trailing zone; pressed state = the filter row's own 2dp ink underline bar beneath the word. No glyph, no circle, no fill.
5. Focus = the window sinks a half-step (day → `#C6BBB2`; night holds `#100E0C` and the top line strengthens 28% → 40%) and `FILTER` wakes to full ink — Curve A, 180ms, all properties simultaneous; the lip, radius and height never move.
6. Choreography: list placement Curve A 260ms, header numeral tick Curve B 180ms, no-results fade Curve A 180ms after a 120ms visual delay; filtering never debounced; the field never resizes for the keyboard and never detaches from its column beyond sticking.
7. Exactly one haptic in the element's life — clear: 4ms @ `DEFAULT_AMPLITUDE`. Sound: none, at any state, by design (no moving part, no voice).
8. The count lives in the header only; no icon, no badge, no number ever enters the window — and if the shipped list is ever truly capped near 12 apps, the honest peak is deleting this field entirely (see Standards Check).

*Fallback, pre-authorised, outside the 8:* if one real-use pass shows the
label-only empty window being missed, restore Rung 3's printed placeholder
(`"FILTER APPS…"`) inside Peak geometry and drop the `FILTER` label — every
other Peak line is untouched by that swap.

---

## STANDARDS CHECK

**ARM ≤10s — the field is audited, and at 12 apps it loses. Said plainly:**
scroll-to-any-app at N=12 ≈ 1–2 swipes ≈ **1.0–1.8s**, zero mode changes,
eyes never leave the list. The search path ≈ tap + keyboard rise
(~0.3s) + 3 characters (~1.2s) + scan + sled tap ≈ **3.5–5s**, plus
clear/dismiss ≈0.8s to restore. Search **loses by ~2–3s at SHOWING 12** and
only breaks even around **N ≈ 28–35** (list beyond ~3 viewports, target
position unknown). Consequences, built into the ladder: (a) no rung adds a
single millisecond to the query path — filtering is never debounced,
animations are non-blocking tweens, clear is one tap replacing ~4
backspaces; (b) the ladder *demotes* the field (Rung 4: 36dp, sticky, useful
only when the list is long) instead of wave-1's promotion to a 48dp hero window — that
direction is reversed here on the numbers; (c) Peak line 8 states the
deletion condition out loud. The field justifies itself as long-list
insurance on a real phone's full installed set (60–120 apps), where it wins
by seconds — not as furniture for a 12-app render. Shield editing is in any
case rested-user work, off the Deck's arm path; nothing here gates arming.

**HOLD absolute:** the field arms nothing, weakens nothing, and adds no
mid-session decision. During a session it follows `effectiveNight` with no
special case; filtering changes only which rows are visible, never a tier.
The existing ratchet rule is untouched. Clear and focus cannot reach a sled.

**TRUTH once:** the count renders in exactly one place — the header's
`SHOWING n` — at every rung and at Peak. The field never carries a count,
a badge, or a match numeral; the no-results block states its cause in words
with no number. Placeholder truth: Rung 3 separates hint from content by
species (mono printed caps vs Atkinson entry); Peak abolishes the string so
no placeholder can read as content in either mode, by construction. Night
truth re-derived from the shipped staircase: recess `#100E0C` below ground
`#171412` — the field sinks after dark, and placeholder `#776A60` (≈3.8:1)
vs input `#D2C6BB` (≈11:1) keeps hint and entry apart without any alpha hack.

**His picks, held:** Atkinson for entry, Anonymous Pro for printed matter,
Sora unused (display only) — three fonts, no fourth. Warm palette
throughout; every specced hex checked warm, **no green**. Night values all
derived from the staircase tokens above, not wave-1 hexes. Ledger, the
banked hold, routines — untouched, not mine. Guard — not touched.

---

## CONFLICTS (+ mail sent)

- **Wort (filter words) — mailed.** Rung 4 splits §4's item: the field
  sticks, Wort's row scrolls away. Cost named in the letter: the selected
  category (and his travelling bar) leaves the viewport while scrolled,
  while query + category remain combined filters — a filtered list whose
  active category is invisible is a truth risk. My proposed settlement:
  field-only sticky (36dp strip); both-sticky would cost 36 + ~38 = ~74dp
  of permanent furniture and fails ARM's spirit. Wort's call on whether his
  off-screen stub idea should extend to the stuck state. Also flagged: my
  no-results copy prints his category labels verbatim (`No apps in
  <CATEGORY>.`) — label wording changes on his side ripple into my block.
- **Schlitten (app sleds) — mailed.** My field changes his rows' context,
  not their geometry: Rung 3's `animateItem()` (Curve A, 260ms placement)
  animates *his* rows during filtering, and Rung 4's sticky strip is the
  ceiling his rows slide under. No change to row height, sled travel, or
  cap spec is requested or implied; his plan-wave geometry (row 54 → 52dp,
  pitch 66 → 64dp, name axis → 36dp for a 28dp icon) shifts my numbers
  (break-even N, sticky payoff) by <5% and no spec here breaks — see the
  mail-resolved items below. Asked him to confirm placement animation doesn't fight his
  first-bind snap rule (rows must never animate on the app's initiative —
  filtering is the user's act, so I read it as exempt; his seat, his call).
- **Yuki (MaxImpl sleds) — mailed, conditional.** Peak changes the *list's
  spec* only at the container level: a `stickyHeader` item split plus item
  placement animation. Sled geometry, travel, detents and feel are
  untouched. Synthesis R3.26's group-by-state (SHUT/PEEK/OPEN sections,
  OPEN collapsed) is **not** in my ladder — if it is built, it composes
  below the sticky field as ordinary items, and the grouping decision
  belongs to Schlitten/Synthesis, not this seat. Letter sent so the
  container change is on his record before any build wave.
- **Resolved by mail during this wave (Wort + Schlitten wrote in):**
  (a) *Shared axis* — Wort holds ALL's left edge on the 12dp page inset =
  my field's outer edge = the header's edge. No conflict; my text axis
  (lip + 16dp at Peak) is internal and claimed by nobody else.
  (b) *Window→words gap* — wave-1's 10dp is dead: the Rung 4 item split
  makes the root's 12dp `spacedBy` the single owner of both §4 gaps.
  Wort's acceptance of 10dp noted and superseded on the spacing system.
  (c) *Header title truth (Wort's flag, my ruling)* — with a category
  selected, the header still reads `ALL APPS` while `SHOWING n` counts the
  subset. **Ruling: the title stays.** It names the section's universe,
  not an unfiltered claim; the qualifier already lives in exactly one
  place (the selected filter word) and the count in exactly one place
  (the header's right side). Retitling the section to the category would
  render the same fact twice — a TRUTH-once violation to cure a
  non-lie. Same logic covers an active query.
  (d) *Schlitten geometry* — his Rung 4: row 54 → 52dp, pitch 66 → 64dp,
  name axis 18 → 36dp reserving a 28dp app icon, sled 34 → 36dp tall.
  Absorbed: my break-even/sticky math shifts <5%; no-results alignment now
  follows his name axis (Rung 2, amended); sticky + group headers
  interleave freely per his letter (no adjacency assumption on his side).
  (e) *Wort's stuck-state stub* — accepted on both sides: his 14dp
  category mini-label rides inside my sticky strip under the field while
  stuck with a non-ALL category active (Rung 4, amended). It never
  coexists with his row and never changes selection, so TRUTH once holds;
  category labels are frozen as shipped, so the no-results block's
  inherited strings are stable.
  (f) *Icon plumbing — his delegation, my call:* icons enter as drawables
  handed into the existing Canvas row view, drawn in the same pass as name
  and sled. A Compose wrapper per row would nest composition inside every
  AndroidView list item for zero visual gain.
- **Synthesis tension, on the record:** R3.26's "sticky 36dp search" is
  adopted at Rung 4/Peak — agreement, reached independently from the ARM
  math. Its "rows 52dp" and grouping lines belong to Schlitten's seat; my
  break-even math used the shipped 54dp host and is insensitive to ±2dp.

— Fenja, Improvers (search)
