# KANTE — INTERACTION DETAIL — #10 RECEIPT: PRINT, TEAR, SETTLE
## Kante, interaction detail (dedicated-100 seat). The surface Sarthak explicitly loved (❤️, batch-7 slip) — its character is protected; this file perfects its physics and touches nothing acquitted.
## Status: SPEC ONLY — no Kotlin edited, nothing compiled (Ren compiles, on Sarthak's word).

Grounding read before writing: AUDIT-receipt.md (Zettel; binding acquittals —
the slip's character survives, it never re-strikes, numbers never animate,
whole-document arrival), ISSUE-10-WAVE2-REPORT.md (Lade: A5 3dp/no border/
shadow-only lift/contact anchor, A7 feed = `slideInVertically` specC 280ms +
fadeIn, A8 dismissal = DOWN-gesture on scrim after 2,000ms dwell, faulted
slips not sheet-dismissible), and ReceiptSheet.kt as built (specC =
`MonolithCurves.specC()` = tween 280ms, `EasingC` CubicBezier(0.3,0,0.8,0.15);
shadow 10dp on RoundedCornerShape(3dp); dwell gate at :124-126).

The defect at gallery standard: A7's feed is ONE smooth tween. Real thermal
paper does not glide — it is DRIVEN, in bites, by a roller, and it RELAXES
when the drive stops. A single easing curve reads as a card sliding (Material
muscle memory), not as a document being printed. The fix is the feed's
velocity profile, its shadow life, its bottom edge, and its settle — the
print CONTENT and its laws are untouched.

---

## 1 · THE FEED — driven, not glided (replaces the single specC tween)

Total **340ms** (was 280ms — the extra 60ms buys the hesitation; a feed
shorter than ~300ms cannot be perceived as driven). Whole document moves as
one body throughout (audit: no per-line printing, no re-strike — binding).

Velocity profile, translateY from +100% (below the bottom edge) to rest,
piecewise — implement as a single `keyframes`/`KeyframesSpec` on the same
`slideInVertically` custom-offset path, NOT three chained animations:

| t | offset | what the paper is doing |
|---|---|---|
| 0ms | +100% | leading edge at the slot |
| 0–150ms | +100% → +22% | DRIVE 1: fast bite, `CubicBezier(0.3,0,0.7,0.4)` — near-linear with a soft start; rollers do not ease-in elegantly, they grip and pull |
| 150–185ms | +22% → +20% | THE HESITATION: 35ms, only 2% of travel — the roller-slip every real printer has. This 35ms is the entire difference between "printed" and "slid". Do not smooth it away. |
| 185–310ms | +20% → −0.4% | DRIVE 2 + OVERSHOOT: `CubicBezier(0.2,0,0,1)`, landing 0.4% of slip height PAST rest (for a 480dp slip ≈ 2dp too high) — paper pushed slightly too far by the last bite |
| 310–340ms | −0.4% → 0% | THE RELAX (§2) |

- **Fade:** the accompanying `fadeIn` is DELETED. Paper does not fade in;
  it arrives opaque or it is a ghost. Opacity is 1.0 from frame 0. (The
  scrim's own 0.12 defocus fade is the host's, untouched.)
- **Restored slips:** `EnterTransition.None` stands (wave-2 Z6) — a restored
  slip was printed in a previous life; it renders SETTLED, §2 already done.
- **Reduced motion (scale 0):** the whole profile collapses to a cut —
  slip present, settled, frame 0. The hesitation carries no information.

## 2 · THE SETTLE — paper relaxing, once, then stillness forever

- The 310–340ms relax (§1) is the settle: 2dp of upward overshoot returning
  to rest as the sheet's internal tension releases. Amplitude cap: never
  more than 2dp regardless of slip height (scale the −0.4% by
  `min(0.004 × height, 2dp)`).
