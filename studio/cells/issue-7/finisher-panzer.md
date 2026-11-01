# Issue #7 — Shield (audit fixes) — Finish Pass: PANZER

**Issue:** Sarthak-2007/Focus-app #7 — “Main-app pass — Shield (audit fixes)”
**Role:** PANZER, FINISHER 1, in VOGT's cell for issue #7
**Slice (only this):** the new live-status sentence (audit R-5) and the routine rows
(R-5 marker / R-6 words / R-12 items 7–8) — taken to gallery standard: exact spacing,
type hierarchy, alignment, how `BLOCKING NOW` / `NOT BLOCKING` sits at the top, and how
`ARMED` / `WON'T FIRE` / `RUNNING NOW` read at a glance, day and night.
**Status:** DECLARED — finish specification only. No file under `app/src/` edited by me;
§6 gives MONTEUR exact patches instead. Nothing compiled, no tests run.
**Sources read:** `studio/plan/AUDIT-shield.md` in full (my own finish-pass filing is
R-12; this file discharges R-12 for the status sentence and the routine rows, and
re-prices nothing else); `studio/cells/issue-7/designer-form.md` (FORM, §1–§3) and
`studio/cells/issue-7/innovator-funke.md` (FUNKE, §4–§5); the working tree on
`agent/issue-7` as MONTEUR's merge stands mid-flight — `ShieldMatrixScreen.kt`
(status item §0, `RoutineRow`), `ShieldBlockingStatus.kt` (the derivation and its real
printed strings), `MonolithViewModel.kt` (witness plumbing), `AppRules.kt`
(`groupedForCatalogue`), `MonolithCustomComponents.kt` (`FullBleedSectionHeader`,
`BlockitActionChip`, `inkControlFill` / `onInkControlFill`), `theme/Color.kt`,
`theme/Type.kt`, `theme/MonolithCurves.kt`, `MonolithModels.kt`
(`RoutineSchedule.liveCount` / `isActiveNow` / `isLive`).
**Sequencing note for VOGT (read first):** the tree is mid-merge. MONTEUR has deleted
the slip's state machinery (`slipTap`, `slipLines`, `restingFlips`, the `animate*` /
`alpha` imports) while the slip's rendering block and `SlipLineRow` still call it, and
the catalogue still renders flat `items(filteredApps)` beside the new unused
`catalogueGroups`. That is R-1 landing in pieces, not a finish defect, and it is
MONTEUR's to complete — but it means §6's patches are written against the **post-R-1**
file. PZ-1…PZ-3 (status) and PZ-4…PZ-7 (routines) touch no slip code and apply cleanly
in either order; only the top-of-screen order in §2.2 assumes R-1's final order.

---

## 1. Verdict in one paragraph

MONTEUR's derivation is right and his first render of it is not finished. The status
sentence currently ships as one bare `Text`: all–Anonymous Pro Bold 13sp, coloured
`scheme.error` whenever the wall is not proven up, no glyph, no fixed slot, no
`maxLines`, no animation, no semantics. Measured against the tokens that is: the day
error (`#D63E06` on `#E4DFDA`) is **3.49:1** — the least legible ink on the screen,
spent on the most safety-critical line; the longest real headline (UNPROVEN with a
routine label, ≈60 chars of 13sp mono ≈ 470dp) wraps to two or three lines inside a
`LazyColumn` whose height then jumps and shoves the LOADOUT cap — the exact jitter
R-12 exists to kill; and mono-for-everything breaks the screen's own mixed grammar
(Atkinson words, Anonymous numerals — the tally already obeys it). The routine rows
are untouched by the merge so far: the name still wears the Sora guest face
(R-12 item 7 evicts it), the cap is a fixed 48dp box that the word `WON'T FIRE`
cannot physically fit (59dp of 11sp mono text alone, before padding), the cap still
reads `enabled` only, and both finished helpers — `routineCapWord()` and
`routineRunningNowSuffix()` — sit imported and unused. Everything below is the
machining that closes those gaps. No derivation, wording, or truth rule changes:
FORM owns the states, FUNKE owns the evidence, MONTEUR owns the code. I own how it
sits.

---

## 2. The status sentence — finish specification

### 2.1 Anatomy of the finished line

One row, three parts, on the screen's left axis (the 12dp content padding every
section header and the routine time column already share):

```
[10dp glyph] 8dp [VERB — qualifier · ENDS 03:30]
             (optional line 2, UNPROVEN only: reason, §2.5)
```

- **Glyph — 10×10dp square, corner radius 2dp**, vertically centred on line 1,
  `RoundedCornerShape(2.dp)`. Square, not circle: this app's geometry is caps and
  squares, and the mark rhymes with the catalogue's margin glyphs (R-12 solidity
  scale). State is carried by glyph **density + verb together**, never by colour:
  - `LIVE` → solid fill, `scheme.onBackground`.
  - `NO_SESSION` → hollow: 1.4dp border `scheme.onBackground`, transparent centre.
  - `UNPROVEN` / `STALE` → solid fill at 55% alpha (`onBackground.copy(alpha = 0.55f)`)
    — PEEK's density from the solidity scale: present, not solid, not absent.
