# Plan — Linea (Improvers) · The Deck Task Line (NOW · title · DONE · QUEUE)

Element: Section 2 of `TactileDeckScreen.kt` — the one line under the instrument
panel that confirms what is armed. Plan wave only; no code.
Coordinator: Ilya. Neighbour: Zeile (task rows, Tasks screen) — same tasks, two
renderings; mail sent, see CONFLICTS.

What changed since wave 1, and governs everything below:

- **The night staircase is real now.** Ground `#171412`; text tiers
  `#D2C6BB` 11:1 (primary) / `#9C8E84` 5.8:1 (secondary) / `#776A60` 3.5:1
  (muted) — `Color.kt` night tokens, verified in code. Wave 1's proposed new
  hex `#B9ACA3` is **superseded and dead**: no new hex anywhere in this plan.
- **Real font weights are real.** Atkinson Hyperlegible ships only 400/700;
  Anonymous Pro only 400/700 (`Type.kt`, verified). Every "Medium" below was
  a lie — see CURRENT. This plan specs only weights that exist.
- **HOLD is law, and wave 1 didn't have it.** Mid-session this line is a
  readout. Wave 1's peak kept DONE, step toggles and QUEUE tappable while
  running; that is cut at Rung 4, with the argument stated there.
- **Ledger is dead.** DONE does not feed a tab. It records the completion
  (`completedInSessionId` already exists on the entity) and the receipt
  surfaces at session end. This line's job after DONE is only: advance.

---

## CURRENT — re-verified in code today, not inherited

Structure (Section 2, `TactileDeckScreen.kt`):

- Rendered **only** `if (activeTask != null)`. No task → the element does not
  exist; the scroll column's `spacedBy(12.dp)` collapses around the hole and
  the Deck's composition changes height when the last task completes.
- Bare `Column(spacedBy(6.dp))`, no padding, no background, no rule. It starts
  at the 12dp page margin; every panel label starts at 12 + 16 = 28dp. Two
  left edges, still.
- Main row: `Row(fillMaxWidth, CenterVertically, spacedBy(10.dp))`, four bare
  `Text`s, two of them `clickable` with **zero padding** — hit height is the
  ~15dp glyph box on the line's only two actions.
- Sub-steps: full-width rows starting at x = 0 (left of the title they belong
  to), `padding(vertical = 3.dp)`, whole row clickable → `onToggleTaskSubStep`.
  Expansion is a plain `remember { mutableStateOf(false) }` flip: instant
  insert/remove, no animation, no haptic, no sound anywhere in the section.

Tokens as they resolve **today** (wave-1 table is stale in every night cell):

| Slot | Spec in code | Day | Night (staircase) |
|---|---|---|---|
| `NOW` | Anon Pro Bold 10.5sp, ls 1sp, `onBackground` | `#2C2A29` | `#D2C6BB` 11:1 |
| Title | Atkinson "Medium" 15sp, `onBackground`, 1 line, ellipsis | `#2C2A29` | `#D2C6BB` 11:1 |
| `DONE` | Anon Pro Bold 11sp, `onBackground`, bare clickable | `#2C2A29` | `#D2C6BB` 11:1 |
| `QUEUE n` | Anon Pro Regular 11sp, `onSurfaceVariant`, bare clickable | `#5F5856` | `#776A60` **3.5:1** |
| Step № | Anon Pro 11sp, `"0${idx + 1}"`; done → `primaryContainer` | `#5F5856` / done `#F55314` | `#776A60` / done `#F47442` |
| Step title | Atkinson 13sp; done → `onSurfaceVariant` + `LineThrough` | — | — |

The honest faults, updated:

1. **It still floats.** No hairline, no inset, no shared axis with the panel.
   Wave 1's ★ is unbuilt.
2. **The title's weight is a lie.** Code asks Atkinson Medium; the family has
   no Medium, so it renders Regular. Any hierarchy built on "Medium vs Bold"
   in this element is built on air until specced in 400/700 only.
3. **Night demotion overshot.** Wave 1's complaint was QUEUE too bright
   (`#FAE4D3`, indistinguishable from DONE). The staircase fixed the *scale*
   but this line still binds QUEUE to `onSurfaceVariant`, which at night is
   now the **3.5:1 muted tier** — a tappable 11sp word set in the tier meant
   for decoration. The hierarchy exists; the door is barely legible. QUEUE
   belongs on the **5.8:1 secondary tier** (`NightTextColorSecondary`).
