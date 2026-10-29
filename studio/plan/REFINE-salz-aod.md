# REFINE — SALZ, AOD DESK: THE DESK CLOCK'S FULL LIGHT BUDGET + THE PARK READ
## Commissioned by Ren (mail/salz/2046, ruling 2), Oct 29, 2026 — filed ~21:46 IST

*Method: every element of the shipped overlay was inventoried from
trunk code (AirlockAndDisarmOverlays.kt, batch-7 state), every ink
taken from trunk's theme tokens (ui/theme/Color.kt), every luminance
computed by script (sRGB relative luminance, WCAG formula; alpha
composites over the overlay's true-black ground). Areas are dp-area
fractions of a 411×914dp logical screen — density-independent. Text
lit-area uses stated glyph-coverage estimates (bold mono ≈0.38 of
cell, bold proportional ≈0.30–0.35); the token luminances are exact,
the areas are estimates Linea's captures replace with pixel counts
(§3). Nacht's AOD law is the court: no printed colour on the night
face above **0.25 relative luminance**.*

---

## 1. THE OVERLAY, ELEMENT BY ELEMENT

The overlay (AlwaysOnDeskClockOverlay, :68) holds the screen awake
(FLAG_KEEP_SCREEN_ON, :86) on a true-black ground (Color.Black,
:139 — luminance 0.000, acquitted: the ground spends nothing).
Four elements print. All four inks are night tokens; three of the
four print at the SAME luminance — the full NightColorPrimary.

| # | Element | Seat | Ink | L (computed) | Lit area (est.) | vs 0.25 |
|---|---|---|---|---|---|---|
| 1 | Hero numerals (MM:SS, 96sp Anonymous Pro bold) | :160–167 | #FFE4CF @ 1.0 | **0.8125** | ≈3.2% of screen | **BREACH 3.3×** |
| 2 | Progress strip — fill (16dp, full content width) | :176–186 | #FFE4CF @ 1.0 | **0.8125** | 0→1.55% of screen, grows with progress | **BREACH 3.3×** |
| 3 | Progress strip — track | :170–175 | #100E0C | 0.0045 | 1.55%→0, shrinks | under |
| 4 | Task banner — text (14sp Atkinson bold) | :197–203 | #FFE4CF @ 1.0 | **0.8125** | ≈0.23% | **BREACH 3.3×** |
| 5 | Task banner — background (full-width box) | :190–196 | #362C27 | 0.0273 | 4.16% | under |
| 6 | Exit line (11sp Atkinson bold) | :150–157 | #FFE4CF @ 0.6 over black | **0.2607** | ≈0.30% | **OVER by 0.011** |

Findings the numerals-only filings could not see:

- **The strip is the second breach, and it is the only one that
  grows.** Every finalist filing that priced the desk clock priced
  the numerals (trunk 0.81 by OBS-2; Temple 0.750; Blueprint
  0.758 — all numerals). The strip's fill is the same 0.8125 ink
  across a 16dp full-width bar, clamped to a 0.04 floor at the
  seal and filling to 1.0 at the span's end (timed branch, :121)
  — or pinned at 1.0 from minute 20 of any hour on the stopwatch
  branch (:124, elapsed/1200). No filing in the wave dims it,
  caps it, or mentions it. A winner that keeps the overlay's shape
  imports a 0.81 bar by default.
- **The exit line is over the ceiling even dimmed.** Its 0.6 alpha
  was presumably the dimming; composited over the black ground it
  measures 0.2607. The alpha that lands exactly 0.25 is 0.589 —
  the cure is a two-point trim of one constant (0.60 → 0.58
  composites to ≈0.246). Cheapest breach in the app to close, and
  until tonight the least visible: it is the line that tells him
  how to leave.
- **The banner text is the third full-primary print.** Small
  (0.23% lit) but at full 0.8125, all night, at reading distance.
  Its background (0.0273) is the overlay's one well-mannered
  surface — under the ceiling by 9×.
- **Burn-in shift acquitted as mechanism, noted for captures:**
  the whole column steps ±6dp on a 5×5 grid by burnInStep
  (:132–133). It spends nothing and saves the panel; L05 captures
  at different steps will show the column displaced — displacement
  is the design, not a defect (§3).

## 2. THE NIGHT BUDGET — luminance × area × hours, summed

Model night: the SLEEP span, 20:00–03:30 = **7.5 h**, timed branch
(progress = span fraction, floor 0.04; mean fill 0.52). Unit:
full-screen-luminance-hours (1.0 = the whole screen at L 1.0 for
one hour). Per element:

| Element | Night integral | Share |
|---|---|---|
| Hero numerals | 0.1957 | **71.6%** |
| Strip fill | 0.0490 | **17.9%** |
| Banner text | 0.0141 | 5.2% |
| Banner background | 0.0085 | 3.1% |
| Exit line | 0.0059 | 2.2% |
| Strip track | 0.0003 | 0.1% |
| **TOTAL** | **0.2734** | 100% |

Variants, same method: the **stopwatch branch** nearly doubles
the strip's term (mean fill 0.979 → 0.0922) for a night total of
≈0.317. A **3 h evening study span** scales the table by 0.4
(≈0.109) with identical shares. The **idle overlay** (no session:
progress 0, banner fallen to its default) still sums ≈0.0299 per
instant-hour — ~90% of a running frame's spend, buying a dial
number dressed as a time and a banner that is not a fact (§4).

**The frame brightens across the night.** Instantaneous frame sum
(Σ L·area): **0.0305 at the seal → 0.0425 at the span's end**,
+39%, and the strip is the only moving term. No frame-sum ceiling
has ever been ruled; the sums are filed as the fact for Nacht/Ren
to rule against, not as a breach of a law that doesn't exist.

**The order in which a night's light actually spends:**
1. **The numerals lead from the first minute and are never
   overtaken** — top rate (≈0.0261/h) at every instant of the
   night; they are nearly three-quarters of everything the
   overlay ever emits.
2. **The banner text opens second** (full-primary ink, small
   area), the banner background third — both constant, both
   overtaken early.
3. **The strip starts as nearly nothing** (the 0.04 floor) and
   climbs all night: it passes the banner background at ~9% of
   the span, the banner text at ~15%, and by the final hour it
   runs at ≈0.0126/h — **48% of the numerals' rate**, the clear
   second spender for the whole second half of the night. The
   brightest hour of the overlay's night is its last.
4. **The exit line** spends a small constant sum (2.2%) — its
   offence is not its budget but its ink (§1).
5. **The track** is a rounding error that shrinks as the fill
   grows; the ground spends nothing at all.

## 3. WHAT A CAPTURE MUST SHOW TO ACQUIT EACH ELEMENT (for Linea, L05)

Per element — a capture acquits only what it measures:

- **Numerals:** capture at a stated remaining time; the glyph
  pixels' measured mean luminance ≤ **0.25** (the 55% composite
  computes to 0.2158 over this ground — in code, not as a token
  promise) AND the capture reports the glyphs' lit-pixel count,
  replacing §1's coverage estimate with a count.
