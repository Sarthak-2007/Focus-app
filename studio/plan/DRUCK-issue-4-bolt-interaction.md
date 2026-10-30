# DRUCK — ISSUE #4 · THE BOLT, INTERACTION DETAIL
## Seat: DRUCK (interaction detail, dedicated-100) · Fri Oct 30, 2026
## Status: SPEC ONLY. DECLARED, uncompiled. Nobody compiles — Ren, on Sarthak's word.
## Consumes as law: DESIGN-issue-4-the-bolt.md (§1–§11), FALZ's detent scale,
## KLANG's sound rulings, HAUT §7.3, GANG §7.1, TON §5, BLICK §6, MIP-02's
## proving-creep + detention-retract. This file re-opens none of them.
## It supplies what none of them contain: the press, landing, and retract
## physics of every Bolt surface, in numbers a builder cannot misread.

**DRUCK'S STANDING LAW (applies to every control in this file and #6's):**
a control is not shipped until it declares all five layers —
(1) **travel** in dp, with its side-wall / contact-shadow behaviour,
(2) **spring** — easing name + ms in, ms out, overshoot or none,
(3) **haptic** — an exact waveform (timings ms / amplitudes 0–255,
    legacy-duration fallback for pre-API-26),
(4) **sound** — an existing engine voice only, named with gain/pitch,
    or ruled silence with the reason,
(5) **light** — the pressed/active colour step, day and night, from
    TON's table; never green or near-green; never a fade on a verdict.
A flat rectangle with a label fails layer 1 by definition.

House voices only (Feel-1 §8 census, countable on one hand): detent,
latch, firm click, seat knock, dull knock. This file invents **zero**
new sound buffers and **zero** new haptic families. Every waveform
below is one of those five voices, parameterised.

---

## 1 · THE HOUSING PRESS — HAUT §7.3, completed to five layers

HAUT specced the dip (1dp, 90ms in / ≤150ms return) and the gate law.
Correct, and incomplete: no wall, no light, no waveform. Completed:

| Layer | Spec |
|---|---|
| Target | The **whole housing is one target**: 60dp tall × full width. The jewel (≤9dp), the window (102×36dp) and the bolt are **print inside it — never separate targets**. A 9dp jewel as a touch target would fail the 48dp minimum and lie about what a press does. |
| Travel | Whole housing sinks **1dp** (HAUT). The channel recess does **not** sink with it — case moves, instrument doesn't. Contact effect: the housing's bottom hairline is covered by the sink (hairline gap 1dp → 0dp at full dip); the top hairline is unchanged. No scale, no tilt, no ripple, ever. |
| Spring | In: **90ms, EasingA** (HAUT PRESS_MS). Out: **140ms, EasingA, stepped last frame** — return ends in a 1-frame settle, no overshoot, no spring ζ: an instrument case does not bounce. Reduced motion: dip collapses to the light step only (0dp, 0ms). |
| Haptic | DOWN (gate open): waveform timings `[0, 12]`, amplitudes `[0, 64]` — one 12ms strike at 25% — the haptic twin of KLANG's quietest seat knock. UP: nothing (the destination speaks, per the single-voice rule). Legacy fallback: `vibrate(12)`. |
| Sound | KLANG's ruling stands verbatim: PARK seat knock, **10ms, gain 0.10 day / 0.06 night, 300Hz class**, fired in the host/VM handler on DOWN — never in the composable. |
| Light | Pressed face steps **one TON step toward the channel recess tone** (day and night rows of TON's table), bolt / jewel / window fills **unchanged** (HAUT's law extended from motion to light: a press may not move *or relight* the proof). Hairlines unchanged. The step is instant at DOWN (1 frame), restores at UP + 1 frame. |
| Gate | `navigationLocked()` re-read synchronously at DOWN **and** UP via a live provider (HAUT). Gated press = **totally dead**: 0dp, no light step, no tick, no semantics change. |
| Locked-press dissent | KLANG's refusal knock governs over HAUT's silence (already adjudicated, §7.3 of the design): sound = refusal knock **6ms, gain 0.2, 320Hz**, throttled 250ms; Druck adds its haptic twin: timings `[0, 16]`, amplitudes `[0, 140]`, legacy `vibrate(16)`. Sound-off: the haptic still fires (engine S0 law — muting loses timbre, never the event). |