- **Text** — two faces, one size (patch PZ-2 builds the `AnnotatedString`):
  - Words in **Atkinson Hyperlegible Bold 14sp, letterSpacing 0.4sp** — one step
    above the 11.5sp section headers. This is the loudest *print* on the screen;
    the LOADOUT cap remains the largest *object*. That ordering is deliberate.
  - Numerals (the `ENDS HH:MM` run, any digit run) in **Anonymous Pro Bold 14sp**,
    via the tally's existing mixed-grammar construction (`tallyText` /
    `rememberMixedTypographyText` pattern). Numerals are Anonymous everywhere on
    this screen; the headline is not an exception.
  - Colour: **`scheme.onBackground` at full strength for every state, both themes**
    (day `#2C2A29` on `#E4DFDA` = **10.8:1**; night `#D2C6BB` on `#171412` =
    **11.0:1**). A dead wall at 1 AM is not greyed into the furniture, and an
    unproven wall is not dimmed either — the glyph carries the doubt, the words
    stay readable. **`scheme.error` is banned from this line** (see §2.4).
- **Slot** — fixed height **40dp** (`Modifier.height(40.dp)`, content vertically
  centred), `testTag` stays `shield_blocking_status` (MONTEUR's tag; tests may
  already cite it — the finish does not rename load-bearing tags).
- **Deviation from FORM §2.2, recorded for VOGT:** FORM specced a 28dp slot and
  55%-ink *text* for the unknown state. Two measured reasons to move: (a) MONTEUR's
  derivation prints longer suffixes than FORM's examples (`…RUNNING, ENFORCEMENT
  NOT PROVEN`), which need the two-line treatment in §2.5 — impossible inside
  28dp; (b) 55% ink by day measures ≈**3.4:1**, below every other print on the
  screen, on the one line that must survive a tired glance. The 55% density moves
  from the text to the glyph, where it costs no legibility. FORM's intent
  (unknown ≠ solid) survives; its mechanism is what changes.

### 2.2 How it sits at the top — order and gaps

The sentence is **item 0 of the `LazyColumn`**, in-flow (not sticky, not floating —
FORM §2.1 stands), and the finished top of the screen reads:

1. **Status sentence** (40dp slot).
2. Ratchet banner — *only when present* (see the gap law below).
3. **Tally headline** (R-1's state-only headline, directly under the sentence).
4. LOADOUT cap, ROUTINES, BLOCKED WEBSITES, search + grouped catalogue (R-1/R-3
   order — MONTEUR's to land; my gaps assume it).

Gap law (exact, all measured inside the 12dp `spacedBy` column rhythm):

- The `LazyColumn`'s top padding stays 8dp. The sentence gets **no additional top
  spacer** — it is the first print on the screen and sits like it.
- **Status → tally: 4dp net.** The sentence and the tally are one statement
  (fact, then counts) and must read as one block, tighter than the 12dp section
  rhythm. Implementation: the tally item carries no top spacer of its own beyond
  the column gap; MONTEUR folds the old `SlipHeaderRow`'s internal 16dp spacer
  out of the tally's new home (that spacer belonged to the slip's section break,
  and R-1 killed the section).
- **The banner must not leave a hole.** Today the banner is its own `item`
  wrapping an `AnimatedVisibility`: when the message is empty (every normal day)
  the item collapses but still collects 12dp of column spacing on both sides —
  a dead 24dp gap under the sentence, every day, forever. **Fix (PZ-3):** move
  the banner *inside* the status item's `Column` (status line, then
  `Spacer(8.dp)` + banner inside one `AnimatedVisibility`). When absent, the
  status block is exactly 40dp and the tally follows at the specced gap; when a
  refusal lands, the banner inserts with 8dp above and below and pushes
  everything down — an event may push; a non-event may not leave a hole.
- Nothing else may ever be inserted above the sentence. If a future section
  wants the top, it queues behind the tally.

### 2.3 Motion

- State change re-strikes the **whole line once**: `AnimatedContent` keyed on the
  headline string, fadeIn/fadeOut 260ms `MonolithCurves.EasingA` — the tally's
  exact manners (StandingTallyLine is the in-file precedent). Glyph crossfades
  with the text (one `AnimatedContent` around the whole `Row`, not per-part).
- **No pulse, no blink, no roll, no entrance animation.** A live wall is calm;
  the line is present at first frame in whatever state the derivation holds
  (null status → the unproven fallback wording MONTEUR already prints — it
  renders in the UNPROVEN dress: 55% glyph, full-ink text).
- Opacity-only throughout, so reduced-motion settings are honoured by
  construction. The fixed 40dp slot means a re-strike moves nothing below it
  by a single dp — including the LIVE → STALE flip the per-second ticker can
  produce when a beat ages out.

### 2.4 Colour law — why `error` dies here

MONTEUR's current rule (`isBlocking → onBackground`, else `scheme.error`) fails
three ways. By day the error token is 3.49:1 (above). By night the error slot is
`#F47442` (6.5:1 — legible, but it *is* the night accent's family): the accent
law (chrome C7) spends the single orange on commit verbs, never on print, and a
status sentence is print. Worst, it inverts the semantics: `NOT BLOCKING — NO
SESSION RUNNING` is the normal, expected state of the phone for most of the
day — printing the man's ordinary evening in alarm orange trains him to ignore
the colour, so the one night it means something (UNPROVEN mid-session) it says
nothing. **Rule: this line never wears a hue. State = glyph density + verb.
Full ink, always.** (The same argument is why the routine caps in §3 spend
fill density, never colour.)

