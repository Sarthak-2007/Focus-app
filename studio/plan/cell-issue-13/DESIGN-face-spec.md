# DESIGN — THE SESSION ROOM FACE SPEC (MERGE FORM)
## Cell issue-13 · Seat: VELA, Designer · Lead: Wacht
## Issue: Sarthak-2007/Focus-app #13 — Session Room merge
## (Klinge's 19 panel items + Wacht's audit items A-1…A-12, onto main)

Filed: Oct 30, 2026. Design only — no code was touched by this seat.
Target tree: `builds/cells/issue-13` (branch `agent/issue-13`,
base = batch-9 tree, DB v12). Verified against the lane face at
`builds/flow-clock/Focus-app/app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt`
(cited below as `Lane:NNN`).

Sources, in authority order: Ren's binding rulings as handed to this
cell (V1 amended · E-1 veil + Landing RATIFIED · E-4 confirmed, §C-2
stands unamended · V6 STRUCK · M-4 keep-awake amended per A-10(v)) ·
`studio/plan/AUDIT-session-room.md` §1 (state inventory), §3
(A-1…A-12), §4 (escalations as ratified above), §7 (proof debts) ·
the panel's cured-face grammar the audit cites as §C-1/§C-2/§C-3 and
L1/L7 · Salz's light desk (`REFINE-salz-aod.md`) for every luminance
figure. Where the audit leaves a size or seat open, this spec fixes
it and marks the line **[VELA]**; everything else is ruled text,
traced to its item. Nothing in this spec adds a verb to the read
face, a surface, or a DB change (audit §3 price total stands).

---

## §0 · THE FACE, AS ONE OBJECT

**Ground.** True black `#000000` in every state, every hour. OLED
pixels off. No surface, bed, card or trough ever stands under any
print on this face (see §8, A-7(iii)).

**Ink.** One ink only: `NightColorPrimary` `#FFE4CF`
(L = 0.8125 at α 1.00 over black). Every alpha below is an alpha
of this ink over the black ground. Computed composites (Salz's
method, WCAG sRGB, re-run by this seat):

| α | L (over black) | Contrast vs black | Used by |
|---|---|---|---|
| 0.58 | ≈ 0.242 | ≈ 5.8 : 1 | exit line, standing |
| 0.55 | ≈ 0.216 | ≈ 5.3 : 1 | hero / small lines, standing |
| 0.50 | ≈ 0.176 | ≈ 4.5 : 1 | small-line descent FLOOR (A-2) |
| 0.40 | ≈ 0.110 | ≈ 3.2 : 1 | exit line, fully descended (A-2) |
| 0.36 | ≈ 0.088 | ≈ 2.8 : 1 | hero / tear rule, fully descended (A-2) |

**Column, top to bottom — the only seats that exist:**

1. **EXIT LINE** — one line, top seat. Constant. (§7, A-6)
2. **HERO CELL** — label line / hero / sub-line, bound in one tight
   cell (label and figure inseparable, K-8). (§1 per state)
3. **BLOCK LINE** — one line, under the hero cell. Timed,
   multi-block, non-break only. (§2, A-1)
4. **TEAR RULE** — full content width, under the block line seat
   (the seat stands empty in states with no rule). (§8, A-7)
5. **BANNER SLOT** — fixed two-line height while a session runs;
   the face's one sentence stands here, or the slot stands empty.
   (§6, A-5)

**Type.** Hero: Anonymous Pro Bold, 96 sp, letterSpacing −3 sp.
Label line (`LEFT` / `ELAPSED` / `THE TIME NOW`): Anonymous Pro
Bold, 11 sp, letterSpacing +2 sp, directly above the hero.
Sub-line and block line: Anonymous Pro Bold, 13 sp,
letterSpacing +2 sp. Exit line: Atkinson Hyperlegible Bold, 11 sp.
Banner / break line: Atkinson Hyperlegible Bold, 15 sp (A-5's
measure is taken in this size — §6). All print is one ink; no
element on this face ever takes the accent/container tone.

**Burn-in glide.** The whole column steps on the 5×5 grid,
dx, dy ∈ {−6, −3, 0, +3, +6} dp, one step per minute, applied at
the column offset only (Mond J3's condition: the glide lives only
at the column offset, so the tear rule's dashes can never moiré
against a second grid). KEEP as built.

**Standing alphas (f = 0, §3).** Exit 0.58 · hero 0.55 · label
0.55 · sub-line 0.55 · block line 0.55 · tear rule 0.55 ·
banner/break line 0.55. These are Salz's cured constants; the
auto-raise ships only under them.

