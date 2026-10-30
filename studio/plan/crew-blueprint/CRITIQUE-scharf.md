# CRITIQUE — SCHARF, Critic (unrestricted), BLUEPRINT crew
## ROUND 1 — the pre-registered charge sheet

*Scharf — Critic of the Blueprint look crew, under Riss
(coordinator), who answers to Ren. Filed Oct 29, 2026, ~22:35 IST.
Scope: unrestricted — design, code, words, process, the
coordinator's own calls. Law: a charge files with proof or not
at all. No compiling was done; every figure below is either
cited to a file:line on disk or recomputed by me from named
hexes, with the method named beside it.*

*Inbox: mail/scharf/ — 0 letters at seating, 0 at filing. Stated
plainly, as ordered: nobody wrote to this desk; every charge
below is from the sources, not from correspondence.*

*Sources read whole or by named section: plan/PRESENTATION-
blueprint.md (whole); plan/AUDIT-spec-machinery-claims.md +
plan/AUDIT-deepen-machinery-claims.md (Una — Blueprint 24/27);
mail/blueprint/2139__from-una__machinery-verdicts.md;
mail/blueprint/2202__from-salz__gap-verdict.md (+ plan/REFINE-
salz-gap-blueprint.md); plan/REFINE-ruth.md Blueprint amendment
(21:44 IST); mail/blueprint/2141__from-ren__k5-supplement-owed.md;
mail/blueprint/2140__from-zunder__blueprint-inventions.md;
plan/SMALL-THINGS-SPECS-PASS.md (Klar, Blueprint addendum);
plan/crew-blueprint/SMALLTHINGS-kleinteil.md (the K-5
supplement, whole §1 + dispositions); plan/crew-blueprint/
DESIGN-zirkel.md (§3.2, §3.3, §6, OPEN-6); plan/crew-blueprint/
IMPROVE-feile.md (F-19, F-20, reconciliation); plan/crew-
blueprint/DAYFIT-tageslauf.md (day-scale hole); plan/crew-
blueprint/REGISTRY-registratur.md; mail/ren/2220__from-riss__
crew-seated-lane-open.md (the partitions); COMMONS.md head;
the Blueprint lane token file (builds/blueprint/…/theme/
Color.kt); trunk Type.kt / MonolithRepository.kt where cited.*

*Grammar held throughout: one indigo ink, annotation red only
where the drawing corrects, Anonymous Pro figures, never green.
No charge below asks this world to become another one.*

---

## 0. STANDING BLEMISHES — carried, not re-charged, with their Round-2 teeth

These are already proven by other desks. I do not re-file them
as mine; I register what Round 2 will do about them, because a
blemish nobody re-measures at the renders is a blemish forgiven
by scheduling.

- **B-F1 (Una, proven).** Spec §1.3 cites the trunk hero as
  "88sp Sora." Trunk: 72sp digits / 50sp units
  (Focus-app/…/ui/theme/Type.kt:63–67 — re-read by me tonight:
  `DialClockTextStyle … fontSize = 72.sp`; TactileDeckScreen.kt:
  583–592). 88sp is Obsidian's lane hero; Blueprint's own §0
  law 3 says so. Registry: OPEN, OL-01, Zirkel corrects the
  text. *Round-2 tooth:* Monteur's theme must not re-import
  88sp anywhere — grep the lane Type.kt at the merge for
  `88.sp` outside a comment explaining this charge; any hit
  reopens B-F1 as a build defect, not a typo.
- **B-F2 (Una, proven).** Spec §2.5 sells the editor's red
  collision drawing as "a presentation of the derive's real
  refusal." No routine-vs-routine edit-time refusal exists:
  `RoutineDerive.overlapsAny` (trunk data/RoutineDerive.kt:159,
  :215, :239 — re-grepped tonight) judges *sessions* against
  windows. Registry: OPEN, OL-02 (text) / OL-22 (the honest
  machinery — one intersect check over stored rows, exposed at
  edit time — Monteur, *unbuilt, unscheduled*). *Round-2 tooth:*
  §2, charge S-02 — the false sentence has already propagated.
- **N3 condition (Salz, proven).** Sheet-over-raised 1.10:1 is
  the shallowest valid sheet step of any filed world. The
  edge-not-lift defence holds ONLY while sheet frames are drawn
  in the full-weight outline token #55649F (2.69:1 on raised,
  Salz-supplied, re-verified by me in §2/S-10's arithmetic);
  the 0x75-alpha hairline variant composites to 1.55:1 and
  **reopens N3**. Registry: OL-03, Monteur builds / Politur
  verifies. *Round-2 tooth:* my own independent pixel sample of
  the sheet frame stroke, pre-registered at §7/R2-F5 — Politur's
  check does not discharge mine.
