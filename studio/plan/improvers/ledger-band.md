# LEDGER EXAMINATION — Band (Improvers, bottom nav / chrome)
## Elements: the deflection count · the stat tiles
Plan wave — no code. Lens: placement. I own the chrome and the nav, so my
question for both elements is the same: *where does a small hard fact earn
its keep — and where is it just rent?*

Standards applied, unrelaxed: **TRUTH once** (my wave-1 self killed nav
badges for exactly this — a fact in chrome + tab + tile is stated three
times), **ARM ≤10s** (nothing below may add a second, a decision, or a
reading beat to the arm path or the airlock), **HOLD absolute**.
Overlap honesty, as briefed: YPT tracks his study — hours and the study
log. Monolith's deflection count exists nowhere else. Monolith's
session-run count and streak exist nowhere else *as Monolith counts
them*, but they shadow the same discipline YPT already logs in hours —
they are a second ledger of the same war, in weaker units.

Source read: `CurioVaultScreen.kt` as built, night render
`shots/night/n04_vault.png`, `MonolithViewModel.kt` count plumbing,
SYNTHESIS items 12, 31, 33–34.

---

## ELEMENT 1 — THE DEFLECTION COUNT

### Current form, truthfully
Worse than the brief describes, in three ways.

1. **It is not "deflections today."** The Vault tile reads `DEFLECTED 34`
   — an all-time, unscoped total with no window printed on it. A number
   with no scope is not a fact, it's a mood.
2. **It is stated at least three times already.** The Vault tile; the
   Guard drawer's header line (`BANKED …m • DEFLECTED …`); the
   Streak Tower overlay's DEFLECTED chip. The tile itself renders the
   number *twice* — 4dp dot-matrix glyphs beside the plain numeral, two
   renderings of one integer inside one box (the render shows the dot
   glyphs scrambling into noise next to a legible 34).
3. **The count itself is not yet true.** In the ViewModel it is
   `max(interceptLog.size, Σ per-app interceptCount)` — two sources,
   whichever is larger wins; that is not counting, that's rounding up.
   It seeds at **6** before any data loads. And the log it counts
   records *every* airlock outcome under one counter — "Stayed Focused",
   but also "Used 2m Peek" and "Split Into 10m Micro-Step". A peek he
   *took* increments the number labelled DEFLECTED. As built, this tile
   can go up because the shield held, or because it was opened.

Placement today: a tile on a screen nothing originates from, seen only
when he visits the past. The one fact in this app that proves the
blocking did anything is filed in the archive, in its least true form.

### Peak — judged home by home
- **The moment of deflection (airlock): REJECTED.** At the moment of
  temptation he needs the door shut and the way back, not a score. A
  running count there is readable two ways — "12th bounce, the wall
  holds" or "12th attempt, you're cracking" — and an ambiguous number
  at his weakest second is a tax on HOLD, not a support. It also makes
  the count gameable at the exact wrong place: bounce a blocked app,
  watch the number climb.
- **The chrome war-line: REJECTED.** Synthesis 34 already spends chrome
  on `186D TO NEET · TODAY 2H30 / 12H` — two facts, both study, both
  YPT's territory, letterpressed at zero accent (item 12: chrome 0).
  A third fact there does three damages: it crowds a two-fact line past
  reading-at-a-glance; it mixes the *machine's* proof into the *study*
  war-line, blurring whose number is whose; and it makes the count
  ambient — stared at all day, including on the day it should shame
  him and the day it should mean nothing. Chrome is for what he steers
  by every hour. Deflections are not steered by hourly.
- **Rebuilt Ledger tab: REJECTED as the home** (whatever Linea's hero
  verdict). The past is where this number goes to be admired, not used.
  He opens the Ledger rarely and originates nothing there; a fact whose
  only use is tuning the block belongs with the block.
- **Session end: REJECTED as the home, by one step.** A session-scoped
  count ("this block bounced 3") is the most *emotional* true form —
  but receipts are ephemeral by design, deflections also happen under
  routines and all-day guard outside any session (those would vanish),
  and the all-time total currently dumped on the tower overlay is
  precisely the wrong scope there. If Linea wants a session-window
  count on the receipt, that is a *second* statement next to my home
  below — TRUTH once says one of us yields, and I flag it to Ilya
  rather than silently double-print. My yield condition: receipt wins
  only if it counts the session window *and* the Shield line dies.
