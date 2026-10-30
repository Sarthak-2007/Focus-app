# RAZOR — Designers, Intentionality seat
## CHROME: header · drawer · bottom nav — 3 Ledger variants × 2 Guard branches

Plan wave, no code. Structure: **§0 is the base chrome, written once.** Every variant
and branch after it is a *delta* against §0 — sizes, tokens and behaviours not
mentioned in a delta are inherited unchanged. Ren builds whichever combination
Sarthak picks by stacking §0 + one V-delta + one G-delta.

Binding law carried in, not relitigated: Concept = NONE. ARM ≤10s · HOLD absolute ·
TRUTH once. One fact, one rendering. Chrome carries **zero orange** — sole exception:
the 3dp armed lamp at FOCUS (SYNTHESIS 33). Nameplate data pills dead. DAY/NIGHT pill
dead — theme follows routine/schedule. Margin 20dp frozen. Radius 8/16. Label plate =
Anonymous Pro Bold 10sp, +2.0 tracking, letterpress offset +0.75dp. Everything answers
on DOWN; no ripple anywhere in chrome.

---

## §0 — BASE CHROME (all six combinations)

### 0.1 Geometry frame

Reference width 390dp (all formulas are width-parametric; numbers at 390dp).
Content margin 20dp each side → chrome field **350dp**. Spacing tokens only:
4 / 8 / 12 / 20 / 32 / 48.

### 0.2 Header — the WAR LINE (replaces the nameplate)

One band, **48dp tall**, top of every screen, no fill, no rule under it. Contents are
a single printed line, baseline at 30dp from band top:

```
186D TO NEET · TODAY 2H30 / 12H
```

- **Type:** the label plate — Anonymous Pro Bold 10sp, +2.0 tracking, letterpressed
  (glyph drawn twice: 0.75dp below in ground-shadow tone at 35% ink, main strike on
  top). Counts as quiet mono in ink, per settled law — no fill, no pill, no box.
- **Makeup, exactly three facts, three live sources:**
  1. `186D TO NEET` — day count, computed (see §0.3).
  2. `·` separator, one space each side.
  3. `TODAY 2H30 / 12H` — today's studied total against the 12H standard, from the
     timer (YPT) sync. This is the *only* rendering of today's total in chrome.
- **Left edge** at the 20dp margin. The **right end of the line is reserved chrome
  territory** — the G-branch deltas (§3) spend it; §0 leaves it empty.
- **Truth behaviour:** if the timer value is stale (>24h unsynced), the TODAY segment
  is *not* printed at all — absence renders as absence. The line degrades to
  `186D TO NEET`, left-anchored, no placeholder dashes, no greyed ghost. A stale
  number in a masthead teaches the eye to distrust the true one beside it.
- **Never in the header:** HELD count, GUARD count as a pill, theme word, app name.
  `MONOLITH ML-01` model-plate printing, if it ever ships, lives on the Guard spec
  plate (§3.2) — printed once, on the product's back, not repeated as a dashboard.

### 0.3 Day-count convention (stated, not invented)

NEET UG 2027 is **unannounced**. Assumption, printed here so it can be corrected in
one place: **exam = Sunday 2 May 2027** (first Sunday of May, matching the recent
pattern). Count = calendar days from tomorrow through exam day inclusive — i.e.
`(exam − today) + 1`. On Thu 29 Oct 2026 that yields **186D**, matching the settled
war-line example. The count is *computed from a single date constant* at render
time; when NTA announces, one constant changes and every screen follows. Nobody
hand-edits the number. (Exclusive counting would read 185D — the inclusive form is
chosen because today is a day he still has to spend, and the line is a war line,
not a timetable.)

### 0.4 Bottom nav — the BAND SWITCH

A flush recessed rail, not a floating pill. Sits at the screen foot, full margin
width (350dp at reference), **no shadow, no elevation** — depth is tonal only.

