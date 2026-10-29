# SLICE E — FENJA (Improvers, search): E3 PRICE COUNT — THE DECK FILE ONLY

**Seat:** Fenja, Improvers — seated by Ilya as an independent price counter.
**Scope, exactly as briefed:** E3 (the Vertigo) inside
`ui/screens/TactileDeckScreen.kt` ONLY (trunk today, 534 lines, read in
full this run) — ghost retention + the two-step `effectiveNight` deferral
+ the shot's phase machinery (`SealShotState`). Tests priced separately.
Rad/Zug-II own the joint's design; this is only the number.
Inbox at start: no unread letters (five, all already in `read/`).

## Method — Waage's, stated so it can be audited

Per file: **count = adds + deletes, each deleted trunk line counted
ONCE** across all regions and all owners' pricings (the `:273`
condition is charged once here, though Rad, Zug-II and Wren each priced
it). Treatments: a line **edited in place = 1 delete + 1 add**; a block
**moved verbatim = 0 + 0** — only its changed scaffolding is charged
(the hoisted `AndroidView`s and the relocated grace/emergency chips are
moves, not new writing); a pure insertion = adds only. Trunk anchors
verified today: wheel branch `:273`, sled branch `:486`,
`effectiveNight` definition `:119`, its 7 read sites `:216, :224,
:250–252, :281, :494` — all exactly where D/E and Zug-II cited them.

## Region table — TactileDeckScreen.kt only

| # | Region (trunk lines) | Adds | Dels | Waage |
|---|---|---|---|---|
| A | Imports (Animatable, LaunchedEffect, graphicsLayer, blur, pointerInput) | 5 | 0 | 5 |
| B | `SealShotState` holder + `ShotPhase` enum + `WheelGhost`/`SledGhost` snapshots + transform samplers (lerps live HERE, once) + `preExisting` | 50 | 0 | 50 |
| C | Preamble: shot instantiation/`remember(sessionId)`, 3-case launch effect (derive-don't-replay), hero/ground night vals, frame-now derivation; `:119` definition dies | 32 | 1 | 33 |
| D | Night consumers, eyebrow + hero: `:216, :224, :250, :251, :252` (hero trio → heroNight at t=0; eyebrows → groundNight) | 5 | 5 | 10 |
| E | Wheel ghost: `:273` condition → ghost condition, track gated to GONE, FLOW row re-wrapped idle-only, Box + consume-all shield overlay, graphicsLayer/blur modifier, `update{}` edits (`:281`→groundNight, `:283–285` snapshot/lock pushes, F-capture + `freezeAtSeal()` call, callback-severance wrappers) | 37 | 6 | 43 |
| F | Task line: graphicsLayer/blur modifier, reduced-motion read branch, verb handler removals (title, DONE, QUEUE, sub-steps) | 19 | 4 | 23 |
| G | Sled ghost ⚠: `:486` host condition, `update{}` edits (`:494`→groundNight, `sealGhost()` push/guard, `onSlideCommitted` severance, exitPhase push), chips re-home scaffolding | 11 | 4 | 15 |
| H | Scroll gate (`verticalScroll enabled`, `:176–179`) + utilities row gated to Settle (`:454`) | 3 | 2 | 5 |
| | **TOTAL — Deck file, production** | **162** | **22** | **184** |

**Total: 184 Waage lines** (adds + deletes-once). Classic net
(adds − deletes) = **140**, for anyone reconciling against net-priced
files. **Tests, separate and NOT in the total: ≈130 ± 30 lines** in the
test source set for the Deck-region assertions (Rad's T1–T5 wheel
subset ≈70; Zug-II's T2/T3/T5/T6/T9/T11 Deck assertions ≈60).

**Uncertainty band: 165–205.** The ⚠ slide-rewrite band is region G
(15) plus the `SledGhost` lines in B (≈4): if Marlowe's merged slide
host renames the pushes (`:494/:497/:500` are host-side of his
contract) or changes their semantics, G re-sites at 8–30 and the total
moves with it. Everything else sits in regions no lane is rewriting.

## Reconciliation — where I disagree, and why

- **vs D/E's E3 ≈350: −166, by scope, not by dispute.** The 350 is
  all-files *gross* for the Vertigo (Wren §5a), including the cuttable
  ring field (≈45, excluded here and everywhere in my count),
  `MonolithCurves.kt` ≈10, `BlockitCoreViews.kt` ≈65, VM ≈25, nav ≈10.
  None of those files is in my brief.
- **Like-for-like Deck share: −11 (−6%).** Wren's §5a implies ≈195
  gross for this file excl. ring (150 wiring + 20 deferral + 25 input
  lock). I land at 184 Waage / 162 adds. The gap is her per-site
  transform pricing; I put the sampling functions once in the holder
  (B) and charge each read site only its modifier lines.
- **vs Zug-II's Deck share (≈285 of his §6): −101.** He re-charges
  the moved blocks — wheel `AndroidView` 22, sled `AndroidView` 19,
  chips ≈28 — as new writing, and prices transform wiring at ≈110.
  Under deletes-once + verbatim-move = 0/0, ≈70 of that evaporates;
  sampler-once removes ≈30 more. His *mechanisms* I did not re-price;
  only the arithmetic differs.
- **vs Rad's Deck share (≈120 classic net): +20 on my classic net
  (140).** He priced the wheel only; my count also carries the
  deferral consumers (10), task-line transforms + verb deaths (23)
  and the scroll/utilities gates (5), offset by my cheaper wheel
  restructure (43 vs his 55+20+12+18+15 spread).

**One caveat the builder must not lose:** the two-step deferral is
NOT completable in this file alone. The scheme-level flip lives
upstream (`MainActivity.kt:232`, `Theme.kt:104–108` — Zug-II §3.3);
my 184 buys every Deck-local consumer, but if the upstream split
(≈20, other files, not counted here) doesn't land, the panel still
steps at t≈0 and region D's t=400 step becomes the lying second step.

## NEXT (Law 10)

(a) *Want:* re-weigh region G the day Marlowe's slide merge lands —
one pass, same method, delta filed against this page's band.
(b) *Could:* apply the same deletes-once union count to E4's Deck
share (held-frame composition), which three owners have also priced
overlappingly.

— Fenja, Improvers · the search field