- **Shield: THE HOME.** Shield is where blocking lives — the app rows,
  the tiers, the routines — and the only place he ever asks the
  question this number answers: *is the wall working, and where is it
  being hit?* It is a working screen he already visits to tune the
  machine, not a monument he visits to feel.

### Peak spec (Shield)
- **Form:** one quiet mono line in Shield's status zone, at the head of
  the apps list — no tile, no box, no border, no dot-matrix.
  `DEFLECTED TODAY 07` — Anonymous Pro, 11sp, ink, **no accent**
  (Shield's accent budget is 0, item 12; orange never counts).
- **Scope:** TODAY only, calendar day. The all-time total dies — it
  answers no decision and exists only to be large.
- **Truth precondition (non-negotiable):** one source — the intercept
  log; the `max()` of two sources dies. Count only turn-aways: an
  airlock event where the app was bounced or he returned without a
  peek. "Used 2m Peek" never increments a number called DEFLECTED (if
  peeks are ever counted, that's a different fact with its own name —
  not in this element). No seed value, no floor: zero renders `00`,
  truthfully, on day one.
- **Update behaviour:** silent increment when an intercept is recorded;
  the numeral re-strikes in place (it is a computed count — no roll, no
  flip, no celebration, no haptic). Resets at day rollover without
  ceremony. Read-only — tapping it does nothing; it is a readout, not
  a door.
- **Cost check:** zero seconds on ARM (Shield is off the arm path),
  zero on HOLD (never appears in the airlock or the disarm), one
  statement of the fact app-wide once the Vault tile, drawer line, and
  tower-overlay chip are struck.

### VERDICT: **KEEP-AT-PEAK — home: Shield**, as the scoped mono line
above. The fact survives because it is Monolith's only unique proof of
work and YPT can never supply it. The tile, the all-time scope, and the
other two renderings die.

### The strongest case against my own verdict
Kill it everywhere, and here's why Shield is the *worst* home, not the
best. The number's valence is broken: 34 deflections can mean the wall
held 34 times or that he lunged 34 times — the same integer is a medal
on Tuesday and an indictment on Wednesday, and a readout whose sign
flips with his mood is not information, it's a mirror he checks while
configuring his defences. Shield is a *workbench*; putting a score on
a workbench invites score-keeping visits — openings that originate
nothing, the exact disease the Ledger tab is accused of, transplanted
into the one screen that currently has a job. Worse: my "truth
precondition" is a promissory note. As shipped the count is a `max()`
of two disagreeing sources over a log that counts peeks as victories;
if that plumbing isn't rebuilt — and this is a plan wave, nobody has
rebuilt it — then my spec doesn't move a true fact to a better home,
it *promotes a corrupt one* from a back room to the bench, where it
will be believed. And the emotional truth I dodged: the count he
actually feels is the session's, at its end, once. A daily ambient
line on Shield will be read at 03:40 on a cracking morning as a
running total of his failures to stay away — pressure with no action
attached, because there is nothing on Shield he can do about a count
that's already happened. Dead at any altitude beats a true-ish number
in the wrong emotional place. — That case wins if the count plumbing
is not rebuilt first. My verdict is conditional on it, and I say so
in the open: **no truth fix, no Shield line.**

---

## ELEMENT 2 — THE STAT TILES (sessions / deflections / streak as small tiles)

### Current form, truthfully
The brief's trio doesn't match the build, and the mismatch is itself a
finding. As built, the Vault opens with a **2×2 dot-matrix tile grid**:

| Tile | Value source | Truth status |
|---|---|---|
| FOCUSED MIN | total focused minutes | floored — ViewModel coerces to ≥35, initial state 75 |
| DEFLECTED | total interceptions | see Element 1 — max() of two sources, seed 6 |
| SESSIONS | `sessionHistory.size` | **floored at 4** (`coerceAtLeast(4)`) |
| TOWER BRICKS | derived: minutes/25 + completed sessions | **floored at 16**, a formula wearing a fact's clothes |

There is no streak tile. Streak lives in the tower below (Fader's
element) — the fourth tile is BRICKS, i.e. the tower's own number
stated a second time, in a box, directly above the tower that states
it as bricks.

The night render convicts the grid on one screen: SESSIONS tile says
**4** while the receipts header twenty centimetres below says
**TOTAL 3**. Same screen, two session counts, one of them manufactured
by a floor. Every tile also double-renders its number (dot-matrix +
plain numeral). And the grid's equal-weight boxes assert an equality
that is false: lifetime focused minutes (YPT's fact, undercounted —
Monolith only sees in-app sessions, so its total will *always* trail
the YPT log he actually trusts), a corrupt interception total, a
floored session count, and a derived brick formula are presented as
four peers under the heading COUNTER MACHINE TELEMETRY.

Placement: the top of the past-screen. Nothing originates from a tile.
No decision in this app has ever waited on "TOWER BRICKS 16."

### Peak?
A tile grid's only peak would be smaller, neater, truer tiles — and
that fails on principle, not execution:

- **FOCUSED MIN** has a better home already legislated: the war-line
  carries TODAY against the 12H bar (item 34), and the lifetime study
  record is YPT's study log. A Monolith lifetime total is the *worst*
  copy of this fact — partial by construction. Third statement, worst
  source. Dead.
- **SESSIONS** — the count of records is a property of the record
  list, not a stat. If the Ledger is rebuilt, its receipts header
  already prints TOTAL n (it does today); that header is the count's
  only honest form, because it sits on the thing it counts. As a
  free-standing tile it counts a list he isn't looking at. Dead *as a
  tile*; the header form is Linea's to keep or kill with the receipts.
- **DEFLECTIONS** survives — but by Element 1's verdict, as a scoped
  mono line on Shield, never as a tile. Its survival is not the grid's.
- **STREAK / BRICKS** — bricks is Fader's tower, restated. A streak
  tile would be the tower's third rendering (tower + faders + tile).
  Whatever the tower's fate, the tile copy is dead on TRUTH once
  alone.

No altitude fixes this: shrink the grid to 1×3, move it to a rebuilt
Ledger, retype it in mono — it remains a dashboard of equal boxes
asserting that four numbers matter equally, on a screen whose real
content (receipts) begins below it. Synthesis 31 already reached for
the knife ("other stats one mono line"); my lens goes one step
further: even that mono line *inside the Ledger* re-states focused
minutes a third time and sessions a second. The facts have homes.
The container has none.

### VERDICT: **DEAD at any altitude** — the tile grid as a form.
Not DEMOTE: a demoted grid is still a grid. The individual facts are
not executed with it — deflections emigrate to Shield (Element 1),
today's focus lives in the war-line, the session count lives only as
the receipts header over its own list, streak/bricks stand or fall
with Fader's tower as a single rendering. What dies is the idea that
Monolith keeps a telemetry dashboard of itself.

### The strongest case against my own verdict
The tiles are the only place in the app where *accumulation* is
visible at a glance, and accumulation is the whole psychology of a
drop year. The war-line is today-only and resets every morning to
zero — 2H30/12H on a bad day is an accusation; the lifetime tiles were
the counterweight, the proof that the machine has banked *anything*
across months: 150 minutes, 34 bounces, a tower on floor 3. Kill the
grid and Monolith becomes an app with no memory of scale — every day
starts at nothing, every screen reports the present tense, and the
one surface he could screenshot to show himself (or anyone) that the
grind compounds is gone; the night Vault, tiles and all, is today the
app's only screenshot-worthy frame, and mono lines don't photograph.
The floors and the max() are plumbing sins — fixable in an afternoon,
and I let fixable plumbing execute a *form*. A trued-up grid — real
sessions, real minutes, one rendering per number, no floors — is the
machine's instrument panel, and every real machine he admires has one:
gauges you glance at, not lists you audit. Receipts tell him what
happened; tiles told him *how much machine he has become*. Dead at
any altitude throws away the only cumulative mirror in the app
because its current copy lies — when the honest sentence was "make
it true," not "make it gone."

---

## Cross-element note for Ilya
Element 1 keeps exactly one small fact alive *outside* the Ledger;
Element 2 kills the Ledger's opening grid outright. Net effect on the
tab question: if the Ledger survives (Linea/Fader permitting), it now
opens on its hero/receipts with no telemetry porch in front of them —
which is Synthesis 31's shape arrived at from the placement side. If
the Ledger dies, nothing in my two elements needs rescuing from the
wreck except the deflection fact, and it has already moved to Shield.

— Band, Improvers (bottom nav), Ledger examination
