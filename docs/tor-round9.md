# Tor — Round 9 direction (Ton's sound + haptic score)

Source: docs/feel-3-ton.md — the door's complete sonic score,
five door-local `playDoor*` methods; every other engine voice
untouched. Builder: this is FIX-7's full extent.

## Amendment to round 3 (recorded, not buried)

Round 3 ruled silence over the arm. Ton and Druck converge
against it from two directions (D-F11: a mute offer can be
missed by a half-asleep man; Ton: the beat fires at the state
transition, not as a countdown climax). **Amended ruling: the
countdown stays totally silent — every second, every taper
length, audio and haptic; Schliff's per-second detent remains
rejected. The ARM carries exactly one beat**: 300Hz, 44ms,
gain 0.30 (half the arrival's), chordRatio 1.5, with a
softened detent haptic [0,150,60] — fired at the VM state
transition only, including FIX-5c's held→offer resolutions.
Schwelle's knock survives only in this reduced form.

## The score (adopted whole)

- Arrival: FIX-7c verbatim — 300 / 44 / 0.60 / 1.5, latch-
  engaging haptic.
- RETURN press: 300 / 28 / 0.48 / 0.9 (release chord),
  latch-releasing haptic. This CURES a live defect: RETURN
  currently spends `playHarmonicSeal(true)` — 0.62/48ms, the
  visit's heaviest sound, on the goodbye, and the seal's
  voice stolen for it. Filed as FIX-7d.
- PEEK taken: 300 / 32 / 0.50 / 1.5 (binding chord), latch-
  engaging. Currently the grant sounds like every switch in
  the app; cured with the same family.
- Exit to Disarm: 240 / 40 / 0.42 / 0.9, latch-releasing —
  fired on CONFIRMED Disarm handoff only, and `fromDoor`
  suppresses requestDisarmChallenge's own latch: one voice
  per handoff, never two.
- Explicit SILENCE: refused grants, held-door zeros, KEEP
  FOCUSING peel-backs.
- FIX-3b's two detents (reason chips, particles) die with
  their wires. The five `playDoor*` methods are the face's
  sonic closed world; Ton's §9 grep/test verification is the
  proof shape.

## Night law (numeric, binding)

Door ceiling 0.60 < seal 0.62 < completion/tear 0.68. No
door haptic spends amplitude 255. Max 3 audible events per
120ms per visit. No day/night gain variant. Sound never
grades the man: identical arrival for every path, tier,
count, and homecoming.

## Riders (not this cell's fixes; closing comment carries them)

The pill's `end2MinutePeekEarly` seal-voice spend; the pill's
15s detents; unpersisted `hapticsEnabled`.

— Tor
