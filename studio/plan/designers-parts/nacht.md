# NACHT — Night seat, Designers cohort (coordinator: Sana)
## PLAN WAVE — the AOD desk clock, the night application audit, the 03:30 test. No code.

Sarthak's order, verbatim, binding: "the citation requirement is LIFTED —
no seat needs to cite the reference library or anything else. Full freedom
and creativity for you and all 20 seats; the 20 references are there if
wanted, never as a quota." Inside the settled law, everything below is mine.

**His reality, which this file serves:** he wakes 03:30 in the dark,
studies fasted 05:00–08:00, sleeps 20:00. Night is not an edge case or a
preference toggle. It is his first screen and his last screen every day,
and for the 05:00–08:00 block the phone sits face-up beside him as a desk
clock while he works. Night mode is the primary shift.

---

## 0. The staircase, as measured (not as assumed)

All ratios computed from the locked hexes (relative luminance, WCAG
formula). This table is the ground the audit in §2 stands on.

| Surface | Hex | Lum | Step vs neighbour |
|---|---|---|---|
| Recess | `#100E0C` | 0.0045 | **1.05:1 vs ground** |
| Ground / surface | `#171412` | 0.0073 | — |
| Block bar | `#161311` | 0.0068 | 1.01 vs ground · 1.04 vs recess · **1.36 vs raised · 1.88 vs sheet** |
| Raised | `#362C27` | 0.0273 | 1.35:1 vs ground ✓ |
| Sheet / cap | `#4A423C` | 0.0568 | 1.38:1 vs raised ✓ · 1.86:1 vs ground |

| Text tier | On ground | On recess | On raised | On sheet |
|---|---|---|---|---|
| Primary `#D2C6BB` | 10.95:1 | 11.5:1 | 8.11:1 | 5.87:1 |
| Secondary `#9C8E84` | 5.78:1 | 6.07:1 | 4.28:1 | **3.10:1** |
| Muted `#776A60` | 3.51:1 | 3.68:1 | **2.60:1** | **1.88:1** |
| Glyph `#EDE3D8` | 14.5:1 | 15.2:1 | 10.7:1 | 7.76:1 |

Cream `#FFE4CF` is text/glyphs only, never a fill — the floodlight pill
is dead and stays dead. Accent `#F47442` is a COMMIT fill only (Deck
slide, Tasks START); on-accent ink `#471602` measures 5.37:1 on it.
Theme follows the routine/schedule, never a pill; a running session
forces the night scheme at any hour. Night sound ×0.6 / haptic ×0.85
are Ton's, untouched here.

Two facts in that table drive half the audit: **the recess barely steps
off the ground (1.05:1)** and **the muted tier collapses the moment it
leaves the ground**. The staircase is sound going up. Going down and
going quiet, it has holes — named as gaps in §2, with no hex invented
to paper over them.

---

## 1. THE AOD DESK CLOCK — complete spec

The AOD is a separate instrument, not the Deck dimmed: a study display
that burns for up to a full study day beside a man who is trying not to
look at it. Its law (C2, mine): ground true `#000000` · ≤2 readings +
armed dot · luminance ceiling 0.25 for every element · position patrol
against burn-in · staged dimming after 20 min.

### 1.1 The settlement with Pit (Cockpit) — decided, mailed, stated here

Pit wanted three readings: remaining, elapsed + progress bar, task —
plus a conditional NEXT. The law says two. Settlement, sent to
`mail/pit/` (thread: aod-readings-settlement) before this file was
written: **Pit countersigned at 05:30 IST** (letter in my inbox; the
settlement is filed as settled in his §8). One AOD spec, two
signatures. His countersign also adopted the colon treatment from my
wave-1 spec — colon at **45%** of the numeral tone — and that value,
not any other, is the one in §1.2 below.

**Pit wins:**
- **ELAPSED is the second standing reading.** It is not a courtesy —
  remaining alone cannot tell him whether a session is young or old,
  and at a glance across a desk that difference is the session.
