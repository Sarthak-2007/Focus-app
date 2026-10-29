# 12 — Composer Field, PLAN WAVE — Fach (Improvers)

Element: the task-entry field on Tasks (`FocusSequencerScreen.kt`, Section 2) **plus the
collapse behaviour** — Synthesis item 28: sticky single line (field + inline duration +
ADD as text), key bank appearing only while the field is focused. The bank's insides
belong to Taste; the slot, the focus state, and the seam belong to me (mailed to Taste,
0524). Plan only — no code.

Conventions in every rung: curve A = cubic-bezier(0.2, 0, 0, 1), B = (0.05, 0.7, 0.1, 1),
C = (0.3, 0, 0.8, 0.15) — the app's `MonolithCurves` EasingA/B/C. Durations from the
180 / 260 / 280 / 420 / 520 family. All sound+haptic pairs are single
`TactileSoundEngine` calls (the engine couples a waveform haptic and a PCM transient in
one call; it offers no silent haptic, so "no haptic" below means no engine call).
Fonts are his three, as they actually ship: Atkinson Hyperlegible **Regular + Bold only**,
Anonymous Pro Regular + Bold, Sora for display. Warm palette, never green.

---

## CURRENT — re-verified in code today (wave-1 findings, audited)

Read from `FocusSequencerScreen.kt` §2, `Color.kt`, `Theme.kt`, `Type.kt` as they stand
now — not from the stale renders (`shots/03_tasks.png`, `shots/night/n03_tasks.png`
pre-date the night staircase and the font fix).

