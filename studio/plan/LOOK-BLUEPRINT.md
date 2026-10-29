# LOOK-BLUEPRINT — the manifest

Coordinator: **Riss** (Blueprint crew, 16 seats). Lane:
`builds/look-blueprint/Focus-app`, built from TRUNK (batch-8
truth merge, DB v11, HB-2 tap-time fix) — kit lane, slim GUH,
amended gradle.properties (-Xmx2g / workers 2). Base census:
`BASE-MANIFEST.sha256` at the lane root.

**The sentence (Leitsatz, defended):** the app is a set of
technical drawings of his day — every screen a framed plate
with a title block, every figure dimensioned, one ink +
annotation red only where the drawing corrects, nothing
unlabelled. The bottom rail and the header band are dead in
this world; navigation is the plate index in the title block.
His day 03:30→03:30 is literally the Shield's dimension line.

## 1. Cured token table (as built — ui/theme/Color.kt, Bp* family)

Day: plate/ground #F4F6FC · sheet #E9EDF9 · recess #D9E0F2 ·
raised sheet #FFFFFF (the one neutral white) · ink #1D2B6B ·
commit fill #1D2B6B (solid indigo, nothing else) · selected
#3C4D9C · glyph #2A3768 · secondary #5B6AA6 · outline FULL
#33428C (hairline variant @0x75 — hairlines only, never a
frame) · block bar #131D47 · text #1A2450 (13.76:1) ·
annotation red #C8102E (corrections only).

Night (the cure, built): ground/plate #0C1533 · the ONE loose
sheet #1D2A58 on ground · recess #060C22 · inverted ink
#E9EEFF · commit fill #E9EEFF — TRAVEL ONLY (CURE-B: the cap
rests at the ground tier #0C1533, fills only while travelling)
· selected #26356E · glyph #E9EEFF · muted #8B9BD8 · outline
FULL #55649F · block bar #05091C · text #D9E1FF · secondary
#93A3D9 · annotation red #FF6B7A · desk-clock numeral
#7D85A3 (CURE-C — over the named #0C1533 ground: Salz 0.238
luminance, 4.92:1).

**CURE-A is structural:** no raised #17234C tier exists in the
family — the token was killed, not demoted; Monteur's Theme
maps the full Material3 surface family from these values so
no stock grey (and no stock orange) can leak through a scheme
slot. Salz's frame condition discharged in construction: the
frame draws the outline token at alpha 1.00 in both weights
(1.5dp outer / 0.75dp inner); the 0x75 variant never frames.