- **The progress bar ships** (120dp × 3dp, inside the cluster). It is
  geometry, not a reading — no digits, fraction carried by fill length.
  The AOD has no dial and no needle; the bar is the needle's twin.
- **The cluster is rigid and the hierarchy never swaps.** I kill my own
  wave-1 anchor/large swap (elapsed ↔ remaining trading sizes every
  30 min): a glance-parse learned once must stay valid all night.
  Burn-in is carried by the patrol and the dimming instead.

**Pit loses, on the law:**
- **Task name is not a standing reading. Neither is NEXT while a
  session runs.** Two means two. A static task title is also the worst
  burn-in shape on the face — the same glyphs at the same duty for the
  length of a session — and he chose the task minutes before arming;
  the desk clock's job is time, not reminding him of his own decision.
- **Counter-offer, so the orientation need is met:** the task line is
  **transient** — it appears for the first 60 s of a session and for
  10 s after any wake-touch or pickup (Atkinson 400, 15sp, elapsed
  tone), then it is gone. Orientation on demand, darkness at rest.
  NEXT lives on the idle face only (§1.5), where it is reading two.
- **`OF <total>` does not print.** The bar carries the fraction;
  elapsed + remaining carry the rest. The total would be a third
  rendering of arithmetic the AOD never asks him to do.

Set (Typography) has been mailed the same settlement: his ladder's
"AOD task line (third reading, when present)" survives exactly as his
own parenthesis hedged it — *when present*, i.e. transient, at his
15sp. His sizes below are adopted unchanged.

### 1.2 The face — running session

Exactly this, as one rigid cluster, nothing else on the screen:

