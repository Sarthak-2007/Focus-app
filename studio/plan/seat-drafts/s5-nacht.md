# §5.8 AOD — THE DESK CLOCK (Nacht's part)

*For MASTER-PLAN §5, to be merged by Sana with Pit's body
(`plan/seat-drafts/s5-pit.md`). This file expands Pit's §5.8 summary
into the full AOD spec; it replaces that summary and nothing else in
his draft. Pit's §§5.1–5.7 stand as filed; his §5.9 item 12 is
expanded at §5.8.12 below for the merge to fold back in.*

*Floor: DESIGNERS-PLAN §6.2 — Nacht's terms, Pit countersigned
(nacht.md §1 + pit.md §8; disagreement 9 closed, two signatures).
Reconciled against WILD-PLAN §2.5 (Pia + Mara, the Rigid Minute
Plate) at §5.8.10 — every difference named and ruled there, or
flagged. Constants are REGISTER's: REMAINING `H:MM` at 104sp ·
patrol 4×3 anchors · dimming luminance 0.215 → 0.174 → 0.130 ·
ceiling 0.25. Night caps bind from the locked staircase
(DESIGNERS-PLAN §1.3, Nacht §0): the AOD is its own token zone
inside that staircase, not an exception to it. Where this draft and
REGISTER ever differ, REGISTER governs and this draft is wrong.*

**One sentence:** the AOD is a separate instrument, not the Deck
dimmed — two readings and a dot on true black, every number a field
of the Now Frame or nothing at all, moving once a minute and
reacting to nothing.

---

### 5.8.1 Sealed-state law (this surface)

The AOD exists only while a session is sealed (running face) or
while the machine is armed and waiting (idle face, §5.8.6). While a
session is sealed the AOD is display-only in the strictest form in
the app: it carries **no verb, no tap target, and no decision** —
there is nothing on it to press, and wake-touch (§5.8.9) changes no
state. It is not a door into the session: the HELD digest is not
read here, the valve does not live here, and no mid-session fact
(deflection, blocking death, break entry) is announced here
(§5.8.7). Unlocking leaves this surface entirely and lands where
§5.8.9 says. HOLD is trivially absolute on a face with no controls;
this paragraph states it anyway, per §0.1-5, so no future element
can claim the face was unspecified.

### 5.8.2 Truth law — the law the fabricated constant died under

