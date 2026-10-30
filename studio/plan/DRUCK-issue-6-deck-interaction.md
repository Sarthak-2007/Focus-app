# DRUCK — ISSUE #6 · THE DECK, INTERACTION DETAIL
## Seat: DRUCK (interaction detail, dedicated-100) · Fri Oct 30, 2026
## Status: SPEC ONLY. DECLARED, uncompiled. Nobody compiles — Ren, on Sarthak's word.
## Consumes as law: AUDIT-deck.md F1–F22, ISSUE-6-verdict.md, FEEL-1 (touch),
## FEEL-2 (sound), FEEL-3 (motion), RAHMEN's composition, MIP-03's state model.
## This file re-opens none of them. It completes the controls they left
## with fewer than five layers, and judges the crazy ideas on feel.

**DRUCK'S STANDING LAW:** every control declares five layers —
(1) travel dp + wall/shadow behaviour, (2) spring (easing + ms in/out,
overshoot or none), (3) haptic waveform (timings ms / amplitudes
0–255, legacy fallback), (4) sound (existing engine voice, named,
or ruled silence), (5) light step (day/night, never green).
House voices only: detent, latch, firm click, seat knock, dull
knock (FEEL-1 §8). Zero new sound buffers in this file.

**Already at five layers — record, do not re-spec:** the wheel
(FEEL-1 §1–2, §7 + FEEL-2 §3, incl. W1 end-stop and H1 FLOW-hub),
the slide (FEEL-1 §3–5 + FEEL-2 §5, incl. S1–S4), the seal Throw
(FEEL-2 §4). Druck's work below is everything else on the Deck.

---

## 1 · THE BUTTON LAW, APPLIED — Deck key-cap standard

Every key-cap on the Deck (DONE, STOP, utilities, FLOW's print
successor if it ever becomes tappable) shares one body so the hand
learns the app once:

