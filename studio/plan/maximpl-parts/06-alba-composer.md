# 06 — THE COMPOSER, COLLAPSED (Plan Wave)

**Seat:** Alba, Maximum-Implementable (coordinator: Voss). **Element:** the Tasks
composer — `FocusSequencerScreen.kt` Section 2: recessed field, duration key bank,
ADD. **Status:** plan only, no code. Wave-1 spec (`maximpl/06-composer.md`) is the
floor; this file re-verifies it against today's code, then applies the one
structural change the synthesis ordered and wave-1 did not have: **collapse**
(SYNTHESIS item 28).

**The counter-argument to the Day-8 friction death, in one paragraph:** the
composer's resting state is a single 76dp line that costs the queue nothing; the
key bank exists only inside a focus the user already paid for; ADD never changes
address, armed or not, open or not; submit never dismisses anything, so filing
ten tasks is ten repetitions of *type → tap*, with no reopen, no refocus, no
re-aim. Every cost wave-1 added (a taller field, a taller bank) is now paid only
while he is actively filing — the moment he stops, the composer folds back to a
line and gives the screen back.

**Three conflicts resolved up front (argued in the sections noted):**
1. **Pitch direction.** Wave-1's detent set rose with duration (540→780Hz).
   The wooden-bar family (WILD §4 / Ivo's desk) falls with duration — a bigger
   bar sounds lower; that is physics, and pitch here is identity, not decoration.
   **Decision: the set falls.** Wave-1's four frequencies are kept as playable,
   verified literals but their assignment is reversed and re-voiced as the Dm7
   stack transposed into the detent's clamp — §Sound.
2. **ADD's address.** Synthesis 28 describes the bank as containing "ADD the
   only ink key" (wave-1's layout). Moving ADD from beside the field (collapsed)
   to under it (expanded) changes the submit target's address on every focus
   change — a mis-tap generator at 50-minute filing rhythm, one-handed, tired.
   **Decision: ADD never moves.** It lives permanently in the top line; what
   morphs is its rendering (text → seated key → ink key), never its slot. The
   bank below holds the four duration caps only. §Mechanism.
3. **Stale night hexes.** Wave-1's night values (`#201D1A` ground, `#272320`
   well, `#332A27` face) are dead: the shipped staircase (current `Color.kt`)
   is ground `#171412`, recess **below** ground at `#100E0C`, raised step
   `#362C27`, fills capped at `#4A423C`, text tiers 11 / 5.8 / 3.5 : 1. Every
   night value below follows the shipped staircase. Alphas from wave-1 survive
   (they are overlays, not tokens); hexes do not.

---

## Mechanism

### M1. The two shapes

The composer has exactly two shapes, and one boolean decides between them:
`fieldFocused`, already the field's focus state. **Bank visibility ≡ field
focus.** There is no separate open/close state to desync, no toggle, no memory.

**COLLAPSED (unfocused) — the sticky single line, total height 76dp:**

```
[ label row 14dp ]  NEW TASK ················ 25M · #04        (readout, §M4)
[ gap 6dp ]
[ field 56dp ........................ · 25M ] [ ADD slot 72dp ]
```

- Field: 56dp tall, 12dp radius, recess stack per §Depth & light. Inside,
  left→right: printed `+` glyph · typed text or placeholder · **inline duration
  echo** `· 25M` (Anonymous Pro 11sp, right-aligned, 12dp from the right wall).
- ADD slot: fixed 72dp wide, full 56dp touch height, immediately right of the
  field, 8dp gap. Its rendering depends on content, not focus (§M3).
- The key bank is **not composed** in this shape — not alpha-0, not height-0
  with content inside; absent. A hidden-but-composed bank is a touch and
  semantics leak.

**EXPANDED (focused) — the line plus the bank, total height 140dp:**

```
[ label row 14dp ]  NEW TASK ················ 25M · #04
[ gap 6dp ]
[ field 56dp ............................... ] [ ADD slot 72dp ]
[ gap 8dp ]
[ cap 15m ][ cap 25m ][ cap 45m ][ cap 90m ]   48dp, weights 1f each
```

- The bank spans the field's width only (not the ADD slot's): the caps are the
  field's keys; ADD is not one of them.
- The inline duration echo **leaves the field** in this shape (fade, §Motion):
  while the bank is open, duration is rendered by the sunk cap, and one fact
  gets one rendering. The echo returns when the bank folds.

### M2. Expand / collapse triggers — exhaustive list

