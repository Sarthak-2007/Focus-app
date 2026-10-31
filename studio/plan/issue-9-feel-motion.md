# ISSUE #9 — FEEL 3 (MOTION): PARK'S MOTION, AND THE CHECK VERB'S

*Feel 3 (motion), issue-9 cell (for Hof). Ground: the cell tree's
built `ParkDrawer.kt` (KIN-DRAWER travel, the 5s staleness beat,
the FIX key's `AnimatedVisibility`, the CHECK key + NOT PROVED
line as seated), `MonolithKin` / `MonolithCurves.kt`,
`WatchStore.kt` (`runSweep` + `permPrintAt`), `AUDIT-park.md`
(§7's metronome acquittal above all), `issue-9-designer.md`,
`issue-9-finisher-board.md` (F-B6, the board's animation law),
`issue-9-finisher-keys.md` (the keys' dip + entry tempo). Paper
only — nobody compiles. Every item is tagged **SPEC-ONLY** (law,
no code change) or **PATCH-READY** (exact change, anchored to
the built file). Where a finisher already ruled a joint, this
spec adopts it and says so — motion does not re-open settled
joints, it fences the ones nobody had claimed: the landings,
the dismissal mid-check, and the budget.*

*Scope, exactly: KIN-DRAWER and everything the CHECK verb sets
in motion. Not sound (Feel 2's seat — voices are cited only
where a motion and a sound share an instant), not touch
geometry, not the board's words or dresses (Finisher 1's).*

---

## §0 · THE HARD LAW, RESTATED AS A MOTION LAW

CHECKING is derived from store stamps, never a timer
(`ParkDrawer.kt`: `checkPressedMono > 0L && !PERMISSIONS.all {
measuredMono >= checkPressedMono }`). Motion inherits that
law whole:

> **No animated thing in this drawer may imply a measurement
> that has not landed.** No progress arc filling on a guess, no
> shimmer pretending to measure, no pulse whose period could be
> read as a sweep cadence, no sequence that makes parallel
> landings look ordered. The sweep's six queries land truly in
> parallel (`WatchStore.runSweep`: six launches at t=0, each
> stamping on its own landing, "a fast grant never waits for a
> slow one"). Any motion that orders, paces, or smooths those
> landings is a lie about the machinery, told in movement.

PARK is the app's quietest screen and a proof surface. Its
motion register is therefore the smallest in the app: the
sheet travels, a key dips, and *nothing else moves, ever*.
§6 prices that as a budget; §§1–5 spend it.

---

## §1 · THE ROWS AS RE-STAMPS LAND — INSTANT PRINT SWAP. NO CROSSFADE.

**Verdict — SPEC-ONLY (as built; this item is a fence):** when a
re-stamp lands during a check, the row's word and ink swap
**atomically, in the landing frame, at 0ms, with no transition
of any kind.** The built rows already comply — no animation API
touches `ParkBoardRow` / `ParkSocialSitesRow` — and the
crossfade, the obvious temptation a "checking" moment invites,
is ruled out on four grounds, the first of which is the audit's
own:

1. **The metronome (AUDIT-park §7, acquitted by name).** The
   board is nine ruled lines at a **49dp pitch** (48dp row +
   1dp hairline) with a **flush word column** — Linea's
   metronome finding, binding. A crossfade puts two words in
   one cell for its whole duration: two right-flush edges,
   glyphs over glyphs, the column reading both words and
   therefore neither. Worse, the words differ in length
   (`GRANTED` → `MISSING` → `STALE`): print grows a longer word
   *leftward from the flush edge*; a crossfade smears that edge
   for every frame it runs. The metronome survives a swap. It
   does not survive a smear.
2. **Ink tiers are discrete dresses, not a dial.** GRANTED
   (quiet), STALE (half-ink 0.60, Finisher 1 F-B2), MISSING
   (full lack-ink) are three *meanings*. A tween between them
   prints every value in between — inks that mean nothing, on
   a surface whose whole job is that its prints mean exactly
   one thing. (Finisher 1's F-B6 makes the same ruling for the
   word's colour; this spec extends it to the word's very
   presence: no fade-in of a landed word either. A stamp does
   not fade in. It lands.)
3. **Parallel landings + staggered fades = a fake progress
   wave.** Six landings arrive at unrelated instants. Give each
   a 160–280ms fade and the board develops a rolling shimmer
   that chases the landings down the rows — visually a progress
   indicator, drawn by accident, implying an order and a pace
   the sweep does not have. That is §0's forbidden object in
   its most plausible disguise.
4. **A blank or ghost frame is a claim.** No swap may pass
   through `null` (name-only), through a dimmed "about to
   change" dress, or through the old word held artificially
   until the set completes. The store is never cleared at press
   time (as built — the press records `checkPressedMono` and
   dispatches; the snapshot stands until landings replace it
   item by item). Partial truth prints as it lands; batching
   the landings into one simultaneous reveal at set completion
   would be theatre, and would hide the one thing a check is
   for: watching which doors answer.

**The swap's exact mechanics — SPEC-ONLY:**

- Word and ink derive from the *same* `permPrintAt` read in the
  *same* composition (Finisher 1 F-B3's single-derivation
  rule), so a landing can never tear — word new / ink old for
  one frame is a torn print, and torn prints are motion bugs.
- Interceptor rows obey the same law. They re-derive inside
  `WatchStore.publish` on every permission landing, so a
  permission's landing may swap an interceptor's word in the
  same frame (RUNNING → OFF as a parent re-stamps MISSING).
  No cascade treatment, no delay to "explain" the side-effect:
  same frame, same step, or the board is telling two times at
  once.
- **The staleness beat does not pause for a check.** The 5s
  `nowMono` re-derivation keeps running while CHECKING stands.
  An unlanded row whose old GRANTED crosses the 45s line
  mid-check flips GRANTED → STALE in one frame, and flips back
  when its stamp lands. This double swap is not flicker to be
  debounced away — it is the derivation telling the truth
  twice. Any hold/hysteresis added to spare the eye that
  second swap would print a freshness nobody measured.
  **Ruled: no debounce, no row-locking during CHECKING.**
- Landings that arrive *during the sheet's rise* (§2) print
  the same way — mid-travel, in place on the moving sheet.
  Print is never frozen until settle; only operability is
  (§2). Freezing print would batch real landings into a fake
  simultaneous reveal at the seat — §0 again.
- LAST PROVED (the head telltale, as built) swaps in the same
  publish that carries the final stamp — `WatchStore.publish`
  advances it inside the same snapshot — so the telltale and
  the last row change in one frame. It gets no highlight, no
  flash, no dwell. A new time printing is the whole event.

---

## §2 · THE CHECK KEY'S ENTRANCE — IT HAS NONE. THE SHEET'S IS ITS ENTRANCE.

**SPEC-ONLY (as built; protect it):** the CHECK key is a bare
`if (!isSealed)` block inside the sheet's scroll column. It is
not wrapped in `AnimatedVisibility`, carries no fade, no
expand, no delay, no stagger behind the board. **That is
correct, and it is the spec:** the key is furniture of the
sheet, printed on it, and it arrives exactly the way the
board's rows arrive —

| Joint | Spec (all existing KIN-DRAWER) |
|---|---|
| Sheet travel | `fraction` 1 → 0, **280ms**, `tween(DRAWER_OPEN_MS, EasingSeat)` — `EasingSeat` = `CubicBezier(0.2, 0, 0, 1)` |
| Scrim | No clock of its own: alpha `0.32 × (1 − fraction)`, driven by the same fraction, frame for frame |
| CHECK key | Rides the sheet's `offset` translation. Zero independent motion, zero fade — during the rise it is print in transit |
| Operability | `interactive = visible && settled && !isSealed`: the key takes its first press only at `fraction == 0f`. A rising key that could be pressed would be a control moving under the finger; a risen key that lagged would be furniture arriving late. Neither exists. |

Three corollaries, each a fence against a plausible patch:

1. **Opening PARK is not pressing CHECK.** The host fires the
   sweep on open (`issue-9-plan.md` §1), but `checkPressedMono`
   is set only by the key's own press — so the key rides up
   reading `CHECK`, at rest, undipped, while the open-sweep's
   landings swap the rows around it (§1). Do not "helpfully"
   seat the key in CHECKING on open: the derivation would be
   honest, but the *gesture* would be false — he did not press
   anything. The key shows the verb he called, not every sweep
   the house runs.
2. **The FIX key's entry is the only intra-sheet arrival, and
   CHECK rides it as layout, never as animation.** When FIX
   appears above CHECK, its `AnimatedVisibility` expansion
   pushes CHECK down the column. That displacement is FIX's
   motion acting on the paper; CHECK must not receive a
   compensating offset, a counter-animation, or an
   `animateContentSize` anywhere on the column to "smooth" it.
   Paper pushed by an arriving object moves in the arriving
   object's tempo — which is why the tempo matters:
   **PATCH-READY (M-1, §7):** FIX's entry comes up to
   **280ms SEAT** (audit §1.8; Finisher 2 §3.1) from the built
   160 — a dismissal's clock on an arrival also makes CHECK
   lurch down too fast, once per lack-appearance.
3. **Seal / unseal while open buys no entrance either.** CHECK
   absenting under the seal and returning on unseal are
   recomposition steps (Finisher 2 §8: object, dip, voice, and
   spacers all go, all return) — instant, unanimated, the
   sheet's geometry unmoved. A key that faded back in after
   unsealing would be performing availability it simply has.

---

## §3 · THE CHECK ITSELF — A HELD STILLNESS WITH TWO SMALL MOTIONS

The whole act, as motion, in order:

1. **Press frame.** The word swaps `CHECK` → `CHECKING`
   instantly (same frame as the press — no fade between
   faces), the seat knock sounds on DOWN (Feel 2's seat), and
   the dip travels in: **PATCH-READY (M-2, §7 — Finisher 2 §2's
   animator, adopted verbatim): 1dp over 90ms, `PRESS_MS`,
   `EasingSeat`.** The built offset snaps; a gallery key
   travels, and 90ms is the register's own press clock.
2. **The wait.** After the dip seats (~90ms), **zero pixels
   move** for the remainder of the act, however long the sweep
   takes. The key sits dipped and still — a held press, not an
   activity. Rows swap only when stamps land (§1), each swap
   itself 0ms. There is no loop, no cycle, no periodic element
   anywhere in CHECKING: nothing in it repeats, so nothing in
   it can be mistaken for a cadence. If the sweep never lands,
   the drawer simply stays still, dipped, forever (Finisher 2
   §5.2: no timer may rescue or condemn it) — stillness is the
   honest face of a measurement that has not arrived, and a
   still screen cannot be accused of pretending to measure.
3. **The completion frame.** The frame the last permission
   stamp reaches `checkPressedMono`, four things happen
   together and none is sequenced after another: `checking`
   flips false by derivation; the last row has already swapped
   (§1); LAST PROVED takes the new instant (§1); the NOT
   PROVED line prints if the set landed unwhole (§5). The dip
   then returns over **160ms, `DRAWER_CLOSE_MS`,
   `EasingThrow`** (M-2) — the one motion in the act that is
   *allowed* to outlast its fact, because a release is a
   physical gesture, not a claim. The word re-prints `CHECK`
   at the completion frame, not at the release's end: the face
   must not keep saying CHECKING for 160ms after the set has
   landed just because the key is still travelling home.
4. **The dress promotion is not a motion.** When the board
   ages and CHECK promotes print dress → ink dress (Finisher 2
   §4.1's ladder, on the 5s beat's frame or a landing's frame),
   the fill swaps instantly at the state boundary. Fills do
   not crossfade in this app; a crossfading fill would pass
   through mixtures that are neither dress — the ink-tier lie
   of §1, one seat down.

---

## §4 · DISMISSAL MID-CHECK — THE LEAVE IS 160 THROW, CHECKING OR NOT

**SPEC-ONLY (as built; every clause a fence):**

- **All three dismissal inputs stay live and ungated during
  CHECKING.** Scrim tap, sheet drag past the 40% law, PARK
  re-tap / rail stop (host-owned). The `checking` flag gates
  exactly one thing in the built file — a second press of the
  CHECK key (`interactive && !checking`) — and it must never
  spread to dismissal. A drawer that holds a man until its
  proof finishes is a trap wearing a virtue; PARK's calm
  includes the right to leave a check unfinished.
- **The leave does not change tempo for the unfinished act.**
  Dismissal is `fraction.animateTo(1f, tween(160,
  EasingThrow))` — `EasingThrow` = `CubicBezier(0.5, 0, 1,
  0.4)` — mid-check, mid-rise-of-a-landing, idle: the same 160.
  Not faster (flinging the proof away reads as panic), not
  slower (holding the sheet to let one more stamp land would
  be the drawer spending the man's gesture on its own
  bookkeeping — and any "wait for the set" logic is a timer
  by another name, §0).
- **A parried drag re-seats and the check survives.** Drag is
  1:1 `snapTo` on the fraction (0ms, finger-driven, no easing
  to argue with); release at ≤ 40% re-seats over **280ms
  SEAT** (as built). Nothing in the drag path touches
  `checkPressedMono`, so a sheet that was wobbled mid-check
  settles back onto a still-running derivation — word still
  `CHECKING`, dip still held. The wobble is transport; the
  proof never noticed it.
- **What closing does to the verb (as built, and right):**
  the built `LaunchedEffect(visible)` reset zeroes
  `checkPressedMono` and clears `lastCheckLackName`. The
  *key's claim* dies with the drawer; the *sweep* does not —
  it completes in the store, and its landings are simply
  printed when PARK next opens. Motion consequence for the
  next open: the key rides up at rest reading `CHECK` (§2.1),
  never resurrected into CHECKING by a sweep it didn't start,
  and the abandoned check leaves no animated trace — no
  catch-up flourish, no replay of the landings. They print,
  in §1's step, whenever they are next seen.
- The dipped key exits *with* the sheet — dip and word ride
  the 160 THROW down as-is. No separate key exit, no dip
  release performed for an audience that is already leaving.

---

## §5 · THE NOT PROVED LINE'S ARRIVAL — A PRINT LANDING, 0MS

**SPEC-ONLY (as built; protect it):** the line is a bare
`if (lackName != null && !checking)` block printing a
`ParkBoardRow` under the CHECK key. Its arrival law:

- **Instant, atomic, unadorned — the board's own law (Finisher
  1 F-B6) applied below the key.** No `AnimatedVisibility`, no
  expand, no fade, no slide. It prints in the completion frame
  (§3.3) — *not* after the dip's 160ms release finishes, not
  on any delay. A verdict that waits for the key's gesture to
  finish would be sequencing evidence behind theatre.
- **It arrives in the metronome, so its arrival does not break
  it.** The line is a board row — 48dp + 1dp hairline, the
  same 49dp pitch as the nine above — preceded by its joint
  (built 14dp). The content below it (the Sound sled block)
  therefore shifts down by the row's full height *in one
  frame*, unanimated: no `animateContentSize` on the scroll
  column, no spring on the shift. A print landing moves paper;
  the paper below does not glide out of its way apologising.
  (The scroll column absorbs the shift; if the drawer is
  scrolled to the foot, the line may land below the fold —
  that is print behaviour too, and no auto-scroll may chase
  it. PARK never scrolls itself.)
- **It persists perfectly still.** No pulse to draw the eye,
  no fade-in-late, no auto-expiry — stillness *is* its
  emphasis, on the drawer's calmest surface a new full-ink
  line at 1 AM needs no help being seen. Its clearings are
  the built ones — next press, close, board-reads-whole —
  and every clearing is likewise instant, including the
  press-frame clearing: a new press removes the old verdict
  in the same frame the dip starts, so a stale NOT PROVED
  never stands over a running check.
- Its twin on a whole landing is an absence: no line, no
  motion, only the key's release and (sound's seat) the firm
  click. Success gets no arrival animation because success
  gets no object — the fresh board is the object.

---

## §6 · THE STILLNESS BUDGET — WHAT A PROOF SURFACE MAY SPEND

Priced, so a future patch can be refused by arithmetic rather
than taste. PARK's entire motion vocabulary is the existing
register — **no new duration, no new easing, no new animated
property may be introduced by any CHECK-related patch.** A
proposal that needs one is over budget by definition.

| Budget line | Cap |
|---|---|
| Looping / ambient / periodic motion | **0ms, 0 cycles, forever.** Nothing in PARK repeats. |
| Board rows (all nine + NOT PROVED) | **0ms per change**, every change, forever (§§1, 5) |
| Concurrent animated properties during CHECKING | **1** — the key's dip; and it is at rest (held) for all but its two travels |
| Motion per CHECK act | **250ms total**: 90ms dip-in (SEAT) + 160ms dip-return (THROW), *independent of sweep duration* — a 300ms sweep and a hung sweep spend the same motion |
| Sheet travel per open–close cycle | **440ms total**: 280 SEAT in + 160 THROW out (+ 280 per parried drag, the man's own gesture refunded) |
| FIX key appearances | One 280-in / 160-out pair, only on a real lack-state change (M-1) — never per check, never per landing |
| Word / dress / verdict swaps drawer-wide | **0ms** — every state boundary in the drawer is a step, not a ramp |
| New durations or easings | **0.** The register is `MonolithKin` as it stands: 280 / 160 / 90, `EasingSeat`, `EasingThrow` |

**The freeze-frame test (acceptance instrument for the whole
budget):** freeze PARK at any arbitrary frame outside sheet
travel and the two dip travels. The frozen frame must contain
*every fact the surface knows* — every word, dress, verdict,
and the key's state — with nothing mid-blend, mid-fade, or
half-printed. If a fact exists only across time (a fade's
endpoints, a shimmer's phase), that fact is being carried by
motion, and motion on a proof surface is never allowed to
carry facts. Transport (the sheet) and touch confirmation
(the dip) are the only two things motion may do here; both
are spent above, in full.

---

## §7 · PATCH-READY INDEX

**M-1 · FIX entry tempo — exact edit** (audit §1.8, Finisher 2
§3.1; motion owns it here because FIX's expansion is the one
motion that displaces CHECK). In the FIX key's
`AnimatedVisibility` in `ParkDrawer.kt`:

```kotlin
enter = expandVertically(
            tween(MonolithKin.DRAWER_OPEN_MS, easing = MonolithKin.EasingSeat)) +
        fadeIn(tween(MonolithKin.DRAWER_OPEN_MS)),
```

Exit stays exactly as built (`DRAWER_CLOSE_MS` +
`EasingThrow` on both halves). Arrivals land at drawer tempo;
departures are thrown.

**M-2 · Dip animators, both keys — exact** (Finisher 2 §2,
adopted verbatim; the motion seat confirms the constants
against the register): import
`androidx.compose.animation.core.animateDpAsState`; one
animator per key beside the existing press states — target
`1.dp` / `0.dp`; spec `tween(if (pressed) MonolithKin.PRESS_MS
else MonolithKin.DRAWER_CLOSE_MS, easing = if (pressed)
MonolithKin.EasingSeat else MonolithKin.EasingThrow)`;
labels `"park_fix_dip"` / `"park_check_dip"`. Replace each
key's `.offset(y = if (…) 1.dp else 0.dp)` with
`.offset(y = fixDip)` / `.offset(y = checkDip)`. Press-in
**90ms SEAT**; release **160ms THROW** — no third duration is
introduced, and the CHECK word still re-prints at the state
frame, never at the animator's end (§3.3).

**M-3 · Negative patches — the fences, stated as diffs that
must never appear.** Any of these in a future diff is a
budget breach on sight, Hof to refuse without re-argument:
`AnimatedContent`, `animateColorAsState`, `animateContentSize`,
`Crossfade`, or any `animate*AsState` **on a board row, the
NOT PROVED block, or the CHECK block**; an `AnimatedVisibility`
wrapper around the CHECK key or the NOT PROVED line; a
`checking` condition added to the scrim's clickable gate, the
grip's drag detector, or the host's dismissal wiring; a
`delay()` before the NOT PROVED print or before LAST PROVED's
swap; any animation parameter added to `ParkBoardRow` /
`ParkSocialSitesRow`.

Everything else in this document is SPEC-ONLY law around code
that already honours it. No DB change, no new permission, no
new colour, no new curve, no new duration.

---

## §8 · ACCEPTANCE, BY EYE

1. **Landings.** Press CHECK on a stale board: rows swap one
   by one, in landing order, each a single-frame word+ink
   step; unlanded rows keep their exact pre-press print
   throughout; at no frame do two words share a cell, and at
   no frame is any row blank, dimmed-in-progress, or glowing.
2. **Entrance.** Open PARK while a sweep is in flight: the
   sheet rises 280 SEAT with the key printed on it from the
   first frame, reading `CHECK`, undipped; landings swap
   mid-rise; the key takes no press until the sheet seats.
3. **The held wait.** Press CHECK and stall a query: after
   ~90ms the screen is perfectly still — dipped key, static
   board — at 1 second, at 10 seconds. Nothing pulses,
   breathes, or cycles. Unstall: the remaining rows step in,
   the word re-prints `CHECK` in the completion frame while
   the dip is still travelling home over its 160.
4. **Dismissal mid-check.** Press CHECK, immediately tap the
   scrim: the sheet leaves at the same 160 THROW as an idle
   dismissal — no hesitation, no speed-up. Reopen: rows print
   the sweep's landings, key at rest, no CHECKING resurrected,
   no NOT PROVED from the abandoned act. Drag the sheet a
   third down mid-check and release: it re-seats at 280 and
   the key still reads `CHECKING`, dip held.
5. **The verdict line.** Complete a check with a lack: NOT
   PROVED prints in the completion frame, in board pitch,
   and the Sound block steps down one frame with it — no
   glide, no fade, no scroll chasing it. It then does not
   move, blink, or expire for as long as the drawer stands.
6. **Freeze-frame.** Pause any recording of a check mid-act:
   every frame outside the dip travels reads as a complete,
   legible proof board. Any frame that doesn't is the bug.

— Feel 3 (motion), issue-9 cell (for Hof)
