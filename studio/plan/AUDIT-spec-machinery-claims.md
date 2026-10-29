# AUDIT — Machinery claims in the presentation specs

**Una (Critics — consistency), seated by Ren, 29 Oct 2026.**
Scope: every claim in plan/PRESENTATION-{obsidian,dawn,noir,temple,signal}.md about what
exists in the shipped app, what a change costs, what is "zero machinery," what a stored
field holds, what a service reads — read against trunk (~/workspace/clone-inspect/Focus-app)
and the audit record. Three verdicts only: **TRUE** (citable both sides), **FALSE** (charge:
the truth + the honest price where priceable), **UNVERIFIABLE-AS-STATED** (the claim names
no checkable fact). Design taste is not this desk's ground.

Salz's DAWN-2 is cross-referenced, not re-filed; its anchor was re-confirmed in passing
(BlockedAppEntity, data/MonolithModels.kt:226; `blockTier` :231; `resolvedTier` parse :239;
provenance stored nowhere in the entity).

**PRESENTATION-blueprint.md has NOT landed** (checked at close). It is named outstanding;
its machinery claims join this desk when it files.

## The charges (FALSE) — summary

| # | Spec | Claim | Trunk truth |
|---|------|-------|-------------|
| O-F1 | Obsidian §1.10, §6-1 | Invisible slide words are "an inherited accident" — a hardcoded near-black constant (#17110C) from the old skin | The constant is `#FBF1EC` cream in trunk; #17110C exists only in Obsidian's own lane, where his phase-1 token pass redefined it. Self-introduced, not inherited |
| O-F2 | Obsidian §1.3 | The airlock "re-asks the pass per entry and counts every one"; the pass is "bought" | Nothing counts peek entries anywhere in the tree; no price or debit exists. The lease is a free 2-minute monotonic window |
| O-F3 | Obsidian §2.7 | Sled mid-stop paints scheme.secondaryContainer, "day #4A4038" | Day token is #615B5A; night is #4A423C. #4A4038 exists nowhere in trunk |
| D-F1 | Dawn §1.3(i) | "The only exits are the finish and the 60-second valve" | A third exit exists: the GRACE WINDOW ABORT (free, unbooked) for a sealed session's opening window |
| S-F1 | Signal §6 | The desk clock prints at 104sp | The shipped clock — and Signal's own lane — print 96.sp. 104sp is the type ramp's hero step, not the clock |

No FALSE verdicts against Noir or Temple.

## Cross-spec facts settled by this pass

- **The slide constants reconcile across all three specs that cite them.** SlideGestureMath
  (ui/views/BlockitCoreViews.kt:1309): GATE 0.68 (:1312), GATE_RELEASE 0.66 (:1315),
  CATCH 0.84 displayed (:1318), OFF_STOP 0.65 (:1321); the catch sits at finger p = 0.92
  (:1331); mesh target displayed 0.92 (:2130); tap-mesh is a 140ms bezier after a ≥90ms
  press (:2075; comment ~:1426). Three different events were being cited as one: CATCH 0.84
  is the commit coordinate; the latch knock (14ms @ 0.85, 320Hz) fires at displayed ≥ 0.97
  (stepSeat, :2319–2320); the latch itself frames at displayed ≥ 1. Obsidian, Dawn and Noir
  are each TRUE on the numbers they cite.
- **The trunk container leak is TRUE, three lanes independently.** Trunk ui/theme/Theme.kt
  maps no member of the Material3 surfaceBright / surfaceDim / surfaceContainer* family
  (grep: zero mappings); the Deck hero paints `scheme.surfaceBright`
  (ui/screens/TactileDeckScreen.kt:521). Noir §7.1, Temple §12 and Signal §10 all state
  this correctly. The winner-merge family mapping stands on verified ground.
- **A refinement for the RC-03 record (no spec verdict attached).** Obsidian §6-5 restates
  Hale's RC-03 premise (re-shown receipts re-derive WITHIN live). The pending receipt's
  assembly for routine rows is in fact frozen: data/MonolithRepository.kt:1443–1453 takes
  the name from the session row's own `profileName` and the window from the row's frozen
  stamps. The live read bites on the other paths — the hand-armed containment lookup
  (:1537, `routineWindowLabelAt`, reads live routine rows) and the re-show paths Hale
  named. Voss's rider-2 freeze should be aimed there; the routine-row pending assembly
  does not need it.

---

## OBSIDIAN — plan/PRESENTATION-obsidian.md

### FALSE

**O-F1 — §1.10 + §6-1: the slide words' root cause.**
Spec: the phase-1 slide words were "painted in a hardcoded near-black constant (#17110C) on
the black track — black on black," framed at §6-1 as "an accident of the old skin's palette,
never tokenised" — an inherited defect his cure repairs.
Trunk: the words paint in `LIGHT_ON_PRIMARY` (ui/views/BlockitCoreViews.kt:1796, 10sp,
letterSpacing 0.2), and that constant is `0xFFFBF1EC` — porcelain cream — at
BlockitCoreViews.kt:70. In the shipped app the words are light on black and legible (Noir's
D14, below, states this correctly). `#17110C` occurs nowhere in trunk. It occurs in
Obsidian's own lane at builds/obsidian/…/BlockitCoreViews.kt:71, where the phase-1 token
pass redefined the shared constant to his ink colour — with the lane's own comment at :1798
recording the result. The illegibility Salz verified in the phase-1 renders (OBS-1) was
real *in that lane*; its provenance is the lane's own redefinition, not the old skin. The
cure (words take `tokTextSecondary()`) is real and Salz-verified in-lane; what is FALSE is
the inheritance story §6 sells it under. Honest price of the cure: unchanged — it is done.

**O-F2 — §1.3: the airlock's pass is counted and priced per entry.**
Spec: "the sled's 2m buy is priced per app… the airlock re-asks the pass per entry and
counts every one."
Trunk: the peek is a free lease — a 2-minute monotonic window read by the accessibility
service (service/MonolithAccessibilityService.kt:207). No counter of peek entries exists
anywhere: the session row's full field census (data/MonolithModels.kt:262–340) holds no
peek count, and the tree's only `peekCount` is the Shield's count of apps *at the PEEK
tier* (ui/screens/ShieldMatrixScreen.kt:106) — a different fact. Nothing is debited; there
is no "buy." Within the lease, re-entry is free and uncounted; after expiry the offer
re-appears — that is lease expiry, not a per-entry price. His redesign (rent collected per
entry, releases counted) is therefore a machinery change, not presentation: an honest
price is a counter on the intercept path plus its write site — small, but not zero — and
it inherits UNA2-01's recorded fact that three of the four doors cannot honour a lease at
all (plan/AUDIT-deck-intercept-claims.md).

**O-F3 — §2.7: the sled mid-stop's day colour.**
Spec: "the mid-stop cap paints scheme.secondaryContainer (day #4A4038)."
Trunk: the day secondary-container token is #615B5A (ui/theme/Color.kt:20); the night token
is #4A423C (:41). #4A4038 exists nowhere in trunk. Minor, numeric — but this spec's
authority elsewhere is its numbers, and this one is no token in the building. (If his lane
carries #4A4038 as a chosen value, the spec does not say so; it cites it as the shipped
scheme's.)

### TRUE (selection — every machinery claim in the spec was checked; these are the load-bearing ones)

- **§1.5 / §6-9, wheel strait value 5; minutes 10–180 in 5-minute steps** —
  BlockitCoreViews.kt minutesValue: angle < 117 → 5; minutes = (10 + ((angle−126)/9).round
  × 5) coerced 10–180.
- **§1.7, the Tasks sub-step print `0${idx+1}`** — ui/screens/FocusSequencerScreen.kt:570.
  Note for Truth Car 2: the same literal prints at **three** sites — Tasks :570, Deck
  (ui/screens/TactileDeckScreen.kt:805) and Drawer (ui/screens/DrawerScreen.kt:277).
  DRW-08's cure is the same edit three times, not one fix.
- **§1.8, HELD counts frozen at seal** — FocusSessionEntity `heldAppsAtSeal` /
  `heldSitesAtSeal` (MonolithModels.kt, Slice E fields), written once inside
  startSession's transaction; −1 = uncaptured, readers omit.
- **§1.9, the proof strip freezes at the latch frame's facts** —
  TactileDeckScreen.kt:431 reads `sealFreeze.warrantState` once sealed; :923 captures the
  DOWN frame's state; the proving controller reads its warrant synchronously at the latch
  frame (ui/ArmProvingController.kt).
- **§1.11, the grace window + the 60-second valve as the arm's two soft exits** — grace:
  chip at TactileDeckScreen.kt:948; outcome GRACE_CANCEL (data/ReceiptState.kt:38); the
  grace-cancel path books nothing (data/MonolithRepository.kt:2028–2054;
  ui/MonolithViewModel.kt:2044). Valve: the banked 60s hold (batch-1 record).
- **§2.2, Shield header counts re-derive per change, never stored** —
  ShieldMatrixScreen.kt:106 (peekCount derived from the apps state), :150 (header suffix).
- **§2.3, cap slot 84dp; the selector's throw on DURATION_260** —
  ui/components/ShieldCapSelector.kt:80 `.height(84.dp)`; ui/theme/MonolithCurves.kt:26.
- **§2.4, the schedule-reset note prints until dismissed** — `routineReseedNoteDismissed`
  (data/MonolithDao.kt:242; gate at ui/MonolithViewModel.kt:425).
- **§2.7, sled row 54dp against the tree's 40dp rows** — ShieldMatrixScreen.kt:302
  `.height(54.dp)` on the sled row; :222 `.height(40.dp)`.
- **§4.1, the duration bank is listOf(15, 25, 45, 90) + ADD** —
  FocusSequencerScreen.kt:211.
- **§4.4, desk clock 96sp; drift is a 5×5 lattice** — ui/components/
  AirlockAndDisarmOverlays.kt:164 (96.sp); :137–138 (dx = ((step % 5) − 2) × 3,
  dy = (((step / 5) % 5) − 2) × 3); true-black ground.
- **§5.4, detention ≤ 1.8s** — ui/MonolithViewModel.kt:981, :1021 (E1800).
- **§10 / §5.2, slide + knock constants** — see the cross-spec settlement above.
- **A1, the WITHIN derivation exists in the Repository's receipt assembly** —
  MonolithRepository.kt:1443–1453. TRUE, with the mechanism corrected: for routine rows
  the name comes from the session row's frozen `profileName` and the window from its
  frozen stamps — the derivation is *more* frozen than the spec claims, and the addition's
  cost stands as priced.
- **§8, the GAP register** — Salz recomputed all six on his tokens, 6/6 exact
  (plan/REFINE-salz.md). Cross-referenced, not re-filed. Salz's OBS-2 correction to this
  spec's AOD figures (0.81 actual; the 55% cure composites ≈ 0.21, not ≈ 0.19) stands as
  his; the underlying trunk fact is verified at T-T4 below.

### UNVERIFIABLE-AS-STATED

- **§1.5's ghost number.** The strait constant 5 is real (above), but no print site for it
  was found — in FLOW the Deck counts elapsed; the "5" surfaces nowhere as a printed ghost.
  Whether it haunts the returning dial is a design premise, not a machinery fact this desk
  can confirm.

---

## DAWN — plan/PRESENTATION-dawn.md

### FALSE

**D-F1 — §1.3(i): "the only exits are the finish and the 60-second valve."**
Trunk carries a third exit: the GRACE WINDOW ABORT — a Deck chip on a freshly sealed
session (TactileDeckScreen.kt:948–959), free and unbooked by the ledger
(ui/MonolithViewModel.kt:2044: "grace cancel is free and unrecorded"), with its own
outcome (data/ReceiptState.kt:38) and its own Repository path
(data/MonolithRepository.kt:2028–2054). Minor severity — a completeness sentence in a
rhetoric-of-steel paragraph — but it asserts a machinery fact, and the fact is wrong.
Obsidian §1.11 and Noir D15 both list grace correctly.

### TRUE

- **§1.2, one (θ,ω) integrator; ζ = 0.38; 520ms settle at every count; inertia scales
  with load** — BlockitCoreViews.kt: loadInertia() = 1 + 0.14 × (blocks − 1) (→ 1.98 at
  8 blocks); spring constants ~:1223–1229.
- **§2, the hero span formula prices breaks at (n−1) × 10** —
  TactileDeckScreen.kt:321: `dialMinutes * studyBlocksCount + (studyBlocksCount - 1) * 10`.
- **§2 / A2, the routine tick stamp already exists** — `lastRoutineTickMillis`
  (data/MonolithModels.kt:685), stamped at data/MonolithDao.kt:263, migrated at
  data/MonolithDatabase.kt:75. Printing it on the Shield is presentation-only, exactly as
  priced (and identical to Voss's F5).
- **§2 + §12, the shipped toggle facts** — SHORTS + REELS state is memory-only
  (ui/MonolithViewModel.kt:1287–1289); the sound toggle persists nowhere (:1254–1257);
  the GuardSettingsEntity holds no shorts field (MonolithModels.kt:670+). Her statements
  of the shipped facts are accurate; the charges themselves are this desk's UNA-01 /
  UNA-03 (plan/AUDIT-park-claims.md), not re-filed.
- **§4.3, the eight factory domains fallback** — service/MonolithAccessibilityService.kt:
  182–185. (Charge UNA-06, ibid.)
- **§5, latch knock at displayed 0.97, 14ms @ 0.85** — BlockitCoreViews.kt:2319–2320.
- **§1.4 / §5, seal shot 1050ms; cascade cadence 400ms** — MonolithCurves.kt:32
  (DURATION_1050); service/SessionSealTransition.kt:263 (jam at latch + 500 = 400
  per-link + 100 slack).
- **§5, valve bank pitches 560 → 470 → 390** — ui/views/
  BlockitSuitcaseAndSwitchesViews.kt:369 (comment), :442 (`2 -> 470f`).
- **§8, desk clock 96sp; drift extent ±6dp** — AirlockAndDisarmOverlays.kt:164; the
  lattice at :137–138 reaches ±6dp at its extremes. (Obsidian's "5×5 lattice" and Dawn's
  "±6dp" are the same mechanism described by structure and by extent; both TRUE.)
- **§11, Defrost's basis: the Now Frame** — `nowFrame` is a real Deck state
  (TactileDeckScreen.kt:349), from which the end-time derivation reads (:368, :473).
  TRUE with a locality note: it is a Deck-local state, not an app-wide publisher; the
  cost claim survives because Defrost lives on the Deck.
- **§12, the DAWN-4 answer** — `finishPulses(kind, row)` exists at data/ReceiptState.kt:
  105, and the E-clock runs as she stated (ui/MonolithViewModel.kt:1070–1073: E0 settle
  knock; E700 pulse gated by finishPulses; E880 knock pair; the E700 slot empty when the
  finish is death-booked). Salz asked; she answered; the answer is TRUE.
- **A4's cost basis, the view-model already observes break state** —
  ui/MonolithViewModel.kt:685 publishes `safeBreak` from the session flow; the session
  row carries `isInSafeBreak` / `safeBreakSecondsRemaining` (MonolithModels.kt).
- **§2, the badge derives from the four counted permissions** — derived state in the
  view-model, no stored count; consistent with Band's header audit and this desk's PARK
  audit.

### Cross-reference

- **DAWN-2 (Salz)** — not re-filed. Dawn's §11 withdrawal and reprice (append-only origin
  column + migration + two write sites) is consistent with the anchor re-confirmed above.

---

## NOIR — plan/PRESENTATION-noir.md

No FALSE verdicts. This spec's machinery claims are the most carefully cited in the wave;
two of its line cites are off by one in its own favour (substance exact, noted where they
occur).

### TRUE

- **D18 / D10, the nine fossil parameters occur exactly once each, signature-only** —
  intentionText, checklist, totalFocusedMinutes, totalInterceptions, completedTasksCount,
  totalQueueMinutes, shortsReelsSniperEnabled, browserUrlBlockerEnabled,
  selectedProfileName: one occurrence each in ui/screens/TactileDeckScreen.kt;
  selectedProfileName at :106, as claimed.
- **S9 / S11, the Shield signature fossils** — ShieldMatrixScreen.kt: shortsReelsSniper
  Enabled :71, browserUrlBlockerEnabled :72, profiles :73, selectedProfileName :74,
  onSelectProfile :83; each occurs exactly once (rendered nowhere). He cited ":72–73" for
  the first pair; actual :71–72.
- **S6, day letters listOf("M","T","W","T","F","S","S")** — ShieldMatrixScreen.kt:531.
- **A7, the loadout verb exists end-to-end; set-by-value is a one-line wiring** — only
  `onCycleRoutineLoadout` reaches the screen (ShieldMatrixScreen.kt:89, used :198);
  ui/MonolithViewModel.kt:1319 cycles and persists via data/MonolithRepository.kt:764.
- **A4 / D11, the session row carries routineId** — MonolithModels.kt (routineId +
  windowStartMillis / windowEndMillis, frozen at materialisation). TRUE — and cheaper
  than he priced: the name also sits frozen on the row (`profileName`), so the "one
  lookup" he prices is avoidable.
- **§5, slide / knock / brush facts** — the cross-spec settlement above; seat knock
  16ms @ 0.85 at 1 block → 24ms @ 1.0 at 8, pitch 320 − 90w (fireSeatKnockForCount,
  BlockitCoreViews.kt).
- **§6, the Tasks and Drawer prints** — `"#${%02d} · ${estimatedMinutes}m"`
  (FocusSequencerScreen.kt:531); `START ${task.estimatedMinutes}m` (:617);
  "TYPE TO FIND A TASK" (ui/screens/DrawerScreen.kt:531); "NOTHING STORED UNDER …"
  (:562); "NOTHING STORED YET" (:697).
- **§7.1, trunk Theme.kt maps none of the container family; the hero paints
  scheme.surfaceBright** — Theme.kt (zero mappings); TactileDeckScreen.kt:521.
- **D14, the shipped slide words are legible in both schemes** — they paint
  LIGHT_ON_PRIMARY #FBF1EC (BlockitCoreViews.kt:70, :1796). TRUE; the mirror image of
  O-F1. His withdrawal of his own phase-1 illegibility claim was the honest move, and the
  trunk record backs the withdrawal.
- **§11.3, the finish time is already derived** — TactileDeckScreen.kt:368
  (endsAtText from nowFrame + total span).
- **§12, the accent census and its hexes** — app/src/test/java/com/example/
  ScreenshotHarnessTest.kt:632–641 (day #F55314, night #F47442, deep #D63E06; #FF8A5C
  banned). He cited ":631"; the comment spans :632–633. Substance exact.
- **§9, the GAP table** — Salz recomputed: five exact, and his N3 self-charge confirmed
  (sheet #221239 sits above hero #150A26). Cross-referenced, not re-filed.

### Noted, not verdicted

- **§14(a) vs §7.2 / §8.** The answers file "the wet plate is IN CODE" while the body
  builds bloom in the plate's place. Both cannot describe the lane's hero rendering at
  once. Klinge flagged the same tension. Settling which state the lane is in requires
  pinning the lane's hero source, which this pass's trunk evidence does not do; referred
  to Klinge's supplement, whose blade already holds it.

---

## TEMPLE — plan/PRESENTATION-temple.md

No FALSE verdicts.

### TRUE

- **§7, HELD counts are captured on the session row at seal** — `heldAppsAtSeal` /
  `heldSitesAtSeal` (MonolithModels.kt); see O-T §1.8.
- **§9, the stone room is shipped code** — "Room comb feedback, both combs: −46 dB at
  ≈900 ms" (service/TactileSoundEngine.kt:92); two feedback combs of 1310 / 1372 samples
  (:175–176, arrays at :204–205); the 160ms transient buffer cap (:375); the seal's
  golden hash `d204b95a…` (:124). His claim that a temple bell is impossible inside the
  160ms cap, and that the two-comb room is the native acoustics, both check out.
- **§9, the press answer is 90ms** — MonolithCurves.kt:101 (PRESS_MS = 90).
- **§10, the AOD overlay prints NightColorPrimary at full alpha, no composite** —
  AirlockAndDisarmOverlays.kt:166 (numerals, no alpha); the 16dp progress strip fills in
  the same colour at :184. Same trunk fact as Salz's OBS-2; his 0.201 cure figure is his
  lane's arithmetic, on Salz's desk.
- **§11, routine rows carry start/end** — RoutineEntity (MonolithModels.kt:491:
  startMinutes / endMinutes / daysMask / loadoutName).
- **§12, the container-family trunk claim** — as N §7.1. TRUE.
- **§13, the confessed-broken day has no representation in shipped code** — TRUE. No
  such state or face exists in trunk; the frozen digest is a different and honest object
  (Hale's Drawer review acquits its three sentences by construction).

### Cross-reference

- **§10, the GAP table (N1 via brass hairline; N4 filed as a fence)** — Salz's standing
  desk extends to this spec; his recompute had not filed at this pass's read. Not
  re-filed here.

---

## SIGNAL — plan/PRESENTATION-signal.md

### FALSE

**S-F1 — §6: the desk clock's numeral size.**
Spec describes the shipped clock "at poster scale (104sp)" and builds its §6 on that
figure. Trunk: the clock prints 96.sp (AirlockAndDisarmOverlays.kt:164). Signal's own
lane prints the same 96.sp at the same line — the restyle did not raise it either. 104sp
exists in the building as the type ramp's hero step (ui/theme/Type.kt:77); it is not the
clock's size in any code this desk can find. Minor — but §6's runtime-scale arithmetic
(0.75–0.9) is computed from the wrong base.

### TRUE

- **§10, the slab's root cause** — the night hero painted `scheme.surfaceBright`
  (TactileDeckScreen.kt:521) while trunk Theme.kt maps no member of the surface family;
  the stock grey that resulted is exactly the mechanism he names. His cure's direction
  (map the family; the night bright step as a stated token) is aimed at a verified fact.
- **§9, the latch facts under "Registration Is the Seal"** — the proving controller's
  latch-frame read and the knock coordinates (cross-spec settlement above).
- **§3, the census method** — the accent census he re-ran exists as claimed
  (ScreenshotHarnessTest.kt, zzAccentCensus; hexes at :632–641). His lane's pixel counts
  are his renders' evidence — Klinge/Salz ground, not this desk's.

### UNVERIFIABLE-AS-STATED

- **§7, "the frame's state was driven through the ViewModel's own intercept trigger."**
  A claim about his lane harness's method. It names no trunk-checkable fact, and the
  harness run itself is not in the record given to this desk. Not doubted; not verifiable
  as stated.

---

## Tally

Claims checked: 60. TRUE 53 · FALSE 5 · UNVERIFIABLE-AS-STATED 2 · one internal tension
noted without verdict (Noir §14). Cross-referenced and not re-filed: Salz DAWN-2, OBS-2,
Noir GAP N3; Hale RC-03 (refined, above); this desk's UNA-01/03/06, UNA2-01.

The wave's machinery discipline is good: five false claims in sixty, four of them minor,
and the one that matters (O-F1) is a provenance story, not a mechanism — the cure it sells
is real, needed in that lane, and already verified there by Salz. The specs can be judged
on their designs; their facts, with the five charges above corrected, hold.

— Una, Critics (consistency). Reading audit; no compilation. Trunk as of 29 Oct 2026, 21:25 IST.

---

## ADDENDUM — BLUEPRINT (filed 21:39 IST, same desk)

plan/PRESENTATION-blueprint.md landed (21:33 IST, 794 lines) and has now had the same
read. The header's "named outstanding" line above is discharged by this section; the
original tally is superseded by the revised tally at this addendum's foot. Blueprint
designs for cured facts under its own scope law (cures declared, not smuggled); claims
about the cures are verdicted as scoped, claims about trunk as trunk.

### The charges (FALSE) — Blueprint

| # | Spec | Claim | Trunk truth |
|---|------|-------|-------------|
| B-F1 | Blueprint §1.3 | The trunk hero states the span "at 88sp Sora" | The Deck hero's digits print at 72sp (DialClockTextStyle, ui/theme/Type.kt:63–67), units at 50sp. 88sp is Obsidian's lane hero — Blueprint itself says so at §0 law 3 |
| B-F2 | Blueprint §2.5 | Editor overlaps draw collisions as "a presentation of the derive's real refusal, not a new rule" | RoutineDerive's overlaps judge sessions against routine windows; no routine-vs-routine overlap refusal exists at edit time. The spec's own A2 files the honest hedge |

**B-F1 — §1.3: the hero's size.**
Spec: "What (trunk): the true span (blocks × length + breaks, priced in) at 88sp Sora."
Trunk: the span's digits print in DialClockTextStyle — Sora Bold at **72.sp**
(ui/theme/Type.kt:63–67) — with units at 50.sp (ui/screens/TactileDeckScreen.kt:583–592;
the code's own comment at :575–577 reads "digits at H2 72, units at H3 50"). 88sp occurs
in no Deck print; it is Obsidian's phase-1 lane hero, as Blueprint's own §0 law 3 states
("Obsidian (§1.3 there keeps the 88sp Sora hero)"). A lane number cited as the trunk's —
the same species as S-F1. Minor: his redesign re-figures the hero in Anonymous Pro
regardless, so nothing he designs stands on the wrong figure; the census sentence does.

**B-F2 — §2.5: the editor's collision drawing.**
Spec: overlaps in the routine editor "draw their collision in red at the touching
boundaries (a presentation of the derive's real refusal, not a new rule)."
Trunk: `RoutineDerive.overlapsAny` (data/RoutineDerive.kt:159, :215, :239) judges
*sessions* against routine windows for materialisation eligibility. No edit-time
routine-vs-routine overlap refusal exists anywhere (grep over the view-model and the
Shield: none), and no sibling-overlap fact is exposed to the UI. As a machinery sentence,
§2.5 asserts a refusal the derive does not make. The spec corrects itself at A2 — "if
derive exposes no overlap fact to the UI, that part is a GAP, named, not faked" — and
the hedge is the truth: the honest price is a small derivation over the stored rows (one
intersect check, exposed at edit time), not the ~20 presentation lines §2.5 implies.
Charge the sentence; credit the hedge. Salz's DAWN-2 fence is respected in form: the
gap is named in the same filing, which is what DAWN-2's spec failed to do.

### TRUE (selection — every machinery claim checked; these are the load-bearing ones)

- **§1.2, the eyebrow trio** — TOTAL / SESSION RUNNING / BREAK · N MIN as shipped
  (this desk's Deck-intercept audit, A-11).
- **§1.3, the span formula** — `dialMinutes * studyBlocksCount + (studyBlocksCount - 1)
  * 10` (TactileDeckScreen.kt:321; same line as Dawn §2).
- **§1.4, the wheel's census** — 288dp (TactileDeckScreen.kt:646); minutes 10–180 in
  5-minute steps (O §1.5 settlement); radial teeth at the filed 3-degree pitch
  (ui/views/BlockitCoreViews.kt ~:668 — 3° pitch is the 120 teeth); eight wells in two
  tetrads with the count-4 closure (BlockitCoreViews.kt:916–918).
- **§1.5, FLOW silently sets the dial to 5, and the 5 never prints** — the strait value
  is verified (O §1.5); no print site for it exists (this desk's earlier ghost-number
  note is the same fact from the other side).
- **§1.6, the % twin exists to be killed** — `"${(progressFraction * 100).toInt()}%"`
  (TactileDeckScreen.kt:712).
- **§1.8, the armed utilities exist while a session runs** — desk clock + held
  notifications section (TactileDeckScreen.kt:838–848; `wordLabel = "DESK CLOCK"` :848).
- **§1.10, the slide's words** — `WORDS_START = "SLIDE OR TAP TO START"`
  (BlockitCoreViews.kt:2407).
- **§2.1, the ratchet banner** — one-way ratchet warning on weakening the loadout
  mid-session (ShieldMatrixScreen.kt:76; renders :119–130).
- **§2.2, the Shield header's honest split** — `n SHUT • m PEEK` prints on Shield
  (UNA2-02 record); the compressed noun Blueprint refuses is the Deck tenant's, as he
  implies.
- **§2.4, the routine rows' stored facts** — RoutineEntity carries name, startMinutes,
  endMinutes, daysMask, loadoutName, enabled (data/MonolithModels.kt:526–533): every
  fact the day-scale draws is stored, as claimed.
- **§2.4, the reset note, verbatim** — "SCHEDULE RESET TO YOUR DAY — TAP ANY ROW TO
  CHANGE IT" (ShieldMatrixScreen.kt:182).
- **§2.4 / A3, `LAST ROUTINE CHECK`** — `lastRoutineTickMillis` stamped at
  data/MonolithDao.kt:263 (Dawn §2, re-confirmed); the price he adopts is Voss's F5,
  correctly attributed.
- **§2.5, the day letters he kills** — listOf("M","T","W","T","F","S","S") ships at
  ShieldMatrixScreen.kt:531 (Noir S6).
- **§2.8, the browser-blocker toggle renders nowhere** — Noir S9/S11: signature-only
  fossil, as §6.11 files it.
- **§2.9, Shorts & Reels is memory-only, and his price is honest** — shipped fact per
  Dawn §2 / this desk's UNA-01; unlike the DAWN-2 pattern, the stored flag he needs is
  named as machinery in the same breath, not assumed.
- **§4.1, the Tasks census** — composer minutes bank and the row's `START
  ${estimatedMinutes}m` (Noir §6); the undo banner exists (FocusSequencerScreen.kt:263);
  the Last-Finished REOPEN strip composes from completed rows (ibid :319–333). Minor
  note: the trunk strip also prints the task's title — his "FINISHED HH:MM and nothing
  else" is his design's trim of that strip, not its current census.
- **§4.2, the Drawer facts** — day words are the closed list REST / ILL / FAMILY
  (data/DayWordStore.kt:54); ALL MONTHS is a shipped plate (DrawerScreen.kt:621, :708);
  the forge fact under his register annotation is DRW-01's record (Truth Car 2). The
  DRW cures he designs for are declared as cures in his scope law, not asserted as
  shipped.
- **§4.3, the receipt's footer is house law as claimed** — "THE MACHINE KEEPS ITS OWN
  LOG · YOUR STUDY RECORD LIVES IN YPT" (ui/components/ReceiptSheet.kt:176). The cured
  lines (RC-01…RC-04) are designed-for under his scope law, each attributed to its car.
- **§4.4, burn-in drift ±6dp as shipped** — Dawn §8 settlement (the lattice's extent).
- **§4.5 / §4.6, the Held Door and Pill state facts exist** — remaining span (the Now
  Frame derivation), peek remaining and `peekCooldownRemainingSeconds` are real state
  (UNA2-06 / SIL-2 / SIL-3 record; plan/PILL-AND-TIMES.md). The pill's overlay price is
  deferred to its own spec in the open ("priced in its own spec, not hidden inside
  mine") — the honest handling, and Freya's spec in fact finds the permission already
  declared, so his caution cost him nothing.
- **§5, the pitch family exists as cited in substance** — `playRotaryDetent(480.0 +
  clamped * 2.5)` (ui/MonolithViewModel.kt:1825). His line cite (:1795) is 30 lines
  stale; the formula is exact.
- **§5, the tear and completion voices** — playTearPulse at 240f fundamental, "the
  counterfoil tear's own voice" (service/TactileSoundEngine.kt:783); playCompletionPulse
  is three ticks that soften as they land (ibid, its own comment and waveform).
- **§5, the wheel's ζ = 0.38 overshoots** — Dawn §1.2 settlement.
- **§2.4, the frieze provenance** — Temple's §1.15 is indeed "The frieze of the day
  (Blueprint's hairlines → …)" (plan/PRESENTATION-temple.md:458).

### The self-filed blemishes — verified as stated (not re-charged)

- **§6.1, the stale night comments.** Verbatim in his lane: Color.kt:35 "(R:23, G:20,
  B:18)" against hex 0xFF0C1533; :40 "Flips to cream"; :53 "the same orange hue" — all
  inside his cited :35–53. The confession is accurate, and its disposition (comments
  rewritten to the true RGB at merge) is his.
- **§8, the GAP register's arithmetic.** Every token in his table stands in the lane's
  Color.kt (:35–53, numbered 1–19 as he claims — "all 19 night tokens" matches the
  file's own census; zero-green holds on the sampled tokens). Spot recomputations from
  the hexes: text-1 on ground 13.80 against his 13.81 (rounding); N3 sheet-over-raised
  1.104 against his 1.10; the AOD raw figure 0.758 exact; the 55% composite lands on
  #7D85A3 exactly as he prints it and measures 0.238 exact. The night sheet step IS the
  shallowest of the filed worlds and the desk-clock numerals as tokenised DO sit at
  0.758 against Nacht's 0.25 ceiling — both blemishes are stated accurately. One rider:
  the lane comments' inline ratios (:37 "1.86:1", :39 "1.35:1", :47 "3.5:1") do not
  match §8's recomputed figures — they belong to the §6.1 stale-comment disease he
  confessed, under the same disposition; §8's hex-derived numbers are the ones that
  verify.
- **§6.2 / §6.12, the lane's own housekeeping claims** — the fossil alias block exists
  (lane Color.kt:103ff: PorcelainCanvas, SunlitPorcelain, …); the lane's Theme.kt does
  flatten the radius scale to 0.dp (lane Theme.kt:107–112).

### UNVERIFIABLE-AS-STATED

- **§1.10 / §3, the slide as "the only irreversible form in the app."** As a law of his
  grammar — no other control's act is a commit — it is his to declare. As a machinery
  absolute it does not verify: the seal the slide makes is reversible twice over (the
  grace abort and the 60-second valve, both of which his own §1.10 keeps at the same
  address; D-F1's record). Not charged as FALSE — unlike Dawn's sentence, his denies no
  exit's existence — but the absolute does not survive its own section, and the judging
  should read it as rhetoric, not mechanism.

### Not verdicted

- **§0's soul-test sentence** (the other five worlds "collapse to a header, a hero
  card, a list" with colour stripped) is a composition claim about five other specs.
  Design taste is not this desk's ground; the sentence is Fremd's court, and his blind
  ranking is where it will be tried.

### Revised tally

Blueprint claims checked: 27. TRUE 24 · FALSE 2 · UNVERIFIABLE-AS-STATED 1.
Wave total: **87 checked · TRUE 77 · FALSE 7 · UNVERIFIABLE-AS-STATED 3** (plus the one
internal tension noted without verdict, Noir §14).

Blueprint's discipline is the wave's most self-policing: a scope law that declares its
cures, a debts section filed before judging, both self-charges verified verbatim, and
§8 arithmetic that survives recomputation to the third decimal. Its two falses are the
wave's recurring species — a borrowed number (B-F1) and an over-claimed refusal (B-F2) —
and the second carries its own correction in the same filing. With B-F1 and B-F2
corrected, the spec's facts hold, and DAWN-2's lesson is visibly learned in its pages:
wherever Blueprint needs storage or a fact the trunk lacks, it names the machinery
instead of assuming it.

— Una, Critics (consistency), addendum filed 29 Oct 2026, 21:39 IST. Reading audit;
no compilation.