**Expand** (exactly one trigger): the field gains focus, from any source —
tap on the field, tap on the disarmed ADD (§M3, which requests focus), tap on
the inline echo (it is inside the field's touch area), or TalkBack focus.

**Collapse** (exactly three triggers):
1. The field loses focus — tap on a queue row, tap elsewhere on the screen,
   system Back while focused, navigation away. All of these are the user
   *leaving*, and the bank folding is the composer agreeing.
2. A session becomes active (`isSessionActive` flips true — he pressed START
   and the screen's personality changes): focus is cleared programmatically,
   which collapses the bank by rule 1's mechanism. No separate animation path.
3. Nothing else. **Scrolling the queue does not collapse** — he may check the
   queue mid-draft with the keyboard up; the draft and the bank wait. **Submit
   does not collapse** (§M5). **A duration tap does not collapse.**

### M3. ADD — one slot, three renderings, zero address changes

The ADD slot (72dp × 56dp touch) holds a visual key of 68dp × 48dp, 12dp
radius, vertically centred. ADD is **INK, never accent** — wave-1 §7 argued
this in full (screen ration already spent on the cued task's START; ink is the
app's commit grammar; night settles it) and it holds unchanged.

| Rendering | When | Fill | Face offset | Label |
|---|---|---|---|---|
| **Text** (the synthesis's "ADD as text") | Collapsed **and** field empty/whitespace | none — no key drawn | — | `ADD`, Atkinson Bold 13sp, letterSpacing 1sp, muted tier (day `#5F5856`; night secondary `#9C8E84`) |
| **Seated** | Field has content, **or** expanded and empty | `surfaceVariant` (day `#D8D1CB`; night recess `#100E0C`) | +1.5dp held (seated in its socket, skirt 1.5dp) | as Text state, on the key |
| **Armed** | Field has ≥1 non-whitespace character | `inkControlFill()` (day `#2C2A29`; night `#4A423C` — the staircase cap) | 0dp (risen, skirt 3dp) | `ADD · 25M` — `ADD` Atkinson Bold 13sp cream `onInkControlFill()` + duration suffix Anonymous Pro 11sp, same cream at alpha 0.75, composed via the existing mixed-typography helper |

- The state variable is **content**, computed on the *trimmed* title:
  `armed = title.trim().isNotEmpty()`. First character typed → Seated/Text →
  Armed (§Motion for the rise). Deleting back to empty → Armed → Seated/Text.
  The key wakes up under his sentence as he writes it; that single transition
  is the composer's idle life.
- The armed label reprints instantly when a duration cap changes (`ADD · 90M`)
  — a computed string, re-struck in place, never animated through values.
- **Disarmed tap (Text or Seated with empty field): never a silent no-op.**
  On press-DOWN: fire `playRotaryDetent(320.0)` (the lowest tick the engine's
  280–1480 clamp produces) and request focus into the field via the
  `FocusRequester`. The keyboard rises, the bank expands, the caret lands at
  the end of any existing draft. The machine redirects instead of ignoring —
  the TRUTH law applied to a dead key: it tells him, in sound and motion, that
  there was nothing to file and where filing starts.
- **Armed tap:** the acceptance choreography, §M5.
- Semantics: `Role.Button`; contentDescription `"Add task, 25 minutes"` when
  armed (verb + value it will file), `"Add task — type a task first"` when
  disarmed. `testTag("add_task_button")` stays on the slot's outer box.

### M4. The printed label row and the margin readout

Unchanged from wave-1 §4, restated for the collapsed shape, with staircase-
corrected tiers. A 14dp row, 6dp above the field, Anonymous Pro Bold 10.5sp,
letterSpacing 1sp:

- **Left:** `NEW TASK`, printed (letterpress shadow, day only), muted tier.
- **Right — the readout**, the pending sentence's margin form. Number in full
  ink, words in the secondary tier (night `#9C8E84`, 5.8:1 — the 3.5:1 muted
  tier is decorative-only and is **not** used for this 10.5sp functional text):
  - Queue non-empty: `25M · #04` — duration that *will* be filed + position it
    *will* occupy (`orderedQueue.size + 1`, `String.format("%02d", …)` — the
    same format Rurik's rows print, so the readout's #04 and the row's #04 are
    the same string from the same formatter).
  - Queue empty: `25M · FIRST`.
  - During the acceptance stamp (§M5): `QUEUED · #04` — the position now
    occupied. This is a *different fact* from the field stamp's `QUEUED · 25M`
    (where it landed vs what was filed), so the pair is not a duplicate
    rendering; each fact appears once per surface it belongs to.
- The readout updates the instant a cap is picked and the instant a task is
  filed (next position). It is present in both shapes — it is part of the
  sticky line, which is what makes the collapsed composer informative at rest:
  *25 minutes, fourth in line, nothing else to know.*

### M5. Acceptance — the submit choreography

One wrapper, `onComposerCommit()`, called by armed ADD tap and IME Done
identically; `submitTask()`'s existing emptiness guard remains the single
gate inside it. Timeline (t=0 is finger-DOWN on ADD):

| t | Event |
|---|---|
| DOWN | ADD face travels 0 → **+3dp** — the deepest travel in the composer (committing is the heaviest act here). No sound yet: the punch is a commit sound and nothing has committed. |
| UP (click) | Commit fires, in this order, same frame: (1) `playCardPunch()` — §Sound; until that engine method lands, `playMechanicalLatch(true)` from this same single call site, **never both**; (2) `onAddTask(clean, newTaskMinutes, "Study", "", orderedQueue.isEmpty())` — signature unchanged; (3) title cleared; (4) `justQueued = (minutes = newTaskMinutes, position = orderedQueue.size)` snapshotted (post-insert size = the filed task's 1-based position). |
| 0–180ms | ADD face returns +3 → 0dp. Simultaneously the **swallow pulse**: the field's focus-deepening overlay (§Depth) pulses *past* its focused resting alpha and back — peak delta **+0.04 day / +0.08 night** above the focused value — one breath of the well. The well swallows; the text is gone because it was taken, not because it was erased. |
| 0–680ms | **The stamp.** While `justQueued != null` and the field is empty, the field's placeholder slot renders instead: `QUEUED · 25M`, Anonymous Pro Bold 11sp, letterSpacing 1sp, secondary tier, letterpressed (day). Hold **420ms**, fade **260ms**, then the printed placeholder returns. Simultaneously the margin readout flips to `QUEUED · #04` for the same 680ms window, then reprints the next pending sentence (`25M · #05`). |
| ~+300ms | The new row lands in the queue (Rurik's `animateItem` placement, §Code). On its landing frame the row fires the **seating detent** — `playRotaryDetent(capPitch(filedMinutes))`, §Sound — one tick in the filed duration's own voice. Owner: the row's entrance, not the composer (mailed to Rurik). If his row spec does not implement entrance sound, this beat does not fire and the punch alone carries acceptance — stated here so the dependency is explicit, not discovered. |
| Always | **Focus and keyboard stay.** No `clearFocus()`, no IME dismiss, bank stays open, duration selection persists. The caret block re-appears at position 0. The next task can be typed in the same breath. This **confirms wave-1's call**, with the collapse-era reason added: submit-clearing-focus would now also collapse the bank and drop the keyboard — three restorations (tap field, wait keyboard, wait bank) taxed on *every* task at his filing rhythm. The Day-8 death is precisely that tax, repeated. |
| Interruption | If he types during the stamp, `onValueChange` clears `justQueued` instantly — stamp and readout revert mid-window. The machine never talks over the user. |

Empty-field IME Done routes through the disarmed path (§M3): detent 320Hz +
focus retained (it never left), no punch, no stamp, nothing queued.

### M6. The field

- **Caret — block, not hairline.** `cursorBrush = SolidColor(Color.Transparent)`;
  the caret is drawn in `BasicTextField(decorationBox = …)`: capture
  `TextLayoutResult` via `onTextLayout`, take `getCursorRect(selection.start)`,
  draw a **7dp × 17dp block**, corner radius 1dp, offset x −2dp so it centres
  on the insertion point, fill = primary text tier (day `#2C2A29`; night
  `#D2C6BB`), **behind** the glyph layer. Blink: `rememberInfiniteTransition`,
  alpha 1→0, `tween(DURATION_520, EasingA)`, `RepeatMode.Reverse` — a ~1.04s
  full cycle, an instrument's blink. Visible only while focused. If
  `onTextLayout` has produced no layout on a frame, draw nothing that frame —
  never fall back to the old orange hairline, not even for one frame. This
  removes the last orange from inside the field (wave-1 §3, carried whole).
- **Placeholder — printed, with real weights only.** Copy: `"Add one focus
  task…"` (wave-1's wording; the meta-phrase "single-line" stays deleted).
  Atkinson Hyperlegible **Regular 400**, 13sp, secondary tier. Wave-1 specced
  Medium for this slot — Atkinson has no Medium; that weight was a fake when
  written and is corrected here: the printed/typed distinction is carried by
  the **real** 400 → 700 step, placeholder Regular against typed Bold. Typed
  text: Atkinson Bold 13sp, primary tier — the darkest, heaviest thing in the
  well.
- **Letterpress:** the Deck's exact constants
  (`TactileDeckScreen.kt:194/202`): day `Shadow(Color(0x66FFFFFF), Offset(0f,
  1f), 0f)`; night `Shadow(Color.Transparent, Offset.Zero, 0f)`. Applied to
  the placeholder, the stamp, and the label row's left word. Typed text gets
  no shadow — fact sits on the glass, print sits in the surface.
- **The leading `+`:** Anonymous Pro 15sp, printed in the well at 16dp from
  the left wall, in the placeholder's treatment. It does not disappear when
  text exists; it steps to alpha 0.55 with content, inks to the primary tier
  on focus, returns on empty. It is printed on the bezel, not a button —
  no touch target, no semantics node.
- **Typing feedback: none.** No per-keystroke sound, haptic, or pulse. The
  system keyboard owns keystroke feel; the composer's only per-character event
  is the first character arming ADD (§M3) and any character clearing the stamp
  (§M5).

### M7. The key bank

Wave-1 §§5–6 carried with two changes (height discipline under collapse, and
the pitch set of §Sound):

- **Geometry:** caps 48dp tall, **12dp radius** (the off-vocabulary 9dp dies),
  gap 8dp, weights 1f each across the field's width. Cap = **skirt + face**:
  skirt is the full 48dp box in `surfaceVariant` (night: recess `#100E0C`);
  face is 45dp aligned top, leaving **3dp of skirt** visible at rest — the
  cap's thickness. Face fill unselected: raised step (day `surfaceBright
  #FAF6F0`; night `#362C27`). Face carries a 1dp `outlineVariant` hairline and
  a lit top edge (§Depth). Label Anonymous Pro Bold 13sp, full primary tier —
  numerals on a raised face are never grey.
- **Three positions, readable from silhouette alone** (grayscale, peripheral
  vision, no colour): raised idle = offset 0, skirt 3dp · pressed = **+2.5dp**,
  skirt 0.5dp · **latched (selected) = +1.5dp held**, skirt 1.5dp, fill
  `inkControlFill()` with `onInkControlFill()` label. The selected cap sits
  visibly *lower* than its neighbours for as long as it is selected — state
  as position, not as colour that happens to accompany position.
- **No ripple, anywhere in the composer:** every cap and the ADD slot use
  `clickable(interactionSource, indication = null)`. Travel is the indication.
- **No scale, ever.** Keys translate into their sockets; they do not shrink.
- Semantics: caps expose `Role.RadioButton` inside one `selectableGroup`;
  contentDescription is the duration (`"25 minutes"`).

---

## Motion (curve + ms)

All specs from `MonolithCurves` (`specA/B/C` = CurveA 0.2,0,0,1 · CurveB
0.05,0.7,0.1,1 · CurveC 0.3,0,0.8,0.15) and the duration vocabulary
{180, 260, 280, 420, 520}. No new easing, no springs, no durations outside the
vocabulary.

| Beat | Spec |
|---|---|
| Bank expand (focus gained) | height `expandVertically(specC(DURATION_280))` + `fadeIn(specA(DURATION_180))`. One motion, **no stagger** — a staggered bank would make the last cap usable later than the first for no information gained. 280ms lands inside the keyboard's own rise, so the bank is ready before the keyboard settles. |
| Bank collapse (focus lost) | `shrinkVertically(specA(DURATION_260))` + `fadeOut(specA(DURATION_180))`. Collapse is slightly faster than expand: leaving is a dismissal, arriving is an invitation. |
| Duration echo fade out (on expand) | `animateFloatAsState`, specA(180), 1→0. |
| Duration echo fade in (on collapse) | specA(180), 0→1, delayed until shrink completes (launch after the 260ms shrink) so the echo never overlaps the caps it duplicates. |
| Field focus deepening | overlay alpha `animateFloatAsState`, specA(DURATION_260), both directions (§Depth for values). |
| ADD arm rise (first character) | fill `animateColorAsState(specA(DURATION_260))`; offset `animateDpAsState(specC(DURATION_280))`, +1.5 → 0dp. |
| ADD disarm seat (text deleted to empty) | same pair, reversed targets. |
| ADD press (armed) | offset → +3dp `specA(DURATION_180)` on DOWN; return `specC(DURATION_280)` on release. |
| ADD press (disarmed) | no travel (Text rendering has no key; Seated rendering is already at its floor). The response is focus + sound, not motion. |
| Cap press-in | offset → +2.5dp, `specA(DURATION_180)` — CurveA's front load lands the strike perceptually in ~60–70ms; the tween is not shortened below the vocabulary to chase speed. |
| Cap release (unselected) | offset → 0, `specC(DURATION_280)` — a settle, not a snap. |
| Cap latch (on selection commit) | offset → +1.5dp held, `specC(DURATION_280)`; fill → ink, `animateColorAsState(specA(DURATION_260))`. The previously selected cap rises +1.5 → 0 on the same 280ms — two caps moving in opposite directions is the bank's one choreographed moment, and it happens on every pick. |
| Swallow pulse (on commit) | overlay alpha: to peak `specA(DURATION_180)`, back to focused resting `specA(DURATION_260)`. |
| Stamp | hold DURATION_420 at full alpha, fade `specA(DURATION_260)` → placeholder crossfades in on the same 260. Total window 680ms. Readout flip is instant at both ends (a counter clicking over — counters do not fade). |
| Caret blink | `tween(DURATION_520, EasingA)`, Reverse, infinite. |
| Queue arrival (Rurik's, depended on) | `Modifier.animateItem(fadeInSpec = specA(260), placementSpec = specB(DURATION_420), fadeOutSpec = specA(180))` on queue items; survivors glide on placementSpec for every reorder. |

---

## Haptics (per beat: ms + amplitude)

`LocalHapticFeedback` / `performHapticFeedback()` are **banned** on this
surface — the engine's class doc forbids the pattern, and every beat below
rides inside a `TactileSoundEngine` call whose waveform is the haptic. There
is no haptic-only beat in the composer: if a beat has no sound, it has no
haptic (focus, typing). Waveforms are `VibrationEffect.createWaveform`
timings/amplitudes as shipped in the engine unless marked as part of an
engine addition (§Sound).

| Beat | Call | Waveform (timings ms / amplitudes 0–255) | Fallback |
|---|---|---|---|
| Duration cap press-DOWN | `playRotaryDetent(pitch)` | `[0, 7, 5]` / `[0, 180, 70]` — 7ms strike, 5ms tail | 1L one-shot |
| Cap re-tap (already selected) | `playRotaryDetent(same pitch)` | identical — the press is real even though the value didn't change; never a silent press | 1L |
| ADD disarmed tap | `playRotaryDetent(320.0)` | `[0, 7, 5]` / `[0, 180, 70]` — same tick, lowest pitch: *registered, nothing filed* | 1L |
| ADD armed commit | `playCardPunch()` *(engine addition, §Sound)* | `[0, 14, 30, 8]` / `[0, 230, 0, 160]` — 14ms punch at near-full, 30ms gap, 8ms slice-settle | interim `playMechanicalLatch(true)`: `[0, 11, 26, 9]` / `[0, 215, 0, 150]`, fallback 4L |
| Row seating (Rurik's beat) | `playRotaryDetent(capPitch)` | `[0, 7, 5]` / `[0, 180, 70]` | 1L |
| Sub-step toggle ON (in the ACTIVE row; pitch owned here, fired by the row) | `playRotaryDetent(stepPitch)` | `[0, 7, 5]` / `[0, 180, 70]` | 1L |
| Sub-step toggle OFF | `playRotaryDetent(same stepPitch)` | identical waveform — the retraction is carried by the strike's visual reverse (§Code note), not by a second haptic dialect | 1L |
| Final sub-step (set completes) | `playStepStrum(stepCount)` *(engine addition, §Sound)* | per-note cells: timings `[0, 6, 12, 6, 12, …, 8]`, amplitudes `[0, 170, 0, 190, 0, 205, 0, 215, 0, 225]` truncated to the task's note count (3-step task = `[0,6,12,6,12,8]` / `[0,170,0,190,0,215]`) | 10L one-shot |
| Field focus / blur / typing / stamp | — | **none** | — |

Trigger discipline (the DOWN/UP rule, one rule for the whole surface):
**touch-feedback sounds fire on DOWN; fact sounds fire on commit (UP).** Cap
detents, the disarmed-ADD detent, and sub-step notes answer the finger, so
they fire on press-DOWN (via `collectIsPressedAsState` edge, the house
pattern from the row spec). The punch and the strum assert that something
*happened* — a task filed, a set completed — so they fire on the click/commit
event; a press that slides off and cancels produces neither.

---

## Sound (primitive + pitch)

### The fixed sets — literals, looked up, never computed

No pitch in this section is produced by a formula. Each is a named literal in
a lookup table keyed by cap or step index. (The cap table's near-even spacing
is a property of the chosen notes, not a computation; the code holds four
literals.)

**CAP SET — wooden bars, Dm7 stack, longer duration = lower pitch.** Ivo's
desk set (D3/F3/A3/C4 = 146.83/174.61/220.00/261.63Hz) sits below
`playRotaryDetent`'s 280Hz clamp and cannot be played by the existing
primitive — so the identities are transposed exactly two octaves, preserving
the notes, the order, and the chord, into the clamp's range:

| Cap | Note | `playRotaryDetent` pitch |
|---|---|---|
| 15m | C6 | **1046.50 Hz** |
| 25m | A5 | **880.00 Hz** |
| 45m | F5 | **698.46 Hz** |
| 90m | D5 | **587.33 Hz** |

This **replaces** wave-1's assignment (540/620/700/780 ascending). The
frequencies change; the primitive does not. Honest limit, stated once: through
`playRotaryDetent` these are 6ms detent *ticks at bar pitches* — pitch identity
ships now; wooden-bar *timbre* (partials 1 / 2.76 / 5.40, 90–140ms decay)
would need a bar-voice engine method and is **not** specced, not smuggled, and
not needed for the set to be learnable by ear within a week. Re-tap of the
selected cap plays its pitch undamped: the engine exposes no gain/damping
parameter, and a faked damper (a different pitch) would lie about which cap
was touched.

**STEP SET — sub-step scale notes** (Ivo's set, untransposed — every member
already inside the clamp):

| Step | Note | Pitch |
|---|---|---|
| 01 | E4 | **329.63 Hz** |
| 02 | G4 | **392.00 Hz** |
| 03 | B4 | **493.88 Hz** |
| 04 | D5 | **587.33 Hz** |
| 05 | E5 | **659.25 Hz** |
| 06+ | E5 | **659.25 Hz** (the set is the set; no sixth pitch is invented) |

Known collision, accepted: step 04 and the 90m cap share 587.33Hz. They live
on different surfaces (bank vs row), never sound in the same gesture, and
pitch here is identity *within* a surface. Noted so nobody "fixes" it by
inventing a pitch.

### The map — every sound on the surface

| Event | Primitive | Trigger | Owner |
|---|---|---|---|
| Cap press | `playRotaryDetent(capPitch)` — CAP SET | DOWN | Composer (screen) |
| Cap re-tap | `playRotaryDetent(same capPitch)` | DOWN | Composer |
| ADD disarmed | `playRotaryDetent(320.0)` | DOWN | Composer |
| ADD armed commit | **`playCardPunch()` — ENGINE ADDITION #1** (below); interim `playMechanicalLatch(true)` | UP / commit | Composer — the existing latch call inside `submitTask()` is *replaced*, not joined |
| Row seating after add | `playRotaryDetent(capPitch of filed duration)` | row landing frame | **Rurik's row entrance** (dependency, §M5) |
| Sub-step toggle on/off | `playRotaryDetent(stepPitch)` — STEP SET | DOWN | Row composable (screen file), pitch table owned by this spec |
| Final sub-step completes the set | **`playStepStrum(stepCount)` — ENGINE ADDITION #2** (below) | commit (UP) of that toggle | Row composable |
| DONE on a task whose steps are all complete | **no strum** — Rurik's `playCompletionPulse()` only | — | Rurik |
| Focus, blur, typing, stamp, duration echo | silence | — | — |
| `playFirmClick()`, `playHarmonicSeal()` | **not used in this element** — firm click belongs to heavy Canvas controls; the seal is session-arming weight. The punch must remain the loudest routine event here, and submit must stay lighter than sealing a session. Wave-1's §3e assignment of `playFirmClick()` to the last sub-step is **superseded** by the strum (synthesis 28 names the strum; a firm click and a strum for one fact would also double-render it). | — | — |

**One-owner rule (binding):** the screen composable is the sole owner of every
sound above. The ViewModel fires **no** sound or haptic for task-add,
duration-select, or sub-step toggle — verified in current code (the only
composer sound today is the screen's own latch call in `submitTask()`; the
Canvas `fireFeel` family belongs to Deck/Shield views and never reaches this
screen). Any future ViewModel-side feel for these gestures double-fires
against this table and is a bug by definition. Within the screen: one gesture
produces exactly one engine call, except the add sequence, which produces two
calls for two facts at two times (punch = filed, seating detent = landed).

**Focus paper-slide — rejected, on the record.** Ivo's desk specced a
shaped-noise paper-slide on focus/blur. The existing engine synthesises sine
partials only; a noise voice would be a third engine addition, and both the
field improver's ladder and wave-1 independently landed on silent focus.
Focus here is a depth change (§Depth), not a sound. The slide dies; if a
noise voice ever enters the engine for another element, this decision may be
revisited — it is not pre-approved.

### ENGINE ADDITION #1 — `playCardPunch()`

Flagged as an engine addition; everything else in this spec uses existing
primitives. The current synth loop renders one fixed-fundamental sine-partial
command; the punch needs a frequency glide and filtered noise, so it is a new
command kind in `TactileSoundEngine`, specified exactly:

- **Haptic (inside the method, same call site law):** waveform timings
  `[0, 14, 30, 8]` ms, amplitudes `[0, 230, 0, 160]`, pre-API-26 fallback 8L.
- **Stage 1 — punch (t=0, 30ms):** sine fundamental gliding linearly
  **320 → 140Hz** over 1323 samples, gain **0.80**; plus white noise (seeded
  `Random(0x4D4F4E4F)` for determinism in tests) through a one-pole lowpass,
  coefficient α = 1 − exp(−2π·700/44100) ≈ **0.0951**, noise gain **0.35**,
  same 30ms window, envelope exp decay with the engine's existing tail step
  (`TAIL_DECAY_STEP`) applied per sample.
- **Stage 2 — slice (t=+90ms, 70ms):** white noise (same generator, continued
  stream) through a two-pole bandpass whose centre sweeps linearly
  **3000 → 1200Hz** across 3087 samples, gain **0.28**, envelope: 5ms linear
  attack, then exp decay at `TAIL_DECAY_STEP`.
- **Total rendered length: 160ms** (7056 samples) — exactly the synth loop's
  existing `maxSamples` cap; no buffer change needed for this method.
- **Level intent:** the desk's loudest routine event, still under the seal
  (`playHarmonicSeal` gain 0.62 at 48ms is a different weight class; the punch
  is sharper, not bigger). Night: identical rendering — the engine has no
  night gain path and this method adds none; level-by-time-of-day is a Guard
  sound-settings question, not this element's.
- **Interim behaviour (until the method lands):** `onComposerCommit()` calls
  `playMechanicalLatch(true)` — the existing call, moved into the wrapper,
  unchanged parameters. The swap to `playCardPunch()` is a one-line change at
  that call site. The two never coexist.

### ENGINE ADDITION #2 — `playStepStrum(stepCount: Int)`

- **Notes:** the first `stepCount` members of the STEP SET literal table held
  inside the engine (`[329.63, 392.00, 493.88, 587.33, 659.25]`, steps past 05
  repeat 659.25), capped at **8 notes** (auto-split produces 3; the cap is a
  render bound, not a product limit).
- **Voice per note:** sine partials at ratios **1 / 2.76 / 5.40**, weights
  **{1.00, 0.42, 0.18}** normalised by their sum; amplitude envelope
  exp(−n / (0.055 · 44100)) per sample (55ms time constant); each note rendered
  into the shared buffer at a **12ms stagger**, low → high.
- **Buffer:** total length = 12·(n−1) + 340ms tail, maximum **520ms**. This
  requires raising the synth loop's `maxSamples` from 160ms to 520ms — the
  one engine constant this spec touches, flagged here, priced in §Code.
  Summed peak normalised to gain **0.80**.
- **Haptic (inside the method):** the per-note waveform table in §Haptics.
- **Firing rule (truth guard):** fires exactly once, on the toggle whose
  commit makes `subSteps.all { done }` true for a task with ≥1 sub-step.
  DONE afterwards does not re-fire it; a task closed with steps open never
  fires it; a zero-step task never fires it. One completion, one strum.

---

## Depth & light

One light, from top-right, consistent with every sled and track already
shipped. All depth is tonal steps, pooled shadow, and one catch line — no
elevation, no blur shadow, no border drawn around the field (wave-1 §2: the
uniform 1dp `outlineVariant` border on the current field is deleted; the
layers below close the shape).

**The well (field), outer → inner, inside one clipped 12dp parent:**

| Layer | Day | Night (shipped staircase) |
|---|---|---|
| Well floor | `surfaceVariant #D8D1CB` on ground `#E4DFDA` | recess token `#100E0C` — **below** ground `#171412` (the staircase killed the old inversion; the well is the darkest surface on the screen) |
| Top pooled shadow | gradient height 7dp, black alpha **0.18** → 0 | black alpha **0.50** → 0 (at night the pool *is* the recess) |
| Left pooled shadow | width 5dp, black alpha **0.10** → 0 | black alpha **0.28** → 0 |
| Bottom catch light | 1.5dp line, inset 12dp from corners, white alpha **0.30** | white alpha **0.07** |
| Focus deepening overlay | black, animated 0 → **0.05** | black, animated 0 → **0.16** |
| Swallow pulse peak | **0.09** (focused 0.05 + 0.04) | **0.24** (focused 0.16 + 0.08) |
| Outer bottom lip | 1dp `surfaceBright` at alpha 0.5, under the field | omitted — night depth is all pool |

Recess constants ported verbatim from the Canvas `SledSwitchView` (top pool
`0x2E000000` / 1.4dp, bottom catch `0x2EFFFFFF`, contact `0x38000000`) — same
machine, same light; the alphas above are their Compose expression per theme.
Shading layers sit *above the floor, below the text*, take no touch input.
All alpha choices key off `effectiveNight` (`isDarkTheme || isSessionActive`,
already computed in the screen); all colours key off `MaterialTheme.colorScheme`
plus the two staircase literals the scheme already carries.

**Caps:** face lit top edge — 1dp line inset 10dp, white alpha **0.55 day /
0.12 night**; on the ink (latched/armed) face it drops to **0.18 day / 0.10
night** (ink catches less light than porcelain). Skirt = `surfaceVariant`
day / recess `#100E0C` night. Night fills never exceed the staircase cap
`#4A423C` — `inkControlFill()` already enforces exactly this; use the helper,
never a literal.

**Text tiers (locked staircase):** primary 11:1 (day `#2C2A29` / night
`#D2C6BB`) — typed text, readout numbers, caret, cap labels · secondary 5.8:1
(day `#5F5856` / night `#9C8E84`) — readout words, placeholder, stamp, ADD's
disarmed label · decorative 3.5:1 (night `#776A60`) — **nothing in the
composer**; every string here is functional. Cream is a text tone only; no
cream fill exists in this element at night.

**Orange audit of this section:** zero. Caret = ink tier, ADD = ink fill,
caps = ink/tonal, stamp = secondary tier, echo = secondary tier. The screen's
one orange remains the cued task's START, below the composer, untouched.

---

## State machine

Composer state = (focus × content × stamp), plus two global modifiers
(session-active, sound-enabled). All transitions:

| State | Focus | Content | Bank | ADD rendering | Caret | Echo |
|---|---|---|---|---|---|---|
| **S0 Collapsed–empty** | off | empty | absent | Text | none | shown (`· 25M`) |
| **S1 Collapsed–draft** | off | draft kept | absent | Seated, or Armed if draft non-whitespace → **Armed** | none | shown |
| **S2 Open–empty** | on | empty | open | Seated | block, blinking | hidden |
| **S3 Open–draft** | on | non-empty | open | Armed | block, at selection | hidden |
| **S4 Stamp** (sub-state of S2, 680ms) | on | just cleared | open | Armed → Seated as title clears* | block at 0 after stamp | hidden |

\* At the commit frame the title is empty, so ADD's *content* state is
disarmed — but ADD is mid-travel returning from +3dp; its fill/offset settle
to Seated over the standard 260/280 pair during the stamp window. No special
case: the arm state is a pure function of content, and the animation system
absorbs the overlap.

**Transitions:**
- S0/S1 → S2/S3: focus gained (tap field, tap disarmed ADD, TalkBack). Bank
  expands (280ms). Draft, duration, and caret position are exactly as left.
- S2/S3 → S0/S1: focus lost (any §M2 collapse trigger). Bank folds (260ms);
  **draft and duration survive collapse** — collapsing is folding, not
  discarding. (Ivo's truth condition, adopted: nothing about collapse may
  imply the draft was thrown away; there is no discard sound, ever.)
- S3 → S4: armed commit. S4 → S2: stamp window expires, or any keystroke
  (instant). S4 → S1: focus lost mid-stamp — stamp clears, draft is empty,
  state is S0 in fact; the table's S4 assumes focus because submit requires
  the field, but the exit is defined: stamp does not survive collapse.
- Any → collapsed-locked rendering: `isSessionActive` flips true → focus
  cleared → S0/S1 shape with night shading (`effectiveNight`). **Dependency
  flagged, not decided here:** whether the composer accepts input mid-session
  is Zeile/Rurik's HOLD question for the queue as a whole — filing *future*
  work does not mutate the running session, and this spec's position is that
  the composer stays live; if the queue locks wholesale, the field disables
  (alpha to secondary tier, focus refused) under the same collapse, and no
  other line of this spec changes.
- Sound-enabled off (`soundEnabled == false`): the engine gates every call
  internally; every visual beat in §Motion still fires in full. Sound is an
  echo of facts the surface also shows; muting removes air, never information.

Duration selection is **not** part of the state machine: it persists across
every transition, across submits, across collapse — the rested choice is
reused by the tired hand until deliberately changed. Default 25 (existing
`remember` initial value).

---

## Edge cases

- **Whitespace-only title:** `armed` is computed on the trimmed string; three
  spaces leave ADD disarmed and its tap takes the redirect path. The stored
  draft keeps the spaces (his text is his), the gate does not.
- **Double-tap on armed ADD:** the first commit clears the title
  synchronously in the same frame as `onAddTask`; the second tap therefore
  lands on an empty field and takes the disarmed path (detent + refocus —
  focus is already there, so: detent only). Double-filing is impossible by
  construction, not by debounce timer.
- **Duration changed during the stamp:** the stamp keeps its snapshot
  (`QUEUED · 25M` — what *was* filed); the readout, after its 680ms window,
  reprints with the *new* duration for the next task. The two surfaces never
  disagree because they are never about the same task.
- **Queue position ≥ 100:** `String.format("%02d", 100)` renders `100`; the
  readout widens naturally. No truncation, no cap.
- **First task ever:** readout `25M · FIRST`; stamp readout `QUEUED · #01`.
  Both strings defined; neither is an afterthought state.
- **Long titles:** `singleLine = true` (existing); the field's internal text
  scrolls horizontally per `BasicTextField` default; the caret block follows
  `getCursorRect` and clamps to the visible text area by the layout result's
  own coordinates. The echo never overlaps text: it fades to 0 while the field
  is focused, and unfocused long text ellipsises *before* the echo's 12dp
  reserve (echo lives in a fixed right slot the text box does not enter).
- **Press cancelled (finger slides off a cap or ADD):** travel returns on
  specC(280); DOWN sounds already fired stay fired (they reported a touch,
  which happened); no commit sound, no state change. Selection commits on UP
  only.
- **Recomposition / rotation:** title, duration, and focus state use the
  screen's existing `remember` scope — behaviour identical to today's build.
  `justQueued` is transient by design and is lost on recreation; losing a
  680ms stamp to a rotation is correct, not a bug.
- **Engine absent (`soundEngine == null`, previews/tests):** every call is
  null-safe exactly as today (`soundEngine?.`); all motion and state proceed.
  The composer is fully usable silent — see the sound-off row in §State
  machine.
- **Haptics disabled / sound disabled independently:** the engine checks its
  own `hapticsEnabled` / `soundEnabled` per call (existing behaviour); this
  spec adds no second gate.
- **TalkBack traversal:** collapsed composer exposes three nodes (field, ADD
  slot, readout is decorative and excluded); expanded adds four radio caps in
  one group. The stamp is not announced as a live region — it duplicates the
  readout's fact and a screen-reader user gets the queue's new row in
  traversal order, which is the durable record.

---

## Code: helps / fights

**Helps (verified in the tree today):**
- `soundEngine: TactileSoundEngine?` is already a parameter of
  `FocusSequencerScreen` and already fired from `submitTask()` — every sound
  above is wiring, not architecture.
- `inkControlFill()` / `onInkControlFill()` (`MonolithCustomComponents.kt`)
  already resolve the night cap `#4A423C` + cream label correctly.
- `MonolithCurves` ships specA/B/C and all five durations used here.
- The Deck's letterpress `Shadow` constants and the Canvas `SledSwitchView`
  recess constants exist in-tree to port.
- `testTag("new_task_input")` and `testTag("add_task_button")` already exist
  and survive this spec unchanged (ADD's tag moves to the slot's outer box,
  which is where a tester would look for it).
- Foundation in the current BOM carries `AnimatedVisibility`,
  `animateItem`, `decorationBox`, `onTextLayout`, `FocusRequester` — every
  mechanism above is stock.

**Fights (named, with the cost of winning):**
1. **The composer lives inside a `LazyColumn` item.** Collapse/expand works
   fully in-item (`AnimatedVisibility` in the Section-2 `Column`). But
   *sticky* in the strong sense — the line pinned while the queue scrolls
   under it — is not available to an item. Winning costs one structural move:
   hoist Section 2 out of the `LazyColumn` into a `Column` above it (the
   queue list keeps its own scroll). This spec's collapse is complete without
   that move; the hoist is recommended as a separate, priced step in the same
   pass over this screen (it also gives the queue its own `LazyListState`,
   which Rurik's promotion-scroll needs). Do not half-hoist via
   `stickyHeader` — its experimental API pins the *header* semantics, not a
   sibling composer, and the seam shows.
2. **`playRotaryDetent` clamps to 280–1480Hz and renders 6ms.** The wooden-bar
   set's native pitches (146–262Hz) are unplayable through it — resolved by
   transposition (§Sound), at the cost of bar timbre. Accepted and stated,
   not hidden.
3. **The synth loop has no glide, no noise, no per-note scheduling.** Hence
   exactly two flagged engine additions (§Sound). The punch fits the existing
   160ms buffer cap; the strum raises `maxSamples` 160 → 520ms — one constant,
   plus its `minBufBytes` consequence (buffer grows ~3.25×; the track is a
   transient sonification track, not a stream, so the memory cost is ~90KB
   of float buffer — negligible, stated so it is a decision, not a surprise).
4. **Atkinson Hyperlegible ships only 400/700.** Any Medium in this element
   is a synthesized smear. This spec uses only real weights (placeholder 400,
   typed/labels 700, Anonymous Pro 700). Wave-1's Medium placeholder is
   corrected, not inherited.
5. **Current field code fights the recess:** the `.border(1.dp,
   outlineVariant)` and `cursorBrush = SolidColor(primaryContainer)` lines
   are deleted, not layered over. The orange hairline must not survive
   underneath the block caret "just in case" — §M6's no-fallback rule exists
   because of this line.
6. **`KeyCap` is shared shape, split future:** the bank caps and ADD diverge
   in this spec (radio latch vs momentary commit, different slots). Keep
   `KeyCap` private for caps; ADD is built in the top line's slot, not by
   calling `KeyCap` with `selected = true` — that call, in today's code, is
   the original impersonation bug and it does not survive.

---

## Law check

- **ARM ≤ 10s:** the composer is not on the seal path and adds nothing to it.
  Its own speed law is stricter: filing a task = type + one tap, with zero
  reopen/refocus/re-aim between tasks (§M5). The bank's 280ms expansion runs
  parallel to the keyboard rise and is never waited on.
- **HOLD absolute:** no control here mutates a running session. The one open
  dependency (mid-session composer input) is flagged in §State machine with
  this spec's position stated and the lock-compatible fallback defined.
- **TELL THE TRUTH once, in human language:** the disarmed ADD answers
  instead of lying silent; the stamp states what was filed in the window
  itself; the readout states the pending sentence in the margin; pitch is
  identity of a printed, sunk control and never the sole carrier of a fact;
  the strum fires once per completion and never for an incomplete close;
  collapse never implies a discarded draft.
- **One fact, one rendering:** duration renders as echo (collapsed) XOR sunk
  cap + ADD suffix (expanded) — never both; queue position renders in the
  readout and in Rurik's row meta, same formatter, same string; completion of
  steps (strum) and completion of task (Rurik's pulse) are different facts
  with different voices, and neither borrows the other's.
- **Beauty is retention:** the composer's beauty is mechanical, not applied —
  a well light falls into, caps whose state is their silhouette, a key that
  wakes as you type. Nothing here is decoration that a tired eye must parse.
- **Rested decides, tired executes:** duration, once chosen, persists through
  submits, collapse, and stamp; the tired hand re-decides nothing per task.
- **Accent budget:** exactly ONE orange on the Tasks screen — the cued task's
  START, below this element. Inside the composer: **zero**, including the
  caret. ADD is ink (wave-1 §7's argument, held). Never green: no colour in
  this spec is green or near it; the check glyph is not used anywhere (the
  stamp is a word, not a ✓).

### Acceptance checklist (render- and behaviour-level, day + night)

- [ ] Collapsed composer is one line: label row + 56dp field line, total
      76dp, no bank composed, queue visible below without scrolling.
- [ ] Collapsed line shows: placeholder or draft, inline echo `· <dur>M`,
      ADD as text (empty) or armed ink key (draft) — ADD's slot position
      identical in every state and shape.
- [ ] Tap field → bank fully expanded in 280ms, keyboard up, echo gone,
      duration rendered only by the caps.
- [ ] Field reads as a hole, not a sticker: top pool visible, one catch line
      at the bottom, both themes; night well is the darkest surface on screen.
- [ ] Focused empty field: one ink block caret blinking at ~1Hz (520ms
      half-cycle); unfocused: no caret; **no orange anywhere in the composer**.
- [ ] Caps: unselected show 3dp skirt; selected sits 1.5dp lower in ink with
      1.5dp skirt — verifiable in a settled screenshot, in grayscale.
- [ ] First typed character: ADD has risen and inked, label reads
      `ADD · <dur>M`, before the second character lands.
- [ ] Cap press: detent at the cap's CAP SET pitch on DOWN (15m highest,
      90m lowest); re-tap of the selected cap ticks at the same pitch.
- [ ] Submit: exactly one commit sound (punch; interim: latch) + swallow
      pulse + stamp `QUEUED · <dur>M` visible 420ms then fading 260ms +
      readout `QUEUED · #<nn>` for the same window; keyboard still open,
      focus retained, bank open, caret back at position 0.
- [ ] Filing rhythm: three tasks filed back-to-back with no refocus, no
      keyboard dismissal, no ADD re-aim; duration unchanged across all three
      unless a cap is tapped.
- [ ] Empty ADD tap: field gains focus, one 320Hz detent, nothing queued, no
      stamp, no error visual.
- [ ] IME Done behaves identically to ADD in both armed and empty states.
- [ ] Typing during the stamp kills the stamp instantly.
- [ ] Sub-steps: toggles tick at STEP SET pitches ascending by step number;
      the completing toggle strums once; DONE afterwards does not re-strum;
      a task closed with steps open never strummed.
- [ ] Ripple count in the composer: zero. Scale-press count: zero.
- [ ] Sound off and haptics off (independently): every visual beat above
      still fires; the composer is fully usable.
- [ ] Orange count inside the composer: **zero**, day and night.
- [ ] Green count inside the composer: **zero**, day and night.

---

## Highest-impact decision

**Collapse bound to focus itself — bank visibility ≡ `fieldFocused`, one
boolean, no open/close state — with ADD's address made invariant across both
shapes.** Wave-1 made the composer beautiful and 124dp tall forever; this
makes it beautiful *while filing* and 76dp the rest of the day, and the
boolean-binding is what makes it trustworthy: a bank that can only exist
while he is already inside the field can never surprise him, tax the arm
path, or desync from the keyboard. The friction death was never the field's
looks — it was rent charged on every visit. Collapse stops charging it.

---

## Addendum — cross-cohort mail received during this run (dispositions)

Two letters landed in my inbox while this file was being written (Taste,
Improvers keys; Fach, Improvers field). Dispositions, so the merge doesn't
have to guess:

- **Taste's ★ (selected duration leaves the ink family; ADD keeps ink
  untouched): NOT adopted.** This spec's brief locks the selected cap as sunk
  in ink, and the impersonation defect is solved here by other means: ADD and
  the selected cap are never the same *kind* of ink — ADD is a momentary key
  in an invariant slot whose ink is earned by content, the cap's ink is a
  latch held by position (+1.5dp, silhouette-readable). Taste's build warning
  **is adopted and was already this spec's design**: the punch lives in
  `onComposerCommit()`, the one wrapper both ADD and IME Done call — never in
  the cap composable. One action, one sound, one call site.
- **Fach's collapse (48dp line, label row cut, combined stamp
  `QUEUED · 25M · #04`, expand 260 CurveB / collapse 180 CurveA): NOT
  adopted where it conflicts, credited where it agrees.** The label row and
  the split stamp (field `QUEUED · 25M` / readout `QUEUED · #04`) are locked
  in this spec's brief (§M4, §M5) and stay. Fach's expand/collapse timings are
  faster than mine; mine (280 specC in / 260 specA out) are chosen so the
  bank settles *with* the keyboard's rise rather than racing it — flagged as
  an open timing divergence for Voss/Ren to settle by measurement, not taste.
  Fach's Atkinson correction (no Medium exists) **is adopted** — it was
  already applied in §M6 before his letter arrived. His Regular/Regular
  input choice is **not** adopted: typed text stays Bold, because the
  printed/typed weight step *is* the letterpress grammar of the well (§M6);
  the first-keystroke weight change lands in the same frame as ADD rising
  and inking — it reads as the machine taking the text seriously, not as a
  pop. Also an open divergence for the merge, stated rather than smoothed.
- Both letters moved to my `read/`; replies sent to both inboxes under the
  same thread slugs.