| Wave-1 finding | Verdict today |
|---|---|
| **Zero focus handling (wave-1 ★)** | **CONFIRMED.** No `FocusRequester`, no `onFocusChanged`, no focus-dependent modifier anywhere in the file. Focused and unfocused are pixel-identical. |
| Placeholder near-cream at night | **FIXED at token level — wave-1 claim is stale.** Night `onSurfaceVariant` is now `#776A60` (Color.kt's own note: 3.5:1 muted tier, "was #FAE4D3 at ~14.9:1"). Day is `#5F5856`. Input text is the primary tier (day `#2C2A29`, night `#D2C6BB` ≈11:1). The placeholder no longer reads at input strength in code. Do not re-fix with alpha — that double-demotes below the tier the staircase designed. |
| Placeholder / input weight pop | **CONFIRMED, and worse than wave-1 knew.** Placeholder `Text` sets no weight (Regular); input is Bold; both 13sp. First keystroke pops weight and width. And wave-1's fix (Medium/Medium) is **unbuildable**: `PlusJakartaSansFamily` is now an alias for Atkinson Hyperlegible, which ships Regular + Bold only — there is no Medium file to resolve to. |
| Silent empty submit | **CONFIRMED.** `submitTask()`: `if (clean.isNotEmpty()) { … }` — no else. ADD and IME Done on an empty field are dead presses, zero signal. |
| Same-frame-erasure submit | **CONFIRMED.** On submit: latch sound, `onAddTask(...)`, `newTaskTitle = ""` — same frame, no animatable, no stamp state, no entry motion specced for the queue row from this section. |
| Uniform border, claimed recess | **CONFIRMED in form, improved in tokens.** Still one 1dp `outlineVariant` border on all four sides. But the fill now lands on the rebuilt staircase: day `surfaceVariant #D8D1CB` on ground `#E4DFDA`; night `surfaceVariant #100E0C` on ground `#171412` — **a real sink below ground now exists** and the field already inherits it. The recess is one honest token away from being built; the border is what still lies about it. |
| Accent spent on caret | **CONFIRMED.** `cursorBrush = SolidColor(scheme.primaryContainer)` — day `#F55314` / night `#F47442`, a permanent pulsing second orange above the queue's START. |
| Not sticky (new, post-Synthesis) | **CONFIRMED.** The composer is a plain `item {}` inside the screen's `LazyColumn`. Scroll the queue and the front door scrolls away with it. Item 28's "sticky" does not exist in any form. |
| What already works | `newTaskMinutes` (default 25) is **retained across submits** — never reset. Nothing calls `clearFocus()`, so IME retention works today *by accident*. The collapse makes both load-bearing; Rung 2 pins them as contract instead of luck. |

Resting geometry today: field 48dp high, radius 12dp, fill `surfaceVariant`, inner
padding 12dp (glyph axis 24dp from screen edge), key row always visible beneath
(10dp gap, caps 36dp). Total resting composer ≈ 94dp to say one line.

---

## LADDER

### Rung 1 — Focus exists (truth & touch, no layout change)

- **Mechanism:** one `var isFocused by remember` + `Modifier.onFocusChanged` on the
  `BasicTextField`; a `FocusRequester` attached (requested by nothing yet — Rungs 2/5
  use it). Submit routes through one `onComposerCommit()` wrapper (ADD tap and IME
  Done call the same branch — they can never diverge again).
- **Caret:** `primaryContainer` → `SolidColor(scheme.onBackground)` (day `#2C2A29`,
  night `#D2C6BB`). Ink caret; the screen's orange returns to START, whole.
- **Type:** placeholder and input both **Atkinson Regular 14sp** (was Regular-13 /
  Bold-13). Copy: `"Add single-line focus task..."` → **`"New task"`**. Placeholder
  colour stays `scheme.onSurfaceVariant` (day `#5F5856`, night `#776A60`) — the muted
  tier against the input's primary tier is the whole placeholder/content distinction.
  First keystroke now changes glyphs only.
- **Focus depth:** a black overlay `Box` above the fill, below the text;
  `animateFloatAsState` alpha 0 → **0.05 day / 0.16 night**, **180ms, curve A**.
  Border strengthens alongside: `animateColorAsState` `outlineVariant` → `outline`,
  180ms, curve A (a bridge — Rung 3 deletes the border entirely). Unfocus reverses
  identically; the well relaxes, never snaps.
- **Empty submit, no longer silent:** `soundEngine.playRotaryDetent(320.0)` — waveform
  **[0, 7ms @ 180, 5ms @ 70]**, transient 320Hz / 6ms / gain 0.38 (the engine's lowest
  tick: *registered, nothing filed*) — plus the placeholder **re-strikes**: alpha
  1 → 0.45 → 1, **240ms, curve C**. No shake, no toast, no scolding text.
- **Sound / haptic otherwise:** focus gain/loss and typing — **none**. The engine has
  no silent haptic and focus is navigation, not a machine action.
- **Depth / tokens:** no new tokens, no new literals. Ships alone.

### Rung 2 — The collapse (sticky single line, bank only while focused)

- **Sticky mechanism:** the composer leaves the `LazyColumn`. Screen root becomes a
  `Column`: header, composer (fixed), queue `LazyColumn` at `weight(1f)`. The front
  door is reachable from any scroll position — returning to it costs **0 scroll taps**.
  Composer pinning must not depend on the Horizon item above it (that bar dies per
  Synthesis 29; the header numeral is not mine).
- **Collapsed line (the resting state):** 48dp high, radius 12dp, fill
  `surfaceVariant` (day `#D8D1CB`, night sink `#100E0C`). Left → right: text slot
  (inset 12dp — glyph axis 24dp, **identical in both states; expansion is vertical
  only, horizontal geometry never moves**) · duration echo · 1dp vertical hairline,
  20dp tall, `outlineVariant` · **ADD as text**. Echo: Anonymous Pro **Bold 11sp**,
  `onSurfaceVariant`, format **`· 25M`** — rendered directly from the same
  `newTaskMinutes` Int that submit passes to `onAddTask`. One state variable, no
  shadow copy: the echo cannot lie about what will be queued.
- **ADD as text:** Atkinson **Bold 13sp**, letterSpacing 1.2sp, inside a full-height
  (48dp) hit zone, min width 60dp, 12dp end padding. States follow the text, truthfully:
  field empty → `onSurfaceVariant` (dimmed — there is nothing to add); first
  non-whitespace character → `onBackground` ink, colour animated **180ms, curve A**.
  Press travel: `graphicsLayer` translationY **+1dp, 80ms curve A** in, return
  **180ms curve C**. Tapping ADD while dimmed does not submit: it requests focus into
  the field (Rung 1's `FocusRequester`) + the Rung 1 detent — a redirect, not a dead key.
- **Bank slot (Taste's container, my animation):** below the field, an 8dp gap, then a
  **52dp** container that exists only while `isFocused`. Expand: height 0 → 60dp
  (gap + container) **260ms, curve B**; content alpha 0 → 1 over **120ms, curve A**.
  Collapse: alpha → 0 in **100ms, curve A**, height → 0 in **180ms, curve A**. The bank
  fades before it shrinks — it is never seen half-clipped mid-sentence.
- **Focus contract (the load-bearing spec):** submit **never** calls `clearFocus()`;
  duration caps are **non-focusable** (`focusable = false`) and never clear focus;
  tapping a cap does not dismiss the IME. If focus drops, the bank closes mid-entry
  and rapid entry dies — so focus retention across submit is a tested invariant of
  this rung, not a hope. Minutes are retained across submits (already true in code;
  now contractual).
- **Sound / haptic:** none new in this rung. Duration-selection feedback inside the
  slot is Taste's; my echo re-strikes instantly with no animation (a computed readout
  does not travel through intermediate values).
- **Depth / tokens:** collapsed line has no fills inside it — echo and ADD are text
  printed on the well floor. Resting composer height: 94dp → **48dp**.

### Rung 3 — The recess, built; the caret becomes a block

- **Mechanism:** delete the uniform 1dp border. Layer stack inside the clipped 12dp
  field, outer → inner: (1) well floor `surfaceVariant`; (2) **top pooled shadow** —
  top-aligned gradient `Box`, 7dp tall, black alpha **0.18 day / 0.50 night** →
  transparent; (3) **left pool** — 5dp wide, black alpha **0.10 day / 0.28 night**;
  (4) Rung 1's focus-deepening overlay; (5) **bottom catch-light** — 1dp line inset
  12dp from the corners: day `surfaceBright #FAF6F0` @ 90%, night
  `NightColorSurfaceBright #362C27` @ 100%. On focus the catch goes to full strength
  and the top pool deepens +0.10 alpha, animated with the overlay, **180ms, curve A**.
  Depth is pooled light and one caught edge — no border, no shadow, no elevation.
- **Type raise:** input and placeholder to **15sp Atkinson Regular**, lineHeight 20sp —
  the queue title's size, one tier under its Bold, so a submitted task lands in the
  queue at the size it was written.
- **Block caret:** native brush → transparent; via `decorationBox` +
  `onTextLayout`, draw a **7dp × 18dp block**, radius 1dp, fill `onBackground`, from
  the real `getCursorRect(selection.start)`, behind the glyph layer. Blink:
  `infiniteRepeatable(tween(520, EasingA), Reverse)` — the 520 from the duration
  vocabulary, a stately instrument blink. If a frame has no layout result, draw
  **nothing** that frame — never fall back to the orange hairline for one frame.
- **Sound / haptic:** none. A window and a caret are not events.
- **Depth / tokens:** zero new colour literals — scheme tokens plus black/white
  alphas only. The night sink `#100E0C` is finally used the way the staircase built it.

### Rung 4 — Submit is an event (the tick dies here)

Replaces wave-1's 1dp accent tick entirely — see CONFLICTS. Confirmation is
**form + the latch the engine already owns**, never colour.

- **t = 0** — ADD / IME Done with non-empty trimmed text: capture `clean`,
  `minutes = newTaskMinutes`, `position = orderedQueue.size + 1` **before** anything
  fires. `soundEngine.playMechanicalLatch(true)` — waveform **[0, 11ms @ 215, 26ms
  gap, 9ms @ 150]**, transient 760Hz / 16ms / gain 0.54 / chord 1.2. This call already
  exists in `submitTask()`; it does not move.
- **t = 0–110ms, curve A** — the field's contents (text + echo) translate **+5dp Y**
  and fade to 0 — one `Animatable<Float>` driving `graphicsLayer` on the content row.
  The well ingests downward, toward the queue.
- **t = 60ms** — `onAddTask(clean, minutes, "Study", "", orderedQueue.isEmpty())`
  fires with the t = 0 captures; the field value clears while invisible. The new
  queue row enters with `Modifier.animateItem()`: fade 0 → 1 + rise from +10dp,
  **260ms, curve B**, starting as the well finishes emptying.
- **The stamp** — while the field is empty and `justQueued` is set, the placeholder
  slot renders instead, Anonymous Pro **Bold 11sp**, letterSpacing 1sp,
  `onSurfaceVariant`: **`QUEUED · 25M · #04`** — the actual minutes and position
  captured at t = 0. Hold **420ms**, fade out **260ms, curve A** (680ms total), then
  the `"New task"` placeholder fades in, 180ms curve A. Typing during the stamp
  cancels it instantly (`onValueChange` clears `justQueued`) — the machine never
  talks over the user. Echo fades back in from t = 110ms, 180ms curve A, showing the
  retained minutes.
- **ADD after submit:** text is empty, so ADD returns to its dimmed state (Rung 2's
  rule — it follows the text, always). Focus, bank, IME, minutes: **untouched**,
  covered by Rung 2's invariant test.
- **Empty submit:** unchanged from Rung 1 (detent + placeholder re-strike). The
  instrument registers the press and refuses, without theatre.
- **Sound / haptic:** the latch above is the whole budget. No completion pulse, no
  seal — filing a task is a latch, not a lock.

### Rung 5 — The front door opens itself (empty queue only)

- **Mechanism:** `LaunchedEffect` on first composition of the Tasks screen: if
  `activeTask == null && orderedQueue.isEmpty()`, `focusRequester.requestFocus()`
  after a 120ms settle delay. Keyboard rises, bank expands (Rung 2's animation),
  block caret resting in an empty well. First task of an empty queue costs
  **typing + 1 tap, total**.
- **The gate is the spec:** queue non-empty → no auto-focus, ever. Browsing an
  existing queue is legitimate use; the door opens itself only when entry is
  self-evidently the job. Re-arms only when the queue is empty again on a fresh
  screen entry.
- **Sound / haptic:** none. **Depth / tokens:** none. Ships last because it is the
  only rung whose failure mode is annoyance rather than absence.

---

## ★ — Best-ratio change (priced)

**★ Rung 1's focus state: one `isFocused` boolean + `FocusRequester`, driving the
deepening overlay and the ink caret.**

Price: ~15 lines on the existing `Box` — one `remember` state, one
`onFocusChanged`, one `animateFloatAsState`, one caret-brush swap, zero new tokens,
zero layout change, zero new composables. It is worth more than wave-1 priced it:
then, focus was a colour change. Now it is the **load-bearing state of the whole
composer** — the collapse (Rung 2), the recess deepening (Rung 3), ADD's arming
(Rung 2), and the front-door auto-focus (Rung 5) all key off this one boolean, and
today it does not exist. The most-touched control on the screen is the only one that
never acknowledges being touched; every other rung is decoration until this lands.

---

## PEAK

1. Resting, Tasks is a queue with one sticky line pinned above it: a 48dp recessed
   well — text, `· 25M`, a hairline, ADD as a word. 48dp doing the work 94dp did.
2. Tap it (or arrive at an empty queue) and the well deepens, a block caret starts
   its 520ms blink, and Taste's bank unfolds beneath — the only time it exists.
3. Type at 15sp; the echo states the exact minutes that will queue; ADD inks the
   moment there is something to add and stays a dimmed word while there isn't.
4. Submit is a swallow: latch sound and waveform, contents drop 5dp and vanish in
   110ms, the row rises into the queue, and the well stamps `QUEUED · 25M · #04`
   for 680ms — focus, bank, keyboard and minutes never move.
5. Next task: typing + one tap. Empty submit: one low detent and the placeholder
   re-strikes. No orange anywhere in the field, in any state, ever — START keeps it.

---

## STANDARDS CHECK

**ARM ≤ 10s — counted, per added task** (T = title keystrokes, title-dependent;
everything else is a tap):

| Case | Taps before typing | Per task | Notes |
|---|---|---|---|
| Current build | 1 (field) | T + 1 submit (+1 per duration change) | Keys always visible — but the field can be scrolled off-screen; returning costs uncounted scroll. |
| Peak, cold, queue exists | 1 (field) | T + 1 | Bank + keyboard arrive with the tap (260ms, non-blocking). |
| Peak, cold, empty queue (Rung 5) | **0** | T + 1 | Door already open on arrival. |
| Peak, steady state | 0 | **T + 1** (+1 only when duration changes) | Focus, bank, IME, minutes all retained across submits — the invariant is tested, not assumed. |
| Empty submit attempt | — | 1 wasted tap, field focused by it | The mistake routes you into the field; it never dead-ends. |

A 5-task queue at default duration: 6 taps total around the typing, submit
choreography never blocks input (field live again at t = 110ms, stamp cancellable).
Collapse adds **zero** taps against the current build and removes 46dp of resting
chrome. The queue-level ARM behaviour — focus retained, minutes retained, submit
without leaving the field — holds in every rung from 2 up.

**HOLD absolute:** the composer is identical during a running session
(`effectiveNight` changes tones only). Nothing in any rung pops, prompts, or asks
for a decision mid-session; adding a task never touches the seal. The bank closes
only on a user-caused focus loss.

**TRUTH once:** (a) placeholder vs content — placeholder is the muted text tier
(day `#5F5856` / night `#776A60`) at the input's own size and weight, vanishes on
the first character, and its copy (`"New task"`) asserts nothing false; (b) the
minutes echo **is** `newTaskMinutes`, the exact Int `onAddTask` receives — the
minutes shown are the minutes queued, from one variable; (c) the stamp's minutes
and position are captured at t = 0 from those same values, before the callback;
(d) duration has one rendering per state — collapsed: the echo alone; expanded:
the latched cap (the input device, state as position) + the echo (its readout).
Control and readout, not two data renderings; (e) the block caret draws only from
a real `TextLayoutResult` and draws nothing when it has none. Fonts: Atkinson for
text, Anonymous Pro for echo/stamp; Sora stays out of a 15sp input — a display
face there would be costume. Palette: warm tokens only, no green anywhere,
**no orange anywhere in the field**.

---

## CONFLICTS

1. **The wave-1 accent tick is rejected — argued, not assumed.** Wave-1's peak spent
   a 1dp orange tick on the field's bottom edge for 180ms per submit, claiming the
   ration survives because tick and START are "never lit in the same settled frame."
   That is the flaw: during Rung 4's row entry (t = 60–320ms) the cued task's START
   is on screen, lit, in the same viewport — the tick *would* be a second orange in
   real frames, on the screen's most frequent action. A caret-width flash is not a
   commit anyway. Peak position: **zero orange in the field in every state**;
   commit is carried by the latch (sound + two-stage waveform), the 110ms swallow,
   and the stamp. Reinstating any orange here requires demoting START — Sarthak's /
   Ren's call, flagged, not taken.
2. **Taste (bank) — seam mailed 0524, answered 0528, fork escalated 0536.**
   ADD leaves the bank and becomes text in my line in both states; Taste owns the
   inside of a 52dp slot (8dp gap above it), receives `newTaskMinutes` /
   `onMinutesChange` / `effectiveNight`, and his caps must be non-focusable and
   never clear focus (**accepted outright by Taste, 0528** — the focus guarantee
   and slot contract stand). **The fork:** Synthesis 28 contradicts itself — its
   line reads "ADD as text," its bank parenthetical reads "ADD the only ink key" —
   and our briefs assign ADD to both of us. Taste's case for the key: the commit
   deserves 3dp of skirt travel, and a punch sound fired by a text word lies about
   its form. My case for the text: this screen's verbs are already text (START,
   DONE, DEL — START, the terminal verb, has zero travel), caps are its value
   grammar, and a collapsed ADD submits *retained* text in one tap (an unfocused
   field is not an empty field — text persists across focus loss; that case is
   the sticky line's reason to exist). **Ilya/Ren rule it; neither of us builds
   the other's silence.** Conditional already agreed by mail: if ADD-in-bank wins,
   my duration echo retires in expanded state and lives only collapsed.
3. **Submit sound — latch (mine) vs card punch (Taste's), one call site either way.**
   Agreed with Taste outright: one action, one sound, fired inside
   `onComposerCommit()` — never a latch on IME Done and a punch on ADD. The fork
   is only which transient: my Rung 4 ships the existing
   `playMechanicalLatch(true)` (zero engine work; the rung stays independently
   shippable). Taste's Rung 2 re-homes submit to a card punch — 210Hz / 30ms /
   gain 0.58 / chord 0.62, haptic [0, 10ms @230, 18ms gap, 6ms @140] — which is
   also the sound Synthesis 28's Paperwork That Plays family names for ADD.
   Filed position: the punch replaces the latch the moment Taste's engine call
   lands; if Ilya rules punch-from-the-start, Rung 4's t = 0 call changes and
   nothing else in the rung moves. Empty-path sounds differ by surface and are
   flagged in the same mail: detent 320 on IME-Done-empty (mine), 6ms @45 thud
   on idle-ADD tap (Taste's, using my hoisted FocusRequester — granted).
4. **Alba (MaxImpl composer) — mailed 0524**: her always-visible ~124dp assembly
   and her `NEW TASK` label row are superseded by the collapse and the single-line
   height budget; her block caret, pooled-recess constants, disarmed-redirect and
   QUEUED stamp are adopted (the stamp's position fact survives inside the field).
   Her ADD-cap arming choreography survives as behaviour on my ADD text.
5. **My own wave-1 peak, retired by my own hand:** the 36dp `+` gutter, the
   `COMPOSER` micro-label straddling the top edge, and the 56dp slab all die. A
   sticky single line cannot carry a gutter column and a nameplate and stay a
   front door — and the printed `+` duplicated ADD's verb in the same line.
6. **Atkinson "Medium" was a fiction** in wave-1 (mine) and in Alba's spec: the
   font ships Regular + Bold only. Every Medium in either file is unbuildable as
   written; Regular/Regular replaces it and is what honestly kills the weight pop.
7. **Sticky extraction touches neighbours:** pulling the composer out of the
   `LazyColumn` shrinks the queue's scroll area by up to 60dp while the bank is
   open (Zeile's rows live below the composer; nothing of theirs changes). The
   Horizon header numeral is untouched, and the composer's pin must be built to
   survive the Horizon bar's death (Synthesis 29) without re-layout.

— Fach, Improvers, composer field