---

## §1 · STATE INVENTORY — EVERY STATE, EXACT PRINT

Times: `HH:MM` is 24-hour wall time, zero-padded. Hero grammar
(L1): remaining/elapsed ≥ 60 min prints `H:MM` (hours unpadded,
minutes zero-padded, no seconds); < 60 min prints `MM:SS`
(minutes and seconds zero-padded). The hero never prints total
minutes ≥ 100 and never prints seconds at ≥ 1 h. One instant
never prints twice on this face (A-1(c)'s law, generalised).

### 1.1 IDLE — hand-raised, no session

| Seat | Print | Alpha |
|---|---|---|
| Exit line | `NIGHTSTAND DESK CLOCK • TAP ANYWHERE TO EXIT` | 0.58, never descends (no span) |
| Label | `THE TIME NOW` | 0.55 |
| Hero | `HH:MM` — the wall clock now | 0.55 |
| Sub-line | — none — | |
| Block line | — none — | |
| Tear rule | — none — (no span, no fraction) | |
| Banner slot | — NO SLOT AT ALL — idle wears no slot (A-5); and no banner in any case (A-8: the banner derives only inside a live session; a queued-but-not-running task names nothing here) | |

KEEP as a state (audit §1). The idle hero is a clock and wears
its label inseparably; it never prints a dial setting.

### 1.2 ARMED-UNDECIDED — sealed, settle not yet fired

**No prints at all.** The face is not composed. This state's
numbers are machinery and never reach print (Nox A-N7, KEEP):
the settle is 20 s from max(seal, last interaction); V1 as
amended by Ren (E-2 ratified) samples ~50 Hz
(`SENSOR_DELAY_GAME`) **only while armed-undecided**, band energy
7–14 Hz, three-state verdict — REST (tremor band at the noise
floor) raises at once; HELD (band above floor, however still the
orientation) suppresses BOTH the sensor path and the time path
while the signature persists (sampler duty-cycled 6 s in 30 s
once HELD persists); UNKNOWN (bag, car, no signature either way)
and sensor-absent take the 20 s backstop, alone. A dismissal is
persisted (V2 + M-4): process death never resurrects a dismissed
face. When the raise fires, arrival is cause-keyed per §5 (A-4):
a V1 set-down fire is ENTRY, WATCHED.

### 1.3 RUNNING, TIMED — the standing state

| Seat | Print | Alpha (standing → descended, §3) |
|---|---|---|
| Exit line | `NIGHTSTAND DESK CLOCK • TAP ANYWHERE TO EXIT` | 0.58 → 0.40 |
| Label | `LEFT` | 0.55 → floor 0.50 |
| Hero | span remaining, L1 grammar (`H:MM` / `MM:SS`) — the SPAN, never the per-block remainder | 0.55 → 0.36 |
| Sub-line | `NOW HH:MM · ENDS HH:MM` | 0.55 → floor 0.50 |
| Block line | §2 (A-1) form; absent when n = 1 **[VELA: a one-block span's block IS the span; the SLEEP ground in §1.7 generalises — Wacht to confirm at the merge table]** | 0.55 → floor 0.50 |
| Tear rule | §8 (A-7): spent solid, unspent dashed, notches at block boundaries | 0.55 → 0.36 |
| Banner slot | fixed height (§6); the face's one sentence — task title, else declared intention, else the slot stands EMPTY. No default name exists. | 0.55 → floor 0.50 |

ENDS derivation (A-12 C-1, answered at the merge): for
routine-born rows ENDS derives from the row's frozen
`windowEndMillis` whenever the span target can differ from the
window — the face promises the end the stand-down will keep;
for hand rows, ENDS = now + spanRemainingSeconds, the same
instant by construction. ENDS prints in the sub-line at ALL
hours of a timed span (cured §C-1) — it is not a last-minute
promotion out of the top line.

### 1.4 RUNNING, STOPWATCH — the most honest state (Nox A-N4)

| Seat | Print | Alpha |
|---|---|---|
| Exit line | constant, as §1.3 | 0.58 → 0.40 |
| Label | `ELAPSED` | 0.55 → floor 0.50 |
| Hero | elapsed, L1 grammar | 0.55 → 0.36 |
| Sub-line | `NOW HH:MM` — alone (A-6(ii)). No ENDS: a stopwatch has no set end, and an ENDS derived from elapsed is forbidden | 0.55 → floor 0.50 |
| Block line | — none — | |
| Tear rule | — none — a mode with no target has no fraction; no strip of any kind is composed | |
| Banner slot | as §1.3 | 0.55 → floor 0.50 |

No descent variable exists for a stopwatch (no span); its alphas
stand at the standing constants all night **[VELA — the audit
names the descent variable span-elapsed fraction; a stopwatch
has no span fraction, so nothing descends. Flagged for Wacht.]**

### 1.5 BREAK — inside a timed span, Safe Break live

Hero, label, sub-line and tear rule keep counting the SPAN,
unchanged (§1.3) — the break is a region of the span, not a room.

| Seat | Print |
|---|---|
| Block line | **SUPPRESSED** for the whole break (A-1(a)) — the per-block remainder is 0 through every break and prints nothing |
| Banner slot | the BREAK LINE owns the slot: `BREAK — PEEK APPS OPEN · UNTIL HH:MM` (cured §C-3; A-11(c)'s single break-sentence derivation — the same stored break-end the glass and the shade read). `HH:MM` = the break's end in wall time. No minutes figure, no ceiling arithmetic, no "DOORS OPEN": only the PEEK tier opens, and the sentence says exactly that (§5 of the audit: §C-3's wording STANDS). |
| Everything else | as §1.3, alphas per §3 |

### 1.6 LAST MINUTE — span remaining ∈ (0 s, 60 s], timed only

| Seat | Print |
|---|---|
| Exit line | **UNMOVED, unchanged** — the constant line keeps the top seat through the finish (A-6). The verb is never displaced in the minute a finishing man most likely taps |
| Sub-line | `ENDS HH:MM` — alone. NOW drops (§C-1's promotion). ENDS prints exactly once on the face |
| Hero | `MM:SS`, counting seconds (L1 grammar under 60 min) |
| Block line | final-block form only: `BLOCK n OF n` (A-1(c)); suppressed if in a break |
| Alphas | the DAWN RAMP governs (§4, A-3): every descended alpha is lerping back to its standing constant, arriving exactly at T-0 |
| Tear rule / banner | as §1.3 / §1.5 |

Proof owed: the 30-seconds-remaining frame — exit line on top
AND `ENDS HH:MM` alone in the sub-line, one frame (audit §7.2).

### 1.7 SLEEP, CHARGER — routine-born SLEEP span, plugged in

The timed state (§1.3) under V4's descent (§3), with three
differences, all ruled:

- Banner slot prints `SLEEP` — the routine row's own name, no
  task/intention derivation stands in front of it (Moench).
- **No block line, no notches** — routine rows materialise with
  studyBlocksCount = 1; their absence is correct by construction
  (Moench MA-1's ground), not a missing print.
- ENDS derives from `windowEndMillis` (§1.3, A-12 C-1) — on this
  span ENDS is `03:30`, the stand-down's own instant.

The span's interior is structurally silent: no breaks exist
inside SLEEP, so no break line ever prints here (Moench MA-1…5).

### 1.8 SLEEP, BATTERY — unplugged

As §1.7 until the yield (A-10(v), §9), then **the face prints
NOTHING — zero figures, by law, not by defect** (Nox). Ground
stays true black; print has already faded to 0.00 in the room's
own curve before the flag clears (§9), so dark and off are
indistinguishable. Blocking holds underneath the dark (Moench).

**Wake:** a wake-tap re-raises the face. The wake is NOT a
dismissal and never writes the dismissal bit. The re-raise is
ENTRY, SEEDED (§5): frame one at full lawful alpha, no fade. It
stamps the interaction clock explicitly, opens at the standing
alphas, and the descent suspends 60 s before resuming from the
span-progress value (A-2(iii)). V1's sampler RE-ARMS at the wake
(A-10(v)) — the wake re-raise is built with the fork closed, not
time-only.

### 1.9 STAND-DOWN — 03:30, the SLEEP span ends

Not a face state — an act (A-10), choreographed in §5
(EXIT, FINISH) and §10 (the veil). On this face it prints
nothing new: no ceremony sounds or pulses for the kind
ROUTINE_SLEEP_STANDDOWN (silence gates the CALLS, so the haptic
half dies with the audio), no receipt stands (routine-born
receipts are born filed), and the print leaves over a held
black ground — never over a lit Deck (A-4's light pin: no
surface brighter than the clock stands between the clock and
the receipt). The standing surface after stand-down is E-1's
question, answered in §10.

### 1.10 RETURN

- **Hand dismissal** (tap anywhere, any state): EXIT, DISMISSAL
  (§5) → the Deck. By day: the Deck at full light, Nacht A-N3
  stands whole. **By night: the LANDING** (E-1 ratified, §10) —
  the Deck at the room's light law, first verb blooms it.
- **Machine return** (finish, stand-down): EXIT, FINISH (§5) →
  the receipt over held black; at night after a routine finish,
  the Deck underneath wears the VEIL (§10) until first touch.

---

## §2 · A-1 — THE BLOCK LINE, RULED FORM

Seat: §0 seat 4, under the hero cell. One line, 13 sp.

- **k < n:** `BLOCK k OF n · UNTIL HH:MM` — the figure is the
  block's end in wall time. UNTIL is the house's phase-end word
  (the break's, §C-3); ENDS belongs to the span alone and never
  prints on this line.
- **k = n (final block):** `BLOCK n OF n` — alone, no figure.
  The final block's end IS the span's ENDS, already standing in
  the sub-line; one instant never prints twice. (This subsumes
  last-minute suppression at all hours.)
- **During a break:** suppressed entirely (§1.5). The line never
  prints a remaining-minutes figure in any state — the
  ceiling-minute form (`n MIN LEFT` / `0 MIN LEFT` through a
  break) is dead on this face.
- **Stopwatch, idle, SLEEP:** absent (§1.4, §1.1, §1.7).
- Alpha: standing 0.55, descent floor 0.50 (§3) — it carries
  figures, so it is a number-carrying small line under A-2.

## §3 · A-2 — DIM BY AREA: THE DESCENT, ITS FLOORS, THE DOOR

**The variable is named:** `f` = span-elapsed fraction =
spanElapsedSeconds / spanTargetSeconds, f ∈ [0, 1]. No other
input moves any alpha on this face. (E-3's night-window
extension is ruled for the merge AFTER this one and is not in
this spec.)

**Descent curves** (linear in f; the descent and the glide share
520 Curve C's character — one room deepening, Nacht A-N4):

| Element | Standing (f = 0) | Fully descended (f = 1) | Law |
|---|---|---|---|
| Hero | 0.55 | 0.36 | α = 0.55 − 0.19·f |
| Tear rule (incl. notches) | 0.55 | 0.36 | α = 0.55 − 0.19·f |
| Sub-line | 0.55 | **0.50 FLOOR** | α = max(0.55 − 0.19·f, 0.50) |
| Block line | 0.55 | **0.50 FLOOR** | same |
| Banner / break line | 0.55 | **0.50 FLOOR** | same |
| Label line | 0.55 | **0.50 FLOOR** | same **[VELA: the label carries no figures; floored with its cell so label and sub-line never split tiers mid-descent]** |
| Exit line | 0.58 | **0.40** — EXCEPTED from the floor (A-2(i)) | α = 0.58 − 0.18·f |

The 0.50 floor is legibility law, not taste: α 0.50 composites
to L ≈ 0.176, contrast ≈ 4.5 : 1 over the black ground. Below
it, a number too dim to read is a number withheld.

**THE DOOR INVARIANT** — pinned beside the constants, in ratio
form, at EVERY span fraction:

> L(exit line) ≥ 1.10 × L(hero print)

Checked at the endpoints with this spec's constants: standing,
0.242 / 0.216 ≈ **1.12** ✓ (margin is thin — the exit/hero pair
0.58/0.55 is a minimum, and any future brightening of the hero
breaks the door before it breaks the ceiling); fully descended,
0.110 / 0.088 ≈ **1.24** ✓. The alpha-difference form (≥ 0.04)
is NOT the invariant — the standing pair violates it and both
seats acquitted the pair; the ratio is what the arithmetic
proved (A-2(iv)).

**Restore moves to the raise (A-2(iii)).** There is no
interaction-restore trigger on a one-verb face — every touch
dismisses. Ruled instead: any raise or re-raise (hand chip,
settle, cold open, battery-yield wake) opens at the STANDING
alphas; the descent suspends for 60 s; it then resumes from
the span-progress value of f at that moment (it does not restart
the descent, and it does not snap — resumption is from the
current f, reached under 520 Curve C's character).

## §4 · A-3 — THE DAWN RAMP

In the final 60 s of a timed span (the same window §1.6
promotes ENDS), every descended alpha lerps back to its
standing constant, reaching full exactly at T-0:

> for remaining r ∈ [0 s, 60 s]: α(r) = α_descended(f) +
> (α_standing − α_descended(f)) × (60 − r) / 60

At r = 60 s the face is at its descended value; at r = 0 every
element stands at §0's standing constants (hero 0.55, exit 0.58,
small lines 0.55, rule 0.55). Information and legibility peak
together. The descent and the ramp are one gesture: down across
the night, up at the end. The ramp governs alpha only — no
string changes except §1.6's own.

## §5 · A-4 — ARRIVAL AND EXIT, KEYED TO CAUSE

Curves (MonolithCurves, exact beziers): Curve A =
cubic-bezier(0.2, 0, 0, 1) · Curve B = cubic-bezier(0.05, 0.7,
0.1, 1) · Curve C = cubic-bezier(0.3, 0, 0.8, 0.15) ·
EasingThrow = cubic-bezier(0.5, 0, 1, 0.4). "Ground" = the
true-black full-screen ground; "print" = the whole column.
**No-blend law (M-1) binds every arrival: print never fades in
over a ground that is not yet fully black.**

| Cause | Witness | Ground | Print | Total |
|---|---|---|---|---|
| ENTRY, UNWATCHED — machine fire ≥ 10 s after last interaction (settle into an untouched phone) | settle machine, lastInteractionMono | 260 Curve A | 520 Curve C, starting at +140 ms | ≈ 660 ms — a held breath, acquitted for exactly this witness state |
| ENTRY, WATCHED — any machine fire < 10 s after last interaction, incl. V1's set-down fire, eyes on | settle machine, lastInteractionMono | 180 Curve A | 420 Curve B, starting at +180 ms — print lands only on completed black | 600 ms |
| ENTRY, HAND — the DESK CLOCK chip | the chip's tap | 180 Curve A | 260 Curve A, starting at +180 ms | 440 ms — a door that answers, without a ghost |
| ENTRY, SEEDED — cold open into a live session; the battery-yield wake return | row predates process birth / wake path | — | **NO FADE.** Frame one at full lawful alpha (standing alphas, §3 restore applies). Window background on the seeded path is `#000000`; no-flash proof is captured from process start | 0 ms |
| EXIT, DISMISSAL — hand tap | the tap | 180 EasingThrow, ground and print together (item 17 stands) | (same) | 180 ms |
| EXIT, FINISH — span end | finish edge | print fades 260 Curve A; the true-black ground HOLDS over the Deck through the detention (E0 → issue, ≤ 1,800 ms); the ground then fades 260 Curve A as the receipt fades in, 280 Curve C | (as stated) | seam, not a flash |

**The face fades only on its first arrival in a session**
(Mond's law): every later appearance in the same session —
re-raise after dismissal is by hand or not at all (a dismissal
is a decision, §1.2); the seeded and wake paths never fade.
Item 19's pin, extended from motion to light, binds the finish:
**no surface brighter than the clock stands between the clock
and the receipt.**

## §6 · A-5 — THE BANNER SLOT HAS ONE HEIGHT

While a session runs, the banner region's height is FIXED at
the two-line measure: **15 sp × 2 lines + the slot's standing
padding**, text top-aligned in the slot. The banner present,
the break line present, or the slot empty — the column's
geometry does not move by a single dp between them. K-6's
instant swaps stand (the charge was the height, never the
swap). Idle wears no slot (§1.1). The banner is a BARE LINE —
no card, no background box, no rounded bed stands behind it;
its bed would be a lit surface under print and a second height
grammar. The slot's emptiness is stillness, not absence of
design: it is the same discipline as the fabricated name's
death — nothing prints, and nothing moves.

## §7 · A-6 — SEATS, SETTLED IN WRITING

- **The top line is constant.** Its seat holds exactly one
  string in every state and every minute:
  `NIGHTSTAND DESK CLOCK • TAP ANYWHERE TO EXIT` — §C-2's
  standing line, unamended (E-4 confirmed; W-35 KEEP). A-6's
  quoted constant `"TAP ANYWHERE TO EXIT"` is this line's verb
  and its operative content: **no conditional ever stands in
  this seat** — no state, no minute, no derivation replaces,
  prefixes or displaces it. (This seat records the reading
  plainly so the merge does not have to guess: if Wacht reads
  A-6's quote as the whole string, the `NIGHTSTAND DESK CLOCK •`
  prefix dies and nothing else in this spec changes.)
- **ENDS lives in the sub-line, only** (§1.3, §1.6). The
  top-line seat for an ends-line is DELETED, not amended. In
  the last minute the face prints ENDS exactly once (sub-line)
  and the exit verb exactly once (top line) — never ENDS twice,
  never the verb lost.
- **Stopwatch sub-line:** `NOW HH:MM` alone. **Idle:** no
  sub-line. (§1.4, §1.1.)
- No other element changes seat in any state. The column order
  of §0 is the whole of the seat law.

## §8 · A-7 — THE TEAR RULE, BOUNDED AND GROUNDED

The rule replaces every progress-strip construction. It is a
tear line across the full content width, read as: **solid is
spent, dashed is still attached, the gaps are the blocks**
(the dialect is taught on the reception sheet, E-4 — the room
itself stays silent).

- **Fraction:** tear fraction = span-elapsed fraction f,
  clamped `coerceIn(hair, 1f)` — item 8's upper clamp added:
  elapsed can run past target in a delayed-finish window while
  the hero sits at 0:00, and the rule never prints past full.
  The lower bound is the hair itself: at f = 0 the rule prints
  at hair weight, honestly empty — no fabricated floor
  fraction.
- **Construction [VELA, from Mond J2/J3 + the teaching line]:**
  the spent portion (left of f) is a solid hairline; the
  unspent portion is dashed on one rigid grid (dash module =
  the hair's own module); the whole rule rides the column's
  burn-in glide at the column offset only, so dashes stay
  soft-edged and never double.
- **Notches (V3, finished):** at every block-boundary fraction
  of the span (k/n boundaries, k = 1…n−1), a gap of **3 dp
  wide, full rule height**. Notches take **no alpha of their
  own** — the gap inherits the descent by being ground, not
  print. A 1 dp notch at α 0.36 antialiases into a thinning
  and is forbidden: 3 dp or nothing.
- **The gaps are GROUND:** true black `#000000`, by
  construction. No bed, track, trough or surface colour may
  exist under the rule anywhere — if a `#100E0C`
  (NightColorSurfaceVariant) bed survives under it, every
  notch is filled with lit pixels and "true-black gaps" is
  false by construction. Construction check at the merge,
  zero price (A-7(iii)).
- **Alpha:** the rule descends with the hero, 0.55 → 0.36 (§3).
- **Absent:** idle, stopwatch, armed-undecided (§1). Present
  through breaks (the span counts through them) and through
  the last minute (under the dawn ramp, §4).

## §9 · KEEP-AWAKE, AS THE FACE FEELS IT — A-10(v) / M-4 AMENDED

Ownership of `FLAG_KEEP_SCREEN_ON` MOVES from this face's
composition to the VM's session machinery, where plug, settle
and finish state live (Ren's ruling; the face spec states only
what the man sees):

- **Surrender deadline** = max(settle instant, last interaction,
  last charger disconnect) + 10 min, on battery. An unplug at
  03:00 no longer kills the face at a deadline hours past. A
  plug-in before the deadline cancels the yield; after it, it
  does not resurrect (resurrection is the wake path's job, §1.8).
- **The going-to-sleep:** at true-off − 520 ms (true-off derived
  from the readable system screen timeout), the print fades
  **520 Curve C to α 0.00** over the black ground; the flag then
  clears into OLED black, where cut and off are
  indistinguishable. The room does not vanish; it goes to sleep
  in its own curve, reversed.
- Charger in: no yield, ever, while plugged (§1.7).

## §10 · E-1 RATIFIED — THE VEIL AND THE LANDING (NIGHT RETURN)

V6 is STRUCK and stays struck: none of this is a face skin; it
is finish/return choreography, which is machinery.

- **THE VEIL (machine return, night, routine finish only):**
  the Deck composes under a black scrim at **α 0.80** until
  first touch. Keep-awake is already cleared, so the OS timeout
  darkens the veiled Deck if he never touches it.
- **THE LANDING (night dismissal):** a hand dismissal at night
  lands on the Deck at the room's light law — **α 0.55 over
  true black**; every control is live from the first touch; the
  first true verb blooms the Deck to full light over **520
  Curve C**.
- **Composed grammar:** machine return wears the veil → first
  touch unveils onto the Landing → first verb blooms.
- **lockNow is NOT in this merge** (coordinator's lean, carried):
  it converts "screen sleeps" into "phone locked" and every
  post-stand-down morning would begin with an unlock he does
  not currently owe. It returns only if the veiled Deck still
  wakes him.

---

## DISAGREEMENTS — THE LANE CODE AGAINST THIS SPEC

Verified seat by seat against
`builds/flow-clock/Focus-app/app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt`
(the lane face; line numbers are that file's) and, where the
machinery lives outside the overlay, `DeskClockSettle.kt` in
the same lane (marked ◆). Agreements are listed first so the
merge keeps what is true; every disagreement carries its
evidence. Severity is this seat's, in the audit's currency.

**Verified agreements (keep):** true-black ground
(`Lane:155`); standing constants 0.55 / 0.58 as constants
(`Lane:73–74`); idle hero = real wall time with `THE TIME NOW`
inseparably above it (`Lane:205–213`, `deskClockHeroText`
`Lane:311–318`); span (never per-block) hero derivation
(`Lane:314`); banner absent when neither task nor intention
exists (`deskClockBannerText`, `Lane:366 ff.`); burn-in glide
at the column offset only (`Lane:147–148, 164`); break line
replaces the banner during a break (`Lane:268–271`).

**D-1 · HIGH — ENDS squats in the exit seat; the sub-line
never carries it (violates A-6, §C-1).** The top line is
conditional: in the last minute `deskClockEndsLine(...)`
replaces the exit line (`Lane:177–186`), and
`deskClockEndsLine` (`Lane:332–340`) exists for exactly that
seat. The sub-line seat the audit rules as ENDS' only home
prints `deskClockNowLine` — `NOW HH:MM` and nothing else
(`Lane:224–232`, `Lane:323`) — at every hour, including the
last minute, where §1.6 requires NOW to drop and ENDS to stand
alone. Composed naively at the merge, one frame prints ENDS
twice and loses the verb in the minute a finishing man most
likely taps (audit §7, proof debt 2). The lane comment at
`Lane:168–176` even states the theft as design ("the exit line
gives its place to ENDS").

**D-2 · HIGH — the hero speaks total minutes; L1's H:MM
grammar does not exist (violates §1.3/L1).**
`deskClockHeroText` formats `"%02d:%02d"` of total seconds
(`Lane:315`): a 7½-hour SLEEP span opens at `449:59` and counts
through three-digit minutes all night; the lane's own test
enshrines `125:49` for a stopwatch. No H:MM switch, no
boundary frame at 3600/3599 (audit §7, proof debt 1, unpayable
against this formatter).

**D-3 · HIGH — the running hero is unlabelled (violates §1.3,
§1.4).** The label seat prints only at idle (`Lane:205–213`).
Running, nothing on the face says `LEFT` (timed) or `ELAPSED`
(stopwatch): the same `MM:SS`-dress carries opposite meanings
with no word to part them (Fremd F-2, carried by the panel).

**D-4 · HIGH — V4 does not exist in the lane: no descent, no
floors, no ramp (violates A-2, A-3).** Every print takes the
single constant `DESK_CLOCK_PRINT_ALPHA = 0.55f` (`Lane:73`;
hero `Lane:222`, label `Lane:212`, sub-line `Lane:231`, strip
`Lane:255`, banner `Lane:285`) and the exit line the single
0.58 (`Lane:74, 190`). There is no span-fraction variable, no
0.36 descent, no 0.50 floor, no 0.40 exit exception, no 60 s
raise-restore suspension, and nothing at all happens to alpha
in the final 60 s. The SLEEP face — the state's primary use —
stands at full standing alpha for 7½ hours. The door invariant
is untestable in this code because nothing moves.

**D-5 · HIGH — the "rule" is a lit-bed capsule bar; the tear
rule, dashes and notches do not exist (violates A-7).** A
16 dp box, `RoundedCornerShape(12.dp)`, filled by fraction
(`Lane:236–257`). Its track is
`.background(NightColorSurfaceVariant)` — `#100E0C`, L ≈ 0.004
(`Lane:242`) — the exact bed A-7(iii) names: were notches cut
in this construction, every gap would be filled with lit
pixels. There are no dashes, no notches, no 3 dp gaps, and the
fill's alpha never descends (D-4).

**D-6 · MEDIUM-HIGH — fabricated fractions (violates A-7(i),
§1.4).** Timed progress is `coerceIn(0.04f, 1f)` (`Lane:141`):
at the session's first second the bar prints 4 % already gone
— an invented figure under a comment claiming "every branch
below is a real derivation… or honestly empty" (`Lane:130–133`).
The upper clamp exists; the ruled lower bound is the hair, not
0.04. Worse, the stopwatch branch invents progress outright —
`elapsedSeconds / 1200f`, pinned full from minute 20
(`Lane:142–143`) — for the one state §1.4 rules must have NO
rule at all.

**D-7 · MEDIUM-HIGH — the block line does not exist (violates
A-1).** No `BLOCK k OF n` string, derivation or seat anywhere
in the file. A-1's whole form (UNTIL for k < n, bare
`BLOCK n OF n` at the final block, suppression in breaks) is
unbuilt, and with it both remainders-labelled (Neu's binding
list) stays half-done: the span is labelled (D-3 aside), the
block is silent.

**D-8 · MEDIUM-HIGH — the break line is the dead sentence
(violates §C-3, §1.5).** `deskClockBreakLine` returns
`"BREAK — DOORS OPEN · $minutes MIN LEFT"` with ceiling
minutes (`Lane:350–353`). §C-3's cured sentence —
`BREAK — PEEK APPS OPEN · UNTIL HH:MM`, from the stored
break-end — stands ruled in the audit's §5, and "DOORS OPEN"
is false as stated: SHUT apps, the sniper and blocked sites
stay locked through a break (Fremd F-3). The lane also derives
the figure from `safeBreakSecondsRemaining` alone; no
break-end wall time is ever computed on this face.

**D-9 · MEDIUM — one fade for every cause (violates A-4).**
The overlay's only motion is `fadeIn(specB())` /
`fadeOut(specA())` — 420 Curve B in, 260 Curve A out
(`Lane:88–92`) — for hand, settle, cold-open and finish
alike. Against §5: no cause is carried; no ground/print split;
the watched entry shows the ~400 ms black slab A-4 exists to
cure; the hand entry takes 420 ms, not 440 composed as
180 + 260; the SEEDED path fades, where the rule is NO fade,
frame one at full alpha; the dismissal exit is 260 Curve A,
not 180 EasingThrow; the finish exit has no held black ground
and no receipt seam at all.

**D-10 · MEDIUM — the banner slot has every height (violates
A-5).** The banner is conditionally composed (`Lane:272–288`):
absent, the column loses the whole box and everything below
the strip jumps; present, it is a card —
`NightColorSurfaceBright` background, `RoundedCornerShape(12.dp)`,
12 dp vertical padding, 14 sp text (`Lane:273–285`) — a lit bed
behind print (forbidden by §6) at the wrong size (A-5's measure
is 15 sp × 2). There is no fixed two-line slot, no top-aligned
standing emptiness, and idle composes the same conditional
banner (D-11) instead of no slot.

**D-11 · MEDIUM — the idle banner is ungated (violates A-8).**
`bannerLine` derives from `activeTask`/`intention` with no
running gate (`Lane:268–271`) and composes whenever non-null,
idle included. With the VM's queued-task fallback (audit A-8:
`MonolithViewModel.kt:273–276`), a hand-raised idle clock names
a session that is not running — on the nightstand's one
sentence.

**D-12 · MEDIUM — no SLEEP face (violates §1.7).** Nothing in
the file derives a `SLEEP` banner, suppresses a block line for
routine rows, or reads `windowEndMillis` for ENDS. A
routine-born SLEEP span renders as an ordinary timed session
with a task/intention banner — the stand-down's own span is
indistinguishable on the face that stands through it.

**D-13 · MEDIUM — keep-awake still lives in the face, and
there is no surrender (violates A-10(v) / M-4 amended).** The
flag is added/cleared by the composable's own
`DisposableEffect` (`Lane:98–110`) — the ownership the ruling
moves to the VM. No deadline (max of settle / interaction /
disconnect + 10 min), no plug-in cancellation semantics, no
520 Curve C fade to 0.00, no battery-yield state: this face
cannot print NOTHING by design (§1.8), so on battery it either
burns until the OS kills it or dies mid-print.

**D-14 · LOW — the hero's comment lies about its size.** The
comment at `Lane:215` says "112sp"; the hero is 96 sp
(`Lane:220`). 96 sp is this spec's ruled size (§0) — the code
is right and its own label is wrong; fix the comment at the
merge so the next reader doesn't "correct" the type.

**D-15 ◆ · MEDIUM — V1 as amended is not in the lane at all.**
`DeskClockSettle.kt` is time-only: a 20 s deadline from
max(arm, lastInteraction) (`DeskClockSettle.kt`, `tick()` /
`SETTLE_MILLIS = 20_000L`) with no sensor sampling, no
three-state verdict, and no HELD suppression — as built, the
unconditional backstop seizes the braced reader Fen's traces
convict (audit E-2). The dismissal bit is likewise in-memory
only (`decidedSessionId`), so process death resurrects a
dismissed face on the cold-open path (Fremd F-1) against the
persisted dismissal (V2 + M-4). Outside the overlay file, but
the merge is where both die; flagged for Wacht/Wecker, not
priced here.

---

*— Vela, Designer, cell issue-13. Spec and disagreements filed
for Wacht's merge hand. Open seats I took, marked [VELA] in
place: §1.3 (one-block block-line absence), §1.4 (stopwatch
never descends), §3 (label line floored with its cell), §7
(the exit-line reading), §8 (rule construction detail). Each
is one line for Wacht to strike at the merge table; none
touches a ruled string, alpha or floor.*
