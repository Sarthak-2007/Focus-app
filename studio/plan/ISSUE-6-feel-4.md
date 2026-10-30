# ISSUE #6 — FEEL-4 · ATMOSPHERE — THE DECK ACROSS A WHOLE NIGHT
## Cell: Rost (lead) · FEEL-4 seat (atmosphere) · Fri Oct 30, 2026
## Status: DECLARED, paper only. Nothing compiled. Nothing committed (Rost commits).

Read first, as briefed: `studio/plan/AUDIT-deck.md` whole (§6's
finish verdict is this seat's charge: brightness hierarchy inverted
at sealed night, and the static triple-printed MM:SS at 72sp is
"an OLED burn-in risk the AOD clock already knows to dodge and
the Deck does not"), `studio/plan/ISSUE-6-innovator.md` (P6 Dark
Deck — the running Deck rests dark, Safe Break is the one lit
running room), `TactileDeckScreen.kt` whole, and the AOD desk
clock's code (`AlwaysOnDeskClockOverlay` in
`AirlockAndDisarmOverlays.kt` + its driver in
`MonolithViewModel.startTickerLoop`). Cross-checked against
`ISSUE-6-designer.md` (D5/D6/D8/D13 — no verdict reopened),
`Color.kt` / `Theme.kt` token tables, and
`MonolithCustomComponents.kt` (`inkControlFill`,
`BlockitActionChip`, the strip tones).

**Boundary honoured:** the header band / Guard line and the top
status strip are host chrome and issue #4's ground. This spec
budgets Deck-owned light only; host chrome enters the room's
feel as a fixed cost (§1.3) and is never re-toned here. No green
or near-green anywhere in what follows — every tone named is
from the existing zero-green token lists.

---

## §0 · THE LAW IN ONE PARAGRAPH

**Darkness on the Deck is a state, not a theme.** The scheme
only decides *which palette* a state may spend; the state
decides *how much of it is lit*. A sealed session that leaves
a furnished, full-ink room burning on a desk for six hours has
failed atmospherically however correct its colours are — it
spends light, battery, OLED life, and the sleeper's dark room
on information nobody is reading. So each state below carries
a **darkness budget**: a ceiling on lit area by luminance tier,
a named brightest element, and a rule for how the room returns
to rest. Fault is the one state with no budget — by design
(Dens's law, adopted by the audit: fault gets more room than
health; here: fault gets more *light* than health, and that is
the alarm working, not a budget breach).

---

## §1 · WHERE NIGHT COMES FROM — THE MACHINERY AS IT STANDS

### 1.1 The evaluation, not the clock

Night is an evaluation (`MonolithViewModel.evaluateTheme`,
chrome C4), with ruled precedence: **(1)** a session is active
→ NIGHT, the session owns it; **(2)** else an enabled, unpaused
SLEEP routine window is open → NIGHT, the routine owns it;
**(3)** else DAY. The Deck mirrors it twice over:
`effectiveNight = isDarkTheme || isSessionRunning` (hero ink,
imprint tense) and the host's own `effectiveNight = darkTheme
|| sealed`. Consequence this spec is built on: **a running
Deck is in the night scheme at 14:00 exactly as at 03:30.**
"Night" in this file therefore names a *scheme and a budget*,
never a wall-clock hour. §6 prices what that feels like in
both rooms.

### 1.2 The seal darkens in two beats, never a fade

At the seal the hero reads t=0's night at once; the GROUND —
eyebrow, wheel, slide — steps to night at t=400ms exactly,
with the scheme pin (`groundNight`, Zug-II §5.1). The theme
evaluation itself swaps scheme on its frame — a 0ms cut. This
spec keeps that grammar everywhere: **every atmosphere
transition in this file is a cut at a named instant, never a
cross-fade.** A room that fades looks like software dimming;
a room that steps feels like a machine changing state, and a
half-asleep eye can verify a step where it cannot verify a
fade. The one timed exception already in the building is the
seal shot's own 1050ms vertigo, re-homed to the ground by
F15 — the instrument seals in focus, the room recedes around
it, and the darkness lands with the settle.

### 1.3 Fixed costs this budget cannot spend down

Counted in the room's feel, owned elsewhere: the header band
(48dp, invariant in both schemes), the rail (52dp), and — in
the AOD overlay, not the Deck — `FLAG_KEEP_SCREEN_ON`. The
Deck itself sets no keep-screen-on flag: a running Deck left
alone can still be killed by the system screen timeout, and
P6's dark room is specced to be *correct if the screen stays
on*, not to demand that it does. Keeping the Deck lit all
night is the desk clock's job, entered deliberately through
DESK CLOCK — which is precisely why the desk clock, not the
Deck, is the surface that already owns burn-in discipline
(§5), and why the Deck's discipline is an import, not a copy.

### 1.4 THE GAP — idle at 03:30 outside the SLEEP window

Precedence (3) has a hole the vignette in §6 must not paper
over: at 03:30, with no session running and no SLEEP window
open, the evaluation returns DAY and the idle Deck blazes
porcelain `#E4DFDA` in a dark room. This is not this seat's
machinery to re-rule — the evaluation is Sten's precedence,
one evaluation driving scheme and PARK readout together, and
a wall-clock term bolted on inside the Deck would split the
two readers the evaluation exists to keep in agreement.
**Flagged to Rost as an open question, not silently specced:**
either the hole is accepted (the SLEEP routine is the
intended owner of the night, and a 03:30 idle Deck outside it
is a man awake and working, for whom day ink is honest), or
Ren rules a clock term into `evaluateTheme` itself — priced
**M** as an evaluation change with PARK-readout consequences,
never as a Deck-local patch. Every 03:30 figure in §6 is
written for the covered cases (session running, or SLEEP
window open) and says so.

