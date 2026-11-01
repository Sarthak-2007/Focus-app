# Issue #7 — Shield (audit fixes) — Feel Spec: SCHWUNG (MOTION)

**Seat:** SCHWUNG, FEEL SPECIALIST 4 (MOTION), in VOGT's cell for issue #7,
"Main-app pass — Shield (audit fixes)."
**Domain, only this:** motion — every change of state on Shield that moves,
fades, scrolls, or conspicuously refuses to. What the words say is FORM's;
what the surface is made of is GLANZ's and Panzer's; how it *changes* is mine.
**Status:** DECLARED — specification only. No app code written or edited,
nothing compiled, no tests run. MONTEUR is sole code editor; §7 is his
motion patch list. GitHub coordination is VOGT's.
**Sources read:** `studio/plan/AUDIT-shield.md` in full (R-1…R-13);
`studio/cells/issue-7/designer-form.md` (§2.4), `innovator-funke.md`,
`critic-salz.md`, `finisher-glanz.md` (§3.3–3.4); the working tree on
`agent/issue-7`: `ui/theme/MonolithCurves.kt` (the whole house vocabulary),
`ui/screens/ShieldMatrixScreen.kt` (status line, tally, RoutineRow, banner),
`ui/ShieldBlockingStatus.kt` (the four proof states),
`ui/components/ShieldCapSelector.kt` (cap travel), the sled in
`ui/views/BlockitSuitcaseAndSwitchesViews.kt` (throw/settle), and the
reduced-motion precedent in `ui/components/MonolithCustomComponents.kt`
(the Strike's animator-scale snap, ~:807).

---

## 0. The motion law of this screen, in four sentences

1. **Shield is a wall, not a slot machine.** Nothing on it rolls, counts,
   odometers, pulses, blinks, shakes, springs past its stop, or flashes.
   Every change of fact is one whole, calm re-strike — or no motion at all.
2. **No new curves, no new durations.** Every spec below is paid for from
   the vocabulary that already exists: durations {110, 140, 180, 250, 260,
   280, 420, 520} ms (`MonolithCurves`), Curves **A** (0.2, 0, 0, 1 — SEAT:
   fast, exact, zero overshoot), **B** (0.05, 0.7, 0.1, 1 — long travel),
   **C** (0.3, 0, 0.8, 0.15 — exit/collapse), and the `MonolithKin` names
   (SEAT = A; THROW = CubicBezier(0.5, 0, 1, 0.4), the dismissal easing;
   drawer rise 280 / leave 160; tab fade-out 120 / fade-in 240).
   `MechanicalOvershoot` / `MechanicalAnticipateOvershoot` exist in the
   file and are **never spent on Shield** — overshoot is a toy's manners,
   and this screen's whole job is to be believed.
3. **Motion follows a booked fact, never leads it.** (The house order,
   already stated in the Deck's code: write, commit, publish — then
   sound/motion.) No transition on this screen may make a state *look*
   settled before the derivation that owns it has settled.
4. **The user's own finger is not animation.** 1:1 drags (sled, cap) are
   his motion and are never eased, delayed, or "helped." Only releases,
   landings, and machine-caused changes get a spec.

### What is wrong in the tree today, motion-wise (the charges this spec answers)

- The status sentence is a plain `Text`: a state change is an **instant
  swap of words and colour in the same frame** — ink → `scheme.error`
  red with no transition. Read at 1 AM, that is an alarm flash wearing a
  sentence's clothes, and a flapping proof (cold start, rebind, stale
  beat) would strobe it.
- `RUNNING NOW` is a **string suffix concatenated into the routine meta
  line** (`routineRunningNowSuffix`): it pops in and out with zero
  transition, re-laying-out the whole meta text in the frame it changes.
- The routine editor opens with a bare `if (expanded)`: a **jump cut**
  that shoves everything below it down in one frame.