- **Strip:** two captures — early span (progress ≤0.1) and late
  span (progress ≥0.9) — showing the fill's measured luminance
  ≤0.25 at BOTH, or the strip absent/redesigned in the winner's
  grammar. A single early capture acquits nothing: the breach is
  at its smallest when the night is young. Fill width against
  the span derivation is the truth fix's ground (shipped), not
  L05's; L05 judges light.
- **Banner:** the capture's banner text is a real task title or
  declared intention, OR the banner is absent. A capture showing
  "Deep Focus Session" with no task and no intention FAILS the
  banner outright — the fabricated default (overlay :193; Una's
  appendix sighting, AUDIT-deck-intercept-claims.md) must be dead
  in the winner's build, not merely unreached in the test setup.
  Background acquits on measure (0.0273, under).
- **Exit line:** measured composited luminance ≤0.25 (as shipped:
  0.2607 — fails by 0.011 until the alpha trim lands).
- **Whole frame:** the capture reports total lit-pixel fraction
  and Σ L·area for the frame, at both strip states, so the pick
  sees the frame sum it is buying (as shipped: 0.0305 → 0.0425).
  Column displacement of ±6dp between captures is the burn-in
  design (§1), not a registration defect.
- **Idle frame:** one capture with no session running. It must
  show the actual time of day (ST-01's standing ruling) or an
  honestly-labelled dial — a frame showing the dial setting as
  an unlabelled MM:SS fails, whatever its luminance.

## 4. THE TRUTH SENTENCE UNDERNEATH THE LIGHT

The budget's spend is worst exactly where the print is falsest.
At idle the overlay prints dialMinutes×60 as its hero (overlay
:104–110) — the composer's dial setting wearing a clock's clothes
(ST-01, register) — and its banner falls through task and
intention to "Deep Focus Session" (:193), a session that does not
exist, named on the surface he reads at night (Una's sighting,
filed kill-or-verify in Truth Car 3). The night face's two
largest constant spenders — numerals and banner — are, at idle,
spending on a number that is not a time and a name that is not a
session. The light audit and the truth audit are the same audit
on this surface; the pick should hear them together.

## 5. PARK — WHAT IT HOLDS, WHAT OF IT IS FACT, WHAT IS FURNITURE

Read from ParkDrawer.kt (batch-7 trunk) against Una's
AUDIT-park-claims.md (charges UNA-01…09, acquittals A-01…A-14 —
hers; this read sorts, it does not re-try).

**FACT — measured at render, each by its own query:**
- **The board's six permission lines** — ACCESSIBILITY ·
  USAGE ACCESS · DEVICE ADMIN · NOTIFICATION LISTENER (the
  counted four) · OVERLAY · BATTERY — printing
  GRANTED / MISSING / STALE / (blank = unmeasured, never a
  guessed word), staleness re-derived at render on a 5-second
  beat while the drawer stands open (ParkDrawer.kt:164–171,
  :347–360; Una A-01). The blank third state is the honest one:
  a line that has not been measured prints its name only.
- **The three interceptor lines** — SHORTS + REELS · SOCIAL
  SITES · NOTIFICATIONS — printing RUNNING / PAUSED / OFF from
  derived measurement (A-10's family). One caveat of kind: the
  SHORTS + REELS word is a fact about the *interceptor's state*;
  the *setting* of the same name exists in no layer of the app
  (UNA-01, S1). The line is fact; the control it implies is not
  merely furniture — it is absent.
- **LAST PROVED HH:MM · SELF-REPORT** — advances only when a
  complete measured set lands; the slot is blank until then
  (A-02).
- **The THEME readout** — one evaluation drives scheme and
  readout both (A-05).
- **The FIX key** — the drawer's only object, present exactly
  while a permission lack exists and naming the first missing
  item in board order (A-04): furniture whose existence is a
  fact.
- **Four of the plate's lines, acquitted as facts** (printed as
  furniture, true as statements): "WHEEL SETS TIME · SLIDE
  ARMS · THE HOLD BREAKS" (A-14); "HOLD: 60 SECONDS, BANKED
  EVERY 15 · ONE BREAK A WEEK" (A-11); "PEEK: 2 MINUTES, THEN
  SHUT AGAIN" (A-12 — the lease verifies; note the line must be
  reprinted the day the peek length becomes his setting, per
  Freya's PILL-AND-TIMES §3); "COLOUR IS A VERB…" (A-13 —
  acquitted as a description of the SHIPPED skin's colour law;
  it describes this skin, and no winner's grammar keeps all
  four verbs, so the line dies at the reskin whoever wins).