- **AOD defect (Salz, proven both halves).** Desk-clock numerals
  as tokenised (#D9E1FF, full alpha) measure 0.758 WCAG relative
  luminance against Nacht's 0.25 ceiling; the 55% composite cure
  is exactly #7D85A3 over the night ground and recomputes to
  0.238 — under by 0.012, the tightest landing of any filed
  cure. *Round-2 tooth:* §7/R2-F10, plus charge S-06 — the cure
  is ground-conditional in a way the spec never states.

---

## 1. THE VERIFICATION REGISTER — every claim that will become a printed word or a rendered pixel, with its method named NOW

Round 2 over the renders is an execution of this register, not
an opinion. Method key: **[grep]** file:line check in the lane
tree · **[hex]** token read in Color.kt/Theme.kt · **[pixel]**
sampled pixel at a named seat in a named frame (§7) · **[frame]**
harness frame inspected for presence/absence · **[recompute]**
luminance/contrast recomputed from sampled hexes by the WCAG
linearised formula (Salz's method — see S-05 for why the method
must be named) · **[run]** harness state change, value re-read.

### 1A. MONTEUR — shell / theme / PARK picker

| # | Claim (source) | Method | Pass condition |
|---|---|---|---|
| M-1 | 19 day + 19 night tokens, zero-green on all (spec §8; lane Color.kt) | [hex]+[recompute] | Every token G < max(R,B); census count = 19+19 in the look-lane file, not the phase-1 lane |
| M-2 | Night tokens' stale comments rewritten to true RGB at merge (spec §6.1 — lane Color.kt:35 "R:23, G:20, B:18" against hex 0xFF0C1533; :40 "Flips to cream"; :53 "the same orange hue"; inline ratios :37/:39/:47 that match nothing) | [grep] | No comment in the shipped Color.kt states an RGB, hue or ratio the hex beside it does not hold |
| M-3 | ~90 fossil aliases (Porcelain/Bauhaus/Basalt/Solar…) die at merge (spec §6.2; lane Color.kt:103ff) | [grep] | Alias census in the look lane = 0, or each survivor named with a reason in the merge note |
| M-4 | Radius scale flattened to 0 (spec §6.12; lane Theme.kt:107–112) | [grep]+[frame] | Theme shapes all 0.dp; no rendered corner radius on any plate furniture in R2 frames |
| M-5 | Double hairline frame + corner registration marks on EVERY plate (spec §0) | [frame] R2-F1/F2/F4/F11 | Frame present on Deck, Shield, Tasks, Drawer, day and night; registration mark never overstrikes a caption (Ruth residue 1 — §6) |
| M-6 | Title block: MONOLITH (Sora, its one seat) · PLATE nn / NAME · ISSUED cell · revision/seal cell · index row · PARK last cell (spec §1.13; Zirkel §3.2/§3.3: block 64dp, Tier B 32dp over Tier A 32dp) | [frame]+[pixel] R2-F1/F2/F3 | Sora appears in the wordmark cell and nowhere else on any plate; block geometry within 1dp of Zirkel's contract |
| M-7 | The floating rail and header band DIE (spec §1.1/§1.13) | [frame] | No rail, no band in any R2 frame; Guard survives only as the margin note (spec §1.1) |
| M-8 | PARK picker: 8 looks, only Blueprint real in-lane (Riss seating letter) | [frame] R2-F12 | Picker renders; unbuilt looks are labelled as such or absent — a picker cell that opens a trunk-skinned plate fails |
| M-9 | B-F2's honest machinery: edit-time intersect derivation exists or the editor draws NO collision (Registry OL-22) | [grep] | A derivation over stored routine rows is findable by name in the lane, OR the editor frame R2-F5 shows no red collision marks |
| M-10 | Night cap cure + night plate cure are TOKENS, not render luck (Ruth amendment; Riss: "pixel-verified debt, not a claim") | [hex] | Night Commit Plate cap token at rest is a dark indigo, not NightColorPrimaryContainer #E9EEFF (the phase-1 lane's night cap token, Color.kt night :8 — the token that produced Ruth's 0.934 sample) |

### 1B. AUSBAU — Deck

| # | Claim (source) | Method | Pass condition |
|---|---|---|---|
| A-1 | Hero is a dimension figure in Anonymous Pro: extension lines drop to the wheel, one dimension line with arrowheads spans the wheel's width (spec §1.3, A1 ~60 lines) | [frame]+[pixel] R2-F1 | Dimension line's pixel span = wheel's pixel diameter ±2px at the same frame's scale; figure's font is Anonymous Pro (no Sora glyphs in the figure) |
| A-2 | Span formula prices breaks in: dialMinutes × blocks + (blocks−1) × break (trunk TactileDeckScreen.kt:321, Una TRUE; break now the STAMPED breakSeconds — Kleinteil §1.3) | [run] | With break set 15 (R2-F6 seam), a 2-block 50-min arm figures 115, not 110 and not 120 |
| A-3 | Wheel 288dp, minutes 10–180 in 5-min steps, teeth at 3°/120, strait's dead sector empty of tick/tooth/numeral (spec §1.4; Una TRUE on trunk) | [frame]+[pixel] | Dial pixel diameter matches 288dp at frame density; FLOW sector visibly bare |
| A-4 | The % twin is DELETED (spec §1.6; trunk prints it at TactileDeckScreen.kt:712) | [frame]+[grep] | No `%` figure anywhere on the running Deck frame R2-F3; no `progressFraction * 100` print in the lane Deck |
| A-5 | FLOW active: wheel ticks sink to hairline; the dial-5 never prints (spec §1.5) | [frame] | FLOW frame: tick tier visibly hairline; no "5" figure appears in any seat |
| A-6 | NOW row: item 01, sub-items 01.1/01.2 — no `010` ever (spec §1.7; DRW-08) | [frame] R2-F11 | A queue with ≥10 items (harness data) prints no three-digit item number |
| A-7 | HELD prints as two cells — `HELD — n SHUT` and `m PEEK` — never the compressed total alone (spec §1.8 final form) | [frame] R2-F3 | Both cells present on the running frame; neither cell shows a bare summed `HELD n` |
| A-8 | Tolerance block (proof strip): BLOCK · APPS · TIME, TIME prints NO END when idle (spec §1.9) | [frame] R2-F1 | Idle frame's TIME cell reads NO END — Fremd's contradiction ("BLOCK: OFF but TIME: ENDS 15:19") cannot recur |
| A-9 | Commit Plate is the ONLY full-width solid indigo verb plate; at seal it empties to `SEALED HH:MM` (spec §1.10) | [frame]+[pixel] R2-F1/F3 | Solid-plate census per frame = 1 (Deck idle), 0 + register line (sealed); SEALED figure = the harness seal minute |
| A-10 | Refusal sentence in annotation red, reserved slot under the plate (spec §1.11) | [pixel] R2-F2 | Refusal text pixel = night alert #FF6B7A (night) / #C8102E (day) — Ruth residue 3: the phase-1 skin wore ink here; the build owes the red or a retraction |
| A-11 | `NEXT — SLEEP 20:00` margin line on the idle Deck (Kleinteil §3.1, exact string spec) | [frame] R2-F3 | Idle frame at a known harness clock prints the next boundary's name + HH:MM; a stand-down variant prints ENDS |
| A-12 | ISSUED cell: one cell, one Text, label inseparable (Zirkel §3.2; Kleinteil §3.2; K-8) | [frame]+[run] R2-F3 age test | See charge S-04: sampling contract settled in code (per plate entry, Zirkel OPEN-6) and the age test passes |

### 1C. EINBAU — Shield / Tasks / Drawer / receipt / clock / door / pill

| # | Claim (source) | Method | Pass condition |
|---|---|---|---|
| E-1 | Header quantity line `n SHUT • m PEEK`; the compressed noun prints nowhere (spec §2.2) | [frame]+[grep] | Shield frame shows the split; grep finds no "APPS WILL SHUT/HELD" string in the lane |
| E-2 | Cap throw figures its position above a ticked scale track (spec §2.3) | [frame] | Figure changes with the cap's seat; one figure, one seat |
| E-3 | Day-scale: one line 03:30→03:30, segments figured, overnight wraps with a break mark, disabled hatched (spec §2.4) — WITH Feile F-19's sliver rule | [frame] R2-F4 (5-routine harness day) | See charge S-03: no label collision, no fattened segment, wrap figure legible at both ends |
| E-4 | `LAST ROUTINE CHECK HH:MM` under the scale, blank when unmeasured (spec §2.4; fact at MonolithDao.kt:263) | [frame] | Figure = the harness tick stamp; blank seat leaves no placeholder text |
| E-5 | Routine row: days figured as DAILY / MON–FRI / named days; bare `M T W T F S S` dead (spec §2.5; trunk ShieldMatrixScreen.kt:531) | [frame]+[grep] | No single-letter day mask renders on any Shield frame |
| E-6 | Editor: figured fields to any minute, keypad or 1/5/15 steps, day-scale redraws live above the sheet (spec §2.5; Feile F-20 priced ~140 lines + VM setter) | [run] R2-F5 | Setting 05:07 (a non-5-multiple) succeeds and the scale segment moves; SAVE is an outline key, never the Plate |
| E-7 | Sled: OPEN / PEEK / SHUT positions LABELLED, current position solid (spec §2.7) | [frame] | Every sled position carries its word; no position is colour-only |
| E-8 | TOLERANCES plate: PEEK [n MIN] 0–30 · 0=OFF, BREAK [n MIN] 0–60 · 0=NO GAPS, figured fields, clamped (Kleinteil §1.2) | [run] R2-F6 | Fields clamp at bounds; PEEK 0 prints OFF + hatched row; BREAK 0 prints `0 MIN — NO GAPS` |
| E-9 | Tasks: no duration asked, no duration printed; order is the information (spec §4.1) | [frame]+[grep] | Composer has no minutes field; no `START …m` string renders (trunk FocusSequencerScreen.kt:617 dead in-lane) |
| E-10 | Drawer register: FILED UNDER and AT as separate headed columns; ENTRIES = count of rows, labelled; no other number on the plate (spec §4.2) | [frame] R2-F11 | A 02:00 finish under yesterday's filing reads as two labelled facts; census of figures on the Drawer frame = dates, times, ENTRIES only |
| E-11 | Receipt: № zero-padded never truncated; PLANNED omitted whole on FLOW slips; derived ENDED bracketed; WITHIN frozen; footer unchanged (spec §4.3; RC-01…RC-04) | [frame] R2-F9 | FLOW slip has no PLANNED line; a derived-path slip shows `ENDED [HH:MM]`; footer string matches trunk ReceiptSheet.kt:176 verbatim |
| E-12 | Desk clock: elapsed + remaining as dimension figures, dimension line filling, ±6dp drift, numerals at the 55% composite (spec §4.4) | [pixel] R2-F10 | See §7: ground sampled FIRST, numeral composite recomputed against that ground; PASS only under the conditions in S-06 |
| E-13 | Held Door: HOLD stamp rotated −8° in annotation red; remaining first (`HELD UNTIL HH:MM · N MIN LEFT`); honest split; offer only where honourable; cooldown printed WITH reason (spec §4.5) | [frame]+[pixel] R2-F7 | Stamp pixel = alert red; on a non-honouring door the offer seat holds the reason, not an offer; cooldown line present when a peek is withheld |
| E-14 | Pill ×3 states + peek-end last frame `COOLDOWN N MIN`; dismisses only at state end (spec §4.6) | [frame] R2-F8 | All three states render the figured remainder; a tap mid-state does not dismiss |
| E-15 | Door's peek key figures the LIVE setting `PEEK n MIN` (Kleinteil §1.3 — "the constant 2 dies") | [run] R2-F6→F7 | After setting PEEK 5 on Shield, the next door offers `PEEK 5 MIN`; a live lease's countdown does NOT move (L-A) |

---

## 2. CHARGES PROVEN FROM THE SOURCES — Round 1

### S-01 — The spec contradicts itself on DONE, and a builder must disobey one section to obey the other
**Proof.** §1.7: "DONE as a solid-ink key at the row's right end
(a verb wears a filled body)." §1.10: "not DONE (solid but
key-sized and ink-black indigo at row scale — ruled: DONE takes
the *outline* key with solid only while pressed, so the plate's
uniqueness survives…)". §1.7 was never amended to match the
§1.10 ruling. The Commit Plate's uniqueness — this world's most
exported law — therefore rests on which section Einbau/Ausbau
happens to read second. **Charge:** strike "solid-ink" in §1.7
or strike the §1.10 ruling; the spec may not carry both.
Round-2 execution: A-9's census counts a solid DONE at rest as
a uniqueness breach.

### S-02 — B-F2 has already propagated: Feile's F-20 prices the phantom collision as presentation
**Proof.** Feile F-20 (IMPROVE-feile.md): the editor's "day-
scale (§2.4, with F-19's routing) redrawing live above it,
collisions annotated red at the touching boundaries **as
spec'd**" — priced inside a ~140-line sheet estimate whose
machinery half is only "~15 lines VM." But the collision fact
does not exist (B-F2; Registry OL-22 carries the derivation as
*unbuilt, unscheduled*, Monteur's). An improver's delta has
inherited the spec's false premise at face value, which is how
a §2.5 sentence becomes a build that "discovers" the missing
derivation mid-lane. **Charge:** F-20's price is understated by
exactly OL-22's derivation; either OL-22 is scheduled into
Einbau's editor build or every collision sentence (§2.5,
F-20) is struck from the build documents until it is. Credit
where due: the filed spec's own A2 hedge named the gap; the
failure is that the hedge did not travel with the claim.

### S-03 — The day-scale, as specced, survives only the seeded three
**Proof, arithmetic.** Spec §2.4 draws every routine as a
segment on one 24-hour line with "segment labels above the
line, figures below" and NO rule for short segments, adjacent
labels, or the wrapped segment's figure. Feile's F-19 supplies
the numbers (IMPROVE-feile.md, re-derived by me): usable width
~342dp ÷ 1440 min = 0.237dp/min. The seeded day
(MonolithRepository.kt routineTargets(): FASTED 05:00–08:00 =
180 min ≈ 43dp; DAY 09:00–19:30 = 630 min; SLEEP 20:00–03:30 =
450 min, wrapped) fits because its shortest segment is 43dp —
and even FASTED's figure `05:00–08:00` (≈73dp at 11sp,
Feile's measure) is wider than its segment; it survives only
because its neighbours are empty line. A 25-minute routine is
a 5.9dp segment carrying a 73dp figure; a five-routine day —
his editor invites any routine at any minute (§2.5) — puts
figures on collision courses the spec never routes. The wrapped
SLEEP segment is worse: one figure string, two line-ends, and
§2.4 never says whether the figure splits, repeats, or sits at
one end and abandons the other. **Disposition of the cure:**
Feile's F-19 sliver rule (true widths; ≥60dp label above;
12–60dp leader to a staggered second row; <12dp item number
only, pointing at the parts-list row) is the right cure and it
exists — but Feile's own reconciliation records it
"UNTOUCHED — the contract is silent on the scale's segment
geometry… F-19 stands exactly as filed and still gates
Einbau's first scale build," and Zirkel's DESIGN settles no
day-scale geometry at all. **Charge:** the spec + the design
contract, the two documents a builder is told to build from,
do not contain the rule that makes their centrepiece survive
a real day. F-19 must be adopted INTO §2.4's build text —
named, not adjacent — before Einbau starts the scale, or
Round 2's R2-F4 frame (five routines, one 25-minute, one
overnight) will be taken against a spec that cannot pass it.