4. **The payload still truncates.** Title capped at 1 line, ellipsis, squeezed
   between `NOW` and two actions. The one fact this line exists to confirm
   can be cut off by its own furniture.
5. **Expansion is still undiscoverable.** Steps present vs absent render
   identically; nothing says the title is a tap target.
6. **Done steps still spend the accent** (number → orange) **and double-encode**
   (strike + mute). Orange in this element: zero tolerance — the Deck's one
   accent lives on the slide.
7. **`"0${idx + 1}"`** still renders step 10 as `010`.
8. **Baselines still float**: `CenterVertically` across three type settings,
   no baseline alignment, no letterpress on the labels while Section 1's
   labels carry it (day: `Shadow(0x66FFFFFF, Offset(0,1), blur 0)`; night:
   none — verified in Section 1 code).
9. **It is live mid-session.** DONE, step toggles and QUEUE are all tappable
   while `isSessionRunning` — three mid-session decisions sitting under the
   readout, against HOLD. (Section 2 doesn't even branch on session state.)
10. **Truth is shared but unguarded.** Deck and Tasks receive the *same*
    `activeTask` / `queuedTasks` flows from `MonolithViewModel` (verified:
    `MainActivity` collects both once and passes the same instances to both
    screens) — so agreement is currently free. Nothing in this element
    protects it: any future local count, cached title or re-derivation
    breaks TRUTH once silently. One wrinkle, flagged not fixed: the
    ViewModel's `activeTask` falls back to the first QUEUED task when no
    ACTIVE exists, so `NOW` can sit over a task Tasks calls QUEUE-lead.
    Both screens agree with each other; the word itself is the question.
    Mailed to Zeile — a rename must be joint or it becomes a mismatch.

What stays, in spirit: no card, no chips, no icons; pure text with text
actions; numbered text steps; DONE in ink, QUEUE quieter.

---

## LADDER — 4 rungs, each independently shippable, in order

Curves are `MonolithCurves` only: **A** `cubic-bezier(0.2, 0, 0, 1)`,
**B** `cubic-bezier(0.05, 0.7, 0.1, 1)`, **C** `cubic-bezier(0.3, 0, 0.8, 0.15)`;
durations only from {180, 260, 280, 420, 520} ms. Haptics are hand-set
`VibrationEffect` one-shots at default amplitude unless a waveform is written
out. Ripple does not exist in this app (purged at theme level) — press
feedback is colour/alpha only, everywhere below. Depth: this element has
**no elevation, no shadow, no fill at any rung**; its only depth device is
the letterpress text shadow Section 1 already uses, day only.

### Rung 1 — Anchor and repair *(one sitting; ★ lives here)*

- ★ **Hairline anchor** — specced and priced in ★ below.
- **Inset:** whole block `padding(horizontal = 16.dp)` → left edge lands on
  the panel's inner axis, 28dp from screen edge, under `FOCUS DURATION`.
- **Hit areas:** DONE and QUEUE get `padding(horizontal = 10.dp,
  vertical = 10.dp)` *before* `clickable`; row height ~20 → ~40dp. Mechanism:
  padding-first so the hit box covers the padded area, glyphs unmoved.
- **Numbering:** `"0${idx + 1}"` → `String.format("%02d", idx + 1)`.
- **Staircase tones (token swaps only, no new tokens):** QUEUE binds to the
  text-secondary token — day `TokenTextColorSecondary #615B5A`, night
  `NightTextColorSecondary #9C8E84` (5.8:1) — replacing `onSurfaceVariant`
  (night 3.5:1) for this element. Step numbers and done-step titles take the
  same secondary tier. Hairline/thread/rule tones stay `outlineVariant`
  (day `#75615B5A`, night `#758F8888`).
- **Done step, de-accented:** number → primary text tone + Bold (real 700);
  title → secondary tier; **`LineThrough` deleted, orange deleted.** One
  encoding (tone) + one weight signal in the number column. No alpha —
  alpha over the night ground produces unmeasured ratios; the staircase
  tiers are measured, so the tiers do the work.
- **Baselines & press:** the four main-row texts align `ByBaseline`;
  `NOW` / `DONE` / `QUEUE` take Section 1's exact letterpress — day
  `Shadow(Color(0x66FFFFFF), Offset(0f, 1f), blurRadius 0f)`, night
  transparent. Same press as the panel, one floor down.
- **Motion:** none. **Haptics:** none. **Sound:** none. This rung is print,
  not mechanism — it ships silent and still.

*Result: attached, tappable, legible at night, no orange leak. The line is
findable. Everything after this is voice.*

### Rung 2 — The title gets its space, and a truth lock

- **Columns, locked:** block inset as Rung 1. Main row min-height 48dp:
  `NOW` fixed 36dp · 1dp × 14dp vertical rule (`outlineVariant`, centred on
  the title's first line) · gap 12dp · title `weight(1f)` · gap 16dp ·
  `QUEUE n` · gap 8dp · `DONE` last, rightmost, its right glyph edge on the
  block's right edge. (Order change from today: DONE goes last — it is the
  verb and the thumb's nearest target; QUEUE is a door and sits inside.)
- **Title: two lines.** Atkinson **Regular 400** (the only honest weight
  below Bold), 16sp / 21sp leading, tracking −0.1sp, `maxLines = 2`,
  ellipsis only after line 2. DONE/QUEUE top-align to line 1 (padding-top
  1dp). Mechanism: row grows with the title; the confirmation can no longer
  be truncated by a long chapter name — ARM ≤10s starts with being able to
  read the whole task.
- **Affordance:** after the title, on its baseline — step count `2/5` in
  Anonymous Pro Regular 11sp, secondary tier, derived **only** from the
  entity's own getters (`completedSubStepsCount` / `subSteps.size`), then a
  `›` chevron, Anonymous Pro 14sp, secondary tier, in a 32 × 48dp hit zone
  merged with the title's tap target. No steps → neither renders; absence
  is the state.
- **Expansion geometry:** sub-step rows indent to the **title axis**
  (36 + 12 = 48dp from block start). Number column fixed 22dp (`%02d`),
  gap 10dp, title `weight(1f)`, rows 36dp tall, air only between them.
  Step states: *done* — number Bold + title, both secondary tier;
  *next-undone* — number Bold primary, title Regular primary;
  *later* — number Regular secondary, title Regular primary. The number
  column alone reads the list's progress.
- **Motion:** expand `AnimatedVisibility(expandVertically + fadeIn)`,
  280ms curve B, steps stagger `idx × 24ms` capped 96ms, initial offset
  4dp; collapse 220→**180ms** curve A (duration vocabulary), fade-out
  180ms curve A; chevron rotates 0° → 90°, 180ms curve A
  (`animateFloatAsState`). Step toggle: number/title crossfade 180ms
  curve A; no layout movement (row heights fixed).
- **Haptics:** expand one-shot 4ms; collapse 2ms; step toggle 4ms;
  QUEUE tap 1ms. All `createOneShot(ms, DEFAULT_AMPLITUDE)`.
- **Sound: silent at this rung, deliberately.** Browsing and toggling are
  reading acts; the engine's clicks belong to commits (Rung 3 spends the
  one sound this element owns).
- **TRUTH lock (the rung's real payload):** this element renders **only**
  from the `activeTask` / `queuedTasks` parameters it is handed — title
  verbatim `activeTask.title`, `QUEUE n` = `queuedTasks.size`, count from
  the entity getters above. No local copies, no cached counts, no
  re-derivation, no formatting of the title beyond wrapping. A comment at
  the section head states the contract: *one fact, stated here as the
  Deck's confirmation; the fact lives in the ViewModel flows both screens
  share.* If Tasks and Deck ever disagree after this rung, it is a data
  bug, never a rendering choice.
- **No-task state (new):** the block renders instead of vanishing —
  hairline + `NOW` + `Nothing queued` (Atkinson Regular 15sp, secondary
  tier) + `QUEUE 0` as static text; DONE omitted; the **whole row** is one
  48dp tap target → `onOpenTaskSequencer()`. Haptic 1ms, silent. (Under
  the current ViewModel fallback, no-active implies queue-empty, so
  `QUEUE 0` is the only honest numeral here — no fantasy states specced.)
  Layout stops jumping; the logbook's next line is already ruled.

### Rung 3 — DONE earns its stamp (idle only), the line learns to advance

- **DONE press (idle / pre-arm only):** mechanism — DONE is the element's
  one commit, so it gets the element's one full feedback stack, fired as a
  **single owner**: `soundEngine.playCompletionPulse()` and nothing else.
  That call already bundles its haptic waveform (timings
  `[0, 10, 24, 10, 24, 14]` ms, amplitudes `[0, 175, 0, 125, 0, 80]` —
  three ticks softening as they land) with its sound (185Hz fundamental,
  64ms, gain 0.68, chord 1.33 — the machine winding down, not alarming).
  No additional `VibrationEffect` on this gesture: one gesture, one owner,
  one waveform. Pressed state before release: DONE alpha 1 → 0.55,
  180ms curve A, no ripple (none exists), no scale.
- **What DONE does:** calls `onCompleteActiveTask(activeTask.id)` exactly
  as today. The completion is recorded on the entity
  (`completedInSessionId`); the receipt is a session-end event, not this
  line's business and not a tab's. This line neither shows a toast nor
  navigates anywhere on DONE.
- **Advance animation:** on the task change, outgoing title fades + rises
  3dp over 180ms curve A; incoming title enters over 260ms curve B after a
  60ms delay; expanded steps collapse first (180ms curve A) if open.
  The logbook turns a line; the text never hard-swaps. QUEUE numeral
  crossfades in the same 260ms window — it changes *because* the fact
  changed, in the same motion, so the eye catches both as one event.
- **Step toggle sound:** stays silent (Rung 2's call stands — toggles are
  reversible edits, and a click on every edit cheapens the one commit
  sound). Haptic stays the 4ms one-shot.
- **Depth/tokens:** unchanged from Rungs 1–2. No new tokens, no orange,
  no fill introduced by celebration. The stamp is felt and heard, never
  seen as decoration.

### Rung 4 — HOLD: the line locks when the session starts

The rung wave 1 could not write, because HOLD wasn't law yet. Argument,
once: a mid-session DONE is a decision (is this task finished? — made by
the tired user, about the armed confirmation, retroactively editing what
he armed); a mid-session step toggle is state editing; a mid-session
QUEUE tap is navigation away from the instrument. HOLD says the tired
user executes and the rested user decides — so at session start every
one of them closes. What remains is the purest version of the line's job:
*what you are doing, read at a glance, untouchable.*

- **Mechanism:** Section 2 branches on `isSessionRunning` (it doesn't
  today). Running rendering = the same composables with **no `clickable`
  anywhere in the block**: DONE and QUEUE are not rendered as actions at
  all — DONE is **absent** (its absence is the lock; a greyed DONE would
  still invite the decision), QUEUE n remains as **static text** in the
  secondary tier (it is a fact, and facts stay readable), chevron absent,
  title tap inert. Steps: if expanded at arm, they stay visible and inert
  (reading is not a decision); if collapsed, they stay collapsed — the
  count `2/5` remains as static text.
- **Running differences, complete list (three, no colour anywhere):**
  (1) title goes Atkinson **Bold 700** — a real weight now, and the jump
  400 → 700 is the loudest quiet signal the family owns;
  (2) a **5dp filled square** appears left of `NOW` (day `#2C2A29`, night
  `#D2C6BB` — primary text tone; state by shape and position, readable in
  grayscale and peripheral vision);
  (3) a **second hairline** closes the block below the row (or below the
  frozen step list), same `outlineVariant` token, 8dp under the last
  row — Section 3's DESK CLOCK / HELD chips sit 12dp below that, and the
  entry needs its bottom rule to stay a separate entry.
- **Transition in:** on arm, the lock lands in one motion — DONE fades
  out 180ms curve A, square fades in 180ms curve A, title weight
  crossfades via the advance mechanism's 260ms curve B window, closing
  hairline draws in with the block's height animation 280ms curve B.
  No haptic, no sound of its own: the slide's seal owns the arm moment;
  this line changes *under* that sound, never over it.
- **Sounds/haptics while running: none, because there are no gestures.**
  Wave 1's "suppress sounds, keep haptics" is moot once there is nothing
  to press — the cleanest suppression is an absent target.
- **Accessibility floor (all rungs, stated here once):** every action
  ≥ 48dp tall; title + chevron one merged 48dp expand target,
  contentDescription `"Show steps, 2 of 5 done"` (numbers from the same
  entity getters); step rows 36dp visual / 48dp effective via the
  padding-before-clickable construction; running state exposes the block
  as a single read-only semantics node: `"Now: <title>. 2 of 5 steps
  done. <n> queued."` — every number in that sentence from the Rung 2
  truth lock, nowhere else.

---

## ★ — Best-ratio change (priced)

**Insert one hairline as the first child of the Section 2 `Column`, and
inset the block 16dp to the panel's axis, in the same edit.**

- **Spec:** `HorizontalDivider(thickness = 1.dp, color =
  scheme.outlineVariant)` as first child; block `Column` gains
  `padding(horizontal = 16.dp)` and its internal spacing becomes 8dp, so
  the rule sits 12dp under the panel (the scroll column's existing gap)
  and the NOW row 8dp under the rule.
- **Price:** one composable + one padding modifier + one spacing change.
  ~15 minutes including the render check. Zero new tokens, zero state,
  zero behaviour change, zero callbacks touched. Independently revertible
  in one commit.
- **Return:** the element's one structural fault — a line floating 12dp
  under a panel it doesn't touch, on a different left edge — is gone. The
  eye gets panel → rule → entry as one printed block, and the void below
  stops reading as unfinished. Every later rung polishes a line the user
  can find; this one makes it findable. Nothing else in the ladder comes
  close on return per line changed.

---

## PEAK

The panel is an instrument; this line is its logbook entry — one line
printed underneath, in the same press, on the same column, in ink one
tier quieter than the readout. It confirms the armed task instantly
(whole title, two lines, nothing truncated), states the queue once in
the same numerals Tasks uses, and spends its single loud moment on DONE
before the session starts. The instant the session starts it locks into
a pure readout — bold title, square, bottom rule, no targets — because
mid-session there is nothing here to decide. Empty, it keeps its ruled
line and says so. It never carries orange, a card, or a second copy of
any fact.

---

## STANDARDS CHECK

- **ARM ≤10s — PASS.** The line is the arm confirmation: whole title at
  16sp on the panel's own axis, under a hairline that ties it to the
  instrument, readable in one fixation. No truncation of the payload
  (Rung 2), no hunting for the element (Rung 1). It adds zero steps and
  zero time to the arm path; it only makes the confirmation legible.
- **HOLD absolute — PASS, and it cost wave 1's favourite features.**
  Rung 4 removes every mid-session action: no DONE, no step toggles, no
  QUEUE navigation, no expansion changes while running. The only
  mid-session verbs left are reading. Each cut is argued in Rung 4, not
  assumed. Residual: step toggles and DONE remain available *pre-arm*,
  which is where decisions belong (rested user).
- **TRUTH once — PASS.** Title, queue count and step count render only
  from the shared `activeTask` / `queuedTasks` flows and the entity's
  own getters (Rung 2 truth lock); identical source to the Tasks screen,
  so the Deck states each fact once as confirmation and Tasks states it
  once as the list — same numbers, by construction. DONE feeds the
  session-end receipt via the existing completion record; no Ledger tab
  is referenced, implied or needed. The line never invents provenance:
  a routine-armed task and a hand-armed task render identically, because
  the entity carries no source field and this line does not guess one.
- **His picks — PASS.** Atkinson (400/700 only — real weights), Anonymous
  Pro (400/700), no Sora needed at this size; warm palette throughout;
  staircase tiers at night; **zero green, zero orange in this element in
  any state.**

---

## CONFLICTS

1. **With wave 1 (mine, superseded on purpose):** (a) night hex `#B9ACA3`
   — dead, replaced by staircase token `#9C8E84` 5.8:1; (b) 55%-alpha done
   titles — dead, replaced by measured tiers; (c) DONE / toggles / QUEUE
   live while running — dead under HOLD, cut at Rung 4; (d) Atkinson
   Medium/SemiBold specs — impossible, family has neither; (e) expansion
   sound at peak — dead, DONE's `playCompletionPulse` is the element's
   only sound, single-owner.
2. **With Zeile's wave-1 task rows (open, mailed):** her rows keep a drawn
   strikethrough on done steps; this line deletes the strike and carries
   done by tone + bold number. Same steps, two renderings, two encodings —
   a TRUTH-once smell even if the facts agree. I propose tone-only on both
   screens; if she holds the strike, I will take the drawn strike here
   rather than diverge — convergence matters more than my preference.
   Also mailed: her meta fraction (`#01 · 25m · 2/3`) must derive from the
   same entity getters as my `2/5`, and the `NOW` vs QUEUE-lead fallback
   naming (CURRENT §10) needs a joint answer, not two unilateral ones.
3. **With the current code's session behaviour:** Section 3's chips and
   the grace/abort bar are live mid-session by his design (desk clock,
   held notifications, emergency valve) — those are session *utilities
   and the exit*, not task decisions, and are outside this line. I flag
   only that my Rung 4 lock makes Section 2 stricter than Section 3, and
   that asymmetry is deliberate: utilities serve the session, the task
   line *is* the session's content.
4. **Mail sent:** `mail/zeile/0516__from-linea__deck-tasks-truth.md` —
   strikethrough convergence, shared-getter counts, NOW/fallback naming.
   No reply yet at delivery; the letter waits for her next seating.
