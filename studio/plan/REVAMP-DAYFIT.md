# REVAMP-DAYFIT — the launcher walked through his real day + the wild bench

**Bundle: HUMANS / DAY-FIT + WILD / INNOVATORS, one file, two parts.**
**Ruling it serves (Sarthak, Oct 30, 04:29 IST):** the block screen is an
un-exitable launcher with separate ALLOWED / PEEK screens; the whole
design revamped; *"make the app crazy good."*
**Judged object:** Slice 1 as BUILT — `LauncherBlockScreen.kt` (three
rooms: HOME dial + ALLOWED grid + PEEK grid) and its `MainActivity.kt`
wiring (Guard-line entry while sealed, Airlock doors, tap-time launch
gate, Safe-Break peek path, Emergency Break foot row on every page) —
read against `REVAMP-DESIGN.md` (the locks) and
`REVAMP-INTERACTION.md` (the feel spec). Where the build and the design
bundle disagree, this file judges the BUILD, because the build is what
his hand meets.
**Method:** his day, anchor by anchor, in the state his body is in at
that hour — half asleep, cold-handed, wet-handed, depleted. At each
anchor: the session/routine state he is actually in (routine seeds are
code, `MonolithRepository.routineTargets()`: FASTED BLOCK 05:00–08:00 ·
DAY BLOCKS 09:00–19:30 · SLEEP 20:00–03:30, all daily), what he needs
from the phone in ≤5 seconds, and whether the launcher gives it or
fights him. A need that takes >5 s or >3 taps at its anchor is a tax;
a need the launcher makes impossible without burning the weekly
Emergency Break is a fight.
**Laws held throughout:** no new exits, ever — every fix below works
inside the seal or at rested setup time. Fonts Sora / Atkinson
Hyperlegible / Anonymous Pro only. Never green. No AI images.
HOLD-absolutely. Keepers untouched: the receipt's print character,
THE BOLT, the seal gesture.

---

# PART 1 — DAY-FIT

## Verdicts at a glance

| Anchor | State he is in | Verdict |
|---|---|---|
| 03:30 wake | SLEEP session ends this minute (or minutes left) | **TAXES** ×3 (DF-01/02/03) |
| 03:30–04:00 bath | Unsealed gap — no session, no launcher | **FITS** (by absence) |
| 04:00–05:00 meditation | Unsealed gap — phone across the room | **TAXES** (DF-04) |
| 05:00 fasted arm | FASTED BLOCK routine session seals | **TAXES** (DF-05) |
| 05:00–08:00 fasted study | Sealed, STUDY loadout, blocks + Safe Breaks | **FIGHTS** (DF-06, conditional) |
| 08:00 gym | Unsealed gap 08:00–09:00 — *if* the morning ended on time | **FITS** gap / **TAXES** carry-over (DF-07) |
| ~09:00 sadhana | DAY BLOCKS seals at 09:00, mid-sadhana | **FIGHTS** (DF-08) |
| ~09:40 first meal | Sealed inside DAY BLOCKS; meal ≠ any break that exists | **FIGHTS — the worst anchor** (DF-09, DF-10) |
| 10:00–19:30 study blocks | Sealed, ~10 blocks, Safe Breaks between | **TAXES** (DF-11/12/13) |
| 19:30–20:00 wind-down | Unsealed gap | **FITS** |
| 20:00 SLEEP arm | SLEEP routine session seals | **TAXES** (DF-14) |
| All day, worst moments | Emergency Break inside the launcher | **TAXES** (DF-15) |

**Friction count: 15 (DF-01…DF-15) — 4 FIGHTS, 11 TAXES.**
**Worst anchor: the first meal, ~09:40** — see DF-09. It is the only
need on his day that is physiological, daily, and has *no lawful tool
at all*: the seal offers him a 10-minute break he cannot start, at a
moment he cannot choose, or the week's one Emergency Break.

## What already fits — recorded so nobody "fixes" it away

- **Back law.** Back on a grid returns HOME; Back on HOME is swallowed
  (`BackHandler` in the build). The seal cannot be backed out of, and
  grid navigation is one thumb-press deep. FITS at every anchor.
- **Emergency Break is present on EVERY launcher page**, grids
  included — never buried behind a return HOME. (Its *size* is DF-15;
  its *presence* is right.)
- **Browsers and the Play Store are barred from ALLOWED** even when
  their tier says OPEN, and the door count and the grid read the same
  filtered list — the count can never promise an app the grid hides.
  TELL-THE-TRUTH, kept.
- **Tap-time launch gate.** A tile launches only what is *still* OPEN
  at the tap instant, re-read from live rows in `MainActivity`. A
  stale grid cannot smuggle an app out. HOLD, kept.
- **Stopwatch honesty.** A FLOW session draws NO progress arc and
  prints elapsed with `ELAPSED · NO SET END` — a count-up session is
  never dressed as a fraction of a whole it doesn't have. (Its day-fit
  cost is separate: DF-07/DF-11.)
- **Safe Break frees PEEK — and only PEEK.** Both services free the
  PEEK tier during a break; LOCKED stays locked. The break is a
  loosening of the held doors, not a hole in the wall. HOLD, kept.