### 2.5 Truncation and the long UNPROVEN line

One line is the law for the states he lives in; the widths prove it fits:

- Content width at 360dp: 360 − 24 (screen padding) − 10 (glyph) − 8 (gap) =
  **318dp**. At 411dp: **369dp**.
- `BLOCKING NOW — SLEEP SESSION · ENDS 03:30` (the audit's canonical LIVE line):
  ≈41 chars of Atkinson Bold 14sp ≈ 295–305dp — **fits at 360dp**, with the
  `ENDS` numerals in mono inside the same budget.
- `NOT BLOCKING — NO SESSION RUNNING`: ≈33 chars ≈ 240dp — fits anywhere.
- `NOT BLOCKING — SHIELD PROOF STALE`: ≈33 chars — fits.
- `NOT BLOCKING — <LABEL> RUNNING, ENFORCEMENT NOT PROVEN`: up to ≈60 chars
  ≈ 440dp — **does not fit, and must not be ellipsised**: Compose ellipsis
  would amputate `…ENFORCEMENT NOT PROV…`, the diagnostic tail, which is the
  only part of that sentence that tells him what to fix.

**Ruling for UNPROVEN only:** the headline splits at its `", "` into two lines
inside the same fixed 40dp slot (PZ-2):

- Line 1 (14sp dress, as §2.1): `NOT BLOCKING — SLEEP SESSION RUNNING`
- Line 2: `ENFORCEMENT NOT PROVEN` — Anonymous Pro Bold 11sp, letterSpacing
  0.6sp, `onBackground` at 72% alpha (day ≈5.9:1, night ≈6.5:1 — secondary
  print, still honestly legible), offset to the text axis (18dp start padding
  inside the slot: 10 glyph + 8 gap), 2dp below line 1. Line heights:
  14sp line ≈19dp + 2dp + 11sp line ≈15dp = 36dp ≤ 40dp slot. ✓

Every other state renders single-line; the split is a property of the string's
comma, not a mode flag, so STALE/NO_SESSION/LIVE can never accidentally
acquire a second line. `BLOCKING NOW` / `NOT BLOCKING` themselves never
truncate, wrap, or shrink under any circumstance — they are the first words
of line 1 in every state, which is why they survive even the §2.6 case.

### 2.6 Edge cases — status sentence

- **Font scale > 1.3** (`LocalDensity.current.fontScale`): the slot trades its
  fixed height for `heightIn(min = 40.dp)` and line 1 gets `maxLines = 2`.
  At 2.0× the verb still leads and still never shrinks; the qualifier wraps
  under it. A fixed 40dp at 2.0× would clip the verb itself — the one outcome
  worse than a taller slot.
- **Very long routine names** (`MORNING REVISION SESSION · ENDS …`): line 1
  ellipsises the *label* first (it is mid-string; end-ellipsis takes the
  `ENDS` segment before the label on a single `Text`). Segment-drop order,
  implemented by PZ-2's builder, not left to the ellipsiser: (1) drop
  ` · ENDS HH:MM`, (2) ellipsise the label, (3) verb untouched. If MONTEUR
  prefers not to build the dropper in this merge, the fallback is bare
  end-ellipsis **plus** a filed follow-up — never silent acceptance: an
  `ENDS` time is a fact he plans around, and losing it without a ruling is
  how the next audit gets its C1.
- **Safe Break suffix** (` · SAFE BREAK — PEEK APPS OPEN`): it is part of the
  LIVE headline and the segment that keeps that sentence true; in the drop
  order it outranks `ENDS` — drop `ENDS` first, the break segment last.
- **TalkBack (R-11 rides this line):** `contentDescription` = the headline
  read plainly, punctuation substituted (`"Blocking now. Sleep session, ends
  03:30."`), set via `Modifier.semantics` on the slot; no custom action —
  in MONTEUR's derivation this line is print, not a door (no CAN'T CHECK /
  retry state exists in `ShieldBlockingStatus`; if VOGT later adopts FORM's
  third state, its dress is already specced: 55% glyph, full-ink text, and
  the tap target becomes the whole 40dp slot — no re-finish needed).
- **Session-active light theme:** `effectiveNight` follows the session for
  the sleds; the sentence reads `MaterialTheme` tokens only, so it follows
  the *scheme* the app actually renders — no separate night branch, no
  hardcoded colour anywhere in the slot (the current banner's `0xFFFBF1EC`
  is R-7's problem, named here only because it sits 8dp below my line:
  when PZ-3 moves the banner into my item, its text colour goes to
  `scheme.onPrimaryContainer` in the same patch — one hardcode dies as a
  side effect, at no extra price).

---

## 3. The routine rows — finish specification

### 3.1 What is broken today (measured, `RoutineRow` as shipped)

1. **The Sora guest.** Name = Sora Bold 16sp. Every other word in the row —
   and the section header above it — is Atkinson/Anonymous. R-12 item 7:
   evicted. A routine row is telemetry, not display type.
2. **The cap cannot hold its new word.** Cap = fixed `width(48.dp)`,
   radius 8dp, text from `if (enabled) "ON" else "OFF"`. `WON'T FIRE` at
   Anonymous Bold 11sp = 9 glyphs × 6.6dp = **59dp of text alone** — it
   clips inside 48dp before padding is even considered. The helper that
   returns the right word (`routineCapWord`) is already written and
   imported; the row just never calls it.