| Part | Spec |
|---|---|
| Rail (recess) | Height **52dp**; radius 16dp; fill = recess token (§0.6); top edge carries a 1dp letterpress light-line (ground-shadow at 35%) so the recess reads *pressed in*, never drawn on |
| Track inset | 4dp on all sides inside the recess → usable track 342 × 44dp |
| Cap (travelling) | Height **44dp**; radius 8dp (the cap is a key, keys are 8); fill = cap token; width = segment − 8dp (§0.5); the cap is the *only* raised object in chrome |
| Stops | Printed words only, on the label plate (Anonymous Pro Bold 10sp, +2.0), centred in each segment, printed **on the recess** — the cap travels *under* the selected word and re-inks it in cap-content tone. No icons. No badges. No counts. |
| Armed lamp | **Ø3dp** dot, orange (day `#F55314` / night glyph register `#F47442`), centred 6dp above the FOCUS word's cap-top edge — visible only while a session is armed/running. The sole orange in chrome. It is a lamp, not a badge: it carries state (armed), never a number. |
| Press behaviour | Everything answers on **DOWN**: the pressed word inks to full strength on touch-down; the cap departs on release-or-drag. No ripple, no scale bounce. |
| Cap travel | Spring slide, critically damped, no overshoot past the target stop. Duration = **180ms + 60ms per stop crossed** (adjacent 240ms, end-to-end 4-stop 360ms). Screen content crossfades 120ms, starting on the cap's seat frame — the word's ink flips on arrival, never mid-flight. |
| Drag | The cap is grabbable: 1:1 finger tracking inside the track, detent tick haptic at each stop centre crossed, seats on release to nearest stop. A drag that ends between stops never leaves the cap parked between words. |

### 0.5 Stop geometry — the formula

`segment = usableTrackWidth ÷ stopCount`, cap width = `segment − 8dp`,
cap travel per step = `segment`, end-to-end travel = `segment × (stopCount − 1)`.
The G-DISSOLVED delta (§3.1) reserves a PARK zone inside the track; where it does,
subtract PARK first (worked numbers in each delta).

### 0.6 Tokens

| Token | Night | Day |
|---|---|---|
| Ground | `#171412` | `#E4DFDA` |
| Rail recess | `#100E0C` (below ground — a true recess) | `#D8D2CC` (one tonal step below day ground, same press logic) |
| Cap fill | `#4A423C` | `#FAF6F0` (day raised) |
| Chrome words on recess (unselected) | `#D2C6BB` at 55% | ink `#252323` at 55% |
| Word on cap (selected) | cream `#FFE4CF` | ink `#252323` at 100% |
| War line ink | `#D2C6BB` | `#252323` |
| Header / drawer secondary ink | `#D2C6BB` at 70% | `#252323` at 70% |
| Drawer ground | `#1E1A17` (one step above ground — a sheet *rises*) | `#FAF6F0` |
| Hairlines in drawer | `#D2C6BB` at 18% | `#252323` at 18% |

Theme (day/night) follows the routine/schedule state — chrome never offers a
theme control in the header. (A drawer row *reports* theme source; §3.1.)

### 0.7 Chrome under session (HOLD absolute)

While a session runs: the rail locks — stops accept no press, the cap is parked at
FOCUS, the 3dp lamp is lit, and the PARK detent (§3.1) is inert. The war line stays
live (TODAY accrues). There are no mid-session decisions and chrome offers none:
no drawer, no Guard detour, no Ledger peek. Chrome unlocks on session end.

---

## §1 — THE THREE LEDGER VARIANTS (deltas)

**Naming law check, once for all three:** the app's existing destination words are
`FOCUS` (Deck), `SYSTEM` (Shield), `HORIZON` (Tasks), `LEDGER` (the record),
`GUARD`. All variants below use exactly these words. **No rename is proposed.** If
Plan's IA verdict renames any screen, that is a flagged decision for Sarthak, not
something chrome smuggles in.