| Element | Spec |
|---|---|
| Ground | `#000000`, full screen. No panels, hairlines, nav, brand, date, wall-clock, streaks. Zero chrome. |
| **Reading 1 — REMAINING** | `H:MM`, no seconds. Sora 700, **104sp**, tracking −3.0, line-height 100 (Set's H1). Colon rendered at 45% of the numeral tone and **never blinks** — a blinking colon toggles the same pixels at maximum duty cycle; it is a burn-in machine. |
| **Reading 2 — ELAPSED** | Micro-label `ELAPSED` (Anonymous Pro 700, 10sp, +2.0 tracking — the plate) + numeral `H:MM` Sora 600, **50sp** (Set's H3), on the same left axis below REMAINING. |
| Progress bar | 120dp × 3dp, directly under the elapsed numeral. Track = label tone at 35% alpha over black. Fill = elapsed tone, length = elapsed ÷ total. No digits, ever. |
| **Armed dot** | **6dp filled disc**, ember `#7A3410` (lum 0.066), fixed at the cluster's top-left, 8dp above REMAINING's cap height, on the left axis. It is the only warm element on the face. It never pulses, never animates, and **never dims** across stages (§1.3) — the proof of armed stays exactly as visible at minute 90 as at minute 1. |
| Transient task line | Atkinson 400, 15sp, elapsed tone, above REMAINING, single line, truncates at 28 chars. Present only per §1.1 (first 60 s · 10 s after wake-touch/pickup). When it leaves, the cluster does not reflow — its slot is reserved, so nothing ever jumps. |

Why REMAINING is the hero and ELAPSED the second: remaining is the
only number that changes what he does next (pace, break, stop). Elapsed
is context. The hierarchy is fixed for the life of the app — he learns
it once, in week one, and reads it half-asleep forever.

### 1.3 Luminance stages — the dimming

Every value below is measured against the 0.25 ceiling. Stage clock =
continuous AOD dwell within the session; a wake-touch (§1.6) borrows
Stage 0 for 10 s without resetting the stage clock.

| Stage | When | REMAINING | ELAPSED | Labels / bar track |
|---|---|---|---|---|
| **0 — fresh** | 0–20 min | `#8A7E71` — lum **0.215**, 5.3:1 on black | `#6E6359` — lum 0.130, 3.6:1 | `#4E463F` — lum 0.064 |
| **1 — settled** | 20–50 min | `#7D7268` — lum 0.174 | `#5C5348` — lum 0.089 | labels **gone** — after 20 min he knows the layout; labels are training wheels and they come off |
| **2 — deep** | 50 min + | `#6E6359` — lum 0.130, 3.6:1 at 104sp | `#554C43` — lum 0.075 | labels gone · bar track alpha halves |

Stage transitions happen **on a minute boundary** (the patrol tick),
crossfading over 2,000 ms — a visible step at 20:00 would pull the eye
the clock exists to not pull. Floors, stated so nobody dims past them
later: REMAINING never below lum 0.130 / 3.5:1 (it is read at 1 metre,
at 104sp); the dot never dims at all. The clock gets quieter the longer
the session runs. It never gets brighter on its own, for any reason
except §1.7.

Sound/haptics on AOD: none. The face is silent and still; Ton's night
multipliers have nothing to multiply here.

### 1.4 The patrol — positions, interval, travel

- The whole cluster moves **as one rigid block**; internal geometry
  never changes, so the glance-parse survives every move.
- **Interval:** one step per minute, on the minute boundary — the same
  tick that updates REMAINING. Protection costs zero extra wakeups.
- **Grid:** 4 × 3 anchors, x-offsets {−36, −12, +12, +36}dp and
  y-offsets {−64, 0, +64}dp from screen centre. Cluster bounding box
  stays ≥24dp from every screen edge at every anchor.
- **Travel:** ≤1 grid step per move (≤24dp horizontal, ≤64dp vertical),
  sequence visits all 12 anchors before repeating any, never repeats
  the previous anchor, never jumps home. Repositioning is a 1-frame
  cut at the tick, never an animation — motion in peripheral vision
  during study is the one thing this face must never produce; a slow
  drift reads as nothing, a glide reads as an event.
- Over a 50-minute block the cluster traverses the full grid; no pixel
  of any glyph holds the same screen position for more than a few
  minutes at full duty, and Stages 1–2 cut the duty further. Patrol +
  dimming together are the burn-in defence; neither alone is enough
  over a 12-hour study day.

### 1.5 The idle face (no session running)

Same cluster, same patrol, same stages (stage clock = continuous idle
dwell, resetting at 03:30 wake or first touch of the day):

- **Reading 1 — wall clock** `HH:MM`, 104sp, same tones. Overnight,
  this is his bedroom clock: `03:30` is the first thing he sees on
  waking, readable without touching anything.
- **Reading 2 — NEXT**: plate label `NEXT` + the next routine's start
  as a 50sp numeral in the ELAPSED slot, with its loadout word at
  15sp beside it (`05:00 · STUDY`, in his own routine's terms — the
  face prints what he set, never a seed). If no routine is armed,
  the slot is empty — an honest blank, not a placeholder.
- **No progress bar.** Bar present = machine is working; bar absent =
  machine is waiting. That single presence/absence is the idle/running
  distinction at a glance (Pit's rule, adopted).
- **Dot:** hollow ring (label tone) when a next routine is armed;
  **absent** when nothing is armed. Nothing armed, no armed mark —
  the face does not lie about a machine that isn't set.

### 1.6 Wake behaviour

- **Pickup / single tap on the AOD:** transient only — Stage 0 tones
  for 10 s + the task line (running) — then back to the current stage.
  No unlock, no state change, patrol position unchanged.
- **Unlock (power / double-tap through):** opens the app on the face
  that is true — the **Running face** if a session is live, the
  **Deck** if not. Never a home screen, never a settings page. The
  first frame rendered is night ground `#171412` — no white flash
  frame, ever; scheme and surface are resolved from stored routine
  state before the first frame draws.
- **Notifications never wake, brighten, or annotate the AOD.** A
  system call yields the face to the system UI; when it ends, the
  AOD returns at the same stage and the next patrol anchor, as if
  uninterrupted.
- At 20:00, when his day ends, the face he leaves running is the idle
  face at Stage 2 — the dimmest lawful thing in the app, burning
  almost nothing beside a sleeping man.

### 1.7 Deflection and finish — what events do to the face

- **Deflection (a blocked app is attempted mid-session): NOTHING.**
  No flicker, no dot change, no count, no annotation. The deflection
  is handled in-hand by the block overlay at the moment it happens;
  the record of it is told once, in the app, in human language — not
  scored on a face he isn't looking at. An AOD that reacts to every
  absorbed temptation is a scoreboard, and a scoreboard is a second
  rendering of a fact that already has a home.
- **Routine firing while AOD is up:** nothing visible. The idle face
  becomes the running face at the next minute tick — content swaps,
  patrol continues, no flash, no takeover. The machine does not wake
  the room to announce it is working.
- **Session finish: exactly one step, for exactly 60 s.** REMAINING
  holds `0:00`; all tones step up to Stage 0 values (an undim, not a
  flare — every value stays under the 0.25 ceiling); the dot steps
  up from ember `#7A3410` to `#C74814` (lum 0.168 — still under the
  ceiling; the wave-1 alert `#FF8A5C` at lum 0.402 breaches C2 and is
  **not** used here). Then the face hands over to the idle face at
  Stage 2 tones and the patrol walks on. One step is noticeable
  across a dark room precisely because nothing else ever steps.
  A tap during the finish minute goes to the Deck, where the
  session-end receipt lives — the AOD itself shows no receipt.

---

## 2. THE NIGHT APPLICATION AUDIT

Every planned screen walked against the staircase. Verdicts first,
gaps named precisely where the staircase cannot express the screen.
**No hex is invented anywhere in this section.** Every gap goes to
Sana/Ren in the register at §2.8 with an interim rule that uses only
locked tones, so the build is never blocked waiting for a ruling.

### 2.1 Deck (idle / set)

**Verdict: expresses, with GAP-N1 on its two recesses.**
- Readout well + wheel well: recess `#100E0C` on ground — see GAP-N1.
- Wheel face = ground (flat by default ✓); hub = raised `#362C27`
  (1.35 ✓); hub pips/segments = glyph `#EDE3D8` on raised (10.7:1 ✓).
  Hub carries no text, so GAP-N5 never bites here.
- Slide: track = recess (GAP-N1 interim applies), sled at rest =
  sheet/cap `#4A423C` with primary-tier label; on commit the sled
  becomes the accent fill `#F47442` and its label flips to dark ink
  `#471602` (5.37:1 on the accent) **at the gate, not before** — the
  flip is part of the commit rendering, colour confirms what position
  already said. This is the Deck's one orange, and the only one.
- FLOW cap = sheet, glyph word ✓. NOW task line on ground: title
  primary, DONE/QUEUE words secondary (5.78:1 on ground ✓) — never
  muted for anything tappable (Linea's wave-1 finding, confirmed by
  the §0 table: muted is the decorative tier).
- FINISHES = secondary on ground ✓.

### 2.2 Shield (loadout, routines, apps)

**Verdict: expresses, with GAP-N1/N2 on tracks and GAP-N3/N4 inside
the routine editor sheet.**
- Loadout sled: track recess (GAP-N1), caps sheet, selected word
  glyph on sheet (7.76:1 ✓), unselected words secondary on ground ✓.
- Routine rows live flat on ground — names primary, meta lines
  (`→ 13:00 MON–FRI · STUDY`) secondary on ground ✓. ON/OFF sleds:
  cap sheet + glyph word ✓. A caution (missing protection) is one
  alert dot at its owning row only, never a fill, never in chrome —
  alert is a tone that exists in the locked table, spent as a dot,
  which is not a COMMIT and does not break the accent ration.
- App sleds (SHUT / PEEK / OPEN): track must **not** be built from
  the block bar on ground (GAP-N2) — interim: track = recess with
  GAP-N1's hairline rule; caps = sheet; state words glyph/secondary,
  never muted on the cap (GAP-N5).
- The 24h strip / horizon track (however the Shield seats settle its
  geometry): recess bed (GAP-N1), routine blocks raised, the next
  block sheet, NOW line glyph. OPEN gaps in the strip are labelled
  only at the secondary tier on recess (6.07:1 ✓) — muted on recess
  (3.68:1) is furniture-grade and may carry tick numerals only.
- **Routine editor** opens as a sheet — and the sheet is where the
  staircase runs out (GAP-N3, GAP-N4). Interim, buildable today:
  fields carved into the sheet as recess wells (sheet→recess is a
  1.96:1 down-step ✓, and recess always means "carved in," so no
  role collides); field text primary on recess (11.5:1 ✓); hints
  secondary on recess (6.07:1 ✓); on-sheet section words primary or
  glyph only; the sheet's one filled control, if any, is its COMMIT
  (accent) or nothing.

### 2.3 Tasks

**Verdict: expresses, with GAP-N5 on the active row and GAP-N3/N4 in
the composer.**
- Rows flat on ground; titles primary, meta secondary ✓. The queue
  total is a hero numeral, not a surface — no staircase demand.
- Horizon/duration strip: track recess (GAP-N1); unselected chips
  raised; selected chip sheet + glyph word — selection reads by step
  + word, never by brightness flood ✓. (The render's cream selected
  chip is exactly the floodlight this law kills.)
- **Active task row**, if it is carried on raised `#362C27`: its
  sub-steps and meta may use primary (8.11:1) and secondary (4.28:1,
  bold/large only) — **muted on raised is 2.60:1 and is banned**
  (GAP-N5). Done steps on the ground rows: secondary if they must be
  re-readable; muted is lawful on ground only for pure furniture
  (step indices, queue numbers).
- Composer sheet: same interim as the routine editor — recess field,
  primary text, secondary hint inside the field. Duration chips as
  above. ADD is not a commit: outline + glyph word on the sheet, no
  fill (there is no lawful non-commit fill above a sheet — GAP-N3).
  **START is the screen's one accent fill**, label flipping to dark
  ink on commit exactly as the Deck slide does.

### 2.4 Running face

**Verdict: expresses — the staircase's best screen — with GAP-N2 on
the block bar.**
- Bare panel on ground: REMAINING hero primary, task line secondary,
  FINISHES secondary — all on ground, all lawful, nothing competing.
  This is the screen the staircase was built for: one number, one
  needle, darkness around it.
- Dial: face recess (GAP-N1 interim), needle + scale glyph, hub pip
  raised. Progress band inside the rim = raised on recess (1.42:1 ✓
  — the one recess pairing that clears the law unaided).
- **Block bar `#161311`:** on ground it measures 1.01:1 — it does not
  read as an object at all (GAP-N2). Interim: the bar sits inside
  the recess band (where its edge is the recess's hairline) or on
  the raised running card (1.36:1 ✓). It never floats on ground.
- Peek band: recess bed, draining fill raised → sheet as the band
  empties is *not* used — fill stays raised (visible against recess,
  lawful) and length alone carries the drain; tone changes would be
  a second rendering of the same fact.
- Emergency hold: ring track recess, progress arc glyph `#EDE3D8`,
  numerals primary. **Zero accent** — the hold is not a commit, and
  colouring the exit like the Deck slide would advertise it all
  session. Alert appears only if a protection actually drops
  mid-session (the WARNING state), as one line + one dot, per the
  alarm discipline — the only alert expenditure on this screen.

### 2.5 Overlays (blocked-app, airlock/peek, session-end, sheets)

**Verdict: expresses, with GAP-N3/N4 on every sheet-borne overlay.**
- Blocked-app overlay: a flat wall of ground `#171412`, full opacity,
  replacing the blocked app — no scrim token is needed because
  nothing of the app behind is shown (a translucent scrim would need
  a tone the staircase doesn't have *and* would imply the app is
  still present; the wall is both lawful and more honest). Remaining
  numeral primary, the one human sentence secondary, state word
  primary — all on ground ✓. Zero accent: nothing here is committable.
- Airlock/peek overlay: same wall; countdown hero primary; the peek
  band per §2.4.
- Hold-to-break overlay: per §2.4 — glyph ring, primary numerals,
  no accent, no alert unless a real WARNING is active.
- Session-end receipt: a sheet (GAP-N3/N4 interim) — fact rows in
  primary on the sheet or carved in recess rows, footer line primary;
  **no secondary or muted text anywhere on the sheet** until GAP-N4
  is ruled. Dismiss control: outline + glyph, or recess-carved —
  never a filled cap, because no tone above the sheet exists.
- Toasts/transient notices: sheet cap + primary text (5.87:1 ✓) —
  the one small floating object the staircase fully expresses.

### 2.6 Chrome (nameplate, header, bottom nav)

**Verdict: expresses, with GAP-N5 on the nav capsule.**
- Nameplate on ground: wordmark glyph/primary, war line secondary —
  printed, flat, no pill, no fill ✓. The DAY/NIGHT pill is dead;
  there is no theme control anywhere in chrome, because there is no
  theme *choice*: the routine decides (§2.7).
- Bottom nav: capsule = **raised** `#362C27` (1.35:1 off ground ✓);
  selected pill = sheet nested inside it (sheet vs raised 1.38:1 ✓ —
  the staircase's one lawful three-level nest: ground → raised →
  sheet). Selected icon + word = glyph/cream on sheet ✓.
  **Unselected icons + words on the raised capsule must be secondary
  (4.28:1), never muted (2.60:1)** (GAP-N5). Nav counts, if any
  survive, ride the war line — no badge surfaces are needed, so no
  gap is opened for them.
- Header status (held pips, guard segments): geometry in glyph/
  secondary on ground ✓ — pips and segments are shapes, and shapes
  are the staircase's native language.

### 2.7 Theme switching — the rule that makes all of the above true

- The scheme is a function of **his routine windows as he set them**
  plus one override: **a running session forces night at any hour.**
  Outside a session, the SLEEP window he configured carries night;
  the app never guesses his bedtime from the clock alone, and never
  asks him with a pill.
- A scheme change while the app is **backgrounded or on AOD** is a
  cut at the boundary — he isn't looking; nothing to transition.
- A scheme change while the app is **in his hand** is a 600 ms
  crossfade of surface tones, **deferred until any in-flight gesture
  ends** — never mid-drag on the slide, never mid-hold on the
  emergency break. A man holding a 60-second hold does not get his
  screen repainted at second 41.
- First frame after launch is always the resolved scheme (§1.6).
  There is no async flip after launch, at any hour, in either
  direction — a day-flash at 03:30 is the single worst frame this
  app can ever draw.

### 2.8 GAP REGISTER — to Sana / Ren. No hexes invented; interim rules use locked tones only.

- **GAP-N1 — Recess ↔ ground adjacency fails the ≥1.35:1 law as
  measured: 1.05:1.** The recess is a real luminance step down
  (0.0045 vs 0.0073) but at these depths the ratio compresses to
  near-invisibility. *Elements:* wheel well, slide track, loadout
  track, app-sled tracks, horizon strip bed, peek band, dial face —
  i.e. nearly every carved element in the app. *Interim rule
  (buildable now):* a recess never borders ground naked — it carries
  a 1px outline hairline (`#807A7A`, 4.35:1 on ground) on its
  light-catch (top) edge, and glyph content inside; or it is nested
  against raised/sheet, where it passes unaided (1.42:1 / 1.96:1).
  *Ruling needed:* is ≥1.35 an upward-only law, or does the recess
  get a deeper token? I do not invent the deeper hex.
- **GAP-N2 — Block bar `#161311` has almost no lawful seat.** Vs
  ground 1.01:1, vs recess 1.04:1, vs true black 1.14:1 — it reads
  only on raised (1.36:1) or sheet (1.88:1). *Elements:* the session
  block bar wherever it was drawn on ground; any sled track or nav
  surface built from it. *Interim:* block bar only seated on
  raised/sheet, or edged by the outline hairline; nav capsule uses
  raised, not block bar (§2.6).
- **GAP-N3 — No super-sheet step.** `#4A423C` is the top of the
  staircase; a control that must sit *proud on a sheet* has no tone.
  *Elements:* routine editor, Tasks composer, session-end receipt —
  their keys, caps, and dismiss controls. *Interim:* on-sheet
  controls are carved (recess field) or outline-only; the only
  filled on-sheet control permitted is the COMMIT accent.
- **GAP-N4 — No secondary text tier exists on a sheet.** Secondary
  on sheet = 3.10:1, muted on sheet = 1.88:1 — both unlawful for
  body text. On a sheet, only primary (5.87:1) and glyph (7.76:1)
  may carry words. *Elements:* sheet subtitles and meta lines,
  receipt fact rows, composer hints. *Interim:* quiet text moves
  into recess fields on the sheet (secondary passes there, 6.07:1)
  or is promoted to primary; nothing is set in secondary/muted
  directly on `#4A423C`.
- **GAP-N5 — The muted tier collapses off-ground.** 3.51:1 on ground
  (decorative only, already), 3.68:1 on recess, **2.60:1 on raised,
  1.88:1 on sheet**. *Elements:* unselected nav labels on the raised
  capsule, state words or captions on sheet caps, sub-step/meta text
  on the raised active task row, hub-adjacent labels anywhere off
  ground. *Rule (no ruling needed — arithmetic, not taste):* muted
  lives on ground and recess only; on raised the floor is secondary
  (bold/large only); on sheet the floor is primary/glyph. Any plan
  element that puts muted on a cap or card is non-compliant as drawn.

- **GAP-N6 — PEEK has no lawful mid-fill, and arithmetically cannot
  get one between raised and sheet.** Raised→sheet is a single
  1.38:1 step; no tone can sit between them and keep ≥1.35:1 against
  both. The only brighter candidate, muted `#776A60` borrowed as a
  fill (Iris's G3), measures lum ≈0.150 — **2.6× the sheet cap's
  luminance** — and would make a half-state the brightest fill on
  any night screen. Ruled in §2.9: PEEK differentiates by word +
  position on an existing step, never by its own fill.

Everything not in this register passed: the staircase expresses the
Deck, Shield, Tasks, the running face, the overlays, and the chrome —
provided the recesses wear their hairline and the quiet text stays
on the surfaces where it is lawful.

### 2.9 Night-seat rulings on Iris's two calls (mailed to Iris, thread: night-peek-and-break-spent)

1. **PEEK borrowing muted `#776A60` as a fill — SPLIT.** As *marks*,
   approved: the airlock top rule (≤2dp) and the legend swatch (≤8dp)
   are line-work, and muted already holds the glyph job — a line in
   muted is glyph-duty, not a fill. As an *area fill* (sled cap,
   chip, any surface a thumb reads as a state plate), **rejected**:
   lum ≈0.150 breaches the fill cap in spirit and in fact (see
   GAP-N6). PEEK area state = raised fill + glyph verb word; SHUT /
   PEEK / OPEN separate by position and word on existing steps.
2. **`#FF8A5C` BREAK SPENT word — APPROVED, narrowed.** Word only,
   never a fill, only Iris's two placements (emergency overlay on
   completion; session-end word when a session ends by break),
   ≤2% area, and it is the *only* alert expenditure on its surface —
   where BREAK SPENT prints, no other alert dot or line coexists.
   Boundary with my territory: it never appears on the AOD, where
   its luminance (0.402) breaches the 0.25 ceiling; the AOD finish
   step stays `#C74814` (§1.7). An irreversible spend is the one
   fact form alone undersells at 03:30 — the exception is earned,
   and it stays exactly this small.

---

## 3. THE 03:30 TEST — the Deck at wake-up

The acceptance scene. He slept at 20:00. The room is dark; his eyes
have been dark-adapted for seven and a half hours. The phone is
face-up on the desk where he left it.

**Before he touches anything (second 0).** The idle AOD is at Stage 2:
wall clock `03:30` at 104sp, lum 0.130 — the brightest thing in the
room and still dimmer than a candle's effect on a wall. Below it,
`NEXT 05:00 · STUDY` in the second slot, and the hollow ring dot:
a routine is armed. He knows the time, the machine's next move, and
that it is set — without picking the phone up, without a photon more
than the ceiling allows. If the AOD were brighter, this moment would
hurt; if it said more, this moment would be a dashboard. It says two
things and a dot.

**He picks it up and unlocks (seconds 0–1).** First frame: night
ground, resolved before the frame drew — the SLEEP routine window
carries the scheme, there is no day flash, no white splash. Window
brightness **ramps**: it opens at the AOD floor and eases to his set
night level over **2.5 s, ease-out**. Dark-adapted eyes are not hit
with the full 11:1 primary tier in one frame; the ramp is the app
respecting the physiology it was built around.

**The Deck, first 3 seconds — where the eye lands, in order:**

1. **The total readout.** 72sp, primary tier (10.95:1), the only
   large high-contrast object on the screen. It shows the duration
   the *rested* user last set — the Deck idles armed-ready, so this
   number is his standing decision, not a question. First fixation,
   ~300 ms: *how long*. Nothing competes for this fixation: chrome
   is secondary-and-below, the nav capsule is a raised whisper with
   no filled pill shouting, the mode pill is dead, there are no
   badges. If anything in chrome ever outranks this number, the
   test fails — that is the acceptance criterion, stated in advance.
2. **The slide-to-arm sled.** His thumb is already moving; the eye
   checks the hand's target second. Sheet cap, primary label —
   found by position (bottom, thumb arc) as much as by tone. The
   accent is **not yet on screen**: orange exists only once the
   sled passes its gate, so at second 2 the screen holds zero
   saturated colour, and the one action is identified by place and
   step, not by flood. *The act*, second fixation.
3. **The NOW task line, and FINISHES at its tail.** One saccade
   across the task line: title in primary (*what*), FINISHES in
   secondary (*until when*). He reads both while his thumb settles
   on the sled. Third fixation completes the picture the arm needs:
   duration, act, task, end time.

**At second 3** he knows everything the morning requires and has
decided nothing new — the rested user decided; the tired user is
executing, which is the whole design. The screen he did it on was
~90% ground by area, emitted one hero number, no orange, no fills
above the sheet cap, and never once asked him to read anything
smaller than the body tier or quieter than secondary-on-ground.

**Named failure modes, so the test can actually be failed:** a day
scheme on the first frame · a brightness step instead of the ramp ·
any chrome element (badge, count, pill) fixated before the readout ·
the accent visible before the gate · muted text carrying anything
he needs in the first 3 seconds · the wheel well or slide track
invisible because GAP-N1's hairline rule was skipped, leaving him
hunting for the sled by memory instead of by sight.

---

## 4. Decisions taken in this file (for Sana's merge)

1. AOD readings settled with Pit: REMAINING + ELAPSED standing, bar
   as geometry, task transient, NEXT idle-only, total never printed.
   My wave-1 large-swap is killed by my own hand.
2. AOD finish uses `#C74814` for the dot's one step up — the wave-1
   alert `#FF8A5C` (lum 0.402) breaches the 0.25 ceiling and is
   rejected on my own law.
3. Deflection does nothing to the AOD. Finish does exactly one
   60-second step. Routine firing does nothing visible.
4. Five gaps registered (N1–N5), none papered over with an invented
   hex; each carries an interim rule in locked tones so the build
   is never blocked on the ruling.
5. Muted-off-ground is banned by arithmetic (N5) — this will bite
   other seats' elements (nav labels, cap captions, active-row
   meta); they should check their files against the §0 table.

— Nacht, Night seat, Designers cohort