---

## §2 · THE DARKNESS BUDGET — TIERS AND THE MEASURING RULE

### 2.1 Luminance tiers (scheme-relative, never raw hex as law)

The innovator's flag on F12 stands and governs this section:
a luminance *relationship* frozen into one hex drifts the
first time a look re-tones the card. Budgets below are stated
in **tiers**; each scheme's tokens satisfy the tiers, and
Schein's Lamp Test (§2.3) checks the tiers, not the hexes.
The one hex quoted per tier is the *current* scheme's answer,
recorded so the builder need not hunt it.

| Tier | Rank | Day token (current) | Night token (current) | Night contrast note (from `Color.kt`) |
|---|---|---|---|---|
| T0 recess | below ground | `surfaceVariant` `#D8D1CB` | `NightColorSurfaceVariant` `#100E0C` | below ground by design |
| T1 ground | the room | `TokenColorBackground` `#E4DFDA` | `NightColorBackground` `#171412` | staircase base |
| T2 card | the instrument's body | `TokenColorSurfaceBright` `#FAF6F0` | `NightColorSurfaceBright` `#362C27` | 1.35:1 over night ground |
| T3 control | filled controls, sheets | ink `#252323` (day fills are ink) | `NightColorSurfaceSheet` `#4A423C` | 1.86:1 over night ground — **the convicted tier, see §4 N2** |
| T4 body ink | standing print | `TokenTextColorPrimary` `#2C2A29` | `NightTextColorPrimary` `#D2C6BB` | 11:1 tier |
| T5 glyph ink | the one numeral | `onBackground` | `NightColorOnSurface` `#EDE3D8` | 14.5:1 over ground |
| T6 cream | print on ink fills | `TokenColorOnPrimary` `#FBF1EC` | `NightColorPrimary` `#FFE4CF` | flips to cream; print only (N1) |
| TA accent | spent rarely | `TokenColorPrimaryContainer` `#F55314` | `NightColorPrimaryContainer` `#F47442` | text/commit only, accent law (N5) |

### 2.2 Budget form

Each state in §3 is budgeted on four numbers:

- **Lit share** — ceiling on the share of Deck-owned pixels
  above T1, split T2 / T3. (T0 recess counts as dark: it is
  below ground.) Paper figures, set as ceilings for the Lamp
  Test to enforce on renders, not as measurements of tonight's
  build — tonight's build has never been measured, which is
  the audit §6 finding this budget exists to close.
- **Brightest element** — the single element allowed at the
  state's top tier. If a render shows two, the state fails.
- **Glyph tier** — the tier the hub/hero numeral may print at
  in that state (T5 full, or T4 at rest, §3.4).
- **Dwell** — how long the state may hold its budget before
  it must step down (only running states dwell; the rest are
  visited).

### 2.3 The measuring rule — Lamp Test, extended

Schein's Lamp Test (endorsed by the audit, D13's acceptance
instrument) renders pure-luminance frames and checks the
staircase `glyph > instrument > controls > ground`. This seat
extends its frame list from three to the states that dwell:
**idle-day, idle-night, sealed-dark (after grace expiry),
sealed-peek, break-lit, fault.** Pass terms per frame: the
monotonic staircase holds (fault excepted, §0), the lit-share
ceilings of §3 hold, and exactly one element sits at the
frame's top tier. A frame that cannot be rendered cannot be
passed — any state added to the Deck later owes a frame
before it owes a defence.

---

## §3 · PER-STATE ATMOSPHERE SPEC

Summary first; the subsections are the spec, the table is
only its index. "Current" names tonight's behaviour where it
differs from the ruling, so Rost can see what each line costs.