Architecture note (Monteur's Delta §1, accepted): the Bp*
family stands BESIDE the untouched stock family; Theme picks
by look. The picker law (STOCK default, unreal looks fall back
to stock) makes the stock family load-bearing in this lane.

## 2. Picker wiring

`data/LookStore.kt` (new): `MonolithLook` — 8 looks (STOCK,
OBSIDIAN, DAWN, NOIR, SIGNAL, TEMPLE, BLUEPRINT, MASCHINE);
only STOCK + BLUEPRINT are `isReal` in this lane. Store =
SharedPreferences `monolith_look` on the DayWordStore pattern,
commit() writes, unreal looks refused at `put()`, unread/
unreal stored values fall back to STOCK. **No Room, no DB
change (v11 stands).** MainActivity holds the look state,
feeds `MyApplicationTheme(look=…)` + `MonolithRootApp(look=…)`
(both defaulted STOCK so other compositions compile
untouched). PARK's LOOK plate lists all 8: worn row prints
WORN, real unworn prints WEAR, unreal rows print
`NOT BUILT HERE — STOCK APPLIES` and take no touch.

## 3. What was built, by partition

SHELL (Monteur): LookStore + DrawingRevisionStore (new);
Color/Theme/Type (Bp family, full scheme mapping, square
BlueprintShapes, contract type ramp — figures Anonymous Pro,
his words Atkinson, Sora once per plate as the title-block
wordmark); BlueprintPlate.kt (new — full-weight double frame,
plain-cross registration marks [the ring was CUT on
Leitsatz's audit], tick rule, TitleBlock, MarginNote,
DimensionFigure/Line, commit helpers); ParkDrawer LOOK plate;
MainActivity (band + rail deleted, plate shell, plate index
navigation at 180ms turns, ISSUED sampled once per plate
entry, B-2 cloud at the second-03:30-boundary rule, HB-2's
tap-time guard byte-identical behind the margin note's kept
`header_guard_line` tag).

DECK (Ausbau): the Deck as dimensioned elevation — dimension
figure in Anonymous Pro (press ghosts dead, extension lines,
filled arrowheads), E-1 section cut on the running line
(elapsed solid / remaining dashed / section circle at NOW),
B-3 broken dimension during a Safe Break (zigzag + `[N MIN]`),
% twin deleted, FLOW key solid ink only, NOW as item 01 with
`01.1` sub-steps, DONE as the outline key (48dp floor), the
tolerance block + E-3 verdict (never without both figures),
margin instruments (DESK CLOCK key, `HELD — n SHUT` + `m PEEK`
cells), K-9 NEXT line host-wired, commit plate per §5 (night
cap dark at rest / pale in travel, words never fade — F-13).

SHIELD + ROOMS (Einbau): Shield as parts list — BOM title row
(`n SHUT • m PEEK`), PEEK · BREAK tolerances plate (figured
from the Repository constants, note verbatim `THESE LENGTHS
ARE FIXED IN THIS BUILD`), routines as ONE 03:30→03:30
dimension line under the F-19 sliver rule (true widths,
staggered leaders ≥12dp, item-number-only below the floor,
disabled = phantom long-short chain, never hatch), B-2
revision cloud drawn, T-7 LAST ROUTINE CHECK (boundary-
relative staleness: age figured + margin rule steps to red,
staleness claimed only), editor as loose sheet with E-4
detail callout (silent on collisions — B-F2 priced, §5),
app rows as OPEN · PEEK · SHUT figured cells (the sled tap
cycle is DELETED — the sled is the only verb), cap as a
dimensioned selector (R2 ladder, rest at the raised tier —
R10). Tasks: line-item schedule, no duration anywhere, R8
torn edge (6dp pitch / 2dp depth, deterministic). Drawer:
cabinet DELETED whole, B-1 as-built strips, register prints
`MADE — <name>`, E-2 elevation set BUILT (7 day columns on
one 03:30 datum). Receipt: the loved slip kept; bracketed
`ENDED [HH:MM]` on the `endedDerived` signal with the legend
`[] — FIGURED FROM THE RECORD, NOT WITNESSED` on any slip
carrying a bracket. Desk clock on the named #0C1533 ground,
CURE-C numerals seated. Held Door plate (HOLD stamp landing
140ms Curve A + seat knock, facts in order). Pill = detached
margin strip (SHIELD / BREAK / PEEK / COOLDOWN forms, seconds
grain in the warning window, PEEK ✕ spends the firm click;
END BREAK slot parameterised, key unmounted — §5).

FIX ROUNDS (both builders re-seated): pitch law reconciled
to Politur's bench (wheel family 440 + min×2.8 everywhere a
minute is voiced — updateTaskDuration re-based from ×2.5;
routine steps pitched by resulting time-of-day 520 +
minOfDay×14/60; peek-end spends playFirmClick; the two flat
640 sites censused as a block-count voice and a sub-step
toggle — not minutes, they stand); AirlockInterceptState
gained its 6 fact fields, filled at the intercept path;
`endedDerived` plumbed (assembly → publish → rehydrate);
StoredMonth counts (DAO COUNT(*) + repository map + VM flow)
wired to the Drawer; HOST-1 next-routine facts wired to the
Deck; Shield tick + Drawer sessions wired; Tier B title
block re-priced (see §6).

COORDINATOR EDITS (Riss's hand, at integration): ReceiptSheet
bracket condition moved from the `restored` proxy to
`endedDerived`; the NEXT label's weekday ruled to the FILING
day and flipped in `nextRoutineLabelFor`; dead helper
`titleBlockRevisionText` deleted (zero call sites); harness
extended (§4).

## 4. Proof — renders + tests

<!-- EVIDENCE: filled after the queue compile + JDK21 run.
     Frames owed: 00 wheel-drag, 01–04 plates day, n01–n04
     night, 03b/n03b tasks-empty, 05/n05 PARK, 08/n08 receipts,
     09 shield-gap receipt, 10 faulted slip (fabricated, named),
     11/n11 sealed-running Deck, 12/n12 desk clock (running
     fabricated / idle), 13 Held Door, 14 five-routine Shield,
     15 pill states (fabricated, named), hb4 detention pair +
     jammed, tc drawer details + eleven steps.
     Census: zzBlueprintCensus — zero stock tones, zero
     #17234C, night ground present, CURE-C numerals present. -->

## 5. Files changed vs BASE-MANIFEST.sha256

Changed (22): MainActivity.kt · data/MonolithDao.kt ·
data/MonolithModels.kt · data/MonolithRepository.kt ·
data/ReceiptState.kt · ui/MonolithViewModel.kt ·
ui/components/AirlockAndDisarmOverlays.kt ·
ui/components/MonolithCustomComponents.kt ·
ui/components/ParkDrawer.kt · ui/components/ReceiptSheet.kt ·
ui/components/ShieldCapSelector.kt · ui/screens/DrawerScreen.kt ·
ui/screens/FocusSequencerScreen.kt ·
ui/screens/ShieldMatrixScreen.kt ·
ui/screens/TactileDeckScreen.kt · ui/theme/Color.kt ·
ui/theme/Theme.kt · ui/theme/Type.kt ·
ui/views/BlockitCoreViews.kt ·
ui/views/BlockitSuitcaseAndSwitchesViews.kt ·
ui/views/BlockitTowerFadersAndCatalogViews.kt ·
app/src/test/java/com/example/ScreenshotHarnessTest.kt.
New (3): data/DrawingRevisionStore.kt · data/LookStore.kt ·
ui/components/BlueprintPlate.kt.
Every change is claimed in a builder's closing letter or
named above as a coordinator edit. DB schema: UNCHANGED (v11).

## 6. Coordinator amendments to the contract (owned, priced)

- **Tier B re-price** (Zirkel §3.2 geometry failed on a real
  411dp plate: fixed cells 328dp left the identity ~61dp
  against ~120dp needed). Ruled: identity cell STACKS
  (`PLATE 0n` over the plate name, tracking +1.0), wordmark
  cell 96dp, revision cell 112dp, ISSUED 104dp stands.
  Contract amended by ruling, registry-noted.
- **Tick strip** (Zirkel §2 × §3.1 tension): the title block's
  foot stands 12dp clear of the inner frame; the tick rule
  runs in the strip; the block draws its own foot rule.
  Monteur's resolution, accepted; renders judge.
- **NEXT weekday = filing day** (not calendar date): the
  app's day is 03:30→03:30 on every other surface.
- **Pause-expiry boundaries print no NEXT**: §3.1 files no
  label form for them; the pair stays silent rather than
  misname (Monteur, named in his fix-round letter).
- **Stock-night ink tint**: Ausbau's scheme-derived
  inkControlFill keys its night branch on background
  luminance, so STOCK night's filled controls also take the
  indigo step #26356E instead of the warm stock tone. In-look
  behaviour is correct; the stock-night delta is the merge
  desk's to accept or gate (a CompositionLocal look flag is
  the ~10-line alternative).

## 7. Conditions — discharged / open

- Salz frame condition (full weight or N3 reopens):
  DISCHARGED in construction (alpha 1.00 both lines); render
  sample in §4 confirms the stroke hex on pixels.
- Salz sheet-step re-verify (night sheet #1D2A58 on ground,
  computed 1.30:1): OPEN at the merge desk — the value ships
  as Zirkel ruled it; Salz's re-verify is a desk check, not a
  lane build item.
- AOD / desk clock: CURE-C seated in-lane; the clock's
  standing remains subject to the Nacht/L05 court at merge.
- Ruth's taxes T-1…T-8: conversion checks RT-01…RT-08 were
  adopted as the render protocol — verdicts in §4.
- B-F1 (Una): the spec's claimed 88sp hero was false (trunk
  72sp); the built figure is Zirkel §6's 72sp DialClock tier —
  the drawing never claimed 88. Closed by the contract.