**Bolt-as-handle, closed:** a tap landing on the bolt's pixels is a
housing press, identical in all five layers. The bolt never dips,
never wiggles, never acknowledges a finger. If a future lane wants a
denied-bolt wiggle, it is refused here in advance: a bolt that moves
under a finger and then doesn't open teaches the hand that proof is
negotiable.

## 2 · THE LANDING GRAMMAR — three landings, three weights

The Bolt arrives in three places. Until now only THROWN had a feel
(it rides inside the Throw). The other two landings were visual only.
One grammar, descending weight = descending certainty:

| Landing | Motion | Haptic | Sound |
|---|---|---|---|
| **THROWN (seat)** | GANG's keyframes stand: 50ms / 3 frames, 0 / 24 / 62 / 94dp (scaled to channel), pin §3, 2dp stepped sink | **None added.** The Throw's own haptic `[0, 24, 38, 16] @ [0, 255, 0, 175]` is the seat feel. Any second waveform at this frame is the doubling disease. | The Throw buffer only (bolt layer +30–85ms, seat +62ms), hash-guarded (KLANG). |
| **CAUGHT via JAM (revoked mid-session)** | Retract THROWN → CAUGHT keyframed in reverse over **50ms / 3 frames** (94 / 62 / 24 → ⅓ detent), then the jam tooth (MIP-02) engages: bolt drops **1dp** into the tooth notch in 1 frame. Pose lands on CAUGHT at **+90ms after jam start** — synchronised to the **second** jam strike, so eye and hand receive the demotion in the same instant. | **None added.** `playJamHaptics` (seat-knock voice 20ms @ 1.0 / 320Hz ×3 at 90ms spacing, Feel-2 C-d) is the feel of this landing. The bolt's arrival is timed to it, not doubled over it. | Jam strikes only (Feel-2 §6: fault purges the queue and speaks alone). |
| **OPEN (home, on release)** | §4's retract (pin first, then 650ms). Home arrival: bolt meets the open stop with a **0dp stop** — no bounce frame, no overshoot; the warning bed is fully exposed the frame it lands. | At home contact only: dull knock waveform `[0, 10] @ [0, 140]` (≈ amp 0.55), legacy `vibrate(10)`. Nothing during the 650ms of travel. | **Silence** (KLANG: no reverse Throw may ever be created; a home-arrival sound would be one wearing a smaller coat). The haptic is the arrival report; Law 6 twin = the exposed bed, same frame. |
| **CAUGHT via cold derivation** (Deck opens already unproven; warrant degrades at idle) | **No travel.** First composition SNAPs (KANTE B-C5); idle pose changes are 1-frame verdict steps (flip law). A bolt that glides at idle performs an event nobody caused. | None. | Silence both directions (KLANG, idle warrant flips). |