### 1.1 V-TAB — Ledger keeps its stop (4 stops)

- **Stops:** FOCUS / SYSTEM / HORIZON / LEDGER — 4 stops.
- **Geometry (no PARK):** segment = 342 ÷ 4 = **85.5dp**; cap **77.5dp** wide;
  step travel 85.5dp; FOCUS→LEDGER end-to-end **256.5dp**, 360ms.
- **Geometry (with PARK, G-DISSOLVED):** §3.1 — segment 73.5dp.
- **Header:** unchanged. The record's home is a destination; chrome says nothing
  about it beyond the stop word itself.
- **Drawer consequences:** none from this variant. If a drawer exists it is
  G-DISSOLVED's, with no Ledger row — the record is *not* duplicated into the
  drawer (one fact, one rendering; a drawer shortcut to a nav stop is a second
  door to the same room, and doors are renderings too).
- **Intentionality note:** the 4-stop rail is the most symmetric and the most
  taxed: LEDGER is the only stop he may not open for weeks at a stretch, and a
  printed stop is a standing invitation. That is Plan's call to weigh, not
  chrome's — chrome's job is that if the stop exists, it is exactly as quiet as
  the other three. It is.

### 1.2 V-DEMOTE — no Ledger stop (3 stops); the record lives smaller

- **Stops:** FOCUS / SYSTEM / HORIZON — 3 stops.
- **Geometry (no PARK):** segment = 342 ÷ 3 = **114dp**; cap **106dp**; step travel
  114dp; end-to-end **228dp**, 300ms. The rail breathes: words get air, the cap
  becomes a true slab.