- The ratchet banner uses `AnimatedVisibility` with **no enter/exit spec
  at all** — Compose defaults, off-vocabulary. R-7 ordered its motion
  "specced to the house curves"; nobody had specced it. §5 does.
- GLANZ §3.3 gives the tally jumps `animateScrollToItem`'s **default
  spec**. A default is not a house curve. §4 supersedes that one line,
  in this domain only.

---

## 1. THE MOTION TABLE — every transition on Shield

Durations are visual durations unless marked *delay*. "Cut" = 0 ms,
single-frame replacement. Reduced-motion column is summarised here and
ruled in full in §6.

| # | Transition | Duration | Curve | Notes |
|---|---|---|---|---|
| M-1 | **Status sentence, any state → any state** (BLOCKING NOW ↔ NOT BLOCKING ↔ CAN'T CHECK / unproven fallback, incl. Safe-Break segment appearing) | 260 ms crossfade | EasingA (`specA(260)`) | Whole line only — one `AnimatedContent` keyed on the *entire* headline + proof state, `fadeIn` + `fadeOut` together, exactly `StandingTallyLine`'s construction. No per-word, per-segment, or per-digit animation; the `ENDS HH:MM` never rolls or counts. Colour is **discrete inside the fade**: outgoing colour fades out, incoming fades in — never a colour lerp, never a background, never a repeat blink. §2. |
| M-2 | Status-sentence **anti-flap coalesce** (pre-strike) | 250 ms *delay* | — (not a visual) | A new headline must survive 250 ms before it may strike; bursts collapse to the latest state, intermediates never print. The tally's governed 250 ms law (`DURATION_250`), extended — same number, no new token. §2.2. |
| M-3 | Status sentence, **first frame** (cold open, any state) | 0 ms — cut | — | Facts do not fade in. The line is present at frame 1 in whatever state the cold-start rule dictates; the first *change* after that is M-1. |
| M-4 | **Tally re-strike** (existing, governed — kept whole) | 250 ms *delay* coalesce + 260 ms crossfade | EasingA | Already in `StandingTallyLine`; restated so no builder "improves" it while rebuilding the line as jump segments. The **whole row** strikes — numerals, words, separators, jump bars together (GLANZ §3.4's extension adopted). Segments never animate individually. Never a digit roll. |
| M-5 | **Tally word-as-jump scroll** — tap SHUT / PEEK / OPEN, target group off-screen | 420 ms; **520 ms cap** if travel > ~2 viewports | EasingB (`specB`) | Long-travel curve; B's default in the file *is* 420. Lands the group header exactly at viewport top, where it sticks — **no bounce, no overshoot, no settle-spring, no landing highlight flash**. Interruptible instantly by any touch/fling; no input lock during travel. If the target header is already at the top / fully visible: **0 ms, no scroll at all** — travel is never performed for its own sake. Filtered case (GLANZ's `pendingJump`): filters clear by cut, scroll starts only once the unfiltered groups have landed — the two never animate over each other. §4. Implementation: drive the offset (`animateScrollBy` against the counted index/offset, specB) — `animateScrollToItem`'s built-in default is off-vocabulary and does not ship as the final spec. |
| M-6 | **RUNNING NOW appears** on a routine row | 260 ms fade-in | EasingA | Suffix segment only (see §3 — it must become its own composable segment, not a longer string). **Opacity only**: no slide, no scale, no width spring. Row height, name, time column, and the ON/OFF cap do not move by a dp. |
| M-7 | **RUNNING NOW disappears** | 160 ms fade-out | EasingThrow (`MonolithKin`) | A dismissal, priced like the drawer's leave (160 THROW): quicker and quieter than its arrival. It leaves; it does not land. Opacity only, same no-reflow law as M-6. |
| M-8 | RUNNING NOW, steady state / per-second tick recompositions | 0 ms — nothing | — | `nowMillis` ticks while Shield is visible. The marker animates **only on the boolean edge** (false→true, true→false). A tick that changes nothing must produce no strike, no fade, no recomposition-visible motion of any kind. Opened mid-window: marker present at first frame, M-3's law — no entrance. |
| M-9 | **Routine row expand** (tap row → editor) | 280 ms | EasingA (SEAT) | Height + fade together (`expandVertically` + `fadeIn`, or `animateContentSize` with `specA(280)` — one mechanism, builder's pick, this spec). The mini-drawer follows KIN-DRAWER's rise. Editor contents arrive **as one body** — no stagger, no per-chip cascade. |
| M-10 | **Routine row collapse** | 160 ms | EasingThrow | Height + fade out, KIN-DRAWER's leave. What it pushes back up must travel with the same spec — the content below rides the height animation, it does not jump after it. |
| M-11 | **Ratchet banner appears** (Deck-origin refusals, post-R-7) | 280 ms | EasingA | `expandVertically` + `fadeIn`, specA(280) — replaces today's unspecified `AnimatedVisibility` defaults. It is item 1, *below* the status sentence: the sentence's slot never moves; the content below the banner rides the expansion smoothly, once. |
| M-12 | **Ratchet banner dismisses / times out** | 160 ms | EasingThrow | `shrinkVertically` + `fadeOut`. **Hold time is not motion**: the banner stays fully readable for its whole dwell and never fades early, pulses, or dims as a countdown (audit R-7: does not self-erase mid-read). |
| M-13 | **In-place ratchet refusal** at the touched sled / group line (R-7) | Appear 180 ms fade; word change 260 ms crossfade | EasingA | The refusal word strikes **at the control**, fast enough to read as the answer to his gesture (180 = the file's quick-answer rung; KIN-NAV's base). If the refusal replaces a word in place, it re-strikes whole like a slip word (the retired `SlipLineRow`'s 260 ms Curve-A word strike is the precedent). **No shake. No x-wiggle. No red flash.** A shake is a scolding; this screen informs, it does not scold. |
| M-14 | **LOADOUT cap travel** (existing, acquitted — kept) | 260 ms | `specA(260)` | Already `ShieldCapSelector`'s spec; do not re-tune. Riding clause for R-4's Ghost Preview: ghosts **fade in 180 ms EasingA** to their hollow state; on commit, ghosts dissolve *inside* the cap's own 260 ms travel (one combined change, never 180 + 260 stacked into a two-act play). |
| M-15 | **Sled throw / settle** (existing, acquitted — kept) | 260 ms; 340 ms if thrown > 1 stop | CurveA (View `CurveA`) | As built in `AppRowSledToggleView.animateRockerTo` (800 dp/s flick law included). Vega V-2's provisional cap is a *drawing state during his drag*, not an animation — it gets no duration. Cancel re-seats on the same spec. |
| M-16 | **Catalogue row leaves / joins a group** after its own sled flip | 0 ms — cut | — | **No FLIP flight, no exit fade, no cross-group travel.** A row flying from PEEK to SHUT would be theatre, would misstate when the ruling changed (it changed at the sled, under his thumb), and the tally's M-4 strike already prints the consequence. GLANZ P-5 retires the slip's exit choreography rather than hoisting it; I concur, on motion grounds. Named follow-up if the phone proves the cut jarring: the slip's old exit (280 ms, EasingC, fade + height collapse) on the leaving row only — not in this merge. |
| M-17 | **Group header sticks / unsticks** while scrolling | 0 ms — nothing | — | Pixel-identical in both positions (GLANZ §4.3). No shadow bloom, no fade, no condensed form. Stickiness is a position, not an event. |
| M-18 | **Search / filter change** → catalogue contents | 0 ms — cut | — | Typing is his motion; results are its direct consequence and must feel instant. No list crossfade, no row stagger-in. (`SHOWING n` updates with the same cut — it is a keystroke's echo, not a burst of machine flips, so the tally's coalesce law does not apply to it.) |
| M-19 | **Status retry tap** (CAN'T CHECK → re-read) | 0 ms — nothing | — | **No spinner, no pulse, no "checking" shimmer.** The line keeps printing CAN'T CHECK until the fresh proof lands, then changes exactly once, by M-1. A spinner on the truth line would be the screen performing effort instead of stating a fact. |
| M-20 | **Whole-screen palette swap** (`effectiveNight` follows session start/stop) | 0 ms — discrete, single frame | — | The theme flip is synchronised to the same frame the status strike *starts*, and is never itself tweened. A 260 ms screen-wide colour wash is a flash by another name. The sentence crossfades (M-1) over an already-swapped palette. |
| M-21 | **Website row removed** (BLOCKED WEBSITES, R-3 surface) | 280 ms fade + height collapse | EasingC (`specC(280)`) | The slip exit's exact spec, reused where an exit is honest: he deleted it, at that row, and the neighbours closing up *is* the receipt. Additions print by cut (M-18's law — his keystroke's consequence). |

**Explicitly unanimated, forever, on this screen:** the status glyph
(solid / hollow / 55% square — it changes *inside* M-1's crossfade, never
on its own clock), the ARMED count, the group counts, the glyph legend,
the PEEK contract line, section headers. If it is a printed fact and it
is not in the table, it does not move.

---

## 2. The status sentence — the deep spec

### 2.1 One strike, whole line, no exceptions

The derivation (`ShieldBlockingStatus`) already returns **one headline
string and one proof state**. Motion keys on exactly that pair:
`AnimatedContent(targetState = status)` with M-1's spec. Consequences
that are rulings, not implementation details:

- **Segments never animate separately.** When `· SAFE BREAK — PEEK APPS
  OPEN` appears, the whole line re-strikes; the segment does not slide
  in from the right like a news ticker. When `ENDS 03:30` changes
  because a *new session* started, that is a new state and strikes
  whole; within one session the end is held (FORM §1.3), so the line
  has no reason to strike on any timer.
- **No digit treatment, ever.** The time is print, not a counter. No
  roll, flip, count-up/down, or odometer on `ENDS`, on the label, or on
  any numeral the line contains. This is the tally's M4 governor
  ("a rolling digit is a slot machine's manners") applied to the
  sentence the screen exists for.
- **No alarm grammar.** NOT BLOCKING arriving is bad news delivered
  calmly: one crossfade, once. Specifically banned: background flash
  or fill of any colour; blink / pulse / repeat-fade on the error
  colour; scale pop; slide-down "alert drop"; any looping animation
  in any state, including CAN'T CHECK. A live wall is calm; a dead
  wall is a sentence, not a siren.
- **The colour question, motion's share of it.** Today's code prints
  non-blocking in `scheme.error` and blocking in ink, swapped in the
  same frame as the words. Under M-1 the two colours crossfade via
  opacity (each word-instance keeps its own colour for its whole
  life). There must never be a frame in which one line is shown
  mid-lerp between ink and red — a pink half-state is a third,
  unproven state rendered in pigment.

### 2.2 The anti-flap coalesce (M-2) — why the sentence needs a governor

The tally earned its 250 ms coalesce because sled bursts are bursty.
The status proof is burstier: cold start (no warrant yet → witnesses
landing beat by beat), accessibility rebind, process restart, and the
stale-beat threshold can each flip the derivation more than once in
well under a second. Without a governor, M-1's crossfade would chain —
CAN'T CHECK → BLOCKING → NOT BLOCKING strobing at crossfade rate,
the exact alarm strobe §2.1 bans, produced by honest machinery.

So: **a candidate headline becomes printable only after it has been
the derivation's output, unchanged, for 250 ms.** Bursts print once,
at the latest state. This is a *print delay*, knowingly priced: it is
the same governed lag the audit accepted for the tally ("priced and
accepted" — standing charges), it is bounded by the same 250 ms, and
250 ms of delay is not a lie where 250 ms of *strobe* would be a harm.
It applies to witness-driven changes. It does **not** stack with M-1:
worst case from settled fact to settled print is 250 (stable) + 260
(strike), once, never a chain.

One boundary, stated so nobody "fixes" the wrong one later: if a
session genuinely ends and a new one genuinely begins seconds apart,
those are two settled facts and the line strikes twice, calmly, by
M-1. The governor eats flicker, not history.

### 2.3 TalkBack timing (R-11's motion share)

The sentence is a live fact; its announcement must land **once, after
the strike completes**, reading the new state — never mid-crossfade
(two half-faded instances are two nodes; an announcement fired at
strike start can read the outgoing line). Under reduced motion (§6)
the cut *is* the completion: announce at the cut.

---

## 3. RUNNING NOW — the deep spec

**The structural fix first:** today the marker is a suffix inside one
`Text` string (`"${days} · ${loadout}" + routineRunningNowSuffix(...)`),
so its arrival rebuilds the whole meta line's layout atomically. For
M-6/M-7 to exist, the suffix becomes its **own segment** at the end of
the meta row (a `Row`: days/loadout text, then the marker segment),
so opacity can animate on the segment alone and nothing else in the
row is ever re-struck. The print is unchanged: same words, same face,
same position — ` · RUNNING NOW`, full ink (FORM §3.2).

- **Appear (M-6): 260 ms EasingA fade-in.** It earns the tally-class
  duration because it is the same kind of event — a fact arriving
  from the machine, not an answer to his finger. No slide-in: a word
  that travels reads as a notification arriving; a word that *fades*
  reads as a state becoming true.
- **Disappear (M-7): 160 ms EasingThrow fade-out.** The window ended;
  the word is dismissed, and dismissals in this house are shorter
  than landings (drawer 280/160; tab 120-out/240-in). It must not
  linger at partial opacity — a half-faded RUNNING NOW at the
  boundary minute is an unproven state in pigment, §2.1's law
  applied to a row.
- **No reflow theatre.** The marker sits at the *end* of the meta
  line; its fade changes no height and moves nothing to its left.
  The ON/OFF / WON'T FIRE cap is in a separate column and never
  shifts. If a long marker would wrap the meta line on a narrow
  screen, that is a layout bug to fix in layout (FORM's truncation
  order), never by shrinking or sliding the marker.
- **Edge-only (M-8).** The derivation re-runs on the screen's clock
  beat; the animation system sees only the boolean. Implement as
  `AnimatedVisibility` / `AnimatedContent` keyed on the boolean, not
  on `nowMillis` and not on the composed string.
- **Contradiction stays visible, calmly.** FORM §3.2 rules that a row
  may print RUNNING NOW under a headline that reads NOT BLOCKING —
  the missed fire surfacing. Motion adds nothing to that moment: no
  emphasis, no pulse on either line. The two facts strike by their
  own specs and sit there. The screen reports; it does not dramatise
  its own contradiction.

---

## 4. The group jumps — the deep spec

R-1 makes the tally's words doors; GLANZ §3.3 builds them. Motion's
rulings on the travel itself:

- **Curve and price (M-5): EasingB, 420 ms, capped at 520 ms.**
  Curve B is the house's long-travel easing (its `specB` default is
  420 for a reason: fast commitment, long gentle settle — a scroll
  that arrives, not one that parks). Short hops (target within the
  first viewport or so) may use the same 420 — one price for all
  jumps is calmer than a distance formula he could feel varying;
  the 520 cap exists only so a 61-row OPEN jump does not stretch
  B past the point where it reads as drifting. **Never Curve A for
  the long jump** (A spends its whole ease in the first instants —
  at catalogue distances it reads as a teleport with a tail), never
  any spring/overshoot at the landing.
- **The landing is the confirmation.** The group header arrives at
  the viewport top and sticks (M-17). No extra feedback is added:
  no header flash, no tally strike, no haptic invented here. If the
  header he tapped for is not the header at the top when travel
  ends, the jump is broken (GLANZ's counted-index law) — motion
  cannot paper over a wrong landing with a flourish.
- **Interruption is absolute.** A touch, fling, or sled gesture
  during travel cancels the scroll where it stands — the man's
  finger outranks the machine's itinerary, always, with no
  completion of the remaining distance and no snap to the target.
- **Already there = nothing happens (M-5 note).** Tapping SHUT
  while the SHUT header is already stuck at the top produces no
  scroll, no bounce, no acknowledgment animation. A door that
  performs opening when you are already through it is lying about
  having done something.
- **The filtered path is two cuts and a travel, in that order:**
  clear search/category (cut) → groups recompose (no animation) →
  travel (M-5). Never scroll the *filtered* list toward where the
  group will be; never fade the catalogue out and in around the
  clear. One perceived move, honestly sequenced.

---

## 5. The refusal and the banner (R-7's motion, specced at last)

R-7 ruled the banner's motion "specced to the house curves" and the
in-place refusal is the audit's headline #5 — the invisible refusal.
Motion's share:

- **In-place means in-place in time, too** (M-13): the refusal prints
  at the touched control within the same gesture's rhythm — its
  180 ms fade *starts* on the refusal, it does not wait for scroll,
  coalesce, or any queue. The tally's 250 ms governor (M-2/M-4)
  governs counts and the status proof; it must **never** be applied
  to a refusal. A refusal delayed is the invisible refusal again,
  with better typography.
- **The banner is the slow lane, deliberately** (M-11/M-12): Deck-
  origin refusals arrive on a surface he is not touching, so they
  get the drawer's manners (280 in / 160 out). Both banner and
  in-place prints share one ban list: no shake, no flash, no pulse,
  no slide-from-top alert drop.
- **Neither refusal ever moves the status sentence.** The banner is
  below it (FORM §2.1's order) and expands downward; an in-place
  refusal lives inside its row. The first line of the screen is
  load-bearing and motionless except by its own M-1 strike.

---

## 6. Reduced motion — the full ruling

**Detection: the system's, by the house's existing precedent — no new
toggle is invented.** The Strike in `MonolithCustomComponents.kt`
(~:807) already reads `Settings.Global.ANIMATOR_DURATION_SCALE` and,
at `0f`, *snaps instead of tweening* ("the Strike is a single-frame
step — snap, don't tween"). Shield adopts that exact test and that
exact verb. There is no in-app reduced-motion setting, and this spec
does not create one; a second, app-local opinion about his system
setting would be a lie waiting for its audit.

Under `ANIMATOR_DURATION_SCALE == 0f`, every row of the table degrades
the same way — **motion is removed; information timing is not**:

| Spec | Reduced-motion form |
|---|---|
| M-1 status strike | **Cut.** New headline replaces old in one frame. |
| M-2 coalesce | **Unchanged.** It is a stability delay, not a visual; removing it would reintroduce the strobe for exactly the user who asked for calm. |
| M-4 tally strike | **Cut** (its 250 ms coalesce unchanged, same reason). |
| M-5 jump scroll | **Instant** — `scrollToItem` to the same counted index. The landing, the header, the fact: identical. Only the travel is gone. |
| M-6 / M-7 RUNNING NOW | **Cut** in / cut out on the boolean edge. |
| M-9 / M-10 row expand/collapse | **Instant.** Editor present/absent in one frame; layout final immediately. |
| M-11 / M-12 banner | **Instant** show/hide. Its **dwell is unchanged** — reduced motion never shortens how long a refusal can be read. |
| M-13 in-place refusal | **Instant** print at the control. (Its immediacy was the point; reduced motion makes it *more* immediate, correctly.) |
| M-14 cap travel / M-15 sled settle | **Snap to the detent.** The drag itself is his finger (1:1) and is untouched; only the machine's settle after release snaps — the Strike precedent applied to a release. The committed tier and the cap's final position are identical, in the same frame as release. |
| M-20 palette swap | Already a cut; unchanged. |
| M-21 website row exit | **Cut.** Row gone in one frame; neighbours close instantly. |

Two honest limits, flagged for VOGT rather than papered over:
(1) partial animator scales (0.5×) are **not** honoured by Compose
tweens anywhere in this tree today — the house precedent tests only
`== 0f`, and this spec matches the precedent instead of inventing a
scaling law one screen wide; (2) TalkBack announcements (§2.3) are
**not** motion and are never reduced — every state still announces
once, in full.

And the standing note: Shield was already nearly compliant by
temperament — it has no loops, no parallax, no overshoot anywhere in
this spec to remove. Reduced motion here is a set of cuts, not a
redesign, which is exactly what it should be.

---

## 7. Exact motion patches for MONTEUR

In `ShieldMatrixScreen.kt` unless named. Anchors are to the working
tree at writing.

- **S-1 · Status line wrapper (M-1, M-2, M-3).** Replace the bare
  `Text(statusHeadline)` item with: a `statusShown` state fed by a
  `LaunchedEffect(statusHeadline)` that delays 250 ms before
  publishing (the tally's coalesce construction, copied, keyed on
  the full `ShieldBlockingStatus` so proof + headline strike
  together), rendered inside `AnimatedContent(statusShown,
  fadeIn(tween(260, EasingA)) togetherWith fadeOut(tween(260,
  EasingA)), label = "shield_blocking_status")`. Initial value =
  first derivation, no entrance. Keep testTag
  `shield_blocking_status`. Colour stays per-state on each instance
  (ink / error as the wording seats rule) — no `animateColorAsState`
  anywhere near this line.
- **S-2 · RUNNING NOW segment (M-6–M-8).** In `RoutineRow`, split the
  meta `Text`: days · loadout as today, then the suffix as a separate
  `AnimatedVisibility(visible = isRunning, enter = fadeIn(tween(260,
  EasingA)), exit = fadeOut(tween(160, MonolithKin.EasingThrow)))`
  wrapping `Text(" · RUNNING NOW")`, where `isRunning` is the
  boolean behind `routineRunningNowSuffix(routine, nowMillis)`.
  Nothing else in the row changes.
- **S-3 · Row expand/collapse (M-9, M-10).** Replace the bare
  `if (expanded)` around the editor rows with
  `AnimatedVisibility(expanded, enter = expandVertically(tween(280,
  EasingA)) + fadeIn(tween(280, EasingA)), exit =
  shrinkVertically(tween(160, EasingThrow)) + fadeOut(tween(160,
  EasingThrow)))` around both editor rows as one body.
- **S-4 · Banner spec (M-11, M-12).** Give the existing
  `AnimatedVisibility` the explicit enter/exit above (280 A expand +
  fade / 160 Throw shrink + fade). Its tokenised colour is R-7's
  patch, not mine; its motion is this one.
- **S-5 · Jump drive (M-5).** Where GLANZ P-4 lands the jump, drive
  it with `specB`: compute the target offset from the counted index
  and `listState.layoutInfo`, `animateScrollBy(offset, tween(420 —
  or 520 if |offset| > ~2 viewports —, EasingB))`; skip entirely if
  the header is already at the top. Cancellation by user gesture is
  the scroll state's default — do not wrap it in anything that
  swallows gestures.
- **S-6 · Reduced-motion gate (§6).** One small helper, read once
  per composition from `Settings.Global.ANIMATOR_DURATION_SCALE`
  (the Strike's `runCatching` read, reused in shape): when `0f`,
  S-1…S-5 select their cut/instant/snap forms (duration 0 tweens /
  `scrollToItem` / snap detents). No new setting, no persistence.
- **S-7 · Nothing else moves.** Cap (`ShieldCapSelector`), sled
  (`AppRowSledToggleView`), sticky headers, search, counts: their
  current motion (or deliberate stillness) stands as tabled. If a
  patch above seems to require touching their internals, stop and
  bring it to VOGT.

**Verification (device, Ren's — paper claims end here):** flip a
session on/off from the Deck and watch the sentence strike once, with
no red flash frame and no intermediate headline; kill the
accessibility service mid-session and confirm the line settles to its
unproven form in exactly one strike after the coalesce; stand at a
routine boundary and watch RUNNING NOW fade in (260) and out (160)
with the row's cap never shifting; tap each tally word from the top
and from deep in OPEN and confirm the landing header is the tapped
group, stuck, with no bounce; interrupt a long jump mid-travel with
a touch and confirm it stops dead; repeat the set with system
animations off and confirm every change is a cut to the identical
final state — and every refusal is still readable for its full dwell.

---

## Draft GitHub comment (for VOGT to post)

— SCHWUNG (FEEL · MOTION), for VOGT's issue #7 cell.

Motion spec delivered: `studio/cells/issue-7/feel-schwung.md` (DECLARED — spec only, no code edited, nothing compiled). One law governs the whole screen: Shield is a wall, not a slot machine — every change is one whole, calm re-strike or no motion at all, and every duration/curve below is paid from the existing house vocabulary (`MonolithCurves` A/B/C + `MonolithKin` SEAT/THROW); no new curve, no new duration, and `MechanicalOvershoot` is never spent on this screen.

The table (M-1…M-21) in brief: **Status sentence** — today it is a bare `Text` whose colour snaps ink→error in the same frame as the words: an alarm flash. Specced as one `AnimatedContent` on the whole headline + proof state, 260 ms Curve-A crossfade (the tally's exact construction), colour discrete inside the fade — never lerped, never a background, never a blink — **plus a 250 ms anti-flap coalesce** (the tally's governed delay, extended): witness bursts at cold start / rebind / stale-beat settle to the latest state and strike once, intermediates never print. First frame is a cut; the retry tap gets no spinner; the `effectiveNight` palette swap is a discrete frame synced to the strike, never a screen-wide colour wash. **RUNNING NOW** — today a concatenated string suffix that pops; specced as its own segment, opacity only: in 260 ms EasingA, out 160 ms EasingThrow (the drawer's leave price), animated only on the boolean edge so the per-second tick produces no motion, and nothing else in the row ever shifts. **Tally jumps** — GLANZ's `animateScrollToItem` default is superseded in this domain: jumps run EasingB, 420 ms (520 ms cap beyond ~2 viewports), landing the group header stuck at the top with no bounce or landing flash; already-there means no scroll at all; any touch cancels travel dead; the filtered path is clear-by-cut, then travel, never both at once. **Also specced, previously unspecced:** routine editor expand 280 A / collapse 160 Throw (today a bare `if` jump cut); ratchet banner 280 A in / 160 Throw out with its dwell untouched (R-7's "specced to the house curves," finally specced); in-place refusal at the touched sled — 180 ms fade, word re-strike 260 A, **no shake, no flash, and never subject to the tally's coalesce**; catalogue rows re-group by cut (no FLIP flights — the tally's strike is the consequence's print); cap travel (260 specA) and sled settle (260/340 CurveA) kept exactly as acquitted.

**Reduced motion** is ruled by the house's own precedent — the Strike's animator-scale test in `MonolithCustomComponents.kt`: at `ANIMATOR_DURATION_SCALE == 0f`, everything snaps/cuts/instant-scrolls to the *identical* final state, coalesce delays and refusal dwell are kept (they are stability and reading time, not visuals), TalkBack still announces once, and no new in-app toggle is invented. Two limits flagged honestly in the file: partial animator scales are unhonoured tree-wide today, and this spec matches the precedent rather than inventing a one-screen scaling law.

§7 is MONTEUR's patch list (S-1…S-7) and the on-device motion checks are listed at its foot. Nothing else on Shield moves: if it is a printed fact and it is not in the table, it does not move.

— SCHWUNG

---

*SCHWUNG, feel specialist (motion), VOGT's issue #7 cell — Oct 30, 2026.
DECLARED: specification only; no app code edited, nothing compiled, no
tests run, nothing committed or pushed. GitHub coordination is VOGT's.*
