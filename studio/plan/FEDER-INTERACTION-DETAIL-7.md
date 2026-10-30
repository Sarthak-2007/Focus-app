# FEDER — INTERACTION DETAIL, ISSUE #7 (THE SHIELD / THE ARM)

Seat: Feder, interaction detail (dedicated-100). Scope: the feel of
every control on Shield, and above all the ARM — the moment
blocking goes live. Upgrades wave-2's priced remainders (R-10 sled
truths, R-12 finish pass items 3–6, Ghost Preview's press) with
numbers. DECLARED — nobody compiled.
Law held: fonts Sora / Atkinson Hyperlegible / Anonymous Pro ·
never green · BLOCKING NOW derives only from the two cascade
witnesses (untouched — this spec governs feel, never derivation).

## 1. THE ARM — the moment blocking goes live

The seal throw is the arm's voice and it is already one voice,
bit-identical for hand and routine arms (SEAL_THROW waveform
[0, 24, 38, 16]ms / [0, 255, 0, 175]; golden buffer, 44,100
samples). Feder's law for Shield: **the arm is felt in the hand
that is holding the phone, on the screen he is looking at.**

- **On Shield when a session goes live** (hand arm from the Deck,
  routine firing while Shield is open — both reach this screen):
  the status sentence's re-strike to "BLOCKING NOW — …" lands in
  the SAME frame as the seal throw's haptic seat (the waveform's
  third segment, t≈62ms). Implementation: the status line's glyph
  flips ○→● and the verb crossfades (180ms specA) timed from the
  session-live emission, not from the next poll. A re-strike that
  arrives silently, seconds after the sound, reads as a print
  catching up with a rumour.
- **The glyph seats, it does not swap:** the ● scales 0.6→1.0 over
  180ms EasingA with the solidity arriving AFTER the outline — for
  180ms the screen shows the wall going solid, then it IS solid.
- **ARMED ≠ BLOCKING in the hand (the distinction this issue
  exists to teach):**
  - ARMED (routines set, no session live): every control on this
    screen is LIGHT — sled detents at the §2 weights, cap press at
    §3. Nothing on an armed-but-not-blocking Shield may use the
    seal waveform, the 255 amplitude, or any gain above 0.48.
    Armed is a promise; it must not feel like a fact.
  - BLOCKING (session live): the ratchet refusals (§4) carry the
    extra weight, the status glyph is solid, and the seal voice
    has sounded. Blocking is a fact; it is allowed to be heavy.
- **Routine arm while the phone is in his pocket** is out of this
  screen's scope — but its voice stays the same seal throw: he
  should recognise the arm by sound alone, from another room, as
  the same event he feels here.

## 2. THE SLED — press and throw craft (R-10 completed)

Acquitted physics stand: 1:1 drag, 800dp/s flick, Curve A settle
260ms one stop / 340ms two stops, cap colour follows the rocker.

- **S-1 cured by constant:** drag and draw share ONE stop-spacing
  constant, 55dp (the midpoint of the 56/54 mismatch — the ~4dp
  far-stop lag dies; the rocker's drawn position equals its felt
  position at every stop).
- **Detent voices (machined, per stop crossed mid-drag):**
  `createOneShot(4, 110)` crossing INTO PEEK's notch;
  `createOneShot(6, 150)` crossing into SHUT's or OPEN's notch —
  the middle is a pass-through (1ms/520Hz pitched detent at commit
  stands, per the M1 ruling in code), the ends are seats.
- **Seat (commit):** existing fireCommitFeel stands — SHUT latch
  4ms, PEEK detent (1ms, 520Hz), OPEN latch-released. Feder adds
  the landing dip: on seat, the cap dips 1.5dp toward the track
  over 50ms and recovers over 140ms EasingA — a rocker that has
  weight when it lands, weightless while it flies.
- **V-2 provisional cap (adopted in R-10, specced here):** between
  detents the cap draws hollow at 55% ink (the solidity scale's
  PEEK density); it fills to its target density over the settle's
  last 80ms, so the hand sees the landing AND the landing's
  meaning before the voice confirms it.