- **The demote forms and their chrome consequences** (Plan names which forms ship;
  chrome specs each one's surface):
  - **(i) Completion sheet** — presented by the session-end event, not invoked
    from chrome. *Chrome consequence: zero.* No button, no corner, no stop. The
    sheet rises over FOCUS with the cap already home; dismissing it returns to
    an untouched rail. If this is the only demote form, the rail and header are
    exactly §0 + 3 stops.
  - **(ii) Shield section** — the record as a section inside SYSTEM. *Chrome
    consequence: zero.* It is content on a screen chrome already serves.
  - <
...[truncated 8327 chars]**(iii) Chrome corner** — the record invoked from chrome itself: the word
    `LEDGER`, label plate, at the **right end of the war line**, hit target
    48 × 48dp (the word is small; the target is not). It is a word, not a pill:
    no fill, no border, ink at 70%, full ink on DOWN. It opens the record as a
    sheet over the current screen; the rail does not move (the cap stays on the
    current stop — a sheet is a depth, not a band). *This is the only demote
    form that costs chrome anything, and the cost is one word.*
- **Header:** unchanged except form (iii), if shipped.
- **Drawer consequences:** none in this variant alone. Under G-DISSOLVED the
  drawer *may* inherit a RECORD row — see the collision resolution in §4 before
  building that combination.

### 1.3 V-KILL — 3 stops, no record surface

- **Stops / geometry:** identical to V-DEMOTE without PARK (segment 114dp).
- **Header / drawer:** §0 exactly. No corner word, no sheet invocation, no
  drawer row.
- **Does the one quiet mono line of record survive? No — argued, not assumed.**
  A record line in chrome (say, `HELD 41 THIS WEEK` at the war line's end)
  fails three laws at once: (1) **TRUTH-once / one-fact-one-rendering** — the
  week's held total already has its single true rendering at the completion
  moment (the receipt); a standing chrome line is a *second*, ambient rendering
  of the same fact. (2) **Chrome is not content** — the line is a dashboard
  statistic wearing the label plate as camouflage; the nameplate pills died
  for exactly this. (3) **It cannot stay true cheaply** — a count that must be
  recomputed, synced and defended forever, in the one band he sees 50 times a
  day, to inform a decision he never makes from chrome. What survives V-KILL
  is only what was never the record: the war line's TODAY total (a *timer*
  fact, rendered once, §0.2) and the ephemeral completion receipt (an *event*,
  not a surface). **If Ledger dies, chrome carries nothing of it. Half-dead is
  the worst state — a record with no home becomes a rumour in the header.**

---

## §2 — (reserved; Guard branches follow as §3 per brief order)

---

## §3 — THE TWO GUARD BRANCHES (deltas)

### 3.1 G-DISSOLVED — Guard dissolves into chrome + drawer

**Top bar — status at the war line's end.** Right end of the header line,
right-aligned to the 20dp margin:

```
○ GUARD 3/4        (permissions missing)
● GUARD 4/4        (all granted)
```

- **Pip:** Ø6dp ring (1.5dp stroke) in header ink; hollow = something missing,
  filled = all granted. The pip is a *caution dot at its owning fact* — it is
  never orange (chrome's orange budget is spent: the FOCUS lamp, and that only
  while armed).
- **Word:** `GUARD n/4`, label plate, quiet mono in ink — the dead pill's count
  survives *only* in this de-filled form, per settled law. It is a readout,
  not a target: tapping it does nothing (the fix lives in the drawer, §below);
  a status you can read but must open the drawer to change keeps chrome honest
  about what chrome is for.
- **Websites:** no chrome consequence — they become a Shield list module
  inside SYSTEM (Plan owns that spec).

**The PARK end-detent — where the drawer lives.** The band switch gains a
fourth *position* that is not a stop:

- **Position:** inside the track, at its right end, after the last stop.
  Reserved width **44dp** + a 4dp notch gap, separated from the last stop's
  segment by a 1dp vertical score line pressed into the recess. Worked widths:
  usable stops track = 342 − 44 − 4 = **294dp** → 3-stop segment **98dp**
  (cap 90dp); 4-stop segment **73.5dp** (cap 65.5dp).
- **Label:** the word `PARK`, label plate, printed vertically-centred in the
  zone at 55% ink — dimmer than stop words, because it is not a destination.
  No icon, no gear glyph, ever.
- **How it opens:** PARK is a *detent, not a stop* — the cap may be dragged
  into it (one extra detent tick as it crosses the score line; travel +48dp
  beyond the last stop's centre) and it seats there with a heavier, lower
  tick — a parked feel, not a selected feel. **On the cap's seat in PARK, the
  drawer opens.** A simple tap on the PARK zone does the same (cap glides,
  drawer rises). While the drawer is open the cap *stays* in PARK — the drawer
  is visibly the cap's cargo. Closing the drawer (drag down, scrim tap, or
  tapping PARK again) sends the cap home to the current stop (travel time per
  §0.4 formula, counted from PARK).
- **Why a detent and not a button:** the drawer holds the app's *standing*
  configuration — the things the rested user sets and the tired user never
  touches. Parking is what you do with a machine between runs. The gesture
  teaches the category without a word of explanation.

**Drawer spec (the sheet PARK opens).**

- **Form:** bottom sheet rising over the rail, radius 16dp top corners, drawer
  ground token (§0.6), top edge at most 60% of screen height. Scrim: ground
  at 55% over content; the war line stays visible above the scrim line —
  the war does not pause for settings.
- **Grab strip:** 32dp tall, one centred score line 32 × 4dp (radius 8 family),
  recess tone. Drag-down dismisses; no X button anywhere.
- **Rows:** 52dp each, hairline-separated, label plate word at left (20dp
  inset), state word at right in body ink (Atkinson 13sp), whole row is the
  target, answers on DOWN:
  1. `PERMISSIONS` → state `3 OF 4` — expands in place to per-permission rows
     (52dp, name + `GRANTED` / `MISSING` word; a MISSING row carries the fix
     action as an ink word `OPEN SETTINGS ›`). This expansion *is* Guard's
     old home, at drawer depth.
  2. `SOUND` → state word `ON` / `OFF` (word + position: the row's right word
     is set in a 44dp-wide recess when OFF — state as form, no sled drawn).
  3. `THEME` → state `AUTO` with the source named beneath in quiet mono:
     `FOLLOWS SLEEP ROUTINE`. A readout, not a toggle — the DAY/NIGHT pill
     does not resurrect here in drawer clothing.
  4. *(Only in the §4-resolved V-DEMOTE combination)* `RECORD` → opens the
     record sheet.
- **Session state:** PARK is inert mid-session (§0.7). The drawer cannot be
  opened over a running hold — permissions are a rested-user decision.

### 3.2 G-KEPT — Guard as a destination: the spec plate

- **Nav citizenship: NOT a rail stop.** GUARD is a *depth*, reached from
  SYSTEM: a printed margin word `GUARD ›` at the foot of SYSTEM's first
  section (label plate, 48dp target), pushing the plate over SYSTEM. The rail
  keeps the variant's stop count untouched (3 or 4); making Guard a 5th stop
  is the collapse case priced in §4. (This matches the shipped app's shape:
  GUARD already exists as a pushed destination off the tab bar, not a tab.)
- **While on the plate:** the rail remains visible; the cap stays seated on
  SYSTEM — the plate is SYSTEM's back room, and chrome says so by where the
  cap rests. The war line persists unchanged above it.
- **How chrome announces it:** exactly one announcement — if any permission is
  missing, SYSTEM's stop word carries **no badge** (badges are dead); instead
  the `GUARD ›` margin word on SYSTEM gains the Ø6dp hollow pip before it —
  the caution dot lives at its owning row, the only place alarm discipline
  allows it. At 4/4 the pip is gone; the word stands plain. Nothing appears in
  the header: under G-KEPT, Guard chose to be a place, and places don't get
  to also be chrome.
- **The plate itself (chrome's frame for it; content is the plate's own
  spec):** full-screen destination on ground token; its identity sheet —
  unit number, verb-code legend, status board — is set as *one printed plate*
  (radius 16dp panel, raised token) inset 20dp, header row inside the plate:
  `GUARD` in Sora SemiBold 18sp + the model line `MONOLITH ML-01 · UNIT Nº 01`
  in label plate beneath it. The back word `‹ SYSTEM` sits at the war line's
  band, left, in the header's 48dp strip — replacing nothing, since the war
  line shifts right of it only on this screen. *(Flag: this is the one place
  chrome's header band is shared. Alternative — back word as the plate's own
  first row — costs the war line nothing and is my preference if Ren finds
  the shared band noisy on renders.)*

---

## §4 — BRANCH × VARIANT INTERACTION MATRIX

| | **G-DISSOLVED** (pip+word in header; PARK drawer) | **G-KEPT** (spec plate, pushed off SYSTEM) |
|---|---|---|
| **V-TAB** (4 stops) | **Coherent, tightest rail.** Stops track 294dp → segment 73.5dp, cap 65.5dp, end-to-end travel 220.5dp + 48dp to PARK. All six stop words fit the label plate at 73.5dp (longest, HORIZON ≈ 62dp tracked). Works; the rail starts to feel like a keyboard. | **Coherent, the conservative build.** 4 stops at 85.5dp, no PARK, no drawer, header clean. Guard announced only by the pip on SYSTEM's `GUARD ›` word. |
| **V-DEMOTE** (3 stops) | **Coherent after one resolution.** Collision: demote form (iii) chrome-corner `LEDGER` word and the guard pip+word both claim the war line's right end. **Resolution: status outranks access** — the guard keeps the corner; the record's chrome form moves into the PARK drawer as its RECORD row (§3.1 row 4), and forms (i) completion sheet + (ii) Shield section carry the rest. If Plan names *only* (i)/(ii), no collision exists at all. | **Coherent, the emptiest header.** Corner = `LEDGER` word (form iii), Guard = a place with a pip on SYSTEM. Two quiet systems, zero overlap. |
| **V-KILL** (3 stops) | **Coherent — the cleanest combination in the matrix.** Segment 98dp, corner carries guard only, drawer carries no record row (§1.3: the line does not survive, and neither does a drawer door to a dead room). | **Coherent.** Nothing in chrome but the war line, 3 stops at 114dp, and a plate behind SYSTEM. Warning, stated once: the plate's *status board* is present-state only — it must not grow a history section, or Ledger has been smuggled back through the Guard door. |

**The one collapse, named:** G-KEPT misread as "Guard becomes a rail stop"
(5 stops: FOCUS / SYSTEM / HORIZON / LEDGER / GUARD, or 4 under V-KILL/DEMOTE)
collapses in both directions — with PARK it is geometrically impossible
(294 ÷ 5 = 58.8dp segments; SYSTEM at label-plate tracking ≈ 60dp does not
fit its own segment), and without PARK it breaks the naming law's spirit:
GUARD is not a peer destination, it is SYSTEM's subject matter. G-KEPT
survives *only* as a pushed plate. Any build that puts GUARD on the rail has
misread the branch.

**PARK × stop-count, summarised:** PARK costs a flat 48dp of track regardless
of variant. It hurts V-TAB (segment 85.5 → 73.5dp, −14%) and is nearly free
under 3 stops (114 → 98dp). If Sarthak keeps both the tab *and* the dissolve,
the rail pays for both decisions visibly — that tightness is honest pricing,
not a defect.

---

## §4b — ADDENDUM (post-filing): Plan's IA verdict arrived

Plan's verdict landed in my inbox as this file was closing (letter 0517,
`ledger-verdict` thread; full text in plan/LEDGER-designers.md):