| State | Ground | Brightest element | Lit ceiling (T2+T3) | Glyph | Dwell / exit |
|---|---|---|---|---|---|
| IDLE · DAY | T1 day | hero glyph (T5 day ink) | card is the one T2 mass | T5 | visited, minutes |
| IDLE · NIGHT | T1 night | hero glyph | card T2 only; controls ≤ T2 (§4 N2) | T5 | visited, minutes |
| PROVING | as idle | proof drawer (T3, by rank) | drawer is the one lit addition | frozen, T5 | seconds + 1 settle beat |
| JAMMED / FAULT | as idle | the sentence / drawer | **no budget** (§0) | frozen, T5 | until healed or annulled |
| SEAL SHOT (1050ms) | steps at t=400 | the instrument, in focus | as idle-night | T5 | t=1050 settle → GRACE |
| GRACE (≤10s) | T1 night | hub countdown | furnished night (§3.5) | T5 | expires → RUNNING-DARK |
| RUNNING-DARK (timed) | T1 night | hub glyph, at rest tier | ≤ 8% (STOP chair only at T3-stepped) | **T4 at rest**, T5 on peek/touch | hours; peek/break/fault exit |
| RUNNING-FLOW | T1 night | hub glyph, at rest tier | ≤ 8%, no rim, no bar | T4 at rest | hours; same exits |
| PEEK (P6, 5s) | T1 night | hub glyph | furnished, ≤ 30% | T5 | 5s or verb → RUNNING-DARK |
| SAFE BREAK | T1 night | break countdown in hub | furnished + END BREAK, ≤ 35% | T5 | break's life → RUNNING-DARK, one cut |
| HELD FINISH | frozen frame | frozen glyph | as the frame it froze | frozen tier | detention; drift still runs (§5.4) |
| RECEIPT | T1 (+ slip) | the slip (T2 day / T2 night) | slip is the one lit mass | — | slip-swipe / PARK / rail only (F19) |

### 3.1 IDLE · DAY — the paper instrument

Ground T1 porcelain, hero card the screen's one T2 raised
mass, wheel numerals and hub in day ink, slide in ink fill
with cream print. Full contrast is *correct* here: the room
competes with daylight (§6) and a dimmed day Deck reads as
broken, not calm. The fake day emboss shadow on the eyebrow
dies with the eyebrow (audit §3, D3) — day depth comes from
the card's real shadow/tone (F11: 2dp day), never from a
printed highlight. Accent: the slide's commit only, per the
accent law. This is the only state whose budget is generous,
because it is the only state whose job — set, verify, seal —
is done by a man looking straight at the phone in good light.

### 3.2 IDLE · NIGHT — the same desk, after dark