- **One time derivation.** The launcher's ends-at is computed from the
  session's own span numbers, the same derivation the Deck and the
  desk clock read. The launcher cannot disagree with the room it
  replaced.

---

## 03:30 — wake · SLEEP ending

**State.** The SLEEP routine window is 20:00–03:30. At 03:30 sharp the
session stands down; minutes before, he is still sealed under the
SLEEP loadout (Full Lockdown — Phone, Clock, Maps open, per
`SmartLoadout.SLEEP`). His first surface of the day is not the
launcher at all but the desk clock across the room — the launcher is
what he meets when he picks the phone up and taps the Guard line.

**DF-01 — No wall-clock time on HOME · TAXES**
*Need (≤5 s):* "What time is it, and is SLEEP over?" — the two facts a
man waking in the dark actually wants.
*What the launcher gives:* a remaining numeral and `ENDS 03:30`. At
03:22 the dial reads `08:00`-class remaining — a number shaped exactly
like a clock time, in the one hero position, that is *not* the time.
Half asleep, he will misread the session's remainder as the hour at
least once; the surface's biggest number impersonating a clock is a
truth hazard, not a style note. The desk clock prints real time; the
launcher, the surface in his hand, never does.
*Fix (print-layer, hours):* one Anonymous Pro small line on HOME,
`03:22` wall time, seated with the `ENDS` line (never inside the dial
well — the well's job is unchanged). Two clock-shaped numbers can then
never be confused, because both are labelled by position: time of day
by the rim, time left in the well.

**DF-02 — Half-asleep legibility: the build's dial is smaller than the
locked dial · TAXES**
*Need:* read the state from arm's length, eyes at half aperture,
before any light is on.
*What the build gives vs what the design bundle locked:*

| Element | REVAMP-DESIGN §A4 lock | Slice-1 build |
|---|---|---|
| Remaining numeral | Anonymous Pro 700, **72sp** | **46sp** |
| Door plate title | Sora 700, 22sp | 18sp |
| Door plate sub-line | (label tier, engraved) | 11.5sp Atkinson |
| App name on tile | Atkinson 700, 15sp | 10.5sp, weight unstated |

Cream on night ground is ≈13.5:1 — contrast is not the problem; *size*
is. At 46sp the numeral is a desk number, not a waking number, and
10.5sp tile names are below the floor the UX seat set for arm's-length
recognition (12sp, never truncated). He can read all of it — slowly,
leaning in, awake. That is the tax: the surface demands the alertness
it is supposed to serve before he has any.
*Fix:* restore the §A4 scale on HOME at minimum (numeral 72sp — the
dial canvas is 248dp, the well takes it), tile names to 12sp minimum.
No new elements, no layout change beyond the dial growing to its
specced size. This is the design bundle's own lock being honoured,
not a new ask.

**DF-03 — Waking early costs the weekly Emergency Break · FIGHTS**
*Need:* on the morning he wakes at 03:05 rested and done sleeping, he
needs the day to start — bath, meditation, an early fasted block.
*What the launcher gives:* SLEEP sealed until 03:30, full stop. The
only lawful door out before window end is the Emergency Break — the
one-per-week, 60-second-hold valve that exists for genuine collapse.
Spending it to *wake up early* is obscene pricing, so in practice he
lies there sealed, or starts his bath with a phone that is still in
last night. A blocker that punishes waking early has its incentives
exactly backwards for a 3:30 riser, whose wake time *will* drift
±30 min across a season.
*Fix — two parts, honestly separated:* (a) **Buildable now, no new
exit:** nothing — and this file will not pretend otherwise. (b) **His
ruling required:** a SLEEP stand-down rule (e.g. the SLEEP session —
never judged, per the routines rebuild — may stand down inside its
final 45 minutes when the Clock alarm is dismissed *and* the phone is
picked up and unlocked by hand). That is a new exit shape and it is
**not** authorised by this document; it is priced here for his
decision: ~1–2 builder-days (stand-down condition in the routine
finalise path + a launcher print of the window, `MAY END FROM 02:45`),
revert = delete the condition. Until he rules, DF-03 stands as the
launcher's one structural fight at the day's first anchor.

## 03:30–04:00 — bath · the gap

**State.** No routine covers 03:30–05:00. Phone unsealed; the launcher
does not exist. **Verdict: FITS, by absence** — a bath needs no
launcher, and a blocker that followed him into the bathroom with a
sealed timer app would be theatre. Wet hands meet no Monolith surface
in this half hour, which is the correct number. One note only: if he
carries the phone for a bath timer, the Clock is a stock app here, not
a Monolith promise — fine, and not the launcher's debt.

## 04:00–05:00 — meditation · the gap, continued

**DF-04 — The meditation hour is the seal's open window · TAXES**
*State:* still the 03:30–05:00 gap. Phone across the room, by his
practice. The desk clock (elapsed + remaining, burn-in shifting) is
the surface, and it fits this hour — it is judged elsewhere.
*The tax is the launcher's absence, stated plainly so it is a choice
on record and not an oversight:* if he reaches for the phone
mid-meditation — a noise outside, a message he suddenly remembers —
the phone is *fully open*. Every other hour of his waking day has a
tier between him and a feed; this one has none, and it is the hour
whose entire purpose is not reaching. The counter-argument is real:
meditation with a sealed phone he must Emergency-Break to check a
noise is its own fight, and his practice puts the phone across the
room, which is a stronger seal than software.
*Fix (rested setup, zero machinery):* none in code. Recorded
recommendation: if he ever wants this hour covered, the lawful shape
already exists — a manual session armed before the bath with the
SLEEP loadout — and the launcher serves it unchanged. Do NOT add a
MEDITATION routine seed unasked; a routine that seals a phone nobody
is holding mints exactly the ghost-session class the Humans cohort
killed (HPA-04).

## 05:00 — the fasted arm · cold hands

**State.** FASTED BLOCK materialises at 05:00 (STUDY loadout) — on the
designed flow the session is routine-born; he does not arm it. He
wakes the phone inside a sealed session and meets the Deck's running
form; the launcher opens from the Guard line's tap, or from an
Airlock door if he first reaches for a held app.

**DF-05 — The launcher's front door is a line of text · TAXES**
*Need (≤5 s):* cold, stiff fingers, straight out of meditation: get
to his allowed apps.
*What the build gives:* entry is the Guard line — a text line in the
header band (`MainActivity` tap-time predicate: sealed ∧ ¬navigation-
locked → launcher HOME). The predicate is excellent (re-read at tap
time, dead during finish detention — truth kept). The *target* is a
line of type. No hit-area is specced for it in the revamp bundle; the
interaction spec lavishes 76dp plates and 64dp tile targets on
everything *inside* the launcher and leaves its own front door as
body text. A cold thumb that misses the line has tapped the header —
nothing happens, no feedback (F-DENIED silence by law) — and at 05:00
silence after a miss reads as "the app is stuck."
*Fix (invisible, hours):* the Guard line's touch area becomes the
full header band height (≥48dp) whenever sealed, visual unchanged.
The line stays a line; the hand stops needing to aim at type.

## 05:00–08:00 — fasted study · sealed, STUDY loadout

**State.** Three hours, multi-block sessions with automatic 10-minute
Safe Breaks between blocks (`SAFE_BREAK_DURATION_SECONDS = 600`,
entered only at a block's end — repository truth). STUDY loadout
subtitle, verbatim: *"Blocks Social & Games, sets YouTube & Messaging
to 2m Peek, enables Shorts & Web shields."*

**DF-06 — His lectures live behind the peek door · FIGHTS (conditional)**
*Need:* a NEET repeater's fasted block runs on lectures — full-length
physics/chemistry/biology videos (his zero-base physics route is full
lectures for learning, one-shots for revision). A lecture is 40–90
minutes of continuous watching.
*What the launcher gives, while the STUDY preset's tiers stand:*
YouTube sits in PEEK — the Held Door offers a **2-minute lease**
(`PEEK_DURATION_SECONDS = 120`) with a **20-minute cooldown** per app.
Two minutes of a lecture, then the door closes, then twenty minutes
of not being able to resume it. The peek mechanism is honest and well
built; it is simply the wrong tier for the day's main study input,
and the launcher is where the mismatch becomes physical — his most-
needed app sits in the dim room (S3), behind a door that counts his
looks, while the bright room (S2) holds apps he needs less. Every
lecture becomes ~15 peek negotiations or one Emergency Break. He
will route around it (browser — barred from ALLOWED for exactly the
right reasons — or unsealing habits), and every route-around teaches
him the seal is negotiable.
*Fix (rested decision, zero code):* the lecture app's tier is a
Shield/loadout decision rested Him owns: YouTube-the-app as OPEN
under STUDY (the Shorts/Reels sniper and site blocker already police
the *feed* inside it — that machinery exists and is separate from
the app tier). The launcher needs no change; the PEEK door returns to
its true job — glances. This file flags it as a fight *conditional on
the preset tier standing*, because tiers are his to set — but as
seeded, the preset fights the fasted block every single morning.

## 08:00 — gym · the gap that mostly fits

**State.** FASTED ends 08:00; DAY BLOCKS opens 09:00. For exactly one
hour the phone is unsealed — stock launcher, every app one tap.
**Verdict on the gap itself: FITS, and it is the day's best accidental
design.** Gym needs — a fitness app, music, a rest timer, a photo of
a form chart — are all one tap, with wet/sweaty hands, on big stock
targets. No Monolith surface taxes the gym hour at all. The answer to
"should an OPEN fitness app be one tap?" is: *in the gap, it already
is — the gap is doing the work.*

**DF-07 — Carry-over days: the sealed gym is 3 taps and small tiles ·
TAXES**
*When it bites:* any morning the fasted work carries past 08:00 — a
FLOW (stopwatch) session has no set end and does not stand down at
the window's edge in his hand; a long block overruns. Now he is at
the gym *sealed*, and the fitness app's path is: Guard line → ALLOWED
plate → tile = **3 taps**, the last onto a **52dp well / 40dp icon**
with a 10.5sp name (DF-02), one-handed, between sets, with chalky or
wet fingers, repeated at every set break for music/timer checks. The
door plates are big and honest; the *frequency* is the tax — a door
designed for twice-a-session crossings is being used twenty times
an hour.
*Fix:* W-2 (the Apron, Part 2) is the structural cure. Interim print
fix: none honest — do not shrink the seal for the gym; price the
Apron and build it if he picks it.

## ~09:00 — sadhana · DAY BLOCKS seals underneath him

**DF-08 — The 09:00 seal lands mid-sadhana · FIGHTS**
*His real morning arithmetic:* gym 08:00 (≈1 h) → sadhana 30–40 min
*after* gym → first meal *after* sadhana. Sadhana therefore runs
≈09:00–09:40 and the meal ≈09:40. The DAY BLOCKS seed fires at
**09:00** — inside sadhana, not after it. The phone seals itself,
in another room or face-down beside him, under a STUDY loadout that
has never heard of sadhana: if his sadhana uses any phone support
(a timer, a recitation/mantra app, music), that app is LOCKED or
PEEK under STUDY, and the session now sealing his morning study has
started its clock — and its block structure, its Safe Break rhythm —
while he is still in the practice the block was supposed to follow.
By the meal he is ~40 minutes inside a session he never saw arm,
whose intention slot is empty (DF-11) and whose first block boundary
— his first lawful Safe Break — lands wherever the blocks fall, not
at his meal.
*Fix (two prices, pick one):* (a) **Retime the seed — 1 rested edit,
zero code:** DAY BLOCKS 09:00 → **09:45**. The seed simply does not
match the day it claims to serve; his stated day (gym 8, sadhana
30–40, meal) cannot produce a 09:00 study start. This is the
recommended fix — the routine is his, the edit is his gesture in
Shield, and the launcher then meets a man who has eaten. (b) Window-
not-firing semantics (routines prime, only his hand seals) is the
Humans cohort's HPA-01 and remains the deeper cure, but it is
machinery and a standing open question — not this file's to settle.

## ~09:40 — first meal · THE WORST ANCHOR

**DF-09 — There is no meal break. There is a 10-minute break he cannot
start. · FIGHTS**
*Need:* eat the day's first meal — 20–30 unhurried minutes — and,
lawfully, use the phone the way a meal uses a phone: message family,
check the day's plan, maybe a delivery/order app. Then return to the
blocks with the session's seal and clock intact.
*What the app actually offers, exhaustively:*
1. **A Safe Break** — 10 minutes (`SAFE_BREAK_DURATION_SECONDS`),
   entered *only* automatically when a block ends, never startable
   by hand, and 10 minutes is not a meal.
2. **End the session** and re-arm after — losing the session's span,
   its block rhythm, and paying a full re-arm at his hungriest,
   most distractible moment of the morning.
3. **The Emergency Break** — the weekly valve, for lunch.
There is no fourth option. The Safe Break's *concept* is exactly the
meal's shape — clock paused, seal held, PEEK freed — but its trigger
(block boundary) and its length (10 min) were specced for between-
block breathers, and his meal is neither at a block boundary nor
10 minutes. Every day, the hungriest hour of a fasted morning meets
a choice between eating inside a countdown, breaking the session's
spine, or spending the week's emergency on food. He will pick (2) or
eat sealed-and-resentful often enough that the meal becomes the
seal's daily crack — the precise place a habit of route-arounds
forms. **This is the worst anchor of the day: daily, physiological,
and tool-less.**
*Fix (machinery, priced, no new exit):* a **Meal Break**: one
hand-startable, 30-minute Safe-Break-class pause per DAY BLOCKS
window, armed at rested setup (a per-routine flag: `mealBreak =
30 MIN · ONCE`), started from the Deck/launcher with the same
one-gesture weight as END BREAK's chip, clock paused, seal held,
PEEK freed, LOCKED held — HOLD is untouched because a Safe Break
never was an exit. Spec sketch: reuse `isInSafeBreak` wholesale with
a duration + once-per-window stamp on the session row (one column,
one funnel — the breakTakenSeconds booking already handles partial
takes truthfully). **Price: ≈2–3 builder-days** (repository funnel +
Deck/launcher entry + receipt truth line), revert = remove the flag
and the entry; existing breaks unaffected. It needs his ruling as a
machinery addition — this file proposes, it does not authorise.

**DF-10 — Inside a Safe Break, the launcher hides the break itself ·
TAXES**
Three print failures, all on surfaces in the build today:
(a) **No break countdown on the launcher.** `LauncherHome` reads
`isInSafeBreak` for one eyebrow line ("ON A SAFE BREAK — THE SESSION
CLOCK IS PAUSED") and *never reads `safeBreakSecondsRemaining`* — the
one number a man on a break wants (`BREAK · 4 MIN LEFT`, which the
Deck's eyebrow does print) is absent from the room he is actually
standing in while he peeks/orders/messages.
(b) **The PEEK grid lies during a break.** Its sub-line still reads
`2M PEEK — THE DOOR DECIDES` while `MainActivity`'s Safe-Break path
launches PEEK apps *directly* — no lease, no cooldown, no door. The
print promises a negotiation the tap won't have (a kind lie, but the
brand is TELL-THE-TRUTH, and kind lies are how counters start
drifting).
(c) **The cliff has no shadow.** When the break expires mid-message
or mid-order, interceptor suspension ends and the PEEK app is cut —
with no warning on any launcher surface, because of (a). A sent-
message interrupted at the boundary is a small, daily, memorable
annoyance.
*Fix (print-layer, ~half a day for all three):* during a Safe Break
the dial well gains the break's remaining as its secondary line
(Deck-identical derivation), the PEEK page sub-line prints the truth
(`OPEN DURING BREAK — BREAK ENDS 09:52`), and the final 60 seconds of
a break show the eyebrow in the HELD verb's place — no; in cream,
bold — alert `#D63E06` is HELD's alone (§A1) and a break ending is
not a refusal. W-4 (Part 2) carries the in-app half of this cure.

## 10:00–19:30 — the block day · sealed, working as designed

The long middle mostly fits: doors are two taps from HOME, OPEN tiles
launch instantly with no interstitial (the Enthusiastic seat's C5 law,
kept in the build), the dial's stillness costs nothing across nine
hours. Three taxes ride along:

**DF-11 — Routine-born sessions leave the dial's centre empty · TAXES**
*Need:* at a midday glance, the dial's load-bearing part — the
intention, in his own words (§E3: strip it and a block is "just a
locked phone and an annoyed person").
*What the build gives:* routine-materialised sessions carry no
hand-written intention, so the intention line is simply absent (the
design's own rule — an empty slot is never filled with placeholder —
honestly kept). The result across FASTED + DAY BLOCKS is a dial that
is a timer all day, every day: number, ring, ends. Truthful, and
thinner than the design's promise. Beside it, HOME prints no block
position: `totalStudyBlocks` / `currentBlockIndex` are on the row and
never rendered on the launcher, so "BLOCK 3 OF 8" — the fact that
tells him the *shape* of his remaining day at a glance — exists only
on the Deck.
*Fix:* (a) print-layer: `BLOCK n OF N` in the label tier under the
dial (data on the row today, zero machinery). (b) Rested setup: a
per-routine intention string, set once in Shield, stamped onto the
session at materialisation — his words, written rested, read tired;
the slot's law (verbatim or absent) is untouched. ≈0.5 builder-day.

**DF-12 — The PEEK grid cannot say which doors will actually open ·
TAXES**
*Need:* at hour nine, pick the PEEK app that will open — first tap,
no ceremony.
*What the build gives:* the grid lists every PEEK app identically.
Cooldown state (20 min per app after a lease) lives in the machinery
and is printed *inside* the Held Door offer — after the tap, after
the 5-second breath. A cooling app costs him a full door ceremony to
learn "not yet," at the hour his patience is thinnest. The Airlock
already computes this text (`PEEK COOLDOWN — AGAIN IN n MIN`); the
grid just never asks.
*Fix (print-layer):* a cooling tile carries its remaining cooldown
as its tag-line (Anonymous Pro label tier, on the sheet fill — the
PEEK verb's own material), derived from the same peek state the
Airlock reads. One fact, already rendered once in the offer; the
grid line is its *address change*, flagged here for the truth
ledger rather than smuggled as a second rendering.

**DF-13 — A night surface at midday · TAXES (minor)**
The launcher ships night tokens only. At midday in a bright room the
cream numeral holds (13.5:1) and the accent ring washes toward its
5.9:1 floor — but TP-7's redundancy is exactly for this: ring, numeral
and label never all fail together, and the numeral is the leg that
carries the fact. Recorded as a minor tax with **no fix proposed**:
a day variant of the sealed room would spend the revamp's scarcest
asset (one room, one light) to cure a leg that does not bear weight.

## 19:30–20:00 — wind-down · the second gap

**State.** DAY BLOCKS ends 19:30; SLEEP arms 20:00. Thirty unsealed
minutes. **Verdict: FITS.** Last messages, family, the day closing —
all one tap, no seal to negotiate, and nothing in the plan renders
this half hour as a shortfall. The gap is the evening's equivalent of
the gym gap, and it should be defended as designed slack, not
"coverage" waiting to be filled.

## 20:00 — SLEEP arms · the day's last touch

**DF-14 — The arm moment gives him no warning and buries the alarm ·
TAXES**
*State:* SLEEP materialises at 20:00 wherever the phone is — in his
hand mid-wind-down conversation, or face-down. Unlike DF-08 the hour
itself is right (20:00 is his stated sleep); the taxes are at the
seam:
(a) **No pre-arm surface exists in the launcher's world** — the
launcher exists only *sealed*, so by construction nothing in this
revamp can warn "SLEEP in 10 minutes" (the Deck's Next Line does,
unsealed — the warning lives in the other regime and dies exactly at
the boundary it predicts). If he is inside a PEEK/OPEN app at 20:00,
the seal lands on him mid-act.
(b) **Post-seal, the night's one setup act — the 03:30 alarm — is
3 taps away:** Clock is OPEN under SLEEP (correct), but the path is
Guard line → ALLOWED plate → tile, at 20:00, with a finished man's
eyes, onto DF-02's small tiles. The alarm is the load-bearing app of
his entire timetable; its path should be the shortest in the app.
*Fix:* (a) is W-2's Apron again (Clock pinned = 1 tap from HOME).
For the seam itself: a pre-arm knock/print is Temple ST-06's old
question and Q6-sound territory — recorded, not specced here.

## All day — the valve inside the launcher

**DF-15 — The lawful exit is the smallest target on the surface ·
TAXES**
*What the build gives:* `EMERGENCY BREAK` as a 12sp text link,
≈30dp of touch height with its padding, bottom-right on every page —
mirrored by `RETURN TO THE DECK` at identical weight bottom-left.
Presence: perfect (see FITS list). Geometry: at the moment the valve
is used — panic, tremor, a genuinely bad minute — the lawful door is
a line of small type that must be *aimed at*, paired against an
equal-looking line that goes somewhere else. The valve's own
machinery (60-second hold, banked) is a masterpiece of weak-hand
design; its *entry in this room* is not.
*Fix (geometry only, no visual change):* both foot-row links take
≥48dp invisible touch height, full half-width each, divided at
screen centre — the Held Door cure from the Temple day-fit (56dp
tablets, whole-foot when alone) applied to the launcher's foot. The
eye sees the same quiet room; the thumb stops being able to miss.

---

# PART 2 — WILD / INNOVATORS

*Remit: invent past the brief. Every mechanism below is priced
honestly, carries its revert cost, and names the law it must not
break. None of them adds an exit, weakens a hold, or asks the tired
user a question. Rested Him configures; tired Him only ever taps.*

## Gravity Light — VERDICT: BUILD (conditionally, as its own commit)

The interaction spec (§1.3, Crazy-ideas seat) proposes one virtual
light for the whole launcher, its direction driven by the rotation-
vector sensor, clamped ±18°, so dial specular, plate highlights and
tile wells all slide together — three drawings become one object.

**Verdict: BUILD — after the revamp ships, as a standalone follow-up
commit, under the four gates below.** Reasons:

1. **It is the cheapest "machine, not screen" evidence available.**
   Monolith's retention law is beauty-as-recognition: a blocker he
   *likes touching* is a blocker he keeps armed (the Blockit lesson —
   the analog-machine look is why he arms it daily). Coherent light
   moving across metal is the single strongest one-object cue a flat
   screen can produce, and it costs zero blocking behaviour.
2. **It fulfils the older light law better than the status quo.**
   WILD-PLAN §0.2 fixed the light *"in space forever"* — but a
   screen-fixed highlight is fixed only to the *glass*: tilt the
   phone and the painted light rotates with it, i.e. it moves
   through the world. A gravity-anchored light is the literal
   reading of Mara's law: fixed in space, the phone moves under it.
   This file rules the two compatible — Gravity Light is §0.2
   obeyed physically instead of pictorially — and flags the reading
   for Ren's merge so nobody later cites §0.2 against it in bad
   faith.
3. **Its fallback is the current design, exactly.** Sensor absent →
   vector locks at the static 135° default and the launcher is
   pixel-for-pixel what Slice 1 ships. There is no degraded mode to
   be embarrassed by.
4. **The day-fit risk is real but containable.** The sealed room's
   law is stillness (§B5: a sealed room does not breathe at you).
   Light that slides while he reads the dial at 03:30 could read as
   restlessness in the one surface that must feel *settled*. The
   clamp and the gates below keep the effect in the hand (it moves
   when the phone moves, and is still when the phone is still) —
   it is a property of the object, never an animation. If on his
   phone it reads as a gimmick, the revert is one commit and nothing
   else in the revamp depends on it.

**Gates (all four, non-negotiable):** (i) NOT in the revamp commit —
the launcher ships correct under static light first; (ii) reduce-
motion / animator-off at OS level ⇒ Gravity Light OFF, no in-app
override; (iii) the sensor listener unregisters whenever the launcher
is not visible (DisposableEffect lifecycle) — an unregistered
listener is a battery leak with good PR; (iv) amplitude on the dial
clamped tighter than the plates (±12° vs ±18°) — the hero object
moves least, always.
**Price (per §1.3, re-verified against the build):** ≈1–1.5
builder-days, ~180 lines, draw-phase only, no DB, no session state,
no permission. **Revert cost:** one commit, zero residue.
**Law it must not break:** light never carries data (§0.2's core
law survives intact — the vector encodes no state, no progress, no
armed-ness); if it ever starts *meaning* something, it has gone
wrong and comes out.

## The wild bench — four new mechanisms

### W-1 — THE TERRAIN DIAL · the remaining day, engraved

**The idea (2 sentences).** The dial's groove gains engraved notches
at every block boundary of the session's span, with each Safe Break
cut as a wider notch, so the ring stops being a featureless circle
and becomes the *shape* of the remaining day — blocks and breaths
readable in one glance. During a Safe Break, the live notch fills
cream: you can see which breath you are standing in.
**Why it serves a sealed study day.** His day is block-structured
(12 h in ~50-min blocks) but the launcher's dial renders only a
fraction — a man at hour seven cannot tell from it whether two
blocks or five remain, nor *when the next lawful breath comes*,
which is the question that decides whether he holds on or reaches
for the valve. Terrain answers both with zero decisions and zero
new numbers to read half-asleep.
**Spec sketch.** Layer-1 static path only (Interaction §2.3's
cached face): notch angles derived from the span arithmetic already
in `FocusSessionEntity` (`spanTargetSeconds`, `totalStudyBlocks`,
`SAFE_BREAK_DURATION_SECONDS`) — block notches 2dp sheet-tone cuts,
break notches 4dp; the in-break fill reuses the progress arc's draw
call clipped to the active notch. No new state, no new colour:
sheet `#4A423C` cuts, cream `#FFE4CF` fill, accent stays the ring's
alone (§A1 budget untouched). Pairs with DF-11's `BLOCK n OF N`
print as its redundant text leg (TP-7).
**Price:** ≈1–1.5 builder-days (draw code + screenshot tests at
90 %/10 % per §2.3's acceptance). **Revert cost:** trivial — delete
the notch path; the dial is Slice-1's dial again.
**Law it must not break:** colour-as-verb (§A1) — a notch may never
be accent or alert; and one-fact-one-rendering — terrain renders
*structure* (boundaries), the numeral keeps *quantity*; if a notch
ever encodes a count the numeral also prints, one of them dies.

### W-2 — THE APRON · his four apps, pinned under the doors

**The idea (2 sentences).** HOME gains a bottom apron: up to four
OPEN-tier apps he pinned while rested (Clock, Phone, his timer,
his fitness app) as seated tiles in a single strip below the door
plates — one tap from HOME to the app, no door trip. The doors stay
for the full grids; the apron is the day's muscle memory, four
slots that never move, never reorder, never learn.
**Why it serves a sealed study day.** It is the direct cure for the
day's three repetition taxes: the 03:30 alarm and the 20:00 alarm
(DF-01/DF-14), the gym's twenty set-break checks (DF-07), the meal's
messaging (DF-09's lawful half). Frequency is the argument: a door
crossed twice a session is a ritual; a door crossed twenty times a
gym hour is a toll. The apron moves the four highest-frequency
*already-permitted* acts to one tap without widening permission by
a single app.
**Spec sketch.** Pins are set only while unsealed, in Shield, from
OPEN-tier apps (rested decides — the strip is not editable while
sealed, ever). Tiles render in §A2's Seated state (engraved, no
shadow at rest) so the hierarchy survives: dial hero, plates raised,
apron docked — the Wow seat's "dial has no company" law is bent,
knowingly, at the *bottom* of the room, and this file names the bend
rather than hiding it. Launch reuses `MainActivity`'s existing
tap-time gate verbatim (live tier re-read, browsers/Play Store still
barred — a pin can never smuggle what the ALLOWED grid bars).
Storage: a 4-slot package list in the existing guard-settings row
or a prefs file — no DB migration. Cap is hard: a fifth pin replaces
by his rested hand or not at all; no "recently used" may ever enter
the strip (§D5: a launcher that moves your apps is gaslighting your
hands).
**Price:** ≈2–3 builder-days (HOME strip + Shield pinning UI +
tap-gate reuse + tests). **Revert cost:** low — remove the strip and
the pin UI; orphaned pin data is inert.
**Law it must not break:** HOLD-absolutely — OPEN tier only, gated
at tap time exactly like the grid; and the seal gesture / THE BOLT
are untouched (the apron lives inside the sealed room, it is not a
way out of it).

### W-3 — THE ARM SLIP · the receipt, at the beginning

**The idea (2 sentences).** When a session seals — routine-born
above all — the launcher's first opening of that session can raise a
small printed slip in the receipt's own print character: routine
name, window, loadout counts as actually sealed, ends-at, the
session's № — the facts of the seal, struck once, in the open. He
dismisses it with the next tap; it never re-appears for that session.
**Why it serves a sealed study day.** The day-fit's two seam
frictions (DF-08's 09:00 seal underneath him, DF-14's 20:00 seal
mid-act) share one root: routine seals happen *to* him, silently,
and the launcher then shows a generic `SEALED` over a session he
never saw being born. The receipt is the one surface he has
explicitly loved (❤️, batch-7) — its print character is the app's
proven trust voice. An arm slip turns "the phone locked itself"
into "the machine shows its work": what sealed, from which routine,
holding how many apps, until when — TELL-THE-TRUTH at the moment of
maximum suspicion.
**Spec sketch.** Pure print: data is already frozen on the session
row at materialisation (`routineId`, `windowStartMillis` /
`windowEndMillis`, `heldAppsAtSeal` / `heldSitesAtSeal` — Slice E's
snapshot, written exactly once). Typography reuses the ReceiptSheet's
Anonymous Pro strike + rule lines verbatim — this is the keeper's
character *extended*, never imitated in a cheaper hand. Slip carries
NO remaining numeral (the dial owns quantity — one fact, one
rendering) and NO intention substitute (if the slot is empty, the
slip prints the routine's name there, labelled as the routine's —
a fact, not a ventriloquism). Dismissal = the receipt's existing
law: next DOWN anywhere, never a timer.
**Price:** ≈1 builder-day (one composable + a seen-per-session flag).
**Revert cost:** trivial — delete the composable; no data depends
on it.
**Law it must not break:** the receipt print character itself — if
the slip cannot be struck in the receipt's exact typographic voice,
it does not ship in another; and TRUTH — counts print only from the
seal snapshot (`-1` uncaptured prints nothing, per the existing
Zettel Z2 omission law).

### W-4 — THE BREAK PILL · the break's countdown, inside the peek

**The idea (2 sentences).** During a Safe Break, any PEEK app
launched free carries a small edge pill — break remaining in
Anonymous Pro, nothing else — that rides over the app for the
break's whole life and dissolves at its end. In its final 60
seconds the pill gains one line, `BREAK ENDS — RETURN WHEN READY`,
so the break's cliff (DF-10c) always casts a shadow before it lands.
**Why it serves a sealed study day.** The meal and the between-block
breath are the two moments he is *lawfully inside* other apps with
the session's clock paused; today both end by ambush — the app he is
mid-sentence in is cut at a boundary no surface warned him about.
The pill makes the break a room with a visible clock instead of a
trapdoor, and it does so without extending the break by a second or
offering any control inside it: it informs, it never negotiates.
**Spec sketch.** Overlay from the Shield service (overlay grant
already held; the AOD/pill lineage in the design corpus is the form
reference — bronze-dark plaque, cream numerals, no dismiss control,
leaves when its state ends). Content is one derivation:
`safeBreakSecondsRemaining` from the active row, ticked by the
service's existing beat — the same number the Deck eyebrow and
(fixed per DF-10) the launcher dial print, one fact in its three
lawful places (on the Deck, in the launcher, over the peek — three
*rooms*, one number, flagged here for the truth ledger). Tap on the
pill returns to the launcher HOME — a return *into* the seal, the
opposite of an exit. No buttons, no extend, no snooze: a pill that
can lengthen a break is a negotiation surface and dies on that line.
**Price:** ≈2–3 builder-days (service overlay lifecycle + break-
state wiring + on-device pass; overlay work is always pricier than
it looks). **Revert cost:** medium-low — remove the overlay; the
break machinery never knew it existed.
**Law it must not break:** HOLD-absolutely — display and return
only, zero controls over the session; and never green / no AI
images are trivially held (type on a plaque, nothing else).

## Rejected on the bench — recorded so they are not re-proposed

- **A "start Safe Break" button with a duration picker on the
  launcher.** The picker is a mid-session decision (HOLD's exact
  prohibition) and a negotiation surface. DF-09's Meal Break is the
  lawful shape of the same want: length and count fixed rested, the
  in-session act is one gesture.
- **Apron tiles for PEEK apps.** A one-tap peek is a peek without
  the Held Door's breath — the door *is* the price of the look.
  Apron stays OPEN-only, forever.
- **Gravity input (tilt/shake to do anything).** Gravity Light is
  light, never input. The day a tilt dismisses, extends, or reveals
  anything, the sensor has become an unpriced exit with plausible
  deniability. Dead on arrival, permanently.
- **A "day progress" percentage anywhere on the launcher.** YPT owns
  the day's study total; a Monolith day-percentage would be a
  second, undercounting rendering of his progress (one fact, one
  rendering) and a score by the back door. The Terrain Dial (W-1)
  renders the *session's* structure only, never the day's grade.

---

## Summary for Ren

**Part 1:** 15 frictions — 4 FIGHTS (DF-03 early-wake SLEEP, DF-06
lectures-behind-peek as seeded, DF-08 09:00-mid-sadhana, DF-09 the
meal — DF-08/DF-09 forming one continuous late-morning collision)
and 11 TAXES. Print-layer cures priced in-file: DF-01,
DF-02, DF-05, DF-10, DF-11, DF-12, DF-15 — together ≈2 builder-days
and they close most of the list. Machinery needing his ruling:
DF-09's Meal Break (≈2–3 days), DF-03's SLEEP stand-down window
(≈1–2 days). One rested edit: DF-08's seed retime to 09:45.
**Worst anchor: the first meal (~09:40)** — daily, physiological,
and the only need with no lawful tool.
**Part 2:** Gravity Light — **BUILD**, phase-2, own commit, four
gates, ≈1–1.5 days, one-commit revert. Wild bench: **W-1 Terrain
Dial** (≈1–1.5 d), **W-2 The Apron** (≈2–3 d), **W-3 The Arm Slip**
(≈1 d), **W-4 The Break Pill** (≈2–3 d). No new exits anywhere in
this file; every mechanism dies cleanly if he hates it on the phone.

*— HUMANS / DAY-FIT + WILD / INNOVATORS bundle, design-revamp wave.
Evidence: Slice-1 build (`LauncherBlockScreen.kt`, `MainActivity.kt`),
routine seeds + session/break machinery (`MonolithRepository.kt`,
`MonolithModels.kt`), `REVAMP-DESIGN.md`, `REVAMP-INTERACTION.md`.
No code was touched by this file.*
