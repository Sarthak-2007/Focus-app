# TEMPLE INVENTIONS — implementation-ready specs (T-1, T-2, T-3), three new Temple-only mechanisms, and the blocking-start invention

*Funke (spark), Innovator (the Wild), Stein's Temple crew. Filed
Oct 29, 2026, ~22:26 IST. Sources read before writing:
plan/DEEPEN-temple.md (the elevation: lintel / stele / courses /
frieze / threshold steps / colonnade), plan/REFINE-zunder.md
addendum (T-1, T-2, T-3 as invented), plan/FLOW-REGISTER.md §1–§2
(the seal's truth and the designed blocking-start flow),
plan/REFINE-haut.md §0+§5 (the feel constitution), and trunk
(~/workspace/clone-inspect/Focus-app) — every data source below is
named to a file a builder can open. Paper only: NO RENDER, NO
CURE claimed anywhere in this file; I do not compile.*

**Laws binding every line below:** no temple bell and no ambient
sound bed, ever (fraud per Haut's constitution — Temple's
resonance is MASS, never duration; the Throw stays bit-identical
and Temple claims nothing at the seal's voice, only its light);
never green (every token named here passes G < max(R,B) by the
lane's existing census — no new colour is introduced in this
file); fonts Sora / Atkinson Hyperlegible / Anonymous Pro only;
blocker, not tracker — **no invention in this file prints a
score, a streak, a total, or a count of anything** (T-1's wall
*is* height, never a number); no DB change (every derivation is
a read over existing rows and flows); DB v11 compatibility
untouched.

---

## §0 · The one shared derivation — `templeDayCourses()` (builder reads this first)

T-1 and T-3 both stand on one count: how many blocks of today
have been *laid*, and how many the day still plans. Implement it
ONCE, as a pure derivation in the Deck's view layer, and let
both inventions read it. Sources, all in trunk:

- **Rows:** `MonolithViewModel.sessionHistory`
  (`ui/MonolithViewModel.kt:354`, from
  `MonolithRepository.sessionHistoryFlow` →
  `MonolithDao.getSessionHistoryFlow()`,
  `data/MonolithDao.kt:63–64`) plus
  `MonolithViewModel.activeSession` (`:351`).
- **The day boundary:** `data/DrawerDay.kt` —
  `DrawerDay.filingDate(instantMillis)` (the filing day starts
  03:30 local, offset 210 min). A session belongs to today iff
  `filingDate(row.endWallMillis)` == today's filing date; for a
  row with `endWallMillis == 0` (never stamped — pre-v11 or
  not ended) fall back to `filingDate(row.startWallTimeMillis)`.
  `endWallMillis` is the true end stamped once at the ending
  write (`data/MonolithModels.kt`, FocusSessionEntity, Truth
  Car 2 RC-01) — never use discovery time.
- **What counts as a FINISH:** `finishKindOf(row, isSleepLoadout,
  now)` in `data/ReceiptState.kt` — inactive ∧ outcome ∈
  {ELAPSED_CLEAN, ELAPSED_SHIELD_GAP} (manual), STOPWATCH_ENDED ∧
  completedSuccessfully (flow), routineOutcome ∈ {ATTENDED,
  PARTIAL} (routine). The outcome vocabulary is
  `SessionOutcome` in the same file. **Unfinalised routine rows
  (routineId != null ∧ routineOutcome == null) count ZERO** —
  the exact exclusion `MonolithViewModel.kt:401–408` already
  applies in `totalFocusedMinutes`; copy that filter, do not
  invent a second one.
- **Blocks laid per finished row:**
  - ELAPSED_CLEAN (manual or routine ATTENDED):
    `row.totalStudyBlocks`.
  - ELAPSED_SHIELD_GAP: `row.totalStudyBlocks`, flagged
    `gap = true` for that row's courses (the span elapsed; the
    shield did not witness it — §1.4).
  - STOPWATCH_ENDED: **1** (a flow session is one standing,
    however long it ran).
  - Routine PARTIAL: `max(0, row.currentBlockIndex - 1)` — the
    blocks that fully stood before the window closed; the
    interrupted block lays nothing.
  - VALVE_BROKE, ENDED_EARLY, GRACE_CANCEL, MISSED, LATE_FIRE:
    **0**. A span that did not finish lays no stone.
- **Blocks laid by the RUNNING session:** `activeSession != null`
  → `activeSession.currentBlockIndex - 1` fully laid, and the
  active block itself is the slab being laid (§1.3). Both fields
  are on the row (`data/MonolithModels.kt`).
- **The day's plan (beds):** `MonolithViewModel.studyBlocksCount`
  (`:157`, StateFlow<Int>, clamped 1..8 at `:1288`) — the hub
  count. While a session runs, the plan for the *remaining* beds
  is the running row's own `totalStudyBlocks`, not the hub's
  (he may have re-set the hub mid-span; the span's own row is
  the truth of that span).

Output: `laidToday: List<CourseFact(minutes, gap)>` (in
start-time order), `runningIndex: Int?`, `plannedRemaining: Int`.
No other invention in this file re-derives any of it.

---

## §1 · T-1 — COURSES OF THE DAY (Zunder, REFINE-zunder addendum)

*The day is built, course by course, in a plinth beneath the
stele. Zunder's invention, specced to build here.*

### 1.1 Where it lives (geometry)

- **Site:** the plinth band of the Deck elevation
  (DEEPEN-temple §2): directly under the stele's base moulding,
  above the frieze course and the threshold steps. It replaces
  nothing in the deepening — the deepening's *courses of fact*
  (block state / apps / end time / witness) are inscriptions ON
  the stele's face; T-1's courses are masonry UNDER it. Two
  different uses of the word, two different bands; a builder
  must not merge them.
- **Width:** the full stele width, edges flush with the slab
  above (the stack is the slab's own footprint, growing down).
- **One course:** height `h = clamp(6dp, minutes × 0.18dp,
  14dp)` (Zunder's scale, kept verbatim). `minutes` per course =
  the block's own stood minutes: for a finished row,
  `row.effectiveTargetSeconds / 60 / totalStudyBlocks` — i.e.
  the per-block effective target (`effectiveTargetSeconds =
  targetDurationSeconds + penaltySecondsAdded`,
  `MonolithModels.kt` derived properties — a penalty-extended
  block stood longer and is a taller stone; that is the truth
  of the span, not a judgement of it). For a flow course:
  `row.elapsedSeconds / 60`. For a running session's laid
  blocks: the active row's per-block effective target, same
  formula.
- **Joints:** courses separated by a **1dp mortar hairline** in
  the lane's recess tone. Vertical joint ticks stagger by
  course parity (even courses: ticks at ⅓ and ⅔ of width; odd:
  at ½) — drawn as DEEPEN §3 chisel pairs (1dp dark edge on the
  shadow side, 1dp light edge on the lit side), so the stack
  reads in daylight glare by shape, not tone.
- **Cap:** at most **8 individual courses** drawn (the hub's own
  maximum). If `laidToday` exceeds 8 (multiple spans in a day),
  the oldest courses collapse into ONE undifferentiated
  **foundation band, 10dp**, at the stack's foot — no joint
  ticks on it. Nothing prints, so collapsing loses no printed
  fact; the wall simply stands on its foundation.
- **Empty beds:** a bed is a course-height outline where a
  planned block has not been laid: **1dp brass outline** (the
  lane's brass, outline only — bronze/brass waits, gold never
  waits), interior in the recess tone, height = the height the
  block *would* lay at the current composition
  (`dialMinutes` × 0.18dp, clamped — `MonolithViewModel
  .dialMinutes`, `:154`). Beds stack ABOVE the last laid course.
  Bed count: idle → `max(0, studyBlocksCount − laidToday.size)`;
  running → `activeSession.totalStudyBlocks −
  activeSession.currentBlockIndex` (beds above the slab, §1.3).
  If he re-sets the hub mid-day, the beds redraw — Zunder's
  builder re-measuring his wall; honest by construction, and
  stated here so no builder adds a latch to "protect" it.

### 1.2 When it changes (hooks)

- **Composition / every `sessionHistory` emission:** the stack
  is a pure function of §0. No state, no animation on ordinary
  recomposition.
- **The settle (the one motion):** when `laidToday.size`
  INCREASES across an emission — i.e. at a finish, witnessed by
  the VM's finish controller (`MonolithViewModel.kt:990ff`,
  which already collects `sessionHistory` as the finish
  witness) — the newly laid course settles: it starts offset
  +6dp upward with its mortar line absent and travels to rest
  over **520ms on MonolithCurves.CurveC**
  (`ui/theme/MonolithCurves.kt` — CurveC + DURATION_520 are the
  lane's existing vocabulary; Temple is the slowest house).
  Once. No loop, no overshoot (CurveC has none in this use),
  no sound of its own — the finish's own voices (the tear, the
  completion pulse per `finishPulses()` in ReceiptState.kt)
  are the finish's; T-1 adds none.
- **Mid-span block boundary:** when the running session's
  `currentBlockIndex` advances (the flip into a Safe Break —
  derived on the row, `MonolithModels.kt:391–398` region), the
  block just finished settles by the same 520ms CurveC travel
  beneath the slab. The break itself renders nowhere on the
  stack (Nacht's law, carried by FLOW-REGISTER §1.3: the span
  counts, the break earns no course).

### 1.3 The current slab

While a session runs, the hero slab IS the course being laid
(Zunder). Presentation consequence, specced so a builder does
not double-draw it: the running block does NOT also appear as
a course in the stack; the stack holds only
`currentBlockIndex − 1` of the running span's courses, and the
slab's base moulding sits directly on the topmost of them (or
on the plinth line if none). At the finish, §1.2's settle is
the slab's course joining the stack as the detention/receipt
takes the screen.

### 1.4 On a BROKEN finish — and the other non-finishes

- **ELAPSED_SHIELD_GAP (the BROKEN booking):** the span's
  courses lay in full (the time stood; §0), and the session is
  marked in the wall's own grammar, exactly as
  PRESENTATION-temple §8 (the Confession) already rules the
  Deck: the mortar line ABOVE that session's last course is
  drawn with a **6dp gap at its centre**, and that course's
  right end carries a **4dp filled vermilion square** (lane
  token #D63E06 — the wound pigment, its one lawful home
  besides true alerts; filled vermilion is spent on wounds,
  never warnings). No course is removed, no course is shortened:
  the wall does not lie about the day by unbuilding it. The
  receipt for the same finish already carries the struck-
  through seal (§8) and prints `ENDED · SHIELD GAP`
  (`MonolithRepository.kt:1625`) / `shieldWasOff`
  (`:1582`) — T-1 reads the same `row.outcome`, so wall and
  slip can never disagree.
- **VALVE_BROKE / ENDED_EARLY / GRACE_CANCEL:** no course lays
  (§0). The half-laid slab simply is not there on the idle Deck
  that follows — a stone that was never set leaves no hole in
  a wall; the beds above the stack remain the day's plan.
- **Routine MISSED / never fired:** no course, and T-1 prints
  nothing about it — the Confession's Deck line (§8) is that
  fact's surface, when the no-fire stamps exist; T-1 does not
  duplicate it in masonry.

### 1.5 Price

**Small-medium** (Zunder's price, confirmed against trunk): one
Canvas stack (~50 lines) + the §0 derivation (~30 lines) in the
Deck's plinth band. No new state, no new data, no DB, no new
sound, no new token (recess, brass outline, vermilion already
in the lane). Fremd test: strip the colour and the names — a
stepped stack of bands of different heights growing under a
slab through a day reads as masonry accumulating, and as
nothing in any other look.

---

## §2 · T-2 — THE LAMP BURNS DOWN (Zunder, REFINE-zunder addendum)

*Night remaining, read as oil. The lamp is Temple's timepiece.*

### 2.1 The fraction (exact source — read this before drawing)

- **Use the SPAN fraction, never the block fraction:**
  `fraction = activeSession.spanRemainingSeconds.toFloat() /
  activeSession.spanTargetSeconds` (both derived on
  FocusSessionEntity, `data/MonolithModels.kt` —
  `spanTargetSeconds = totalStudyBlocks ×
  effectiveTargetSeconds + (totalStudyBlocks − 1) × 600`;
  `spanRemainingSeconds` from `spanElapsedSeconds`, which
  prices Safe Breaks into the span). Reason, stated for the
  builder: the per-block `remainingSeconds` reads 0 for the
  whole of a break and refills at the next block — a lamp fed
  on it would flare back to full at every block boundary,
  which is a lie in light. The span fraction is monotonic
  across the whole session; a lamp can only sink. This is the
  Now Frame law (one derivation, every surface) applied to a
  gradient.
- **Stopwatch sessions:** `spanTargetSeconds` still computes
  from the composed target, but a flow session's true end is
  his hand, not the target — the fraction is meaningless.
  Rule: stopwatch (`row.isStopwatchMode`) → the lamp holds
  FULL for the session's life (a tended lamp, not a measured
  one). Do not clamp a fake fraction.
- **No active session:** T-2 is inert. The night Deck's glow
  sits at REST (radius 0.55, alpha 0.72, centre sunk — §2.2's
  end state): the lamp at rest in its niche, so the kindling
  at a seal (§5) has somewhere to rise FROM.

### 2.2 Geometry of the burn (night hero only)

The brush is the lane's EXISTING night-hero radial lamplight
glow (Temple lane, night branch of the hero draw in
TactileDeckScreen / BlockitCoreViews — the builder reads the
current centre/radius/alpha constants there and parameterises
them; this file invents no hex and no second glow).

- **Centre:** x = hero slab centre-x, fixed. y starts at **18%
  of slab height from the slab's top edge** (fraction 1.0) and
  sinks **+18dp** by fraction 0.0 (Zunder's travel, kept).
- **Radius:** `radius = lerp(0.55, 1.0, fraction) × heroDiagonal`
  where heroDiagonal = the slab's own diagonal (Zunder: 1.0 of
  the hero's diagonal at full).
- **Alpha:** `alpha = lerp(0.72, 1.0, fraction)` (Zunder's
  settle, kept).
- **Stepping:** the parameters are quantised to the **minute
  bucket** — recompute only when
  `floor(spanRemainingSeconds / 60)` changes. The VM's existing
  per-second ticker (`MonolithViewModel.kt:820ff`) and the
  Deck's `nowFrame` (`TactileDeckScreen.kt:349ff`) both already
  tick; the bucket is a `remember`/derivedState keyed on the
  minute, NOT a new timer. **No continuous animation, no
  interpolation between steps** — the lamp moves the way lamps
  appear to move, between glances (the stillness law). Between
  steps the brush is byte-still.
- **Day:** T-2 draws NOTHING. Day Temple is sunlit stone
  (DEEPEN §3); there is no glow brush on the day hero and this
  invention does not add one. The fraction is spent nowhere by
  day — refused by name, so no builder "helpfully" dims the
  day slab.

### 2.3 Hooks

- **Seal (t=0):** the lamp fills at the seal, not at the slide.
  The hook is the seal's publish step inside
  `SessionSealTransition.executeSeal` (claim → seal row →
  publish → Throw, `service/SessionSealTransition.kt`) as
  surfaced by the Deck's sealed state: on the first composition
  where `activeSession != null` ∧ fraction-derived, the glow
  stands at fraction ≈ 1.0. **Annulment** (jam at latch+1500ms,
  row deleted — FLOW-REGISTER §1.1): no session, no fill; the
  glow never left REST. A lamp that lit for a seal that died
  would be a false witness.
- **The lamp-rake at t=0** (PRESENTATION-temple §1.3 — the
  450ms warm rake across the hero's hairlines) is a separate,
  already-adopted event and stays separate: the rake passes
  once; T-2's glow is what remains after it passes. Do not
  merge them into one animation.
- **Finish — the ember ring:** at the finish edge (the VM
  already hides the desk clock there,
  `MonolithViewModel.kt:1051`; the receipt publishes through
  the finish controller, `:990ff`), when the Deck returns
  under the receipt, the glow collapses from its sunk state to
  an **ember ring behind the receipt's parchment**: a 1dp
  stroked rounded-rect outline (the receipt slip's own bounds,
  inset −6dp — the ring stands 6dp OUTSIDE the slip on every
  side), brass-gold stroke (the lane's gold), alpha 0.5, over
  true-dark bronze ground. It stands exactly as long as the
  receipt stands (the shipped F-B1 law: the receipt stands
  until dismissed), then dies to REST with the dismissal. No
  pulse, no flicker — an ember ring does not perform.
- **BROKEN finish (ELAPSED_SHIELD_GAP): NO ember ring.** The
  receipt's seal is struck through for this finish (§8);
  a warm ring behind a refused authentication would contradict
  it in light. Instead the glow drops straight to REST
  (0.55 / 0.72 / sunk) at the finish edge, and the Deck carries
  the Confession's broken rule. `finishPulses()` already
  returns false for this outcome — Temple's light follows the
  same law as its sound: a gap finish gets neither pulse nor
  ring.
- **VALVE_BROKE / disarm / emergency-break completion:** no
  receipt exists (ReceiptState: VALVE_BROKE — no receipt, no
  №, ever), so no ring; the glow returns to REST at the disarm
  edges the VM already writes (`MonolithViewModel.kt:2075`,
  `:2102`).

### 2.4 Price

**Small** (Zunder's price, confirmed): three parameterised
constants on an existing brush + the minute-bucket key + the
ember-ring stroke on the receipt state. Zero new state, zero
new data. Fremd test: a light that sinks and tightens as a
session runs out is a lamp spending oil — Dawn's light only
ever *grows*, Obsidian spends light as one numeral's scarcity;
neither can run out of a room's light on schedule.

---

## §3 · T-3 — THE KEYSTONE (Zunder, REFINE-zunder addendum)

*The last block carries the arch. The day's last commit closes
the building.*

### 3.1 The condition (a derivation, never a latch)

From §0, with `remaining = plannedRemaining` for the CURRENT
composition and `laid = laidToday.size`:

- **Idle form:** keystone PENDING iff `studyBlocksCount ≥ 1 ∧
  laid == studyBlocksCount − 1` — exactly one bed remains. (A
  one-block day qualifies from its first composition: an arch
  of one stone is still closed by that stone. Stated so a
  builder does not special-case `≥ 2`.)
- **Running form:** keystone PENDING iff the active session is
  on its final block — `activeSession.currentBlockIndex ==
  activeSession.totalStudyBlocks` — AND sealing this span
  completes the day's plan under the CURRENT hub count
  (`laid-before-this-span + totalStudyBlocks ==
  studyBlocksCount`, where laid-before-this-span is §0's
  finished-rows count alone). If he re-set the hub upward
  mid-day, the running form goes false and the stone waits for
  the true last block.
- **Never latched:** the keystone's drawn state is recomputed
  from §0 on every emission. Re-set the hub 2 → 4 after one
  course laid and the pending keystone unseats back to a plain
  bed — the building was re-measured; the stone follows the
  measure. The ONLY latched event in T-3 is the gold fill at a
  seal (§3.3), and even that reverts on a non-finish ending.

### 3.2 Geometry (Zunder's figures, kept verbatim)

- **The stone:** a trapezoid seated CENTRE in the slab's top
  rule line — **64dp wide at its top edge, 44dp at its base,
  26dp tall**, its base ON the inner double rule's line, its
  body rising above it. Path: four points, no curves (a cut
  stone has no curves).
- **Waiting state:** **bronze outline, 1.5dp stroke**, interior
  = the slab's own tone (the stone is of the slab until it is
  committed). In the IDLE form it seats in the topmost empty
  bed's place — drawn at the bed's position above the stack,
  same trapezoid, same outline (the waiting stone shown where
  it will sit). In the RUNNING form it seats on the hero slab
  itself (the slab is the final course, §1.3).
- **The rule:** while pending, the stele's inner double rule
  is INTERRUPTED at the keystone's base — the 44dp span under
  the stone is not drawn. The arch is visibly open.

### 3.3 The seal hook — gold at t=0

- At the seal of the block that satisfies §3.1's running form
  — the same `executeSeal` publish hook as §2.3, t=0, the
  Throw bit-identical and Temple silent at it — the keystone
  **fills with gold leaf** (lane token #DFAF3F,
  PRESENTATION-temple §1.2: leaf gold, the commit metal —
  lawful here and only here, because gold is this world's
  commit metal and this is the day's last commit). The fill is
  a state change at t=0, not an animation: the stone is bronze
  before the Throw and gold after it. (The §1.3 lamp-rake may
  pass across it in the same 450ms; that is the rake's travel,
  not the fill's.)
- The fill PERSISTS for the final session's whole life.
- **At the finish (ELAPSED_CLEAN / ATTENDED / FLOW_FINISH):**
  the inner double rule **completes its run across the
  keystone's base** — the interrupted 44dp segment is drawn,
  1dp, slab ink — at the finish-publish (same hook family as
  §1.2's settle; the rule's completion and the final course's
  settle are one 520ms CurveC moment, the rule segment fading
  in as the course seats). The gold keystone then stands on
  the idle Deck for the REST of the filing day: the day ends
  with a building standing complete (§3.5).
- **Re-set after completion:** if he composes a NEW plan later
  the same day (hub raised, a new span armed), §0 recomputes:
  a new last block exists, so a NEW pending keystone appears
  at the new plan's end and the completed one remains as a
  laid course's cap — drawn gold, rule complete, as history.
  (Implementation: the completed keystone is a property of the
  finished span's last course in the stack, not of the slab.)

### 3.4 On a BROKEN finish — and the other endings of the final block

- **ELAPSED_SHIELD_GAP:** the keystone **stays bronze outline**
  (any gold fill from its seal is WITHDRAWN at the finish
  publish — the machine could not witness the arch closing,
  so it does not gild it) and takes the struck-through
  treatment PRESENTATION-temple §8 already rules the receipt:
  **one brass hairline struck diagonally across the stone's
  face** (corner to corner of the trapezoid's bounding box,
  1dp). The rule does NOT complete — the 44dp gap stands. The
  course still lays (§1.4) with its broken mortar line and
  vermilion square; wall, stone, and slip tell one story.
- **VALVE_BROKE / ENDED_EARLY during the final block:** the
  gold fill reverts to bronze outline at the disarm edge (the
  commit was unmade by the only hand allowed to unmake it);
  the stone returns to PENDING if §3.1 still holds, else to a
  plain bed. No strike, no vermilion — a stone unseated by
  its builder is not a wound.
- **GRACE_CANCEL of the final block's arm (annulment):** the
  seal never landed; the fill never happened (§2.3's law);
  nothing to revert.

### 3.5 Price

**Small** (Zunder): one trapezoid path + outline/fill/rule-gap
conditionals on §0 + the finish conditional. If T-1 is refused
at judging, T-3 prices alone at **small-medium** (it then
carries §0's derivation by itself — Zunder's own pricing, kept).

**The moment it owns (Zunder's, endorsed):** 19:30, the last
finish — courses below, lamp low (T-2 at fraction ≈ 0), one
stone closing the arch in gold. The day does not end with a
summary. It ends with a building standing complete, in
lamplight, that was not standing at 03:30.

---

## §4 · NEW — three mechanisms that could live ONLY in Temple

*Each was put through the sharpened move test: if it could live
in another look — including THE MACHINE — it was refused. The
refusals I made against myself are filed at §4.4.*

### N-1 · THE CRACKED SEAL — the emergency valve breaks a stone, and the stone stays broken all day

**The mechanism.** Temple's authentication object is a seal
STONE (DEEPEN §11: square because seal stones are square,
outline because unstruck). The emergency valve — the week's
one 60-second continuous hold — is, in this world, the
deliberate breaking of that stone:

- **During the hold:** his thumb holds on the vermilion
  *outline* seal (the valve surface's existing seal mark).
  The hold's progress — the valve's own existing fraction —
  is drawn as **crack propagation across the seal stone**:
  four pre-drawn crack stages, switched at the hold's
  quarters (15s / 30s / 45s), each stage adding one polyline
  branch (1dp, slab ink, jagged 3–5 segments, no curves).
  Stage timing carries the meaning (Haut's law: shape by
  time); there is no percentage, no number, no filling bar.
  At 60s the final crack crosses the stone corner-to-corner
  and the valve fires — the stone is broken, the session
  breaks with it (VALVE_BROKE, its existing booking).
- **After a spend:** for the REST of that filing day, the
  Deck's witness course (DEEPEN §2 — the lowest course,
  HELD / NOT HELD) carries the broken stone beside it: the
  seal drawn at its course size, **cracked at stage 4,
  outline only, unrepaired**. Source: the append-only spend
  ledger — `EmergencyBreakSpendEntity` rows
  (`data/MonolithModels.kt`; DAO
  `getSpendsForWeek(anchor)` / `observeSpendsForWeek`,
  `data/MonolithDao.kt:124–128`) filtered to
  `DrawerDay.filingDate(spentAtWallMillis) == today`. At the
  03:30 boundary the stone is gone — a new day is a new
  witness course; the ledger row itself is never touched.
  No count, no "1 used" text: a cracked stone, present or
  absent, is the whole statement.
- **Release before 60s:** cracks heal to the stage matching
  the released hold's retreat — i.e. the drawing follows the
  existing hold fraction back down and is gone at zero. A
  stone only stays broken if it WAS broken; anything less
  would be the wall lying about an act that did not complete.
- **Machinery: SMALL.** Four static crack paths + a stage
  switch on the valve's existing hold fraction (view), plus
  one read of an existing Flow for the day-persistent mark.
  No new state, no DB, no sound (the valve's existing voices
  stand; a "cracking sound" through the queue would be a
  duration fraud — refused by name).
- **Move test:** Noir breaks a *letter of a sign* for a
  shield-gap finish (N-2 there — damage to a message, booked
  by the machine). Signal tears paper; Obsidian cools an
  ember. None of them has a seal that is a physical stone the
  user himself must break with his own thumb to get out, nor
  an authentication grammar (outline = unstruck, struck =
  refused) for the broken stone to persist inside. Move the
  crack to any other look and it becomes decoration; here it
  is the valve's own physics and the witness course's memory.
  **Survives.**

### N-2 · THE PLUMB LINE — the building tells him it is out of true, by hanging crooked

**The mechanism.** A mason checks a wall with a plumb bob:
a weight on a line, gravity as the one incorruptible witness.
Temple hangs one from the lintel (DEEPEN §2 — the beam carries
the Guard lack in words already; the plumb is its physical
twin, readable without reading):

- **The object:** from the lintel's centre, a **1dp hairline**
  drops the stele's full height, ending in a small bronze
  **bob: a 10dp-tall, 6dp-wide pointed cone** (two straight
  edges, one point — drawn, never rendered). At the stele's
  base course, a **centre mark**: a 12dp vertical chisel tick
  (DEEPEN §3 chisel pair) cut into the base.
- **True:** when the Guard bind is HELD and proof is positive
  (the same witness state DEEPEN §2's witness course prints),
  the line hangs exactly over the centre mark. The building
  is in plumb; nothing is said.
- **Out of true:** when any Guard lack exists (the lintel's
  own spelled lack — accessibility missing, shield unbound,
  the warrant unlanded), the line's foot rests **9dp to the
  right of the centre mark** — a fixed, binary offset, never
  proportional (there is no "how broken" scale; a wall is in
  plumb or it is not). The bob drawn at the offset, the line
  drawn as the straight diagonal gravity would actually make
  it — from lintel centre to the displaced bob, the line
  itself stays vertical and the STELE's centre mark is what
  it misses: implementation-honest version, specced plainly —
  the line hangs vertical from a lintel anchor displaced 9dp
  right; a temple whose beam is displaced hangs its plumb
  off its own foundation mark. One anchor offset, one line,
  one bob: three draw parameters.
- **At a seal:** during the proving cascade the bob is drawn
  at the offset IF a lack exists (the cascade is proving
  exactly this); at t=0 publish with proof positive, the
  anchor steps to centre in the same 520ms CurveC settle as
  §1.2 — the building comes into plumb as it is barred.
  Annulment: it stays where it was. The plumb never swings
  (no pendulum animation — a swinging bob is a clock, and
  clocks are other rooms' furniture; refused by name).
- **Machinery: SMALL.** One line, one cone path, one anchor
  offset conditional on the existing Watch Store / guard
  verdict the lintel already reads (DEEPEN §2's spelled lack
  and the witness course read the same state — the builder
  takes the plumb's boolean from that same derivation, never
  a second source). No new state.
- **Move test:** Signal's registration (S-1) aligns INK PLATES
  as proof lands — print-shop truth, horizontal, in the type.
  THE MACHINE could hang a level — a bubble in a vial, an
  instrument read by eye. A plumb line is neither: it is not
  an instrument ON the building, it is the building measured
  against gravity, and its failure mode (the beam displaced
  from the foundation mark) is a masonry failure no poster,
  sign, sky, glass, or drawing can suffer. Dawn has no wall;
  Noir's signs hang crooked for style, which is exactly why
  crookedness can mean nothing there. **Survives.**

### N-3 · THE MORTAR CURE — a freshly laid course is wet, and dries in the wall

**The mechanism.** Lime mortar is dark when wet and pales as
it cures. T-1's newest course carries that fact:

- For **30 minutes of wall time after a finish** (the row's
  `endWallMillis`, §0), the newest laid course's mortar
  hairline (§1.1) draws in the DARK recess step (the lane's
  deep recess tone — wet) and the course's own tone sits one
  step darker. At 30 minutes it steps — once, on the minute
  bucket (§2.2's cadence) — to the dry hairline and tone of
  every older course. No gradient, no fade: mortar does not
  fade, it is wet and then it is cured; the step IS the
  material's behaviour, and it keeps the stillness law.
- Only the newest course can be wet; a second finish within
  30 minutes moves the wet state to the new course (the older
  one is cured by the act of laying on it — a mason lays the
  next course on a joint that has taken its set).
- **Machinery: NONE beyond T-1.** One tone conditional keyed
  on `now − endWallMillis < 30min` for the last course in
  §0's list, read on the existing ticker. No state, no data.
- **Move test — argued against myself, hard:** Obsidian's
  O-2 cools an ember edge on a RECEIPT (heat leaving a struck
  slip; forge grammar; the record cools). This is not that:
  nothing here is hot, nothing is a record, and the change is
  dark→pale drying in a JOINT, not bright→dim cooling on an
  edge. Dawn's D-2 clears frost off glass (a surface becoming
  transparent). Mortar curing is the one material verb in the
  wave that belongs to a wall being built — it exists only
  because T-1 lays courses with mortar joints, an organ no
  other look possesses. **Survives — but detachable:** if
  judging rules it a cousin of O-2, it dies alone and T-1
  stands untouched; it is priced and specced so its removal
  is deleting one conditional.

### 4.4 Refused by my own hand (the move test's casualties)

- **A sundial / moving sun-patch on the day slab** — refused
  (Zunder refused it first): the sky is Dawn's verb; Temple's
  light is a lamp — tended, carried, finite.
- **A revision mark for edited routines** — Blueprint's B-2
  owns revision (a drawing is re-issued); a carving is re-cut
  or it stands. Temple prints its routine edits on the
  Shield's plaques (DEEPEN §4) and adds no mark. Refused.
- **An hour-chime / any bell, any bed** — fraud (Haut §0/§5),
  refused before it was an idea.
- **Wear on the threshold step** (a hollow worn by crossings)
  — that is a cumulative count wearing stone's clothes: a
  tracker in disguise. Refused on the blocker law, not the
  move test.

---

## §5 · THE BLOCKING-START INVENTION — THE LAMP IS CARRIED IN

*One invention for the moment blocking starts, built strictly
inside FLOW-REGISTER §2's beats — every beat Ablauf designed
stands; Temple changes only WHAT THE TRANSITION IS in this
world.*

### 5.1 The beats, Temple's reading

1. **Second 0 — the bolt.** Slide or tap commits on the
   threshold course (DEEPEN §2: the gate bolt in the top
   step). The proving cascade runs; Temple adds nothing.
   The Deck is lamplit at REST (T-2, §2.1) — a lamp in its
   niche, unlit for a session.
2. **t=0 — the seal.** `executeSeal` publishes; the Throw
   sounds, bit-identical, Temple silent. Temple's light: the
   lamp-rake passes (PRESENTATION §1.3, 450ms), and behind it
   T-2's glow stands FULL (fraction 1.0 — the lamp is filled
   and lit at the seal, §2.3). The plumb steps into true
   (N-2) on the same settle if proof is positive.
3. **t≈1s — the shot lands.** The running face stands and is
   READ: eyebrow, block countdown, `n APPS HELD`, the task
   line. Nothing is taken from him yet (FLOW §2.1's law).
   The lamp burns full over the stele.
4. **t≈20s untouched — THE CARRYING (the invention).** FLOW
   §2.1 raises the desk clock on the settle (20s no-touch
   after the seal or last interaction) with the overlay's
   existing specB fade, 420ms. In Temple that fade is a
   **carrying of the lamp from the stele to the niche**:
   - **Phase one (0–210ms, CurveC):** the Deck's glow
     contracts — T-2's radius interpolates from its session
     value toward 0.30 × heroDiagonal and its centre sinks
     the full +18dp and down toward the threshold; the stele,
     courses, and chrome dim to true black behind it. The
     building empties; only the lamp is still lit.
   - **Phase two (210–420ms, specB as shipped):** the desk
     clock fades in — but its face is the **lamp niche**
     (DEEPEN §5): the numerals rise inside a low radial glow
     whose parameters are T-2's OWN at the current fraction
     (centre low on the face, radius 0.55–1.0 law, alpha law)
     — the same lamp, now in the niche, still spending the
     same oil. The session did not change rooms' physics;
     the lamp was carried, and it arrives with exactly the
     fuel it had on the stele.
   - Total elapsed: the same 420ms FLOW prices. No new
     duration enters the vocabulary.
5. **The session's middle.** Nothing changes (FLOW §2.1,
   hour three). On the niche face the glow steps down on the
   minute bucket exactly as §2.2 — the clock's lamp and the
   Deck's lamp are one derivation (`spanRemainingSeconds`
   fraction), so the two faces can never disagree about the
   oil. Burn-in: the overlay's existing 5×5 grid walk
   (AirlockAndDisarmOverlays.kt:130–131) is untouched — the
   glow is centred on the walking column, so the lamp walks
   WITH the numerals; a glow left behind while the numerals
   stepped would be a lamp detached from its niche.
6. **The last minute.** FLOW §2.4's prints stand (current
   time on the face; `ENDS HH:MM` takes the exit line's place
   in the last minute — print-layer, from the row). Temple
   adds: the glow stands at its floor (fraction ≈ 0: radius
   0.55, alpha 0.72, centre fully sunk) — the lamp is at its
   last oil exactly when the print says the end's hour.
7. **The finish.** The VM hides the clock at the finish edge
   (existing, `:1051`) — in Temple's reading the lamp goes
   OUT with the session: phase-two reversed is NOT run (no
   carrying back; he is awake and present at a finish). What
   stands underneath is the Deck and the receipt with T-2's
   ember ring (§2.3) — the lamp's last light, around the slip
   that leaves the building. **BROKEN finish:** the clock
   closes the same way, but there is no ember ring (§2.3) —
   the lamp goes out and the Confession's broken rule is
   what the returning light (the Deck's normal night face)
   shows him. The lamp does not warm a broken day.

### 5.2 The FLOW laws, kept by name

- **Dismissal is a decision** (FLOW §2.2 law 2): tap the
  niche away → the lamp is carried BACK (the two phases run
  in reverse, same 420ms) to the stele, and the clock does
  not re-raise this session. The DESK CLOCK chip remains the
  hand's way back in; re-entry by hand kindles the niche
  directly at phase two (no Deck phase — he is standing on
  the Deck already; the lamp is where he left it).
- **Cold open into a live session** (FLOW §2.3): the clock is
  the first surface, lamp already lit in the niche at the
  current fraction — NO carrying animation. He was not there
  to see it carried; an animation played for nobody is a
  performance, and Temple does not perform.
- **Routine-born sessions** (FLOW §2.5): nothing lights in an
  empty room. First touch finds the lamp lit (above). For
  SLEEP, the niche IS the nightstand face all span, light
  under Salz's cured ceiling — **the §2.4 light cure is a
  condition of this invention shipping**: T-2's alpha law
  (≤1.0 → 0.72) applies to the GLOW brush; the numerals,
  strip, and banner take FLOW §2.4's 55% composite (≈0.216,
  under Nacht's 0.25) exactly as Ablauf priced it. The glow
  is a brush behind the numerals, never an addition to their
  luminance.
- **Break pickup** (FLOW §2.3): the banner's break line
  prints as designed there; the lamp does nothing special —
  the span fraction already includes the break (§2.1), so
  the oil kept burning down through the repose, which is
  the truth of a lamp left tended in a courtyard.
- **Read face** (FLOW §2.2 law 3): no verbs on the niche
  except leaving. The lamp adds none.

### 5.3 Machinery price

**SMALL, and entirely inside FLOW §2.6's already-priced
items.** The settle flag, the cold-open derivation, the
dismissal bit: Ablauf's table prices them; Temple claims no
second price for them. Temple's own addition is view-only:
the two-phase parameter interpolation on the existing 420ms
fade (glow radius/centre driven from T-2's parameterised
brush, §2.2 — if T-2 is built, this is ~20 lines of lerp on
its constants; if T-2 is refused, the carrying degrades to
FLOW's plain fade and this invention dies with T-2 — stated
plainly, it is T-2's dependent, not its smuggler). No new
state, no new sound, no bell, no bed.

### 5.4 Why only Temple

Every other look's desk clock is a FACE the phone shows.
Temple's is a lamp the building keeps — and Temple is the
only world whose session-light is a physical, finite,
carryable object with a home (the stele by day-session, the
niche by night-desk). Dawn's clock is lit by a sky (you
cannot carry a sky indoors); Obsidian's is one numeral in
darkness (nothing to carry); Noir's would be a sign (signs
are fixed to their walls); Signal's a poster (posters do not
travel between rooms); Blueprint's a plate (plates are
filed, not carried). The carrying survives Fremd: strip the
names and colour, and a light that leaves the work-face and
reappears, diminished by exactly the session's elapsed
fraction, around the time-face is still a lamp being carried
— in any other world it is a crossfade looking for a reason.

---

## §6 · Price summary (for Stein's merge)

| Piece | Price | New state / data |
|---|---|---|
| §0 shared derivation | small (inside T-1) | none — reads only |
| T-1 Courses of the Day | small-medium | none |
| T-2 The Lamp Burns Down | small | none |
| T-3 The Keystone | small (small-medium alone) | none |
| N-1 The Cracked Seal | small | none — reads spend ledger |
| N-2 The Plumb Line | small | none — reads guard verdict |
| N-3 The Mortar Cure | none beyond T-1 | none |
| §5 The Lamp Is Carried In | small, inside FLOW §2.6's prices | none beyond FLOW's own |

Nothing in this file touches the DB, the seal's voice, the
wheel's physics, or any other look's surface. No render is
claimed; every visual statement here is a spec to be proven
at the lane's next render, under the wave's proof law.

*— Funke (spark), Innovator (the Wild), Temple crew,
Oct 29, 2026. NEXT is in my closing letter to Stein.*