- Verfahren (Methods seat): REFUSED by the safety filter
  before writing; not retried, rephrased, or routed around,
  per the refusal's terms. Its paper deliverables (compile
  plan, waste audit) are open gaps the lane did not depend on.

## 8. Priced machinery proposals (Ren's desk — nothing faked in-lane)

1. **B-F2 collision derivation** (Una, re-priced): the
   editor's collision refusal does not exist in trunk. Cost:
   one intersect check over stored routine rows at edit time
   + the refusal print in the editor. Until built, the sheet
   stays SILENT on collisions (Scharf S-02) — no fake drawing.
2. **ST-02 END BREAK verb**: face built, slot parameterised,
   key unmounted. Needs the break-end verb in the VM/service.
3. **ST-04 +N MIN extension**: no stored extension fact
   exists; the Drawer/receipt annotation needs the fact
   first (small state addition), then the print.
4. **ST-05 service-layer completion voice** + **ST-08
   grant-launch**: service seats, priced in Kleinteil's
   filing (SMALLTHINGS-kleinteil.md).
5. **Pill overlay window**: the strip's face + in-app host
   are built; a real floating window over other apps
   (overlay permission + service driver) is Pastille's lane.
6. **FL-02 desk-clock settle-raise** (Verlauf, SMALL) +
   **FL-05 / FL-13** flow machinery: priced in
   FLOW-verlauf.md; the clock's auto-raise after a 20s settle
   is the headline item.