### S-04 — ISSUED HH:MM: honest label, unsettled sampling — and the unsettlement is the lie risk
**The question put to this desk: is `ISSUED HH:MM` an honest
static fact or a lie by stillness? Verdict: the label is
honest; the sampling contract is not yet a fact at all, and
under one of the crew's two contracts the stamp becomes the
worst of both — a clock that ticks in jumps.**
Proof, three documents, three contracts:
1. PRESENTATION §1.12/A6: "sampled at composition, labelled as
   issue, never ticking." Klar's K-8 condition (adopted by Ren,
   mail/blueprint/2141): the label is inseparable from the
   figure or it doesn't ship.
2. Kleinteil §3.2 satisfies K-8 in form (one cell, one string,
   never abbreviated, never demoted) — and then rules:
   "each composition stamps its own issue; turning away and
   back re-composes and re-stamps." In Compose, recomposition
   is not navigation: any state change on the Deck can
   recompose it. Under Kleinteil's sentence a stamp can
   silently advance mid-stay — 19:41 at entry, 19:52 after he
   turns the wheel — claiming a re-issue that never happened.
   That is UNA2-03 in miniature, the exact failure Kleinteil's
   own §3.2 names in its next breath.
3. Zirkel OPEN-6 states the stricter contract (sampled once
   per plate entry, keyed to plate identity) — and files it
   as OPEN: "builders confirm an entry hook exists; if the
   only hook is composition…" The hook's existence is
   unconfirmed on disk.
