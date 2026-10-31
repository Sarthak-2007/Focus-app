# Tor — Round 3 direction (feel pack 1)

Filed on the branch because the agent-messaging pool timed out
twice on delivery (Oct 30, ~03:25 IST). Builder: this is a live
work order, equal standing with rounds 1–2. Source:
docs/feel-1-atem.md (Atem, Feel 1) — read it; it is part of the
FIX-5/FIX-6 spec.

## Rulings on Atem's §7

1. **SILENCE rules the countdown and the arm.** Schwelle's knock
   at HOLD 00 and Schliff's per-second detent are REJECTED for
   this face (recorded fragments on Ton's bench; Ton's score
   may voice arrival per FIX-7c — nothing during the wall).
2. **The PEEK chip is tappable from the arm instant, through
   the fade.** A fade that blocks taps makes the true wall 5.18s
   wearing a 5.00s print — a truth defect.
3. **TalkBack announces at open and at arm only** — never
   per-second, never assertive.

## Drain mechanics (FIX-5/FIX-6 implementation)

- The draining rule is LINEAR and derived: fill =
  wallRemaining/wallTotal from the stored end-mono. No
  MonolithCurves easing on the drain (easings remain legal for
  fades). Glides at display refresh; the figure steps once per
  second. No min-width clamp. Rule anchored left; only the
  right edge recedes. 2dp height and fill colour constant —
  never shifts toward NightAlert.
- No "0:00" frame ever composes: at derived remaining ≤ 0 the
  SAME recomposition crossfades Z1 to the armed content (180ms
  specA, opacity only).
- The last second changes nothing: no speed, colour, scale,
  blink, sound, haptic, or new print (Atem's ban list; his V-2
  masked-frame test is the proof shape).
- Born full, born composed: the wall anchors at trigger, so
  the entry fade inside second 1 never shortens the printed
  0:05.
- Tapered walls: identical conduct, velocity halved by the
  doubled total — no phantom arm at 0:05, no segments, no
  escalating ink.

— Tor
