# ULYSSES

A phone blocker built as a bank strongroom.

Ulysses (working name: Monolith) is a personal Android app for one person's study day. The name is the idea: Odysseus had himself tied to the mast before the sirens, by his own order, while he was strong — this app is the ropes. In the evening you sign tomorrow: which apps stay reachable, when the phone lifts, and your own combination. Signing seals the phone behind the strongroom door. During the sealed hours the phone lives in the strongroom's launcher — allowed apps sit in the antechamber, everything else is behind the door, and reaching for a locked app is answered by the knock. At the lifting hour the bolts withdraw, the door opens, and the day is struck onto a brass day plate. Plates stack as the record of kept days — never scored, never totalled.

What I settle while I am strong, this holds while I am not. It reopens nothing. At night it tells me the truth.

## How a day runs

- **The Signing** — the evening ritual that sets tomorrow: allowed apps, the lifting hour, the combination, turned by hand as the signature.
- **The Seal** — the signed span seals whole, gaps inside it included. The sealed face is engraved once, at sealing, and never altered while the seal stands.
- **The Sealed Hours** — the launcher is the whole phone: the sealed face, the antechamber for allowed apps, held calls from unknown numbers kept quiet, contacts ringing through.
- **The Break** — a break of freely chosen length, priced by the Guard; and once a week, a 60-second emergency valve.
- **The Lifting** — at the set hour the door opens by its own weight and strikes the day plate.
- **The Drawer** — finished tasks are stored quietly, to look back through — no scores, no day-grading.

## The Guard

Three rules hold the allowance:

1. **Frozen allowance** — it can tighten instantly, any time; a raise takes effect only from the next day-start.
2. **Slow off** — turning the Guard off takes effect at the next day-start; turning it on is instant; there is no off while a sealed block is active.
3. **A stretch has no off** — the end date chosen at setup is the exit.

## Design

- **Palette** — seven inks only: `#F04FCB` · `#493EA4` · `#E2BC39` · `#C84481` · `#E47E35` · `#377ED0` · `#34323B`. Never green.
- **Type** — Sora for display, Atkinson Hyperlegible for interface and body, Anonymous Pro for numbers.
- **Feel** — every control is a physical object first: mass, resistance, settle, and its matched sound and touch are designed, not defaulted. Real-world objects in the design are the app's language for how it should feel — the app itself is software, built and judged on the phone.

## Status

The app is mid-rebuild. A redesign is being built slice by slice on top of the existing engine (blocking, boot recovery, exact alarms, data), with each slice landing as an installable build: foundation and tokens, the sealed face and ring, the signing, the antechamber, the lifting and day plate, the drawer, and the Guard. Behaviour that cannot be proven on a device yet is carried in the code and the record as unproven, never silently assumed.

## Building

- Single-module Kotlin / Jetpack Compose app.
- Builds run in CI (`rescue-build.yml`); a piece is compiled once when it is finished.
- Work is committed to this repository at major milestones.

## Project record

- **PR tracker** — issue #839 (read before opening, merging, or closing any PR; update it in the same run).
- **Decisions log** — Discussion #814.
- **Ideas log** — Discussion #673.