**Charge:** two crew documents rule two different stamp
behaviours, and the stricter one depends on a hook nobody has
verified exists. Until Riss rules ONE contract (this desk's
position: Zirkel's — per entry, keyed, never per recomposition)
and Monteur/Ausbau confirm the hook by file:line, `ISSUED`
must not be described — in any judging document — as "an
honest static fact." It is an honest *intention* with an
unbuilt mechanism. The Fremd half stands separately (§6):
ISSUED is the Deck's only clock-like figure, Fremd's pass
found "no clock" the shared absence, and a tired man reads a
HH:MM in a title block as the time. K-8's inseparability is
necessary; it is not sufficient against a 17-minute-old stamp
(Klar's example: composed 19:41, read 19:58). Round-2 age
test pre-registered at R2-F3.

### S-05 — The record speaks two luminance languages and calls both "rel-lum"
**Proof, my recomputation tonight** (sRGB relative luminance;
WCAG linearised vs the un-linearised weighted sum):
- #17234C (night plate): WCAG **0.0191** · weighted-sum **0.1389**
- #E9EEFF (cap at rest): WCAG **0.8569** · weighted-sum **0.9341**
- #D9E1FF (AOD raw): WCAG **0.7582** · weighted-sum 0.8842
- #7D85A3 (AOD cure): WCAG **0.2378** · weighted-sum 0.5234
Ruth's amendment files the plate at **0.139** and the cap at
**0.934** — the weighted-sum figures, exact. Salz's desk files
the AOD pair at **0.758 / 0.238** — the WCAG figures, exact.
Both desks are internally exact; the studio record nonetheless
holds "rel-lum 0.139" and "rel-lum 0.238" computed by different
formulae, and Ruth's comparison "more than double Signal's
cured ground (0.06)" is only meaningful inside her method.
**Charge:** not against either desk — against the record.
If Round 2 compares a fresh WCAG sample against Ruth's 0.139,
Blueprint will appear cured by arithmetic it never performed;
if it compares a weighted-sum sample against Nacht's 0.25
ceiling (a WCAG number), the AOD cure fails on paper while
passing in fact. **Ruling asked of Riss:** one canonical method
for the crew — WCAG linearised (Salz's, Nacht's ceiling's) —
and Ruth's two numbers restated in it wherever they are cited:
plate **0.0191**, cap **0.8569**. The cap charge survives the
restatement at full strength; the plate's ratio to ground is
1.18:1 WCAG — which is exactly spec §8's N6, so the drawing's
own register already agrees with the restated figure.

### S-06 — The AOD cure is ground-conditional, and the spec never names the ground
**Proof, my recomputation.** §4.4 adopts "numerals at the 55%
composite" and §8 prices the cure over the night ground
#0C1533 → #7D85A3, 0.2378. But a 55% composite is a function
of its ground, and the spec never states which token the desk
clock's ground IS:
- over #0C1533 (night ground): #7D85A3 → **0.2378** ✓ under 0.25
- over #000000 (trunk clock's true black): #777C8C → **0.2023** ✓
- over #17234C (night raised — the Deck plate's own surface):
  #828CAE → **0.2656** ✗ OVER the ceiling
- over #1D2A58 (night sheet): #848FB4 → **0.2785** ✗ OVER
Salz noted the base-dependence in passing ("composited over
true black the same 55% would land lower"); nobody carried the
other direction. If Einbau builds the night plate (§4.4 calls
the clock "the drawing at night") on the raised plate token —
the natural reading of "plate" — the cure fails by 0.016 while
every document on disk calls it exact. **Charge:** §4.4 must
name the clock's ground token (night ground or true black —
both pass) as part of the cure, or the cure is a number in
search of a surface. Round 2 samples the ground FIRST
(R2-F10); a numeral sample without its ground proves nothing.

### S-07 — `REV C` at rest is a letter without a fact — condemned by Zirkel's own rule
**Proof.** Zirkel §3.2 puts `REV C` in the title block's
revision/seal cell at rest. In the same section Zirkel kills
the phase-1 `SCALE 1:1` with the rule: "Scale is not printed
in the block — the phase-1 SCALE 1:1 was furniture; a block
cell prints a fact or stays structural." REV C prints a
revision history — two prior revisions of this sheet — that
does not exist as any state in the app. The only honest REV
machinery on disk is Zunder's B-2 (revision triangle + REV n
from ONE stored revision integer + per-routine last-edited
day), and Registratur C-10 records B-2 as **UNRULED — not
adopted, not refused, no answer filed**. **Charge:** by the
contract's own stated rule, `REV C` at rest is furniture
wearing a fact's dress. Kill it (cell stays structural/empty
until the seal prints SEALED HH:MM), or rule B-2 and print the
stored integer. There is no third state in which a letter C
is a fact. (The same section's other divergence is noted, not
charged: spec §0/§1.10 stamp the cell **APPROVED** at the seal;
Zirkel's cell prints **SEALED HH:MM**. SEALED is the fact;
APPROVED answers questions nobody asked — by whom, of what.
Round 2 takes SEALED as the pass string, §6 files APPROVED
under kill.)

### S-08 — Process: the K-5 disposition routes Shield's plate to the Deck's builder
**Proof.** Riss's seating letter partitions the tree three
ways: Monteur = shell/theme/PARK, **Ausbau = DECK**,
**Einbau = SHIELD + ROOMS**. Kleinteil's K-5 disposition
(§1.4) and his summary table both read: "BUILT-IN-LANE
(**Ausbau** — Shield tolerances plate…)". Every other
disposition in the same file routes correctly (pill → Einbau,
NEXT line → Ausbau, desk clock → Einbau). The single slip
sits on the single most load-bearing routing line in the
supplement — the owed K-5 plate. If Ausbau builds to his
letter and Einbau builds to the roster, the tolerances plate
is built twice or orphaned once. **Charge:** Riss corrects
the routing in Kleinteil's file (append-only correction) to
Einbau before either builder starts Shield. Small, proven,
and exactly the class of error a registry exists to catch —
Registratur's row for Kleinteil should carry it.

### S-09 — The interim TOLERANCES note names a studio programme to the user
**Proof.** Kleinteil §1.4 rules the plate ships disabled-but-
labelled until Truth Car 3 lands, under the margin note:
`FIGURES TRUE AS SHIPPED — SETTING LANDS WITH TRUTH CAR 3`.
"TRUTH CAR 3" is studio shorthand; to the man holding the
phone it decodes in five seconds as nothing (§6 test). The
interim design is otherwise this desk's model of honesty —
hairline frame, no editor's dress, a verb shown must work —
and the note's first clause is true and sufficient.
**Charge, conditional:** IF the interim plate ships in any
build he installs, the note's second clause dies and the
first stands alone, or the clause is reworded in his
vocabulary ("SETTING COMING"). If the car lands before any
build — the programme's stated order — the charge lapses
unexecuted. Pre-registered either way: R2-F6 reads the note
as built.

---

## 3. THE K-5 VERDICT — does the supplement satisfy his order, or is it a stance in grammar's clothing?

**Verdict: the supplement satisfies the order in substance.
The stance charge now lands on the unmerged spec, not on
Kleinteil.** The distinction matters and both halves are proven.

What the filed spec had (Klar, six of six): §4.5 calls the peek
length "a real constant of the machinery, stated, not
decorated" — a stance about the fact, nothing about its
editability; the break's length appears only inside the hero's
span arithmetic; §3's instrument table seats four instruments
and durations are none of them. That WAS grammar's clothing:
the world's own vocabulary ("figured, settable dimensions")
applied to routine times and withheld from the two lengths his
order actually named.

What Kleinteil filed (SMALLTHINGS-kleinteil.md §1, read whole
by this desk):
- **A seat, argued, with the alternatives ruled out by name:**
  a TOLERANCES plate on Shield between the cap (§2.3) and
  ROUTINES (§2.4); PARK refused (a navigation verb cannot hold
  an editor); the Deck's tolerance block refused as the seat
  (a present-tense proof must not carry a future-pricing
  control under a 03:30 thumb).
- **Exact form:** figured fields in the §2.5 Figured Field
  family, bounds printed on the row itself (`0–30 · 0 = OFF`,
  `0–60 · 0 = NO GAPS`), setters clamped to Freya's bounds so
  no rejection state exists.
- **Zero states with downstream obedience:** PEEK 0 → field
  prints OFF, row hatches, the door renders no offer and owes
  no reason; BREAK 0 → `0 MIN — NO GAPS`, the span prices
  (n−1)×0, the eyebrow never prints BREAK, the pill never
  takes its BREAK state.
- **A print law (L-A) that is the actual substance of the
  order:** running surfaces print STAMPED figures only —
  breakSeconds stamped on the session row at creation, the
  lease end written absolute at grant; no live surface ever
  re-reads the setting; the door's offer key is re-figured to
  `PEEK n MIN` reading the current setting, with the worked
  example (raise 2→5 mid-peek at 14:00: the live countdown
  does not move, the next door offers 5). This is the part a
  stance could not have produced: it decides, correctly, WHAT
  "his to set" means for a session already sealed.
- **A named seam, not an assumption:** two readable values +
  breakSeconds stamp + two clamped setters from Truth Car 3,
  migration 11→12 not renumbered; and until the car lands, the
  disabled-but-labelled interim (S-09's wording aside) obeys
  the scope law — no inert field dressed as an editor.

**The residual charges, all proven above or here:**
1. **The drawing itself still says otherwise.** PRESENTATION-
   blueprint.md on disk still prints the door's key as the
   figured constant `PEEK 2 MIN` (§4.5), still seats no
   durations instrument in §3's table, and carries no
   supersession note pointing at the supplement. Kleinteil
   §1.3 declares "the constant 2 dies" — in Kleinteil's file.
   A builder who builds from the spec (the document the lane
   calls its constitution) builds the constant. *The K-5
   answer is complete only when the supplement is merged into
   the spec text it amends — §4.5's key re-figured, §3's table
   gaining the TOLERANCES row, §2 gaining the plate between
   §2.3 and §2.4 — or the spec's header names the supplement
   as binding where they differ.* Until then K-5 is answered
   in an annex, and annexes are where answers go to be missed.
2. **Routing slip S-08** (Ausbau for Einbau) — the plate's own
   disposition misroutes it.
3. **The heading word TOLERANCES** fails the Fremd test even
   though its rows pass it (§6): the plate's name is the one
   word on it a tired man cannot decode, in the world whose
   law is "nothing unlabelled" — a label he cannot read is a
   label in name only.
Round-2 execution of this verdict: register E-8 + E-15 +
A-2 — set PEEK 5 / BREAK 15 at the plate, then read the door,
the pill, and a fresh hero span. If all three obey, K-5 closes
in fact, not in filing.

## 4. THE ISSUED VERDICT — restated in one paragraph

An honest static fact must be (a) labelled as static, (b)
actually static under a stated sampling rule, and (c) not
seated where it will be read as the live fact it is not.
Blueprint/Kleinteil deliver (a) completely (K-8, one cell, one
string). (b) is currently TWO rules in two files, one of which
(re-stamp on recomposition) breaks staticness silently, and
the sound one (Zirkel OPEN-6, per plate entry) rests on an
unconfirmed hook. (c) is unproven: ISSUED is the world's only
answer to "what time is it" on the Deck, Fremd proved a man
hunts for exactly that figure, and no age qualifier exists.
**So: not a lie by stillness — a truth whose mechanism is
unfinished.** It ships when Riss rules Zirkel's contract,
Monteur/Ausbau cite the entry hook by file:line, and R2-F3's
age test passes. If the hook does not exist, the stamp dies
and the title block carries no time at all: a drawing that
cannot timestamp honestly prints none. That fallback costs
the world nothing — its timelessness was §1.12's own first
sentence ("a drawing is timeless; his day is not") before the
stamp was proposed to bridge it.

---

## 5. THE FREMD LIST — every printed word the build will introduce, judged at 03:30, five seconds, half-asleep

Test applied (Fremd's own): can a tired man who knows nothing
decode it in five seconds? Evidence cited per word; where the
word is trunk-inherited rather than Blueprint-introduced, that
is stated — inheritance is not acquittal, but the kill price
differs and honesty requires the distinction.

| Word / mark | Where it prints | Verdict | Argument |
|---|---|---|---|
| `DWG NO. 01` | Phase-1 skin's title line: `DWG NO. 01 — TACTILE DECK · SCALE 1:1 · REV C` (Ruth residue 1 — the least legible line on its own sheet, overstruck by the registration mark) | **KILL** | It duplicates PLATE 01 — one fact, two renderings, against this world's own law (§1.6's principle). Zirkel's title block already omits it; keep it dead. Round 2 greps frames for "DWG" — any hit reopens. |
| `SCALE 1:1` | Same title line | **KILL** | A false figure, not merely an obscure one: 1:1 claims this drawing is the same size as its object; its object is a day. Zirkel killed it ("furniture") — endorsed, absence pre-registered. |
| `REV C` | Title block revision cell, at rest (Zirkel §3.2) | **KILL at rest** | Charge S-07: a revision history that does not exist. REV *n* may print only if B-2 is ruled and the integer is stored fact. |
| `APPROVED` (stamp) | Spec §0/§1.10: the title block stamps APPROVED at the seal | **KILL** | Approved by whom, of what? Zirkel's cell already prints the fact instead — SEALED HH:MM. The spec text should be corrected to match its own designer's contract. |
| `PLATE 01 / DECK` + index `01 DECK · 02 SHIELD…` | Title block (spec §1.13; Zirkel §3.2/§3.3) | **KEEP** | The number never travels alone — the word rides beside it in every cell, so the pair decodes as a list, not a code. This is the drawing-office word that earns its seat: it IS the navigation, and a man learns it in one pass because each cell names where it goes. |
| `LEDGER` | Index cell 04 (Zirkel §3.3: "per the crew brief") | **KILL — escalate to Riss** | Fremd, verbatim, on this word in the shared skeleton: "This is a blocking app — why does it have a *ledger*?" The plate's own spec title is THE DRAWER (§4.2), its metaphor a register. Three names for one room (Drawer / register / LEDGER) in one spec is the opposite of one-fact-one-rendering. Print `04 DRAWER`. If the brief mandates LEDGER across all seven looks, the mandate is the defect — Riss carries it up, I do not bury it. |
| `TOLERANCES` (plate heading) | Shield, Kleinteil §1.2 | **KILL the heading, KEEP the plate** | Nobody at 03:30 — or at noon, outside an engineering office — decodes "tolerances" as "how long a peek and a break are." The rows beneath it (`PEEK [2 MIN]`, `BREAK [10 MIN]`) decode instantly and need no heading to do it. A heading that must be explained by its rows is furniture. The rows stay verbatim; the plate is found by its rows. |
| Tolerance language generally ("tolerance block") | Spec §1.9's name for the proof strip | **NEVER PRINT** | §1.9's printed cells are BLOCK · APPS · TIME — lawful trunk words. The phrase "tolerance block" is spec-internal vocabulary; pre-registered grep over lane strings: it must appear in no resource, no composable literal, no render. Internal metaphors that leak into print are how DWG NO. happened. |
| Hatching (the mark, not a word) | Day-scale disabled segments (spec §2.4); TOLERANCES PEEK-0 row (Kleinteil §1.2) | **KEEP, with its condition** | Diagonal hatch reads as "present, not built" only because it never carries a fact alone: the routine's row below the scale prints its name, figure and ON/OFF toggle in words (§2.5), and the PEEK-0 row prints OFF in the field itself. Condition, pre-registered at R2-F4: any hatched segment whose words cannot be found on the same plate within one glance fails — hatch as sole carrier is a kill. |
| `ISSUED HH:MM` | Title block (spec §1.12) | **CONDITIONAL KEEP** | Only under §4's full verdict: K-8 inseparability (met), Zirkel's sampling contract ruled, entry hook cited, age test passed. Any leg fails → kill, per §4's fallback. |
| `SEALED HH:MM` | Title block cell at the seal (Zirkel §3.2; spec §1.10 register) | **KEEP** | Past-tense verb + the event's own minute. It decodes because it reports something that happened to him, in words he uses. The model every static figure on the sheet should be judged against. |
| `NEXT — SLEEP 20:00` | Idle Deck margin (Kleinteil §3.1) | **KEEP** | Plain words, one fact, forward-looking — the line Klar's K-9 asked every finalist for. Its variants (ENDS, named later days) stay inside the same plainness. |
| `LAST ROUTINE CHECK HH:MM` | Shield margin under the scale (spec §2.4) | **KEEP, one defect attached** | The words decode. The defect is Tageslauf's named hole, adopted here: §3 promises the figure turns annotation red when "stale," and NO threshold exists anywhere in spec, supplement or contract — a verdict with no rule is a builder's guess wearing red. Cure price is one sentence (stale = no tick within N minutes of the last expected boundary); Riss owns assigning it before Einbau builds the line. |
| `FORGED — <name>` | Drawer register annotation (spec §4.2) | **KILL the word** | In plain English "forged" means faked — a counterfeit. The fact it carries is that finishing a task *made* an artifact. A tired man reading FORGED beside his own finished work reads an accusation. Print `MADE — <name>`; the one true forge rule in the register's front note can keep its machinery vocabulary internally, but the printed annotation is his to read, not the machinery's. (Inherited from Truth Car 2's wording — stated, per this list's rule; the kill is priced as one string.) |
| `ENTRIES` | Drawer ALL MONTHS counts (spec §4.2) | **KEEP** | A plain word labelling a count of records. It survives because its seat labels it — the count never appears without the word, and the word never promises a grade. |
| `FILED UNDER` / `AT` | Drawer column heads (spec §4.2) | **KEEP** | Two short labels that turn a potential falsehood (a 02:00 finish under yesterday) into two true facts. This is labelling doing exactly its job. |
| `HELD` / `HOLD` (stamp) / `HELD UNTIL` | Door + Deck cells (spec §1.8, §4.5) | **KEEP** | Plain English, physically accurate — the drawing holds the door. Fremd never saw the door; the words need no office to decode. |
| `SHUT` / `PEEK` / `OPEN` | Sled, splits, door (spec §2.2/§2.7) | **KEEP** | Fremd decoded PEEK from the Shield header unaided; what failed him in the skeleton was `2m` — a duration wearing a tier's seat. Blueprint prints the WORD as the tier and figures durations only as durations (door key, pill). Keep the words; Round 2 greps for any `2m`-style tier label. |
| `GUARD n/4` | Deck margin note (spec §1.1) | **KEEP, flagged** | Trunk-inherited, and Fremd failed it cold ("One of four *what*? Is something broken?"). Blueprint's mitigation is real — the lack names itself in the same note, in red — and the count is a true census of four permissions. Renaming is app-wide machinery vocabulary, above this crew's pay grade; flagged to Riss as the one inherited word this world could not cure from inside its own plates. |
| `ENDED [09:41]` (brackets) | Receipt, derived path only (spec §4.3) | **CONDITIONAL KEEP** | The bracket is the draughtsman's mark for "not measured on site" — inside the office. On the slip, nothing explains it: a man comparing two receipts, one bracketed, one not, gets no legend at arm's length. Keep ONLY with a one-line legend on the slip itself (the footer zone already carries house law; one more line, plain words: `[ ] = worked out afterwards, not witnessed`). Without the legend: kill the brackets and omit derived ENDED whole — RC-01's own lawful alternative ("labelled as derived or not at all"), and omission beats an unexplained mark. |
| `PARK` | Title block last cell (spec §1.13) | **KEEP** | A verb in a labelled cell. Fremd's failure was the skeleton's unlabelled two-lines ICON, not this word; Blueprint's cell prints the word itself. A man parking a sheet is a metaphor he performs, not one he must decode. |
| `DESK CLOCK` | Deck armed utilities (spec §1.8) | **KEEP** | It is a clock, on the desk, labelled with both words. No notes. |

**Census law coming out of this list:** every printed string
in the build must be findable in this table, the filed spec's
kept trunk vocabulary, or a ruling that postdates this file.
A string found in a render and in none of those three is a
Round-2 charge by definition — pre-registered now so its
builder cannot call it taste.

---

## 6. ROUND 2 — PRE-REGISTERED FRAMES AND PIXEL SAMPLES (exact)

When Riss re-seats this desk over the renders, these are the
frames and samples I will take — named now, so no frame can be
chosen after the fact to flatter the build. All luminance/
contrast recomputation in WCAG linearised form (S-05's
canonical method); where Ruth's weighted-sum figures are the
comparison on record, both numbers will be printed side by side.

- **R2-F1 · Deck, day, idle.** Full frame. Checks: frame +
  registration marks present, mark overstrikes nothing (M-5);
  dimension line pixel-span vs wheel pixel-diameter (A-1);
  TIME cell = NO END (A-8); solid-plate census = 1 (A-9);
  title block geometry vs Zirkel's 64dp/32+32 (M-6); "DWG" /
  "SCALE" absent (§5 census).
- **R2-F2 · Deck, night, idle — THE NIGHT SAMPLES.**
  (i) **Night Deck plate sample:** pixels at the plate interior
  away from line-work (four points: header margin, hero field,
  strip field, rail seat). Ruth on the phase-1 skin: plate
  #17234C (weighted-sum 0.139; WCAG 0.0191 per S-05), ground
  #0C1533. PASS for the cure Riss carries (plate → ground):
  plate sample hex = ground hex #0C1533 at all four points.
  Anything still at #17234C means the cure is a claim, not a
  token. (ii) **Cap-at-rest sample:** the slide cap's fill pixel,
  untouched, at rest. Phase-1 skin: #E9EEFF (weighted-sum 0.934;
  WCAG 0.8569). PASS: cap fill is the dark commit ink (day
  #1D2B6B family / its night seat per Zirkel's token table) —
  the cap fills light only in travel, never at rest. (iii)
  Refusal-line pixel = #FF6B7A night alert (A-10).
- **R2-F3 · Deck, sealed/running + the ISSUED age test.** Enter
  the Deck at a recorded wall time T; do NOT leave the plate;
  capture at T+17 min (Klar's example interval). PASS: ISSUED
  still reads T (per-entry sampling, OPEN-6) AND SEALED reads
  the seal minute AND NEXT — reads the true next boundary.
  FAIL conditions named now: ISSUED advanced without re-entry
  (recomposition re-stamp — S-04's lie, caught); ISSUED = T
  but presented anywhere as current time. Plus: % absent (A-4),
  HELD two-cell split (A-7).
- **R2-F4 · Shield, day, FIVE-routine harness day** — the seeded
  three PLUS a 25-minute routine and a second overnight or
  short-evening routine, one of them disabled. Checks (S-03):
  segment widths true (pixel-measured against 0.237dp/min at
  frame scale, ±1px); F-19 routing obeyed (≥60dp above-line,
  12–60dp leader+stagger, <12dp number-only); wrapped figure
  legible; hatched segment's words findable on the same plate
  (§5 hatch condition); TOLERANCES plate present between cap
  and ROUTINES (E-8 seat).
- **R2-F5 · Shield, night, editor sheet open — THE FRAME STROKE
  SAMPLE.** Sample the editor sheet's frame stroke pixel.
  PASS: stroke hex = full-weight outline #55649F (2.69:1 on
  raised — the N3 condition, Salz). FAIL — and **N3 REOPENS
  by the condition's own terms**: stroke composites to the
  0x75-alpha variant (#334172 over raised, 1.55:1 — my
  recomputation: alpha 0x75 = 117/255 over #17234C yields
  exactly Salz's 1.55). Note for the builder, filed now so it
  cannot be called a trap later: spec §0 law 1 DEFINES the
  hairline as "0.75dp at 0x75 alpha," and §8's defence requires
  the sheet frame at FULL weight. The sheet frame is structure
  (1.5dp tier), not a hairline; build it from the outline
  token, not the variant, or bring a retraction of §8's
  defence to Round 2 with you. Also in this frame: sheet-over-
  raised step recomputed from sampled hexes; editor sets
  05:07 exactly (E-6); SAVE is outline (E-6).
- **R2-F6 · TOLERANCES run.** At the plate: set PEEK 5, BREAK
  15 (E-8 clamping at 0 and at 31/61 attempted); PEEK 0 →
  OFF + hatch; BREAK 0 → `0 MIN — NO GAPS`; restore 5/15.
  Interim note read as built (S-09). THEN, without re-entering
  Shield: arm a 2-block session — hero span must price the
  stamped 15 (A-2); open a SHUT app — door offers `PEEK 5 MIN`
  (E-15); take the peek — pill counts from 5:00, and a mid-peek
  re-set to 9 on Shield (second surface) must NOT move the
  live countdown (L-A).
- **R2-F7 · Held Door, both forms.** Honouring door + a door
  that cannot honour a lease. Samples: HOLD stamp pixel =
  alert red (day #C8102E / night #FF6B7A), rotation −8° ±1°
  measured off the text baseline; remaining line first;
  cooldown-with-reason present on the withheld form (E-13).
- **R2-F8 · Pill ×3 states + last frame.** SHIELD / BREAK /
  PEEK tags at bottom margin; PEEK end's last frame reads
  `COOLDOWN N MIN` before dismissal; a mid-state tap does not
  dismiss (E-14). BREAK tag at BREAK 15 (post-R2-F6) figures
  15-derived remainders, never 10 — the stamp, not the default.
- **R2-F9 · Receipt ×2.** A timed slip and a FLOW slip.
  Checks (E-11): № zero-padded; FLOW slip omits PLANNED whole;
  derived-path slip's ENDED bracketed AND the §5 legend line
  present, or derived ENDED omitted; footer verbatim.
- **R2-F10 · Desk clock, night — THE NUMERAL COMPOSITE
  SAMPLE, ground first.** Sample order is the method (S-06):
  (1) ground pixel at the clock field's corner; (2) numeral
  pixel at a glyph's solid interior. Recompute the numeral's
  effective luminance against ITS OWN sampled ground. PASS:
  ground hex ∈ {#0C1533, #000000} AND numeral WCAG luminance
  ≤ 0.25 (expected composite #7D85A3 → 0.2378 over #0C1533;
  #777C8C → 0.2023 over black). FAIL by arithmetic, no appeal
  to taste: ground at #17234C or #1D2A58 with a 55% composite
  (0.2656 / 0.2785) — Nacht's court gets the frame either way;
  this desk reports the numbers it sampled. Drift: two
  captures ≥1 tick apart, numeral origin displaced ≤6dp.
- **R2-F11 · Tasks + Drawer.** Queue of ≥10 items (no `010`,
  A-6); Tasks composer shows no duration field (E-9); Drawer
  day with a 02:00 finish filed under yesterday (FILED UNDER /
  AT separation, E-10); figure census on the Drawer frame =
  dates, times, ENTRIES only.
- **R2-F12 · PARK picker.** Eight looks listed; Blueprint opens
  the built world; every other cell is labelled unbuilt or
  opens its own real look — no cell opens a trunk-skinned
  plate wearing Blueprint's name (M-8).

**What acquits, in one line per standing item:** B-F1 — lane
grep clean + §1.3 corrected. B-F2 — OL-22 derivation cited by
file:line, or no collision drawn anywhere. N3 — R2-F5 stroke
at #55649F. AOD — R2-F10 PASS as defined. K-5 — R2-F6 chain
obeyed end-to-end + the spec-merge named in §3(1). ISSUED —
R2-F3 age test + the hook's file:line. Day-scale — R2-F4
against F-19 as adopted build text.

*— Scharf, Critic (unrestricted), Blueprint crew. Round 1
filed Oct 29, 2026, ~22:35 IST. Inbox: 0 at seating, 0 at
close. I will be re-seated for Round 2 over the renders; the
frames above are the appointment.*