**Every number the AOD prints derives from the Now Frame, or it
renders nothing.** One pure `derive()` (REGISTER; MAXIMPL-INVENTIONS
top-3 #3, Nadia × Freya) publishes verified elapsed, penalty-aware
remaining, block/break state, finishes-at, window state and weekId;
the AOD consumes fields and derives nothing of its own — no clock
read, no stored counter, no local fraction.

This paragraph is the law the shipped lie died under, stated so it
cannot regrow: the shipped AOD, when no session total existed
(stopwatch / idle), rendered its progress as the **literal constant
`0.65f`** — a fabricated number drawn as a measurement
(`AirlockAndDisarmOverlays.kt`, Nadia T6). That fabrication is
already fixed in the build (monolith-truth.apk, per the re-seating
brief); what this section owns is the rule that made it a lie:

- REMAINING = the frame's penalty-aware remaining, formatted per
  §5.8.3. ELAPSED = the frame's verified elapsed. The bar's fraction
  = frame elapsed ÷ true span, the span always the registered
  formula **n × block + (n−1) × 10** (Safe Break 600s between blocks
  only, never after the last).
- **Absence renders as absence, never as a number.** Where the frame
  carries no value, the element does not render: no total in the
  frame (idle, §5.8.6) → no bar; no next routine in the frame's
  window state → the NEXT slot is an honest blank, not a placeholder.
- **FLOW, ruled by derivation (Nacht ruling, flagged for Sana/Ren
  ratification — not bilaterally settled, because neither seat
  priced a no-limit session on this face):** a FLOW session has no
  remaining and no total in the frame. The hero slot therefore
  renders **nothing** — its register is held, the cluster does not
  reflow, ELAPSED stands as the face's sole reading, and the bar is
  absent. The hierarchy never swaps (§5.8.3) is not broken by this:
  nothing is promoted; an absent fact is simply not drawn. The
  alternative — elapsed promoted into the hero, or a drawn `NO
  LIMIT` — would be a swap in one case and a slogan in the other,
  and both are dead for independent reasons.
- The finish `0:00` (§5.8.8) is the frame's remaining reaching zero,
  not a drawn constant. The idle wall clock (§5.8.6) is the frame's
  wall time, the one wall-clock rendering this face is licensed —
  on the idle face only.

### 5.8.3 The face — running session

True `#000000`, full screen. No panels, hairlines, nav, brand, date,
wall clock, streaks, or specular of any kind. Zero chrome. Exactly
this, as **one rigid cluster**, nothing else on the screen:

| Element | Spec |
|---|---|
| **Reading 1 — REMAINING** | `H:MM`, no seconds, ever. Sora 700, **104sp**, tracking −3.0, lh100 (REGISTER; Set's H1 — H1 104 is AOD remaining only). Colon at **45%** of the numeral tone, and it **never blinks** — a blinking colon toggles the same pixels at maximum duty; it is a burn-in machine. The format differs from the running screen's `M:SS` by surface (§5.3) — law, not drift. |
| **Reading 2 — ELAPSED** | Plate label `ELAPSED` (Anonymous Pro 700, 10sp, +2.0 tracking) + numeral `H:MM`, Sora 600, **50sp** (Set's H3), on the same left axis below REMAINING. |
| **Progress bar** | 120dp × 3dp, directly under the elapsed numeral. Track = label tone at 35% alpha over black; fill = the elapsed tone (§5.8.5 stage values), length = the frame fraction (§5.8.2). **Geometry, not a reading:** no digits, no `%`, ever. The AOD has no needle; the bar is the needle's twin (Pit's §3 logic, countersigned). |
| **Armed dot** | Ø6dp filled disc, ember `#7A3410` (lum 0.066), fixed at the cluster's top-left, 8dp above REMAINING's cap height, on the left axis. The face's only warm element. It never pulses, never animates, and **never dims** across stages (§5.8.5) — the proof of armed is exactly as visible at minute 90 as at minute 1. AOD accent budget is zero (DESIGNERS-PLAN §1.3); the ember dot is a tone, not the accent, and spends none of it. |
| **Transient task line** | Atkinson 400, 15sp, elapsed tone, above REMAINING, single line, truncates at 28 chars. Present **only** for the first 60s of a session and for 10s after any wake-touch or pickup (§5.8.9), then gone. Its slot is reserved for the session's life, so its leaving never reflows the cluster. |

**Two standing readings, rigid hierarchy, never swaps.** REMAINING
is always the hero: it is the only number that changes what he does
next. ELAPSED is context. Nacht's wave-1 30-minute anchor/large swap
was killed by its own author in the settlement — a glance-parse
learned once stays valid all night; burn-in is carried by the
patrol and the dimming, not by swapping sizes. `OF <total>` never
prints: elapsed + remaining + the bar carry the whole fact, and the
total would be arithmetic's third rendering. NEXT never prints
while a session runs (§5.8.6 gives it its only home).

**Updates:** once per minute, on the minute boundary — the numeral
change, the patrol step (§5.8.4) and any stage step (§5.8.5) are
**one wakeup**, never three. Between ticks the face is perfectly
still. Sound and haptics: none — the AOD is unconditionally silent
(Ton's silence list); the night multipliers have nothing to
multiply here.

### 5.8.4 The patrol

- The whole cluster moves **as one rigid block**; internal geometry
  never changes, so the glance-parse survives every move.
- **Grid (REGISTER): 4×3 anchors** — x-offsets {−36, −12, +12, +36}dp,
  y-offsets {−64, 0, +64}dp from screen centre. The cluster's
  bounding box stays **≥24dp from every screen edge** at every
  anchor.
- **Travel:** ≤1 grid step per minute, on the minute tick. The
  sequence visits **all 12 anchors before repeating any**, never
  repeats the previous anchor, never jumps home.
- **Form of the move: a 1-frame cut, never an animation** (DESIGNERS-
  PLAN §1.5: AOD patrol = 0ms jumps on the minute tick). Motion in
  peripheral vision during study is the one thing this face must
  never produce; a glide reads as an event, a cut reads as nothing.
- Patrol + dimming together are the burn-in defence; neither alone
  is enough over a 12-hour study day. Over a 50-minute block the
  cluster traverses the full grid, and Stages 1–2 cut the duty
  further.

### 5.8.5 Dimming — the staircase on this face

Stage clock = continuous AOD dwell within the session. A wake-touch
(§5.8.9) borrows Stage 0 for 10s **without resetting the stage
clock**. Every value is measured against the REGISTER ceiling:
**no element on this face ever exceeds luminance 0.25.**

| Stage | When | REMAINING | ELAPSED | Labels / bar track |
|---|---|---|---|---|
| **0 — fresh** | 0–20 min | `#8A7E71` — lum **0.215** | `#6E6359` — lum 0.130 | `#4E463F` — lum 0.064 |
| **1 — settled** | 20–50 min | `#7D7268` — lum **0.174** | `#5C5348` — lum 0.089 | labels **gone** |
| **2 — deep** | 50 min + | `#6E6359` — lum **0.130** | `#554C43` — lum 0.075 | labels gone · bar-track alpha halves |

- The REGISTER numbers (0.215 → 0.174 → 0.130) are REMAINING's
  luminance at each stage; the hexes above are Nacht's measured
  tones carrying them. (Stage 2's REMAINING hex `#6E6359` is Stage
  0's ELAPSED hex — the same hex in a different role at a different
  hour is the staircase working, not a collision to "fix.")
- **Labels drop at Stage 1:** the `ELAPSED` plate (and, on the idle
  face, the `NEXT` plate) is gone from 20 minutes on — after 20
  minutes he knows the layout; labels are training wheels and they
  come off. The numerals never drop.
- **Transitions** happen on a minute boundary and crossfade over
  **2,000ms** — a visible step at 20:00 would pull the eye the clock
  exists to not pull. Where a stage transition and a patrol step
  land on the same tick: position cuts (1 frame), tone crossfades
  (2s), simultaneously; neither waits for the other.
- **Floors:** REMAINING never below lum 0.130 (read at 1 metre, at
  104sp); the armed dot never dims at all (§5.8.3). The clock gets
  quieter the longer the session runs. **It never gets brighter on
  its own, for any reason except the finish step (§5.8.8).**
- Night caps, stated once so no element drifts past them: AOD tones
  live under the 0.25 ceiling above — which is this zone's cap, and
  it is stricter than any reading of the general night staircase
  (locked staircase top step, the cap `#4A423C`, lum 0.0568, governs
  *fills*; AOD elements are glyphs on true black, governed by the
  0.25 ceiling, never by a fill tone). No AOD element is a fill, and
  no staircase fill tone is ever borrowed onto this face.

### 5.8.6 The idle face (no session running)

Same cluster, same patrol, same stages — the stage clock is
continuous idle dwell, resetting at the 03:30 wake or the first
touch of the day:

- **Reading 1 — wall clock** `HH:MM`, 104sp, same tones, same colon
  law. Overnight this is his bedroom clock: `03:30` is the first
  thing he sees on waking, readable without touching anything.
- **Reading 2 — NEXT:** plate label `NEXT` + the next routine's
  start as a 50sp numeral in the ELAPSED slot, with its loadout word
  at 15sp beside it (`05:00 · STUDY` — in his own routine's terms;
  the face prints what he set, never a seed). Source: the frame's
  window state (§5.8.2). If no routine is armed, the slot is empty —
  an honest blank, not a placeholder.
- **No progress bar.** Bar present = the machine is working; bar
  absent = the machine is waiting. That single presence/absence is
  the idle/running distinction at a glance (Pit's rule, adopted in
  the settlement).
- **Dot:** hollow ring (label tone) when a next routine is armed;
  **absent** when nothing is armed. Nothing armed, no armed mark —
  the face does not lie about a machine that isn't set.
- At 20:00, when his day ends, the face he leaves running is this
  face at Stage 2 — the dimmest lawful thing in the app, beside a
  sleeping man.

### 5.8.7 Events do nothing

The face's event law is invariance. Each item below changes **zero
pixels** beyond the frame's own numbers stepping on their minute
tick:

- **Deflection** (a blocked app is attempted mid-session): nothing.
  No flicker, no dot change, no count, no annotation. An AOD that
  reacts to every absorbed temptation is a scoreboard, and the
  count's one home is the receipt (§5.7).
- **Blocking death** (his verdicts Q7+Q8, §5.6): nothing — no word,
  no tone step, no early end. The clock runs to its scheduled end
  in silence with the room; the receipt tells the truth once.
- **Safe Break entry / exit:** nothing. The AOD's form is invariant
  across block and break; REMAINING keeps stating the session's
  frame remaining throughout. The break's own word (`BREAK · 9:41`)
  lives on the running screen's zone 0 (§5.3), not here — a break
  countdown promoted onto this face would be a second hero wearing
  the first one's slot. *(Nacht ruling, same derivation as §5.8.2's
  FLOW ruling; flagged for ratification with it.)*
- **Routine firing while the AOD is up:** nothing visible. An idle
  face becomes the running face at the next minute tick — content
  swaps on the tick, patrol continues, no flash, no takeover. The
  machine does not wake the room to announce it is working.
- **Notifications:** never wake, brighten, or annotate the AOD. A
  system call yields the face to the system UI; when it ends, the
  AOD returns at the same stage and the next patrol anchor, as if
  uninterrupted.

### 5.8.8 Finish — exactly one step, for exactly 60 seconds

When the frame's remaining reaches zero:

1. REMAINING holds `0:00`; all tones step up to **Stage 0 values**
   — an undim, not a flare; every value stays under the 0.25
   ceiling (REMAINING 0.215).
2. The dot steps up from ember `#7A3410` to `#C74814` (lum 0.168 —
   still under the ceiling; the wave-1 alert `#FF8A5C` at lum 0.402
   breaches the ceiling and is **not** used on this face, by the
   night seat's own ruling, nacht.md §2.9).
3. The step holds for **exactly 60s**. A tap during the finish
   minute goes to the Deck, where the receipt lives (§5.7) — **the
   AOD itself shows no receipt, no total, no `TURNED AWAY`.**
4. Then the face hands over to the idle face (§5.8.6) at Stage 2
   tones, and the patrol walks on.

One step is noticeable across a dark room precisely because nothing
else ever steps. A session ended by the valve (BROKE, §5.7) gets
**no finish step on this face**: the session is simply no longer
running at the next tick, and the idle face succeeds it — endings
never share a silhouette, and a break is not dressed as a finish on
the one face he might be asleep in front of. *(Form of §5.7's law
on this surface; the BROKE receipt and its words live on the Deck.)*

### 5.8.9 Wake behaviour

- **Pickup / single tap on the AOD:** transient only — Stage 0 tones
  for **10s** + the task line (running face, §5.8.3) — then back to
  the current stage. No unlock, no state change, patrol position
  unchanged, stage clock unreset (§5.8.5).
- **Unlock (power / double-tap through):** opens the app on the face
  that is true — the **running screen** (§5.3) if a session is live,
  the **Deck** (§2) if not. Never a home screen, never a settings
  page.
- **No day-flash, ever.** The first frame rendered is night ground
  `#171412` (the locked staircase's ground); scheme and surface are
  resolved from stored routine state **before** the first frame
  draws. Window brightness **ramps**: it opens at the AOD floor and
  eases to his set night level over **2.5s, ease-out** — dark-adapted
  eyes are not hit with the full primary tier in one frame.
- **ARM from this face** is priced in §2.6 (plate → sealed ≈3.1–3.5s
  typical, ≈7.2s worst, inside the 10s wall). Nothing in this
  section adds a step to that path: the wake transient is a
  rendering, not a gate.

---

### 5.8.10 Reconciliation — WILD-PLAN §2.5 (the Rigid Minute Plate)

Pia + Mara's plate is the strongest rival this face has had, and
parts of it were already the settled law before either file was
written. Each difference is named and ruled; **ruled** means the
settled §6.2 terms govern and the Wild form does not enter §5.
Nothing below is forked: where Wild and the settlement agree, the
agreement is recorded so the merge does not re-open it.

**Agreements (no difference to rule):** true `#000000`, no container
· colon at 45% · the fraction drawn, never numbered, no `%` text ·
one wakeup per minute performing the numeral change and the burn-in
move together · staged quieting beginning at 20 minutes · total
haptic and sound silence · keep-screen-on as the face's premise (the
kept desk clock is his verdict, not either cohort's preference).

- **D1 — Typeface and hero size. RULED: settled governs.** Wild:
  Anonymous Pro Bold, 132sp at ≤4 glyphs / 112sp in the finale.
  Settled: Sora 700, **104sp**, always. 132 and 112 are off the
  registered ladder (104 / 72 / 50 / 22 / 15 / 10 — off-ladder sizes
  are build errors, REGISTER), and the ladder assigns H1 104 to AOD
  remaining in Sora, the display family; Anonymous Pro is the plate
  family, and a hero set in the plate family breaks the type law
  the whole app reads by. Wild's size-switch at the finale (132→112)
  is also a hierarchy event at the one moment §5.8.8 reserves for a
  single tonal step.
- **D2 — Hero tone. RULED: settled governs.** Wild: 88% cream.
  Settled: the staged tones of §5.8.5, ceiling 0.25. Cream
  `#FFE4CF` is lum ≈0.81 full-strength and remains far above the
  REGISTER ceiling at 88% over black; on this face cream is not a
  licensed tone at any alpha that reads as cream. The night
  staircase's standing rule — cream is text/glyph only, never a
  flood — was written for screens *brighter* than this one.
- **D3 — The second reading's contents. RULED: settled governs.**
  Wild: an elapsed **+ ends-at** micro-block. Settled: ELAPSED
  alone. Ends-at (finishes-at) is a third reading, and two means
  two; its homes already exist — the running screen's zone 4
  (§5.3) and the frame's other consumers. A man who unlocks gets
  finishes in one frame; a man glancing at the desk clock gets the
  two numbers that change what he does next, and ends-at changes
  nothing at a glance that remaining doesn't already say.
- **D4 — The second reading's position. RULED: settled governs.**
  Wild: the micro-block sits **beside** the hero. Settled: ELAPSED
  sits **below** REMAINING, on the same left axis, inside the rigid
  cluster. Beside widens the cluster's bounding box against the
  patrol's edge law (≥24dp at all 12 anchors) and splits the
  glance-parse across two axes; below keeps one axis the eye learns
  once. The patrol grid was priced for the settled cluster's box.
- **D5 — Progress form. RULED: settled governs; the shared principle
  is recorded.** Wild: a 2dp elapsed-fraction **seam hairline**,
  block ticks cut in black, 100% head marker. Settled: the 120×3dp
  **bar** (§5.8.3). Both draw the fraction and neither numbers it —
  that principle is common ground, not a difference. The forms
  differ in what else they say: the seam's block ticks would state
  block boundaries on a face whose law is two readings — block
  state is a frame field with its homes on the running screen, and
  ticks cut at block boundaries without the inter-block breaks
  drawn would misstate the true span's shape (breaks are 10 of
  every 60 minutes at 3×50). The bar says the one thing this face
  is licensed to say geometrically — how much of the span is gone —
  and nothing else. The seam survives nowhere in §5; Horiz's later
  night-tape raid on this seam (Invention wave) is a separate
  filing, not part of §2.5, and is not ruled here.
- **D6 — Footnote. RULED: settled governs.** Wild: a footnote
  carrying the block word + time of day. Settled: neither exists on
  the running face — no wall clock while running (§5.8.3), no block
  word at all. Time of day survives only transposed: on the idle
  face it is not a footnote but the hero itself (§5.8.6). A footnote
  is a third tier of small print on a face whose smallest licensed
  print is a 10sp plate label that itself drops at Stage 1.
- **D7 — Frozen Specular. RULED: settled governs.** Mara's layer: a
  static top/left catch edge at 6% alpha, shifting with the patrol;
  digits untouched. Her discipline inside it — the travelling
  specular functions are **never called** on AOD — is adopted as
  settled law and stated at §5.8.3 (no specular of any kind). The
  edge itself does not ship: the specular rig's territory is closed
  (knob cap, sled caps, wheel crown/hub, pressed key caps only —
  DESIGNERS-PLAN §1.4), AOD ground and text are forbidden territory,
  and a catch edge that shifts with the patrol is a second moving
  element on a face whose only motion is the patrol cut itself.
- **D8 — Patrol path and motion. RULED: settled governs.** Wild:
  the plate tours a golden-angle Lissajous figure-8, 52×34dp field,
  **520ms heavy settle**. Settled: the 4×3 anchor grid (REGISTER),
  ≤1 step per minute, all 12 before any repeat, **1-frame cut**.
  Three separate differences, one ruling: (a) the grid is a
  registered constant and the Lissajous field is an unregistered
  rival — REGISTER forbids building from it; (b) a 520ms settle is
  *animation* — peripheral motion during study, the exact event
  §5.8.4's cut exists to never produce, and DESIGNERS-PLAN §1.5
  fixes AOD patrol at 0ms jumps; (c) the exhaustive grid carries a
  guarantee the continuous figure cannot state — no anchor repeats
  before all 12 are visited, so no glyph pixel holds full duty
  beyond a bounded run of minutes.
- **D9 — Brightness cap. RULED: settled governs; Wild's number is
  not translated.** Wild: 0.35 brightness cap. Settled: luminance
  ceiling **0.25** per element (REGISTER). These are different
  quantities — a device brightness fraction is not a relative
  luminance — and importing 0.35 in either reading forks the
  register: as luminance it breaches the ceiling, as brightness it
  registers a constant nobody measured against this panel. §5
  builds to the ceiling; device brightness on unlock is governed by
  §5.8.9's ramp, not by a cap imported from the plate.
- **D10 — Restore-on-dispose. RULED: settled governs, Wild's intent
  absorbed.** Wild: brightness restored when the face disposes.
  Settled has no dispose-and-snap: exit from this face is the unlock
  path, whose brightness **ramps over 2.5s from the AOD floor**
  (§5.8.9). An instant restore is a brightness step, and a step at
  03:30 is the day-flash's smaller brother. Where the face is
  disposed without unlock (system call, §5.8.7), it returns at the
  same stage — there is no third behaviour to restore to.
- **D11 — Finish. RULED: settled governs; Wild's constraint is
  satisfied by the settled values and recorded as their reason.**
  Wild: finish dismisses automatically, with no brightness or
  colour breach for a man who may be asleep. Settled: exactly one
  60s step, capped at Stage 0 (0.215) with the dot at 0.168 — no
  value breaches anything, which is Wild's constraint met in
  numbers. The forms differ on dismissal: an immediate vanish at
  zero would spend the one glanceable confirmation the face is
  allowed, and the settled handover after the 60s *is* automatic.
  If Wild's sentence meant only "no breach, no action demanded of
  him," there is no remaining difference; if it meant instant
  dismissal, that form is ruled out by §5.8.8.
- **D12 — The task line. RULED: settled governs; Wild's silence is
  not a counter-spec.** §2.5's plate carries no task line (its
  footnote, D6, is not one). Settled carries the transient line
  (§5.8.3) — the settlement's answer to a real orientation need at
  the only two moments it is real. A rival that omits an element
  does not out-argue the bilateral settlement that priced it.

**Nothing in §2.5 is left unresolvable.** Every difference above is
closed by a registered constant, the type ladder, the staircase, or
the bilateral settlement itself — the three authorities §0 puts
above both cohorts' preferences, this seat's included.

### 5.8.11 Moench's Extinguished Clock — carried invention, NOT spec

**Ruling: the Extinguished Clock does NOT enter §5 in this draft.
It stays a carried invention** — preserved whole below, with its
numbers, its bypass law and its kill condition, in the
too-good-to-lose sense: filed, owned, and promotable by ruling,
but not plan law. (Source: invention/HUMANS-INVENTIONS.md,
Stillness; full spec at invention/humans-parts/moench-moments.md
§2.)

**The invention, as filed:** inside a sealed block, the device
**face-down** (proximity covered) **and motionless** (accelerometer
variance below threshold) for a continuous **120s** → the AOD fades
to pixel-off black over **2,000ms, linear, brightness channel
only**. While extinguished: nothing — no dot, no ghost, the patrol
idles. Any pickup / tilt / face-up restores the normal AOD at its
dimmest lawful tier for **10s**, then re-fades over 2s if stillness
resumes. Event surfaces (intercept overlay, a break, a completion)
bypass extinction instantly at full lawful brightness. Price as
filed: ~1 engineer-day. **Its author's kill condition, carried
verbatim in force:** if device trial shows him picking the phone up
*more* per block, the invention dies by its own evidence.

**Why it does not enter — four reasons, in order of weight:**

1. **Its trigger's core constant does not exist.** Moench specs the
   stillness gate as "the settled threshold already used for AOD
   position-shifting" — but the settled patrol is **clock-driven**
   (one step on the minute tick, §5.8.4); no accelerometer variance
   threshold exists anywhere in the settled spec or REGISTER.
   Entering the invention would mean registering an invented
   threshold in a merge draft, which is exactly the fork REGISTER
   exists to prevent. The 120s / 2,000ms / 10s numbers are filed and
   could be registered; the variance threshold must be measured and
   proposed first, by name.
2. **It is unmerged.** No coordinator merge and no Ren ruling has
   adopted it; it sits in the Humans merge's unranked Stillness
   tier. §5's floor for this seat is the bilaterally settled §6.2,
   and a single seat promoting another cohort's invention into the
   plan of record inside a merge draft bypasses the route every
   other adopted invention took (the Frozen Digest reached §5.5
   through the Humans merge ranking + Pit's draft, not a side
   door). Carrying it here, visibly, is the lawful maximum a merge
   draft may do for it.
3. **Its own author pre-registered its death by evidence, and the
   evidence does not exist yet.** The anti-case is real even in its
   weakened form: face-down, the glance the clock serves is already
   priced at a pickup, and the 10s return means information is
   priced, never denied — but whether the extinction *teaches the
   glance to die* (Moench's claim) or *taxes pacing* (his
   anti-case) is an empirical question about this man's blocks.
   Spec entry before the device trial would invert his gate:
   adoption first, falsification later.
4. **Its text carries one internal tension I cannot resolve from
   this seat, and will not resolve by invention:** the bypass list
   includes *a completion* ("every event surface bypasses instantly
   at full lawful brightness"), while its session-end paragraph
   says completion while extinguished "behaves exactly as it does
   today" — and today's completion on this face is the §5.8.8 step,
   which under a bypass would fire face-down into a desk at full
   lawful brightness, and under the second reading would run unseen
   while the face stays black. Which completion law the invention
   intends is Moench's / Ruth's to state — flagged in Sana's letter
   as the one unresolvable item in this draft. It does not block
   the merge, because the invention is carried, not specced; it
   blocks any future promotion until answered.

**What carrying it costs the settled face: nothing.** Checked
against the laws it would touch, so promotion stays cheap if it
ever comes: extinction renders absence, not a false number (TRUTH
holds — black claims nothing); it removes no verb (there is none);
the kept-AOD verdict is not relitigated by it, since his kept use
is face-**up** beside him, a posture the trigger never fires in;
and its fade form (2s, linear, brightness-only) is already this
face's stage-transition form. If it is ever promoted, the promotion
needs exactly: a measured variance threshold registered, the
completion-bypass question answered, and the pickup-count trial
run. Until then it rides here, named, and §5 builds without it.

### 5.8.12 Acceptance checks (this surface)

*Expanding Pit's §5.9 item 12 for the merge; numbering is his to
fold.*

1. **Two readings, counted:** with a session running, the face
   carries REMAINING, ELAPSED, the bar, and the dot — and nothing
   else, at any session minute, in any stage. The task line appears
   only inside its two transient windows and its slot never reflows
   the cluster.
2. **Truth, frame-equality:** AOD, running screen and Deck print
   the same remaining / elapsed for the same instant (screenshot
   set at three session minutes); the bar's fraction equals frame
   elapsed ÷ (n × block + (n−1) × 10) at every tick; grep stands —
   no fabricated progress constant, no local fraction, no wall-clock
   read on the running face. In FLOW and on the idle face the bar
   does not render at any fraction.
3. **Hierarchy:** REMAINING is 104sp and ELAPSED 50sp in the first
   minute and the ninetieth; no swap frame exists in a full-session
   capture.
4. **Patrol:** a 50-minute capture visits all 12 anchors before any
   repeat, moves ≤1 grid step per minute, cuts in 1 frame (no
   settle, no glide in frame-diff), and the cluster's box never
   comes within 24dp of an edge.
5. **Ceiling and stages:** measured luminance of every element
   ≤0.25 at every stage; REMAINING measures 0.215 / 0.174 / 0.130
   at Stages 0 / 1 / 2 within tolerance; labels present at minute
   19, gone at minute 21; the dot's tone is identical at minutes 1
   and 90; no element brightens at any point except the finish step.
6. **Events change zero pixels:** deflection, blocking death, Safe
   Break entry/exit, and a routine firing each produce an empty
   screenshot-diff against the pre-event face (the frame's own
   minute tick excepted).
7. **Finish:** at zero, exactly one step — `0:00` at Stage 0, dot
   at `#C74814` — held 60s, then idle at Stage 2 tones. A BROKE
   ending produces no finish step. No receipt element ever renders
   on this face.
8. **Wake:** pickup yields exactly the 10s transient and returns to
   the pre-touch stage and anchor; unlock's first frame is night
   ground with no day-flash frame in the capture, and brightness
   reaches the set night level by ramp (≈2.5s), never by step.
9. **Silence:** a full session on this face produces no sound and
   no haptic, including at stage transitions, patrol steps, and the
   finish step.

**Build status at filing (Oct 29, ~08:14 IST):** the truth fix this
section's law demanded is built (monolith-truth.apk — the `0.65f`
fabrication is dead in code, per the re-seating brief). The settled
face as specced here — frame-derived bar, exhaustive 4×3 patrol,
staged luminances at the registered values, transient task line,
finish step — is **not yet verified built to these numbers**; where
the shipped AOD differs, this section, not the shipped app, is the
record. The Extinguished Clock (§5.8.11) is carried, not built, and
no build item is raised for it by this draft.

*— Nacht, Designers cohort, night seat. Seat draft for MASTER-PLAN
§5 (AOD part), filed to Sana for the merge with Pit's body. No code
touched.*