- **DEMOTE wins.** Nav = 3 stops + PARK end-detent. No fourth stop, no width
  reserved for Ledger. My operative build target is therefore
  **§1.2 (V-DEMOTE) × §3.1 (G-DISSOLVED geometry)**: segment **98dp**, cap 90dp.
- **Named demote forms: (i) the completion Receipt** — ephemeral sheet over
  the Deck, zero chrome invocation, exactly my §1.2(i) — **and (ii) RECORD** —
  a non-interactive hairline section at the *foot* of Shield, my §1.2(ii).
- **The chrome corner form (iii) is NOT named — it dies.** Consequence, stated
  plainly: the §4 header-corner collision (V-DEMOTE × G-DISSOLVED)
  **dissolves** — the corner belongs to the guard word uncontested, and no
  RECORD row is needed in the PARK drawer. §1.2's corner spec is retained
  above only as the priced option if Sarthak re-opens it.
- Plan confirms from the IA side what §1.3 argued: **chrome carries zero
  record data** — no badges, no counts, no hold dot.
- **One divergence, flagged as instructed:** Plan's verdict names the stops
  `DECK · SHIELD · TASKS`; the shipped code's destination words are
  `FOCUS · SYSTEM · HORIZON`. My file uses the shipped words. Whichever set
  Sarthak ratifies, geometry is untouched — but the rename is a decision for
  him/Ren, and it is now on the record from both seats.

## §5 — What I'm deliberately not deciding

- Whether Ledger lives at all (Plan's verdict; Sarthak's pick) — this file
  prices all three so any verdict is buildable without re-speccing chrome.
- Guard dissolved vs kept (Sarthak's open pick) — both branches are fully
  chrome-specced; the matrix states coherence, not preference. If forced to
  one line: dissolved is the more intentional chrome — Guard's *status* is a
  fact he should meet where he already looks; its *settings* are rested-user
  business and PARK is exactly where rested-user business belongs.
- The exam date constant (2 May 2027 assumed) — one-line correction when
  announced; every screen inherits it.

— Razor, Designers (intentionality), for Sana.
