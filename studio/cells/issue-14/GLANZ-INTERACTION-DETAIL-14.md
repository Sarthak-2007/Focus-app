# GLANZ — INTERACTION DETAIL · Issue #14 · Setup Sheet + Onboarding

**Seat:** Glanz, interaction detail (dedicated-100). **Scope:** every press, landing, and
state change on the Reception Sheet — the door cards, FIX / CHECK keys, the return beat,
the DONE tear. **Posture:** DECLARED, spec only, nothing compiled. This file sits *under*
the cell's landed law — Letzt's 420 ms tear choreography, the release-true commit, Portal's
One Reading, Klang's sound cure, Haut's release law — and supplies the layer none of those
files numbers: press travel, 3D light/shadow, haptic waveforms, sound character, and the
timing of every state change. Where this file proposes over an open item, it says PROPOSAL.

House notation: haptics as `timings [ms] / amplitudes [0–255]` waveform arrays
(`VibrationEffect.createWaveform`) or `one-shot <ms>/<amp>`. Easing names are the house's:
Curve A (whole-line re-strike), THROW (`CubicBezierEasing(0.5, 0, 1.0, 0.4)`).
Fonts: Sora (state words, DONE), Atkinson Hyperlegible (body, keys' labels),
Anonymous Pro (all numerals, proof ages, chit times). Never green. No ripple anywhere
in this app — a press is ink + dip + bezel, never a spreading circle.

---

## 0. The one law of this sheet

Every press on the sheet belongs to exactly one of three physical families.
If a builder cannot say which family a press is in, it does not ship.

| Family | Meaning | Members | Press physics (shared) |
|---|---|---|---|
| **KEY** | a commit act he chose | FIX, DONE, OPEN THE LIST | dip 2 dp, bezel swap, commit on release-true |
| **DETENT** | a state change acknowledged | CHECK, collapsed-card re-expand, PARK stub row | dip 1 dp, one firm detent on landing only |
| **TEAR** | the one exit that must feel final | DONE's release (the tear itself) | §5 — the only multi-beat haptic on the sheet |

**KEY press physics (all KEYs, exact):**
- Target: height ≥ 48 dp; the printed word is never the target, the slab is.
- Press-down, frame 1: slab translates **+2 dp Y inside its fixed bounds** (bounds never
  move; the sheet never reflows under a press). Duration **60 ms**, ease-out.
- 3D treatment: at rest, light from above — **1 dp top highlight** (surface ink lightened
  12%), **1 dp bottom shadow** (ink deepened 18%). At press-down the pair swaps weight:
  top highlight → 0 dp, bottom shadow → **2 dp**, and the face ink deepens exactly one
  step. The key reads as *pressed into* the paper, not greyed.
- Release-true (finger lifts inside bounds): commit fires at the lift frame; slab returns
  over **180 ms** Curve A. Slide-off before lift: return over 120 ms, **no commit, no
  sound, no haptic** — a cancelled press is a non-event on every channel.
- Press-down itself: **no haptic, no sound.** Feedback before commitment teaches that
  touching is doing (house law, #12 Feel-1). The report comes at the commit, §2–§5.

---

## 1. The reception ceremony (first open only)

The first open is a handover, not a form load. Timings are totalled: the sheet is fully
legible at **1,050 ms**; nothing after that moves unless he acts.

| t (ms) | Event | Detail |
|---|---|---|
| 0–600 | **The threshold.** Sheet surface present, nameplate slot empty, doors not yet printed. Quiet. | No spinner, no skeleton shimmer — a held breath, not a loading state. Scroll locked. |
| 600 | **The nameplate stamps in.** | Scale 1.04 → 1.00 in **120 ms** ease-out + opacity 0 → 1 in the same window. Haptic: one-shot **18 ms / 160** at the contact frame. Sound: one paper-stamp thock — **180 Hz, 50 ms, gain 0.40**, no tail. This is the only stamp sound the sheet ever makes; DONE (§5) reuses its first half, so the ceremony rhymes at both ends. |
| 650–1,050 | **The sweep performs.** Door verdicts land in COUNTED order, staggered **90 ms** apart. | Each landing is a **re-strike** (cut to final ink in one frame — never a fade; a fade lies about when the truth arrived). Haptic per landing: **only** when the verdict is SET — one-shot **6 ms / 70**. NOT SET / UNPROVED / NOT CHECKED landings are **silent** — a lack is print, never a buzz (mirrors #7's binding never-buzz list). |

Re-opens (PARK stub, §6) get **none** of this: no stamp, no stagger. Ceremony is spent
once, like the tear's perforation (Rausch D4). A returning sheet is furniture, risen in
240 ms decelerate, verdicts already printed.

**Reduced motion:** threshold collapses to 0 ms, stamp becomes a cut, sweep stagger
becomes 0 ms (all verdicts in one frame). Facts identical, theatre removed.

---

## 2. The FIX key (per-door escort)

- Family KEY (§0 physics). Geometry: **48 dp** tall, width = card's inner width; the
  destination sentence (the cured escort line) prints *above* the key, never on it —
  the key carries only the verb.
- Commit (release-true): haptic one-shot **12 ms / 140** (one firm detent — leaving the
  app is a deliberate act, priced like one), then the system Settings transition takes
  the screen. The sheet plays **no exit animation of its own**; inventing one would fight
  the system's.
- While he is away, the sheet does not poll, pulse, or wait visibly. Stillness is the
  waiting state.

## 3. The CHECK key (re-prove)

- Family DETENT: dip **1 dp** (60 ms down / 180 ms up, §0 easing), ink deepen one step.
- Press while a sweep is already in flight (his own earlier press, or the open-sweep):
  the key **holds its dipped state** for the sweep's duration — bezel shadow stays at
  2 dp, word reads CHECKING — and a re-press **re-dispatches in silence** (adopts Ruhe's
  #9 narrowing law verbatim: the knock sounds only on the idle → CHECKING transition).
- Verdict landing, per door: word re-strikes in the landing frame. SET landing: one-shot
  **10 ms / 160**. Any other landing: **silent**. The total, the Guard line, and the card
  change in the **same frame** (Portal's One Reading makes this structural; this spec
  makes it a feel requirement: a two-frame skew is a visible contradiction).

## 4. The return beat (he comes back from a FIX trip)

This is the sheet's trust moment (Persona User 2: *"prove it worked"*). Choreography:

1. On resume, the sweep runs; the sheet **scrolls to the repaired card** — 320 ms
   decelerate, no overshoot, landing with the card's head at the 24 dp top inset.
   (This is Atem §3's clocked chit scroll, numbered.)
2. The Return Chit prints on that card by **re-strike** (cut, one frame) in the same
   frame the card's verdict word changes.
3. Sound/haptic, by outcome:
   - **Flipped NOT SET → SET:** one knock — waveform timings **[0, 14, 40, 20] /
     amplitudes [0, 200, 0, 160]** (the house bolt waveform, #11) + sound **240 Hz,
     44 ms, gain 0.45**. One knock per return, however many doors he fixed in the trip:
     the *trip* is the act, not each door.
   - **Returned unchanged:** **silence**, print only. A knock for no change would spend
     the signal the flipped case needs.

## 5. DONE — the tear, pressed

Letzt's choreography is law and is not re-opened here: stamp books first, pulse replaces
click, leaf drops 180 (specA), dashes halve 260 (specB), stub motionless, **420 ms total**,
commit on release-true. What follows is the press physics *under* that choreography —
the part a builder currently has to guess.

- **At rest:** DONE is a full-ink slab, **56 dp**, Sora Bold. Above it the tear rule:
  dashed, and per Portal INV-2 the *same* rule that stands over PARK's stub — one rule,
  two seats; it is never animated at rest, never "tidied" into a hairline.
- **Press-down (60 ms):** slab dips **2 dp** (§0 KEY physics); simultaneously the tear
  dashes **brighten one ink step** and their gaps narrow by **1 dp** — the perforation
  visibly *takes the strain* of the press. No haptic yet. This strain frame is what makes
  the release read as a tear rather than a fade: the paper was loaded before it parted.
- **Release-true — the tear fires, in this order, all inside Letzt's 420 ms:**
  1. **0 ms — the stamp books.** Ledger write first (the cell's law), before any motion.
  2. **Haptic — the pulse (replaces any click):** waveform timings
     **[0, 30, 60, 30] / amplitudes [0, 120, 0, 200]** — two beats: a soft load, then the
     parting beat at full amplitude. Total haptic span 120 ms, ending well inside the
     visual tear so the hand finishes before the eye does.
  3. **Sound:** stamp thock **180 Hz / 44 ms / 0.50** leading, overlapped at **+20 ms** by
     the tear itself — filtered noise burst, **90 ms**, sweeping **2,400 → 700 Hz**,
     gain **0.30**, no tail past 130 ms. Dry paper, never a whoosh, never a chime.
     **PROPOSAL on the open decision item** (tear-pulse night volume, two feel rulings
     disagree): after **22:00**, tear noise gain → **0.18** and pulse second beat →
     amplitude **150**; the stamp thock is unchanged (it is the receipt, the tear is the
     theatre). For Ren's ruling, not builder's choice.
  4. **Leaf:** drops per specA (180 ms); dashes halve per specB (260 ms); stub motionless;
     the Deck is what remains at 420 ms. No part of the sheet scrolls, shrinks, or fades
     as a whole — only the leaf leaves.
- **Cancelled press** (slide-off): dashes relax to rest ink/gaps over **120 ms**, slab
  returns 120 ms, **no pulse, no sound, nothing booked**. The tear was loaded and set
  down — that is all he should feel.
- **DONE at 0/4 (the Blank Leaf, Rausch D2):** identical physics except the pulse's
  second beat drops to amplitude **120** and the tear noise to gain **0.22** — emptiness
  tears more quietly. The leaf is blank where the count would stand. No scolding
  sentence, no extra confirmation dialog — the quiet *is* the message, and a dialog
  would unpick the ceremony.

## 6. The remaining presses

- **OPEN THE LIST:** family KEY (§0), commit detent one-shot **10 ms / 120**. The sheet
  **persists underneath** — leaving via this key and returning lands on the sheet at the
  identical scroll offset, verdicts as left, **no re-ceremony, no re-sweep stagger**
  (the Persona User 2 trapdoor cure, in feel terms: the sheet was *standing there*,
  not re-summoned).
- **Collapsed SET card (F-6):** collapse glide **260 ms** (height, decelerate); content
  crossfades at the glide's 40% point — the one sanctioned crossfade on the sheet,
  because nothing's truth changes during it, only its bulk. Pressing the collapsed
  ruled line re-expands: **220 ms**, detent one-shot **6 ms / 80** on landing.
- **PARK stub row (the re-open):** DETENT family with the house wash — **#493EA4 at
  8% (light) / 22% (night)** on the press frame, **120 ms** fade on release (Regal's #8
  numbers, adopted so the stub and the shelf feel like one app). Sheet rises **240 ms**
  decelerate. The stub's dashed rule does not move, flash, or tear again — spent is spent.

## 7. State-change timing table (the whole sheet)

| Change | Visual | Haptic | Sound |
|---|---|---|---|
| Door verdict lands (sweep) | re-strike, 1 frame | SET only: 6 ms / 70 | none |
| SET → UNPROVED (5 s age flip) | **atomic re-strike of card + total + Guard line in one frame**; no fade, no slide | none | none |
| FIX commit | system transition | 12 ms / 140 | none |
| CHECK verdict SET | re-strike, 1 frame | 10 ms / 160 | none |
| Return, flipped | chit re-strike + scroll 320 ms | bolt waveform (above) | 240 Hz / 44 ms / 0.45 |
| Return, unchanged | chit re-strike | none | none |
| Card collapse / expand | 260 / 220 ms glide | expand only: 6 ms / 80 | none |
| DONE tear | Letzt 420 ms (§5) | pulse [0,30,60,30]/[0,120,0,200] | thock + tear (§5) |
| Any cancelled press | return 120 ms | none | none |

**Haptics-off / reduced-motion fallbacks:** every haptic above has its visual twin
already in the same row (dip, bezel swap, re-strike) — nothing is *only* felt. Tear
under reduced motion: **120 ms** full-alpha hold on the perforation, then a cut
(#12's house law). Stamp sound and tear sound mute with the system sound setting;
the stamp *print* never does.

## Acceptance (for the builder's tests)

- Frame-stepped: stamp-in 120 ms ± 1 frame @ 60 Hz; sweep stagger 90 ms ± 1 frame;
  tear total 420 ms ± 1 frame; cancelled-press return 120 ms ± 1 frame.
- Waveform assertions: pulse, knock, and each one-shot fired exactly once per commit,
  zero on cancel, zero on press-down.
- One-frame law: SET → UNPROVED flip changes card, total, and Guard line in the same
  frame (screenshot pair, frame N vs N+1, no intermediate state exists).
- First-open ceremony fires exactly once across process death and re-creation
  (ledger-gated, like the stamp it performs).

— Glanz, interaction detail
