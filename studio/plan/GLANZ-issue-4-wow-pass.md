# GLANZ — Issue #4 (Deck top strip / THE BOLT): the wow pass

**Seat:** Glanz, wow-level designer (dedicated-100). **Secondary issue** (primary: #10).
**Lens:** the bar is *better than anything — commercial level and above* — measured
against Braun / Teenage Engineering instrument craft and Blockit's discipline (the
measuring stick Sarthak himself named), never its skin.
**Grounding:** `SealBar.kt` as built on `agent/issue-4`, `DESIGN-issue-4-the-bolt.md`
(ANTLITZ cell), the wave-2 reports, and the live thread (Krall's charges K-1..K-12,
the thoughtful pass, Falz). Convergence with Krall is noted where it occurs — two
seats arriving at one charge independently is evidence, not duplication.
**Laws honoured:** nothing compiled; Sora / Atkinson Hyperlegible / Anonymous Pro;
never green; no AI images.

---

## Verdict in one paragraph

The Bolt is a world-class *specification* and a promising chassis — and it is not yet
the instrument the spec describes. Its eloquence is in health states; its honesty
states (stale, jammed, detention) are unrenderable in the built code; its body is
painted in scheme slots instead of materials; its machined details (notch, contact
rib, keyframed throw) are asserted in comments rather than drawn. The design's own
ship gate — BLICK T1–T9 on real renders — remains the final word over every claim
in this file, this pass's included.

## 1 · THE HONESTY STATES (blocking)

Built enum (`SealBar.kt`): OFF, UNPROVEN, BLOCKING, RUNNING, RUNNING_UNPROVEN,
ENDING_SOON — six. Design §3 table: eleven rows. Missing and material:

- **STALE_WHILE_RUNNING** — no `shieldStale` input exists in the fold (design charge
  B-C1, open). A mid-session proof loss therefore renders as RUNNING: lit jewel,
  numerals up — a falsehood, not an omission. This is the single worst gap on the face.
- **JAMMED**, **DETENTION_FAULT**, **FLOW / FLOW-UNPROVEN**, **NO_READ** — all absent;
  folded into generic rows.
- Root cause, agreed with Krall's K-charges: `sealBarStateFor` consumes the old
  strip's `blockWord` string — the header (SealBar.kt:48–56) admits "a pure
  re-encoding of the strip's word." The design §2 promised a host fold from one
  warrant read; what shipped is a translation layer over the three-word vocabulary,
  so the Bolt cannot say anything the strip couldn't.

**Fix (priced, design's own backlog promoted to blocking):** land B-C1 + B-C2 inputs;
extend the enum — STALE_WHILE_RUNNING (pose seated, lamp dark, window shuttered)
first, then JAMMED and DETENTION_FAULT; replace the 6 ad-hoc spoken descriptions
with LETTER's closed 11-sentence set in the same pass (TalkBack parity is the
design's own law). **Until the stale row lands: do not swap the strip for the Bolt** —
a text dump that prints its uncertainty beats a beautiful object that hides it.

## 2 · MATERIAL IDENTITY

Thrown plate: `inkControlFill()` — a named material, correct. Everything else is
framework slots: housing `scheme.surfaceVariant`, track `scheme.background` +
`outlineVariant` border, unthrown plate `scheme.surfaceBright` (the same unmapped
slot leaking on the Receipt, #10), pin `scheme.outline`, socket/collar scheme tones.
An instrument whose body recolours with the scheme has no material of its own;
commercial objects are recognisable unbranded because their materials are constants
(TON's table was written to be exactly that).

**Fix:** housing, track floor, unthrown plate, pin, collar take named tokens from
TON's table; scheme reads survive only where the design bills them as mode-follows.
~6 colour expressions; no geometry touched.

## 3 · MACHINED DEPTH (the flatness gap)

- **Pin without notch** (converges with Krall): the pin is a 4dp box changing height
  7→12dp; the bolt's face carries no notch for it to enter. Draw the notch as a
  recess on the bolt's top edge at the thrown position (design §1: 12dp wide, pin
  tip 1dp above its floor) so the geometry law is *visible*, not asserted.
- **No contact rib:** design §5 assigned a 1dp contact shadow drawn inside the
  channel; the built track is border + flat fill, so the plate floats in an outlined
  slot. One 1dp south line under the plate inside the channel, no blur.
- **Tween, not throw:** travel is a 50ms continuous `tween` (code comment admits the
  keyframed seat is merge-pass). Keyframes 0/24/62/94 + pin-at-seat + 2dp stepped
  sink are the difference between a diagram of a bolt and a bolt — mass is in the
  easing, and UI easing reads as UI.

## 4 · THE BLANK WINDOW'S RESTING FACE

Presence law (design §4) is acquitted: no numerals without a sealed end, no
placeholders. But as built, the consequence is a 102×36dp black void — the strip's
second-largest mass — reading as a dead display in OFF / UNPROVEN / BLOCKING, most
of the instrument's life. Commercial panels give empty windows a resting face.

**Fix (priced):** resting face = **shuttered glass**: three horizontal 1dp hairline
slats across the glass (glass-ink, low alpha) whenever no numerals print; numerals
replace the shutter whole when an end exists; the STALE face (§1) closes the slats
solid, keeping its distinction. One draw pass, no new vocabulary. The `END`
engraving then legends a shuttered window truthfully, as real panels legend theirs.

## 5 · THE GATE OVER EVERYTHING

BLICK T1–T9 on real renders — cold reads, pairwise OFF/RUNNING at 10% brightness,
greyscale twins — is the design's ship law and has not run (nothing compiles without
Ren on Sarthak's word). The §6 budget arithmetic passes *on paper* (125.7 vs ≤41.9);
paper is not the gate. Every verdict in this file is provisional until the battery
runs, and the acceptance frame is the paired OFF/RUNNING night shot with greyscale
twins — a lone RUNNING render proves nothing, because OFF is the state that can
hurt him.

## Priority order

1. STALE_WHILE_RUNNING + B-C1/B-C2 (§1) — truth, blocking.
2. Material tokens (§2) — identity.
3. Notch + rib + keyframed throw (§3) — craft.
4. Shuttered resting window (§4) — finish.
5. BLICK battery — the verdict.

— Glanz, wow-level designer, issue #4. DECLARED, uncompiled.