**Detent ticks (FALZ's scale) — interaction ruling:** the three
machined ticks (1dp × 8dp at 0 / ⅓ / 1) fire **no haptic and no sound
as the bolt passes or lands**, in either direction. At 50ms of throw,
a per-tick tick would be fake granularity — the same lie Feel-1 §3
refused on the slide ("more ticks would fake granularity the gesture
doesn't have"). The ticks are read, never felt. FALZ's own kill
condition stands and is extended: if they ever light, fill, label —
or knock — revert the addition.

## 3 · THE PIN — drop and lift, in numbers

Geometry from the design: notch 12dp wide, 2dp clearance per side,
tip stops 1dp above the notch floor; the notch is under the pin
only at THROWN, so the pin physically cannot drop elsewhere.

- **Drop travel: 6dp** (parked tip 7dp above the notch floor →
  seated tip 1dp above it). **34ms, 2 frames, stepped rigid**
  (0dp → 3dp → 6dp), first frame at bolt-landing +1 frame —
  GANG's "pin snapping 1 frame after landing", now with a distance
  and a curve: none. Gravity does not ease.
- **Contact tell:** on the seat frame, a 1dp contact line appears
  under the pin tip (drawn, no blur — the Bolt bar bans Compose
  `.shadow()`), and the pin's top cap steps one tone toward recess.
  Both die the frame the pin lifts.
- **Haptic / sound: none of its own.** The drop lands inside the
  Throw's +62ms seat, which already contains it. A pin click added
  here would be a second voice at the app's most sacred frame.
- **Lift (release / detention path):** pin rises the same 6dp in
  **34ms / 2 stepped frames, before the bolt moves by 1dp**
  (MIP-02's pin-lifts-first, given a duration). Haptic at lift
  start: the hand-back tick — seat knock **5ms @ 0.4 / 320Hz**
  (Feel-2 C-f's voice, reused, not re-invented); waveform
  `[0, 5] @ [0, 102]`. Sound: the same single engine call; no
  second call for the lift's end.

## 4 · RELEASE RETRACT (MIP-02's 650ms, felt)

MIP-02 specced: detention rim → pin lifts → 650ms silent retract.
"Silent" is honoured as *no sound*. Felt, end to end:

1. **t=0:** detention rim lights (MIP-02) + pin lift (§3) with the
   hand-back tick. Two reports, one instant: it is letting go.
2. **t=34–684ms:** bolt retracts THROWN → OPEN over **650ms,
   EasingA**, travel keyframed evenly (no detent stops on the way
   out — a release does not pause at CAUGHT; pausing would print
   an unproven pose the release has already disproven).
3. **No haptic, no light change during travel.** A continuous or
   stepped buzz under a retract is a progress bar in the hand.
4. **t=684ms:** home landing per §2 (dull-knock haptic, silence,
   bed exposed same frame).
Reduced motion: pin lift 1 frame, retract = 1-frame pose step at
t=0, home haptic stands.

## 5 · PROVING CREEP (MIP-02's 0.40 / 0.55 / 0.70 + jam tooth)

Each creep step: **visual only** — 1-frame pose step at the link's
answer frame, no haptic, no sound. Proving is silent (Feel-2 §4)
and the hand is not even on the instrument; a buzz per link is
progress theatre for an audience of nobody. The jam tooth's
engagement is §2's CAUGHT-via-JAM landing and inherits its feel.
The creep's cap below THROWN (0.70 max) is also a feel law: the
bolt must never *almost* seat during proving — a near-seat reads
as a seat to a peripheral glance, and the seat's feel (the Throw)
has not happened.

## 6 · WINDOW, JEWEL, SHUTTER, INVERSION — press & light discipline

- **Window tap / jewel tap:** housing press (§1), all five layers,
  no exceptions. Neither element ever scales, brightens, or dips
  independently.
- **Jewel light changes:** 1-frame steps, always (verdict law).
  The RUNNING halo ring appears/disappears in the same 1 frame as
  the jewel's state step — a ring that fades in is a pulse wearing
  a ramp. Never-dim law (TON §5) binds every press state too:
  pressing the housing may not dim the jewel, the numerals, the
  bed, or the rim step, at any frame of the dip.
- **STALE shutter:** the shutter plate covers the window in
  **2 stepped frames** (18dp → 36dp of cover), no fade, no sound,
  no haptic — staleness is discovered at the Deck's knock, never
  from the bar (KLANG). Reopening is a **1-frame snap** (retreat
  is a fact, mirroring GANG's flag law).
- **ENDING_SOON inversion:** the whole-window brass inversion
  (`#252323` on `#E2BC39`, TON) lands as a **1-frame block swap**
  at the ≤300s threshold frame — not a fade, not a cross-dissolve;
  a fade would spend 6+ frames printing a window that is neither
  state. No sound (KLANG), **no haptic**: the inversion is a
  standing condition, not an event; a buzz at T−300s would be an
  alarm, and this house's alarms are singular. It falls as a snap
  if the end moves (session extended), same law as the flag.

## 7 · RAUSCH GRAFTS — interaction verdicts

- **G2 (relay law for the jewel circuit): ADOPT as documentation**
  — it is already §2 of the design stated as wiring; the lamp
  derivation test should be written as a circuit test (dark unless
  the fold is LIVE). No feel change.
- **G3 (carabiner sleeve threads on the pin): ADOPT, with a feel
  price.** Thread rings on the dropped pin are print only — but
  they change the pin's read, so the pin's seat frame (§3) gains
  one requirement: threads render only at full 6dp drop, never at
  the 3dp intermediate frame. A half-dropped pin showing threads
  would print proof at a pose that isn't proof.
- **G1 / G4 (crack seam in the bed / gapped filament after
  revocation): ADOPT as print, zero feel.** Both render in the
  JAMMED frame only, appear in 1 frame with the pose, and clear
  at the next seal's throw frame — never at release (the corpse
  is cleared by the next act of trust, not by opening).
- **G5 (eclipse jewel) / G6 (spirit bubble in the bolt): REFUSED
  on feel grounds.** G5 replaces the halo ring's size cue with a
  corona that must be *read*; BLICK's gate passed on size, not
  symbolism. G6's bubble can only centre at THROWN — which the
  pin already proves with geometry; a second, liquid proof of
  the same fact is the six-prints disease in miniature.
- **A1's wire shiver (200ms at the throw): REFUSED.** The throw
  is 50ms inside an 85ms sound window; a 200ms shiver outlives
  its own event by 4× and reads as vibration, i.e. as a fault.
  The Bolt's stillness after the seat *is* the held read.

## 8 · BEDACHT'S THREE FLAGS — interaction dispositions

1. **STALE_WHILE_RUNNING pairwise test: SUPPORTED, strengthened.**
   Add to BLICK's battery as T10: STALE vs RUNNING forced choice
   at the CAUGHT bar (≥95% "not protected"). Druck adds the felt
   half: if T10 fails on pose-dominance, the cure is **not** a
   haptic (the phone is in a pocket or face-down when stale
   lands; a buzz with no actor is KLANG's jump scare) — the cure
   is the shutter (§6) growing a 2dp brass top edge in STALE
   only, priced S, print only. Ruled here so the lane exists
   before the battery runs.
2. **Font scale ≥1.3:** SUPPORTED — steal from the channel.
   The channel is the one flexible element (§1 of the design);
   at font scale ≥1.3 the window grows 102 → **118dp** and the
   channel surrenders the 16dp (channel floor: never below
   120dp at the 360dp reference; below that floor the housing
   wraps to 72dp tall before any digit clips). A clipped digit
   is a wrong time; a shorter channel is still a true bolt.
3. **B-C1 / B-C2 as care gates:** CONCURRED, no addition — MIP-02
   already closes both as mappings of existing VM flows. Druck
   notes only that §2/§4's landing grammar is written against
   the closed inputs; shipping the Bolt without B-C1/B-C2 makes
   the CAUGHT and detention faces unreachable lies of omission.

## 9 · MODE MATRIX (every Bolt feel, by device mode)

| Mode | What survives |
|---|---|
| Sound OFF | All haptics in this file fire unchanged (S0 law). |
| Haptics OFF | All sounds, dips, and light steps stand; landings read by eye alone — which is why every landing above also has a same-frame visual twin. |
| Reduced motion | Dips → light step only; retract/creep → 1-frame steps; shutter → 1 frame. Throw keyframes collapse to final pose at the anchor frame (GANG). Haptics and sounds unchanged — reduced *motion* is not reduced *truth*. |
| Night | All gains/haptics identical; only KLANG's housing-knock gain steps 0.10 → 0.06 and light steps read from TON's night row. Feel does not get quieter in the hand at night — a 1 AM landing must land as certainly as a noon one. |

## 10 · ACCEPTANCE (interaction, executable)

1. Grep: no `play*` call exists in the SealBar composable; the
   housing knock fires in the host handler only (KLANG's gate).
2. Waveform audit: every haptic on this surface is one of the
   five house voices or a waveform tabled in §1–§4 of this file.
3. Frame audit on renders: pin intermediate frame shows no
   threads (G3); shutter mid-frame is exactly 18dp of cover;
   inversion has no intermediate frame at all.
4. Timing audit: CAUGHT-via-JAM pose lands within ±1 frame of
   the second jam strike (+90ms); pin lift completes before
   bolt travel frame 1 of the retract.
5. BLICK T1–T9 stand as the ship gate; T10 (§8.1) joins them.

— Druck, interaction detail