- **Shadow life (the light):** elevation shadow animates WITH the feed, on
  the same keyframes: **4dp at +100% → 7dp at the hesitation → 10dp at
  rest** (10dp is wave-2's ruled rest shadow — endpoint unchanged). A shadow
  that pops in at full 10dp on frame 0 makes the slip a card that appeared;
  a shadow that GROWS as the paper lands makes it a sheet that arrived.
  Implementation: animate the `.shadow()` elevation from an `Animatable`
  driven by the same 340ms spec — one clock for offset and shadow, never two.
- **After t=340ms: nothing moves again. Ever.** No breathing shadow, no
  idle sway, no attention pulse on the №. The audit's stillness law is the
  receipt's dignity; this file's settle exists precisely so the stillness
  that follows feels EARNED rather than frozen.

## 3 · THE TEAR EDGE — the bottom of the slip is torn, not cut

Wave-2 cut the slip square (3dp, thermal). One refinement, bottom edge only:

- The slip's bottom edge carries a **perforation tear**: a deterministic
  zigzag path, amplitude **1.5dp**, pitch **7dp**, phase fixed (seeded by
  the receipt №, so slip № 041 always tears identically — a reprint is the
  same document, not a similar one). Top and side edges stay straight 3dp:
  one torn edge says "torn from the roll"; four torn edges say "craft paper".
- The tear is GEOMETRY (clip path), not an image — no AI imagery, no PNG.
  The 10dp rest shadow follows the torn path (shadow shape = clip shape),
  so the light agrees with the paper.
- **No tear GESTURE is added.** Dismissal stays wave-2's law (DOWN on scrim
  after dwell; §4). A swipe-to-tear dismissal was considered and is REJECTED
  here on the record: it would make the loved object disposable-by-gesture,
  and it contradicts A8's ruled dismissal. The tear is how the slip was BORN
  (off the roll), not how it dies.

## 4 · DISMISSAL FEEL — the dwell gate must not feel like a dead tap

Wave-2's trade (a deliberate tap inside the 2,000ms dwell "reads as
nothing") is accepted for the SLIP's safety — but "nothing" is below
standard. The acknowledgment that costs nothing:

- A DOWN on the scrim during the dwell: the scrim's alpha moves
  0.12 → 0.16 over **90ms** and back over **160ms** (house seat/throw) —
  the room darkens a breath under his finger and releases. No dismissal, no
  haptic, no sound. He learns the tap LANDED and the slip is staying, which
  is the truth, instead of learning that the app ignored him, which is not.
- A DOWN after the dwell dismisses as ruled; the exit is a **cut** (host
  unmounts — wave-2 deleted the dead fadeOut; do not resurrect an exit
  animation). Paper you have finished reading does not fly away; it is gone
  when you look back. Exit duration: 0ms.
- A DOWN on the SLIP ITSELF (not scrim): nothing at all — 0dp travel, no
  wash, no dip. The slip is paper on a desk; pressing paper does not press
  back. Any future button proposed ON the slip must come to this seat first.

## 5 · FEED SOUND + HAPTIC — paper has a voice, at paper volume

Only when the app's sound-effects setting is ON; haptic independent (§5.3):

- **5.1 Feed sound (procedural, TactileSoundEngine — no audio files):**
  filtered noise, band-pass 1,200–3,500Hz, gain envelope: attack 20ms to
  **−18dBFS peak**, sustain through DRIVE 1, **dip −6dB during the 35ms
  hesitation** (the sound hesitates WITH the paper — one clock, §2's law
  applied to audio), second swell −20dBFS through DRIVE 2, hard end at
  310ms. Then §5.2. Total ≤340ms, never looped, never repeated.
- **5.2 Settle thock:** at t=310ms, one 25ms low sine burst, 170Hz,
  gain −24dBFS, decay-only. The paper landing flat. This replaces any
  impulse to add a "success" tone — a thock has no pitch direction and
  therefore no valence; the receipt is a record, not a reward.
- **5.3 Haptic (sound setting does NOT gate it):** during the feed, two
  micro-pulses synced to the drives: `[at 10ms] 4ms @ amplitude 40`,
  `[at 195ms] 4ms @ amplitude 40` — the roller's two bites, felt not heard.
  Nothing at the settle (the thock's frame is silent in the hand; sound and
  haptic never strike the same instant on this surface). If the device
  cannot do amplitude control, both pulses are omitted rather than faked
  at full strength — a loud buzz for a quiet slip is worse than none.

## 6 · ACCEPTANCE (phone-checkable)
1. Feed visibly hesitates once, ~⅘ down, for a beat; no fade anywhere in the arrival.
2. Slip lands ~2dp high and relaxes into place by ~340ms; shadow visibly deepens as it lands; then 60s of absolute stillness.
3. Bottom edge tears identically for the same № across reprints; shadow follows the tear.
4. Tap inside 2s dwell: scrim breathes 0.12→0.16→0.12, slip stays, nothing sounds. Tap after: instant cut, no exit animation.
5. Sound on: feed noise dips audibly at the hesitation; one soft thock at settle; no tone, no chime, ever.

— Kante, interaction detail