7. **F-20 keypad sheet** (full form): ~155 lines + ~15 VM
   lines (absolute `setRoutineTime`); the inline figured-
   field form SHIPPED in-lane — this buys the sheet only.
8. **Routine delete/rename/create verbs**: ~10–15 lines each
   through the persistRoutine funnel + Shield wiring; the
   editor prints none of these verbs meanwhile.
9. **K-5 settability seam**: the tolerances plate prints the
   fixed lengths and the verbatim fixed-in-this-build note;
   making PEEK/BREAK lengths settable is Truth Car 3's
   storage seam (Wagen's lane, joint migration 11→12) — the
   plate's figured fields are built disabled-but-labelled
   against that day.

## 9. Named fabrication in the proof rig (and nowhere else)

The harness fabricates, by name: the PARK snapshot (trunk
pattern), the faulted receipt state (trunk pattern), the
desk-clock session entity, the pill strip's state set, and a
fresh heartbeat tick in the guard-settings flow (without it
the freshness gate — correctly — keeps NEXT / E-3 / LAST
ROUTINE CHECK silent under the rig; the routines, boundaries,
labels and verdicts around the tick are production
derivations from real seeds). No production surface
fabricates anything: unmeasured prints nothing, an unmounted
verb shows no key, an unwitnessed figure wears brackets.

— Riss, Blueprint coordinator, Oct 29–30 2026.

## ADDENDUM — DECLARED CLOSE (Oct 30, ~01:55 IST)

This manifest is the lane's final record. Two orders closed
the programme before §4's evidence could exist:

1. **Build authority (Sarthak, Oct 30 01:15 IST):** only Ren
   compiles, only on his explicit word; lanes close DECLARED
   (code + tests written, uncompiled). Five compile attempts
   are logged in plan/crew-blueprint/INTEGRATION.md — every
   failure was infrastructure (queue saturation by pid-less
   relic flags, box RAM exhaustion, a daemon killed mid-write
   and a probable VFS/file-changes corruption in this lane's
   own GUH — suspect dirs named there for whoever builds
   next); **no attempt ever reported a code error**, and the
   last attempt was armed simultaneously with the order and
   is the lane's last.
2. **Stand down (Ren, Oct 30 ~01:33 IST):** Sarthak stopped
   the seven designs — the main app gets built properly
   first. The lane state stands on disk exactly as built;
   nothing merges now.

Therefore: §4's frames and census were never produced, and
the render-verdict seats (Scharf round 2, Tageslauf RT-01…
RT-08 conversions, Politur gallery, the blind soul test) and
Ren's colour-stripped structure proof (ordered 23:38) stand
UNEXECUTED — superseded by the stand-down, recorded here so
nothing reads as silently dropped. What the desk holds
instead: the complete built lane, the proof apparatus in the
tree (harness + zzBlueprintCensus), Haut's feel verdict
(KEPT — strongest feel build in the wave, pitch family
verified in code), Salz's census reproduced exactly, and
§§1–3 + 5–9 above as the full account of what was built,
what it cost, and what remains priced. The lane closes
DECLARED.