**FURNITURE — print without a measurement behind it, or a print
whose claim fails:**
- **The sound sled's OFF** — the row's margin word is a fact
  about a variable; as a claim about the phone it is FALSE
  (UNA-03, S1): the toggle persists nowhere and does not govern
  every engine, so PARK can print OFF over a sound-on app. The
  sled itself is a shipped control moved whole; its word is
  furniture wearing a fact's clothes.
- **Plate: "DATA: ON THIS PHONE ONLY"** — FALSE as shipped
  (UNA-04): allowBackup="true" walks the database off the phone
  by the platform's own road.
- **Plate: "ROUTINES: SET BY HAND ON SHIELD · FIRED AS SET"** —
  the second clause awaits its fact (UNA-05): the no-fire audit
  proved the chain could annul its own session; the cure is
  Truth Car item 18's, and the words should land with it or not
  at all.
- **Plate: "STUDY RECORD: IN YPT"** — FALSE by omission
  (UNA-08): the Drawer keeps an on-phone record of every finish;
  the true sentence is STUDY HOURS.
- **Plate: "SOUND: FLOAT-PCM, 4 PARTIALS · POWER: YOUR PHONE"** —
  the machinery clause verifies (the engine's 4 harmonic
  partials stand at TactileSoundEngine.kt:34, :50); the tail is
  a shrug in print. Half fact, half colour.
- **The plate's head** ("MONOLITH ML-01 · UNIT Nº 01 · BUILT
  FOR ONE USER · NOT FOR SALE") and the GUARD header — pure
  nameplate furniture; they claim nothing measurable and are
  named as furniture so nobody mistakes them for readings.

**ABSENT from PARK — the pick should know what the drawer does
not hold:** no Shorts+Reels setting (UNA-01), no Social Sites
switch (UNA-02 — the flag can only be forced ON), no websites
control (Shield's editor owns it), no desk-clock control of any
kind behind the face §1 measures, and no backup control standing
behind the DATA line it prints. Under the seal the drawer shows
and cannot touch (FIX absent, sled print-only) — consistent with
the seal's law; door mechanics are Band's ground, not charged
here.

— Salz, Critics (truth / feel-coherence), Oct 29, 2026, ~21:46 IST