- **S-3 (mid-drag re-tier):** an external re-tier during a drag
  fires one 8ms / amplitude 130 knock at the moment of override
  and the rocker re-seats under the thumb — never a silent erase,
  never a fight: the drag ends, the knock says why.

## 3. THE CAP (LOADOUT) — press craft for Ghost Preview

Ghost Preview (R-4a, ~120 lines) is unbuilt; its PRESS is specced
now so the builder pass doesn't invent it:

- **First tap (preview):** cap travels 3dp down over 70ms EasingA
  and HOLDS at depth while the ghost state stands — the control
  stays pressed because the act is half-done. Ghost caps render at
  40% ink with the solidity scale applied. Voice: 300Hz, 30ms,
  gain 0.36, chordRatio 1.2 — a question, pitched between the
  door's 300Hz family and nothing. Haptic [0, 10, 20, 10] /
  [0, 130, 0, 90]: a press that does not seat.
- **Second tap (commit):** cap seats the last 1dp (70→ total 4dp
  travel) and returns over 220ms; the commit voice is the sled's
  own grammar scaled up — mechanical latch engaged + the tally
  re-strike (§5) in the same frame. If the committed loadout is
  STUDY (the one with sniper/blocker side-effects), the voice adds
  ONE extra 8ms knock 90ms after the latch: the side-effect has a
  sound because it has a print.
- **Abandon (tap elsewhere / 4s timeout):** cap returns over
  180ms, ghost inks drain at 120ms, NO voice, NO haptic past a
  single 4ms / 80 release tick. An un-chosen act must leave
  almost no trace in the hand.

## 4. REFUSAL AT THE SLED (R-7 remainder, feel half)

The print half is priced (~½ builder-day, VM carries the package
to the row). The FEEL half, buildable with it:

- The refused sled does not move AT ALL — not a shake, not a
  bounce (a wall does not wobble). The cap's density drops to the
  55% provisional hollow for 600ms and restores: the control
  briefly admits it cannot be what he asked.
- Haptic: [0, 16, 60, 16] / [0, 170, 0, 170] — two equal knocks,
  a door tried twice by the same hand. Sound: 220Hz, 40ms, gain
  0.40, chordRatio 0.9 — BELOW the door family's 300Hz: refusal
  is the lowest voice on the screen, and it is spent once per
  gesture, never per recomposition.

## 5. TALLY JUMPS + STATUS RE-STRIKE

- **Tally segment tap:** 2dp dip, 50ms down / 160ms up, commit on
  press-down; the scroll it triggers is a 420ms specB glide
  (EasingB 0.05, 0.7, 0.1, 1) — the catalogue visibly TRAVELS to
  the group, it does not teleport; the group header lands with a
  6ms / 110 seat tick.
- **Status re-strike (any state change):** whole-line crossfade
  180ms inside the fixed 40dp slot (wave-2's cure); the glyph
  alone scales. A re-strike NEVER fires a voice on this screen
  except the §1 arm — prints are silent; only acts speak.

## 6. ROUTINE ROWS + ON/OFF CAP

- Row expand/collapse: height animates 280ms EasingA, content
  fades in the last 120ms — the row opens like a drawer, its
  contents already inside.
- ON/OFF cap: radius 12dp (R-12), press travel 2dp / 60ms,
  commit voice = sled detent family at half weight (4ms / 110)
  ON, latch-released OFF. A row that prints WON'T FIRE disables
  the cap's voice entirely — a dead control must not sound alive.

## Price
§1 ~20 LOC (glyph seat anim + emission-timed re-strike) · §2 ~30
LOC + the S-1 constant · §3 rides Ghost Preview's 120 · §4 ~20
LOC with R-7's print half · §5 ~15 LOC · §6 ~15 LOC. 0 DB, 0 new
prints (every word above is already ruled). All feel checks batch
into the merge's on-device pass with Panzer's finish items.

— Feder, interaction detail