Entered by SLEEP window or manual scheme night, no session
yet. Same composition as idle-day, night tokens: ground
`#171412`, card `#362C27` (T2, 1.35:1 — a step the eye finds
by edge, not by glow), glyph T5. **Controls in idle-night
obey §4 N2 from the start:** no filled control may sit at
raw T3 while the card sits at T2 — the inversion Schein
convicted at sealed night (`#4A423C` fills outshining the
`#362C27` card) is already latent at idle-night wherever an
ink-filled cap composes beside the card. The arming moment
at 01:00 (Muede's walk) happens in this room: it must be
*operable* at full glyph contrast and *unverifiable* nowhere
— atmosphere never trades against the proof drawer's right
to be read (§3.3).

### 3.3 PROVING and JAMMED / FAULT — darkness yields, in order

PROVING: the instrument freezes as set; the proof drawer is
the state's one lit addition and takes T3 by rank — for the
proving seconds the drawer *outranks* the instrument (D13's
fault-family exception begins here, at proving, not only at
fault: the question "is it holding?" is the only question,
and its room is the brightest). One settle beat after success
the drawer collapses and the room steps down through the
shot into GRACE — proof gets the stage for exactly the
seconds it is the question, and its light retires with it.
JAMMED / FAULT: **no darkness budget.** Drawer, sentence
(promoted above the slide in the pinned stack, D10), and
fault-density strip cells at cap fill with the doubled fault
hairline — tones, never a new hue; TA accent appears only
where the accent law already permits it. A fault that
respected the night budget would read as decoration. The
budget resumes the frame the wire heals: fault light is
latched to the fault, never to a timer.

### 3.4 RUNNING-DARK — the ruling state of this file (P6 landed in atmosphere)

Timed running, after grace expiry, is the Deck's longest
dwell — hours, face-up on a desk or face-down forgotten — and
its resting form is P6's: **the Fusion hub readout and the
STOP chair, nothing else composed, nothing else lit.**

- **Composition:** hub (glyph + rim ink) centred at the
  wheel's address (Δ-address = 0, D8); STOP chair at the
  slide's address, full 72dp (D11/F6). No utilities, no task
  line, no strip, no sentence. The imprint stands as the foot
  at T4 body tier — it is the seal's record, printed at record
  brightness, never at glyph brightness.
- **Budget:** ≥ 92% of Deck-owned pixels at T1 or below
  (ground + recess). T2 spend: the hub's immediate field only.
  T3 spend: the STOP chair's footprint alone, and only at the
  stepped-down control fill of §4 N2 — the chair must be
  findable in the dark without being a lit rectangle in it.
- **Glyph at rest tier:** the ticking readout prints at **T4**
  (`NightTextColorPrimary #D2C6BB`, the 11:1 body tier), not
  T5. Rationale, both halves load-bearing: (a) room feel —
  T5 at 14.5:1 is across-the-room ink; a man who has sealed
  himself away from the phone does not need across-the-room
  ink, and a sleeping room does not forgive it; (b) burn-in —
  differential OLED wear scales with luminance × dwell, so
  the rest tier is the single largest burn-in mitigation in
  §5, larger than the position shift it accompanies. T4 at
  11:1 stays honestly readable at arm's length in daylight
  shade (§6.2's noon check) — rest tier is a step, not a
  disappearance. FLOW (§3.6) takes the same tier.
- **Rim ink:** progress lives in the tick ring at instrument
  tier, never glyph tier; the rim is a filling edge, not a
  second numeral. It is also, not incidentally, the one
  running element whose lit pixels *move* by construction.
- **What wakes it:** a touch anywhere on the hub raises PEEK
  (§3.7) — light is summoned by a deliberate hold, never by
  proximity, motion, or a notification (held notifications
  are the HELD count's fact, and the count waits for the peek
  like everything else).
- **Current vs ruled:** tonight the running Deck composes
  the furnished room standing (utilities row, task line,
  progress track + %, strip, chair) in forced night scheme
  for the session's whole life. The dark room is P6's lane
  (M) — this section is its atmosphere acceptance spec, and
  the Lamp Test's sealed-dark frame is its check.

### 3.5 GRACE — the landing light (first ≤ 10s of running)

The seal shot settles into a *furnished, full-tier* room:
glyph T5, utilities composed, STOP chair present with its
free tap live. This is deliberate overspend and it is priced
as a landing, not a leak: a man who has just sealed needs
one beat to see the whole machine standing — what he sealed,
what it holds, where STOP lives — before the room closes.
At grace expiry (the same 10s that prices STOP, F6) the
room steps **in one cut** to RUNNING-DARK: furnishings
uncompose, glyph drops to rest tier. Two darkness beats
total — t=400 ground step, grace-expiry close — and then
stillness. The grace countdown itself lives in the hub (F6),
so the landing light's clock and its verb share one address.

### 3.6 RUNNING-FLOW — dark, and denominator-free

As RUNNING-DARK with two subtractions: no rim ink ever (NO
END means no denominator — a ring filling toward nothing is
a lie with a luminance budget), and the hub counts *up*.
FLOW's burn-in profile is the worst on the Deck (an unbounded
count-up can dwell all night), which is why FLOW gets no
exemption from the rest tier or from §5's drift — it is the
state §5 was written for, timed sessions merely inherit it.

### 3.7 PEEK — five seconds of summoned light (P6)

The 600ms press-and-hold on the hub raises the furnishings —
ghost title + DONE, DESK CLOCK, HELD with its noun — at full
tiers: glyph back to T5, controls at stepped-down fill (§4
N2), furnishings ≤ 30% lit share. The draining rim (P6's ink
running backwards) is the state's clock and its atmosphere
in one: light visibly *leaving* the room is the promise that
it will. Exit is guaranteed by the timer, not by his
discipline: 5s, or any verb's completion, and the room cuts
back to RUNNING-DARK. A peek that needs a second gesture to
end is a room, not a peek, and fails this spec. DESK CLOCK
taken from the peek hands light-keeping to the AOD overlay
(§1.3) — the Deck behind it stays dark.

### 3.8 SAFE BREAK — the one lit running room

The exception, ruled by the brief and by P6, and specced
here as a *room with a closing time*:

- **Why it is lit:** the break is the one running interval
  where the phone is legitimately in his hand. Its light is
  not a budget breach; it is the signal that distinguishes
  "in hand, on purpose, for ten minutes" from "the session
  leaked." A break that looked like RUNNING-DARK would train
  him to treat the dark room as touchable.
- **Composition:** as RUNNING, furnished standing — hub IS
  the break countdown (F5/D-designer §2: the largest print
  answers "how long is my break?"), foot line `BACK AT
  HH:MM`, END BREAK chip full width in the utilities' room
  (its existing ST-02 seating), primary ink — during a break
  END BREAK is the Deck's one forward verb and wears the
  room's brightest control fill, stepped per §4 N2 like every
  control, its *width* carrying the rank its fill no longer
  shouts.
- **Budget:** ≤ 35% lit share, glyph T5, dwell = the break's
  own life (10 minutes between blocks, per the span formula
  blocks × block + (blocks−1) × 10). The budget's enforcement
  is the break timer itself: **at break end the room cuts to
  RUNNING-DARK in the same frame the session resumes** — no
  fade, no lingering furnished afterglow, no "just checked
  one thing" tail. The cut is the lesson the room teaches:
  lit means break, dark means sealed, and the boundary is a
  frame, not a gradient.
- **Night within night:** a break at 03:30 in a SLEEP session
  is still the lit room at full T5 — briefly. Its ten lit
  minutes against the night session's dark hours is the
  ratio that makes the exception affordable, atmospherically
  and on the panel (§5.5).

### 3.9 HELD FINISH and RECEIPT — frozen light, then paper

HELD FINISH renders the session's last frame — frozen
readout, frozen rim, slide seated. Its atmosphere is that
frame's, unchanged, with one exception: §5's drift keeps
running (§5.4), because a frozen frame is the longest static
dwell the Deck can produce and "frozen" names the *values*,
never the pixels' duty to move. RECEIPT: the room behind the
slip sits at its ground; the slip is the one lit mass (T2 in
either scheme — paper in day, the raised night tone after
dark), dismissed by slip-swipe or PARK/rail only (F19). No
scrim-tap death: a stray tap shakes the slip, and a shake is
the receipt's whole vocabulary of refusal.

### 3.10 ROUTINE-BORN sessions — same darkness, named

A SLEEP (or other routine-born) session is born RUNNING and
inherits its state's full budget — a night SLEEP session
rests in RUNNING-DARK like any other. Its one atmospheric
addition is print, not light: the foot line carries its name
for its whole life (`SLEEP · ROUTINE — ENDS HH:MM`, designer
§2) at the imprint's T4 record tier. It must never be *lit*
differently from a hand-armed session — a routine that
glowed would read as a different machine, and the audit's
charge was that it is currently indistinguishable in the
wrong way (its *origin* is silent), not that it should be
distinguishable by brightness.

---

## §4 · NIGHT INK BEHAVIOUR — RULES N1–N8

**N1 · Ink inverts; it never greys, and cream is print.**
The night palette's founding move (`Color.kt`): primary ink
flips to cream `#FFE4CF` rather than greying. The corollary
the current code half-knows: cream is for *print on a fill*,
never a fill itself. `inkControlFill()` exists because a
cream filled control would be the brightest mass on the
screen — the rule is ratified here as law for every Deck
control in every state: **at night, fills step down the
staircase; print steps up it. Nothing does both.**

**N2 · The control-fill step-down, as a ratio (F12 cured).**
Tonight's inversion, measured by Schein and adopted by the
audit: filled controls at `#4A423C` (T3, 1.86:1) outshine the
hero card at `#362C27` (T2, 1.35:1) — three dark masses shout
where the instrument should stand alone. F12 as filed names
`#2A2320`; the innovator's flag is upheld — the hex is one
scheme's answer, not the law. **The law: in night states, a
filled control's fill must sit strictly below the instrument
card's tone on the staircase** (fill tier < card tier), with
its print at T6 cream. For the current scheme that prices
out at a fill between ground `#171412` and card `#362C27` —
F12's `#2A2320` satisfies it and may land as this scheme's
token — but the token the F17 sweep mints is named by rank
(control-fill-at-night), and any future look must satisfy the
inequality, not inherit the hex. Scope: DESK CLOCK, HELD,
DONE's key-cap, the STOP chair, END BREAK — every
`isPrimaryInk` and default chip on the Deck, in idle-night
and in every running state. Day is untouched: day fills are
ink, and ink *is* the top of the day staircase.

**N3 · The muted tier exists — use it or lose the hierarchy.**
Night `onSurfaceVariant #776A60` (3.5:1) is the only true mute
in the palette (its predecessor printed at ~14.9:1 — no mute
existed, everything shouted). Eyebrow-successor print, the
ghost title at idle, the foot line, and HELD's noun all print
at muted tier at night. A fact that cannot survive 3.5:1 is
not a quiet fact — it is a loud fact in the wrong chair, and
it moves (§3's state rooms), it does not get brighter.

**N4 · Ghosts are a day technique.** The hero's press ghosts
are ruled single-print by F10 (one tokenised shadow ghost,
ghosts OFF on FLOW and on the ticking readout). Atmosphere
adds the night half: at night the surviving shadow ghost is
the *only* ghost permitted, in the shadow direction only,
and the catch ghost dies in both schemes — tonight's night
catch ghost (`#594A423C`) is literally the convicted T3
control fill leaking into the numeral's face at 0x59 alpha.
A press does not strike a moving target, and at night it
does not strike through a lit smear either.

**N5 · Accent is a word, never a wash.** Night accent
`#F47442` warms one step for dark ground and stays under the
accent law: commit verbs' text, never a second fill, never
a furniture colour. In RUNNING-DARK the accent budget is
zero — no element in the dark room may carry TA; the room's
only warm note is the glyph's own cream tier. Fault and
grace spend accent only where the shipped accent law already
spends it.

**N6 · Hairlines hold the room when fills retire.** As fills
step down (N2) and furnishings uncompose (RUNNING-DARK), the
night hairlines (`#8F8888` at 0x75 alpha) become the room's
only drawn geometry besides the hub and chair. They are
specced to *stay*: a dark room with no hairlines reads as a
dead screen, and a dead screen invites a tap to check it is
alive — the exact fiddle P6 exists to refuse. The imprint's
double rule likewise never dims below its standing tone.

**N7 · The struck imprint is the one full-alpha exception.**
Tenant ink to full alpha when struck (Schein, audit §3) —
in the dark room the imprint is a record being *read back*,
and records do not mumble. Its tier is T4 (§3.4), its alpha
is full; the two together are the record's correct volume.

**N8 · No raw hex survives this spec.** Tonight the hero
glyph is a literal `Color(0xFFEDE3D8)` and its ghosts are
literals — the F17 debt, in the numeral itself. Every tier
in §2.1 must land as a scheme token before any look touches
this screen (audit sequencing), and this file's tier names
are the vocabulary that sweep mints: glyph, instrument,
control-fill-at-night, ground, recess, muted, record. A Deck
colour that cannot be named as a tier does not belong on
the Deck (D13's term, adopted).

---

## §5 · BURN-IN — THE RULING: IMPORT THE AOD LAW, ADAPTED, NOT COPIED

**Ruling, plainly: IMPORT.** The Deck's ticking hub readout
takes the desk clock's position-shift discipline, adapted in
three named ways (glyph-layer only, running-gated, and paired
with the rest-tier dimming the AOD itself lacks). Priced
**S** as a rider on F18, below. The alternative — pricing why
not — was weighed and fails on the evidence: the Deck's
exposure is real (§5.2), the donor mechanism exists and is
already paid for (§5.1), and the only serious obstacle (the
Δ-address law) is solved by the adaptation, not by refusal.

### 5.1 The donor law, exactly as it stands in the tree

- **Driver:** `MonolithViewModel.startTickerLoop` increments
  `_burnInFigure8MinuteStep` once per 60 ticker seconds
  (`tickSecondCounter % 60 == 0`), modulo 360. One counter,
  app-wide, already running whenever the VM lives.
- **Consumer:** `AlwaysOnDeskClockOverlay`
  (`AirlockAndDisarmOverlays.kt`) computes
  `dx = ((burnInStep % 5) − 2) * 3`,
  `dy = (((burnInStep / 5) % 5) − 2) * 3`
  and applies `Modifier.offset { IntOffset(dx, dy) }` to the
  **whole column** — header line, 96sp numerals, progress
  strip, task banner together.
- **Ground:** true black (`Color.Black` — OLED pixels off),
  with `FLAG_KEEP_SCREEN_ON` held for the overlay's life.
- **Cadence wisdom worth keeping:** the shift steps on the
  minute, in step with the value it protects changing its
  minute digit — the eye attributes the motion to the tick,
  never to a wandering clock.

Three honest limits of the donor, recorded so the import
does not inherit its myths:

1. **It is not a figure-8.** The name `burnInFigure8MinuteStep`
   promises a Lissajous; the arithmetic is a **row-major
   raster over a 5×5 grid** — dx sweeps −6,−3,0,+3,+6 px
   fastest, dy steps every 5 minutes, with a teleport from
   the right edge back to the left every fifth step. 25
   distinct positions, full cycle 25 minutes; step 25 aliases
   step 0 exactly, so the modulo-360 counter's remaining 335
   values never produce a new position. The comment-truth
   pass (F21's family) should rename or re-comment it — an
   atmosphere mechanism nobody can describe truthfully will
   be "fixed" by a future lane into something worse.
2. **The amplitude is small against the glyph.** ±6 **pixels**
   (IntOffset is px, not dp — roughly ±2dp at typical
   density) against a 96sp numeral whose stroke width is
   several times that: the 25 footprints overlap heavily.
   The shift *spreads* differential wear; it does not clear
   any pixel's duty. It is a mitigation with a 25-minute
   period, not an immunity — and the AOD pairs it with
   nothing else (its numerals print at full `NightColorPrimary
   #FFE4CF`, the palette's brightest cream, all night).
3. **Its ground does the heavy lifting.** True black means
   the AOD's unlit pixels are *off* — zero wear, zero light.
   The Deck's ground is `#171412`, lit everywhere, always.
   The Deck cannot import that term (its ground is scheme
   ground, and looks must inherit it) and must not pretend
   the shift alone buys AOD-grade safety.

### 5.2 The Deck's exposure, named pixel by pixel

What actually dwells on the running Deck, longest first:

- **The colon and the tens-of-minutes digit** of the MM:SS
  hub readout (72sp class, Sora Bold, `DialClockTextStyle`):
  the colon never changes for the session's whole life; the
  tens digit changes a handful of times per hour. These are
  the burn candidates — in the current room they are worse
  than static: **triple-printed** with ±1dp ghosts, three
  overlapping footprints struck for hours (audit §6).
- **The tick ring / rim** (Fusion form): tick marks are fixed
  geometry for the session's life; rim ink moves as it fills
  and is thereby self-protecting along the arc, but the
  unfilled ticks dwell.
- **The STOP chair's outline and word**: a static lit
  rectangle + static cream print, hours at a time.
- **The imprint's double rule and struck tenant**: static
  print, but at T4 record tier and hairline weight — low
  luminance × thin geometry, the least exposure on the face.
- Seconds and ones-of-minutes digits change constantly and
  need no protection; FLOW's count-up (§3.6) dwells its colon
  and high digits longest of all and is the ruling case.

Exposure condition: the Deck burns only if the screen stays
on — which is exactly the behaviour P6's dark room invites
(phone left face-up, session running, screen awake on a
desk charger). A furnished bright Deck discourages the
dwell; a correct dark Deck *creates* it. Shipping P6
without §5 would be building the exposure and declining
its discipline.

### 5.3 The adaptation — three changes to the donor law

**A1 · Shift the print layer, never the instrument.**
The AOD shifts its whole column because its column *is*
print. The Deck's wheel is an instrument: its ring registers
against detents and values, the slide/chair addresses are
Δ = 0 law across the seal (designer §5), and shifting the
wheel would move a control's geometry to protect a glyph.
So: **in running states only, the hub's print layer — glyph,
rim ink, and tick marks, drawn inside the hub's fixed bounds
— takes the grid offset; the wheel's physics, hitbox, and
address never move.** In the interim (pre-Fusion) room the
same rule reads: the hero glyph `Box` takes the offset; the
card, wheel, and everything else stand still. The touch
target, where the hub carries one, is the unshifted bounds —
drift is a property of ink, never of the hand's map.

**A2 · Gate the drift to the dwell.** Drift runs only while
`isSessionRunning && !shotActive` (routine-born and restored
sessions included — their dwell is identical). Idle's span
print stands for minutes and needs no protection; drifting
it would spend the mechanism's invisibility where it buys
nothing and would teach the eye that the setting instrument
wanders. During the seal shot the offset freezes at its
current step (the shot's own motion owns those 1050ms);
during a slide press or peek *hold* the offset likewise
holds — a target being aimed at does not step mid-gesture —
and resumes stepping at the next minute boundary after.
HELD FINISH: drift **continues** (§3.9) — frozen values,
moving pixels.

**A3 · Pair the shift with the dimming the donor lacks.**
The Deck's second defence is §3.4's rest tier: glyph at T4
(11:1) instead of T5 (14.5:1) for the dwell's whole length.
Wear scales with luminance × time; the tier step cuts the
first term for every pixel at once, including the ones the
±6px grid cannot clear. **Flagged beyond this cell, for
Rost to carry or drop:** the AOD should inherit A3 in
return (a rest-tier desk-clock numeral after its first
hour) — the donor needs the import's other half as much as
the Deck needs the shift. Not specced here; the overlay is
not this issue's room.

**Kept unchanged from the donor:** the grid itself (5×5,
3px step, ±6px span — do not invent a second grid; one
mechanism, one tuning history), the 60-second cadence
(faster would shimmer against a seconds-ticking readout and
be *seen*, which defeats it; slower starves the colon), and
the single VM counter as the only source — the Deck consumes
`burnInFigure8MinuteStep`; it never forks a second clock.
The Deck currently receives no such parameter: MainActivity
passes `burnInStep` only to the overlay (line ~726). The
import is therefore one new Deck input (a payload field
under the innovator's P3 contract, when that lands; a plain
parameter until then, cut with the rest of F1's discipline
— read in the body, or it joins the dead 21).

**Not imported:** true-black ground (§5.1.3), whole-column
shift (A1), `FLAG_KEEP_SCREEN_ON` (§1.3), and the figure-8
name (F21 rider in §5.4's price).

### 5.4 Price and sequencing

**S, as a rider on F18 (the Fusion)** — the hub is being
rebuilt as the readout in that lane anyway; seating the
print-layer offset and the drift gate inside it is a
sitting's edit against a mechanism that already exists.
Built standalone on the current hero it is also **S**, but
**do not build it twice**: if F18's landing is the next
merge, the interim hero version is waste — the ghosts it
would offset are already ruled off the ticking readout by
F10, and one interim night of exposure is not worth a
throwaway lane. Sequencing, plainly: F10 lands (ghosts off
the ticking readout — tonight's worst term, three footprints
to one), F18 lands with §5.3 inside it, §3.4's rest tier
lands with P6. If Rost sequences P6 *before* F18, the rest
tier and the interim glyph offset both promote to that lane
and the price becomes **M** for the pair — recorded here so
the dependency is a choice, not a surprise.

### 5.5 Acceptance (paper checks for the builder's close)

1. Render the hub at steps 0–24: exactly 25 distinct print
   offsets, grid span ±6px on both axes, and the wheel /
   chair / imprint bounds **byte-identical** across all 25.
2. Step 25 renders identically to step 0 (the alias is
   documented, not discovered).
3. Offset frozen across the seal shot's 1050ms and across
   any press/hold; stepping resumes on a minute boundary.
4. Idle, proving, and receipt frames show offset (0,0) —
   the gate holds; held-finish frames show stepping.
5. Lamp Test sealed-dark frame (§2.3) passes at rest tier
   with drift running — brightness and motion are checked
   together, because on the panel they will live together.

---

## §6 · THE TWO ROOMS — 03:30 AND 14:00 AS SPEC CHECKS

These are not mood writing; each sentence is a §3/§4 ruling
wearing the hour it will actually meet.

### 6.1 03:30, dark room, session running (the covered case)

He sealed the SLEEP session hours ago, or a late block is
running; the phone lies face-up. What the room gets: a ground
at `#171412` that reads as *off* from the bed — not black,
but below the threshold where a dark-adapted eye hunts it.
One object: the hub, its glyph at rest tier, readable if he
lifts his head and looks, invisible if he doesn't. The STOP
chair is a shape he could find by memory and hairline, not
a lit rectangle competing with the clock. Nothing on the
face moves except the minute: the drift steps with the tick
and is never seen (§5.1), the rim fills at instrument tier,
the imprint sits struck and silent at record volume. If he
takes a break, the room *opens* — furnished, full tier, END
BREAK wide — for ten minutes, and the cut back to dark at
break end is itself information he can feel from across the
room: lit meant break; dark means sealed again. If the wire
drops at 04:00, the room breaks its own budget without
apology — drawer, sentence, fault tones — because at 03:30
a loud fault is worth more than a dark room, and a quiet
one would be the Deck lying by dimness. The desk clock, if
he opens it, takes over the all-night watch on true black
with its own discipline; the Deck behind it stays dark and
owes it nothing.

### 6.2 14:00, daylight, same states

The idle Deck is a paper instrument on a bright desk:
porcelain ground, one cream card, near-black ink, the slide
a black key with cream print — full contrast, honestly spent,
because daylight is the competing light and a Deck that
whispers at noon is a Deck that gets its brightness hunted
for in system settings. The running Deck at 14:00 is the
evaluation's hard case (§1.1): sealed means night scheme in
full sun. The spec's answer is the tier *ratios*, not the
absolute values: rest-tier glyph at 11:1 still reads at
arm's length in shade; the peek restores T5 and full
furnishings on a hold, so the daylight check — how long
left, what's held — is one deliberate gesture and five
full-tier seconds, after which the room re-darkens against
the sun exactly as it does against the night, because the
dark room's job at noon is identical: *stop being a phone*.
What this spec does not do is invent an ambient-light term:
no sensor reading exists in the tree, and a brightness the
machine guessed from a clock it already ignores (§1.4) would
be a second evaluation wearing the first one's name. If
device time shows the noon dark room failing in real sun,
that is a finding for Ren against the evaluation — priced
there, never patched here.

---

## §7 · FLAGS AND OPEN ITEMS FOR ROST

1. **§1.4's gap** (idle 03:30 outside the SLEEP window prints
   DAY) — needs Rost/Ren's acceptance or an evaluation ruling;
   this seat recommends acceptance (the routine owning the
   night is the design) but will not let the file pretend the
   hole isn't there.
2. **A3's return trip** (§5.3): the AOD desk clock should
   inherit rest-tier dimming. Outside this issue's room;
   carried as a flag, not a spec.
3. **F12's hex** (§4 N2): this file rules the ratio and lets
   `#2A2320` land as the current scheme's token value. If the
   finisher's sweep has already minted a different satisfying
   value, the inequality governs and this file does not
   contest the hex.
4. **Lamp Test frames** (§2.3): the three-frame instrument
   the audit endorsed becomes six frames under this spec.
   Whoever specifies the Lamp Test for Ren should take the
   extended list whole; the sealed-dark frame is the one
   tonight's Deck would fail worst.
5. **P6 dependency:** §3.4/§3.7/§3.8 are acceptance spec for
   the innovator's P6 (priced M there). Nothing in this file
   re-prices P6; §5.4's conditional M is the *pairing* price
   if P6 is sequenced before F18, stated there and only there.

## §8 · PRICE SUMMARY

| Item | Price | Lands with |
|---|---|---|
| Darkness budgets + Lamp Test frame extension (§2, §3) | spec only — checks ride the Lamp Test spec (S, Ren's instrument) | — |
| Night ink step-down as ratio + token (§4 N2, N8) | inside F12 (S) + F17 (M), unchanged | F12/F17 |
| RUNNING-DARK / PEEK / BREAK atmosphere (§3.4, §3.7, §3.8) | acceptance spec for P6 (M, innovator's price stands) | P6 |
| Glyph rest tier in running states (§3.4) | S inside P6; M paired with §5 if P6 precedes F18 (§5.4) | P6 |
| Burn-in import, adapted (§5.3) + comment-truth rename | **S** | rider on F18 |
| §1.4 evaluation gap | flagged; M *if* Ren rules it, never Deck-local | Ren's call |

— FEEL-4 (ATMOSPHERE), Rost's cell, issue #6. DECLARED,
uncompiled, uncommitted. File: `studio/plan/ISSUE-6-feel-4.md`.