3. **`RUNNING NOW` is computed nowhere in the row.** `routineRunningNowSuffix`
   is likewise imported and unused; the meta line prints days · loadout and
   stops.
4. **The meta line has no truncation law** (no `maxLines`), so a six-day
   custom mask + loadout + (once wired) RUNNING NOW can wrap the row to
   three lines and break the one-axis column the time gutter builds.
5. **The expanded body hangs off nothing:** the ±30 / day-chip rows start at
   the screen's left edge, under the time gutter — no indent to the name
   axis, no hairline marking where the calm row ends and its machinery
   begins (R-12 item 8's “indent + hairline”).

What is **acquitted and does not move**: the collapsed row's whole rhythm —
time gutter left, name/meta centre, cap right; rows 16dp apart; collapsed
by default. Three audit seats acquitted collapsed-row calm; a finisher
does not churn what the audit cleared.

### 3.2 The finished collapsed row — exact geometry

```
[time 54dp] 12dp [name 15sp Atkinson Bold     ] 12dp [cap]
                 [meta 12sp mono · RUNNING NOW ]
```

- **Time gutter — unchanged.** `width(54.dp)`; start = Anonymous Bold 15sp
  `onBackground`; end = `→ HH:MM` Anonymous 11sp `onSurfaceVariant`;
  1dp between. It fits its widest content (`→ 07:00` ≈ 46dp) with room,
  and every routine on the screen shares the axis — the day reads as a
  column. One addition (§3.4): when the routine is running, the end time
  promotes to `onBackground` Bold — the window is live, both its numbers
  speak at full volume.
- **Name** — `AppTitleTextStyle` (Atkinson Hyperlegible Bold 15sp / 20sp
  line height — the ladder's existing row-title style; no new style is
  invented), uppercase as today, `maxLines = 1`, `TextOverflow.Ellipsis`.
  Baseline check: name cap-height against the start time's — 15sp Atkinson
  Bold vs 15sp Anonymous Bold sit within 1dp on the shared top; the current
  `CenterVertically` row alignment is kept (fixing what is broken, not
  re-hanging what hangs straight).
- **Meta line** — Anonymous Pro 12sp, `onSurfaceVariant`, composed as a
  `Row`: base text (`DAILY · SLEEP`) with `weight(1f)`, `maxLines = 1`,
  ellipsis; the RUNNING NOW suffix as a **separate, unweighted `Text`**
  so it can never be the part that ellipsises (§3.4).
- **Gaps** — explicit `Spacer(12.dp)` between gutter and name column and
  between name column and cap (today they are implicit in
  `SpaceBetween`; explicit spacers make the axis survive the cap's new
  variable width). Row vertical padding stays 4dp; row minimum height
  becomes 48dp (`heightIn(min = 48.dp)` on the header row) — the whole
  row is the expand tap target and 48dp is the honest floor for a target
  he hits at 1 AM. Name+meta block is ≈38dp, so the min-height costs
  ≈5dp of air per row and buys a target that cannot be missed.
- **Inter-row gap stays 16dp.** With the 48dp floor the routines block
  grows ≈5dp per row; that is the only vertical price in this section
  and it is spent on the tap target, on the record.

### 3.3 The cap — one grammar, three densities (`ON` / `WON'T FIRE` / `OFF`)

The cap is the row's verdict; it must be readable across the room of the
row — at a glance, without reading the meta line. Same machining for all
three states; **density carries the state, never hue** (the §2.4 law,
applied to controls):

| State (derivation) | Fill | Border | Word colour | Reads as |
|---|---|---|---|---|
| `ON` (`enabled ∧ daysMask ≠ 0`) | `inkControlFill()` (solid ink; night `#4A423C`) | 1dp, same as fill | `onInkControlFill()` (day cream on ink 14.1:1; night `#FFE4CF` on `#4A423C` 8.1:1) | his switch, live — the solid density, SHUT's density |
| `WON'T FIRE` (`enabled ∧ daysMask == 0`) | `scheme.surfaceVariant` (recessed; night `#100E0C` — *below* ground, a visible hole) | 1dp `scheme.outlineVariant` | `scheme.onBackground`, **full** | switched on and inert — recessed body, but the word refuses to whisper |
| `OFF` (`!enabled`) | `scheme.surfaceVariant` | 1dp `scheme.outlineVariant` | `scheme.onSurfaceVariant` | his switch, off — recessed body, quiet word |

`WON'T FIRE` vs `OFF` share a body and differ in word density (full ink vs
variant) plus the word itself — at a glance: solid = will fire, holed +
loud word = *cannot* fire, holed + quiet word = *is* off. No strikethrough
(that strike is R-7's refusal grammar; borrowing it here blurs both), no
icon, no second badge.

- **Size:** height stays **32dp**; width becomes payload-sized —
  `widthIn(min = 48.dp)` + `padding(horizontal = 10.dp)`, no fixed width.
  ON/OFF keep their current 48dp footprint (13–14dp of text + 20dp padding
  < 48dp floor, so the min governs and nothing on screen moves when only
  ON/OFF rows exist). `WON'T FIRE` sizes to 59 + 20 = **79dp**. The cap
  never shrinks, wraps, or scales its text: `maxLines = 1`, no
  `softWrap` tricks.
- **Radius 8dp → 12dp.** The app has exactly two radii (12 small / 24
  large, `MonolithShapes`); 8dp is an off-law radius that makes the cap
  read as a different species from `BlockitActionChip` (12dp, 42dp tall)
  in the expanded body directly beneath it. At 12dp the collapsed cap and
  the expanded chips are visibly one family at two heights. This is
  R-12 item 8's radius ruling, confirmed against the shapes law.
- **Tap separation:** the cap keeps its own `clickable` (toggle) inside
  the row's `clickable` (expand) — current nesting already resolves to
  the inner control; the finish changes no behaviour, only the explicit
  requirement that the cap's touch area is the whole cap box (79×32dp
  for WON'T FIRE is below the 48dp ideal on height — accepted: the cap
  is 32dp by the screen's cap grammar everywhere, and inflating one
  state's cap height to 48dp would make the verdict's *size* carry
  state, which it must not).
- **Word source:** `routineCapWord(routine.enabled, routine.daysMask)` —
  the existing helper, one-line wiring (PZ-5). The header count and the
  cap now agree by construction: `liveCount` excludes exactly the rows
  whose caps say `WON'T FIRE` or `OFF`.

### 3.4 `RUNNING NOW` — the only true “now” on a row

- **Source:** `routineRunningNowSuffix(routine, nowMillis)` — the existing
  helper over `RoutineSchedule.isActiveNow`. (FUNKE §5b argues the marker
  should ultimately be powered by `WindowState.OPEN_RUNNING`; that is an
  evidence upgrade for VOGT to rule on, and it changes nothing in this
  finish: whichever derivation feeds the suffix, it prints where and how
  this section says. The finish is derivation-blind on purpose.)
- **Print:** appended to the meta line as its own `Text` (see §3.2):
  text ` · RUNNING NOW`, Anonymous Pro **Bold** 12sp, `scheme.onBackground`
  full ink, against the meta base's regular-weight `onSurfaceVariant`.
  Weight + density, same size — it pops without shouting, and it is the
  only bold word in the meta line, so the eye lands on it first. It is
  never printed for a `WON'T FIRE` row (`isActiveNow` is false for
  `daysMask == 0` by construction — the two verdicts can never collide
  on one row) and never for an OFF row.
- **Time gutter echo:** while running, the end-time text promotes from
  `onSurfaceVariant` regular to `onBackground` Bold (§3.2). Two quiet
  signals, one fact, both on the row's existing axes — a man scanning
  the gutter column sees which window is live before he reads a word.
- **Refresh:** the suffix derives from the `nowMillis` the screen already
  receives; the VM's per-second ticker recomposes it. No new clock, no
  per-row timer. At the boundary minute the marker appears/disappears
  with **no animation** — routine rows do not re-strike; a word that
  fades in on a schedule row reads as a flicker, and this block's
  manners are calm.

### 3.5 The header above the rows — `n ARMED`

MONTEUR has already landed the word (R-6: `ACTIVE` → `ARMED`). The finish
on it is three small things:

- **Dress is already right and stays:** number in Anonymous Bold
  `onBackground`, suffix in Atkinson `onSurfaceVariant` (the
  `FullBleedSectionHeader` mixed grammar) — count loud, word quiet,
  exactly the hierarchy the status sentence uses in reverse. No change.
- **Right-edge axis:** the header's right word carries the header family's
  `padding(end = 3.dp)` — the 3dp off-axis pad R-12 item 1 kills globally.
  In this block the misalignment is visible against a hard reference:
  every routine cap's right edge sits on the content edge, 3dp to the
  right of the ARMED count above it. PZ-7 removes the pad *at this
  header's call site* if the global item-1 pass has not landed first;
  the two must not both fire (VOGT sequences it — noted in §6).
- **The reset note** (R-6 rewording is copy, FORM §3/R-6 own it): finish
  only — note-to-first-row gap becomes 8dp (today: note `top = 6dp` +
  a 4dp spacer + 16dp inter-item rhythm reads as three unrelated gaps).
  One gap, 8dp, whether or not the note is present the first row's top
  does not move: when the note dismisses, rows rise by the note's height
  — accepted, it is a one-time strip and its departure *should* read.

### 3.6 The expanded body — finish only (editor is Voss's)

The ±30 chips are DELEGATED to Voss's batch-10 wheels (R-8) — I design
no interim editor and change no chip. What the finish owns is how the
body *sits* when the row opens:

- **Indent:** both expanded rows get `padding(start = 66.dp)` — 54dp
  gutter + 12dp gap — so the machinery hangs under the name axis, not
  under the times. The eye keeps one left axis per row-part: times,
  then name-and-its-controls.
- **Hairline:** a 1dp `scheme.outlineVariant` line above the body,
  full row width, with 8dp above and 8dp below (inside the expanded
  block, replacing the current flat 8dp `spacedBy` seam). Hairlines are
  reserved for “next item inside a list” (the header family's own law);
  the expanded body is exactly that — a sub-list inside the row.
- **No height animation** on expand/collapse in this pass: the current
  instant toggle stays. (A 260ms Curve-A height settle is the obvious
  polish and I am explicitly *not* speccing it — `LazyColumn` item
  height animation mid-scroll is how lists learn to jitter, and nobody
  has measured it on device. Filed as a named follow-up, not smuggled.)
- Toggling the last day chip off flips the cap to `WON'T FIRE` on
  commit, immediately, no confirmation, no added copy — the cap *is*
  the notice (FORM §3.3 stands, unchanged).

### 3.7 Edge cases — routine rows

- **Zero routines:** the block prints the screen's one absence grammar —
  `NONE`, Anonymous Pro 11sp, letterSpacing 1sp, `onSurfaceVariant`,
  in a 40dp left-aligned box (the `SlipNoneLine` precedent, reused, not
  reinvented). A missing block reads as a bug; a printed NONE is a fact.
  (Seeds make this rare; deletions can still reach it.)
- **Long names** (`MORNING REVISION BLOCK`, custom names longer): name
  ellipsises; the cap is outside the weighted column and never yields
  width. A name may lose its tail; a verdict may not lose a letter.
- **Widest meta:** six-day mask (`MON TUE WED THU FRI SAT` ≈ 23 chars
  ≈ 166dp at 12sp mono) + ` · SLEEP` + ` · RUNNING NOW` (≈100dp) totals
  ≈274dp against ≈190–240dp of name-column width next to a 79dp
  WON'T FIRE cap — the base text ellipsises (it has the weight), the
  suffix (unweighted) survives whole. Priority order on the meta line:
  RUNNING NOW > loadout > days tail. That ordering is why the suffix is
  a separate `Text` and not a concatenated string.
- **Overnight windows** (22:30 → 07:00): print is unchanged;
  `isActiveNow` already checks yesterday's start — the gutter echo
  (§3.4) must use the same `running` boolean as the suffix, computed
  once per row per composition, never two calls that could disagree
  across a midnight recomposition.
- **Font scale > 1.3:** meta stays `maxLines = 1` (it is secondary
  print; ellipsis is honest), name stays `maxLines = 1`, cap text
  never scales down — at 2.0× the WON'T FIRE cap grows in height with
  its text (32dp is a floor: `heightIn(min = 32.dp)`, vertical padding
  8dp) rather than clipping. Rows get taller; nothing gets unreadable.
- **TalkBack (R-11):** header row `contentDescription` =
  `"Sleep routine, 22:30 to 07:00, daily, sleep loadout"` +
  state word spoken plainly (`"on"` / `"off"` / `"won't fire, no days
  set"`) + `", running now"` when present; the cap remains a separate
  actionable node (`"Toggle Sleep routine"`); expanded state via
  `stateDescription` (`"Expanded"` / `"Collapsed"`). The glyph-density
  language of §3.3 is visual only — every density distinction has a
  word, above, so nothing is carried by fill alone.

---

## 4. Day / night — the glance matrix (both slices)

All values are scheme tokens; the numbers are measured from
`theme/Color.kt`. Nothing in either slice hardcodes a colour, so the
session-forced night scheme (`effectiveNight`) and the system night
scheme take the identical finish.

| Element | Day | Night | Contrast (day / night) |
|---|---|---|---|
| Status verb + qualifier | `onBackground` `#2C2A29` on `#E4DFDA` | `onBackground` `#D2C6BB` on `#171412` | 10.8:1 / 11.0:1 |
| Status glyph — LIVE | solid `onBackground` | solid `onBackground` | (shape, not text) |
| Status glyph — NO_SESSION | hollow 1.4dp `onBackground` | hollow 1.4dp `onBackground` | (shape, not text) |
| Status glyph — UNPROVEN/STALE | solid `onBackground` @55% | solid `onBackground` @55% | density step, word stays full ink |
| Status line 2 (UNPROVEN reason) | `onBackground` @72% | `onBackground` @72% | ≈5.9:1 / ≈6.5:1 |
| ~~Status in `scheme.error`~~ (killed) | `#D63E06` | `#F47442` (the accent family) | 3.5:1 / 6.5:1 — dim by day, hue-spending by night |
| Routine cap ON | `inkControlFill` `#252323`, word `#FBF1EC` | fill `#4A423C`, word `#FFE4CF` | 14.1:1 / 8.1:1 |
| Routine cap WON'T FIRE | `surfaceVariant` `#D8D1CB`, word `onBackground` full | `surfaceVariant` `#100E0C` (below ground — reads as a hole), word `onBackground` full | word ≈9:1 / ≈11:1 on its fill |
| Routine cap OFF | `surfaceVariant`, word `onSurfaceVariant` `#5F5856` | `surfaceVariant`, word `onSurfaceVariant` `#776A60` | quiet by design; the *word* OFF is short and the state is the absence of claim |
| RUNNING NOW suffix | Anonymous Bold `onBackground` | Anonymous Bold `onBackground` | 10.8:1 / 11.0:1 against meta base 5.3:1 / 3.5:1 — the pop is the point |

Night note: night `onSurfaceVariant` (`#776A60`, 3.5:1) is the weakest
print in the block — it carries only the meta base and the OFF word,
both of which are *supposed* to recede, and both are backed by a second
channel (position in the meta grammar; the cap's recessed fill). No
verdict depends on it alone, in either theme. That is the line I held:
**recession may be quiet; verdicts may not.**

---

## 5. What I did not touch, and why

- **The derivation and its strings** (`ShieldBlockingStatus.kt`,
  `routineCapWord`, `routineRunningNowSuffix`, the VM plumbing): right
  as built; FUNKE's `OPEN_RUNNING` upgrade for the marker's evidence is
  VOGT's ruling to make, and §3.4 is written so it lands without
  re-finishing.
- **The LOADOUT cap's machining** (R-12 item 3 / R-4): another slice of
  the finish pass; my only claim on it is the gap law in §2.2.
- **The tally's dress** beyond its gap to the sentence: its mixed
  grammar and 260ms re-strike are acquitted and are my §2.3 precedent,
  not my subject.
- **The time editor:** DELEGATED to Voss's batch-10 wheels (R-8).
  §3.6 finishes the body's seating only; when the wheels land they
  inherit the 66dp indent and the hairline unchanged.

---

## 6. Exact patches for MONTEUR (post-R-1 file; no other editor)

All in `app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt`
unless named. Sizes are honest estimates from the snippets.

**PZ-1 — Status slot: glyph + fixed height** (~25 lines changed).
Replace the bare status `Text` item with:

```kotlin
item {
    Column { // status + banner share one item (PZ-3)
        AnimatedContent(
            targetState = statusHeadline,
            transitionSpec = {
                fadeIn(tween(260, easing = MonolithCurves.EasingA)) togetherWith
                    fadeOut(tween(260, easing = MonolithCurves.EasingA))
            },
            label = "shield_status"
        ) { headline ->
            StatusSentenceLine(
                headline = headline,
                proof = blockingStatus?.proof, // null → UNPROVEN dress
                modifier = Modifier.testTag("shield_blocking_status")
            )
        }
        AnimatedVisibility(visible = !ratchetWarningMessage.isNullOrBlank()) {
            /* banner as today, but Spacer(8.dp) above it and
               text colour Color(0xFFFBF1EC) → scheme.onPrimaryContainer (§2.6) */
        }
    }
}
```

`StatusSentenceLine` (new private composable, same file): a `Row`,
`height(if (fontScale > 1.3f) Modifier.heightIn(min = 40.dp) else Modifier.height(40.dp))`,
`verticalAlignment = CenterVertically`; glyph `Box` 10dp per §2.1
(density from `proof`: LIVE solid / NO_SESSION hollow 1.4dp /
else 55%); `Spacer(8.dp)`; the text column of PZ-2. Semantics per §2.6.

**PZ-2 — Status text: mixed grammar + UNPROVEN split + drop order** (~35 lines).
Build the `AnnotatedString` from `headline`: split once on `" — "` →
verb (Atkinson Bold 14sp, ls 0.4sp) + rest (same, with digit runs
re-styled Anonymous Bold 14sp — reuse the `SpanStyle` pair from
`tallyText`). If the rest contains `", "`, split there: head stays on
line 1, tail becomes line 2 (Anonymous Bold 11sp, ls 0.6sp,
`onBackground.copy(alpha = 0.72f)`, `Spacer(2.dp)` between lines, line
2 padded `start = 18.dp` only when it is a separate composable — if
both lines live in the text column right of the glyph, no extra pad:
the column already starts at the text axis; **the 18dp figure in §2.5
is that axis, not an additional indent**). Segment-drop for LIVE per
§2.6: measure with `TextMeasurer` only if VOGT wants the dropper in
this merge; otherwise end-ellipsis + the filed follow-up.

**PZ-3 — Banner into the status item** (move, ~0 net lines). Delete
the standalone banner `item { … }`; its `AnimatedVisibility` moves
inside PZ-1's `Column`. Net effect: −24dp of dead gap on every
banner-less day.

**PZ-4 — Routine name: evict Sora** (2 lines). In `RoutineRow`:
`fontFamily = SoraFamily, fontSize = 16.sp` → `style = AppTitleTextStyle`
(import `com.example.ui.theme.AppTitleTextStyle`), add
`maxLines = 1, overflow = TextOverflow.Ellipsis`. Remove the
`SoraFamily` import if this was its last use in the file.

**PZ-5 — The cap: word, width, radius, densities** (~20 lines).
Replace the fixed 48dp box:

```kotlin
val capWord = routineCapWord(routine.enabled, routine.daysMask)
val running = routineRunningNowSuffix(routine, nowMillis).isNotEmpty()
Box(
    modifier = Modifier
        .widthIn(min = 48.dp)
        .heightIn(min = 32.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(when (capWord) {
            "ON" -> inkControlFill()
            else -> scheme.surfaceVariant
        })
        .border(1.dp, when (capWord) {
            "ON" -> inkControlFill()
            else -> scheme.outlineVariant
        }, RoundedCornerShape(12.dp))
        .clickable { onToggle() }
        .padding(horizontal = 10.dp, vertical = 8.dp),
    contentAlignment = Alignment.Center
) {
    Text(
        text = capWord,
        fontFamily = AnonymousProFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        maxLines = 1,
        color = when (capWord) {
            "ON" -> onInkControlFill()
            "WON'T FIRE" -> scheme.onBackground
            else -> scheme.onSurfaceVariant
        }
    )
}
```

`RoutineRow` gains a `nowMillis: Long` parameter, passed from the
screen's existing `nowMillis` at the `routines.forEach` call site
(1 line there). `running` is computed once and feeds both PZ-6 and
the gutter echo — never two `isActiveNow` calls per row (§3.7).

**PZ-6 — Meta line: suffix as its own Text + gutter echo** (~12 lines).
Meta `Text` → `Row(Modifier.weight(1f))` per §3.2/§3.4: base text
weighted + ellipsised, suffix text unweighted (text =
`routineRunningNowSuffix(...)` result — or reuse `running` and print
the constant ` · RUNNING NOW`; prefer the helper's return so the
string has one home). End-time `Text`: colour/weight become
`if (running) onBackground/Bold else onSurfaceVariant/Normal`.
Header row gains `.heightIn(min = 48.dp)`; the two `Spacer(12.dp)`
of §3.2 are inserted.

**PZ-7 — Header axis + note gap** (3 lines, **sequence with R-12
item 1**). If the global header pass has not removed
`padding(end = 3.dp)` from `FullBleedSectionHeader`'s right word,
do not patch it locally per-call-site (the header takes no such
parameter) — instead this patch is *only* the reset-note gap:
note `padding(top = 6.dp)` stays, the `Spacer(4.dp)` after the
header block becomes `Spacer(8.dp)` when `showResetNote` is true
and stays 4dp otherwise… simpler and preferred: leave both spacers,
change the routines `Column` top spacing by wrapping: first row
unchanged. **Honest simplification:** the visible defect is small;
if VOGT cuts one patch, cut this one and let item 1's global pass
carry the axis. The ARMED dress itself needs no patch.

**PZ-8 — Expanded body seating** (~8 lines). Both expanded `Row`s
gain `.padding(start = 66.dp)`; insert above them:
`Spacer(8.dp)` + `Box(Modifier.fillMaxWidth().height(1.dp)
.background(scheme.outlineVariant))` + `Spacer(8.dp)`, replacing
the flat 8dp seam. Zero-routines `NONE` line per §3.7 (~10 lines)
rides here if VOGT wants it in this merge.

**Acceptance checks (visual, no compile — for MONTEUR's preview /
VOGT's screenshot pass):**
1. 360dp + 411dp widths, fontScale 1.0: LIVE line with the longest
   seeded routine name fits one line, `ENDS` visible; NO_SESSION
   fits; glyph densities match §4 at arm's length in both themes.
2. UNPROVEN (kill a11y mid-session on device): two lines inside the
   40dp slot, nothing below moves, reason fully readable — no
   ellipsis anywhere in the sentence.
3. A `daysMask = 0` routine beside an ON and an OFF routine: three
   caps, three densities, `WON'T FIRE` whole at 79dp, ON/OFF caps
   still 48dp, all right edges on one axis.
4. A routine inside its window: suffix present and unbolded meta
   ellipsises *before* the suffix ever clips (use a six-day mask);
   end time in the gutter is bold ink.
5. Banner absent: status-to-tally gap is the specced tight gap, no
   24dp hole; banner present (ratchet refusal): it sits 8dp under
   the sentence and the sentence has not moved.

---

## Draft GitHub comment (for VOGT to post)

— PANZER (FINISHER), for VOGT's issue #7 cell.

Finish delivered for my slice: `studio/cells/issue-7/finisher-panzer.md` (DECLARED — specification only, no `app/src/` edits, nothing compiled). It covers the R-5 status sentence and the routine rows (R-12 items 7–8); eight exact patches (PZ-1…PZ-8) are written for MONTEUR against the post-R-1 file.

Top decisions: (1) The status line never wears a hue — MONTEUR's current `scheme.error` dress measures 3.49:1 by day and spends the night accent on print; every state goes full ink (10.8:1 day / 11.0:1 night) and state is carried by a 10dp solidity glyph (solid = BLOCKING NOW, hollow = NOT BLOCKING, 55% = unproven/stale) plus the verb. (2) Type goes back to the house grammar — Atkinson Bold 14sp words, Anonymous Pro numerals (the tally's construction) — inside a fixed 40dp slot with the tally's 260ms Curve-A whole-line re-strike, so a state flip moves nothing below it. (3) The long UNPROVEN headline is never ellipsised (ellipsis would amputate the diagnostic tail): it splits at its comma into two lines inside the same slot. (4) The ratchet banner moves inside the status item — as a separate item it leaves a dead 24dp gap under the sentence every banner-less day. (5) Routine caps become one grammar in three densities — ON solid ink, WON'T FIRE recessed with a full-ink word, OFF recessed with a quiet word — payload-sized (`widthIn(min = 48dp)` + 10dp padding; WON'T FIRE needs 79dp and physically cannot fit today's fixed 48dp box), radius 8→12dp per the shapes law, wired to the already-written `routineCapWord()`. (6) The Sora guest is evicted from routine names (Atkinson Bold 15sp, the existing `AppTitleTextStyle`), and `RUNNING NOW` prints as a separate unweighted bold suffix so truncation can never eat it; the running routine's end time also promotes to bold ink in the time gutter. One recorded deviation for VOGT: I move FORM's 55%-ink *text* for the unknown state to the glyph (55% text measures ≈3.4:1 by day — too weak for this line) and widen her 28dp slot to 40dp, because MONTEUR's real UNPROVEN strings are longer than her examples. Sequencing flag: the tree is mid-R-1 (slip rendering still calls the deleted slip machinery); my patches touch no slip code and apply in either order, but the top-of-screen gap law assumes R-1's final order — status, tally, cap, routines.

— PANZER, FINISHER 1, VOGT's issue #7 cell — Oct 30, 2026. DECLARED.

---

*PANZER, FINISHER 1, VOGT's issue #7 cell — Oct 30, 2026. DECLARED: specification
only; no code edited, nothing compiled, no tests run, nothing committed or pushed.*