- **Rest:** face raised **2dp** over its ground; bottom wall
  (the cap's lower edge band) **3dp**, drawn as a solid tone step,
  no blur, no Compose `.shadow()` elevation.
- **Press:** sink **1dp over 45ms** (`HubPressDip` — FEEL-1 §6's
  "ONE press feel" made Deck-wide), wall 3dp → **2dp** as it
  seats, specular/core light travels with the sink where the cap
  has one (hub precedent: 2dp toward 135°).
- **Return:** **160ms** (`HubSeatReturn`), no overshoot.
- **Commit:** on release-inside (hub law); drag-off cancels in
  silence — except the STOP chair, which commits on DOWN for the
  reasons FEEL-1 §6 already ruled. Exceptions are named per
  control below, never improvised.
- **Light:** pressed face steps one tone toward its ground tone
  (day and night rows of the scheme table); label ink never dims.
- **Double-tap guard (Deck-wide, new):** a second tap on the same
  cap within **300ms** of a commit is consumed and dropped,
  silent — generalised from FEEL-1 §6's STOP law ("a control
  that acknowledges twice teaches double-tapping").
- **Targets:** visual cap ≥44dp tall; touch target ≥48dp in both
  axes, padding invisible. A cap that looks 44 and touches 48 is
  honest; the reverse is a trap.

## 2 · DONE — the Deck's most-used verb had no press. Now it does.

Today DONE is a key-cap body with the audit's acquittal ("verb in
a verb's body was right") and no feel spec anywhere in the cell's
files. FUNKE's delight pass promises "DONE's click" — this is it,
priced inside F7's M:

| Layer | Spec |
|---|---|
| Travel | §1 standard: sink 1dp / 45ms, wall 3→2dp, return 160ms. |
| Commit | Release-inside (hub law). The task completes at the **release frame**, not DOWN — DONE is reversible in the hand until the finger leaves, which is what makes a fast DONE safe to throw. |
| Haptic | Firm click, as built: waveform `[0, 13, 8, 8]` timings with amplitudes `[0, 225, 0, 135]` (13ms strike, 8ms breath, 8ms tail — the engine's `playFirmClick` haptic twin, FEEL-2 §0). Legacy fallback: `vibrate(25)`. |
| Sound | `playFirmClick` — 12ms, gain 0.50, 620Hz, chord 1.1. One call, from the cap's own handler; the ViewModel stays silent (FEEL-2 Law 2: state changes are silent; the surface that moves owns the sound). |
| Light | Pressed: §1 tone step. **Completed form:** at the commit frame the cap does not bounce back first and ask questions later — it returns over 160ms *while* the task line advances (F7's ghost title swaps under it). DONE never shows a checkmark, a burst, or a colour celebration: completing homework is not a slot machine. The cap for the next task is at rest height, full ink, by the time the return lands. |
| Failure | No active task (idle ghost state): DONE does not compose (F7) — a DONE with nothing to finish is a dead control, and dead controls get no feel because they get no pixels. Tap during the 300ms guard: §1, consumed silent. |

## 3 · THE STOP CHAIR — FEEL-1 §6 completed (light + waveform)

FEEL-1 specced the chair's dip, DOWN-commit, and era haptics in
engine terms. Druck ratifies all of it and adds the two layers
it left blank, so F6 can land whole:

- **Light, grace era (≤10s):** the chair's face is the scheme's
  ordinary control tone; its words are FUNKE's pair, adopted:
  `STOP — FREE, NO BREAK USED` (grace) / `STOP — USES YOUR 1
  BREAK THIS WEEK` (priced). At the era crossing (no finger
  down), the chair's **wall** steps from 3dp to 4dp and its face
  steps one tone heavier — the price becomes visible as *mass*
  in the same frame the words change, with no haptic (FEEL-1:
  a buzz at 10s with no touch is an alarm from nowhere).
- **Light, priced era:** face = the heaviest control tone on the
  Deck short of fault print (Lamp Test: chair luminance ranks
  below the hero glyph, above every other control — the cure
  for the verdict's "unranked STOP-chair luminance" failure).
- **Waveforms (FEEL-1's engine terms, made exact):**
  grace DOWN = firm click `[0, 13, 8, 8] @ [0, 225, 0, 135]`;
  priced DOWN = seat knock `[0, 14, 7] @ [0, 217, 76]`
  (14ms @ 0.85 + half-length tail @ 35%, ≈230Hz class);
  zero-token tap = dull knock `[0, 10] @ [0, 140]` (≈0.55, 220Hz).
  Legacy fallbacks: 25 / 20 / 10ms single vibrates.
- **No pressed-state latch:** the chair is momentary in both
  eras; after a priced tap opens the valve challenge, the chair
  returns in 160ms and the *overlay* owns the hold (§4). The
  chair never stays sunk while the challenge runs — a sunk
  chair under an open overlay prints "already spent".

## 4 · THE VALVE HOLD — the 60-second hold had a label lying about
## it (audit §5.3) and no physics at all. The hold, specced.

The overlay's hold is the longest press in the app and until now
the least designed: sixty seconds of finger with (per FEEL-2)
its per-second ticking killed and nothing put in its place.
A man holding for 60s needs to feel *quarters*, not seconds:

| Layer | Spec |
|---|---|
| Body | Hold cap: full-width key-cap, §1 body at rest (2dp raise, 3dp wall), target = the whole overlay card's lower half, min 72dp tall. The word on it is `HOLD TO USE YOUR BREAK` — the gesture and the price in one line; `(60S HOLD)` as a label format is dead (audit). |
| Travel | Progressive sink: **1dp at DOWN** (§1 dip, 45ms), then **+0.5dp at each quarter** (15s / 30s / 45s), total 2.5dp at 45s+, wall compressing 3dp → 0.5dp across the same quarters. The sink *is* the progress: the cap is visibly, tangibly deeper the longer he has meant it. The overlay's progress print, if any, is redundant and subordinate — sink first, numerals second. |
| Quarter detents | At each quarter crossing: seat knock **10ms @ 0.5 / 280Hz** (the strait-toll voice, reused) — waveform `[0, 10, 5] @ [0, 128, 45]`. Exactly **3 knocks in 60s**. Per-second ticks stay dead (FEEL-2 Law 4: the Deck never sounds the passage of time; quarters are *events* — thresholds crossed — not time passing). |
| Release early | Finger leaves before 60s: cap springs home over **120ms, EasingA, no overshoot**; one dull knock `[0, 10] @ [0, 140]` at release; **zero partial credit** — no print, no "you held 43s", nothing stored (blocker, not tracker; a partial-hold score is a streak in a trench coat). Re-press starts at 0. |
| Completion (60s) | Two-stage release voice: `playMechanicalLatch(engaged=false)` (**520Hz release chord**, 16ms) + the heaviest seat knock the family owns short of the jam: **20ms @ 1.0 / 320Hz**, waveform `[0, 20, 10] @ [0, 255, 89]`. Cap returns over **180ms** with the house's one permitted overshoot grammar (ζ=0.38, the wheel's signature — the only control besides the wheel allowed to overshoot, because this release should feel like the wheel's relief, not a button's). Then the break's own state owns the screen (F5 hero). No Throw: opening a break is not a seal and must never borrow the seal's voice. |
| Drift guard | Finger drift >24dp from the cap's centre pauses accumulation (the quarters stop; sink holds); drift back within 2s resumes; beyond 2s = release (above). A hold that punishes a trembling hand at second 58 is cruelty with a spec number. |
| Accessibility | The hold is F22 debt's hardest case, named: a switch-access / TalkBack path that completes the same price by a sustained *focus-and-confirm* (two confirms 5s apart on the same chair) must land in the same lane, or the valve is a door some users cannot open. Priced inside F22, flagged here so it is not forgotten twice. |

## 5 · UTILITIES — two chips, two different verbs, two feels

DESK CLOCK and HELD sit in one row (F-audit: row acquitted) but
are opposite kinds of control. Giving them one feel would lie:

- **DESK CLOCK — a toggle (it latches).** Press: §1 dip. Commit
  on release-inside fires `playMechanicalLatch`: **engage 760Hz**
  when the clock opens, **release 520Hz** when it closes (the
  engine's two chords exist for exactly this). Haptic twin as
  built (two-stage strike–gap–seat). **State by form:** while
  the clock is shown, the cap *stays sunk 1dp* with its face at
  the recess tone — a depressed cap = ON, readable in peripheral
  vision, no LED dot, no checkmark. It rises (160ms return) when
  the clock closes. A toggle whose body doesn't stay where you
  put it is a button pretending.
- **HELD — a door (it navigates).** Press: §1 dip, momentary,
  returns in full. Sound: the destination voice — navigateTo
  detent **540Hz class** (KLANG's press-voices-at-destinations
  ruling), fired by the destination handler, not the chip.
  Haptic: detent twin `[0, 7, 5] @ [0, 180, 70]`. The count
  badge **never moves, scales, or relights** on press — a badge
  that jumps when touched trains tapping the number, not the door.
- Both: night ink step-down (F12, `#2A2320` family) applies to
  the *rest* face only; the pressed step is computed from the
  night face, so a night press is exactly as legible as a day one.

## 6 · GUARD LINE & RAIL — the two unsigned doors get press manners
## (F20's family; neither gets a dip — text and icons are not caps)

- **Guard line (header, PARK's door):** target = its full 48dp
  band row. Press feedback is **light only**: the line's ink
  steps to full alpha and the band's bottom hairline steps one
  tone brighter for the press duration — text that sinks looks
  broken; text that *answers* brightens. Commit on release-
  inside (hub law), sound = navigateTo detent 540Hz at the
  destination, haptic = detent twin (above). Long-press: nothing
  (no new verb). Frozen-at-seal values keep their label law
  (audit §5.7) — press manners do not launder frozen print.
- **Rail stops (×4) + PARK zone:** each stop's icon+label sink
  **1dp / 45ms, return 160ms** (§1 spring, icon-scale travel —
  icons may sink where text may not, because an icon is an
  object and a word is not). Active stop: icon filled + a 2dp
  tick above it, both 1-frame state steps. Commit haptic =
  detent twin; sound = silence for sibling stops (you are
  already looking at the rail; a sound per rail tap is a
  menu bar, not an instrument), **except PARK**, which is a
  door to another room and takes the 540Hz destination detent.
  F20's print (the word PARK on the zone) lands with this feel
  or the zone stays a secret door with better manners — print
  first, feel second, both in F20's S.

## 7 · PROOF DRAWER (F16) & RECEIPT (F19) — state motion vs gesture

- **Drawer:** it opens on *state*, not on a finger (proving /
  jammed / faulted + one settle beat). State motion takes
  FEEL-3's motion spec and **no haptic, no sound, no end-stop
  knock** — a drawer that knocks when the machine opens it is
  the machine tapping him on the shoulder about its own health.
  If the drawer's header row is tappable to expand detail:
  target 48dp, §1 dip on the row, dull tick on commit
  (`[0, 7] @ [0, 128]`), expand/collapse travel per FEEL-3.
- **Receipt scrim tap (F19):** ruled — a stray scrim tap shakes
  the slip, never kills it. The shake, numbered: horizontal
  offset **±4dp, 3 cycles decaying 4 → 2.7 → 1.3dp, 240ms
  total, ease-out**, slip rotation 0 (paper does not tilt on a
  flat desk). Haptic: **one** dull knock `[0, 10] @ [0, 140]`
  at the first reversal only — three knocks would be the jam's
  repetition budget spent on a receipt. Sound: none (the knock
  is haptic-only here; a sounded shake is a cartoon). Scrim
  opacity **does not change** on press — a scrim that answers
  a tap with light teaches tapping it again.
- **Slip-swipe dismissal:** swipe threshold **80dp** of travel;
  past it, release exits the slip over **180ms EasingA** with
  the latch-release chord (520Hz) — the receipt is *filed*,
  not torn (the tear voice belongs to Tasks' counterfoil,
  FEEL-2 §0). Below threshold: spring home 160ms, silent.

## 8 · KOMET'S IDEAS — interaction verdicts (K1–K13, feel only)

- **K3 Ship's bells + FUNKE's amendment: ADOPTED as amended**
  (boundaries only, no half-block strike). Druck adds the gain
  law: bells are the only Deck sound allowed **outside** the
  five-voice census because they are not interface sounds —
  they are the room's clock. Cap: twin strikes, each ≤40ms,
  gain ≤0.30, never within the Throw's 1000ms, never during the
  valve hold. If a bell and a quarter detent collide, the bell
  yields (the hand's event outranks the room's).
- **K2 Rotate-to-seal: SUPPORTED as optional grammar, with the
  seat made felt.** Overshoot = **+18°** past the span (exactly
  2 detents — a margin the hand can count); passing the span on
  the way out takes the strait-toll knock (10ms @ 0.5 / 280Hz);
  reverse crossings ride the ordinary ladder down (no second
  encoding); the seat = held within **±2° for 300ms**, confirmed
  by the hub seat knock by count **plus** one firm click at
  grammar completion — the click is the combination *catching*,
  the one moment this ritual is allowed a voice the slide path
  doesn't have. Unseated after 3s: coast home on the built
  integrator, silent — an uncompleted ritual is not a refusal
  and must never take the refusal knock.
- **K6 Blind Deck: SUPPORTED with a pitch-collision ruling.**
  Three pitch encodings were about to share one ladder: value
  (340→880Hz, built), wire class (×0.72 / flat 240Hz, FEEL-2 §3),
  and K6's span pitch-classes. **Ruled: K6 gets no third
  encoding.** The value ladder already *is* the learnable scale
  K6 wants (the 50 knock already sounds higher than the 30
  knock — that is the built ladder, not a new feature). Block
  count as a chord: permitted **after** the hub commit click,
  +40ms, ≤3 notes at the ladder pitch of the count's own stop,
  gain ≤0.30, and the chord rides the wire class factor too
  (Law 5 — a chord that sings SOLID over a stale wire lies).
  Blind seal confirmation = the Throw, unchanged: it was
  already the eyes-free proof.
- **K12 Flip-the-record: SUPPORTED, gesture numbered.**
  Two-finger rotation of the hub disc, **≥120° within 800ms**;
  haptic-only dull ticks every **30°** (`[0, 7] @ [0, 90]`, no
  sound — a break gesture should be felt, not heard across a
  library); the disc's face swaps at the **90° frame**; comple-
  tion = latch release chord (520Hz) + firm click. Below 120°:
  spring back 200ms, silent, no partial flip state is kept —
  a half-flipped record is a break that hasn't ended, printed
  as one.
- **K13 Entropy dial: SUPPORTED, silenced.** Decay travel rides
  the coast integrator in reverse at **1 detent per 2.5s** and
  is **completely silent and haptic-free** — a wheel that knocks
  while nobody touches it is an alarm, and FEEL-2 Law 4 already
  bans the Deck from sounding time. The eyebrow print
  (`UNSEALED — LETTING GO`) is the decay's only voice. Any
  touch halts decay at that frame with the ordinary detent —
  the halt must feel like *catching* the wheel, which the built
  1:1 drag already does.
- **K1 Deck-eats-HOME, the shelves' press law:** allowed/peek
  tiles during a seal are launcher icons, and they press like
  it: tile sinks **1dp / 45ms**, halo ring steps one tone
  brighter at DOWN, commit on release-inside, **no sound**
  (launching an allowed app mid-session is not a Deck event).
  Long-press during a seal: **consumed, silent, no jiggle, no
  edit mode** — a sealed phone that enters icon-editing has
  left the session in spirit. Peek tiles take the identical
  press; banking a peek token causes **zero** Bolt reaction
  (KANTE §3.8, issue #4 — a shelf press must not glint the
  instrument).
- **K4 / K5 / K7–K11:** no interaction objection; their feels
  inherit the surfaces they touch (K5's dead detents below the
  floor take FEEL-1 §7's REFUSED class — flat 240Hz ladder —
  because a floor set by last night *is* a wire-class fact in
  the hand; K9's line is typed *before* the slide and the slide
  never waits: the slide's feel is unchanged, which is the
  whole design).

## 9 · FUNKE'S GRACE WORDS — ratified, with the squint test priced

`STOP — FREE, NO BREAK USED` / `STOP — USES YOUR 1 BREAK THIS
WEEK`: **adopted** (§3). Druck's addition — the 03:30 squint
test, made executable: both lines must cold-read correctly at
10% brightness, 40cm, in ≤1s, in Atkinson Hyperlegible at the
chair's full 72dp with the era wall step (§3) as the only
non-print difference. If either line fails, the *words* shorten
(`STOP — FREE` / `STOP — 1 BREAK`), never the type. The chair
is the one place on the Deck where a word may not be traded for
an icon: a panicking man reads STOP, he does not decode a glyph.

## 10 · MODE MATRIX & ACCEPTANCE

- **Sound OFF:** every haptic in this file still fires (S0 law).
- **Haptics OFF:** sounds and sinks stand; the valve hold keeps
  its progressive sink as its only quarter report — which is
  why the sink, not the knock, is §4's primary channel.
- **Reduced motion:** dips → light steps only; the receipt
  shake → one 2dp nudge + the knock; the valve sink compresses
  to two steps (DOWN 1dp, completion 2.5dp) with the quarter
  knocks intact.
- **Acceptance:** (a) grep — no `play*` in `setDialMinutes` /
  `setStudyBlocksCount` (FEEL-2 A1) extended by Druck: no
  Deck cap handler fires both a view voice and a VM voice for
  one commit; (b) waveform audit — every Deck haptic is a house
  voice or a waveform tabled here / in FEEL-1 / in DRUCK-#4;
  (c) the valve hold passes a device walk: quarters felt blind
  at 15/30/45 ±1s, early release at 59s spends nothing and
  prints nothing; (d) F22: DONE, STOP, valve, drawer header all
  carry semantics with the press verbs this file names
  ("press", "hold") — a semantics label that says "tap" for
  the 60s hold repeats the `(60S HOLD)` lie in the spoken channel.

— Druck, interaction detail
